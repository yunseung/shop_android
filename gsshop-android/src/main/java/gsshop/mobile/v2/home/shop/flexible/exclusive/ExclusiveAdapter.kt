package gsshop.mobile.v2.home.shop.flexible.exclusive

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.shop.BaseViewHolder
import gsshop.mobile.v2.home.shop.ViewHolderType
import gsshop.mobile.v2.home.shop.bestshop.BestShopAdapter
import gsshop.mobile.v2.home.shop.ltype.BanSldGbgVH
import gsshop.mobile.v2.home.shop.ltype.GrBrdGbaVH
import gsshop.mobile.v2.home.shop.renewal.flexible.BanMoreGbaVH
import gsshop.mobile.v2.support.gtm.GTMEnum

class ExclusiveAdapter(context: Context?) : BestShopAdapter(context) {

    private var tabVH: BanTabSelVH? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): BaseViewHolder {
        var viewHolder: BaseViewHolder? = null
        var itemView: View? = null

        when (viewType) {
            ViewHolderType.VIEW_TYPE_BAN_TAB_SEL -> {
                itemView = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.view_holder_ban_tab_sel, viewGroup, false)
                viewHolder = BanTabSelVH(itemView)
                tabVH = viewHolder
            }
            ViewHolderType.VIEW_TYPE_GR_BRD_GBA_FXC -> {
                itemView = LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.view_holder_gr_brd_gba, viewGroup, false)
                viewHolder = GrBrdGbaVH(itemView)
            }
            ViewHolderType.VIEW_TYPE_BAN_SLD_GBG_FXC -> {
                itemView = LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.view_holder_ban_sld_gbg, viewGroup, false)
                viewHolder = BanSldGbgVH(itemView);
            }
            else -> viewHolder = super.onCreateViewHolder(viewGroup, viewType)
        }

        return viewHolder
    }

    override fun onBindViewHolder(viewHolder: BaseViewHolder, position: Int) {
        val action = GTMEnum.GTM_NONE
        val label = GTMEnum.GTM_NONE

        when (viewHolder.itemViewType) {
            ViewHolderType.VIEW_TYPE_BAN_TAB_SEL,
            ViewHolderType.VIEW_TYPE_GR_BRD_GBA_FXC,
            ViewHolderType.VIEW_TYPE_BAN_SLD_GBG_FXC
            -> {
                viewHolder.onBindViewHolder(mContext, position, mInfo, action, label, null)
            }

            else -> super.onBindViewHolder(viewHolder, position)
        }
    }

    fun setTabSel(position:Int) {
        tabVH?.setSel(position)
    }
}