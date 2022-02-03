/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.ltype

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.EmptyUtils
import com.gsshop.mocha.ui.util.ViewUtils
import gsshop.mobile.v2.home.main.ModuleList
import gsshop.mobile.v2.home.main.SectionContentList
import gsshop.mobile.v2.home.shop.BaseViewHolderV2
import gsshop.mobile.v2.home.shop.ShopInfo
import gsshop.mobile.v2.home.shop.renewal.views.CommonTitleLayout
import kotlinx.android.synthetic.main.view_holder_ban_sld_gbg.view.*
import roboguice.util.Ln
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 시그니처매장 가로 캐로셀+
 */
class BanSldGbgVH(itemView: View) : BaseViewHolderV2(itemView) {

    private val mLlRoot: LinearLayout = itemView.ll_root
    private val mCommonTitleLayout: CommonTitleLayout = itemView.common_title_layout
    private val mProductRecyclerView: RecyclerView = itemView.recycler_list
    private val mViewBottomDivider1dp: View = itemView.view_bottom_divider_1dp
    private val mViewBottomDivider10dp: View = itemView.view_bottom_divider_10dp

    private val isAddedItemDecoration = AtomicBoolean(false)

    override fun onBindViewHolder(context: Context?, position: Int, info: ShopInfo?, action: String?, label: String?, sectionName: String?) {
        super.onBindViewHolder(context, position, info, action, label, sectionName)
        val item = info?.contents?.get(position)?.sectionContent
        if (item == null) {
            ViewUtils.hideViews(mLlRoot)
            return
        }

        ViewUtils.showViews(mLlRoot)
        mCommonTitleLayout.setCommonTitle(this, item)

        //아이템 간격 세팅
        if (isAddedItemDecoration.compareAndSet(false, true)) {
            try {
                for (i in 0 until mProductRecyclerView.itemDecorationCount) {
                    mProductRecyclerView.removeItemDecorationAt(i)
                }
            }
            catch (e:IndexOutOfBoundsException) {
                Ln.e(e.message)
            }
            mProductRecyclerView.addItemDecoration(SpacesItemDecoration())
        }

        mProductRecyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        mProductRecyclerView.layoutManager = layoutManager
        val adapter = BanSldGbgAdapter(context, item.subProductList!!)

        mProductRecyclerView.adapter = adapter
        setNextItemMargin(info, position)
    }

    override fun onBindViewHolder(context: Context, position: Int, moduleList: MutableList<ModuleList>?) {
        super.onBindViewHolder(context, position, moduleList)
        val varModuleList = moduleList?.get(position)

        if (varModuleList?.moduleList == null) {
            ViewUtils.hideViews(mLlRoot)
            return
        }

        val list: ArrayList<ModuleList> = varModuleList.moduleList!!
        ViewUtils.showViews(mLlRoot)
        mCommonTitleLayout.setCommonTitle(this, varModuleList)
        //아이템 간격 세팅
        if (isAddedItemDecoration.compareAndSet(false, true)) {
            mProductRecyclerView.addItemDecoration(SpacesItemDecoration())
        }
        mProductRecyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        mProductRecyclerView.layoutManager = layoutManager
        val adapter = BanSldGbgAdapter(context, list as ArrayList<SectionContentList>)
        mProductRecyclerView.adapter = adapter
        setNextItemMargin(moduleList, position)
    }

    /**
     * 아이템간격을 데코레이션 한다.
     */
    inner class SpacesItemDecoration : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val position = parent.getChildAdapterPosition(view)
            if (position == 0) {
                outRect.left = ConvertUtils.dp2px(SLIDE_OUTER_MARGIN)
            } else {
                outRect.left = ConvertUtils.dp2px(SLIDE_INNER_MARGIN)
            }
            if (position == parent.adapter!!.itemCount - 1) {
                outRect.right = ConvertUtils.dp2px(SLIDE_OUTER_MARGIN)
            }
        }
    }

    /**
     * 다음 아이템과 동일한 뷰타입인 경우는 1dp, 아인 경우는 10dp의 마진을 설정한다.
     *
     * @param moduleLists List<ModuleList>
     * @param position 뷰홀더 포지션
    </ModuleList> */
    private fun setNextItemMargin(moduleLists: List<ModuleList>, position: Int) {
        var isSameToNext = false
        val moduleList = moduleLists[position]
        if (moduleLists.size > position + 1) {
            val nextItem = moduleLists[position + 1]
            if (EmptyUtils.isNotEmpty(nextItem) && EmptyUtils.isNotEmpty(nextItem.viewType) &&
                    EmptyUtils.isNotEmpty(moduleList) && EmptyUtils.isNotEmpty(moduleList.viewType) && moduleList.viewType == nextItem.viewType) {
                isSameToNext = true
            }
        }
        if (isSameToNext) {
            mViewBottomDivider1dp.visibility = View.VISIBLE
            mViewBottomDivider10dp.visibility = View.GONE
        } else {
            mViewBottomDivider1dp.visibility = View.GONE
            mViewBottomDivider10dp.visibility = View.VISIBLE
        }
    }

    /**
     * 다음 아이템과 동일한 뷰타입인 경우는 1dp, 아인 경우는 10dp의 마진을 설정한다.
     *
     * @param moduleLists List<ModuleList>
     * @param position 뷰홀더 포지션
    </ModuleList> */
    private fun setNextItemMargin(info: ShopInfo, position: Int) {

        val lists = info.contents
        var isSameToNext = false
        val moduleList = lists[position]
        if (lists.size > position + 1) {
            val nextItem = lists[position + 1]
            if (EmptyUtils.isNotEmpty(nextItem) && EmptyUtils.isNotEmpty(nextItem.type) &&
                    EmptyUtils.isNotEmpty(moduleList) && EmptyUtils.isNotEmpty(moduleList.type) && moduleList.type == nextItem.type) {
                isSameToNext = true
            }
        }
        if (isSameToNext) {
            mViewBottomDivider1dp.visibility = View.VISIBLE
            mViewBottomDivider10dp.visibility = View.GONE
        } else {
            mViewBottomDivider1dp.visibility = View.GONE
            mViewBottomDivider10dp.visibility = View.VISIBLE
        }
    }

    init {
        //스와이프 허용각도 확대 적용
        increaseSwipeAngle = true
    }

    companion object {
        /**
         * 카드뷰 마진
         */
        private const val CARDVIEW_MARGIN = 4f

        /**
         * 슬라이드 좌우 마진
         */
        private const val SLIDE_OUTER_MARGIN = 20f - CARDVIEW_MARGIN

        /**
         * 슬라이드 아이템 간 마진
         */
        private const val SLIDE_INNER_MARGIN = 12f - CARDVIEW_MARGIN * 2
    }
}