package co.gorkem.namaz.util

import android.util.Log
import co.gorkem.namaz.BuildConfig
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

data class UpdateCheckResult(
    val updateAvailable: Boolean,
    val latestVersion: String,
    val currentVersion: String,
    val releaseUrl: String,
    val downloadUrl: String,
    val releaseNotes: String,
    val isError: Boolean = false
)

class UpdateChecker(private val gson: Gson = Gson()) {

    companion object {
        private const val TAG = "UpdateChecker"
        private const val VERSION_JSON_URL = "https://gorkemguray.github.io/Namaz-App/version.json"
        private const val GITHUB_RELEASES_URL = "https://github.com/GorkemGuray/Namaz-App/releases/latest"
        private const val GITHUB_DOWNLOAD_BASE = "https://github.com/GorkemGuray/Namaz-App/releases/download"
    }

    suspend fun check(currentVersion: String): UpdateCheckResult = withContext(Dispatchers.IO) {
        try {
            val connection = URL(VERSION_JSON_URL).openConnection() as HttpURLConnection
            connection.apply {
                requestMethod = "GET"
                connectTimeout = 10_000
                readTimeout = 10_000
            }

            val responseCode = connection.responseCode
            if (responseCode != 200) {
                Log.w(TAG, "Version endpoint returned $responseCode")
                return@withContext UpdateCheckResult(
                    updateAvailable = false,
                    latestVersion = "",
                    currentVersion = currentVersion,
                    releaseUrl = GITHUB_RELEASES_URL,
                    downloadUrl = "",
                    releaseNotes = "",
                    isError = true
                )
            }

            val body = connection.inputStream.bufferedReader().use { it.readText() }
            val versionInfo = gson.fromJson(body, VersionInfo::class.java)

            val latestVersion = versionInfo.version.removePrefix("v")
            val updateAvailable = isNewerThan(latestVersion, currentVersion)

            val downloadUrl = "$GITHUB_DOWNLOAD_BASE/v$latestVersion/Namaz-$latestVersion-app-debug.apk"
            val releaseUrl = versionInfo.releaseUrl.ifEmpty { GITHUB_RELEASES_URL }

            if (BuildConfig.DEBUG) Log.d(TAG, "Current: $currentVersion, Latest: $latestVersion, Update available: $updateAvailable")

            UpdateCheckResult(
                updateAvailable = updateAvailable,
                latestVersion = latestVersion,
                currentVersion = currentVersion,
                releaseUrl = releaseUrl,
                downloadUrl = downloadUrl,
                releaseNotes = versionInfo.releaseNotes.ifEmpty { "Yeni özellikler ve hata düzeltmeleri." }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to check for updates", e)
            UpdateCheckResult(
                updateAvailable = false,
                latestVersion = "",
                currentVersion = currentVersion,
                releaseUrl = GITHUB_RELEASES_URL,
                downloadUrl = "",
                releaseNotes = "",
                isError = true
            )
        }
    }

    private fun isNewerThan(latest: String, current: String): Boolean {
        val latestParts = latest.split(".").mapNotNull { it.toIntOrNull() }
        val currentParts = current.split(".").mapNotNull { it.toIntOrNull() }
        if (latestParts.isEmpty() || currentParts.isEmpty()) return latest != current

        val maxLen = maxOf(latestParts.size, currentParts.size)
        for (i in 0 until maxLen) {
            val l = latestParts.getOrElse(i) { 0 }
            val c = currentParts.getOrElse(i) { 0 }
            if (l > c) return true
            if (l < c) return false
        }
        return false
    }
}

private data class VersionInfo(
    val version: String = "",
    @SerializedName("release_url") val releaseUrl: String = "",
    @SerializedName("release_notes") val releaseNotes: String = ""
)
