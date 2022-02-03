/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.tvshoping;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gsshop.mocha.core.exception.ApplyExceptionHandler;

import java.util.ArrayList;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.ViewHolderType;
import gsshop.mobile.v2.home.shop.flexible.TvTcfVH;
import gsshop.mobile.v2.home.shop.flexible.FlexibleShopAdapter;
import gsshop.mobile.v2.support.gtm.GTMEnum;

/**
 *
 *
 */
public class TvShopAdapter extends FlexibleShopAdapter {

    private ArrayList<ShopInfo> externalCategoryItem;

//    private int currentCategory = 0;

    public TvShopAdapter(Context context) {
        super(context);
//        externalCategoryItem = new ArrayList<ShopInfo>();
    }

    @Override
    public void onBindViewHolder(BaseViewHolder viewHolder, int position) {
        String action = GTMEnum.GTM_NONE;
        String label = GTMEnum.GTM_NONE;
        // String tab = GTMEnum.GTM_NONE;
        String sectionName = GTMEnum.GTM_NONE;
        String productCode = GTMEnum.GTM_NONE;

        switch (viewHolder.getItemViewType()) {
            case ViewHolderType.VIEW_TYPE_TCF:
                // event item banner
                if (mInfo != null && mInfo.sectionList != null
                        && mInfo.sectionList.sectionName != null
                        && mInfo.contents.get(position) != null) {
                    action = String.format("%s_%s_%s", GTMEnum.GTM_ACTION_HEADER,
                            mInfo.sectionList.sectionName, mInfo.contents.get(position).sectionContent.viewType);
                    label = String.format("%s", Integer.toString(position));
                }
                ((TvTcfVH) viewHolder).onBindViewHolder(mContext, position,
                        mInfo, action, label, null);
                break;
            case ViewHolderType.BANNER_TYPE_TV_LIVE_DUAL:
                // tv live 배너.
                if (mInfo != null && mInfo.sectionList != null
                        && mInfo.sectionList.sectionName != null && mInfo.tvLiveBanner != null) {
                    // 베스트딜에 있는 Data 구조는 플렉서블 하지 않다. View 타입이 존재 하지 않는다.
                    // if( mInfo.contents.get(position) != null &&
                    // mInfo.contents.get(position).sectionContent != null )
                    {
                        action = String.format("%s_%s_%s", GTMEnum.GTM_ACTION_HEADER,
                                mInfo.sectionList.sectionName, GTMEnum.GTM_ACTION_LIVE_TAIL);
                        label = String.format("%s", mInfo.tvLiveBanner.productName);
                        sectionName = mInfo.sectionList.sectionName;
                    }
                }
                ((TVLiveBannerTVLiveViewHolder) viewHolder).onBindViewHolder(mContext, position,
                        mInfo, action, null, sectionName);
                break;
            default:
                super.onBindViewHolder(viewHolder, position);
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case ViewHolderType.BANNER_TYPE_TV_LIVE_DUAL:
                // tv live banner
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_fx_tv_live_talk, viewGroup, false);
                return new TVLiveBannerTVLiveViewHolder(itemView);
            case ViewHolderType.VIEW_TYPE_TCF:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_tv_shop_category_banner, viewGroup, false);
                return new TvTcfVH(itemView);
            default:
                return super.onCreateViewHolder(viewGroup, viewType);
        }
    }
}
