package com.sinch.android.rtc.sample.push

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import com.example.helloworld.R
import java.io.IOException

class AudioPlayer(private val context: Context) {

    private val audioManager: AudioManager
        get() = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val mediaPlayer: MediaPlayer by lazy {
        MediaPlayer()
    }

    private val progressTone: AudioTrack by lazy {
        createProgressTone()
    }

    fun playRingtone() {
        when (audioManager.ringerMode) {
            AudioManager.RINGER_MODE_NORMAL -> {
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING)
                try {
                    mediaPlayer.setDataSource(
                        context,
                        Uri.parse(
                            "android.resource://" + context.packageName + "/"
                                    + R.raw.phone_loud1
                        )
                    )
                    mediaPlayer.prepare()
                } catch (e: IOException) {
                    Log.e(TAG, "Could not setup media player for ringtone")
                    return
                }
                mediaPlayer.isLooping = true
                mediaPlayer.start()
            }
            else -> {
                Log.w(
                    TAG,
                    "AudioManager.ringerMode is either vibrate or silent aborting playing the ringtone"
                )
            }
        }
    }

    fun stopRingtone() {
        mediaPlayer.stop()
    }

    fun playProgressTone() {
        stopProgressTone()
        try {
            progressTone.play()
        } catch (e: Exception) {
            Log.e(TAG, "Could not play progress tone", e)
        }
    }

    fun stopProgressTone() {
        progressTone.stop()
    }

    private fun createProgressTone(): AudioTrack {
        val fd = context.resources.openRawResourceFd(R.raw.progress_tone)
        val length = fd.length.toInt()
        val audioTrack = AudioTrack(
            AudioManager.STREAM_VOICE_CALL, SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, length,
            AudioTrack.MODE_STATIC
        )
        val data = ByteArray(length)
        readFileToBytes(fd, data)
        audioTrack.write(data, 0, data.size)
        audioTrack.setLoopPoints(0, data.size / 2, 30)
        return audioTrack
    }

    private fun readFileToBytes(fd: AssetFileDescriptor, data: ByteArray) {
        val inputStream = fd.createInputStream()
        var bytesRead = 0
        while (bytesRead < data.size) {
            val res = inputStream.read(data, bytesRead, data.size - bytesRead)
            if (res == -1) {
                break
            }
            bytesRead += res
        }
    }

    companion object {
        private val TAG = AudioPlayer::class.java.simpleName
        private const val SAMPLE_RATE = 16000
    }

}