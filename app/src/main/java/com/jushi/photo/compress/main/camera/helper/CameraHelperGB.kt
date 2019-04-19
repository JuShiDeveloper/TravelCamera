package com.jushi.photo.compress.main.camera.helper

import android.annotation.TargetApi
import android.hardware.Camera

@TargetApi(9)
class CameraHelperGB : CameraHelper.CameraHelperImpl {
    override fun getNumberOfCameras(): Int = Camera.getNumberOfCameras()

    override fun openCamera(id: Int): Camera = Camera.open(id)

    override fun openDefaultCamera(): Camera = Camera.open(0)

    override fun openCameraFacing(facing: Int): Camera = Camera.open(getCameraId(facing))

    override fun hasCamera(cameraFacingFront: Int): Boolean = getCameraId(cameraFacingFront) != -1

    override fun getCameraInfo(cameraId: Int, cameraInfo: CameraHelper.CameraInfo2) {
        val info = Camera.CameraInfo()
        Camera.getCameraInfo(cameraId, info)
        cameraInfo.facing = info.facing
        cameraInfo.orientation = info.orientation
    }

    private fun getCameraId(facing: Int): Int {
        val numberOfCameras = Camera.getNumberOfCameras()
        val cameraInfo = Camera.CameraInfo()
        for (id in 0..numberOfCameras) {
            Camera.getCameraInfo(id, cameraInfo)
            if (cameraInfo.facing == facing) {
                return id
            }
        }
        return -1
    }
}