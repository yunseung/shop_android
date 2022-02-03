/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible.vip

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.TextUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.shop.BaseViewHolder
import gsshop.mobile.v2.home.shop.ShopInfo
import gsshop.mobile.v2.util.ImageUtil
import kotlinx.android.synthetic.main.view_holder_ban_card_vip_common.view.*
import kotlinx.android.synthetic.main.view_holder_ban_vip_card_gba.view.*
import roboguice.util.Ln

class BanVIPCardGbaVH(itemView: View) : BaseViewHolder(itemView) {
    // 상단 공통 VIP 타이틀
    private val mTitleVip = itemView.view_title_vip
    // cardView가 추가될 뷰
    private val mViewAdd = itemView.view_add

    private val mViewBack = itemView.view_back

    override fun onBindViewHolder(context: Context, position: Int, info: ShopInfo?,
                                  action: String?, label: String?, sectionName: String?) {
        // item 이랑 vipCardDiscount 가 null 이면 표시핧 수 없기 때문에 return.
        val item = info?.contents?.get(position)?.sectionContent ?: return

        mViewBack.setNew("Y".equals(item.newYN, ignoreCase = true))

        mTitleVip.setItems(item)
        item.vipCardDiscount ?: return

        var nNumOfCard = 0
        try {
            for (pItem in info?.contents) {
                if (pItem.sectionContent.viewType == "BAN_VIP_CARD_GBA") {
                    nNumOfCard++
                }
            }
        }
        catch (e : Exception) {
            Ln.e(e.message)
        }

        mViewAdd.removeAllViews()
//        for (vipCardDcnt in item.vipCardDiscount!!) {
        var strDescription = ""
        for (i in 0..item.vipCardDiscount!!.size - 1) {
            var vipCardDcnt = item.vipCardDiscount!![i]
            val cardView = LayoutInflater.from(context).inflate(R.layout.view_holder_ban_card_vip_common, null)
            if (i >= item.vipCardDiscount!!.size - 1) {
                cardView.view_bottom_divider_1dp.visibility = View.GONE
            }

            if (!TextUtils.isEmpty(vipCardDcnt.imgUrl)) {
                ImageUtil.loadImageFit(context, vipCardDcnt.imgUrl, cardView.img_right, R.drawable.noimage_375_188)
            }

            if (TextUtils.isEmpty(vipCardDcnt.text)) {
                cardView.text.visibility = View.GONE
            }
            else {
                cardView.text.text = vipCardDcnt.text
                strDescription = vipCardDcnt.text!!
            }

            if (vipCardDcnt.addInfoList == null) {
                continue
            }

            // 카드 크기가 하나일 경우에 좌측 마진 변경
//            var params:LinearLayout.LayoutParams = cardView.view_add_dscnt.layoutParams as LinearLayout.LayoutParams
//            if (nNumOfCard == 1) {
//                params.leftMargin = 0
//                params.gravity = Gravity.RIGHT
//            }
//            else {
//                params.leftMargin = DisplayUtils.convertDpToPx(context, 34f)
//                params.gravity = Gravity.LEFT
//            }
//            cardView.view_add_dscnt.layoutParams = params

            for(strDcnt in vipCardDcnt.addInfoList!!) {
                val textView = TextView(context)
                textView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                textView.setTextColor(Color.parseColor("#111111"))
                if (nNumOfCard == 1) {
                    // 텍스트에 오늘이 포함되어 있고 전체 카드 배너가 하나이고 현재 카드 배너의 카드 종류가 하나일때... 크기 22로...
                    // 변경 됨 오늘 포함 여부 상관 없이 카드 배너가 하나일 때 크기 20
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20f)
                }
                else {
                    // 작을 때 19
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19f)
                }

                textView.text = strDcnt
                textView.setTypeface(null, Typeface.BOLD)
                strDescription += textView.text.toString() + " "
                cardView.view_add_dscnt.addView(textView)
            }
            cardView.contentDescription = strDescription
            mViewAdd.addView(cardView)
        }
    }
}