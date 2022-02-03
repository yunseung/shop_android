/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.R;

/**
 * 추천딜 리스트 어뎁터.
 */
public class FlexibleBannerSubTabAdapter extends RecyclerView.Adapter<FlexibleBannerSubTabAdapter.SubTabViewHolder> {

    private int mParentsPosition;
    private List<SubMenuList> mSubMenuList;
    private Context mContext;
    private String mLabel;
    private String mAction;
    private String mCategory;

    private RecyclerView mRecyclerParent;

    // tab click 시 reloading 되면서 마지막 선택값이 초기화되는 현상을 방지하기 위해 static 으로 선언.
    // 해당 변수 당겨서 새로고침 중이면 다른곳에서도 변경 가능 하게 하기 위해 public 으로 변경...
    public static int gSelectedPosition = 0;
    public static int gSelectedPositionOffset = 0;

    /**
     * @param context
     * @param subProductList
     * @param action
     * @param label
     */
    public FlexibleBannerSubTabAdapter(Context context, List<SubMenuList> subProductList, String action, String label) {
        this(context, subProductList, -1, action, label);
    }

    /**
     * @param context
     * @param subProductList
     * @param moreBtnImgUrl  전체보기 버튼 이미지 url (해당 param 이 있으면 전체보기 버튼을 이 이미지로 뿌림)
     * @param moreBtnUrl     전체보기 버튼 링크 url
     * @param action
     * @param label
     */
    public FlexibleBannerSubTabAdapter(Context context, List<SubMenuList> subProductList, String moreBtnImgUrl, String moreBtnUrl, String action, String label) {
        this(context, subProductList, -1, action, label);
    }

    /**
     * 리스트이 length 추가.
     *
     * @param context
     * @param subProductList
     * @param position
     * @param action
     * @param label
     */
    public FlexibleBannerSubTabAdapter(Context context, List<SubMenuList> subProductList, int position, String action, String label) {
        this.mContext = context;
        this.mSubMenuList = subProductList;
        this.mParentsPosition = position;
        this.mAction = action;
        this.mLabel = label;
    }

    public void setCategory(String category) {
        this.mCategory = category;
    }

    public void setItem(ArrayList<SubMenuList> subProductList) {
        this.mSubMenuList = subProductList;
    }

    @Override
    public SubTabViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.home_row_type_fx_sub_tab_item, viewGroup, false);

        return new SubTabViewHolder(itemView);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerParent = recyclerView;
    }

    @Override
    public void onBindViewHolder(SubTabViewHolder holder, final int position) {
        final SubMenuList item = mSubMenuList.get(position);

        holder.mTvName.setText(item.sectionName);

        if (gSelectedPosition != position) {
            holder.mTvName.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            holder.mTvName.setTextColor(Color.parseColor("#7B111111"));
            holder.mIvSelectedBar.setVisibility(View.GONE);
        } else {
            holder.mTvName.setTextColor(Color.parseColor("#111111"));
            holder.mTvName.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            holder.mIvSelectedBar.setVisibility(View.VISIBLE);
        }

        holder.mLlRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.mTvName.setTextColor(Color.parseColor("#111111"));
                holder.mTvName.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                holder.mIvSelectedBar.setVisibility(View.VISIBLE);
                gSelectedPosition = position;
                if (mRecyclerParent != null) {
                    LinearLayoutManager manager = (LinearLayoutManager) mRecyclerParent.getLayoutManager();
                    gSelectedPositionOffset = (int) holder.mLlRoot.getX();
                    int paddingRecyclerLeft = mRecyclerParent.getPaddingLeft();
                    gSelectedPositionOffset -= paddingRecyclerLeft;
                    manager.scrollToPositionWithOffset(gSelectedPosition, gSelectedPositionOffset);
                }

                EventBus.getDefault().post(new Events.FlexibleEvent.UpdateFlexibleShopEvent(position, mParentsPosition));
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSubMenuList == null ? 0 : mSubMenuList.size();
    }

    /**
     * 뷰홀더
     */
    public static class SubTabViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout mLlRoot;
        public TextView mTvName;
        public ImageView mIvSelectedBar;

        public SubTabViewHolder(View itemView) {
            super(itemView);
            mLlRoot = itemView.findViewById(R.id.ll_root);
            mTvName = itemView.findViewById(R.id.tv_name);
            mIvSelectedBar = itemView.findViewById(R.id.iv_selected_bar);
        }
    }
}