package com.jushi.photo.compress.main

import android.Manifest
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.arcsoft.face.ErrorInfo
import com.arcsoft.face.FaceEngine
import com.jushi.library.base.BaseActivity
import com.jushi.library.takingPhoto.util.PermissionUtil
import com.jushi.photo.compress.main.camera.CameraActivity
import com.jushi.photo.compress.main.meiHua.MeiHuaActivity
import com.jushi.photo.compress.main.photoCompress.CompressActivity
import com.jushi.photo.compress.main.pinTu.PinTuActivity
import com.jushi.photo.compress.main.utils.Constansts
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import travel.camera.photo.compress.R

class MainActivity : BaseActivity() {
    private val REQUEST_PERMISSION_CODE = 0x001
    private val ACTION_REQUEST_PERMISSIONS = 0x002
    private val NEEDED_PERMISSIONS = arrayOf(Manifest.permission.READ_PHONE_STATE)

    override fun setPageLayout() {
        setContentView(R.layout.activity_main, true, true)
    }

    override fun initData() {
        requestPermission()
        activeEngine()
    }

    private fun requestPermission(): Boolean {
        return PermissionUtil.request(this, arrayOf(Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_PERMISSION_CODE)
    }

    /**
     * 激活人脸识别sdk
     */
    private fun activeEngine() {
//        var activeCode = -1
//        if (!checkPermissions(NEEDED_PERMISSIONS)) {
//            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS)
//            return
//        }
//        Thread(Runnable {
//            val faceEngine = FaceEngine()
//            activeCode = faceEngine.active(this@MainActivity, Constansts.APP_ID, Constansts.SDK_KEY)
//        }).start()
//        when (activeCode) {
//            ErrorInfo.MOK -> showToast(getString(R.string.active_success))
//            ErrorInfo.MERR_ASF_ALREADY_ACTIVATED -> showToast(getString(R.string.already_activated))
//            else -> showToast(getString(R.string.active_failed, activeCode))
//        }
    }

    private fun checkPermissions(neededPermissions: Array<String>?): Boolean {
        if (neededPermissions == null || neededPermissions.size == 0) {
            return true
        }
        var allGranted = true
        for (neededPermission in neededPermissions) {
            allGranted = allGranted and (ContextCompat.checkSelfPermission(this, neededPermission) == PackageManager.PERMISSION_GRANTED)
        }
        return allGranted
    }

    override fun initWidget() {
        setSystemBarViewLayoutParamsL(systemBar_View)
    }

    override fun setOnViewListener() {
        MeiHuaButton.setOnClickListener {
            //美化按钮点击事件
            startActivity(MeiHuaActivity::class.java)
        }
        PinTuButton.setOnClickListener {
            //拼图按钮
            startActivity(PinTuActivity::class.java)
        }
        CameraButton.setOnClickListener {
            if (requestPermission()) {
                //贴图相机按钮
                startActivity(CameraActivity::class.java)
            }
        }
        PhotoCompressButton.setOnClickListener {
            //图片压缩按钮
            startActivity(CompressActivity::class.java)
        }
    }
}
