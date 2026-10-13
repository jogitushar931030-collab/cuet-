package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Subject
import com.example.model.TestMode
import com.example.ui.theme.NtaBlueDark
import com.example.ui.theme.NtaBluePrimary
import com.example.ui.theme.NtaGold

@Composable
fun NtaExamHeader(
    subject: Subject,
    testMode: TestMode,
    candidateName: String = "Tushar",
    candidateRollNo: String = "CUET20261014",
    remainingSeconds: Long,
    isTimerWarning: Boolean,
    isDarkMode: Boolean,
    isPaletteOpen: Boolean,
    onTogglePalette: () -> Unit,
    onToggleDarkMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = NtaBluePrimary,
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Top NTA Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NtaBlueDark)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(NtaGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "NTA",
                            color = Color.Black,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CUET (UG) 2026 • CBT PORTAL",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleDarkMode,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("dark_mode_toggle_btn")
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Dark/Light Mode",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Mobile palette toggle
                    IconButton(
                        onClick = onTogglePalette,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("palette_toggle_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.GridOn,
                            contentDescription = "Toggle Palette",
                            tint = if (isPaletteOpen) NtaGold else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Candidate & Timer Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Candidate Details
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Candidate Avatar",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Candidate: $candidateName ($candidateRollNo)",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${subject.title} • ${subject.code}",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 11.sp
                        )
                    }
                }

                // Timer Pill
                TimerDisplay(
                    testMode = testMode,
                    remainingSeconds = remainingSeconds,
                    isWarning = isTimerWarning
                )
            }
        }
    }
}

@Composable
fun TimerDisplay(
    testMode: TestMode,
    remainingSeconds: Long,
    isWarning: Boolean,
    modifier: Modifier = Modifier
) {
    if (testMode == TestMode.TOPIC_PRACTICE) {
        Surface(
            color = Color.White.copy(alpha = 0.15f),
            shape = RoundedCornerShape(20.dp),
            modifier = modifier
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = NtaGold,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Untimed Practice",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    } else {
        val minutes = remainingSeconds / 60
        val seconds = remainingSeconds % 60
        val formattedTime = String.format("%02d:%02d", minutes, seconds)

        val warningBg = if (isWarning) Color(0xFFDC2626) else Color.White
        val warningTextColor = if (isWarning) Color.White else NtaBlueDark

        Surface(
            color = warningBg,
            shape = RoundedCornerShape(20.dp),
            shadowElevation = if (isWarning) 3.dp else 1.dp,
            modifier = modifier.testTag("exam_timer_display")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isWarning) Icons.Default.Warning else Icons.Default.Timer,
                    contentDescription = "Time Left",
                    tint = if (isWarning) Color.White else NtaBluePrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Time Left: $formattedTime",
                        color = warningTextColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (isWarning) {
                        Text(
                            text = "5 MIN WARNING",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}
