package com.example.codefix.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun CodeBlockViewer(
    code: String,
    title: String? = null,
    highlightLine: Int? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lines = code.lines()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(CodeBg, RoundedCornerShape(8.dp))
            .border(1.dp, Slate700, RoundedCornerShape(8.dp))
    ) {
        // Code header bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Slate800, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // macOS-like editor dots
                Box(modifier = Modifier.size(8.dp).background(Color(0xFFEF4444), RoundedCornerShape(4.dp)))
                Box(modifier = Modifier.size(8.dp).background(Color(0xFFF59E0B), RoundedCornerShape(4.dp)))
                Box(modifier = Modifier.size(8.dp).background(Color(0xFF10B981), RoundedCornerShape(4.dp)))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title ?: "Code Snippet",
                    color = Slate300,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium
                )
            }

            IconButton(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("code", code))
                    Toast.makeText(context, "Code copied to clipboard", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.size(28.dp).testTag("copy_code_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy Code",
                    tint = Slate400,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        // Code content with horizontal scroll
        val scrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .horizontalScroll(scrollState)
        ) {
            Column {
                lines.forEachIndexed { index, line ->
                    val lineNum = index + 1
                    val isHighlighted = highlightLine == lineNum

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isHighlighted) Color(0x33EF4444) else Color.Transparent)
                            .padding(horizontal = 12.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = String.format("%2d ", lineNum),
                            color = if (isHighlighted) RoseError else Slate600,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            modifier = Modifier.width(32.dp)
                        )

                        Text(
                            text = highlightSyntax(line),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DiffCodeViewer(
    beforeCode: String,
    afterCode: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Before section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CodeBg, RoundedCornerShape(8.dp))
                .border(1.dp, RoseError.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x33EF4444), RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "– BEFORE (Problematic Code)",
                    color = Color(0xFFF87171),
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
            Column(modifier = Modifier.padding(12.dp)) {
                beforeCode.lines().forEach { line ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0x22EF4444))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "- ",
                            color = Color(0xFFF87171),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                        Text(
                            text = highlightSyntax(line),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // After section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CodeBg, RoundedCornerShape(8.dp))
                .border(1.dp, EmeraldSuccess.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x3310B981), RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "+ AFTER (AI-Generated Fix)",
                    color = Color(0xFF34D399),
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
            Column(modifier = Modifier.padding(12.dp)) {
                afterCode.lines().forEach { line ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0x2210B981))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "+ ",
                            color = Color(0xFF34D399),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                        Text(
                            text = highlightSyntax(line),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

fun highlightSyntax(line: String) = buildAnnotatedString {
    val trimmed = line.trimStart()
    if (trimmed.startsWith("//") || trimmed.startsWith("#")) {
        withStyle(SpanStyle(color = CodeComment)) {
            append(line)
        }
        return@buildAnnotatedString
    }

    val keywords = setOf(
        "const", "let", "var", "function", "async", "await", "return",
        "if", "else", "try", "catch", "throw", "new", "import", "from",
        "export", "require", "module", "null", "undefined", "true", "false",
        "def", "class", "public", "private", "void", "static"
    )

    val tokens = line.split(Regex("(?<=\\b)|(?=\\b)|(?<=[\\s(),;{}])|(?=[\\s(),;{}])"))
    for (token in tokens) {
        when {
            token in keywords -> {
                withStyle(SpanStyle(color = CodeKeyword, fontWeight = FontWeight.SemiBold)) {
                    append(token)
                }
            }
            token.startsWith("'") || token.startsWith("\"") || token.startsWith("`") -> {
                withStyle(SpanStyle(color = CodeString)) {
                    append(token)
                }
            }
            token.matches(Regex("[0-9]+(\\.[0-9]+)?")) -> {
                withStyle(SpanStyle(color = AmberWarning)) {
                    append(token)
                }
            }
            token.matches(Regex("[a-zA-Z_][a-zA-Z0-9_]*\\s*(?=\\()")) -> {
                withStyle(SpanStyle(color = CodeFunction)) {
                    append(token)
                }
            }
            else -> {
                withStyle(SpanStyle(color = Slate100)) {
                    append(token)
                }
            }
        }
    }
}
