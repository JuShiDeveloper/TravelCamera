package com.jushi.photo.compress.main.camera.editPicture

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import com.jushi.library.base.BaseActivity
import com.jushi.library.customView.dragScaleView.DragScaleView
import com.jushi.library.utils.FileUtil
import com.jushi.photo.compress.main.camera.editPicture.utils.TagsUtil
import com.jushi.photo.compress.main.camera.editPicture.view.LableSelector
import com.jushi.photo.compress.main.camera.editPicture.view.LableView
import com.jushi.photo.compress.main.utils.Constansts
import kotlinx.android.synthetic.main.activity_edit_picture_layout.*
import travel.camera.photo.compress.R

/**
 * 拍照/相册选择图片后编辑图片界面
 */
class EditPictureActivity : BaseActivity(), LableView.ShowLabelContentListener {
    private lateinit var imagePath: String
    private lateinit var lableView: LableView
    private val lableList = arrayListOf<LableView>()
    private lateinit var lableSelector: LableSelector
    private val MOOD_REQUEST_CODE = 0x001
    private val LOCATION_REQUEST_CODE = 0x002
    private lateinit var tagsList: List<Int> //贴纸数据
    private lateinit var tagsAdapter: TagsRecyclerViewAdapter

    override fun setPageLayout() {
        setContentView(R.layout.activity_edit_picture_layout, true, true)
    }

    override fun initData() {
        imagePath = FileUtil.getRealFilePathFromUri(this, intent.data as Uri)
        tagsList = TagsUtil.getTags()
    }

    override fun initWidget() {
        setSystemBarViewLayoutParamsR(edit_picture_system_bar)
        initGPUImageView()
        initRecyclerView()
        addLableView()
        addLableSelector()
    }

    private fun initGPUImageView() {
        edit_picture_GPUImageView.setImage(BitmapFactory.decodeFile(imagePath))
    }

    private fun initRecyclerView() {
        var layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        rv_edit_picture.layoutManager = layoutManager
        rv_edit_picture.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
                super.getItemOffsets(outRect, itemPosition, parent)
                outRect.set(15, 15, 15, 0)
            }
        })
        tagsAdapter = TagsRecyclerViewAdapter(tagsList, this)
        rv_edit_picture.adapter = tagsAdapter
        tagsAdapter.setOnItemClickListener(View.OnClickListener {
            addTagsView(tagsList[it.tag as Int])
        })
    }

    /**
     * 添加贴纸 view
     */
    private fun addTagsView(resId: Int) {
        var tagsView = View.inflate(this, R.layout.view_tags, null) as DragScaleView
//        var tagsView = RotateImageView(this)
        val params = RelativeLayout.LayoutParams(300, 300)
        params.addRule(RelativeLayout.CENTER_IN_PARENT)
        tagsView.layoutParams = params
        tagsView.scaleType = ImageView.ScaleType.FIT_CENTER
        tagsView.setImageResource(resId)
        rl_GPUImageView_area.addView(tagsView)
    }

    /**
     * 添加显示地点/心情的view
     */
    private fun addLableView() {
        lableView = LableView(this)
        var params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        if (lableList.size > 0) {
            params.rightMargin = lableList[lableList.size - 1].width
            params.bottomMargin = lableList[lableList.size - 1].height
        }
        params.addRule(RelativeLayout.CENTER_IN_PARENT)
        lableView.layoutParams = params
        lableView.setShowLabelContentListener(this)
        rl_GPUImageView_area.addView(lableView)
        lableList.add(lableView)
    }

    /**
     * 添加标签选择器
     */
    private fun addLableSelector() {
        lableSelector = LableSelector(this)
        var params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
        lableSelector.layoutParams = params
        lableSelector.visibility = View.GONE
        rl_GPUImageView_area.addView(lableSelector)
    }

    override fun setOnViewListener() {
        //返回按钮
        edit_picture_backBtn.setOnClickListener { finish() }
        //保存图片按钮
        tv_edit_picture_saveBtn.setOnClickListener { }
        //贴纸按钮
        tv_edit_picture_tie_zhi_Btn.setOnClickListener { }
        //滤镜按钮
        tv_edit_picture_lv_jing_Btn.setOnClickListener { }
        //标签按钮
        tv_edit_picture_biao_qian_Btn.setOnClickListener { }
        rl_GPUImageView_area.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    lableView.upLableViewLocation(event.x, event.y)
                    lableSelector.isShouldVisibility()
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener true
        }
        //设置标签选择器点击监听
        lableSelector.setLableSelectorOnClickListener(LableSelectorListener())
    }

    /**
     * 输入的标签内容显示成功
     */
    override fun labelContentShowSuccess() {
        lableSelector.isShouldVisibility()
        addLableView()
    }

    override fun onDestroy() {
        super.onDestroy()
        lableView.cancelWave()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            MOOD_REQUEST_CODE -> { //点击心情跳转返回
                if (resultCode == Activity.RESULT_OK) {
                    if (data == null) return
                    val inputContent = data.getStringExtra(Constansts.INPUT_LABEl_CONTENT)
                    lableView.showLabelContent(inputContent, LableSelector.LableType.MOOD)
                }
            }
            LOCATION_REQUEST_CODE -> { //地点跳转返回
                if (resultCode == Activity.RESULT_OK) {
                    if (data == null) return
                    val inputContent = data.getStringExtra(Constansts.INPUT_LABEl_CONTENT)
                    lableView.showLabelContent(inputContent, LableSelector.LableType.LOCATION)
                }
            }
        }
    }

    private inner class LableSelectorListener : View.OnClickListener {
        override fun onClick(v: View) {
            when (v.tag as LableSelector.LableType) {
                LableSelector.LableType.MOOD -> { //点击心情
                    startActivityForResult(InputLableContentActivity::class.java, MOOD_REQUEST_CODE)
                }
                LableSelector.LableType.LOCATION -> {  //点击地点
                    startActivityForResult(InputLableContentActivity::class.java, LOCATION_REQUEST_CODE)
                }
            }
        }
    }
}