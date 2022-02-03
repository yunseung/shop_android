package gsshop.mobile.v2.home.shop.flexible.beauty

import android.content.Context
import android.view.View
import android.view.ViewGroup
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
import gsshop.mobile.v2.util.ImageUtil
import gsshop.mobile.v2.web.WebUtils
import kotlinx.android.synthetic.main.view_holder_ban_beauty_sld_today.view.*
import org.json.JSONObject
import roboguice.util.Ln

class BanBeautySldTodayVH(itemView: View, context: Context) : BaseRollViewHolder(itemView) {
    private var mPageChangeListener: SimpleOnPageChangeListener? = null

    init {
        viewPager = itemView.findViewById(R.id.viewPager)
        DisplayUtils.resizeHeightAtViewToScreenSize(context, viewPager)
    }

    override fun onBindViewHolder(context: Context?, position: Int, info: ShopInfo?, action: String?, label: String?, sectionName: String?) {
        super.onBindViewHolder(context, position, info, action, label, sectionName)

        val content = info!!.contents[position]
        val list: List<SectionContentList>? = content.sectionContent.subProductList

        itemView.tv_title.text = content.sectionContent.name

        if (list != null) {
            if (list.size > 1) {
                viewPager.visibility = View.VISIBLE
                itemView.fl_single_item_area.visibility = View.GONE

                var preview = context!!.resources
                        .getDimensionPixelOffset(R.dimen.mobile_live_board_item_preview_width)
                var margin = context!!.resources
                        .getDimensionPixelOffset(R.dimen.mobile_live_board_item_margin_pager_margin)
                var leftPadding = DisplayUtils.convertDpToPx(context, 16f)
                var itemWidth = DisplayUtils.convertDpToPx(context, 300f)

                preview = DisplayUtils.getResizedPixelSizeToScreenSize(context!!, preview)
                margin = DisplayUtils.getResizedPixelSizeToScreenSize(context!!, margin)
                leftPadding = DisplayUtils.getResizedPixelSizeToScreenSize(context!!, leftPadding)
                itemWidth = DisplayUtils.getResizedPixelSizeToScreenSize(context!!, itemWidth)

                var tempWidth = DisplayUtils.screenWidth - preview - margin - leftPadding

                tempWidth = if (tempWidth < itemWidth) {
                    DisplayUtils.screenWidth - itemWidth - margin - leftPadding
                } else {
                    preview
                }

                /**
                 * 페이지 이동.
                 */
                mPageChangeListener = object : SimpleOnPageChangeListener() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        startTimer()
                    }
                }

                viewPager.setPadding(0, 0, tempWidth, 0)
                viewPager.clipToPadding = false
                viewPager.pageMargin = margin

                val wrappedAdapter = InfinitePagerAdapter(RollImageAdapter(context, list))

                viewPager.adapter = wrappedAdapter

                viewPager.removeOnPageChangeListener(mPageChangeListener as SimpleOnPageChangeListener)
                viewPager.addOnPageChangeListener(mPageChangeListener as SimpleOnPageChangeListener)

                setRandom(content)
            } else {
                viewPager.visibility = View.GONE
                itemView.fl_single_item_area.visibility = View.VISIBLE

                if (list[0].imageUrl != null) {
                    ImageUtil.loadImageFit(mContext, list[0].imageUrl, itemView.iv_product_img, R.drawable.noimage_375_375)
                }
                if (list[0].linkUrl != null) {
                    itemView.fl_single_item_area.setOnClickListener { WebUtils.goWeb(context, list[0].linkUrl) }
                }
                if (list[0].name != null) {
                    itemView.tv_product_name.text = list[0].name
                }
                if (list[0].subName != null) {
                    itemView.tv_sub_name.text = list[0].subName
                }
            }
        }


    }

    /**
     * 이미지 롤링배너 어댑터 pager adapter
     */
    private class RollImageAdapter(private val context: Context, private val list: List<SectionContentList>?) : PagerAdapter() {
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = BanBeautySldTodayItem(context)
            view.setItem(list?.get(position)!!)

            //이미지 리스트
            if (list != null) {
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
            }
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