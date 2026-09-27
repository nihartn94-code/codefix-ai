package com.example.codefix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codefix.data.SampleProjects
import com.example.codefix.model.*
import com.example.codefix.service.GeminiFixResult
import com.example.codefix.service.GeminiService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CodeFixUiState(
    val currentScreen: Screen = Screen.LANDING,
    val screenBackStack: List<Screen> = listOf(Screen.LANDING),
    val currentProject: Project = SampleProjects.defaultSampleProject,
    val issues: List<Issue> = SampleProjects.sampleIssues,
    val selectedIssue: Issue? = null,
    val activeFix: Issue? = null,
    val activeFixResult: GeminiFixResult? = null,
    val appliedFixIds: Set<String> = emptySet(),
    val testCases: List<TestCase> = SampleProjects.createInitialTestCases(),
    val history: List<AnalysisHistoryItem> = SampleProjects.initialHistory,
    val isAnalyzing: Boolean = false,
    val analysisProgress: Float = 0f,
    val currentAnalysisStep: String = "",
    val analysisCompleted: Boolean = false,
    val isGeneratingFix: Boolean = false,
    val isRunningTests: Boolean = false,
    val testsExecuted: Boolean = false,
    val fixVerified: Boolean = false,
    val toastMessage: String? = null,
    val isDemoMode: Boolean = true,
    val customApiKey: String = "",
    val searchQuery: String = "",
    val filterSeverity: Severity? = null,
    val filterCategory: Category? = null,
    val selectedLanguage: String = "JavaScript",
    val isDarkTheme: Boolean = true
)

class CodeFixViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CodeFixUiState())
    val uiState: StateFlow<CodeFixUiState> = _uiState.asStateFlow()

    init {
        // Check if API key is present
        val hasKey = GeminiService.isAiAvailable(null)
        if (hasKey) {
            _uiState.value = _uiState.value.copy(isDemoMode = false)
        }
    }

    fun navigateTo(screen: Screen) {
        val currentStack = _uiState.value.screenBackStack
        val newStack = if (currentStack.lastOrNull() == screen) {
            currentStack
        } else {
            currentStack + screen
        }
        _uiState.value = _uiState.value.copy(
            currentScreen = screen,
            screenBackStack = newStack
        )
    }

    fun navigateBack(): Boolean {
        val stack = _uiState.value.screenBackStack
        if (stack.size > 1) {
            val updatedStack = stack.dropLast(1)
            _uiState.value = _uiState.value.copy(
                currentScreen = updatedStack.last(),
                screenBackStack = updatedStack
            )
            return true
        }
        return false
    }

    fun loadSampleProject() {
        _uiState.value = _uiState.value.copy(
            currentProject = SampleProjects.defaultSampleProject,
            issues = SampleProjects.sampleIssues,
            appliedFixIds = emptySet(),
            testCases = SampleProjects.createInitialTestCases(emptySet()),
            analysisCompleted = false,
            testsExecuted = false,
            fixVerified = false,
            toastMessage = "Sample project 'FinPay Core API' loaded"
        )
        navigateTo(Screen.ANALYZE)
    }

    fun startAnalysis() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isAnalyzing = true,
                analysisProgress = 0f,
                currentAnalysisStep = "Project loaded"
            )

            val steps = listOf(
                "Scanning files...",
                "Understanding project structure...",
                "Analyzing code syntax & dependencies...",
                "Detecting issues & security vulnerabilities...",
                "Generating explanations & root causes...",
                "Preparing AI fix recommendations...",
                "Analysis complete"
            )

            for (i in steps.indices) {
                delay(400)
                val progress = (i + 1).toFloat() / steps.size
                _uiState.value = _uiState.value.copy(
                    analysisProgress = progress,
                    currentAnalysisStep = steps[i]
                )
            }

            delay(200)
            _uiState.value = _uiState.value.copy(
                isAnalyzing = false,
                analysisCompleted = true,
                toastMessage = "Analysis complete: ${_uiState.value.issues.size} issues detected"
            )
        }
    }

    fun selectIssue(issue: Issue) {
        _uiState.value = _uiState.value.copy(selectedIssue = issue)
        navigateTo(Screen.ISSUE_DETAIL)
    }

    fun generateFix(issue: Issue) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isGeneratingFix = true,
                activeFix = issue
            )

            val key = if (_uiState.value.isDemoMode) null else _uiState.value.customApiKey
            val result = GeminiService.generateFix(issue, key)
            val fixResult = result.getOrNull()

            delay(600) // Brief realistic animation
            _uiState.value = _uiState.value.copy(
                isGeneratingFix = false,
                activeFixResult = fixResult
            )
            navigateTo(Screen.FIX_VIEW)
        }
    }

    fun applyFix(issue: Issue) {
        val updatedFixes = _uiState.value.appliedFixIds + issue.id
        val updatedIssues = _uiState.value.issues.map {
            if (it.id == issue.id) it.copy(status = IssueStatus.FIXED) else it
        }
        val updatedTests = SampleProjects.createInitialTestCases(updatedFixes)

        _uiState.value = _uiState.value.copy(
            appliedFixIds = updatedFixes,
            issues = updatedIssues,
            testCases = updatedTests,
            selectedIssue = issue.copy(status = IssueStatus.FIXED),
            toastMessage = "Fix applied for '${issue.title}' ✓"
        )
    }

    fun rejectFix(issue: Issue) {
        _uiState.value = _uiState.value.copy(
            activeFix = null,
            activeFixResult = null,
            toastMessage = "Fix rejected"
        )
        navigateBack()
    }

    fun markResolved(issue: Issue) {
        val updatedIssues = _uiState.value.issues.map {
            if (it.id == issue.id) it.copy(status = IssueStatus.FIXED) else it
        }
        val updatedFixes = _uiState.value.appliedFixIds + issue.id
        _uiState.value = _uiState.value.copy(
            issues = updatedIssues,
            appliedFixIds = updatedFixes,
            selectedIssue = issue.copy(status = IssueStatus.FIXED),
            toastMessage = "Issue marked as resolved"
        )
    }

    fun ignoreIssue(issue: Issue) {
        val updatedIssues = _uiState.value.issues.map {
            if (it.id == issue.id) it.copy(status = IssueStatus.IGNORED) else it
        }
        _uiState.value = _uiState.value.copy(
            issues = updatedIssues,
            selectedIssue = issue.copy(status = IssueStatus.IGNORED),
            toastMessage = "Issue marked as ignored"
        )
    }

    fun runTests() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isRunningTests = true,
                testsExecuted = false,
                fixVerified = false
            )

            // Step through tests with animation
            val tests = SampleProjects.createInitialTestCases(_uiState.value.appliedFixIds)
            val animatedTests = tests.map { it.copy(status = TestStatus.RUNNING) }
            _uiState.value = _uiState.value.copy(testCases = animatedTests)

            delay(400)
            for (i in tests.indices) {
                delay(200)
                val current = _uiState.value.testCases.toMutableList()
                current[i] = tests[i]
                _uiState.value = _uiState.value.copy(testCases = current)
            }

            val passedCount = tests.count { it.status == TestStatus.PASSED }
            val totalCount = tests.size
            val allPassed = passedCount == totalCount

            _uiState.value = _uiState.value.copy(
                isRunningTests = false,
                testsExecuted = true,
                fixVerified = allPassed || _uiState.value.appliedFixIds.isNotEmpty(),
                toastMessage = if (allPassed) "All $totalCount tests passed! Fix verified ✓" else "$passedCount of $totalCount tests passed"
            )
        }
    }

    fun toggleDemoMode() {
        val newDemo = !_uiState.value.isDemoMode
        _uiState.value = _uiState.value.copy(
            isDemoMode = newDemo,
            toastMessage = if (newDemo) "Demo Mode enabled" else "Demo Mode disabled (Live AI active)"
        )
    }

    fun setCustomApiKey(key: String) {
        _uiState.value = _uiState.value.copy(
            customApiKey = key,
            isDemoMode = key.isBlank(),
            toastMessage = if (key.isNotBlank()) "Gemini API Key saved" else "API Key cleared (Demo Mode enabled)"
        )
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun setFilterSeverity(severity: Severity?) {
        _uiState.value = _uiState.value.copy(filterSeverity = severity)
    }

    fun setFilterCategory(category: Category?) {
        _uiState.value = _uiState.value.copy(filterCategory = category)
    }

    fun setSelectedLanguage(lang: String) {
        _uiState.value = _uiState.value.copy(selectedLanguage = lang)
    }

    fun clearToast() {
        _uiState.value = _uiState.value.copy(toastMessage = null)
    }
}
