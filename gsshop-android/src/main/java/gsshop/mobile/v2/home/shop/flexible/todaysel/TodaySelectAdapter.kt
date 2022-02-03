package gsshop.mobile.v2.home.shop.flexible.todaysel

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.shop.BaseViewHolder
import gsshop.mobile.v2.home.shop.ViewHolderType
import gsshop.mobile.v2.home.shop.bestshop.BestShopAdapter
import gsshop.mobile.v2.home.shop.renewal.flexible.BanMoreGbaVH
import gsshop.mobile.v2.support.gtm.GTMEnum

class TodaySelectAdapter(context: Context?) : BestShopAdapter(context) {

//    private var beautyTabMenuTomm: BeautyTabMenuTomm? = null
//    private var beautyTabMenuBest: BeautyTabMenuBest? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): BaseViewHolder {
        var viewHolder: BaseViewHolder? = null
        var itemView: View? = null
        when (viewType) {
            ViewHolderType.VIEW_TYPE_BAN_SLD_ROUNDED_SMALL -> {
                itemView = LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.view_holder_ban_sld_rounded_small, viewGroup, false)
                viewHolder = BanSldRoundedSVH(itemView, context)
            }
            ViewHolderType.VIEW_TYPE_BAN_SLD_ROUNDED_BIG -> {
                itemView = LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.view_holder_ban_sld_rounded_big, viewGroup, false)
                viewHolder = BanSldRoundedBVH(itemView, context)
            }
            ViewHolderType.VIEW_TYPE_BAN_TS_TXT_SUB_GBA -> {
                itemView = LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.view_holder_ts_txt_sub_gba, viewGroup, false)
                viewHolder = BanTsTxtSubGbaVH(itemView)
            }
            ViewHolderType.VIEW_TYPE_GR_TAB_TODAY_SEL -> {
                itemView = LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.view_holder_gr_tab_today_sel, viewGroup, false)
                viewHolder = GrTabTodaySel(itemView)
            }
            ViewHolderType.VIEW_TYPE_BAN_MORE_GBA -> {
                itemView = LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.view_holder_ban_more_gba_today_sel, viewGroup, false)
                viewHolder = BanMoreGbaVH(itemView)
            }
            else -> viewHolder = super.onCreateViewHolder(viewGroup, viewType)
        }
        return viewHolder
    }

    override fun onBindViewHolder(viewHolder: BaseViewHolder, position: Int) {
        val action = GTMEnum.GTM_NONE
        val label = GTMEnum.GTM_NONE

        when (viewHolder.itemViewType) {
            ViewHolderType.VIEW_TYPE_BAN_SLD_ROUNDED_SMALL,
            ViewHolderType.VIEW_TYPE_BAN_SLD_ROUNDED_BIG,
            ViewHolderType.VIEW_TYPE_BAN_TS_TXT_SUB_GBA,
            ViewHolderType.VIEW_TYPE_GR_TAB_TODAY_SEL
            -> {
                viewHolder.onBindViewHolder(mContext, position, mInfo, action, label, null)
            }
            else -> super.onBindViewHolder(viewHolder, position)
        }
    }

}