package com.jushi.photo.compress.main.camera.view

import com.jushi.photo.compress.main.camera.utils.FlashMode

interface CameraView {
    //闪光灯状态 （开启/关闭/自动）
    fun flashStatus(mode: FlashMode)
    //显示Toast
    fun showTus(msg: String)
}