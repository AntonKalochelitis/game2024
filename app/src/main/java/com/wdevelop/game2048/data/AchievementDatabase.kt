package com.wdevelop.game2048.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.wdevelop.game2048.TileModel

data class Achievement(
    val id: Int,
    val title: String,
    val description: String,
    val unlocked: Boolean
)

class AchievementDatabase(
    context: Context
) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    override fun onCreate(
        db: SQLiteDatabase
    ) {
        db.execSQL(
            """
            CREATE TABLE achievements (
                id INTEGER PRIMARY KEY,
                title TEXT NOT NULL,
                description TEXT NOT NULL,
                unlocked INTEGER NOT NULL DEFAULT 0
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE tiles (
                id INTEGER PRIMARY KEY,
                tile_value INTEGER NOT NULL,
                tile_row INTEGER NOT NULL,
                tile_col INTEGER NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE game_info (
                id INTEGER PRIMARY KEY DEFAULT 1,
                score INTEGER NOT NULL DEFAULT 0,
                win_shown INTEGER NOT NULL DEFAULT 0
            )
            """.trimIndent()
        )

        val achievements = listOf(
            Triple(1, "ach_1_title", "ach_1_desc"),
            Triple(2, "ach_2_title", "ach_2_desc"),
            Triple(3, "ach_3_title", "ach_3_desc"),
            Triple(4, "ach_4_title", "ach_4_desc"),
            Triple(5, "ach_5_title", "ach_5_desc"),
            Triple(6, "ach_6_title", "ach_6_desc")
        )

        achievements.forEach { item ->
            val values = ContentValues().apply {
                put("id", item.first)
                put("title", item.second)
                put("description", item.third)
                put("unlocked", 0)
            }
            db.insert("achievements", null, values)
        }
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        if (oldVersion < 3) {
            db.execSQL("DROP TABLE IF EXISTS tiles")
            db.execSQL("DROP TABLE IF EXISTS game_info")
            db.execSQL(
                """
                CREATE TABLE tiles (
                    id INTEGER PRIMARY KEY,
                    tile_value INTEGER NOT NULL,
                    tile_row INTEGER NOT NULL,
                    tile_col INTEGER NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE TABLE game_info (
                    id INTEGER PRIMARY KEY DEFAULT 1,
                    score INTEGER NOT NULL DEFAULT 0,
                    win_shown INTEGER NOT NULL DEFAULT 0
                )
                """.trimIndent()
            )
        }
    }

    fun saveGame(tiles: List<TileModel>, score: Int, winShown: Boolean) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.delete("tiles", null, null)
            tiles.forEach { tile ->
                val values = ContentValues().apply {
                    put("id", tile.id)
                    put("tile_value", tile.value)
                    put("tile_row", tile.row)
                    put("tile_col", tile.column)
                }
                db.insert("tiles", null, values)
            }

            val infoValues = ContentValues().apply {
                put("id", 1)
                put("score", score)
                put("win_shown", if (winShown) 1 else 0)
            }
            db.insertWithOnConflict("game_info", null, infoValues, SQLiteDatabase.CONFLICT_REPLACE)
            
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun loadSavedTiles(): List<TileModel>? {
        val result = mutableListOf<TileModel>()
        val cursor = readableDatabase.query("tiles", null, null, null, null, null, null)
        if (cursor.count == 0) {
            cursor.close()
            return null
        }
        cursor.use {
            while (it.moveToNext()) {
                result += TileModel(
                    id = it.getLong(it.getColumnIndexOrThrow("id")),
                    value = it.getInt(it.getColumnIndexOrThrow("tile_value")),
                    row = it.getInt(it.getColumnIndexOrThrow("tile_row")),
                    column = it.getInt(it.getColumnIndexOrThrow("tile_col"))
                )
            }
        }
        return result
    }

    fun loadSavedScore(): Int {
        var score = 0
        val cursor = readableDatabase.query("game_info", arrayOf("score"), "id = 1", null, null, null, null)
        if (cursor.moveToFirst()) {
            score = cursor.getInt(0)
        }
        cursor.close()
        return score
    }

    fun loadWinShown(): Boolean {
        var shown = false
        val cursor = readableDatabase.query("game_info", arrayOf("win_shown"), "id = 1", null, null, null, null)
        if (cursor.moveToFirst()) {
            shown = cursor.getInt(0) == 1
        }
        cursor.close()
        return shown
    }

    fun clearSavedGame() {
        val db = writableDatabase
        db.delete("tiles", null, null)
        val infoValues = ContentValues().apply {
            put("id", 1)
            put("score", 0)
            put("win_shown", 0)
        }
        db.insertWithOnConflict("game_info", null, infoValues, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun getAchievements(): List<Achievement> {
        val result = mutableListOf<Achievement>()
        val cursor = readableDatabase.query("achievements", null, null, null, null, null, "id DESC")
        cursor.use {
            while (it.moveToNext()) {
                result += Achievement(
                    id = it.getInt(it.getColumnIndexOrThrow("id")),
                    title = it.getString(it.getColumnIndexOrThrow("title")),
                    description = it.getString(it.getColumnIndexOrThrow("description")),
                    unlocked = it.getInt(it.getColumnIndexOrThrow("unlocked")) == 1
                )
            }
        }
        return result
    }

    fun unlock(achievementId: Int) {
        val values = ContentValues().apply { put("unlocked", 1) }
        writableDatabase.update("achievements", values, "id = ?", arrayOf(achievementId.toString()))
    }

    companion object {
        private const val DATABASE_NAME = "game_2048.db"
        private const val DATABASE_VERSION = 3
    }
}
