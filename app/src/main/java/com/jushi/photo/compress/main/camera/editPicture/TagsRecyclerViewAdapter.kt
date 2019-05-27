package com.jushi.photo.compress.main.camera.editPicture

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import travel.camera.photo.compress.R

class TagsRecyclerViewAdapter(private val tags: List<Int>, private val context: Context) : RecyclerView.Adapter<TagsRecyclerViewAdapter.TagsHolder>() {

    private lateinit var listener: View.OnClickListener

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): TagsHolder {
        return TagsHolder(LayoutInflater.from(context).inflate(R.layout.item_tags_recycler_view, parent, false))
    }

    override fun getItemCount(): Int = tags.size

    override fun onBindViewHolder(holder: TagsHolder, position: Int) {
        holder.imageView.setImageResource(tags[position])
        holder.imageView.setOnClickListener {
            holder.imageView.tag = position
            listener.onClick(holder.imageView)
        }
    }

    fun setOnItemClickListener(listener: View.OnClickListener) {
        this.listener = listener
    }

    class TagsHolder : RecyclerView.ViewHolder {
        var imageView: ImageView

        constructor(itemView: View) : super(itemView) {
            imageView = itemView as ImageView
        }
    }
}