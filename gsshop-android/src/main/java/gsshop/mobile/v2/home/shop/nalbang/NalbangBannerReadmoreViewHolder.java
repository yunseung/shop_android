/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.nalbang;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.gsshop.mocha.core.exception.ApplyExceptionHandler;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;


/**
 *
 *
 */
public class NalbangBannerReadmoreViewHolder extends BaseViewHolder {

    private final LinearLayout read_more_layout;

    /**
     * @param itemView
     */
    public NalbangBannerReadmoreViewHolder(View itemView) {
        super(itemView);
        read_more_layout = (LinearLayout) itemView.findViewById(R.id.read_more_layout);
    }

    /**
     * @param context
     * @param position
     * @param info
     * @param action
     * @param label
     * @param sectionName
     */
    @Override
    public void onBindViewHolder(final Context context, final int position, final ShopInfo info,
                                 final String action, final String label, String sectionName) {

        if(info.contents.get(position).sectionContent.isShow){
            read_more_layout.setVisibility(View.VISIBLE);
            read_more_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new Events.FlexibleEvent.NalbangReadmore());
                }
            });
        }else{
            read_more_layout.setVisibility(View.GONE);
        }



    }
}
