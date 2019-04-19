package com.jushi.photo.compress.main.camera

import android.content.ComponentCallbacks2
import android.view.SurfaceHolder
import com.jushi.library.base.BaseActivity
import com.jushi.photo.compress.main.camera.presenter.CameraPresenter
import com.jushi.photo.compress.main.camera.view.CameraView
import kotlinx.android.synthetic.main.activity_camera_layout.*
import travel.camera.photo.compress.R

/**
 * 贴图相机界面
 */
class CameraActivity : BaseActivity(), CameraView, SurfaceHolder.Callback {
    private lateinit var cameraPresenter: CameraPresenter

    override fun setPageLayout() {
        setContentView(R.layout.activity_camera_layout, true, true)
    }

    override fun initWidget() {
        setSystemBarViewLayoutParamsL(camera_systemBar)
        initSurfaceView()
    }

    private fun initSurfaceView() {
        val surfaceHolder = camera_SurfaceView.holder
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        surfaceHolder.setKeepScreenOn(true)
        camera_SurfaceView.isFocusable = true
        camera_SurfaceView.setBackgroundColor(ComponentCallbacks2.TRIM_MEMORY_BACKGROUND)
        surfaceHolder.addCallback(this)
    }

    override fun initData() {
        cameraPresenter = CameraPresenter(this, this)
    }

    override fun setOnClickListener() {
        //闪光灯按钮
        camera_flash_button.setOnClickListener {
            cameraPresenter.setFlashStatus()
        }
        //摄像头切换按钮
        camera_flip_button.setOnClickListener {
            cameraPresenter.changeCamera()
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        cameraPresenter.initCamera(holder!!)
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        cameraPresenter.releaseCamera()
    }

    /**
     * 闪光灯状态
     * @param status 根据状态值改变图标
     */
    override fun flashStatus(status: String) {

    }


}