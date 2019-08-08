package com.anwesh.uiprojects.dotlinerotnextview

/**
 * Created by anweshmishra on 08/08/19.
 */

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color

val nodes : Int = 5
val sizeFactor : Float = 2f
val strokeFactor : Int = 90
val foreColor : Int = Color.parseColor("#311B92")
val backColor : Int = Color.parseColor("#BDBDBD")
val scGap : Float = 0.05f
val rotDeg : Float = 180f
val rFactor : Float = 3.1f

fun Canvas.drawDotLine(size : Float, scale : Float, ballDraw : Boolean, paint : Paint) {
    save()
    rotate(rotDeg * scale)
    drawLine(0f, 0f, -size, 0f, paint)
    if (ballDraw) {
        drawCircle(0f, -size, size / rFactor, paint)
    }
    restore()
}

fun Canvas.drawDLRNode(i : Int, scale : Float, ballDraw : Boolean, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = w / (nodes)
    val size : Float = gap / sizeFactor
    paint.color = foreColor
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    paint.color = foreColor
    paint.strokeCap = Paint.Cap.ROUND
    save()
    translate(w / 2, gap * (i + 1))
    drawDotLine(size, scale, ballDraw, paint)
    restore()
}

class DotLineRotNextView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += dir * scGap
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }
}

