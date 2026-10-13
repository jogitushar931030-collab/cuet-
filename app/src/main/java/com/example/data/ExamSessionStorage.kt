package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.QuestionState
import com.example.model.QuestionStatus
import com.example.model.Subject
import com.example.model.TestMode
import org.json.JSONArray
import org.json.JSONObject

class ExamSessionStorage(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("cuet_mock_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_HAS_ACTIVE_SESSION = "has_active_session"
        private const val KEY_SUBJECT_ID = "subject_id"
        private const val KEY_TEST_MODE = "test_mode"
        private const val KEY_REMAINING_SECONDS = "remaining_seconds"
        private const val KEY_CURRENT_INDEX = "current_index"
        private const val KEY_STATES_JSON = "states_json"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_LAST_SAVED_TIMESTAMP = "last_saved_timestamp"
    }

    fun saveSession(
        subject: Subject,
        testMode: TestMode,
        remainingSeconds: Long,
        currentIndex: Int,
        states: Map<Int, QuestionState>
    ) {
        val array = JSONArray()
        states.values.forEach { state ->
            val obj = JSONObject().apply {
                put("questionId", state.questionId)
                put("status", state.status.name)
                put("selectedOption", state.selectedOption ?: -1)
                put("timeSpent", state.timeSpentSeconds)
            }
            array.put(obj)
        }

        prefs.edit()
            .putBoolean(KEY_HAS_ACTIVE_SESSION, true)
            .putString(KEY_SUBJECT_ID, subject.id)
            .putString(KEY_TEST_MODE, testMode.name)
            .putLong(KEY_REMAINING_SECONDS, remainingSeconds)
            .putInt(KEY_CURRENT_INDEX, currentIndex)
            .putString(KEY_STATES_JSON, array.toString())
            .putLong(KEY_LAST_SAVED_TIMESTAMP, System.currentTimeMillis())
            .apply()
    }

    fun hasActiveSession(): Boolean = prefs.getBoolean(KEY_HAS_ACTIVE_SESSION, false)

    data class RestoredSession(
        val subject: Subject,
        val testMode: TestMode,
        val remainingSeconds: Long,
        val currentIndex: Int,
        val states: Map<Int, QuestionState>
    )

    fun restoreSession(): RestoredSession? {
        if (!hasActiveSession()) return null
        val subjectId = prefs.getString(KEY_SUBJECT_ID, null) ?: return null
        val subject = Subject.values().firstOrNull { it.id == subjectId } ?: Subject.GENERAL_TEST
        val modeStr = prefs.getString(KEY_TEST_MODE, TestMode.FULL_MOCK.name)
        val mode = try {
            TestMode.valueOf(modeStr ?: TestMode.FULL_MOCK.name)
        } catch (_: Exception) {
            TestMode.FULL_MOCK
        }
        val remaining = prefs.getLong(KEY_REMAINING_SECONDS, 2700L)
        val lastSavedTime = prefs.getLong(KEY_LAST_SAVED_TIMESTAMP, 0L)
        val elapsedSeconds = if (lastSavedTime > 0L && mode == TestMode.FULL_MOCK) {
            val diff = (System.currentTimeMillis() - lastSavedTime) / 1000L
            if (diff > 0) diff else 0L
        } else {
            0L
        }
        val effectiveRemaining = (remaining - elapsedSeconds).coerceAtLeast(0L)
        val currentIndex = prefs.getInt(KEY_CURRENT_INDEX, 0)
        val jsonStr = prefs.getString(KEY_STATES_JSON, "[]")

        val statesMap = mutableMapOf<Int, QuestionState>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val qId = obj.getInt("questionId")
                val statusName = obj.optString("status", QuestionStatus.NOT_VISITED.name)
                val status = try {
                    QuestionStatus.valueOf(statusName)
                } catch (_: Exception) {
                    QuestionStatus.NOT_VISITED
                }
                val selOpt = obj.optInt("selectedOption", -1)
                val time = obj.optLong("timeSpent", 0L)
                statesMap[qId] = QuestionState(
                    questionId = qId,
                    status = status,
                    selectedOption = if (selOpt >= 0) selOpt else null,
                    timeSpentSeconds = time
                )
            }
        } catch (_: Exception) {
            // fallback
        }

        return RestoredSession(
            subject = subject,
            testMode = mode,
            remainingSeconds = effectiveRemaining,
            currentIndex = currentIndex,
            states = statesMap
        )
    }

    fun clearSession() {
        prefs.edit().remove(KEY_HAS_ACTIVE_SESSION).apply()
    }

    fun isDarkMode(): Boolean = prefs.getBoolean(KEY_DARK_MODE, false)

    fun setDarkMode(dark: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_MODE, dark).apply()
    }
}
