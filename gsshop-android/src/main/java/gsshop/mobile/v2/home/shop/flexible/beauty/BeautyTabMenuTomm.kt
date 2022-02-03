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
import kotlinx.android.synthetic.main.view_holder_tab_beauty_menu.view.*
import roboguice.util.Ln

class BeautyTabMenuTomm(itemView: View, context: Context) : BaseViewHolder(itemView) {

    private var mItemClickListener : RecyclerItemClickListener? = null

    // 두개의 리스트 위치 (초기 0)
    private var mListPosition = 0

//    private var mListPosition: HashMap<Int, Int> = HashMap<Int, Int>()

    private val mRootView = itemView

    private var mAdapter: AdapterMenuCommon? = null

    init {
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL

        mRootView.list_beauty?.layoutManager = layoutManager
        Ln.d("hklim init BeautyTabMenuTomm")
//        mListPosition = 0
    }
    
    override fun onBindViewHolder(context: Context?, position: Int, info: ShopInfo?, action: String?, label: String?, sectionName: String?) {
        super.onBindViewHolder(context, position, info, action, label, sectionName)

//        mListPosition.get()
//        Ln.d("hklim BeautyTabMenuTomm onBindViewHolder : " + mListPosition)

        val item = info?.contents?.get(position)?.sectionContent ?: return
        item.subProductList ?: return

        // 들어오는 값 가지고 뿌릴건데 try catch 묶어서 그냥 빠져나가게 할거다. 귀찮다 나누는것도 귀찮다.
        // 연봉도 안올려주는데 그냥 대충해야지.
        try {
            // 클릭 리스너 삭제
            if (mItemClickListener != null)
                mRootView.list_beauty.removeOnItemTouchListener(mItemClickListener!!)

            if (item.subProductList!!.size > 0) {
                mAdapter = AdapterMenuCommon(context, item.subProductList!!, mRootView.list_beauty)
                mAdapter?.setSelectedItem(mListPosition)
                mRootView.list_beauty.adapter = mAdapter
            }

            mItemClickListener = RecyclerItemClickListener(mRootView.context, RecyclerItemClickListener.OnItemClickListener { _, clickPosition ->
                if (clickPosition == mListPosition) {
                    return@OnItemClickListener
                }

                mListPosition = clickPosition

                mRootView.list_beauty.scrollToPosition(mListPosition)

                try {
                    var linkUrl = item.linkUrl +
                            item.subProductList!![mListPosition].linkUrl

                    EventBus.getDefault().post(Events.BeautyShopEvent.BeautyRefreshEvent(
                            linkUrl, clickPosition, null,
                            position + 1, ViewHolderType.VIEW_TYPE_PRD_2_BEAUTY_TOMM))

                    (context as AbstractBaseActivity).setWiseLogHttpClient(ServerUrls.WEB.WISE_CLICK_URL +
                            "mseq=" + item.subProductList!![mListPosition].tabSeq)
                }
                catch (e:NullPointerException) {
                    Ln.e(e.message)
                }
                catch (e: ClassCastException) {
                    Ln.e(e.message)
                }
            })

            mRootView.list_beauty.addOnItemTouchListener(mItemClickListener!!)
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
            (mRootView.list_beauty?.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(mListPosition, mRootView.list_beauty.width / 3)
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

    fun setListPosition(position :Int) {
        mListPosition = position
    }

    fun getListPosition():Int {
        return mListPosition
    }
}