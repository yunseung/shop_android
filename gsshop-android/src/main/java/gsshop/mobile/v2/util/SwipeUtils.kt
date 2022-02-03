/*
 * Copyright(C) 2013 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.util

import android.content.Context
import android.os.Handler
import android.view.MotionEvent
import android.view.ViewConfiguration
import gsshop.mobile.v2.library.viewpager.InfiniteViewPager
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * 1.캐로셀 좌우 스와이프시 처음과 마지막 아이템에서 매장이동 안되도록 하는 기능
 * 2.캐로셀 좌우 스와이프 허용 각도를 확대 적용하는 기능
 */
object SwipeUtils {

    /**
     * 스와이프 허용 각도 정의
     */
    @JvmField
    val mSwipeAngle = Math.toRadians(70.0)

    /**
     * ACTION_DOWN 이벤트 저장
     */
    private var mLastEvent: MotionEvent? = null

    /**
     * 매장 플리킹 방지 플래그 원복
     */
    private val mHandler = Handler()
    private val mRunnable = Runnable {
        InfiniteViewPager.isHorizontalScrollDisallow = false
    }

    /**
     * 매장 플리킹 방지용 플래그를 활성화한다.
     */
    private fun enableSwipe() {
        mHandler.removeCallbacks(mRunnable)
        mHandler.postDelayed(mRunnable,1000)
    }

    /**
     * 매장 플리킹 방지용 플래그를 비활성화한다.
     */
    fun disableSwipe() {
        InfiniteViewPager.isHorizontalScrollDisallow = true
        enableSwipe()
    }

    /**
     * 사용자의 액션이 스와이프 허용각도에 포함되는지 확인한다.
     *
     * @param context Context
     * @param e MotionEvent
     * @return 허용각도인 경우 true 리턴
     */
    fun isSwipeAngleRange(context:Context, e: MotionEvent): Boolean {
        var result = false
        val action = e.action

        if (action == MotionEvent.ACTION_DOWN) {
            mLastEvent = MotionEvent.obtain(e)
        }

        mLastEvent?.let {
            if (action == MotionEvent.ACTION_MOVE) {
                //축별 이동거리
                val dx: Double = abs((e.x - it.x).roundToInt()).toDouble()
                val dy: Double = abs((e.y - it.y).roundToInt()).toDouble()

                //직선 이동거리 (d = R(x2+y2))
                val d = sqrt(dx * dx + dy * dy)

                //터치로 정의한 최소길이보다 크고 설정한 각도보다 작은 경우
                result = d > ViewConfiguration.get(context).scaledTouchSlop
                        && atan2(dy, dx) <= mSwipeAngle
            }
        }

        return result
    }
}