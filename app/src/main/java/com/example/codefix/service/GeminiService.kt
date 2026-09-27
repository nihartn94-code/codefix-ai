package com.example.codefix.service

import com.example.BuildConfig
import com.example.codefix.model.Issue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class GeminiFixResult(
    val explanation: String,
    val rootCause: String,
    val beforeCode: String,
    val afterCode: String,
    val changes: List<String>,
    val suggestedTests: List<String>,
    val isFromAi: Boolean
)

object GeminiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    fun getApiKey(customKey: String?): String {
        if (!customKey.isNullOrBlank()) {
            return customKey.trim()
        }
        return try {
            val key = BuildConfig.GEMINI_API_KEY
            if (key != "MY_GEMINI_API_KEY" && key.isNotBlank()) key else ""
        } catch (_: Exception) {
            ""
        }
    }

    fun isAiAvailable(customKey: String?): Boolean {
        return getApiKey(customKey).isNotBlank()
    }

    suspend fun generateFix(
        issue: Issue,
        customKey: String? = null
    ): Result<GeminiFixResult> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey(customKey)

        if (apiKey.isBlank()) {
            // High-fidelity deterministic fallback for Demo Mode
            return@withContext Result.success(
                GeminiFixResult(
                    explanation = issue.whatIsWrong,
                    rootCause = issue.whyItHappens,
                    beforeCode = issue.beforeCode,
                    afterCode = issue.afterCode,
                    changes = issue.changes,
                    suggestedTests = issue.suggestedTests,
                    isFromAi = false
                )
            )
        }

        try {
            val prompt = """
                You are CodeFix AI, an expert code analysis and debugging assistant.
                Analyze the following bug and provide a structured JSON fix:

                File: ${issue.file} (Line: ${issue.line})
                Issue Title: ${issue.title}
                Category: ${issue.category.name}
                Severity: ${issue.severity.name}
                Current Problematic Code:
                ```
                ${issue.currentCode}
                ```

                Provide your response strictly as a JSON object with this exact structure:
                {
                  "explanation": "Clear plain-language explanation of what is wrong",
                  "rootCause": "Technical analysis of the root cause",
                  "beforeCode": "Original problematic code block",
                  "afterCode": "Corrected code block with safe fix",
                  "changes": ["Change description 1", "Change description 2"],
                  "suggestedTests": ["Test suggestion 1", "Test suggestion 2"]
                }
            """.trimIndent()

            val requestJson = JSONObject().apply {
                val contents = JSONArray().apply {
                    val part = JSONObject().apply {
                        put("text", prompt)
                    }
                    val contentObj = JSONObject().apply {
                        put("parts", JSONArray().put(part))
                    }
                    put(contentObj)
                }
                put("contents", contents)

                val generationConfig = JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.2)
                }
                put("generationConfig", generationConfig)
            }

            val body = requestJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                // If API fails (quota or auth), fallback cleanly to deterministic demo mode data
                return@withContext Result.success(
                    GeminiFixResult(
                        explanation = issue.whatIsWrong,
                        rootCause = issue.whyItHappens,
                        beforeCode = issue.beforeCode,
                        afterCode = issue.afterCode,
                        changes = issue.changes,
                        suggestedTests = issue.suggestedTests,
                        isFromAi = false
                    )
                )
            }

            val responseBody = response.body?.string() ?: ""
            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text") ?: ""

            // Parse text as JSON object
            val parsedResult = parseAiJson(text, issue)
            Result.success(parsedResult)
        } catch (e: Exception) {
            // Graceful fallback on network exception
            Result.success(
                GeminiFixResult(
                    explanation = issue.whatIsWrong,
                    rootCause = issue.whyItHappens,
                    beforeCode = issue.beforeCode,
                    afterCode = issue.afterCode,
                    changes = issue.changes,
                    suggestedTests = issue.suggestedTests,
                    isFromAi = false
                )
            )
        }
    }

    private fun parseAiJson(rawText: String, fallbackIssue: Issue): GeminiFixResult {
        return try {
            val cleanJson = rawText.trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()
            val obj = JSONObject(cleanJson)

            val changesList = mutableListOf<String>()
            val changesArr = obj.optJSONArray("changes")
            if (changesArr != null) {
                for (i in 0 until changesArr.length()) {
                    changesList.add(changesArr.getString(i))
                }
            } else {
                changesList.addAll(fallbackIssue.changes)
            }

            val testList = mutableListOf<String>()
            val testsArr = obj.optJSONArray("suggestedTests")
            if (testsArr != null) {
                for (i in 0 until testsArr.length()) {
                    testList.add(testsArr.getString(i))
                }
            } else {
                testList.addAll(fallbackIssue.suggestedTests)
            }

            GeminiFixResult(
                explanation = obj.optString("explanation", fallbackIssue.whatIsWrong),
                rootCause = obj.optString("rootCause", fallbackIssue.whyItHappens),
                beforeCode = obj.optString("beforeCode", fallbackIssue.beforeCode),
                afterCode = obj.optString("afterCode", fallbackIssue.afterCode),
                changes = changesList,
                suggestedTests = testList,
                isFromAi = true
            )
        } catch (_: Exception) {
            GeminiFixResult(
                explanation = fallbackIssue.whatIsWrong,
                rootCause = fallbackIssue.whyItHappens,
                beforeCode = fallbackIssue.beforeCode,
                afterCode = fallbackIssue.afterCode,
                changes = fallbackIssue.changes,
                suggestedTests = fallbackIssue.suggestedTests,
                isFromAi = false
            )
        }
    }
}
