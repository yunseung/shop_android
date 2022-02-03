package gsshop.mobile.v2.home.shop.flexible.todaysel

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
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
import gsshop.mobile.v2.util.ImageUtil
import gsshop.mobile.v2.util.SwipeUtils
import gsshop.mobile.v2.web.WebUtils
import kotlinx.android.synthetic.main.view_holder_ban_sld_rounded_b_item.view.*
import kotlinx.android.synthetic.main.view_holder_ban_sld_rounded_big.view.*

class BanSldRoundedBVH(itemView: View, context: Context) : BaseRollViewHolder(itemView) {

    private var mPageChangeListener: SimpleOnPageChangeListener? = null
    private val mTvTitle: TextView = itemView.txt_title

    private val separationMargin = 16 //이미지뷰 사이사이 간격

    init {
        viewPager = itemView.findViewById(R.id.view_pager)
        DisplayUtils.resizeHeightAtViewToScreenSize(context, viewPager)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(context: Context?, position: Int, info: ShopInfo?, action: String?, label: String?, sectionName: String?) {
        super.onBindViewHolder(context, position, info, action, label, sectionName)
        val content = info!!.contents[position]
        val list: List<SectionContentList>? = content.sectionContent.subProductList
        val item = content.sectionContent

        if (item.name.isNullOrEmpty()) {
            mTvTitle.visibility = View.GONE
        } else {
            mTvTitle.visibility = View.VISIBLE
            mTvTitle.text = item.name
        }

        if (list!!.size == 1) {
            viewPager.setPagingEnabled(false)
            viewPager.visibility = View.GONE
            itemView.card_main.visibility = View.VISIBLE
            if (!list[0].imageUrl.isNullOrEmpty()) {
                ImageUtil.loadImageResize(mContext, list[0].imageUrl, itemView.img_main, R.drawable.noimage_375_375)
            }
            itemView.img_main.setOnClickListener {
                WebUtils.goWeb(context, list[0].linkUrl)
            }
            return
        }
        else{
            viewPager.visibility = View.VISIBLE
            itemView.card_main.visibility = View.GONE
        }

        viewPager.clipToPadding = false
        viewPager.pageMargin = separationMargin //뷰 양옆 여백(10dp)

        var preview = context!!.resources
            .getDimensionPixelOffset(R.dimen.today_sel_big_view_width)
        var margin = context.resources
            .getDimensionPixelOffset(R.dimen.today_sel_big_view_pager_margin)
        var leftPadding = DisplayUtils.convertDpToPx(context, 16f)
        var itemWidth = DisplayUtils.convertDpToPx(context, 290f)

        // 빅 슬라이드 배너 쓸때마다 선언하는것은 좋지 않음. 모듈로 빼야할듯

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

        viewPager.setPadding(0, 0, tempWidth, 0)
        viewPager.clipToPadding = false
        viewPager.pageMargin = margin

        //이미지배너 바뀔때(이동할때) 리스너
        mPageChangeListener = object : SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                startTimer()
            }
        }

        val wrappedAdapter = RollImageAdapter(context!!, list)
        viewPager.adapter = wrappedAdapter

        viewPager.removeOnPageChangeListener(mPageChangeListener as SimpleOnPageChangeListener)
        viewPager.addOnPageChangeListener(mPageChangeListener as SimpleOnPageChangeListener)
    }

    /**
     * 이미지 롤링배너 어댑터 pager adapter
     */
    private class RollImageAdapter(private val context: Context, private val list: List<SectionContentList>?) : PagerAdapter() {
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = BanSldRoundedBItem(context)
            view.setItem(list?.get(position)!!)

            //이미지 리스트
            if (list != null) {
                val item = list[position]
                view.contentDescription = item.productName

                //이미지 선택시
                view.setOnClickListener {
                    WebUtils.goWeb(context, item.linkUrl)
                }
//                ImageUtil.loadImageFitWithRoundCenterCrop(context, item.imageUrl, view.findViewById(R.id.iv_img),
//                    DisplayUtils.convertDpToPx(context, 16f), R.drawable.noimage_375_375)
//                ImageUtil.loadImageFit(context, item.imageUrl, view.findViewById(R.id.iv_img), R.drawable.noimage_375_375)



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