package gsshop.mobile.v2.web.productDetail.views

import android.annotation.SuppressLint
import android.content.Context
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import gsshop.mobile.v2.R
import gsshop.mobile.v2.support.gtm.AMPAction
import gsshop.mobile.v2.support.gtm.AMPEnum
import gsshop.mobile.v2.user.User
import gsshop.mobile.v2.util.ImageUtil
import gsshop.mobile.v2.web.productDetail.ProductDetailWebActivityV2
import gsshop.mobile.v2.web.productDetail.dto.NativeProductV2
import kotlinx.android.synthetic.main.product_detail_price_personalization_layout.view.*
import org.json.JSONObject

class ProductDetailPricePersonalizationLayout : RelativeLayout {
    private var mContext: Context = context

    private lateinit var mIvGradeIcon: ImageView
    private lateinit var mTvGradeText: TextView
    private lateinit var mTvUserName: TextView
    private lateinit var mTvGradeInfo: TextView
    private lateinit var mLlViewMoreArea: LinearLayout
    private lateinit var mLlContentsLayout: LinearLayout
    private lateinit var mLlSaleMaxList: LinearLayout
    private lateinit var mLlSaleMaxListSub: LinearLayout
    private lateinit var mLlJuklibMaxList: LinearLayout
    private lateinit var mLlJuklibMaxListSub: LinearLayout
    private lateinit var mIvSaleMaxListLine: ImageView
    private lateinit var mLlMoreInfoArea: LinearLayout
    private lateinit var mIvMoreInfoLine: ImageView
    private lateinit var mTvMoreText: TextView
    private lateinit var mIvMoreIcon: ImageView
    private lateinit var mBtnShowPrice: TextView
    private lateinit var mLlAddSaleInfo: LinearLayout
    private lateinit var mLlAddPmoInfo: LinearLayout
    private lateinit var mBtnToolTip: Button

    private var mIsOpen = false
    private var mIsPrsnl = false
    private var mIsEmptySecondData = false
    private var mIsZeroPrice = false
    private var mIsMoreInfoAreaGone = false
    private var mIsAddSaleAreaGone = false;

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        initLayout()
    }

    private fun initLayout() {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.product_detail_price_personalization_layout, this, true)
        mIvGradeIcon = iv_grade_icon
        mTvGradeText = tv_grade_text
        mTvUserName = tv_user_name
        mTvGradeInfo = tv_grade_info
        mLlViewMoreArea = ll_view_more_area
        mLlContentsLayout = ll_contents_layout
        mLlSaleMaxList = ll_sale_max_list
        mLlSaleMaxListSub = ll_sale_max_list_sub
        mLlJuklibMaxList = ll_juklib_max_list
        mLlJuklibMaxListSub = ll_juklib_max_list_sub
        mIvSaleMaxListLine = iv_max_discount_line
        mLlMoreInfoArea = ll_more_info_area
        mIvMoreInfoLine = iv_more_info_line
        mTvMoreText = tv_more
        mIvMoreIcon = iv_more_icon
        mBtnShowPrice = btn_show_price
        mLlAddSaleInfo = ll_add_sale_info
        mLlAddPmoInfo = ll_add_pmo_info
        mBtnToolTip = btn_tooltip

        mLlViewMoreArea.setOnClickListener {
            if (mIsEmptySecondData) return@setOnClickListener
            if (mIsOpen) {
                if (mIsMoreInfoAreaGone) mLlMoreInfoArea.visibility = GONE
                mLlSaleMaxListSub.visibility = GONE
                mLlJuklibMaxListSub.visibility = GONE
                mTvMoreText.text = mContext.getString(R.string.sale_juklib_more)
                if (mIsPrsnl) mIvSaleMaxListLine.margin(top = 0F)
                mIvMoreIcon.background = ContextCompat.getDrawable(mContext, R.drawable.btn_more)

                if (!mIsAddSaleAreaGone) mLlAddSaleInfo.visibility = VISIBLE

                if (mIsZeroPrice) mBtnShowPrice.visibility = VISIBLE
            } else {
                if (mIsMoreInfoAreaGone) mLlMoreInfoArea.visibility = VISIBLE
                mLlSaleMaxListSub.visibility = VISIBLE
                mLlJuklibMaxListSub.visibility = VISIBLE
                mTvMoreText.text = mContext.getString(R.string.more_close)
                // 펼쳐보기 시에 saleMaxList 상세 최하단과의 마진이 16이어야 해서 라인에 8을 더함.
                if (mIsPrsnl) mIvSaleMaxListLine.margin(top = 8F)
                mIvMoreIcon.background = ContextCompat.getDrawable(mContext, R.drawable.btn_more_close)

                if (!mIsAddSaleAreaGone) mLlAddSaleInfo.visibility = GONE

                if (mIsZeroPrice) mBtnShowPrice.visibility = GONE

                //펼칠때만 효율 보낸다
                val eventProperties = JSONObject()
                eventProperties.put(AMPEnum.AMP_VIEW_TYPE, AMPEnum.AMP_TYPE_B)
                eventProperties.put(AMPEnum.AMP_ACTION_NAME, AMPEnum.AMP_VIEW_MORE_BENEFIT)
                AMPAction.sendAmpEventProperties(AMPEnum.AMP_CLICK_PRD, eventProperties)
            }
            (mContext as ProductDetailWebActivityV2).resize()
            mIsOpen = !mIsOpen
        }
    }

    /**
     * 개인화 서버 api setting  (2차 api)
     */
    fun setPrsnlApiTemplate(result: NativeProductV2.PersonalizationResult) {
        if (result.tooltipYN != null && "Y".equals(result.tooltipYN, ignoreCase = true)) {
            mBtnToolTip.visibility = VISIBLE
        }

        if (result.saleMaxList.size == 0 && result.juklibMaxList.size == 0) {
            mIsEmptySecondData = true
            return
        }
        mIsEmptySecondData = false

        if (result.saleMaxList != null && result.saleMaxList.size > 0) {
            mLlSaleMaxList.visibility = VISIBLE
            mIvSaleMaxListLine.visibility = VISIBLE
            mLlSaleMaxList.removeAllViews()
            for (i in result.saleMaxList.indices) {
                val childView = ProductDetailPricePersonalizationDetail(mContext, this)
                // 0 번째는 타이틀
                if (i == 0) {
                    for (txtInfo in result.saleMaxList[0].rightItem) {
                        if (txtInfo.textValue == "0") {
                            // 0원일때 가격보기 버튼 노출.
                            mBtnShowPrice.visibility = VISIBLE
                            mBtnShowPrice.setOnClickListener {
                                mLlViewMoreArea.performClick()
                            }
                            mIsZeroPrice = true
                            break
                        }
                    }
                    childView.setRowData(result.saleMaxList[i], ProductDetailPricePersonalizationDetail.RowType.TITLE)
                    mLlSaleMaxList.addView(childView)
                } else {
                    childView.setRowData(result.saleMaxList[i], ProductDetailPricePersonalizationDetail.RowType.SUB)
                    mLlSaleMaxListSub.addView(childView)
                }
            }

            // addSaleInfo 는 saleMaxList 와 종속관계이다.
            if (result.addSaleInfo != null && result.addSaleInfo.size > 0) {
                mLlAddSaleInfo.visibility = VISIBLE
                mLlAddSaleInfo.removeAllViews()
                for (element in result.addSaleInfo) {
                    val childView = ProductDetailPricePersonalizationDetail(mContext, this)
                    childView.setRowData(element, ProductDetailPricePersonalizationDetail.RowType.SUB)
                    mLlAddSaleInfo.addView(childView)
                }
            } else {
                mLlAddSaleInfo.visibility = GONE
            }
        } else {
            mLlSaleMaxList.visibility = GONE
            mIvSaleMaxListLine.visibility = GONE
            // addSaleInfo 는 saleMaxList 와 종속관계이기 때문에 개인화 data 에 saleMaxList 가 없으면 숨겨야한다.
            mLlAddSaleInfo.visibility = GONE
            mIsAddSaleAreaGone = true
        }

        if (result.juklibMaxList != null && result.juklibMaxList.size > 0) {
            mLlJuklibMaxList.visibility = VISIBLE
            mLlJuklibMaxList.removeAllViews()
            for (i in result.juklibMaxList.indices) {
                val childView = ProductDetailPricePersonalizationDetail(mContext, this)
                if (i == 0) {
                    childView.setRowData(result.juklibMaxList[i], ProductDetailPricePersonalizationDetail.RowType.TITLE)
                    mLlJuklibMaxList.addView(childView)
                } else {
                    childView.setRowData(result.juklibMaxList[i], ProductDetailPricePersonalizationDetail.RowType.SUB)
                    mLlJuklibMaxListSub.addView(childView)
                }
            }
        } else {
            mLlJuklibMaxList.visibility = GONE
        }

        if (result.moreInfoList != null && result.moreInfoList.size > 0) {
            for (i in result.moreInfoList.indices) {
                val childView = ProductDetailPricePersonalizationDetail(mContext, this)
                childView.setRowData(result.moreInfoList[i], ProductDetailPricePersonalizationDetail.RowType.MORE)
                mLlMoreInfoArea.addView(childView)
            }

            // 최대 2개 항목만 노출되어야 해서 적립, 세일 리스트가 있으면 moreInfo 는 숨긴다.
            if ((result.juklibMaxList != null && result.juklibMaxList.size > 0) &&
                    (result.saleMaxList != null && result.saleMaxList.size > 0)) {
                mLlMoreInfoArea.visibility = GONE
                mIvMoreInfoLine.visibility = GONE
                mIsMoreInfoAreaGone = true
            } else {
                mLlMoreInfoArea.visibility = VISIBLE
                mIvMoreInfoLine.visibility = VISIBLE
            }
        } else {
            mLlMoreInfoArea.visibility = GONE
            mIvMoreInfoLine.visibility = GONE
        }

        (mContext as ProductDetailWebActivityV2).resize()
    }

    /**
     * 단품 api setting (1차 api)
     */
    @SuppressLint("SetTextI18n")
    fun setProductDetailPricePersonInfoLayout(component: NativeProductV2.Component) {
        if (TextUtils.isEmpty(component.prsnlApiUrl)) {
            mIsPrsnl = false
            // 개인화 데이터 없을 UI
            mLlSaleMaxList.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            if (component.prsnlName != null) {
                if (!TextUtils.isEmpty(component.prsnlName.iConUrl)) {
                    ImageUtil.loadImage(mContext, component.prsnlName.iConUrl, mIvGradeIcon, R.drawable.noimage_78_78)
                    mIvGradeIcon.visibility = VISIBLE
                } else {
                    mIvGradeIcon.visibility = GONE
                }

                if (component.prsnlName.gradeText != null) {
                    mTvGradeText.visibility = VISIBLE
                    (mContext as ProductDetailWebActivityV2).setTextInfoList(mTvGradeText, component.prsnlName.gradeText)
                } else {
                    mTvGradeText.visibility = GONE
                }

                (mContext as ProductDetailWebActivityV2).setTextInfoList(mTvGradeInfo, component.prsnlName.gradeInfoText)

                if ("Y".equals(component.prsnlName.useName, ignoreCase = true)) {
                    if (User.getCachedUser() != null) {
                        if (User.getCachedUser().userName.length > 5) {
                            val ssb = SpannableStringBuilder()
                            for (i in 0..4) {
                                ssb.append(User.getCachedUser().userName[i])
                            }
                            mTvUserName.text = ssb.append("..")
                        } else {
                            mTvUserName.text = User.getCachedUser().userName
                        }
                        // 앞에 등급이 오면 사용자 이름이랑 한 칸 띄우기 위함.
                        if (component.prsnlName.gradeText != null && component.prsnlName.gradeText.size > 0) {
                            mTvUserName.text = " ${mTvUserName.text}"
                        }
                    } else {
                        mTvUserName.visibility = GONE
                    }
                } else {
                    mTvUserName.visibility = GONE
                }

            }

            if (component.addPmoInfo != null && component.addPmoInfo.size > 0) {
                mLlAddPmoInfo.visibility = VISIBLE
                mIvSaleMaxListLine.visibility = VISIBLE
                for (i in component.addPmoInfo.indices) {
                    val childView = ProductDetailPricePersonalizationDetail(mContext, this)
                    childView.setRowData(component.addPmoInfo[i], ProductDetailPricePersonalizationDetail.RowType.SUB)
                    mLlAddPmoInfo.addView(childView)
                }
            }

            if (component.juklibMaxList != null && component.juklibMaxList.size > 0) {
                mLlJuklibMaxList.visibility = VISIBLE
                for (i in component.juklibMaxList.indices) {
                    val childView = ProductDetailPricePersonalizationDetail(mContext, this)
                    if (i == 0) {
                        childView.setRowData(component.juklibMaxList[i], ProductDetailPricePersonalizationDetail.RowType.TITLE)
                        mLlJuklibMaxList.addView(childView)
                    } else {
                        childView.setRowData(component.juklibMaxList[i], ProductDetailPricePersonalizationDetail.RowType.SUB)
                        mLlJuklibMaxListSub.addView(childView)
                    }
                }
            } else {
                mLlJuklibMaxList.visibility = GONE
                mLlJuklibMaxListSub.visibility = GONE
            }

            // 개인화 데이터가 없을때에는 moreInfo 데이터 있으면 항상 그려준다.
            if (component.moreInfoList != null && component.moreInfoList.size > 0) {
                mLlMoreInfoArea.visibility = VISIBLE
                mIvMoreInfoLine.visibility = VISIBLE
                for (i in component.moreInfoList.indices) {
                    val childView = ProductDetailPricePersonalizationDetail(mContext, this)
                    childView.setRowData(component.moreInfoList[i], ProductDetailPricePersonalizationDetail.RowType.MORE)
                    mLlMoreInfoArea.addView(childView)
                }
            } else {
                mLlMoreInfoArea.visibility = GONE
                mIvMoreInfoLine.visibility = GONE
            }
        } else { // 개인화 데이터 있을때
            mIsPrsnl = true
            if (component.prsnlName != null) {
                if (!TextUtils.isEmpty(component.prsnlName.iConUrl)) {
                    ImageUtil.loadImage(mContext, component.prsnlName.iConUrl, mIvGradeIcon, R.drawable.noimage_78_78)
                    mIvGradeIcon.visibility = VISIBLE
                } else {
                    mIvGradeIcon.visibility = GONE
                }

                if (component.prsnlName.gradeText != null) {
                    mTvGradeText.visibility = VISIBLE
                    (mContext as ProductDetailWebActivityV2).setTextInfoList(mTvGradeText, component.prsnlName.gradeText)
                } else {
                    mTvGradeText.visibility = GONE
                }


                if ("Y".equals(component.prsnlName.useName, ignoreCase = true)) {
                    if (User.getCachedUser() != null) {

                        if (User.getCachedUser().userName.length > 5) {
                            val ssb = SpannableStringBuilder()
                            for (i in 0..4) {
                                ssb.append(User.getCachedUser().userName[i])
                            }
                            mTvUserName.text = "${ssb.append("..")}"
                        } else {
                            mTvUserName.text = "${User.getCachedUser().userName}"
                        }

                        // 앞에 등급이 오면 사용자 이름이랑 한 칸 띄우기 위함.
                        if (component.prsnlName.gradeText != null && component.prsnlName.gradeText.size > 0) {
                            mTvUserName.text = " ${mTvUserName.text}"
                        }
                    } else {
                        mTvUserName.visibility = GONE
                    }
                } else {
                    mTvUserName.visibility = GONE
                }

                (mContext as ProductDetailWebActivityV2).setTextInfoList(mTvGradeInfo, component.prsnlName.gradeInfoText)
            }

            if (component.saleMaxList != null && component.saleMaxList.size > 0) {
                mLlSaleMaxList.visibility = VISIBLE
                mIvSaleMaxListLine.visibility = VISIBLE
                for (i in component.saleMaxList.indices) {
                    val childView = ProductDetailPricePersonalizationDetail(mContext, this)
                    if (i == 0) {
                        childView.setRowData(component.saleMaxList[i], ProductDetailPricePersonalizationDetail.RowType.TITLE)
                    } else {
                        childView.setRowData(component.saleMaxList[i], ProductDetailPricePersonalizationDetail.RowType.SUB)
                    }
                    mLlSaleMaxList.addView(childView)
                }

                // addSaleInfo 는 saleMaxList 와 종속관계이다.
                if (component.addSaleInfo != null && component.addSaleInfo.size > 0) {
                    mLlAddSaleInfo.visibility = VISIBLE
                    for (element in component.addSaleInfo) {
                        val childView = ProductDetailPricePersonalizationDetail(mContext, this)
                        childView.setRowData(element, ProductDetailPricePersonalizationDetail.RowType.SUB)
                        mLlAddSaleInfo.addView(childView)
                    }
                }
            } else {
                mLlSaleMaxList.visibility = GONE
                mIvSaleMaxListLine.visibility = GONE
                mLlSaleMaxListSub.visibility = GONE

                // addSaleInfo 는 saleMaxList 와 종속관계이다.
                mLlAddSaleInfo.visibility = GONE
                mIsAddSaleAreaGone = true
            }

            if (component.juklibMaxList != null && component.juklibMaxList.size > 0) {
                mLlJuklibMaxList.visibility = VISIBLE
                for (i in component.juklibMaxList.indices) {
                    val childView = ProductDetailPricePersonalizationDetail(mContext, this)
                    if (i == 0) {
                        childView.setRowData(component.juklibMaxList[i], ProductDetailPricePersonalizationDetail.RowType.TITLE)
                    } else {
                        childView.setRowData(component.juklibMaxList[i], ProductDetailPricePersonalizationDetail.RowType.SUB)
                    }
                    mLlJuklibMaxList.addView(childView)
                }
            } else {
                mLlJuklibMaxList.visibility = GONE
                mLlJuklibMaxListSub.visibility = GONE
            }

            if (component.moreInfoList != null && component.moreInfoList.size > 0) {
                for (i in component.moreInfoList.indices) {
                    val childView = ProductDetailPricePersonalizationDetail(mContext, this)
                    childView.setRowData(component.moreInfoList[i], ProductDetailPricePersonalizationDetail.RowType.MORE)
                    mLlMoreInfoArea.addView(childView)
                }

                // 최대 2개 항목만 노출되어야 해서 적립, 세일 리스트가 있으면 moreInfo 는 숨긴다.
                if ((component.juklibMaxList != null && component.juklibMaxList.size > 0) &&
                        (component.saleMaxList != null && component.saleMaxList.size > 0)) {
                    mLlMoreInfoArea.visibility = GONE
                    mIvMoreInfoLine.visibility = GONE
                    mIsMoreInfoAreaGone = true
                } else {
                    mLlMoreInfoArea.visibility = VISIBLE
                    mIvMoreInfoLine.visibility = VISIBLE
                }
            } else {
                mLlMoreInfoArea.visibility = GONE
                mIvMoreInfoLine.visibility = GONE
            }

        }

        (mContext as ProductDetailWebActivityV2).resize()
    }

    private fun View.margin(left: Float? = null, top: Float? = null, right: Float? = null, bottom: Float? = null) {
        layoutParams<MarginLayoutParams> {
            left?.run { leftMargin = dpToPx(this) }
            top?.run { topMargin = dpToPx(this) }
            right?.run { rightMargin = dpToPx(this) }
            bottom?.run { bottomMargin = dpToPx(this) }
        }
    }

    private inline fun <reified T : ViewGroup.LayoutParams> View.layoutParams(block: T.() -> Unit) {
        if (layoutParams is T) block(layoutParams as T)
    }

    private fun View.dpToPx(dp: Float): Int = context.dpToPx(dp)
    private fun Context.dpToPx(dp: Float): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()

}