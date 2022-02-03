package gsshop.mobile.v2.home.shop.ltype

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.EmptyUtils
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.main.ModuleList
import gsshop.mobile.v2.home.main.SectionContentList
import gsshop.mobile.v2.util.ImageUtil
import gsshop.mobile.v2.util.SwipeUtils
import kotlinx.android.synthetic.main.view_holder_gr_brd_gba_img.view.*

/**
 * 정사각이미지 영역 ViewPager2 어뎁터
 */
class GrBrdGbaImgAdapter : RecyclerView.Adapter<GrBrdGbaImgAdapter.PrdViewHolder>() {
    /**
     * 켠텍스트
     */
    private lateinit var mContext: Context

    /**
     * 데이타
     */
    private var moduleLists: List<SectionContentList>? = null

    fun updateProducts(moduleLists: List<SectionContentList>?) {
        this.moduleLists = moduleLists
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrdViewHolder {
        mContext = parent.context
        return PrdViewHolder(LayoutInflater
                .from(parent.context).inflate(R.layout.view_holder_gr_brd_gba_img, parent, false))
    }

    override fun onBindViewHolder(holder: PrdViewHolder, position: Int) {
        val item = moduleLists!![position]
        ImageUtil.loadImage(mContext, item.imageUrl, holder.mIvPrd, R.drawable.noimage_166_166)

        //2페이지 이상인 경우만 매장이동 제한
        holder.mIvPrd.setOnTouchListener(null)
        if (moduleLists!!.size > 1) {
            holder.mIvPrd.setOnTouchListener { _: View, event: MotionEvent ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        SwipeUtils.disableSwipe()
                    }
                }
                false
            }
        }
    }

    override fun getItemCount(): Int {
        return if (EmptyUtils.isNotEmpty(moduleLists)) moduleLists!!.size else 0
    }

    /**
     * 뷰홀더
     */
    class PrdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mIvPrd: ImageView = itemView.iv_prd
    }
}