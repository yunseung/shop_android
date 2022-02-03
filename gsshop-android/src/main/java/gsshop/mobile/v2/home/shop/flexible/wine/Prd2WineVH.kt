/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible.wine

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.shop.BaseViewHolder
import gsshop.mobile.v2.home.shop.ShopInfo
import gsshop.mobile.v2.home.shop.flexible.wine.common.ViewWineItem
import gsshop.mobile.v2.util.DisplayUtils
import kotlinx.android.synthetic.main.view_holder_prd_2_wine.view.*

class Prd2WineVH(itemView: View) : BaseViewHolder(itemView) {
    // 상단 공통 VIP 타이틀

    override fun onBindViewHolder(context: Context, position: Int, info: ShopInfo?,
                                  action: String?, label: String?, sectionName: String?) {
        itemView.rootView.view_add_sub.removeAllViews()

        val item = info?.contents?.get(position)?.sectionContent ?: return
        item.subProductList ?: return

        val itemView1st = ViewWineItem(context)
        val itemView2nd = ViewWineItem(context)

        val param1st = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT)
        param1st.weight = 1f
        itemView1st.layoutParams = param1st

        val param2nd = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT)
        param2nd.weight = 1f
        itemView2nd.layoutParams = param1st

        itemView1st.setView(R.layout.banner_item_prd_2_list_wine)
        itemView2nd.setView(R.layout.banner_item_prd_2_list_wine)

        val view = View(context)
        view.layoutParams = LinearLayout.LayoutParams(
                DisplayUtils.convertDpToPx(context, 10f), LinearLayout.LayoutParams.MATCH_PARENT)

        itemView.rootView.view_add_sub.addView(itemView1st)
        itemView.rootView.view_add_sub.addView(view)
        itemView.rootView.view_add_sub.addView(itemView2nd)

        if (item.subProductList!!.size > 0) {
            itemView1st.visibility = View.VISIBLE
            itemView1st.setItems(item.subProductList!![0], 0, false)

            if (item.subProductList!!.size >= 2) {
                itemView2nd.visibility = View.VISIBLE
                itemView2nd.setItems(item.subProductList!![1], 1, false)
            }
            else {
                itemView2nd.visibility = View.INVISIBLE
            }
        }

    }
}