package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ExamSummary
import com.example.model.Question
import com.example.model.QuestionState
import com.example.model.QuestionStatus
import com.example.model.ReviewFilter
import com.example.ui.theme.NtaBluePrimary
import com.example.ui.theme.NtaGold
import com.example.ui.theme.NtaStatusAnswered
import com.example.ui.theme.NtaStatusMarked
import com.example.ui.theme.NtaStatusNotAnswered

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ResultsAnalyticsScreen(
    summary: ExamSummary,
    questions: List<Question>,
    questionStates: Map<Int, QuestionState>,
    reviewFilter: ReviewFilter,
    isDarkMode: Boolean,
    onSetFilter: (ReviewFilter) -> Unit,
    onRetakeTest: () -> Unit,
    onSelectNewSubject: () -> Unit,
    onToggleDarkMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Filter questions according to review filter
    val filteredQuestions = questions.filter { q ->
        val state = questionStates[q.id] ?: QuestionState(q.id)
        val isAttempted = state.selectedOption != null
        val isCorrect = isAttempted && state.selectedOption == q.correctOptionIndex
        val isIncorrect = isAttempted && state.selectedOption != q.correctOptionIndex

        when (reviewFilter) {
            ReviewFilter.ALL -> true
            ReviewFilter.INCORRECT -> isIncorrect
            ReviewFilter.CORRECT -> isCorrect
            ReviewFilter.UNATTEMPTED -> !isAttempted
            ReviewFilter.MARKED -> state.status == QuestionStatus.MARKED_FOR_REVIEW ||
                    state.status == QuestionStatus.ANSWERED_AND_MARKED_FOR_REVIEW
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize().testTag("results_dashboard_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "CUET Performance Scorecard",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onSelectNewSubject,
                        modifier = Modifier.testTag("results_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Return to subjects",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onToggleDarkMode,
                        modifier = Modifier.testTag("results_dark_mode_btn")
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Dark Mode",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NtaBluePrimary)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Top Score Card
                ScoreSummaryCard(summary = summary)
            }

            item {
                // Secondary Metrics Row
                MetricsDetailRow(summary = summary)
            }

            item {
                // Action Buttons: Retake Test & Choose Another Subject
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onRetakeTest,
                        colors = ButtonDefaults.buttonColors(containerColor = NtaBluePrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).testTag("retake_test_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Retake Test", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onSelectNewSubject,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).testTag("choose_subject_btn")
                    ) {
                        Text("Choose Subject")
                    }
                }
            }

            item {
                // Solution Review Header & Filters
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "QUESTION-BY-QUESTION REVIEW",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Showing ${filteredQuestions.size} of ${questions.size}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Filter chips row
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        ReviewFilter.values().forEach { filter ->
                            FilterChip(
                                selected = reviewFilter == filter,
                                onClick = { onSetFilter(filter) },
                                label = { Text(filter.title, fontSize = 12.sp) },
                                leadingIcon = if (reviewFilter == filter) {
                                    { Icon(Icons.Default.Check, null, Modifier.size(14.dp)) }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                modifier = Modifier.testTag("filter_${filter.name}")
                            )
                        }
                    }
                }
            }

            // Question solutions list
            itemsIndexed(filteredQuestions) { _, question ->
                val state = questionStates[question.id] ?: QuestionState(question.id)
                SolutionCard(question = question, questionState = state)
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun ScoreSummaryCard(summary: ExamSummary) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("score_summary_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = summary.subjectTitle,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Candidate: ${summary.candidateName} • Roll No: ${summary.candidateRollNo}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (summary.isAutoSubmitted) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = Color(0xFFFEF2F2),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5)),
                    modifier = Modifier.fillMaxWidth().testTag("auto_submit_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Time Expired",
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Exam Auto-Submitted (Time Expired)",
                                color = Color(0xFF991B1B),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "The countdown timer reached 00:00. All responses were auto-saved and evaluated.",
                                color = Color(0xFFB91C1C),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Score Highlight
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${summary.totalScore}",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                    color = if (summary.totalScore >= 0) NtaStatusAnswered else NtaStatusNotAnswered
                )
                Text(
                    text = " / ${summary.maxScore} Marks",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4 Key metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricPill(
                    count = "${summary.correctCount}",
                    label = "Correct (+5)",
                    color = NtaStatusAnswered
                )
                MetricPill(
                    count = "${summary.incorrectCount}",
                    label = "Incorrect (-1)",
                    color = NtaStatusNotAnswered
                )
                MetricPill(
                    count = "${summary.unattemptedCount}",
                    label = "Unattempted (0)",
                    color = Color.Gray
                )
                MetricPill(
                    count = String.format("%.1f%%", summary.accuracyPercentage),
                    label = "Accuracy",
                    color = NtaBluePrimary
                )
            }
        }
    }
}

@Composable
fun MetricPill(count: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            color = color.copy(alpha = 0.12f),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.35f))
        ) {
            Text(
                text = count,
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 10.5.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun MetricsDetailRow(summary: ExamSummary) {
    val totalMins = summary.totalTimeTakenSeconds / 60
    val totalSecs = summary.totalTimeTakenSeconds % 60
    val formattedTime = String.format("%02dm %02ds", totalMins, totalSecs)

    val avgSecs = if (summary.totalQuestions > 0) summary.totalTimeTakenSeconds / summary.totalQuestions else 0L

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Timer, contentDescription = null, tint = NtaBluePrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = formattedTime, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Text(text = "Total Time Taken", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "${avgSecs}s / Question", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(text = "Avg Speed", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "${summary.answeredCount + summary.answeredAndMarkedCount} / ${summary.totalQuestions}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(text = "NTA Evaluated", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun SolutionCard(
    question: Question,
    questionState: QuestionState,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    val userSelected = questionState.selectedOption
    val isAttempted = userSelected != null
    val isCorrect = isAttempted && userSelected == question.correctOptionIndex

    val statusColor = when {
        !isAttempted -> Color.Gray
        isCorrect -> NtaStatusAnswered
        else -> NtaStatusNotAnswered
    }

    val statusLabel = when {
        !isAttempted -> "Unattempted (0)"
        isCorrect -> "Correct (+5)"
        else -> "Incorrect (-1)"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("solution_q_${question.id}"),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = statusColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "Q${question.id}",
                            color = statusColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = question.topicTag,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = statusLabel,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Question Text
            Text(
                text = question.questionText,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Options summary
            val optionLabels = listOf("A", "B", "C", "D")
            question.options.forEachIndexed { idx, optionText ->
                val isCorrectOption = idx == question.correctOptionIndex
                val isUserOption = idx == userSelected

                val optBg = when {
                    isCorrectOption -> NtaStatusAnswered.copy(alpha = 0.12f)
                    isUserOption && !isCorrectOption -> NtaStatusNotAnswered.copy(alpha = 0.12f)
                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                }

                val optBorder = when {
                    isCorrectOption -> NtaStatusAnswered
                    isUserOption && !isCorrectOption -> NtaStatusNotAnswered
                    else -> Color.Transparent
                }

                Surface(
                    color = optBg,
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, optBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${optionLabels.getOrElse(idx) { "$idx" }}.",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = optionText,
                            fontSize = 12.5.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )

                        if (isCorrectOption) {
                            Text(
                                text = "✓ Correct Answer",
                                color = NtaStatusAnswered,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        } else if (isUserOption) {
                            Text(
                                text = "✗ Your Choice",
                                color = NtaStatusNotAnswered,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Time spent on this question
            if (questionState.timeSpentSeconds > 0) {
                Text(
                    text = "Time spent on this question: ${questionState.timeSpentSeconds} seconds",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Expand Explanation Accordion
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isExpanded) "Hide Detailed Solution" else "View Step-by-Step Solution & Concept",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.06f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Detailed Explanation:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = question.explanation,
                            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 20.sp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
