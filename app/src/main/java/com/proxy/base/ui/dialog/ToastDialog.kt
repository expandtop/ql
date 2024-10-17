package com.proxy.base.ui.dialog

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.proxy.base.databinding.DialogToastBinding
import ex.ss.lib.base.dialog.BaseDialog
import ex.ss.lib.base.extension.viewBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

fun FragmentActivity.showToastDialog(resId: Int): ToastDialog {
    return showToastDialog(getString(resId))
}

fun Fragment.showToastDialog(resId: Int): ToastDialog {
    return showToastDialog(getString(resId))
}

fun Fragment.showToastDialog(message: String): ToastDialog {
    return requireActivity().showToastDialog(message)
}

fun FragmentActivity.showToastDialog(message: String): ToastDialog {
    return ToastDialogController.show(message) {
        ToastDialog.show(supportFragmentManager, it)
    }
}

object ToastDialogController {
    private val showingMapper = ConcurrentHashMap<String, ToastDialog>()

    fun dismiss(message: String) = synchronized(showingMapper) {
        showingMapper.remove(message)
    }

    fun show(message: String, invoke: (message: String) -> ToastDialog): ToastDialog =
        synchronized(showingMapper) {
            showingMapper[message] ?: invoke(message).apply {
                showingMapper[message] = this
            }
        }
}

class ToastDialog : BaseDialog<DialogToastBinding>() {

    companion object {
        fun show(fragmentManager: FragmentManager, message: String): ToastDialog {
            return ToastDialog().apply {
                arguments = bundleOf("MESSAGE" to message)
                show(fragmentManager, "ToastDialog")
            }
        }
    }

    override val binding: DialogToastBinding by viewBinding()

    private val message by lazy { arguments?.getString("MESSAGE") ?: "" }

    override fun initView() {
        if (message.isEmpty()) {
            dismiss()
        } else {
            binding.tvMessage.text = message
        }
    }

    override fun initData() {
        viewLifecycleOwner.lifecycleScope.launch {
            delay(1800)
            dismiss()
        }
    }

    override fun dismiss() {
        ToastDialogController.dismiss(message)
        super.dismiss()
    }

    override fun isFullWidth(): Boolean = true
    override fun isFullHeight(): Boolean = true
    override fun outsideCancel(): Boolean = false
    override fun dimAmount(): Float = 0.5F
}
