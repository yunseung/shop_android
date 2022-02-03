package gsshop.mobile.v2.home.shop.flexible.wine.common

import android.content.Context
import android.graphics.Paint
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.gsshop.mocha.ui.util.ViewUtils
import gsshop.mobile.v2.Keys
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.main.ProductInfoUtil
import gsshop.mobile.v2.home.main.SectionContentList
import gsshop.mobile.v2.util.DisplayUtils
import gsshop.mobile.v2.util.DisplayUtils.getFormattedNumber
import gsshop.mobile.v2.util.DisplayUtils.getNumberFromString
import gsshop.mobile.v2.util.DisplayUtils.isValidNumberStringExceptZero
import gsshop.mobile.v2.util.ImageUtil
import gsshop.mobile.v2.web.WebUtils
import roboguice.util.Ln

class ViewWineItem : FrameLayout {

    private val VIEW_TYPE_READ_MORE = "_MORE"

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setView(viewId: Int?) {
        val tempViewId = viewId ?: R.layout.banner_item_prd_pmo_list_wine

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(tempViewId, this, true)

//        var itemView = ItemView(this)
    }

    fun setItems(item: SectionContentList?, position: Int?, isRankVisible: Boolean?) {
        if (item == null) {
            return
        }

        val imageMain: ImageView? = findViewById(R.id.image_main)
        val textCnt: TextView? = findViewById(R.id.txt_count)
        val layoutAdd: LinearLayout? = findViewById(R.id.layout_add_sub)
        val textMain: TextView? = findViewById(R.id.txt_main)
        val textSub: TextView? = findViewById(R.id.txt_sub)
        val textPrice: TextView? = findViewById(R.id.txt_price)
        val textPriceUnit: TextView? = findViewById(R.id.txt_price_unit)
        val textDiscountRate: TextView? = findViewById(R.id.txt_discount_rate)
        val textBasePrice: TextView? = findViewById(R.id.txt_base_price)
        val textReviewAvr: TextView? = findViewById(R.id.txt_review_avr)
        val textReviewCnt: TextView? = findViewById(R.id.txt_review_count)
        val bannerBottomInfo: View? = findViewById(R.id.banner_bottom_info)
        val txtReadMore: TextView? = findViewById(R.id.txt_read_more)
        val thisRootView: View? = findViewById(R.id.root)

        // text 초기화
        if (textMain != null) textMain.text = ""
        if (textSub != null) textSub.text = ""
        if (textPrice != null) textPrice.text = ""
        if (textPriceUnit != null) textPriceUnit.text = ""
        if (textDiscountRate != null) textDiscountRate.text = ""
        if (textBasePrice != null) textBasePrice.text = ""
        if (textReviewAvr != null) textReviewAvr.text = ""
        if (textReviewCnt != null) textReviewCnt.text = ""

        // 해당 부분은 수정하려다가 그냥 기존에서 추가하는것이 좋을 것 같아 빠꾸
        /*********************************************************************
        val wineProductInfo: WineProductInfo? = findViewById(R.id.layout_product_info)
        val info = SetDtoUtil.setDto(item)
        wineProductInfo?.setViews(info, SetDtoUtil.BroadComponentType.product)
        *********************************************************************/
        try {
            ImageUtil.loadImageFitCenter(context, item.imageUrl, imageMain!!, R.drawable.noimage_166_166)

            if (textCnt != null) {
                if (position != null && isRankVisible != null && isRankVisible) {
                    textCnt.visibility = View.VISIBLE
                    textCnt.text = (position + 1).toString()
                } else {
                    textCnt.visibility = View.GONE
                }
            }

            layoutAdd?.removeAllViews()

            if (item.viewType != null && item.viewType!!.contains(VIEW_TYPE_READ_MORE)) {
                if (bannerBottomInfo != null) {
                    bannerBottomInfo.visibility = View.GONE
                }

                if (txtReadMore != null) {
                    txtReadMore.visibility = View.VISIBLE
                }
            } else {
                if (bannerBottomInfo != null) {
                    bannerBottomInfo.visibility = View.VISIBLE
                }

                if (txtReadMore != null) {
                    txtReadMore.visibility = View.GONE
                }
            }

            if (TextUtils.isEmpty(item.type) && TextUtils.isEmpty(item.nation)) {
                layoutAdd?.visibility = View.GONE
                textMain?.setMargins(top = dpToPx(12F))
                // 해당 부분은 수정하려다가 그냥 기존에서 추가하는것이 좋을 것 같아 빠꾸
//                wineProductInfo?.setMargins(top = dpToPx(12F))
            }
            else {
                layoutAdd?.visibility = View.VISIBLE
                textMain?.setMargins(top = dpToPx(4F))
            }

            if (!TextUtils.isEmpty(item.type)) {
                layoutAdd?.addView(getTextBadge(item.type))
            }
            if (!TextUtils.isEmpty(item.nation)) {
                layoutAdd?.addView(getTextBadge(item.nation))
            }

            if (!TextUtils.isEmpty(item.productName)) {
                if (textMain != null) {
                    textMain.text = item.productName
                }
            }

            if (!TextUtils.isEmpty(item.exposEngPrdNm)) {
                if (textSub != null) {
                    textSub.text = item.exposEngPrdNm
                }
            }

            if (!TextUtils.isEmpty(item.salePrice)) {
                if (textPrice != null) {
                    textPrice.text = item.salePrice
                }
            }

            if (!TextUtils.isEmpty(item.exposePriceText)) {
                if (textPriceUnit != null) {
                    textPriceUnit.text = item.exposePriceText
                }
            }

            if (!TextUtils.isEmpty(item.addTextLeft)) {
                if (textReviewAvr != null) {
                    textReviewAvr.text = item.addTextLeft
                }
            }

            if (!TextUtils.isEmpty(item.addTextRight)) {
                if (textReviewCnt != null) {
                    textReviewCnt.text = item.addTextRight
                }
            }

            if (!TextUtils.isEmpty(item.linkUrl)) {
                thisRootView?.setOnClickListener {
                    WebUtils.goWeb(context, item.linkUrl)
                }
            }


            // 퍼센트값으로 표현되는 할인률이 있을 경우에만 base price 표시
            // base price가 0이거나 salePrice가 basePrice보다 크면 basePrice 표시 안함
            val salePrice = getNumberFromString(item.salePrice)
            val basePrice = getNumberFromString(item.basePrice)

            // 베이스가 처리로직: 할인률 있으면
            if (item.discountRate != null && !item.discountRate!!.isEmpty() && item.discountRate!!.toInt() > Keys.ZERO_DISCOUNT_RATE) {
                //베이스가 로직==찌익그은가, 찌익 그은가는 할인율이 없거나, 세일가보다 비싸면 안보여야함 디폴트는 안보임
                if (isValidNumberStringExceptZero(item.basePrice) && salePrice < basePrice) {
                    if (textBasePrice != null) {
                        textBasePrice.text = getFormattedNumber(item.basePrice)
                        textBasePrice.append(item.exposePriceText)
                        textBasePrice.visibility = View.VISIBLE
                        textBasePrice.paintFlags = textBasePrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    }
                    if (textDiscountRate != null) {
                        textDiscountRate.text = ProductInfoUtil.getDiscountCommon(context, item.discountRate)
                        textDiscountRate.ellipsize = null
                        textDiscountRate.visibility = View.VISIBLE
                    }
                } else {
                    ViewUtils.hideViews(textBasePrice, textDiscountRate)
                }
            }
        } catch (e: NullPointerException) {
            Ln.e(e.message)
        }
    }

    private fun getTextBadge(strOption: String?): TextView {
        val textOption = TextView(context)
        if (strOption == null) {
            return textOption
        }
        val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
//                            LayoutParams.WRAP_CONTENT, DisplayUtils.convertDpToPx(context, 25f)
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT
        )
        params.gravity = Gravity.CENTER
        params.rightMargin = DisplayUtils.convertDpToPx(context, 4f)
        textOption.layoutParams = params
        textOption.textSize = 13f
        textOption.maxLines = 1

        val drawable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.getDrawable(R.drawable.bg_line_e5e5e5)
        } else {
            context.resources.getDrawable(R.drawable.bg_line_e5e5e5)
        }

        textOption.background = drawable
        textOption.includeFontPadding = false
        textOption.setPadding(DisplayUtils.convertDpToPx(context, 8f), DisplayUtils.convertDpToPx(context, 4f),
                DisplayUtils.convertDpToPx(context, 8f), DisplayUtils.convertDpToPx(context, 4f))

        var strTemp = strOption

        textOption.text = strTemp
        return textOption
    }

    fun View.setMargins(
            left: Int? = null,
            top: Int? = null,
            right: Int? = null,
            bottom: Int? = null,
    ) {
        val lp = layoutParams as? MarginLayoutParams
                ?: return

        lp.setMargins(
                left ?: lp.leftMargin,
                top ?: lp.topMargin,
                right ?: lp.rightMargin,
                bottom ?: lp.rightMargin
        )

        layoutParams = lp
    }

    private fun View.dpToPx(dp: Float): Int = context.dpToPx(dp)
    private fun Context.dpToPx(dp: Float): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()
}