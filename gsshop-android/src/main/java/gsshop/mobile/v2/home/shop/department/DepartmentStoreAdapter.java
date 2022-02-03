/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.department;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gsshop.mocha.core.exception.ApplyExceptionHandler;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ViewHolderType;
import gsshop.mobile.v2.home.shop.bestshop.BestShopAdapter;
import gsshop.mobile.v2.home.shop.bestshop.BTs2VH;
import gsshop.mobile.v2.home.shop.flexible.BTscVH;
import gsshop.mobile.v2.support.gtm.GTMEnum;

/**
 *
 *
 */
public class DepartmentStoreAdapter extends BestShopAdapter {

    public DepartmentStoreAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView;
        switch (viewType) {

            case ViewHolderType.VIEW_TYPE_FPC_S:
                // 카테고리.
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_best_shop_category_banner, viewGroup, false);
                return new FpcSVH(itemView);
            case ViewHolderType.VIEW_TYPE_B_TS2:
                // 타이틀
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_department_store_title_banner, viewGroup, false);
                return new BTs2VH(itemView);
            case ViewHolderType.VIEW_TYPE_B_TSC:
                // 타이틀 배너.
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_mart_department_store_title, viewGroup, false);
                return new BTscVH(itemView);

            case ViewHolderType.VIEW_TYPE_B_IT:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_department_store_special, viewGroup, false);
                return new BItVH(itemView);

            default:
                return super.onCreateViewHolder(viewGroup, viewType);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder viewHolder, int position) {
        String action = GTMEnum.GTM_NONE;
        String label = GTMEnum.GTM_NONE;

        switch (viewHolder.getItemViewType()) {
            case ViewHolderType.VIEW_TYPE_B_TS2:
            case ViewHolderType.VIEW_TYPE_B_TSC:
            case ViewHolderType.VIEW_TYPE_B_IT:
                viewHolder.onBindViewHolder(mContext, position, mInfo, action, label, null);
                break;
            default:
                super.onBindViewHolder(viewHolder, position);
        }
    }

}
