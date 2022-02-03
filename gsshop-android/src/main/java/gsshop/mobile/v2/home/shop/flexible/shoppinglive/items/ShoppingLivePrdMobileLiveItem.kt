/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible.shoppinglive.items

import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginBottom
import com.gsshop.mocha.ui.util.ViewUtils
import de.greenrobot.event.EventBus
import gsshop.mobile.v2.Events
import gsshop.mobile.v2.Keys
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.main.ProductInfoUtil
import gsshop.mobile.v2.home.main.SectionContentList
import gsshop.mobile.v2.home.shop.renewal.flexible.RenewalPriceInfoBottomViewHolder
import gsshop.mobile.v2.util.DisplayUtils.getFormattedNumber
import gsshop.mobile.v2.util.DisplayUtils.getNumberFromString
import gsshop.mobile.v2.util.DisplayUtils.isValidNumberStringExceptZero
import gsshop.mobile.v2.util.DisplayUtils.isValidString
import gsshop.mobile.v2.util.ImageUtil
import gsshop.mobile.v2.util.StringUtils
import gsshop.mobile.v2.web.WebUtils
import kotlinx.android.synthetic.main.recycler_item_shopping_live_prd_mobile_live.view.*
import roboguice.util.Ln
import java.util.*

/**
 * 쇼핑라이브 오늘의 라이브 아이템
 */
class ShoppingLivePrdMobileLiveItem (itemView: View) {

    private val COUNSELING_FONT_SIZE = 16f

    private var mItemView: View

    private val AIR_BUY = "AIR_BUY"
    private val SOLD_OUT = "SOLD_OUT"
    private val SALE_END = "SALE_END"   // 판매 종료 추가됨
    private val TO_SALE = "TO_SALE"     // 판매 예정 추가됨.

    private var mStartDate : Long? = null

    /**
     * 타이머 타스크
     */
    private var timerTask : Timer? = Timer()

    init {
        mItemView = itemView
    }

    fun onBindViewHolder(context: Context, item: SectionContentList?): View? {
        return setItems(context, item)
    }

    fun getView(): View? {
        return mItemView
    }

    private fun setItems(context: Context, item: SectionContentList?): View {
        if (item == null) {
            return mItemView;
        }

        mItemView.img_main.contentDescription = context.getString(R.string.description_image)

        if (!TextUtils.isEmpty(item.titleImgUrl)) {
            ImageUtil.loadImageFitCenter(context, item.titleImgUrl, mItemView.img_main, R.drawable.noimage_164_246)
        }

        if (!TextUtils.isEmpty(item.imageUrl)) {
            ImageUtil.loadImageFitCenter(context, item.imageUrl, mItemView.img_sub, R.drawable.noimage_78_78)
        }

        if (!TextUtils.isEmpty(item.promotionName)) {
            mItemView.txt_title_main.text = item.promotionName
        }

        if (!TextUtils.isEmpty(item.subName)) {
            mItemView.tv_sub_name.visibility = View.VISIBLE
            mItemView.tv_sub_name.text = item.subName
            mItemView.view_bottom_margin_8.visibility = View.GONE
        } else {
            mItemView.tv_sub_name.visibility = View.GONE
            mItemView.view_bottom_margin_8.visibility = View.VISIBLE
        }

        if (!TextUtils.isEmpty(item.productName)) {
            mItemView.txt_title_sub.text = item.productName
        }

        if (!TextUtils.isEmpty(item.streamViewCount)) {
            mItemView.view_live_count.visibility = View.VISIBLE
            mItemView.txt_live_count.text = item.streamViewCount
        }
        else {
            mItemView.view_live_count.visibility = View.GONE
        }

        if (!TextUtils.isEmpty(item.pgmLiveYn) && "Y".equals(item.pgmLiveYn, ignoreCase = true)) {
            mItemView.img_video_play.visibility = View.VISIBLE
        }
        else {
            mItemView.img_video_play.visibility = View.GONE
        }

        if (!TextUtils.isEmpty(item.videoTime)) {
            mItemView.txt_time.visibility = View.VISIBLE
            mItemView.txt_time.text = changeTimeFormatToAmPm(item.videoTime!!)
        }
        else {
            mItemView.txt_time.visibility = View.GONE
        }

        mStartDate = item.startDate

//        //test용
//        mStartDate = System.currentTimeMillis() + (BanMLPrdMobileLive.testCnt * 10000)
//        BanMLPrdMobileLive.testCnt ++

        mItemView.tv_broad_start.visibility = View.GONE
        mItemView.tv_start_date.visibility = View.GONE

        mItemView.txt_comment.visibility = View.GONE
        if (item.directOrdInfo == null) {
            mItemView.txt_comment.visibility = View.VISIBLE
            if (AIR_BUY.equals(item.imageLayerFlag, ignoreCase = true)) {
                mItemView.txt_comment.setText(R.string.layer_flag_air_buy)
            } else if (SOLD_OUT.equals(item.imageLayerFlag, ignoreCase = true)) {
                mItemView.txt_comment.setText(R.string.layer_flag_sold_out)
            } else if (SALE_END.equals(item.imageLayerFlag, ignoreCase = true)) {
                mItemView.txt_comment.setText(R.string.layer_flag_sale_end)
            }
            else if (TO_SALE.equals(item.imageLayerFlag, ignoreCase = true)) {
                mItemView.txt_comment.setText(R.string.layer_flag_to_sale)
            }
            else {
                mItemView.txt_comment.visibility = View.GONE
            }
        }

        setPriceInfo(context, item)

        mItemView.txt_benefit.text = ""
        if(item.source != null || item.allBenefit != null) {
            mItemView.txt_benefit.setData(item)
//            mItemView.txt_benefit.text = item.source!!.text
        }

        if (!TextUtils.isEmpty(item.linkUrl)) {
            mItemView.root.setOnClickListener {
                WebUtils.goWeb(context, item.linkUrl)
            }
        }

        if (!TextUtils.isEmpty(item.brandLinkUrl)) {
            mItemView.img_main.setOnClickListener{
                WebUtils.goWeb(context, item.brandLinkUrl)
            }
        }

        return mItemView
    }

    /**
     * 판매 가격 표시 로직
     */
    private fun setPriceInfo(context: Context, item: SectionContentList) {
//        mItemView.txt_base_price
//        mItemView.txt_base_price_unit
//        mItemView.txt_price
//        mItemView.txt_price_unit
//        mItemView.txt_discount_rate
//        mItemView.txt_rental_text

        mItemView.txt_rental_text.visibility = View.GONE
        mItemView.txt_base_price.visibility = View.GONE
        mItemView.layout_base.visibility = View.GONE

        val priceStringBuilder = SpannableStringBuilder()

        // 퍼센트값으로 표현되는 할인률이 있을 경우에만 base price 표시
        // base price가 0이거나 salePrice가 basePrice보다 크면 basePrice 표시 안함
        val salePrice = getNumberFromString(item.salePrice)
        val basePrice = getNumberFromString(item.basePrice)

        // 베이스가 처리로직: 할인률 있으면
        if (item.discountRate != null && !item.discountRate!!.isEmpty() && item.discountRate!!.toInt() > Keys.ZERO_DISCOUNT_RATE) {
            //베이스가 로직==찌익그은가, 찌익 그은가는 할인율이 없거나, 세일가보다 비싸면 안보여야함 디폴트는 안보임
            if (isValidNumberStringExceptZero(item.basePrice) && salePrice < basePrice) {
                if (mItemView.txt_base_price != null) {
                    mItemView.layout_base.visibility = View.VISIBLE
                    mItemView.txt_base_price.setText(getFormattedNumber(item.basePrice))
                    mItemView.txt_base_price.append(item.exposePriceText)
                    mItemView.txt_base_price.setVisibility(View.VISIBLE)
                    mItemView.txt_base_price.setPaintFlags(mItemView.txt_base_price.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
                }
                if (mItemView.txt_discount_rate != null) {
                    mItemView.txt_discount_rate.append(ProductInfoUtil.getDiscountCommon(context, item.discountRate))
                    mItemView.txt_discount_rate.setEllipsize(null)
                    mItemView.txt_discount_rate.setVisibility(View.VISIBLE)
                }
            } else {
                ViewUtils.hideViews(mItemView.txt_base_price, mItemView.layout_base,/*mItemView.txt_base_price_unit,*/ mItemView.txt_discount_rate)
            }
        }

        // 세일 price 처리 로직 : 있으면 그려 / else 면 안그려
        if (isValidString(item.salePrice)) {
            val salePriceText = getFormattedNumber(item.salePrice)
            if (mItemView.txt_price_unit != null) {
                mItemView.txt_price_unit.setText(item.exposePriceText)
                mItemView.txt_price_unit.setVisibility(View.VISIBLE)
            }
            priceStringBuilder.append(salePriceText)
            if (mItemView.txt_price != null) {
                mItemView.txt_price.append(priceStringBuilder)
                mItemView.txt_price.setVisibility(View.VISIBLE)
            }
        } else {
            ViewUtils.hideViews(mItemView.txt_price, mItemView.txt_price_unit)
        }

        /**
         * 다 처리하고, 상품 종류에 따른 처리를 하는것이 맞는가 일단은 가격 표시 영역의 그룹은 잘 해놨기 때문에 추후에 문제가 발생하면 처리가 가능할거라 본다
        */
        // deal 상품이면서 렌탈, 여행, 시공, 딜 등의 무형 상품일 때 ( 렌탈이 아닌경우에는 rRentalText 없다고 확답 받음 )
        // 혹은 deal 상품이 아니면서 렌탈일 때
        if (("true" == item.deal && ("R" == item.productType || "T" == item.productType || "U" == item.productType || "S" == item.productType /*상품 시공*/)) ||
                "false" == item.deal && "R" == item.productType /* 렌탈*/) {
            //항목1 무언가 써있으면???
            // 렌탈 에서만 rRentalText 를 비교한다.
            if ("R" == item.productType && item.rentalText != null && !item.rentalText!!.isEmpty()) {
                //항목1에 "월 렌탈료"가 써있으면 -> "월"로 변경
                //API에서 항목1에 "월"이 내려올 수 있는 조건이 추가되어 "월"도 "월 렌탈료"와 동일로직으로 처리하도록 조건 추가
                if (RenewalPriceInfoBottomViewHolder.COMMON_RENTALTEXT == item.rentalText || RenewalPriceInfoBottomViewHolder.COMMON_DISPLAY_RENTALTEXT == item.rentalText) {
                    if (mItemView.txt_rental_text != null) {
                        mItemView.txt_rental_text.setText(RenewalPriceInfoBottomViewHolder.COMMON_DISPLAY_RENTALTEXT)
                        mItemView.txt_rental_text.setVisibility(View.VISIBLE)
                    }

                    //"월" + 항목2(rRentalPrice) 값이 있으면 뿌린다
                    if (item.mnlyRentCostVal != null && !item.mnlyRentCostVal!!.isEmpty() && !item.mnlyRentCostVal!!.startsWith("0")) {
                        if (mItemView.txt_price != null) {
                            mItemView.txt_price.setText(item.mnlyRentCostVal)
                            mItemView.txt_price.setVisibility(View.VISIBLE)
                        }
                        // 원자 지우기 ,
                        ViewUtils.hideViews(mItemView.txt_price_unit)
                    } else {
                        //렌탈이지만 "월 렌탈료"가 안써있으면 다 우지고 "상담 전용 상품입니다." 세긴다.
                        //mItemView.txt_price.setText("상담 전용 상품입니다.");
                        if (mItemView.txt_price != null) {
                            mItemView.txt_price.setText(R.string.common_rental_title)
                            mItemView.txt_price.setVisibility(View.VISIBLE)
                            mItemView.txt_price.setTextSize(COUNSELING_FONT_SIZE)
                        }
                        ViewUtils.hideViews(mItemView.txt_rental_text, mItemView.txt_price_unit)
                    }
                } else { //렌탈인데 월이 아니면,
                    if (item.mnlyRentCostVal != null && !item.mnlyRentCostVal!!.isEmpty() && !item.mnlyRentCostVal!!.startsWith("0")) {
                        if (mItemView.txt_price != null) {
                            mItemView.txt_price.setText(item.mnlyRentCostVal)
                            mItemView.txt_price.setVisibility(View.VISIBLE)
                        }
                        // rRentalPrice 게 없으니 "월" 영역 지우기 // 원자 지우기 ,
                        ViewUtils.hideViews(mItemView.txt_rental_text, mItemView.txt_price_unit)
                    } else {
                        //"월" 을 지우고, "상담 전용 상품입니다." 세긴다.
                        //mItemView.txt_price.setText("상담 전용 상품입니다.");
                        if (mItemView.txt_price != null) {
                            mItemView.txt_price.setText(R.string.common_rental_title)
                            mItemView.txt_price.setVisibility(View.VISIBLE)
                            mItemView.txt_price.setTextSize(COUNSELING_FONT_SIZE)
                        }
                        // rRentalPrice 게 없으니 "월" 영역 지우기 // 원자 지우기 ,
                        ViewUtils.hideViews(mItemView.txt_rental_text, mItemView.txt_price_unit)
                    }
                }
            } else if (item.mnlyRentCostVal != null && !item.mnlyRentCostVal!!.isEmpty() && !item.mnlyRentCostVal!!.startsWith("0")) {
                if (mItemView.txt_price != null) {
                    mItemView.txt_price.setText(item.mnlyRentCostVal)
                    mItemView.txt_price.setVisibility(View.VISIBLE)
                }
                // rRentalPrice 게 없으니 "월" 영역 지우기 // 원자 지우기 ,
                ViewUtils.hideViews(mItemView.txt_rental_text, mItemView.txt_price_unit)
            } else {
                //렌탈이지만 "월 렌탈료"가 안써있으면 다 지우고 "상담 전용 상품입니다." 세긴다.
                //mItemView.txt_price.setText("상담 전용 상품입니다.");
                if (mItemView.txt_price != null) {
                    mItemView.txt_price.setText(R.string.common_rental_title)
                    mItemView.txt_price.setVisibility(View.VISIBLE)
                    mItemView.txt_price.setTextSize(COUNSELING_FONT_SIZE)
                }

                // rRentalPrice 게 없으니 "월" 영역 지우기 // 원자 지우기 ,
                ViewUtils.hideViews(mItemView.txt_rental_text, mItemView.txt_price_unit)
            }
        } else {
            //렌탈 아닌데 상담 전용 상품 뜨는경우가 없다고 하자
            ViewUtils.hideViews(mItemView.txt_rental_text)

            //보험일때, 계속 추가 할게요
            if ("I" == item.productType) {
                //보험이면 타이틀만 보이자
                //layout_price.setVisibility(View.GONE);
                // Null pointer 발생하는 경우 있음. null이라도 안죽는 함수로 변경
                ViewUtils.hideViews(mItemView.txt_price, mItemView.txt_price_unit, mItemView.txt_base_price, mItemView.layout_base,/*mItemView.txt_base_price_unit,*/ mItemView.txt_discount_rate)
            } else {
                if (mItemView.txt_price != null) {
                    mItemView.txt_price.setText(item.salePrice)
                    mItemView.txt_price.setVisibility(View.VISIBLE)
                    mItemView.txt_price_unit.setVisibility(View.VISIBLE)
                }
            }
        }
    }

    fun setStartDate(context: Context) {

        if (mStartDate != null) {
            mItemView.tv_broad_start.visibility = View.VISIBLE
            mItemView.tv_start_date.visibility = View.VISIBLE
            executeTask(context, mStartDate!!)
        }
    }

    /**
     * 타스크를 생성한다.
     *
     * @param delay
     */
    private fun executeTask(context: Context, endTime: Long) {
        stopTask()
        timerTask = Timer()
        try {
            timerTask!!.schedule(object : TimerTask() {
                override fun run() {
                    setRemainTime(context, endTime)
                }
            }, 1000, 1000)
        }
        catch (e:IllegalStateException){
            Ln.e(e.message)
        }
        catch (e:NullPointerException) {
            Ln.e(e.message)
        }
    }

    private fun stopTask() {
        if (timerTask != null) {

            timerTask!!.cancel()
            timerTask = null

        }
    }

    private fun setRemainTime(context: Context, endTime: Long) {

        var startTime: Long = System.currentTimeMillis()
        var defTime = endTime - startTime

        try {
            if (defTime > 0) {
                mItemView.tv_start_date.visibility = View.VISIBLE
                (context as Activity).runOnUiThread {
                    mItemView.tv_start_date.text = StringUtils.stringForHHMMSS(defTime, false)
                }
            } else {
                //해제확정 성공했을때제
                //알림해제 UI업데이트
                // 재호출시 부하를 줄이기 위해 4초에서 14초 사이의 랜덤값으로 api를 호출한다.


//                val randomInteger = (Random().nextInt(10) + 4) * 1000

                (context as Activity).runOnUiThread {
                    mItemView.tv_start_date.visibility = View.GONE
                    mItemView.tv_broad_start.visibility = View.GONE
//                    EventBus.getDefault().post(Events.ShoppingLiveEvent.MobileLiveSldRefreshEvent(true))
                }
                stopTask()
            }
        }
        catch (e:NullPointerException) {
            Ln.e(e.message)
        }
    }

    private fun changeTimeFormatToAmPm(time: String): String {
        var stringBuilder = StringBuilder()
        var hour = Integer.parseInt(time.split(":")[0])
        var min = Integer.parseInt(time.split(":")[1])

        if (hour in 0..11) {
            stringBuilder.append("오전 " + hour + "시 ")
        }
        else if (hour == 12) {
            stringBuilder.append("오후 12시 ")
        }
        else {
            stringBuilder.append("오후 " + (hour - 12) + "시 ")
        }

        if (min != 0) {
            stringBuilder.append(min.toString() + "분")
        }

        return stringBuilder.toString()
    }

    fun View.setMargins(
            left: Int? = null,
            top: Int? = null,
            right: Int? = null,
            bottom: Int? = null,
    ) {
        val lp = layoutParams as? ViewGroup.MarginLayoutParams
                ?: return

        lp.setMargins(
                left ?: lp.leftMargin,
                top ?: lp.topMargin,
                right ?: lp.rightMargin,
                bottom ?: lp.rightMargin
        )

        layoutParams = lp
    }

    fun View.dpToPx(dp: Float): Int = context.dpToPx(dp)
    private fun Context.dpToPx(dp: Float): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()
}