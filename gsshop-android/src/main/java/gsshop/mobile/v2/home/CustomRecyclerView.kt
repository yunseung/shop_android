/*
 * Copyright(C) 2013 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import gsshop.mobile.v2.home.shop.BaseViewHolder
import gsshop.mobile.v2.util.SwipeUtils

/**
 * 매장 커스텀 리사이클러뷰
 * -매장내 캐로셀 스와이프 시 수평축(x축) 기준으로 각도를 확인함
 * -허용각도내에 포함되는 경우 이벤트를 무조건 캐로셀로 보냄
 */
class CustomRecyclerView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : RecyclerView(context, attrs, defStyleAttr) {

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        if (SwipeUtils.isSwipeAngleRange(context, e)) {

            findChildViewUnder(e.x, e.y)?.let {
                getChildViewHolder(it)?.let { me ->
                    if (me is BaseViewHolder && me.increaseSwipeAngle) {
                        //뷰홀더 내 리사이클뷰에 이벤트를 그대로 전달하여 매장 상하스크롤이 아닌 뷰홀더 스크롤로 사용함
                        return false
                    }
                }
            }
        }

        return super.onInterceptTouchEvent(e)
    }
}

