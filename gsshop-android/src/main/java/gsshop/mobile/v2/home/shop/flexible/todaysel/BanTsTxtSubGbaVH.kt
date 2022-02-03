package gsshop.mobile.v2.home.shop.flexible.todaysel

import android.content.Context
import android.view.View
import gsshop.mobile.v2.home.main.SectionContentList
import gsshop.mobile.v2.home.shop.BaseViewHolder
import gsshop.mobile.v2.home.shop.ShopInfo
import gsshop.mobile.v2.web.WebUtils
import kotlinx.android.synthetic.main.view_holder_ts_txt_sub_gba.view.*
import roboguice.util.Ln

class BanTsTxtSubGbaVH(itemView: View?) : BaseViewHolder(itemView){
    /*
         * bind
         */
    override fun onBindViewHolder(context: Context, position: Int, info: ShopInfo?,
                                  action: String?, label: String?, sectionName: String?) {
        val item = info?.contents?.get(position)?.sectionContent ?: return
        setView(context, item)
    }

    private fun setView(context: Context, item: SectionContentList) {
        try {

            if (item.moreBtnUrl.isNullOrEmpty()) {
                itemView.view_big_more.visibility = View.GONE
                itemView.view_small_more.visibility = View.GONE
            }
            else {
                itemView.view_big_more.visibility = View.VISIBLE
                itemView.view_small_more.visibility = View.VISIBLE
                if (!item.moreText.isNullOrEmpty()) {
                    itemView.txt_big_more.text = item.moreText
                    itemView.txt_small_more.text = item.moreText
                }

                // 더보기 리스너 설정
                val listener = View.OnClickListener {
                    WebUtils.goWeb(context, item.moreBtnUrl)
                }

                itemView.view_big_more.setOnClickListener(listener)
                itemView.view_small_more.setOnClickListener(listener)
            }

            if (!item.name.isNullOrEmpty()) {
                itemView.txt_big_main.text = item.name
                itemView.txt_small_main.text = item.name
            }

            if (item.subName.isNullOrEmpty()) {
                itemView.view_big.visibility = View.GONE
                itemView.view_small.visibility = View.VISIBLE
            }
            else {
                itemView.view_big.visibility = View.VISIBLE
                itemView.view_small.visibility = View.GONE
                itemView.txt_big_sub.text = item.subName
            }

            if ("Y".equals(item.bdrBottomYn, ignoreCase = true)) {
                itemView.view_bottom_divider_1dp.visibility = View.VISIBLE
            }
            else {
                itemView.view_bottom_divider_1dp.visibility = View.GONE
            }
        }
        catch (e:NullPointerException) {
            Ln.e(e.message)
        }
    }
}