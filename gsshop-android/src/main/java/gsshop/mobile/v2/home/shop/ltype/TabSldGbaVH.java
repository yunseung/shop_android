/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.ltype;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.main.ModuleList;
import gsshop.mobile.v2.home.shop.BaseViewHolderV2;
import gsshop.mobile.v2.support.ui.BugFixedStaggeredGridLayoutManager;
import gsshop.mobile.v2.util.ClickUtils;
import roboguice.util.Ln;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;
import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;
import static gsshop.mobile.v2.util.StringUtils.trim;


/**
 * gschoice 카테고리.
 */
public class TabSldGbaVH extends BaseViewHolderV2 {

    /**
     * 리사이클뷰
     */
    private final RecyclerView recycler;

    /**
     * 어뎁터
     */
    private Adapter adapter;

    /**
     * 레이아웃 매니저
     */
    protected StaggeredGridLayoutManager mLayoutManager;

    /**
     * 뷰홀더와 해더의 싱크를 위한 변수
     */
    private int offSet = 0; // offset
    private int fvPosition = 0; // first visible position

    /**
     * 본 뷰홀더가 해더인지 여부
     */
    private boolean isHeader = false;

    /**
     * 선택된 아이템 인덱스
     */
    private int selectedItemIndex = 0;

    private String mNavigationId;

    /**
     * @param itemView View
     */
    public TabSldGbaVH(View itemView, String navigationId) {
        super(itemView);

        EventBus.getDefault().register(this);

        recycler = itemView.findViewById(R.id.recycler_gschoice_category);

        mNavigationId = navigationId;

        mLayoutManager = new BugFixedStaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        recycler.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onViewAttachedToWindow() {
        super.onViewAttachedToWindow();
        EventBus.getDefault().postSticky(new Events.FlexibleEvent.GSChoiceSyncPositionEvent(mNavigationId, offSet, fvPosition, selectedItemIndex));
    }

    /**
     * 뷰홀더와 해더의 위치를 동기화한다.
     *
     * @param event GSChoiceSyncPositionEvent
     */
    public void onEvent(Events.FlexibleEvent.GSChoiceSyncPositionEvent event) {
        //스티키이벤트 제거
        Events.FlexibleEvent.GSChoiceSyncPositionEvent syncPositionEvent =
                EventBus.getDefault().getStickyEvent(Events.FlexibleEvent.GSChoiceSyncPositionEvent.class);
        if (syncPositionEvent != null) {
            EventBus.getDefault().removeStickyEvent(syncPositionEvent);
        }

        if (event.navigationId != null && event.navigationId.equals(mNavigationId)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (event.fvPosition >= 0 && event.offset >= 0) {
                        mLayoutManager.scrollToPositionWithOffset(event.fvPosition, event.offset);
                    }
                    if (adapter != null && selectedItemIndex != event.selectedItemIndex) {
                        selectedItemIndex = event.selectedItemIndex;
                        adapter.notifyDataSetChanged();
                    }
                }
            }, 100);
        }
    }

    /**
     * 이벤트버스 unregister
     *
     * @param event TvLiveUnregisterEvent
     */
    public void onEvent(Events.FlexibleEvent.TvLiveUnregisterEvent event) {
        fvPosition = 0;
        offSet = 0;
        selectedItemIndex = 0;
        EventBus.getDefault().unregister(this);
    }

    /**
     * 카테고리 스크롤중 매장 스크롤시 카테고리 스크롤을 중지한다.
     * (해더와 동기화를 위해)
     *
     * @param event GSChoiceCateStopScrollEvent
     */
    public void onEvent(Events.FlexibleEvent.GSChoiceCateStopScrollEvent event) {
        if (ClickUtils.work2(500)) {
            return;
        }
        recycler.stopScroll();
    }

    @Override
    public void onBindViewHolder(final Context context, int position, List<ModuleList> contents) {
        super.onBindViewHolder(context, position, contents);
        final ModuleList content = contents.get(position);

        if (content.moduleList == null) {
            return;
        }

        //스크롤중 onBind 수행시 기존 위치를 유지한다.
        if (fvPosition > 0) {
            EventBus.getDefault().postSticky(new Events.FlexibleEvent.GSChoiceSyncPositionEvent(mNavigationId, offSet, fvPosition, selectedItemIndex));
        }

        // 카테고리 리스트
        adapter = new Adapter(context, content.moduleList);
        recycler.setAdapter(adapter);
        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == SCROLL_STATE_IDLE) {
                    if (ClickUtils.work(500)) {
                        return;
                    }
                    syncPosition(selectedItemIndex);
                }
            }
        });
    }

    /**
     * 뷰홀더와 해더의 위치를 동기화한다.
     */
    private void syncPosition(int position) {
        fvPosition = mLayoutManager.findFirstCompletelyVisibleItemPositions(null)[0];
        //getChildAt 사용시 정확한 getLeft 값을 못가져옴
        View v = mLayoutManager.findViewByPosition(fvPosition);
        if (v != null) {
            offSet = v.getLeft();
        }
        EventBus.getDefault().postSticky(new Events.FlexibleEvent.GSChoiceSyncPositionEvent(mNavigationId, offSet, fvPosition, position));
    }

    /**
     * 카테고리 어뎁터
     */
    private class Adapter extends RecyclerView.Adapter<ItemViewHolder> {

        public final Context context;
        public final List<ModuleList> list;

        private Adapter(Context context, List<ModuleList> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View viewItem = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_item_gschoice_category_item, parent, false);
            return new ItemViewHolder(viewItem);
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {
            if (position == 0 || position == getItemCount() - 1) {
                holder.itemView.setPadding(2, 0, 0, 2);
            }

            if (position == 0) {
                holder.viewSpaceLeft.setVisibility(View.VISIBLE);
            }
            else if (position == getItemCount() - 1) {
                holder.viewSpaceRight.setVisibility(View.VISIBLE);
            }
            else {
                holder.viewSpaceLeft.setVisibility(View.GONE);
                holder.viewSpaceRight.setVisibility(View.GONE);
            }

            try {
                Glide.with(context).load(trim(list.get(position).tabBgImg)).diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.brand_no_android).dontAnimate().into(holder.img_sumnail);
            } catch (Exception er) {
                Ln.e(er.getMessage());
            }

            holder.txt_title.setText(list.get(position).name);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ClickUtils.work(1000)) {
                        return;
                    }
                    //매장 스크롤 정지 (카테고리 상품 로딩 후 위치를 상단으로 잡기 위해)
                    EventBus.getDefault().post(new Events.FlexibleEvent.GSChoiceStopScrollEvent());

                    // ItemBorderColor는 그냥 UI만 바꿔주게끔 event를 밖으로 옮김.
                    setItemBorderColor(holder);
                    EventBus.getDefault().postSticky(new Events.FlexibleEvent.GSChoiceSyncPositionEvent(mNavigationId, -1, -1, position));

//                    scrollToCenter(v, position);
                    //카테고리 데이타 로딩
                    EventBus.getDefault().post(new Events.FlexibleEvent.GSChoiceCategoryEvent(getIsHeader(), list.get(position).ajaxTabPrdListUrl));
                    //와이즈로그 호출
                    ((HomeActivity) context).setWiseLog(list.get(position).wiseLog);
                }
            });

            if (selectedItemIndex == position) {
                holder.img_sumnail.setBorderColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.gschoice_category_selected));
            } else {
                holder.img_sumnail.setBorderColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.gschoice_category_default));
            }
        }

        private void scrollToCenter(View v, int position) {
            int itemToScroll = recycler.getChildAdapterPosition(v);
            int centerOfScreen = recycler.getWidth() / 2 - v.getWidth() / 2;
            mLayoutManager.scrollToPositionWithOffset(itemToScroll, centerOfScreen);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    syncPosition(position);
                }
            }, 500);
        }

        @Override
        public int getItemCount() {
            return isNotEmpty(list) ? list.size() : 0;
        }
    }

    /**
     * 카테고리 뷰홀더
     */
    private static class ItemViewHolder extends RecyclerView.ViewHolder {

        public final CircleImageView img_sumnail;
        public final TextView txt_title;
        public final View viewSpaceLeft;
        public final View viewSpaceRight;

        public ItemViewHolder(View itemView) {
            super(itemView);
            img_sumnail = itemView.findViewById(R.id.img_sumnail);
            txt_title = itemView.findViewById(R.id.txt_title);
            viewSpaceLeft = itemView.findViewById(R.id.blank_space_left);
            viewSpaceRight = itemView.findViewById(R.id.blank_space_right);
        }
    }

    /**
     * 카테고리 원형테두리를 설정한다.
     *
     * @param holder ItemViewHolder
     */
    private void setItemBorderColor(ItemViewHolder holder) {
        //모든 아이템 테두리를 디폴트색으로 세팅
        for (int i = 0; i < recycler.getChildCount(); i++) {
            View view = recycler.getChildAt(i);
            ItemViewHolder vh = (ItemViewHolder) recycler.getChildViewHolder(view);
            vh.img_sumnail.setBorderColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.gschoice_category_default));
        }
        //선택된 아이템 테두리 세팅, 볼드체
        holder.img_sumnail.setBorderColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.gschoice_category_selected));
    }

    /**
     * 해더여부를 세팅한다.
     * @param isHeader if true, header
     */
    public void setIsHeader(boolean isHeader) {
        this.isHeader = isHeader;
    }

    /**
     * 해더여부를 반환한다.
     *
     * @return isHeader
     */
    public boolean getIsHeader() {
        return isHeader;
    }

    @Override
    public void setSelectedItem(int selctedItem) {
        selectedItemIndex = selctedItem;
    }

}
