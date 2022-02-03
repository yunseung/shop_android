package gsshop.mobile.v2.home.shop.bestshop

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import gsshop.mobile.v2.home.shop.BaseViewHolder
import gsshop.mobile.v2.home.shop.ShopInfo
import kotlinx.android.synthetic.main.view_holder_menu_gift_recommend_fix.view.*

class MenuGiftRecommendFixVH(itemView: View) : BaseViewHolder(itemView) {
    private val mTvTitle: TextView = itemView.tv_title
    private val mTitleLine: View = itemView.title_line
    private val mClFirstRow: ConstraintLayout = itemView.cl_first_row
    private val mClSecondRow: ConstraintLayout = itemView.cl_second_row

    private val mItem11: MenuGiftRecommendFixItem = itemView.item_1_1
    private val mItem12: MenuGiftRecommendFixItem = itemView.item_1_2
    private val mItem13: MenuGiftRecommendFixItem = itemView.item_1_3
    private val mItem14: MenuGiftRecommendFixItem = itemView.item_1_4
    private val mItem15: MenuGiftRecommendFixItem = itemView.item_1_5
    private val mItem21: MenuGiftRecommendFixItem = itemView.item_2_1
    private val mItem22: MenuGiftRecommendFixItem = itemView.item_2_2
    private val mItem23: MenuGiftRecommendFixItem = itemView.item_2_3
    private val mItem24: MenuGiftRecommendFixItem = itemView.item_2_4
    private val mItem25: MenuGiftRecommendFixItem = itemView.item_2_5

    private val mFirstRowItems: ArrayList<MenuGiftRecommendFixItem> = arrayListOf()
    private val mSecondRowItems: ArrayList<MenuGiftRecommendFixItem> = arrayListOf()

    init {
        mFirstRowItems.add(mItem11)
        mFirstRowItems.add(mItem12)
        mFirstRowItems.add(mItem13)
        mFirstRowItems.add(mItem14)
        mFirstRowItems.add(mItem15)

        mSecondRowItems.add(mItem21)
        mSecondRowItems.add(mItem22)
        mSecondRowItems.add(mItem23)
        mSecondRowItems.add(mItem24)
        mSecondRowItems.add(mItem25)
    }

    override fun onBindViewHolder(
        context: Context?,
        position: Int,
        info: ShopInfo?,
        action: String?,
        label: String?,
        sectionName: String?
    ) {
        super.onBindViewHolder(context, position, info, action, label, sectionName)

        var item = info?.contents?.get(position)?.sectionContent ?: return

        if (TextUtils.isEmpty(item.productName)) {
            mTvTitle.visibility = View.GONE
            mTitleLine.visibility = View.GONE
        } else {
            mTvTitle.text = item.productName
        }

        if (item.subProductList != null) {
            if (item.subProductList!!.size < 2) {
                mClSecondRow.visibility = View.GONE
                for (j in item.subProductList!![0].subProductList!!.indices) {
                    mFirstRowItems[j].setItem(item.subProductList!![0].subProductList!![j])
                }
            } else if (item.subProductList!!.size == 2) {
                for (i in item.subProductList!!.indices) {
                    if (i == 0) {
                        for (j in item.subProductList!![i].subProductList!!.indices) {
                            mFirstRowItems[j].setItem(item.subProductList!![i].subProductList!![j])
                        }
                    } else {
                        for (j in item.subProductList!![i].subProductList!!.indices) {
                            mSecondRowItems[j].setItem(item.subProductList!![i].subProductList!![j])
                        }
                    }
                }
            }
        }
    }
}