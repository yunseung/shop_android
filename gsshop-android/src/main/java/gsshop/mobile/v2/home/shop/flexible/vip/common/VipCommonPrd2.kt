package gsshop.mobile.v2.home.shop.flexible.vip.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.main.SectionContentList
import kotlinx.android.synthetic.main.view_holder_banner_vip_prd_2.view.*
import roboguice.util.Ln

class VipCommonPrd2 : LinearLayout {
    private var mContext: Context = context

//    private lateinit var mRootView: View
    private var vipPrd2Ids = emptyArray<VipCommonPrd2Item>()

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        initLayout()
    }

    private fun initLayout() {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.view_holder_banner_vip_prd_2, this, true)

//        mRootView = root
        vipPrd2Ids = arrayOf(root.view_1, root.view_2, root.view_3, root.view_4)
    }

    fun setItems(item: SectionContentList?) {
        setView(item)
    }

    private fun setView(item: SectionContentList?) {
        if (item == null) {
            return
        }

        //add view 할때마다 버벅일 수 있어 최대 4개 이기 때문에 4개만 설정
        try {
            for (i in 0 until item.subProductList!!.size) {
                if (i > 3) break

                vipPrd2Ids[i].setItems(item.subProductList!![i])
            }
        }
        catch (e: NullPointerException) {
            Ln.e(e.message)
        }
        catch (e: IndexOutOfBoundsException) {
            Ln.e(e.message)
        }
    }
}