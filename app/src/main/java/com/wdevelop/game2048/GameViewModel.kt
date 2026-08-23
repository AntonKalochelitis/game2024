package com.wdevelop.game2048

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.wdevelop.game2048.data.Achievement
import com.wdevelop.game2048.data.AchievementDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val preferences =
        application.getSharedPreferences(
            "game_preferences",
            Context.MODE_PRIVATE
        )

    private val database =
        AchievementDatabase(application)

    private val _state =
        MutableStateFlow(initialState())

    val state: StateFlow<GameState> =
        _state.asStateFlow()

    private fun initialState(): GameState {
        return GameState(
            tiles = GameEngine.createNewGame(),
            score = 0,
            bestScore = loadBestScore(),
            soundEnabled =
                preferences.getBoolean(
                    KEY_SOUND,
                    true
                )
        )
    }

    fun move(direction: Direction) {

        val current = _state.value

        if (current.isGameOver) {
            return
        }

        val result =
            GameEngine.move(
                current.tiles,
                direction
            )

        if (!result.moved) {
            return
        }

        val score =
            current.score + result.scoreAdded

        val best =
            maxOf(
                current.bestScore,
                score
            )

        saveBestScore(best)

        val isGameOver = !GameEngine.canMove(result.tiles)

        if (isGameOver) {
            unlockAchievements(
                result.tiles,
                score
            )
        }

        val showWin =
            result.reached2048 &&
                !current.winAlreadyShown

        _state.value =
            current.copy(
                tiles = result.tiles,
                score = score,
                bestScore = best,
                isGameOver = isGameOver,
                showWinDialog = showWin,
                winAlreadyShown =
                    current.winAlreadyShown ||
                        result.reached2048
            )
    }

    fun newGame() {

        _state.value =
            GameState(
                tiles =
                    GameEngine.createNewGame(),
                score = 0,
                bestScore =
                    loadBestScore(),
                soundEnabled =
                    _state.value.soundEnabled
            )
    }

    fun openSettings() {

        _state.value =
            _state.value.copy(
                showSettings = true
            )
    }

    fun closeSettings() {

        _state.value =
            _state.value.copy(
                showSettings = false
            )
    }

    fun setSoundEnabled(
        enabled: Boolean
    ) {

        preferences.edit()
            .putBoolean(
                KEY_SOUND,
                enabled
            )
            .apply()

        _state.value =
            _state.value.copy(
                soundEnabled = enabled
            )
    }

    fun continueAfterWin() {

        _state.value =
            _state.value.copy(
                showWinDialog = false
            )
    }

    fun getAchievements():
        List<Achievement> =
        database.getAchievements()

    private fun unlockAchievements(
        tiles: List<TileModel>,
        score: Int
    ) {

        val maxValue =
            tiles.maxOfOrNull {
                it.value
            } ?: 0

        if (maxValue >= 128) {
            database.unlock(1)
        }

        if (maxValue >= 256) {
            database.unlock(2)
        }

        if (maxValue >= 512) {
            database.unlock(3)
        }

        if (maxValue >= 1024) {
            database.unlock(4)
        }

        if (maxValue >= 2048) {
            database.unlock(5)
        }

        if (score >= 10000) {
            database.unlock(6)
        }
    }

    private fun loadBestScore(): Int {
        return preferences.getInt(
            KEY_BEST_SCORE,
            0
        )
    }

    private fun saveBestScore(
        score: Int
    ) {
        preferences.edit()
            .putInt(
                KEY_BEST_SCORE,
                score
            )
            .apply()
    }

    private companion object {
        const val KEY_BEST_SCORE = "best_score"
        private const val KEY_SOUND =
            "sound_enabled"
    }
}
