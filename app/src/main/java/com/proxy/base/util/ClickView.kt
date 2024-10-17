package com.proxy.base.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.proxy.base.BuildConfig
import ex.ss.lib.base.extension.dp
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

class ClickView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    data class ViewSize(
        var w: Float = 0F,
        var h: Float = 0F,
    ) {
        fun center(): PointF {
            return PointF(w / 2F, h / 2F)
        }

        fun r(): Float {
            return min(w, h) / 2F
        }
    }


    private val padding by lazy { 25.dp }
    private val paint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.BLACK
            alpha = 1
        }
    }

    private var size: ViewSize = ViewSize()

    private var onclick: (() -> Unit)? = null

    fun setOnClick(click: () -> Unit) {
        this.onclick = click
    }

    private val lastDownTime = AtomicLong(0)
    private val lastDownPoint = PointF(0F, 0F)

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                lastDownTime.set(System.currentTimeMillis())
                lastDownPoint.set(event.x, event.y)
                println("ClickView:down:${event.x} ${event.y}")
            }

            MotionEvent.ACTION_UP -> {
                if (System.currentTimeMillis() - lastDownTime.get() < 500) {
                    val center = size.center()
                    val r = size.r() - padding
                    val upPoint = PointF(event.x, event.y)
                    val actionDis = getDistanceBetween2Points(lastDownPoint, upPoint)
                    val downDis = getDistanceBetween2Points(lastDownPoint, center)
                    val upDis = getDistanceBetween2Points(upPoint, center)
                    if (actionDis < 5.dp && downDis <= r && upDis <= r) {
                        println("ClickView:被点击了")
                        onclick?.invoke()
                    }
                }
            }

            else -> {}
        }
        return true
    }

    private fun getDistanceBetween2Points(p0: PointF, p1: PointF): Float {
        val distance = sqrt((p0.y - p1.y).pow(2.0F) + (p0.x - p1.x).pow(2.0F))
        return distance
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        size.w = measuredWidth * 1F
        size.h = measuredHeight * 1F
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (BuildConfig.DEBUG) {
            val center = size.center()
            canvas.drawCircle(
                center.x,
                center.y,
                size.r() - padding,
                paint
            )
        }
    }

}