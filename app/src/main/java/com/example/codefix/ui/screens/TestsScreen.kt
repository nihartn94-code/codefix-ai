package com.example.codefix.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codefix.model.TestCase
import com.example.codefix.model.TestStatus
import com.example.codefix.ui.components.TestStatusBadge
import com.example.ui.theme.*

@Composable
fun TestsScreen(
    testCases: List<TestCase>,
    isRunningTests: Boolean,
    testsExecuted: Boolean,
    fixVerified: Boolean,
    appliedFixCount: Int,
    onRunTests: () -> Unit,
    onViewReport: () -> Unit,
    modifier: Modifier = Modifier
) {
    val passedCount = testCases.count { it.status == TestStatus.PASSED }
    val failedCount = testCases.count { it.status == TestStatus.FAILED }
    val skippedCount = testCases.count { it.status == TestStatus.SKIPPED }
    val totalTimeMs = testCases.sumOf { it.durationMs }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Slate950)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Fix Verification Header Banner
        item {
            if (fixVerified && failedCount == 0 && appliedFixCount > 0) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x3310B981), RoundedCornerShape(12.dp))
                        .border(1.dp, EmeraldSuccess, RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(24.dp))
                        Text(
                            text = "FIX VERIFIED ✓",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = EmeraldLight,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "Workflow Complete: Issue detected → Fix generated → Fix applied → Tests executed → Verification passed with zero regressions.",
                        fontSize = 12.sp,
                        color = Slate100,
                        lineHeight = 16.sp
                    )

                    Button(
                        onClick = onViewReport,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("view_final_report_from_tests_btn")
                    ) {
                        Text("View Final Productivity Report →", fontWeight = FontWeight.Bold, color = Slate950)
                    }
                }
            } else if (failedCount > 0) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x33EF4444), RoundedCornerShape(12.dp))
                        .border(1.dp, RoseError.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = RoseError)
                        Text(
                            text = "TEST FAILURES DETECTED",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF87171)
                        )
                    }
                    Text(
                        text = "$failedCount test(s) failed. Fix the corresponding issues to satisfy the test assertions.",
                        fontSize = 12.sp,
                        color = Slate300
                    )
                }
            }
        }

        // Test Metrics Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TestMetricBox(
                    label = "Passed",
                    value = "$passedCount",
                    color = EmeraldSuccess,
                    modifier = Modifier.weight(1f)
                )
                TestMetricBox(
                    label = "Failed",
                    value = "$failedCount",
                    color = if (failedCount > 0) RoseError else Slate500,
                    modifier = Modifier.weight(1f)
                )
                TestMetricBox(
                    label = "Skipped",
                    value = "$skippedCount",
                    color = Slate400,
                    modifier = Modifier.weight(1f)
                )
                TestMetricBox(
                    label = "Duration",
                    value = "${totalTimeMs}ms",
                    color = CyanAccent,
                    modifier = Modifier.weight(1.2f)
                )
            }
        }

        // Action: Run Tests Again
        item {
            Button(
                onClick = onRunTests,
                enabled = !isRunningTests,
                colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("run_tests_again_btn")
            ) {
                if (isRunningTests) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Executing automated test suite...", fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("RUN TESTS AGAIN", fontWeight = FontWeight.Black, fontSize = 14.sp, letterSpacing = 0.5.sp)
                }
            }
        }

        // Test Cases List
        item {
            Text(
                text = "AUTOMATED TEST SUITE (${testCases.size} TESTS)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Slate400,
                letterSpacing = 1.sp
            )
        }

        items(testCases) { testCase ->
            TestCaseItemCard(testCase = testCase)
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun TestMetricBox(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Slate900, RoundedCornerShape(8.dp))
            .border(1.dp, Slate800, RoundedCornerShape(8.dp))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = label, fontSize = 10.sp, color = Slate400, fontWeight = FontWeight.Medium)
        Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = color)
    }
}

@Composable
fun TestCaseItemCard(
    testCase: TestCase,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Slate900, RoundedCornerShape(10.dp))
            .border(
                1.dp,
                if (testCase.status == TestStatus.FAILED) RoseError.copy(alpha = 0.4f) else Slate800,
                RoundedCornerShape(10.dp)
            )
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = when (testCase.status) {
                        TestStatus.PASSED -> Icons.Default.CheckCircle
                        TestStatus.FAILED -> Icons.Default.Cancel
                        TestStatus.SKIPPED -> Icons.Default.RemoveCircleOutline
                        TestStatus.RUNNING -> Icons.Default.Refresh
                    },
                    contentDescription = null,
                    tint = when (testCase.status) {
                        TestStatus.PASSED -> EmeraldSuccess
                        TestStatus.FAILED -> RoseError
                        TestStatus.SKIPPED -> Slate500
                        TestStatus.RUNNING -> CyanAccent
                    },
                    modifier = Modifier.size(18.dp)
                )

                Text(
                    text = testCase.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = Slate100
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "${testCase.durationMs}ms",
                    fontSize = 11.sp,
                    color = Slate500,
                    fontFamily = FontFamily.Monospace
                )
                TestStatusBadge(testCase.status)
            }
        }

        Text(
            text = testCase.description,
            fontSize = 12.sp,
            color = Slate400
        )

        if (testCase.status == TestStatus.FAILED && testCase.failureReason != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x33EF4444), RoundedCornerShape(6.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = testCase.failureReason,
                    color = Color(0xFFFCA5A5),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 15.sp
                )
            }
        }
    }
}
