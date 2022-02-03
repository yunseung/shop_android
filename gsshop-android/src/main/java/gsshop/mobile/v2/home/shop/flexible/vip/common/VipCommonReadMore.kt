package gsshop.mobile.v2.home.shop.flexible.vip.common

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.InflateException
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.main.SectionContentList
import roboguice.util.Ln

class VipCommonReadMore : LinearLayout {
    private var mContext: Context = context
    private lateinit var mRootView: View

    enum class ENUM_TYPE_MORE {
        TYPE_RENEWAL,  // 갱신형
        TYPE_GO_WEB, // 웹 호출형
        TYPE_EXTENSION // 확장형
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        initLayout()
    }

    private fun initLayout() {
        val inflater = mContext!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.view_holder_ban_read_more, this, true)

        mRootView = view.findViewById(R.id.root)
        if (mRootView == null) {
            mRootView = view
        }
    }

    fun setViewResources(resourceId: Int) {
        try {
            val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(resourceId, this, true)

            mRootView = view.findViewById(R.id.root)
            if (mRootView == null) {
                mRootView = view
            }
        }
        catch (e: InflateException) {
            Ln.e(e.message)
        }
    }

    fun setItems(item: SectionContentList?, type : ENUM_TYPE_MORE) {
        setView(item, type)
    }

    private fun setView(item: SectionContentList?, type : ENUM_TYPE_MORE) {
        if (item == null) {
            mRootView.visibility = GONE
            return
        }

        if (ENUM_TYPE_MORE.TYPE_EXTENSION != type) {
            if (TextUtils.isEmpty(item.moreBtnUrl) && TextUtils.isEmpty(item.linkUrl)) {
                mRootView.visibility = GONE
                return
            }
        }

        try {
            val text = mRootView.findViewById(R.id.text) as TextView
            val imgArrow = mRootView.findViewById(R.id.img_arrow) as ImageView
            val imgRefresh = mRootView.findViewById(R.id.img_refresh) as ImageView

            if (!TextUtils.isEmpty(item.moreText)) {
                text.text = item.moreText
                mRootView.contentDescription = item.moreText
            } else if (!TextUtils.isEmpty(item.name)) {
                text.text = item.name
                mRootView.contentDescription = item.name
            }

            if (ENUM_TYPE_MORE.TYPE_RENEWAL == type) {
                imgArrow.visibility = GONE
                imgRefresh.visibility = VISIBLE
            } else {
                imgArrow.visibility = VISIBLE
                imgRefresh.visibility = GONE
                imgArrow.setBackgroundResource(
                        if (ENUM_TYPE_MORE.TYPE_GO_WEB == type) R.drawable.icon_vip_arrow_right
                        else R.drawable.icon_vip_arrow_down
                )
            }
        }
        catch (e:NullPointerException) {
            Ln.e(e.message)
        }
        catch (e:ClassCastException) {
            Ln.e(e.message)
        }
    }

    fun setSeperator(isVisible: Boolean) {
        try {
            if (isVisible) {
                (mRootView.findViewById(R.id.view_seperator) as View).visibility = View.VISIBLE
            } else {
                (mRootView.findViewById(R.id.view_seperator) as View).visibility = View.GONE
            }
        }
        catch (e:NullPointerException) {
            Ln.e(e.message)
        }
    }
}