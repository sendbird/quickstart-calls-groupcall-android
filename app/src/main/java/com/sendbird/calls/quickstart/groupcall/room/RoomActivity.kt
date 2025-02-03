package com.sendbird.calls.quickstart.groupcall.room

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import com.sendbird.calls.quickstart.groupcall.R
import com.sendbird.calls.quickstart.groupcall.util.BaseActivity
import com.sendbird.calls.quickstart.groupcall.util.EXTRA_IS_NEWLY_CREATED
import com.sendbird.calls.quickstart.groupcall.util.EXTRA_ROOM_ID

class RoomActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)
        handleBackPressed()
    }

    private fun handleBackPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentFragment =
                    supportFragmentManager.findFragmentById(R.id.nav_host_room).let {
                        it?.childFragmentManager?.fragments?.firstOrNull()
                    }

                if (currentFragment is GroupCallFragment) {
                    // ignore event
                } else {
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    fun getRoomId(): String {
        return intent.getStringExtra(EXTRA_ROOM_ID) ?: throw IllegalStateException()
    }

    fun isNewlyCreated(): Boolean {
        return intent.getBooleanExtra(EXTRA_IS_NEWLY_CREATED, false)
    }
}
