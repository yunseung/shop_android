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
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.bestshop.TvBannerCategoryViewHolder;

/**
 * 카테고리.
 */
@SuppressLint("NewApi")
public class TvTcfVH extends TvBannerCategoryViewHolder {

    private TextView program_text;

    public TvTcfVH(View itemView) {
        super(itemView);
        program_text = (TextView) itemView.findViewById(R.id.program_text);
        currentIndex = 0;
        LinearLayout root = (LinearLayout)itemView.findViewById(R.id.root);

        root.setPadding(MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.vh_blank_space_middle), 0,
                MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.vh_blank_space_middle),
                MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.vh_blank_space_middle));

        EventBus.getDefault().register(this);
    }

    @Override
    public void onBindViewHolder(final Context context, final int position, final ShopInfo info,
                                 final String action, final String label, String sectionName) {

        super.onBindViewHolder(context, position, info, action, label, sectionName);

        if(info.contents.get(position).sectionContent != null && info.contents.get(position).sectionContent.productName != null && !"".equals(info.contents.get(position).sectionContent.productName)) {
            program_text.setText(info.contents.get(position).sectionContent.productName);
        }else{
            program_text.setText(R.string.home_tv_live_recommend);
        }
    }

    @Override
    public void initCatagory(Context context, ArrayList<SectionContentList> categories, String selectedName) {
        super.initCatagory(context, categories, selectedName);
        program_text.setVisibility(View.VISIBLE);
    }

    public void onEvent(Events.FlexibleEvent.ResetItemEvent event) {
        currentIndex = 0;
    }

}
