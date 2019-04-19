package com.jushi.photo.compress.main.camera.helper

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Camera

class CameraHelperBase(private val context: Context) : CameraHelper.CameraHelperImpl {
    override fun getNumberOfCameras(): Int = if (hasCameraSupport()) 1 else 0

    override fun openCamera(id: Int): Camera = Camera.open()

    override fun openDefaultCamera(): Camera = Camera.open()

    override fun openCameraFacing(facing: Int): Camera? {
        if (facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
            return Camera.open()
        }
        return null
    }

    override fun hasCamera(cameraFacingFront: Int): Boolean {
        if (cameraFacingFront == Camera.CameraInfo.CAMERA_FACING_BACK) {
            return hasCameraSupport()
        }
        return false
    }

    override fun getCameraInfo(cameraId: Int, cameraInfo: CameraHelper.CameraInfo2) {
        cameraInfo.facing = Camera.CameraInfo.CAMERA_FACING_BACK
        cameraInfo.orientation = 90
    }

    private fun hasCameraSupport(): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
    }
}