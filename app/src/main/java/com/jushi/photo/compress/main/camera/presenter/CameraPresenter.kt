package com.jushi.photo.compress.main.camera.presenter

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.ImageFormat
import android.graphics.PixelFormat
import android.graphics.Rect
import android.hardware.Camera
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import com.arcsoft.face.*
import com.jushi.library.systemBarUtils.SystemBarUtil
import com.jushi.photo.compress.main.camera.arcFace.DrawHelper
import com.jushi.photo.compress.main.camera.arcFace.DrawInfo
import com.jushi.photo.compress.main.camera.arcFace.FaceRectView
import com.jushi.photo.compress.main.camera.helper.CameraHelper
import com.jushi.photo.compress.main.camera.utils.FlashMode
import com.jushi.photo.compress.main.camera.utils.ImageUtil
import com.jushi.photo.compress.main.camera.utils.SavePictureUtil
import com.jushi.photo.compress.main.camera.view.CameraView
import travel.camera.photo.compress.R
import java.io.File
import java.lang.reflect.Method
import java.util.*

/**
 * 拍照逻辑处理类
 *
 */
class CameraPresenter(private val cameraView: CameraView, private val context: Context) : Camera.PictureCallback, SavePictureUtil.PictureSaveListener, Camera.PreviewCallback {

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
     * ------------人脸识别相关-----------------
     */
    private lateinit var drawHelper: DrawHelper
    private lateinit var previewView: View
    private lateinit var faceRectView: FaceRectView
    private var faceEngine: FaceEngine? = null
    private var afCode = -1
    private val processMask = FaceEngine.ASF_FACE_DETECT or FaceEngine.ASF_AGE or
            FaceEngine.ASF_FACE3DANGLE or FaceEngine.ASF_GENDER or FaceEngine.ASF_LIVENESS


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
            cameraView.initFaceEngine()
        }
    }

    private fun initCamera() {
        camera!!.setPreviewCallback(this)
        cameraParameters = camera!!.parameters
//        cameraParameters.pictureFormat = PixelFormat.JPEG
        cameraParameters.previewFormat = ImageFormat.NV21
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
        previewView = cameraView.getPreviewView()
        faceRectView = cameraView.getFaceRectView()
        drawHelper = DrawHelper(previewSize.width, previewSize.height, previewView.width, previewView.height,
                90, currentCameraId, true)
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
                camera!!.setPreviewCallback(null)
                camera!!.stopPreview()
                camera!!.lock()
                camera!!.release()
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
     * 点击对焦
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
    fun startPreview() {
        if (camera != null) {
            camera!!.startPreview()
        }else{
            initCamera(holder)
        }
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
        ImageUtil.notifySystemScanImage(context, imagePath)
    }

    /**
     * 初始化人脸识别sdk
     */
    fun initFaceEngine() {
        faceEngine = FaceEngine()
        afCode = faceEngine!!.init(context, FaceEngine.ASF_DETECT_MODE_VIDEO, FaceEngine.ASF_OP_270_ONLY,
                16, 20, processMask)
        val versionInfo = VersionInfo()
        faceEngine!!.getVersion(versionInfo)
        if (afCode != ErrorInfo.MOK) {
            Log.v("yufie", "人脸识别sdk初始化失败$afCode")
        }
    }

    //停止预览
    fun stopPreview() {
        releaseCamera()
    }

    /**
     * 返回相机拍预览的每一帧数据用于人脸识别(nv21格式)
     */
    override fun onPreviewFrame(nv21: ByteArray, camera: Camera) {
//        if (faceRectView != null) {
//            faceRectView.clearFaceInfo()
//        }
//        val faceInfoList = ArrayList<FaceInfo>()
//        var code = faceEngine!!.detectFaces(nv21, previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, faceInfoList)
//        Log.v("yufei", "size = ${faceInfoList.size}")
//        Log.v("yufei", "code = $code")
//        if (code == ErrorInfo.MOK && faceInfoList.size > 0) {
//            code = faceEngine!!.process(nv21, previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, faceInfoList, processMask)
//            if (code != ErrorInfo.MOK) {
//                return
//            }
//        } else {
//            return
//        }
//        val ageInfoList = ArrayList<AgeInfo>()
//        val genderInfoList = ArrayList<GenderInfo>()
//        val face3DAngleList = ArrayList<Face3DAngle>()
//        val faceLivenessInfoList = ArrayList<LivenessInfo>()
//        val ageCode = faceEngine!!.getAge(ageInfoList)
//        val genderCode = faceEngine!!.getGender(genderInfoList)
//        val face3DAngleCode = faceEngine!!.getFace3DAngle(face3DAngleList)
//        val livenessCode = faceEngine!!.getLiveness(faceLivenessInfoList)
//        //有其中一个的错误码不为0，return
//        if (ageCode or genderCode or face3DAngleCode or livenessCode != ErrorInfo.MOK) {
//            return
//        }
//        if (faceRectView != null && drawHelper != null) {
//            val drawInfoList = ArrayList<DrawInfo>()
//            for (i in faceInfoList.indices) {
//                drawInfoList.add(DrawInfo(faceInfoList[i].rect, genderInfoList[i].gender, ageInfoList[i].age, faceLivenessInfoList[i].liveness, null))
//            }
//            drawHelper.draw(faceRectView, drawInfoList)
//        }
    }


}