package com.bignerdranch.android.kidsdrawapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    private lateinit var mDrawPath : CustomPath // траектория движения маркера
    private lateinit var mCanvasBitmap : Bitmap // Побитовая разметка области рисования
    private var mBackgroundBitMap : Bitmap? = null
    private lateinit var mDrawPaint : Paint // Отвечает за хранение характеристик
    private lateinit var mCanvasPaint : Paint // Отвечает за хранение характеристик области рисования
    private var mBrushSize : Float // отвечает за размер маркера
    private var color : Int // цвет маркера
    private lateinit var canvas : Canvas // отвечает за инициализацию графической области рисования

    private var mPath = ArrayList<CustomPath>() // список нарисованных каракуль

    init {
        mBrushSize = 20f
        color = Color.BLACK
        setUpDrawing()
    }

    internal class CustomPath(color: Int, brushThickness : Float) : Path() { }

    fun setBackGround(bitmap: Bitmap) {
        mBackgroundBitMap = Bitmap.createScaledBitmap(bitmap, canvas.width, canvas.height, true)
        invalidate()
    }

    fun setColor(colorOut: Int) {
        color = colorOut
        mDrawPaint.color = color
    }

    fun setBrushSize(size: Float) {
        mBrushSize = size
        mDrawPaint.strokeWidth = mBrushSize
    }

    fun getBitMap() : Bitmap {
        val mergedBitmap = Bitmap.createBitmap(
            mCanvasBitmap.getWidth(),
            mCanvasBitmap.getHeight(),
            mCanvasBitmap.getConfig()
        )

        val canvasBitMap = Canvas(mergedBitmap)
        canvasBitMap.drawBitmap(mCanvasBitmap, Matrix(), null)

        canvasBitMap.drawBitmap(mBackgroundBitMap!!, matrix, null)
        canvasBitMap.drawBitmap(mCanvasBitmap, Matrix(), null)

        return mergedBitmap
    }

    fun setUpDrawing() {
        mDrawPath = CustomPath(color, mBrushSize)

        mDrawPaint = Paint()
        mDrawPaint.style = Paint.Style.STROKE
        mDrawPaint.color = color
        mDrawPaint.strokeJoin = Paint.Join.ROUND
        mDrawPaint.strokeCap = Paint.Cap.ROUND
        mDrawPaint.strokeWidth = mBrushSize

        mCanvasPaint = Paint()
        mCanvasPaint.flags = Paint.DITHER_FLAG
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(mCanvasBitmap)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.save()

        if (mBackgroundBitMap != null) {
            canvas.drawBitmap(mBackgroundBitMap!!, 0f, 0f, mCanvasPaint)
        }

        for (path in mPath) {
            canvas.drawPath(path, mDrawPaint)
        }

        canvas.drawBitmap(mCanvasBitmap, 0f, 0f, mCanvasPaint)
        canvas.restore()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val current = PointF(event.x, event.y)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mDrawPath = CustomPath(color, mBrushSize)

                mDrawPath.moveTo(current.x, current.y)
                mPath.add(mDrawPath)
            }
            MotionEvent.ACTION_MOVE -> {
                mDrawPath.lineTo(current.x, current.y)

            }
            MotionEvent.ACTION_UP -> {
                canvas.drawPath(mDrawPath, mDrawPaint)
                mDrawPath.reset()
            }
            else -> {
                return false
            }
        }

        invalidate()
        return true
    }
}

