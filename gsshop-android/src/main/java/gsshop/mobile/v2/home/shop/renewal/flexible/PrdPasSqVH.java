/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.renewal.flexible;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.ModuleList;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolderV2;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.renewal.views.CommonTitleLayout;
import gsshop.mobile.v2.library.viewpager.InfiniteViewPager;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.SwipeUtils;

/**
 * 리뉴얼 된 홀더는 모두 새로 CP 로 생성.
 */
@SuppressLint("NewApi")
public class PrdPasSqVH extends BaseViewHolderV2 {
    private static final int DIVIDER_SPACE_01 = MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.renewal_vh_blank_space_side);
    private static final int DIVIDER_SPACE_02 = MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.renewal_vh_blank_space_between);

    private final RecyclerView mRecyclerListCommand;

    private final CommonTitleLayout mCommonTitleLayout;
    private LinearLayout mLlPreferenceArea;
    private TextView mTvName;
    private TextView mTvSubName;
    private LinearLayout mLlHeartArea;

    private RecyclerView.ItemDecoration itemDecoration;

    private static final String RT_TYPE_MORE = "MORE";

    public PrdPasSqVH(View itemView, String naviId) {
        super(itemView);

        //스와이프 허용각도 확대 적용
        increaseSwipeAngle = true;

        mCommonTitleLayout = itemView.findViewById(R.id.common_title_layout);

        mRecyclerListCommand = itemView.findViewById(R.id.list_recommend);
        mTvName = itemView.findViewById(R.id.tv_name);
        mTvSubName = itemView.findViewById(R.id.tv_sub_name);
        mLlPreferenceArea = itemView.findViewById(R.id.ll_preference_area);
        mLlHeartArea = itemView.findViewById(R.id.ll_heart_area);
    }

    @Override
    public void onBindViewHolder(Context context, int position, List<ModuleList> moduleList) {
        final SectionContentList item = moduleList.get(position);

        setView(context, item, null, null, L_TYPE);
    }

    /*
     * bind
     */
    @Override
    public void onBindViewHolder(final Context context, int position, ShopInfo info, final String action, final String label, String sectionName) {
        final SectionContentList item = info.contents.get(position).sectionContent;

        setView(context, item, action, label, C_TYPE);
    }

    private void setView(final Context context, SectionContentList item, final String action, final String label, int type) {

        mCommonTitleLayout.setCommonTitle(this, item);

        mTvName.setText(item.name);
        mTvSubName.setText(item.subName);

        if (itemDecoration == null) {            //아이템 간격 세팅
            itemDecoration = new PrdPasSqVH.SpacesItemDecoration();
            mRecyclerListCommand.addItemDecoration(itemDecoration);
        }

        mRecyclerListCommand.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);

        mRecyclerListCommand.setLayoutManager(llm);

        if (TextUtils.isEmpty(item.wishCnt)) {
            mLlPreferenceArea.setVisibility(View.GONE);
        } else {
            mLlPreferenceArea.setVisibility(View.VISIBLE);
            mLlHeartArea.removeAllViews();
            for (int i = 1; i < 6; i++) {

                ImageView imageView = new ImageView(context);
                LinearLayout.LayoutParams imageLParams = new LinearLayout.LayoutParams(DisplayUtils.convertDpToPx(context, 9), DisplayUtils.convertDpToPx(context, 9));
                int marginImageBadge = DisplayUtils.convertDpToPx(context, 4);
                imageLParams.setMargins(0, 0, marginImageBadge, 0);
                imageView.setLayoutParams(imageLParams);

                if (i <= Integer.parseInt(item.wishCnt)) {
                    imageView.setBackgroundResource(R.drawable.preference_heart_on);
                } else {
                    imageView.setBackgroundResource(R.drawable.preference_heart_off);
                }
                mLlHeartArea.addView(imageView);
            }
        }


        PrdCSqAdapter adapter;
        if (type == C_TYPE) {
            adapter = new PrdCSqAdapter(context, item.subProductList, action, label, navigationId);
        } else {
            adapter = new PrdCSqAdapter(context, item.productList, action, label, navigationId);
        }
        mRecyclerListCommand.setAdapter(adapter);

        mRecyclerListCommand.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        SwipeUtils.INSTANCE.disableSwipe();
                        break;
                    case MotionEvent.ACTION_UP:
                        setOnTouchUp();
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void setOnTouchUp() {
        for (int i = 0; i < mRecyclerListCommand.getChildCount(); i++) {
            mRecyclerListCommand.getChildAt(i).setScaleX(1);
            mRecyclerListCommand.getChildAt(i).setScaleY(1);
        }
    }

    /**
     * 아이템간격을 데코레이션 한다.
     */
    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            final int position = parent.getChildAdapterPosition(view);
            if (position == 0) {
                outRect.left = DIVIDER_SPACE_01;
            }

            if (position == parent.getAdapter().getItemCount() - 1) {
                outRect.right = DIVIDER_SPACE_01;
            } else {
                outRect.right = DIVIDER_SPACE_02;
            }
        }
    }

}
