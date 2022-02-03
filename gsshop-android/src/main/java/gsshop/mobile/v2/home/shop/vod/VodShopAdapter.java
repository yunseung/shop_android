/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.vod;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gsshop.mocha.core.exception.ApplyExceptionHandler;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ViewHolderType;
import gsshop.mobile.v2.home.shop.flexible.FlexibleShopAdapter;
import gsshop.mobile.v2.home.shop.renewal.flexible.BrdVodVH;
import gsshop.mobile.v2.support.gtm.GTMEnum;

/**
 * vod 매장.
 *
 */
public class VodShopAdapter extends FlexibleShopAdapter {

    public VodShopAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = null;
        BaseViewHolder viewHolder = null;
        switch (viewType) {

            case ViewHolderType.VIEW_TYPE_BRD_VOD:
                // renewal vod
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.renewal_recycler_item_vod, viewGroup, false);
                return new BrdVodVH(itemView);
            case ViewHolderType.VIEW_TYPE_BAN_VOD_GBA:
                // vod landscape
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.recycler_item_vod_land, viewGroup, false);
               return new BanVodGbaVH(itemView);
            case ViewHolderType.VIEW_TYPE_BAN_VOD_GBB:
                // vod portrait
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.recycler_item_vod_port, viewGroup, false);
                return new BanVodGbbVH(itemView);
            case ViewHolderType.VIEW_TYPE_BAN_VOD_GBC:
                // vod portrait
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.recycler_item_vod_square, viewGroup, false);
                return new BanVodGbcVH(itemView);

            case ViewHolderType.VIEW_TYPE_BAN_ORD_GBA_SPACE:
                // 내일TV 타이틀 영역 아래 여백
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.recycler_item_vod_space, viewGroup, false);
                return new BanOrdGbaSpaceVH(itemView);
            case ViewHolderType.VIEW_TYPE_MAP_CX_TXT_GBB:
                // 내일 tv 화면에 할인 카드 정보 뷰타입 추가.
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.recycler_item_card_view, viewGroup, false);
                return new MapCxTxtGbbVH(itemView);

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
            case ViewHolderType.VIEW_TYPE_BRD_VOD:

            case ViewHolderType.VIEW_TYPE_BAN_VOD_GBA:
            case ViewHolderType.VIEW_TYPE_BAN_VOD_GBB:
            case ViewHolderType.VIEW_TYPE_BAN_VOD_GBC:
            case ViewHolderType.VIEW_TYPE_MAP_CX_TXT_GBB:

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

            default:
                super.onBindViewHolder(viewHolder, position);
        }


    }
}
