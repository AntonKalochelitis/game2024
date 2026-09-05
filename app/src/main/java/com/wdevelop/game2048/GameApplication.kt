package com.wdevelop.game2048

import android.app.Application

class GameApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Инициализируем перехватчик максимально рано
        CrashHandler.init(this)
    }
}
