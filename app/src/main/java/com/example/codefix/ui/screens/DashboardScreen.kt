package com.example.codefix.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codefix.model.Issue
import com.example.codefix.model.Project
import com.example.codefix.model.TestCase
import com.example.codefix.model.TestStatus
import com.example.codefix.ui.components.CategoryBadge
import com.example.codefix.ui.components.IssueStatusBadge
import com.example.codefix.ui.components.SeverityBadge
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    project: Project,
    issues: List<Issue>,
    testCases: List<TestCase>,
    appliedFixCount: Int,
    onNavigateToAnalyze: () -> Unit,
    onNavigateToIssues: () -> Unit,
    onNavigateToTests: () -> Unit,
    onNavigateToReport: () -> Unit,
    onSelectIssue: (Issue) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalIssues = issues.size
    val testsPassed = testCases.count { it.status == TestStatus.PASSED }
    val testsFailed = testCases.count { it.status == TestStatus.FAILED }
    val timeSavedMinutes = appliedFixCount * 25 + 10

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Slate950)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome and Quick Actions Banner
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Slate900, RoundedCornerShape(12.dp))
                    .border(1.dp, Slate800, RoundedCornerShape(12.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Developer Productivity Dashboard",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate100
                        )
                        Text(
                            text = "Active: ${project.name} (${project.filesCount} files, ${project.linesCount} LOC)",
                            fontSize = 12.sp,
                            color = Slate400
                        )
                    }

                    Surface(
                        color = Slate800,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = project.language,
                            fontSize = 11.sp,
                            color = CyanAccent,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onNavigateToAnalyze,
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).testTag("dash_analyze_btn")
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Analyze", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onNavigateToIssues,
                        colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).testTag("dash_issues_btn")
                    ) {
                        Icon(Icons.Default.BugReport, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Issues ($totalIssues)", fontSize = 12.sp, color = Slate100)
                    }

                    Button(
                        onClick = onNavigateToTests,
                        colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).testTag("dash_tests_btn")
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Tests", fontSize = 12.sp, color = Slate100)
                    }
                }
            }
        }

        // Metrics Grid (6 KPI Cards)
        item {
            Text(
                text = "PROJECT METRICS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Slate400,
                letterSpacing = 1.sp
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Projects Analyzed",
                    value = "1",
                    subtitle = "Sample repository loaded",
                    icon = Icons.Default.Folder,
                    accentColor = CyanAccent,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Issues Detected",
                    value = "$totalIssues",
                    subtitle = "${issues.count { it.severity == com.example.codefix.model.Severity.CRITICAL }} Critical",
                    icon = Icons.Default.BugReport,
                    accentColor = RoseError,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Issues Fixed",
                    value = "$appliedFixCount",
                    subtitle = "of $totalIssues resolved",
                    icon = Icons.Default.AutoFixHigh,
                    accentColor = EmeraldSuccess,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Tests Passed",
                    value = "$testsPassed",
                    subtitle = "$testsFailed failing tests",
                    icon = Icons.Default.CheckCircle,
                    accentColor = if (testsFailed == 0) EmeraldSuccess else AmberWarning,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Tests Failed",
                    value = "$testsFailed",
                    subtitle = if (testsFailed == 0) "Zero regressions" else "Requires fixes",
                    icon = Icons.Default.Cancel,
                    accentColor = if (testsFailed > 0) RoseError else Slate400,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Estimated Saved",
                    value = "${timeSavedMinutes}m",
                    subtitle = "Demo workflow estimate",
                    icon = Icons.Default.Timer,
                    accentColor = PurpleAi,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Recent Issues Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECENT ISSUES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate400,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "View All →",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CyanAccent,
                    modifier = Modifier.clickable { onNavigateToIssues() }
                )
            }
        }

        items(issues.take(4)) { issue ->
            DashboardIssueCard(
                issue = issue,
                onClick = { onSelectIssue(issue) }
            )
        }

        // Final Report Shortcut Card
        item {
            Surface(
                color = Slate900,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate800),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToReport() }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0x336366F1), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Assessment, contentDescription = null, tint = IndigoAi)
                        }
                        Column {
                            Text(
                                text = "Comprehensive Analysis Report",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Slate100
                            )
                            Text(
                                text = "View developer productivity metrics & time savings",
                                fontSize = 11.sp,
                                color = Slate400
                            )
                        }
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Slate400)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Slate900, RoundedCornerShape(10.dp))
            .border(1.dp, Slate800, RoundedCornerShape(10.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Slate400
            )
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(16.dp)
            )
        }
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = Slate100
        )
        Text(
            text = subtitle,
            fontSize = 10.sp,
            color = Slate500,
            maxLines = 1
        )
    }
}

@Composable
fun DashboardIssueCard(
    issue: Issue,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Slate900, RoundedCornerShape(10.dp))
            .border(1.dp, Slate800, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SeverityBadge(issue.severity)
                CategoryBadge(issue.category)
            }
            IssueStatusBadge(issue.status)
        }

        Text(
            text = issue.title,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = Slate100
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${issue.file} : ${issue.line}",
                fontSize = 11.sp,
                color = Slate400,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
            )
            Text(
                text = "Inspect →",
                fontSize = 11.sp,
                color = CyanAccent,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
