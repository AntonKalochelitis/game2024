package com.wdevelop.game2048

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.wdevelop.game2048.data.Achievement
import com.wdevelop.game2048.data.AchievementDatabase
import com.wdevelop.game2048.ui.SoundManager
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

    private val soundManager = SoundManager(application)

    private val _state =
        MutableStateFlow(initialState())

    val state: StateFlow<GameState> =
        _state.asStateFlow()

    private fun initialState(): GameState {
        val savedTiles = database.loadSavedTiles()
        return if (savedTiles != null) {
            GameEngine.restoreGame(savedTiles)
            val currentMax = savedTiles.maxOfOrNull { it.value } ?: 0
            GameState(
                tiles = savedTiles,
                score = database.loadSavedScore(),
                bestScore = loadBestScore(),
                currentMaxTile = currentMax,
                maxTileRecord = database.loadMaxTile(),
                maxTileDate = database.loadMaxTileDate(),
                winAlreadyShown = database.loadWinShown(),
                soundEnabled = preferences.getBoolean(KEY_SOUND, true)
            )
        } else {
            val initialTiles = GameEngine.createNewGame()
            GameState(
                tiles = initialTiles,
                score = 0,
                bestScore = loadBestScore(),
                currentMaxTile = initialTiles.maxOfOrNull { it.value } ?: 0,
                maxTileRecord = database.loadMaxTile(),
                maxTileDate = database.loadMaxTileDate(),
                soundEnabled = preferences.getBoolean(KEY_SOUND, true)
            )
        }
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

        if (current.soundEnabled) {
            soundManager.playMove()
        }

        val score =
            current.score + result.scoreAdded

        val best =
            maxOf(
                current.bestScore,
                score
            )

        saveBestScore(best)

        val currentMax = result.tiles.maxOfOrNull { it.value } ?: 0
        val isGameOver = !GameEngine.canMove(result.tiles)

        if (isGameOver) {
            unlockAchievements(
                result.tiles,
                score
            )
            database.saveMaxTileRecord(currentMax)
            
            if (current.soundEnabled) {
                soundManager.playGameOver()
            }
        }

        val showWin =
            result.reached2048 &&
                !current.winAlreadyShown
        
        if (showWin && current.soundEnabled) {
            soundManager.playWin()
        }

        val nextState = current.copy(
            tiles = result.tiles,
            score = score,
            bestScore = best,
            currentMaxTile = currentMax,
            maxTileRecord = database.loadMaxTile(),
            maxTileDate = database.loadMaxTileDate(),
            isGameOver = isGameOver,
            showWinDialog = showWin,
            winAlreadyShown = current.winAlreadyShown || result.reached2048
        )

        _state.value = nextState
        database.saveGame(nextState.tiles, nextState.score, nextState.winAlreadyShown)
    }

    override fun onCleared() {
        super.onCleared()
        soundManager.release()
    }


    fun newGame() {
        database.clearSavedGame()
        val tiles = GameEngine.createNewGame()
        _state.value =
            GameState(
                tiles = tiles,
                score = 0,
                bestScore =
                    loadBestScore(),
                currentMaxTile = tiles.maxOfOrNull { it.value } ?: 0,
                maxTileRecord =
                    database.loadMaxTile(),
                maxTileDate =
                    database.loadMaxTileDate(),
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
