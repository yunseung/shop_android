package gsshop.mobile.v2.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView
import roboguice.util.Ln

class ScrollViewWithStopListener
// 자바에서는 생성자를 따로 선언했지만 간단하게 선언하기 위해 JvmOverloads를 써서 사용.
@JvmOverloads
constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ScrollView(context, attrs, defStyleAttr) {
    var onScrollFinishListener: OnScrollFinishListener? = null

    interface OnScrollFinishListener {
        // 스크롤이 멈췄다고 판단되었을 때에 호출
        fun onScrollFinished()

        // 스크롤이 마지막에 도착했을 때에 호출
        fun onScrollArrivedToEnd(scrollDirection: Int)
    }

    private var mIsFling = false
    override fun fling(velocityY: Int) {
        super.fling(velocityY)
        mIsFling = true
    }

    override fun onScrollChanged(x: Int, y: Int, oldX: Int, oldY: Int) {
        super.onScrollChanged(x, y, oldX, oldY)
        if (mIsFling) {
            if (Math.abs(y - oldY) < 2) {
                if (onScrollFinishListener != null) {
                    // 세이프 콜
                    onScrollFinishListener?.onScrollFinished()
                }
                mIsFling = false
            }
            if (onScrollFinishListener != null) {
                if (y == 0) {
                    onScrollFinishListener?.onScrollArrivedToEnd(SCROLL_TO_TOP)
                } else if (y >= measuredHeight) {
                    onScrollFinishListener?.onScrollArrivedToEnd(SCROLL_TO_BOTTOM)
                }
            }
        }
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        try {
            return super.onTouchEvent(ev)
        } catch (e: IllegalArgumentException) {
            Ln.e(e.message)
        }
        return false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        try {
            return super.onInterceptTouchEvent(ev)
        } catch (e: IllegalArgumentException) {
            Ln.e(e.message)
        }
        return false
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        try {
            return super.dispatchTouchEvent(ev)
        } catch (e: IllegalArgumentException) {
            Ln.e(e.message)
        }
        return false
    }

    companion object {
        var SCROLL_TO_TOP = 0
        var SCROLL_TO_BOTTOM = 1
    }
}