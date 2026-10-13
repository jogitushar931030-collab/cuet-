package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Question
import com.example.model.QuestionState
import com.example.model.Subject
import com.example.model.TestMode
import com.example.ui.components.NtaExamHeader
import com.example.ui.components.NtaQuestionPalette
import com.example.ui.components.QuestionPane
import com.example.ui.components.SubmitConfirmationModal

@Composable
fun ExamInterfaceScreen(
    subject: Subject,
    testMode: TestMode,
    candidateName: String = "Tushar",
    candidateRollNo: String = "CUET20261014",
    questions: List<Question>,
    currentIndex: Int,
    questionStates: Map<Int, QuestionState>,
    remainingSeconds: Long,
    isTimerWarning: Boolean,
    showSubmitModal: Boolean,
    isPaletteOpen: Boolean,
    isDarkMode: Boolean,
    onSelectOption: (Int) -> Unit,
    onSaveAndNext: () -> Unit,
    onSaveAndMarkForReview: () -> Unit,
    onMarkForReviewAndNext: () -> Unit,
    onClearResponse: () -> Unit,
    onNavigateToQuestion: (Int) -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onOpenSubmitDialog: () -> Unit,
    onCloseSubmitDialog: () -> Unit,
    onConfirmSubmit: () -> Unit,
    onTogglePalette: () -> Unit,
    onToggleDarkMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentQ = questions.getOrNull(currentIndex)
    val currentState = if (currentQ != null) questionStates[currentQ.id] else null

    Scaffold(
        modifier = modifier.fillMaxSize().testTag("exam_interface_screen"),
        topBar = {
            NtaExamHeader(
                subject = subject,
                testMode = testMode,
                candidateName = candidateName,
                candidateRollNo = candidateRollNo,
                remainingSeconds = remainingSeconds,
                isTimerWarning = isTimerWarning,
                isDarkMode = isDarkMode,
                isPaletteOpen = isPaletteOpen,
                onTogglePalette = onTogglePalette,
                onToggleDarkMode = onToggleDarkMode
            )
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            val isWideLayout = maxWidth >= 720.dp

            if (currentQ != null) {
                if (isWideLayout) {
                    // EXACT NTA CBT Split-Screen Layout: Question Pane on the Left (68%), Question Palette Grid on Right (32%)
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    ) {
                        QuestionPane(
                            question = currentQ,
                            questionState = currentState,
                            questionIndex = currentIndex,
                            totalQuestions = questions.size,
                            onSelectOption = onSelectOption,
                            onSaveAndNext = onSaveAndNext,
                            onSaveAndMarkForReview = onSaveAndMarkForReview,
                            onMarkForReviewAndNext = onMarkForReviewAndNext,
                            onClearResponse = onClearResponse,
                            onPrevious = onPrevious,
                            onNext = onNext,
                            onSubmit = onOpenSubmitDialog,
                            modifier = Modifier
                                .weight(0.68f)
                                .fillMaxHeight()
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        NtaQuestionPalette(
                            questions = questions,
                            questionStates = questionStates,
                            currentIndex = currentIndex,
                            onSelectQuestion = onNavigateToQuestion,
                            modifier = Modifier
                                .weight(0.32f)
                                .fillMaxHeight()
                        )
                    }
                } else {
                    // Mobile-first responsive layout with fast question switching and slide-over Question Palette
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            // Quick Mobile Palette Bar button
                            Surface(
                                onClick = onTogglePalette,
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 6.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.GridOn,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(end = 6.dp)
                                    )
                                    Text(
                                        text = "Question ${currentIndex + 1} of ${questions.size} • Tap to Open Palette Grid",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = if (isPaletteOpen) "CLOSE ✕" else "VIEW ▾",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            // Main Question Pane
                            QuestionPane(
                                question = currentQ,
                                questionState = currentState,
                                questionIndex = currentIndex,
                                totalQuestions = questions.size,
                                onSelectOption = onSelectOption,
                                onSaveAndNext = onSaveAndNext,
                                onSaveAndMarkForReview = onSaveAndMarkForReview,
                                onMarkForReviewAndNext = onMarkForReviewAndNext,
                                onClearResponse = onClearResponse,
                                onPrevious = onPrevious,
                                onNext = onNext,
                                onSubmit = onOpenSubmitDialog,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // Mobile Slide-in Question Palette Overlay
                        if (isPaletteOpen) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.5f))
                                    .clickable(onClick = onTogglePalette)
                            ) {
                                Card(
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .fillMaxHeight()
                                        .width(320.dp)
                                        .clickable(enabled = false) {}, // consume clicks
                                    shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Column(modifier = Modifier.fillMaxSize()) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(MaterialTheme.colorScheme.primary)
                                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "QUESTION PALETTE",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                modifier = Modifier.weight(1f)
                                            )
                                            IconButton(
                                                onClick = onTogglePalette,
                                                modifier = Modifier.testTag("close_palette_overlay_btn")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Close",
                                                    tint = Color.White
                                                )
                                            }
                                        }

                                        NtaQuestionPalette(
                                            questions = questions,
                                            questionStates = questionStates,
                                            currentIndex = currentIndex,
                                            onSelectQuestion = onNavigateToQuestion,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Submit Confirmation Modal
            if (showSubmitModal) {
                SubmitConfirmationModal(
                    questions = questions,
                    questionStates = questionStates,
                    remainingSeconds = remainingSeconds,
                    onDismiss = onCloseSubmitDialog,
                    onConfirmSubmit = onConfirmSubmit
                )
            }
        }
    }
}
