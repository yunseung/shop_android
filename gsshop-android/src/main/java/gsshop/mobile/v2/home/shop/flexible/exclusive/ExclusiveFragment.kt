package gsshop.mobile.v2.home.shop.flexible.exclusive

import android.os.Bundle
import android.view.View
import com.google.inject.Inject
import com.gsshop.mocha.network.rest.RestClient
import com.gsshop.mocha.pattern.mvc.BaseAsyncController
import de.greenrobot.event.EventBus
import gsshop.mobile.v2.Events
import gsshop.mobile.v2.home.main.ModuleListRoot
import gsshop.mobile.v2.home.shop.ShopInfo
import gsshop.mobile.v2.home.shop.ViewHolderType
import gsshop.mobile.v2.home.shop.bestshop.BestShopFragment
import gsshop.mobile.v2.util.DataUtil
import roboguice.util.Ln

class ExclusiveFragment : BestShopFragment() {

    @Inject
    private val mRestClient: RestClient? = null

    companion object {

        @JvmField
        var instance : ExclusiveFragment? = null

        @JvmStatic
        fun getInstance(): ExclusiveFragment? {
            return instance
        }

        @JvmStatic
        fun newInstance(position: Int): ExclusiveFragment? {
            instance = newInstanceInternal(position)
            return instance
        }

        @JvmStatic
        private fun newInstanceInternal(position: Int): ExclusiveFragment? {
            val fragment = ExclusiveFragment()
            val args = Bundle()
            args.putInt(ARG_PARAM_POSITION, position)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAdapter = ExclusiveAdapter(activity)
        mRecyclerView.adapter = mAdapter
    }

    override fun getFlexibleViewType(viewType: String?): Int {
        return when (viewType) {
            "BAN_TAB_SEL" -> {
                ViewHolderType.VIEW_TYPE_BAN_TAB_SEL
            }
            "GR_BRD_GBA_FXC" -> {
                ViewHolderType.VIEW_TYPE_GR_BRD_GBA_FXC
            }
            "BAN_SLD_GBG_FXC" -> {
                ViewHolderType.VIEW_TYPE_BAN_SLD_GBG_FXC
            }
            else -> {
                super.getFlexibleViewType(viewType)
            }
        }
    }

    fun onEvent(event: Events.GSExcusiveEvent.TabRefreshEvent) {
        val curationEvent = EventBus.getDefault().getStickyEvent(Events.GSExcusiveEvent.TabRefreshEvent::class.java)
        if (curationEvent != null) {
            EventBus.getDefault().removeStickyEvent(curationEvent)
        }
        getExclusiveData(event.url)
    }

    private fun updateExclusiveData(list: ModuleListRoot) {
        val productList = list.productList ?: return
        // 갱신 URL 수정.
        mAdapter.info.ajaxPageUrl = list.ajaxfullUrl

        val listNew = ArrayList<ShopInfo.ShopItem>()

        var indexTab = -1

        for (content in mAdapter.info.contents) {
            // 데이터 변경을 위해 tab 이 나오기 전까지 넣어 준다.
            listNew.add(content)

            // 현재 어댑터의 타입을 확인하여 탭 위치를 찾았으면 break 탭 까지만 넣어주기 위함.
            if (content.type == ViewHolderType.VIEW_TYPE_BAN_TAB_SEL) {

                // 어댑터 내의 탭 위치
                indexTab = mAdapter.info.contents.indexOf(content)

                break;
            }
        }

        for (i in 0 until productList.size) {
            val addItem = ShopInfo.ShopItem()
            val listItem = productList[i]

            addItem.type = getFlexibleViewType(listItem.viewType)
            addItem.sectionContent = listItem

            listNew.add(addItem)
        }

        mAdapter.info.contents = listNew

        mAdapter.notifyDataSetChanged()

//        if (indexTab > 0) {
//            mAdapter.notifyItemRangeChanged(indexTab, listNew.size)
//        }
//        else {
//            mAdapter.notifyDataSetChanged()
//        }
    }

    private fun getExclusiveData(linkUrl : String) {
        object : BaseAsyncController<ModuleListRoot>(mActivity) {

            override fun onPrepare(vararg params: Any?) {
                if (dialog != null) {
                    try {
                        dialog.setOnCancelListener {
                            // 아무것도 안함. 해당 컨트롤러도 연결하다 시간 종료 되어서 될 지언정 작업 취소하지 않음
                        }
                        dialog.show()
                    } catch (var3: IllegalAccessError) {
                        Ln.e(var3.message)
                    }
                }
//                super.onPrepare(*params)
            }

            @Throws(Exception::class)
            override fun process(): ModuleListRoot {
                return DataUtil.getData(context, mRestClient, ModuleListRoot::class.java,
                        false, false, linkUrl) as ModuleListRoot
            }

            @Throws(Exception::class)
            override fun onSuccess(list : ModuleListRoot) {
                updateExclusiveData(list)
            }
        }.execute()
    }

    override fun onSwipeRefrehsing() {
        try {
            // 당겨서 새로고침시에 기존 라디오 버튼이 초기화 안되네;
            (mAdapter as ExclusiveAdapter).setTabSel(0)
        }
        catch (e:ClassCastException) {
            Ln.e(e.message)
        }

        super.onSwipeRefrehsing()
    }
}