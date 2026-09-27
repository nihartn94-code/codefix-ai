package com.example.codefix.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codefix.model.Screen
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavBar(
    currentScreen: Screen,
    projectName: String,
    isDemoMode: Boolean,
    onBackClick: () -> Unit,
    onProjectClick: () -> Unit,
    onDemoModeClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Slate900,
            titleContentColor = Slate100
        ),
        navigationIcon = {
            if (currentScreen != Screen.LANDING && currentScreen != Screen.DASHBOARD) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("top_nav_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Slate100
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .size(36.dp)
                        .background(Slate800, RoundedCornerShape(8.dp))
                        .border(1.dp, CyanAccent.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = "CodeFix AI",
                        tint = CyanAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "CODEFIX",
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp,
                            color = Slate100,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "AI",
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp,
                            color = CyanAccent,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Text(
                        text = currentScreen.title,
                        fontSize = 11.sp,
                        color = Slate400
                    )
                }

                // Project Selector Pill
                if (currentScreen != Screen.LANDING) {
                    Row(
                        modifier = Modifier
                            .background(Slate800, RoundedCornerShape(16.dp))
                            .border(1.dp, Slate700, RoundedCornerShape(16.dp))
                            .clickable { onProjectClick() }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = "Project",
                            tint = AmberWarning,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = projectName,
                            color = Slate300,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
                        )
                    }
                }
            }
        },
        actions = {
            // Demo Mode / AI indicator pill
            Row(
                modifier = Modifier
                    .background(
                        if (isDemoMode) Color(0x33F59E0B) else Color(0x3310B981),
                        RoundedCornerShape(16.dp)
                    )
                    .border(
                        1.dp,
                        if (isDemoMode) AmberWarning.copy(alpha = 0.5f) else EmeraldSuccess.copy(alpha = 0.5f),
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { onDemoModeClick() }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .background(
                            if (isDemoMode) AmberWarning else EmeraldSuccess,
                            CircleShape
                        )
                )
                Text(
                    text = if (isDemoMode) "Demo Mode" else "Gemini AI",
                    color = if (isDemoMode) AmberLight else EmeraldLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier.testTag("top_nav_settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Slate400
                )
            }
        },
        modifier = modifier
    )
}
