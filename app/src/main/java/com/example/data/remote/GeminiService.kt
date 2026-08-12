package com.example.data.remote

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiApiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private fun getApiKey(): String {
        val key = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }
        return key.ifEmpty { "" }
    }

    suspend fun generateText(prompt: String, systemPrompt: String? = null): Result<String> = withContext(Dispatchers.IO) {
        val key = getApiKey()
        if (key.isBlank() || key == "MY_GEMINI_API_KEY") {
            return@withContext Result.failure(Exception("کلید Gemini API یافت نشد. لطفاً کلید API را در پانل Secrets تنظیم کنید."))
        }

        try {
            val rootJson = JSONObject()

            // Contents
            val contentsArray = JSONArray()
            val contentObj = JSONObject()
            val partsArray = JSONArray()
            val partObj = JSONObject()
            partObj.put("text", prompt)
            partsArray.put(partObj)
            contentObj.put("parts", partsArray)
            contentsArray.put(contentObj)
            rootJson.put("contents", contentsArray)

            // System Instruction
            if (!systemPrompt.isNullOrBlank()) {
                val sysObj = JSONObject()
                val sysParts = JSONArray()
                val sysPart = JSONObject()
                sysPart.put("text", systemPrompt)
                sysParts.put(sysPart)
                sysObj.put("parts", sysParts)
                rootJson.put("systemInstruction", sysObj)
            }

            // Generation Config
            val configObj = JSONObject()
            configObj.put("temperature", 0.7)
            rootJson.put("generationConfig", configObj)

            val requestBody = rootJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val url = "$BASE_URL?key=$key"

            val httpRequest = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val httpResponse = okHttpClient.newCall(httpRequest).execute()
            val responseBodyString = httpResponse.body?.string() ?: ""

            if (!httpResponse.isSuccessful) {
                return@withContext Result.failure(Exception("خطا از سرور هوش مصنوعی (${httpResponse.code})"))
            }

            val responseJson = JSONObject(responseBodyString)
            val candidates = responseJson.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val candidate = candidates.getJSONObject(0)
                val content = candidate.optJSONObject("content")
                if (content != null) {
                    val parts = content.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        val text = parts.getJSONObject(0).optString("text", "")
                        if (text.isNotBlank()) {
                            return@withContext Result.success(text.trim())
                        }
                    }
                }
            }

            Result.failure(Exception("پاسخی از هوش مصنوعی دریافت نشد."))
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "خطا در ارتباط با هوش مصنوعی"))
        }
    }
}
