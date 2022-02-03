package gsshop.mobile.v2.home.shop.ltype

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.blankj.utilcode.util.EmptyUtils
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.main.ModuleList
import gsshop.mobile.v2.home.main.SectionContentList
import gsshop.mobile.v2.home.shop.BaseViewHolderV2

class PrdMltGbaVH(itemView: View?) : BaseViewHolderV2(itemView) {
    private var mTempList: MutableList<SectionContentList> = mutableListOf()

    private lateinit var mLlGridItemLayout: LinearLayout
    private lateinit var mLlMoreClickArea: LinearLayout
    private lateinit var mTvMoreText: TextView

    /**
     * 하단 디바이더가 다음 뷰타입과 같으면 1dp 다르면 10dp
     */
    private lateinit var mViewBottomDivider1dp: View
    private lateinit var mViewBottomDivider10dp: View

    init {
        if (itemView != null) {
            mLlGridItemLayout = itemView.findViewById(R.id.ll_grid_item_layout)
            mLlMoreClickArea = itemView.findViewById(R.id.ll_more_area)
            mTvMoreText = itemView.findViewById(R.id.tv_more_text)
            mViewBottomDivider10dp = itemView.findViewById(R.id.view_bottom_divider_10dp)
            mViewBottomDivider1dp = itemView.findViewById(R.id.view_bottom_divider_1dp)
        }
    }

    override fun onBindViewHolder(context: Context, position: Int, moduleList: MutableList<ModuleList>?) {
        super.onBindViewHolder(context, position, moduleList)

        // 매장 스크롤시 RecyclerView 특성상 계속해서 binding 되며 addView 되는 현상을 막기 위함.
        mLlGridItemLayout.removeAllViews()

        // 리스트가 6개 초과일때에는 최초 6개만 보여주고 더보기 버튼을 통해 펼치기 위한 if문.
        if (moduleList != null && moduleList[position].productList != null) {
            if (moduleList[position].productList!!.size > 6) {
                if (!moduleList[position].isShowAllForPrdMltGba) {
                    // 한 줄에 3개씩 그려야해서 3의 배수에 대해 명확하게 하기 위해 for 문은 1부터 시작한다. 따라서 list.add 는 -1.
                    for (i in 1..6) {
                        mTempList.add(moduleList[position].productList!![i - 1])
                        if (i % 3 == 0) {
                            val prdMltGabRowLayout = PrdMltGbaRowLayout(context)
                            mLlGridItemLayout.addView(prdMltGabRowLayout)
                            prdMltGabRowLayout.setRowData(mTempList)
                            mTempList.clear()
                        }
                    }
                    mLlMoreClickArea.visibility = View.VISIBLE
                    if (moduleList[position].productList != null) {
                        mTvMoreText.text = context.getString(R.string.prd_mlt_gba_count_text, moduleList[position].moreText, (moduleList[position].productList!!.size - 6).toString())
                    }
                }
                else {
                    setAllList(context, moduleList[position].productList!!)
                }
                // 더보기 클릭 (전체 노출)
                mLlMoreClickArea.setOnClickListener {
                    moduleList[position].isShowAllForPrdMltGba = true
                    mLlMoreClickArea.visibility = View.GONE
                    mLlGridItemLayout.removeAllViews()
                    setAllList(context, moduleList[position].productList!!)
                }
            } else {
                mLlGridItemLayout.visibility = View.GONE
                setAllList(context, moduleList[position].productList!!)
            }
            setNextItemMargin(moduleList, position)
        }
    }

    private fun setAllList(context:Context, productList:ArrayList<SectionContentList>) {
        for (i in 1..productList.size) {
            if (i % 3 == 0) {
                mTempList.add(productList[i - 1])
                val prdMltGbaRowLayout = PrdMltGbaRowLayout(context)
                mLlGridItemLayout.addView(prdMltGbaRowLayout)
                prdMltGbaRowLayout.setRowData(mTempList)
                mTempList.clear()
            } else {
                mTempList.add(productList[i - 1])
            }
        }

        // 3의 배수마다 addView 하고 남은 아이템을 add 한다.
        if (mTempList.size > 0) {
            val prdMltGbaRowLayout = PrdMltGbaRowLayout(context)
            mLlGridItemLayout.addView(prdMltGbaRowLayout)
            prdMltGbaRowLayout.setRowData(mTempList)
            mTempList.clear()
        }
    }

    /**
     * 다음 아이템과 동일한 뷰타입인 경우는 1dp, 아인 경우는 10dp의 마진을 설정한다.
     *
     * @param moduleLists List<ModuleList>
     * @param position    뷰홀더 포지션
     */
    private fun setNextItemMargin(moduleLists: MutableList<ModuleList>, position: Int) {
        var isSameToNext = false
        val moduleList = moduleLists[position]
        if (moduleLists.size > position + 1) {
            val nextItem = moduleLists[position + 1]
            if (EmptyUtils.isNotEmpty(nextItem) && EmptyUtils.isNotEmpty(nextItem.viewType) &&
                    EmptyUtils.isNotEmpty(moduleList) && EmptyUtils.isNotEmpty(moduleList.viewType) && moduleList.viewType == nextItem.viewType) {
                isSameToNext = true
            }
        }
        if (isSameToNext) {
            mViewBottomDivider1dp.visibility = View.VISIBLE
            mViewBottomDivider10dp.visibility = View.GONE
        } else {
            mViewBottomDivider1dp.visibility = View.GONE
            mViewBottomDivider10dp.visibility = View.VISIBLE
        }
    }
}