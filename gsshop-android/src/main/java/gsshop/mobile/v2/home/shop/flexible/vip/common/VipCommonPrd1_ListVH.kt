/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible.vip.common

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.gsshop.mocha.ui.util.ViewUtils
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.main.SectionContentList
import gsshop.mobile.v2.home.shop.renewal.publicusage.Prd1_ListVH
import roboguice.util.Ln

/**
 * 홈 하단 개인화구좌(편성표 부상품 형태) 단품 item
 */
class VipCommonPrd1_ListVH
/**
 * @param itemView
 */
(itemView: View?) : Prd1_ListVH(itemView) {

    private val mTxtBrdTime = itemView?.findViewById<TextView>(R.id.text_brd_time_top)
    private val mTxtPurchaseCnt = itemView?.findViewById<TextView>(R.id.txt_purchase_cnt)
    private val mTxtCartDiscnt = itemView?.findViewById<TextView>(R.id.txt_cart_disount)
    private val mTxtPrice = itemView?.findViewById<TextView>(R.id.txt_base_price)
    private val mTxtPriceUnit = itemView?.findViewById<TextView>(R.id.txt_base_price_unit)
    private val mTxtDiscntRate = itemView?.findViewById<TextView>(R.id.txt_discount_rate)

    val mBorderLine = itemView?.findViewById<View>(R.id.view_bottom_divider_1dp)

    override fun setItems(context: Context?, item: SectionContentList?, isSameToNext: Boolean): View {
        super.setItems(context, item, isSameToNext)
        try {
            if (!TextUtils.isEmpty(item?.alarmText)) {
                if (mTxtBrdTime != null) {
                    mTxtBrdTime.visibility = View.VISIBLE
                    mTxtBrdTime.text = item?.alarmText
                }
            }

            if (item?.imgBadgeCorner?.LT != null && item.imgBadgeCorner!!.LT.size > 0) {
                if (!TextUtils.isEmpty(item.imgBadgeCorner!!.LT[0].text)) {
                    if (mTxtPurchaseCnt != null) {
                        mTxtPurchaseCnt.visibility = View.VISIBLE
                        mTxtPurchaseCnt.text = item.imgBadgeCorner!!.LT[0].text
                    }
                }
            }
            // imageLayerFlag 만 확인 후 품절 여부 표시
            if(!TextUtils.isEmpty(item?.imageLayerFlag) && prdComment != null) {
                prdComment.visibility = View.VISIBLE
                if (AIR_BUY.equals(item?.imageLayerFlag, ignoreCase = true)) {
                    prdComment.setText(R.string.layer_flag_air_buy)
                }
                else if (SOLD_OUT.equals(item!!.imageLayerFlag, ignoreCase = true)) {
                    prdComment.setText(R.string.layer_flag_sold_out)
                }
                else {
                    prdComment.visibility = View.GONE
                }
            }

            if (mTxtCartDiscnt != null) {
                if (TextUtils.isEmpty(item?.cartDiscountRateText)) {
                    mTxtCartDiscnt.visibility = View.GONE
                } else {
                    mTxtCartDiscnt.text = item?.cartDiscountRateText
                    mTxtCartDiscnt.visibility = View.VISIBLE
                    ViewUtils.hideViews(mTxtPrice, mTxtPriceUnit, mTxtDiscntRate)
                }
            }
        }
        catch (e: NullPointerException) {
            Ln.e(e.message)
        }

        return itemView
    }
}