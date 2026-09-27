package com.example.codefix.model

enum class Severity(val label: String) {
    CRITICAL("Critical"),
    HIGH("High"),
    MEDIUM("Medium"),
    LOW("Low")
}

enum class Category(val label: String) {
    BUG("Bug"),
    SECURITY("Security"),
    PERFORMANCE("Performance"),
    CODE_QUALITY("Code Quality"),
    TESTING("Testing"),
    MAINTAINABILITY("Maintainability")
}

enum class IssueStatus(val label: String) {
    OPEN("Open"),
    IN_PROGRESS("In Progress"),
    FIXED("Fixed"),
    IGNORED("Ignored")
}

enum class TestStatus(val label: String) {
    PASSED("Passed"),
    FAILED("Failed"),
    SKIPPED("Skipped"),
    RUNNING("Running")
}

data class ProjectFile(
    val name: String,
    val path: String,
    val language: String,
    val content: String,
    val linesCount: Int = content.lines().size
)

data class Project(
    val id: String,
    val name: String,
    val description: String,
    val language: String,
    val framework: String,
    val files: List<ProjectFile> = emptyList(),
    val isSample: Boolean = false
) {
    val filesCount: Int get() = files.size
    val linesCount: Int get() = files.sumOf { it.linesCount }
}

data class Issue(
    val id: String,
    val projectId: String,
    val title: String,
    val severity: Severity,
    val category: Category,
    val file: String,
    val line: Int,
    val status: IssueStatus = IssueStatus.OPEN,
    val whatIsWrong: String,
    val whyItHappens: String,
    val currentCode: String,
    val aiRecommendation: String,
    val beforeCode: String,
    val afterCode: String,
    val changes: List<String> = emptyList(),
    val suggestedTests: List<String> = emptyList()
)

data class TestCase(
    val id: String,
    val name: String,
    val description: String,
    val durationMs: Long,
    val status: TestStatus,
    val failureReason: String? = null,
    val targetIssueId: String? = null
)

data class AnalysisHistoryItem(
    val id: String,
    val projectName: String,
    val timestamp: Long,
    val issuesFound: Int,
    val issuesFixed: Int,
    val testsPassed: Int,
    val testsTotal: Int,
    val status: String
)

enum class Screen(val title: String) {
    LANDING("Welcome"),
    DASHBOARD("Dashboard"),
    ANALYZE("Analyze Project"),
    ISSUES("Detected Issues"),
    ISSUE_DETAIL("Issue Details"),
    FIX_VIEW("Fix Comparison"),
    TESTS("Automated Tests"),
    REPORTS("Analysis Report"),
    HISTORY("History"),
    SETTINGS("Settings")
}
