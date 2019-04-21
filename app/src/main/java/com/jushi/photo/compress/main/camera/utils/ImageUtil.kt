package com.jushi.photo.compress.main.camera.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import com.jushi.library.utils.FileUtil
import com.jushi.photo.compress.main.camera.entity.AlbumEntity
import java.io.File
import java.util.ArrayList
import java.util.HashMap

object ImageUtil {
    /**
     * 获取手机中的相册文件夹
     */
    fun findAlbums(mContext: Context, paths: MutableList<String>, babyId: Long): Map<String, AlbumEntity> {
        paths.clear()
        paths.add(FileUtil.getSystemPhotoPath())
        val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_ADDED)//FIXME 拍照时间为新增照片时间
        val cursor = mContext.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, //指定所要查询的字段
                MediaStore.Images.Media.SIZE + ">?", //查询条件
                arrayOf("100000"), //查询条件中问号对应的值
                MediaStore.Images.Media.DATE_ADDED + " desc")

        cursor!!.moveToFirst()
        //文件夹照片
        val albums = HashMap<String, AlbumEntity>()
        while (cursor.moveToNext()) {
            val data = cursor.getString(1)
            if (data.lastIndexOf("/") < 1) {
                continue
            }
            val sub = data.substring(0, data.lastIndexOf("/"))
            if (!albums.keys.contains(sub)) {
                val name = sub.substring(sub.lastIndexOf("/") + 1, sub.length)
                if (!paths.contains(sub)) {
                    paths.add(sub)
                }
                albums[sub] = AlbumEntity(name, sub, ArrayList())
            }

            albums[sub]!!.photos?.add(data)
        }
        //系统相机照片
        val sysPhotos = FileUtil.findPicsInDir(FileUtil.getSystemPhotoPath())
        if (!sysPhotos.isEmpty()) {
            albums[FileUtil.getSystemPhotoPath()] = AlbumEntity("系统相册", FileUtil.getSystemPhotoPath(), sysPhotos)
        } else {
            albums.remove(FileUtil.getSystemPhotoPath())
            paths.remove(FileUtil.getSystemPhotoPath())
        }
        return albums
    }

    /**
     * 通知系统检索刚刚保存的图片
     */
    fun notifySystemScanImage(context: Context, imagePath: String) {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.DATA, imagePath)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        var uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
    }

}