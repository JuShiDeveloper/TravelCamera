package com.jushi.photo.compress.main.camera.view

import com.jushi.photo.compress.main.camera.utils.FlashMode

interface CameraView {
    //闪光灯状态 （开启/关闭/自动）
    fun flashStatus(mode: FlashMode)

    //显示Toast
    fun showTus(msg: String)

    //是否显示确认（√）以及取消（×）按钮的布局，隐藏拍照按钮的布局
    fun isShowConfirmLayout(isShow: Boolean)

    //拍摄的照片保存成功
    fun pictureSaveSuccess()
}