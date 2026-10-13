package com.example.model

data class CandidateProfile(
    val name: String = "Tushar",
    val rollNumber: String = "CUET20261014"
)

enum class Subject(
    val id: String,
    val title: String,
    val code: String,
    val questionCount: Int,
    val durationMinutes: Int,
    val description: String,
    val tag: String
) {
    FULL_CUET_MOCK(
        id = "full_cuet_mock",
        title = "CUET 50-MCQ Full Mock",
        code = "CUET-FULL-50",
        questionCount = 50,
        durationMinutes = 60,
        description = "Complete 50-question simulation across Language (Q1-15), General Test (Q16-35), and Domain subjects (Q36-50).",
        tag = "50 Questions • 60 Mins"
    ),
    CUET_SAMPLE_MOCK(
        id = "cuet_sample_mock",
        title = "CUET NTA Sample Test",
        code = "CUET-NTA-2026",
        questionCount = 5,
        durationMinutes = 15,
        description = "Official NTA portal sample questions across verbal, math, computer science, calculus & physics.",
        tag = "Core NTA Sample"
    ),
    GENERAL_TEST(
        id = "general_test",
        title = "General Test",
        code = "CUET-501",
        questionCount = 10,
        durationMinutes = 60,
        description = "Quantitative reasoning, logical thinking, arithmetic & general knowledge.",
        tag = "Section III"
    ),
    ENGLISH(
        id = "english",
        title = "English / Verbal Ability",
        code = "CUET-101",
        questionCount = 10,
        durationMinutes = 45,
        description = "Reading comprehension passages, vocabulary, idioms, grammar & verbal skills.",
        tag = "Section IA"
    ),
    MATHEMATICS(
        id = "mathematics",
        title = "Mathematics (Core & Applied)",
        code = "CUET-319",
        questionCount = 10,
        durationMinutes = 60,
        description = "Calculus, matrices, probability, vectors, differential equations & algebra.",
        tag = "Section II Domain"
    ),
    PHYSICS(
        id = "physics",
        title = "Physics",
        code = "CUET-322",
        questionCount = 10,
        durationMinutes = 45,
        description = "Electrostatics, electromagnetic induction, optics, semiconductor electronics.",
        tag = "Section II Domain"
    ),
    ECONOMICS(
        id = "economics",
        title = "Economics / Business Econ",
        code = "CUET-309",
        questionCount = 10,
        durationMinutes = 45,
        description = "Microeconomics, national income accounting, fiscal policy & Indian economy.",
        tag = "Section II Domain"
    )
}

enum class TestMode(val label: String, val description: String) {
    FULL_MOCK(
        label = "Full Mock Test",
        description = "Timed exam following strict NTA countdown with warning at 5 mins left"
    ),
    TOPIC_PRACTICE(
        label = "Topic Practice",
        description = "Untimed deep-dive session for leisurely practice and conceptual clarity"
    )
}

enum class QuestionStatus(val label: String) {
    NOT_VISITED("Not Visited"),
    NOT_ANSWERED("Not Answered"),
    ANSWERED("Answered"),
    MARKED_FOR_REVIEW("Marked for Review"),
    ANSWERED_AND_MARKED_FOR_REVIEW("Answered & Marked for Review")
}

data class Question(
    val id: Int,
    val subjectId: String,
    val sectionName: String,
    val passageTitle: String? = null,
    val passageText: String? = null,
    val questionText: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val explanation: String,
    val topicTag: String
)

data class QuestionState(
    val questionId: Int,
    val status: QuestionStatus = QuestionStatus.NOT_VISITED,
    val selectedOption: Int? = null,
    val timeSpentSeconds: Long = 0L
)

data class ExamSummary(
    val subjectTitle: String,
    val testMode: TestMode,
    val candidateName: String = "Tushar",
    val candidateRollNo: String = "CUET20261014",
    val totalQuestions: Int,
    val answeredCount: Int,
    val notAnsweredCount: Int,
    val markedCount: Int,
    val answeredAndMarkedCount: Int,
    val notVisitedCount: Int,
    val correctCount: Int,
    val incorrectCount: Int,
    val unattemptedCount: Int,
    val totalScore: Int,
    val maxScore: Int,
    val accuracyPercentage: Double,
    val totalTimeTakenSeconds: Long,
    val isAutoSubmitted: Boolean = false
)

enum class ReviewFilter(val title: String) {
    ALL("All Questions"),
    INCORRECT("Incorrect Only"),
    CORRECT("Correct Only"),
    UNATTEMPTED("Unattempted"),
    MARKED("Marked for Review")
}

enum class Screen {
    SUBJECT_SELECTION,
    EXAM_INTERFACE,
    RESULTS_DASHBOARD
}
