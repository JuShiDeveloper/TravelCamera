package com.jushi.photo.compress.main.camera.view

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

class ScrollableViewPager : ViewPager {
     var scrollable = true

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return scrollable && super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return scrollable && super.onTouchEvent(ev)
    }
}