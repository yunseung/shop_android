package gsshop.mobile.v2.home.shop.flexible.vip.common

import android.content.Context
import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.style.UnderlineSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.main.SectionContentList
import gsshop.mobile.v2.util.DisplayUtils
import gsshop.mobile.v2.util.ImageUtil
import gsshop.mobile.v2.util.StringUtils.checkHexColor
import kotlinx.android.synthetic.main.home_row_type_fx_mart_department_store_title.view.*
import kotlinx.android.synthetic.main.view_holder_ban_title_vip_common.view.*
import kotlinx.android.synthetic.main.view_holder_ban_title_vip_common.view.root
import roboguice.util.Ln
import java.lang.Exception


class VipCommonTitle : ConstraintLayout {
    private var mContext: Context = context

    private lateinit var mRootView: View

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        initLayout()
    }

    private fun initLayout() {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.view_holder_ban_title_vip_common, this, true)

        mRootView = root
    }

    fun setItems(item: SectionContentList?) {
        setView(item)
    }

    fun getTitleTxt():String {
        return mRootView.contentDescription.toString()
    }

    private fun setView(item: SectionContentList?) {
        if (item == null) {
            return
        }

        if (!TextUtils.isEmpty(item.titleImgUrl))
            ImageUtil.loadImage(mContext, item.titleImgUrl, mRootView.img_left, R.drawable.noimage_78_78)

        if (!TextUtils.isEmpty(item.subName))
            txt_sub.text = item.subName
//        mRootView.layout_txt_main

        mRootView.txt_main.visibility = VISIBLE
        if(!TextUtils.isEmpty(item.name)) {
//            mRootView.layout_txt_main.visibility = GONE
            mRootView.txt_main.text = item.name
        }
        else if(item.customTitle != null && item.customTitle!!.size > 0) {
//            mRootView.txt_main.visibility = GONE
//            mRootView.layout_txt_main.visibility = VISIBLE
//
//            mRootView.layout_txt_main.removeAllViews()

            val titleStringBuilder = SpannableStringBuilder()
            var startPos = 0;

            for (titleItem in item.customTitle!!) {
//                titleItem.text += "test test test test test test"
//                titleList.add(titleItem)

                var strColor = "#dbf109"

                try {
                    if (!TextUtils.isEmpty(titleItem.text)) {
                        titleStringBuilder.append(titleItem.text)
                        if ("Y" == titleItem.point) {
                            if (!TextUtils.isEmpty(titleItem.pointColor)) {
                                if (titleItem.pointColor!!.contains("#")) {
                                    strColor = titleItem.pointColor!!
                                }
                                else {
                                    strColor = "#" + titleItem.pointColor
                                }
                            }

                            var backColor = Color.parseColor(strColor)
                            var span = CustomBackgroundColorSpan(backColor, DisplayUtils.convertDpToPx(context, 8f).toFloat())
                            titleStringBuilder.setSpan(span, startPos, startPos + titleItem.text!!.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        startPos += titleItem.text!!.length
                    }
                }
                catch (e:Exception) {
                    Ln.e(e.message)
                }
            }
            mRootView.txt_main.text = titleStringBuilder
        }
        var strDescription = mRootView.txt_sub.text.toString()
        strDescription += " " + mRootView.txt_main.text.toString()
        mRootView.contentDescription = strDescription

    }
}