package com.proxy.base.ui.dialog

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.proxy.base.databinding.DialogLoadingBinding
import ex.ss.lib.base.dialog.BaseDialog
import ex.ss.lib.base.extension.viewBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

class LoadDialog : BaseDialog<DialogLoadingBinding>() {
    override val binding: DialogLoadingBinding by viewBinding()
    override fun initData() {}
    override fun initView() {}
    override fun isFullHeight(): Boolean = true
    override fun isFullWidth(): Boolean = true
    override fun dimAmount(): Float = 0.7F
    override fun isCancelable(): Boolean = false
}

object Loading {

    private val loadingMapper = ConcurrentHashMap<Int, LoadDialog>()

    fun show(activity: FragmentActivity): LoadDialog = synchronized(loadingMapper) {
        return LoadDialog().apply {
            show(activity.supportFragmentManager, "Loading")
            loadingMapper[activity.hashCode()] = this
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_DESTROY) {
                    loadingMapper.remove(activity.hashCode())
                    this.dismiss()
                }
            }
            activity.lifecycle.addObserver(observer)
            setOnDismissCallback {
                loadingMapper.remove(activity.hashCode())
                activity.lifecycle.removeObserver(observer)
            }
        }
    }

    fun invoke(activity: FragmentActivity): LoadDialog? {
        return loadingMapper[activity.hashCode()]
    }
}

fun FragmentActivity.showLoading(): LoadDialog = Loading.show(this)
fun FragmentActivity.dismissLoading() = Loading.invoke(this)?.dismiss()
suspend fun FragmentActivity.suspendShowLoading(): LoadDialog =
    withContext(Dispatchers.Main) { showLoading() }

suspend fun FragmentActivity.suspendDismissLoading() =
    withContext(Dispatchers.Main) { dismissLoading() }

fun Fragment.showLoading(): LoadDialog = requireActivity().showLoading()
fun Fragment.dismissLoading() = requireActivity().dismissLoading()
suspend fun Fragment.suspendShowLoading(): LoadDialog = requireActivity().suspendShowLoading()
suspend fun Fragment.suspendDismissLoading() = requireActivity().suspendDismissLoading()