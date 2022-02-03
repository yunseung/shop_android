package gsshop.mobile.v2.home.shop.flexible.beauty

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import gsshop.mobile.v2.R
import gsshop.mobile.v2.home.shop.BaseViewHolder
import gsshop.mobile.v2.home.shop.ViewHolderType
import gsshop.mobile.v2.home.shop.bestshop.BestShopAdapter
import gsshop.mobile.v2.home.shop.ltype.BanSldGbgVH
import gsshop.mobile.v2.home.shop.renewal.publicusage.Prd2VH
import gsshop.mobile.v2.support.gtm.GTMEnum
import roboguice.util.Ln

class BeautyShopAdapter(context: Context?, fragment: BeautyShopFragment) : BestShopAdapter(context) {

    private val mFragment: BeautyShopFragment = fragment

//    private var beautyTabMenuTomm: BeautyTabMenuTomm? = null
//    private var beautyTabMenuBest: BeautyTabMenuBest? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): BaseViewHolder {
        var viewHolder: BaseViewHolder? = null
        var itemView: View? = null
        when (viewType) {
            ViewHolderType.VIEW_TYPE_BAN_BEAUTY_SLD -> {
                itemView = LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.view_holder_ban_beauty_sld, viewGroup, false)
                viewHolder = BanBeautySldVH(itemView, context)
            }
            ViewHolderType.VIEW_TYPE_BAN_BEAUTY_SLD_TIME_SALE -> {
                itemView = LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.view_holder_ban_beauty_sld_time_sale, viewGroup, false)
                viewHolder = BanBeautySldTimeSaleVH(itemView, context)
            }
            ViewHolderType.VIEW_TYPE_PRD_BEAUTY_SLD_WEEKLY_EVENT -> {
                itemView = LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.view_holder_prd_beauty_sld_weekly_event, viewGroup, false)
                viewHolder = PrdBeautySldWeeklyEventVH(itemView, context)
            }
            ViewHolderType.VIEW_TYPE_TAB_BEAUTY_MENU_BEST -> {
                itemView = LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.view_holder_tab_beauty_menu_best, viewGroup, false)
//                Ln.d("hklim Create BeautyTabMenuBest")
                viewHolder = BeautyTabMenuBest(itemView, context)
            }
            ViewHolderType.VIEW_TYPE_TAB_BEAUTY_MENU -> {
                itemView = LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.view_holder_tab_beauty_menu, viewGroup, false)
//                Ln.d("hklim Create BeautyTabMenuTomm")
                viewHolder = BeautyTabMenuTomm(itemView, context)
            }
            ViewHolderType.VIEW_TYPE_GR_BEAUTY_CATE -> {
                itemView = LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.view_holder_prd_common_recycler, viewGroup, false)
                viewHolder = BanGrBeautyCate(itemView)
            }
            ViewHolderType.VIEW_TYPE_GR_BEAUTY_SLD_BRAND -> {
                itemView = LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.view_holder_ban_sld_gbg, viewGroup, false)
                viewHolder = BanSldGbgVH(itemView)
            }
            ViewHolderType.VIEW_TYPE_PRD_2_BEAUTY_BEST, ViewHolderType.VIEW_TYPE_PRD_2_BEAUTY_TOMM -> {
                itemView = LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.renewal_banner_type_l_prd_2, viewGroup, false)
                viewHolder = Prd2VH(itemView)
            }

            ViewHolderType.VIEW_TYPE_NO_DATA_BEST, ViewHolderType.VIEW_TYPE_NO_DATA_TOMM -> {
                itemView = LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.view_holder_beauty_no_data, viewGroup, false)
                viewHolder = BanBeaytyNoData(itemView)
            }
            ViewHolderType.VIEW_TYPE_BAN_BEAUTY_SLD_TODAY -> {
                itemView = LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.view_holder_ban_beauty_sld_today, viewGroup, false)
                viewHolder = BanBeautySldTodayVH(itemView, context)
            }
            ViewHolderType.VIEW_TYPE_BAN_VIEW_MORE_BEAUTY -> {
                itemView = LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.view_holder_common_add_sub, viewGroup, false)
                viewHolder = BanBeautyMoreVH(itemView)
            }
            ViewHolderType.VIEW_TYPE_BAN_IMG_BEAUTY_TOMM -> {
                itemView = LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.view_holder_ban_img_beauty_tomm, viewGroup, false)
                viewHolder = BanImgBeautyTomm(itemView)
            }
            else -> viewHolder = super.onCreateViewHolder(viewGroup, viewType)
        }
        return viewHolder
    }

    override fun onBindViewHolder(viewHolder: BaseViewHolder, position: Int) {
        val action = GTMEnum.GTM_NONE
        val label = GTMEnum.GTM_NONE

        when (viewHolder.itemViewType) {
            ViewHolderType.VIEW_TYPE_BAN_BEAUTY_SLD,
            ViewHolderType.VIEW_TYPE_BAN_BEAUTY_SLD_TIME_SALE,
            ViewHolderType.VIEW_TYPE_PRD_BEAUTY_SLD_WEEKLY_EVENT,
            ViewHolderType.VIEW_TYPE_GR_BEAUTY_CATE,
            ViewHolderType.VIEW_TYPE_GR_BEAUTY_SLD_BRAND,
            ViewHolderType.VIEW_TYPE_BAN_BEAUTY_SLD_TODAY,
            ViewHolderType.VIEW_TYPE_BAN_VIEW_MORE_BEAUTY,
            ViewHolderType.VIEW_TYPE_BAN_IMG_BEAUTY_TOMM,
            -> {
                viewHolder.onBindViewHolder(mContext, position, mInfo, action, label, null)
            }
            ViewHolderType.VIEW_TYPE_TAB_BEAUTY_MENU -> {
                try {
                    val positionCate = mFragment.gsXBrandCategoryHolder.getSelectedPosition(mInfo.sectionList.navigationId)

                    var positionTab = 0
                    if (mFragment.gListTomorrow[positionCate] == null) {
                        mFragment.gListTomorrow[positionCate] = 0
                    } else {
                        positionTab = mFragment.gListTomorrow[positionCate]!!
                    }

//                    beautyTabMenuTomm = viewHolder as BeautyTabMenuTomm
                    (viewHolder as BeautyTabMenuTomm).setListPosition(positionTab)
                }
                catch (e:IndexOutOfBoundsException) {
                    Ln.e(e.message)
                }
                catch (e:NullPointerException) {
                    Ln.e(e.message)
                }

                viewHolder.onBindViewHolder(mContext, position, mInfo, action, label, null)
            }
            ViewHolderType.VIEW_TYPE_TAB_BEAUTY_MENU_BEST -> {
                try{
                    val positionCate = mFragment.gsXBrandCategoryHolder.getSelectedPosition(mInfo.sectionList.navigationId)

                    var positionTab = mutableListOf(0, 0)
                    if (mFragment.gListBest[positionCate] == null) {
                        mFragment.gListBest[positionCate] = mutableListOf(0, 0)
                    }
                    else {
                        positionTab = mFragment.gListBest[positionCate]!!
                    }
//                    beautyTabMenuBest = viewHolder as BeautyTabMenuBest
                    (viewHolder as BeautyTabMenuBest).setListPosition(positionTab)
                }
                catch (e:IndexOutOfBoundsException) {
                    Ln.e(e.message)
                }
                catch (e:NullPointerException) {
                    Ln.e(e.message)
                }
                viewHolder.onBindViewHolder(mContext, position, mInfo, action, label, null)
            }
            ViewHolderType.VIEW_TYPE_NO_DATA_BEST,
            ViewHolderType.VIEW_TYPE_NO_DATA_TOMM -> {
                try {
                    for (item in mInfo.contents) {
                        if (item.type == ViewHolderType.BAN_CX_SLD_CATE_GBA) {
                            if (mFragment.gsXBrandCategoryHolder != null) {
                                val selPosition = mFragment.gsXBrandCategoryHolder.getSelectedPosition(mInfo.sectionList.navigationId)
                                mInfo.contents[position].sectionContent.mseq = item.sectionContent.subProductList?.get(selPosition)?.mseq
                            }
                            break;
                        }
                    }
                }
                catch (e:NullPointerException) {
                    Ln.e(e.message)
                }
                viewHolder.onBindViewHolder(mContext, position, mInfo, action, label, null)
            }
            ViewHolderType.VIEW_TYPE_PRD_2_BEAUTY_BEST,
            ViewHolderType.VIEW_TYPE_PRD_2_BEAUTY_TOMM,
            -> {
                mInfo.contents[position].sectionContent.isSameItem = true
                viewHolder.onBindViewHolder(mContext, position, mInfo, action, label, null)

            }
            else -> super.onBindViewHolder(viewHolder, position)
        }
    }

//    fun getBestTabPosition() : MutableList<Int>? {
//        return beautyTabMenuBest?.getListPosition()
//    }
//
//    fun getTomTabPosition() : Int? {
//        return beautyTabMenuTomm?.getListPosition()
//    }
}