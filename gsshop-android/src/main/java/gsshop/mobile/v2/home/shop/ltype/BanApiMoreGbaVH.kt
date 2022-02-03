/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.ltype

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.blankj.utilcode.util.EmptyUtils
import com.gsshop.mocha.ui.util.ViewUtils
import de.greenrobot.event.EventBus
import gsshop.mobile.v2.Events
import gsshop.mobile.v2.home.main.ModuleList
import gsshop.mobile.v2.home.shop.BaseViewHolderV2
import kotlinx.android.synthetic.main.view_holder_ban_api_more_gba.view.*

/**
 * 브랜드 더보기 뷰홀더
 */
class BanApiMoreGbaVH(itemView: View) : BaseViewHolderV2(itemView) {

    /**
     * 전체 영역
     */
    private val mLiBrandMore: LinearLayout = itemView.ll_brand_more

    /**
     * 텍스트 영역
     */
    private val mTvBrandMore: TextView = itemView.tv_brand_more

    /**
     * 하단 디바이더가 다음 뷰타입과 같으면 1dp 다르면 10dp
     */
    private val mViewBottomDivider1dp: View = itemView.view_bottom_divider_1dp
    private val mViewBottomDivider10dp: View = itemView.view_bottom_divider_10dp

    override fun onBindViewHolder(context: Context, position: Int, moduleList: MutableList<ModuleList>?) {
        super.onBindViewHolder(context, position, moduleList)
        val module = moduleList?.get(position)

        if (module == null
                || module.moreBtnUrl?.isEmpty() != false
                || module.moreText?.isEmpty() != false) {
            ViewUtils.hideViews(mLiBrandMore)
            return
        }

        mTvBrandMore.text =  module.moreText ?: ""

        mLiBrandMore.setOnClickListener {
            EventBus.getDefault().post(Events.FlexibleEvent.SignatureBrandMoreEvent(module.moreBtnUrl, position))
        }

        setNextItemMargin(moduleList, position)
    }

    /**
     * 다음 아이템과 동일한 뷰타입인 경우는 1dp, 아인 경우는 10dp의 마진을 설정한다.
     *
     * @param moduleLists List<ModuleList>
     * @param position 뷰홀더 포지션
     */
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
}