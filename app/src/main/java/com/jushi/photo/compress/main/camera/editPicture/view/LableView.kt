package com.jushi.photo.compress.main.camera.editPicture.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.lable_view_layout.view.*
import travel.camera.photo.compress.R

/**
 * 显示输入的地点/心情的 View
 */
class LableView : LinearLayout, View.OnTouchListener, Animation.AnimationListener {

    private var lastX = 0
    private var lastY = 0
    private var parentWidth = 0
    private var parentHeight = 0
    private var offset = 0 //可拖动的偏移量

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        View.inflate(context, R.layout.lable_view_layout, this)
        setOnTouchListener(this)
//        tv_lable_view_text
//        dot_lable_view
        wave()
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.rawX.toInt()
                lastY = event.rawY.toInt()
                val parent = parent as ViewGroup
                parentWidth = parent.width
                parentHeight = parent.height
            }
            MotionEvent.ACTION_MOVE -> {
                var dx = event.rawX.toInt() - lastX
                var dy = event.rawY.toInt() - lastY
                upLableViewLayout(dx, dy)
                lastX = event.rawX.toInt()
                lastY = event.rawY.toInt()
                return true
            }
            MotionEvent.ACTION_UP -> {
                return true
            }
        }
        return true
    }

    /**
     * 更新显示的位置
     */
    private fun upLableViewLayout(x: Int, y: Int) {
        var l = this.left + x
        var r = this.right + x
        var t = this.top + y
        var b = this.bottom + y
        if (l < -offset) {
            l = -offset
            r = l + this.width
        }
        if (r > parentWidth + offset) {
            r = parentWidth
            l = r - this.width
        }
        if (t < -offset) {
            t = -offset
            b = t - this.height
        }
        if (b > parentHeight + offset) {
            b = parentHeight
            t = b - this.height
        }
        layout(l, t, r, b)
    }

    /**
     * 更新显示的坐标
     */
    fun upLableViewLocation(x: Float, y: Float) {
        this.x = x
        this.y = y
        postInvalidate()
        wave()
    }

    /**
     * 水波纹动画
     */
    private fun wave() {
        var ats = AnimationSet(true)
        var scaleAnim = ScaleAnimation(1f, 1.5f, 1f, 1.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f)
        scaleAnim.duration = 3 * 600
        scaleAnim.repeatCount = 15
        var alphaAnima = AlphaAnimation(1f, 0.1f)
        alphaAnima.repeatCount = 15
        alphaAnima.duration = 3 * 600
        ats.addAnimation(scaleAnim)
        ats.addAnimation(alphaAnima)
        dot_lable_view.startAnimation(ats)
        alphaAnima.setAnimationListener(this)
    }

    /**
     * 取消动画
     */
    fun cancelWave(){
        dot_lable_view.clearAnimation()
    }

    override fun onAnimationStart(animation: Animation?) {
        isShowDot(View.VISIBLE)
    }

    override fun onAnimationRepeat(animation: Animation?) {

    }

    override fun onAnimationEnd(animation: Animation?) {
        isShowDot(View.INVISIBLE)
    }

    /**
     * 是否显示白点
     */
    private fun isShowDot(visibility: Int) {
        dot_lable_view.visibility = visibility
    }
}