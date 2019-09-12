package com.jushi.photo.compress.main.camera.album

import android.annotation.SuppressLint
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.jushi.library.base.BaseActivity
import com.jushi.library.customView.scaleImageView.ScaleImageView
import kotlinx.android.synthetic.main.activity_album_preview_layout.*
import travel.camera.photo.compress.R

/**
 * 图片大图预览界面
 * create time 2019/9/12
 */
class AlbumPreviewActivity : BaseActivity(), ViewPager.OnPageChangeListener {
    companion object {
        val EXTRA_KEY_POSITION = "extra_key_position"
        val EXTRA_KEY_PHOTOS = "extra_key_photos"
    }

    private lateinit var photos: ArrayList<String>
    private var index: Int = 0
    private val images = ArrayList<ScaleImageView>()


//startActivity(EditPictureActivity::class.java, Uri.parse(photos[position]))

    override fun setPageLayout() {
        setContentView(R.layout.activity_album_preview_layout, true, true)
    }

    override fun initData() {
        index = intent.getIntExtra(EXTRA_KEY_POSITION, 0)
        photos = intent.getStringArrayListExtra(EXTRA_KEY_PHOTOS)
        for (i in 0 until photos.size) {
            val view: ScaleImageView = View.inflate(this, R.layout.view_preview_item, null) as ScaleImageView
            Glide.with(this).asBitmap().load(photos[i]).into(view)
            images.add(view)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun initWidget() {
        setSystemBarViewLayoutParamsR(systemBar_album_activity_preview)
        tv_photo_count.text = "${index + 1}/${photos.size}"
        preview_ViewPager.adapter = ViewPagerAdapter()
        preview_ViewPager.currentItem = index
    }

    override fun setOnViewListener() {
        iv_backBtn_album.setOnClickListener { finish() }
        preview_ViewPager.addOnPageChangeListener(this)
    }

    override fun onPageScrollStateChanged(p0: Int) {

    }

    override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
    }

    override fun onPageSelected(p0: Int) {
        tv_photo_count.text = "${p0 + 1}/${photos.size}"
    }

    inner class ViewPagerAdapter : PagerAdapter() {

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = images[position]
            if (view.parent != null) {
                container.removeView(view)
            }
            container.addView(view)
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

        }

        override fun isViewFromObject(p0: View, p1: Any): Boolean = p0 == p1

        override fun getCount(): Int = images.size

    }
}