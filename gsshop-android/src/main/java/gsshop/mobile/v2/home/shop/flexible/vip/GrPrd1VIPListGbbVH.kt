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

/**
 * GBA 같이 쓰고 숫자만 다르게 해서 쓰려 했지만, 나중에 헷갈리니 그냥 따로 만듦
 */
class GrPrd1VIPListGbbVH(itemView: View) : BaseViewHolder(itemView) {
    // 상단 공통 VIP 타이틀
    private val mTitleVip = itemView.view_title_vip
    // 하단 공통 더보기
    private val mMoreView = itemView.view_read_more
    // 추가될 뷰
    private val mViewAdd = itemView.view_add

    private val mRootView = itemView.root

    private val mViewBack = itemView.view_back

    private val LAYOUT_ID = R.layout.view_holder_gr_prd_1_vip_list_item

    private val MAX_ITEM_CNT_BEFORE_EXTENSION = 3
//    private val MAX_ITEM_CNT = 10

    override fun onBindViewHolder(context: Context, position: Int, info: ShopInfo?,
                                  action: String?, label: String?, sectionName: String?) {
        val item = info?.contents?.get(position)?.sectionContent ?: return

        mViewBack.setNew("Y".equals(item.newYN, ignoreCase = true))

        // VIP 타이틀 설정
        mTitleVip.setItems(item)
        
        // 더보기 설정
        mMoreView.setItems(item, VipCommonReadMore.ENUM_TYPE_MORE.TYPE_EXTENSION)

        mRootView.contentDescription = mTitleVip.getTitleTxt()

        // 현재 더보기 열린 상태 체크
        if (isExtension) {
            viewExtension(context, item)
        }
        else {
             // 열린 상태가 아니라면 
            mViewAdd.removeAllViews()
            // 아이템이 3개 이하면 더보기 노출 불필요
            if (item.subProductList!!.size <= MAX_ITEM_CNT_BEFORE_EXTENSION) {
                mMoreView.visibility = View.GONE
            }
            else {
                mMoreView.visibility = View.VISIBLE
            }

                // 아아템 최대 3개까지 만 추가
            for (i in 0..item.subProductList!!.size) {
                if (i >= MAX_ITEM_CNT_BEFORE_EXTENSION) {
                    break
                }
                try {
                    val listItem = item.subProductList!![i]
                    val itemView = LayoutInflater.from(context).inflate(LAYOUT_ID, null)
                    val subPrd = VipCommonPrd1_ListVH(itemView)

                    if (subPrd.mBorderLine != null) {
                        if (i < MAX_ITEM_CNT_BEFORE_EXTENSION - 1 && i < item.subProductList!!.size - 1) {
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
        }

        mMoreView.setOnClickListener {
            viewExtension(context, item)
        }
//        ImageUtil.loadImageResize(context, item.imageUrl, mImgMain, R.drawable.noimage_375_188)

    }

    /**
     * 더보기 선택시
     */
    private fun viewExtension(context: Context, item: SectionContentList) {
        // 10개까지 보여주는 함수
        isExtension = true

        mViewAdd.removeAllViews()

        for (i in 0..item.subProductList!!.size) {
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
        catch (e: ClassCastException) {
            Ln.e(e.message)
        }
        catch (e:NoSuchMethodException) {
            Ln.e(e.message)
        }
    }
}