package com.jushi.library.customView.rotateScaleView

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * @author jushi
 * create time：2019/4/26
 * 功能：
 * 1、点击右下角缩放/旋转
 * 2、点击左上角删除
 * 3、点击其他位置拖动时，移动
 */
class RotateImageView : AppCompatImageView, View.OnTouchListener {
    private var x = 0
    private var y = 0
    private var pointType: PointType = PointType.OTHER

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        setOnTouchListener(this)

    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action and event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                x = event.x.toInt()
                y = event.y.toInt()
                getPointType(v)
            }
            MotionEvent.ACTION_MOVE -> {

            }
        }
        return true
    }

    /**
     * 点击的位置
     */
    private fun getPointType(v: View) {
        if ((x - v.top) < 60 && (y - v.left) < 60) { //点击左上角
            pointType = PointType.LEFT_TOP
        }
        if ((v.right - x) < 60 && (v.bottom - y) < 60) {  //点击右下角
            pointType = PointType.RIGHT_BOTTOM
        }
    }

    enum class PointType {
        LEFT_TOP, //点击左上角
        RIGHT_BOTTOM, //点击右下角
        OTHER      //其他位置
    }
}