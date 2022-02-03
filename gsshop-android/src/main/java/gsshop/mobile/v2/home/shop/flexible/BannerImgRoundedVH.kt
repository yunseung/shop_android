/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible

import gsshop.mobile.v2.R

import android.content.Context
import android.view.View
import gsshop.mobile.v2.home.main.SectionContentList
import gsshop.mobile.v2.home.shop.BaseViewHolder
import gsshop.mobile.v2.home.shop.ShopInfo
import gsshop.mobile.v2.util.ImageUtil
import gsshop.mobile.v2.web.WebUtils
import kotlinx.android.synthetic.main.view_holder_image_rounded.view.*

/**
 * 이미지 배너.
 *
 */
class BannerImgRoundedVH(itemView: View) : BaseViewHolder(itemView) {


    /* 이미지 배너. bind */
    override fun onBindViewHolder(context: Context?, position: Int, info: ShopInfo?,
                                  action: String?, label: String?, sectionName: String?) {
        super.onBindViewHolder(context, position, info, action, label, sectionName)
        val item = info?.contents?.get(position)?.sectionContent ?: return
        setView(context, item)
    }

    private fun setView(context: Context?, item: SectionContentList) {
        if (context == null) return

        if (!item.imageUrl.isNullOrEmpty()) {
            ImageUtil.loadImageResize(context, item.imageUrl, itemView.main_img, R.drawable.noimg_banner)
        }

        if (!item.linkUrl.isNullOrEmpty()) {
            itemView.setOnClickListener {
                WebUtils.goWeb(context, item.linkUrl)
            }
        }
    }

}