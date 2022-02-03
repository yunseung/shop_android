package gsshop.mobile.v2.home.shop.flexible.wine

import android.content.Context
import android.text.TextUtils
import android.view.View
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.shop.BaseViewHolder
import gsshop.mobile.v2.home.shop.ShopInfo
import gsshop.mobile.v2.util.ImageUtil
import kotlinx.android.synthetic.main.view_holder_ban_title_wine.view.*

class BanTitleWineVH (itemView: View) : BaseViewHolder(itemView) {

    private val mTxtTitle = itemView.txt_title

    private val mImgTitle = itemView.img_title

    private val mSeperator = itemView.view_seperator

    override fun onBindViewHolder(context: Context, position: Int, info: ShopInfo?,
                                  action: String?, label: String?, sectionName: String?) {
        val item = info?.contents?.get(position)?.sectionContent ?: return

        if (!TextUtils.isEmpty(item.name)) {
            mTxtTitle.text = item.name
        }

        if (!TextUtils.isEmpty(item.imageUrl)) {
            mImgTitle.visibility = View.VISIBLE

            ImageUtil.loadImageResizeBadge(context, item.imageUrl, mImgTitle, R.drawable.noimg_logo, ImageUtil.BaseImageResolution.HD)
        }
        else {
            mSeperator.visibility = View.GONE
            mImgTitle.visibility = View.GONE
        }
    }
}