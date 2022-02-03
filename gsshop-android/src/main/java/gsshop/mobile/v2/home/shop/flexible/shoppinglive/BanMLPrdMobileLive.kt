/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible.shoppinglive

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import de.greenrobot.event.EventBus
import gsshop.mobile.v2.Events
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.shop.BaseViewHolder
import gsshop.mobile.v2.home.shop.ShopInfo
import gsshop.mobile.v2.home.shop.flexible.shoppinglive.items.ShoppingLivePrdMobileLiveItem
import kotlinx.android.synthetic.main.view_holder_prd_shopping_live_mobile_live.view.*

class BanMLPrdMobileLive(itemView: View) : BaseViewHolder(itemView) {

    // 추가될 뷰
    private val mViewAdd = itemView.view_add
    // 최상단 뷰
    private val mRootView = itemView.root
    // 추가될 뷰 ID
    private val LAYOUT_ID = R.layout.recycler_item_shopping_live_prd_mobile_live

    // 테스트 용
//    companion object {
//        @JvmStatic
//        var testCnt: Int = 1
//    }

    override fun onBindViewHolder(context: Context, position: Int, info: ShopInfo?,
                                  action: String?, label: String?, sectionName: String?) {
//        testCnt = 1
        val item = info?.contents?.get(position)?.sectionContent ?: return

        mViewAdd.removeAllViews()

        var isAvailableStartDate = true

        if (item.subProductList != null && item.subProductList!!.size > 0) {
            for (listItem in item.subProductList!!) {
                val itemView = LayoutInflater.from(context).inflate(LAYOUT_ID, null)
                val subPrd = ShoppingLivePrdMobileLiveItem(itemView)

//                Ln.d("System.currentTimeMillis() : " + System.currentTimeMillis() + " / startDate : " + listItem.startDate + " / 차이 : " + (listItem.startDate!! - System.currentTimeMillis()))

                if (listItem.startDate == null ||
                        listItem.startDate != null && (listItem.startDate!! - System.currentTimeMillis() <= 0) ) {
                    continue
                }
                mViewAdd.addView(subPrd.onBindViewHolder(context, listItem))
                if (isAvailableStartDate) {
                    subPrd.setStartDate(context)
                }
                isAvailableStartDate = false

            }
        }
        if (isAvailableStartDate) {
            // 시간이 흐르는 뷰가 없다. 그렇다면 빼야 하니 리프레시 한번 호출 해 준다.
            EventBus.getDefault().post(Events.ShoppingLiveEvent.MobileLiveSldRefreshEvent(false))
        }
    }
}