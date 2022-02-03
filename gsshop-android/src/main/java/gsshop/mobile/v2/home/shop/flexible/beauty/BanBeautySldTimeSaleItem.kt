package gsshop.mobile.v2.home.shop.flexible.beauty

import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.gsshop.mocha.ui.util.ViewUtils
import de.greenrobot.event.EventBus
import gsshop.mobile.v2.Events.TimerEvent
import gsshop.mobile.v2.Keys
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.main.ProductInfoUtil
import gsshop.mobile.v2.home.main.SectionContentList
import gsshop.mobile.v2.util.DisplayUtils
import gsshop.mobile.v2.util.DisplayUtils.getFormattedNumber
import gsshop.mobile.v2.util.DisplayUtils.getNumberFromString
import gsshop.mobile.v2.util.DisplayUtils.isValidNumberStringExceptZero
import gsshop.mobile.v2.util.DisplayUtils.isValidString
import gsshop.mobile.v2.util.GlobalTimer.Companion.getInstance
import gsshop.mobile.v2.util.ImageUtil
import gsshop.mobile.v2.util.StringUtils
import gsshop.mobile.v2.web.WebUtils
import kotlinx.android.synthetic.main.view_holder_ban_beauty_sld_time_sale_item.view.*
import roboguice.util.Ln

class BanBeautySldTimeSaleItem : LinearLayout {

    private var mContext: Context = context

    private var mPosition: Int = 0

    private var mEndTime: Long = 0

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private lateinit var mOnZeroTimeItemRemoveListener: OnZeroTimeItemRemoveListener

    interface OnZeroTimeItemRemoveListener {
        fun setZeroTimeItemRemove(position: Int)
    }

    fun setOnZeroTimeItemRemoveListener(listener: OnZeroTimeItemRemoveListener) {
        mOnZeroTimeItemRemoveListener = listener
    }

    init {
        initLayout()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        try {
            EventBus.getDefault().register(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        getInstance().startTimer()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        EventBus.getDefault().unregister(this)
    }

    private fun initLayout() {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.view_holder_ban_beauty_sld_time_sale_item, this, true)

        DisplayUtils.resizeWidthAtViewToScreenSize(context, root_view)
        DisplayUtils.resizeHeightAtViewToScreenSize(context, root_view)

        DisplayUtils.resizeHeightAtViewToScreenSize(context, view_time)
        DisplayUtils.resizeWidthAtViewToScreenSize(context, view_time)

        DisplayUtils.resizeHeightAtViewToScreenSize(context, view_time_text)
        DisplayUtils.resizeWidthAtViewToScreenSize(context, view_time_text)

        DisplayUtils.resizeHeightAtViewToScreenSize(context, iv_img)
        DisplayUtils.resizeWidthAtViewToScreenSize(context, iv_img)
    }

    fun setItem(product: SectionContentList, position: Int) {
        mPosition = position
        mEndTime = product.endDate!!

        if (product.imageUrl != null) {
            ImageUtil.loadImageFit(mContext, product.imageUrl, iv_img, R.drawable.noimage_375_375)
        }

        if (product.linkUrl != null) {
            root_view.setOnClickListener { WebUtils.goWeb(context, product.linkUrl) }
        }

        if (product.endDate != null) {
            setRemainTime()
        }

        if (product.productName != null) {
            tv_product_name.text = product.productName
        }

        // 초기화
        tv_price.text = ""
        tv_price_unit.text = ""
        tv_discount_rate.text = ""
        tv_discount_rate_unit.text = ""
        tv_base_price.text = ""

        // 퍼센트값으로 표현되는 할인률이 있을 경우에만 base price 표시
        // base price가 0이거나 salePrice가 basePrice보다 크면 basePrice 표시 안함
        val salePrice = getNumberFromString(product.salePrice)
        val basePrice = getNumberFromString(product.basePrice)

        if (product.discountRateText != null) {
            tv_discount_rate_unit.text = product.discountRateText
        }

        // 베이스가 처리로직: 할인률 있으면
        if (product.discountRate != null && !TextUtils.isEmpty(product.discountRate) && product.discountRate!!.toInt() > Keys.ZERO_DISCOUNT_RATE) {
            //베이스가 로직==찌익그은가, 찌익 그은가는 할인율이 없거나, 세일가보다 비싸면 안보여야함 디폴트는 안보임
            if (isValidNumberStringExceptZero(product.basePrice) && salePrice < basePrice) {
                if (tv_base_price != null) {
                    tv_base_price.text = getFormattedNumber(product.basePrice)
                    tv_base_price.append(product.exposePriceText)
                    tv_base_price.visibility = View.VISIBLE
                    tv_base_price.paintFlags = tv_base_price.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                }
                if (tv_discount_rate != null) {
                    tv_discount_rate.append(ProductInfoUtil.getDiscountCommon(context, product.discountRate))
                    tv_discount_rate.ellipsize = null
                    tv_discount_rate.visibility = View.VISIBLE
                }
            } else {
                ViewUtils.hideViews(tv_base_price, tv_discount_rate_unit, tv_discount_rate)
            }
        }

        // 세일 price 처리 로직 : 있으면 그려 / else 면 안그려
        if (isValidString(product.salePrice)) {
            val salePriceText = getFormattedNumber(product.salePrice)
            if (tv_price_unit != null) {
                tv_price_unit.text = product.exposePriceText
                tv_price_unit.visibility = View.VISIBLE
            }

            val priceStringBuilder = SpannableStringBuilder()
            priceStringBuilder.append(salePriceText)
            if (tv_price != null) {
                tv_price.append(priceStringBuilder)
                tv_price.visibility = View.VISIBLE
            }
        } else {
            ViewUtils.hideViews(tv_price, tv_price_unit)
        }

    }

    fun onEventMainThread(event: TimerEvent?) {
        setRemainTime()
    }

    private fun setRemainTime() {

        val startTime: Long = System.currentTimeMillis()
        val defTime = mEndTime - startTime

        try {
            if (defTime > 0) {
                (context as Activity).runOnUiThread {
                    tv_time_text.text = StringUtils.stringForHHMMSS(defTime, false)
                }
            } else {
                mOnZeroTimeItemRemoveListener.setZeroTimeItemRemove(mPosition)
                return
            }
        } catch (e: NullPointerException) {
            Ln.e(e.message)
        }
    }

}