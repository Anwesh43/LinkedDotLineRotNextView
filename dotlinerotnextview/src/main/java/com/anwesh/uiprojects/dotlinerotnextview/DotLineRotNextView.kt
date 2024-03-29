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
        drawCircle(-size, 0f, size / rFactor, paint)
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
    translate(gap * (i + 1), h / 2)
    drawDotLine(size, scale, ballDraw, paint)
    restore()
}

class DotLineRotNextView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
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

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()

            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class DLRNode(var i : Int, val state : State = State()) {

        private var next : DLRNode? = null
        private var prev : DLRNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = DLRNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint, currI : Int) {
            canvas.drawDLRNode(i, state.scale, currI == i, paint)
            next?.draw(canvas, paint, currI)
        }

        fun update(cb : (Int, Float) -> Unit) {
            state.update {
                cb(i, it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : DLRNode {
            var curr : DLRNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class DotLineRotNext(var i : Int) {

        private val root : DLRNode = DLRNode(0)
        private var curr : DLRNode = root
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            root.draw(canvas, paint, curr.i)
        }

        fun update(cb : (Int, Float) -> Unit) {
            curr.update {i, scl ->
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(i, scl)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : DotLineRotNextView) {

        private val animator : Animator = Animator(view)
        private val dlr : DotLineRotNext = DotLineRotNext(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(backColor)
            dlr.draw(canvas, paint)
            animator.animate {
                dlr.update {i, scl ->
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            dlr.startUpdating {
                animator.start()
            }
        }
    }

    companion object {
        fun create(activity: Activity) : DotLineRotNextView {
            val view : DotLineRotNextView = DotLineRotNextView(activity)
            activity.setContentView(view)
            return view
        }
    }
}

