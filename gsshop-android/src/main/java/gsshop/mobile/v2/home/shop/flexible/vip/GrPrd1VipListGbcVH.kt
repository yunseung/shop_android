package gsshop.mobile.v2.home.shop.flexible.vip

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.main.SectionContentList
import gsshop.mobile.v2.home.shop.BaseViewHolder
import gsshop.mobile.v2.home.shop.ShopInfo
import gsshop.mobile.v2.home.shop.flexible.vip.common.VipCommonPrd1GbcMainItem
import gsshop.mobile.v2.home.shop.flexible.vip.common.VipCommonPrd1_ListVH
import gsshop.mobile.v2.home.shop.flexible.vip.common.VipCommonReadMore
import kotlinx.android.synthetic.main.view_holder_gr_prd_1_vip_list_common.view.*
import roboguice.util.Ln

class GrPrd1VipListGbcVH(itemView: View) : BaseViewHolder(itemView) {

    private val mViewBack = itemView.view_back
    private val mTitleVip = itemView.view_title_vip
    private val mMoreView = itemView.view_read_more
    private val mViewAdd = itemView.view_add

    private val LAYOUT_ID = R.layout.view_holder_gr_prd_1_vip_list_gbc_item_sub

    override fun onBindViewHolder(context: Context, position: Int, info: ShopInfo?, action: String?, label: String?, sectionName: String?) {
        super.onBindViewHolder(context, position, info, action, label, sectionName)

        val item = info?.contents?.get(position)?.sectionContent ?: return

        mViewBack.setNew("Y".equals(item.newYN, ignoreCase = true))

        mTitleVip.setItems(item)
        mMoreView.setItems(item, VipCommonReadMore.ENUM_TYPE_MORE.TYPE_EXTENSION)
        itemView.rootView.contentDescription = mTitleVip.getTitleTxt()

        // Recycler 안에 Recycler 아이템이 많으면 펼치고 나서 버벅일 수 있는 문제 있어 addview로...
        if (isExtension) {
            viewExtension(context, item)
        }
        else {
            mViewAdd.removeAllViews()
            mMoreView.visibility = View.VISIBLE

            try {
                if (item.subProductList!!.size > 0) {
                    val mainImageView = VipCommonPrd1GbcMainItem(context)
                    mainImageView.setItems(item.subProductList!![0])

                    mViewAdd.addView(mainImageView)
                    if (item.subProductList!![0].subProductList != null &&
                            item.subProductList!![0].subProductList!!.size > 0) {
                        val itemView = LayoutInflater.from(context).inflate(LAYOUT_ID, null)
                        val subPrd = VipCommonPrd1_ListVH(itemView)
                        mViewAdd.addView(subPrd.onBindViewHolder(context, item.subProductList!![0].subProductList!![0]))
                    }
                    if (item.subProductList!!.size < 2 && item.subProductList!![0].subProductList!!.size < 2) {
                        mMoreView.visibility = View.GONE
                    }
                }


            } catch (e: NullPointerException) {
                Ln.e(e.message)
            }
        }

        mMoreView.setOnClickListener {
            viewExtension(context, item)
        }

    }

    private fun viewExtension(context: Context, item: SectionContentList) {
        item.subProductList ?: return

        isExtension = true;

        mViewAdd.removeAllViews()

        for (i in 0 until item.subProductList!!.size) {

            try {
                val eachMain = item.subProductList!![i]
                val mainImageView = VipCommonPrd1GbcMainItem(context)

                mainImageView.setItems(eachMain)

                mViewAdd.addView(mainImageView)

                eachMain.subProductList ?: continue

                for (j in 0 until eachMain.subProductList!!.size) {
                    try {
                        val eachSub = eachMain.subProductList!![j]

                        val subItem = LayoutInflater.from(context).inflate(LAYOUT_ID, null)
                        val subPrd = VipCommonPrd1_ListVH(subItem)

                        if (subPrd.mBorderLine != null) {
                            if (j < item.subProductList!!.size - 1) {
                                subPrd.mBorderLine.visibility = View.VISIBLE
                                subPrd.mBorderLine.setBackgroundColor(Color.parseColor("#eeeeee"))
                            } else {
                                subPrd.mBorderLine.visibility = View.GONE
                                subPrd.mBorderLine.setBackgroundResource(android.R.color.transparent)
                            }
                        }

                        mViewAdd.addView(subPrd.onBindViewHolder(context, eachSub))
                    }
                    catch (e:IndexOutOfBoundsException) {
                        Ln.e(e.message)
                    }
                }
            }
            catch (e:IndexOutOfBoundsException) {
                Ln.e(e.message)
            }
        }

        mMoreView.visibility = View.GONE
    }
}