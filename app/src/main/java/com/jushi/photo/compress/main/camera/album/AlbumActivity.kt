package com.jushi.photo.compress.main.camera.album

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.util.Log
import com.jushi.library.base.BaseActivity
import com.jushi.photo.compress.main.camera.entity.AlbumEntity
import com.jushi.photo.compress.main.camera.utils.ImageUtil
import kotlinx.android.synthetic.main.activity_album_layout.*
import travel.camera.photo.compress.R

/**
 * 我的相册界面
 */
class AlbumActivity : BaseActivity() {
    private lateinit var albums: Map<String, AlbumEntity>
    private var albumPaths: ArrayList<String> = arrayListOf()

    override fun setPageLayout() {
        setContentView(R.layout.activity_album_layout, true, true)
    }

    override fun initData() {
        albums = ImageUtil.findAlbums(this, albumPaths, 0)
    }

    override fun initWidget() {
        setSystemBarViewLayoutParamsR(systemBar_album_activity)
        album_viewpager.adapter = ViewPagerAdapter(supportFragmentManager)
        album_selectTab.setViewPager(album_viewpager)
        album_selectTab.textSize = 16
        album_selectTab.textColor = resources.getColor(R.color._333333)
        album_selectTab.selectedTextColor = resources.getColor(R.color.color_theme)
    }

    override fun setOnViewListener() {
        iv_backBtn_album.setOnClickListener { finish() }
    }

    inner class ViewPagerAdapter(private val fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(p0: Int): Fragment = AlbumFragment.newInstance(albums[albumPaths[p0]]?.photos!!)

        override fun getCount(): Int = albumPaths.size

        override fun getPageTitle(position: Int): CharSequence? {
            val album = albums?.get(albumPaths[position % albumPaths.size])
            return album?.title
        }

    }
}