package com.proxy.base.ui.service

import androidx.fragment.app.FragmentActivity
import com.proxy.base.model.AppViewModel
import com.proxy.base.util.BrowserUtils

/**
 * 2024/8/7
 */
object AppServer {
    fun open(context: FragmentActivity) {
        AppViewModel.requireCommChannelConfig {
            BrowserUtils.openBrowser(context, it.customer_service_address)
        }
    }

}