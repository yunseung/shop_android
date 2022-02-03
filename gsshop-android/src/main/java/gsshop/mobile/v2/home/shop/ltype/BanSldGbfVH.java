/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.ltype;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.ModuleList;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolderV2;
import gsshop.mobile.v2.home.shop.renewal.views.CommonTitleLayout;
import gsshop.mobile.v2.library.viewpager.InfiniteViewPager;
import gsshop.mobile.v2.util.SwipeUtils;

/**
 * 추천딜 단품 item
 */
@SuppressLint("NewApi")
public class BanSldGbfVH extends BaseViewHolderV2 {
    private static final int DIVIDER_SPACE_BIG = MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.vh_blank_space_large);
    private static final int DIVIDER_SPACE_MIDDLE = MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.vh_blank_space_middle);
    private static final int DIVIDER_SPACE_SMALL = MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.vh_blank_space_small);

    private final CommonTitleLayout mCommonTitleLayout;
    private final RecyclerView mProductRecyclerView;

    private RecyclerView.ItemDecoration itemDecoration;

    /**
     * @param itemView
     */
    public BanSldGbfVH(View itemView) {
        super(itemView);

        //스와이프 허용각도 확대 적용
        increaseSwipeAngle = true;

        mCommonTitleLayout = itemView.findViewById(R.id.common_title_layout);
        mProductRecyclerView = (RecyclerView) itemView.findViewById(R.id.recycler_list);
    }

    /*
     * bind
     */
    @Override
    public void onBindViewHolder(Context context, int position, List<ModuleList> moduleList){
        super.onBindViewHolder(context, position, moduleList);
        ModuleList thisModuleList = moduleList.get(position);

        if (thisModuleList.productList == null) {
            return;
        }
        final List<SectionContentList> list = thisModuleList.productList;

        if (list == null) {
            return;
        }

        mCommonTitleLayout.setCommonTitle(this, thisModuleList);

        if (itemDecoration == null) {            //아이템 간격 세팅
            itemDecoration = new SpacesItemDecoration();
            mProductRecyclerView.addItemDecoration(itemDecoration);
        }

        mProductRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        mProductRecyclerView.setLayoutManager(layoutManager);
        BanSldGbfAdapter adapter = new BanSldGbfAdapter(context, list, null, null);
        mProductRecyclerView.setAdapter(adapter);

        mProductRecyclerView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        SwipeUtils.INSTANCE.disableSwipe();
                        break;
                }
                return false;
            }
        });
    }

    /**
     * 아이템간격을 데코레이션 한다.
     */
    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

            final int position = parent.getChildAdapterPosition(view);

            if (position == 0) {
                outRect.left = DIVIDER_SPACE_MIDDLE;
            }
            else {
                outRect.left = 0;
            }

            if (position == parent.getAdapter().getItemCount() - 1) {
                outRect.right = DIVIDER_SPACE_BIG;
            }
            else {
                outRect.right = DIVIDER_SPACE_SMALL;
            }
        }
    }

}
