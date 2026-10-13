package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.ui.theme.NtaBluePrimary
import com.example.ui.theme.NtaStatusAnswered
import com.example.ui.theme.NtaStatusDotGreen
import com.example.ui.theme.NtaStatusMarked
import com.example.ui.theme.NtaStatusNotAnswered
import com.example.ui.theme.NtaStatusNotVisited
import com.example.ui.theme.NtaStatusNotVisitedText

@Composable
fun SubmitConfirmationModal(
    questions: List<Question>,
    questionStates: Map<Int, QuestionState>,
    remainingSeconds: Long,
    onDismiss: () -> Unit,
    onConfirmSubmit: () -> Unit
) {
    var answered = 0
    var notAnswered = 0
    var marked = 0
    var ansMarked = 0
    var notVisited = 0

    questions.forEach { q ->
        val st = questionStates[q.id]?.status ?: QuestionStatus.NOT_VISITED
        when (st) {
            QuestionStatus.ANSWERED -> answered++
            QuestionStatus.NOT_ANSWERED -> notAnswered++
            QuestionStatus.MARKED_FOR_REVIEW -> marked++
            QuestionStatus.ANSWERED_AND_MARKED_FOR_REVIEW -> ansMarked++
            QuestionStatus.NOT_VISITED -> notVisited++
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = NtaBluePrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Exam Submission Summary",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Please review your test status before final submission. Once submitted, you cannot change your responses.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // NTA Summary Table Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SummaryRowItem("Total Questions", "${questions.size}", Color.Gray)
                        SummaryRowItem("Answered", "$answered", NtaStatusAnswered)
                        SummaryRowItem("Not Answered", "$notAnswered", NtaStatusNotAnswered)
                        SummaryRowItem("Marked for Review", "$marked", NtaStatusMarked)
                        SummaryRowItem("Ans & Marked for Review", "$ansMarked", NtaStatusMarked, hasGreenDot = true)
                        SummaryRowItem("Not Visited", "$notVisited", NtaStatusNotVisited, textColor = NtaStatusNotVisitedText)
                    }
                }

                // Important Note Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(NtaStatusAnswered.copy(alpha = 0.08f))
                        .border(1.dp, NtaStatusAnswered.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "★ Note: Official NTA policy evaluates both 'Answered' ($answered) and 'Ans & Marked for Review' ($ansMarked). Total evaluated: ${answered + ansMarked}.",
                        style = MaterialTheme.typography.labelSmall.copy(lineHeight = 15.sp),
                        color = NtaStatusAnswered,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirmSubmit,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NtaBluePrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.testTag("confirm_submit_btn")
            ) {
                Text(text = "Yes, Submit Test", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.testTag("cancel_submit_btn")
            ) {
                Text(text = "Return to Test")
            }
        }
    )
}

@Composable
fun SummaryRowItem(
    label: String,
    count: String,
    color: Color,
    textColor: Color = Color.White,
    hasGreenDot: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = count,
                    color = textColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
            if (hasGreenDot) {
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(NtaStatusDotGreen)
                )
            }
        }
    }
}
