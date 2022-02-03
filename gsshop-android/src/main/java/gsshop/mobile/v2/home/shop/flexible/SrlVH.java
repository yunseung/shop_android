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
import android.graphics.Rect;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.library.viewpager.InfiniteViewPager;
import gsshop.mobile.v2.user.User;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.SwipeUtils;

/**
 * 추천딜 단품 item
 */
@SuppressLint("NewApi")
public class SrlVH extends BaseViewHolder {
    private static final int DIVIDER_SPACE_01 = MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.srl_vh_blank_space_side);
    private static final int DIVIDER_SPACE_02 = MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.srl_vh_blank_space_between);

    private final TextView user_name;
    private final TextView productName;
    private final RecyclerView list_recommend;

    private RecyclerView.ItemDecoration itemDecoration;

    /**
     * @param itemView
     */
    public SrlVH(View itemView) {
        super(itemView);

        //스와이프 허용각도 확대 적용
        increaseSwipeAngle = true;

        user_name = (TextView) itemView.findViewById(R.id.user_name);
        productName = (TextView) itemView.findViewById(R.id.productName);
        list_recommend = (RecyclerView) itemView.findViewById(R.id.list_recommend);

        DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), list_recommend);
    }

    /*
     * bind
     */
    @Override
    public void onBindViewHolder(final Context context, int position, ShopInfo info, final String action, final String label, String sectionName) {

        final SectionContentList item = info.contents.get(position).sectionContent;

        User user = User.getCachedUser();
        if (user != null) {
            user_name.setText(user.getUserName() + context.getString(R.string.user));
            user_name.setVisibility(View.VISIBLE);
            //이미 user_name 레프트 패딩이 포함되어 있다.
            //productName.setPadding(0,0,0,0);
        }else{
            user_name.setVisibility(View.GONE);
            //이미 user_name 레프트 패딩이 포함되어 있다.
            //productName.setPadding(context.getResources().getDimensionPixelSize(R.dimen.vh_blank_space_small),0,0,0);
        }

        if (!TextUtils.isEmpty(item.productName)) {
            productName.setText(item.productName);
        }

        if (itemDecoration == null) {            //아이템 간격 세팅
            itemDecoration = new SpacesItemDecoration();
            list_recommend.addItemDecoration(itemDecoration);
        }

        list_recommend.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);

        list_recommend.setLayoutManager(llm);
        FlexibleRecommendAdapter adapter = new FlexibleRecommendAdapter(context, item.subProductList, action, label);
        list_recommend.setAdapter(adapter);

        list_recommend.setOnTouchListener(new View.OnTouchListener() {


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
