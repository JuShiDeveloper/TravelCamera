package com.jushi.photo.compress.main.camera.editPicture

import com.jushi.library.base.BaseActivity
import kotlinx.android.synthetic.main.activity_input_lable_content_layout.*
import travel.camera.photo.compress.R

/**
 * 输入标签内容的界面
 */
class InputLableContentActivity : BaseActivity() {
    override fun setPageLayout() {
        setContentView(R.layout.activity_input_lable_content_layout, true, true)
    }

    override fun initWidget() {
        setSystemBarViewLayoutParamsL(input_lable_content_system_bar)
    }

    override fun initData() {
    }

    override fun setOnViewListener() {
    }
}