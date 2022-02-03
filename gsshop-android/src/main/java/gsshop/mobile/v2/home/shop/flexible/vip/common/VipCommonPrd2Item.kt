package gsshop.mobile.v2.home.shop.flexible.vip.common

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import gsshop.mobile.v2.MainApplication
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.main.SectionContentList
import gsshop.mobile.v2.home.shop.renewal.utils.SetDtoUtil
import gsshop.mobile.v2.home.shop.schedule.viewholder.TLineBaseViewHolder.AIR_BUY
import gsshop.mobile.v2.home.shop.schedule.viewholder.TLineBaseViewHolder.SOLD_OUT
import gsshop.mobile.v2.util.CookieUtils
import gsshop.mobile.v2.util.DisplayUtils
import gsshop.mobile.v2.util.ImageUtil
import gsshop.mobile.v2.web.WebUtils
import kotlinx.android.synthetic.main.renewal_banner_type_l_prd_2_item.view.*

//class ViewPrd2Item(itemView: View) : BaseViewHolderV2(itemView) {
class VipCommonPrd2Item : FrameLayout {
    private var mContext: Context = context

    private lateinit var mRootView: View

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        initLayout()
    }

    private fun initLayout() {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.renewal_banner_type_l_prd_2_item, this, true)

        mRootView = root
    }

    fun setItems(item: SectionContentList?) {
        setView(item)
    }

    private fun setView(item: SectionContentList?) {
        if (item == null) {
            return;
        }

        // 성인 확인
        val adult = CookieUtils.getAdult(MainApplication.getAppContext())

        if (!TextUtils.isEmpty(item.adultCertYn) &&
                "Y".equals(item.adultCertYn, ignoreCase = true) &&
                !("true" == adult || "temp" == adult)) {
            mRootView.image_prd.setImageResource(R.drawable.s_19_image_166_166)
        }
        else {
            ImageUtil.loadImageResize(context, item.imageUrl, mRootView.image_prd, R.drawable.noimage_166_166)
        }

        if (DisplayUtils.isValidString(item.broadTimeText)) {
            mRootView.text_brd_time.text = item.broadTimeText
            mRootView.text_brd_time.visibility = View.VISIBLE
        } else {
            mRootView.text_brd_time.visibility = View.GONE
        }

        mRootView.txt_comment.visibility = View.GONE
        if (item.directOrdInfo == null) {
            mRootView.txt_comment.visibility = View.VISIBLE
            if (AIR_BUY.equals(item.imageLayerFlag, ignoreCase = true)) {
                mRootView.txt_comment.setText(R.string.layer_flag_air_buy)
            } else if (SOLD_OUT.equals(item.imageLayerFlag, ignoreCase = true)) {
                mRootView.txt_comment.setText(R.string.layer_flag_sold_out)
            } else {
                mRootView.txt_comment.visibility = View.GONE
            }
        }

        mRootView.view_product.setOnClickListener(View.OnClickListener {
            WebUtils.goWeb(context, item.linkUrl)
        })

        val info = SetDtoUtil.setDto(item)
        // vip에서는 brandNm 없다!
        info.brandNm = null
        mRootView.layout_product_info.setViews(info, SetDtoUtil.BroadComponentType.product, null)
    }
}