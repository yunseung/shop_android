package gsshop.mobile.v2.home.shop.retail

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import gsshop.mobile.v2.MainApplication
import gsshop.mobile.v2.home.shop.BaseViewHolder
import gsshop.mobile.v2.home.shop.ShopInfo
import gsshop.mobile.v2.home.shop.renewal.views.CommonTitleLayout
import gsshop.mobile.v2.util.DisplayUtils
import gsshop.mobile.v2.util.SwipeUtils.disableSwipe
import kotlinx.android.synthetic.main.view_holder_pmo_gsr_c_gba.view.*

class PmoGsrCGba(itemView: View) : BaseViewHolder(itemView) {
    private lateinit var mContext: Context

    private val mCommonTitleLayout: CommonTitleLayout = itemView.common_title_layout
    private val mRvList: RecyclerView = itemView.rv_list
    private val mAdapter: PmoGsrCGbaAdapter = PmoGsrCGbaAdapter()
    private var itemDecoration: RecyclerView.ItemDecoration? = null
    private var mLayoutManager: LinearLayoutManager? = null


    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(context: Context?, position: Int, info: ShopInfo?, action: String?, label: String?, sectionName: String?) {
        super.onBindViewHolder(context, position, info, action, label, sectionName)
        val item = info?.contents?.get(position)?.sectionContent ?: return

        mCommonTitleLayout.setCommonTitle(this, item)

        if (itemDecoration == null) {            //아이템 간격 세팅
            itemDecoration = SpacesItemDecoration()
            mRvList.addItemDecoration(itemDecoration as SpacesItemDecoration)
        }
        mRvList.setHasFixedSize(true)

        mRvList.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> disableSwipe()
            }
            false
        }

        mLayoutManager = LinearLayoutManager(context)
        mLayoutManager!!.orientation = LinearLayoutManager.HORIZONTAL
        mRvList.layoutManager = mLayoutManager

        mRvList.adapter = mAdapter
        mAdapter.updateProducts(item.subProductList)

    }

    /**
     * 아이템간격을 데코레이션 한다.
     */
    class SpacesItemDecoration() : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val position = parent.getChildAdapterPosition(view)
            if (position == 0) {
                outRect.left = DisplayUtils.convertPxToDp(MainApplication.getAppContext(), 12f)
            }
            if (position == parent.adapter!!.itemCount - 1) {
                outRect.right = DisplayUtils.convertPxToDp(MainApplication.getAppContext(), 12f)
            } else {
                outRect.right = DisplayUtils.convertPxToDp(MainApplication.getAppContext(), 10f)
            }
        }
    }
}