/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.event;

import android.graphics.Rect;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.ShopInfo;

import static com.blankj.utilcode.util.ConvertUtils.dp2px;

/**
 *
 *
 */
public class EventBannerEventTabViewHolder  extends RecyclerView.ViewHolder {

    private final RecyclerView recyclerView;
    private final AtomicBoolean isAddedItemDecoration = new AtomicBoolean(false);

    /**
     * @param itemView
     */
    public EventBannerEventTabViewHolder(View itemView) {
        super(itemView);
        recyclerView = (RecyclerView) itemView.findViewById(R.id.recycler_event_tabs);
        if (isAddedItemDecoration.compareAndSet(false, true)) {
            recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    super.getItemOffsets(outRect, view, parent, state);
                    outRect.left = dp2px(0.5f);
                    outRect.right = dp2px(0.5f);
                    final int position = parent.getChildAdapterPosition(view);
                    if (position == 0) {
                        outRect.left = 0;
                    } else if (position == parent.getAdapter().getItemCount() - 1) {
                        outRect.right = 0;
                    }

                }
            });
        }
    }


    public void onBindViewHolder(List<SectionContentList> tabs, ShopInfo info) {
        recyclerView.setLayoutManager(new GridLayoutManager(itemView.getContext(), tabs.size()));
        recyclerView.setAdapter(new TabAdapter(tabs, info));
    }

    private static class TabAdapter extends RecyclerView.Adapter<TabViewHolder> {
        private final List<SectionContentList> tabs;
        private final ShopInfo info;


        public TabAdapter(List<SectionContentList> tabs, ShopInfo info) {
            this.tabs = tabs;
            this.info = info;
        }

        @Override
        public TabViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new TabViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.home_row_type_fx_event_tab, parent, false));
        }

        @Override
        public void onBindViewHolder(TabViewHolder holder, int position) {
            TextView tab = (TextView) holder.itemView;
            tab.setText(tabs.get(position).productName);
            String strConDescription = tabs.get(position).productName;
            if (TextUtils.isEmpty(strConDescription)) {
                strConDescription = tabs.get(position).gsAccessibilityVariable;
            }
            if (!TextUtils.isEmpty(strConDescription)) {
                tab.setContentDescription(strConDescription);
            }
            tab.setSelected(info.tabIndex == position ? true : false);
            tab.setTextAppearance(holder.itemView.getContext(),
                    info.tabIndex == position ? R.style.EventTabOn : R.style.EventTabOff);
        }

        @Override
        public int getItemCount() {
            return tabs == null ? 0 : tabs.size();
        }
    }

    private static class TabViewHolder extends RecyclerView.ViewHolder {
        public TabViewHolder(View itemView) {
            super(itemView);
        }
    }

}
