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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codefix.model.Issue
import com.example.codefix.service.GeminiFixResult
import com.example.codefix.ui.components.CategoryBadge
import com.example.codefix.ui.components.DiffCodeViewer
import com.example.codefix.ui.components.SeverityBadge
import com.example.ui.theme.*

@Composable
fun FixComparisonScreen(
    issue: Issue?,
    fixResult: GeminiFixResult?,
    isFixApplied: Boolean,
    onApplyFix: (Issue) -> Unit,
    onRejectFix: (Issue) -> Unit,
    onRunTests: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    if (issue == null) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Slate950)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = Slate600, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text("No fix selected", color = Slate300, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Select an issue to inspect or generate an AI fix.", color = Slate500, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Slate800)) {
                Text("Return to Issues", color = CyanAccent)
            }
        }
        return
    }

    val beforeCode = fixResult?.beforeCode ?: issue.beforeCode
    val afterCode = fixResult?.afterCode ?: issue.afterCode
    val changes = fixResult?.changes?.takeIf { it.isNotEmpty() } ?: issue.changes
    val suggestedTests = fixResult?.suggestedTests?.takeIf { it.isNotEmpty() } ?: issue.suggestedTests
    val isAiPowered = fixResult?.isFromAi == true

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Slate950)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Fix Header Banner
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

                    Surface(
                        color = if (isAiPowered) Color(0x336366F1) else Color(0x33F59E0B),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (isAiPowered) Icons.Default.AutoAwesome else Icons.Default.Shield,
                                contentDescription = null,
                                tint = if (isAiPowered) IndigoAi else AmberWarning,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = if (isAiPowered) "Gemini 3.5 Flash" else "Verified Fix Pattern",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isAiPowered) IndigoAi else AmberWarning
                            )
                        }
                    }
                }

                Text(
                    text = "FIX GENERATED",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanAccent,
                    letterSpacing = 1.sp
                )

                Text(
                    text = issue.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate100
                )

                Text(
                    text = fixResult?.explanation ?: issue.whatIsWrong,
                    fontSize = 13.sp,
                    color = Slate300,
                    lineHeight = 18.sp
                )
            }
        }

        // Before vs After Diff Viewer
        item {
            Text(
                text = "BEFORE / AFTER CODE COMPARISON",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Slate400,
                letterSpacing = 1.sp
            )
        }

        item {
            DiffCodeViewer(
                beforeCode = beforeCode,
                afterCode = afterCode
            )
        }

        // Summary: "What changed?"
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Slate900, RoundedCornerShape(10.dp))
                    .border(1.dp, Slate800, RoundedCornerShape(10.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Checklist, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(16.dp))
                    Text(
                        text = "WHAT CHANGED?",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldSuccess,
                        letterSpacing = 0.5.sp
                    )
                }

                changes.forEach { change ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("•", color = EmeraldSuccess, fontWeight = FontWeight.Bold)
                        Text(change, fontSize = 12.sp, color = Slate200)
                    }
                }
            }
        }

        // Suggested Tests
        if (suggestedTests.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Slate900, RoundedCornerShape(10.dp))
                        .border(1.dp, Slate800, RoundedCornerShape(10.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(16.dp))
                        Text(
                            text = "SUGGESTED VERIFICATION TESTS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent,
                            letterSpacing = 0.5.sp
                        )
                    }

                    suggestedTests.forEach { testName ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("✓", color = CyanAccent, fontWeight = FontWeight.Bold)
                            Text(testName, fontSize = 12.sp, color = Slate300, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }

        // Apply Fix / Run Tests Section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (isFixApplied) {
                    // Success Banner after applying
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0x3310B981), RoundedCornerShape(10.dp))
                            .border(1.dp, EmeraldSuccess.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldSuccess)
                            Text(
                                text = "Fix Applied ✓",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = EmeraldLight
                            )
                        }
                        Text(
                            text = "Source files updated in repository state. Run test suite to verify fix prevents regression.",
                            fontSize = 12.sp,
                            color = Slate200
                        )

                        Button(
                            onClick = onRunTests,
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("run_tests_after_fix_btn")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Slate950)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Run Automated Tests →", fontWeight = FontWeight.Bold, color = Slate950)
                        }
                    }
                } else {
                    Button(
                        onClick = { onApplyFix(issue) },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("apply_fix_btn")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Slate950)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "APPLY FIX",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = Slate950,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("fixed_code", afterCode))
                            Toast.makeText(context, "Fixed code copied to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate300),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate700),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).testTag("copy_fix_code_btn")
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy Code", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = { onRejectFix(issue) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = RoseError),
                        border = androidx.compose.foundation.BorderStroke(1.dp, RoseError.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).testTag("reject_fix_btn")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reject Fix", fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
