package com.wdevelop.game2048

data class GameState(
    val tiles: List<TileModel>,
    val score: Int,
    val bestScore: Int,
    val isGameOver: Boolean = false,
    val showWinDialog: Boolean = false,
    val winAlreadyShown: Boolean = false,
    val showSettings: Boolean = false,
    val soundEnabled: Boolean = true
)
