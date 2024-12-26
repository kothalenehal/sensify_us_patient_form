package com.sensifyawareapp.ui.traceaware

import androidx.appcompat.widget.AppCompatImageView
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import java.util.ArrayList

class DrawView : AppCompatImageView {
    var fingerTouchCount = 0
        private set
    private var color = Color.parseColor("#000000")
    private var width = 8f
    private val holderList: MutableList<Holder> = ArrayList()

    private inner class Holder(color: Int, width: Float) {
        var path: Path = Path()
        var paint: Paint = Paint()

        init {
            paint.isAntiAlias = true
            paint.strokeWidth = width
            paint.color = color
            paint.style = Paint.Style.STROKE
            paint.strokeJoin = Paint.Join.ROUND
            paint.strokeCap = Paint.Cap.ROUND
        }
    }

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!, attrs, defStyle
    ) {
        init()
    }

    private fun init() {
        holderList.add(Holder(color, width))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (holder in holderList) {
            canvas.drawPath(holder.path, holder.paint)
        }
    }

    var isValidTouch = true //touch will be invalid if users draw using 2 fingers

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val eventX = event.x
        val eventY = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isValidTouch = true
                holderList.add(Holder(color, width))
                holderList[holderList.size - 1].path.moveTo(eventX, eventY)
                holderList[holderList.size - 1].path.lineTo(eventX, eventY)
                return true
            }
            MotionEvent.ACTION_POINTER_DOWN,
            MotionEvent.ACTION_POINTER_UP -> {
                isValidTouch = false
            }
            MotionEvent.ACTION_MOVE -> {
                if (isValidTouch)
                    holderList[holderList.size - 1].path.lineTo(eventX, eventY)
            }
            MotionEvent.ACTION_UP -> {
                fingerTouchCount++
            }
            else -> {
                return false
            }
        }
        invalidate()
        return true
    }

    fun resetPaths() {
        for (holder in holderList) {
            holder.path.reset()
        }
        invalidate()
    }
}