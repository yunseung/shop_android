/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible.todaysel

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.main.SectionContentList
import gsshop.mobile.v2.home.shop.BaseViewHolder
import gsshop.mobile.v2.home.shop.ShopInfo
import gsshop.mobile.v2.home.shop.flexible.todaysel.common.AdapterTabCommon
import gsshop.mobile.v2.home.shop.renewal.publicusage.Prd2VH
import gsshop.mobile.v2.support.ui.BugFixedStaggeredGridLayoutManager
import gsshop.mobile.v2.support.ui.RecyclerItemClickListener
import gsshop.mobile.v2.util.ImageUtil
import gsshop.mobile.v2.web.WebUtils
import kotlinx.android.synthetic.main.view_holder_gr_tab_today_sel.view.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * GS X Brand 배너
 */
@SuppressLint("NewApi")
class GrTabTodaySel
/**
 * @param itemView itemView
 */
(itemView: View) : BaseViewHolder(itemView) {
    private val isAddedItemDecoration = AtomicBoolean(false)

    private var mAdapterTab: AdapterTabCommon? = null
//    private var mAdapterItem: TodaySelectAdapter? =null
    private lateinit var mList: List<SectionContentList>

    init {
    }

    /*
     * bind
     */
    override fun onBindViewHolder(context: Context, position: Int, info: ShopInfo?,
                                  action: String?, label: String?, sectionName: String?) {
        super.onBindViewHolder(context, position, info, action, label, sectionName)

        val item = info?.contents?.get(position)?.sectionContent ?: return
        item.subProductList ?: return
        mList = item.subProductList!!

        var selectedItem = 0

        val layoutManager =
            BugFixedStaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL)
        itemView.recycler_tab.layoutManager = layoutManager

        mAdapterTab = AdapterTabCommon(context, item.subProductList!!,
                itemView.recycler_tab, selectedItem)

        itemView.recycler_tab.adapter = mAdapterTab

        itemView.recycler_tab.addOnItemTouchListener(
            RecyclerItemClickListener(context) { _, selPosition ->
                setSelectedItem(context, selPosition, mList)

                itemView.recycler_tab.scrollToPosition(selPosition)

                mAdapterTab!!.setSelectedItem(selPosition)
            }
        )

        setSelectedItem(context, selectedItem, mList)
    }

    fun setSelectedItem(context: Context, position: Int, list: List<SectionContentList>) {
        if(!list[position].name.isNullOrEmpty()) {
            itemView.txt_main.text = list[position].name
        }
        if(!list[position].subName.isNullOrEmpty()) {
            itemView.txt_sub.text = list[position].subName
        }

        itemView.txt_more_bottom.text = ""
        if (!list[position].moreText.isNullOrEmpty())
            itemView.txt_more_bottom.text = list[position].moreText

        if(list[position].moreBtnUrl.isNullOrEmpty()) {
            itemView.view_more_bottom.visibility = View.GONE
        }
        else {
            itemView.view_more_bottom.visibility = View.VISIBLE

            itemView.view_more_bottom.setOnClickListener{
                WebUtils.goWeb(context, list[position].moreBtnUrl)
            }
        }

        if (!list[position].imageUrl.isNullOrEmpty()) {
            ImageUtil.loadImageResize(
                context, list[position].imageUrl,
                itemView.image_main, R.drawable.noimage_375_188)
        }

        if (list[position].linkUrl.isNullOrEmpty()) {
            itemView.view_more.visibility = View.GONE
        }
        else {
            itemView.view_more.visibility = View.VISIBLE
            itemView.view_more.setOnClickListener{
                WebUtils.goWeb(context, list[position].linkUrl)
            }
            itemView.image_main.setOnClickListener{
                WebUtils.goWeb(context, list[position].linkUrl)
            }
        }

        itemView.view_add.removeAllViews()
        if (!list[position].subProductList.isNullOrEmpty() &&
            list[position].subProductList!!.size > 0) {
            for (listItem in list[position].subProductList!!) {
                // 단품 쌍.
                val itemPrdView = LayoutInflater.from(context)
                    .inflate(R.layout.renewal_banner_type_l_prd_2, null)
                val prd2Item = Prd2VH(itemPrdView)
                prd2Item.setItems(context, listItem, true)

                itemView.view_add.addView(itemPrdView)
            }
        }
        else {

        }
    }

}