package com.jushi.photo.compress.main.camera.album

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.PagerAdapter
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.jushi.photo.compress.main.camera.editPicture.EditPictureActivity
import com.jushi.photo.compress.main.camera.view.AlbumView
import com.muslim.pro.imuslim.azan.portion.common.base.ViewPagerFragment
import kotlinx.android.synthetic.main.fragment_album_layout.*
import travel.camera.photo.compress.R

/**
 * 以列表形式显示文件夹中图片
 */
class AlbumFragment : ViewPagerFragment(), View.OnClickListener {
    var albumView: AlbumView? = null
    private lateinit var photos: ArrayList<String>
    private lateinit var adapter: AlbumAdapter
    private lateinit var animatorSet: AnimatorSet
    private var position = 0
    private val PHOTOS_INDEX = "photos_index"

    companion object {
        private val DATA_KEY = "photos"
        fun newInstance(photos: ArrayList<String>): AlbumFragment {
            val albumFragment = AlbumFragment()
            val bundle = Bundle()
            bundle.putStringArrayList(DATA_KEY, photos)
            albumFragment.arguments = bundle
            return albumFragment
        }
    }

    override fun initRootView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootView = inflater.inflate(R.layout.fragment_album_layout, container, false)
        return rootView!!
    }

    override fun initWidget() {
        album_fragment_recyclerVeiw.layoutManager = GridLayoutManager(context, 4)
        album_fragment_recyclerVeiw.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
                super.getItemOffsets(outRect, itemPosition, parent)
                outRect.set(10, 10, 10, 10)
            }
        })
    }

    override fun setViewListener() {

    }

    override fun onFragmentVisibleChange(isVisible: Boolean) {
        if (isVisible && !isInitDataSuccess) {
            photos = arguments!![DATA_KEY] as ArrayList<String>
            adapter = AlbumAdapter(context!!)
            isInitDataSuccess = true
        }
        if (isVisible && isInitDataSuccess) {
            album_fragment_recyclerVeiw.adapter = adapter
            adapter.setOnItemClickListener(this)
        }
    }

    override fun onClick(v: View?) {
        position = v?.tag as Int
        val intent = Intent(context, AlbumPreviewActivity::class.java)
        intent.putExtra(AlbumPreviewActivity.EXTRA_KEY_POSITION, position)
        intent.putExtra(AlbumPreviewActivity.EXTRA_KEY_PHOTOS, photos)
        try {
            val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity as Activity, v, "app_name")
            ContextCompat.startActivity(context!!, intent, optionsCompat.toBundle())
        } catch (e: Exception) {
            startActivity(intent)
        }
    }

    /**
     * 图片列表的Adapter
     */
    inner class AlbumAdapter(private val context: Context) : RecyclerView.Adapter<AlbumAdapter.ViewHolder>() {
        private var listener: View.OnClickListener? = null

        override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_album_recycler_view, parent, false))
        }

        override fun getItemCount(): Int = photos.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            Glide.with(context).load(photos[position])
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(holder.image)
            if (listener != null) {
                holder.itemView.setOnClickListener {
                    holder.itemView.tag = position
                    listener?.onClick(holder.itemView)
                }
            }
        }

        fun setOnItemClickListener(listener: View.OnClickListener) {
            this.listener = listener
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val image: ImageView = itemView.findViewById(R.id.iv_album_item)
        }

    }

}