const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read or write exam_progress", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("exam_progress").doc("current").get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("completed_exams").get());
});

test("Authenticated user: cannot read another user's progress or exams", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(BOB_UID).collection("exam_progress").doc("current").set({
      userId: BOB_UID,
      subjectId: "general_test",
      subjectTitle: "General Test",
      testMode: "FULL_MOCK",
      remainingSeconds: 3600,
      currentIndex: 0,
      answersJson: "[]",
      candidateName: "Bob",
      candidateRollNo: "CUET999",
      updatedAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(BOB_UID).collection("exam_progress").doc("current").get());
  await assertFails(aliceDb.collection("users").doc(BOB_UID).collection("completed_exams").get());
});

test("Authenticated user: can save, read, and clear their own exam progress", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const docRef = aliceDb.collection("users").doc(ALICE_UID).collection("exam_progress").doc("current");
  const now = new Date();

  await assertSucceeds(
    docRef.set({
      userId: ALICE_UID,
      subjectId: "full_cuet_mock",
      subjectTitle: "CUET 50-MCQ Full Mock",
      testMode: "FULL_MOCK",
      remainingSeconds: 3000,
      currentIndex: 5,
      answersJson: "[]",
      candidateName: "Alice",
      candidateRollNo: "CUET123",
      updatedAt: now,
    })
  );

  await assertSucceeds(docRef.get());
  await assertSucceeds(docRef.delete());
});

test("Authenticated user: can create completed exam records, but cannot mutate them", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const colRef = aliceDb.collection("users").doc(ALICE_UID).collection("completed_exams");
  const now = new Date();

  const doc = await colRef.add({
    userId: ALICE_UID,
    subjectId: "full_cuet_mock",
    subjectTitle: "CUET 50-MCQ Full Mock",
    testMode: "FULL_MOCK",
    candidateName: "Alice",
    candidateRollNo: "CUET123",
    totalScore: 45,
    maxScore: 50,
    correctCount: 9,
    incorrectCount: 1,
    unattemptedCount: 0,
    accuracyPercentage: 90.0,
    totalTimeTakenSeconds: 1800,
    completedAt: now,
  });

  await assertSucceeds(colRef.get());

  // Immutability of completed records
  await assertFails(
    doc.update({
      totalScore: 50,
    })
  );
});
