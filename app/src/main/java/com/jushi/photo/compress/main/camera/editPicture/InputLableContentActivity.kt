package com.jushi.photo.compress.main.camera.editPicture

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import com.jushi.library.base.BaseActivity
import com.jushi.photo.compress.main.utils.Constansts
import kotlinx.android.synthetic.main.activity_input_lable_content_layout.*
import travel.camera.photo.compress.R

/**
 * 输入标签内容的界面
 */
class InputLableContentActivity : BaseActivity(), TextWatcher {
    override fun setPageLayout() {
        setContentView(R.layout.activity_input_lable_content_layout, true, true)
    }

    override fun initWidget() {
        setSystemBarViewLayoutParamsL(input_lable_content_system_bar)
    }

    override fun initData() {
    }

    override fun setOnViewListener() {
        tv_input_lable_content_calcelBtn.setOnClickListener {
            //取消按钮
            setContentResult(Activity.RESULT_CANCELED, null)
        }
        tv_input_lable_content_saveBtn.setOnClickListener {
            //保存按钮
            val content = et_input_lable_content.text.toString()
            if (content.isEmpty()) {
                showToastAtLocation(getString(R.string.please_input_content_after), Gravity.CENTER)
                return@setOnClickListener
            }
            val intent = Intent()
            intent.putExtra(Constansts.INPUT_LABEl_CONTENT, content)
            setContentResult(Activity.RESULT_OK, intent)
        }
        et_input_lable_content.addTextChangedListener(this)
    }

    private fun setContentResult(resultCode: Int, data: Intent?) {
        setResult(resultCode, data)
        finish()
    }

    @SuppressLint("SetTextI18n")
    override fun afterTextChanged(s: Editable?) {
        var count = s.toString().length
        var num = 10 - count
        tv_input_content_hint.text = "你还可以输入$num 个字（$num/10）"
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        if (start >= 9) {
            showToastAtLocation(getString(R.string.input_limit_hint), Gravity.CENTER)
            tv_input_hint.visibility = View.VISIBLE
            return
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }
}