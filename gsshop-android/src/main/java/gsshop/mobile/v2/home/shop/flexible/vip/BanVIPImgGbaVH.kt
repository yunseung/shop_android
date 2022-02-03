/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible.vip

import android.content.Context
import android.text.TextUtils
import android.view.View
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.shop.BaseViewHolder
import gsshop.mobile.v2.home.shop.ShopInfo
import gsshop.mobile.v2.home.shop.flexible.vip.common.VipCommonReadMore
import gsshop.mobile.v2.util.DisplayUtils
import gsshop.mobile.v2.util.ImageUtil
import gsshop.mobile.v2.web.WebUtils
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.view_holder_ban_vip_img_gba.view.*

class BanVIPImgGbaVH(itemView: View) : BaseViewHolder(itemView) {

    private val mImageMain = itemView.img_main
    // 하단 공통 더보기
    private val mMoreView = itemView.view_read_more

    private val mViewRoot = itemView.view_back

    override fun onBindViewHolder(context: Context, position: Int, info: ShopInfo?,
                                  action: String?, label: String?, sectionName: String?) {
        val item = info?.contents?.get(position)?.sectionContent ?: return

        mViewRoot.setNew("Y".equals(item.newYN, ignoreCase = true))

        ImageUtil.loadImageResize(context, item.imageUrl, mImageMain, R.drawable.noimage_375_188
            , DisplayUtils.convertDpToPx(context, 6f), RoundedCornersTransformation.CornerType.TOP)

        if (!TextUtils.isEmpty(item.linkUrl)) {
            mImageMain.setOnClickListener(View.OnClickListener {
                WebUtils.goWeb(context, item.linkUrl)
            })
        }

        var description = if(item.gsAccessibilityVariable != null) item.gsAccessibilityVariable else ""
        description += if(item.productName != null) item.productName else ""
        if (TextUtils.isEmpty(description)) description = context.getString(R.string.content_description_image)

        if (!TextUtils.isEmpty(description)) {
            mImageMain.contentDescription = description
        }

        mMoreView.setItems(item, VipCommonReadMore.ENUM_TYPE_MORE.TYPE_GO_WEB)
        mMoreView.setOnClickListener {
            if (!TextUtils.isEmpty(item.moreBtnUrl)) {
                WebUtils.goWeb(context, item.moreBtnUrl)
            }
        }
    }
}