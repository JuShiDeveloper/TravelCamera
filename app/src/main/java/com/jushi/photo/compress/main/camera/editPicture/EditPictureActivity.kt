package com.jushi.photo.compress.main.camera.editPicture

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import com.jushi.library.base.BaseActivity
import com.jushi.library.utils.FileUtil
import com.jushi.photo.compress.main.camera.editPicture.view.LableSelector
import com.jushi.photo.compress.main.camera.editPicture.view.LableView
import kotlinx.android.synthetic.main.activity_edit_picture_layout.*
import kotlinx.android.synthetic.main.lable_selector_layout.view.*
import travel.camera.photo.compress.R

/**
 * 拍照后编辑图片界面
 */
class EditPictureActivity : BaseActivity() {
    private lateinit var imagePath: String
    private lateinit var lableView: LableView
    private val lableList = arrayListOf<LableView>()
    private lateinit var lableSelector: LableSelector

    override fun setPageLayout() {
        setContentView(R.layout.activity_edit_picture_layout, true, true)
    }

    override fun initData() {
        imagePath = FileUtil.getRealFilePathFromUri(this, intent.data as Uri)
        Log.v("yufei", "${intent.data as Uri}")
    }

    override fun initWidget() {
        setSystemBarViewLayoutParamsR(edit_picture_system_bar)
        initGPUImageView()
        addLableView()
        addLableSelector()
    }

    private fun initGPUImageView() {
        edit_picture_GPUImageView.setImage(BitmapFactory.decodeFile(imagePath))
    }

    /**
     * 添加显示地点/心情的view
     */
    private fun addLableView() {
        lableView = LableView(this)
        var params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        if (lableList.size > 0) {
            params.rightMargin = lableList[lableList.size - 1].width
            params.bottomMargin = lableList[lableList.size - 1].height
        }
        params.addRule(RelativeLayout.CENTER_IN_PARENT)
        lableView.layoutParams = params
        rl_GPUImageView_area.addView(lableView)
        lableList.add(lableView)
    }

    /**
     * 添加标签选择器
     */
    private fun addLableSelector() {
        lableSelector = LableSelector(this)
        var params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
        lableSelector.layoutParams = params
        lableSelector.visibility = View.GONE
        rl_GPUImageView_area.addView(lableSelector)
    }

    override fun setOnViewListener() {
        //返回按钮
        edit_picture_backBtn.setOnClickListener { finish() }
        //保存图片按钮
        tv_edit_picture_saveBtn.setOnClickListener { }
        //贴纸按钮
        tv_edit_picture_tie_zhi_Btn.setOnClickListener { }
        //滤镜按钮
        tv_edit_picture_lv_jing_Btn.setOnClickListener { }
        //标签按钮
        tv_edit_picture_biao_qian_Btn.setOnClickListener { }
        rl_GPUImageView_area.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    lableView.upLableViewLocation(event.x, event.y)
                    lableSelector.isShouldVisibility()
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener true
        }
        //设置标签选择器点击监听
        lableSelector.setLableSelectorOnClickListener(LableSelectorListener())
    }

    override fun onDestroy() {
        super.onDestroy()
        lableView.cancelWave()
    }

    private inner class LableSelectorListener : View.OnClickListener {
        override fun onClick(v: View) {
            when (v.tag as LableSelector.LableType) {
                LableSelector.LableType.MOOD -> { //点击心情

                }
                LableSelector.LableType.LOCATION -> {  //点击地点

                }
            }
        }
    }
}