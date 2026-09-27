package com.example.codefix.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codefix.model.Issue
import com.example.codefix.model.Project
import com.example.codefix.model.Severity
import com.example.codefix.model.TestCase
import com.example.codefix.model.TestStatus
import com.example.ui.theme.*

@Composable
fun ReportScreen(
    project: Project,
    issues: List<Issue>,
    appliedFixCount: Int,
    testCases: List<TestCase>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val criticalCount = issues.count { it.severity == Severity.CRITICAL }
    val highCount = issues.count { it.severity == Severity.HIGH }
    val mediumCount = issues.count { it.severity == Severity.MEDIUM }
    val lowCount = issues.count { it.severity == Severity.LOW }

    val testsPassed = testCases.count { it.status == TestStatus.PASSED }
    val testsFailed = testCases.count { it.status == TestStatus.FAILED }
    val testsTotal = testCases.size

    val estimatedManualMins = 120
    val codefixMins = 35
    val savedMins = estimatedManualMins - codefixMins

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Slate950)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Report Title Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(listOf(Slate900, Slate800)),
                        RoundedCornerShape(12.dp)
                    )
                    .border(1.dp, Slate700, RoundedCornerShape(12.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CODEFIX AI REPORT",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = CyanAccent,
                        letterSpacing = 1.sp
                    )

                    Surface(color = Slate800, shape = RoundedCornerShape(6.dp)) {
                        Text(
                            text = "Executive Summary",
                            fontSize = 11.sp,
                            color = Slate300,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Text(
                    text = project.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Slate100
                )

                Text(
                    text = "Automated analysis, bug resolution, and test verification results for ${project.language} (${project.framework}).",
                    fontSize = 13.sp,
                    color = Slate300
                )
            }
        }

        // Project Scope Stats
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ReportStatCard("Files Analyzed", "${project.filesCount}", Slate100, Modifier.weight(1f))
                ReportStatCard("Lines Analyzed", "${project.linesCount}", CyanAccent, Modifier.weight(1f))
                ReportStatCard("Issues Detected", "${issues.size}", AmberWarning, Modifier.weight(1f))
            }
        }

        // Severity Breakdown Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Slate900, RoundedCornerShape(10.dp))
                    .border(1.dp, Slate800, RoundedCornerShape(10.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "ISSUE SEVERITY BREAKDOWN",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate400,
                    letterSpacing = 0.5.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    SeverityCountPill("Critical", criticalCount, RoseError)
                    SeverityCountPill("High", highCount, Color(0xFFF97316))
                    SeverityCountPill("Medium", mediumCount, AmberWarning)
                    SeverityCountPill("Low", lowCount, CyanAccent)
                }
            }
        }

        // Fix & Testing Results
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ReportStatCard("Fixes Generated", "${issues.size}", IndigoAi, Modifier.weight(1f))
                ReportStatCard("Fixes Applied", "$appliedFixCount", EmeraldSuccess, Modifier.weight(1f))
                ReportStatCard("Tests Passed", "$testsPassed / $testsTotal", if (testsFailed == 0) EmeraldSuccess else AmberWarning, Modifier.weight(1.2f))
            }
        }

        // Developer Productivity & Time Savings Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Slate900, RoundedCornerShape(12.dp))
                    .border(1.dp, EmeraldSuccess.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DEVELOPER PRODUCTIVITY IMPACT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldSuccess,
                        letterSpacing = 1.sp
                    )

                    Surface(color = Slate800, shape = RoundedCornerShape(4.dp)) {
                        Text(
                            text = "Demo workflow estimate",
                            fontSize = 10.sp,
                            color = Slate400,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Estimated Manual Effort", fontSize = 11.sp, color = Slate400)
                        Text("$estimatedManualMins min", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF87171))
                    }
                    Text("vs", color = Slate500, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("CodeFix AI Workflow", fontSize = 11.sp, color = Slate400)
                        Text("$codefixMins min", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = CyanAccent)
                    }
                }

                HorizontalDivider(color = Slate800)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.TrendingUp, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Estimated Effort Saved: $savedMins minutes (~70% reduction)",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = EmeraldLight
                    )
                }
            }
        }

        // Before vs After Workflow Visual Comparison
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Slate900, RoundedCornerShape(12.dp))
                    .border(1.dp, Slate800, RoundedCornerShape(12.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "WORKFLOW COMPARISON",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate400,
                    letterSpacing = 1.sp
                )

                // Comparison Columns
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Manual Workflow
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0x22EF4444), RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0x33EF4444), RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "BEFORE CODEFIX AI",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color(0xFFF87171)
                        )
                        WorkflowItem("1. Manual code inspection")
                        WorkflowItem("2. Manual debugging")
                        WorkflowItem("3. Manual investigation")
                        WorkflowItem("4. Manual fix writing")
                        WorkflowItem("5. Manual test re-run")
                    }

                    // AI Workflow
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0x2210B981), RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0x3310B981), RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "AFTER CODEFIX AI",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color(0xFF34D399)
                        )
                        WorkflowItem("1. AI-assisted analysis")
                        WorkflowItem("2. Issue explanation")
                        WorkflowItem("3. AI-generated fix")
                        WorkflowItem("4. Code comparison")
                        WorkflowItem("5. Automated verification")
                    }
                }
            }
        }

        // Export Report Button
        item {
            Button(
                onClick = {
                    val reportText = """
                        CODEFIX AI REPORT
                        Project: ${project.name}
                        Files Analyzed: ${project.filesCount}
                        Lines Analyzed: ${project.linesCount}
                        Issues Detected: ${issues.size}
                        Fixes Applied: $appliedFixCount
                        Tests Passed: $testsPassed / $testsTotal
                        Estimated Effort Saved: $savedMins minutes
                        Generated with CodeFix AI
                    """.trimIndent()
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("report", reportText))
                    Toast.makeText(context, "Summary report copied to clipboard", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("export_report_btn")
            ) {
                Icon(Icons.Default.Share, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("EXPORT SUMMARY REPORT", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ReportStatCard(title: String, value: String, valueColor: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Slate900, RoundedCornerShape(8.dp))
            .border(1.dp, Slate800, RoundedCornerShape(8.dp))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(title, fontSize = 10.sp, color = Slate400, fontWeight = FontWeight.Medium, maxLines = 1)
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Black, color = valueColor)
    }
}

@Composable
fun SeverityCountPill(label: String, count: Int, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(text = "$count", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
        Text(text = label, fontSize = 10.sp, color = Slate400)
    }
}

@Composable
fun WorkflowItem(text: String) {
    Text(
        text = text,
        fontSize = 11.sp,
        color = Slate200,
        lineHeight = 15.sp
    )
}
