package gsshop.mobile.v2.home.shop.flexible.wine

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ConvertUtils
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.shop.BaseViewHolder
import gsshop.mobile.v2.home.shop.ShopInfo
import gsshop.mobile.v2.home.shop.flexible.wine.common.RecyclerAdapterWineHorizontal
import gsshop.mobile.v2.util.ImageUtil
import gsshop.mobile.v2.util.StringUtils
import gsshop.mobile.v2.util.SwipeUtils
import gsshop.mobile.v2.web.WebUtils
import kotlinx.android.synthetic.main.view_holder_prd_pmo_list_wine.view.*
import roboguice.util.Ln
import java.util.*
import kotlin.collections.ArrayList

class PrdPmoListWineVH (itemView: View) : BaseViewHolder(itemView) {

    // 좌우 간격 조정을 item decoration
    var mItemDecoration = SpacesItemDecoration()

    /**
     * 타이머 타스크
     */
    private var timerTask : Timer? = Timer()

    init {
        //스와이프 허용각도 확대 적용
        increaseSwipeAngle = true
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(context: Context, position: Int, info: ShopInfo?, action: String?, label: String?, sectionName: String?) {
        val item = info?.contents?.get(position)?.sectionContent ?: return

        var startTime: Long = System.currentTimeMillis()
        if (item.endDate == null ||
                (item.endDate != null && item.endDate!! - startTime < 0)) {
            var params = itemView.root_view.layoutParams
            params.height = 0
            itemView.root_view.layoutParams = params
            return
        }

        if( item.subProductList == null ) item.subProductList = ArrayList()

        if (!TextUtils.isEmpty(item.linkUrl)) {
            itemView.rootView.img_background.setOnClickListener {
                WebUtils.goWeb(context, item.linkUrl)
            }
        }
        if (!TextUtils.isEmpty(item.tabBgImg)) {
            ImageUtil.loadImageResize(context, item.tabBgImg, itemView.rootView.img_background, R.drawable.noimage_375_188)
        }

        if (!TextUtils.isEmpty(item.tabImg)) {
            ImageUtil.loadImageFit(context, item.tabImg, itemView.rootView.img_stopwatch, R.drawable.noimage_78_78)
        }

        if (!TextUtils.isEmpty(item.name)) {
            itemView.rootView.txt_main.text = item.name
        }

        executeTask(context, item.endDate!!)

        val recycler = itemView.rootView.recycler_goods

        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL

        recycler.layoutManager = layoutManager
        recycler.adapter = RecyclerAdapterWineHorizontal(
                context, item.subProductList!!, R.layout.banner_item_prd_pmo_list_wine, false)

        recycler.removeItemDecoration(mItemDecoration)
        recycler.addItemDecoration(mItemDecoration)

        recycler.setOnTouchListener { _: View, event: MotionEvent ->
            when(event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> SwipeUtils.disableSwipe()
            }
            false
        }
    }

    /**
     * 아이템간격을 데코레이션 한다.
     */
    inner class SpacesItemDecoration : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val position = parent.getChildAdapterPosition(view)
            if (position == 0) {
                outRect.left = ConvertUtils.dp2px(16f)
            } else {
                outRect.left = ConvertUtils.dp2px(8f)
            }
            if (position == parent.adapter!!.itemCount - 1) {
                outRect.right = ConvertUtils.dp2px(16f)
            }
            else {
                outRect.right = 0
            }
        }
    }

    /**
     * 타스크를 생성한다.
     *
     * @param delay
     */
    private fun executeTask(context: Context, endTime: Long) {
        stopTask()
        timerTask = Timer()
        try {
            timerTask!!.schedule(object : TimerTask() {
                override fun run() {
                    setRemainTime(context, endTime)
                }
            }, 1000, 1000)
        }
        catch (e:IllegalStateException){
            Ln.e(e.message)
        }
        catch (e:NullPointerException) {
            Ln.e(e.message)
        }
    }

    private fun stopTask() {
        if (timerTask != null) {

            timerTask!!.cancel()
            timerTask = null

        }
    }

    private fun setRemainTime(context: Context, endTime: Long) {

        var startTime: Long = System.currentTimeMillis()
        var defTime = endTime - startTime

        try {
            if (defTime > 0) {
                itemView.rootView.txt_sub.visibility = View.VISIBLE
                (context as Activity).runOnUiThread {
                    itemView.rootView.txt_sub.text = StringUtils.stringForHHMMSS(defTime, false)
                }
            } else {
                (context as Activity).runOnUiThread {

                    var params = itemView.root_view.layoutParams
                    params.height = 0
                    itemView.root_view.layoutParams = params
                }
                stopTask()
            }
        }
        catch (e:NullPointerException) {
            Ln.e(e.message)
        }
    }
}