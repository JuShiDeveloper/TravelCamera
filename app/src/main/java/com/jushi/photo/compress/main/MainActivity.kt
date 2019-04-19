package com.jushi.photo.compress.main

import android.Manifest
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.jushi.library.base.BaseActivity
import com.jushi.library.takingPhoto.util.PermissionUtil
import com.jushi.photo.compress.main.camera.CameraActivity
import com.jushi.photo.compress.main.meiHua.MeiHuaActivity
import com.jushi.photo.compress.main.photoCompress.CompressActivity
import com.jushi.photo.compress.main.pinTu.PinTuActivity
import kotlinx.android.synthetic.main.activity_main.*
import travel.camera.photo.compress.R

class MainActivity : BaseActivity() {
    private val REQUEST_PERMISSION_CODE = 0X001
    override fun setPageLayout() {
        setContentView(R.layout.activity_main, true, true)
    }

    override fun initData() {
        requestPermission()
    }

    private fun requestPermission(): Boolean {
        return PermissionUtil.request(this, arrayOf(Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_PERMISSION_CODE)
    }

    override fun initWidget() {
        setSystemBarViewLayoutParamsL(systemBar_View)
    }

    override fun setOnViewListener() {
        MeiHuaButton.setOnClickListener {
            //美化按钮点击事件
            startActivity(MeiHuaActivity::class.java)
        }
        PinTuButton.setOnClickListener {
            //拼图按钮
            startActivity(PinTuActivity::class.java)
        }
        CameraButton.setOnClickListener {
            if (requestPermission()) {
                //贴图相机按钮
                startActivity(CameraActivity::class.java)
            }
        }
        PhotoCompressButton.setOnClickListener {
            //图片压缩按钮
            startActivity(CompressActivity::class.java)
        }
    }
}
