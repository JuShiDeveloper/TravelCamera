package com.jushi.photo.compress.main.camera.editPicture.utils

import travel.camera.photo.compress.R

/**
 * 贴纸数据工具类
 */
object TagsUtil {

    fun getTags(): List<Int> {
        val tagsList = ArrayList<Int>()
        tagsList.add(R.drawable.dog_1)
        tagsList.add(R.drawable.dog_2)
        tagsList.add(R.drawable.dog_3)
        tagsList.add(R.drawable.dog_4)
        tagsList.add(R.drawable.dog_5)
        tagsList.add(R.drawable.dog_6)
        tagsList.add(R.drawable.bird)
        return tagsList
    }
}