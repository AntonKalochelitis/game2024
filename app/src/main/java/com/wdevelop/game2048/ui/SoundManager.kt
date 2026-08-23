package com.wdevelop.game2048.ui

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.wdevelop.game2048.R

class SoundManager(context: Context) {
    private val soundPool: SoundPool
    private val moveSoundId: Int
    private val winSoundId: Int
    private val gameOverSoundId: Int

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()

        moveSoundId = soundPool.load(context, R.raw.sword_cut, 1)
        winSoundId = soundPool.load(context, R.raw.winner_sound, 1)
        gameOverSoundId = soundPool.load(context, R.raw.game_over_sound, 1)
    }

    fun playMove() {
        soundPool.play(moveSoundId, 1f, 1f, 0, 0, 1f)
    }

    fun playWin() {
        soundPool.play(winSoundId, 1f, 1f, 0, 0, 1f)
    }

    fun playGameOver() {
        soundPool.play(gameOverSoundId, 1f, 1f, 0, 0, 1f)
    }

    fun release() {
        soundPool.release()
    }
}
