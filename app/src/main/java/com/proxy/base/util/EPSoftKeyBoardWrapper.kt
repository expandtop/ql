package com.proxy.base.util

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import androidx.viewbinding.ViewBinding
import com.proxy.base.util.EPSoftKeyBoardListener.OnSoftKeyBoardChangeListener

/**
 * 2024/10/10
 */
object EPSoftKeyBoardWrapper {

    fun register(activity: AppCompatActivity, binding: ViewBinding) {
        val contentHeight by lazy { binding.root.measuredHeight }
        EPSoftKeyBoardListener.setListener(activity, object : OnSoftKeyBoardChangeListener {
            override fun keyBoardShow(height: Int) {
                binding.root.updateLayoutParams {
                    this.height = contentHeight - height
                }
            }

            override fun keyBoardHide(height: Int) {
                binding.root.updateLayoutParams {
                    this.height = contentHeight
                }
            }
        })
    }

}