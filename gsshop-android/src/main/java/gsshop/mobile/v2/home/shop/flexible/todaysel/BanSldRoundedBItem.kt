package gsshop.mobile.v2.home.shop.flexible.todaysel

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.main.SectionContentList
import gsshop.mobile.v2.util.DisplayUtils
import gsshop.mobile.v2.util.ImageUtil
import kotlinx.android.synthetic.main.view_holder_ban_sld_rounded_b_item.view.*

class BanSldRoundedBItem : ConstraintLayout {

    private var mContext: Context = context

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        initLayout()
    }

    private fun initLayout() {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.view_holder_ban_sld_rounded_b_item, this, true)

        DisplayUtils.resizeWidthAtViewToScreenSize(context, root_view)
        DisplayUtils.resizeHeightAtViewToScreenSize(context, root_view)
    }

    fun setItem(product: SectionContentList) {

        if (!product.imageUrl.isNullOrEmpty()) {
            ImageUtil.loadImageFit(mContext, product.imageUrl, iv_img, R.drawable.noimage_375_375)
        }

        if (!product.name.isNullOrEmpty()) {
            txt_main.visibility = View.VISIBLE
            txt_main.text = product.name
        }
        else {
            txt_main.visibility = View.GONE
        }

        if (!product.subName.isNullOrEmpty()) {
            txt_sub.visibility = View.VISIBLE
            txt_sub.text = product.subName
        }
        else {
            txt_sub.visibility = View.GONE
        }

//        if (product.linkUrl != null) {
//            iv_img.setOnClickListener { WebUtils.goWeb(context, product.linkUrl) }
//        }
    }
}