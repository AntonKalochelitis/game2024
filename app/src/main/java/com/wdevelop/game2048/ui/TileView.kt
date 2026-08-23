package com.wdevelop.game2048.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wdevelop.game2048.TileModel

@Composable
fun TileView(
    tile: TileModel,
    modifier: Modifier = Modifier
) {

    val scale =
        remember(tile.id) {
            Animatable(
                if (tile.isNew) 0.2f else 1f
            )
        }

    LaunchedEffect(tile.id) {

        if (tile.isNew) {

            scale.animateTo(
                targetValue = 1f,
                animationSpec =
                    tween(180)
            )
        }
    }

    LaunchedEffect(
        tile.isMerged
    ) {

        if (tile.isMerged) {

            scale.snapTo(1f)

            scale.animateTo(
                1.15f,
                tween(90)
            )

            scale.animateTo(
                1f,
                tween(110)
            )
        }
    }

    Box(
        modifier = modifier
            .scale(scale.value)
            .background(
                color = tileColor(tile.value),
                shape =
                    RoundedCornerShape(14.dp)
            ),
        contentAlignment =
            Alignment.Center
    ) {

        Text(
            text = tile.value.toString(),
            color = tileTextColor(tile.value),
            fontSize = tileFontSize(tile.value),
            fontWeight = FontWeight.ExtraBold
        )
    }
}

private fun tileColor(
    value: Int
): Color {

    return when (value) {
        2 -> GameColors.Tile2
        4 -> GameColors.Tile4
        8 -> GameColors.Tile8
        16 -> GameColors.Tile16
        32 -> GameColors.Tile32
        64 -> GameColors.Tile64
        128 -> GameColors.Tile128
        256 -> GameColors.Tile256
        512 -> GameColors.Tile512
        1024 -> GameColors.Tile1024
        else -> GameColors.Tile2048
    }
}

private fun tileTextColor(
    value: Int
): Color {

    return when (value) {
        2, 4 -> GameColors.TextDark
        else -> Color.White
    }
}

private fun tileFontSize(
    value: Int
) =
    when {
        value < 100 -> 38.sp
        value < 1000 -> 32.sp
        value < 10000 -> 25.sp
        else -> 20.sp
    }
