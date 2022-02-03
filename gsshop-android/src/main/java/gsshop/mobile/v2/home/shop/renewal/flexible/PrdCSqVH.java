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
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.main.ModuleList;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolderV2;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.renewal.views.CommonTitleLayout;
import gsshop.mobile.v2.library.viewpager.InfiniteViewPager;
import gsshop.mobile.v2.util.SwipeUtils;
import roboguice.util.Ln;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;

/**
 * 리뉴얼 된 홀더는 모두 새로 CP 로 생성.
 */
@SuppressLint("NewApi")
public class PrdCSqVH extends BaseViewHolderV2 {
    private static final int DIVIDER_SPACE_01 = MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.renewal_vh_blank_space_side);
    private static final int DIVIDER_SPACE_02 = MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.renewal_vh_blank_space_between);

    private final RecyclerView mRecyclerListCommand;

    private LinearLayout root; //이런 상품 어떠세요 전체 영역

    private final CommonTitleLayout mCommonTitleLayout;

    private RecyclerView.ItemDecoration itemDecoration;

    private String mNaviId;

    private ArrayList<Integer> mLastVisibleItemsPositionList = new ArrayList<>();

    //    private ArrayList<String> mAlreadySentPrdIdList = new ArrayList<>();
    private HashMap<String, Boolean> mMapChkPrdIdIdSended = new HashMap<>();

    private boolean mIsFirstScroll = true;
    private boolean mIsSentFirstCSqWiseLog = false;

    private Rect rect; // 이런상품 어떠세요 전체 영역 보일때 체크위해 추가

    /**
     * 레이아웃 매니저
     */
    protected LinearLayoutManager mLayoutManager;

    private Context mContext;

    public PrdCSqVH(View itemView, String naviId) {
        super(itemView);

        //스와이프 허용각도 확대 적용
        increaseSwipeAngle = true;

        mCommonTitleLayout = itemView.findViewById(R.id.common_title_layout);

        mRecyclerListCommand = (RecyclerView) itemView.findViewById(R.id.list_recommend);
        root = itemView.findViewById(R.id.root);
        this.mNaviId = naviId;
    }

    @Override
    public void onBindViewHolder(Context context, int position, List<ModuleList> moduleList) {
        final SectionContentList item = moduleList.get(position);
        mContext = context;

        setView(mContext, item, null, null, L_TYPE);
    }

    /*
     * bind
     */
    @Override
    public void onBindViewHolder(final Context context, int position, ShopInfo info, final String action, final String label, String sectionName) {
        final SectionContentList item = info.contents.get(position).sectionContent;
        mContext = context;

        setView(context, item, action, label, C_TYPE);
    }

    protected void setView(final Context context, SectionContentList item, final String action, final String label, int type) {

        if (mCommonTitleLayout != null) {
            mCommonTitleLayout.setCommonTitle(this, item);
        }

        if (itemDecoration == null) {            //아이템 간격 세팅
            itemDecoration = new PrdCSqVH.SpacesItemDecoration();
            mRecyclerListCommand.addItemDecoration(itemDecoration);
        }

        rect = new Rect();
        mRecyclerListCommand.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(context);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerListCommand.setLayoutManager(mLayoutManager);

        PrdCSqAdapter adapter;
        if (type == C_TYPE) {
            adapter = new PrdCSqAdapter(context, item.subProductList, action, label, navigationId);
        } else {
            adapter = new PrdCSqAdapter(context, item.productList, action, label, navigationId);
        }
        mRecyclerListCommand.setAdapter(adapter);

        mRecyclerListCommand.setOnTouchListener((v, event) -> {

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
        });
    }

    @Override
    public void onViewAttachedToWindow() {
        super.onViewAttachedToWindow();
        try {
            EventBus.getDefault().register(this);
        } catch (Exception e) {
            Ln.e(e);
        }
        mMapChkPrdIdIdSended.clear();
        mIsSentFirstCSqWiseLog = false;
        mIsFirstScroll = true;
    }

    @Override
    public void onViewDetachedFromWindow() {
        mMapChkPrdIdIdSended.clear();
        mIsSentFirstCSqWiseLog = false;
        mIsFirstScroll = true;
        EventBus.getDefault().unregister(this);
        super.onViewDetachedFromWindow();
    }

    public void onEvent(Events.FlexibleEvent.SendPrdCSqWiseLogEvent event) {
        // 여기로 들어왔다는 건 홈매장에서 상하 스크롤로 인해 PrdCSqVH 가 사라졌다가 보였다는 의미로 봐도 되기 때문에 기존 보냈던 리스트 map 을 초기화 한다.
        mMapChkPrdIdIdSended.clear();
        mIsFirstScroll = true;
        mIsSentFirstCSqWiseLog = true;


    }

    /**
     * 현재 화면에 보여지는 아이템들의 position list 를 반환한다.
     *
     * @return items position list
     */
    private ArrayList<Integer> getVisibleItemsPosition() {
        int firstViewsIds = mLayoutManager.findFirstCompletelyVisibleItemPosition();
        int lastViewsIds = mLayoutManager.findLastCompletelyVisibleItemPosition();

        ArrayList<Integer> visibleItemsPosition = new ArrayList<>();
        int visibleItemsCount = lastViewsIds - firstViewsIds + 1;
        for (int i = 0; i < visibleItemsCount; i++) {
            visibleItemsPosition.add(firstViewsIds + i);
        }

        return visibleItemsPosition;
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
