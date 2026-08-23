package com.wdevelop.game2048

import kotlin.random.Random

object GameEngine {

    const val SIZE = 4

    private var nextId = 1L

    fun createNewGame(): List<TileModel> {
        nextId = 1L

        var tiles = emptyList<TileModel>()

        tiles = addRandomTile(tiles)
        tiles = addRandomTile(tiles)

        return tiles
    }

    fun restoreGame(tiles: List<TileModel>) {
        val maxId = tiles.maxOfOrNull { it.id } ?: 0L
        nextId = maxId + 1
    }

    fun move(
        tiles: List<TileModel>,
        direction: Direction
    ): MoveResult {

        val grid = Array(SIZE) {
            arrayOfNulls<TileModel>(SIZE)
        }

        tiles.forEach { tile ->
            grid[tile.row][tile.column] = tile
        }

        val result =
            when (direction) {
                Direction.LEFT ->
                    moveLeft(grid)

                Direction.RIGHT ->
                    moveRight(grid)

                Direction.UP ->
                    moveUp(grid)

                Direction.DOWN ->
                    moveDown(grid)
            }

        if (!result.moved) {
            return result
        }

        val withNewTile =
            addRandomTile(result.tiles)

        return result.copy(
            tiles = withNewTile
        )
    }

    private fun moveLeft(
        grid: Array<Array<TileModel?>>
    ): MoveResult {

        return moveRows(
            grid = grid,
            reverse = false
        )
    }

    private fun moveRight(
        grid: Array<Array<TileModel?>>
    ): MoveResult {

        return moveRows(
            grid = grid,
            reverse = true
        )
    }

    private fun moveRows(
        grid: Array<Array<TileModel?>>,
        reverse: Boolean
    ): MoveResult {

        val output = mutableListOf<TileModel>()
        var score = 0
        var moved = false
        var reached2048 = false

        for (row in 0 until SIZE) {

            val line = (0 until SIZE)
                .mapNotNull { column ->
                    grid[row][column]
                }

            val ordered =
                if (reverse) {
                    line.reversed()
                } else {
                    line
                }

            val merged =
                mergeLine(
                    ordered,
                    horizontal = true,
                    fixedIndex = row,
                    reverse = reverse
                )

            output += merged.tiles

            score += merged.score
            moved = moved || merged.moved
            reached2048 =
                reached2048 || merged.reached2048
        }

        return MoveResult(
            tiles = output,
            scoreAdded = score,
            moved = moved,
            reached2048 = reached2048
        )
    }

    private fun moveUp(
        grid: Array<Array<TileModel?>>
    ): MoveResult {

        return moveColumns(
            grid = grid,
            reverse = false
        )
    }

    private fun moveDown(
        grid: Array<Array<TileModel?>>
    ): MoveResult {

        return moveColumns(
            grid = grid,
            reverse = true
        )
    }

    private fun moveColumns(
        grid: Array<Array<TileModel?>>,
        reverse: Boolean
    ): MoveResult {

        val output = mutableListOf<TileModel>()
        var score = 0
        var moved = false
        var reached2048 = false

        for (column in 0 until SIZE) {

            val line = (0 until SIZE)
                .mapNotNull { row ->
                    grid[row][column]
                }

            val ordered =
                if (reverse) {
                    line.reversed()
                } else {
                    line
                }

            val merged =
                mergeLine(
                    ordered,
                    horizontal = false,
                    fixedIndex = column,
                    reverse = reverse
                )

            output += merged.tiles

            score += merged.score
            moved = moved || merged.moved
            reached2048 =
                reached2048 || merged.reached2048
        }

        return MoveResult(
            tiles = output,
            scoreAdded = score,
            moved = moved,
            reached2048 = reached2048
        )
    }

    private data class LineResult(
        val tiles: List<TileModel>,
        val score: Int,
        val moved: Boolean,
        val reached2048: Boolean
    )

    private fun mergeLine(
        line: List<TileModel>,
        horizontal: Boolean,
        fixedIndex: Int,
        reverse: Boolean
    ): LineResult {

        val result = mutableListOf<TileModel>()

        var score = 0
        var changed = false
        var reached2048 = false

        var index = 0

        while (index < line.size) {

            val current = line[index]

            if (
                index + 1 < line.size &&
                line[index + 1].value == current.value
            ) {
                val second = line[index + 1]

                val mergedValue =
                    current.value * 2

                val position =
                    result.size

                val row =
                    if (horizontal) fixedIndex
                    else if (!reverse) position
                    else SIZE - 1 - position

                val column =
                    if (!horizontal) fixedIndex
                    else if (!reverse) position
                    else SIZE - 1 - position

                result += TileModel(
                    id = current.id,
                    value = mergedValue,
                    row = row,
                    column = column,
                    isMerged = true
                )

                score += mergedValue

                if (mergedValue >= 2048) {
                    reached2048 = true
                }

                changed = true
                index += 2

            } else {

                val position =
                    result.size

                val row =
                    if (horizontal) fixedIndex
                    else if (!reverse) position
                    else SIZE - 1 - position

                val column =
                    if (!horizontal) fixedIndex
                    else if (!reverse) position
                    else SIZE - 1 - position

                val newTile =
                    current.copy(
                        row = row,
                        column = column,
                        isNew = false,
                        isMerged = false
                    )

                if (
                    current.row != row ||
                    current.column != column
                ) {
                    changed = true
                }

                result += newTile

                index++
            }
        }

        return LineResult(
            tiles = result,
            score = score,
            moved = changed,
            reached2048 = reached2048
        )
    }

    fun addRandomTile(
        tiles: List<TileModel>
    ): List<TileModel> {

        val occupied =
            tiles.map { it.row to it.column }.toSet()

        val empty =
            mutableListOf<Pair<Int, Int>>()

        for (row in 0 until SIZE) {
            for (column in 0 until SIZE) {

                if ((row to column) !in occupied) {
                    empty += row to column
                }
            }
        }

        if (empty.isEmpty()) {
            return tiles
        }

        val (row, column) =
            empty.random()

        val value =
            if (Random.nextInt(100) < 90) {
                2
            } else {
                4
            }

        return tiles + TileModel(
            id = nextId++,
            value = value,
            row = row,
            column = column,
            isNew = true
        )
    }

    fun canMove(
        tiles: List<TileModel>
    ): Boolean {

        if (tiles.size < SIZE * SIZE) {
            return true
        }

        val grid = Array(SIZE) {
            arrayOfNulls<TileModel>(SIZE)
        }

        tiles.forEach {
            grid[it.row][it.column] = it
        }

        for (row in 0 until SIZE) {
            for (column in 0 until SIZE) {

                val value =
                    grid[row][column]?.value
                        ?: return true

                if (
                    column + 1 < SIZE &&
                    grid[row][column + 1]?.value == value
                ) {
                    return true
                }

                if (
                    row + 1 < SIZE &&
                    grid[row + 1][column]?.value == value
                ) {
                    return true
                }
            }
        }

        return false
    }
}
