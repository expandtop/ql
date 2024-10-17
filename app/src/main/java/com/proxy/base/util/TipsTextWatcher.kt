package com.proxy.base.util


import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextSwitcher
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.proxy.base.R
import ex.ss.lib.base.extension.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

class TipsTextWatcher(context: Context?, attrs: AttributeSet?) : TextSwitcher(context, attrs) {

    private val mainScope by lazy { MainScope() }
    private val currentIndex = AtomicInteger(0)
    private val currentTurnJob = AtomicReference<Job>(null)

    private val onClickListener = mutableListOf<OnClickListener>()

    init {
        setInAnimation(context, R.anim.tip_switch_show)
        setOutAnimation(context, R.anim.tip_switch_hide)

        super.setOnClickListener {
            for (listener in onClickListener) {
                listener.onClick(it)
            }
        }
    }

    fun setConfig(
        drawStart: Int = 0,
        textSize: Float = 12F,
        textColor: Int = Color.BLACK,
        isSingleLine: Boolean = false,
        gravity: Int = Gravity.CENTER,
    ) {
        setFactory {
            TextView(context).apply {
                setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
                setSingleLine(isSingleLine)
                if (isSingleLine) {
                    ellipsize = TextUtils.TruncateAt.END
                }
                setTextColor(ColorStateList.valueOf(textColor))
                if (drawStart != 0) {
                    val drawable =
                        ContextCompat.getDrawable(this@TipsTextWatcher.context, drawStart)
                            ?.apply {
                                setBounds(0, 0, 15.dp, 15.dp)
                            }
                    setCompoundDrawables(drawable, null, null, null)
                    compoundDrawablePadding = 5.dp
                }
                this.gravity = gravity
                layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            }
        }
    }

    fun setList(list: List<String>, delay: Long = 5000) {
        if (list.isEmpty()) return
        currentIndex.set(0)
        setText(list[currentIndex.get()])
        currentTurnJob.get()?.cancel()
        val job = mainScope.launch {
            while (true) {
                delay(delay)
                if (currentIndex.get() + 1 >= list.size) {
                    currentIndex.set(0)
                } else {
                    currentIndex.addAndGet(1)
                }
                setText(list[currentIndex.get()])
            }
        }
        currentTurnJob.set(job)
    }


    override fun setOnClickListener(l: OnClickListener?) {
        l?.also { onClickListener.add(it) }
    }

    fun setOnItemClickListener(onItemClickListener: (Int) -> Unit) {
        setOnClickListener {
            val currentIndex = displayedChild
            onItemClickListener.invoke(currentIndex)
        }
    }

    fun onDestroy() {
        currentTurnJob.get()?.cancel()
    }

}