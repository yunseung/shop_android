/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import java.util.List;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;

/**
 * 카테고리.
 */
@SuppressLint("NewApi")
public class SubPrdListTextVH extends BaseViewHolder {

    private RecyclerView mRecyclerView;

    private Handler mHandler;
    private Runnable mRunnable;

    /**
     * @param itemView
     */
    public SubPrdListTextVH(View itemView) {
        super(itemView);
        mRecyclerView = itemView.findViewById(R.id.recycler_list);

    }

    /* 카테고리. */
    @Override
    public void onBindViewHolder(final Context context, final int position, ShopInfo info, final String action,
                                 final String label, String sectionName) {
        final List<SubMenuList> subMenuList = info.sectionList.subMenuList;
        if (subMenuList == null) {
            return;
        }

        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        mRecyclerView.setLayoutManager(layoutManager);
        FlexibleBannerSubTabAdapter adapter = new FlexibleBannerSubTabAdapter(context, subMenuList, position,null, null);
        mRecyclerView.setAdapter(adapter);
//        mRecyclerView.scrollToPosition(FlexibleBannerSubTabAdapter.gSelectedPosition);
        LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        manager.scrollToPositionWithOffset(FlexibleBannerSubTabAdapter.gSelectedPosition, FlexibleBannerSubTabAdapter.gSelectedPositionOffset);
//        mHandler = new Handler();
//        mRunnable = new Runnable() {
//            @Override
//            public void run() {
//                InfiniteViewPager.isHorizontalScrollDisallow = false;
//            }
//        };


    }
}
