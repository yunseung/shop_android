/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.department;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.bestshop.BestShopBannerPrdViewHolder;

/**
 *
 *
 */
public class BfpVH extends BestShopBannerPrdViewHolder {

    private ImageView[] image_badge = new ImageView[2];
    /**
     * @param itemView itemView
     */
    public BfpVH(View itemView) {
        super(itemView);
        image_badge[0] = (ImageView)itemView.findViewById(R.id.content_frame_1st).findViewById(R.id.image_badge);
        image_badge[1] = (ImageView)itemView.findViewById(R.id.content_frame_2nd).findViewById(R.id.image_badge);
    }

    @Override
    public void onBindViewHolder(final Context context, final int position, final ShopInfo info,
                                 final String action, final String label, String sectionName) {
        super.onBindViewHolder(context,position,info,action,label,sectionName);

        ArrayList<SectionContentList> subProductList = info.contents.get(position).sectionContent.subProductList;

        if(subProductList != null && !subProductList.isEmpty()){
            if("Deal".equals(subProductList.get(0).dealProductType)){
                image_badge[0].setVisibility(View.VISIBLE);
            }else{
                image_badge[0].setVisibility(View.GONE);
            }
        }

        if(subProductList != null && subProductList.size() > 1){
            if("Deal".equals(subProductList.get(1).dealProductType)){
                image_badge[1].setVisibility(View.VISIBLE);
            }else{
                image_badge[1].setVisibility(View.GONE);
            }
        }


    }

}
