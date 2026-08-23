package com.wdevelop.game2048.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.wdevelop.game2048.Direction
import com.wdevelop.game2048.TileModel
import kotlin.math.abs

@Composable
fun GameBoard(
    tiles: List<TileModel>,
    onMove: (Direction) -> Unit,
    modifier: Modifier = Modifier
) {
    var drag by remember {
        mutableStateOf(Offset.Zero)
    }

    BoxWithConstraints(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(GameColors.Board)
            .padding(10.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        drag = Offset.Zero
                    },
                    onDrag = { change, amount ->
                        change.consume()
                        drag += amount
                    },
                    onDragEnd = {
                        val threshold = 50f

                        if (
                            abs(drag.x) < threshold &&
                            abs(drag.y) < threshold
                        ) {
                            return@detectDragGestures
                        }

                        val direction =
                            if (abs(drag.x) > abs(drag.y)) {
                                if (drag.x > 0)
                                    Direction.RIGHT
                                else
                                    Direction.LEFT
                            } else {
                                if (drag.y > 0)
                                    Direction.DOWN
                                else
                                    Direction.UP
                            }

                        onMove(direction)
                    }
                )
            }
    ) {

        val spacing = 8.dp
        val cellSize =
            (maxWidth - spacing * 3) / 4

        for (row in 0 until 4) {
            for (column in 0 until 4) {

                Box(
                    modifier = Modifier
                        .padding(
                            start =
                                (cellSize + spacing) * column,
                            top =
                                (cellSize + spacing) * row
                        )
                        .size(cellSize)
                        .background(
                            GameColors.EmptyTile,
                            RoundedCornerShape(14.dp)
                        )
                )
            }
        }

        tiles.forEach { tile ->

            val x =
                animateDpAsState(
                    targetValue =
                        (cellSize + spacing) * tile.column,
                    animationSpec =
                        tween(150),
                    label = "tileX"
                )

            val y =
                animateDpAsState(
                    targetValue =
                        (cellSize + spacing) * tile.row,
                    animationSpec =
                        tween(150),
                    label = "tileY"
                )

            Box(
                modifier = Modifier
                    .padding(
                        start = x.value,
                        top = y.value
                    )
                    .size(cellSize)
                    .zIndex(
                        if (tile.isMerged) 2f
                        else 1f
                    )
            ) {
                TileView(
                    tile = tile,
                    modifier =
                        Modifier.fillMaxSize()
                )
            }
        }
    }
}
