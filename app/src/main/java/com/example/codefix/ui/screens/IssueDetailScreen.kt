package com.example.codefix.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codefix.model.Issue
import com.example.codefix.ui.components.CategoryBadge
import com.example.codefix.ui.components.CodeBlockViewer
import com.example.codefix.ui.components.IssueStatusBadge
import com.example.codefix.ui.components.SeverityBadge
import com.example.ui.theme.*

@Composable
fun IssueDetailScreen(
    issue: Issue,
    isGeneratingFix: Boolean,
    onGenerateFix: () -> Unit,
    onMarkResolved: () -> Unit,
    onIgnore: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Slate950)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Slate900, RoundedCornerShape(12.dp))
                    .border(1.dp, Slate800, RoundedCornerShape(12.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
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
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Slate100
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = null,
                        tint = AmberWarning,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "${issue.file} (Line ${issue.line})",
                        fontSize = 12.sp,
                        color = Slate300,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Section 1: WHAT IS WRONG?
        item {
            DetailSectionCard(
                title = "WHAT IS WRONG?",
                content = issue.whatIsWrong,
                icon = Icons.Default.ErrorOutline,
                accentColor = RoseError
            )
        }

        // Section 2: WHY DOES IT HAPPEN?
        item {
            DetailSectionCard(
                title = "WHY DOES IT HAPPEN?",
                content = issue.whyItHappens,
                icon = Icons.Default.Psychology,
                accentColor = AmberWarning
            )
        }

        // Section 3: CURRENT CODE
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "CURRENT CODE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate400,
                    letterSpacing = 1.sp
                )
                CodeBlockViewer(
                    code = issue.currentCode,
                    title = "${issue.file} : line ${issue.line}",
                    highlightLine = null
                )
            }
        }

        // Section 4: AI RECOMMENDATION
        item {
            DetailSectionCard(
                title = "AI RECOMMENDATION",
                content = issue.aiRecommendation,
                icon = Icons.Default.AutoFixHigh,
                accentColor = CyanAccent
            )
        }

        // Action Buttons
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onGenerateFix,
                    enabled = !isGeneratingFix,
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("generate_fix_btn")
                ) {
                    if (isGeneratingFix) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generating fix with Gemini AI...", fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "GENERATE FIX",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onMarkResolved,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = EmeraldSuccess),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldSuccess),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("mark_resolved_btn")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Mark Resolved", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onIgnore,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate400),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate700),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ignore_issue_btn")
                    ) {
                        Icon(Icons.Default.Block, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ignore", fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun DetailSectionCard(
    title: String,
    content: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Slate900, RoundedCornerShape(10.dp))
            .border(1.dp, Slate800, RoundedCornerShape(10.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor,
                letterSpacing = 0.5.sp
            )
        }

        Text(
            text = content,
            fontSize = 13.sp,
            color = Slate200,
            lineHeight = 18.sp
        )
    }
}
