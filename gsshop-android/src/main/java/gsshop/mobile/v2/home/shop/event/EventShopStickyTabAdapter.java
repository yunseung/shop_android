/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.event;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.flexible.FlexibleShopAdapter;

/**
 *
 *
 */
public class EventShopStickyTabAdapter extends FlexibleShopAdapter
        implements StickyRecyclerHeadersAdapter<EventBannerEventTabViewHolder> {

    private int categoryPosition = -1;
    private SectionContentList headerData;

    /**
     * @param context
     */
    public EventShopStickyTabAdapter(Context context) {
        super(context);

    }

    public void setHeaderData(SectionContentList headerData, int categoryPosition) {
        this.headerData = headerData;
        this.categoryPosition = categoryPosition;
    }

    public SectionContentList getHeaderData() {
        return headerData;
    }

    public int getCategoryPosition() {
        return categoryPosition;
    }

    @Override
    public long getHeaderId(int position) {
        if (position < categoryPosition) return -1;
        else return 0;
    }

    @Override
    public EventBannerEventTabViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        // Event Menu banner
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_row_type_fx_event_tabs_banner, parent, false);
        return new EventBannerEventTabViewHolder(itemView);

    }

    @Override
    public void onBindHeaderViewHolder(EventBannerEventTabViewHolder holder, int position) {
        if (this.headerData != null) {
            holder.onBindViewHolder(this.headerData.subProductList, getInfo());
        }
    }

}
