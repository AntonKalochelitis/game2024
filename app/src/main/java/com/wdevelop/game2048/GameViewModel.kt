package com.wdevelop.game2048

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wdevelop.game2048.data.Achievement
import com.wdevelop.game2048.data.AchievementDatabase
import com.wdevelop.game2048.ui.SoundManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GameViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val preferences =
        application.getSharedPreferences(
            "game_preferences",
            Context.MODE_PRIVATE
        )

    private val database = AchievementDatabase.getInstance(application)
    private val soundManager = SoundManager(application)

    private val _state = MutableStateFlow<GameState?>(null)
    val state: StateFlow<GameState?> = _state.asStateFlow()

    private val _achievements = MutableStateFlow<List<Achievement>>(emptyList())
    val achievements: StateFlow<List<Achievement>> = _achievements.asStateFlow()

    init {
        loadInitialState()
        loadAchievements()
    }

    private fun loadInitialState() {
        viewModelScope.launch(Dispatchers.IO) {
            val savedTiles = database.loadSavedTiles()
            val initialState = if (savedTiles != null) {
                GameEngine.restoreGame(savedTiles)
                GameState(
                    tiles = savedTiles,
                    score = database.loadSavedScore(),
                    bestScore = loadBestScore(),
                    currentMaxTile = savedTiles.maxOfOrNull { it.value } ?: 0,
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
            withContext(Dispatchers.Main) {
                _state.value = initialState
            }
        }
    }

    private fun loadAchievements() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = database.getAchievements()
            withContext(Dispatchers.Main) {
                _achievements.value = list
            }
        }
    }

    fun move(direction: Direction) {
        val current = _state.value ?: return
        if (current.isGameOver) return

        val result = GameEngine.move(current.tiles, direction)
        if (!result.moved) return

        if (current.soundEnabled) {
            soundManager.playMove()
        }

        val score = current.score + result.scoreAdded
        val best = maxOf(current.bestScore, score)
        saveBestScore(best)

        val currentMax = result.tiles.maxOfOrNull { it.value } ?: 0
        val isGameOver = !GameEngine.canMove(result.tiles)

        if (isGameOver) {
            viewModelScope.launch(Dispatchers.IO) {
                unlockAchievements(result.tiles, score)
                database.saveMaxTileRecord(currentMax)
                loadAchievements()
            }
            if (current.soundEnabled) {
                soundManager.playGameOver()
            }
        }

        val showWin = result.reached2048 && !current.winAlreadyShown
        if (showWin && current.soundEnabled) {
            soundManager.playWin()
        }

        val nextState = current.copy(
            tiles = result.tiles,
            score = score,
            bestScore = best,
            currentMaxTile = currentMax,
            maxTileRecord = if (isGameOver) maxOf(current.maxTileRecord, currentMax) else current.maxTileRecord,
            isGameOver = isGameOver,
            showWinDialog = showWin,
            winAlreadyShown = current.winAlreadyShown || result.reached2048
        )

        _state.value = nextState
        
        viewModelScope.launch(Dispatchers.IO) {
            database.saveGame(nextState.tiles, nextState.score, nextState.winAlreadyShown)
        }
    }

    fun newGame() {
        viewModelScope.launch(Dispatchers.IO) {
            database.clearSavedGame()
            val tiles = GameEngine.createNewGame()
            val newState = GameState(
                tiles = tiles,
                score = 0,
                bestScore = loadBestScore(),
                currentMaxTile = tiles.maxOfOrNull { it.value } ?: 0,
                maxTileRecord = database.loadMaxTile(),
                maxTileDate = database.loadMaxTileDate(),
                soundEnabled = _state.value?.soundEnabled ?: true
            )
            withContext(Dispatchers.Main) {
                _state.value = newState
            }
        }
    }

    fun openSettings() {
        loadAchievements()
        _state.value = _state.value?.copy(showSettings = true)
    }

    fun closeSettings() {
        _state.value = _state.value?.copy(showSettings = false)
    }

    fun setSoundEnabled(enabled: Boolean) {
        preferences.edit().putBoolean(KEY_SOUND, enabled).apply()
        _state.value = _state.value?.copy(soundEnabled = enabled)
    }

    fun continueAfterWin() {
        _state.value = _state.value?.copy(showWinDialog = false)
    }

    private fun unlockAchievements(tiles: List<TileModel>, score: Int) {
        val maxValue = tiles.maxOfOrNull { it.value } ?: 0
        if (maxValue >= 128) database.unlock(1)
        if (maxValue >= 256) database.unlock(2)
        if (maxValue >= 512) database.unlock(3)
        if (maxValue >= 1024) database.unlock(4)
        if (maxValue >= 2048) database.unlock(5)
        if (score >= 10000) database.unlock(6)
    }

    private fun loadBestScore(): Int = preferences.getInt(KEY_BEST_SCORE, 0)

    private fun saveBestScore(score: Int) {
        preferences.edit().putInt(KEY_BEST_SCORE, score).apply()
    }

    override fun onCleared() {
        soundManager.release()
    }

    private companion object {
        const val KEY_BEST_SCORE = "best_score"
        const val KEY_SOUND = "sound_enabled"
    }
}
