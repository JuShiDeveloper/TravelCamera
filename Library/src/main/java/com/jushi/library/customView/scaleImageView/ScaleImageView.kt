package com.jushi.library.customView.scaleImageView

import android.content.Context
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.support.v4.view.ViewPager
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import android.view.*

/**
 * @author jushi
 *
 * careate time：2019/04/24
 *
 * 功能：
 * 1、手势缩放图片
 * 2、放大后单指触摸拖动图片
 * 3、双击放大缩小
 */
class ScaleImageView : AppCompatImageView, ScaleGestureDetector.OnScaleGestureListener,
        View.OnTouchListener, ViewTreeObserver.OnGlobalLayoutListener {
    /**--------手指控制图片缩放的变量------------**/
    private var mOnce = false
    //初始化时缩放的值
    private var initScale = 0.0f
    //双击放大时到达的值
    private var midScale = 0.0f
    //放大的最大值
    private var maxScale = 0.0f
    private lateinit var mScaleMatrix: Matrix
    //多点触控
    private lateinit var mScaleGestureDetector: ScaleGestureDetector

    /**--------移动相关的变量-----------------------------------**/
    //记录上一次触控点的数量
    private var lastPointCount = 0
    //最后一次的位置
    private var lastX = 0.0f
    private var lastY = 0.0f
    private var touchSlop = 0
    private var isCanDrag = false
    private var isCheckLeftAndRight = false
    private var isCheckTopAndBottom = false
    /**-----------双击放大缩小的变量----------------------------**/
    private lateinit var mGestureDetector: GestureDetector
    private var isAutoScale = false
    private var clickListener: View.OnClickListener? = null

    init {
        mScaleMatrix = Matrix()
        scaleType = ScaleType.FIT_CENTER
        mScaleGestureDetector = ScaleGestureDetector(context, this)
        setOnTouchListener(this)
        touchSlop = ViewConfiguration.get(context).scaledTouchSlop
        mGestureDetector = GestureDetector(context, GestureListener())
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    /**
     * 如果在同一个页面使用同一个ScaleImageView显示不同的图片，
     * 则在设置图片之前须先调用此方法
     */
    fun reset() {
        mScaleMatrix = Matrix()
        scaleType = ScaleType.FIT_CENTER
        mScaleGestureDetector = ScaleGestureDetector(context, this)
        setOnTouchListener(this)
        touchSlop = ViewConfiguration.get(context).scaledTouchSlop
        mGestureDetector = GestureDetector(context, GestureListener())
        initScale = 0.0f
        maxScale = 0.0f
        touchSlop = 0
        lastPointCount = 0
        mOnce = false
        isAutoScale = false
        isCanDrag = false
        isCheckLeftAndRight = false
        isCheckTopAndBottom = false
    }

    override fun setOnClickListener(l: OnClickListener) {
        super.setOnClickListener(l)
        this.clickListener = l
    }

    /**
     * 获取ImageView控件加载完成的图片
     */
    override fun onGlobalLayout() {
        if (mOnce) return
        var drawable: Drawable? = drawable ?: return
        var drawableWidth = drawable!!.intrinsicWidth
        var drawableHeight = drawable.intrinsicHeight
        var scale = 1.0f
        if (drawableWidth > width && drawableHeight < height) {
            scale = width * 1.0f / drawableWidth
        }
        if (drawableWidth < width && drawableHeight > height) {
            scale = height * 1.0f / drawableHeight
        }
        if ((drawableWidth > width && drawableHeight > height)) {
            scale = Math.min(width * 1.0f / drawableWidth, height * 1.0f / drawableHeight)
        }
        if (drawableWidth < width && drawableHeight < height) {
            scale = Math.min(width * 1.0f / drawableWidth, height * 1.0f / drawableHeight)
        }
        initScale = scale
        midScale = scale * 2
        maxScale = scale * 4
        var dx = width / 2 - drawableWidth / 2
        var dy = height / 2 - drawableHeight / 2
        mScaleMatrix.postTranslate(dx.toFloat(), dy.toFloat())
        mScaleMatrix.postScale(initScale, initScale, (width / 2).toFloat(), (height / 2).toFloat())
        scaleType = ScaleType.MATRIX
        imageMatrix = mScaleMatrix
        mOnce = true
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        var scale = getCurrentScale()
        //获得手指触控后的缩放值
        var scaleFactor = detector.scaleFactor
        if (drawable == null) return true
        //放大或缩小的缩放范围控制
        if ((scale < maxScale && scaleFactor > 1.0f) || (scale > initScale && scaleFactor < 1.0f)) {
            if (scale * scaleFactor < initScale) {
                scaleFactor = initScale / scale
            }
            if (scale * scaleFactor > maxScale) {
                scaleFactor = maxScale / scale
            }
        }
        mScaleMatrix.postScale(scaleFactor, scaleFactor, detector.focusX, detector.focusY)
        checkBorderWhenScale()
        imageMatrix = mScaleMatrix
        return true
    }

    override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector?) {

    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (mGestureDetector.onTouchEvent(event)) {
            return true
        }
        mScaleGestureDetector.onTouchEvent(event)
        var x = 0.0f
        var y = 0.0f
        //获得触控屏幕点的数量
        var pointCount = event.pointerCount
        for (i in 0 until pointCount) {
            x += event.getX(i)
            y += event.getY(i)
        }
        x /= pointCount
        y /= pointCount
        if (lastPointCount != pointCount) {
            isCanDrag = false
            lastX = x
            lastY = y
        }
        lastPointCount = pointCount
        var rect = getMatrixRectF()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (rect.width() > width + 0.01 || rect.height() > height + 0.01) {
                    if (parent is ViewPager) {
                        //父控件禁止拦截当前view的触摸事件
                        parent.requestDisallowInterceptTouchEvent(true)
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (rect.width() > width + 0.01 || rect.height() > height + 0.01) {
                    if (parent is ViewPager) {
                        //父控件禁止拦截当前view的触摸事件
                        parent.requestDisallowInterceptTouchEvent(true)
                    }
                }
                var dx = x - lastX
                var dy = y - lastY
                if (!isCanDrag) {
                    isCanDrag = isMoveAction(dx, dy)
                }
                if (isCanDrag) {
                    isCheckLeftAndRight = true
                    isCheckTopAndBottom = true
                    if (rect.width() < width) {
                        isCheckLeftAndRight = false
                        dx = 0f
                    }
                    if (rect.height() < height) {
                        isCheckTopAndBottom = false
                        dy = 0f
                    }
                    mScaleMatrix.postTranslate(dx, dy)
                    checkBorderWhenTranslate()
                    imageMatrix = mScaleMatrix
                }
                lastX = x
                lastY = y
            }
            MotionEvent.ACTION_UP -> {
                lastPointCount = 0
            }
            MotionEvent.ACTION_CANCEL -> {
                lastPointCount = 0
            }
        }
        return true
    }

    /**
     * 有没有移动
     */
    private fun isMoveAction(dx: Float, dy: Float): Boolean {
        return Math.sqrt((dx * dx + dy * dy).toDouble()) > touchSlop
    }

    /**
     * 平移的时候检查边界
     */
    private fun checkBorderWhenTranslate() {
        var rect = getMatrixRectF()
        var dx = 0.0f
        var dy = 0.0f
        if (rect.top > 0 && isCheckTopAndBottom) {
            dy = -rect.top
        }
        if (rect.bottom < height && isCheckTopAndBottom) {
            dy = height - rect.bottom
        }
        if (rect.left > 0 && isCheckLeftAndRight) {
            dx = -rect.left
        }
        if (rect.right < width && isCheckLeftAndRight) {
            dx = width - rect.right
        }
        mScaleMatrix.postTranslate(dx, dy)
    }

    inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent): Boolean {
            if (isAutoScale) {
                return true
            }
            var x = e.x
            var y = e.y
            //如果当前的缩放大小小于2倍,放大
            isAutoScale = if (getCurrentScale() < midScale) {
                postDelayed(AutoScaleRunnable(midScale, x, y), 16)
                true
            } else { //如果当前缩放大小大于2倍，缩小
                postDelayed(AutoScaleRunnable(initScale, x, y), 16)
                true
            }
            return true
        }

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            clickListener?.onClick(this@ScaleImageView)
            return true
        }
    }

    /**
     * 自动缩放
     */
    inner class AutoScaleRunnable : Runnable {
        private var targetScale = 0.0f
        private var x = 0.0f
        private var y = 0.0f
        private val BIGGER = 1.07f
        private val SMALL = 0.93f
        private var tempScale = 0.0f

        constructor(targetScale: Float, x: Float, y: Float) {
            this.targetScale = targetScale
            this.x = x
            this.y = y
        }


        override fun run() {
            tempScale = if (getCurrentScale() < targetScale) BIGGER else SMALL
            mScaleMatrix.postScale(tempScale, tempScale, x, y)
            checkBorderWhenScale()
            imageMatrix = mScaleMatrix
            var currentScale = getCurrentScale()
            //继续缩放
            if ((tempScale > 1.0f && currentScale < targetScale) || (tempScale < 1.0f && currentScale > targetScale)) {
                postDelayed(this, 16)
            } else {
                var scale = targetScale / currentScale
                mScaleMatrix.postScale(scale, scale, x, y)
                checkBorderWhenScale()
                imageMatrix = mScaleMatrix
                isAutoScale = false
            }
        }
    }

    /**
     * 获得当前的缩放大小
     */
    private fun getCurrentScale(): Float {
        var values = FloatArray(9)
        mScaleMatrix.getValues(values)
        return values[Matrix.MSCALE_X]
    }


    /**
     * 缩放时检查边界及位置控制
     */
    private fun checkBorderWhenScale() {
        val rect = getMatrixRectF()
        var dx = 0.0f
        var dy = 0.0f
        if (rect.width() >= width) {
            if (rect.left > 0) {
                dx = -rect.left
            }
            if (rect.right < width) {
                dx = width - rect.right
            }
        }
        if (rect.height() >= height) {
            if (rect.top > 0) {
                dy = -rect.top
            }
            if (rect.bottom < height) {
                dy = height - rect.bottom
            }
        }
        if (rect.width() < width) {
            dx = width / 2f - rect.right + rect.width() / 2f
        }
        if (rect.height() < height) {
            dy = height / 2f - rect.bottom + rect.height() / 2f
        }
        mScaleMatrix.postTranslate(dx, dy)
    }

    private fun getMatrixRectF(): RectF {
        val rect = RectF()
        var d = drawable
        if (d != null) {
            rect.set(0f, 0f, d.intrinsicWidth.toFloat(), d.intrinsicHeight.toFloat())
            mScaleMatrix.mapRect(rect)
        }
        return rect
    }
}