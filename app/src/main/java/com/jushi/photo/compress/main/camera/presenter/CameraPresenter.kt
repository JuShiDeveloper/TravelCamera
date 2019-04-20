package com.jushi.photo.compress.main.camera.presenter

import android.app.Activity
import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Rect
import android.hardware.Camera
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import com.jushi.library.systemBarUtils.SystemBarUtil
import com.jushi.photo.compress.main.camera.helper.CameraHelper
import com.jushi.photo.compress.main.camera.utils.FlashMode
import com.jushi.photo.compress.main.camera.utils.SavePictureUtil
import com.jushi.photo.compress.main.camera.view.CameraView
import travel.camera.photo.compress.R
import java.lang.reflect.Method
import java.util.*

/**
 * 拍照逻辑处理类
 * @param width SurfaceView的宽
 * @param height SurfaceView的高
 */
class CameraPresenter(private val cameraView: CameraView, private val context: Context) : Camera.PictureCallback, SavePictureUtil.PictureSaveListener {
    private val screenWidth = (context as Activity).windowManager.defaultDisplay.width
    private val screenHeight = (context as Activity).windowManager.defaultDisplay.height
    private var cameraHelper: CameraHelper = CameraHelper(context)
    private var camera: Camera? = null
    private lateinit var cameraParameters: Camera.Parameters
    private lateinit var savePictureSize: Camera.Size  //保存大小的尺寸
    private lateinit var previewSize: Camera.Size  //预览的尺寸
    private var targetWidth = 4224 //图片保存的目标宽
    private var targetHeight = 5632 //图片保存的目标高
    private var currentCameraId = 0
    private lateinit var holder: SurfaceHolder
    private var curZoomValue = 0 //放大缩小
    private lateinit var bundle: Bundle
    private lateinit var data: ByteArray

    /**
     * 改变闪光灯你状态
     */
    fun setFlashStatus() {
        if (camera == null || camera!!.parameters == null || camera!!.parameters.supportedFlashModes == null) {
            return
        }
        val parameters = camera!!.parameters
        val flashMode = parameters.flashMode
        val supportedFlashModes = parameters.supportedFlashModes
        if (Camera.Parameters.FLASH_MODE_OFF == flashMode
                && supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_ON)) { //当前为关闭状态
            parameters.flashMode = Camera.Parameters.FLASH_MODE_ON
            camera!!.parameters = parameters
            cameraView.flashStatus(FlashMode.ON)
        } else if (flashMode == Camera.Parameters.FLASH_MODE_ON) {  //当前为开启状态
            if (supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
                parameters.flashMode = Camera.Parameters.FLASH_MODE_AUTO
                camera!!.parameters = parameters
                cameraView.flashStatus(FlashMode.AUTO)
            } else if (supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
                parameters.flashMode = Camera.Parameters.FLASH_MODE_OFF
                camera!!.parameters = parameters
                cameraView.flashStatus(FlashMode.OFF)
            }
        } else if (flashMode == Camera.Parameters.FLASH_MODE_AUTO &&
                supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_OFF)) { //当前为自动状态
            parameters.flashMode = Camera.Parameters.FLASH_MODE_OFF
            camera!!.parameters = parameters
            cameraView.flashStatus(FlashMode.OFF)
        }
    }

    fun initCamera(holder: SurfaceHolder) {
        this.holder = holder
        if (camera == null) {
            camera = Camera.open()
            camera!!.setPreviewDisplay(holder)
            initCamera()
            camera!!.startPreview()
        }
    }

    private fun initCamera() {
        cameraParameters = camera!!.parameters
        cameraParameters.pictureFormat = PixelFormat.JPEG
        savePictureSize = getBastSize(targetWidth, targetHeight, cameraParameters.supportedPictureSizes)!!
        previewSize = getBastSize(screenWidth, getPreviewTargetHeight(), cameraParameters.supportedPreviewSizes)!!
        if (savePictureSize != null) {
            cameraParameters.setPictureSize(savePictureSize.width, savePictureSize.height)
        }
        if (previewSize != null) {
            cameraParameters.setPreviewSize(previewSize.width, previewSize.height)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) { //对焦模式
            cameraParameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE//1连续对焦
        } else {
            cameraParameters.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
        }
        setDispaly()
        try {
            camera!!.parameters = cameraParameters
        } catch (e: Exception) {
            e.printStackTrace()
        }
        camera!!.startPreview()
        camera!!.cancelAutoFocus()// 2如果要实现连续的自动对焦，这一句必须加上
    }

    /**
     * 获得预览的目标高度
     * 屏幕的高度 - 状态栏的高 - 底部拍照按钮位置的高
     */
    private fun getPreviewTargetHeight(): Int {
        return screenHeight - SystemBarUtil.getStatusBarHeight(context) - 150
    }

    /**
     * 设置图片的正确显示方向
     */
    private fun setDispaly() {
        if (Build.VERSION.SDK_INT >= 8) {
            val downPolymorphic: Method?
            try {
                downPolymorphic = camera!!.javaClass.getMethod("setDisplayOrientation", Int::class.java)
                downPolymorphic?.invoke(camera, 90)
            } catch (e: Exception) {
            }
        } else {
            cameraParameters.setRotation(90)
        }
    }

    /**
     * 获取最适合的尺寸
     */
    private fun getBastSize(targetWidth: Int, targetHeight: Int, sizeList: List<Camera.Size>): Camera.Size? {
        var bastSize: Camera.Size? = null
        //目标大小的宽高比
        var targetRatio = (targetHeight.toDouble() / targetWidth)
        var mindiff = targetRatio
        for (size in sizeList) {
            //系统支持的尺寸
            var supportedRatio = (size.width.toDouble() / size.height)
            if (size.width == targetHeight && size.height == targetWidth) {
                bastSize = size
                break
            }
            if (Math.abs(supportedRatio - targetRatio) < mindiff) {
                mindiff = Math.abs(supportedRatio - targetRatio)
                bastSize = size
            }
        }
        return bastSize
    }

    /**
     * 切换摄像头
     */
    fun changeCamera() {
        currentCameraId = (currentCameraId + 1) % cameraHelper.getNumberOfCameras()
        releaseCamera()
        camera = cameraHelper.openCamera(currentCameraId)
        if (camera != null) {
            camera!!.setPreviewDisplay(holder)
            initCamera()
            camera!!.startPreview()
        } else { //切换摄像头失败
            cameraView.showTus(context.getString(R.string.change_camera_hint))
        }
    }

    /**
     * 释放相机
     */
    fun releaseCamera() {
        try {
            if (camera != null) {
                camera?.stopPreview()
                camera?.release()
                camera = null
            }
        } catch (e: Exception) {

        }
    }

    /**
     * 对焦 （自动对焦/点击手动对焦都调用次方法）
     */
    fun setFocus() {
        object : Thread() {
            override fun run() {
                try {
                    Thread.sleep(100)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                if (camera == null) {
                    return
                }
                camera!!.autoFocus(Camera.AutoFocusCallback { success, camera ->
                    if (success) {
                        initCamera()//实现相机的参数初始化
                    }
                })
            }
        }
    }

    /**
     * 缩放
     */
    fun addZoomIn(scale: Int) {
        try {
            val parameters = camera!!.parameters
            if (!parameters.isZoomSupported) return
            curZoomValue += scale
            if (curZoomValue < 0) {
                curZoomValue = 0
            } else if (curZoomValue > parameters.maxZoom) {
                curZoomValue = parameters.maxZoom
            }
            if (!parameters.isSmoothZoomSupported) {
                parameters.zoom = curZoomValue
                camera!!.parameters = parameters
                return
            } else {
                camera!!.startSmoothZoom(curZoomValue)
            }
        } catch (e: Exception) {

        }
    }

    /**
     * 焦点位置
     */
    fun pointFoucs(x: Int, y: Int) {
        camera!!.cancelAutoFocus()
        cameraParameters = camera!!.parameters
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (cameraParameters.maxNumMeteringAreas > 0) {
                val areas = ArrayList<Camera.Area>()
                //xy变换了
                val rectY = -x * 2000 / screenWidth + 1000
                val rectX = y * 2000 / screenHeight - 1000

                val left = if (rectX < -900) -1000 else rectX - 100
                val top = if (rectY < -900) -1000 else rectY - 100
                val right = if (rectX > 900) 1000 else rectX + 100
                val bottom = if (rectY > 900) 1000 else rectY + 100
                val area1 = Rect(left, top, right, bottom)
                areas.add(Camera.Area(area1, 800))
                cameraParameters.meteringAreas = areas
            }
            cameraParameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
        }
        camera!!.parameters = cameraParameters
        setFocus()
    }

    /**
     * 获取拍摄的照片（点击拍照按钮时调用）
     */
    fun takingPicture() {
        camera!!.takePicture(null, null, this)
    }

    /**
     * 重新开始预览
     */
    private fun startPreview() {
        if (camera == null) return
        camera!!.startPreview()
    }

    override fun onPictureTaken(data: ByteArray?, camera: Camera?) {
        this.camera = camera
        this.data = data!!
        bundle = Bundle()
        bundle.putByteArray("pictureArray", data)
        SavePictureUtil(data, currentCameraId, context, this).execute()
        startPreview()
    }

    /**
     * 拍摄的图片保存成功
     */
    override fun pictureSaveSuccess(imagePath: String) {
        cameraView.pictureSaveSuccess(imagePath)
    }
}