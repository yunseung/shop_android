/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible.shoppinglive.items

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.main.SectionContentList
import gsshop.mobile.v2.util.ImageUtil
import gsshop.mobile.v2.web.WebUtils
import kotlinx.android.synthetic.main.recycler_item_shopping_live_prd_mobile_live.view.*
import kotlinx.android.synthetic.main.recycler_item_shopping_live_prd_noticed.view.*
import kotlinx.android.synthetic.main.recycler_item_shopping_live_prd_noticed.view.img_main
import kotlinx.android.synthetic.main.recycler_item_shopping_live_prd_noticed.view.tv_sub_name

/**
 * 쇼핑라이브 오늘의 라이브 아이템
 */
class ShoppingLivePrdNoticedItem (itemView: View) {


    private var mItemView: View

    init {
        mItemView = itemView
    }

    fun onBindViewHolder(context: Context, item: SectionContentList?): View? {
        return setItems(context, item)
    }

    private fun setItems(context: Context, item: SectionContentList?): View {
        if (item == null) {
            return mItemView;
        }
        if (!TextUtils.isEmpty(item.imageUrl)) {
            ImageUtil.loadImageFitCenter(context, item.imageUrl, mItemView.img_main, R.drawable.noimage_166_166)
        }

        if (!TextUtils.isEmpty(item.videoTime)) {
            mItemView.txt_title.text = item.videoTime
        }

        if (!TextUtils.isEmpty(item.promotionName)) {
            mItemView.txt_main.text = item.promotionName
        }

        if (!TextUtils.isEmpty(item.subName)) {
            mItemView.tv_sub_name.text = item.subName
        }

        if (!TextUtils.isEmpty(item.linkUrl)) {
            mItemView.setOnClickListener{
                WebUtils.goWeb(context, item.linkUrl)
            }
        }

        return mItemView
    }
}