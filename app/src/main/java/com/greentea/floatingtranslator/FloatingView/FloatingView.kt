package com.greentea.floatingtranslator.FloatingView

import android.content.ComponentCallbacks
import android.content.Context
import android.gesture.GestureLibraries
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout
import java.util.jar.Attributes

class FloatingView @JvmOverloads constructor(
    val mContext: Context,
    val attributeSet: AttributeSet? = null,
    val def: Int = 0
) : FrameLayout(mContext, attributeSet, def), View.OnTouchListener {

    private val CLICK_DRAG_TOLERANCE = 10f

    private var downRawX: Int = 0
    private var downRawY: Int = 0
    private var lastX: Int = 0
    private var lastY: Int = 0
    private lateinit var callbacks: Callbacks
    private val mGestureDetector: GestureDetector
    private var mTouchSlop: Int

    init {
        setOnTouchListener(this)
        mGestureDetector = GestureDetector(context, GestureListener())
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
    }

    fun setCallbacks(callbacks: Callbacks) {
        this.callbacks = callbacks
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {

        val layoutParams = view.layoutParams

        mGestureDetector.onTouchEvent(motionEvent)

        val action = motionEvent.action
        val x = motionEvent.rawX.toInt()
        val y = motionEvent.rawY.toInt()

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                downRawX = x
                downRawY = y
                lastX = x
                lastY = y

                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val nx = (x - lastX)
                val ny = (x - lastY)
                lastX = x
                lastY = y

                callbacks.onDrag(nx, ny)
                return true
            }
            MotionEvent.ACTION_UP -> {
                callbacks.onDragEnd(x,y)
                return true
            }
            else -> return super.onTouchEvent(motionEvent)
        }
    }

    inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            callbacks.onClick()
            return true
        }
    }

    interface Callbacks {
        fun onDrag(dx: Int, dy: Int)
        fun onDragEnd(dx: Int, dy: Int)
        fun onDragStart(dx: Int, dy: Int)
        fun onClick()
    }
}