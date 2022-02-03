/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible.vip

import android.content.Context
import android.view.View
import gsshop.mobile.v2.home.shop.BaseViewHolder
import gsshop.mobile.v2.home.shop.ShopInfo
import gsshop.mobile.v2.home.shop.flexible.vip.common.VipCommonReadMore
import gsshop.mobile.v2.web.WebUtils
import kotlinx.android.synthetic.main.view_holder_gr_prd_2_vip_list.view.*

class GrPrd2VIPListGbaVH(itemView: View) : BaseViewHolder(itemView) {
    // 상단 공통 VIP 타이틀
    private val mTitleVip = itemView.view_title_vip
    // 하단 공통 더보기
    private val mMoreView = itemView.view_read_more
    // 추가될 뷰
    private val mViewPrd2 = itemView.view_prd_2
    private val mRootView = itemView.root

    private val mViewBack = itemView.view_back

    override fun onBindViewHolder(context: Context, position: Int, info: ShopInfo?,
                                  action: String?, label: String?, sectionName: String?) {
        val item = info?.contents?.get(position)?.sectionContent ?: return

        mViewBack.setNew("Y".equals(item.newYN, ignoreCase = true))

        mTitleVip.setItems(item)
        mMoreView.setItems(item, VipCommonReadMore.ENUM_TYPE_MORE.TYPE_GO_WEB)
        // 웹 이동 동작
        mMoreView.setOnClickListener(View.OnClickListener {
            WebUtils.goWeb(context, item.moreBtnUrl)
        })

        mRootView.contentDescription = mTitleVip.getTitleTxt()
        mViewPrd2.setItems(item)
    }
}