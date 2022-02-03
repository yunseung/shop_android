/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible.wine

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.shop.BaseViewHolder
import gsshop.mobile.v2.home.shop.ShopInfo
import gsshop.mobile.v2.web.WebUtils
import kotlinx.android.synthetic.main.view_holder_ban_txt_exp_wine.view.*
import kotlinx.android.synthetic.main.banner_item_ban_exp_wine.view.*
import kotlinx.android.synthetic.main.banner_item_ban_exp_wine_item.view.*
import roboguice.util.Ln
import java.lang.IndexOutOfBoundsException

/**
 * 리뉴얼 된 홀더는 모두 새로 CP 로 생성.
 */
@SuppressLint("NewApi")
class BanTxtExpWineVH(itemView: View) : BaseViewHolder(itemView) {

    override fun onBindViewHolder(context: Context?, position: Int, info: ShopInfo?,
                                  action: String?, label: String?, sectionName: String?) {
        val item = info?.contents?.get(position)?.sectionContent ?: return

        if(!TextUtils.isEmpty(item.name)) {
            itemView.rootView.txt_main.text = item.name
        }

        if (item.subProductList == null) return

        var layoutAdd : View? = null
        var viewSet : View? = null
        val inflater : LayoutInflater = LayoutInflater.from(context)

        itemView.rootView.view_add.removeAllViews()

        try {
            for (i in 0 until item.subProductList!!.size) {
                if ((i % 2) == 0) { // 짝수
                    layoutAdd = inflater.inflate(R.layout.banner_item_ban_exp_wine, null)
                    layoutAdd.item_left.visibility = View.VISIBLE

                    viewSet = layoutAdd.item_left

                    itemView.rootView.view_add.addView(layoutAdd)
                }
                else {
                    layoutAdd?.item_right?.visibility = View.VISIBLE
                    viewSet = layoutAdd?.item_right
                }

                // 랭크 설정
                if (!TextUtils.isEmpty(item.subProductList!![i].saleQuantity) &&
                        viewSet != null) {
                    viewSet.txt_rank.text = item.subProductList!![i].saleQuantity
                }

                // 키워드 설정
                if (!TextUtils.isEmpty(item.subProductList!![i].productName) &&
                        viewSet != null) {
                    viewSet.txt_keyword.text = item.subProductList!![i].productName
                }

                // 선택 시 이동할 url 설정
                if (!TextUtils.isEmpty(item.subProductList!![i].linkUrl) &&
                        viewSet != null) {
                    viewSet.setOnClickListener {
                        WebUtils.goWeb(context, item.subProductList!![i].linkUrl)
                    }
                }
            }
        }
        catch (e:NullPointerException) {
            Ln.e(e.message)
        }
        catch (e:IndexOutOfBoundsException) {
            Ln.e(e.message)
        }
    }
}