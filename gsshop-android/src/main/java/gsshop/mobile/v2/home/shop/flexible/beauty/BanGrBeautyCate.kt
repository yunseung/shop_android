package gsshop.mobile.v2.home.shop.flexible.beauty

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.blankj.utilcode.util.ConvertUtils
import com.gsshop.mocha.ui.util.ViewUtils
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.main.SectionContentList
import gsshop.mobile.v2.home.shop.BaseViewHolder
import gsshop.mobile.v2.home.shop.ShopInfo
import gsshop.mobile.v2.support.ui.BugFixedStaggeredGridLayoutManager
import gsshop.mobile.v2.util.ImageUtil
import gsshop.mobile.v2.web.WebUtils
import kotlinx.android.synthetic.main.view_holder_prd_common_recycler.view.*
import roboguice.util.Ln
import java.lang.NullPointerException

class BanGrBeautyCate (itemView: View) : BaseViewHolder(itemView) {

    private val mRootView = itemView.root

    private val mLayoutManager: BugFixedStaggeredGridLayoutManager? = BugFixedStaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)

    private val mRecyclerView : RecyclerView = itemView.recycler

    init {
        try {
            mRootView.view_bottom_divider_10dp.visibility = View.GONE
            mRootView.recycler.setBackgroundColor(Color.parseColor("#eeeeee"))
        }
        catch (e:NullPointerException) {
            Ln.e(e.message);
        }

        mRecyclerView.layoutManager = mLayoutManager

        mRecyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                super.getItemOffsets(outRect, view, parent, state)
                val position = parent.getChildAdapterPosition(view)

//                (parent.layoutManager as BugFixedStaggeredGridLayoutManager).

                var nTemp1 = position % 4

                if (nTemp1 == 0) {
                    outRect.left = ConvertUtils.dp2px(1f)
                }
                else {
                    outRect.left = ConvertUtils.dp2px(0.5f)
                }

                if (nTemp1 == 3) {
                    outRect.right = ConvertUtils.dp2px(1f)
                }
                else {
                    outRect.right = ConvertUtils.dp2px(0.5f)
                }

                if (position < 4) {
                    outRect.top = ConvertUtils.dp2px(0f)
                }
                else {
                    outRect.top = ConvertUtils.dp2px(0.5f)
                }

                if (position < parent.childCount - 4 ) {
                    outRect.bottom = ConvertUtils.dp2px(0.5f)
                }
                else {
                    outRect.bottom = ConvertUtils.dp2px(1f)
                }
            }
        })
    }

    override fun onBindViewHolder(context: Context, position: Int, info: ShopInfo?,
                                  action: String?, label: String?, sectionName: String?) {
        val item = info?.contents?.get(position)?.sectionContent ?: return

        if (item.subProductList != null) {
            mRecyclerView.adapter = AdapterBeautyCate(item.subProductList!!)
        }
    }

    private class AdapterBeautyCate(list: ArrayList<SectionContentList>) : RecyclerView.Adapter<ViewHolderBeautyCate>() {
        private val mList: ArrayList<SectionContentList> = list

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderBeautyCate {
            val rootView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recycler_item_gr_beauty_cate, parent, false)
            return ViewHolderBeautyCate(rootView)
        }

        override fun getItemCount(): Int {
            return 4 * (mList.size / 4 + if (mList.size % 4 > 0) { 1 } else { 0 })
        }

        override fun onBindViewHolder(holder: ViewHolderBeautyCate, position: Int) {
            if (mList.size <= position) {
                ViewUtils.hideViews(holder.mImageBeautyCate, holder.mViewBeautyHot, holder.mTextBeautyCate)
                return
            }
            else {
                ViewUtils.showViews(holder.mImageBeautyCate, holder.mViewBeautyHot, holder.mTextBeautyCate)
            }

            val item = mList[position]

            if (item.imageUrl != null) {
                ImageUtil.loadImageFit(holder.itemView.context, item.imageUrl, holder.mImageBeautyCate, R.drawable.noimage_78_78)
            }

            if ("Y".equals(item.newYN, ignoreCase = true)) {
                holder.mViewBeautyHot.visibility = View.VISIBLE
            }
            else {
                holder.mViewBeautyHot.visibility = View.GONE
            }

            if (!TextUtils.isEmpty(item.name)) {
                holder.mTextBeautyCate.text = item.name
            }

            if (!TextUtils.isEmpty(item.linkUrl)) {
                if (holder.mRootView != null) {
                    holder.mRootView.setOnClickListener {
                        WebUtils.goWeb(holder.itemView.context, item.linkUrl)
                    }
                }
            }
        }

    }

    private class ViewHolderBeautyCate(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mRootView: View = itemView.findViewById(R.id.root)
        val mImageBeautyCate: ImageView = itemView.findViewById(R.id.product_img)
        val mViewBeautyHot: View = itemView.findViewById(R.id.view_hot)
        val mTextBeautyCate: TextView = itemView.findViewById(R.id.info_text)
    }
}