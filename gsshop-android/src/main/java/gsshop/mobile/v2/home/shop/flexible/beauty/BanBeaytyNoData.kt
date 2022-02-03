package gsshop.mobile.v2.home.shop.flexible.beauty

import android.content.Context
import android.text.TextUtils
import android.view.View
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.shop.BaseViewHolder
import gsshop.mobile.v2.home.shop.ShopInfo
import gsshop.mobile.v2.util.ImageUtil
import kotlinx.android.synthetic.main.view_holder_beauty_no_data.view.*

class BanBeaytyNoData (itemView: View) : BaseViewHolder(itemView) {

    private val mRootView = itemView.root_view

    override fun onBindViewHolder(context: Context, position: Int, info: ShopInfo?,
                                  action: String?, label: String?, sectionName: String?) {
        val item = info?.contents?.get(position)?.sectionContent ?: return

        if (TextUtils.isEmpty(item.imageUrl)) {
            mRootView.image.visibility = View.GONE

            mRootView.layout_text.visibility = View.VISIBLE

            if (!TextUtils.isEmpty(item.name)) {
                mRootView.text_main.visibility = View.VISIBLE
                mRootView.text_main.text = item.name
            }
            else {
                mRootView.text_main.visibility = View.GONE
            }

            if (!TextUtils.isEmpty(item.subName)) {
                mRootView.text_sub.visibility = View.VISIBLE
                mRootView.text_sub.text = item.subName
            }
            else {
                mRootView.text_sub.visibility = View.GONE
            }

        }
        else {
            ImageUtil.loadImageResize(context, item.imageUrl, mRootView.image, R.drawable.noimage_375_188)
            mRootView.image.visibility = View.VISIBLE

            mRootView.layout_text.visibility = View.GONE
        }

    }
}