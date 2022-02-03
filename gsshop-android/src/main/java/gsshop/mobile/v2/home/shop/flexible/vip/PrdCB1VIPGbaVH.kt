/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible.vip

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.blankj.utilcode.util.EmptyUtils
import com.gsshop.mocha.ui.util.ViewUtils
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.shop.BaseViewHolder
import gsshop.mobile.v2.home.shop.ShopInfo
import gsshop.mobile.v2.home.shop.renewal.flexible.PrdCB1VH
import kotlinx.android.synthetic.main.view_holder_prd_c_b1_vip_gba.view.*

class PrdCB1VIPGbaVH(itemView: View) : BaseViewHolder(itemView) {

    // 상단 공통 VIP 타이틀
    private val mTitleVip = itemView.view_title_vip

    private val mLlRoot = itemView.ll_root

    // 추가될 뷰
    private val mViewAdd = itemView.view_add

    private val mViewBack = itemView.view_back

    override fun onBindViewHolder(context: Context, position: Int, info: ShopInfo?,
                              action: String?, label: String?, sectionName: String?) {
        val item = info?.contents?.get(position)?.sectionContent ?: return

        if (EmptyUtils.isEmpty(item.subProductList)) {
            //뷰홀더 전체를 숨김
            ViewUtils.hideViews(mLlRoot)
            return
        }

        mViewBack.setNew("Y".equals(item.newYN, ignoreCase = true))

        mTitleVip.setItems(item)

        val itemView = LayoutInflater.from(context).inflate(R.layout.renewal_home_row_type_fx_prd_c_b1, null)
        val subPrd = PrdCB1VH(itemView, null)
        mViewAdd.addView(subPrd.getView(context, item, C_TYPE_PRD_CB1_VIP));

        mLlRoot.contentDescription = mTitleVip.getTitleTxt()
    }

    init {
        increaseSwipeAngle = true
    }
}