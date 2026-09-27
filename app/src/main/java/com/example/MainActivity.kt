package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.codefix.model.Screen
import com.example.codefix.ui.components.CodeFixBottomNavigation
import com.example.codefix.ui.components.CodeFixNavigationRail
import com.example.codefix.ui.components.TopNavBar
import com.example.codefix.ui.screens.*
import com.example.codefix.viewmodel.CodeFixViewModel
import com.example.ui.theme.CodeFixTheme
import com.example.ui.theme.Slate950

class MainActivity : ComponentActivity() {

    private val viewModel: CodeFixViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CodeFixTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val context = LocalContext.current

                // Show toast notifications from ViewModel
                LaunchedEffect(uiState.toastMessage) {
                    uiState.toastMessage?.let { msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        viewModel.clearToast()
                    }
                }

                // Handle Android System Back Button cleanly
                BackHandler(enabled = uiState.screenBackStack.size > 1) {
                    viewModel.navigateBack()
                }

                BoxWithConstraints(modifier = Modifier.fillMaxSize().background(Slate950)) {
                    val isWideScreen = maxWidth >= 600.dp

                    Row(modifier = Modifier.fillMaxSize()) {
                        // Wide screen navigation rail
                        if (isWideScreen && uiState.currentScreen != Screen.LANDING) {
                            CodeFixNavigationRail(
                                currentScreen = uiState.currentScreen,
                                onNavigate = { viewModel.navigateTo(it) }
                            )
                        }

                        Scaffold(
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            containerColor = Slate950,
                            topBar = {
                                TopNavBar(
                                    currentScreen = uiState.currentScreen,
                                    projectName = uiState.currentProject.name,
                                    isDemoMode = uiState.isDemoMode,
                                    onBackClick = { viewModel.navigateBack() },
                                    onProjectClick = { viewModel.navigateTo(Screen.ANALYZE) },
                                    onDemoModeClick = { viewModel.toggleDemoMode() },
                                    onSettingsClick = { viewModel.navigateTo(Screen.SETTINGS) }
                                )
                            },
                            bottomBar = {
                                if (!isWideScreen && uiState.currentScreen != Screen.LANDING) {
                                    CodeFixBottomNavigation(
                                        currentScreen = uiState.currentScreen,
                                        onNavigate = { viewModel.navigateTo(it) }
                                    )
                                }
                            }
                        ) { innerPadding ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            ) {
                                when (uiState.currentScreen) {
                                    Screen.LANDING -> {
                                        LandingScreen(
                                            onAnalyzeClick = { viewModel.navigateTo(Screen.ANALYZE) },
                                            onTrySampleClick = { viewModel.loadSampleProject() }
                                        )
                                    }
                                    Screen.DASHBOARD -> {
                                        DashboardScreen(
                                            project = uiState.currentProject,
                                            issues = uiState.issues,
                                            testCases = uiState.testCases,
                                            appliedFixCount = uiState.appliedFixIds.size,
                                            onNavigateToAnalyze = { viewModel.navigateTo(Screen.ANALYZE) },
                                            onNavigateToIssues = { viewModel.navigateTo(Screen.ISSUES) },
                                            onNavigateToTests = { viewModel.navigateTo(Screen.TESTS) },
                                            onNavigateToReport = { viewModel.navigateTo(Screen.REPORTS) },
                                            onSelectIssue = { viewModel.selectIssue(it) }
                                        )
                                    }
                                    Screen.ANALYZE -> {
                                        AnalyzeScreen(
                                            project = uiState.currentProject,
                                            isAnalyzing = uiState.isAnalyzing,
                                            progress = uiState.analysisProgress,
                                            currentStep = uiState.currentAnalysisStep,
                                            analysisCompleted = uiState.analysisCompleted,
                                            selectedLanguage = uiState.selectedLanguage,
                                            onLanguageSelected = { viewModel.setSelectedLanguage(it) },
                                            onStartAnalysis = { viewModel.startAnalysis() },
                                            onViewIssues = { viewModel.navigateTo(Screen.ISSUES) },
                                            onLoadSample = { viewModel.loadSampleProject() }
                                        )
                                    }
                                    Screen.ISSUES -> {
                                        IssuesScreen(
                                            issues = uiState.issues,
                                            searchQuery = uiState.searchQuery,
                                            filterSeverity = uiState.filterSeverity,
                                            filterCategory = uiState.filterCategory,
                                            onSearchChanged = { viewModel.setSearchQuery(it) },
                                            onFilterSeverity = { viewModel.setFilterSeverity(it) },
                                            onFilterCategory = { viewModel.setFilterCategory(it) },
                                            onSelectIssue = { viewModel.selectIssue(it) },
                                            onAnalyzeMore = { viewModel.navigateTo(Screen.ANALYZE) }
                                        )
                                    }
                                    Screen.ISSUE_DETAIL -> {
                                        uiState.selectedIssue?.let { issue ->
                                            IssueDetailScreen(
                                                issue = issue,
                                                isGeneratingFix = uiState.isGeneratingFix,
                                                onGenerateFix = { viewModel.generateFix(issue) },
                                                onMarkResolved = { viewModel.markResolved(issue) },
                                                onIgnore = { viewModel.ignoreIssue(issue) },
                                                onBack = { viewModel.navigateBack() }
                                            )
                                        } ?: run {
                                            viewModel.navigateTo(Screen.ISSUES)
                                        }
                                    }
                                    Screen.FIX_VIEW -> {
                                        FixComparisonScreen(
                                            issue = uiState.activeFix ?: uiState.selectedIssue,
                                            fixResult = uiState.activeFixResult,
                                            isFixApplied = (uiState.activeFix ?: uiState.selectedIssue)?.id in uiState.appliedFixIds,
                                            onApplyFix = { viewModel.applyFix(it) },
                                            onRejectFix = { viewModel.rejectFix(it) },
                                            onRunTests = {
                                                viewModel.runTests()
                                                viewModel.navigateTo(Screen.TESTS)
                                            },
                                            onBack = { viewModel.navigateBack() }
                                        )
                                    }
                                    Screen.TESTS -> {
                                        TestsScreen(
                                            testCases = uiState.testCases,
                                            isRunningTests = uiState.isRunningTests,
                                            testsExecuted = uiState.testsExecuted,
                                            fixVerified = uiState.fixVerified,
                                            appliedFixCount = uiState.appliedFixIds.size,
                                            onRunTests = { viewModel.runTests() },
                                            onViewReport = { viewModel.navigateTo(Screen.REPORTS) }
                                        )
                                    }
                                    Screen.REPORTS -> {
                                        ReportScreen(
                                            project = uiState.currentProject,
                                            issues = uiState.issues,
                                            appliedFixCount = uiState.appliedFixIds.size,
                                            testCases = uiState.testCases,
                                            onBack = { viewModel.navigateBack() }
                                        )
                                    }
                                    Screen.HISTORY -> {
                                        HistoryScreen(
                                            historyItems = uiState.history,
                                            onViewReport = { viewModel.navigateTo(Screen.REPORTS) }
                                        )
                                    }
                                    Screen.SETTINGS -> {
                                        SettingsScreen(
                                            isDemoMode = uiState.isDemoMode,
                                            customApiKey = uiState.customApiKey,
                                            onToggleDemoMode = { viewModel.toggleDemoMode() },
                                            onSaveApiKey = { viewModel.setCustomApiKey(it) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
