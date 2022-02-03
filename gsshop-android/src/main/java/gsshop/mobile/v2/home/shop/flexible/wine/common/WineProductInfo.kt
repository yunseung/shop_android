package gsshop.mobile.v2.home.shop.flexible.wine.common

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.shop.renewal.utils.SetDtoUtil
import gsshop.mobile.v2.home.shop.renewal.utils.dto.ProductInfo
import gsshop.mobile.v2.home.shop.renewal.views.RenewalLayoutProductInfo

class WineProductInfo : RenewalLayoutProductInfo {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    var textSub: TextView? = null

    override fun initLayout() {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.wine_layout_product_info, this, true)
    }

    override fun initViews() {
        super.initViews()

        textSub = findViewById(R.id.txt_sub)
    }

    override fun setViews(info: ProductInfo?, componentType: SetDtoUtil.BroadComponentType?) {
        super.setViews(info, componentType)

        if (info != null) {
            if (!TextUtils.isEmpty(info.exposEngPrdNm)) {
                if (textSub != null) {
                    textSub!!.text = info.exposEngPrdNm
                }
            }
        }
    }
}