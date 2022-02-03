package gsshop.mobile.v2.home.shop.flexible.wine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ViewHolderType;
import gsshop.mobile.v2.home.shop.flexible.FlexibleBannerImageViewHolder_AB;
import gsshop.mobile.v2.home.shop.flexible.FlexibleShopAdapter;
import gsshop.mobile.v2.home.shop.gssuper.BanSldGbdVH;
import gsshop.mobile.v2.home.shop.gssuper.MapCbSldGbaVH;
import gsshop.mobile.v2.home.shop.renewal.flexible.PrdCSqVH;
import gsshop.mobile.v2.support.gtm.GTMEnum;

public class WineShopAdapter extends FlexibleShopAdapter {
    public WineShopAdapter(Context context) {
        super(context);
    }
    protected MenuSldWindVH mMenuSldVH;

    @Override
    public void onBindViewHolder(BaseViewHolder viewHolder, int position) {
        String action = GTMEnum.GTM_NONE;
        String label = GTMEnum.GTM_NONE;

        switch (viewHolder.getItemViewType()) {
            case ViewHolderType.VIEW_TYPE_BAN_TITLE_WINE :
            case ViewHolderType.VIEW_TYPE_BAN_SLD_WINE:
            case ViewHolderType.VIEW_TYPE_MENU_SLD_WINE:
            case ViewHolderType.VIEW_TYPE_PRD_PMO_LIST_WINE:
            case ViewHolderType.VIEW_TYPE_PRD_C_SQ_LIST_WINE:
            case ViewHolderType.VIEW_TYPE_BAN_TXT_EXP_WINE:
            case ViewHolderType.VIEW_TYPE_TAB_ANCH_WINE:
            case ViewHolderType.VIEW_TYPE_TAB_ANCH_WINE_TOP :
            case ViewHolderType.VIEW_TYPE_PRD_2_WINE:
            case ViewHolderType.VIEW_TYPE_BAN_VIEW_MORE_WINE :
                viewHolder.onBindViewHolder(mContext, position, mInfo, action, label, null);

            default:
                super.onBindViewHolder(viewHolder, position);
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        BaseViewHolder viewHolder = null;
        View itemView = null;
        switch (viewType) {
            case ViewHolderType.VIEW_TYPE_BAN_TITLE_WINE :
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_ban_title_wine, viewGroup, false);
                viewHolder = new BanTitleWineVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_BAN_SLD_WINE :
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_gs_super_roll_image_banner, viewGroup, false);
                viewHolder = new BanSldGbdVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_MENU_SLD_WINE :
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_menu_sld_wine, viewGroup, false);
                viewHolder = mMenuSldVH = new MenuSldWindVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_PRD_PMO_LIST_WINE :
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_prd_pmo_list_wine, viewGroup, false);
                viewHolder = new PrdPmoListWineVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_PRD_C_SQ_LIST_WINE :
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_prd_common_recycler_with_txt_title, viewGroup, false);
                viewHolder = new PrdCSqListWineVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_BAN_TXT_EXP_WINE :
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_ban_txt_exp_wine, viewGroup, false);
                viewHolder = new BanTxtExpWineVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_TAB_ANCH_WINE :
            case ViewHolderType.VIEW_TYPE_TAB_ANCH_WINE_TOP :
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_tab_anch_wine, viewGroup, false);
                viewHolder = new TabAnchWineVH(itemView, mInfo.sectionList.navigationId, false );
                break;
            case ViewHolderType.VIEW_TYPE_PRD_2_WINE :
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_prd_2_wine, viewGroup, false);
                viewHolder = new Prd2WineVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_BAN_VIEW_MORE_WINE :
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_common_add_sub, viewGroup, false);
                viewHolder = new BanViewMoreVH(itemView);
                break;
            default :
                viewHolder = super.onCreateViewHolder(viewGroup, viewType);
        }
        return viewHolder;
    }
}
