package com.example.codefix.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codefix.model.Project
import com.example.ui.theme.*

@Composable
fun AnalyzeScreen(
    project: Project,
    isAnalyzing: Boolean,
    progress: Float,
    currentStep: String,
    analysisCompleted: Boolean,
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onStartAnalysis: () -> Unit,
    onViewIssues: () -> Unit,
    onLoadSample: () -> Unit,
    modifier: Modifier = Modifier
) {
    val languages = listOf("JavaScript", "TypeScript", "Python", "Java")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Slate950)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Mode Selector: Built-in Sample Project vs Upload / Custom
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
                            text = "PROJECT SOURCE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = project.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Slate100
                        )
                    }

                    Button(
                        onClick = onLoadSample,
                        colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("reload_sample_btn")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reset Sample", fontSize = 11.sp, color = CyanAccent)
                    }
                }

                Text(
                    text = project.description,
                    fontSize = 13.sp,
                    color = Slate300
                )

                // Language Selectors
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Supported Language Stack:",
                        fontSize = 11.sp,
                        color = Slate400,
                        fontWeight = FontWeight.Medium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        languages.forEach { lang ->
                            val isSelected = selectedLanguage == lang
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSelected) Slate800 else Slate900,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) CyanAccent else Slate700,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { onLanguageSelected(lang) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = lang,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) CyanAccent else Slate400
                                )
                            }
                        }
                    }
                }

                // Project Details
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Slate950, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Files", fontSize = 10.sp, color = Slate400)
                        Text("${project.filesCount}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Slate100)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Lines of Code", fontSize = 10.sp, color = Slate400)
                        Text("${project.linesCount}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = CyanAccent)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Framework", fontSize = 10.sp, color = Slate400)
                        Text(project.framework, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Slate100)
                    }
                }
            }
        }

        // Analysis Execution Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Slate900, RoundedCornerShape(12.dp))
                    .border(1.dp, Slate800, RoundedCornerShape(12.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CODE ANALYSIS ENGINE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate400,
                        letterSpacing = 1.sp
                    )

                    if (isAnalyzing) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                strokeWidth = 2.dp,
                                color = CyanAccent
                            )
                            Text(
                                text = "Running...",
                                fontSize = 11.sp,
                                color = CyanAccent,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                if (!isAnalyzing && !analysisCompleted) {
                    Button(
                        onClick = onStartAnalysis,
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("start_analysis_btn")
                    ) {
                        Icon(Icons.Default.Bolt, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ANALYZE PROJECT",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }

                // Animated Progress Bar & Steps
                if (isAnalyzing || analysisCompleted) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = currentStep,
                                fontSize = 12.sp,
                                color = Slate200,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${(progress * 100).toInt()}%",
                                fontSize = 12.sp,
                                color = CyanAccent,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = CyanAccent,
                            trackColor = Slate800,
                        )
                    }

                    // Progress Steps List
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Slate950, RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val analysisSteps = listOf(
                            "Project loaded",
                            "Scanning files",
                            "Understanding project structure",
                            "Analyzing code syntax & dependencies",
                            "Detecting issues & security vulnerabilities",
                            "Generating explanations & root causes",
                            "Preparing AI fix recommendations"
                        )

                        analysisSteps.forEachIndexed { idx, step ->
                            val stepProgress = (idx + 1).toFloat() / analysisSteps.size
                            val isDone = progress >= stepProgress
                            val isActive = !isDone && progress >= (idx.toFloat() / analysisSteps.size)

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (isDone) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = EmeraldSuccess,
                                        modifier = Modifier.size(16.dp)
                                    )
                                } else if (isActive) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(12.dp),
                                        strokeWidth = 2.dp,
                                        color = CyanAccent
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(Slate700, CircleShape)
                                    )
                                }

                                Text(
                                    text = step,
                                    fontSize = 11.sp,
                                    color = if (isDone) Slate100 else if (isActive) CyanAccent else Slate500,
                                    fontWeight = if (isActive || isDone) FontWeight.Medium else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                // Completion Summary Card
                if (analysisCompleted && !isAnalyzing) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0x3310B981), RoundedCornerShape(8.dp))
                            .border(1.dp, EmeraldSuccess.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldSuccess)
                            Text(
                                text = "Analysis Complete",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldLight
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Files Analyzed: ${project.filesCount}", fontSize = 12.sp, color = Slate100)
                            Text("Lines Analyzed: ${project.linesCount}", fontSize = 12.sp, color = Slate100)
                            Text("Issues Detected: 6", fontSize = 12.sp, color = AmberWarning, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onViewIssues,
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("view_detected_issues_btn")
                        ) {
                            Text("View Detected Issues →", fontWeight = FontWeight.Bold, color = Slate950)
                        }
                    }
                }
            }
        }

        // Project File Explorer
        item {
            Text(
                text = "PROJECT FILES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Slate400,
                letterSpacing = 1.sp
            )
        }

        items(project.files) { file ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Slate900, RoundedCornerShape(8.dp))
                    .border(1.dp, Slate800, RoundedCornerShape(8.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (file.name.endsWith(".test.js")) Icons.Default.CheckCircle else Icons.Default.Description,
                        contentDescription = null,
                        tint = if (file.name.endsWith(".test.js")) EmeraldSuccess else CyanAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Column {
                        Text(
                            text = file.name,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = Slate100
                        )
                        Text(
                            text = file.path,
                            fontSize = 11.sp,
                            color = Slate500,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Text(
                    text = "${file.linesCount} lines",
                    fontSize = 11.sp,
                    color = Slate400
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
