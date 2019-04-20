package com.jushi.photo.compress.main.camera.entity

/**
 * 相册实体类
 */
class AlbumEntity {
    /**
     * 存放图片的文件夹路径
     */
    var albumUri: String = ""
    /**
     * 该文件夹标题
     */
    var title: String = ""
    /**
     * 该文件夹下的图片路径集合
     */
    var photos: ArrayList<String>? = null

    constructor(title: String,albumUri: String,  photos: ArrayList<String>?) {
        this.albumUri = albumUri
        this.title = title
        this.photos = photos
    }

    override fun toString(): String {
        return "AlbumEntity(albumUri='$albumUri', title='$title', photos=$photos)"
    }


}