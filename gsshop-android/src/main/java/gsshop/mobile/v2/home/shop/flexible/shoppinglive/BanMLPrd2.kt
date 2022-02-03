/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible.shoppinglive

import android.content.Context
import android.view.View
import gsshop.mobile.v2.home.shop.BaseViewHolder
import gsshop.mobile.v2.home.shop.ShopInfo
import kotlinx.android.synthetic.main.view_holder_prd_shopping_live_prd_2.view.*

class BanMLPrd2(itemView: View) : BaseViewHolder(itemView) {

    // 최상단 뷰
    private val mRootView = itemView.root

    override fun onBindViewHolder(context: Context, position: Int, info: ShopInfo?,
                                  action: String?, label: String?, sectionName: String?) {
        val item = info?.contents?.get(position)?.sectionContent ?: return

        if (item.subProductList != null && item.subProductList!!.size > 0) {
            // 왼쪽 초기화
            mRootView.prd_1.onBindViewHolder(item.subProductList?.get(0))

            // 우측 초기화
            if (item.subProductList!!.size == 1) {
                mRootView.prd_2.visibility = View.INVISIBLE
            }
            else {
                mRootView.prd_2.visibility = View.VISIBLE
                mRootView.prd_2.onBindViewHolder(item.subProductList?.get(1))
            }
        }
    }
}