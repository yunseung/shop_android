package gsshop.mobile.v2.home.shop.bestshop

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.main.SectionContentList
import gsshop.mobile.v2.util.DisplayUtils
import gsshop.mobile.v2.util.ImageUtil
import gsshop.mobile.v2.web.WebUtils
import kotlinx.android.synthetic.main.view_holder_menu_gift_recommend_fix_item.view.*

class MenuGiftRecommendFixItem : LinearLayout {
    private var mContext: Context = context

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        initLayout()
    }

    private fun initLayout() {
        var inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.view_holder_menu_gift_recommend_fix_item, this, true)
    }

    fun setItem(item: SectionContentList) {
        tv_name.text = item.name
        ImageUtil.loadImageFit(
            mContext,
            item.imageUrl,
            iv_prd,
            R.drawable.noimage_166_166
        )

        if (item.newYN.equals("y", true)) {
            iv_badge.visibility = VISIBLE
            iv_badge.setBackgroundResource(R.drawable.badge_new)
        } else if (item.hotYN.equals("y", true)) {
            iv_badge.visibility = VISIBLE
            iv_badge.setBackgroundResource(R.drawable.badge_hot)
        } else if (item.newYN.equals("n", true) && item.hotYN.equals("n", true)) {
            iv_badge.visibility = GONE
        }

        root_view.setOnClickListener { WebUtils.goWeb(mContext, item.linkUrl) }
    }
}