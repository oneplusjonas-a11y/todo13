package com.grid.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grid.app.data.Priority
import com.grid.app.ui.theme.GridCyan
import com.grid.app.ui.theme.GridHigh
import com.grid.app.ui.theme.GridLow
import com.grid.app.ui.theme.GridMedium
import com.grid.app.ui.theme.GridUrgent

fun metallicBrush(accent: Color): Brush = Brush.linearGradient(
    colors = listOf(
        Color(0xFF10151C),
        Color(0xFF1C242E),
        accent.copy(alpha = 0.22f),
        Color(0xFF161B22)
    )
)

fun metallicBorderBrush(accent: Color): Brush = Brush.linearGradient(
    colors = listOf(accent.copy(alpha = 0.9f), Color(0xFF1C242E), accent.copy(alpha = 0.35f))
)

@Composable
fun MetallicPanel(
    accent: Color = GridCyan,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(metallicBrush(accent))
            .border(1.dp, metallicBorderBrush(accent), RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
fun PriorityChip(priority: Priority) {
    val color = when (priority) {
        Priority.URGENT -> GridUrgent
        Priority.HIGH -> GridHigh
        Priority.MEDIUM -> GridMedium
        Priority.LOW -> GridLow
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.8f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = priority.label.uppercase(),
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}
