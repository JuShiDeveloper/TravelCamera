package com.jushi.photo.compress.main.camera.utils

import android.content.Context
import android.graphics.*
import android.hardware.Camera
import android.os.AsyncTask
import android.os.Build
import android.os.Environment
import android.view.Gravity
import com.jushi.library.takingPhoto.util.FileUtil
import com.jushi.library.utils.ToastUtils
import travel.camera.photo.compress.R
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream

/**
 * 保存拍摄的照片
 */
class SavePictureUtil(private val data: ByteArray, private val curCameraId: Int,
                      private val context: Context, private val listener: PictureSaveListener) : AsyncTask<Any, Any, String>() {

    override fun onPreExecute() {
        super.onPreExecute()
        ToastUtils.showToastAtLocation(context, context.getString(R.string.chu_li_ing), Gravity.CENTER)
    }

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
            listener.pictureSaveSuccess(result)
        }
    }

    private fun saveToSDCard(): String {
        var cBitmap: Bitmap? = null
        //获得图片大小
        var options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeByteArray(data, 0, data.size, options)
        options.inJustDecodeBounds = false
        var rect = Rect(0, 0, options.outWidth, options.outHeight)
        try {
            cBitmap = decodeRegionCrop(rect, options)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        var folder = Environment.getExternalStorageDirectory().path
        val fileDir = File(folder + "/" + context.getString(R.string.app_name) + "/Pictures")
        var imagePath = FileUtil.saveToFile(fileDir.path, true, cBitmap)
        cBitmap!!.recycle()
        return imagePath
    }

    private fun decodeRegionCrop(rect: Rect, options: BitmapFactory.Options): Bitmap {
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
        //将图片旋转90度
        matrix.setRotate(90f, (options.outWidth / 2).toFloat(), (options.outHeight / 2).toFloat())
        if (curCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            //前置摄像头如不单独设置则保存后的图片与拍照的方向相反
            matrix.postScale(1f, -1f)
        }
        var rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, options.outWidth, options.outHeight, matrix, true)
        if (rotateBitmap != bitmap) {
            bitmap!!.recycle()
        }
        return rotateBitmap
    }

    interface PictureSaveListener {
        fun pictureSaveSuccess(imagePath: String)
    }
}