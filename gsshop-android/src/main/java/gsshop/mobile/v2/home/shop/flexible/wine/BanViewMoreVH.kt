package gsshop.mobile.v2.home.shop.flexible.wine

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.LinearLayout
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.shop.BaseViewHolder
import gsshop.mobile.v2.home.shop.ShopInfo
import gsshop.mobile.v2.home.shop.flexible.vip.common.VipCommonReadMore
import gsshop.mobile.v2.web.WebUtils
import kotlinx.android.synthetic.main.view_holder_common_add_sub.view.*
import roboguice.util.Ln

open class BanViewMoreVH (itemView: View) : BaseViewHolder(itemView) {

    protected var viewMore: VipCommonReadMore? = null

    override fun onBindViewHolder(context: Context, position: Int, info: ShopInfo?,
                                  action: String?, label: String?, sectionName: String?) {
        val item = info?.contents?.get(position)?.sectionContent ?: return

//        Ln.e("hklim 1 position : " + position + " / item.linkUrl : " + item.linkUrl)

        viewMore = VipCommonReadMore(context)

        if (viewMore == null) {
            return
        }

        val params:LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        )
        try {
            viewMore?.layoutParams = params
        }
        catch (e:IllegalArgumentException) {
            Ln.e(e.message)
        }

        viewMore?.setViewResources(R.layout.view_holder_ban_read_more_wine)
        viewMore?.setItems(item, VipCommonReadMore.ENUM_TYPE_MORE.TYPE_GO_WEB)

        itemView.view_add_sub.removeAllViews()
        itemView.view_add_sub.addView(viewMore)

        if (!TextUtils.isEmpty(item.linkUrl)) {
            try {
                itemView.root_view.setOnClickListener {
//                    Ln.e("hklim 2 position : " + position + " / item.linkUrl : " + item.linkUrl)
                    WebUtils.goWeb(context, item.linkUrl)
                }
            }
            catch (e:NullPointerException) {
                Ln.e(e.message)
            }
        }
    }
}