package com.jushi.photo.compress.main.camera.helper

import android.app.Activity
import android.content.Context
import android.hardware.Camera
import android.os.Build
import android.view.Surface

class CameraHelper(private val context: Context) {
    private lateinit var cameraHelperImpl: CameraHelperImpl

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            cameraHelperImpl = CameraHelperGB()
        } else {

        }
    }

    interface CameraHelperImpl {
        fun getNumberOfCameras(): Int
        fun openCamera(id: Int): Camera
        fun openDefaultCamera(): Camera
        fun openCameraFacing(facing: Int): Camera?
        fun hasCamera(cameraFacingFront: Int): Boolean
        fun getCameraInfo(cameraId: Int, cameraInfo: CameraInfo2)
    }

    inner class CameraInfo2 {
        var facing: Int = 0
        var orientation: Int = 0
    }

    fun getNumberOfCameras(): Int = cameraHelperImpl.getNumberOfCameras()

    fun openCamera(id: Int): Camera = cameraHelperImpl.openCamera(id)

    fun openDefaultCamera(): Camera = cameraHelperImpl.openDefaultCamera()

    fun openFrontCamera(): Camera? = cameraHelperImpl.openCameraFacing(Camera.CameraInfo.CAMERA_FACING_BACK)

    fun openBackCamera(): Camera? = cameraHelperImpl.openCameraFacing(Camera.CameraInfo.CAMERA_FACING_BACK)

    fun hasFrontCamera(): Boolean = cameraHelperImpl.hasCamera(Camera.CameraInfo.CAMERA_FACING_FRONT)

    fun hasBackCamera(): Boolean = cameraHelperImpl.hasCamera(Camera.CameraInfo.CAMERA_FACING_BACK)

    fun getCameraInfo(cameraId: Int, cameraInfo: CameraInfo2) {
        cameraHelperImpl.getCameraInfo(cameraId, cameraInfo)
    }

    fun getCameraDisplayOrientation(activity: Activity, cameraId: Int): Int {
        val orientation = activity.windowManager.defaultDisplay.orientation
        var degrees = 0
        when (orientation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }
        var result = 0
        val cameraInfo = CameraInfo2()
        getCameraInfo(cameraId, cameraInfo)
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (cameraInfo.orientation + degrees) % 360
        } else { //back-facing
            result = (cameraInfo.orientation - degrees + 360) % 360
        }
        return result
    }
}