package gsshop.mobile.v2.home.shop.flexible.beauty

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
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
import kotlinx.android.synthetic.main.view_holder_ban_beauty_sld.view.*
import org.json.JSONObject
import roboguice.util.Ln

class BanBeautySldVH(itemView: View, context: Context) : BaseRollViewHolder(itemView) {
    
    private var mPageChangeListener: SimpleOnPageChangeListener? = null
    private val mTvCount: TextView = itemView.text_count
    private val mBtnPreview: FrameLayout = itemView.btn_preview
    private val mBtnNext: FrameLayout = itemView.btn_next

    init {
        viewPager = itemView.findViewById(R.id.view_pager_beauty_sld)
        DisplayUtils.resizeHeightAtViewToScreenSize(context, viewPager)
    }

    override fun onBindViewHolder(context: Context?, position: Int, info: ShopInfo?, action: String?, label: String?, sectionName: String?) {
        super.onBindViewHolder(context, position, info, action, label, sectionName)
        val content = info!!.contents[position]
        val list: List<SectionContentList>? = content.sectionContent.subProductList

        viewPager.clipToPadding = false

        if (list!!.size == 1) {
            viewPager.setPagingEnabled(false)
        } else {
            viewPager.setPagingEnabled(true)
        }

        mBtnPreview.setContentDescription("?????? ????????? ??????")

        mBtnPreview.setOnClickListener(View.OnClickListener {
            val position = viewPager.realCurrentItem - 1
            viewPager.setRealCurrentItem(position, true)
        })

        mBtnNext.setContentDescription("?????? ????????? ??????")

        mBtnNext.setOnClickListener(View.OnClickListener {
            val position = viewPager.realCurrentItem + 1
            viewPager.setRealCurrentItem(position, true)
        })

        if (list.size == 1) {
            viewPager.setPagingEnabled(false)
            mBtnPreview.visibility = View.GONE
            mBtnNext.visibility = View.GONE
            mTvCount.visibility = View.GONE
        } else {
            viewPager.setPagingEnabled(true)
            mBtnPreview.visibility = View.VISIBLE
            mBtnNext.visibility = View.VISIBLE
            mTvCount.visibility = View.VISIBLE
        }

        //??????????????? ?????????(????????????) ?????????
        mPageChangeListener = object : SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val item = viewPager.currentItem
                val count = (viewPager.adapter as InfinitePagerAdapter?)!!.realCount

                val text = (item + 1).toString() + " / " + count
                val wordToSpan: Spannable = SpannableString(text)
                wordToSpan.setSpan(ForegroundColorSpan(Color.parseColor("#66ffffff")), text.indexOf("/"), text.length, 0)


                mTvCount.text = wordToSpan
                mTvCount.contentDescription = "??? " + count + "????????? ??? " + (item + 1) + "?????????"

                content.indicator = item

                startTimer()
            }
        }

        val wrappedAdapter = InfinitePagerAdapter(RollImageAdapter(context!!, list))
        viewPager.adapter = wrappedAdapter
        
        viewPager.removeOnPageChangeListener(mPageChangeListener as SimpleOnPageChangeListener)
        viewPager.addOnPageChangeListener(mPageChangeListener as SimpleOnPageChangeListener)

        // ?????? ??????
        mTvCount.text = ("1 / " + (viewPager.adapter as InfinitePagerAdapter?)!!.realCount.toString())

    }

    /**
     * ????????? ???????????? ????????? pager adapter
     */
    private class RollImageAdapter(private val context: Context, private val list: List<SectionContentList>?) : PagerAdapter() {
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = BanBeautySldItem(context)
            view.setItem(list?.get(position)!!)

            //????????? ?????????
            if (list != null) {
                val item = list[position]
                view.contentDescription = item.productName

                //????????? ?????????
                view.setOnClickListener {
                    WebUtils.goWeb(context, item.linkUrl)

                    //??????????????? ?????? ?????????
                    Apptimize.track(AMPEnum.APPTI_CLICK_IMGBANNER)

                    //??????????????? ?????? ?????????
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