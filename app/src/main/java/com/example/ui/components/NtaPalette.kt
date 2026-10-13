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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Question
import com.example.model.QuestionState
import com.example.model.QuestionStatus
import com.example.ui.theme.NtaStatusAnswered
import com.example.ui.theme.NtaStatusDotGreen
import com.example.ui.theme.NtaStatusMarked
import com.example.ui.theme.NtaStatusNotAnswered
import com.example.ui.theme.NtaStatusNotVisited
import com.example.ui.theme.NtaStatusNotVisitedText

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NtaQuestionPalette(
    questions: List<Question>,
    questionStates: Map<Int, QuestionState>,
    currentIndex: Int,
    onSelectQuestion: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Calculate counts for legend
    var answeredCount = 0
    var notAnsweredCount = 0
    var markedCount = 0
    var ansMarkedCount = 0
    var notVisitedCount = 0

    questions.forEach { q ->
        val st = questionStates[q.id]?.status ?: QuestionStatus.NOT_VISITED
        when (st) {
            QuestionStatus.ANSWERED -> answeredCount++
            QuestionStatus.NOT_ANSWERED -> notAnsweredCount++
            QuestionStatus.MARKED_FOR_REVIEW -> markedCount++
            QuestionStatus.ANSWERED_AND_MARKED_FOR_REVIEW -> ansMarkedCount++
            QuestionStatus.NOT_VISITED -> notVisitedCount++
        }
    }

    Card(
        modifier = modifier.testTag("question_palette_card"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .verticalScroll(scrollState)
        ) {
            // Palette Header
            Text(
                text = "QUESTION PALETTE",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Official NTA Legend Grid
            NtaLegendSummary(
                answered = answeredCount,
                notAnswered = notAnsweredCount,
                notVisited = notVisitedCount,
                marked = markedCount,
                ansMarked = ansMarkedCount
            )

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "Choose a Question:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // 5-column / responsive question button grid
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        maxItemsInEachRow = 5
                    ) {
                        questions.forEachIndexed { index, question ->
                            val state = questionStates[question.id]
                            val status = state?.status ?: QuestionStatus.NOT_VISITED
                            val isCurrent = index == currentIndex

                            NtaQuestionBadge(
                                number = index + 1,
                                status = status,
                                isCurrent = isCurrent,
                                onClick = { onSelectQuestion(index) },
                                modifier = Modifier.testTag("palette_q_${index + 1}")
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NtaLegendSummary(
    answered: Int,
    notAnswered: Int,
    notVisited: Int,
    marked: Int,
    ansMarked: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LegendItem(
                    count = answered,
                    label = "Answered",
                    color = NtaStatusAnswered,
                    hasDot = false
                )
                LegendItem(
                    count = notAnswered,
                    label = "Not Answered",
                    color = NtaStatusNotAnswered,
                    hasDot = false
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LegendItem(
                    count = notVisited,
                    label = "Not Visited",
                    color = NtaStatusNotVisited,
                    textColor = NtaStatusNotVisitedText,
                    hasDot = false
                )
                LegendItem(
                    count = marked,
                    label = "Marked for Review",
                    color = NtaStatusMarked,
                    hasDot = false,
                    isCircle = true
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                LegendItem(
                    count = ansMarked,
                    label = "Ans & Marked for Review (Evaluated)",
                    color = NtaStatusMarked,
                    hasDot = true,
                    isCircle = true
                )
            }
        }
    }
}

@Composable
fun LegendItem(
    count: Int,
    label: String,
    color: Color,
    textColor: Color = Color.White,
    hasDot: Boolean = false,
    isCircle: Boolean = false,
    modifier: Modifier = Modifier
) {
    val shape = if (isCircle) CircleShape else RoundedCornerShape(6.dp)
    Row(
        modifier = modifier.padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(shape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = count.toString(),
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            if (hasDot) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(NtaStatusDotGreen)
                        .border(1.dp, Color.White, CircleShape)
                )
            }
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            fontSize = 10.5.sp,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun NtaQuestionBadge(
    number: Int,
    status: QuestionStatus,
    isCurrent: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor: Color
    val textColor: Color
    var showDot = false
    val isReview = status == QuestionStatus.MARKED_FOR_REVIEW || status == QuestionStatus.ANSWERED_AND_MARKED_FOR_REVIEW
    val badgeShape = if (isReview) CircleShape else RoundedCornerShape(8.dp)

    when (status) {
        QuestionStatus.NOT_VISITED -> {
            bgColor = NtaStatusNotVisited
            textColor = NtaStatusNotVisitedText
        }
        QuestionStatus.NOT_ANSWERED -> {
            bgColor = NtaStatusNotAnswered
            textColor = Color.White
        }
        QuestionStatus.ANSWERED -> {
            bgColor = NtaStatusAnswered
            textColor = Color.White
        }
        QuestionStatus.MARKED_FOR_REVIEW -> {
            bgColor = NtaStatusMarked
            textColor = Color.White
        }
        QuestionStatus.ANSWERED_AND_MARKED_FOR_REVIEW -> {
            bgColor = NtaStatusMarked
            textColor = Color.White
            showDot = true
        }
    }

    val currentBorderModifier = if (isCurrent) {
        Modifier.border(2.5.dp, MaterialTheme.colorScheme.primary, badgeShape)
    } else {
        Modifier.border(0.5.dp, Color.Black.copy(alpha = 0.15f), badgeShape)
    }

    Box(
        modifier = modifier
            .size(44.dp)
            .then(currentBorderModifier)
            .clip(badgeShape)
            .background(bgColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number.toString(),
            color = textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        if (showDot) {
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .align(Alignment.BottomEnd)
                    .padding(end = 2.dp, bottom = 2.dp)
                    .clip(CircleShape)
                    .background(NtaStatusDotGreen)
                    .border(1.dp, Color.White, CircleShape)
            )
        }
    }
}
