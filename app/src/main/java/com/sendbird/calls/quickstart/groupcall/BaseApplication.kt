package com.sendbird.calls.quickstart.groupcall

import android.app.Application
import com.sendbird.calls.SendBirdCall
import com.sendbird.calls.quickstart.groupcall.util.SharedPreferencesManager

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SendBirdCall.setLoggerLevel(SendBirdCall.LOGGER_INFO)
        SharedPreferencesManager.init(applicationContext)
    }
}
