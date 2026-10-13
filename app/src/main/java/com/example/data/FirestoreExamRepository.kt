package com.example.data

import android.content.Context
import android.util.Log
import com.example.R
import com.example.model.ExamSummary
import com.example.model.QuestionState
import com.example.model.QuestionStatus
import com.example.model.Subject
import com.example.model.TestMode
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject

@IgnoreExtraProperties
data class ExamProgressDoc(
    @DocumentId val id: String = "",
    val userId: String = "",
    val subjectId: String = "",
    val subjectTitle: String = "",
    val testMode: String = "",
    val remainingSeconds: Long = 0L,
    val currentIndex: Int = 0,
    val answersJson: String = "",
    val candidateName: String = "",
    val candidateRollNo: String = "",
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class CompletedExamDoc(
    @DocumentId val id: String = "",
    val userId: String = "",
    val subjectId: String = "",
    val subjectTitle: String = "",
    val testMode: String = "",
    val candidateName: String = "",
    val candidateRollNo: String = "",
    val totalScore: Long = 0L,
    val maxScore: Long = 0L,
    val correctCount: Long = 0L,
    val incorrectCount: Long = 0L,
    val unattemptedCount: Long = 0L,
    val accuracyPercentage: Double = 0.0,
    val totalTimeTakenSeconds: Long = 0L,
    @ServerTimestamp val completedAt: Timestamp? = null
)

class FirestoreExamRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {

    companion object {
        fun getFirestoreInstance(context: Context): FirebaseFirestore {
            val appCtx = context.applicationContext
            val resId = appCtx.resources.getIdentifier("firestore_database_id", "string", appCtx.packageName)
            val dbId = if (resId != 0) appCtx.getString(resId) else "demo-database"
            return FirebaseFirestore.getInstance(dbId)
        }
    }

    constructor(context: Context) : this(
        getFirestoreInstance(context),
        Firebase.auth
    )

    private fun currentUserId(): String? = auth.currentUser?.uid

    suspend fun saveExamProgress(
        subject: Subject,
        testMode: TestMode,
        remainingSeconds: Long,
        currentIndex: Int,
        states: Map<Int, QuestionState>,
        candidateName: String,
        candidateRollNo: String
    ): Boolean {
        val uid = currentUserId() ?: return false
        val statesArray = JSONArray()
        states.values.forEach { state ->
            val obj = JSONObject().apply {
                put("questionId", state.questionId)
                put("status", state.status.name)
                put("selectedOption", state.selectedOption ?: -1)
                put("timeSpent", state.timeSpentSeconds)
            }
            statesArray.put(obj)
        }

        val docRef = db.collection("users").document(uid).collection("exam_progress").document("current")
        val payload = mapOf(
            "userId" to uid,
            "subjectId" to subject.id,
            "subjectTitle" to subject.title,
            "testMode" to testMode.name,
            "remainingSeconds" to remainingSeconds,
            "currentIndex" to currentIndex,
            "answersJson" to statesArray.toString(),
            "candidateName" to candidateName,
            "candidateRollNo" to candidateRollNo,
            "updatedAt" to FieldValue.serverTimestamp()
        )

        return try {
            docRef.set(payload).await()
            true
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            System.err.println("SAVE_EXAM_PROGRESS_FAILED: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    suspend fun loadExamProgress(): ExamProgressDoc? {
        val uid = currentUserId() ?: run {
            System.err.println("LOAD_EXAM_PROGRESS_UID_NULL")
            return null
        }
        val docRef = db.collection("users").document(uid).collection("exam_progress").document("current")
        return try {
            val snapshot = docRef.get().await()
            System.err.println("LOAD_SNAPSHOT: exists=${snapshot.exists()} data=${snapshot.data}")
            if (snapshot.exists()) {
                val obj = snapshot.toObject(
                    ExamProgressDoc::class.java,
                    com.google.firebase.firestore.DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
                System.err.println("LOAD_OBJ: $obj")
                obj
            } else null
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, docRef.path)
            System.err.println("LOAD_EXAM_PROGRESS_FAILED: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    suspend fun clearExamProgress(): Boolean {
        val uid = currentUserId() ?: return false
        val docRef = db.collection("users").document(uid).collection("exam_progress").document("current")
        return try {
            docRef.delete().await()
            true
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            false
        }
    }

    suspend fun saveCompletedExam(summary: ExamSummary): Boolean {
        val uid = currentUserId() ?: return false
        val colRef = db.collection("users").document(uid).collection("completed_exams")
        val payload = mapOf(
            "userId" to uid,
            "subjectId" to summary.subjectTitle,
            "subjectTitle" to summary.subjectTitle,
            "testMode" to summary.testMode.name,
            "candidateName" to summary.candidateName,
            "candidateRollNo" to summary.candidateRollNo,
            "totalScore" to summary.totalScore.toLong(),
            "maxScore" to summary.maxScore.toLong(),
            "correctCount" to summary.correctCount.toLong(),
            "incorrectCount" to summary.incorrectCount.toLong(),
            "unattemptedCount" to summary.unattemptedCount.toLong(),
            "accuracyPercentage" to summary.accuracyPercentage,
            "totalTimeTakenSeconds" to summary.totalTimeTakenSeconds,
            "completedAt" to FieldValue.serverTimestamp()
        )

        return try {
            colRef.add(payload).await()
            clearExamProgress()
            true
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, colRef.path)
            false
        }
    }

    fun observeCompletedExams(): Flow<List<CompletedExamDoc>> = flow {
        val uid = currentUserId() ?: return@flow
        val colRef = db.collection("users").document(uid).collection("completed_exams")
        colRef.snapshots().map { snapshot ->
            snapshot.toObjects(CompletedExamDoc::class.java)
        }.catch { e ->
            if (e is Exception) handleFirestoreError(e, OperationType.LIST, colRef.path)
            throw e
        }.collect { list ->
            emit(list)
        }
    }
}
