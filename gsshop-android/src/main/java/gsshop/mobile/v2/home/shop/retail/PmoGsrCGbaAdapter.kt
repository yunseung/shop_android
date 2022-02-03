package gsshop.mobile.v2.home.shop.retail

import android.content.Context
import android.graphics.Outline
import android.os.Build
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.EmptyUtils
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.main.SectionContentList
import gsshop.mobile.v2.util.ImageUtil
import gsshop.mobile.v2.web.WebUtils
import kotlinx.android.synthetic.main.view_holder_pmo_gsr_c_gba_item.view.*

/**
 * 정사각이미지 영역 ViewPager2 어뎁터
 */
class PmoGsrCGbaAdapter : RecyclerView.Adapter<PmoGsrCGbaAdapter.PrdViewHolder>() {
    /**
     * 켠텍스트
     */
    private lateinit var mContext: Context

    /**
     * 데이타
     */
    private var mSectionContentList: List<SectionContentList>? = null

    fun updateProducts(sectionContentList: List<SectionContentList>?) {
        this.mSectionContentList = sectionContentList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrdViewHolder {
        mContext = parent.context
        return PrdViewHolder(LayoutInflater
                .from(parent.context).inflate(R.layout.view_holder_pmo_gsr_c_gba_item, parent, false))
    }

    override fun onBindViewHolder(holder: PrdViewHolder, position: Int) {
        val item = mSectionContentList!![position]

        ImageUtil.loadImageFit(mContext, item.titleImgUrl, holder.mIvTitleImg, R.drawable.noimage_166_166)
        ImageUtil.loadImageFit(mContext, item.imageUrl, holder.mIvImg, R.drawable.noimage_166_166)

        holder.mIvTitleImg.setOnClickListener { WebUtils.goWeb(mContext, item.brandLinkUrl) }
        holder.mIvImg.setOnClickListener { WebUtils.goWeb(mContext, item.linkUrl) }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // ImageView 의 하단에만 radius 를 만들기 위한 코드.
            val mOutlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    val left = 0
                    val top = 0
                    val right = view.width
                    val bottom = view.height
                    val cornerRadiusDP = 4f
                    val cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, cornerRadiusDP, mContext.resources.displayMetrics).toInt()
                    outline.setRoundRect(left, top - cornerRadius, right, bottom, cornerRadius.toFloat())
                }
            }

            holder.mIvImg.outlineProvider = mOutlineProvider
            holder.mIvImg.clipToOutline = true
        }
    }

    override fun getItemCount(): Int {
        return if (EmptyUtils.isNotEmpty(mSectionContentList)) mSectionContentList!!.size else 0
    }

    /**
     * 뷰홀더
     */
    class PrdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mIvImg: ImageView = itemView.iv_img
        val mIvTitleImg: ImageView = itemView.iv_title_img
    }
}