package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Question
import com.example.model.QuestionState
import com.example.ui.theme.NtaBluePrimary
import com.example.ui.theme.NtaStatusAnswered
import com.example.ui.theme.NtaStatusMarked
import com.example.ui.theme.NtaStatusNotAnswered

@Composable
fun QuestionPane(
    question: Question,
    questionState: QuestionState?,
    questionIndex: Int,
    totalQuestions: Int,
    onSelectOption: (Int) -> Unit,
    onSaveAndNext: () -> Unit,
    onSaveAndMarkForReview: () -> Unit,
    onMarkForReviewAndNext: () -> Unit,
    onClearResponse: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val selectedOption = questionState?.selectedOption

    Card(
        modifier = modifier.testTag("question_pane_card"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Question Sub-header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Question ${questionIndex + 1} of $totalQuestions",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = question.sectionName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Official Marking Scheme Badges
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        color = NtaStatusAnswered.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(4.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NtaStatusAnswered.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "+5.00",
                            color = NtaStatusAnswered,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Surface(
                        color = NtaStatusNotAnswered.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(4.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NtaStatusNotAnswered.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "-1.00",
                            color = NtaStatusNotAnswered,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Scrollable Content area (Passage + Question statement + Options)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
            ) {
                // If question has reading passage
                if (question.passageText != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = question.passageTitle ?: "Read the following passage carefully:",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            Text(
                                text = question.passageText,
                                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Question Text
                Text(
                    text = question.questionText,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 24.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(vertical = 10.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Options (A, B, C, D)
                val optionLabels = listOf("A", "B", "C", "D")
                question.options.forEachIndexed { optIndex, optionText ->
                    val isSelected = selectedOption == optIndex
                    val optionLabel = optionLabels.getOrElse(optIndex) { "${optIndex + 1}" }

                    OptionRow(
                        label = optionLabel,
                        text = optionText,
                        isSelected = isSelected,
                        onClick = { onSelectOption(optIndex) },
                        modifier = Modifier.testTag("option_$optIndex")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons in Official NTA CBT Layout
            NtaExamControls(
                isFirst = questionIndex == 0,
                isLast = questionIndex == totalQuestions - 1,
                hasSelectedOption = selectedOption != null,
                onSaveAndNext = onSaveAndNext,
                onSaveAndMark = onSaveAndMarkForReview,
                onMarkAndNext = onMarkForReviewAndNext,
                onClear = onClearResponse,
                onPrev = onPrevious,
                onNext = onNext,
                onSubmit = onSubmit
            )
        }
    }
}

@Composable
fun OptionRow(
    label: String,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    val bgColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface

    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Option identifier circle
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    lineHeight = 20.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NtaExamControls(
    isFirst: Boolean,
    isLast: Boolean,
    hasSelectedOption: Boolean,
    onSaveAndNext: () -> Unit,
    onSaveAndMark: () -> Unit,
    onMarkAndNext: () -> Unit,
    onClear: () -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Row 1: Primary Action Buttons
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Save & Next (Official NTA green-highlighted button)
            Button(
                onClick = onSaveAndNext,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NtaStatusAnswered,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("save_and_next_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Save & Next", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
            }

            // Save & Mark for Review
            Button(
                onClick = onSaveAndMark,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NtaStatusMarked,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("save_and_mark_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.BookmarkBorder,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Save & Mark Review", fontSize = 12.sp)
            }

            // Clear Response
            OutlinedButton(
                onClick = onClear,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.6f)),
                modifier = Modifier.testTag("clear_response_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Clear Response", fontSize = 12.sp)
            }

            // Mark for Review & Next
            OutlinedButton(
                onClick = onMarkAndNext,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = NtaStatusMarked
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, NtaStatusMarked.copy(alpha = 0.7f)),
                modifier = Modifier.testTag("mark_for_review_btn")
            ) {
                Text(text = "Mark Review & Next", fontSize = 12.sp)
            }
        }

        // Row 2: Navigation & Submit
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedButton(
                    onClick = onPrev,
                    enabled = !isFirst,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("prev_question_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Previous", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onNext,
                    enabled = !isLast,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("next_question_btn")
                ) {
                    Text("Next", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Submit Test (Always prominent NTA action)
            Button(
                onClick = onSubmit,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NtaBluePrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("submit_test_btn")
            ) {
                Text(
                    text = "Submit Test",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp
                )
            }
        }
    }
}
