/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.renewal.flexible;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.renewal.views.CustomTitleDTLayout;

/**
 * PRD_C_CST_SQ 뷰타입의 뷰홀더
 */
@SuppressLint("NewApi")
public class PrdCCstSqVH extends PrdCSqVH {
    private CustomTitleDTLayout mCustomTitleDTLayout;
    private LinearLayout mLayoutRefresh;

    public PrdCCstSqVH(View itemView, String naviId) {
        super(itemView, naviId);
        mCustomTitleDTLayout = itemView.findViewById(R.id.custom_title_dt_layout);
        mLayoutRefresh = itemView.findViewById(R.id.layout_stream_refresh);
    }

    @Override
    protected void setView(Context context, SectionContentList item, String action, String label, int type) {
        super.setView(context, item, action, label, type);
        mCustomTitleDTLayout.setTitle(context, item);
        mLayoutRefresh.setVisibility(View.GONE);
    }
}
