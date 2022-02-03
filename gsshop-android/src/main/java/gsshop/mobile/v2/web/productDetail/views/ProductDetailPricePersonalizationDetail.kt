package gsshop.mobile.v2.web.productDetail.views

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.google.android.flexbox.FlexboxLayout
import gsshop.mobile.v2.R
import gsshop.mobile.v2.util.DisplayUtils.parseColor
import gsshop.mobile.v2.web.productDetail.ProductDetailWebActivityV2
import gsshop.mobile.v2.web.productDetail.dto.NativeProductV2
import kotlinx.android.synthetic.main.product_detail_price_personalization_detail.view.*
import roboguice.util.Ln

class ProductDetailPricePersonalizationDetail : LinearLayout {
    private val mContext: Context = context
    private lateinit var mRootView: RelativeLayout
    private lateinit var mLlLeft: FlexboxLayout
    private lateinit var mLlRight: LinearLayout
    private var mParents: ProductDetailPricePersonalizationLayout? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, parents: ProductDetailPricePersonalizationLayout?) : super(context) {
        this.mParents = parents
    }

    enum class RowType {
        TITLE, SUB, MORE
    }

    enum class ItemType(val type: String) {
        TEXT("text"),
        BUTTON("button"),
        DOWN("down"),
        INFO("info"),
        RUBY("ruby"),
        DASH("dash"),
        STAR("star"),
        GOLD("gold"),
        VIP("vip"),
        VVIP("vvip"),
        LOADING("loading")
    }

    init {
        initLayout()
    }

    private fun initLayout() {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.product_detail_price_personalization_detail, this, true)
        mRootView = root_view
        mLlLeft = ll_left
        mLlRight = ll_right
    }

    fun setRowData(contents: NativeProductV2.PrsnlTxtInfoList, type: RowType) {
        if (contents.leftItem != null && contents.leftItem.size > 0) {
            setContents(mLlLeft, contents.leftItem, type)
            if (contents.rightItem != null) {
                if (contents.rightItem.size < 1) {
                    mLlRight.visibility = View.GONE
                }
            } else {
                mLlRight.visibility = View.GONE
            }
        }

        if (contents.rightItem != null && contents.rightItem.size > 0) {
            setContents(mLlRight, contents.rightItem, type)
            if (contents.leftItem != null) {
                if (contents.leftItem.size < 1) {
                    mLlLeft.visibility = View.GONE
                }
            } else {
                mLlLeft.visibility = View.GONE
            }
        }
    }

    private fun setContents(containerView: ViewGroup, contentsArray: MutableList<NativeProductV2.TxtInfo>, type: RowType) {
        mRootView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        mRootView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        containerView.removeAllViews()
        when (type) {
            RowType.TITLE -> {

            }
            RowType.SUB -> {
                mRootView.margin(bottom = 8F)
            }
            RowType.MORE -> {

            }
        }

        for (data in contentsArray) {
            when (data.type) {
                ItemType.TEXT.type -> {
                    val strArray: List<String> = data.textValue.split("\\s".toRegex())
                    for (element in strArray) {
                        val tv: TextView =
                                if ("Y".equals(data.point, ignoreCase = true)) {
                                    View.inflate(mContext, R.layout.text_view_underline_f9d106, null) as TextView
                                } else {
                                    TextView(mContext)
                                }
                        val txtInfo: NativeProductV2.TxtInfo = data
                        // if 공백으로 split 하다보면 텍스트들이 붙어서 노출되는 문제가 발생해서 element 뒤에 공백을 붙힌다.
                        // else strArray 의 size 가 1 일 경우 맨 뒤에 공백이 붙으면 안된다.
                        if (strArray.size > 1) {
                            txtInfo.textValue = "$element "
                        } else {
                            txtInfo.textValue = element
                        }
                        setTextInfoList(tv, txtInfo)
                        containerView.addView(tv)
                    }
                }
                ItemType.DOWN.type -> {
                    val iv = ImageView(mContext)
                    iv.setBackgroundResource(R.drawable.down_img)
                    iv.layoutParams = LayoutParams(dpToPx(7F), dpToPx(10F))
                    containerView.addView(iv)
                    val tvSpace = TextView(mContext)
                    tvSpace.text = " "
                    containerView.addView(tvSpace)
                }
                ItemType.INFO.type -> {
                    val iv = ImageView(mContext).apply {
                        layoutParams = LayoutParams(dpToPx(20F), dpToPx(20F))
                        background = ContextCompat.getDrawable(mContext, R.drawable.icon_info)
                    }
                    if (!TextUtils.isEmpty(data.linkUrl)) {
                        iv.setOnClickListener {
                            (mContext as ProductDetailWebActivityV2).goToLink(data.linkUrl)
                        }
                    }
                    containerView.addView(iv)
                }
                ItemType.RUBY.type -> {
                    val iv = ImageView(mContext).apply {
                        layoutParams = LayoutParams(dpToPx(12F), dpToPx(10F))
                        background = ContextCompat.getDrawable(mContext, R.drawable.icon_ruby)
                    }
                    if (!TextUtils.isEmpty(data.linkUrl)) {
                        iv.setOnClickListener {
                            (mContext as ProductDetailWebActivityV2).goToLink(data.linkUrl)
                        }
                    }

                    containerView.addView(iv)
                    val tv = TextView(mContext)
                    tv.text = " "
                    containerView.addView(tv)
                }
                ItemType.DASH.type -> {
                    val tv = TextView(mContext)
                    setTextInfoList(tv, data)
                    containerView.addView(tv)

                }
                ItemType.STAR.type -> {
                    val tv = TextView(mContext)
                    setTextInfoList(tv, data)
                    containerView.addView(tv)
                }
                ItemType.BUTTON.type -> {
                    val btn = TextView(mContext)
                    setTextInfoList(btn, data)
                    btn.background = ContextCompat.getDrawable(mContext, R.drawable.btn_white_gray_border)
                    btn.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, dpToPx(30F))
                    btn.setPadding(dpToPx(8F), dpToPx(6F), dpToPx(8F), dpToPx(4F))
                    btn.setMargins(right = dpToPx(21F))
                    if (!TextUtils.isEmpty(data.linkUrl)) {
                        btn.setOnClickListener {
                            (mContext as ProductDetailWebActivityV2).goToLink(data.linkUrl)
                        }
                    }
                    containerView.addView(btn)
                }

                ItemType.GOLD.type -> {
                    val iv = ImageView(mContext).apply {
                        layoutParams = LayoutParams(dpToPx(20F), dpToPx(20F))
                        background = ContextCompat.getDrawable(mContext, R.drawable.icon_grade_gold)
                    }

                    containerView.addView(iv)
                }
                ItemType.VIP.type -> {
                    val iv = ImageView(mContext).apply {
                        layoutParams = LayoutParams(dpToPx(20F), dpToPx(20F))
                        background = ContextCompat.getDrawable(mContext, R.drawable.icon_grade_vip)
                    }

                    containerView.addView(iv)
                }
                ItemType.VVIP.type -> {
                    val iv = ImageView(mContext).apply {
                        layoutParams = LayoutParams(dpToPx(20F), dpToPx(20F))
                        background = ContextCompat.getDrawable(mContext, R.drawable.icon_grade_vvip)
                    }

                    containerView.addView(iv)
                }
                ItemType.LOADING.type -> {
                    val tv = TextView(mContext)
                    tv.text = "계산중"
                    tv.setTextColor(Color.parseColor("#111111"))
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15f)
                    containerView.addView(tv)
                    val lottieAnimationView = LottieAnimationView(mContext)
                    lottieAnimationView.layoutParams = LayoutParams(dpToPx(30F), dpToPx(30F))
                    lottieAnimationView.repeatCount = LottieDrawable.INFINITE
                    try {
                        lottieAnimationView.setAnimation(R.raw.web_prd_loading)
                        lottieAnimationView.playAnimation()
                    } catch (e: IllegalArgumentException) {
                        Ln.e(e.message)
                    } catch (e: IllegalStateException) {
                        Ln.e(e.message)
                    }
                    containerView.addView(lottieAnimationView)
                }
            }
        }
    }


    private fun setTextInfoList(tv: TextView, txtInfo: NativeProductV2.TxtInfo?) {
        if (txtInfo == null) {
            tv.visibility = View.GONE
            return
        }
        val ssb = SpannableStringBuilder()

        // txtList 별로 텍스트 속성을 정해주기 위해 순차적으로 0부터 어디까지 적용됐는지 저장하는 int
        var lastCharIndex = 0

        if (!TextUtils.isEmpty(txtInfo.textValue)) {
            // android 에서 자동으로 라인이 넘어가는 현상을 무시하고 텍스트뷰의 끝까지 텍스트를 채우기 위함.
            val sb = StringBuilder()
            for (element in txtInfo.textValue) {
                sb.append(element)
                sb.append("\u200B")
            }
            if (sb.isNotEmpty()) {
                sb.deleteCharAt(sb.length - 1)
            }
            txtInfo.textValue = sb.toString()
            ///////////////////////////////////////////////////////////////////////////////////////

            // font size span
            val sizeSpan = AbsoluteSizeSpan(txtInfo.size.toInt(), true)
            // font style span
            val styleSpan = StyleSpan(if ("Y".equals(txtInfo.boldYN, ignoreCase = true)) Typeface.BOLD else Typeface.NORMAL)
            // font color span
            val foregroundColorSpan = ForegroundColorSpan(parseColor(txtInfo.textColor))
            ssb.append(txtInfo.textValue)
            ssb.setSpan(foregroundColorSpan, lastCharIndex, ssb.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            ssb.setSpan(sizeSpan, lastCharIndex, ssb.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            ssb.setSpan(styleSpan, lastCharIndex, ssb.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            lastCharIndex += txtInfo.textValue.length
            if (ssb.toString().isNotEmpty()) {
                tv.visibility = View.VISIBLE
                tv.text = ssb
            } else {
                tv.visibility = View.GONE
            }
        }
    }

    private fun View.margin(left: Float? = null, top: Float? = null, right: Float? = null, bottom: Float? = null) {
        layoutParams<MarginLayoutParams> {
            left?.run { leftMargin = dpToPx(this) }
            top?.run { topMargin = dpToPx(this) }
            right?.run { rightMargin = dpToPx(this) }
            bottom?.run { bottomMargin = dpToPx(this) }
        }
    }

    fun View.setMargins(
            left: Int? = null,
            top: Int? = null,
            right: Int? = null,
            bottom: Int? = null
    ) {
        val lp = layoutParams as? MarginLayoutParams
                ?: return

        lp.setMargins(
                left ?: lp.leftMargin,
                top ?: lp.topMargin,
                right ?: lp.rightMargin,
                bottom ?: lp.rightMargin
        )

        layoutParams = lp
    }

    private inline fun <reified T : ViewGroup.LayoutParams> View.layoutParams(block: T.() -> Unit) {
        if (layoutParams is T) block(layoutParams as T)
    }

    private fun View.dpToPx(dp: Float): Int = context.dpToPx(dp)
    private fun Context.dpToPx(dp: Float): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()

}