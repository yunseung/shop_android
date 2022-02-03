/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.nalbang;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ViewHolderType;
import gsshop.mobile.v2.home.shop.bestshop.BestShopAdapter;
import gsshop.mobile.v2.home.shop.flexible.SbtVH;
import gsshop.mobile.v2.home.shop.flexible.SslVH;
import gsshop.mobile.v2.support.gtm.GTMEnum;


/**
 * 날방 어뎁터
 *
 */
public class NalbangShopAdapter extends BestShopAdapter {


    public NalbangShopAdapter(Context context) {
        super(context);
    }


    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case ViewHolderType.BANNER_TYPE_NALBANG_LIVE:
                // tv live banner
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_fx_nalbang_live, viewGroup, false);
                return new NalbangBannerLiveViewHolder(itemView);
            case ViewHolderType.BANNER_TYPE_NALBANG_HASH_TAG_TAB:
                // tv live banner
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_nalbang_shop_hashtab_table_banner, viewGroup, false);
                return new NalbangBannerHashtagTabViewHolder(itemView);
            case ViewHolderType.BANNER_TYPE_SHORTBANG_HASH_TAG_TAB:
                // tv live banner
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_nalbang_shop_hashtab_table_banner, viewGroup, false);
                return new ShortbangBannerCategoryTabViewHolder(itemView);

            case ViewHolderType.BANNER_TYPE_NALBANG_PRD:
                // tv live banner
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_nalbang_shop_prd_banner, viewGroup, false);
                return new NalbangBannerPrdViewHolder(itemView);
            case ViewHolderType.VIEW_TYPE_SBT:
                // tv live banner
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_shortbang_list, viewGroup, false);
                return new SbtVH(itemView);
            case ViewHolderType.VIEW_TYPE_SSL:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_shortbang_roll_banner, viewGroup, false);
                return new SslVH(itemView);

            case ViewHolderType.BANNER_TYPE_SHORTBANG_BANNER:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_fx_shortbang_banner, viewGroup, false);
                return new FlexibleBannerShortbangBannerViewHolder(itemView);
            case ViewHolderType.BANNER_TYPE_SHORTBANG_READ_MORE:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.row_short_bang_read_more, viewGroup, false);
                return new ShortbangBannerReadmoreViewHolder(itemView);
            case ViewHolderType.BANNER_TYPE_NALBANG_READ_MORE:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.row_nal_bang_read_more, viewGroup, false);
                return new NalbangBannerReadmoreViewHolder(itemView);
            case ViewHolderType.BANNER_TYPE_SHORTBANG_EMPTY:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.home_row_type_shortbang_empty, viewGroup, false);
                return new ShortbangBannerEmptyViewHolder(itemView);

            default:
                return super.onCreateViewHolder(viewGroup, viewType);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder viewHolder, int position) {
//        Ln.i("count - position : " + position);
        String action = GTMEnum.GTM_NONE;
        String label = GTMEnum.GTM_NONE;
        String sectionName = GTMEnum.GTM_NONE;
        String productCode = GTMEnum.GTM_NONE;

        switch (viewHolder.getItemViewType()) {
            case ViewHolderType.BANNER_TYPE_NALBANG_LIVE:
                // tv live 배너.
                if (mInfo != null && mInfo.sectionList != null
                        && mInfo.sectionList.sectionName != null && mInfo.tvLiveDealBanner != null) {
                    {
                        action = String.format("%s_%s_%s", GTMEnum.GTM_ACTION_HEADER,
                                mInfo.sectionList.sectionName, GTMEnum.GTM_ACTION_LIVE_TAIL);
                        label = String.format("%s", mInfo.tvLiveDealBanner.promotionName);
                        sectionName = mInfo.sectionList.sectionName;
                    }
                }
                viewHolder.onBindViewHolder(mContext, position, mInfo, action, label, sectionName);
                break;
            case ViewHolderType.BANNER_TYPE_NALBANG_HASH_TAG_TAB:
            case ViewHolderType.BANNER_TYPE_NALBANG_PRD:
            case ViewHolderType.VIEW_TYPE_SBT:
            case ViewHolderType.VIEW_TYPE_SSL:
            case ViewHolderType.BANNER_TYPE_SHORTBANG_HASH_TAG_TAB:
            case ViewHolderType.BANNER_TYPE_SHORTBANG_READ_MORE:
            case ViewHolderType.BANNER_TYPE_NALBANG_READ_MORE:
            case ViewHolderType.BANNER_TYPE_SHORTBANG_EMPTY:
            case ViewHolderType.BANNER_TYPE_SHORTBANG_BANNER:



                // 단품 기본형.
                if (mInfo != null && mInfo.sectionList != null
                        && mInfo.sectionList.sectionName != null) {

                    if (mInfo.contents != null && mInfo.contents.get(position) != null && mInfo.contents.get(position).sectionContent != null) {
                        SectionContentList temp = mInfo.contents.get(position).sectionContent;
                        if (!TextUtils.isEmpty(temp.dealNo) && !"0".equals(temp.dealNo)) {
                            productCode = temp.dealNo;
                        } else if (!TextUtils.isEmpty(temp.prdid) && !"0".equals(temp.prdid)) {
                            productCode = temp.prdid;
                        }
                        if (GTMEnum.GTM_NONE.equals(sectionName)) {
                            sectionName = mInfo.sectionList.sectionName;
                        }
                        action = String.format("%s_%s_%s", GTMEnum.GTM_ACTION_HEADER, sectionName,
                                productCode);
                        label = String.format("%s_%s", Integer.toString(position), temp.productName);
                    }
                }
                viewHolder.onBindViewHolder(mContext, position, mInfo, action, label, sectionName);
                break;
            default:
                super.onBindViewHolder(viewHolder, position);
        }
    }

    @Override
    public int getItemCount() {
//        Ln.i("count : " + super.getItemCount());
        return super.getItemCount();
    }
}
