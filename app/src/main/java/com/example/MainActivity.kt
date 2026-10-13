package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.model.Screen
import com.example.ui.screens.ExamInterfaceScreen
import com.example.ui.screens.ResultsAnalyticsScreen
import com.example.ui.screens.SubjectSelectionScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.ExamViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ExamViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val uiState by viewModel.uiState.collectAsState()

            MyApplicationTheme(darkTheme = uiState.isDarkMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    when (uiState.currentScreen) {
                        Screen.SUBJECT_SELECTION -> {
                            SubjectSelectionScreen(
                                selectedSubject = uiState.selectedSubject,
                                selectedMode = uiState.testMode,
                                candidateName = uiState.candidateProfile.name,
                                candidateRollNo = uiState.candidateProfile.rollNumber,
                                hasRestorableSession = uiState.hasRestorableSession,
                                isDarkMode = uiState.isDarkMode,
                                onUpdateCandidate = { name, roll -> viewModel.updateCandidate(name, roll) },
                                onSelectSubject = { viewModel.selectSubject(it) },
                                onSelectMode = { viewModel.setTestMode(it) },
                                onStartExam = { viewModel.startExam() },
                                onResumeSession = { viewModel.restorePreviousSession() },
                                onDiscardSession = { viewModel.discardPreviousSession() },
                                onToggleDarkMode = { viewModel.toggleDarkMode() }
                            )
                        }

                        Screen.EXAM_INTERFACE -> {
                            // BackHandler prevents accidental app exit during exam, opens submit/leave confirmation
                            BackHandler {
                                viewModel.openSubmitDialog()
                            }

                            ExamInterfaceScreen(
                                subject = uiState.selectedSubject,
                                testMode = uiState.testMode,
                                candidateName = uiState.candidateProfile.name,
                                candidateRollNo = uiState.candidateProfile.rollNumber,
                                questions = uiState.questions,
                                currentIndex = uiState.currentQuestionIndex,
                                questionStates = uiState.questionStates,
                                remainingSeconds = uiState.remainingSeconds,
                                isTimerWarning = uiState.isTimerWarning,
                                showSubmitModal = uiState.showSubmitModal,
                                isPaletteOpen = uiState.isPaletteOpen,
                                isDarkMode = uiState.isDarkMode,
                                onSelectOption = { viewModel.selectOption(it) },
                                onSaveAndNext = { viewModel.saveAndNext() },
                                onSaveAndMarkForReview = { viewModel.saveAndMarkForReview() },
                                onMarkForReviewAndNext = { viewModel.markForReviewAndNext() },
                                onClearResponse = { viewModel.clearResponse() },
                                onNavigateToQuestion = { viewModel.navigateToQuestion(it) },
                                onPrevious = { viewModel.previousQuestion() },
                                onNext = { viewModel.nextQuestion() },
                                onOpenSubmitDialog = { viewModel.openSubmitDialog() },
                                onCloseSubmitDialog = { viewModel.closeSubmitDialog() },
                                onConfirmSubmit = { viewModel.submitExam() },
                                onTogglePalette = { viewModel.togglePalette() },
                                onToggleDarkMode = { viewModel.toggleDarkMode() }
                            )
                        }

                        Screen.RESULTS_DASHBOARD -> {
                            BackHandler {
                                viewModel.returnToSubjectSelection()
                            }

                            uiState.examSummary?.let { summary ->
                                ResultsAnalyticsScreen(
                                    summary = summary,
                                    questions = uiState.questions,
                                    questionStates = uiState.questionStates,
                                    reviewFilter = uiState.reviewFilter,
                                    isDarkMode = uiState.isDarkMode,
                                    onSetFilter = { viewModel.setReviewFilter(it) },
                                    onRetakeTest = { viewModel.retakeTest() },
                                    onSelectNewSubject = { viewModel.returnToSubjectSelection() },
                                    onToggleDarkMode = { viewModel.toggleDarkMode() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
