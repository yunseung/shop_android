package gsshop.mobile.v2.home.shop.flexible.beauty

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.main.SectionContentList
import gsshop.mobile.v2.home.shop.ltype.CommonPriceUtil
import gsshop.mobile.v2.util.DisplayUtils.resizeHeightAtViewToScreenSize
import gsshop.mobile.v2.util.DisplayUtils.resizeWidthAtViewToScreenSize
import gsshop.mobile.v2.util.ImageUtil
import gsshop.mobile.v2.web.WebUtils
import kotlinx.android.synthetic.main.view_holder_prd_beauty_sld_weekly_event_item.view.*

class PrdBeautySldWeeklyEventItem : ConstraintLayout {

    private var mContext: Context = context

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        initLayout()
    }

    private fun initLayout() {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.view_holder_prd_beauty_sld_weekly_event_item, this, true)

        resizeWidthAtViewToScreenSize(mContext, view_img)
        resizeHeightAtViewToScreenSize(mContext, view_img)
    }

    fun setItem(product: SectionContentList) {

        if (product.imageUrl != null) {
            ImageUtil.loadImageFit(mContext, product.imageUrl, iv_img, R.drawable.noimage_375_375)
        }

        if (product.productName != null) {
            tv_product_name.text = product.productName
        }

        CommonPriceUtil.getInstance().setPriceRule(mContext, product, root_view, tv_product_name, tv_sale_price, tv_price_unit)

        if (product.addTextLeft != null) {
            tv_review_average.text = product.addTextLeft
        } else {
            tv_review_average.visibility = View.GONE
        }

        if (product.addTextRight != null) {
            tv_review_cnt.text = product.addTextRight
        } else {
            tv_review_cnt.visibility = View.GONE
        }

        root_view.setOnClickListener {
            if (product.linkUrl != null) {
                WebUtils.goWeb(context, product.linkUrl)
            }
        }

        layout_benefit.setData(product)
    }
}