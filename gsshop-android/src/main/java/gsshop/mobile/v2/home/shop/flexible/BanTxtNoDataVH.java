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
import android.widget.TextView;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;

/**
 * 기본형 단품 item
 */
public class BanTxtNoDataVH extends BaseViewHolder {

    private final TextView empty_text;
    /**
     * @param itemView
     */
    public BanTxtNoDataVH(View itemView) {
        super(itemView);
        empty_text = (TextView) itemView.findViewById(R.id.empty_text);
    }

    /*
     * bind
     */
    @Override
    public void onBindViewHolder(Context context, int position, ShopInfo info, String action, String label, String sectionName) {
        final SectionContentList item = info.contents.get(position).sectionContent;
        if(item.productName == null || item.productName.equals("")){
            empty_text.setText("오늘은\n새로 오픈한 딜이 없습니다.");
        }else{
            empty_text.setText("오늘은 '"+item.productName + "'에\n새로 오픈한 딜이 없습니다.");
        }

    }
}
