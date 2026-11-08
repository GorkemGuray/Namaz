package co.gorkem.namaz.service

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import co.gorkem.namaz.R
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Manages playback of the prayer adhan sound with a gradual 10-second volume ramp
 * to provide a comfortable wake-up / alert experience for users.
 */
object PrayerAudioPlayer {
    private const val TAG = "PrayerAudioPlayer"
    const val RAMP_DURATION_MS = 10_000L
    const val RAMP_STEPS = 20
    const val INITIAL_VOLUME = 0.05f
    const val TARGET_VOLUME = 1.0f

    @Volatile
    private var mediaPlayer: MediaPlayer? = null
    private var rampJob: Job? = null

    /**
     * Suspending version used by PrayerNotificationWorker.
     * Starts playback, ramps volume over 10 seconds, and suspends until playback finishes or is cancelled.
     */
    suspend fun playWithRampSuspended(context: Context) = withContext(Dispatchers.Default) {
        stop()
        val player = try {
            MediaPlayer.create(context, R.raw.ezan)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create MediaPlayer", e)
            null
        } ?: return@withContext

        mediaPlayer = player

        try {
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            player.setAudioAttributes(audioAttributes)

            var currentVolume = INITIAL_VOLUME
            player.setVolume(currentVolume, currentVolume)
            player.start()

            val stepInterval = RAMP_DURATION_MS / RAMP_STEPS
            val volumeStep = (TARGET_VOLUME - INITIAL_VOLUME) / RAMP_STEPS

            // Volume ramp loop (10 seconds)
            for (i in 1..RAMP_STEPS) {
                delay(stepInterval)
                if (!isActive || mediaPlayer != player || !player.isPlaying) break
                currentVolume = (currentVolume + volumeStep).coerceAtMost(TARGET_VOLUME)
                player.setVolume(currentVolume, currentVolume)
            }

            // Wait until player finishes playback or coroutine is cancelled
            while (isActive && mediaPlayer == player && player.isPlaying) {
                delay(500)
            }
        } catch (e: CancellationException) {
            Log.d(TAG, "Playback cancelled")
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Error during playback", e)
        } finally {
            stop()
        }
    }

    /**
     * Non-suspending version with scope (used for 'Sesi Dene' preview in Settings).
     */
    fun playWithRamp(
        context: Context,
        scope: CoroutineScope,
        onCompletion: (() -> Unit)? = null
    ) {
        stop()
        val player = try {
            MediaPlayer.create(context, R.raw.ezan)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create MediaPlayer for preview", e)
            null
        } ?: return

        mediaPlayer = player

        try {
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            player.setAudioAttributes(audioAttributes)

            var currentVolume = INITIAL_VOLUME
            player.setVolume(currentVolume, currentVolume)
            player.start()

            player.setOnCompletionListener {
                stop()
                onCompletion?.invoke()
            }

            val stepInterval = RAMP_DURATION_MS / RAMP_STEPS
            val volumeStep = (TARGET_VOLUME - INITIAL_VOLUME) / RAMP_STEPS

            rampJob = scope.launch(Dispatchers.Default) {
                for (i in 1..RAMP_STEPS) {
                    delay(stepInterval)
                    if (!isActive || mediaPlayer != player || !player.isPlaying) break
                    currentVolume = (currentVolume + volumeStep).coerceAtMost(TARGET_VOLUME)
                    player.setVolume(currentVolume, currentVolume)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error starting preview playback", e)
            stop()
        }
    }

    fun stop() {
        rampJob?.cancel()
        rampJob = null
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing MediaPlayer", e)
        } finally {
            mediaPlayer = null
        }
    }

    fun isPlaying(): Boolean {
        return try {
            mediaPlayer?.isPlaying == true
        } catch (e: Exception) {
            false
        }
    }
}
