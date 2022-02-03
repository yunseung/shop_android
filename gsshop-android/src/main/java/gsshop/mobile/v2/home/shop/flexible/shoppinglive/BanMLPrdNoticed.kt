/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible.shoppinglive

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.shop.BaseViewHolder
import gsshop.mobile.v2.home.shop.ShopInfo
import gsshop.mobile.v2.home.shop.flexible.shoppinglive.items.ShoppingLivePrdNoticedItem
import gsshop.mobile.v2.web.WebUtils
import kotlinx.android.synthetic.main.view_holder_prd_shopping_live_noticed.view.*
import roboguice.util.Ln
import java.lang.NullPointerException

class BanMLPrdNoticed(itemView: View) : BaseViewHolder(itemView) {

    // 추가될 뷰
    private val mViewAdd = itemView.view_add

    private val mViewMore = itemView.view_more

    // 추가될 뷰 ID
    private val LAYOUT_ID = R.layout.recycler_item_shopping_live_prd_noticed

    override fun onBindViewHolder(context: Context, position: Int, info: ShopInfo?,
                                  action: String?, label: String?, sectionName: String?) {
        val item = info?.contents?.get(position)?.sectionContent ?: return

        try {
            mViewAdd.removeAllViews()

            if (item.subProductList != null && item.subProductList!!.size > 0) {
                for (listItem in item.subProductList!!) {
                    val itemView = LayoutInflater.from(context).inflate(LAYOUT_ID, null)
                    val subPrd = ShoppingLivePrdNoticedItem(itemView)
                    mViewAdd.addView(subPrd.onBindViewHolder(context, listItem))
                }
            }

            if (TextUtils.isEmpty(item.moreBtnUrl)) {
                mViewMore.visibility = View.GONE
            }
            else {
                mViewMore.setOnClickListener {
                    WebUtils.goWeb(context, item.moreBtnUrl)
                }
            }
        }
        catch (e:NullPointerException) {
            Ln.e(e.message)
        }
    }
}