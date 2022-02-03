package gsshop.mobile.v2.home.shop.flexible.beauty

import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.gsshop.mocha.ui.util.ViewUtils
import gsshop.mobile.v2.Keys
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.main.ProductInfoUtil
import gsshop.mobile.v2.home.main.SectionContentList
import gsshop.mobile.v2.home.shop.BaseViewHolder
import gsshop.mobile.v2.home.shop.ShopInfo
import gsshop.mobile.v2.util.DisplayUtils
import gsshop.mobile.v2.util.ImageUtil
import gsshop.mobile.v2.util.StringUtils
import gsshop.mobile.v2.web.WebUtils
import kotlinx.android.synthetic.main.view_holder_ban_beauty_sld_time_sale.view.*
import roboguice.util.Ln
import java.util.*

class BanBeautySldTimeSaleVH(itemView: View, context: Context) : BaseViewHolder(itemView) {
    private var mTimer: Timer? = Timer()

    private val mViewPager: ViewPager = itemView.findViewById(R.id.view_pager)
    private val mTvTime: TextView = itemView.findViewById(R.id.tv_beauty_time_time_text)
    private val mIvImg: ImageView = itemView.findViewById(R.id.iv_beauty_time_img)
    private val mTvProductName: TextView = itemView.findViewById(R.id.tv_beauty_time_product_name)
    private val mTvPrice: TextView = itemView.findViewById(R.id.tv_beauty_time_price)
    private val mTvPriceUnit: TextView = itemView.findViewById(R.id.tv_beauty_time_price_unit)
    private val mTvDiscountRate: TextView = itemView.findViewById(R.id.tv_beauty_time_discount_rate)
    private val mTvDiscountRateUnit: TextView = itemView.findViewById(R.id.tv_beauty_time_discount_rate_unit)
    private val mTvBasePrice: TextView = itemView.findViewById(R.id.tv_beauty_time_base_price)

    open lateinit var mList: MutableList<SectionContentList>

    private lateinit var mContext: Context

    init {
        DisplayUtils.resizeHeightAtViewToScreenSize(context, mViewPager)
        val pRightNew = DisplayUtils.getResizedPixelSizeToScreenSize(context, mViewPager.paddingRight)
        mViewPager.setPadding(0, 0, pRightNew, 0)
    }

    override fun onBindViewHolder(context: Context, position: Int, info: ShopInfo?, action: String?, label: String?, sectionName: String?) {
        super.onBindViewHolder(context, position, info, action, label, sectionName)
        mContext = context

        val content = info!!.contents[position]

        mList = arrayListOf()

        for (i in content.sectionContent.subProductList!!) {
            if (i.endDate!! - System.currentTimeMillis() > 0) {
                this.mList.add(i)
            }
        }

        if (content.sectionContent.name != null) {
            itemView.tv_title.text = content.sectionContent.name
        }

        drawUI(context)

    }

    private fun setZeroTimeItemRemove(position: Int) {
        if (position >= mList.size) {
            return
        }

        mList.removeAt(position)
        (mContext as Activity).runOnUiThread {
            drawUI(mContext)
        }
    }

    private fun drawUI(context: Context) {
        if (mList.size < 1) {
            return
        }

        if (mList.size > 1) {
            mViewPager.visibility = View.VISIBLE
            itemView.ll_single_item_area.visibility = View.GONE

            mViewPager.clipToPadding = false

            mViewPager.removeAllViews()
            mViewPager.adapter = RollImageAdapter(context, mList, this)
        } else {
            itemView.ll_single_item_area.visibility = View.VISIBLE
            mViewPager.visibility = View.GONE

            if (mList[0].imageUrl != null) {
                ImageUtil.loadImageFit(mContext, mList[0].imageUrl, mIvImg, R.drawable.noimage_375_375)
            }

            if (mList[0].linkUrl != null) {
                itemView.root_view.setOnClickListener { WebUtils.goWeb(mContext, mList[0].linkUrl) }
            }

            if (mList[0].endDate != null) {
                setRemainTime(mContext, mList[0].endDate!!)
            }

            if (mList[0].productName != null) {
                mTvProductName.text = mList[0].productName
            }


            // 초기화
            mTvPrice.text = ""
            mTvPriceUnit.text = ""
            mTvDiscountRate.text = ""
            mTvDiscountRateUnit.text = ""
            mTvBasePrice.text = ""
            // 퍼센트값으로 표현되는 할인률이 있을 경우에만 base price 표시
            // base price가 0이거나 salePrice가 basePrice보다 크면 basePrice 표시 안함
            val salePrice = DisplayUtils.getNumberFromString(mList[0].salePrice)
            val basePrice = DisplayUtils.getNumberFromString(mList[0].basePrice)

            if (mList[0].discountRateText != null) {
                mTvDiscountRateUnit.text = mList[0].discountRateText
            }

            // 베이스가 처리로직: 할인률 있으면
            if (mList[0].discountRate != null && !TextUtils.isEmpty(mList[0].discountRate) && mList[0].discountRate!!.toInt() > Keys.ZERO_DISCOUNT_RATE) {
                //베이스가 로직==찌익그은가, 찌익 그은가는 할인율이 없거나, 세일가보다 비싸면 안보여야함 디폴트는 안보임
                if (DisplayUtils.isValidNumberStringExceptZero(mList[0].basePrice) && salePrice < basePrice) {
                    if (mTvBasePrice != null) {
                        mTvBasePrice.text = DisplayUtils.getFormattedNumber(mList[0].basePrice)
                        mTvBasePrice.append(mList[0].exposePriceText)
                        mTvBasePrice.visibility = View.VISIBLE
                        mTvBasePrice.paintFlags = mTvBasePrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    }
                    if (mTvDiscountRate != null) {
                        mTvDiscountRate.append(ProductInfoUtil.getDiscountCommon(mContext, mList[0].discountRate))
                        mTvDiscountRate.ellipsize = null
                        mTvDiscountRate.visibility = View.VISIBLE
                    }
                } else {
                    ViewUtils.hideViews(mTvBasePrice, mTvDiscountRateUnit, mTvDiscountRate)
                }
            }


            // 세일 price 처리 로직 : 있으면 그려 / else 면 안그려
            if (DisplayUtils.isValidString(mList[0].salePrice)) {
                val salePriceText = DisplayUtils.getFormattedNumber(mList[0].salePrice)
                if (mTvPriceUnit != null) {
                    mTvPriceUnit.text = mList[0].exposePriceText
                    mTvPriceUnit.visibility = View.VISIBLE
                }

                val priceStringBuilder = SpannableStringBuilder()
                priceStringBuilder.append(salePriceText)
                if (mTvPrice != null) {
                    mTvPrice.append(priceStringBuilder)
                    mTvPrice.visibility = View.VISIBLE
                }
            } else {
                ViewUtils.hideViews(mTvPrice, mTvPriceUnit)
            }

            executeTask(mContext, mList[0].endDate!!)
        }
    }

    private fun executeTask(context: Context, endTime: Long) {
        stopTask()
        mTimer = Timer()
        try {
            mTimer!!.schedule(object : TimerTask() {
                override fun run() {
                    setRemainTime(context, endTime)
                }
            }, 1000, 1000)
        } catch (e: IllegalStateException) {
            Ln.e(e.message)
        } catch (e: NullPointerException) {
            Ln.e(e.message)
        }
    }

    private fun stopTask() {
        if (mTimer != null) {
            mTimer!!.cancel()
            mTimer = null

        }
    }

    /**
     * 타임세일 아이템이 하나일 경우 높이를 0으로 만들어 뷰를 삭제한다.
     */
    private fun setZeroHeight() {
        val params = itemView.root_view.layoutParams
        params.height = 0
        itemView.root_view.layoutParams = params
    }

    private fun setRemainTime(context: Context, endTime: Long) {

        val startTime: Long = System.currentTimeMillis()
        val defTime = endTime - startTime
        try {
            if (defTime > 0) {
                (context as Activity).runOnUiThread {
                    mTvTime.text = StringUtils.stringForHHMMSS(defTime, false)
                }
            } else {
                (context as Activity).runOnUiThread {
                    setZeroHeight()
                }
                stopTask()
                return
            }
        } catch (e: NullPointerException) {
            Ln.e(e.message)
        }
    }

    /**
     * 이미지 롤링배너 어댑터 pager adapter
     */
    private class RollImageAdapter(private val context: Context, private val list: MutableList<SectionContentList>, private val parent: BanBeautySldTimeSaleVH) : PagerAdapter(), BanBeautySldTimeSaleItem.OnZeroTimeItemRemoveListener {

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = BanBeautySldTimeSaleItem(context)
            view.setOnZeroTimeItemRemoveListener(this)
            view.setItem(list[position], position)

            container.addView(view)


            return view
        }

        override fun getCount(): Int {
            return list.size
        }

        override fun getItemPosition(`object`: Any): Int {
            val index = list.indexOf(`object`)
            return if (index == -1) {
                POSITION_NONE
            } else {
                super.getItemPosition(`object`)
            }
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view === obj as View
        }

        override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
            collection.removeView(view as View)
        }

        /**
         * 타임세일 0분0초 되면 호출되는 인터페이스 함수. (뷰를 삭제시킴)
         */
        override fun setZeroTimeItemRemove(position: Int) {
            parent.setZeroTimeItemRemove(position)
        }
    }
}