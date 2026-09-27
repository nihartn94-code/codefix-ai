package com.example.codefix.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codefix.model.Screen
import com.example.ui.theme.*

@Composable
fun LandingScreen(
    onAnalyzeClick: () -> Unit,
    onTrySampleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate950)
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // IBM Bob 2.0 Hackathon Header Badge
        Surface(
            color = Slate900,
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate700)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(CyanAccent, RoundedCornerShape(4.dp))
                )
                Text(
                    text = "Built for the IBM Bob 2.0 Hackathon",
                    color = Slate300,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Hero Section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "CODEFIX ",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Black,
                    color = Slate100,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "AI",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Black,
                    color = CyanAccent,
                    letterSpacing = 1.sp
                )
            }

            Text(
                text = "Find. Understand. Fix. Verify.",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = EmeraldSuccess,
                letterSpacing = 0.5.sp
            )

            Text(
                text = "An AI-powered developer assistant that analyzes software projects, identifies coding issues, explains their causes, generates fixes, and verifies them through testing.",
                fontSize = 14.sp,
                color = Slate300,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onAnalyzeClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyanPrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("analyze_my_project_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Analyze Project",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            OutlinedButton(
                onClick = onTrySampleClick,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = CyanAccent
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("try_sample_project_button")
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Try Sample Project",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        // 4-Step Workflow Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Slate900, RoundedCornerShape(12.dp))
                .border(1.dp, Slate800, RoundedCornerShape(12.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "END-TO-END DEVELOPER WORKFLOW",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = CyanAccent,
                letterSpacing = 1.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                WorkflowStepBadge("1", "Analyze", Slate800, CyanAccent)
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Slate600, modifier = Modifier.size(18.dp))
                WorkflowStepBadge("2", "Detect", Slate800, AmberWarning)
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Slate600, modifier = Modifier.size(18.dp))
                WorkflowStepBadge("3", "Fix", Slate800, PurpleAi)
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Slate600, modifier = Modifier.size(18.dp))
                WorkflowStepBadge("4", "Verify", Slate800, EmeraldSuccess)
            }
        }

        // Features Grid
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "CORE CAPABILITIES",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Slate400,
                letterSpacing = 1.sp
            )

            val features = listOf(
                Triple("AI Code Analysis", "Inspect project architecture, files, and syntax trees in seconds.", Icons.Default.Code),
                Triple("Bug Detection", "Detect null dereferences, syntax errors, and edge-case exceptions.", Icons.Default.BugReport),
                Triple("AI Fix Generation", "Gemini 3.5 Flash crafts safe, contextual fixes with zero hallucination.", Icons.Default.AutoFixHigh),
                Triple("Code Comparison", "Interactive visual diff viewer with exact line-by-line syntax highlights.", Icons.Default.CompareArrows),
                Triple("Automated Testing", "Run automated test suites to verify that regressions are prevented.", Icons.Default.CheckCircle),
                Triple("Developer Reports", "Clear productivity metrics and estimates of developer hours saved.", Icons.Default.Assessment)
            )

            features.chunked(2).forEach { rowFeatures ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowFeatures.forEach { (title, desc, icon) ->
                        FeatureCard(
                            title = title,
                            description = desc,
                            icon = icon,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Before vs After Workflow Preview Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(listOf(Slate900, Slate800)),
                    RoundedCornerShape(12.dp)
                )
                .border(1.dp, Slate700, RoundedCornerShape(12.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "WHY CODEFIX AI?",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = EmeraldSuccess,
                letterSpacing = 1.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Before
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0x22EF4444), RoundedCornerShape(8.dp))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "BEFORE",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color(0xFFF87171)
                    )
                    Text("• Manual code inspection", fontSize = 11.sp, color = Slate300)
                    Text("• Manual debugging", fontSize = 11.sp, color = Slate300)
                    Text("• Manual fix authoring", fontSize = 11.sp, color = Slate300)
                    Text("• Tedious rework", fontSize = 11.sp, color = Slate300)
                }

                // After
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0x2210B981), RoundedCornerShape(8.dp))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "AFTER",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color(0xFF34D399)
                    )
                    Text("• AI-assisted analysis", fontSize = 11.sp, color = Slate100)
                    Text("• Root-cause explanation", fontSize = 11.sp, color = Slate100)
                    Text("• 1-click AI fix & diff", fontSize = 11.sp, color = Slate100)
                    Text("• Automated verification", fontSize = 11.sp, color = Slate100)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun WorkflowStepBadge(step: String, label: String, bg: Color, accent: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(bg, RoundedCornerShape(8.dp))
                .border(1.dp, accent.copy(alpha = 0.6f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = step,
                color = accent,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
        Text(
            text = label,
            fontSize = 11.sp,
            color = Slate300,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun FeatureCard(
    title: String,
    description: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Slate900, RoundedCornerShape(10.dp))
            .border(1.dp, Slate800, RoundedCornerShape(10.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Slate800, RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = CyanAccent,
                modifier = Modifier.size(18.dp)
            )
        }
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = Slate100
        )
        Text(
            text = description,
            fontSize = 11.sp,
            color = Slate400,
            lineHeight = 15.sp
        )
    }
}
