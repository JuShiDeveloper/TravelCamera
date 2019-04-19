package com.jushi.photo.compress.main.camera.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class CameraGrid : View {
    private var topBannerWidth = 0
    private var paint: Paint = Paint()
    var isShowGrid = true

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        paint.color = Color.WHITE
        paint.alpha = 120
        paint.strokeWidth = 1f
    }

    /**
     * 画网格
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        var width = width
        var height = height
        if (width < height) {
            topBannerWidth = height - width
        }
        if (isShowGrid) {
            //画第一根竖线
            canvas.drawLine((width / 3).toFloat(), 0f, (width / 3).toFloat(), height.toFloat(), paint)
            //画第二根竖线
            canvas.drawLine((width * 2 / 3).toFloat(), 0f, (width * 2 / 3).toFloat(), height.toFloat(), paint)
            //画第一根横线
            canvas.drawLine(0f, (height / 5).toFloat(), width.toFloat(), (height / 5).toFloat(), paint)
            //画第二根横线
            canvas.drawLine(0f, (height * 2 / 5).toFloat(), width.toFloat(), (height * 2 / 5).toFloat(), paint)
            //画第三根横线
            canvas.drawLine(0f, (height * 3 / 5).toFloat(), width.toFloat(), (height * 3 / 5).toFloat(), paint)
            //画第四根横线
            canvas.drawLine(0f, (height * 4 / 5).toFloat(), width.toFloat(), (height * 4 / 5).toFloat(), paint)
        }
    }


}