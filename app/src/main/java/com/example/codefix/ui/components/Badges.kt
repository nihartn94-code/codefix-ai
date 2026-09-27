package com.example.codefix.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codefix.model.Category
import com.example.codefix.model.IssueStatus
import com.example.codefix.model.Severity
import com.example.codefix.model.TestStatus
import com.example.ui.theme.*

@Composable
fun SeverityBadge(severity: Severity, modifier: Modifier = Modifier) {
    val (bgColor, textColor, borderColor) = when (severity) {
        Severity.CRITICAL -> Triple(Color(0x33EF4444), Color(0xFFF87171), Color(0xFFEF4444))
        Severity.HIGH -> Triple(Color(0x33F97316), Color(0xFFFB923C), Color(0xFFF97316))
        Severity.MEDIUM -> Triple(Color(0x33F59E0B), Color(0xFFFCD34D), Color(0xFFF59E0B))
        Severity.LOW -> Triple(Color(0x3338BDF8), Color(0xFF7DD3FC), Color(0xFF38BDF8))
    }

    Box(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(6.dp))
            .border(1.dp, borderColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = severity.label.uppercase(),
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun CategoryBadge(category: Category, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Slate800, RoundedCornerShape(6.dp))
            .border(1.dp, Slate700, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = category.label,
            color = Slate300,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun IssueStatusBadge(status: IssueStatus, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when (status) {
        IssueStatus.OPEN -> Pair(Color(0x33EF4444), Color(0xFFF87171))
        IssueStatus.IN_PROGRESS -> Pair(Color(0x3338BDF8), Color(0xFF38BDF8))
        IssueStatus.FIXED -> Pair(Color(0x3310B981), Color(0xFF34D399))
        IssueStatus.IGNORED -> Pair(Color(0x3364748B), Color(0xFF94A3B8))
    }

    Box(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = status.label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun TestStatusBadge(status: TestStatus, modifier: Modifier = Modifier) {
    val (bgColor, textColor, label) = when (status) {
        TestStatus.PASSED -> Triple(Color(0x3310B981), Color(0xFF34D399), "PASSED")
        TestStatus.FAILED -> Triple(Color(0x33EF4444), Color(0xFFF87171), "FAILED")
        TestStatus.SKIPPED -> Triple(Color(0x3364748B), Color(0xFF94A3B8), "SKIPPED")
        TestStatus.RUNNING -> Triple(Color(0x3338BDF8), Color(0xFF38BDF8), "RUNNING...")
    }

    Box(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
