package com.jushi.photo.compress.main.camera.editPicture

import android.net.Uri
import android.util.Log
import com.jushi.library.base.BaseActivity

/**
 * 拍照后编辑图片界面
 */
class EditPictureActivity : BaseActivity() {
    override fun setPageLayout() {

    }

    override fun initData() {
        Log.v("yufei","${intent.data as Uri}")
    }

    override fun setOnViewListener() {
    }
}