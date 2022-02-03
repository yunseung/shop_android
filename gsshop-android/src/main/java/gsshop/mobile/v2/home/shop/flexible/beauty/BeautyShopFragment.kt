package gsshop.mobile.v2.home.shop.flexible.beauty

import android.content.Context
import android.os.Bundle
import android.view.View
import android.webkit.URLUtil
import android.widget.Toast
import com.google.inject.Inject
import com.gsshop.mocha.network.rest.RestClient
import com.gsshop.mocha.pattern.mvc.BaseAsyncController
import de.greenrobot.event.EventBus
import gsshop.mobile.v2.Events
import gsshop.mobile.v2.Events.FlexibleEvent.UpdateFlexibleShopEvent
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.main.ContentsListInfo
import gsshop.mobile.v2.home.main.SectionContentList
import gsshop.mobile.v2.home.shop.ShopInfo.ShopItem
import gsshop.mobile.v2.home.shop.ViewHolderType
import gsshop.mobile.v2.home.shop.bestshop.BestShopFragment
import gsshop.mobile.v2.util.DataUtil
import roboguice.util.Ln
import java.util.*
import kotlin.collections.ArrayList

class BeautyShopFragment : BestShopFragment() {

    private var mNoDataLast = false

    var gListTomorrow: HashMap<Int, Int> = HashMap()

    var gListBest: HashMap<Int, MutableList<Int>> = HashMap()

    companion object {

        @JvmField
        var instance : BeautyShopFragment? = null

        @JvmStatic
        fun getInstance(): BeautyShopFragment? {
            return instance
        }

        @JvmStatic
        fun newInstance(position: Int): BeautyShopFragment? {
            instance = newInstanceInternal(position)
            return instance
        }

        @JvmStatic
        private fun newInstanceInternal(position: Int): BeautyShopFragment? {
            val fragment = BeautyShopFragment()
            val args = Bundle()
            args.putInt(ARG_PARAM_POSITION, position)
            fragment.arguments = args
            return fragment
        }
    }

//    private var mItemClickListener: RecyclerItemClickListener? = RecyclerItemClickListener(context, RecyclerItemClickListener.OnItemClickListener { _, position ->
//        gListTomorrow[position] = 0
//        gListBest[position] = mutableListOf(0, 0)
//    })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAdapter = BeautyShopAdapter(activity, this)
        mRecyclerView.adapter = mAdapter
    }

    override fun getFlexibleViewType(viewType: String?): Int {
        return when (viewType) {
            "L" -> {    // L 이 들어있는 경우가 있어 옛날에 들어오던 타입이고 쓸 수 없어 None 처리
                ViewHolderType.BANNER_TYPE_NONE
            }
            "BAN_BEAUTY_SLD" -> {
                ViewHolderType.VIEW_TYPE_BAN_BEAUTY_SLD
            }
            "BAN_BEAUTY_SLD_TIME_SALE" -> {
                ViewHolderType.VIEW_TYPE_BAN_BEAUTY_SLD_TIME_SALE
            }
            "PRD_BEAUTY_SLD_WEEKLY_EVENT" -> {
                ViewHolderType.VIEW_TYPE_PRD_BEAUTY_SLD_WEEKLY_EVENT
            }
            "GR_BEAUTY_CATE" -> {
                ViewHolderType.VIEW_TYPE_GR_BEAUTY_CATE
            }
            "GR_BEAUTY_SLD_BRAND" -> {
                ViewHolderType.VIEW_TYPE_GR_BEAUTY_SLD_BRAND
            }
            "TAB_BEAUTY_MENU" -> {
                ViewHolderType.VIEW_TYPE_TAB_BEAUTY_MENU
            }
            "TAB_BEAUTY_MENU_BEST" -> {
                ViewHolderType.VIEW_TYPE_TAB_BEAUTY_MENU_BEST
            }
            "PRD_2_BEAUTY_BEST" -> {
                ViewHolderType.VIEW_TYPE_PRD_2_BEAUTY_BEST
            }
            "PRD_2_BEAUTY_TOMM" -> {
                ViewHolderType.VIEW_TYPE_PRD_2_BEAUTY_TOMM
            }
            "NO_DATA_BEST" -> {
                ViewHolderType.VIEW_TYPE_NO_DATA_BEST
            }
            "NO_DATA_TOMM" -> {
                ViewHolderType.VIEW_TYPE_NO_DATA_TOMM
            }
            "BAN_BEAUTY_SLD_TODAY" -> {
                ViewHolderType.VIEW_TYPE_BAN_BEAUTY_SLD_TODAY
            }
            "BAN_VIEW_MORE_BEAUTY" -> {
                ViewHolderType.VIEW_TYPE_BAN_VIEW_MORE_BEAUTY
            }
            "BAN_IMG_BEAUTY_TOMM" -> {
                ViewHolderType.VIEW_TYPE_BAN_IMG_BEAUTY_TOMM
            }
            else -> {
                super.getFlexibleViewType(viewType)
            }
        }
    }

    /**
     * 뷰티 탭을 직접 선택했을때 갱신 이벤트
     */
    fun onEvent(event: Events.BeautyShopEvent.BeautyRefreshEvent)
    {
        val curationEvent = EventBus.getDefault().getStickyEvent(Events.BeautyShopEvent.BeautyRefreshEvent::class.java)
        if (curationEvent != null) {
            EventBus.getDefault().removeStickyEvent(curationEvent)
        }

        val currentPosition = gsXBrandCategoryHolder.getSelectedPosition(mAdapter.info.sectionList.navigationId)

//        gListTomorrow[currentPosition] = 0
//        gListBest[currentPosition] = mutableListOf(0, 0)

        try {
            if (event.clickPositionTom != null) {
                gListTomorrow[currentPosition] = event.clickPositionTom
            }
        }
        catch (e:NullPointerException) {
            Ln.e(e.message)
        }

        try {
            if (event.clickPositionBest != null) {
                gListBest[currentPosition] = event.clickPositionBest
            }
        }
        catch (e:NullPointerException) {
            Ln.e(e.message)
        }

        BeautyUpdateController(context, (mAdapter as BeautyShopAdapter)).execute(event.url, false,
                event.removePosition, event.delViewType)
    }

    fun onEvent(event: Events.FlexibleEvent.GsXClickEvent) {
        val curationEvent = EventBus.getDefault().getStickyEvent(Events.FlexibleEvent.GsXClickEvent::class.java)
        if (curationEvent != null) {
            EventBus.getDefault().removeStickyEvent(curationEvent)
        }

        if (!userVisibleHint) return

        gListTomorrow[event.position] = 0
        gListBest[event.position] = mutableListOf(0, 0)
    }

    /**
     * 더보기를 통해서 이동 했을 때의 이벤트
     */
    fun onEvent(event: Events.BeautyShopEvent.BeautyMoveTabEvent) {
        val curationEvent = EventBus.getDefault().getStickyEvent(Events.BeautyShopEvent.BeautyMoveTabEvent::class.java)
        if (curationEvent != null) {
            EventBus.getDefault().removeStickyEvent(curationEvent)
        }

        val tabSeq = event.tabSeq ?: return;

        try {
            var indexBest = -1
            var indexTom = -1
            for (content in mAdapter.info.contents) {
                if (content.type == ViewHolderType.VIEW_TYPE_TAB_BEAUTY_MENU_BEST) {
                    indexBest = mAdapter.info.contents.indexOf(content)
                }
                else if (content.type == ViewHolderType.VIEW_TYPE_TAB_BEAUTY_MENU) {
                    indexTom = mAdapter.info.contents.indexOf(content)
                }

                if (indexBest > 0 && indexTom > 0) {
                    break;
                }
            }

            for (content in mAdapter.info.contents) {
                if (content.type == ViewHolderType.BAN_CX_SLD_CATE_GBA) {
                    val items = content.sectionContent.subProductList ?: continue

                    for (i in 0 until items.size) {
                        val listITem = items[i]
                        if (tabSeq == listITem.tabSeq.toString()) {
                            if (gsXBrandCategoryHolder != null) {

                                val item = SectionContentList()
                                item.productName = items[i].productName
                                item.linkUrl = items[i].linkUrl

                                var link = event.linkUrl

                                if (mNoDataLast) {
                                    gListTomorrow[i] = 0
                                    gListBest[i] = mutableListOf(0, 0)
                                }
                                else {
                                    val currentPosition = gsXBrandCategoryHolder.getSelectedPosition(mAdapter.info.sectionList.navigationId)
                                    val currentPositionValueTom = gListTomorrow[currentPosition] ?: 0
                                    val currentPositionValueBest = gListBest[currentPosition] ?: mutableListOf(0, 0)

                                    gListTomorrow[i] = currentPositionValueTom
                                    gListBest[i] = currentPositionValueBest
                                    var bestParam1 = ""
                                    var bestParam2 = ""
                                    try {
                                        // 베스트 파람 2개, 내일도착 파람 1개 를 다 붙여 준다.
                                        bestParam1 = mAdapter.info.contents[indexBest].sectionContent.subProductList?.get(0)?.subProductList?.get(currentPositionValueBest[0])?.linkUrl?: ""
                                        bestParam2 = mAdapter.info.contents[indexBest].sectionContent.subProductList?.get(1)?.subProductList?.get(currentPositionValueBest[1])?.linkUrl?: ""
                                    }
                                    catch (e:java.lang.IndexOutOfBoundsException) {
                                        Ln.e(e.message)
                                    }
                                    var tomParam = ""
                                    try {
                                        tomParam = mAdapter.info.contents[indexTom].sectionContent.subProductList?.get(currentPositionValueTom)?.linkUrl ?: ""
                                    } catch (e: IndexOutOfBoundsException) {
                                        Ln.e(e.message)
                                    }

                                    if (bestParam1.startsWith("http://") || bestParam1.startsWith("https://")) {bestParam1 = ""}
                                    if (bestParam2.startsWith("http://") || bestParam2.startsWith("https://")) {bestParam2 = ""}
                                    if (tomParam.startsWith("http://") || tomParam.startsWith("https://")) {tomParam = ""}
                                    link += (bestParam1 + bestParam2 + tomParam)

                                }

                                if(URLUtil.isValidUrl(link)) {
                                    item.linkUrl = link
                                    gsXBrandCategoryHolder.setListPosition(mAdapter.info.sectionList.navigationId, i, item)
                                }
                                else {
                                    Toast.makeText(context, R.string.network_unstable_detail_Vew, Toast.LENGTH_SHORT).show()
                                }
                            }
                            break;
                        }
                    }
                }
                break;
            }
        }
        catch (e:NullPointerException) {
            Ln.e(e.message)
        }
    }

    // 추가로  gsXBrandCategoryHolder 클릭시에 클릭 리스너를 추가해 주기로 한다.
//    override fun updateList(sectionList: TopSectionList?, contentsListInfo: ContentsListInfo?, tab: Int, listPosition: Int) {
//        super.updateList(sectionList, contentsListInfo, tab, listPosition)
//        if (gsXBrandCategoryHolder != null) {
//            gsXBrandCategoryHolder.removeItemClickListener(mItemClickListener)
//            gsXBrandCategoryHolder.addItemClickListener(mItemClickListener)
//        }
//    }

    /**
     * 섹션 업데이트
     */
    inner class BeautyUpdateController(activityContext: Context?, adapter: BeautyShopAdapter?) : BaseAsyncController<ContentsListInfo?>(activityContext) {
        @Inject
        private val restClient: RestClient? = null
        private val mAdapter = adapter
        private var mUrl: String? = null
        private var mIsCacheData = false
        private var mStartPosition = 0
        private var mViewType = ViewHolderType.BANNER_TYPE_NONE
        private val mContext: Context? = activityContext

        override fun onPrepare(vararg params: Any) {
            super.onPrepare(*params)
            try {
                mUrl = params[0] as String          // 호출 URL
                mIsCacheData = params[1] as Boolean // 캐시 여부
                mStartPosition = params[2] as Int   // 삭제하고 add 될 위치
                mViewType = params[3] as Int        // 삭제 될 뷰타입

                if (dialog != null) {
                    dialog.setCancelable(false)
                }
            }
            catch (e:IndexOutOfBoundsException) {
                Ln.e(e.message)
            }
        }

        override fun process(): ContentsListInfo {
            // 플렉서블 매장 카테고리 탭 이미지 다운로드.
            return DataUtil.getData(mContext, restClient, ContentsListInfo::class.java,
                    mIsCacheData, true, mUrl, null) as ContentsListInfo
        }

        override fun onSuccess(listInfo: ContentsListInfo?) {
            if (listInfo == null || mAdapter == null) {
//                onError(Throwable("Updated list is null"))
                return
            }

            val updateDataSize = listInfo.productList.size
            if (updateDataSize == 0) {
                return
            }
            val listShopItem: ArrayList<ShopItem> = ArrayList()

            try {
//                var removeStartPosition = -1
//                var removeCnt:Int = 0;
                // 어댑터에 있는 녀석들 다 뒤져서 삭제.
//                val tempList = mAdapter.info.contents

                val iterator = mAdapter.info.contents.iterator()

                var noDataType = ViewHolderType.BANNER_TYPE_NONE
                if (mViewType == ViewHolderType.VIEW_TYPE_PRD_2_BEAUTY_BEST) {
                    noDataType = ViewHolderType.VIEW_TYPE_NO_DATA_BEST
                }
                else if (mViewType == ViewHolderType.VIEW_TYPE_PRD_2_BEAUTY_TOMM) {
                    noDataType = ViewHolderType.VIEW_TYPE_NO_DATA_TOMM
                }

                while(iterator.hasNext()) {
                    val itemIterator = iterator.next()

                    if (itemIterator.type == mViewType || itemIterator.type == noDataType) {

                        // 해당 하는 녀석들 삭제.
                        val removeIndex = mAdapter.info.contents.indexOf(itemIterator)
                        iterator.remove()
                        mAdapter.notifyItemRemoved(mStartPosition)
                    }
                }

                mNoDataLast = false
                if (listInfo.productList.size == 1) {
                    val type = getFlexibleViewType(listInfo.productList[0].viewType)
                    if (ViewHolderType.VIEW_TYPE_NO_DATA_BEST == type ||
                            ViewHolderType.VIEW_TYPE_NO_DATA_TOMM == type) {
                        mNoDataLast = true
                    }
                }

                for (i in listInfo.productList.indices) {
                    val item = ShopItem()

                    val type = getFlexibleViewType(listInfo.productList[i].viewType)

                    item.sectionContent = listInfo.productList[i]
                    item.type = type

                    listShopItem.add(item)
                }

                // 헤더 위치를 다시 찾아서 넣는다.
                // (해당 위치가 아닌 다른 위치 삭제 될 경우 index를 넘어가는 경우가 생긴다.)
                var headerType = ViewHolderType.BANNER_TYPE_NONE
                if (mViewType == ViewHolderType.VIEW_TYPE_PRD_2_BEAUTY_BEST) {
                    headerType = ViewHolderType.VIEW_TYPE_TAB_BEAUTY_MENU_BEST
                }
                else if (mViewType == ViewHolderType.VIEW_TYPE_PRD_2_BEAUTY_TOMM) {
                    headerType = ViewHolderType.VIEW_TYPE_TAB_BEAUTY_MENU
                }

                var startIndex = 0
                for (i in mAdapter.info.contents.indices) {
                    if (headerType == mAdapter.info.contents[i].type) {
                        startIndex = i + 1
                        break
                    }
                }

//                Ln.d("hklim insert start : " + startIndex + " / listShopItem size : " + listShopItem.size +
//                        "adapter size : " + mAdapter.itemCount)

                mAdapter.info.contents.addAll(startIndex, listShopItem)

//                Ln.d("hklim insert end : " + startIndex + " / listShopItem size : " + listShopItem.size +
//                        "adapter size : " + mAdapter.itemCount)
                mAdapter.notifyItemRangeInserted(startIndex, listShopItem.size)

//                mAdapter.notifyDataSetChanged()

            }
            catch (e:Exception) {
                Ln.e(e.message)
            }

        }

        override fun onError(e: Throwable?) {
            super.onError(e)

            Ln.e(e?.message)
            Toast.makeText(mContext, getString(R.string.app_refresh), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSwipeRefrehsing() {
        isNowSwipeRefreshing = true

        if (gsXBrandCategoryHolder != null) {
            gsXBrandCategoryHolder.setSelectedPosition(mAdapter.info.sectionList.navigationId, 0)
        }

        gListTomorrow = HashMap()
        gListBest = HashMap()

        EventBus.getDefault().post(UpdateFlexibleShopEvent(0, -1))
    }
}