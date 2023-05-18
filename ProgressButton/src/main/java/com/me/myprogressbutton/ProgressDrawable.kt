package com.me.myprogressbutton

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable


class CProgressDrawable constructor() : Drawable() {

//    private val TAG = "CircularProgressDrawable"
    private var mSweepAngle = 0f
    private var mStartAngle = 0f
    private var mSize = 0
    private var mStrokeWidth = 0
    private var mStrokeWidthOut = 0
    private var mStrokeColor = 0
    private var mPath : Path? = null
    private var mContext: Context? = null
    private var mMiddleRect: RectF? = null
    private var mRectF: RectF? = null
    private var mPaint: Paint? = null

    constructor(
        context: Context?,
        size: Int,
        strokeWidth: Int,
        stokenWidthOut: Int,
        strokeColor: Int,
    ) : this() {
        mSize = size
        mContext = context
        mStrokeWidthOut = stokenWidthOut
        mStrokeWidth = strokeWidth
        mStrokeColor = strokeColor
        mStartAngle = -90f
        mSweepAngle = 0f
    }


    fun setSweepAngle(sweepAngle : Float){
        mSweepAngle = sweepAngle
    }

    private fun getSize(): Int {
        return mSize
    }


    override fun draw(canvas: Canvas) {
       val bounds : Rect = bounds

        if(mPath == null){
            mPath = Path()
        }

        mPath!!.reset()
        getRect(mStrokeWidthOut)?.let { mPath!!.addArc(it,0f,360f) }
        mPath!!.offset(bounds.left.toFloat(),bounds.top.toFloat())
        createPaint(mStrokeWidthOut)?.let { canvas.drawPath(mPath!!, it) }

        mPath!!.reset()
        getRect(mStrokeWidthOut)?.let { mPath!!.addArc(it,mStartAngle,mSweepAngle) }
        mPath!!.offset(bounds.left.toFloat(),bounds.top.toFloat())
        createPaint(mStrokeWidth)?.let { canvas.drawPath(mPath!!,it) }

        mPath!!.reset()
        getRectInMiddle()?.let { mPath!!.addRect(it, Path.Direction.CCW) }
        mPath!!.offset(bounds.left.toFloat(),bounds.top.toFloat())
        createPaint(0)?.let { canvas.drawPath(mPath!!, it) }
    }


    private fun getRectInMiddle(): RectF? {
        val size = getSize()
        mMiddleRect = RectF(
            (size / 3).toFloat(),
            (size / 3).toFloat(),
            (size - size / 3).toFloat(),
            (size - size / 3).toFloat()
        )
        return mMiddleRect
    }

    override fun setAlpha(alpha: Int) {
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }

    @Deprecated("Deprecated in Java",
        ReplaceWith("PixelFormat.OPAQUE", "android.graphics.PixelFormat")
    )
    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }

    private fun getRect(stoken: Int): RectF? {
        val size = getSize()
        val index = stoken / 2
        mRectF = RectF(
            index.toFloat(),
            index.toFloat(),
            (size - index).toFloat(),
            (size - index).toFloat()
        )
        return mRectF
    }



    private fun createPaint(stokenWidth: Int): Paint? {
        if (mPaint == null) {
            mPaint = Paint()
            mPaint!!.isAntiAlias = true
            mPaint!!.color = mStrokeColor
        }
        if (stokenWidth == 0) {
            mPaint!!.style = Paint.Style.FILL
        } else {
            mPaint!!.style = Paint.Style.STROKE
            mPaint!!.strokeWidth = stokenWidth.toFloat()
        }
        return mPaint
    }

}