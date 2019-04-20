package com.jushi.photo.compress.main.camera.album

import com.jushi.library.base.BaseActivity
import kotlinx.android.synthetic.main.activity_album_layout.*
import travel.camera.photo.compress.R

/**
 * 我的相册界面
 */
class AlbumActivity : BaseActivity() {
    override fun setPageLayout() {
        setContentView(R.layout.activity_album_layout, true, true)
    }

    override fun initWidget() {

    }

    override fun initData() {
        setSystemBarViewLayoutParamsR(systemBar_album_activity)
    }

    override fun setOnViewListener() {
        iv_backBtn_album.setOnClickListener { finish() }
    }
}