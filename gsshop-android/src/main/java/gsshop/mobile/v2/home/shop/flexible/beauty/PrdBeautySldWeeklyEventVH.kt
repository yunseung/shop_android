package gsshop.mobile.v2.home.shop.flexible.beauty

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener
import com.apptimize.Apptimize
import gsshop.mobile.v2.ApptimizeExpManager
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.main.SectionContentList
import gsshop.mobile.v2.home.shop.ShopInfo
import gsshop.mobile.v2.home.shop.flexible.BaseRollViewHolder
import gsshop.mobile.v2.library.viewpager.InfinitePagerAdapter
import gsshop.mobile.v2.support.gtm.AMPAction
import gsshop.mobile.v2.support.gtm.AMPEnum
import gsshop.mobile.v2.util.DisplayUtils
import gsshop.mobile.v2.web.WebUtils
import kotlinx.android.synthetic.main.view_holder_prd_beauty_sld_weekly_event.view.*
import org.json.JSONObject
import roboguice.util.Ln

class PrdBeautySldWeeklyEventVH(itemView: View, context: Context) : BaseRollViewHolder(itemView) {
    private var mPageChangeListener: SimpleOnPageChangeListener? = null

    private val mTvTitle: TextView = itemView.tv_name
    private val mTvCurrentPage: TextView = itemView.tv_current_page
    private val mTvAllPageCnt: TextView = itemView.tv_all_page_cnt

    init {
        viewPager = itemView.findViewById(R.id.view_pager_weekly_event)
        DisplayUtils.resizeHeightAtViewToScreenSize(context, viewPager)

        val pRightNew = DisplayUtils.getResizedPixelSizeToScreenSize(context, viewPager.paddingRight)
        viewPager.setPadding(0, 0, pRightNew, 0)
    }

    override fun onBindViewHolder(context: Context?, position: Int, info: ShopInfo?, action: String?, label: String?, sectionName: String?) {
        super.onBindViewHolder(context, position, info, action, label, sectionName)

        val content = info!!.contents[position]
        val list: List<SectionContentList>? = content.sectionContent.subProductList

        /**
         * 페이지 이동.
         */
        mPageChangeListener = object : SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                startTimer()

                mTvAllPageCnt.text = (viewPager.adapter as InfinitePagerAdapter?)!!.realCount.toString()
                mTvCurrentPage.text = (viewPager.currentItem + 1).toString()
            }
        }

        viewPager.clipToPadding = false

        val wrappedAdapter = InfinitePagerAdapter(RollImageAdapter(context!!, list))

        viewPager.adapter = wrappedAdapter

        viewPager.removeOnPageChangeListener(mPageChangeListener as SimpleOnPageChangeListener)
        viewPager.addOnPageChangeListener(mPageChangeListener as SimpleOnPageChangeListener)

        mTvAllPageCnt.text = (viewPager.adapter as InfinitePagerAdapter?)!!.realCount.toString()

        setRandom(content)

        mTvTitle.text = content.sectionContent.name

    }

    /**
     * 이미지 롤링배너 어댑터 pager adapter
     */
    private class RollImageAdapter(private val context: Context, private val list: List<SectionContentList>?) : PagerAdapter() {
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = PrdBeautySldWeeklyEventItem(context)
            view.setItem(list?.get(position)!!)

            //이미지 리스트
            val item = list[position]
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
            container.addView(view)
            return view
        }

        override fun getCount(): Int {
            return list?.size ?: 0
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view === obj as View
        }

        override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
            collection.removeView(view as View)
        }
    }
}