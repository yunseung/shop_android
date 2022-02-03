/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.gssuper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gsshop.mocha.core.exception.ApplyExceptionHandler;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ViewHolderType;
import gsshop.mobile.v2.home.shop.flexible.FlexibleShopAdapter;
import gsshop.mobile.v2.support.gtm.GTMEnum;

/**
 *
 *
 */
public class GSSuperAdapter extends FlexibleShopAdapter {

//    private static final int DEFAULT_LIST_LENGTH = -1;
//    // 더보기에 따라 Length 크기가 조절되어야 한다.
//    private int mLength = DEFAULT_LIST_LENGTH;
    private String mNavigationID = null;

    private String mTabURL = null;

    public GSSuperAdapter(Context context) {
        super(context);
    }

    public void setNavigationID (String navId) {
        mNavigationID = navId;
    }

    public void setTabUrl (String tabURL) {
        mTabURL = tabURL;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        BaseViewHolder viewHolder = null;
        View itemView = null;

        switch (viewType) {
            case ViewHolderType.BANNER_TYPE_GS_SUPER_ROLL_MENU :
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_gssuper_menu_banner, viewGroup, false);
                viewHolder = new MapCbSldGbaVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_BAN_IMG_GSF_GBA:
            case ViewHolderType.VIEW_TYPE_BAN_IMG_GSF_GBD:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_gssuper_list_title, viewGroup, false);
                viewHolder = new BanImgGsfGbaVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_BAN_IMG_GSF_GBB:
            case ViewHolderType.VIEW_TYPE_BAN_IMG_GSF_GBC:
            case ViewHolderType.VIEW_TYPE_BAN_IMG_GSF_GBE:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_gssuper_title, viewGroup, false);
                viewHolder = new BanImgGsfGbbVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_BAN_GSF_LOC_GBA:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_gssuper_list_location, viewGroup, false);
                viewHolder = new BanGsfLocGbaVH(itemView);
                break;
            case ViewHolderType.VIEW_TYPE_MAP_CX_GBB:
            case ViewHolderType.VIEW_TYPE_MAP_CX_GBB_TAB: //첫번째 MAP_CX_GBB 뷰타입을 해더로 사용할때
                // 단품 쌍.
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_gs_super_banner, viewGroup, false);
                viewHolder = new MapCxGbbVH(itemView, mNavigationID, mTabURL);
                break;

            case ViewHolderType.BANNER_TYPE_GS_SUPER_SLD_GBD :
                // 자동 롤링 이미지
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_gs_super_roll_image_banner, viewGroup, false);
                viewHolder = new BanSldGbdVH(itemView);

                break;

            default:
                return super.onCreateViewHolder(viewGroup, viewType);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder viewHolder, int position) {
        String action = GTMEnum.GTM_NONE;
        String label = GTMEnum.GTM_NONE;

        switch (viewHolder.getItemViewType()) {
            case ViewHolderType.BANNER_TYPE_GS_SUPER_ROLL_MENU : // GS Super
            case ViewHolderType.VIEW_TYPE_BAN_IMG_GSF_GBA:
            case ViewHolderType.VIEW_TYPE_BAN_IMG_GSF_GBD:
            case ViewHolderType.VIEW_TYPE_BAN_IMG_GSF_GBB:
            case ViewHolderType.VIEW_TYPE_BAN_IMG_GSF_GBC:
            case ViewHolderType.VIEW_TYPE_BAN_IMG_GSF_GBE:
            case ViewHolderType.VIEW_TYPE_BAN_GSF_LOC_GBA:
            case ViewHolderType.VIEW_TYPE_MAP_CX_GBB:        // GS Super 단품 쌍
            case ViewHolderType.VIEW_TYPE_MAP_CX_GBB_TAB:
            case ViewHolderType.BANNER_TYPE_GS_SUPER_SLD_GBD :
                viewHolder.onBindViewHolder(mContext, position, mInfo, action, label, null);

            default:
                super.onBindViewHolder(viewHolder, position);
        }
    }
    /*
    @Override
    public int getItemCount() {
        int length = super.getItemCount();
        if (mLength != DEFAULT_LIST_LENGTH)
            length = mLength;
        return length;
    }

    public void setItemCount(int length) {
        if(this.mLength != length) {
            this.mLength = length;
            notifyDataSetChanged();
        }
    }
    */
}
