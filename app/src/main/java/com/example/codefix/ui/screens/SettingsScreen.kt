package com.example.codefix.ui.screens

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codefix.service.GeminiService
import com.example.ui.theme.*

@Composable
fun SettingsScreen(
    isDemoMode: Boolean,
    customApiKey: String,
    onToggleDemoMode: () -> Unit,
    onSaveApiKey: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var keyInput by remember { mutableStateOf(customApiKey) }
    var showKey by remember { mutableStateOf(false) }
    val isSystemKeyConfigured = remember { GeminiService.isAiAvailable(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Slate950)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "SETTINGS & PREFERENCES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate400,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Configuration & AI Runtime",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate100
                )
            }
        }

        // AI Configuration Section
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
                    Text(
                        text = "AI ENGINE CONFIGURATION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanAccent,
                        letterSpacing = 0.5.sp
                    )

                    Surface(
                        color = if (!isDemoMode && (isSystemKeyConfigured || customApiKey.isNotBlank())) Color(0x3310B981) else Color(0x33F59E0B),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (!isDemoMode && (isSystemKeyConfigured || customApiKey.isNotBlank())) "Gemini AI Ready" else "Demo Mode Active",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (!isDemoMode && (isSystemKeyConfigured || customApiKey.isNotBlank())) EmeraldSuccess else AmberWarning,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Text(
                    text = "Primary Model: gemini-3.5-flash. When Gemini API is unavailable or unconfigured, CodeFix AI automatically uses deterministic verified sample data without blocking workflows.",
                    fontSize = 12.sp,
                    color = Slate300,
                    lineHeight = 16.sp
                )

                OutlinedTextField(
                    value = keyInput,
                    onValueChange = { keyInput = it },
                    label = { Text("Custom Gemini API Key (Optional)", fontSize = 12.sp) },
                    placeholder = { Text("Enter AI Studio API Key...", color = Slate500, fontSize = 12.sp) },
                    visualTransformation = if (showKey) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showKey = !showKey }) {
                            Icon(
                                imageVector = if (showKey) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (showKey) "Hide" else "Show",
                                tint = Slate400
                            )
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Slate950,
                        unfocusedContainerColor = Slate950,
                        focusedBorderColor = CyanAccent,
                        unfocusedBorderColor = Slate700,
                        focusedTextColor = Slate100,
                        unfocusedTextColor = Slate100
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("custom_api_key_input")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onSaveApiKey(keyInput) },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).testTag("save_api_key_btn")
                    ) {
                        Text("Save API Key", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    if (keyInput.isNotBlank()) {
                        OutlinedButton(
                            onClick = {
                                keyInput = ""
                                onSaveApiKey("")
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate400),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Slate700),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Clear", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Demo Mode Toggle Section
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "DEMO MODE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmberWarning,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Force Offline Demo Mode",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Slate100
                        )
                        Text(
                            text = "Bypasses external network calls and provides instant, deterministic analysis for presentation demos.",
                            fontSize = 12.sp,
                            color = Slate400
                        )
                    }

                    Switch(
                        checked = isDemoMode,
                        onCheckedChange = { onToggleDemoMode() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AmberWarning,
                            checkedTrackColor = Color(0x66F59E0B)
                        ),
                        modifier = Modifier.testTag("demo_mode_switch")
                    )
                }
            }
        }

        // Application Preferences Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Slate900, RoundedCornerShape(12.dp))
                    .border(1.dp, Slate800, RoundedCornerShape(12.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "APPLICATION PREFERENCES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate400,
                    letterSpacing = 0.5.sp
                )

                PreferenceRow("Developer Dark Theme", "High-contrast IDE syntax scheme", true, {})
                HorizontalDivider(color = Slate800)
                PreferenceRow("Automated Regression Checking", "Execute tests after applying fixes", true, {})
                HorizontalDivider(color = Slate800)
                PreferenceRow("Safe Sandbox Inspection", "Strict readonly inspection for source files", true, {})
            }
        }

        // About & Hackathon Positioning
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Slate900, RoundedCornerShape(12.dp))
                    .border(1.dp, Slate800, RoundedCornerShape(12.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "ABOUT & ATTRIBUTION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate400,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "CodeFix AI v1.0.0",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Slate100
                )
                Text(
                    text = "Built for the IBM Bob 2.0 Hackathon. Powered by Google AI Studio & Gemini 3.5 Flash for intelligent code analysis and fix synthesis.",
                    fontSize = 12.sp,
                    color = Slate300,
                    lineHeight = 16.sp
                )
                Text(
                    text = "Tagline: Find. Understand. Fix. Verify.",
                    fontSize = 11.sp,
                    color = CyanAccent,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun PreferenceRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Slate100)
            Text(subtitle, fontSize = 11.sp, color = Slate400)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = CyanAccent,
                checkedTrackColor = Slate800
            )
        )
    }
}
