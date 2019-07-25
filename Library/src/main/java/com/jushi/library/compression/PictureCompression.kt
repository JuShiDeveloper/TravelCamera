package com.jushi.library.compression

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import com.jushi.library.compression.utils.PATH
import com.jushi.library.compression.minterface.OnCompressionPicturesListener
import com.jushi.library.compression.minterface.OnPictureCompressionListener
import com.jushi.library.compression.luban.Luban
import com.jushi.library.compression.luban.OnCompressListener
import java.io.File

/**
 * 图片压缩类
 */
object PictureCompression {

    private fun initialize(context: Context) {
        PATH.initialize(context)
    }

    private val temPictureFile by lazy {
        File(PATH.imageSaveDir)
    }

    private fun getTargetDir() = temPictureFile.toString()

    private fun getRename() = "compression-" + getCurrentTime() + ".jpg"

    private fun getCurrentTime() = System.currentTimeMillis()


    /**
     * 图片压缩
     * @param context
     * @param path
     * @param listener 回掉接口
     */
    fun compressionPicture(context: Context, path: String, listener: OnPictureCompressionListener) {
        initialize(context)
        Luban.with(context)
                .load(path)
                .ignoreBy(20)
                .setTargetDir(getTargetDir())
                .setFocusAlpha(false)
                .filter { !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif")) }
                .setRenameListener { getRename() }
                .setCompressListener(object : OnCompressListener {
                    override fun onStart() {
                        listener.onCompressStart()
                    }

                    override fun onSuccess(file: File) {
                        listener.onCompressSuccess(file)
                    }

                    override fun onError(e: Throwable) {
                        listener.onCompressError(e.message!!)
                    }
                }).launch()
    }

    /**
     * 图片压缩
     * @param context
     * @param file
     * @param listener 回掉接口
     */
    fun compressionPicture(context: Context, file: File, listener: OnPictureCompressionListener) {
        initialize(context)
        Luban.with(context)
                .load(file)
                .ignoreBy(60)
                .setTargetDir(getTargetDir())
                .setFocusAlpha(false)
                .setRenameListener { getRename() }
                .setCompressListener(object : OnCompressListener {
                    override fun onStart() {
                        listener.onCompressStart()
                    }

                    override fun onSuccess(file: File) {
                        listener.onCompressSuccess(file)
                    }

                    override fun onError(e: Throwable?) {
                        listener.onCompressError(e?.message!!)
                    }
                }).launch()
    }

    /**
     * 图片压缩
     * @param context
     * @param uri
     * @param listener 回掉接口
     */
    fun compressionPicture(context: Context, uri: Uri, listener: OnPictureCompressionListener) {
        initialize(context)
        Luban.with(context)
                .load(uri)
                .ignoreBy(100)
                .setTargetDir(getTargetDir())
                .setFocusAlpha(false)
                .setRenameListener { getRename() }
                .setCompressListener(object : OnCompressListener {
                    override fun onStart() {
                    }

                    override fun onSuccess(file: File) {
                        listener.onCompressSuccess(file)
                    }

                    override fun onError(e: Throwable?) {

                    }
                }).launch()
    }

    private var compressPhotos: List<File> = arrayListOf()
    /**
     * 多张图片压缩  （使用RxJava）
     * @param context
     * @param photos  装有图片的集合  （集合的泛型可以是  File、String和Uri 类型）
     * @param listener 回掉接口
     */
    fun compressPictures(context: Context, photos: List<*>, listener: OnCompressionPicturesListener) {
        initialize(context)
        compressPhotos = Luban.with(context)
                .setTargetDir(getTargetDir())
                .setRenameListener { getRename() }
                .ignoreBy(100)
                .setCompressListener(object : OnCompressListener {
                    override fun onStart() {

                    }

                    override fun onSuccess(file: File?) {
                        listener.onPictureFiles(compressPhotos)
                    }

                    override fun onError(e: Throwable?) {

                    }

                })
                .load(photos)
                .get()
    }
}