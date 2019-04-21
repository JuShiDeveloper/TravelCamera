package com.jushi.photo.compress.main.photoCompress

import android.Manifest
import android.animation.Animator
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.view.View
import com.base.muslim.compression.PictureCompression
import com.base.muslim.compression.minterface.OnPictureCompressionListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.jushi.library.base.BaseActivity
import com.jushi.library.takingPhoto.PictureHelper
import com.jushi.library.utils.FileUtil
import com.jushi.library.takingPhoto.util.PermissionUtil
import com.jushi.photo.compress.main.camera.utils.ImageUtil
import kotlinx.android.synthetic.main.activity_compress_layout.*
import travel.camera.photo.compress.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * 图片压缩界面
 */
class CompressActivity : BaseActivity(), OnPictureCompressionListener {
    //从相册选择图片的请求码
    private val REQUEST_CHOICE_PICTURE_CODE = 0x0001
    private val REQUEST_PERMISSION_CODE = 1
    private lateinit var file: File
    private var curFileSize: String = ""  //记录当前文件的大小
    private var preFileSize: String = ""  //记录前一次的文件大小
    private lateinit var savePath: File
    private var isSave: Boolean = false
    private val COMPRESS = "compress"
    private val SAVE = "save"
    private lateinit var compressAnimationListener: CompressAnimationListener
    private lateinit var saveAnimationListener: SaveAnimationListener

    override fun setPageLayout() {
        setContentView(R.layout.activity_compress_layout, true, true)
    }

    override fun initData() {

    }

    override fun initWidget() {
        setSystemBarViewLayoutParamsR(activity_compress_systemBar_View)
        compressAnimationListener = CompressAnimationListener();
        compress_LottieAnimationView.addAnimatorListener(compressAnimationListener)
        saveAnimationListener = SaveAnimationListener()
        save_LottieAnimationView.addAnimatorListener(saveAnimationListener)
    }

    override fun setOnViewListener() {
        //返回按钮
        Back_Compress_Button.setOnClickListener { finish() }
        //选择图片按钮
        tv_choice_picture.setOnClickListener {
            PictureHelper.gotoPhotoAlbum(this@CompressActivity, REQUEST_CHOICE_PICTURE_CODE)
        }
        //开始压缩按钮
        tv_compress_picture.setOnClickListener {
            PictureCompression.compressionPicture(this, file, this)
        }

        //保存按钮
        tv_compress_save_button.setOnClickListener {
            if (isSave) {
                showToast(getString(R.string.picture_save_success))
                return@setOnClickListener
            }
            playAnimation(SAVE)
        }
    }

    /**
     * 图片开始压缩
     */
    override fun onCompressStart() {
        playAnimation(COMPRESS)
    }

    /**
     * 图片压缩成功
     */
    override fun onCompressSuccess(file: File) {
        this.file = file
    }

    /**
     * 图片压缩失败
     */
    override fun onCompressError(msg: String) {

    }

    private fun showPicture(filePath: String) {
        Glide.with(this).load(filePath)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(iv_compress_photo)
    }

    private fun showPictureSize(size: String) {
        tv_picture_size.text = size
        preFileSize = curFileSize
        curFileSize = size
    }

    private fun setViewVisibility(visibility: Int) {
        //压缩到最低大小时的提示语
        tv_compress_hint.visibility = visibility
    }

    /**
     * 设置开始压缩按钮和保存按钮的状态
     */
    private fun setButtonIsEnabled(isEnabled: Boolean) {
        if (isEnabled) {
            tv_compress_save_button.setTextColor(resources.getColor(R.color.color_theme))
            tv_compress_picture.setTextColor(resources.getColor(R.color._999999))
        } else {
            tv_compress_save_button.setTextColor(resources.getColor(R.color._999999))
            tv_compress_picture.setTextColor(resources.getColor(R.color.color_theme))
        }
        tv_compress_save_button.isEnabled = isEnabled
        tv_compress_picture.isEnabled = !isEnabled
    }

    /**
     * 播放动画
     */
    private fun playAnimation(type: String) {
        when (type) {
            COMPRESS -> {
                compress_LottieAnimationView.visibility = View.VISIBLE
                compress_LottieAnimationView.playAnimation()
            }
            SAVE -> {
                save_LottieAnimationView.visibility = View.VISIBLE
                save_LottieAnimationView.playAnimation()
                tv_compress_hint.text = getString(R.string.saveing)
                tv_compress_save_button.isEnabled = false
            }
        }
    }

    /**
     * 停止动画
     */
    private fun stopAnimation(type: String) {
        when (type) {
            COMPRESS -> {
                compress_LottieAnimationView.visibility = View.INVISIBLE
                compress_LottieAnimationView.pauseAnimation()
            }
            SAVE -> {
                save_LottieAnimationView.visibility = View.INVISIBLE
                save_LottieAnimationView.pauseAnimation()
                tv_compress_save_button.isEnabled = true
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CHOICE_PICTURE_CODE -> { //从相册选择图片返回
                if (resultCode != Activity.RESULT_OK || data == null || data.data == null) {
                    showToast(getString(R.string.no_choice_picture))
                    return
                }
                val uri = data.data as Uri
                val filePath = FileUtil.getRealFilePathFromUri(this, uri)
                file = File(filePath)
                showPictureSize(getFileSize())
                showPicture(filePath)
                setViewVisibility(View.INVISIBLE)
                setButtonIsEnabled(false)
                setViewInVisible()
                tv_compress_picture.text = getString(R.string.start_compress)
            }
            REQUEST_PERMISSION_CODE -> { //请求读写权限
                if (resultCode == Activity.RESULT_OK) {
                    showSaveSuccess(FileUtil.copyFile(file.path, savePath.path))
                }
            }
        }
    }

    private fun setViewInVisible() {
        tv_picture_size_hint.visibility = View.VISIBLE
        tv_picture_size.visibility = View.VISIBLE
        tv_hint_user.visibility = View.GONE
    }

    private fun getFileSize(): String {
        var size: String
        var fSize = file.length()
        fSize /= 1024
        size = "${fSize}KB"
        if (fSize >= 1024) {
            fSize /= 1024
            size = "${fSize}MB"
        }
        return size
    }

    /**
     * 显示文件保存提示
     */
    private fun showSaveSuccess(isSuccess: Boolean) {
        if (isSuccess) {
            tv_compress_hint.text = getString(R.string.save_hint)
        }
        isSave = isSuccess
    }

    override fun onDestroy() {
        super.onDestroy()
        compress_LottieAnimationView.removeAnimatorListener(compressAnimationListener)
        save_LottieAnimationView.removeAnimatorListener(saveAnimationListener)
    }

    /**
     * 压缩动画监听类
     */
    private inner class CompressAnimationListener : Animator.AnimatorListener {

        override fun onAnimationStart(animation: Animator?) {
            showPicture("")
            tv_compress_loading.visibility = View.VISIBLE
        }

        override fun onAnimationCancel(animation: Animator?) {

        }

        override fun onAnimationEnd(animation: Animator?) {
            stopAnimation(COMPRESS)
            showPicture(file.path)
            showPictureSize(getFileSize())
            if (curFileSize == preFileSize) {
                setViewVisibility(View.VISIBLE)
                setButtonIsEnabled(true)
            }
            tv_compress_loading.visibility = View.INVISIBLE
            tv_compress_picture.text = getString(R.string.second_compress)
        }

        override fun onAnimationRepeat(animation: Animator?) {

        }
    }

    /**
     * 保存动画监听类
     */
    private inner class SaveAnimationListener : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator?) {
            val rootPath = Environment.getExternalStorageDirectory().path
            val fileDir = File(rootPath + "/" + getString(R.string.app_name))
            if (!fileDir.exists()) fileDir.mkdirs()
            val date = Date()
            val format = SimpleDateFormat("yyyyMMddHHmmss") // 格式化时间
            val filename = "压缩${format.format(date)}.jpg"
            savePath = File(fileDir.path, filename)
            if (!savePath.exists()) {
                savePath.createNewFile()
            }
        }

        override fun onAnimationCancel(animation: Animator?) {

        }

        override fun onAnimationEnd(animation: Animator?) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (PermissionUtil.request(this@CompressActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSION_CODE)) {
                    showSaveSuccess(FileUtil.copyFile(file.path, savePath.path))
                }
            } else {
                showSaveSuccess(FileUtil.copyFile(file.path, savePath.path))
            }
            stopAnimation(SAVE)
        }

        override fun onAnimationRepeat(animation: Animator?) {

        }
    }
}