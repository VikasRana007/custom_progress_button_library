@file:Suppress("DEPRECATION")

package com.me.myprogressbutton

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton

/**
 * Created by Vikas Rana 18/05/2023.
 */
class CProgressButton @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
     AppCompatButton(context!!, attrs, defStyleAttr), ProgressListener {
    private var mBackground: Drawable? = null
    private var mProgressDrawable: CProgressDrawable? = null
    private var mWidth = 0
    private var mHeight = 0
    var mState = STATE.NORMAL
    private var morphingCircle = false
    private var morphingNormal = false
    private var mFromCornerRadius = 40f
    private val mToCornerRadius = 90f
    private val mDuration: Long = 500
    private var mProgress = 0
    private val mMaxProgress = 100
    private var mStrokeColor = 0
    private var mStokeWidth = 0
    private var mStokeWidthOut = 0
    private var resultString: String? = null



    init {
        val a = getContext().theme.obtainStyledAttributes(
            attrs,
            R.styleable.CProgressButton,
            0, 0
        )
        try {
            mStrokeColor = a.getInteger(R.styleable.CProgressButton_color, -1)
            mBackground = a.getDrawable(R.styleable.CProgressButton_drawable_xml)
            mStokeWidthOut = a.getDimension(R.styleable.CProgressButton_stroke_width, -1f).toInt()
            mFromCornerRadius =
                a.getDimension(R.styleable.CProgressButton_radius, -1f).toInt().toFloat()
        } finally {
            a.recycle()
        }
        if (mStrokeColor == -1) {
            mStrokeColor = resources.getColor(R.color.black)
        }
        if (mBackground == null) {
            throw NullPointerException("drawable_xml can not be null")
        }
        if (mStokeWidthOut == -1) {
            mStokeWidthOut = dip2px(getContext(), 1f)
        }
        if (mFromCornerRadius == -1f) {
            throw NullPointerException("radius must can not be null")
        }
        mStokeWidth = mStokeWidthOut * 3
        normal(0)
    }

    fun getState(): STATE? {
        return mState
    }

     fun setState(state: STATE, anim: Boolean) {
        if (state == this.mState) {
            if (state == STATE.NORMAL) {
                text = resultString
            }
            return
        }
        if (width == 0 || morphingCircle || morphingNormal) return
        this.mState = state
        if (anim) {
            if (this.mState == STATE.PROGRESS) {
                morph2Circle()
            } else if (this.mState == STATE.NORMAL) {
                morph2Normal()
            }
        } else {
            morphingNormal = false
            morphingCircle = morphingNormal
            if (this.mState == STATE.PROGRESS) {
                text = ""
            } else if (this.mState == STATE.NORMAL) {
                text = resultString
            }
            setBound(0)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = width - paddingLeft - paddingRight
        mHeight = height - paddingTop - paddingRight
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w - paddingLeft - paddingRight
        mHeight = h - paddingTop - paddingRight
        if (mState == STATE.NORMAL || mState == STATE.PROGRESS && morphingCircle) {
            setBound(0)
        } else {
            invalidate()
        }
    }

    private fun setBound(padding: Int) {
        if (mWidth == 0) {
            mWidth = width - paddingLeft - paddingRight
        }
        if (mHeight == 0) {
            mHeight = height - paddingTop - paddingRight
        }
        mBackground!!.setBounds(
            paddingLeft + padding,
            paddingTop, paddingLeft + mWidth - padding, paddingTop + mHeight
        )
        invalidate()
    }

    private fun setProgress(progress: Int) {
        mProgress = progress
        if (morphingCircle || morphingNormal) return
        if (mState != STATE.PROGRESS) {
            mState = STATE.PROGRESS
            text = ""
        }
        if (mProgress >= mMaxProgress) {
            mProgress = mMaxProgress
        }
        if (mProgress <= 0) {
            mProgress = 0
        }
        setBound(0)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mState == STATE.NORMAL || mState == STATE.PROGRESS && morphingCircle) {
            mBackground!!.draw(canvas)
        } else if (mState == STATE.PROGRESS && !morphingCircle) {
            if (mProgressDrawable == null) {
                val offset = (mWidth - mHeight) / 2 + paddingLeft
                val size = mHeight
                mProgressDrawable =
                    CProgressDrawable(context, size, mStokeWidth, mStokeWidthOut, mStrokeColor)
                mProgressDrawable!!.setBounds(
                    offset,
                    paddingTop,
                    offset + mHeight,
                    paddingTop + mHeight
                )
            }
            val sweepAngle = 360f / mMaxProgress * mProgress
            mProgressDrawable!!.setSweepAngle(sweepAngle)
            mProgressDrawable!!.draw(canvas)
        }
    }
    @SuppressLint("ObjectAnimatorBinding")
    private fun morph2Normal() {
        val cornerAnimation =
            ObjectAnimator.ofFloat(mBackground, "cornerRadius", mToCornerRadius, mFromCornerRadius)
        val start = (mWidth - mHeight) / 2
        val animator = ValueAnimator.ofInt(start, 0)
        animator.setDuration(mDuration)
            .addUpdateListener { animation ->
                val value = animation.animatedValue as Int
                setBound(value)
            }
        val animatorSet = AnimatorSet()
        animatorSet.duration = mDuration
        animatorSet.playTogether(animator, cornerAnimation)
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                morphingNormal = true
            }

            override fun onAnimationEnd(animation: Animator) {
                morphingNormal = false
                text = resultString
            }

            override fun onAnimationCancel(animation: Animator) {
                morphingNormal = false
            }

            override fun onAnimationRepeat(animation: Animator) {}
        })
        animatorSet.start()
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun morph2Circle() {
        val cornerAnimation =
            ObjectAnimator.ofFloat(mBackground, "cornerRadius", mFromCornerRadius, mToCornerRadius)
        val end = (mWidth - mHeight) / 2
        val animator = ValueAnimator.ofInt(0, end)
        animator.setDuration(mDuration)
            .addUpdateListener { animation ->
                val value = animation.animatedValue as Int
                setBound(value)
            }
        val animatorSet = AnimatorSet()
        animatorSet.duration = mDuration
        animatorSet.playTogether(animator, cornerAnimation)
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                text = ""
                morphingCircle = true
            }

            override fun onAnimationEnd(animation: Animator) {
                text = ""
                morphingCircle = false
            }

            override fun onAnimationCancel(animation: Animator) {
                morphingCircle = false
            }

            override fun onAnimationRepeat(animation: Animator) {}
        })
        animatorSet.start()
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val savedState = SavedState(superState)
        savedState.mProgress = mProgress
        savedState.morphingNormal = morphingNormal
        savedState.morphingCircle = morphingCircle
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is SavedState) {
            val savedState = state
            mProgress = savedState.mProgress
            morphingNormal = savedState.morphingNormal
            morphingCircle = savedState.morphingCircle
            super.onRestoreInstanceState(savedState.superState)
            setProgress(mProgress)
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    internal class SavedState : BaseSavedState {
        var morphingNormal = false
        var morphingCircle = false
        var mProgress = 0

        constructor(parcel: Parcelable?) : super(parcel) {}
        private constructor(`in`: Parcel) : super(`in`) {
            mProgress = `in`.readInt()
            morphingCircle = `in`.readInt() == 1
            morphingNormal = `in`.readInt() == 1
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(mProgress)
            out.writeInt(if (morphingNormal) 1 else 0)
            out.writeInt(if (morphingCircle) 1 else 0)
        }

        companion object {
            @JvmField
            val CREATOR: Creator<SavedState?> = object : Creator<SavedState?> {
                override fun createFromParcel(`in`: Parcel): SavedState {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }

        override fun describeContents(): Int {
            return 0
        }
    }

    override fun download(progress: Int) {
        setProgress(progress)
    }

    fun normal(status: Int, anim: Boolean = true) {
        resultString = statusString[status]
        setState(STATE.NORMAL, anim)
    }

    override fun normal(status: Int) {

    }

    override fun startDownLoad() {
        resultString = ""
        setState(STATE.PROGRESS, true)
    }

    companion object {
        private var statusString = arrayOf("download", "pause", "complete", "error", "delete")
        private const val TAG = "CProgressButton"

        /**
         * order by yourself
         *
         * @param status
         */
        fun initStatusString(status: Array<String>?) {
            if (status != null && status.isNotEmpty()) {
                statusString = status
            }
        }

        fun dip2px(context: Context, dpValue: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (dpValue * scale + 0.5f).toInt()
        }
    }
}