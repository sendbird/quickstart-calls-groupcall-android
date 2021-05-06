package com.sendbird.calls.quickstart.groupcall.util

import android.content.Context
import android.content.SharedPreferences

private const val SHARED_PREFERENCES_FILE_NAME = "sendbird_groupcall"
private const val PREF_KEY_USER_ID = "pref_key_user_id"
private const val PREF_KEY_ACCESS_TOKEN = "pref_key_access_token"
private const val PREF_KEY_APP_ID = "pref_key_app_id"

object SharedPreferencesManager {
    lateinit var prefs: SharedPreferences

    var appId: String? = null
        get() = prefs.getString(PREF_KEY_APP_ID, null)
        set(value) {
            if (value.isNullOrEmpty()) {
                return
            }

            prefs.edit().putString(PREF_KEY_APP_ID, value).apply()
            field = value
        }

    var userId: String? = null
        get() = prefs.getString(PREF_KEY_USER_ID, null)
        set(value) {
            if (value.isNullOrEmpty()) {
                return
            }

            prefs.edit().putString(PREF_KEY_USER_ID, value).apply()
            field = value
        }

    var accessToken: String? = null
        get() = prefs.getString(PREF_KEY_ACCESS_TOKEN, null)
        set(value) {
            if (value.isNullOrEmpty()) {
                return
            }

            prefs.edit().putString(PREF_KEY_ACCESS_TOKEN, value).apply()
            field = value
        }

    fun init(context: Context) {
        prefs = context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, 0)
    }
}
