/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible.wine

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import gsshop.mobile.v2.MainApplication
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.shop.BaseViewHolder
import gsshop.mobile.v2.home.shop.ShopInfo
import gsshop.mobile.v2.home.shop.flexible.wine.common.RecyclerAdapterWineHorizontal
import gsshop.mobile.v2.util.SwipeUtils
import kotlinx.android.synthetic.main.view_holder_prd_common_recycler_with_txt_title.view.*

/**
 * 리뉴얼 된 홀더는 모두 새로 CP 로 생성.
 */
@SuppressLint("NewApi")
class PrdCSqListWineVH(itemView: View) : BaseViewHolder(itemView) {

    var mItemDecoration = SpacesItemDecoration()

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(context: Context?, position: Int, info: ShopInfo?,
                                  action: String?, label: String?, sectionName: String?) {
        val item = info?.contents?.get(position)?.sectionContent ?: return

        if(!TextUtils.isEmpty(item.name)) {
            itemView.txt_main.text = item.name
        }

        val recycler = itemView.rootView.list_recommend

        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL

        recycler.layoutManager = layoutManager
        recycler.adapter = RecyclerAdapterWineHorizontal(
                context!!, item.subProductList!!, R.layout.banner_item_prd_cs_q_list_wine, true)

        recycler.removeItemDecoration(mItemDecoration)
        recycler.addItemDecoration(mItemDecoration)

        recycler.setOnTouchListener { _: View, event: MotionEvent ->
            when(event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> SwipeUtils.disableSwipe()
            }
            false
        }

    }

    override fun setOnTouchUp() {
        for (i in 0 until itemView.list_recommend.childCount) {
            itemView.list_recommend.getChildAt(i).scaleX = 1f
            itemView.list_recommend.getChildAt(i).scaleY = 1f
        }
    }

    /**
     * 아이템간격을 데코레이션 한다.
     */
    inner class SpacesItemDecoration : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val position = parent.getChildAdapterPosition(view)
            if (position == 0) {
                outRect.left = DIVIDER_SPACE_01
            }
            if (position == parent.adapter!!.itemCount - 1) {
                outRect.right = DIVIDER_SPACE_01
            } else {
                outRect.right = DIVIDER_SPACE_02
            }
        }
    }

    companion object {
        private val DIVIDER_SPACE_01 = MainApplication.getAppContext().resources.getDimensionPixelSize(R.dimen.renewal_vh_blank_space_side)
        private val DIVIDER_SPACE_02 = MainApplication.getAppContext().resources.getDimensionPixelSize(R.dimen.renewal_vh_blank_space_between)
    }

    init {
        //스와이프 허용각도 확대 적용
        increaseSwipeAngle = true
    }
}