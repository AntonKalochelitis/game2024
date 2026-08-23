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
                "ach_1_title",
                "ach_1_desc"
            ),
            Triple(
                2,
                "ach_2_title",
                "ach_2_desc"
            ),
            Triple(
                3,
                "ach_3_title",
                "ach_3_desc"
            ),
            Triple(
                4,
                "ach_4_title",
                "ach_4_desc"
            ),
            Triple(
                5,
                "ach_5_title",
                "ach_5_desc"
            ),
            Triple(
                6,
                "ach_6_title",
                "ach_6_desc"
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
            "id DESC"
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

        /**
         * IMPORTANT: If you change the database schema (add, rename, or remove columns/tables),
         * you MUST increment this version number by 1. This triggers onUpgrade(),
         * which ensures that existing users receive the updated structure.
         */
        private const val DATABASE_VERSION = 2
    }
}
