package com.example.codefix.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codefix.model.AnalysisHistoryItem
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    historyItems: List<AnalysisHistoryItem>,
    onViewReport: (AnalysisHistoryItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Slate950)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "ANALYSIS HISTORY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate400,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Past Project Analyses & Regression Audits",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate100
                )
            }
        }

        if (historyItems.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Slate900, RoundedCornerShape(12.dp))
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.History, contentDescription = null, tint = Slate600, modifier = Modifier.size(40.dp))
                    Text("No past analysis runs", color = Slate300, fontWeight = FontWeight.Bold)
                    Text("Analyses and test runs will be recorded here.", color = Slate500, fontSize = 12.sp)
                }
            }
        }

        items(historyItems) { item ->
            HistoryItemCard(
                item = item,
                dateStr = dateFormat.format(Date(item.timestamp)),
                onClick = { onViewReport(item) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun HistoryItemCard(
    item: AnalysisHistoryItem,
    dateStr: String,
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
            .testTag("history_card_${item.id}"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.projectName,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Slate100
            )

            Surface(color = Slate800, shape = RoundedCornerShape(6.dp)) {
                Text(
                    text = item.status,
                    fontSize = 11.sp,
                    color = EmeraldSuccess,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        Text(
            text = dateStr,
            fontSize = 11.sp,
            color = Slate400
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Slate950, RoundedCornerShape(6.dp))
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Issues Found", fontSize = 10.sp, color = Slate400)
                Text("${item.issuesFound}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RoseError)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Issues Fixed", fontSize = 10.sp, color = Slate400)
                Text("${item.issuesFixed}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Tests Passed", fontSize = 10.sp, color = Slate400)
                Text("${item.testsPassed} / ${item.testsTotal}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CyanAccent)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "View Report →",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = CyanAccent
            )
        }
    }
}
