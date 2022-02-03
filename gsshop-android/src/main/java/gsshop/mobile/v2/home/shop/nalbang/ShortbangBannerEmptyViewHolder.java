/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.nalbang;

import android.content.Context;
import android.view.View;

import com.gsshop.mocha.core.exception.ApplyExceptionHandler;

import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;


/**
 *
 *
 */
public class ShortbangBannerEmptyViewHolder extends BaseViewHolder {


    /**
     * @param itemView
     */
    public ShortbangBannerEmptyViewHolder(View itemView) {
        super(itemView);
    }

    /**
     * @param context
     * @param position
     * @param info
     * @param action
     * @param label
     * @param sectionName
     */
    @Override
    public void onBindViewHolder(final Context context, final int position, final ShopInfo info,
                                 final String action, final String label, String sectionName) {


    }
}
