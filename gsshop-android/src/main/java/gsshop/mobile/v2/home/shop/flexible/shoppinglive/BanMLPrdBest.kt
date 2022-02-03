/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible.shoppinglive

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import gsshop.mobile.v2.MainApplication
import gsshop.mobile.v2.home.main.SectionContentList
import gsshop.mobile.v2.home.shop.BaseViewHolder
import gsshop.mobile.v2.home.shop.ShopInfo
import gsshop.mobile.v2.util.DisplayUtils.convertDpToPx
import gsshop.mobile.v2.util.SwipeUtils.disableSwipe
import kotlinx.android.synthetic.main.view_holder_prd_common_recycler.view.*
import roboguice.util.Ln

/**
 * 기존 CST SQ 기반으로 만듦
 */
@SuppressLint("NewApi")
class BanMLPrdBest(itemView: View) : BaseViewHolder(itemView) {
    private val mRecyclerListCommand: RecyclerView
    // 리스트 좌우 간격을 맞추기 위한 데코레이션
    private var mItemDecoration: SpacesItemDecoration = SpacesItemDecoration()

    /**
     * 레이아웃 매니저
     */
    private var mLayoutManager: LinearLayoutManager? = null
    // context
    private var mContext: Context? = null

    // 초기화
    init {
        //스와이프 허용각도 확대 적용
        increaseSwipeAngle = true
        mRecyclerListCommand = itemView.recycler
    }

    // 상수 선언
    companion object {
        // 좌우 끝 간격
        private val DIVIDER_SPACE_01 = convertDpToPx(MainApplication.getAppContext(), 16f)

        // 리스트 사이 간격
        private val DIVIDER_SPACE_02 = convertDpToPx(MainApplication.getAppContext(), 14f)
    }

    /*
     * bind
     */
    override fun onBindViewHolder(context: Context, position: Int, info: ShopInfo?,
                                  action: String?, label: String?, sectionName: String?) {
        val item = info?.contents?.get(position)?.sectionContent ?: return

        mContext = context
        setView(context, item)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setView(context: Context, item: SectionContentList) {
        if (item.subProductList == null) {
            return
        }

        try {
            mRecyclerListCommand.removeItemDecoration(mItemDecoration)
            mRecyclerListCommand.addItemDecoration(mItemDecoration)

            mRecyclerListCommand.setHasFixedSize(true)
            mLayoutManager = LinearLayoutManager(context)
            mLayoutManager!!.orientation = LinearLayoutManager.HORIZONTAL
            mRecyclerListCommand.layoutManager = mLayoutManager

            val adapter = AdapterMLPrdBest(context, item.subProductList!!)

            mRecyclerListCommand.adapter = adapter
            mRecyclerListCommand.setOnTouchListener { v: View?, event: MotionEvent ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> disableSwipe()
                    MotionEvent.ACTION_UP -> setOnTouchUp()
                }
                false
            }
        }
        catch (e:NullPointerException) {
            Ln.e(e.message)
        }
    }

    override fun setOnTouchUp() {
        for (i in 0 until mRecyclerListCommand.childCount) {
            mRecyclerListCommand.getChildAt(i).scaleX = 1f
            mRecyclerListCommand.getChildAt(i).scaleY = 1f
        }
    }

    /**
     * 아이템간격을 데코레이션 한다.
     */
    inner class SpacesItemDecoration : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            try {
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
            catch (e:NullPointerException) {
                Ln.e(e.message)
            }
        }
    }
}