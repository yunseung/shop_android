package gsshop.mobile.v2.home.shop.flexible.shoppinglive

import android.content.Context
import android.text.TextUtils
import android.view.View
import gsshop.mobile.v2.home.shop.BaseViewHolder
import gsshop.mobile.v2.home.shop.ShopInfo
import kotlinx.android.synthetic.main.view_holder_ban_shoppinglive_title_sub_added.view.*

class BanMLPrdTitleSubAddedMobileLive (itemView: View) : BaseViewHolder(itemView) {

    private val mTxtTitle = itemView.txt_title
    private val mTxtTitleSub = itemView.txt_title_sub

    override fun onBindViewHolder(context: Context, position: Int, info: ShopInfo?,
                                  action: String?, label: String?, sectionName: String?) {
        val item = info?.contents?.get(position)?.sectionContent ?: return

        if (!TextUtils.isEmpty(item.name)) {
            mTxtTitle.text = item.name
        }
        if (!TextUtils.isEmpty(item.subName)) {
            mTxtTitleSub.text = item.subName
        }
    }
}