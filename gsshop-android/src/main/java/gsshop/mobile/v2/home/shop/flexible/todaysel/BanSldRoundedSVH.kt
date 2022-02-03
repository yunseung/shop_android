package gsshop.mobile.v2.home.shop.flexible.todaysel

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.main.SectionContentList
import gsshop.mobile.v2.home.shop.ShopInfo
import gsshop.mobile.v2.home.shop.flexible.BaseRollViewHolder
import gsshop.mobile.v2.library.viewpager.InfinitePagerAdapter
import gsshop.mobile.v2.util.DisplayUtils
import gsshop.mobile.v2.web.WebUtils
import kotlinx.android.synthetic.main.view_holder_ban_sld_rounded_small.view.*

class BanSldRoundedSVH(itemView: View, context: Context) : BaseRollViewHolder(itemView) {

    private var mPageChangeListener: SimpleOnPageChangeListener? = null
    private val mTvCount: TextView = itemView.text_count
    private val mViewCount: View = itemView.view_count

    init {
        viewPager = itemView.findViewById(R.id.view_pager)
        DisplayUtils.resizeHeightAtViewToScreenSize(context, viewPager)
    }

    override fun onBindViewHolder(context: Context?, position: Int, info: ShopInfo?, action: String?, label: String?, sectionName: String?) {
        super.onBindViewHolder(context, position, info, action, label, sectionName)
        val content = info!!.contents[position]
        val list: List<SectionContentList>? = content.sectionContent.subProductList
        val item = content.sectionContent

        if (item.name.isNullOrEmpty()) {
            itemView.txt_title.visibility = View.GONE
            itemView.view_top_divider.visibility = View.VISIBLE
        } else {
            itemView.txt_title.visibility = View.VISIBLE
            itemView.view_top_divider.visibility = View.GONE
            itemView.txt_title.text = item.name
        }

        viewPager.clipToPadding = false

        if (list?.size == 1) {
            viewPager.setPagingEnabled(false)
            mViewCount.visibility = View.GONE
//            mTvCount.visibility = View.GONE
        } else {
            viewPager.setPagingEnabled(true)
//            mTvCount.visibility = View.VISIBLE
            mViewCount.visibility = View.VISIBLE
        }

        //이미지배너 바뀔때(이동할때) 리스너
        mPageChangeListener = object : SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val item = viewPager.currentItem
                val count = (viewPager.adapter as InfinitePagerAdapter?)!!.realCount

                val text = (item + 1).toString() + "/" + count
//                val wordToSpan: Spannable = SpannableString(text)
//                wordToSpan.setSpan(ForegroundColorSpan(Color.parseColor("#66ffffff")), text.indexOf("/"), text.length, 0)

                mTvCount.text = text
                mTvCount.contentDescription = "총 " + count + "페이지 중 " + (item + 1) + "페이지"

                content.indicator = item

                startTimer()
            }
        }

        val wrappedAdapter = InfinitePagerAdapter(RollImageAdapter(context!!, list))
        viewPager.adapter = wrappedAdapter

        viewPager.removeOnPageChangeListener(mPageChangeListener as SimpleOnPageChangeListener)
        viewPager.addOnPageChangeListener(mPageChangeListener as SimpleOnPageChangeListener)

        // 초기 셋팅
        mTvCount.text = ("1/" + (viewPager.adapter as InfinitePagerAdapter?)!!.realCount.toString())

    }

    /**
     * 이미지 롤링배너 어댑터 pager adapter
     */
    private class RollImageAdapter(private val context: Context, private val list: List<SectionContentList>?) : PagerAdapter() {
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = BanSldRoundedSItem(context)
            view.setItem(list?.get(position)!!)

            //이미지 리스트
            if (list != null) {
                val item = list[position]
                view.contentDescription = item.productName

                //이미지 선택시
                view.setOnClickListener {
                    WebUtils.goWeb(context, item.linkUrl)

//                    //앱티마이즈 클릭 이벤트
//                    Apptimize.track(AMPEnum.APPTI_CLICK_IMGBANNER)
//
//                    //앰플리튜드 클릭 이벤트
//                    try {
//                        val eventProperties = JSONObject()
//                        eventProperties.put(AMPEnum.AMP_INDEX, position)
//                        eventProperties.put(AMPEnum.AMP_AB_INFO, ApptimizeExpManager.TYPE_A)
//                        AMPAction.sendAmpEventProperties(AMPEnum.AMP_CLICK_IMGBANNER, eventProperties)
//                    } catch (e: Exception) {
//                        Ln.e(e)
//                    }
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