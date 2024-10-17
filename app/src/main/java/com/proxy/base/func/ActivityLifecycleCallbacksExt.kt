package com.proxy.base.func

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.util.concurrent.atomic.AtomicReference

/**
 * 2024/10/14
 */
class ActivityLifecycleExt {

    private val currentActivity = AtomicReference<Activity>(null)

    fun getActivity(): Activity? {
        val activity = currentActivity.get()
        return if (activity != null && !activity.isDestroyed) activity else null
    }

    inline fun <reified T : Activity> get(): T? {
        return getActivity()?.let { it as? T }
    }

    fun initialize(application: Application) {
        application.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

            }

            override fun onActivityStarted(activity: Activity) {

            }

            override fun onActivityResumed(activity: Activity) {
                currentActivity.set(activity)
            }

            override fun onActivityPaused(activity: Activity) {

            }

            override fun onActivityStopped(activity: Activity) {

            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

            }

            override fun onActivityDestroyed(activity: Activity) {

            }
        })
    }

}