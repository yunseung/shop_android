package gsshop.mobile.v2.home.shop.flexible.vip.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import gsshop.mobile.v2.R
import gsshop.mobile.v2.util.DisplayUtils
import kotlinx.android.synthetic.main.view_holder_banner_vip_new.view.*
import roboguice.util.Ln

class VipCommonNew : FrameLayout {
    private var mContext: Context = context

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private lateinit var mRootView:View

    init {
        initLayout()
    }

    private fun initLayout() {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.view_holder_banner_vip_new, this, true)

        mRootView = view_back
    }

    fun setNew(isNew: Boolean) {
        try {
            var params: FrameLayout.LayoutParams = mRootView.view_back.layoutParams as FrameLayout.LayoutParams
            if (isNew) {
                params.leftMargin = DisplayUtils.convertDpToPx(context!!, 7f)
                params.rightMargin = DisplayUtils.convertDpToPx(context!!, 7f)
                mRootView.view_back.layoutParams = params

                mRootView.view_back.background = ContextCompat.getDrawable(context, R.drawable.bg_gradient_vip)

                mRootView.txt_new.visibility = View.VISIBLE
            } else {
                params.leftMargin = DisplayUtils.convertDpToPx(context!!, 8f)
                params.rightMargin = DisplayUtils.convertDpToPx(context!!, 8f)
                mRootView.view_back.layoutParams = params

                mRootView.view_back.background = ContextCompat.getDrawable(context,R.drawable.bg_white_round8)

                mRootView.txt_new.visibility = View.GONE
            }
        }
        catch (e:ClassCastException) {
            Ln.e(e.message)
        }
        catch (e:NullPointerException) {
            Ln.e(e.message)
        }
    }
}