package com.example.codefix.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codefix.model.Category
import com.example.codefix.model.Issue
import com.example.codefix.model.Severity
import com.example.codefix.ui.components.CategoryBadge
import com.example.codefix.ui.components.IssueStatusBadge
import com.example.codefix.ui.components.SeverityBadge
import com.example.ui.theme.*

@Composable
fun IssuesScreen(
    issues: List<Issue>,
    searchQuery: String,
    filterSeverity: Severity?,
    filterCategory: Category?,
    onSearchChanged: (String) -> Unit,
    onFilterSeverity: (Severity?) -> Unit,
    onFilterCategory: (Category?) -> Unit,
    onSelectIssue: (Issue) -> Unit,
    onAnalyzeMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredIssues = issues.filter { issue ->
        val matchesSearch = searchQuery.isBlank() ||
                issue.title.contains(searchQuery, ignoreCase = true) ||
                issue.file.contains(searchQuery, ignoreCase = true) ||
                issue.whatIsWrong.contains(searchQuery, ignoreCase = true)
        val matchesSeverity = filterSeverity == null || issue.severity == filterSeverity
        val matchesCategory = filterCategory == null || issue.category == filterCategory
        matchesSearch && matchesSeverity && matchesCategory
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Slate950)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Search & Filters Header
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChanged,
                    placeholder = { Text("Search issues by title, file, or description...", color = Slate500, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Slate400) },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { onSearchChanged("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Slate400)
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Slate900,
                        unfocusedContainerColor = Slate900,
                        focusedBorderColor = CyanAccent,
                        unfocusedBorderColor = Slate800,
                        focusedTextColor = Slate100,
                        unfocusedTextColor = Slate100
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("issues_search_input")
                )

                // Severity Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = filterSeverity == null,
                        onClick = { onFilterSeverity(null) },
                        label = { Text("All Severities (${issues.size})", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Slate800,
                            selectedLabelColor = CyanAccent,
                            containerColor = Slate900,
                            labelColor = Slate400
                        )
                    )

                    Severity.values().forEach { sev ->
                        val count = issues.count { it.severity == sev }
                        FilterChip(
                            selected = filterSeverity == sev,
                            onClick = { onFilterSeverity(if (filterSeverity == sev) null else sev) },
                            label = { Text("${sev.label} ($count)", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Slate800,
                                selectedLabelColor = CyanAccent,
                                containerColor = Slate900,
                                labelColor = Slate400
                            )
                        )
                    }
                }

                // Category Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = filterCategory == null,
                        onClick = { onFilterCategory(null) },
                        label = { Text("All Categories", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Slate800,
                            selectedLabelColor = CyanAccent,
                            containerColor = Slate900,
                            labelColor = Slate400
                        )
                    )

                    Category.values().forEach { cat ->
                        val count = issues.count { it.category == cat }
                        if (count > 0) {
                            FilterChip(
                                selected = filterCategory == cat,
                                onClick = { onFilterCategory(if (filterCategory == cat) null else cat) },
                                label = { Text("${cat.label} ($count)", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Slate800,
                                    selectedLabelColor = CyanAccent,
                                    containerColor = Slate900,
                                    labelColor = Slate400
                                )
                            )
                        }
                    }
                }
            }
        }

        // Issue Count Summary
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DETECTED ISSUES (${filteredIssues.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate400,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${issues.count { it.status == com.example.codefix.model.IssueStatus.FIXED }} Fixed",
                    fontSize = 12.sp,
                    color = EmeraldSuccess,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Empty state
        if (filteredIssues.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Slate900, RoundedCornerShape(12.dp))
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = EmeraldSuccess,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "No issues found matching criteria",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Slate100
                    )
                    Text(
                        text = "Try clearing filters or search query.",
                        fontSize = 12.sp,
                        color = Slate400
                    )
                    Button(
                        onClick = {
                            onSearchChanged("")
                            onFilterSeverity(null)
                            onFilterCategory(null)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Slate800)
                    ) {
                        Text("Reset Filters", color = CyanAccent)
                    }
                }
            }
        }

        // Issues List
        items(filteredIssues) { issue ->
            IssueItemCard(
                issue = issue,
                onClick = { onSelectIssue(issue) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun IssueItemCard(
    issue: Issue,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Slate900, RoundedCornerShape(10.dp))
            .border(1.dp, Slate800, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(14.dp)
            .testTag("issue_card_${issue.id}"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
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
            IssueStatusBadge(issue.status)
        }

        Text(
            text = issue.title,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Slate100
        )

        Text(
            text = issue.whatIsWrong,
            fontSize = 12.sp,
            color = Slate300,
            maxLines = 2,
            lineHeight = 16.sp
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Code,
                    contentDescription = null,
                    tint = Slate500,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "${issue.file}:${issue.line}",
                    fontSize = 11.sp,
                    color = Slate400,
                    fontFamily = FontFamily.Monospace
                )
            }

            Text(
                text = "View Details →",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = CyanAccent
            )
        }
    }
}
