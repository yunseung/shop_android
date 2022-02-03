/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible;

import android.content.Context;
import android.view.View;

import com.gsshop.mocha.core.exception.ApplyExceptionHandler;

import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.util.DisplayUtils;

/**
 * 기본형 단품 item
 */
public class BanImgSquareGbaVH extends LVH {

    /**
     * @param itemView
     */
    public BanImgSquareGbaVH(View itemView, FlexibleShopAdapter adapter, String navigationId) {
        super(itemView, adapter, navigationId);
        if (mainImg != null) {
            DisplayUtils.resizeWidthAtViewToScreenSize(MainApplication.getAppContext(), mainImg);
        }
    }

    /*
     * bind
     */
    @Override
    public void onBindViewHolder(Context context, int position, ShopInfo info, String action, String label, String sectionName) {
        super.onBindViewHolder(context, position, info, action, label, sectionName);
    }
}
