/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.bestshop;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;

/**
 *
 * 전체 배스트 시간 추가된 텍스트 베너
 */
public class BTs2VH extends BaseViewHolder {


    private final TextView title;
    private final TextView time;
    /**
     * @param itemView itemView
     */
    public BTs2VH(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.text_title);
        time = (TextView) itemView.findViewById(R.id.text_time);

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

        final SectionContentList content = info.contents.get(position).sectionContent;

//        productName, promotionName
        title.setText(content.productName);
        time.setText(content.promotionName);

    }

}
