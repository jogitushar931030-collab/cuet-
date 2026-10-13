package com.example.viewmodel

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.MainActivity
import com.example.model.Screen
import com.example.model.Subject
import com.example.model.TestMode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExamTimerRobolectricTest {

    private lateinit var app: Application
    private lateinit var viewModel: ExamViewModel

    @Before
    fun setUp() {
        app = ApplicationProvider.getApplicationContext()
        if (com.google.firebase.FirebaseApp.getApps(app).isEmpty()) {
            com.google.firebase.FirebaseApp.initializeApp(
                app,
                com.google.firebase.FirebaseOptions.Builder()
                    .setApplicationId("com.example.app")
                    .setProjectId("demo-no-project")
                    .setApiKey("fake-api-key")
                    .build()
            )
        }
        viewModel = ExamViewModel(app)
        viewModel.discardPreviousSession()
    }

    @Test
    fun `startExam initializes countdown timer for selected subject duration`() {
        viewModel.selectSubject(Subject.FULL_CUET_MOCK)
        viewModel.setTestMode(TestMode.FULL_MOCK)
        viewModel.startExam()

        val state = viewModel.uiState.value
        assertEquals(Screen.EXAM_INTERFACE, state.currentScreen)
        assertEquals(3600L, state.remainingSeconds)
        assertEquals(3600L, state.initialDurationSeconds)
        assertFalse(state.isTimerWarning)
        assertFalse(state.isAutoSubmitted)
    }

    @Test
    fun `timer warning flag activates when remaining time is 5 minutes or less`() {
        viewModel.startExam()
        assertFalse(viewModel.uiState.value.isTimerWarning)

        // Set to 300 seconds (5 minutes)
        viewModel.setRemainingSecondsForTesting(300L)
        assertTrue(viewModel.uiState.value.isTimerWarning)

        // Set to 60 seconds (1 minute)
        viewModel.setRemainingSecondsForTesting(60L)
        assertTrue(viewModel.uiState.value.isTimerWarning)

        // Set to 301 seconds (above warning threshold)
        viewModel.setRemainingSecondsForTesting(301L)
        assertFalse(viewModel.uiState.value.isTimerWarning)
    }

    @Test
    fun `auto submits exam when time expires`() {
        viewModel.startExam()
        assertEquals(Screen.EXAM_INTERFACE, viewModel.uiState.value.currentScreen)

        // Answer question 1
        viewModel.selectOption(0)
        viewModel.saveAndNext()

        // Force expiration of timer
        viewModel.forceExpireTimer()

        val state = viewModel.uiState.value
        assertEquals(Screen.RESULTS_DASHBOARD, state.currentScreen)
        assertTrue(state.isAutoSubmitted)
        assertNotNull(state.examSummary)
        assertTrue(state.examSummary!!.isAutoSubmitted)
        assertEquals(0L, state.remainingSeconds)
        assertFalse(state.showSubmitModal)
        assertFalse(state.isPaletteOpen)
    }

    @Test
    fun `topic practice mode does not count down or auto submit`() {
        viewModel.setTestMode(TestMode.TOPIC_PRACTICE)
        viewModel.startExam()

        val state = viewModel.uiState.value
        assertEquals(Screen.EXAM_INTERFACE, state.currentScreen)
        assertEquals(TestMode.TOPIC_PRACTICE, state.testMode)
        assertFalse(state.isTimerWarning)
        assertFalse(state.isAutoSubmitted)
    }

    @Test
    fun `exam state and timer persist through activity configuration changes`() {
        // Launch Activity via Robolectric
        val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
        val activity = controller.get()

        // Access the activity's ViewModel
        val activityViewModel = androidx.lifecycle.ViewModelProvider(activity)[ExamViewModel::class.java]

        // Start Exam and progress into the exam
        activityViewModel.startExam()
        activityViewModel.selectOption(1)
        activityViewModel.saveAndNext()
        activityViewModel.setRemainingSecondsForTesting(1500L)

        val beforeState = activityViewModel.uiState.value
        assertEquals(Screen.EXAM_INTERFACE, beforeState.currentScreen)
        assertEquals(1, beforeState.currentQuestionIndex)
        assertEquals(1500L, beforeState.remainingSeconds)

        // Simulate Android configuration change (recreating the Activity while preserving ViewModelStore)
        controller.configurationChange()

        val recreatedActivity = controller.get()
        val recreatedViewModel = androidx.lifecycle.ViewModelProvider(recreatedActivity)[ExamViewModel::class.java]

        val afterState = recreatedViewModel.uiState.value
        // State, current question, answers, and remaining time are strictly preserved!
        assertEquals(Screen.EXAM_INTERFACE, afterState.currentScreen)
        assertEquals(1, afterState.currentQuestionIndex)
        assertEquals(1500L, afterState.remainingSeconds)
        assertEquals(beforeState.questionStates[1]?.selectedOption, afterState.questionStates[1]?.selectedOption)
    }
}
