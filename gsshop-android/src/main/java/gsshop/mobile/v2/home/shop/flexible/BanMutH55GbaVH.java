/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.gsshop.mocha.ui.util.ViewUtils;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.web.WebUtils;

/**
 * 타이틀 배너.
 */
public class BanMutH55GbaVH extends BaseViewHolder {

    private final TextView title;
    private final View arrow;

    /**
     * @param itemView
     */
    public BanMutH55GbaVH(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.text_ai_title);
        arrow = itemView.findViewById(R.id.view_ai_arrow);
    }

    /* bind */
    @Override
    public void onBindViewHolder(final Context context, int position, ShopInfo info, String action, String label, String sectionName) {
        final SectionContentList item = info.contents.get(position).sectionContent;
        if(TextUtils.isEmpty(item.linkUrl)) {
            ViewUtils.hideViews(arrow);
        } else {
            ViewUtils.showViews(arrow);
        }

        title.setText(item.productName);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(item.linkUrl)) {
                    WebUtils.goWeb(context, item.linkUrl);
                }
            }
        });
    }

}
