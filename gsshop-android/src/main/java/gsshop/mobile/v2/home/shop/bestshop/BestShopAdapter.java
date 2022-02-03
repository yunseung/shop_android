/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.bestshop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gsshop.mocha.core.exception.ApplyExceptionHandler;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ViewHolderType;
import gsshop.mobile.v2.home.shop.department.BfpVH;
import gsshop.mobile.v2.home.shop.department.BItVH;
import gsshop.mobile.v2.home.shop.flexible.FlexibleShopAdapter;
import gsshop.mobile.v2.support.gtm.GTMEnum;

/**
 *
 *
 */
public class BestShopAdapter extends FlexibleShopAdapter {


    public BestShopAdapter(Context context) {
        super(context);
    }




    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case ViewHolderType.VIEW_TYPE_FPC_S:
                // 카테고리.
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_best_shop_category_banner_new, viewGroup, false);
                return new BestShopFpcSVH(itemView);
            case ViewHolderType.VIEW_TYPE_B_TS2:
                // 타이틀
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_best_shop_title_banner, viewGroup, false);
                return new BTs2VH(itemView);
            case ViewHolderType.VIEW_TYPE_RPS:
                // 인기 검색어.
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_best_shop_populate_keyword_banner, viewGroup, false);
                return new RpsVH(itemView);
            case ViewHolderType.VIEW_TYPE_BFP:
                // 단품 쌍.
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_department_store_prd_banner, viewGroup, false);
                return new BfpVH(itemView);
            case ViewHolderType.VIEW_TYPE_BAN_IMG_C2_GBB:
                // 단품 쌍.
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_best_shop_prd_banner, viewGroup, false);
                return new BanImgC2GbbVH(itemView);
            case ViewHolderType.VIEW_TYPE_MAP_SLD_C3_GBA:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_plan_shop_big_brand_banner, viewGroup, false);
                return new MapSldC3GbaVH(itemView);
            case ViewHolderType.VIEW_TYPE_MAP_CX_GBA:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_gs_x_brand_banner, viewGroup, false);
                return new MapCxGbaVH(itemView);
            case ViewHolderType.VIEW_TYPE_BP_O:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_plan_shop_theme_banner, viewGroup, false);
                return new BpOVH(itemView);
            case ViewHolderType.VIEW_TYPE_TP_S:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_plan_shop_mobile_banner, viewGroup, false);
                return new TpSVH(itemView);
            case ViewHolderType.VIEW_TYPE_TP_SA:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_plan_shop_e_mobile_banner, viewGroup, false);
                return new TpSVH(itemView);
            case ViewHolderType.VIEW_TYPE_B_IT:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_department_store_special, viewGroup, false);
                return new BItVH(itemView);
            case ViewHolderType.BAN_CX_SLD_CATE_GBA:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.recycler_item_gs_x_brand_category, viewGroup, false);
                return new BanCxSldCateGbaVH(itemView);
            case ViewHolderType.VIEW_TYPE_BAN_TXT_CHK_GBA:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_best_now, viewGroup, false);
                return new ChkGbaVH(itemView);
            case ViewHolderType.VIEW_TYPE_GR_PMO_T2_MORE:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_gr_pmo_t2_more, viewGroup, false);
                return new GrPmoT2MoreVH(itemView);
            case ViewHolderType.VIEW_TYPE_PMO_T2_A:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_holder_pmo_t2_a, viewGroup, false);
                return new PmoT2AVH(itemView);
            default:
                return super.onCreateViewHolder(viewGroup, viewType);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder viewHolder, int position) {
        String action = GTMEnum.GTM_NONE;
        String label = GTMEnum.GTM_NONE;

        switch (viewHolder.getItemViewType()) {
            case ViewHolderType.VIEW_TYPE_FPC_S:         // 카테고리.
            case ViewHolderType.VIEW_TYPE_B_TS2:     // 타이틀
            case ViewHolderType.VIEW_TYPE_RPS: // 인기 검색어.
            case ViewHolderType.VIEW_TYPE_BFP:
            case ViewHolderType.VIEW_TYPE_BAN_IMG_C2_GBB:              // 단품 쌍
            case ViewHolderType.VIEW_TYPE_MAP_SLD_C3_GBA: //빅브랜드 기획전.
            case ViewHolderType.VIEW_TYPE_BP_O:     // 기획전 2 - 주차별 테마에 따른 기획전 (배너 + 상품1개 조합)
            case ViewHolderType.VIEW_TYPE_TP_S:    // 모바일 전용 기획전
            case ViewHolderType.VIEW_TYPE_TP_SA:    // 플렉서블 E 모바일 전용 기획전
            case ViewHolderType.VIEW_TYPE_B_IT:    // 플렉서블 E
            case ViewHolderType.VIEW_TYPE_MAP_CX_GBA:                 // gs x brand
            case ViewHolderType.BAN_CX_SLD_CATE_GBA:                 // gs x brand category
            case ViewHolderType.VIEW_TYPE_BAN_TXT_CHK_GBA:           // 지금 베스트 체크박스 배너
            case ViewHolderType.VIEW_TYPE_PMO_T2_A:
            case ViewHolderType.VIEW_TYPE_GR_PMO_T2_MORE:
                viewHolder.onBindViewHolder(mContext, position, mInfo, action, label, null);
                break;

            default:
                super.onBindViewHolder(viewHolder, position);
        }
    }

}
