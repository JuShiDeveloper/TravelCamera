package com.jushi.library.base

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.jushi.library.systemBarUtils.SystemBarUtil
import com.jushi.library.utils.ToastUtils

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
    }

    private fun initialize() {
        setPageLayout()
        initData()
        initWidget()
        initAnimator()
        setOnViewListener()
    }

    /**
     * 设置页面布局文件
     */
    abstract fun setPageLayout()

    /**
     * @param layoutResId 布局文件资源id
     * @param isFitsSystemWindows 是否设置沉浸式状态栏
     * @param isTranslucentSystemBar 是否设置状态栏透明
     */
    fun setContentView(layoutResId: Int, isFitsSystemWindows: Boolean, isTranslucentSystemBar: Boolean) {
        setSystemBarStatus(isFitsSystemWindows, isTranslucentSystemBar)
        setContentView(layoutResId)
    }

    /**
     * 设置状态栏状态 （必须在setContentView()方法之前调用才有效）
     * @param isFitsSystemWindows 是否沉浸式状态栏
     * @param isTranslucentSystemBar 是否透明状态栏
     */
    fun setSystemBarStatus(isFitsSystemWindows: Boolean, isTranslucentSystemBar: Boolean) {
        SystemBarUtil.setRootViewFitsSystemWindows(this, isFitsSystemWindows)
        if (isTranslucentSystemBar) {
            SystemBarUtil.setTranslucentStatus(this)
        }
    }

    /**
     * 如果设置沉浸式状态栏，如果view的父控件是 LinearLayout 则调用该方法
     *@param view 显示在状态栏位置的View
     */
    fun setSystemBarViewLayoutParamsL(view: View) {
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, SystemBarUtil.getStatusBarHeight(this))
        view.layoutParams = params
    }

    /**
     * 如果设置沉浸式状态栏，如果view的父控件是RelativeLayout则调用该方法
     *@param view 显示在状态栏位置的View
     */
    fun setSystemBarViewLayoutParamsR(view: View) {
        val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, SystemBarUtil.getStatusBarHeight(this))
        view.layoutParams = params
    }

    /**
     * 用作初始化数据
     */
    abstract fun initData()

    /**
     * 初始化组建（根据需要选择是否重写）
     */
    open fun initWidget() {

    }

    /**
     * 初始化动画
     */
    open fun initAnimator(){

    }

    /**
     * 设置点击事件
     */
    abstract fun setOnViewListener()

    /**
     * activity跳转（不带数据跳转）
     * @param cls 目标activity
     */
    fun startActivity(cls: Class<*>) {
        var intent = Intent(this, cls)
        startActivity(intent)
    }

    /**
     * activity跳转（携带数据跳转）
     * @param cls 目标activity
     * @param bundle 携带数据的 Bundle对象
     */
    fun startActivity(cls: Class<*>, bundle: Bundle) {
        var intent = Intent(this, cls)
        intent.putExtra(cls.name, bundle)
        startActivity(intent)
    }


    fun showToast(msg: Any) {
        ToastUtils.showToast(this, msg.toString())
    }

    fun showToastAtLocation(msg: Any, gravity: Int) {
        ToastUtils.showToastAtLocation(this, msg.toString(), gravity)
    }
}