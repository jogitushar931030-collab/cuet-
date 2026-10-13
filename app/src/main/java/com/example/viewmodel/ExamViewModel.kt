package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ExamSessionStorage
import com.example.data.QuestionBank
import com.example.model.CandidateProfile
import com.example.model.ExamSummary
import com.example.model.Question
import com.example.model.QuestionState
import com.example.model.QuestionStatus
import com.example.model.ReviewFilter
import com.example.model.Screen
import com.example.model.Subject
import com.example.model.TestMode
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import com.example.data.ExamProgressDoc
import com.example.data.FirestoreExamRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

data class ExamUiState(
    val currentScreen: Screen = Screen.SUBJECT_SELECTION,
    val candidateProfile: CandidateProfile = CandidateProfile(),
    val selectedSubject: Subject = Subject.FULL_CUET_MOCK,
    val testMode: TestMode = TestMode.FULL_MOCK,
    val questions: List<Question> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val questionStates: Map<Int, QuestionState> = emptyMap(),
    val remainingSeconds: Long = 3600L,
    val initialDurationSeconds: Long = 3600L,
    val isTimerWarning: Boolean = false,
    val showSubmitModal: Boolean = false,
    val isPaletteOpen: Boolean = false,
    val isDarkMode: Boolean = false,
    val reviewFilter: ReviewFilter = ReviewFilter.ALL,
    val examSummary: ExamSummary? = null,
    val hasRestorableSession: Boolean = false,
    val isCloudConnected: Boolean = false,
    val userEmail: String? = null,
    val hasCloudProgress: Boolean = false,
    val cloudProgressDoc: ExamProgressDoc? = null,
    val isAutoSubmitted: Boolean = false
)

class ExamViewModel(application: Application) : AndroidViewModel(application) {

    private val storage = ExamSessionStorage(application)
    private val firestoreRepo = FirestoreExamRepository(application)
    private var authListener: FirebaseAuth.AuthStateListener? = null

    private val _uiState = MutableStateFlow(
        ExamUiState(
            isDarkMode = storage.isDarkMode(),
            hasRestorableSession = storage.hasActiveSession()
        )
    )
    val uiState: StateFlow<ExamUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var targetEndTimeMillis: Long? = null

    init {
        // Prepare initial questions
        val initialQuestions = QuestionBank.getQuestionsForSubject(Subject.FULL_CUET_MOCK)
        _uiState.update { it.copy(questions = initialQuestions) }

        // Setup Firebase Auth observer safely
        try {
            val initialUser = Firebase.auth.currentUser
            _uiState.update {
                it.copy(
                    isCloudConnected = initialUser != null,
                    userEmail = initialUser?.email
                )
            }
            if (initialUser != null) {
                checkCloudProgress()
            }

            authListener = FirebaseAuth.AuthStateListener { auth ->
                val u = auth.currentUser
                _uiState.update {
                    it.copy(
                        isCloudConnected = u != null,
                        userEmail = u?.email
                    )
                }
                if (u != null) {
                    checkCloudProgress()
                } else {
                    _uiState.update { it.copy(hasCloudProgress = false, cloudProgressDoc = null) }
                }
            }
            Firebase.auth.addAuthStateListener(authListener!!)
        } catch (_: Exception) {
            // Firebase Auth not initialized or unavailable in unit test harness
        }
    }

    fun updateCandidate(name: String, rollNo: String) {
        _uiState.update {
            it.copy(
                candidateProfile = CandidateProfile(
                    name = name.ifBlank { "Tushar" },
                    rollNumber = rollNo.ifBlank { "CUET20261014" }
                )
            )
        }
    }

    fun selectSubject(subject: Subject) {
        val questions = QuestionBank.getQuestionsForSubject(subject)
        val initialSeconds = subject.durationMinutes * 60L
        _uiState.update {
            it.copy(
                selectedSubject = subject,
                questions = questions,
                remainingSeconds = initialSeconds,
                initialDurationSeconds = initialSeconds
            )
        }
    }

    fun setTestMode(mode: TestMode) {
        _uiState.update { it.copy(testMode = mode) }
    }

    fun startExam() {
        val state = _uiState.value
        val questions = state.questions
        val initialSeconds = state.selectedSubject.durationMinutes * 60L

        val initialStates = questions.associate { q ->
            q.id to QuestionState(questionId = q.id, status = QuestionStatus.NOT_VISITED)
        }.toMutableMap()

        // First question is immediately visited (Not Answered if not already answered)
        if (questions.isNotEmpty()) {
            val firstId = questions[0].id
            initialStates[firstId] = QuestionState(questionId = firstId, status = QuestionStatus.NOT_ANSWERED)
        }

        targetEndTimeMillis = null
        _uiState.update {
            it.copy(
                currentScreen = Screen.EXAM_INTERFACE,
                currentQuestionIndex = 0,
                questionStates = initialStates,
                remainingSeconds = initialSeconds,
                initialDurationSeconds = initialSeconds,
                isTimerWarning = false,
                showSubmitModal = false,
                isPaletteOpen = false,
                hasRestorableSession = false,
                isAutoSubmitted = false
            )
        }

        startTimer()
        saveSessionToStorage()
    }

    fun restorePreviousSession() {
        val session = storage.restoreSession() ?: return
        val questions = QuestionBank.getQuestionsForSubject(session.subject)

        if (session.testMode == TestMode.FULL_MOCK && session.remainingSeconds <= 0L) {
            _uiState.update {
                it.copy(
                    currentScreen = Screen.EXAM_INTERFACE,
                    selectedSubject = session.subject,
                    testMode = session.testMode,
                    questions = questions,
                    currentQuestionIndex = session.currentIndex.coerceIn(0, (questions.size - 1).coerceAtLeast(0)),
                    questionStates = session.states,
                    remainingSeconds = 0L,
                    initialDurationSeconds = session.subject.durationMinutes * 60L,
                    hasRestorableSession = false,
                    isAutoSubmitted = true
                )
            }
            submitExam(isAutoSubmit = true)
            return
        }

        _uiState.update {
            it.copy(
                currentScreen = Screen.EXAM_INTERFACE,
                selectedSubject = session.subject,
                testMode = session.testMode,
                questions = questions,
                currentQuestionIndex = session.currentIndex.coerceIn(0, (questions.size - 1).coerceAtLeast(0)),
                questionStates = session.states,
                remainingSeconds = session.remainingSeconds,
                initialDurationSeconds = session.subject.durationMinutes * 60L,
                isTimerWarning = session.testMode == TestMode.FULL_MOCK && session.remainingSeconds in 1..300,
                hasRestorableSession = false,
                isAutoSubmitted = false
            )
        }
        startTimer()
    }

    fun discardPreviousSession() {
        storage.clearSession()
        _uiState.update { it.copy(hasRestorableSession = false) }
    }

    internal fun startTimer() {
        timerJob?.cancel()
        if (_uiState.value.testMode == TestMode.TOPIC_PRACTICE) {
            // Untimed mode: counts upward or idle
            return
        }

        if (_uiState.value.remainingSeconds <= 0L) {
            submitExam(isAutoSubmit = true)
            return
        }

        val currentSeconds = _uiState.value.remainingSeconds
        targetEndTimeMillis = android.os.SystemClock.elapsedRealtime() + (currentSeconds * 1000L)

        timerJob = viewModelScope.launch {
            while (_uiState.value.remainingSeconds > 0L) {
                delay(1000L)
                val target = targetEndTimeMillis
                val calculatedRemaining = if (target != null) {
                    ((target - android.os.SystemClock.elapsedRealtime() + 500L) / 1000L).coerceAtLeast(0L)
                } else {
                    (_uiState.value.remainingSeconds - 1L).coerceAtLeast(0L)
                }

                _uiState.update { current ->
                    val isWarning = calculatedRemaining in 1..300

                    // Increment time spent on current question
                    val currentQ = current.questions.getOrNull(current.currentQuestionIndex)
                    val updatedStates = if (currentQ != null) {
                        val existingState = current.questionStates[currentQ.id]
                            ?: QuestionState(currentQ.id)
                        current.questionStates + (currentQ.id to existingState.copy(
                            timeSpentSeconds = existingState.timeSpentSeconds + 1
                        ))
                    } else {
                        current.questionStates
                    }

                    current.copy(
                        remainingSeconds = calculatedRemaining,
                        isTimerWarning = isWarning,
                        questionStates = updatedStates
                    )
                }

                if (_uiState.value.remainingSeconds % 10 == 0L) {
                    saveSessionToStorage()
                }

                if (_uiState.value.remainingSeconds <= 0L) {
                    submitExam(isAutoSubmit = true)
                    break
                }
            }
        }
    }

    fun setRemainingSecondsForTesting(seconds: Long) {
        val safeSeconds = seconds.coerceAtLeast(0L)
        targetEndTimeMillis = android.os.SystemClock.elapsedRealtime() + (safeSeconds * 1000L)
        _uiState.update {
            it.copy(
                remainingSeconds = safeSeconds,
                isTimerWarning = safeSeconds in 1..300
            )
        }
    }

    fun forceExpireTimer() {
        targetEndTimeMillis = null
        _uiState.update { it.copy(remainingSeconds = 0L, isTimerWarning = false) }
        submitExam(isAutoSubmit = true)
    }

    fun selectOption(optionIndex: Int) {
        val currentQ = currentQuestion() ?: return
        _uiState.update { current ->
            val existing = current.questionStates[currentQ.id] ?: QuestionState(currentQ.id)
            val newSelected = if (existing.selectedOption == optionIndex) null else optionIndex
            val updated = existing.copy(
                selectedOption = newSelected,
                // Status remains visible until Save & Next or Mark
                status = if (newSelected == null) QuestionStatus.NOT_ANSWERED else existing.status
            )
            current.copy(questionStates = current.questionStates + (currentQ.id to updated))
        }
        saveSessionToStorage()
    }

    fun saveAndNext() {
        val currentQ = currentQuestion() ?: return
        _uiState.update { current ->
            val existing = current.questionStates[currentQ.id] ?: QuestionState(currentQ.id)
            val newStatus = if (existing.selectedOption != null) {
                QuestionStatus.ANSWERED
            } else {
                QuestionStatus.NOT_ANSWERED
            }
            val updatedStates = current.questionStates + (currentQ.id to existing.copy(status = newStatus))

            val nextIndex = (current.currentQuestionIndex + 1).coerceAtMost(current.questions.size - 1)
            val nextQ = current.questions.getOrNull(nextIndex)
            val finalStates = if (nextQ != null && updatedStates[nextQ.id]?.status == QuestionStatus.NOT_VISITED) {
                updatedStates + (nextQ.id to QuestionState(nextQ.id, QuestionStatus.NOT_ANSWERED))
            } else {
                updatedStates
            }

            current.copy(
                questionStates = finalStates,
                currentQuestionIndex = nextIndex
            )
        }
        saveSessionToStorage()
    }

    fun saveAndMarkForReview() {
        val currentQ = currentQuestion() ?: return
        _uiState.update { current ->
            val existing = current.questionStates[currentQ.id] ?: QuestionState(currentQ.id)
            val newStatus = if (existing.selectedOption != null) {
                QuestionStatus.ANSWERED_AND_MARKED_FOR_REVIEW
            } else {
                QuestionStatus.MARKED_FOR_REVIEW
            }
            val updatedStates = current.questionStates + (currentQ.id to existing.copy(status = newStatus))

            val nextIndex = (current.currentQuestionIndex + 1).coerceAtMost(current.questions.size - 1)
            val nextQ = current.questions.getOrNull(nextIndex)
            val finalStates = if (nextQ != null && updatedStates[nextQ.id]?.status == QuestionStatus.NOT_VISITED) {
                updatedStates + (nextQ.id to QuestionState(nextQ.id, QuestionStatus.NOT_ANSWERED))
            } else {
                updatedStates
            }

            current.copy(
                questionStates = finalStates,
                currentQuestionIndex = nextIndex
            )
        }
        saveSessionToStorage()
    }

    fun markForReviewAndNext() {
        val currentQ = currentQuestion() ?: return
        _uiState.update { current ->
            val existing = current.questionStates[currentQ.id] ?: QuestionState(currentQ.id)
            val newStatus = if (existing.selectedOption != null) {
                QuestionStatus.ANSWERED_AND_MARKED_FOR_REVIEW
            } else {
                QuestionStatus.MARKED_FOR_REVIEW
            }
            val updatedStates = current.questionStates + (currentQ.id to existing.copy(status = newStatus))

            val nextIndex = (current.currentQuestionIndex + 1).coerceAtMost(current.questions.size - 1)
            val nextQ = current.questions.getOrNull(nextIndex)
            val finalStates = if (nextQ != null && updatedStates[nextQ.id]?.status == QuestionStatus.NOT_VISITED) {
                updatedStates + (nextQ.id to QuestionState(nextQ.id, QuestionStatus.NOT_ANSWERED))
            } else {
                updatedStates
            }

            current.copy(
                questionStates = finalStates,
                currentQuestionIndex = nextIndex
            )
        }
        saveSessionToStorage()
    }

    fun clearResponse() {
        val currentQ = currentQuestion() ?: return
        _uiState.update { current ->
            val existing = current.questionStates[currentQ.id] ?: QuestionState(currentQ.id)
            val updated = existing.copy(
                selectedOption = null,
                status = QuestionStatus.NOT_ANSWERED
            )
            current.copy(questionStates = current.questionStates + (currentQ.id to updated))
        }
        saveSessionToStorage()
    }

    fun navigateToQuestion(index: Int) {
        val state = _uiState.value
        if (index !in state.questions.indices) return

        _uiState.update { current ->
            val departingQ = current.questions.getOrNull(current.currentQuestionIndex)
            val intermediateStates = if (departingQ != null) {
                val depState = current.questionStates[departingQ.id] ?: QuestionState(departingQ.id)
                if (depState.status == QuestionStatus.NOT_VISITED || depState.status == QuestionStatus.NOT_ANSWERED) {
                    val syncedStatus = if (depState.selectedOption != null) QuestionStatus.ANSWERED else QuestionStatus.NOT_ANSWERED
                    current.questionStates + (departingQ.id to depState.copy(status = syncedStatus))
                } else {
                    current.questionStates
                }
            } else {
                current.questionStates
            }

            val targetQ = current.questions[index]
            val existing = intermediateStates[targetQ.id]
            val updatedStates = if (existing == null || existing.status == QuestionStatus.NOT_VISITED) {
                intermediateStates + (targetQ.id to QuestionState(targetQ.id, QuestionStatus.NOT_ANSWERED))
            } else {
                intermediateStates
            }
            current.copy(
                currentQuestionIndex = index,
                questionStates = updatedStates,
                isPaletteOpen = false // auto-close palette on mobile when selecting
            )
        }
        saveSessionToStorage()
    }

    fun previousQuestion() {
        val currentIndex = _uiState.value.currentQuestionIndex
        if (currentIndex > 0) {
            navigateToQuestion(currentIndex - 1)
        }
    }

    fun nextQuestion() {
        val state = _uiState.value
        if (state.currentQuestionIndex < state.questions.size - 1) {
            navigateToQuestion(state.currentQuestionIndex + 1)
        }
    }

    fun openSubmitDialog() {
        _uiState.update { it.copy(showSubmitModal = true) }
    }

    fun closeSubmitDialog() {
        _uiState.update { it.copy(showSubmitModal = false) }
    }

    fun submitExam(isAutoSubmit: Boolean = false) {
        timerJob?.cancel()
        targetEndTimeMillis = null
        storage.clearSession()

        val state = _uiState.value
        val questions = state.questions
        val states = state.questionStates

        var answered = 0
        var notAnswered = 0
        var marked = 0
        var answeredAndMarked = 0
        var notVisited = 0

        var correct = 0
        var incorrect = 0
        var unattempted = 0

        questions.forEach { q ->
            val qState = states[q.id] ?: QuestionState(q.id)
            when (qState.status) {
                QuestionStatus.ANSWERED -> answered++
                QuestionStatus.NOT_ANSWERED -> notAnswered++
                QuestionStatus.MARKED_FOR_REVIEW -> marked++
                QuestionStatus.ANSWERED_AND_MARKED_FOR_REVIEW -> answeredAndMarked++
                QuestionStatus.NOT_VISITED -> notVisited++
            }

            // In official NTA CUET CBT:
            // Both "Answered" and "Answered & Marked for Review" are evaluated!
            val hasAnswer = qState.selectedOption != null
            if (hasAnswer) {
                if (qState.selectedOption == q.correctOptionIndex) {
                    correct++
                } else {
                    incorrect++
                }
            } else {
                unattempted++
            }
        }

        // Marking scheme: +5 for Correct, -1 for Incorrect, 0 for Unattempted
        val totalScore = (correct * 5) - (incorrect * 1)
        val maxScore = questions.size * 5
        val totalAttempted = correct + incorrect
        val accuracy = if (totalAttempted > 0) {
            (correct.toDouble() / totalAttempted.toDouble()) * 100.0
        } else 0.0

        val totalTimeTaken = state.initialDurationSeconds - state.remainingSeconds

        val summary = ExamSummary(
            subjectTitle = state.selectedSubject.title,
            testMode = state.testMode,
            candidateName = state.candidateProfile.name,
            candidateRollNo = state.candidateProfile.rollNumber,
            totalQuestions = questions.size,
            answeredCount = answered,
            notAnsweredCount = notAnswered,
            markedCount = marked,
            answeredAndMarkedCount = answeredAndMarked,
            notVisitedCount = notVisited,
            correctCount = correct,
            incorrectCount = incorrect,
            unattemptedCount = unattempted,
            totalScore = totalScore,
            maxScore = maxScore,
            accuracyPercentage = accuracy,
            totalTimeTakenSeconds = totalTimeTaken.coerceAtLeast(0L),
            isAutoSubmitted = isAutoSubmit
        )

        if (Firebase.auth.currentUser != null) {
            viewModelScope.launch {
                firestoreRepo.saveCompletedExam(summary)
            }
        }

        _uiState.update {
            it.copy(
                currentScreen = Screen.RESULTS_DASHBOARD,
                showSubmitModal = false,
                isPaletteOpen = false,
                examSummary = summary,
                reviewFilter = ReviewFilter.ALL,
                isAutoSubmitted = isAutoSubmit
            )
        }
    }

    fun retakeTest() {
        startExam()
    }

    fun returnToSubjectSelection() {
        timerJob?.cancel()
        targetEndTimeMillis = null
        storage.clearSession()
        _uiState.update {
            it.copy(
                currentScreen = Screen.SUBJECT_SELECTION,
                showSubmitModal = false,
                isPaletteOpen = false,
                examSummary = null,
                isAutoSubmitted = false
            )
        }
    }

    fun togglePalette() {
        _uiState.update { it.copy(isPaletteOpen = !it.isPaletteOpen) }
    }

    fun toggleDarkMode() {
        val newMode = !_uiState.value.isDarkMode
        storage.setDarkMode(newMode)
        _uiState.update { it.copy(isDarkMode = newMode) }
    }

    fun setReviewFilter(filter: ReviewFilter) {
        _uiState.update { it.copy(reviewFilter = filter) }
    }

    private fun currentQuestion(): Question? {
        val state = _uiState.value
        return state.questions.getOrNull(state.currentQuestionIndex)
    }

    fun checkCloudProgress() {
        viewModelScope.launch {
            val doc = firestoreRepo.loadExamProgress()
            _uiState.update {
                it.copy(
                    hasCloudProgress = doc != null,
                    cloudProgressDoc = doc
                )
            }
        }
    }

    fun restoreCloudProgress() {
        val doc = _uiState.value.cloudProgressDoc ?: return
        val subject = Subject.values().firstOrNull { it.id == doc.subjectId } ?: Subject.FULL_CUET_MOCK
        val mode = try { TestMode.valueOf(doc.testMode) } catch (_: Exception) { TestMode.FULL_MOCK }
        val questions = QuestionBank.getQuestionsForSubject(subject)

        val statesMap = mutableMapOf<Int, QuestionState>()
        try {
            val array = org.json.JSONArray(doc.answersJson)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val qId = obj.getInt("questionId")
                val statusName = obj.optString("status", QuestionStatus.NOT_VISITED.name)
                val status = try { QuestionStatus.valueOf(statusName) } catch (_: Exception) { QuestionStatus.NOT_VISITED }
                val selOpt = obj.optInt("selectedOption", -1)
                val time = obj.optLong("timeSpent", 0L)
                statesMap[qId] = QuestionState(qId, status, if (selOpt >= 0) selOpt else null, time)
            }
        } catch (_: Exception) {}

        _uiState.update {
            it.copy(
                currentScreen = Screen.EXAM_INTERFACE,
                selectedSubject = subject,
                testMode = mode,
                candidateProfile = CandidateProfile(
                    name = doc.candidateName.ifBlank { "Tushar" },
                    rollNumber = doc.candidateRollNo.ifBlank { "CUET20261014" }
                ),
                questions = questions,
                currentQuestionIndex = doc.currentIndex.coerceIn(0, (questions.size - 1).coerceAtLeast(0)),
                questionStates = statesMap,
                remainingSeconds = doc.remainingSeconds,
                initialDurationSeconds = subject.durationMinutes * 60L,
                isTimerWarning = mode == TestMode.FULL_MOCK && doc.remainingSeconds in 1..300,
                hasCloudProgress = false,
                hasRestorableSession = false
            )
        }
        startTimer()
    }

    fun discardCloudProgress() {
        viewModelScope.launch {
            firestoreRepo.clearExamProgress()
            _uiState.update { it.copy(hasCloudProgress = false, cloudProgressDoc = null) }
        }
    }

    private fun saveSessionToStorage() {
        val state = _uiState.value
        if (state.currentScreen == Screen.EXAM_INTERFACE) {
            storage.saveSession(
                subject = state.selectedSubject,
                testMode = state.testMode,
                remainingSeconds = state.remainingSeconds,
                currentIndex = state.currentQuestionIndex,
                states = state.questionStates
            )
            // Persist to Cloud Firestore when authenticated
            try {
                if (Firebase.auth.currentUser != null) {
                    viewModelScope.launch {
                        firestoreRepo.saveExamProgress(
                            subject = state.selectedSubject,
                            testMode = state.testMode,
                            remainingSeconds = state.remainingSeconds,
                            currentIndex = state.currentQuestionIndex,
                            states = state.questionStates,
                            candidateName = state.candidateProfile.name,
                            candidateRollNo = state.candidateProfile.rollNumber
                        )
                    }
                }
            } catch (_: Exception) {
                // Firebase not initialized
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        try {
            authListener?.let { Firebase.auth.removeAuthStateListener(it) }
        } catch (_: Exception) {
            // Firebase not initialized
        }
    }
}
