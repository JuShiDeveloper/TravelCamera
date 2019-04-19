package com.jushi.photo.compress.main.camera.presenter

import android.app.Activity
import android.content.Context
import android.graphics.PixelFormat
import android.hardware.Camera
import android.os.Build
import android.view.SurfaceHolder
import com.jushi.photo.compress.main.camera.helper.CameraHelper
import com.jushi.photo.compress.main.camera.view.CameraView
import java.lang.reflect.Method
import java.util.*

class CameraPresenter(private val cameraView: CameraView, private val context: Context) {
    private var cameraHelper: CameraHelper = CameraHelper(context)
    private var camera: Camera? = null
    private lateinit var cameraParameters: Camera.Parameters
    private lateinit var adapterSize: Camera.Size
    private lateinit var previewSize: Camera.Size
    private val MAX_ASPECT_DISTORTION = 0.15  //最大宽高比
    private val MIN_PREVIEW_PIXELS = 480 * 320 //最小预览界面的分辨率
    private var currentCameraId = 0
    private lateinit var holder: SurfaceHolder

    /**
     * 改变闪光灯你状态
     */
    fun setFlashStatus() {

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
        adapterSize = findBestPictureResolution()
        previewSize = findBestPreviewResolution()
        if (adapterSize != null) {
            cameraParameters.setPictureSize(adapterSize.width, adapterSize.height)
        }
        if (previewSize != null) {
            cameraParameters.setPreviewSize(previewSize.width, previewSize.height)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
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
     * 找出最适合的预览界面分辨率
     *
     * @return
     */
    private fun findBestPictureResolution(): Camera.Size {
        val supportedPicResolutions = cameraParameters.supportedPictureSizes // 至少会返回一个值
        val picResolutionSb = StringBuilder()
        for (supportedPicResolution in supportedPicResolutions) {
            picResolutionSb.append(supportedPicResolution.width).append('x')
                    .append(supportedPicResolution.height).append(" ")
        }
        val defaultPictureResolution = cameraParameters.pictureSize
        // 排序
        val sortedSupportedPicResolutions = ArrayList(supportedPicResolutions)
        Collections.sort<Camera.Size>(sortedSupportedPicResolutions, Comparator<Camera.Size> { a, b ->
            val aPixels = a.height * a.width
            val bPixels = b.height * b.width
            if (bPixels < aPixels) {
                return@Comparator -1
            }
            if (bPixels > aPixels) {
                1
            } else 0
        })
        // 移除不符合条件的分辨率
        val screenAspectRatio = (context as Activity).windowManager.defaultDisplay.width / context.windowManager.defaultDisplay.height
        val it = sortedSupportedPicResolutions.iterator()
        while (it.hasNext()) {
            val supportedPreviewResolution = it.next()
            val width = supportedPreviewResolution.width
            val height = supportedPreviewResolution.height
            // 在camera分辨率与屏幕分辨率宽高比不相等的情况下，找出差距最小的一组分辨率
            // 由于camera的分辨率是width>height，我们设置的portrait模式中，width<height
            // 因此这里要先交换然后在比较宽高比
            val isCandidatePortrait = width > height
            val maybeFlippedWidth = if (isCandidatePortrait) height else width
            val maybeFlippedHeight = if (isCandidatePortrait) width else height
            val aspectRatio = maybeFlippedWidth.toDouble() / maybeFlippedHeight.toDouble()
            val distortion = Math.abs(aspectRatio - screenAspectRatio)
            if (distortion > MAX_ASPECT_DISTORTION) {
                it.remove()
                continue
            }
        }
        // 如果没有找到合适的，并且还有候选的像素，对于照片，则取其中最大比例的，而不是选择与屏幕分辨率相同的
        if (!sortedSupportedPicResolutions.isEmpty()) {
            return sortedSupportedPicResolutions[0]
        }
        // 没有找到合适的，就返回默认的
        return defaultPictureResolution
    }

    private fun findBestPreviewResolution(): Camera.Size {
        val defaultPreviewResolution = cameraParameters.previewSize
        val rawSupportedSizes = cameraParameters.supportedPreviewSizes
                ?: return defaultPreviewResolution
        // 按照分辨率从大到小排序
        val supportedPreviewResolutions = ArrayList(rawSupportedSizes)
        Collections.sort<Camera.Size>(supportedPreviewResolutions, Comparator<Camera.Size> { a, b ->
            val aPixels = a.height * a.width
            val bPixels = b.height * b.width
            if (bPixels < aPixels) {
                return@Comparator -1
            }
            if (bPixels > aPixels) {
                1
            } else 0
        })
        val previewResolutionSb = StringBuilder()
        for (supportedPreviewResolution in supportedPreviewResolutions) {
            previewResolutionSb.append(supportedPreviewResolution.width).append('x').append(supportedPreviewResolution.height)
                    .append(' ')
        }
        // 移除不符合条件的分辨率
        val screenAspectRatio = (context as Activity).windowManager.defaultDisplay.width / context.windowManager.defaultDisplay.height
        val it = supportedPreviewResolutions.iterator()
        while (it.hasNext()) {
            val supportedPreviewResolution = it.next()
            val width = supportedPreviewResolution.width
            val height = supportedPreviewResolution.height

            // 移除低于下限的分辨率，尽可能取高分辨率
            if (width * height < MIN_PREVIEW_PIXELS) {
                it.remove()
                continue
            }
            // 在camera分辨率与屏幕分辨率宽高比不相等的情况下，找出差距最小的一组分辨率
            // 由于camera的分辨率是width>height，我们设置的portrait模式中，width<height
            // 因此这里要先交换然preview宽高比后在比较
            val isCandidatePortrait = width > height
            val maybeFlippedWidth = if (isCandidatePortrait) height else width
            val maybeFlippedHeight = if (isCandidatePortrait) width else height
            val aspectRatio = maybeFlippedWidth.toDouble() / maybeFlippedHeight.toDouble()
            val distortion = Math.abs(aspectRatio - screenAspectRatio)
            if (distortion > MAX_ASPECT_DISTORTION) {
                it.remove()
                continue
            }
        }
        // 如果没有找到合适的，并且还有候选的像素，则设置其中最大比例的，对于配置比较低的机器不太合适
        if (!supportedPreviewResolutions.isEmpty()) {
            val largestPreview = supportedPreviewResolutions[0]
            return largestPreview
        }
        // 没有找到合适的，就返回默认的
        return defaultPreviewResolution
    }

    /**
     * 切换摄像头
     */
    fun changeCamera() {
        currentCameraId = (currentCameraId + 1) % cameraHelper.getNumberOfCameras()
        releaseCamera()
        camera = cameraHelper.openCamera(currentCameraId)
        camera!!.setPreviewDisplay(holder)
        initCamera()
        camera!!.startPreview()
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
}