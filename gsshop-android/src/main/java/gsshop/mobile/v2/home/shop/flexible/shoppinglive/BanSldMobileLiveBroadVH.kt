package gsshop.mobile.v2.home.shop.flexible.shoppinglive

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.apptimize.Apptimize
import de.greenrobot.event.EventBus
import gsshop.mobile.v2.ApptimizeExpManager
import gsshop.mobile.v2.Events
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.main.SectionContentList
import gsshop.mobile.v2.home.shop.BaseViewHolder
import gsshop.mobile.v2.home.shop.ShopInfo
import gsshop.mobile.v2.home.shop.flexible.shoppinglive.items.BanSldMobileLiveBroadItem
import gsshop.mobile.v2.support.gtm.AMPAction
import gsshop.mobile.v2.support.gtm.AMPEnum
import gsshop.mobile.v2.util.DisplayUtils
import gsshop.mobile.v2.util.DisplayUtils.getResizedPixelSizeToScreenSize
import gsshop.mobile.v2.util.SwipeUtils.disableSwipe
import gsshop.mobile.v2.web.WebUtils
import org.json.JSONObject
import roboguice.util.Ln

class BanSldMobileLiveBroadVH(itemView: View, context: Context) : BaseViewHolder(itemView) {

    private val separationMargin = 10 //이미지뷰 사이사이 간격

    private var mViewPager: ViewPager = itemView.findViewById(R.id.view_pager)

    init {
        DisplayUtils.resizeHeightAtViewToScreenSize(context, mViewPager)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(context: Context?, position: Int, info: ShopInfo?, action: String?, label: String?, sectionName: String?) {
        super.onBindViewHolder(context, position, info, action, label, sectionName)
        val content = info!!.contents[position]
        val list: List<SectionContentList>? = content.sectionContent.subProductList

        mPagePosition = 0

        mViewPager.clipToPadding = false
        mViewPager.pageMargin = separationMargin //뷰 양옆 여백(10dp)

        var preview = context!!.resources
                .getDimensionPixelOffset(R.dimen.mobile_live_board_item_preview_width)
        var margin = context!!.resources
                .getDimensionPixelOffset(R.dimen.mobile_live_board_item_margin_pager_margin)
        var leftPadding = DisplayUtils.convertDpToPx(context, 16f)
        var itemWidth = DisplayUtils.convertDpToPx(context, 300f)

        preview = getResizedPixelSizeToScreenSize(context!!, preview)
        margin = getResizedPixelSizeToScreenSize(context!!, margin)
        leftPadding = getResizedPixelSizeToScreenSize(context!!, leftPadding)
        itemWidth = getResizedPixelSizeToScreenSize(context!!, itemWidth)

        var tempWidth = DisplayUtils.screenWidth - preview - margin - leftPadding

        if (tempWidth < itemWidth) {
            tempWidth = DisplayUtils.screenWidth - itemWidth - margin - leftPadding
        } else {
            tempWidth = preview
        }

        mViewPager.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> disableSwipe()
            }
            false
        }

        mViewPager.addOnPageChangeListener(
            object : ViewPager.SimpleOnPageChangeListener() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    mPagePosition = position
                    try {
                        if ( "LIVE" == list?.get(position)?.viewType ) {
//                        if (position == 0) { // test
                            (mViewPager.adapter as RollImageAdapter).setPlayerPlay(true)
                        }
                        else {
                            (mViewPager.adapter as RollImageAdapter).setPlayerPlay(false)
                            (mViewPager.adapter as RollImageAdapter).setMute(true)      // 사라졌다 나타나면 mute
                        }
                    }
                    catch (e:IndexOutOfBoundsException) {
                        Ln.e(e.message)
                    }
                }
            }
        )

        mViewPager.setPadding(0, 0, tempWidth, 0)
        mViewPager.clipToPadding = false
        mViewPager.pageMargin = margin

        mViewPager.adapter = RollImageAdapter(context, list)
    }

    fun onEvent(event: Events.ShoppingLiveEvent.RemoveLivePlayerEvent) {
        val curationEvent = EventBus.getDefault()
            .getStickyEvent(Events.ShoppingLiveEvent.RemoveLivePlayerEvent::class.java)
        if (curationEvent != null) {
            EventBus.getDefault().removeStickyEvent(curationEvent)
        }

        val adapter = mViewPager.adapter as RollImageAdapter

        for (i in 0..adapter.count) {
            if (event.mPosition == i) {
                mViewPager.adapter = null
                adapter.removeItem(i)
                mViewPager.adapter = adapter
//                adapter.notifyDataSetChanged()
                break
            }
        }

    }

    /**
     * 이미지 롤링배너 어댑터 pager adapter
     */
    private class RollImageAdapter(private val context: Context, private val list: List<SectionContentList>?) : PagerAdapter() {
        var mViewLive : BanSldMobileLiveBroadItem? = null

        val mList:ArrayList<SectionContentList> = list as ArrayList<SectionContentList>

        fun getList():List<SectionContentList>? {
            return mList
        }

        fun removeItem(position: Int) {
            mList.removeAt(position)
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = BanSldMobileLiveBroadItem(context)
            view.setItem(mList?.get(position)!!, position, mPagePosition)

            //이미지 리스트
            if (mList != null) {
                val item = mList[position]
                view.contentDescription = item.productName

                //이미지 선택시
                view.setOnClickListener {
                    WebUtils.goWeb(context, item.linkUrl)

                    //앱티마이즈 클릭 이벤트
                    Apptimize.track(AMPEnum.APPTI_CLICK_IMGBANNER)

                    //앰플리튜드 클릭 이벤트
                    try {
                        val eventProperties = JSONObject()
                        eventProperties.put(AMPEnum.AMP_INDEX, position)
                        eventProperties.put(AMPEnum.AMP_AB_INFO, ApptimizeExpManager.TYPE_A)
                        AMPAction.sendAmpEventProperties(AMPEnum.AMP_CLICK_IMGBANNER, eventProperties)
                    } catch (e: Exception) {
                        Ln.e(e)
                    }
                }
//                loadImageFit(context, item.imageUrl, view.findViewById(R.id.img), R.drawable.noimg_logo)
                if ("LIVE" == mList[position].viewType) {
//                if (position == 0) { // test
                    mViewLive = view
                }

                container.addView(view)
            }
            return view
        }

        override fun getCount(): Int {
            return mList?.size ?: 0
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view === obj as View
        }

        override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
            collection.removeView(view as View)
        }

        fun setPlayerPlay(isPlay : Boolean) {
            mViewLive?.setPlayerPlay(isPlay)
        }

        fun setMute(isMute : Boolean) {
            mViewLive?.setMute(isMute)
        }
    }

    companion object {
        var mPagePosition = 0
    }

    override fun onViewAttachedToWindow() {
        super.onViewAttachedToWindow()
        EventBus.getDefault().register(this)
    }

    override fun onViewDetachedFromWindow() {
        super.onViewDetachedFromWindow()
        EventBus.getDefault().unregister(this)
    }

}