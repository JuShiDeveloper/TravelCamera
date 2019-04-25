package com.jushi.photo.compress.main.camera.editPicture.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.lable_selector_layout.view.*
import travel.camera.photo.compress.R

/**
 * 标签选择器 （心情/地点）
 */
class LableSelector : LinearLayout {
    private lateinit var listener: OnClickListener

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        initView()
        setClickListener()
    }

    private fun initView() {
        View.inflate(context, R.layout.lable_selector_layout, this)
    }

    private fun setClickListener() {
        iv_mood_lable_selector.setOnClickListener {
            iv_mood_lable_selector.tag = LableType.MOOD
            listener.onClick(iv_mood_lable_selector)
        }
        iv_location_lable_selector.setOnClickListener {
            iv_location_lable_selector.tag = LableType.LOCATION
            listener.onClick(iv_location_lable_selector)
        }
    }

    fun setLableSelectorOnClickListener(listener: OnClickListener) {
        this.listener = listener
    }

    fun isShouldVisibility() {
        if (this.visibility == View.VISIBLE) {
            this.visibility = View.GONE
        }else{
            this.visibility = View.VISIBLE
        }
    }

    enum class LableType {
        MOOD,
        LOCATION
    }

}