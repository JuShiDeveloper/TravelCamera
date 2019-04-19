package com.jushi.photo.compress.main.camera.utils

import android.content.Context
import android.graphics.*
import android.os.AsyncTask
import android.os.Environment
import com.jushi.library.takingPhoto.util.FileUtil
import travel.camera.photo.compress.R
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream

class SavePictureUtil(private val data: ByteArray, private val curCameraId: Int,
                      private val context: Context, private val listener: PictureSaveListener) : AsyncTask<Any, Any, String>() {

    private var photoSize = 2000

    override fun doInBackground(vararg params: Any?): String? {
        try {
            return saveToSDCard()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        if (result != null) {
            listener.pictureSaveSuccess()
        }
    }

    private fun saveToSDCard(): String {
        var cBitmap: Bitmap? = null
        //获得图片大小
        var options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeByteArray(data, 0, data.size, options)
        photoSize = if (options.outHeight > options.outWidth) options.outWidth else options.outHeight
        var height = if (options.outHeight > options.outWidth) options.outHeight else options.outWidth
        options.inJustDecodeBounds = false
        var rect = if (curCameraId == 1) {
            Rect(height - photoSize, 0, height, photoSize)
        } else {
            Rect(0, 0, photoSize, photoSize)
        }
        try {
            cBitmap = decodeRegionCrop(rect)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        var folder = Environment.getExternalStorageDirectory().path
        val fileDir = File(folder + "/" + context.getString(R.string.app_name) + "/Pictures")
        var imagePath = FileUtil.saveToFile(fileDir.path, true, cBitmap)
        cBitmap!!.recycle()
        return imagePath
    }

    private fun decodeRegionCrop(rect: Rect): Bitmap {
        var inputStream: InputStream? = null
        System.gc()
        var bitmap: Bitmap? = null
        try {
            inputStream = ByteArrayInputStream(data)
            var decoder = BitmapRegionDecoder.newInstance(inputStream, false)
            bitmap = decoder.decodeRegion(rect, BitmapFactory.Options())
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (inputStream != null) {
                inputStream.close()
            }
        }
        var matrix = Matrix()
        matrix.setRotate(90f, (photoSize / 2).toFloat(), (photoSize / 2).toFloat())
        if (curCameraId == 1) {
            matrix.postScale(1f, -1f)
        }
        var rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, photoSize, photoSize, matrix, true)
        if (rotateBitmap != bitmap) {
            bitmap!!.recycle()
        }
        return rotateBitmap
    }

    interface PictureSaveListener {
        fun pictureSaveSuccess()
    }
}