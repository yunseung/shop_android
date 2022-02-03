/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.bestdeal;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ViewHolderType;
import gsshop.mobile.v2.home.shop.bestshop.BestShopTVLiveViewHolder;
import gsshop.mobile.v2.home.shop.flexible.FlexibleShopAdapter;
import gsshop.mobile.v2.home.shop.renewal.flexible.RenewalBestDealLiveViewHolder;
import gsshop.mobile.v2.home.shop.renewal.flexible.RenewalBestDealMobileLiveViewHolder;
import gsshop.mobile.v2.support.gtm.GTMEnum;


public class BestDealShopAdapter extends FlexibleShopAdapter {
    public BestDealShopAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = null;
        switch (viewType) {
            case ViewHolderType.BANNER_TYPE_HOME_TV_LIVE:
            case ViewHolderType.BANNER_TYPE_HOME_TV_DATA:
                itemView = LayoutInflater.from(viewGroup.getContext())
                            .inflate(R.layout.renewal_home_fx_best_deal_live, viewGroup, false);

                return new RenewalBestDealLiveViewHolder(itemView);
            case ViewHolderType.BANNER_TYPE_MOBILE_LIVE:
                    itemView = LayoutInflater.from(viewGroup.getContext())
                            .inflate(R.layout.renewal_home_fx_best_deal_mobile_live, viewGroup, false);

                return new RenewalBestDealMobileLiveViewHolder(itemView);
            case ViewHolderType.BANNER_TYPE_TV_LIVE:
                // tv live banner
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_fx_tv_live, viewGroup, false);
                return new BestShopTVLiveViewHolder(itemView);

            case ViewHolderType.VIEW_TYPE_BAN_IMG_C5_GBA:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_service_shop_link, viewGroup, false);
                return new BanImgC5GbaVH(itemView, mInfo.naviId);
            default:
                return super.onCreateViewHolder(viewGroup, viewType);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder viewHolder, int position) {
        String action = GTMEnum.GTM_NONE;
        String label = GTMEnum.GTM_NONE;
        // String tab = GTMEnum.GTM_NONE;
        String sectionName = GTMEnum.GTM_NONE;
        String productCode = GTMEnum.GTM_NONE;

        switch (viewHolder.getItemViewType()) {
            case ViewHolderType.BANNER_TYPE_TV_LIVE_A:
                //dual live 배너.
                if (mInfo != null && mInfo.sectionList != null
                        && mInfo.sectionList.sectionName != null && mInfo.tvLiveBanner != null) {
                    {
                        action = String.format("%s_%s_%s", GTMEnum.GTM_ACTION_HEADER,
                                mInfo.sectionList.sectionName, GTMEnum.GTM_ACTION_LIVE_TAIL);
                        label = String.format("%s", mInfo.tvLiveBanner.productName);
                        sectionName = mInfo.sectionList.sectionName;
                    }
                }
                viewHolder.onBindViewHolder(mContext, position,
                        mInfo, action, null, sectionName);
                break;
            case ViewHolderType.BANNER_TYPE_TV_LIVE_B:
            case ViewHolderType.BANNER_TYPE_HOME_TV_LIVE:
            case ViewHolderType.BANNER_TYPE_HOME_TV_DATA:
                //dual live 배너.
                if (mInfo != null && mInfo.sectionList != null
                        && mInfo.sectionList.sectionName != null && mInfo.tvLiveBanner != null) {
                    {
                        action = String.format("%s_%s_%s", GTMEnum.GTM_ACTION_HEADER,
                                mInfo.sectionList.sectionName, GTMEnum.GTM_ACTION_LIVE_TAIL);
                        label = String.format("%s", mInfo.tvLiveBanner.productName);
                        sectionName = mInfo.sectionList.sectionName;
                    }
                }
                viewHolder.onBindViewHolder(mContext, position,
                        mInfo, action, null, sectionName);
                break;
            case ViewHolderType.BANNER_TYPE_MOBILE_LIVE:
                //mobile live 배너.
                if (mInfo != null && mInfo.sectionList != null
                        && mInfo.sectionList.sectionName != null && mInfo.tvLiveBanner != null) {
                    {
                        action = String.format("%s_%s_%s", GTMEnum.GTM_ACTION_HEADER,
                                mInfo.sectionList.sectionName, GTMEnum.GTM_ACTION_LIVE_TAIL);
                        label = String.format("%s", mInfo.mobileLiveBanner.productName);
                        sectionName = mInfo.sectionList.sectionName;
                    }
                }
                viewHolder.onBindViewHolder(mContext, position,
                        mInfo, action, null, sectionName);
                break;
            case ViewHolderType.VIEW_TYPE_ML:
                // 단품 비디오.
                if (mInfo != null && mInfo.sectionList != null
                        && mInfo.sectionList.sectionName != null) {

                    if (mInfo.contents != null && mInfo.contents.get(position) != null) {
                        SectionContentList temp = mInfo.contents.get(position).sectionContent;
                        if (!TextUtils.isEmpty(temp.dealNo) && !"0".equals(temp.dealNo)) {
                            productCode = temp.dealNo;
                        } else if (!TextUtils.isEmpty(temp.prdid) && !"0".equals(temp.prdid)) {
                            productCode = temp.prdid;
                        }

                        // 서버 매장에 대한 처리
                        if (mInfo.sectionList.subMenuList != null
                                && !mInfo.sectionList.subMenuList.isEmpty()) {
                            if (mInfo.sectionList.subMenuList.get(mInfo.tabIndex) != null
                                    && mInfo.sectionList.subMenuList
                                    .get(mInfo.tabIndex).sectionName != null) {
                                sectionName = String.format("%s_%s", mInfo.sectionList.sectionName,
                                        mInfo.sectionList.subMenuList.get(mInfo.tabIndex).sectionName);
                            }
                        }
                        if (GTMEnum.GTM_NONE.equals(sectionName)) {
                            sectionName = mInfo.sectionList.sectionName;
                        }
                        action = String.format("%s_%s_%s", GTMEnum.GTM_ACTION_HEADER, sectionName,
                                productCode);
                        label = String.format("%s_%s", Integer.toString(position), temp.productName);
                    }
                }
                (viewHolder).onBindViewHolder(mContext, position, mInfo,
                        action, label, null);
                break;
            case ViewHolderType.VIEW_TYPE_BAN_IMG_C5_GBA:
                if (mInfo != null && mInfo.sectionList != null
                        && mInfo.sectionList.sectionName != null && mInfo.tvLiveBanner != null) {
                    {
                        action = String.format("%s_%s_%s", GTMEnum.GTM_ACTION_HEADER,
                                mInfo.sectionList.sectionName, GTMEnum.GTM_ACTION_LIVE_TAIL);
                        
                        sectionName = mInfo.sectionList.sectionName;
                    }
                }
                viewHolder.onBindViewHolder(mContext, position,
                        mInfo, action, null, sectionName);
                break;
            default:
                super.onBindViewHolder(viewHolder, position);
        }


    }
}
