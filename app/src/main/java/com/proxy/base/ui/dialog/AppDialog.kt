package com.proxy.base.ui.dialog

import android.graphics.Bitmap
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import coil.load
import com.proxy.base.databinding.DialogAppBinding
import ex.ss.lib.base.dialog.BaseDialog
import ex.ss.lib.base.extension.dp
import ex.ss.lib.base.extension.viewBinding

class AppDialog(private val builder: AppDialogBuilder) : BaseDialog<DialogAppBinding>() {

    companion object {
        fun show(fragmentManager: FragmentManager, block: AppDialogBuilder.() -> Unit) {
            AppDialogBuilder().apply(block).show(fragmentManager)
        }
    }


    override val binding: DialogAppBinding by viewBinding()

    override fun initView() {
        binding.tvTitle.isVisible = builder.title.isNotEmpty()
        binding.tvTitle.text = builder.title
        binding.tvContent.isVisible = builder.content.isNotEmpty()
        binding.tvContent.text = builder.content
        binding.tvCancel.isVisible = builder.cancel.isNotEmpty()
        binding.tvCancel.text = builder.cancel
        binding.tvDone.isVisible = builder.done.isNotEmpty()
        binding.tvDone.text = builder.done
        binding.ivConnect.isVisible = builder.imgContent != null
        builder.imgContent?.also {
            binding.ivConnect.load(it)
        }

        binding.ivClose.isVisible = !builder.alwaysShow
        binding.ivClose.setOnClickListener {
            if (!builder.alwaysShow) dismiss()
            builder.onAction?.invoke()
            builder.onCancel?.invoke()
        }

        binding.tvCancel.setOnClickListener {
            if (!builder.alwaysShow) dismiss()
            builder.onAction?.invoke()
            builder.onCancel?.invoke()
        }
        binding.tvDone.setOnClickListener {
            if (!builder.alwaysShow) dismiss()
            builder.onAction?.invoke()
            builder.onDone?.invoke()
        }
        setOnDismissCallback {
            builder.onClose?.invoke()
        }
    }

    override fun initData() {

    }

    override fun isFullWidth(): Boolean = true
    override fun outsideCancel(): Boolean = builder.cancelOutside
    override fun widthMargin(): Int = 50.dp
    override fun dimAmount(): Float = 0.7F
}

class AppDialogBuilder internal constructor() {

    var onCancel: (() -> Unit)? = null
    var onDone: (() -> Unit)? = null
    var onAction: (() -> Unit)? = null
    var onClose: (() -> Unit)? = null

    var title: CharSequence = ""
    var content: CharSequence = ""
    var cancel: CharSequence = ""
    var done: CharSequence = ""
    var imgContent: Bitmap? = null
    var cancelOutside: Boolean = false
    var alwaysShow: Boolean = false
    fun show(manager: FragmentManager) {
        AppDialog(this).show(manager, "AppDialog")
    }
}