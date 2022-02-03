package gsshop.mobile.v2.home.shop.flexible.beauty

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import de.greenrobot.event.EventBus
import gsshop.mobile.v2.AbstractBaseActivity
import gsshop.mobile.v2.Events
import gsshop.mobile.v2.ServerUrls
import gsshop.mobile.v2.home.shop.BaseViewHolder
import gsshop.mobile.v2.home.shop.ShopInfo
import gsshop.mobile.v2.home.shop.ViewHolderType
import gsshop.mobile.v2.home.shop.flexible.beauty.common.AdapterMenuCommon
import gsshop.mobile.v2.support.ui.RecyclerItemClickListener
import kotlinx.android.synthetic.main.view_holder_tab_beauty_menu_best.view.*
import roboguice.util.Ln

class BeautyTabMenuBest(itemView: View, context: Context) : BaseViewHolder(itemView) {

    private var mItemClickListener1st : RecyclerItemClickListener? = null
    private var mItemClickListener2nd : RecyclerItemClickListener? = null

    // 두개의 리스트 위치 (초기 0)
    private var mListPosition = mutableListOf(0, 0)

    private val mRootView = itemView

    private var mAdapter1st: AdapterMenuCommon? = null
    private var mAdapter2nd: AdapterMenuCommon? = null

    init {
        val layoutManager = LinearLayoutManager(context)
        val layoutManager2 = LinearLayoutManager(context)
        
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        layoutManager2.orientation = LinearLayoutManager.HORIZONTAL

        mRootView.list_1st?.layoutManager = layoutManager
        mRootView.list_2nd?.layoutManager = layoutManager2

    }
    
    override fun onBindViewHolder(context: Context?, position: Int, info: ShopInfo?, action: String?, label: String?, sectionName: String?) {
        super.onBindViewHolder(context, position, info, action, label, sectionName)

        Ln.d("hklim BeautyTabMenuBest onBindViewHolder : " + mListPosition[0] + " / " + mListPosition[1])

        val item = info?.contents?.get(position)?.sectionContent ?: return
        item.subProductList ?: return

        // 들어오는 값 가지고 뿌릴건데 try catch 묶어서 그냥 빠져나가게 할거다. 귀찮다 나누는것도 귀찮다.
        // 연봉도 안올려주는데 그냥 대충해야지.
        try {
            // 클릭 리스너 삭제
            if (mItemClickListener1st != null)
                mRootView.list_1st.removeOnItemTouchListener(mItemClickListener1st!!)

            if (mItemClickListener2nd != null)
                mRootView.list_2nd.removeOnItemTouchListener(mItemClickListener2nd!!)

            if (item.subProductList!!.size > 0) {
                mAdapter1st = AdapterMenuCommon(context, item.subProductList!![0].subProductList!!, mRootView.list_1st)
                mAdapter1st?.setSelectedItem(mListPosition[0])
                mRootView.list_1st.adapter = mAdapter1st
            }

            if (item.subProductList!!.size > 1) {
                mAdapter2nd = AdapterMenuCommon(context, item.subProductList!![1].subProductList!!, mRootView.list_2nd)
                mAdapter2nd?.setSelectedItem(mListPosition[1])
                mRootView.list_2nd.adapter = mAdapter2nd
            }

            // 클릭도 두개 그냥 다 따로 만들꺼야 돈도 안올려 주는데 그냥 막 할래...

            mItemClickListener1st = RecyclerItemClickListener(mRootView.context, RecyclerItemClickListener.OnItemClickListener { _, clickPosition ->
                if (clickPosition == mListPosition[0]) {
                    return@OnItemClickListener
                }

                mListPosition[0] = clickPosition

                mRootView.list_1st.scrollToPosition(clickPosition)

                try {
                    val link = item.linkUrl +
                            item.subProductList!![0].subProductList?.get(mListPosition[0])!!.linkUrl +
                            item.subProductList!![1].subProductList?.get(mListPosition[1])!!.linkUrl
                    EventBus.getDefault().post(Events.BeautyShopEvent.BeautyRefreshEvent(link,
                            null, mListPosition,
                            position + 1, ViewHolderType.VIEW_TYPE_PRD_2_BEAUTY_BEST))

                    (context as AbstractBaseActivity).setWiseLogHttpClient(ServerUrls.WEB.WISE_CLICK_URL +
                            "?mesq=" + item.subProductList!![0].subProductList?.get(mListPosition[0])!!.mseq)
                }
                catch (e:NullPointerException) {
                    Ln.e(e.message)
                }
                catch (e:ClassCastException) {
                    Ln.e(e.message)
                }
            })

            mItemClickListener2nd = RecyclerItemClickListener(mRootView.context, RecyclerItemClickListener.OnItemClickListener { _, clickPosition ->
                if (clickPosition == mListPosition[1]) {
                    return@OnItemClickListener
                }

                mListPosition[1] = clickPosition

                mRootView.list_2nd.scrollToPosition(clickPosition)

                try {
                    val link = item.linkUrl +
                            item.subProductList!![0].subProductList?.get(mListPosition[0])!!.linkUrl +
                            item.subProductList!![1].subProductList?.get(mListPosition[1])!!.linkUrl
                    EventBus.getDefault().post(Events.BeautyShopEvent.BeautyRefreshEvent(link,
                            null, mListPosition,
                            position + 1, ViewHolderType.VIEW_TYPE_PRD_2_BEAUTY_BEST))
                    (context as AbstractBaseActivity).setWiseLogHttpClient(ServerUrls.WEB.WISE_CLICK_URL +
                            "?mesq=" + item.subProductList!![1].subProductList?.get(mListPosition[1])!!.mseq)
                }
                catch (e:NullPointerException) {
                    Ln.e(e.message)
                }
                catch (e:ClassCastException) {
                    Ln.e(e.message)
                }
            })

            mRootView.list_1st.addOnItemTouchListener(mItemClickListener1st!!)
            mRootView.list_2nd.addOnItemTouchListener(mItemClickListener2nd!!)

        }
        catch (e:NullPointerException) {
            Ln.e(e.message)
        }
        catch (e:IndexOutOfBoundsException) {
            Ln.e(e.message)
        }
    }

    override fun onViewAttachedToWindow() {
        super.onViewAttachedToWindow()
        try {
            (mRootView.list_1st?.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(mListPosition[0], mRootView.list_1st.width / 3)
            (mRootView.list_2nd?.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(mListPosition[1], mRootView.list_2nd.width / 3)
        }
        catch (e:IndexOutOfBoundsException) {
            Ln.e(e.message)
        }
        catch (e:ClassCastException) {
            Ln.e(e.message)
        }
        catch (e:NullPointerException) {
            Ln.e(e.message)
        }
    }

    fun setListPosition(position: MutableList<Int>){
        if (position != null) {
            mListPosition = position
        }
    }

    fun getListPosition(): MutableList<Int> {
        return mListPosition
    }
}