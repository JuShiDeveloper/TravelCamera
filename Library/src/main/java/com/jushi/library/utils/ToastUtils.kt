package com.jushi.library.utils

import android.content.Context
import android.widget.TextView
import android.widget.Toast
import com.jushi.library.R

object ToastUtils {

    fun showToast(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    fun showToastAtLocation(context: Context, msg: String, gravity: Int) {
        val view = TextView(context)
        view.textSize = 20f
        view.setTextColor(context.resources.getColor(R.color.color_theme))
        view.text = msg
        val toast = Toast(context)
        toast.setGravity(gravity, 0, 0)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = view
        toast.show()
    }
}