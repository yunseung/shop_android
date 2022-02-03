package gsshop.mobile.v2.home.shop.flexible.beauty

import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.main.SectionContentList
import gsshop.mobile.v2.util.DisplayUtils
import gsshop.mobile.v2.util.ImageUtil
import gsshop.mobile.v2.web.WebUtils
import kotlinx.android.synthetic.main.view_holder_ban_beauty_sld_today_item.view.*

class BanBeautySldTodayItem(context: Context) : LinearLayout(context) {

    private var mContext: Context = context

    init {
        initLayout()
    }

    private fun initLayout() {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.view_holder_ban_beauty_sld_today_item, this, true)

        DisplayUtils.resizeWidthAtViewToScreenSize(context, iv_product_img)
        DisplayUtils.resizeHeightAtViewToScreenSize(context, iv_product_img)
    }

    fun setItem(product: SectionContentList) {
        if (product.imageUrl != null) {
            ImageUtil.loadImageFit(mContext, product.imageUrl, iv_product_img, R.drawable.noimage_375_375)
        }
        if (product.linkUrl != null) {
            root_view.setOnClickListener { WebUtils.goWeb(context, product.linkUrl) }
        }
        if (product.name != null) {
            tv_product_name.text = product.name
        }
        if (product.subName != null) {
            tv_sub_name.text = product.subName
        }
    }
}