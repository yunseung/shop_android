package gsshop.mobile.v2.home.shop.ltype

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.main.SectionContentList
import gsshop.mobile.v2.util.ImageUtil
import gsshop.mobile.v2.web.WebUtils
import kotlinx.android.synthetic.main.layout_item_prd_mlt_gba.view.*

class PrdMltGbaItemLayout : ConstraintLayout {
    private var mContext: Context = context

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        initLayout()
    }

    private fun initLayout() {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.layout_item_prd_mlt_gba, this, true)
    }

    fun setData(product: SectionContentList) {
        ImageUtil.loadImage(mContext, product.imageUrl, iv_prd_img, R.drawable.noimage_375_375)
        tv_name.text = product.productName
        tv_price.text = product.salePrice
        tv_price_unit.text = product.exposePriceText
        tv_rate.text = product.addTextLeft
        tv_rate_count.text = product.addTextRight

        CommonPriceUtil.getInstance().setPriceRule(mContext, product, root, tv_name, tv_price, tv_price_unit)

        root.setOnClickListener { WebUtils.goWeb(mContext, product.linkUrl) }
    }

}