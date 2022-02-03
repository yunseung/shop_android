/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.department;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.bestshop.BestShopBannerCategoryViewHolder;

/**
 * 카테고리.
 */
@SuppressLint("NewApi")
public class FpcSVH extends BestShopBannerCategoryViewHolder {

    private TextView program_text;

    public FpcSVH(View itemView) {
        super(itemView);
        currentIndex = 0;
    }

    @Override
    public void onBindViewHolder(final Context context, final int position, final ShopInfo info,
                                 final String action, final String label, String sectionName) {
        currentIndex = info.tabIndex;
        super.onBindViewHolder(context, position, info, action, label, sectionName);
    }

    @Override
    public void initCategory(Context context, ArrayList<SectionContentList> categories, String selectedName) {
        super.initCategory(context, categories, selectedName);
    }

    public void setIndex(int index){
        currentIndex = index;
    }

}
