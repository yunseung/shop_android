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
import android.view.LayoutInflater
import android.view.View
import gsshop.mobile.v2.AbstractBaseActivity
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.main.SectionContentList
import gsshop.mobile.v2.home.shop.BaseViewHolder
import gsshop.mobile.v2.home.shop.ShopInfo
import gsshop.mobile.v2.home.shop.flexible.vip.common.VipCommonPrd1_ListVH
import gsshop.mobile.v2.home.shop.flexible.vip.common.VipCommonReadMore
import kotlinx.android.synthetic.main.view_holder_gr_prd_1_vip_list_common.view.*
import roboguice.util.Ln

class GrPrd1VIPListGbaVH(itemView: View) : BaseViewHolder(itemView) {
    // 상단 공통 VIP 타이틀
    private val mTitleVip = itemView.view_title_vip
    // 하단 공통 더보기
    private val mMoreView = itemView.view_read_more
    // 추가될 뷰
    private val mViewAdd = itemView.view_add

    private val mRootView = itemView.root

    private val mViewBack = itemView.view_back

    private val LAYOUT_ID = R.layout.view_holder_gr_prd_1_vip_list_item



//    private val MAX_ITEM_CNT = 5

    override fun onBindViewHolder(context: Context, position: Int, info: ShopInfo?,
                                  action: String?, label: String?, sectionName: String?) {
        val item = info?.contents?.get(position)?.sectionContent ?: return

        mViewBack.setNew("Y".equals(item.newYN, ignoreCase = true))

        mTitleVip.setItems(item)
        mMoreView.setItems(item, VipCommonReadMore.ENUM_TYPE_MORE.TYPE_EXTENSION)
        mRootView.contentDescription = mTitleVip.getTitleTxt()

        if (isExtension) {
            viewExtension(context, item)
        }
        else {
            mViewAdd.removeAllViews()
            mMoreView.visibility = View.VISIBLE

            if (item.subProductList != null && item.subProductList!!.size > 0) {
                val itemView = LayoutInflater.from(context).inflate(LAYOUT_ID, null)
                val subPrd = VipCommonPrd1_ListVH(itemView)
                if( subPrd.mBorderLine != null ) {
                    subPrd.mBorderLine.visibility = View.GONE
                    subPrd.mBorderLine.setBackgroundResource(android.R.color.transparent)
                }
                mViewAdd.addView(subPrd.onBindViewHolder(context, item.subProductList!![0]))
            }

            if (item.subProductList != null && item.subProductList!!.size < 2) {
                mMoreView.visibility = View.GONE
            }
        }

        mMoreView.setOnClickListener(View.OnClickListener {
            viewExtension(context, item)
        })
//        ImageUtil.loadImageResize(context, item.imageUrl, mImgMain, R.drawable.noimage_375_188)

    }

    private fun viewExtension(context: Context, item: SectionContentList) {
        isExtension = true;

        mViewAdd.removeAllViews()
        for (i in 0 until item.subProductList!!.size) {
//            if (i >= MAX_ITEM_CNT) break

            try {
                val listItem = item.subProductList!![i]
                val itemView = LayoutInflater.from(context).inflate(LAYOUT_ID, null)
                val subPrd = VipCommonPrd1_ListVH(itemView)

                if (subPrd.mBorderLine != null) {
//                    if (i < MAX_ITEM_CNT - 1 && i < item.subProductList!!.size - 1) {
                    if (i < item.subProductList!!.size - 1) {
                        subPrd.mBorderLine.visibility = View.VISIBLE
                        subPrd.mBorderLine.setBackgroundColor(Color.parseColor("#eeeeee"))
                    } else {
                        subPrd.mBorderLine.visibility = View.GONE
                        subPrd.mBorderLine.setBackgroundResource(android.R.color.transparent)
                    }
                }
                mViewAdd.addView(subPrd.onBindViewHolder(context, listItem))
            }
            catch (e:IndexOutOfBoundsException) {
                Ln.e(e.message)
            }
        }

        mMoreView.visibility = View.GONE

        try {
            (context as AbstractBaseActivity).setWiseLogHttpClient(item.moreBtnUrl)
        }
        catch (e:ClassCastException) {
            Ln.e(e.message)
        }
        catch (e:NoSuchMethodException) {
            Ln.e(e.message)
        }

    }
}