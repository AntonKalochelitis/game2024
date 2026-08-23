package com.wdevelop.game2048

data class MoveResult(
    val tiles: List<TileModel>,
    val scoreAdded: Int,
    val moved: Boolean,
    val reached2048: Boolean
)
