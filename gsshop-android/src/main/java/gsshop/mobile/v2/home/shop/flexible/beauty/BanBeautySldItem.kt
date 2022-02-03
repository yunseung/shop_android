package gsshop.mobile.v2.home.shop.flexible.beauty

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.main.SectionContentList
import gsshop.mobile.v2.util.ImageUtil
import gsshop.mobile.v2.web.WebUtils
import kotlinx.android.synthetic.main.view_holder_ban_sld_mobile_live_broad_item.view.*

class BanBeautySldItem : ConstraintLayout {

    private var mContext: Context = context

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        initLayout()
    }

    private fun initLayout() {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.view_holder_ban_beauty_sld_item, this, true)

//        resizeWidthAtViewToScreenSize(context, root_view)
//        resizeHeightAtViewToScreenSize(context, root_view)
    }

    fun setItem(product: SectionContentList) {

        if (product.imageUrl != null) {
            ImageUtil.loadImageFit(mContext, product.imageUrl, iv_img, R.drawable.noimage_375_375)
        }

        if (product.linkUrl != null) {
            iv_img.setOnClickListener { WebUtils.goWeb(context, product.linkUrl) }
        }
    }
}