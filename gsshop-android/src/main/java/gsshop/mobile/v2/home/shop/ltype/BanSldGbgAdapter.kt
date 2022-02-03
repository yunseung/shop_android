/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.ltype

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.EmptyUtils
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.main.SectionContentList
import gsshop.mobile.v2.util.ImageUtil
import gsshop.mobile.v2.util.SwipeUtils
import gsshop.mobile.v2.web.WebUtils
import kotlinx.android.synthetic.main.view_holder_ban_sld_gbg_item.view.*
import java.util.*

/**
 * 시그니처매장 가로 캐로셀 어뎁터.
 *
 * BanSldGbgVH에서 본 뷰홀더 호출시 moduleList.moduleList값이 null or empty가 아닌 경우만 호출하므로
 * API Result 클래스임에도 불구하고 moduleList를 not null로 사용
 */
class BanSldGbgAdapter(private val mContext: Context?, moduleList: ArrayList<SectionContentList>) :
        RecyclerView.Adapter<BanSldGbgAdapter.ItemViewHolder>() {

    /**
     * 데이타
     */
    private val moduleList: List<SectionContentList>

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.view_holder_ban_sld_gbg_item, viewGroup, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = moduleList[position]

        //접근성
        if (EmptyUtils.isNotEmpty(item.name)) {
            holder.mIvProduct.contentDescription = item.name
        }
        if (mContext != null && EmptyUtils.isNotEmpty(item.imageUrl) ) {
            ImageUtil.loadImageFitWithRound(mContext, item.imageUrl, holder.mIvProduct, 0, 0)
        }

        if (!TextUtils.isEmpty(item.linkUrl)) {
            holder.mFlRoot.setOnClickListener {
                WebUtils.goWeb(mContext, item.linkUrl)
            }
        }

        holder.mFlRoot.setOnTouchListener { _: View, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    SwipeUtils.disableSwipe()
                }
            }
            false
        }
    }

    override fun getItemCount(): Int {
        return moduleList.size
    }

    /**
     * 뷰홀더
     */
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mFlRoot: FrameLayout = itemView.fl_root
        var mIvProduct: ImageView = itemView.iv_product
    }

    /**
     * 아이템의 유효성을 확인한다.
     *
     * @param moduleList List<ModuleList>
     * @return 유효하지 않은 아이템을 제거한 결과
     */
    private fun removeInvalidItem(moduleList: List<SectionContentList>): List<SectionContentList> {
        val newModuleList: MutableList<SectionContentList> = ArrayList()
        for (m in moduleList) {
            //이미지, 링크 정보가 존재하는 경우만 노출
            if (EmptyUtils.isNotEmpty(m.imageUrl) || EmptyUtils.isNotEmpty(m.linkUrl)) {
                newModuleList.add(m)
            }
        }
        return newModuleList
    }

    init {
        this.moduleList = removeInvalidItem(moduleList)
//        this.moduleList = moduleList
    }
}