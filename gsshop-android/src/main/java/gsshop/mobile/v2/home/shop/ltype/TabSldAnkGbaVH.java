package gsshop.mobile.v2.home.shop.ltype;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.blankj.utilcode.util.EmptyUtils;

import java.util.List;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.main.ModuleList;
import gsshop.mobile.v2.home.shop.BaseViewHolderV2;
import gsshop.mobile.v2.support.ui.BugFixedStaggeredGridLayoutManager;
import gsshop.mobile.v2.util.ClickUtils;
import roboguice.util.Ln;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;

public class TabSldAnkGbaVH extends BaseViewHolderV2 {

    /**
     * 리사이클뷰
     */
    private final RecyclerView mRecyclerAnchor;

    /**
     * 어뎁터
     */
    private Adapter adapter;

    /**
     * 레이아웃 매니저
     */
    protected StaggeredGridLayoutManager mLayoutManager;

    /**
     * 뷰홀더와 해더의 싱크를 위한 변수 (싱크를 위해서 static으로 해 두었지만 해당 홀더가 꽂히는 매장이 많아지면 모두 공유가 되어버리는 문제 발생)
     */
    private int offSet = 0; // offset
    private int fvPosition = 0; // first visible position

    /**
     * 뷰홀더가 해더인지 여부
     */
    private boolean isHeader = false;

    /**
     * 선택된 아이템 인덱스 (싱크를 위해서 static으로 해 두었지만 해당 홀더가 꽂히는 매장이 많아지면 모두 공유가 되어버리는 문제 발생)
     */
    private int selectedItemIndex = 0;

    private String mNavigationId;

    /**
     * @param itemView View
     */
    public TabSldAnkGbaVH(View itemView, String navigationId) {
        super(itemView);

        EventBus.getDefault().register(this);

        mRecyclerAnchor = itemView.findViewById(R.id.recycler_gschoice_category_b);

        mNavigationId = navigationId;

        mLayoutManager = new BugFixedStaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        mRecyclerAnchor.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onViewAttachedToWindow() {
        super.onViewAttachedToWindow();
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
        mRecyclerAnchor.stopScroll();
    }

    /**
     * 데이타 변경을 고지한다.
     *
     * @param event GSChoiceDataChangeEvent
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
        mRecyclerAnchor.setAdapter(adapter);
        mRecyclerAnchor.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                    .inflate(R.layout.recycler_item_gschoice_category_b_item, parent, false);
            return new ItemViewHolder(viewItem);
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {

            holder.txt_title.setText(list.get(position).name);

            if (selectedItemIndex == position) {
                holder.txt_title.setTypeface(null, Typeface.BOLD);
            } else {
                holder.txt_title.setTypeface(Typeface.SANS_SERIF);
            }

            holder.itemView.setTag(list.get(position).tabSeq);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ClickUtils.work(1000)) {
                        return;
                    }
                    // 매장 스크롤 정지 (카테고리 상품 로딩 후 위치를 상단으로 잡기 위해)
                    EventBus.getDefault().post(new Events.FlexibleEvent.GSChoiceStopScrollEvent());
                    // 스크롤 위치 조정.
                    EventBus.getDefault().postSticky(new Events.ClickEventLtypeAnchor(mNavigationId, position, list.get(position).tabSeq, 46));
                    //와이즈로그 호출
                    ((HomeActivity) context).setWiseLog(list.get(position).wiseLog);
                }
            });

            if (selectedItemIndex == position) {
                setSelectedItemUI(context, holder, list.get(position));
            } else {
                holder.imgBackground.setVisibility(View.GONE);
                holder.txt_title.setTextColor(Color.parseColor("#666666"));
            }
        }

        private void setSelectionByTabSeq(String tabSeq) {
            if (tabSeq == null) {
                return;
            }
            for (int i = 0; i < list.size(); i++) {
                if (tabSeq.equals(list.get(i).tabSeq)) {
                    selectedItemIndex = i;
                }
            }

            mRecyclerAnchor.scrollToPosition(selectedItemIndex);
            notifyDataSetChanged();
        }

        private void scrollToCenter(View v, int position) {
            int itemToScroll = mRecyclerAnchor.getChildAdapterPosition(v);
            int centerOfScreen = mRecyclerAnchor.getWidth() / 2 - v.getWidth() / 2;
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
            return list.size();
        }


        public String getMoreBtnUrl() {
            try {
                return list.get(selectedItemIndex).moreBtnUrl;
            } catch (NullPointerException e) {
                Ln.e(e.getMessage());
                return null;
            } catch (IndexOutOfBoundsException e) {
                Ln.e(e.getMessage());
                return null;
            }
        }
    }

    /**
     * 카테고리 뷰홀더
     */
    private static class ItemViewHolder extends RecyclerView.ViewHolder {

        public final TextView txt_title;
        public final ImageView imgBackground;

        public ItemViewHolder(View itemView) {
            super(itemView);
            txt_title = itemView.findViewById(R.id.txt_title);
            imgBackground = itemView.findViewById(R.id.img_background);
        }
    }

    /**
     * 해당 아이템의 텍스트 및 테두리 색상 설정.
     *
     * @param context
     * @param holder
     * @param item    ModuleList
     */
    private void setItem(Context context, ItemViewHolder holder, ModuleList item) {
        for (int i = 0; i < mRecyclerAnchor.getChildCount(); i++) {
            View view = mRecyclerAnchor.getChildAt(i);
            ItemViewHolder vh = (ItemViewHolder) mRecyclerAnchor.getChildViewHolder(view);
            vh.imgBackground.setVisibility(View.GONE);
            vh.txt_title.setTextColor(Color.parseColor("#666666"));
            vh.txt_title.setTypeface(Typeface.SANS_SERIF);
        }

        setSelectedItemUI(context, holder, item);
    }

    private void setSelectedItemUI(Context context, ItemViewHolder holder, ModuleList item) {
        String colorBack = item.bgColor;
        String colorText = item.activeTextColor;

        //선택된 아이템 테두리 세팅, 볼드체
        holder.txt_title.setTextColor(Color.WHITE);
        holder.txt_title.setTypeface(null, Typeface.BOLD);

        if (EmptyUtils.isNotEmpty(colorBack)) {
            try {
                GradientDrawable shape = new GradientDrawable();
                shape.setCornerRadius(context.getResources()
                        .getDimensionPixelOffset(R.dimen.gschoice_image_selected_background_radius));
                shape.setColor(Color.parseColor("#" + colorBack));
                holder.imgBackground.setBackground(shape);
            } catch (IllegalArgumentException e) {
                Ln.e(e.getMessage());
            }
        }

        if (EmptyUtils.isNotEmpty(colorText)) {
            try {
                holder.txt_title.setTextColor(Color.parseColor("#" + colorText));
            } catch (IllegalArgumentException e) {
                Ln.e(e.getMessage());
            }
        }

        holder.imgBackground.setVisibility(View.VISIBLE);
    }

    /**
     * 해더여부를 세팅한다.
     *
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


    public String getMoreBtnUrl() {
        try {
            return adapter.getMoreBtnUrl();
        } catch (NullPointerException e) {
            Ln.e(e.getMessage());
            return null;
        }
    }

    @Override
    public void onViewDetachedFromWindow() {
        super.onViewDetachedFromWindow();

    }

    @Override
    public void setSelectedItem(int selctedItem) {
        selectedItemIndex = selctedItem;
    }

    @Override
    public boolean setSelectedTabPosition(String tabSeq) {
        if (!TextUtils.isEmpty(tabSeq)) {
            adapter.setSelectionByTabSeq(tabSeq);
            return true;
        }
        return false;
    }
}
