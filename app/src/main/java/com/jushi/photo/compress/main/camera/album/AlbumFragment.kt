package com.jushi.photo.compress.main.camera.album

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.muslim.pro.imuslim.azan.portion.common.base.ViewPagerFragment
import kotlinx.android.synthetic.main.fragment_album_layout.*
import travel.camera.photo.compress.R

class AlbumFragment : ViewPagerFragment(), View.OnClickListener {

    private lateinit var photos: ArrayList<String>
    private lateinit var adapter: PhotoAdapter

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
        album_fragment_recyclerVeiw.addItemDecoration(object :RecyclerView.ItemDecoration(){
            override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
                super.getItemOffsets(outRect, itemPosition, parent)
                outRect.set(10,10,10,10)
            }
        })
    }

    override fun onFragmentVisibleChange(isVisible: Boolean) {
        if (isVisible && !isInitDataSuccess) {
            photos = arguments!![DATA_KEY] as ArrayList<String>
            adapter = PhotoAdapter(context!!)
            isInitDataSuccess = true
        }
        if (isVisible && isInitDataSuccess) {
            album_fragment_recyclerVeiw.adapter = adapter
            adapter.setOnItemClickListener(this)
        }
    }

    override fun onClick(v: View?) {
        val imagePath = v?.tag as String
        Log.v("yufei", "imagePath = $imagePath")
    }

    inner class PhotoAdapter(private val context: Context) : RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {
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
                    holder.itemView.tag = photos[position]
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