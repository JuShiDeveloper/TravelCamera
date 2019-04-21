package com.jushi.photo.compress.main.camera.album

import com.jushi.library.base.BaseActivity
import kotlinx.android.synthetic.main.activity_picture_layout.*
import travel.camera.photo.compress.R

/**
 * 显示大图的界面
 */
class PictureActivity : BaseActivity() {
    companion object {
        val PICTURE_INDEX_KEY = "picture_index" //跳转过来时，该图片在列表中的位置
        val PHOTOS = "photos" //图片列表
    }

    private lateinit var photos: ArrayList<String>
    private var position = -1

    override fun setPageLayout() {
        setContentView(R.layout.activity_picture_layout, true, true)
    }

    override fun initWidget() {
        setSystemBarViewLayoutParamsR(systemBar_picture_activity)
        showPictureIndex()
    }

    override fun initData() {
        val bundle = intent.getBundleExtra(PictureActivity::class.java.name)
        photos = bundle.getStringArrayList(PHOTOS)
        position = bundle.getInt(PICTURE_INDEX_KEY)
    }

    override fun setOnViewListener() {
        iv_backBtn_picture.setOnClickListener { finish() }
    }

    /**
     * 显示当前图片在列表中的位置
     */
    private fun showPictureIndex() {
        tv_title_picture.text = "${position + 1}/${photos.size}"
    }
}