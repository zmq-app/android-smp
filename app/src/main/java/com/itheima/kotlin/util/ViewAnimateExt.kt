package com.itheima.kotlin.util

import android.view.View

fun View.scale(scaleX: Float, scaleY: Float, duration: Long) {
    this.animate()
        .scaleX(scaleX)
        .scaleY(scaleY)
        .setDuration(duration)
        .start()
}