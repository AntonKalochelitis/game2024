package com.wdevelop.game2048.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

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

        val achievements = listOf(
            Triple(
                1,
                "Первая сотня",
                "Создайте плитку 128"
            ),
            Triple(
                2,
                "Уверенная игра",
                "Создайте плитку 256"
            ),
            Triple(
                3,
                "Сильный игрок",
                "Создайте плитку 512"
            ),
            Triple(
                4,
                "Профессионал",
                "Создайте плитку 1024"
            ),
            Triple(
                5,
                "2048",
                "Соберите плитку 2048"
            ),
            Triple(
                6,
                "Мастер очков",
                "Наберите 10000 очков"
            )
        )

        achievements.forEach { item ->

            val values =
                ContentValues().apply {
                    put("id", item.first)
                    put("title", item.second)
                    put("description", item.third)
                    put("unlocked", 0)
                }

            db.insert(
                "achievements",
                null,
                values
            )
        }
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        db.execSQL(
            "DROP TABLE IF EXISTS achievements"
        )

        onCreate(db)
    }

    fun getAchievements(): List<Achievement> {

        val result = mutableListOf<Achievement>()

        val cursor = readableDatabase.query(
            "achievements",
            null,
            null,
            null,
            null,
            null,
            "id ASC"
        )

        cursor.use {

            while (it.moveToNext()) {

                result += Achievement(
                    id = it.getInt(
                        it.getColumnIndexOrThrow("id")
                    ),
                    title = it.getString(
                        it.getColumnIndexOrThrow("title")
                    ),
                    description = it.getString(
                        it.getColumnIndexOrThrow("description")
                    ),
                    unlocked = it.getInt(
                        it.getColumnIndexOrThrow("unlocked")
                    ) == 1
                )
            }
        }

        return result
    }

    fun unlock(
        achievementId: Int
    ) {

        val values =
            ContentValues().apply {
                put("unlocked", 1)
            }

        writableDatabase.update(
            "achievements",
            values,
            "id = ?",
            arrayOf(achievementId.toString())
        )
    }

    companion object {

        private const val DATABASE_NAME =
            "game_2048.db"

        private const val DATABASE_VERSION = 1
    }
}
