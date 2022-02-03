package gsshop.mobile.v2.view

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import de.greenrobot.event.EventBus
import gsshop.mobile.v2.Events.EventProductDetail.NativeTouchEvent
import gsshop.mobile.v2.R
import roboguice.util.Ln
import java.util.*

class InterceptTouchFrameLayout(private var mContext: Context?, attrs: AttributeSet?, defStyleAttr: Int) : FrameLayout(mContext!!, attrs, defStyleAttr) {
    private var mNativeScroll: ScrollViewWithStopListener? = null
    private var mIsScrolling = false
    private var mIsWebPageFinished = false
    private var mThrowTouch = false

    /**
     * 기본 생성자
     *
     * @param context
     */
    constructor(context: Context?) : this(context, null) {
        mContext = context
    }

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0) {
        mContext = context
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        EventBus.getDefault().register(this)
        mNativeScroll = findViewById(R.id.native_scroll)
    }

    /**
     * 스크롤 중인지 액티비티로부터 받아오는 함수
     *
     * @param isScrolling scrolling 여부
     */
    fun setIsScrolling(isScrolling: Boolean) {
        mIsScrolling = isScrolling
    }

    fun onWebPageFinished(isWebPageFinished: Boolean) {
        mIsWebPageFinished = isWebPageFinished
    }

    fun onEvent(event: NativeTouchEvent) {
        mThrowTouch = event.throwTouch
    }

    private val mEvList = ArrayList<MotionEvent?>()
    private var mNewEvent: MotionEvent? = null
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        // yun 2020.04.23
        // 1. 터치가 들어왔을 때 이벤트를 저장해뒀다가 네이티브 영역 터치이면 30m/s 이후에 터치를 내린다.
        // 스크립트에서 네이티브 터치 콜백 타이밍과 실제 터치가 들어가는 타이밍을 로그로 찍어본 결과
        // 10m/s 내외이기 때문에 30m/s 로 여유롭게 둠.
        // 2. 웹 영역 터치이면 터치 내리지 않고 웹에서 터치 먹어버린다.
        // 3. 페이지 로딩 이전에는 자연스러운 터치로 동작.
        // 특정 단말, 특정 상황에서 발생하는 IllegalArgumentException: pointerIndex out of range 를 방어하기 위한 예외처리.
        try {
            if (mIsWebPageFinished) {
                mNewEvent = MotionEvent.obtain(ev)
                mEvList?.add(mNewEvent)
                mNewEvent = null
                Handler().postDelayed({
                    if (mThrowTouch && !mIsScrolling) {
                        for (motionEvent in mEvList) {
                            mNativeScroll?.dispatchTouchEvent(motionEvent!!)
                        }
                        mEvList?.clear()
                    } else {
                        mEvList?.clear()
                        mThrowTouch = false
                    }
                }, 30)
            } else {
                mNativeScroll?.dispatchTouchEvent(ev)
            }
        } catch (e: IllegalArgumentException) {
            Ln.e(e.message)
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        EventBus.getDefault().unregister(this)
    }
}