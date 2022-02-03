package gsshop.mobile.v2.home.shop.ltype

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.main.SectionContentList
import kotlinx.android.synthetic.main.layout_row_prd_mlt_gba.view.*

class PrdMltGbaRowLayout : LinearLayout {
    private var mContext: Context? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        mContext = context
        initLayout()
    }

    private fun initLayout() {
        val inflater = mContext!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.layout_row_prd_mlt_gba, this, true)
    }

    fun setRowData(productList: MutableList<SectionContentList>) {
        when (productList.size) {
            3 -> {
                item_1.setData(productList[0])
                item_2.setData(productList[1])
                item_3.setData(productList[2])
            }
            2 -> {
                item_1.setData(productList[0])
                item_2.setData(productList[1])
                item_3.visibility = View.INVISIBLE
            }
            1 -> {
                item_1.setData(productList[0])
                item_2.visibility = View.INVISIBLE
                item_3.visibility = View.INVISIBLE
            }
        }
    }
}