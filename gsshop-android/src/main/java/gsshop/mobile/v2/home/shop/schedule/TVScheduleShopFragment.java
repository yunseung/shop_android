/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.schedule;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.pattern.mvc.BaseAsyncController;
import com.gsshop.mocha.pattern.mvc.Model;
import com.gsshop.mocha.ui.util.ViewUtils;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nshmura.snappysmoothscroller.SnapType;
import com.nshmura.snappysmoothscroller.SnappySmoothScroller;
import com.nshmura.snappysmoothscroller.StaggeredGridLayoutScrollVectorDetector;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.AbstractBaseActivity;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.home.GroupSectionList;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.HomeGroupInfo;
import gsshop.mobile.v2.home.TopSectionList;
import gsshop.mobile.v2.home.shop.renewal.flexible.SchMapMutxxxVH;
import gsshop.mobile.v2.home.shop.schedule.model.BroadAlarmResult;
import gsshop.mobile.v2.home.shop.schedule.model.Product;
import gsshop.mobile.v2.home.shop.schedule.model.SchedulePrd;
import gsshop.mobile.v2.home.shop.schedule.model.TVScheduleModel;
import gsshop.mobile.v2.home.shop.schedule.viewholder.DateViewHolder;
import gsshop.mobile.v2.home.shop.schedule.viewholder.TLineBaseViewHolder;
import gsshop.mobile.v2.home.shop.schedule.viewholder.TLineLiveViewHolder;
import gsshop.mobile.v2.home.shop.schedule.viewholder.TLinePrdViewHolder_ab_bcv;
import gsshop.mobile.v2.home.shop.schedule.viewholder.TimeLineViewHolder;
import gsshop.mobile.v2.home.shop.tvshoping.MainShopFragment;
import gsshop.mobile.v2.library.quickreturn.QuickReturnRecyclerViewOnScrollListener;
import gsshop.mobile.v2.library.quickreturn.QuickReturnType;
import gsshop.mobile.v2.library.quickreturn.library.QuickReturnUtils;
import gsshop.mobile.v2.support.ui.BugFixedStaggeredGridLayoutManager;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog;
import gsshop.mobile.v2.support.ui.RecyclerItemClickListener;
import gsshop.mobile.v2.util.CustomToast;
import gsshop.mobile.v2.util.DataUtil;
import gsshop.mobile.v2.util.NetworkUtils;
import gsshop.mobile.v2.util.StringUtils;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;
import static com.blankj.utilcode.util.ConvertUtils.dp2px;
import static com.blankj.utilcode.util.EmptyUtils.isEmpty;
import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;
import static gsshop.mobile.v2.MainApplication.useNativeProduct;
import static gsshop.mobile.v2.ServerUrls.WEB.SCH_LIVE_CLICK;
import static gsshop.mobile.v2.ServerUrls.WEB.SCH_MYSHOP_CLICK;
import static gsshop.mobile.v2.ServerUrls.WEB.SCH_NAVI_NEXT_DAY;
import static gsshop.mobile.v2.ServerUrls.WEB.SCH_NAVI_PRE_DAY;
import static gsshop.mobile.v2.ServerUrls.WEB.SCH_NAVI_TODAY;
import static gsshop.mobile.v2.ServerUrls.WEB.SCH_RIGHT_ONAIR_PRD;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleBroadAlarmDialogFragment.makeBroadAlarmConfirmDialog;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleViewType.TEXT_PRD_LIVE;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleViewType.TEXT_SCHEDULE_DATE;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleViewType.TEXT_SCHEDULE_NEXT_LINK;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleViewType.TEXT_SCHEDULE_PREV_LINK;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleViewType.TEXT_SPACE;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleViewType.TYPE_PRD_BAN_W540;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleViewType.TYPE_PRD_LIVE;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleViewType.TYPE_PRD_LIVE_AB_BCV;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleViewType.TYPE_PRD_MAIN;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleViewType.TYPE_PRD_MAIN_AB_BCV;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleViewType.TYPE_SCHEDULE_DATE;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleViewType.TYPE_SCHEDULE_NEXT_LINK;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleViewType.TYPE_SCHEDULE_PREV_LINK;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleViewType.TYPE_TIME_LINE_ON_AIR;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleViewType.TYPE_TIME_LINE_THUMB;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleViewType.viewTypeMap;
import static gsshop.mobile.v2.menu.BaseTabMenuActivity.TV_SCHEDULE_NAVI_ID;
import static gsshop.mobile.v2.web.WebUtils.BROADTYPE_PARAM_KEY;

/**
 * TV 편셩표 매장.
 */
public class TVScheduleShopFragment extends MainShopFragment {

    private static final String ARG_PARAM_POSITION = "_arg_param_position";

    private static final int LIVE_TOP_MARGIN;
    private static final int LIVE_BOTTOM_MARGIN;

    private static final int TIME_LINE_EVENT_EMPTY_SPACE;

    /**
     * 편성상품영역 스크롤시 편성요약상품 싱크를 위한 상하단 기준선
     */
    private static final int SYNC_TOP_POSITION;
    private static final int SYNC_BOTTOM_POSITION;

    static {
        LIVE_TOP_MARGIN = MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.tv_scd_live_top_margin);
        LIVE_BOTTOM_MARGIN = MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.tv_scd_live_bottm_margin);
        TIME_LINE_EVENT_EMPTY_SPACE = MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.tv_scd_time_line_event_empty_space);
        SYNC_TOP_POSITION = MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.tv_scd_prd_sync_top_position);
        SYNC_BOTTOM_POSITION = MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.tv_scd_prd_sync_bottom_position);
    }

    private Context context;

    /**
     * date view
     */
    @InjectView(R.id.frame_date)
    View dateView;

    /**
     * date
     */
    @InjectView(R.id.recycler_tv_schedule_date)
    RecyclerView dateRecycler;

//    @InjectView(R.id.check_tv_schedule_live)
//    private CheckBox liveCheck;
//
//    @InjectView(R.id.text_tv_schedule_live_title)
//    private TextView liveTitleText;
//
//    @InjectView(R.id.text_tv_schedule_live_phrase)
//    private TextView livePhraseText;

    @InjectView(R.id.btn_schedule_live)
    private CheckBox mCheckLive;

    @InjectView(R.id.btn_schedule_shop)
    private CheckBox mCheckData;

    @InjectView(R.id.btn_schedule_shoppy)
    private View mViewShoppy;

    @InjectView(R.id.btn_scheduler)
    private View mViewSchedule;

    private final AtomicBoolean isAddedItemDecoration = new AtomicBoolean(false);

    /**
     * time line
     */
    @InjectView(R.id.recycler_tv_schedule_timeline)
    RecyclerView timeLineRecycler;

    @InjectView(R.id.recycler_tv_schedule_product)
    RecyclerView productRecycler;

    /**
     * refresh
     */
    @InjectView(R.id.flexible_refresh_layout)
    View mRefreshView;

    @InjectView(R.id.btn_refresh)
    FrameLayout mBtnRefresh;

    @InjectView(R.id.btn_go_web)
    FrameLayout mBtnGoWeb;

    /**
     * siren
     */
    @InjectView(R.id.frame_tv_schedule_siren)
    View sirentView;
    @InjectView(R.id.btn_siren)
    ImageView sirenButton;
    @InjectView(R.id.tooltip_siren)
    ImageView tooltipSiren;

    @InjectView(R.id.btn_tv_schedule_top)
    ImageView topButton;

    @InjectView(R.id.frame_tv_schedule_count)
    View countView;

    @InjectView(R.id.view_tv_schedule_bottom)
    View bottomView;

    private int dateSelectedPosition = -1;
    private int eventSelectedPosition = -1;
    private boolean onAirVisible = true;

    /**
     * 리사이클러뷰 간 포지션 연동을 위한 맵
     */
    private Map<String, Integer> productMap = new HashMap<String, Integer>();
    private Map<String, Integer> timeLineMap = new HashMap<String, Integer>();

    /**
     * 날짜영역 맵
     */
    private Map<String, Integer> dateMap = new HashMap<String, Integer>();

    /**
     * 편성상품 날짜영역 오프셋 높이(dp)
     * 35(상단여백) - 27 = 8dp를 만들기 위함
     */
    private static final float PRD_TIME_OFFSET = -27f;

    /**
     * 1.편성요약 클릭->2.편성상품 자동 스크롤됨->3.편성요약 다시 자동 스크롤됨
     * 3번을 막기 위한 플래그
     */
    private boolean skipTimeLineSync = false;

    /**
     * skipTimeLineSync 플래그 처리를 위한 변수
     */
    private Handler skipTimeLineSyncHandler;
    private Runnable skipTimeLineSyncRunnable;

    /**
     * 편성표 종류
     */
    public enum ScheduleBroadType {
        LIVE, DATA
    }

    public static ScheduleBroadType scheduleBroadType = ScheduleBroadType.LIVE;

    /**
     * 배너에서 호출되었는지 여부
     * (배너호출 : 편성표 강제 갱신, 탭클릭:화면유지)
     */
    public static boolean isCalledFromBanner;

    // quick return
    private QuickReturnRecyclerViewOnScrollListener timeLineScrollListener;
    private QuickReturnRecyclerViewOnScrollListener productScrollListener;
    private TopSectionList sectionList = null;
    private int mMinFooterTranslation = 0;
    private boolean isPositionCheck;

    public static TVScheduleShopFragment newInstance(int position) {
        //Ln.i("position : " + position);
        TVScheduleShopFragment fragment = new TVScheduleShopFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPosition = getArguments().getInt(ARG_PARAM_POSITION);
        }

        HomeGroupInfo homeGroupInfo = AbstractBaseActivity.getHomeGroupInfo();
        GroupSectionList groupSectionList = homeGroupInfo.groupSectionList.get(0);
        sectionList = groupSectionList.sectionList.get(mPosition); // 홈 하위 하나의 인덱스 요청
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tv_schedule_shop, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /**
         * 편성상품, 편성요약 싱크용 핸들러, 러너블 등록
         */
        skipTimeLineSyncHandler = new Handler();
        skipTimeLineSyncRunnable = () -> skipTimeLineSync = false;

        ViewUtils.hideViews(dateView, mRefreshView, sirentView, topButton, countView);
        // refresh
        mBtnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawFragment();
            }
        });
        // 웹으로 보기 버튼 셋팅(버튼 클릭시 GS SHOP mobile web 으로 간다.)
        mBtnGoWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goWebIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ServerUrls.MOBILE_WEB));
                startActivity(goWebIntent);
            }
        });

        // siren
        //사이렌 버튼 클릭이벤트 정의
        sirentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //사이렌 웹뷰 페이지 호출
                ((HomeActivity) getActivity()).goSirenPage();
            }
        });

        // quick return
        // 숭겨질 상단 레이아웃의 세로 사이즈
        int headerHeight = getResources().getDimensionPixelSize(R.dimen.main_header_height);
        // 숭겨질 하단 레이아웃의 세로 사이즈
        int footerHeight = getResources().getDimensionPixelSize(R.dimen.tab_menu_height);
        int indicatorHeight = QuickReturnUtils.dp2px(getActivity(), 0);
        int headerTranslation = -headerHeight + indicatorHeight;
        mMinFooterTranslation = -footerHeight + indicatorHeight;

        // 퀵 리턴뷰 리스너 설정 싸이렌이 없어지고 그 자리에 CSP 레이아웃이 들어간다.
        int defaultItemHeight = getResources().getDimensionPixelSize(R.dimen.tv_scd_time_line_event_default_height);
        timeLineScrollListener = new QuickReturnRecyclerViewOnScrollListener.Builder(QuickReturnType.FOOTER)
                .footer(getActivity(), mCoordinator.getFooterLayout(), topButton, countView, mCoordinator.getCspLayout())
                .minHeaderTranslation(headerTranslation)
                .minFooterTranslation(-mMinFooterTranslation).defaultItemHeight(defaultItemHeight).isSnappable(true).build();

        defaultItemHeight = getResources().getDimensionPixelSize(R.dimen.tv_scd_product_default_height);
        productScrollListener = new QuickReturnRecyclerViewOnScrollListener.Builder(QuickReturnType.FOOTER)
                .footer(getActivity(), mCoordinator.getFooterLayout(), topButton, countView, mCoordinator.getCspLayout())
                .minHeaderTranslation(headerTranslation)
                .minFooterTranslation(-mMinFooterTranslation).defaultItemHeight(defaultItemHeight).isSnappable(true).build();

        /**
         * date recycler
         */
        if (isAddedItemDecoration.compareAndSet(false, true)) {
            dateRecycler.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    super.getItemOffsets(outRect, view, parent, state);
                    outRect.left = dp2px(5.0f);
                    outRect.right = dp2px(5.0f);
                }
            });
        }
        dateRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        dateRecycler.setAdapter(new DateAdapter());
        dateRecycler.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //키바나 수집 로그로 앱크래쉬 방지  (java.lang.ArrayIndexOutOfBoundsException: length=73; index=-1)
                if (dateSelectedPosition == position || position < 0) {
                    return;
                }

                //자빠져도 혼자 자 빠져라 ,
                try {
                    // 날짜 네베게이션 효율 코드
                    if (dateRecycler != null && dateRecycler.getAdapter() != null) {
                        int todayIndex = getTodayIndex(((DateAdapter) dateRecycler.getAdapter()).getDates());
                        //-1 반환되면 오늘을 모찾은거다 , 설마?
                        if (todayIndex != -1) {
                            //오늘
                            if (todayIndex == position) {
                                ((HomeActivity) context).setWiseLogHttpClient(makeWiselogUrl(SCH_NAVI_TODAY));
                            }//과거
                            else if (todayIndex > position) {
                                ((HomeActivity) context).setWiseLogHttpClient(makeWiselogUrl(SCH_NAVI_PRE_DAY) + (todayIndex - position));
                            }//미래
                            else //if(todayIndex < position)
                            {
                                ((HomeActivity) context).setWiseLogHttpClient(makeWiselogUrl(SCH_NAVI_NEXT_DAY) + (position - todayIndex));
                            }
                        }
                    }

                } catch (Exception e) {
                    Ln.e(e);
                }

                dateSelectedPosition = -1;
                TVScheduleModel.ScheduleDate date = ((DateAdapter) dateRecycler.getAdapter()).getDates().get(position);
                if (!TextUtils.isEmpty(date.apiUrl)) {
                    onAirVisible = true;
                    TLineLiveViewHolder.isStartAnimation = false;
                    //재생중인 생방송 중지
                    EventBus.getDefault().post(new Events.FlexibleEvent.SchLivePlayEvent(false, -1, false));
                    new TvScheduleFetchController(getContext()).execute(date.apiUrl, date.apiParm, false, false, "date");
                }
            }
        }));
        dateRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int first = ((LinearLayoutManager) dateRecycler.getLayoutManager()).findFirstVisibleItemPosition();
                int firstComplete = ((LinearLayoutManager) dateRecycler.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                int last = ((LinearLayoutManager) dateRecycler.getLayoutManager()).findLastVisibleItemPosition();
                int lastComplete = ((LinearLayoutManager) dateRecycler.getLayoutManager()).findLastCompletelyVisibleItemPosition();
//                Ln.i("first: " + first + ", fist complete: " + firstComplete + ", last: " + last + ", last complete: " + lastComplete + ", count: " + recyclerView.getAdapter().getItemCount());

            }
        });

        /**
         * time line recycler
         */
        timeLineRecycler.setVisibility(View.GONE);
        timeLineRecycler.setLayoutManager(new BugFixedStaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        timeLineRecycler.setAdapter(new TimeLineAdapter());
        timeLineRecycler.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TVScheduleModel.ScheduleTimeLineThumb thumb = null;
                try {
                    thumb = ((TimeLineAdapter) timeLineRecycler.getAdapter()).getThumbs().get(position);
                } catch (ArrayIndexOutOfBoundsException e) {
                    Ln.e(e.getMessage());
                }

                if (thumb == null) {
                    return;
                }

                boolean isNext = false;
                switch (viewTypeMap.get(thumb.viewType)) {
                    case TYPE_SCHEDULE_NEXT_LINK:
                        isNext = true;
                        new TvScheduleFetchController(getContext()).execute(thumb.apiUrl, thumb.apiParam, isNext, false, "more_next");
                        break;
                    case TYPE_SCHEDULE_PREV_LINK:
                        String param = "more_pre_from_timeline";
                        if (isIncludePrevLink()) {
                            param = "more_pre";
                        }
                        new TvScheduleFetchController(getContext()).execute(thumb.apiUrl, thumb.apiParam, isNext, false, param);
                        break;
                    case TYPE_TIME_LINE_ON_AIR:
                    case TYPE_TIME_LINE_THUMB:
                        eventSelectedPosition = position;
                        timeLineRecycler.getAdapter().notifyDataSetChanged();
                        //스크롤중인 상태에서 포시션 이동시 false 세팅하여 스크롤 멈춤
                        ((BugFixedStaggeredGridLayoutManager) productRecycler.getLayoutManager()).setScrollEnabled(false);
                        if (thumb.product != null) {
                            syncProductPosition(thumb.broadStartDate + thumb.product.prdId, true, 0, 0);
                        }
                        //오른쪽 타임 라인 상품 클릭 효율코드 생방송과 안생방송
                        //생방송 기준 과거와 미래는 같게 한다고 햇는데??? 확인 하자
                        if (TYPE_TIME_LINE_ON_AIR == viewTypeMap.get(thumb.viewType)) {
                            ((HomeActivity) context).setWiseLogHttpClient(makeWiselogUrl(SCH_RIGHT_ONAIR_PRD));
                        }
                        break;


                    case TYPE_SCHEDULE_DATE:

                }
            }
        }));
        timeLineRecycler.addOnScrollListener(timeLineScrollListener);
        timeLineRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                boolean hasStarted = newState == SCROLL_STATE_DRAGGING;
                boolean hasEnded = newState == SCROLL_STATE_IDLE;
                BugFixedStaggeredGridLayoutManager layoutManger = (BugFixedStaggeredGridLayoutManager) productRecycler.getLayoutManager();
                switch (newState) {
                    case SCROLL_STATE_DRAGGING: // start
                        Ln.i("SCROLL_STATE_DRAGGING");
                        if (layoutManger.getScrollEnabled()) {
                            layoutManger.setScrollEnabled(false);
                            resetScrollEnable();
                        }
                        break;
                }

            }

            private void resetScrollEnable() {
                new Handler().postDelayed(() -> {
                    Ln.i("SCROLL_STATE_DRAGGING-reset");
                    BugFixedStaggeredGridLayoutManager layoutManger = (BugFixedStaggeredGridLayoutManager) productRecycler.getLayoutManager();
                    layoutManger.setScrollEnabled(true);
                }, 400);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int firstVisiblePosition = ((StaggeredGridLayoutManager) timeLineRecycler.getLayoutManager()).findFirstVisibleItemPositions(null)[0];
                int lastVisiblePosition = ((StaggeredGridLayoutManager) timeLineRecycler.getLayoutManager()).findLastVisibleItemPositions(null)[0];
                if (isPositionCheck) {
                    if (firstVisiblePosition == 0) {
                        TVScheduleModel.ScheduleTimeLineThumb thumb = ((TimeLineAdapter) timeLineRecycler.getAdapter()).getThumbs().get(firstVisiblePosition);
                        if (thumb.apiUrl != null) {
                            String param = "more_pre_from_timeline";
                            if (isIncludePrevLink()) {
                                param = "more_pre";
                            }
                            new TvScheduleFetchController(getContext()).execute(thumb.apiUrl, thumb.apiParam, false, false, param);
                        }

                    } else if (lastVisiblePosition == timeLineRecycler.getAdapter().getItemCount() - 1) {
                        TVScheduleModel.ScheduleTimeLineThumb thumb = ((TimeLineAdapter) timeLineRecycler.getAdapter()).getThumbs().get(lastVisiblePosition);
                        if (thumb.apiUrl != null) {
                            new TvScheduleFetchController(getContext()).execute(thumb.apiUrl, thumb.apiParam, true, false, "more_next");
                        }
                    }
                }
            }

        });
        timeLineRecycler.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);

                int position = parent.getChildAdapterPosition(view);
                int viewType = parent.getAdapter().getItemViewType(position);

                if (viewType == TYPE_TIME_LINE_ON_AIR || viewType == TYPE_TIME_LINE_THUMB) {
                    outRect.top = TIME_LINE_EVENT_EMPTY_SPACE;
                    outRect.left = TIME_LINE_EVENT_EMPTY_SPACE;
                    outRect.right = TIME_LINE_EVENT_EMPTY_SPACE;
                    if (position == parent.getAdapter().getItemCount() - 1) {
                        outRect.bottom = TIME_LINE_EVENT_EMPTY_SPACE;
                    }
                }
            }
        });


        /**
         * product recycler
         */
        productRecycler.setLayoutManager(new BugFixedStaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL) {
            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                SnappySmoothScroller scroller = new SnappySmoothScroller.Builder()
                        .setPosition(position)
                        .setSnapType(SnapType.START)
                        .setScrollVectorDetector(new StaggeredGridLayoutScrollVectorDetector(this))
                        .build(recyclerView.getContext());

                startSmoothScroll(scroller);
            }
        });
        productRecycler.setAdapter(new TLinePrdAdapter(getActivity()));
        productRecycler.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SchedulePrd prd = ((TLinePrdAdapter) productRecycler.getAdapter()).getProduct().get(position);
                boolean isNext = false;
                switch (viewTypeMap.get(prd.viewType)) {
                    case TYPE_SCHEDULE_NEXT_LINK:
                        isNext = true;
                        new TvScheduleFetchController(getContext()).execute(prd.broadLink.apiUrl, prd.broadLink.apiParam, isNext, false, "more_next");
                        break;
                    case TYPE_SCHEDULE_PREV_LINK:
                        new TvScheduleFetchController(getContext()).execute(prd.broadLink.apiUrl, prd.broadLink.apiParam, isNext, false, "more_pre");
                        break;
                    case TYPE_PRD_BAN_W540:
                        String finalImgLink = prd.liveBanner.linkUrl;
                        // GTM AB Test 클릭이벤트 전달
                        if (finalImgLink != null && finalImgLink.length() > 4) {
                            WebUtils.goWeb(context, finalImgLink);
                        }
                        break;
                }
            }
        }));
        productRecycler.addOnScrollListener(productScrollListener);
        productRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                //스크롤 멈춘시점에 이미지 캐싱 진행
                if (useNativeProduct) {
                    if (newState == SCROLL_STATE_IDLE) {
                        EventBus.getDefault().post(new Events.ImageCacheStartEvent(sectionList.navigationId));
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (productRecycler.getLayoutManager().isSmoothScrolling()) {
                    return;
                }
                //Ln.e("onScrolled (dx,dy) : " + dx + "," + dy);
                //Ln.i("mCoordinator: " + ViewHelper.getTranslationY(mCoordinator.getFooterLayout()));
                //Ln.i("mCoordinator: " + ViewHelper.getTranslationY(bottomView));

                boolean found = false;
                for (int i = 0; i < productRecycler.getChildCount(); i++) {
                    View view = productRecycler.getChildAt(i);
                    TLineBaseViewHolder vh = (TLineBaseViewHolder) productRecycler.getChildViewHolder(view);

                    if (vh.getItemViewType() == TYPE_PRD_LIVE || vh.getItemViewType() == TYPE_PRD_MAIN || vh.getItemViewType() == TYPE_PRD_LIVE_AB_BCV || vh.getItemViewType() == TYPE_PRD_MAIN_AB_BCV) {

                        SchedulePrd prd;

                        if (vh.getItemViewType() == TYPE_PRD_LIVE_AB_BCV || vh.getItemViewType() == TYPE_PRD_MAIN_AB_BCV) {
                            TLinePrdViewHolder_ab_bcv prdVh = (TLinePrdViewHolder_ab_bcv) vh;
                            prd = prdVh.getPrdData();
                        } else {
                            SchMapMutxxxVH prdVh = (SchMapMutxxxVH) vh;
                            prd = prdVh.getPrdData();
                        }


                        if (view.getTop() >= SYNC_TOP_POSITION && view.getTop() <= SYNC_BOTTOM_POSITION) {
                            //편성요약 싱크
                            if (prd.product != null) {
                                if (!skipTimeLineSync) {
                                    syncTimeLinePosition(prd.broadStartDate + prd.product.prdId);
                                }
                            }
                            //날짜 싱크
                            if (dateSelectedPosition >= 0) {
                                syncDatePosition(prd.datePosition);
                            }
                        }
                    }

                    // onair view show/hide + AB테스트 뷰타입일경우도 혹시 몰라서 추가
                    if (vh.getItemViewType() == TYPE_PRD_LIVE || vh.getItemViewType() == TYPE_PRD_LIVE_AB_BCV) {
                        if (view.getTop() < productRecycler.getHeight() - LIVE_BOTTOM_MARGIN && view.getBottom() > LIVE_TOP_MARGIN) {
                            //i("view.getTop(): " + view.getTop() + ", view.getBottom(): " + view.getBottom() + ", productRecycler.getTop(): " + productRecycler.getTop() + ", productRecycler.getBottom(): " + productRecycler.getBottom() + ", productRecycler.getHeight(): " + productRecycler.getHeight() + ", LIVE_BOTTOM_MARGIN: " + LIVE_BOTTOM_MARGIN + ", LIVE_TOP_MARGIN: " + LIVE_TOP_MARGIN);
                            found = true;
                        }
                    }
                }

                int firstVisiblePosition = ((StaggeredGridLayoutManager) productRecycler.getLayoutManager()).findFirstVisibleItemPositions(null)[0];
                int lastVisiblePosition = ((StaggeredGridLayoutManager) productRecycler.getLayoutManager()).findLastVisibleItemPositions(null)[0];
                if (isPositionCheck) {
                    if (firstVisiblePosition == 0) {
                        SchedulePrd prd = ((TLinePrdAdapter) productRecycler.getAdapter()).getProduct().get(0);
                        if (prd.broadLink != null) {
                            new TvScheduleFetchController(getContext()).execute(prd.broadLink.apiUrl, prd.broadLink.apiParam, false, false, "more_pre");
                        }
                    } else if (lastVisiblePosition == productRecycler.getAdapter().getItemCount() - 1) {
                        SchedulePrd prd = ((TLinePrdAdapter) productRecycler.getAdapter()).getProduct().get(lastVisiblePosition);
                        if (prd.broadLink != null) {
                            //연속적으로 2번 호출되는데 isPositionCheck 해당 플레그가 아이템이 다 그려지면 true 변경된다.
                            //지금 시점에 isPositionCheck=false 막고, TvScheduleFetchController 완료 이후에 아이템이 다 그려지면 그때 true 풀어준다.
                            isPositionCheck = false;
                            new TvScheduleFetchController(getContext()).execute(prd.broadLink.apiUrl, prd.broadLink.apiParam, true, false, "more_next");
                        }
                    }

                }

            }
        });

        drawFragment();

        // 메모리에 데이터가 날아간경우 데이터 복구
        if (savedInstanceState != null) {
            Bundle restoreData = savedInstanceState.getBundle("restore");
            if (mPosition == restoreData.getInt("position")) {
                HomeActivity homeActivity = (mActivity);
                homeActivity.GetHomeGroupListInfo();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBundle("restore", saveState());
    }

    /**
     * 앱내 데이터가 날아가거나 백그라운드로 앱이 넘어갔을때 데이터 복구를 위해 사용
     *
     * @return Bundle
     */
    private Bundle saveState() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("restore", true);
        bundle.putInt("position", mCoordinator.getCurrentViewPosition());
        return bundle;
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    /**
     * 이전편성보기 셀이 리사이클러 뷰에 포함되어 있는지 확인한다.
     *
     * @return 이전편성보기 셀이 포함되어 있는지 여부
     */
    private boolean isIncludePrevLink() {
        View view = productRecycler.getChildAt(0);
        if (view != null) {
            TLineBaseViewHolder vh = (TLineBaseViewHolder) productRecycler.getChildViewHolder(view);
            return vh.getItemViewType() == TYPE_SCHEDULE_PREV_LINK;
        } else {
            return false;
        }
    }

    /**
     * 탭메뉴(GNB), 날짜(상단), 이전편성보기, 편성요약영역(우측) 클릭시
     * 편성상품영역(좌측)의 스크롤 포시션을 변경한다.
     *
     * @param key                  broadStartDate + prdid
     * @param isSkip               편성상품 변경시 편성요약 싱크를 수행할지 여부
     * @param firstVisiblePosition 리사이클러뷰 상단라인에 걸친 뷰의 인덱스
     * @param offset               오프셋
     */
    private void syncProductPosition(final String key, boolean isSkip, int firstVisiblePosition, int offset) {
        new Handler().postDelayed(() -> {
            //포시션 이동 후 값 true로 원복
            ((BugFixedStaggeredGridLayoutManager) productRecycler.getLayoutManager()).setScrollEnabled(true);

            //편성상품 영역의 포지션 이동이 편성요약 포지션에 영향을 주지 않도록 함
            setSkipTimeLineSync(isSkip);

            Integer position = productMap.get(key);
            if (position != null) {
                if ("date".equals(key)) {
                    Integer livePos = productMap.get("live");
                    if (livePos != null) {
                        //생방송이 존재하면 생방송 포지션으로 이동
                        ((BugFixedStaggeredGridLayoutManager) productRecycler.getLayoutManager()).scrollToPositionWithOffset(livePos, 0);
                    } else {
                        //생방송이 없으면 첫번째 방송시간으로 이동 (단, 공영상품인 경우는 "public" 위치로 이동)
                        //offset = DisplayUtils.convertDpToPx(getActivity(), PRD_TIME_OFFSET);
                        Integer defaultPos = isNotEmpty(productMap.get("public")) ? productMap.get("public") : position.intValue();
                        ((BugFixedStaggeredGridLayoutManager) productRecycler.getLayoutManager()).scrollToPositionWithOffset(defaultPos, 0);
                    }
                } else if ("more_pre".equals(key)
                        || "more_pre_from_timeline".equals(key)
                        || "more_next".equals(key)) {
                    //이전편성표 더보기 클릭시 포지션 이동없이 위치 유지
                    ((BugFixedStaggeredGridLayoutManager) productRecycler.getLayoutManager()).scrollToPositionWithOffset(position.intValue() + firstVisiblePosition, offset);
                } else {
                    //편성요약 클릭시 편성상품 위치 이동
                    int newPosition = position.intValue() + firstVisiblePosition;

                    ((BugFixedStaggeredGridLayoutManager) productRecycler.getLayoutManager()).smoothScrollToPosition(productRecycler, newPosition);
                }
            }
        }, 200);
    }

    /**
     * 편성요약 특정상품 클릭시 해당 편성상품이 상단으로 스크롤되고
     * 이로 인해 다시 편성요약 특정상품이 최상단으로 스크롤되는 현상을 방지한다.
     *
     * @param isSkip if true, skip sync
     */
    private void setSkipTimeLineSync(boolean isSkip) {
        skipTimeLineSync = isSkip;

        if (skipTimeLineSyncHandler != null) {
            //1초이내에 요청이 다시 들어오면 이전에 등록된 러너블은 취소한다.
            skipTimeLineSyncHandler.removeCallbacks(skipTimeLineSyncRunnable);
        }
        skipTimeLineSyncHandler.postDelayed(skipTimeLineSyncRunnable, 1000);
    }

    /**
     * 편성상품영역(좌측) 스크롤시 편성요약영역(우측) 포시션을 변경한다.
     *
     * @param key broadStartDate
     */
    private void syncTimeLinePosition(final String key) {
        new Handler().postDelayed(() -> {
            Integer position = timeLineMap.get(key);
            if (position != null) {
                ((BugFixedStaggeredGridLayoutManager) timeLineRecycler.getLayoutManager()).scrollToPositionWithOffset(position.intValue(), 0);
                eventSelectedPosition = position;
                timeLineRecycler.getAdapter().notifyDataSetChanged();
            }
        }, 200);
    }

    /**
     * 편성상품영역(좌측) 스크롤시 날짜영역(상단) 포시션을 변경한다.
     *
     * @param position 날짜 포지션
     */
    private void syncDatePosition(int position) {
        if (dateSelectedPosition != position && (dateSelectedPosition >= 0 && position >= 0)) {
//            Ln.i("dateSelectedPosition: " + dateSelectedPosition + ", position: " + position);
            dateSelectedPosition = position;
            dateRecycler.getAdapter().notifyDataSetChanged();
            ((LinearLayoutManager) dateRecycler.getLayoutManager()).scrollToPositionWithOffset(dateSelectedPosition - 1, 0);
        }
    }

    @Override
    public void drawFragment() {
        dateSelectedPosition = -1;
        onAirVisible = true;
        TLineLiveViewHolder.isStartAnimation = false;

        //디폴트는 LIVE
        String newValue;
        if (scheduleBroadType == ScheduleBroadType.DATA) {
            newValue = ScheduleBroadType.DATA.name();
        } else {
            newValue = ScheduleBroadType.LIVE.name();
        }
        String params = StringUtils.replaceUriParameter(
                Uri.parse("?" + sectionList.sectionLinkParams), BROADTYPE_PARAM_KEY, newValue).toString();

        new TvScheduleFetchController(getActivity()).execute(sectionList.sectionLinkUrl, params.substring(1), false, false, "date");
    }

    private void requestApi(String url, String param) {
        dateSelectedPosition = -1;
        onAirVisible = true;
        TLineLiveViewHolder.isStartAnimation = false;
        new TvScheduleFetchController(getActivity()).execute(url, param, false, false, "date");
    }

    /**
     * fetch controller class
     */

    private class TvScheduleFetchController extends BaseAsyncController<TVScheduleModel> {
        private String url;
        private String params;
        private boolean isNext;
        private boolean isCacheData = false;
        private String firstFocusCell;  //화면로딩 후 처음 노출할 셀을 위한 구분자
        private final Context activityContext;

        @Inject
        private RestClient restClient;

        private TvScheduleFetchController(Context activityContext) {
            super(activityContext);
            this.activityContext = activityContext;
        }

        @Override
        protected void onPrepare(Object... params) {
            if (getUserVisibleHint()) {
                if (dialog != null) {
                    dialog.dismiss();
                    dialog.setCancelable(false);
                    dialog.show();
                }
            }
            this.url = (String) params[0];
            this.params = (String) params[1];
            this.isNext = (boolean) params[2];
            this.isCacheData = (boolean) params[3];
            this.firstFocusCell = (String) params[4];
            ViewUtils.hideViews(mRefreshView);
            ((BugFixedStaggeredGridLayoutManager) productRecycler.getLayoutManager()).setScrollEnabled(false);
            ((BugFixedStaggeredGridLayoutManager) timeLineRecycler.getLayoutManager()).setScrollEnabled(false);
        }

        @Override
        protected void onFinally() {
            super.onFinally();
            ((BugFixedStaggeredGridLayoutManager) productRecycler.getLayoutManager()).setScrollEnabled(true);
            ((BugFixedStaggeredGridLayoutManager) timeLineRecycler.getLayoutManager()).setScrollEnabled(true);
        }

        @Override
        protected TVScheduleModel process() throws Exception {
            return (TVScheduleModel) DataUtil.getData(context, restClient, TVScheduleModel.class,
                    isCacheData, true, url,
                    params + "&reorder=true", sectionList.sectionName);

        }

        @Override
        protected void onCancelled() {
            ((BugFixedStaggeredGridLayoutManager) productRecycler.getLayoutManager()).setScrollEnabled(true);
            ((BugFixedStaggeredGridLayoutManager) timeLineRecycler.getLayoutManager()).setScrollEnabled(true);
        }

        @Override
        protected void onSuccess(final TVScheduleModel model) throws Exception {
            super.onSuccess(model);
            ViewUtils.showViews(dateView);
            /**
             * date view
             */
            if (scheduleBroadType == ScheduleBroadType.DATA) {
                mCheckLive.setChecked(false);
                mCheckData.setChecked(true);
//                liveCheck.setChecked(false);
//                liveTitleText.setText(model.dataBroadInfo.broadLeftText);
//                livePhraseText.setText(model.dataBroadInfo.broadRightText);
            } else {
                mCheckLive.setChecked(true);
                mCheckData.setChecked(false);
//                liveCheck.setChecked(true);
//                liveTitleText.setText(model.liveBroadInfo.broadLeftText);
//                livePhraseText.setText(model.liveBroadInfo.broadRightText);
            }

            View.OnClickListener listenerClick = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //생방송 재생중 마이샵 클릭시(또는 반대케이스) 재생중인 방송 정지
                    playTVLive(false);

                    if (view.getId() == mCheckLive.getId()) {
//                        liveTitleText.setText(model.liveBroadInfo.broadLeftText);
//                        livePhraseText.setText(model.liveBroadInfo.broadRightText);
                        requestApi(model.liveBroadInfo.apiUrl, model.liveBroadInfo.apiParam);
                        scheduleBroadType = ScheduleBroadType.LIVE;

                        mCheckLive.setChecked(true);
                        mCheckData.setChecked(false);

                        mViewSchedule.setContentDescription("생방송이 선택됨");
                        //와이즈로그 전송
                        ((HomeActivity) activityContext).setWiseLogHttpClient(SCH_LIVE_CLICK);
                    } else {
//                        liveTitleText.setText(model.dataBroadInfo.broadLeftText);
//                        livePhraseText.setText(model.dataBroadInfo.broadRightText);
                        requestApi(model.dataBroadInfo.apiUrl, model.dataBroadInfo.apiParam);
                        scheduleBroadType = ScheduleBroadType.DATA;

                        mCheckLive.setChecked(false);
                        mCheckData.setChecked(true);

                        mViewSchedule.setContentDescription("마이샵이 선택됨");
                        //와이즈로그 전송
                        ((HomeActivity) activityContext).setWiseLogHttpClient(SCH_MYSHOP_CLICK);
                    }
                }
            };

            mCheckLive.setOnClickListener(listenerClick);
            mCheckData.setOnClickListener(listenerClick);
            mViewShoppy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (model.shoppyBroadInfo.apiUrl != null)
                        WebUtils.goWeb(getActivity(), model.shoppyBroadInfo.apiUrl);
                }
            });

            boolean refresh = "Y".equals(model.refreshYn);

            /**
             * schedule date
             */
//            Ln.i("dateSelectedPosition: " + dateSelectedPosition);
            if (dateSelectedPosition == -1) {
                for (int i = 0; i < model.dateList.size(); i++) {
                    if ("Y".equals(model.dateList.get(i).selectedYn)) {
                        dateSelectedPosition = i;
//                        Ln.i("dateSelectedPosition: " + dateSelectedPosition + ", i: " + i);
                        break;
                    }
                }
            }

            //Ln.i("dateSelectedPosition: " + dateSelectedPosition);
            if (refresh) {
                TLineLiveViewHolder.isPlayButtonVisible = true;
                ((DateAdapter) dateRecycler.getAdapter()).setDates(model.dateList);
            }

            dateRecycler.getAdapter().notifyDataSetChanged();
            ((LinearLayoutManager) dateRecycler.getLayoutManager()).scrollToPositionWithOffset(dateSelectedPosition - 1, 0);

            for (int i = 0; i < model.dateList.size(); i++) {
                dateMap.put(model.dateList.get(i).yyyyMMdd, i);
            }


            /**
             * time line
             */
            // date
            TVScheduleModel.ScheduleTimeLineThumb date = new TVScheduleModel.ScheduleTimeLineThumb();
            date.product = new TVScheduleModel.ScheduleTimeLineProduct();
            date.viewType = TEXT_SCHEDULE_DATE;
            date.product.prdName = model.broadDate;
            model.timeLineList.add(0, date);

            // prev link
            if (model.preBroadInfo != null) {
                TVScheduleModel.ScheduleTimeLineThumb prev = new TVScheduleModel.ScheduleTimeLineThumb();
                prev.product = new TVScheduleModel.ScheduleTimeLineProduct();
                prev.viewType = TEXT_SCHEDULE_PREV_LINK;
                prev.apiUrl = model.preBroadInfo.apiUrl;
                prev.apiParam = model.preBroadInfo.apiParam;
                prev.product.prdName = model.preBroadInfo.broadRightText;
                model.timeLineList.add(0, prev);
            }

            // next link
            if (model.nextBroadInfo != null) {
                TVScheduleModel.ScheduleTimeLineThumb next = new TVScheduleModel.ScheduleTimeLineThumb();
                next.product = new TVScheduleModel.ScheduleTimeLineProduct();
                next.viewType = TEXT_SCHEDULE_NEXT_LINK;
                next.apiUrl = model.nextBroadInfo.apiUrl;
                next.apiParam = model.nextBroadInfo.apiParam;
                next.product.prdName = model.nextBroadInfo.broadRightText;
                model.timeLineList.add(next);
            }

            // view type이 없는 timeLine은 제거.
            List<TVScheduleModel.ScheduleTimeLineThumb> thumbs = model.timeLineList;
            for (int i = 0; i < thumbs.size(); i++) {
                if (viewTypeMap.get(thumbs.get(i).viewType) == null) {
                    thumbs.remove(i);
                    i--;
                }
            }

            updateTimeLineThumbList(refresh, model.timeLineList);

            /**
             * product
             */
            updateProduct(model, this.isNext, firstFocusCell);
        }

        /**
         * 편성상품영역과 편성요약영역간 스크롤시 싱크를 맞추기 위한 맵 생성
         */
        private void makeTimeLinePositionData() {
            timeLineMap.clear();
            List<TVScheduleModel.ScheduleTimeLineThumb> thumbs = ((TimeLineAdapter) timeLineRecycler.getAdapter()).getThumbs();
            for (int i = 0; i < thumbs.size(); i++) {
                TVScheduleModel.ScheduleTimeLineThumb thumb = thumbs.get(i);
                if (viewTypeMap.get(thumb.viewType) == TYPE_TIME_LINE_ON_AIR
                        || viewTypeMap.get(thumb.viewType) == TYPE_TIME_LINE_THUMB) {
                    if (thumb.product != null) {
                        String key = thumb.broadStartDate + thumb.product.prdId;
                        if (timeLineMap.get(key) == null) {
                            timeLineMap.put(key, i);
                        }
                    }
                }
            }
        }

        private void updateTimeLineThumbList(boolean refresh, List<TVScheduleModel.ScheduleTimeLineThumb> thumbs) {
            // 방송없음 제거
            for (TVScheduleModel.ScheduleTimeLineThumb thumb : thumbs) {
                if (thumb.viewType.equals("SCH_PRO_NO_DATA")) {
                    thumbs.remove(thumb);
                }
            }
            timeLineRecycler.setSelected(true);
            View topView = timeLineRecycler.getChildAt(1);

            int firstVisiblePosition = ((StaggeredGridLayoutManager) timeLineRecycler.getLayoutManager()).findFirstVisibleItemPositions(null)[0];
            if (refresh) {
                ((TimeLineAdapter) timeLineRecycler.getAdapter()).setThumbs(thumbs);
            } else {
                ((TimeLineAdapter) timeLineRecycler.getAdapter()).addThumbs(thumbs, this.isNext);
            }
            timeLineRecycler.getAdapter().notifyDataSetChanged();
            if (refresh) {
                eventSelectedPosition = getOnAirPosition(((TimeLineAdapter) timeLineRecycler.getAdapter()).getThumbs());

                if (eventSelectedPosition >= 0) {
                    ((StaggeredGridLayoutManager) timeLineRecycler.getLayoutManager()).scrollToPositionWithOffset(eventSelectedPosition, 0);
                    timeLineRecycler.getAdapter().notifyItemChanged(eventSelectedPosition);
                } else {
                    eventSelectedPosition = getFirstThumbPosition(((TimeLineAdapter) timeLineRecycler.getAdapter()).getThumbs());
                    if (eventSelectedPosition >= 0) {
                        new Handler().postDelayed(() -> {
                            ((StaggeredGridLayoutManager) timeLineRecycler.getLayoutManager()).scrollToPositionWithOffset(eventSelectedPosition, 0);
                            eventSelectedPosition++;
                            timeLineRecycler.getAdapter().notifyItemChanged(eventSelectedPosition);
                        }, 60);
                        ((StaggeredGridLayoutManager) timeLineRecycler.getLayoutManager()).scrollToPositionWithOffset(eventSelectedPosition, 0);
                    } else {
                        ((StaggeredGridLayoutManager) timeLineRecycler.getLayoutManager()).scrollToPositionWithOffset(0, 0);
                    }
                }
            } else if (!this.isNext) {
                if (topView == null) {
                    return;
                }
                int offset = topView.getTop();
                if (eventSelectedPosition != -1) {
                    eventSelectedPosition += thumbs.size() - 1;
                }
                int newPosition = firstVisiblePosition + thumbs.size();
                ((StaggeredGridLayoutManager) timeLineRecycler.getLayoutManager()).scrollToPositionWithOffset(newPosition, offset);
            }

            //리사이클러뷰 간 포지션 연동위한 맵 생성
            makeTimeLinePositionData();
        }

        // on air position
        private int getOnAirPosition(List<TVScheduleModel.ScheduleTimeLineThumb> thumbs) {
            for (int i = 0; i < thumbs.size(); i++) {
                TVScheduleModel.ScheduleTimeLineThumb thumb = thumbs.get(i);
                Integer type = viewTypeMap.get(thumb.viewType);
                if (type == TYPE_TIME_LINE_ON_AIR) {
                    return i;
                }
            }
            return -1;
        }

        private int getFirstThumbPosition(List<TVScheduleModel.ScheduleTimeLineThumb> thumbs) {
            for (int i = 0; i < thumbs.size(); i++) {
                TVScheduleModel.ScheduleTimeLineThumb thumb = thumbs.get(i);
                Integer type = viewTypeMap.get(thumb.viewType);
                if (type == TYPE_SCHEDULE_DATE) {
                    return i;
                }
            }
            return -1;
        }


        @Override
        protected void onError(Throwable e) {
            boolean bb = dateRecycler.getAdapter().getItemCount() > 0;
            boolean cc = timeLineRecycler.getAdapter().getItemCount() > 0;
            boolean dd = productRecycler.getAdapter() != null && productRecycler.getAdapter().getItemCount() > 0;
            if (dateRecycler.getAdapter().getItemCount() > 0
                    && timeLineRecycler.getAdapter().getItemCount() > 0
                    && (productRecycler.getAdapter() != null && productRecycler.getAdapter().getItemCount() > 0)) {
                // ignoare;
            } else {
                if (dateRecycler.getAdapter().getItemCount() > 0) {
                    ((DateAdapter) dateRecycler.getAdapter()).getDates().clear();
                    dateRecycler.getAdapter().notifyDataSetChanged();
                }
                if (timeLineRecycler.getAdapter().getItemCount() > 0) {
                    ((TimeLineAdapter) timeLineRecycler.getAdapter()).getThumbs().clear();
                    timeLineRecycler.getAdapter().notifyDataSetChanged();
                }
                if (productRecycler.getAdapter().getItemCount() > 0) {
                    ((TLinePrdAdapter) productRecycler.getAdapter()).getProduct().clear();
                    productRecycler.getAdapter().notifyDataSetChanged();
                }

                ViewUtils.hideViews(dateView);
                ViewUtils.showViews(mRefreshView);
            }

            // 네트워크 에러팝업 중복으로 뜨는 현상 개선 (에러팝업을 SetABTestController에서 띄우지 않고 본 컨트롤러에서 띄운다.)
            super.onError(e);
        }
    }

    /**
     * 편성상품영역 업데이트
     *
     * @param model          TVScheduleModel
     * @param isNext         if true, 다음편성 if false, 이전편성
     * @param firstFocusCell 호출자("date" or "more_pre" or "more_pre_from_timeline" or "more_next")
     */
    private void updateProduct(TVScheduleModel model, boolean isNext, String firstFocusCell) {
        boolean refresh = "Y".equals(model.refreshYn);
        isPositionCheck = false;

        //API에서 생방송이 여러개 내려올 경우 처음 만나는 상품 하나만 적용
        boolean isLiveSet = false;

        for (int i = 0; i < model.broadPrdList.size(); i++) {
            SchedulePrd prd = model.broadPrdList.get(i);
            //정의되지 않은 뷰타입인 경우 리스트에서 제거
            if (viewTypeMap.get(prd.viewType) == null) {
                model.broadPrdList.remove(i);
                i--;
                continue;
            }
            //주상품중 생방송인 경우 뷰타입을 LIVE로 세팅한다.
            if (viewTypeMap.get(prd.viewType) == TYPE_PRD_MAIN) {
                //세팅되지 않은 주상품이 부상품을 포함한 경우 제거
                if (prd.product == null || !"Y".equals(prd.product.mainProductYn)) {
                    model.broadPrdList.remove(i);
                    i--;
                    continue;
                }
                if ("Y".equals(prd.pgmLiveYn) && !isLiveSet) {
                    prd.viewType = TEXT_PRD_LIVE;
                    isLiveSet = true;
                }
            }
        }

        //이전 편성보기
        if (model.preBroadInfo != null) {
            SchedulePrd prev = new SchedulePrd();
            prev.viewType = TEXT_SCHEDULE_PREV_LINK;
            prev.broadLink = model.preBroadInfo;
            model.broadPrdList.add(0, prev);
        }

        //다음 편성보기
        if (model.nextBroadInfo != null) {
            SchedulePrd next = new SchedulePrd();
            next.viewType = TEXT_SCHEDULE_NEXT_LINK;
            next.broadLink = model.nextBroadInfo;
            model.broadPrdList.add(next);
        } else {
            //하단 여백
            SchedulePrd next = new SchedulePrd();
            next.viewType = TEXT_SPACE;
            next.broadLink = model.nextBroadInfo;
            model.broadPrdList.add(next);
        }

        //이전보기 시 리사이클러뷰 스크롤위치 유지를 위해 현재위치 저장
        View topView = productRecycler.getChildAt(0);
        int firstVisiblePosition = ((StaggeredGridLayoutManager) productRecycler.getLayoutManager()).findFirstVisibleItemPositions(null)[0];
        //이전편성보기가 화면에 노출된 경우는 topView에 날짜뷰 세팅
        if (isIncludePrevLink()) {
            for (int i = 0; i < productRecycler.getChildCount(); i++) {
                View view = productRecycler.getChildAt(i);
                TLineBaseViewHolder vh = (TLineBaseViewHolder) productRecycler.getChildViewHolder(view);
                if (vh.getItemViewType() == TYPE_SCHEDULE_DATE) {
                    topView = view;
                }
            }
        }

        if (refresh) {
            // clear data
            if (productRecycler.getAdapter().getItemCount() > 0) {
                ((TLinePrdAdapter) productRecycler.getAdapter()).getProduct().clear();
                productRecycler.getAdapter().notifyDataSetChanged();
            }
        }

        ((TLinePrdAdapter) productRecycler.getAdapter()).addProduct(model.broadPrdList, isNext);
        productRecycler.getAdapter().notifyDataSetChanged();
        //notifyDataSetChanged 수행시 onScrolled 콜백 호출되어 편성요약 스크롤 위치가 변경되는 현상 막기위해
        setSkipTimeLineSync(true);

        //리사이클러뷰 간 포지션 연동위한 맵 생성
        productMap.clear();
        List<SchedulePrd> schPrdList = ((TLinePrdAdapter) productRecycler.getAdapter()).getProduct();
        for (int i = 0; i < schPrdList.size(); i++) {
            SchedulePrd prd = schPrdList.get(i);
            //상단 날짜 클릭시 이동하기 위한 날짜셀 포지션 저장
            if (viewTypeMap.get(prd.viewType) == TYPE_SCHEDULE_DATE) {
                //날짜타입에 broadStartDate 세팅 (편성상품 스크롤시 상단 날짜이동 위해)
                if (i < schPrdList.size() - 1) {
                    prd.broadStartDate = schPrdList.get(i + 1).broadStartDate;
                }
            }
            //날짜 클릭시 편성상품 영역은 첫번째 방송시간으로 이동하기 위한 포지션 저장
            if ((viewTypeMap.get(prd.viewType) == TYPE_PRD_MAIN || viewTypeMap.get(prd.viewType) == TYPE_PRD_LIVE)) {
                if (productMap.get("date") == null) {
                    productMap.put("date", i);
                }
            }

            //우측 편성요약 클릭시 이동하기 위한 시간셀 포지션 저장
            if (viewTypeMap.get(prd.viewType) == TYPE_PRD_MAIN || viewTypeMap.get(prd.viewType) == TYPE_PRD_LIVE
                    || viewTypeMap.get(prd.viewType) == TYPE_PRD_MAIN_AB_BCV || viewTypeMap.get(prd.viewType) == TYPE_PRD_LIVE_AB_BCV) {
                if (prd.product != null) {
                    String key = prd.broadStartDate + prd.product.prdId;
                    if (productMap.get(key) == null) {
                        productMap.put(key, i);
                        if ("Y".equals(prd.pgmLiveYn)) {
                            //생방송인 경우 (편성표 최초 진입시 이동하기 위한 생방송셀 포지션 저장)
                            productMap.put("live", i);
                        }
                        if ("Y".equals(prd.pgmAnchorYn)) {
                            //공영방송인 경우 (편성표 최초 진입시 이동하기 위한 생방송셀 포지션 저장)
                            productMap.put("public", i);
                        }
                    }
                }
            }
            //편성상품 스크롤시 상단 날짜이동을 위한 포지션 저장
            if (!TextUtils.isEmpty(prd.broadStartDate) && prd.broadStartDate.length() >= 8) {
                Integer pos = dateMap.get(prd.broadStartDate.substring(0, 8));
                if (pos != null) {
                    prd.datePosition = pos.intValue();
                }
            }
        }
        dateMap.clear();

        //이전보기 클릭시 이동할 포지션 저장
        productMap.put("more_pre", model.broadPrdList.size());
        productMap.put("more_pre_from_timeline", model.broadPrdList.size() - 1);

        //노출할 셀로 이동
        syncProductPosition(firstFocusCell, true, firstVisiblePosition, topView != null ? topView.getTop() : 0);

        //아이템이 업데이트 된후 0.1초지난 이후부터 포지션을 체크한다.(이전에는 정상적인 체크 불가)
        new Handler().postDelayed(() -> isPositionCheck = true, 100);

        //매장스크롤이 멈추는 시점에만 캐시작업을 수행하므로 매장로딩 후 처음에 여기서 이벤트 전달하여 캐시 수행
        if (useNativeProduct) {
            new Handler().postDelayed(() -> EventBus.getDefault().post(new Events.ImageCacheStartEvent(sectionList.navigationId)), 1000);
        }
    }

    /**
     * date list
     */
    private class DateAdapter extends RecyclerView.Adapter<DateViewHolder> {

        private List<TVScheduleModel.ScheduleDate> getDates() {
            return dates;
        }

        private void setDates(List<TVScheduleModel.ScheduleDate> dates) {
            this.dates = dates;
        }

        private List<TVScheduleModel.ScheduleDate> dates;

        @Override
        public DateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_item_tv_schedule_date, parent, false);
            return new DateViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(DateViewHolder holder, int position) {
            TVScheduleModel.ScheduleDate date = this.dates.get(position);

            holder.dayText.setText(date.day + "일");
            holder.weekText.setText(date.week);

            holder.itemView.setBackgroundResource(android.R.color.transparent);
            holder.dayText.setTextColor(Color.parseColor("#ffffff"));
            holder.weekText.setTextColor(Color.parseColor("#a3ffffff"));

            // today
            if ("Y".equals(date.todayYn)) {
                holder.itemView.setBackgroundResource(R.drawable.bg_gray_radius);
            }

            if (position == dateSelectedPosition) {
                holder.itemView.setBackgroundResource(R.drawable.bg_white_radius);
                holder.dayText.setTextColor(Color.parseColor("#ee1f60"));
                holder.weekText.setTextColor(Color.parseColor("#ee1f60"));
            }
        }

        @Override
        public int getItemCount() {
            return CollectionUtils.isEmpty(this.dates) ? 0 : this.dates.size();
        }


    }


    /**
     * time line adapter
     */
    private class TimeLineAdapter extends RecyclerView.Adapter<TimeLineViewHolder.TimeLineBaseViewHolder> {

        private List<TVScheduleModel.ScheduleTimeLineThumb> thumbs;

        private List<TVScheduleModel.ScheduleTimeLineThumb> getThumbs() {
            return thumbs;
        }

        private void setThumbs(List<TVScheduleModel.ScheduleTimeLineThumb> thumbs) {
            this.thumbs = thumbs;
        }

        private void addThumbs(List<TVScheduleModel.ScheduleTimeLineThumb> thumbs, boolean isNext) {
            if (CollectionUtils.isEmpty(this.thumbs)) {
                this.thumbs = thumbs;
            } else if (isNext) {
                Integer type = viewTypeMap.get(thumbs.get(0).viewType);
                if (type == TYPE_SCHEDULE_PREV_LINK) {
                    thumbs.remove(0);
                }

                type = viewTypeMap.get(this.thumbs.get(getItemCount() - 1).viewType);
                if (type == TYPE_SCHEDULE_NEXT_LINK) {
                    this.thumbs.remove(getItemCount() - 1);
                }
                this.thumbs.addAll(thumbs);
            } else {
                Integer type = viewTypeMap.get(this.thumbs.get(0).viewType);
                if (type == TYPE_SCHEDULE_PREV_LINK) {
                    this.thumbs.remove(0);
                }

                type = viewTypeMap.get(thumbs.get(thumbs.size() - 1).viewType);
                if (type == TYPE_SCHEDULE_NEXT_LINK) {
                    thumbs.remove(thumbs.size() - 1);
                }

                this.thumbs.addAll(0, thumbs);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return viewTypeMap.get(thumbs.get(position).viewType);
        }

        @Override
        public TimeLineViewHolder.TimeLineBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TimeLineViewHolder.TimeLineBaseViewHolder holder = null;
            View itemView = null;
            switch (viewType) {
                case TYPE_SCHEDULE_PREV_LINK:
                    itemView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.recycler_item_tv_schedule_time_prev_link, parent, false);
                    holder = new TimeLineViewHolder.TimeLineLinkViewHolder(itemView);
                    break;
                case TYPE_SCHEDULE_NEXT_LINK:
                    itemView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.recycler_item_tv_schedule_time_next_link, parent, false);
                    holder = new TimeLineViewHolder.TimeLineLinkViewHolder(itemView);
                    break;
                case TYPE_SCHEDULE_DATE:
                    itemView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.recycler_item_tv_schedule_time_date, parent, false);
                    holder = new TimeLineViewHolder.TimeLineDateViewHolder(itemView);
                    break;
                case TYPE_TIME_LINE_ON_AIR:
                    itemView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.recycler_item_tv_schedule_time_onair, parent, false);

                    holder = new TimeLineViewHolder.TimeLineOnAirViewHolder(itemView);
                    break;
                case TYPE_TIME_LINE_THUMB:
                    itemView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.recycler_item_tv_schedule_time_onair, parent, false);
                    holder = new TimeLineViewHolder.TimeLineThumbViewHolder(itemView);
                    break;

            }
            return holder;
        }

        @Override
        public void onBindViewHolder(TimeLineViewHolder.TimeLineBaseViewHolder holder, int position) {
            holder.bindViewHolder(this.thumbs.get(position), position == eventSelectedPosition, onAirVisible);

        }

        @Override
        public int getItemCount() {
            return CollectionUtils.isEmpty(this.thumbs) ? 0 : this.thumbs.size();
        }

        @Override
        public void onViewAttachedToWindow(TimeLineViewHolder.TimeLineBaseViewHolder holder) {
            super.onViewAttachedToWindow(holder);
            holder.onViewAttachedToWindow();
        }

        @Override
        public void onViewDetachedFromWindow(TimeLineViewHolder.TimeLineBaseViewHolder holder) {
            super.onViewDetachedFromWindow(holder);
            holder.onViewDetachedFromWindow();
        }
    }

    // tv 생방송  실행.
    private void playTVLive(boolean play) {
        if (productRecycler.getAdapter() == null) {
            return;
        }

        if (!play) {
            EventBus.getDefault().post(new Events.FlexibleEvent.SchLivePlayEvent(false, -1, false));
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getView() != null) {
            // shop swiping stops tv live
            if (!isVisibleToUser) {
                playTVLive(false);
                //편성표에서 다른매장 진입시 키보드 숨김
                ViewUtils.hideSoftInput(getView());
            } else {
//                Ln.i("mCoordinator: " + ViewHelper.getTranslationY(mCoordinator.getFooterLayout()));
//                Ln.i("mCoordinator: " + ViewHelper.getTranslationY(bottomView));

                // 하단 탭바가 올라오기 때문에 bottomView 위치 조절.
                if (ViewHelper.getTranslationY(bottomView) == Math.abs(mMinFooterTranslation)) {
                    ObjectAnimator anim = ObjectAnimator.ofFloat(bottomView, "translationY", ViewHelper.getTranslationY(bottomView), 0);
                    anim.setDuration(100);
                    anim.start();
                }
                EventBus.getDefault().post(new Events.FlexibleEvent.StartRollingNo1BestDealBannerEvent(true));

                reloadTvSchedule();

                //네트워크 지연알림이 노출된 매장에 사용자가 다시 진입 시 자동갱신
                if (NetworkUtils.isNetworkAvailable(getContext()) && mRefreshView.getVisibility() == View.VISIBLE) {
                    mBtnRefresh.performClick();
                }
            }


        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity) getActivity()).setSirenLayoutVisibility(sirentView, sirenButton, tooltipSiren);
        //생방송 타이머 구동을 위한 이벤트 전달
        new Handler().postDelayed(() -> {
            if (getView() != null && timeLineRecycler.getAdapter().getItemCount() > 0) {
                //생방송 남은시간 표시용 타이머 시작을 위한 이벤트 전달
                EventBus.getDefault().post(new Events.FlexibleEvent.SchLiveTimerEvent(true));
            }
        }, 1000);
        if (this.getUserVisibleHint()) {
            EventBus.getDefault().post(new Events.FlexibleEvent.StartRollingNo1BestDealBannerEvent(true));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (this.getUserVisibleHint()) {
            playTVLive(false);
        }

        //생방송 남은시간 표시용 타이머 정지를 위한 이벤트 전달
        EventBus.getDefault().post(new Events.FlexibleEvent.SchLiveTimerEvent(false));
        EventBus.getDefault()
                .post(new Events.FlexibleEvent.StartRollingNo1BestDealBannerEvent(false));
    }

    /**
     * 방송알림 등록 수행
     */
    public void onEvent(Events.AlarmRegistEvent event) {
        if (TVScheduleBroadAlarmDialogFragment.TOMORROWTV.equals(event.caller)) {


        } else {
            String url = ServerUrls.getHttpRoot() + ServerUrls.REST.TV_SCHEDULE_ALARM_ADD;
            HttpEntity<Object> requestEntity = makeFormData(event.prdId, event.prdName, event.period, event.times);
            new BroadAlarmUpdateController(context).execute(url, requestEntity, "add");
        }

    }

    public void onEvent(Events.FlexibleEvent.TvLiveUnregisterEvent event) {
        //unregister이벤트 못받을 경우 -> 하단 홈 여러번 누르고 ex. 방송알림 누르면 팝업이 여러번 뜨는 에러 발생
        //새 프레그먼트 만들때마다 unregister 이벤트 받도록 해야함
        EventBus.getDefault().unregister(this);
    }

    /**
     * 편성요약 영역 "ON"버튼 노출/비노출 처리
     */
    public void onEvent(Events.FlexibleEvent.OnAirEvent event) {
        onAirVisible = event.isVisible;
        timeLineRecycler.getAdapter().notifyDataSetChanged();
    }

    /**
     * 방송알림 API 호출용 FormData를 생성한다.
     *
     * @param prdId   상품아이디
     * @param prdName 상품명
     * @param period  기간
     * @param times   횟수
     * @return HttpEntity<Object>
     */
    public static HttpEntity<Object> makeFormData(String prdId, String prdName, String period, String times) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Connection", "Close");
        headers.setContentType(MediaType.APPLICATION_JSON);

        BroadAlarmParam param = new BroadAlarmParam();
        param.type = "PRDID";
        param.prdId = prdId;
        param.prdName = prdName;
        param.period = period;
        param.alarmCnt = times;

        return new HttpEntity<>(param, headers);
    }

    /**
     * 생방송/마이샵 여부에 따라 와이즈로그 주소를 생성한다.
     *
     * @param url 변경전 와이즈로그 주소
     * @return 변경된 와이즈로그 주소
     */
    public static String makeWiselogUrl(String url) {
        return scheduleBroadType == ScheduleBroadType.DATA ?
                url.replace("[TAIL]", "_D") : url.replace("[TAIL]", "");
    }

    /**
     * 방송알림 API 호출
     */
    public static class BroadAlarmUpdateController extends BaseAsyncController<BroadAlarmResult> {
        private final Context context;
        private String url;
        private HttpEntity<Object> requestEntity;
        private String type;

        @Inject
        private RestClient restClient;

        public BroadAlarmUpdateController(Context activityContext) {
            super(activityContext);
            this.context = activityContext;
        }

        @Override
        protected void onPrepare(Object... params) throws Exception {
            super.onPrepare(params);
            if (dialog != null) {
                dialog.setCancelable(true);
            }
            this.url = (String) params[0];
            this.requestEntity = (HttpEntity<Object>) params[1];
            this.type = (String) params[2];
        }

        @Override
        protected BroadAlarmResult process() throws Exception {
            return restClient.postForObject(new URI(url), requestEntity, BroadAlarmResult.class);
        }

        @Override
        protected void onSuccess(BroadAlarmResult result) throws Exception {
            super.onSuccess(result);

            BroadAlarmResult.ALARM_RESULT_TYPE resultType = BroadAlarmResult.ALARM_RESULT_TYPE.valueOf(result.errMsg);
            switch (resultType) {
                case MSG_SUCCESS:
                    if ("query".equals(type)) {
                        //조회
                        TVScheduleBroadAlarmDialogFragment cateDialog = TVScheduleBroadAlarmDialogFragment.newInstance(
                                result.imgUrl, result.prdId, result.prdName, result.phoneNo, result.infoTextList);
                        cateDialog.show(((FragmentActivity) context).getSupportFragmentManager(), TVScheduleBroadAlarmDialogFragment.class.getSimpleName());
                        break;
                    }
                case ERR_DUPLICATE_PRODUCT: //이미 방송알림 등록된 상품
                    if ("query".equals(type)) {
                        new CustomOneButtonDialog((Activity) context).message(result.errMsgText).buttonClick(CustomOneButtonDialog.DISMISS).show();
                        EventBus.getDefault().post(new Events.AlarmUpdatetEvent(result.prdId, true));
                        break;
                    }
                case ERR_NOT_ALARM_PRODUCT: //이미 방송알림 해제된 상품
                    if ("query".equals(type)) {
                        CustomToast.makeTVScheduleBroadAlarmCancel(context, Toast.LENGTH_SHORT).show();
                        EventBus.getDefault().post(new Events.AlarmUpdatetEvent(result.prdId, false));
                        break;
                    }
                    if ("delete".equals(type)) {
                        CustomToast.makeTVScheduleBroadAlarmCancel(context, Toast.LENGTH_SHORT).show();
                        EventBus.getDefault().post(new Events.AlarmUpdatetEvent(result.prdId, false));
                    } else if ("add".equals(type)) {
                        makeBroadAlarmConfirmDialog(context, result.phoneNo, result.linkUrlText, result.linkUrl).show();
                        EventBus.getDefault().post(new Events.AlarmUpdatetEvent(result.prdId, true));
                    }

                    break;
                case ERR_NOT_LOGIN: //로그인 필요
                    String param;
                    Intent intent = new Intent(Keys.ACTION.LOGIN);
                    if (scheduleBroadType == ScheduleBroadType.DATA) {
                        param = ScheduleBroadType.DATA.name();
                    } else {
                        param = ScheduleBroadType.LIVE.name();
                    }
                    intent.putExtra(Keys.INTENT.WEB_URL,
                            ServerUrls.WEB.MOVE_SHOP_FROM_TABID_URL + TV_SCHEDULE_NAVI_ID + "&" + BROADTYPE_PARAM_KEY + "=" + param);
                    context.startActivity(intent);
                    break;
                case ERR_PHONE_EMPTY:   //핸드폰 번호 필요
                case ERR_MAX_ALARM_SIZE:    //방송알림 최대 등록 개수 초과
                case ERR_SERVER_SAVE_FAIL:  //방송알림 등록 실패
                case ERR_SERVER_DELETE_FAIL:    //방송알림 취소 실패
                    new CustomOneButtonDialog((Activity) context).message(result.errMsgText).buttonClick(CustomOneButtonDialog.DISMISS).show();
                    break;
                default:
                    //정의되지 않은 에러타입인 경우
                    new CustomOneButtonDialog((Activity) context).message(R.string.undefined_error).buttonClick(CustomOneButtonDialog.DISMISS).show();
                    break;
            }
        }

        @Override
        protected void onError(Throwable e) {
            super.onError(e);
        }
    }

    /**
     * 방송알림 업데이트 수행 (동일한 prdId는 모두 업데이트)
     */
    public void onEvent(Events.AlarmUpdatetEvent event) {
        if (!TextUtils.isEmpty(event.prdId)) {
            List<SchedulePrd> schPrdList = ((TLinePrdAdapter) productRecycler.getAdapter()).getProduct();
            if (isEmpty(schPrdList)) {
                return;
            }
            for (int i = 0; i < schPrdList.size(); i++) {
                SchedulePrd prd = schPrdList.get(i);
                if (viewTypeMap.get(prd.viewType) == TYPE_PRD_LIVE || viewTypeMap.get(prd.viewType) == TYPE_PRD_MAIN
                        || viewTypeMap.get(prd.viewType) == TYPE_PRD_MAIN_AB_BCV || viewTypeMap.get(prd.viewType) == TYPE_PRD_LIVE_AB_BCV) {
                    //주상품 업데이트
                    if (prd.product != null && event.prdId.equals(prd.product.prdId)) {
                        prd.product.broadAlarmDoneYn = event.isRegisted ? "Y" : "N";
                    }
                    //부상품 업데이트
                    if (prd.product.subProductList != null && prd.product.subProductList.size() > 0) {
                        for (int j = 0; j < prd.product.subProductList.size(); j++) {
                            Product subPrd = prd.product.subProductList.get(j);
                            if (subPrd != null && event.prdId.equals(subPrd.prdId)) {
                                subPrd.broadAlarmDoneYn = event.isRegisted ? "Y" : "N";
                            }
                        }
                    }
                }
            }
            productRecycler.getAdapter().notifyDataSetChanged();
        }
    }

    /**
     * 편성표를 다시 로드한다. (타매장 또는 배너등을 통해 호출됨)
     */
    private void reloadTvSchedule() {
        //다른 매장의 배너로 부터 호출된 경우(tabId 형태), 편성표 매장이 이미 로딩된 경우라도 무조건 API 재호출
        if (isCalledFromBanner) {
            isCalledFromBanner = false;
            drawFragment();
        }
    }

    /**
     * TVScheduleModel.ScheduleDate 에서 오늘의 인덱스를 반환
     * <p>
     * 0~n 선택 인덱스
     *
     * @param temp TVScheduleModel.ScheduleDate
     * @return -1 (오늘을 못찾음 ) 0~n 오늘 인덱스
     */
    private int getTodayIndex(List<TVScheduleModel.ScheduleDate> temp) {
        if (temp != null) {
            for (int i = 0; i < temp.size(); ++i) {
                if (temp.get(i) != null && temp.get(i).todayYn != null) {
                    if ("Y".equals(temp.get(i).todayYn)) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    /**
     * 방송알림 API 호출을 위한 POST 파라미터
     */
    @Model
    public static class BroadAlarmParam {
        public String type;
        public String prdId;
        public String prdName;
        public String period;
        public String alarmCnt;
    }
}
