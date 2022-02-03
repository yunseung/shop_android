package gsshop.mobile.v2.home.shop.ltype

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.EmptyUtils
import com.gsshop.mocha.ui.util.ViewUtils
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.main.SectionContentList
import gsshop.mobile.v2.home.shop.renewal.utils.SetDtoUtil
import gsshop.mobile.v2.home.shop.renewal.views.RenewalLayoutProductInfo
import gsshop.mobile.v2.util.DisplayUtils
import gsshop.mobile.v2.util.ImageUtil
import gsshop.mobile.v2.util.SwipeUtils
import gsshop.mobile.v2.web.WebUtils
import kotlinx.android.synthetic.main.view_holder_gr_brd_gba_prd.view.*
import java.util.*
import kotlin.math.ceil

/**
 * 상품컴포넌트 영역 ViewPager2 어뎁터
 */
class GrBrdGbaPrdAdapter : RecyclerView.Adapter<GrBrdGbaPrdAdapter.PrdViewHolder>() {
    /**
     * 켠텍스트
     */
    private lateinit var mContext: Context

    /**
     * 데이타 가공용도 맵
     */
    private val mContentMap: MutableMap<Int, List<SectionContentList>> = HashMap()

    fun updateProducts(products: List<SectionContentList>?) {
        //맵 초기화
        mContentMap.clear()
        if (EmptyUtils.isEmpty(products)) {
            notifyDataSetChanged()
            return
        }

        //페이지 수
        val size = products!!.size
        if (size <= PRD_LIMIT_NUM) {
            mContentMap[0] = products
        } else {
            val loopCnt = ceil((size.toDouble() / NUM_PER_PAGE.toDouble())).toInt()
            for (i in 0 until loopCnt) {
                val sIdx = i * NUM_PER_PAGE
                var eIdx = sIdx + NUM_PER_PAGE
                //상품수가 NUM_PER_PAGE의 배수가 아닌 경우
                if (size < eIdx) {
                    //마지막 페이지는 상품이 1 ~ NUM_PER_PAGE 일수 있음
                    eIdx -= (eIdx - size)
                }
                //페이지별로 상품을 저장 (i값이 페이지 번호)
                mContentMap[i] = products.subList(sIdx, eIdx)
            }
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrdViewHolder {
        mContext = parent.context
        return PrdViewHolder(LayoutInflater
                .from(parent.context).inflate(R.layout.view_holder_gr_brd_gba_prd, parent, false))
    }

    override fun onBindViewHolder(holder: PrdViewHolder, position: Int) {
        ViewUtils.hideViews(holder.mLlPrd1th, holder.mLlPrd2th, holder.mLlPrd3th, holder.mLlPrd4th)

        val items = mContentMap[position]!!
        for ((i, v) in items.withIndex()) {
            when (i) {
                0 -> {
                    ViewUtils.showViews(holder.mLlPrd1th)
                    setViews(v, holder.mLlPrd1th, holder.mLayoutProductInfo1th, holder.mIvPrd1th, holder.mTxtBrdTime1th)
                }
                1 -> {
                    ViewUtils.showViews(holder.mLlPrd2th)
                    setViews(v, holder.mLlPrd2th, holder.mLayoutProductInfo2th, holder.mIvPrd2th, holder.mTxtBrdTime2th)
                }
                2 -> {
                    ViewUtils.showViews(holder.mLlPrd3th)
                    setViews(v, holder.mLlPrd3th, holder.mLayoutProductInfo3th, holder.mIvPrd3th, holder.mTxtBrdTime3th)
                }
                3 -> {
                    ViewUtils.showViews(holder.mLlPrd4th)
                    setViews(v, holder.mLlPrd4th, holder.mLayoutProductInfo4th, holder.mIvPrd4th, holder.mTxtBrdTime4th)
                }
            }
        }
    }

    private fun setViews(item: SectionContentList, llRoot: LinearLayout, prdInfo: RenewalLayoutProductInfo, iv: ImageView, tvBrdTime: TextView) {
        //가격표시용 공통모듈에 맞게 데이타 변경
        val info = SetDtoUtil.setDto(item)
        prdInfo.setViews(info, SetDtoUtil.BroadComponentType.signature)

        //섬네일 로딩
        ImageUtil.loadImage(mContext, item.imageUrl, iv, R.drawable.noimage_166_166)

        //방송시간 표시
        tvBrdTime.visibility = View.GONE
        if (item.broadTimeText != null && DisplayUtils.isValidString(item.broadTimeText)) {
            tvBrdTime.text = item.broadTimeText
            tvBrdTime.visibility = View.VISIBLE
        }

        llRoot.setOnTouchListener(null)
        if (mContentMap.size > 1) {
            //2페이지 이상인 경우만 매장이동 제한
            llRoot.setOnTouchListener { _: View, event: MotionEvent ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        SwipeUtils.disableSwipe()
                    }
                    MotionEvent.ACTION_UP -> {
                        WebUtils.goWeb(mContext, item.linkUrl)
                    }
                }
                true
            }
        } else {
            llRoot.setOnClickListener { WebUtils.goWeb(mContext, item.linkUrl) }
        }
    }

    override fun getItemCount(): Int {
        return if (EmptyUtils.isEmpty(mContentMap)) 0 else mContentMap.size
    }

    /**
     * 레이아웃 파일에서 <include> 사용시 2페이지부터 데이타가 랜덤하게 표시 안되는 현상이 있어
     * 중복된 레이아웃을 그대로 삽입하여 사용함.
    */
    class PrdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mLlPrd1th: LinearLayout = itemView.ll_prd_1th
        var mLayoutProductInfo1th: RenewalLayoutProductInfo = itemView.layout_product_info_1th
        var mIvPrd1th: ImageView = itemView.iv_prd_1th
        var mTxtBrdTime1th: TextView = itemView.txt_brd_time_1th
        var mLlPrd2th: LinearLayout = itemView.ll_prd_2th
        var mLayoutProductInfo2th: RenewalLayoutProductInfo = itemView.layout_product_info_2th
        var mIvPrd2th: ImageView = itemView.iv_prd_2th
        var mTxtBrdTime2th: TextView = itemView.txt_brd_time_2th
        var mLlPrd3th: LinearLayout = itemView.ll_prd_3th
        var mLayoutProductInfo3th: RenewalLayoutProductInfo = itemView.layout_product_info_3th
        var mIvPrd3th: ImageView = itemView.iv_prd_3th
        var mTxtBrdTime3th: TextView = itemView.txt_brd_time_3th
        var mLlPrd4th: LinearLayout = itemView.ll_prd_4th
        var mLayoutProductInfo4th: RenewalLayoutProductInfo = itemView.layout_product_info_4th
        var mIvPrd4th: ImageView = itemView.iv_prd_4th
        var mTxtBrdTime4th: TextView = itemView.txt_brd_time_4th
    }

    companion object {
        /**
         * 단일페이지 또는 캐로셀 노출의 기준이 되는 상품수
         */
        const val PRD_LIMIT_NUM = 4

        /**
         * 페이지당 표시할 상품 수
         */
        const val NUM_PER_PAGE = 3
    }
}