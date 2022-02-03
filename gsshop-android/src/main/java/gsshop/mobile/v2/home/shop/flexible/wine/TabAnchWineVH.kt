/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible.wine

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.EmptyUtils
import de.greenrobot.event.EventBus
import gsshop.mobile.v2.Events
import gsshop.mobile.v2.Events.FlexibleEvent.TvLiveUnregisterEvent
import gsshop.mobile.v2.home.shop.BaseViewHolder
import gsshop.mobile.v2.home.shop.ShopInfo
import gsshop.mobile.v2.home.shop.flexible.wine.common.AdapterAnchorCommon
import kotlinx.android.synthetic.main.view_holder_tab_anch_wine.view.*
import roboguice.util.Ln
import java.util.concurrent.atomic.AtomicBoolean

/**
 * GS X Brand 배너
 */
@SuppressLint("NewApi")
class TabAnchWineVH
/**
 * @param itemView itemView
 */
(itemView: View, naviId: String, isHeader: Boolean) : BaseViewHolder(itemView) {
    private val isAddedItemDecoration = AtomicBoolean(false)

    private var mIsHeader = false

    private var mAdapter: AdapterAnchorCommon? = null

    /**
     * 섹션코드
     */
    private var mSectionCode: String? = null

    /**
     * 섹션코드에 대응하는 위치 저장
     */
    private val mSectionPosMap = mutableMapOf<String, Int>()

    init{
        //이벤트버스 등록
        EventBus.getDefault().register(this)

//        itemView.rootView.recycler.layoutManager =
//                BugFixedStaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL)
        navigationId = naviId
        mIsHeader = isHeader
    }

    /*
     * bind
     */
    override fun onBindViewHolder(context: Context, position: Int, info: ShopInfo?,
                                  action: String?, label: String?, sectionName: String?) {
        super.onBindViewHolder(context, position, info, action, label, sectionName)

        val item = info?.contents?.get(position)?.sectionContent ?: return
        item.subProductList ?: return

        //섹션코드 저장
        mSectionCode = item.tabSeq

        var selectedItem = 0
        try {
            for (i in 0 until item.subProductList!!.size) {
                val sCode: String? = item.subProductList!![i].tabSeq
                if (EmptyUtils.isEmpty(sCode)) {
                    continue
                }
                mSectionPosMap[sCode!!] = i
                if (sCode == item.tabSeq) {
                    selectedItem = i
                }
            }
        }
        catch (e:NullPointerException) {
            Ln.e(e.message)
        }
        catch (e:IndexOutOfBoundsException) {
            Ln.e(e.message)
        }

        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL

        if (mIsHeader) {
            itemView.view_bottom_divider_1dp.visibility = View.VISIBLE
        }

        itemView.rootView.recycler.layoutManager = layoutManager

        mAdapter = AdapterAnchorCommon(context, item.subProductList!!,
                itemView.rootView.recycler, navigationId, mIsHeader, selectedItem)

        itemView.rootView.recycler.adapter = mAdapter
    }

    /**
     * 섹션코드 활성화영역을 변경한다. (해더로 사용된 경우만 해당됨)
     *
     * @param event GSSuperSectionEvent
     */
    fun onEvent(event: Events.FlexibleEvent.EventWine.SectionSyncEvent) {
        //해더가 아니면 카테고리 변경 하지 않음
        if (!mIsHeader) {
            return
        }

//        // 여러개 존재할 때에 navigation ID로 비교.
//        if (navigationId != null && navigationId != event.navigationId) {
//            return
//        }

        //스크롤 이벤트에서 전달받은 섹션코드
        var evCode = event.sectionCode

        evCode = evCode ?: ""
        try {
            for (i in 0 until itemView.rootView.recycler.childCount) {
                val view: View = itemView.rootView.recycler.getChildAt(i)
                val vh = itemView.rootView.recycler.getChildViewHolder(view)
                        as AdapterAnchorCommon.ItemViewHolder
                val vhTag = vh.itemView.tag

                if (EmptyUtils.isEmpty(vhTag)) {
                    continue
                }
                val vhCode = vhTag.toString()
                if (evCode == vhCode) {
                    mAdapter?.setSelectedItem(mSectionPosMap[evCode]!!)
                }
            }
        }
        catch (e:NullPointerException) {
//            무시해도 되는 Exception
//            Ln.e(e.message)
        }
    }

    /**
     * 이벤트버스 unregister
     *
     * @param event TvLiveUnregisterEvent
     */
    fun onEvent(event: TvLiveUnregisterEvent?) {
        mAdapter?.setSelectedItem(0)
        EventBus.getDefault().unregister(this)
    }

    override fun getSectionCode(): String {
        return mSectionCode.toString();
    }
}