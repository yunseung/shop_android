/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible.shoppinglive

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.EmptyUtils
import gsshop.mobile.v2.Keys
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.main.SectionContentList
import gsshop.mobile.v2.home.shop.BaseViewHolder
import gsshop.mobile.v2.util.ImageUtil.loadImageFitCenter
import gsshop.mobile.v2.util.SwipeUtils.disableSwipe
import gsshop.mobile.v2.web.WebUtils
import kotlinx.android.synthetic.main.recycler_item_shopping_live_prd_best.view.*
import roboguice.util.Ln

/**
 * 추천딜 리스트 어뎁터.
 */
class AdapterMLPrdBest
/**
 * 리스트이 length 추가.
 *
 * @param mContext
 * @param subProductList
 */(private val mContext: Context, private var subProductList: List<SectionContentList>)
    : RecyclerView.Adapter<AdapterMLPrdBest.MLPrdBestViewHolder>() {
    private var category: String? = null

    fun setCategory(category: String?) {
        this.category = category
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MLPrdBestViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.recycler_item_shopping_live_prd_best, viewGroup, false)
        return MLPrdBestViewHolder(itemView)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: MLPrdBestViewHolder, position: Int) {
        try {
            val item = subProductList[position]

            loadImageFitCenter(mContext, item.imageUrl, holder.root.product_img, R.drawable.noimage_164_246)
            holder.imageForCache = item.imageUrl
            holder.root.txt_product.text = item.promotionName

            holder.root.txt_badge_corner.text = (position + 1).toString()
            holder.root.txt_time.text = item.videoTime
            holder.root.txt_view_counter.text = item.streamViewCount

            holder.root.setOnClickListener {
                holder.root.scaleX = 1f
                holder.root.scaleY = 1f
                val intent = Intent()
                intent.putExtra(Keys.INTENT.IMAGE_URL, if (EmptyUtils.isNotEmpty(holder.imageForCache)) holder.imageForCache!!.replace(BaseViewHolder.IMG_CACHE_RPL3_FROM, BaseViewHolder.IMG_CACHE_RPL_TO) else "")
                WebUtils.goWeb(mContext, item.linkUrl, intent)
            }
            holder.root.setOnTouchListener { _, motionEvent ->
                if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                    disableSwipe()
                    holder.root.scaleX = 0.96f
                    holder.root.scaleY = 0.96f
                }
                false
            }
        }
        catch (e:NullPointerException) {
            Ln.e(e.message)
        }
    }

    override fun getItemCount(): Int {
        return subProductList.size
    }

    /**
     * 뷰홀더
     */
    class MLPrdBestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var root: LinearLayout = itemView.root
        var mImgForCache: String? = null

        var imageForCache: String?
            get() = if (EmptyUtils.isNotEmpty(mImgForCache)) mImgForCache else ""
            set(url) {
                mImgForCache = url
            }
    }

}