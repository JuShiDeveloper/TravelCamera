package com.jushi.photo.compress.main.camera.album

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
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
    private var pivotX = 0.0f
    private var pivotY = 0.0f
    private val PIVOT_X = "pivot_x"
    private val PIVOT_Y = "pivot_y"
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
        iv_album_show_picture.setOnClickListener {
            if (rl_title_bar.visibility != View.VISIBLE) {
                rl_title_bar.visibility = View.VISIBLE
                rl_album_show_picture_layout.setBackgroundColor(resources.getColor(R.color.black))
            } else {
                rl_title_bar.visibility = View.INVISIBLE
                rl_album_show_picture_layout.setBackgroundColor(resources.getColor(R.color.white))
            }
        }
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
        val bundle = v?.tag as Bundle
        pivotX = bundle.getFloat(PIVOT_X)
        pivotY = bundle.getFloat(PIVOT_Y)
        position = bundle.getInt(PHOTOS_INDEX)
        showPicture(position)
        initAnimator(View.VISIBLE, 0.1f, 1f)
    }

    private fun showPicture(position: Int) {
        iv_album_show_picture.reset()
        Glide.with(context!!).load(photos[position]).into(iv_album_show_picture)
    }

    private fun initAnimator(visibility: Int, scaleStart: Float, scaleEnd: Float) {
        iv_album_show_picture.pivotX = pivotX + 15
        iv_album_show_picture.pivotY = pivotY - 400
        showView(visibility)
        animatorSet = AnimatorSet()
        val scaleX = ObjectAnimator.ofFloat(iv_album_show_picture, "scaleX", scaleStart, scaleEnd)
        val scaleY = ObjectAnimator.ofFloat(iv_album_show_picture, "scaleY", scaleStart, scaleEnd)
        animatorSet.duration = 600
        animatorSet.interpolator = LinearInterpolator()
        animatorSet.play(scaleX).with(scaleY)
        animatorSet.start()
    }

    /**
     * 显示与隐藏缩放的View
     */
    private fun showView(visibility: Int) {
        if (visibility == View.VISIBLE) {
            rl_album_show_picture_layout.visibility = visibility
            albumView?.viewPagerCanScrollable(false)
            rl_album_show_picture_layout.postDelayed({
                rl_album_show_picture_layout.setBackgroundColor(resources.getColor(R.color.white))
            }, 700)
        } else {
            rl_title_bar.visibility = View.INVISIBLE
            rl_album_show_picture_layout.setBackgroundColor(Color.TRANSPARENT)
            rl_album_show_picture_layout.postDelayed({
                rl_album_show_picture_layout.visibility = visibility
                albumView?.viewPagerCanScrollable(true)
            }, 600)
        }
    }

    fun backPressed(): Boolean {
        if (rl_album_show_picture_layout.visibility == View.VISIBLE) {
            initAnimator(View.GONE, 1f, 0.1f)
            return false
        }
        return true
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
                var x = 0f
                var y = 0f
                holder.itemView.setOnTouchListener { v, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            x = event.rawX
                            y = event.rawY
                        }
                        MotionEvent.ACTION_UP -> {
                            val bundle = Bundle()
                            bundle.putFloat(PIVOT_X, x)
                            bundle.putFloat(PIVOT_Y, y)
                            bundle.putInt(PHOTOS_INDEX, position)
                            holder.itemView.tag = bundle
                            listener?.onClick(holder.itemView)
                            return@setOnTouchListener true
                        }
                    }
                    return@setOnTouchListener true
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