package com.example

import com.example.codefix.data.SampleProjects
import com.example.codefix.model.Severity
import com.example.codefix.model.TestStatus
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun sampleProject_hasExpectedFilesAndBugs() {
        val project = SampleProjects.defaultSampleProject
        assertTrue(project.filesCount >= 6)
        assertTrue(project.linesCount > 50)
        assertEquals("JavaScript", project.language)
    }

    @Test
    fun sampleIssues_containCriticalAndSecurityIssues() {
        val issues = SampleProjects.sampleIssues
        assertTrue(issues.any { it.severity == Severity.CRITICAL })
        assertTrue(issues.any { it.title.contains("null", ignoreCase = true) })
        assertTrue(issues.any { it.title.contains("assignment", ignoreCase = true) })
    }

    @Test
    fun testCases_reflectFixedIssuesCorrectly() {
        // Initial state before fixes: tests fail for null user and gold tier
        val initialTests = SampleProjects.createInitialTestCases(emptySet())
        val initialFailed = initialTests.filter { it.status == TestStatus.FAILED }
        assertTrue(initialFailed.size >= 2)

        // After fixing issue-1 and issue-2: their tests pass
        val updatedTests = SampleProjects.createInitialTestCases(setOf("issue-1", "issue-2", "issue-3", "issue-4", "issue-5"))
        val remainingFailed = updatedTests.filter { it.status == TestStatus.FAILED }
        assertEquals(0, remainingFailed.size)
    }
}
