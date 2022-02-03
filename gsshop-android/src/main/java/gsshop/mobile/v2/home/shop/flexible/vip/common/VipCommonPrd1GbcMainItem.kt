package gsshop.mobile.v2.home.shop.flexible.vip.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.main.SectionContentList
import gsshop.mobile.v2.util.ImageUtil
import gsshop.mobile.v2.web.WebUtils
import kotlinx.android.synthetic.main.view_holder_gr_prd_1_vip_list_gbc_item_main.view.*
import roboguice.util.Ln


class VipCommonPrd1GbcMainItem : ConstraintLayout {
    private var mContext: Context = context

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        initLayout()
    }

    private fun initLayout() {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.view_holder_gr_prd_1_vip_list_gbc_item_main, this, true)
    }

    fun setItems(item: SectionContentList) {
        setView(item)
    }


    private fun setView(item: SectionContentList) {

        try {
            ImageUtil.loadImageResize(context, item.imageUrl, rootView.iv_prd_img, R.drawable.noimage_375_188)
            rootView.tv_prd_name.text = item.productName
            rootView.setOnClickListener {
                WebUtils.goWeb(context, item.linkUrl)
            }
        }
        catch (e:NullPointerException) {
            Ln.e(e.message)
        }
    }
}