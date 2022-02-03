package gsshop.mobile.v2.home.shop.flexible

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.greenrobot.event.EventBus
import gsshop.mobile.v2.Events
import gsshop.mobile.v2.home.shop.BaseViewHolder
import gsshop.mobile.v2.home.shop.ShopInfo
import gsshop.mobile.v2.home.shop.flexible.beauty.common.AdapterMenuCommon
import gsshop.mobile.v2.support.ui.RecyclerItemClickListener
import kotlinx.android.synthetic.main.view_holder_tab_basic.view.*
import roboguice.util.Ln

class BanTabMenu(itemView: View, context: Context) : BaseViewHolder(itemView) {

    private var mItemClickListener : RecyclerItemClickListener? = null

    // 두개의 리스트 위치 (초기 0)
    private var mListPosition = 0

//    private var mListPosition: HashMap<Int, Int> = HashMap<Int, Int>()

    private val mRootView = itemView

    private var mAdapter: AdapterMenuCommon? = null

    private var mScrollX = 0

    init {
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL

        mRootView.recycler_view?.layoutManager = layoutManager
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
                mRootView.recycler_view.removeOnItemTouchListener(mItemClickListener!!)

            if (item.subProductList!!.size > 0) {
                mAdapter = AdapterMenuCommon(context, item.subProductList!!, mRootView.recycler_view)
                mAdapter?.setSelectedItem(mListPosition)
                mRootView.recycler_view.adapter = mAdapter
            }

            mItemClickListener = RecyclerItemClickListener(mRootView.context, RecyclerItemClickListener.OnItemClickListener { _, clickPosition ->
                if (clickPosition == mListPosition) {
                    return@OnItemClickListener
                }

                mListPosition = clickPosition

                mRootView.recycler_view.scrollToPosition(mListPosition)

                try {

                    val linkUrl = item.subProductList!![mListPosition].linkUrl

                    EventBus.getDefault().post(Events.FlexibleEvent.RefreshTabEvent(
                            linkUrl, clickPosition,position + 1, item.viewType))
//                            ArrayList<Int>(listOf(ViewHolderType.VIEW_TYPE_PRD_2_MOBILE_LIVE,
//                                    ViewHolderType.VIEW_TYPE_NO_DATA_MOBILE_LIVE))))
                }
                catch (e:NullPointerException) {
                    Ln.e(e.message)
                }
                catch (e: ClassCastException) {
                    Ln.e(e.message)
                }
            })

            mRootView.recycler_view.addOnItemTouchListener(mItemClickListener!!)
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
            // 뷰가 다시 보일때에 해당 위치로 이동하도록 수정.
            (mRootView.recycler_view.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(mListPosition, mRootView.recycler_view.width / 3)
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

    fun setItemBackground(resource :Int) {
        mAdapter?.setSelectedItemBackground(resource)
    }

    fun setItemBackgroundColor(color: Int) {
        mAdapter?.setSelectedItemColor(color)
    }

    fun setListPosition(position :Int) {
        mListPosition = position
    }

    fun getListPosition():Int {
        return mListPosition
    }
}