/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.bestshop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.AbstractBaseActivity;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.support.ui.BugFixedStaggeredGridLayoutManager;
import gsshop.mobile.v2.support.ui.RecyclerItemClickListener;
import gsshop.mobile.v2.util.ClickUtils;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.ConvertUtils.dp2px;
import static com.blankj.utilcode.util.EmptyUtils.isEmpty;

/**
 * GS X Brand 카테고리 배너
 */
@SuppressLint("NewApi")
public class BanCxSldCateGbaVH extends BaseViewHolder {

    //    private int selectedPosition = 0;
    private final RecyclerView recyclerView;
    private final BugFixedStaggeredGridLayoutManager layoutManager;

    private final AtomicBoolean isAddedItemDecoration = new AtomicBoolean(false);

    // 그냥 포지션을 가지고 있으니 해당 홀더를 사용곳이 많아짐에 따라 간섭이 생기는지 position 값이 변경된다.
    // 해당 홀더를 사용하는 곳 마다 navigation ID 를 가지게 있게끔 설정 한다. (Navi ID가 key, 현재 순서를 담는다.)
    protected static Map<String, Integer> mMapSelectedPosition = new HashMap<>();

    // 개인화 탭 선택시 효율 코드 호출
    private static final String GSX_BRAND_PERSONAL = "419124";
    /**
     * @param itemView itemView
     */
    public BanCxSldCateGbaVH(View itemView) {
        super(itemView);
        recyclerView = (RecyclerView) itemView.findViewById(R.id.recycler_gs_x_brand_category);
        this.layoutManager = new BugFixedStaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        if (isAddedItemDecoration.compareAndSet(false, true)) {

            try {
                for (int i = 0; i < recyclerView.getItemDecorationCount(); i++) {
                    recyclerView.removeItemDecorationAt(i);
                }
            }
            catch (IndexOutOfBoundsException e) {
                Ln.e(e.getMessage());
            }

            recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    super.getItemOffsets(outRect, view, parent, state);
                    outRect.left = dp2px(12.5f);
                    outRect.right = dp2px(12.5f);
                }
            });
        }
    }

    public int getSelectedPosition(String navigationID) {
        return mMapSelectedPosition.get(navigationID);
    }

    public void setSelectedPosition(String navigatioinID, int position) {
        mMapSelectedPosition.put(navigatioinID, position);
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    public void updateList(String navigationId) {
        recyclerView.getAdapter().notifyDataSetChanged();
        int position = getSelectedPosition(navigationId);
        recyclerView.scrollToPosition(position);
    }

    //    public void setListPosition(int position) {  //, SectionContentList item) {
    public void setListPosition(String navigationId, int position, SectionContentList item) {
        setListPosition(navigationId, position, item, false);
    }
    public void setListPosition(String navigationId, int position, SectionContentList item, boolean isFromSwipeRefresh) {
        mMapSelectedPosition.put(navigationId, position);
        setGSXList(navigationId, position, item, isFromSwipeRefresh);
    }

    private void setGSXList(String navigationId, int position, SectionContentList item) {
        setGSXList(navigationId, position, item, false);
    }
    private void setGSXList(String navigationId, int position, SectionContentList item, boolean isFromSwipeRefresh) {

        // 20190227 찜 기능으로 인해 캐시 데이터가 아닌 실제 해당 URL 데이터 호출 fixed by hklim
        EventBus.getDefault().post(new Events.FlexibleEvent.UpdateBestShopEvent(position, item.linkUrl, item.productName, false, isFromSwipeRefresh));
        int first = layoutManager.findFirstVisibleItemPositions(null)[0];
        int last = layoutManager.findLastVisibleItemPositions(null)[0];
        if (position == first || position == last) {
            recyclerView.smoothScrollToPosition(position);
        }
//        Ln.d("hklim setGSXList navigationId : " + navigationId + " / position : " + position);
        mMapSelectedPosition.put(navigationId, position);
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    private RecyclerItemClickListener mItemClickListener = null;

    /*
     * bind
     */
    @Override
    public void onBindViewHolder(final Context context, int position, ShopInfo info, final String action, final String label, final String sectionName) {
        final List<SectionContentList> items = info.contents.get(position).sectionContent.subProductList;
        recyclerView.setAdapter(new GSXBrandCategoryAdapter(items, info.sectionList.navigationId));

        String naviId = info.sectionList.navigationId;
        Integer itemPosition = mMapSelectedPosition.get(naviId);

        // 선택된 포지션이 없으면 0으로 초기화.
        if (itemPosition == null) {
            itemPosition = 0;
            mMapSelectedPosition.put(naviId, 0);
        }

        if (mItemClickListener != null) {
            recyclerView.removeOnItemTouchListener(mItemClickListener);
        }

        try {
            if (itemPosition == position && // 현재 선택된 포지션이면서
                    GSX_BRAND_PERSONAL.equals(items.get(position).prdid) && // 개인화 일때에.
                    !ClickUtils.work3(500)) {

                ((AbstractBaseActivity) context).setWiseLogHttpClient(ServerUrls.WEB.MSEQ_GSX_BRAND_PERSONAL);
            }
        }
        catch (NullPointerException e) {
            Ln.e(e.getMessage());
        }
        catch (NumberFormatException e) {
            Ln.e(e.getMessage());
        }

        mItemClickListener = new RecyclerItemClickListener(itemView.getContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                int thisPosition = mMapSelectedPosition.get(naviId) == null ? 0 : mMapSelectedPosition.get(naviId);

//                Ln.i("hklim onItemClick position : " + position + ", naviId : " + naviId + ", selectedPosition : " + thisPosition);
                if (position < 0 || position == thisPosition) {
                    return;
                }

                SectionContentList item = items.get(position);

                setGSXList(naviId, position, item);

                EventBus.getDefault().post(new Events.FlexibleEvent.GsXClickEvent(position));
            }
        });

        recyclerView.removeOnItemTouchListener(mItemClickListener);

        recyclerView.addOnItemTouchListener(mItemClickListener);
    }
    /**
     * adapter 선언, 20190208 기존 static 선언되어 있음, new 해서 쓰는데 static 쓸 필요 없음. static 삭제.
     */
    private class GSXBrandCategoryAdapter extends RecyclerView.Adapter<GSXBrandBaseViewHolder> {
        private static final int SELECTED = 1;
        private static final int UNSELECTED = 0;
        private final String mNaviId;
        private final List<SectionContentList> items;

        public GSXBrandCategoryAdapter(List<SectionContentList> items, String naviId) {
            this.items = items;
            this.mNaviId = naviId;
        }

        @Override
        public int getItemViewType(int position) {
//            Ln.d("hklim getItemViewType : " + position + " / selectedPosition : " + mMapSelectedPosition.get(mNaviId));
            if (position == mMapSelectedPosition.get(mNaviId)) {
                return SELECTED;
            }
            return UNSELECTED;
        }

        @Override
        public GSXBrandBaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            switch (viewType) {
                case SELECTED:
                    return new ItemSelectedViewHolder(LayoutInflater.from(viewGroup.getContext())
                            .inflate(R.layout.recycler_item_gs_x_brand_category_selected, viewGroup, false));
                default:
                    return new ItemUnSelectedViewHolder(LayoutInflater.from(viewGroup.getContext())
                            .inflate(R.layout.recycler_item_gs_x_brand_category_unselected, viewGroup, false));

            }
        }

        @Override
        public void onBindViewHolder(GSXBrandBaseViewHolder holder, int position) {
            holder.bind(items.get(position).productName);
        }

        @Override
        public int getItemCount() {
            return isEmpty(items) ? 0 : items.size();
        }
    }

    /**
     * 뷰홀더
     */
    private static abstract class GSXBrandBaseViewHolder extends RecyclerView.ViewHolder {

        public GSXBrandBaseViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void bind(String title);
    }

    private static class ItemSelectedViewHolder extends GSXBrandBaseViewHolder {
        private final TextView titleText;

        public ItemSelectedViewHolder(View itemView) {
            super(itemView);
            titleText = (TextView) itemView.findViewById(R.id.text_gs_x_brand_category_selected);

        }

        @Override
        public void bind(String title) {
            titleText.setText(title);
        }
    }

    private static class ItemUnSelectedViewHolder extends GSXBrandBaseViewHolder {
        private final TextView titleText;

        public ItemUnSelectedViewHolder(View itemView) {
            super(itemView);
            titleText = (TextView) itemView.findViewById(R.id.text_gs_x_brand_category_unselected);

        }

        @Override
        public void bind(String title) {
            titleText.setText(title);
        }
    }
}
