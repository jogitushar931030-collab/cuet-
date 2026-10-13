package com.example.data

import com.example.base.FirestoreEmulatorTestBase
import com.example.model.ExamSummary
import com.example.model.QuestionState
import com.example.model.QuestionStatus
import com.example.model.Subject
import com.example.model.TestMode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExamRepositoryRuleTest : FirestoreEmulatorTestBase() {

  @Test
  fun saveExamProgress_authenticatedUser_success() = runBlocking {
    signInTestUser(ALICE_EMAIL)
    val repository = FirestoreExamRepository(firestore, auth)

    val states = mapOf(
      1 to QuestionState(questionId = 1, status = QuestionStatus.ANSWERED, selectedOption = 2, timeSpentSeconds = 30),
      2 to QuestionState(questionId = 2, status = QuestionStatus.NOT_ANSWERED, selectedOption = null, timeSpentSeconds = 15)
    )

    val result = withTimeout(DEFAULT_TIMEOUT_MS) {
      repository.saveExamProgress(
        subject = Subject.FULL_CUET_MOCK,
        testMode = TestMode.FULL_MOCK,
        remainingSeconds = 3450,
        currentIndex = 1,
        states = states,
        candidateName = "Alice",
        candidateRollNo = "CUET2026101"
      )
    }

    assertTrue(result)
  }

  @Test
  fun loadExamProgress_authenticatedUser_returnsSavedProgress() = runBlocking {
    signInTestUser(ALICE_EMAIL)
    val repository = FirestoreExamRepository(firestore, auth)

    val states = mapOf(
      1 to QuestionState(questionId = 1, status = QuestionStatus.ANSWERED, selectedOption = 0, timeSpentSeconds = 45)
    )

    withTimeout(DEFAULT_TIMEOUT_MS) {
      repository.saveExamProgress(
        subject = Subject.GENERAL_TEST,
        testMode = TestMode.FULL_MOCK,
        remainingSeconds = 3200,
        currentIndex = 2,
        states = states,
        candidateName = "Alice",
        candidateRollNo = "CUET2026101"
      )
    }

    val loaded = withTimeout(DEFAULT_TIMEOUT_MS) {
      repository.loadExamProgress()
    }

    assertNotNull(loaded)
    assertEquals(Subject.GENERAL_TEST.id, loaded?.subjectId)
    assertEquals(2, loaded?.currentIndex)
    assertEquals(3200L, loaded?.remainingSeconds)
  }

  @Test
  fun clearExamProgress_authenticatedUser_clearsState() = runBlocking {
    signInTestUser(ALICE_EMAIL)
    val repository = FirestoreExamRepository(firestore, auth)

    withTimeout(DEFAULT_TIMEOUT_MS) {
      repository.saveExamProgress(
        subject = Subject.PHYSICS,
        testMode = TestMode.TOPIC_PRACTICE,
        remainingSeconds = 1200,
        currentIndex = 0,
        states = emptyMap(),
        candidateName = "Alice",
        candidateRollNo = "CUET2026101"
      )
    }

    val cleared = withTimeout(DEFAULT_TIMEOUT_MS) {
      repository.clearExamProgress()
    }
    assertTrue(cleared)

    val progressAfterClear = withTimeout(DEFAULT_TIMEOUT_MS) {
      repository.loadExamProgress()
    }
    assertNull(progressAfterClear)
  }

  @Test
  fun saveCompletedExam_and_observeCompletedExams_success() = runBlocking {
    signInTestUser(ALICE_EMAIL)
    val repository = FirestoreExamRepository(firestore, auth)

    val summary = ExamSummary(
      subjectTitle = "CUET 50-MCQ Full Mock",
      testMode = TestMode.FULL_MOCK,
      candidateName = "Alice",
      candidateRollNo = "CUET2026101",
      totalQuestions = 50,
      answeredCount = 40,
      notAnsweredCount = 5,
      markedCount = 2,
      answeredAndMarkedCount = 1,
      notVisitedCount = 2,
      correctCount = 35,
      incorrectCount = 5,
      unattemptedCount = 10,
      totalScore = 170,
      maxScore = 250,
      accuracyPercentage = 87.5,
      totalTimeTakenSeconds = 2400
    )

    val saved = withTimeout(DEFAULT_TIMEOUT_MS) {
      repository.saveCompletedExam(summary)
    }
    assertTrue(saved)

    val records = withTimeout(FLOW_TIMEOUT_MS) {
      repository.observeCompletedExams().first { it.isNotEmpty() }
    }
    assertTrue(records.isNotEmpty())
    assertEquals(summary.subjectTitle, records.first().subjectTitle)
    assertEquals(170L, records.first().totalScore)
  }

  @Test
  fun loadExamProgress_unauthenticatedUser_returnsNull() = runBlocking {
    auth.signOut()
    val repository = FirestoreExamRepository(firestore, auth)

    val result = withTimeout(DEFAULT_TIMEOUT_MS) {
      repository.loadExamProgress()
    }
    assertNull(result)
  }

  private companion object {
    const val ALICE_EMAIL = "alice@cbt.com"
    const val DEFAULT_TIMEOUT_MS = 6000L
    const val FLOW_TIMEOUT_MS = 5000L
  }
}
