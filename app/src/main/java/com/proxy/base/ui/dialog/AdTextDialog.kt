package com.proxy.base.ui.dialog

import android.util.Log
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import com.proxy.base.databinding.DialogTextAdBinding
import ex.ss.lib.base.dialog.BaseDialog
import ex.ss.lib.base.extension.dp
import ex.ss.lib.base.extension.viewBinding
import ex.ss.lib.tools.common.BrowserTools
import ex.ss.lib.tools.extension.isHttp
import ex.ss.lib.tools.extension.isHttps

class AdTextDialog : BaseDialog<DialogTextAdBinding>() {

    companion object {
        fun show(fragmentManager: FragmentManager, content: String, link: String? = null): AdTextDialog {
            return AdTextDialog().apply {
                arguments = bundleOf("CONTENT" to content, "LINK" to link)
                show(fragmentManager, "AdTextDialog")
            }
        }
    }

    override val binding: DialogTextAdBinding by viewBinding()
    private val content by lazy { arguments?.getString("CONTENT") }
    private val link by lazy { arguments?.getString("LINK") }

    override fun initView() {
        if (!content.isNullOrEmpty()) {
            Log.d("MAIN_AD","TextAd load")
            binding.tvContent.text = content
            if (!link.isNullOrEmpty() && (link.isHttps() || link.isHttp())) {
                binding.tvShow.isVisible = true
                binding.tvShow.setOnClickListener {
                    BrowserTools.openBrowser(requireContext(), link ?: "")
                }
            }
        } else {
            Log.d("MAIN_AD","TextAd dismiss")
            dismiss()
        }
        binding.tvDone.setOnClickListener {
            dismiss()
        }
    }

    override fun initData() {
    }

    override fun isFullWidth(): Boolean = true
    override fun isFullHeight(): Boolean = true
    override fun outsideCancel(): Boolean = false
    override fun widthMargin(): Int = 54.dp
    override fun dimAmount(): Float = 0.5F
}
