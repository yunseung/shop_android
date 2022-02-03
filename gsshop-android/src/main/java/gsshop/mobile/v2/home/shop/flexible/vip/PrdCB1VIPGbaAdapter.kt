package gsshop.mobile.v2.home.shop.flexible.vip

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.main.SectionContentList
import gsshop.mobile.v2.home.shop.renewal.flexible.RenewalFlexiblePrdCB1Adapter
import gsshop.mobile.v2.home.shop.renewal.utils.SetDtoUtil

class PrdCB1VIPGbaAdapter(context: Context, subProductList: List<SectionContentList?>?, action: String?, label: String?, naviId: String?)
    : RenewalFlexiblePrdCB1Adapter(context, subProductList, action, label, naviId) {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ProductViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.view_holder_prd_c_b1_vip_gba_item, viewGroup, false)
        return ProductViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.mLayoutProductInfo.setViews(SetDtoUtil.setDto(mItem), SetDtoUtil.BroadComponentType.product_c_b1_vip_gba)
    }
}