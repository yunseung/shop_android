package gsshop.mobile.v2.home.shop.flexible.shoppinglive

import android.content.Context
import android.view.View
import gsshop.mobile.v2.home.shop.BaseViewHolder
import gsshop.mobile.v2.home.shop.ShopInfo
import gsshop.mobile.v2.web.WebUtils
import kotlinx.android.synthetic.main.view_holder_title_mobile_live.view.*

class TitleMobileLiveVH(itemView: View) : BaseViewHolder(itemView) {

    override fun onBindViewHolder(context: Context?, position: Int, info: ShopInfo?, action: String?, label: String?, sectionName: String?) {
        super.onBindViewHolder(context, position, info, action, label, sectionName)

        val item = info?.contents?.get(position)?.sectionContent ?: return

        if (!item.moreBtnUrl.isNullOrEmpty()) {
            itemView.ll_more_btn_area.visibility = View.VISIBLE

            itemView.ll_more_btn_area.setOnClickListener { WebUtils.goWeb(context, item.moreBtnUrl) }

            if (!item.subName.isNullOrEmpty())
                itemView.tv_sub_name.text = item.subName
        } else {
            itemView.ll_more_btn_area.visibility = View.GONE
        }

        if (!item.name.isNullOrEmpty()) {
            itemView.tv_name.text = item.name
        }

        if (!item.showLiveIcon.isNullOrEmpty() && "Y".equals(item.showLiveIcon, ignoreCase = true)) {
            itemView.iv_img_title.visibility = View.VISIBLE
        }
        else {
            itemView.iv_img_title.visibility = View.GONE
        }

        if (!item.showUnderLine.isNullOrEmpty() && "N".equals(item.showUnderLine, ignoreCase = true)) {
            itemView.iv_underline.visibility = View.GONE
        }
        else {
            itemView.iv_underline.visibility = View.VISIBLE
        }
    }

    override fun onViewAttachedToWindow() {
        super.onViewAttachedToWindow()
    }

    override fun onViewDetachedFromWindow() {
        super.onViewDetachedFromWindow()
    }
}