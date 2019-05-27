package com.jushi.photo.compress.main.camera

import android.Manifest
import android.app.Activity
import android.content.ComponentCallbacks2
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Camera
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.*
import android.view.animation.ScaleAnimation
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.jushi.library.base.BaseActivity
import com.jushi.library.takingPhoto.PictureHelper
import com.jushi.photo.compress.main.camera.album.AlbumActivity
import com.jushi.photo.compress.main.camera.arcFace.FaceRectView
import com.jushi.photo.compress.main.camera.editPicture.EditPictureActivity
import com.jushi.photo.compress.main.camera.presenter.CameraPresenter
import com.jushi.photo.compress.main.camera.utils.FlashMode
import com.jushi.photo.compress.main.camera.view.CameraView
import kotlinx.android.synthetic.main.activity_camera_layout.*
import travel.camera.photo.compress.R

/**
 * 贴图相机界面（拍照界面）
 */
class CameraActivity : BaseActivity(), CameraView, SurfaceHolder.Callback {
    private lateinit var cameraPresenter: CameraPresenter
    private var pointX = 0.0f
    private var pointY = 0.0f
    private val FOCUS = 1 //聚焦
    private val ZOOM = 2 //缩放
    private var mode: Int = -1
    private var dist: Float = 0f
    private val handler: Handler = Handler()
    /**
     * 人脸识别所需的权限
     */
    private val NEEDED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE)
    private val ACTION_REQUEST_PERMISSIONS = 0x001


    override fun setPageLayout() {
        setContentView(R.layout.activity_camera_layout, false, true)
    }

    override fun initWidget() {
        setSystemBarViewLayoutParamsR(camera_systemBar)
        initSurfaceView()
        setOnTouchListener()
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

    /**
     * 初始化人脸sdk
     */
    override fun initFaceEngine() {
        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS)
        } else {
            cameraPresenter.initFaceEngine()
        }
    }

    override fun setOnViewListener() {
        //闪光灯按钮
        camera_flash_button.setOnClickListener {
            cameraPresenter.setFlashStatus()
        }
        //摄像头切换按钮
        camera_flip_button.setOnClickListener {
            cameraPresenter.changeCamera()
        }
        camera_SurfaceView.setOnClickListener {
            surfaceViewClick()
        }
        //拍照按钮
        Taking_Picture_Button.setOnClickListener {
            cameraPresenter.takingPicture()
        }
        //拍照按钮右边的相册按钮
        iv_curTakingImage.setOnClickListener {
            startActivity(AlbumActivity::class.java)
        }
    }

    private fun surfaceViewClick() {
        try {
            cameraPresenter.pointFoucs(pointX.toInt(), pointY.toInt())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val params = RelativeLayout.LayoutParams(focus_index.layoutParams)
        params.setMargins(pointX.toInt() - 60, pointY.toInt() - 60, 0, 0)
        focus_index.layoutParams = params
        focus_index.visibility = View.VISIBLE
        val sa = ScaleAnimation(3f, 1f, 3f, 1f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f)
        sa.duration = 800
        focus_index.startAnimation(sa)
        handler.postDelayed({ focus_index.visibility = View.INVISIBLE }, 800)
    }

    private fun setOnTouchListener() {
        camera_SurfaceView.setOnTouchListener { v, event ->
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> {  //主点按下
                    pointX = event.x
                    pointY = event.y
                    mode = FOCUS
                }
                MotionEvent.ACTION_POINTER_DOWN -> {  //副点按下
                    dist = spacing(event)
                    if (spacing(event) > 10f) { //如果连续两点距离大于10，则判定为多点模式
                        mode = ZOOM
                    }
                }
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_POINTER_UP -> {
                    mode = FOCUS
                }
                MotionEvent.ACTION_MOVE -> {
                    if (mode == ZOOM) {
                        val newDist = spacing(event)
                        if (newDist > 10f) {
                            var scale = (newDist - dist) / dist
                            if (scale < 0) {
                                scale *= 10
                            }
                            cameraPresenter.addZoomIn(scale.toInt())
                        }
                    }
                }
            }
            return@setOnTouchListener false
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        cameraPresenter.initCamera(holder!!)
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        cameraPresenter.setFocus()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        cameraPresenter.releaseCamera()
    }

    /**
     * 闪光灯状态
     * @param status 根据状态值改变图标
     */
    override fun flashStatus(mode: FlashMode) {
        when (mode) {
            FlashMode.OFF -> { //关闭闪光灯
                camera_flash_button.setImageResource(R.mipmap.camera_flash_off)
            }
            FlashMode.ON -> { //开启闪光灯
                camera_flash_button.setImageResource(R.mipmap.camera_flash_on)
            }
            FlashMode.AUTO -> { //自动
                camera_flash_button.setImageResource(R.mipmap.camera_flash_auto)
            }
        }
    }

    /**
     * 显示Toast
     */
    override fun showTus(msg: String) {
        showToast(msg)
    }

    /**
     * 计算两点间的距离
     */
    private fun spacing(event: MotionEvent): Float {
        if (event == null) return 0f
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return Math.sqrt((x * x + y * y).toDouble()).toFloat()
    }


    /**
     * 拍照保存成功
     */
    override fun pictureSaveSuccess(imagePath: String) {
        Glide.with(this).load(imagePath)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(iv_curTakingImage)
        var uri = if (imagePath.startsWith("file:")) Uri.parse(imagePath) else Uri.parse("file://$imagePath")
        val intent = Intent(this, EditPictureActivity::class.java)
        intent.data = uri
//        startActivity(intent)
    }

    /**
     * 获取显示相机预览画面的View
     */
    override fun getPreviewView(): View = camera_SurfaceView

    /**
     * 获取显示人脸框的View
     */
    override fun getFaceRectView(): FaceRectView = FaceRectView


    private fun checkPermissions(neededPermissions: Array<String>?): Boolean {
        if (neededPermissions == null || neededPermissions.isEmpty()) {
            return true
        }
        var allGranted = true
        for (neededPermission in neededPermissions) {
            allGranted = allGranted and (ContextCompat.checkSelfPermission(this.applicationContext, neededPermission) == PackageManager.PERMISSION_GRANTED)
        }
        return allGranted
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            ACTION_REQUEST_PERMISSIONS -> {
                var isAllGranted = true
                for (grantResult in grantResults) {
                    isAllGranted = isAllGranted and (grantResult == PackageManager.PERMISSION_GRANTED)
                }
                if (isAllGranted) {
                    cameraPresenter.initFaceEngine()
                } else {
                    showTus("权限被拒绝")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraPresenter.stopPreview()
    }
}