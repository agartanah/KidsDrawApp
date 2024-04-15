package com.bignerdranch.android.kidsdrawapp

import android.app.Application
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class DrawViewModel(application: Application) : AndroidViewModel(application) {
    val drawingView = MutableLiveData<DrawingView>()

    fun setDrawingView(view: DrawingView) {
        drawingView.value = view
    }

    fun setBackGround(bitmap: Bitmap) {
        drawingView.value?.setBackGround(bitmap)
    }

    fun setBrushSize(size: Float) {
        drawingView.value?.setBrushSize(size)
    }

    fun setColor(color: Int) {
        drawingView.value?.setColor(color)
    }

    fun getBitMap() : Bitmap? {
        return drawingView.value?.getBitMap()
    }
}