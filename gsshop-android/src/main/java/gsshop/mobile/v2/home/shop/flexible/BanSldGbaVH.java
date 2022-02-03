/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.util.DisplayUtils;


/**
 * todays hot 배너.
 */
@SuppressLint("NewApi")
public class BanSldGbaVH extends BanSldGbbVH {


    /**
     * @param itemView
     */
    public BanSldGbaVH(View itemView) {
        super(itemView);
    }

    @Override
    public void onBindViewHolder(Context context, int position, ShopInfo info, String action, String label, String sectionName) {
        int bannerHeight = DisplayUtils.getScreenWidth() / 2;
        setBannerHeight(bannerHeight);

        super.onBindViewHolder(context, position, info, action, label, sectionName);
    }
}
