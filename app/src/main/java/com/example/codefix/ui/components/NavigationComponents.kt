package com.example.codefix.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codefix.model.Screen
import com.example.ui.theme.*

data class NavDestination(
    val screen: Screen,
    val label: String,
    val icon: ImageVector,
    val testTag: String
)

val mainNavDestinations = listOf(
    NavDestination(Screen.DASHBOARD, "Dashboard", Icons.Default.Dashboard, "nav_dashboard"),
    NavDestination(Screen.ANALYZE, "Analyze", Icons.Default.Search, "nav_analyze"),
    NavDestination(Screen.ISSUES, "Issues", Icons.Default.BugReport, "nav_issues"),
    NavDestination(Screen.FIX_VIEW, "Fixes", Icons.Default.AutoFixHigh, "nav_fixes"),
    NavDestination(Screen.TESTS, "Tests", Icons.Default.CheckCircle, "nav_tests"),
    NavDestination(Screen.REPORTS, "Reports", Icons.Default.Assessment, "nav_reports"),
    NavDestination(Screen.HISTORY, "History", Icons.Default.History, "nav_history"),
    NavDestination(Screen.SETTINGS, "Settings", Icons.Default.Settings, "nav_settings")
)

@Composable
fun CodeFixBottomNavigation(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Slate900,
        tonalElevation = 8.dp,
        modifier = modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = Slate800)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            mainNavDestinations.forEach { dest ->
                val selected = currentScreen == dest.screen
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = dest.icon,
                            contentDescription = dest.label,
                            tint = if (selected) CyanAccent else Slate400,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    label = {
                        Text(
                            text = dest.label,
                            fontSize = 11.sp,
                            color = if (selected) CyanAccent else Slate400
                        )
                    },
                    selected = selected,
                    onClick = { onNavigate(dest.screen) },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = Slate800,
                        unselectedContainerColor = Slate900
                    ),
                    modifier = Modifier
                        .testTag(dest.testTag)
                        .padding(horizontal = 2.dp)
                )
            }
        }
    }
}

@Composable
fun CodeFixNavigationRail(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationRail(
        containerColor = Slate900,
        contentColor = Slate100,
        modifier = modifier.border(width = 1.dp, color = Slate800)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        mainNavDestinations.forEach { dest ->
            val selected = currentScreen == dest.screen
            NavigationRailItem(
                selected = selected,
                onClick = { onNavigate(dest.screen) },
                icon = {
                    Icon(
                        imageVector = dest.icon,
                        contentDescription = dest.label,
                        tint = if (selected) CyanAccent else Slate400
                    )
                },
                label = {
                    Text(
                        text = dest.label,
                        fontSize = 10.sp,
                        color = if (selected) CyanAccent else Slate400
                    )
                },
                colors = NavigationRailItemDefaults.colors(
                    indicatorColor = Slate800
                ),
                modifier = Modifier.testTag(dest.testTag)
            )
        }
    }
}
