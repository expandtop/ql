package com.proxy.base.ui.dialog

import android.util.Log
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import coil.load
import coil.size.Scale
import coil.transform.RoundedCornersTransformation
import com.proxy.base.databinding.DialogImageAdBinding
import ex.ss.lib.base.dialog.BaseDialog
import ex.ss.lib.base.extension.dp
import ex.ss.lib.base.extension.viewBinding
import ex.ss.lib.tools.common.BrowserTools
import ex.ss.lib.tools.extension.isHttp
import ex.ss.lib.tools.extension.isHttps

class AdImageDialog : BaseDialog<DialogImageAdBinding>() {

    companion object {
        fun show(fragmentManager: FragmentManager, url: String, link: String? = null): AdImageDialog {
            return AdImageDialog().apply {
                arguments = bundleOf("URL" to url, "LINK" to link)
                show(fragmentManager, "AdImageDialog")
            }
        }
    }

    override val binding: DialogImageAdBinding by viewBinding()
    private val url by lazy { arguments?.getString("URL") }
    private val link by lazy { arguments?.getString("LINK") }

    override fun initView() {
        if (!url.isNullOrEmpty() && (url.isHttp() || url.isHttps())) {
            Log.d("MAIN_AD","ImageAd load")
            binding.ivAdImage.load(url) {
                transformations(RoundedCornersTransformation(10F.dp))
                scale(Scale.FIT)
            }
            if (!link.isNullOrEmpty() && (link.isHttps() || link.isHttp())) {
                binding.ivAdImage.setOnClickListener {
                    BrowserTools.openBrowser(requireContext(), link ?: "")
                }
            }
        } else {
            Log.d("MAIN_AD","ImageAd dismiss")
            dismiss()
        }
        binding.ivAdClose.setOnClickListener {
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
