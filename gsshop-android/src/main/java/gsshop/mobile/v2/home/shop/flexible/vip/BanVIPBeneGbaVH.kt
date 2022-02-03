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
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.shop.BaseViewHolder
import gsshop.mobile.v2.home.shop.ShopInfo
import gsshop.mobile.v2.home.shop.flexible.vip.common.VipCommonReadMore
import gsshop.mobile.v2.util.StringUtils
import gsshop.mobile.v2.web.WebUtils
import kotlinx.android.synthetic.main.view_holder_ban_vip_bene_gba.view.*
import roboguice.util.Ln

class BanVIPBeneGbaVH(itemView: View) : BaseViewHolder(itemView) {
    // 상단 공통 VIP 타이틀
    private val mTitleVip = itemView.view_title_vip
    // 하단 공통 바로가기
    private val mMoreView = itemView.view_read_more
    // 메인 이미지
    private val mViewImage = itemView.img_main

    private val mViewRoot = itemView.view_root

    override fun onBindViewHolder(context: Context, position: Int, info: ShopInfo?,
                                  action: String?, label: String?, sectionName: String?) {
        val item = info?.contents?.get(position)?.sectionContent ?: return

        mViewRoot.setNew("Y".equals(item.newYN, ignoreCase = true))

        mTitleVip.setItems(item)

        mMoreView.setItems(item, VipCommonReadMore.ENUM_TYPE_MORE.TYPE_GO_WEB)
        mMoreView.setOnClickListener {
            if (!TextUtils.isEmpty(item.moreBtnUrl)) {
                WebUtils.goWeb(context, item.moreBtnUrl)
            }
        }

        if (!TextUtils.isEmpty(item.imageUrl)) {
            mViewImage.scaleType = ImageView.ScaleType.FIT_XY
            // 접근성 설정
            if (!TextUtils.isEmpty(item.moreText)) {
                mViewImage.contentDescription = item.moreText
            }
            try {
                // newer versions
                Glide.with(context).load(StringUtils.trim(item.imageUrl)).placeholder(R.drawable.noimage_375_188)
                        .diskCacheStrategy(DiskCacheStrategy.NONE).into(mViewImage)
            } catch (e: Exception) {
                Ln.e(e)
            }
//            ImageUtil.loadImageResize(context, item.imageUrl, mViewImage, R.drawable.noimage_375_188)
        }
    }
}