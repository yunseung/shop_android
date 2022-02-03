package gsshop.mobile.v2.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import roboguice.util.Ln

/**
 * 텍스트 뷰에 지정된 색상의 굵은 선을 그려주는 뷰
 */
class TextViewBackLine
@JvmOverloads
constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : AppCompatTextView(context, attrs, defStyleAttr) {
    var mIsLineOn = false

    private var mColor:String? = null

    private var mLisnter: OnDrawListener? = null

    interface OnDrawListener {
        fun onDraw(view: View?)
    }

    fun setOnDrawListener(listener: OnDrawListener) {
        mLisnter = listener
    }

    override fun onDraw(canvas: Canvas) {

        if (mIsLineOn) {
            val paint = Paint()
            paint.style = Paint.Style.FILL

            var strColor:String? = "#dbf109"
            if (mColor != null) {
                strColor = "#" + mColor
            }
            try {
                paint.color = Color.parseColor(strColor)
            }
            catch (e: IllegalArgumentException) {
                Ln.e(e.message)
                paint.color = Color.parseColor("#dbf109")
            }
            canvas.drawRect(0f, canvas.height / 3 * 2 - 3.toFloat(), canvas.width.toFloat(), canvas.height - 3.toFloat(), paint)
        }

        super.onDraw(canvas)

        if (mLisnter != null) {
            mLisnter!!.onDraw(this)
        }
    }

    fun setTextLine(isLineOn: Boolean) {
        mIsLineOn = isLineOn
    }

    fun setTextLineColor(color: String?) {
        mColor = color
    }

}