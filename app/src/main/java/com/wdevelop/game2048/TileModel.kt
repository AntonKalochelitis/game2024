package com.wdevelop.game2048

data class TileModel(
    val id: Long,
    val value: Int,
    val row: Int,
    val column: Int,
    val isNew: Boolean = false,
    val isMerged: Boolean = false
)
