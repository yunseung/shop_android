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
import android.os.Build;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.inject.Inject;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.pattern.mvc.BaseAsyncController;

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
import gsshop.mobile.v2.home.shop.renewal.views.CustomTitleDTLayout;
import gsshop.mobile.v2.home.shop.renewal.views.ScrollingLayoutManager;
import gsshop.mobile.v2.library.viewpager.InfiniteViewPager;
import gsshop.mobile.v2.util.ClickUtils;
import roboguice.util.Ln;

/**
 * 리뉴얼 된 홀더는 모두 새로 CP 로 생성.
 */
@SuppressLint("NewApi")
public class RtsPrdCCstSqVH extends BaseViewHolderV2 {
    private static final int DIVIDER_SPACE_01 = MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.renewal_vh_blank_space_side);
    private static final int DIVIDER_SPACE_02 = MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.renewal_vh_blank_space_between);

    private final RecyclerView mRecyclerListCommand;

    private LinearLayout root; //이런 상품 어떠세요 전체 영역

    private CustomTitleDTLayout mCustomTitleDTLayout;

    private RecyclerView.ItemDecoration itemDecoration;

    private LinearLayout mLayoutRefresh;

    private RtsPrdCCstSqAdapter adapter;

    private Handler mHandler;
    private Runnable mRunnable;
    private String mNaviId;

    private ArrayList<Integer> mLastVisibleItemsPositionList = new ArrayList<>();

    //    private ArrayList<String> mAlreadySentPrdIdList = new ArrayList<>();
    private HashMap<String, Boolean> mMapChkPrdIdIdSended = new HashMap<>();

    private boolean mIsFirstScroll = true;
    private boolean mIsSentFirstCSqWiseLog = false;

    /**
     * 2초뒤 다시 움직이게
     */
    private Handler mRunHandler;
    private Runnable mRunRunnable;
    /**
     * 2초뒤에 다시 움직이게 하려면 멈춤 이후에 멈추 이후에 다시 액션을 한것인지에 대한 Flag를 추가하였다.
     */
    private boolean mReScrollAction = false;
    /**
     * 2초뒤에 다시 움직이게 하려몀 멈추 이후에 새로운 액션인것을 확인해야 되는데
     * 지금 자동 롤링중인지도 확인해야 해서 flag 추가,
     */
    private boolean mIsCurrent = false;

    /**
     * 속도 계산하는 트랙커
     * 버전 대응
     */
    private VelocityTracker mVelocityTracker = null;

    /**
     * 최종 계산된 이동값
     */
    private float mLastVelocity = 0;

    /**
     * 레이아웃 매니저
     */
    protected ScrollingLayoutManager mLayoutManager;

    private Context mContext;

    public RtsPrdCCstSqVH(View itemView, String naviId) {
        super(itemView);

        mCustomTitleDTLayout = itemView.findViewById(R.id.custom_title_dt_layout);

        mLayoutRefresh = itemView.findViewById(R.id.layout_stream_refresh);

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

        if (mCustomTitleDTLayout != null) {
            mCustomTitleDTLayout.setTitle(context, item);
        }

        if (itemDecoration == null) {            //아이템 간격 세팅
            itemDecoration = new RtsPrdCCstSqVH.SpacesItemDecoration();
            mRecyclerListCommand.addItemDecoration(itemDecoration);
        }

        mRecyclerListCommand.setHasFixedSize(true);


        if (type == C_TYPE) {
            adapter = new RtsPrdCCstSqAdapter(context, item.subProductList, action, label, navigationId);
        } else {
            adapter = new RtsPrdCCstSqAdapter(context, item.productList, action, label, navigationId);
        }
        mRecyclerListCommand.setAdapter(adapter);

        //RecyclerView에 자동롤링기능 추가 + duration 디폴트 설정,
        mLayoutManager = new ScrollingLayoutManager(context, LinearLayoutManager.HORIZONTAL, false, mRecyclerListCommand.getAdapter().getItemCount() * 1000);
        mRecyclerListCommand.setLayoutManager(mLayoutManager);

        //자동롤링기능 trigger
        mRecyclerListCommand.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mRecyclerListCommand != null && mRecyclerListCommand.getChildCount() >0 ) {
                    //저사양단말에서는 오토 스크롤되지 않는다. 4.4
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                        //doing
                        mLayoutManager.smoothScrollToPosition(mRecyclerListCommand, null, mRecyclerListCommand.getAdapter().getItemCount());
                    } else {
                        //noting
                    }
                    mIsCurrent = true;
                }
            }
        },2500);


        mRunHandler = new Handler();
        mRunRunnable = () -> {
            //저사양단말에서는 오토 스크롤되지 않는다. 4.4
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                //doing
                mLayoutManager.smoothScrollToPosition(mRecyclerListCommand, null, mRecyclerListCommand.getAdapter().getItemCount());
            } else {
                //noting
            }
            mIsCurrent = true;
        };

        mHandler = new Handler();
        mRunnable = () -> InfiniteViewPager.isHorizontalScrollDisallow = false;

        mRecyclerListCommand.setOnTouchListener((v, event) -> {

            int index = event.getActionIndex();
            int pointerId = event.getPointerId(index);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initSpeedTracker();
                    //break; 동작 효율을 위해 break를 사용하지 않음,
                case MotionEvent.ACTION_MOVE:
                    InfiniteViewPager.isHorizontalScrollDisallow = true;
                    mHandler.removeCallbacks(mRunnable);
                    mHandler.postDelayed(mRunnable, 1000);

                    moveSpeedTracker(event);
                    //흐르고 있었던 중이 아니라면 다음 액션에는 다시 움직여야 한다.
                    //흐르고 있떤 중이라면 else 다음 액션에 움직이 조건에 부합하지 않는다.
                    if(mIsCurrent==false)
                        mReScrollAction = true;
                    else
                        mReScrollAction = false;

                    break;

                case MotionEvent.ACTION_UP:

                    //신규 문법인가? setOnTouchListener 내에서 return 하는 메소드는 사용할수 없다고 나옴
                    //초기값음 0 이다. mLastVelocity
                    removeChecking(pointerId);

                    setOnTouchUp();

                    break;
            }
            return false;
        });

        //새로고침 동작
        mLayoutRefresh.setVisibility(View.VISIBLE);
        mLayoutRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1초에 한번씩만 동작하라고
                if (ClickUtils.work(1000)) {
                    return;
                }
                refreshRTSData(context, HomeActivity.PsnlCurationType.RTS);
            }
        });

    }

    /**
     *
     * //속도의 값을 체크 한다 왼쪽으로 가면 양수, 오른쪽으로 가면 음수, 1초라는 컴퓨팅(계싼 로직으로) 그냥 넘어가자,
     * move중에 마지막 계산된값을 사용한다.
     *
     * @param pointerId 현재 모션 포인트의 아이디
     * @return +- 속도 0 이면 계산이 안된거다,
     */
    private void removeChecking(int pointerId)
    {
        //저사양단말에서는 오토 스크롤되지 않는다. 4.4 그래서 속도 체크 하지 않음
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            //doing
            if(mVelocityTracker != null) {
                float velocity = 0; // 잘못되면 자동으로 흐르지 말라고,
                //속도의 값을 체크 한다 왼쪽으로 가면 양수, 오른쪽으로 가면 음수, 1초라는 컴퓨팅(계싼 로직으로) 그냥 넘어가자,
                //Move중에 마지막 계산된값을 사용한다.
                mLastVelocity = mVelocityTracker.getXVelocity(pointerId);

                //한번의 속도체크가 끝났기 때문에 초기화
                try {
                    mVelocityTracker.recycle();
                } catch (IllegalStateException e) {
                    Ln.e(e);
                }
            }
            //흐르고 있지 않았고(멈춤) 상태이면서 mReScrollAction 할 대상이라면 0.5초뒤에 다시 움직인다.
            //속도의 값을 체크 한다 왼쪽으로 가면 양수, 오른쪽으로 가면 음수, 1초라는 컴퓨팅(계싼 로직으로) 그냥 넘어가자,
            if( mIsCurrent == false && mReScrollAction && mLastVelocity < 0)
            {
                mRunHandler.removeCallbacks(mRunRunnable);
                mRunHandler.postDelayed(mRunRunnable, 500);
                mReScrollAction = false;
                mLastVelocity = 0;
            }
        } else {
            //noting
        }
        //터치 move중에 stop, up 이후에 다시 흐르는 현상이 확인되어 일단은 up이면 무조건 스크롤 중단,
        mRecyclerListCommand.stopScroll();
        mIsCurrent = false;
    }
    /**
     * 오토 스크롤 컨트롤을 위한 속도 체커 모듈 초기화
     */
    private void initSpeedTracker(){
        //저사양단말에서는 오토 스크롤되지 않는다. 4.4 그래서 속도 체크 하지 않음
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            //doing
            if(mVelocityTracker == null) {
                //터치 이벤트가 시작되면 "다운액션"에 obtatin() == get인스턴스
                mVelocityTracker = VelocityTracker.obtain();
            }
            else {
                // init
                mVelocityTracker.clear();
            }
        } else {
            //noting
        }
    }

    /**
     * 모션 event를 받아 +-속도를 계산하는 로직 가능
     * Move 이벤트에서만 의미가 있다.
     * @param event
     */
    private void moveSpeedTracker(MotionEvent event){
        //저사양단말에서는 오토 스크롤되지 않는다. 4.4 그래서 속도 체크 하지 않음
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            //doing
            if(mVelocityTracker != null && event != null)
            {
                //event 등록 *up과 쌍으로 움직여야함, recycle();
                mVelocityTracker.addMovement(event);
                // 속도가 계산되는 주기를 뜻한다. When you want to determine
                // Log velocity of pixels per second
                // Best practice to use VelocityTrackerCompat where possible.
                mVelocityTracker.computeCurrentVelocity(1000);
            }
        } else {
            //noting
        }
    }

    /**
     * DT (S1케이스) 스트리밍 새로고침 데이터 호출
     * @param context
     * @param psnlCurationType
     */
    private void refreshRTSData(Context context, HomeActivity.PsnlCurationType psnlCurationType) {
        new BaseAsyncController<SectionContentList>(context) {
            private HomeActivity.PsnlCurationType loadType;

            @Inject
            private RestClient restClient;

            @Override
            protected void onPrepare(Object... params) throws Exception {
                loadType = (HomeActivity.PsnlCurationType) params[0];
            }

            @Override
            protected SectionContentList process() throws Exception {
                return restClient.getForObject(ServerUrls.getHttpRoot() + ServerUrls.REST.HyperPersonalizedUrl + ServerUrls.REST.RCMTYPE + loadType.toString() , SectionContentList.class);
            }

            @Override
            protected void onSuccess(SectionContentList result) throws Exception {
                adapter.refreshInfoRTS(result);

                mRecyclerListCommand.scrollToPosition(0);
                mRecyclerListCommand.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //저사양 단말에서는 오토 스크롤링이 없다.
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                            //doing
                            mLayoutManager.smoothScrollToPosition(mRecyclerListCommand, null, mRecyclerListCommand.getAdapter().getItemCount());
                        } else {
                            //noting
                        }

                        mIsCurrent = true;
                    }
                },1500);
            }

            @Override
            protected void onError(Throwable e) {
                Ln.e(e);
            }
        }.execute(psnlCurationType);
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
