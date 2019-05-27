package com.jushi.photo.compress.main.camera.view

import android.hardware.Camera
import android.view.View
import com.jushi.photo.compress.main.camera.arcFace.FaceRectView
import com.jushi.photo.compress.main.camera.utils.FlashMode

interface CameraView {
    //闪光灯状态 （开启/关闭/自动）
    fun flashStatus(mode: FlashMode)

    //显示Toast
    fun showTus(msg: String)

    //拍摄的照片保存成功
    fun pictureSaveSuccess(imagePath: String)

    //获取显示预览画面的View
    fun getPreviewView(): View

    //获取显示人脸框的View
    fun getFaceRectView(): FaceRectView

    //初始化人脸sdk
    fun initFaceEngine()

}