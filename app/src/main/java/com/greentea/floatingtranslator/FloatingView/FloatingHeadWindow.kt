package com.greentea.floatingtranslator.FloatingView

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.provider.Settings
import android.view.*
import android.view.animation.DecelerateInterpolator
import com.greentea.floatingtranslator.MainActivity
import com.greentea.floatingtranslator.R
import java.lang.Exception

class FloatingHeadWindow(val context: Context) : FloatingView.Callbacks {

    private var mWindowManager: WindowManager =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private lateinit var mLayoutParams: WindowManager.LayoutParams
    private lateinit var mView: View
    private var mViewAdded = false
    private lateinit var rect: Rect
    private lateinit var mAnimator: ObjectAnimator

    fun show() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(context)) {
                mWindowManager.addView(mView, mLayoutParams)
            }
        } else {
            mWindowManager.addView(mView, mLayoutParams)
        }
        mViewAdded = true
    }

    fun hide() {
        mWindowManager.removeView(mView)
        mViewAdded = false
    }

    fun create() {
        if (!mViewAdded) {
            mView = LayoutInflater.from(context).inflate(R.layout.item_floating, null, false)
            (mView as FloatingView).setCallbacks(this)
            rect = Rect()
            updateScreenLimit(rect)
            mAnimator = ObjectAnimator.ofPropertyValuesHolder(this)
            mAnimator.interpolator = DecelerateInterpolator(1.0f)
        }
    }

    fun updateScreenLimit(rect: Rect) {
        val dm = context.resources.displayMetrics
        rect.left = -30
        rect.right = dm.widthPixels - getWidth()
        rect.top = 30
        rect.bottom = dm.heightPixels - getHeight()

    }

    fun magHorizontal(x: Int): Int {
        var x = x
        val dm = context.resources.displayMetrics
        if (x + 78 < 0) {
            x = rect.left
        } else if (x + getWidth() - 78 > dm.widthPixels) {
            x = rect.right
        }
        return x
    }

    fun magVertical(y: Int): Int {
        var y = y
        if (y < rect.top) {
            y = rect.top
        } else if (y > rect.bottom) {
            y = rect.bottom
        }
        return y
    }

    fun getX(): Int = mLayoutParams.x
    fun getY(): Int = mLayoutParams.y

    fun settle() {
        val x = magHorizontal(getX())
        val y = magVertical(getY())
        animateTo(x, y)
    }

    fun animateTo(x: Int, y: Int) {
        val xHolder = PropertyValuesHolder.ofInt("x", x)
        val yHolder = PropertyValuesHolder.ofInt("y", y)

        mAnimator.setValues(xHolder, yHolder)
        mAnimator.duration = 200
        mAnimator.start()
        mAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animator: Animator?) {}

            override fun onAnimationEnd(animator: Animator?) {}

            override fun onAnimationCancel(animator: Animator?) {}

            override fun onAnimationStart(animator: Animator?) {}
        })
    }

    fun createLayoutParams() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mLayoutParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                (WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                        or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        or WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR),
                PixelFormat.TRANSLUCENT
            )
        } else {
            mLayoutParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                (WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                        or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        or WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR),
                PixelFormat.TRANSLUCENT
            )
        }
        mLayoutParams.gravity = Gravity.TOP or Gravity.LEFT
        mLayoutParams.x = -mView.width
        mLayoutParams.y = -mView.height
    }

    fun moveTo(pos: Point) {
        moveTo(pos.x, pos.y)
    }

    fun moveTo(x: Int, y: Int) {
        if ((mView != null) and (mViewAdded)) {
            mLayoutParams.x = x
            mLayoutParams.y = y
            mWindowManager.updateViewLayout(mView, mLayoutParams)
        }
    }

    fun moveBy(dx: Int, dy: Int) {
        if (mView != null && mViewAdded) {

            mLayoutParams.x += dx
            mLayoutParams.y += dy
            mWindowManager.updateViewLayout(mView, mLayoutParams)
        }
    }

    fun getWidth(): Int = mView.measuredWidth
    fun getHeight(): Int = mView.measuredHeight

    override fun onDrag(dx: Int, dy: Int) {
        moveBy(dx, dy)
    }

    override fun onDragEnd(dx: Int, dy: Int) {
        settle()
    }

    override fun onDragStart(dx: Int, dy: Int) {

    }

    override fun onClick() {
        startActivity()
    }

    private fun startActivity() {
        try {
            val contentIntent = PendingIntent.getActivity(
                context,
                9999,
                Intent(context, MainActivity::class.java)
                    .addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK
                                or Intent.FLAG_ACTIVITY_SINGLE_TOP
                                or Intent.FLAG_ACTIVITY_NO_USER_ACTION
                                or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                    ),
                PendingIntent.FLAG_ONE_SHOT
            )
            contentIntent.send()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}