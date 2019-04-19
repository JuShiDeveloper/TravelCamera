package com.jushi.photo.compress.main

import com.jushi.library.base.BaseActivity
import com.jushi.photo.compress.main.camera.CameraActivity
import com.jushi.photo.compress.main.meiHua.MeiHuaActivity
import com.jushi.photo.compress.main.photoCompress.CompressActivity
import com.jushi.photo.compress.main.pinTu.PinTuActivity
import kotlinx.android.synthetic.main.activity_main.*
import travel.camera.photo.compress.R

class MainActivity : BaseActivity() {

    override fun setPageLayout() {
        setContentView(R.layout.activity_main, true, true)
    }

    override fun initData() {

    }

    override fun initWidget() {
        setSystemBarViewLayoutParamsL(systemBar_View)
    }

    override fun setOnClickListener() {
        MeiHuaButton.setOnClickListener {
            //美化按钮点击事件
            startActivity(MeiHuaActivity::class.java)
        }
        PinTuButton.setOnClickListener {
            //拼图按钮
            startActivity(PinTuActivity::class.java)
        }
        CameraButton.setOnClickListener {
            //贴图相机按钮
            startActivity(CameraActivity::class.java)
        }
        PhotoCompressButton.setOnClickListener {
            //图片压缩按钮
            startActivity(CompressActivity::class.java)
        }
    }
}
