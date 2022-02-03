package gsshop.mobile.v2.home.shop.flexible.beauty

import android.content.Context
import android.view.View
import android.widget.ImageView
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.shop.BaseViewHolder
import gsshop.mobile.v2.home.shop.ShopInfo
import gsshop.mobile.v2.util.DisplayUtils
import gsshop.mobile.v2.util.ImageUtil
import gsshop.mobile.v2.web.WebUtils

class BanImgBeautyTomm(itemView: View): BaseViewHolder(itemView) {

    private val mIvImg: ImageView = itemView.findViewById(R.id.iv_img)

    override fun onBindViewHolder(context: Context?, position: Int, info: ShopInfo?, action: String?, label: String?, sectionName: String?) {
        super.onBindViewHolder(context, position, info, action, label, sectionName)

        DisplayUtils.resizeHeightAtViewToScreenSize(context!!, mIvImg)

        ImageUtil.loadImageResize(context!!, info!!.contents[position].sectionContent.imageUrl, mIvImg, R.drawable.noimage_375_188)

        if (info!!.contents[position].sectionContent.linkUrl != null) {
            mIvImg.setOnClickListener { WebUtils.goWeb(context!!, info!!.contents[position].sectionContent.linkUrl) }
        }
    }
}