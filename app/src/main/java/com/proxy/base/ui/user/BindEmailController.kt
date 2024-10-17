package com.proxy.base.ui.user

import androidx.fragment.app.FragmentActivity

object BindEmailController {

    private val activitys = mutableListOf<FragmentActivity>()

    fun add(activity: FragmentActivity) {
        activitys.add(activity)
    }

    fun finish() {
        for (activity in activitys) {
            if (!activity.isFinishing && !activity.isDestroyed) {
                activity.finish()
            }
        }
    }

}