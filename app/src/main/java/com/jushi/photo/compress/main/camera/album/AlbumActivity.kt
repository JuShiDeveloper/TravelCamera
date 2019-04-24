package com.jushi.photo.compress.main.camera.album

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import com.jushi.library.base.BaseActivity
import com.jushi.photo.compress.main.camera.entity.AlbumEntity
import com.jushi.photo.compress.main.camera.utils.ImageUtil
import com.jushi.photo.compress.main.camera.view.AlbumView
import kotlinx.android.synthetic.main.activity_album_layout.*
import travel.camera.photo.compress.R

/**
 * 我的相册界面
 */
class AlbumActivity : BaseActivity(), ViewPager.OnPageChangeListener , AlbumView {
    private lateinit var albums: Map<String, AlbumEntity>
    private var albumPaths: ArrayList<String> = arrayListOf()
    private var pages = HashMap<String, AlbumFragment>()
    private lateinit var pageAdapter: ViewPagerAdapter
    private var pageIndex = 0

    override fun setPageLayout() {
        setContentView(R.layout.activity_album_layout, true, true)
    }

    override fun initData() {
        albums = ImageUtil.findAlbums(this, albumPaths, 0)
        pageAdapter = ViewPagerAdapter(supportFragmentManager)
    }

    override fun initWidget() {
        setSystemBarViewLayoutParamsR(systemBar_album_activity)
        album_viewpager.scrollable = true
        album_viewpager.adapter = pageAdapter
        album_selectTab.setViewPager(album_viewpager)
        album_selectTab.textSize = 16
        album_selectTab.selectedTextColor = resources.getColor(R.color.color_theme)
    }

    override fun setOnViewListener() {
        iv_backBtn_album.setOnClickListener { finish() }
        album_selectTab.setOnPageChangeListener(this)
    }

    override fun viewPagerCanScrollable(scrollable: Boolean) {
        album_viewpager.scrollable = scrollable
    }

    override fun onPageScrollStateChanged(p0: Int) {

    }

    override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {

    }

    override fun onPageSelected(p0: Int) {
        pageIndex = p0
    }

    override fun onBackPressed() {
        if ((pageAdapter.getItem(pageIndex) as AlbumFragment).backPressed()) {
            super.onBackPressed()
        }
    }

    private fun getPage(position: Int): AlbumFragment {
        var page = pages[albumPaths[position]]
        if (page == null) {
            page = AlbumFragment.newInstance(albums[albumPaths[position]]?.photos!!)
            pages[albumPaths[position]] = page
        }
        page.albumView = this
        return page
    }

    inner class ViewPagerAdapter(private val fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(p0: Int): Fragment = getPage(p0)

        override fun getCount(): Int = albumPaths.size

        override fun getPageTitle(position: Int): CharSequence? {
            val album = albums?.get(albumPaths[position % albumPaths.size])
            return album?.title
        }

    }
}