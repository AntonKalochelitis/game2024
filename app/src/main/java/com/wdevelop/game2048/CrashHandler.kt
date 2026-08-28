package com.wdevelop.game2048

import android.content.Context
import android.content.Intent
import android.util.Log
import kotlin.system.exitProcess

class CrashHandler(private val context: Context) : Thread.UncaughtExceptionHandler {
    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        val stackTrace = Log.getStackTraceString(throwable)
        
        // Сохраняем ошибку в SharedPreferences
        val prefs = context.getSharedPreferences("crash_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("last_crash", stackTrace).apply()

        Log.e("CRITICAL_ERROR", stackTrace)

        // Завершаем процесс
        exitProcess(1)
    }

    companion object {
        fun init(context: Context) {
            Thread.setDefaultUncaughtExceptionHandler(CrashHandler(context))
        }

        fun getLatestCrash(context: Context): String? {
            val prefs = context.getSharedPreferences("crash_prefs", Context.MODE_PRIVATE)
            val crash = prefs.getString("last_crash", null)
            // Очищаем после получения, чтобы не показывать одно и то же
            if (crash != null) {
                prefs.edit().remove("last_crash").apply()
            }
            return crash
        }
    }
}
