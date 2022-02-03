/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible.vip

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.shop.BaseViewHolder
import gsshop.mobile.v2.home.shop.ShopInfo
import gsshop.mobile.v2.home.shop.renewal.views.TextViewWithListenerWhenOnDraw
import gsshop.mobile.v2.util.ImageUtil
import kotlinx.android.synthetic.main.view_holder_ban_vip_gba.view.*

/*
    웰컴 메세지
 */
class BanVIPGbaVH(itemView: View) : BaseViewHolder(itemView) {

    private val mImgMain: ImageView = itemView.img_main
    private val mTxtName:TextViewWithListenerWhenOnDraw = itemView.txt_name
    private val mTxtMain = itemView.txt_main
    private val mViewMain = itemView.view_main

    private val mViewRoot = itemView.root

    // 수정 예정
    override fun onBindViewHolder(context: Context, position: Int, info: ShopInfo?,
                                  action: String?, label: String?, sectionName: String?) {
        val item = info?.contents?.get(position)?.sectionContent ?: return

        ImageUtil.loadImageResize(context, item.imageUrl, mImgMain, R.drawable.noimage_375_188)

        mTxtName.setOnDrawListener(object : TextViewWithListenerWhenOnDraw.OnDrawListener {
            override fun onDraw(view: View?) {
                val lineCnt: Int = mTxtName.getLineCount()
                if (lineCnt < 2) {
                    var params = mViewMain.layoutParams as RelativeLayout.LayoutParams
                    params.addRule(RelativeLayout.CENTER_VERTICAL)
                    mViewMain.layoutParams = params
                }
            }
        })
        mTxtName.text = item.name
        mTxtMain.text = item.subName
        mViewRoot.contentDescription = item.name + " " + item.subName
    }
}