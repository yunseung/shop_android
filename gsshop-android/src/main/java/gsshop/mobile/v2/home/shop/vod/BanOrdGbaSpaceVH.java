/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.vod;

import android.content.Context;
import android.view.View;

import com.gsshop.mocha.core.exception.ApplyExceptionHandler;

import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;

/**
 * 여백
 */
public class BanOrdGbaSpaceVH extends BaseViewHolder {

    /**
     * @param itemView itemView
     */
    public BanOrdGbaSpaceVH(View itemView) {
        super(itemView);
    }

    /**
     * @param context context
     * @param position position
     * @param info info
     * @param action action
     * @param label label
     * @param sectionName sectionName
     */
    @Override
    public void onBindViewHolder(final Context context, final int position, final ShopInfo info,
                                 final String action, final String label, String sectionName) {
    }
}
