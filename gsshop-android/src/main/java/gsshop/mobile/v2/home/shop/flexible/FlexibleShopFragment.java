/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.inject.Inject;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.pattern.mvc.BaseAsyncController;
import com.gsshop.mocha.ui.back.BackKeyHandler;
import com.gsshop.mocha.ui.back.WebPageBackHandler;
import com.gsshop.mocha.web.WebViewProgressBar;
import com.nineoldandroids.view.ViewHelper;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersTouchListener;
import com.tms.inappmsg.InAppMessageManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.AbstractBaseActivity;
import gsshop.mobile.v2.AbstractTMSService;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.Keys.CACHE;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.home.CustomRecyclerView;
import gsshop.mobile.v2.home.GroupSectionList;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.HomeGroupInfo;
import gsshop.mobile.v2.home.TopSectionList;
import gsshop.mobile.v2.home.main.ContentsListInfo;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.DividerItemDecoration;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.ShopInfo.ShopItem;
import gsshop.mobile.v2.home.shop.ViewHolderType;
import gsshop.mobile.v2.home.shop.flexible.shoppinglive.MLAlarm;
import gsshop.mobile.v2.home.shop.flexible.shoppinglive.MobileLiveAlarmCancelDialogFragment;
import gsshop.mobile.v2.home.shop.flexible.shoppinglive.MobileLiveAlarmDialogFragment;
import gsshop.mobile.v2.home.shop.flexible.shoppinglive.ShoppingLiveShopAdapter;
import gsshop.mobile.v2.home.shop.gssuper.GSSuperTabViewHolder;
import gsshop.mobile.v2.home.shop.tvshoping.MainShopFragment;
import gsshop.mobile.v2.library.quickreturn.QuickReturnInterface;
import gsshop.mobile.v2.library.quickreturn.QuickReturnRecyclerViewOnScrollListener;
import gsshop.mobile.v2.library.quickreturn.QuickReturnType;
import gsshop.mobile.v2.library.quickreturn.library.QuickReturnUtils;
import gsshop.mobile.v2.support.ui.BugFixedStaggeredGridLayoutManager;
import gsshop.mobile.v2.user.User;
import gsshop.mobile.v2.util.DataUtil;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.util.NetworkUtils;
import gsshop.mobile.v2.util.PrefRepositoryNamed;
import gsshop.mobile.v2.web.MainWebViewChromeClient;
import gsshop.mobile.v2.web.MainWebViewClient;
import gsshop.mobile.v2.web.WebUtils;
import gsshop.mobile.v2.web.WebViewControlInherited;
import roboguice.util.Ln;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;
import static com.blankj.utilcode.util.EmptyUtils.isEmpty;
import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;
import static com.blankj.utilcode.util.KeyboardUtils.hideSoftInput;
import static gsshop.mobile.v2.MainApplication.useNativeProduct;
import static gsshop.mobile.v2.home.shop.ViewHolderType.BANNER_TYPE_ROLL_IMAGE_NEW_GBA;
import static gsshop.mobile.v2.home.shop.ViewHolderType.BANNER_TYPE_ROLL_IMAGE_NEW_GBH;
import static gsshop.mobile.v2.home.shop.ViewHolderType.VIEW_TYPE_BAN_IMG_SLD_GBA;
import static gsshop.mobile.v2.home.shop.ViewHolderType.VIEW_TYPE_BAN_NO_PRD;
import static gsshop.mobile.v2.home.shop.ViewHolderType.VIEW_TYPE_BAN_SLD_GBB;
import static gsshop.mobile.v2.home.shop.ViewHolderType.VIEW_TYPE_BAN_SLD_MOBILE_LIVE_BROAD;
import static gsshop.mobile.v2.home.shop.ViewHolderType.VIEW_TYPE_BAN_SLD_MOBILE_LIVE_SMALL;
import static gsshop.mobile.v2.home.shop.ViewHolderType.VIEW_TYPE_BAN_TXT_CST_GBA;
import static gsshop.mobile.v2.home.shop.ViewHolderType.VIEW_TYPE_BAN_VIP_BENE_GBA;
import static gsshop.mobile.v2.home.shop.ViewHolderType.VIEW_TYPE_BAN_VIP_CARD_GBA;
import static gsshop.mobile.v2.home.shop.ViewHolderType.VIEW_TYPE_BAN_VIP_GBA;
import static gsshop.mobile.v2.home.shop.ViewHolderType.VIEW_TYPE_BAN_VIP_IMG_GBA;
import static gsshop.mobile.v2.home.shop.ViewHolderType.VIEW_TYPE_GR_HOME_CATE_TAB_GATE;
import static gsshop.mobile.v2.home.shop.ViewHolderType.VIEW_TYPE_GR_PRD_1_VIP_LIST_GBA;
import static gsshop.mobile.v2.home.shop.ViewHolderType.VIEW_TYPE_GR_PRD_1_VIP_LIST_GBB;
import static gsshop.mobile.v2.home.shop.ViewHolderType.VIEW_TYPE_GR_PRD_1_VIP_LIST_GBC;
import static gsshop.mobile.v2.home.shop.ViewHolderType.VIEW_TYPE_GR_PRD_2_VIP_LIST_GBA;
import static gsshop.mobile.v2.home.shop.ViewHolderType.VIEW_TYPE_GR_PRD_2_VIP_LIST_GBB;
import static gsshop.mobile.v2.home.shop.ViewHolderType.VIEW_TYPE_PRD_C_B1_VIP_GBA;
import static gsshop.mobile.v2.home.shop.ViewHolderType.VIEW_TYPE_PRD_PAS_SQ;

/**
 *
 */
public class FlexibleShopFragment extends MainShopFragment {

    public static final String ARG_PARAM_POSITION = "_arg_param_position";
    public static final String MOBILE_LIVE_LIST_CALLER = "MOBILE_LIVE_LIST"; //등록확정 팝업이 모라 생방송 플레이어 or 쇼핑라이브 탭매장 중에 어디 위에 뜰지 구분할 구분자

    /**
     * 매장별 사용할 프래그먼트 레이아웃 구분자
     */
    public enum FRAGMENT_TYPE {
        APPBAR, //내일TV 형태 (default)
        HEADER,  //gs super 스티키 해더 형태
    }

    protected FRAGMENT_TYPE mFragmentType = FRAGMENT_TYPE.APPBAR;

    protected CustomRecyclerView mRecyclerView;
    /**
     * 상단으로 버튼
     */
    protected ImageView btnTop;

    /**
     * 사이렌 버튼
     */
    private LinearLayout layoutSiren;
    private ImageView btnSiren;
    private ImageView tooltipSiren;

    /**
     * 리스트 카운트 레이아웃
     */
    protected LinearLayout list_count_layout;

    /**
     * 키보드가 표시되어 있는 유무
     */
    protected boolean isKeyInputShow = false;


    /**
     * 데이터를 불러오는중 오류가 났을때 표시되는 새로고침 레이아웃
     */
    protected View mRefreshView;

    /**
     * 새로고침 버튼, 웹으로 보기 버튼 (네트워크 장애)
     */
    private FrameLayout mBtnRefresh, mBtnGoWeb;

    /**
     * 스크롤 리스트
     */
    protected QuickReturnRecyclerViewOnScrollListener scrollListener;
    protected FlexibleShopAdapter mAdapter;

    /**
     * 광고 notice 팝업뷰
     */
    private PopupWindow mPopupWindow;
    private View popupView;

    /* 기존꺼. */
    protected TopSectionList tempSection;
    public WebViewControlInherited mWebControl;
    public BackKeyHandler backHandler;
    protected StaggeredGridLayoutManager mLayoutManager;
    private StickyRecyclerHeadersDecoration headersDecor;

    protected boolean isPageLoading = false;

    /**
     * 스티키 해더용 뷰
     */
    protected LinearLayout header;
    protected BaseViewHolder headerViewHolder;

    /**
     * GS SUPER 탭과 하단상품 매핑용
     */
    public static Map<String, Integer> tabPrdMap = new HashMap<String, Integer>();

    /**
     * Wine 용으로 탭 비교용 생성... 여기도 나중에 L타입처럼 통합 해야할듯. 에효 이렇게 두벌로 갈꺼면 대체 왜 따로 간거여... L타입으로만 간담서!
     */
    public static Map<String, Integer> tabPrdMapWine = new HashMap<String, Integer>();

    /**
     * 당겨서 새로고침 할 레이아웃
     */
    protected SwipeRefreshLayout mLayoutSwipe;

    /**
     * 당겨서 새로고침 여부
     */
    public boolean isNowSwipeRefreshing = false;

    /**
     * GS SUPER 스티키해더로 사용할 첫번째 뷰홀더 확인용
     */
    protected boolean isFirst = true;

    /**
     * Click Url이 존재하면 날리기 위해서 저장해 놓을 변수 선언
     */
    private String mCommonClickUrl = null;

    /**
     * 각 FlexibleFragment의 네비아이디
     */
    protected String myNaviId = "";

    /**
     * MOLOCO_PRD_C_SQ 효율코드 보내기 위한 변수
     */
    private Runnable mRunnableSendWiseLog; // MOLOCO_PRD_C_SQ 관련 wiseLog 를 전송하는 Runnable 객체
    private Handler mHandlerSendWiseLog = new Handler(); // MOLOCO_PRD_C_SQ 관련 wiseLog 를 전송하는 핸들러 (2초 딜레이 때문에 사용)
    private ArrayList<Integer> mLastVisibleItemsPositionList = new ArrayList<>(); // 마지막으로 화면에 보여진 아이템 (뷰홀더) 포지션을 담고있는 리스트
    private int mPrdCSqVisibleCount = 0; // MOLOCO_PRD_C_SQ 가 화면에 노출 된 횟수. (화면 밖으로 나가면 0으로 초기화된다)

    // VIP 에서는 겹쳐지는 리스트가 있어 해당 리스트를 갱신할때 초기화 해주고 더해주어야 한다.
    private ArrayList<RecyclerView.ItemDecoration> arrVipDecoration = new ArrayList<>();

    public static FlexibleShopFragment newInstance(int position) {
        FlexibleShopFragment fragment = new FlexibleShopFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public FlexibleShopFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPosition = getArguments().getInt(ARG_PARAM_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        EventBus.getDefault().register(this);
        View view = null;

        //여기에 get(0) 에 대한 방어로직을 넣으면? 이상할것 같음
        try {
            HomeGroupInfo homeGroupInfo = getHomeGroupInfo();
            GroupSectionList groupSectionList = homeGroupInfo.groupSectionList.get(0);
            tempSection = groupSectionList.sectionList.get(mPosition); // 홈 하위 하나의 인덱스 요청
        } catch (Exception e) {
            return view;
        }


        if ("WEB".equals(tempSection.sectionType)) {
            view = inflater.inflate(R.layout.fragment_webview, container, false);
            setupWebControl(view);
            mWebControl.loadUrl(tempSection.sectionLinkUrl, MainApplication.customHeaders);
        } else {
            if (mFragmentType == FRAGMENT_TYPE.HEADER) {
                view = inflater.inflate(R.layout.fragment_flexible_shop_header, container, false);

                header = view.findViewById(R.id.header);
                header.setVisibility(View.GONE);

                View itemView = inflater.inflate(R.layout.home_row_type_fx_sticky_type_fresh_tab, container, false);
                headerViewHolder = new GSSuperTabViewHolder(itemView);
                header.addView(headerViewHolder.itemView);
            } else {
                view = inflater.inflate(R.layout.fragment_flexible_shop_appbar, container, false);
            }
        }

        return view;
    }

    public void onEvent(Events.FlexibleEvent.TvLiveUnregisterEvent event) {
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);

        if (isNotEmpty(mRecyclerView)) {
            // destroy view holder
            int count = mRecyclerView.getChildCount();
            Ln.i("TVLiveBannerTVLiveViewHolder count : " + count);
            for (int i = 0; i < count; i++) {
                View view = mRecyclerView.getChildAt(i);
                BaseViewHolder vh = (BaseViewHolder) mRecyclerView.getChildViewHolder(view);
                Ln.i("TVLiveBannerTVLiveViewHolder viewtype : " + vh.getItemViewType());
                vh.onViewDetachedFromWindow();
            }
        }

        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();

        // 베스티 딜 롤링 시작 - TCLIST
        if (this.getUserVisibleHint()) {
            if (getView() != null && mAdapter.getInfo() != null) {
                EventBus.getDefault().post(new Events.FlexibleEvent.StartRollingNo1BestDealBannerEvent(true));
            }
        }
        //사이렌 버튼 노출여부 세팅
//		((HomeActivity)getContext()).setSirenIconVisibility(btnSiren);
        try {
            ((HomeActivity) getContext()).setSirenLayoutVisibility(layoutSiren, btnSiren, tooltipSiren);
        } catch (NullPointerException e) {
            Ln.e(e.getMessage());
        }
    }

    @Override
    public void onPause() {
        // 베스트 딜 롤링 중지 : TCLIST
        if (this.getUserVisibleHint()) {
            EventBus.getDefault()
                    .post(new Events.FlexibleEvent.StartRollingNo1BestDealBannerEvent(false));
        }
        EventBus.getDefault().post(new Events.FlexibleEvent.AdTooltipEvent(null, false));
        super.onPause();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        // 해당 화면이 보여질 때에  호출된다.
        if (isVisibleToUser) {
            if (tempSection != null) {
                // 홈화면이 보여질 때에
                if (tempSection.navigationId.equals(ShopInfo.NAVIGATION_HOME)) {
                    // PrdCSq 의 wiseLog 발신
                    sendPrdCSqWiseLog();

                    // test 용 ( 5분 기다리지 않는다. )
//                    InAppMessageManager.getInstance().setCashEventIdDisabled(true);

                    mActivity.setTmsAddView();
                    // 탭 페이지 변경될 때마다, (클릭 이던, 슬라이드던 페이지 변경시에 호출)
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            InAppMessageManager.getInstance().registInAppMessageEvent(AbstractTMSService.EventInAppMessage.EVENT_MAIN_TAB);
                        }
                    }, AbstractTMSService.EventInAppMessage.EVNET_MAIN_TAB_DELAY);

                    // test
//                    InAppMessage inAppMessage = new InAppMessage();
//                    inAppMessage.event_id = "event";
//                    inAppMessage.camp_Id = "01";
//                    inAppMessage.msgUrl = "https://tms31.gsshop.com/TMS/inapp/2021062900023.html";
//                    InAppMessageManager.getInstance().displayInAppMessage(inAppMessage);
//                    Ln.d("hklim addOnPageChangeListener InAppMessageManager 2000");
                }
                if (!TextUtils.isEmpty(mCommonClickUrl)) {
                    try {
                        ((AbstractBaseActivity) getContext()).setWiseLogHttpClient(mCommonClickUrl);
                    }
                    catch (ClassCastException | NullPointerException | NoSuchMethodError e) {
                        Ln.e(e.getMessage());
                    }
                }
            }
        }
        if (getView() != null) {
            if (isVisibleToUser) {
                //네트워크 지연알림이 노출된 매장에 사용자가 다시 진입 시 자동갱신
                if (NetworkUtils.isNetworkAvailable(getContext()) && mRefreshView.getVisibility() == View.VISIBLE) {
                    mBtnRefresh.performClick();
                }
                EventBus.getDefault().post(new Events.FlexibleEvent.StartRollingNo1BestDealBannerEvent(true));
            } else {
                /**
                 * 매장이 이동되면 키보드를 내린다.
                 */
                hideSoftInput(getActivity());
            }
            //좌우 플리킹시 하단탭메뉴가 노출되면서 탑버튼 위치가 맞지 않은 현상 개선(임시)
            btnTop.setVisibility(View.GONE);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = view.findViewById(R.id.recycle_goods);
        btnTop = (ImageView) view.findViewById(R.id.btn_top);
        layoutSiren = (LinearLayout) view.findViewById(R.id.layout_siren);
        btnSiren = (ImageView) view.findViewById(R.id.btn_siren);
        tooltipSiren = (ImageView) view.findViewById(R.id.tooltip_siren);
        list_count_layout = (LinearLayout) view.findViewById(R.id.list_count_layout);
        mRefreshView = view.findViewById(R.id.flexible_refresh_layout);
        mRefreshView.setVisibility(View.GONE);

        // 새로고침 버튼 셋팅(버튼 클릭시 데이터를 다시 불러온다.)
        mBtnRefresh = (FrameLayout) view.findViewById(R.id.btn_refresh);
        mBtnRefresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                drawFragment();
            }
        });
        // 웹으로 보기 버튼 셋팅(버튼 클릭시 GS SHOP mobile web 으로 간다.)
        mBtnGoWeb = view.findViewById(R.id.btn_go_web);
        mBtnGoWeb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goWebIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ServerUrls.MOBILE_WEB));
                startActivity(goWebIntent);
            }
        });

        popupView = LayoutInflater.from(getActivity()).inflate(R.layout.popup_window, null);
        mPopupWindow = new PopupWindow(popupView,
                RelativeLayout.LayoutParams.WRAP_CONTENT, StaggeredGridLayoutManager.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setAnimationStyle(0);

        // flexible 리스트 매장.
        if (tempSection.viewType.equals(ShopInfo.TYPE_BRAND)) {
            // 브랜드 메뉴.
            mAdapter = new FlexibleShopBrandStickeyTabAdapter(getActivity());
            mRecyclerView.setAdapter(mAdapter);

            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {
                // Add the sticky headers decoration
                headersDecor = new StickyRecyclerHeadersDecoration(
                        (StickyRecyclerHeadersAdapter) mAdapter);
                mRecyclerView.addItemDecoration(headersDecor);

                // Add touch listeners
                StickyRecyclerHeadersTouchListener touchListener = new StickyRecyclerHeadersTouchListener(
                        mRecyclerView, headersDecor);
                touchListener.setOnHeaderClickListener(
                        new StickyRecyclerHeadersTouchListener.OnHeaderClickListener() {
                            @Override
                            public void onHeaderClick(View header, int position, long headerId) {
                                int tabIndex = 0;
                                int selectedTabPosition = -1;
                                LinearLayout tabs = (LinearLayout) header
                                        .findViewById(R.id.view_tabs);

                                for (int i = 0; i < tabs.getChildCount(); i++) {
                                    View tab = tabs.getChildAt(i);
                                    if (tab instanceof Button) {
                                        int left = tab.getLeft();
                                        int top = tab.getTop();
                                        int width = tab.getWidth();
                                        int height = tab.getHeight();
                                        Rect rect = new Rect(left, top, left + width, top + height);

                                        //aspectj 제거과정 중 stickyheadersrecyclerview lib.를 lib 폴더에서
                                        //gradle implementation으로 변경하면서 onHeaderClick 함수 파라미터가 변경되어 아래코드 주석처리
                                        /*if (rect.contains(x, y)) {
                                            selectedTabPosition = tabIndex;
                                            //Ln.i("selectedTabPosition : " + selectedTabPosition + ", tabIndex : " + tabIndex);
                                        }*/
                                        tabIndex++;
                                    }

                                }
                                // 이벤트 탭 클릭 아님.
                                if (selectedTabPosition < 0) {
                                    return;
                                }

                                tabIndex = 0;
                                // 탭 클릭 효과 주기.
                                for (int i = 0; i < tabs.getChildCount(); i++) {
                                    View tab = tabs.getChildAt(i);
                                    if (tab instanceof Button) {

                                        if (tabIndex == selectedTabPosition) {
                                            setButtonState(((Button) tab), true);
                                        } else {
                                            setButtonState(((Button) tab), false);
                                        }
                                        tabIndex++;
                                    }

                                }

                                //메뉴를 클릭하면 해당 카테고리리스트로 스크롤한다.
                                ShopInfo info = mAdapter.getInfo();
                                int brandCategoryLayoutHeight = getResources().getDimensionPixelSize(R.dimen.brand_category_layout_height);
                                mLayoutManager.invalidateSpanAssignments();
                                mAdapter.notifyDataSetChanged();

                                if (info != null && info.categoryIndex != null) {

                                    switch (selectedTabPosition) {
                                        case 1:
                                            if (info.categoryIndex.size() > 1)
                                                mLayoutManager.scrollToPositionWithOffset(info.categoryIndex.get(1), brandCategoryLayoutHeight);
                                            break;
                                        case 2:
                                            if (info.categoryIndex.size() > 2)
                                                mLayoutManager.scrollToPositionWithOffset(info.categoryIndex.get(2), brandCategoryLayoutHeight);
                                            break;
                                        case 3:
                                            if (info.categoryIndex.size() > 3)
                                                mLayoutManager.scrollToPositionWithOffset(info.categoryIndex.get(3), brandCategoryLayoutHeight);
                                            break;
                                        default:
                                            mLayoutManager.scrollToPositionWithOffset(0, 0);
                                            break;
                                    }
                                }
                            }
                        });

                mRecyclerView.addOnItemTouchListener(touchListener);


                // Add decoration for dividers between list items
//                int staggeredInner = getActivity().getResources()
//                        .getDimensionPixelSize(R.dimen.event_menu_staggered_inner_space);
                int staggeredOuter = getActivity().getResources()
                        .getDimensionPixelSize(R.dimen.event_menu_staggered_outer_space);
                int wideInner = getActivity().getResources()
                        .getDimensionPixelSize(R.dimen.event_menu_wide_inner_space);
                int wideOuter = getActivity().getResources()
                        .getDimensionPixelSize(R.dimen.event_menu_wide_outer_space);

//                // resize space
//                staggeredInner = DisplayUtils.getResizedPixelSizeToScreenSize(getActivity(),
//                        staggeredInner);

                //Ln.i("size - staggeredInner : " + staggeredInner);
                // staggeredOuter = DisplayUtils.getResizedPixelSizeToScreenSize(getActivity(),
                // staggeredOuter);
                mRecyclerView.addItemDecoration(new DividerItemDecoration(0,
                        staggeredOuter, wideInner, wideOuter));

            }

            mRecyclerView.setHasFixedSize(true);
            mLayoutManager = new BugFixedStaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(mLayoutManager);
        }
        else {
            mRecyclerView.setHasFixedSize(true);
            mLayoutManager = new BugFixedStaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(mLayoutManager);

            mAdapter = new FlexibleShopAdapter(getActivity());
            mRecyclerView.setAdapter(mAdapter);

            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int lastVisiblePosition = ((StaggeredGridLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPositions(null)[0];
                    int itemTotalCount = recyclerView.getAdapter().getItemCount() - 1;
                    if (lastVisiblePosition == itemTotalCount) {


                        if (recyclerView.getAdapter() instanceof FlexibleShopAdapter &&
                                // 쇼핑 라이브에서는 ajaxPageUrl 이 스크롤 시에 갱신용으로 사용하지 않는다.
                            !(recyclerView.getAdapter() instanceof ShoppingLiveShopAdapter)) {
                            FlexibleShopAdapter flexibleAdapter = (FlexibleShopAdapter) recyclerView.getAdapter();
                            if (!isPageLoading && flexibleAdapter.getInfo().ajaxPageUrl != null && !"".equals(flexibleAdapter.getInfo().ajaxPageUrl)) {
                                isPageLoading = true;
                                new PageUpdateController(getContext(), flexibleAdapter).execute(flexibleAdapter.getInfo().ajaxPageUrl, false);
                            }
                        }
                    }


                    mLastVisibleItemsPositionList.clear();
                    mHandlerSendWiseLog.removeCallbacks(mRunnableSendWiseLog);
                }

                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    // 홈매장에서 그려진 C_SQ 일 경우에만 수행.
                    if (newState == SCROLL_STATE_IDLE) {
                        ArrayList<Integer> viewTypeList = new ArrayList<>();
                        for (int i = 0; i < getVisibleItemsPosition().size(); i++) {
                            viewTypeList.add(mAdapter.getItemViewType(getVisibleItemsPosition().get(i)));
                        }

                        if (mPrdCSqVisibleCount < 1) {
                            sendPrdCSqWiseLog();
                        }

                        if (viewTypeList.contains(ViewHolderType.VIEW_TYPE_MOLOCO_PRD_C_SQ)) {
                            mPrdCSqVisibleCount++;
                        } else {
                            mPrdCSqVisibleCount = 0;
                        }
                    }
                }

            });
        }

        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent event) {
                if (rv == null || event.getAction() != MotionEvent.ACTION_UP) {
                    return false;
                }
                try {
                    View view = rv.findChildViewUnder(event.getX(), event.getY());
                    BaseViewHolder vh = (BaseViewHolder) mRecyclerView.getChildViewHolder(view);
                    if (vh.getItemViewType() == ViewHolderType.VIEW_TYPE_API_SRL ||
                            vh.getItemViewType() == ViewHolderType.VIEW_TYPE_PRD_C_SQ ||
                            vh.getItemViewType() == ViewHolderType.VIEW_TYPE_MOLOCO_PRD_C_SQ ||
                            vh.getItemViewType() == ViewHolderType.VIEW_TYPE_PRD_C_CST_SQ ||
                            vh.getItemViewType() == ViewHolderType.VIEW_TYPE_RTS_PRD_C_CST_SQ ||
                            vh.getItemViewType() == ViewHolderType.VIEW_TYPE_PMO_T1_PREVIEW_D ||
                            vh.getItemViewType() == ViewHolderType.VIEW_TYPE_PMO_T1_PREVIEW_B ||
                            vh.getItemViewType() == ViewHolderType.VIEW_TYPE_PRD_C_B1 ||
                            vh.getItemViewType() == ViewHolderType.VIEW_TYPE_PRD_PAS_SQ ||
                            vh.getItemViewType() == ViewHolderType.VIEW_TYPE_BEST_MOBILE_LIVE
                    ) {
                        vh.setOnTouchUp();
                    }
                } catch (NullPointerException exception) {
                    Ln.e(exception.getMessage());
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });

        drawFragment();

        /**
         * 아래 코드로 인해 홈이 두번 호출되어 간혹 아래 오류발생하며 앱 종료됨 (주석처리)
         *
         * E/WindowManager: android.view.WindowLeaked:
         * Activity gsshop.mobile.v2.home.HomeActivity has leaked window DecorView@5638c2a[] that was originally added here
         * at android.app.Dialog.show(Dialog.java:329)
         * ...
         * java.lang.IllegalAccessError: Field 'com.gsshop.mocha.core.exception.AbstractExceptionHandlingAspect.exceptionHandler'
         * is inaccessible to class 'com.gsshop.mocha.pattern.mvc.BaseAsyncController$2' (declaration of 'com.gsshop.mocha.pattern.mvc.BaseAsyncController$2'
         */
        // 메모리에 데이터가 날아간경우 데이터 복구
        /*if (savedInstanceState != null) {
            Bundle restoreData = savedInstanceState.getBundle("restore");
            if (mPosition == restoreData.getInt("position")) {
                HomeActivity homeActivity = (mActivity);
                homeActivity.GetHomeGroupListInfo();

            }
        }*/

        setQuickReturn();

        // 당겼을 때 동작 등 추가 위한 함수
        setRefreshLayout(view);

        setSendWiseLogRunnable(getContext());
    }

    private void sendPrdCSqWiseLog() {
        // 스크롤이 멈췄을 때 마지막으로 화면에 보여지는 아이템들의 포지션을 리스트에 저장.
        mLastVisibleItemsPositionList = getVisibleItemsPosition();

        // 2초 뒤에 위에 저장해놓은 아이템이 그대로 보이는지 확인하기 위한 핸들러.
        mHandlerSendWiseLog.postDelayed(mRunnableSendWiseLog, 2000);
    }

    @Override
    public boolean getUserVisibleHint() {
        return super.getUserVisibleHint();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof QuickReturnInterface) {
            mCoordinator = (QuickReturnInterface) activity;
        } else
            throw new ClassCastException(
                    "Parent container must implement the QuickReturnInterface");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBundle("restore", saveState());
    }

    /**
     * 퀵 리턴 메뉴 설정
     */
    protected void setQuickReturn() {

        // 숭겨질 상단 레이아웃의 세로 사이즈
        int headerHeight = getResources().getDimensionPixelSize(R.dimen.main_header_height);
        // 숭겨질 하단 레이아웃의 세로 사이즈
        int footerHeight = getResources().getDimensionPixelSize(R.dimen.main_footer_height);
        int indicatorHeight = QuickReturnUtils.dp2px(mActivity, 4);
        int headerTranslation = -headerHeight + indicatorHeight;
        int footerTranslation = -footerHeight + indicatorHeight;

        // 퀵 리턴뷰 리스너 설정 - 0827 사이렌이 없어지고 그 자리에 CSP레이아웃 탑재

        scrollListener = new QuickReturnRecyclerViewOnScrollListener.Builder(QuickReturnType.HEADER_FOOTER)
                .footer(getActivity(), mCoordinator.getFooterLayout(), btnTop, list_count_layout, mCoordinator.getCspLayout())
                .minHeaderTranslation(headerTranslation)
                .minFooterTranslation(-footerTranslation).isSnappable(true).build();


        // 클릭시 리스트 상단으로 버튼
        btnTop.setVisibility(View.GONE);

        scrollListener.registerExtraOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPositions(null)[0];

                if (!isKeyInputShow && btnTop.getVisibility() != View.VISIBLE && firstVisibleItemPosition > 0) {
                    btnTop.setVisibility(View.VISIBLE);
                } else if (firstVisibleItemPosition <= 0) {
                    btnTop.setVisibility(View.GONE);
                }

                //스크롤 멈춘시점에 이미지 캐싱 진행
                if (useNativeProduct) {
                    if (newState == SCROLL_STATE_IDLE) {
                        EventBus.getDefault().post(new Events.ImageCacheStartEvent(tempSection.navigationId));
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // Ln.i("dx : " + dx + ", dy : " + dy);
                if (mPopupWindow != null && mPopupWindow.isShowing()) {
                    EventBus.getDefault().post(new Events.FlexibleEvent.AdTooltipEvent(null, false));
                }

                int firstVisibleItemPosition = mLayoutManager
                        .findFirstVisibleItemPositions(null)[0];


                //브랜드관에서 스크롤 중인 경우
                if (tempSection.viewType.equals(ShopInfo.TYPE_BRAND)) {
                    LinearLayout headerLayout = (LinearLayout) headersDecor.getHeaderView(mRecyclerView, 1);
                    Button category_1 = (Button) headerLayout.findViewById(R.id.category_1);
                    Button category_2 = (Button) headerLayout.findViewById(R.id.category_2);
                    Button category_3 = (Button) headerLayout.findViewById(R.id.category_3);
                    Button category_4 = (Button) headerLayout.findViewById(R.id.category_4);

                    setButtonState(category_1, false);
                    setButtonState(category_2, false);
                    setButtonState(category_3, false);
                    setButtonState(category_4, false);
                    switch (getNearIndex(firstVisibleItemPosition)) {
                        case 1:
                            setButtonState(category_2, true);
                            break;
                        case 2:
                            setButtonState(category_3, true);
                            break;
                        case 3:
                            setButtonState(category_4, true);
                            break;
                        default:
                            setButtonState(category_1, true);
                            break;
                    }

                }
            }

        });

        // //상단으로 버튼 클릭시
        btnTop.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mRecyclerView.stopScroll();
                mLayoutManager.scrollToPosition(0);
                btnTop.setVisibility(View.GONE);
                if (isNotEmpty(header)) {
                    header.setVisibility(View.GONE);
                }

                //탑버튼 클릭시 해더영역 노출
                ((HomeActivity) getContext()).expandHeader();

                ViewHelper.setTranslationY(btnTop, 0);

                ViewHelper.setTranslationY(mCoordinator.getFooterLayout(), 0);

                //CSP BUTTON MSLEE
                ViewHelper.setTranslationY(mCoordinator.getCspLayout(), 0);
            }
        });

        //사이렌 버튼 클릭이벤트 정의
        layoutSiren.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //사이렌 웹뷰 페이지 호출
                ((HomeActivity) getContext()).goSirenPage();
            }
        });

        mRecyclerView.setOnScrollListener(scrollListener);

    }

    /**
     * refresh layout 설정 (당겼을 때 동작 등)
     */

    protected void setRefreshLayout(View view) {
        mLayoutSwipe = view.findViewById(R.id.layout_refresh);
        mLayoutSwipe.setColorSchemeColors(Color.parseColor("#BED730"));
        mLayoutSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 프래그먼트 다시 그림.
                onSwipeRefrehsing();
            }
        });
    }

    protected GetUpdateController mSwipeRefreshController = null;

    protected void onSwipeRefrehsing() {
        isNowSwipeRefreshing = true;
        if (mSwipeRefreshController != null) {
            mSwipeRefreshController.cancel();
            mSwipeRefreshController = null;
        }
        mSwipeRefreshController = new GetUpdateController(getActivity(), tempSection, false);
        mSwipeRefreshController.execute(true);
    }

    public void onEvent(Events.EventSwipeRefreshStop event) {
        Events.EventSwipeRefreshStop stickyEvent = (Events.EventSwipeRefreshStop) EventBus.getDefault().getStickyEvent(Events.EventSwipeRefreshStop.class);
        if (stickyEvent != null) {
            EventBus.getDefault().removeStickyEvent(stickyEvent);
        }
        if (mSwipeRefreshController != null) {
            mSwipeRefreshController.cancel();
        }

        if (isNotEmpty(mLayoutSwipe)) {
            mLayoutSwipe.setRefreshing(false);
        }
    }

    /**
     * 섹션 업데이트
     */
    public class PageUpdateController extends BaseAsyncController<ContentsListInfo> {

        @Inject
        private RestClient restClient;
        private String url;
        private boolean isCacheData;
        private Context context;

        private FlexibleShopAdapter flexibleAdapter;

        protected PageUpdateController(Context activityContext, FlexibleShopAdapter flexibleAdapter) {
            super(activityContext);
            this.context = activityContext;
            this.flexibleAdapter = flexibleAdapter;
        }

        @Override
        protected void onPrepare(Object... params) throws Exception {
            super.onPrepare(params);
            url = (String) params[0];
            isCacheData = (boolean) params[1];
            if (dialog != null) {
                dialog.setCancelable(false);
            }
        }

        @Override
        protected ContentsListInfo process() throws Exception {
            // 플렉서블 매장 카테고리 탭 이미지 다운로드.
            return (ContentsListInfo) DataUtil.getData(context, restClient, ContentsListInfo.class,
                    isCacheData, true, url + "&reorder=true", null);
        }

        @Override
        protected void onError(Throwable e) {
            super.onError(e);
            //오류시 다시 로딩 안함.
            isPageLoading = true;
        }

        @Override
        protected void onSuccess(ContentsListInfo listInfo) throws Exception {
            isPageLoading = false;
            flexibleAdapter.getInfo().ajaxPageUrl = listInfo.ajaxfullUrl;
            int startDataSize = flexibleAdapter.getInfo().contents.size();
            int updateDataSize = listInfo.productList.size();

            if (updateDataSize == 0) {
                isPageLoading = true;
                return;
            }
            ShopInfo.ShopItem content;
            //마지막에 푸터가있으면 삭제
            if (startDataSize > 0 && flexibleAdapter.getInfo().contents.get(startDataSize - 1).type == ViewHolderType.BANNER_TYPE_FOOTER) {
                flexibleAdapter.getInfo().contents.remove(startDataSize - 1);
            }

            for (int i = 0; i < listInfo.productList.size(); i++) {
                SectionContentList subContent = listInfo.productList.get(i);
                int subType = getFlexibleViewType(subContent);
                if (subType != ViewHolderType.BANNER_TYPE_NONE) {
                    content = new ShopInfo.ShopItem();
                    content.type = subType;
                    content.sectionContent = subContent;
                    flexibleAdapter.getInfo().contents.add(content);
                }
            }

            //푸터 저장
            content = new ShopInfo.ShopItem();
            content.type = ViewHolderType.BANNER_TYPE_FOOTER;
            flexibleAdapter.getInfo().contents.add(content);


            flexibleAdapter.notifyItemRangeInserted(startDataSize, flexibleAdapter.getInfo().contents.size());
        }
    }


    /**
     * @param view view
     */
    private void setupWebControl(View view) {
        if (getActivity() != null) {
            mWebControl = new WebViewControlInherited.Builder(getActivity())
                    .target((WebView) view.findViewById(R.id.webview))
                    .with(new MainWebViewClient(getActivity()) {

                        @Override
                        public void onPageStarted(WebView view, String url, Bitmap favicon) {
                            Ln.i("[FlexibleShopFragment onPageStarted]" + url);
                            super.onPageStarted(view, url, favicon);
                            // 메인 페이지에서만 검색바가 보이고 그 외의 페이지에서는 사라지도록 함.
                        }

                        @Override
                        public void onPageFinished(WebView view, String url) {
                            super.onPageFinished(view, url);
                        }

                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            Ln.i("[FlexibleShopFragment shouldOverrideUrlLoading]" + url);
                            return super.shouldOverrideUrlLoading(view, url);
                        }
                    }).with(new MainWebViewChromeClient(getActivity()))
                    .with(new WebViewProgressBar(
                            (ProgressBar) getActivity().findViewById(R.id.mc_webview_progress_bar)))
                    .build();

            // 웹뷰 컨텍스트 메뉴 활성화
            registerForContextMenu(mWebControl.getWebView());

            // 웹뷰 오른쪽 여백 없애기
            mWebControl.getWebView().setVerticalScrollbarOverlay(true);

            backHandler = new WebPageBackHandler(getActivity(), mWebControl.getWebView());
        }
    }

    /**
     * 캐시된 메인과 매장 정보 가져옴
     *
     * @return HomeGroupInfo
     */
    protected HomeGroupInfo getHomeGroupInfo() {
        return PrefRepositoryNamed.get(MainApplication.getAppContext(), CACHE.HOME_GROUP_INFO, HomeGroupInfo.class);
    }

    /**
     * ViewPager에 표시할 데이타를 가져와서 화면을 그린다.
     */
    @Override
    public void drawFragment() {

//         setSwipeRefresh();
        // TV생방송 존재 여부에 상관없이 전체 data loading
        new GetUpdateController(getActivity(), tempSection, true).execute();
    }


    /**
     * 앱내 데이터가 날아가거나 백그라운드로 앱이 넘어갔을때 데이터 복구를 위해 사용
     *
     * @return Bundle
     */
    public Bundle saveState() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("restore", true);
        bundle.putInt("position", mCoordinator.getCurrentViewPosition());
        return bundle;
    }

    /**
     * 상단 배너, 상품 리스트, 푸터 설정
     *
     * @param sectionList      sectionList
     * @param contentsListInfo contentsListInfo
     * @param tab              tab
     * @param listPosition
     */

    /**
     * tensera
     * 1. Block scroll to top during refresh – in method void updateList(TopSectionList
     * sectionList, ContentsListInfo contentsListInfo, int tab, int listPosition) wrap the line
     * ((StaggeredGridLayoutManager)
     * this.mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(0, 0);
     */
    public void updateList(TopSectionList sectionList, ContentsListInfo contentsListInfo, int tab, int listPosition) {
        //Ln.i("FlexibleShopFragment setInitUI");
        if ("WEB".equals(sectionList.sectionType)) {
            // ;
        } else {
            if (contentsListInfo != null || getActivity() != null) {

                ShopInfo info = new ShopInfo();

                //네비게이션 아이디 세팅
                info.naviId = sectionList.navigationId;
                //모바일라이브 방송알림 이벤트를 위해 세팅
                myNaviId = info.naviId;

                // 배너.
                ShopItem content;
                isPageLoading = false;
                if (contentsListInfo.ajaxPageUrl != null && !"".equals(contentsListInfo.ajaxPageUrl)) {
                    info.ajaxPageUrl = contentsListInfo.ajaxPageUrl;
                } else if (contentsListInfo.ajaxfullUrl != null && !"".equals(contentsListInfo.ajaxfullUrl)) {
                    info.ajaxPageUrl = contentsListInfo.ajaxfullUrl;
                }

                // GS X Brand 개인화 탭 예외처리. (GR_PMO_T2 아래에 있는 subProductList 를 다 끄집어내서 같은 레벨에 위치시킨다. 더보기 버튼도 별도 처리.)
                // 2020.06.18 yun.
                for (int i = 0; i < contentsListInfo.productList.size(); i++) {
                    if ("GR_PMO_T2".equals(contentsListInfo.productList.get(i).viewType)) {
                        List<SectionContentList> reorderList = new ArrayList<>();
                        for (SectionContentList sc : contentsListInfo.productList.get(i).subProductList) {
                            if ("PRD_2".equals(sc.viewType)) {
                                sc.isSameItem = true;
                            }
                            reorderList.add(sc);
                        }
                        SectionContentList sectionContentList = new SectionContentList();
                        sectionContentList.viewType = "GR_PMO_T2_MORE";
                        sectionContentList.linkUrl = contentsListInfo.productList.get(i).moreBtnUrl;
                        reorderList.add(sectionContentList);

                        int j = 0;
                        for (SectionContentList reorder : reorderList) {
                            j++;
                            contentsListInfo.productList.add(i + j, reorder);
                        }
                    }
                }
                ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


                if (listPosition >= 0) {
                    info = mAdapter.getInfo();

                    List<ShopItem> tempContents = info.contents.subList(0, listPosition);
                    info.contents = new ArrayList<>(tempContents);

                    mAdapter.notifyItemRangeRemoved(listPosition + 1, mAdapter.getItemCount());
                }
                else {
                    info.contents = new ArrayList<>();
                    info.sectionList = sectionList;


                    // 상단 배너. imageUrl로 배너 존재 여부 체크.
                    if (contentsListInfo.banner != null
                            && DisplayUtils.isValidString(contentsListInfo.banner.imageUrl)) {
                        content = new ShopItem();
                        content.type = ViewHolderType.BANNER_TYPE_BAND;
                        content.sectionContent = new SectionContentList();
                        content.sectionContent.imageUrl = contentsListInfo.banner.imageUrl;
                        content.sectionContent.linkUrl = contentsListInfo.banner.linkUrl;
                        content.sectionContent.gsAccessibilityVariable = contentsListInfo.banner.title;
                        content.sectionContent.viewType = "B_PZ";
                        info.contents.add(content);
                    }

                    // 이벤트 메뉴. 헤더.
                    if (contentsListInfo.headerList != null && !contentsListInfo.headerList.isEmpty()) {
                        for (SectionContentList header : contentsListInfo.headerList) {
                            int type = getFlexibleViewType(header);
                            if (type != ViewHolderType.BANNER_TYPE_NONE) {
                                content = new ShopItem();
                                content.type = type;
                                content.sectionContent = header;
                                if (content.sectionContent != null) {
                                    info.contents.add(content);
                                }
                            } else {
                                if ("SE".equals(header.viewType)) {
                                    // 슬라이드 이벤트 뷰타입 설정
                                    if (header.subProductList != null && !header.subProductList.isEmpty()) {
                                        content = new ShopItem();
                                        if (header.subProductList.size() > 1) {
                                            content.type = ViewHolderType.VIEW_TYPE_SE;
                                            content.sectionContent = header;
                                        } else if (header.subProductList.size() == 1) {
                                            // 슬라이드 이미지 갯수가 1개이면 이미지 배너에 넣음.
                                            content.type = ViewHolderType.BANNER_TYPE_ROLL_ONE_IMAGE;
                                            content.sectionContent = header.subProductList.get(0);
                                        }
                                        if (content.sectionContent != null) {
                                            info.contents.add(content);
                                        }
                                    }
                                }
                            }
                        }

                    }

                    // 카테고리.
                    if (sectionList.subMenuList != null && !sectionList.subMenuList.isEmpty()) {
                        info.tabIndex = 0;
                        if (sectionList.viewType.equals(ShopInfo.TYPE_FLEXIBLE)) {
                            // 플렉서블 매장.
                            content = new ShopItem();
                            if ("SUB_PRD_LIST_TEXT".equals(sectionList.subMenuList.get(0).viewType.trim())) {
                                content.type = ViewHolderType.VIEW_TYPE_SUB_PRD_LIST_TEXT;
                                // 현재 당겨서 새로고침 중이면 0번째 리스트로 초기화.
                                if (isNowSwipeRefreshing) {
                                    FlexibleBannerSubTabAdapter.gSelectedPosition = 0;
                                }
                            } else {
                                content.type = ViewHolderType.VIEW_TYPE_FXCLIST;
                            }

                            info.tabIndex = tab;
                            info.contents.add(content);
                        }
                        else if (sectionList.viewType.equals(ShopInfo.TYPE_BEAUTY)) {
                            if (sectionList.subMenuList.size() > 0) {
                                content = new ShopItem();
                                content.type = ViewHolderType.BAN_CX_SLD_CATE_GBA;

                                SectionContentList brandList = new SectionContentList();
                                brandList.subProductList = new ArrayList<SectionContentList>();
                                brandList.viewType = "BAN_CX_SLD_CATE_GBA";
                                for (int i=0; i<sectionList.subMenuList.size(); i++) {
                                    SubMenuList menuItem = sectionList.subMenuList.get(i);
                                    try {
                                        if ("SUB_PRD_LIST_TEXT".equals(menuItem.viewType.trim())) {
                                            SectionContentList itemList = new SectionContentList();
                                            itemList.productName = menuItem.sectionName;
                                            itemList.tabSeq = menuItem.navigationId;
                                            itemList.linkUrl = menuItem.sectionLinkUrl.trim() + "?" + menuItem.sectionLinkParams.trim();
                                            itemList.wiseLog = menuItem.wiseLogUrl;
                                            brandList.subProductList.add(itemList);
                                        }
                                    }
                                    catch (NullPointerException e) {
                                        Ln.e(e.getMessage());
                                    }
                                }
                                content.sectionContent = brandList;
                                info.contents.add(content);
                            }
                        }
                    }

                    // 베스트딜 탭에만 노출되는 아이템 세팅
                    if (sectionList.viewType.equals(ShopInfo.TYPE_BESTDEAL)) {
                        if (contentsListInfo.tvLiveBanner != null
                                && isNotEmpty(contentsListInfo.tvLiveBanner.broadType)
                                && isNotEmpty(contentsListInfo.tvLiveBanner.linkUrl)) {
                            content = new ShopItem();
                            content.type = ViewHolderType.BANNER_TYPE_HOME_TV_LIVE;
                            info.tvLiveBanner = contentsListInfo.tvLiveBanner;
                            info.contents.add(content);
                        }
                        if (contentsListInfo.dataLiveBanner != null
                                && isNotEmpty(contentsListInfo.dataLiveBanner.broadType)
                                && isNotEmpty(contentsListInfo.dataLiveBanner.linkUrl)) {
                            content = new ShopItem();
                            content.type = ViewHolderType.BANNER_TYPE_HOME_TV_DATA;
                            info.dataLiveBanner = contentsListInfo.dataLiveBanner;
                            info.contents.add(content);
                        }

                        //mobile live
                        if (isNotEmpty(contentsListInfo.mobileLiveBanner) || isNotEmpty(contentsListInfo.mobileLiveDefaultBanner)) {
                            content = new ShopInfo.ShopItem();
                            content.type = ViewHolderType.BANNER_TYPE_MOBILE_LIVE;
                            info.mobileLiveBanner = contentsListInfo.mobileLiveBanner;
                            info.mobileLiveDefaultBanner = contentsListInfo.mobileLiveDefaultBanner;
                            info.contents.add(content);
                        }
                    }

                    // 날방 탭 생방송영역
                    if (contentsListInfo.tvLiveDealBanner != null
                            && sectionList.viewType.equals(ShopInfo.TYPE_NALBANG) && info != null) {//10/19 품질팀 요청
                        content = new ShopItem();
                        content.type = ViewHolderType.BANNER_TYPE_NALBANG_LIVE;
                        info.tvLiveDealBanner = contentsListInfo.tvLiveDealBanner;
                        info.tvLiveDealBanner.tvLiveDealUrl = getHomeGroupInfo().appUseUrl.tvLiveDealBannerUrl;
                        info.contents.add(content);
                    }

                    //브랜드딜 예외처리
                    if (sectionList.viewType.equals(ShopInfo.TYPE_BRAND) && contentsListInfo.brandBanner != null) {
                        content = new ShopItem();
                        if (contentsListInfo.brandBanner.size() > 1) {
                            content.type = ViewHolderType.BANNER_TYPE_BRAND_ROLL_IMAGE;

                            SectionContentList brandList = new SectionContentList();
                            brandList.subProductList = new ArrayList<SectionContentList>();
                            brandList.subProductList.addAll(contentsListInfo.brandBanner);

                            content.sectionContent = brandList;
                            if (content.sectionContent != null) {
                                info.contents.add(content);
                            }
                        } else if (contentsListInfo.brandBanner.size() == 1) {
                            // 슬라이드 이미지 갯수가 1개이면 이미지 배너에 넣음.
                            content.type = ViewHolderType.BANNER_TYPE_IMAGE;
                            content.sectionContent = contentsListInfo.brandBanner.get(0);
                            if (content.sectionContent != null) {
                                info.contents.add(content);
                            }
                        }
                    }
                }

                if (contentsListInfo.productList != null) {
                    int pos = 0;

                    // 추가한 vip 데코레이션들을 삭제.
                    for (RecyclerView.ItemDecoration decoration : arrVipDecoration) {
                        mRecyclerView.removeItemDecoration(decoration);
                    }
                    arrVipDecoration.clear();

//                    for (SectionContentList c : contentsListInfo.productList) {
                    for (int i=0; i<contentsListInfo.productList.size(); i++) {
                        SectionContentList c = contentsListInfo.productList.get(i);

                        // VIP 테두리 테스트 용
                        // c.newYn = "Y";

                        int type = getFlexibleViewType(c);

                        if (type != ViewHolderType.BANNER_TYPE_NONE && type != ViewHolderType.BANNER_TYPE_VOD_ORD
                                && type != ViewHolderType.BANNER_TYPE_VOD_CARD_POPUP) {
                            content = new ShopItem();
                            content.type = type;
                            content.sectionContent = c;

                            //상품중 API를 별도 호출해야 하는 케이스 정의 (개인화 동적배너)
                            if (type == ViewHolderType.VIEW_TYPE_API_LOGIN_BAN_TXT_IMG_COLOR_GBA
                                    || type == ViewHolderType.VIEW_TYPE_API_SUB_SEC_LINE
                                    || type == ViewHolderType.VIEW_TYPE_API_SRL) {

                                long timestamp = System.currentTimeMillis();
                                content.timestamp = timestamp;

                                if (TextUtils.isEmpty(c.linkUrl)) {
                                    //API주소가 유효하지 않으면 컨텐츠 리스트에 포함시키지 않는다.
                                    content.sectionContent = null;
                                } else {
                                    if (type == ViewHolderType.VIEW_TYPE_API_LOGIN_BAN_TXT_IMG_COLOR_GBA) {
                                        //로그인한 경우만 노출함
                                        User user = User.getCachedUser();
                                        if (user != null && !TextUtils.isEmpty(user.customerNumber)) {
                                            loadPsnlData(mActivity, type, c.linkUrl, timestamp);
                                        } else {
                                            //비로그인 상태라면 항목 제거 및 API 호출 안함
                                            content.sectionContent = null;
                                        }
                                    } else {
                                        loadPsnlData(mActivity, type, c.linkUrl, timestamp);
                                    }
                                }
                            }

                            if (sectionList.viewType.equals(ShopInfo.TYPE_FLEXIBLE)) {
                                if (type == ViewHolderType.VIEW_TYPE_B_IG4XN) {
                                    if (content.sectionContent.imageUrl == null || "".equals(content.sectionContent.imageUrl)) {
                                        content.type = ViewHolderType.BANNER_TYPE_NONE;
                                    }
                                }
                            }

                            //브랜드딜 예외처리
                            if (sectionList.viewType.equals(ShopInfo.TYPE_BRAND) && info.categoryIndex != null) {

                                if (!info.categoryIndex.isEmpty()) {
                                    ShopItem dividerContent = new ShopItem();
                                    dividerContent.type = ViewHolderType.BANNER_TYPE_BRAND_DIVIDER;
                                    info.contents.add(dividerContent);
                                }

                                content.type = ViewHolderType.BANNER_TYPE_BRAND_CATEGORY;
                                info.contents.add(content);
                                info.categoryIndex.add(info.contents.indexOf(content));

                                for (SectionContentList sub : c.subProductList) {
                                    ShopItem subContent = new ShopItem();
                                    subContent.type = ViewHolderType.BANNER_TYPE_BRAND_CONTENT;
                                    subContent.sectionContent = sub;
                                    if (content.sectionContent != null) {
                                        info.contents.add(subContent);
                                    }
                                }

                            }
                            else if (sectionList.viewType.equals(ShopInfo.TYPE_GS_SUPER)) {
                                //GS SUPER 스티키해더 세팅
                                if (content.sectionContent != null) {
                                    //첫번째 MAP_CX_GBB 뷰타입을 해더로 사용
                                    if (isFirst && type == ViewHolderType.VIEW_TYPE_MAP_CX_GBB) {
                                        type = ViewHolderType.VIEW_TYPE_MAP_CX_GBB_TAB;
                                        isFirst = false;
                                    }

                                    content.type = type;
                                    info.contents.add(content);

                                    if (type == ViewHolderType.VIEW_TYPE_MAP_CX_GBB_TAB) {
                                        headerViewHolder.setIsHeader(true);
                                        headerViewHolder.onBindViewHolder(getContext(), info.contents.size() - 1, info, "", "", "");
                                    }

                                    if (type == ViewHolderType.VIEW_TYPE_MAP_CX_GBB
                                            || type == ViewHolderType.VIEW_TYPE_MAP_CX_GBB_TAB) {
                                        if (isEmpty(tabPrdMap.get(c.dealNo))) {
                                            //동일 카테고리가 여러개인 경우 최상위 위치만 저장
                                            tabPrdMap.put(c.dealNo, pos);
                                        }
                                    }
                                    pos++;
                                }
                            }
                            // 코드가 더러워 지지만 다른 매장에 영향도에 문제를 주지 않기 위해 추가.
                            else if (sectionList.viewType.equals(ShopInfo.TYPE_WINE)) {
                                if (content.sectionContent != null) {
                                    if (isFirst && type == ViewHolderType.VIEW_TYPE_TAB_ANCH_WINE) {
                                        type = ViewHolderType.VIEW_TYPE_TAB_ANCH_WINE_TOP;
                                        isFirst = false;
                                    }

                                    content.type = type;
                                    info.contents.add(content);

                                    if (type == ViewHolderType.VIEW_TYPE_TAB_ANCH_WINE_TOP) {
                                        headerViewHolder.setIsHeader(true);
                                        headerViewHolder.onBindViewHolder(getContext(), info.contents.size() - 1, info, "", "", "");
                                    }

                                    if (type == ViewHolderType.VIEW_TYPE_TAB_ANCH_WINE
                                            || type == ViewHolderType.VIEW_TYPE_TAB_ANCH_WINE_TOP) {
                                        if (isEmpty(tabPrdMap.get(c.tabSeq))) {
                                            //동일 카테고리가 여러개인 경우 최상위 위치만 저장
                                            tabPrdMapWine.put(c.tabSeq, pos);
                                        }
                                    }
                                    pos++;
                                }
                            }
                            else {
                                if (content.sectionContent != null) {
                                    // 640 캐로셀의 아이템이 1개일 경우 C_B1 어뎁터를 그대로 쓰면 허전하게 나오는 문제를 해결하기 위해
                                    // 타이틀 + 640 prd1 상품으로 가공
                                    if (type == ViewHolderType.VIEW_TYPE_PRD_C_B1) {
                                        if (c.subProductList.size() == 1) {
                                            // title.
                                            content = new ShopItem();
                                            content.type = ViewHolderType.VIEW_TYPE_BAN_TXT_IMG_LNK_GBA;
                                            content.sectionContent = c;
                                            content.sectionContent.bdrBottomYn = "Y";
                                            info.contents.add(content);
                                            // 640 prd 1
                                            content = new ShopItem();
                                            content.type = ViewHolderType.VIEW_TYPE_PRD_1_640;
                                            content.sectionContent = c.subProductList.get(0);
                                            content.sectionContent.imgBadgeCorner.LT = null;
                                            info.contents.add(content);
                                        } else {
                                            info.contents.add(content);
                                        }
                                    } else {
                                        info.contents.add(content);
                                    }
                                }
                            }

                            if (type == VIEW_TYPE_BAN_VIP_GBA) {
                                final int thisPosition = i;
                                RecyclerView.ItemDecoration decoration = new RecyclerView.ItemDecoration() {
                                    @Override
                                    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                                        super.getItemOffsets(outRect, view, parent, state);
                                        final int adapterPosition = parent.getChildAdapterPosition(view);
                                        if (adapterPosition == thisPosition) {
                                            outRect.bottom = -DisplayUtils.convertDpToPx(getContext(), 80f);
                                        }
                                    }
                                };
                                arrVipDecoration.add(decoration);
                                mRecyclerView.addItemDecoration(decoration);
                            }
                        }
                        else {
                            if ("B_SIS".equals(c.viewType)) {
                                // "슬라이드 이미지배너" = B_SIS (subProductList -> imageUrl, prdUrl)
                                content = new ShopItem();
                                if (c.subProductList.size() > 1) {
                                    content.type = ViewHolderType.VIEW_TYPE_B_SIC;
                                    content.sectionContent = c;
                                    if (content.sectionContent != null) {
                                        info.contents.add(content);
                                    }
                                } else if (c.subProductList.size() == 1) {
                                    // 슬라이드 이미지 갯수가 1개이면 이미지 배너에 넣음.
                                    content.type = ViewHolderType.BANNER_TYPE_IMAGE;
                                    content.sectionContent = c.subProductList.get(0);
                                    if (content.sectionContent != null) {
                                        info.contents.add(content);
                                    }

                                }
                            }
                        }
                    }
                }
                // 마지막 풋터 지정.
                content = new ShopItem();
                content.type = ViewHolderType.BANNER_TYPE_FOOTER;
                info.contents.add(content);

                mAdapter.setInfo(info);
                if (listPosition >= 0) {
                    info.tabIndex = tab;
                    mAdapter.notifyItemRangeInserted(listPosition + 1, mAdapter.getItemCount());
                } else {
                    mAdapter.notifyDataSetChanged();
                    ((StaggeredGridLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(0, 0);
                }

                //매장스크롤이 멈추는 시점에만 캐시작업을 수행하므로 매장로딩 후 처음에 여기서 이벤트 전달하여 캐시 수행
                if (useNativeProduct) {
                    if (getUserVisibleHint()) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                EventBus.getDefault().post(new Events.ImageCacheStartEvent(tempSection.navigationId));
                            }
                        }, 1000);
                    }
                }

                if("54".equals(info.naviId)){
                    MainApplication.homeLoaded = "NONE";
                }
            }
        }

        //[Integration Note] - block scroll to top
        // 위에 로직처름 notifyItemRangeInserted / notifyDataSetChanged+scrollToPositionWithOffset 해야 하는데 조건이 없어서 조정
        /*
        if (!TenseraApi.shouldBlockRefreshScrollToTop()) {
            ((StaggeredGridLayoutManager)this.mRecyclerView.getLayoutManager()).
                    scrollToPositionWithOffset(0, 0);
        }
        */
    }

    /**
     * 개인화 동적배너 API를 호출하여 리스트정보에 업데이트한다.
     *
     * @param context 컨텍스트
     * @param type    뷰타입
     * @param url     api url
     * @param uKey    배너별 유니크값 (리스트정보 업데이트시 사용하기 위함)
     */
    private void loadPsnlData(Context context, int type, String url, long uKey) {
        new BaseAsyncController<SectionContentList>(context) {
            private int type;
            private String url;
            private long uKey;

            @Inject
            private RestClient restClient;

            @Override
            protected void onPrepare(Object... params) throws Exception {
                type = (int) params[0];
                url = (String) params[1];
                uKey = (long) params[2];
            }

            @Override
            protected SectionContentList process() throws Exception {
                return restClient.getForObject(url, SectionContentList.class);
            }

            @Override
            protected void onSuccess(SectionContentList result) throws Exception {
                switch (type) {
                    case ViewHolderType.VIEW_TYPE_API_LOGIN_BAN_TXT_IMG_COLOR_GBA:
                        if (TextUtils.isEmpty(result.productName)) {
                            result = null;
                        }
                        break;
                    case ViewHolderType.VIEW_TYPE_API_SUB_SEC_LINE:
                    case ViewHolderType.VIEW_TYPE_API_SRL:
                        if (result.subProductList == null || result.subProductList.size() == 0) {
                            result = null;
                        }
                        break;
                    default:
                        break;
                }

                mAdapter.updateInfo(result, uKey);
            }

            @Override
            protected void onError(Throwable e) {
                mAdapter.updateInfo(null, uKey);
            }
        }.execute(type, url, uKey);
    }

    protected int getFlexibleViewType(SectionContentList content) {
        int type = getFlexibleViewType(content.viewType);
        switch (type) {
            case BANNER_TYPE_ROLL_IMAGE_NEW_GBA:
            case BANNER_TYPE_ROLL_IMAGE_NEW_GBH:
            case VIEW_TYPE_BAN_SLD_GBB:
                /**
                 * rolling banner에서 subproduct가 1개 이하이면 view를 표시하지 않음.
                 */
                if (content.subProductList == null || content.subProductList.size() < 2) {
                    return ViewHolderType.BANNER_TYPE_NONE;
                }
                break;
        }
        return type;
    }

    protected int getFlexibleViewType(String viewType) {
        int type = ViewHolderType.BANNER_TYPE_NONE;
        if ("PMO_T3_IMG_AB".equals(viewType)) {
            type = ViewHolderType.BANNER_TYPE_RENEWAL_PMO_T3_IMAGE_AB;
        } else if ("SL".equals(viewType)) {
            // todays hot : "슬라이드 상품" = SL (subProductList -> 일반)
            type = ViewHolderType.VIEW_TYPE_SL;
        } else if ("L".equals(viewType) || "".equals(viewType)) {
            // 단품 배너.
            type = ViewHolderType.VIEW_TYPE_L;
        } else if ("PRD_1_640".equals(viewType)) {
            // 단품 배너.
            type = ViewHolderType.VIEW_TYPE_PRD_1_640;
        } else if ("PRD_2".equals(viewType)) {
            // 단품 2개 new
            type = ViewHolderType.VIEW_TYPE_PRD_2;
        } else if ("PRD_1_LIST".equals(viewType)) {
            // 단품 배너 (편성표 부상품 형태)
            type = ViewHolderType.VIEW_TYPE_PRD_1_LIST;
        } else if ("TSL".equals(viewType)) {
            // theme deal : "슬라이드 상품" = SL (subProductList -> 일반)
            type = ViewHolderType.VIEW_TYPE_TSL;

        } else if ("I".equals(viewType) || "B_IXS".equals(viewType) || "B_IS".equals(viewType)
                || "B_IM".equals(viewType) || "B_IL".equals(viewType) || "B_IXL".equals(viewType)
                || "B_IM440".equals(viewType) || "B_INS".equals(viewType) || "B_NIS".equals(viewType)) {
            // 이미지 배너.
            // "이미지특소배너" = B_IXS (imageUrl, prdUrl) -> 35(70)
            // "이미지소배너" = B_IS (imageUrl, prdUrl) -> 50(100)
            // "이미지중배너" = B_IM (imageUrl, prdUrl) -> 160(320)
            // "이미지중440배너" = B_IM440 (imageUrl, prdUrl) -> 220(440)
            // "이미지대배너" = B_IL (imageUrl, prdUrl) -> 240(480)
            // "이미지특대배너" = B_IXL (imageUrl, prdUrl) -> 270(540)
            type = ViewHolderType.BANNER_TYPE_IMAGE;
        } else if ("B_ISS".equals(viewType)) {
            // 이미지 배너.
            // "이미지특소배너" = B_ISS (imageUrl, prdUrl) -> 흰색배경 40(80), 내부이미지 196dp*20dp
            type = ViewHolderType.VIEW_TYPE_B_ISS;
        } else if ("B_MIA".equals(viewType)) {
            // now hot
            // "다중 이미지배너A" = B_MIA (subProductList -> subProjectList -> imageUrl,
            // prdUrl)
            type = ViewHolderType.VIEW_TYPE_B_MIA;
        } else if ("B_TS".equals(viewType)) {
            // "텍스트소배너" = B_TS (productName )
            type = ViewHolderType.BANNER_TYPE_TITLE;
        } else if ("BAN_TXT_IMG_LNK_GBB".equals(viewType)) {
            // yunseung* 위 B_TS 가 변한것. B_TS 일단 지우지 않는다. 2019.10.11.... 나중에 시간이 흐른뒤에도 B_TS 안쓰면 지울것.
            type = ViewHolderType.VIEW_TYPE_BAN_TXT_IMG_LNK_GBB;
        } else if ("DSL_A".equals(viewType) || "DSL_B".equals(viewType)) {
            // "딜 슬라이드 상품A" = DSL_A (imageUrl, prdUrl , subProductList -> 일반) (하단 .... 대신 좌측상단 1/N
            // 형태로 표기, 일반베너 타입 타이들, 자동 스크롤)
            // "딜 슬라이드 상품B" = DSL_B (imageUrl, prdUrl , subProductList -> 일반) (하단 .... 대신 좌측상단 1/N
            // 형태로 표기, 말풍선 타입 타이들)
            type = ViewHolderType.VIEW_TYPE_DSL_X;
        } else if ("DSL_A2".equals(viewType)) {
            // "딜 슬라이드 상품A" = DSL_A (imageUrl, prdUrl , subProductList -> 일반) (하단 .... 대신 좌측상단 1/N
            // 형태로 표기, 일반베너 타입 타이들, 자동 스크롤)
            // "딜 슬라이드 상품B" = DSL_B (imageUrl, prdUrl , subProductList -> 일반) (하단 .... 대신 좌측상단 1/N
            // 형태로 표기, 말풍선 타입 타이들)
            type = ViewHolderType.VIEW_TYPE_DSL_A2;
        } else if ("SUB_SEC_LINE".equals(viewType)) {
            // "중단 메뉴" = SUB_SEC (subProductList -> productName,imageUrl,prdUrl)
            type = ViewHolderType.VIEW_TYPE_SUB_SEC_LINE;
        } else if ("API_SUB_SEC_LINE".equals(viewType)) {
            // 중단 메뉴 (API호출)
            type = ViewHolderType.VIEW_TYPE_API_SUB_SEC_LINE;
        } else if ("SRL".equals(viewType)) {
            // 추천딜
            type = ViewHolderType.VIEW_TYPE_SRL;
        } else if ("API_SRL".equals(viewType)) {
            // 추천딜 (API호출)
            type = ViewHolderType.VIEW_TYPE_API_SRL;
        } else if ("B_HIM".equals(viewType)) {
            type = ViewHolderType.VIEW_TYPE_B_HIM;
        } else if ("PC".equals(viewType)) {
            type = ViewHolderType.VIEW_TYPE_PC;
        } else if ("B_DHS".equals(viewType)) {
            type = ViewHolderType.VIEW_TYPE_B_DHS;
        } else if ("SPL".equals(viewType)) {
            type = ViewHolderType.VIEW_TYPE_SPL;
        } else if ("SPC".equals(viewType)) {
            type = ViewHolderType.VIEW_TYPE_SPC;
        } else if ("B_IG4XN".equals(viewType)) {
            //마트장보기 카테고리
            type = ViewHolderType.VIEW_TYPE_B_IG4XN;
        } else if ("BTL".equals(viewType)) {
            //마트장보기 스페셜
            type = ViewHolderType.VIEW_TYPE_BTL;
        } else if ("TAB_SL".equals(viewType)) {
            //마트장보기 스크롤 배너
            type = ViewHolderType.VIEW_TYPE_TAB_SL;
        } else if ("TCF".equals(viewType)) {
            //카테고리 소팅 탭
            type = ViewHolderType.VIEW_TYPE_TCF;
        } else if ("PDV".equals(viewType)) {
            type = ViewHolderType.VIEW_TYPE_PDV;
        } else if ("B_SIC".equals(viewType)) {
            type = ViewHolderType.VIEW_TYPE_B_SIC;
        } else if ("BAN_SLD_GBB".equals(viewType)) {
            type = ViewHolderType.VIEW_TYPE_BAN_SLD_GBB;
        } else if ("B_CM".equals(viewType)) {
            type = ViewHolderType.VIEW_TYPE_B_CM;
        } else if ("BAN_SLD_GBA".equals(viewType)) {
            type = ViewHolderType.BANNER_TYPE_ROLL_IMAGE_NEW_GBA;
        } else if ("BAN_SLD_GBH".equals(viewType)) {
            type = ViewHolderType.BANNER_TYPE_ROLL_IMAGE_NEW_GBH;
        } else if ("PMO_T2_IMG_C".equals(viewType)) {
            type = ViewHolderType.VIEW_TYPE_PMO_T2_IMG_C;
        } else if ("MAP_SLD_C3_GBA".equals(viewType)) {
            // 빅브랜드 기획전.
            type = ViewHolderType.VIEW_TYPE_MAP_SLD_C3_GBA;
        } else if ("BAN_MUT_H55_GBA".equals(viewType)) {
            // ai 매장 - 링크와 >있는 텍스트 베너
            type = ViewHolderType.VIEW_TYPE_BAN_MUT_H55_GBA;
        } else if ("BAN_IMG_SQUARE_GBA".equals(viewType)) {
            // ai 매장 - 정사각 이미지 상품셀
            type = ViewHolderType.VIEW_TYPE_BAN_IMG_SQUARE_GBA;
        } else if ("MAP_MUT_CATEGORY_GBA".equals(viewType)) {
            // 카테고리 배너.
            type = ViewHolderType.VIEW_TYPE_MAP_MUT_CATEGORY_GBA;
        } else if ("BAN_IMG_H000_GBA".equals(viewType)) {
            // 카테고리 배너.
            type = ViewHolderType.BANNER_TYPE_BAND;
        } else if ("MAP_CX_TXT_GBA".equals(viewType)) {
            // 카드 할인 배너
            type = ViewHolderType.VIEW_TYPE_MAP_CX_TXT_GBA;
        } else if ("BAN_IMG_H000_GBB".equals(viewType)) {
            // 카테고리 배너 아래 공백없음.
            type = ViewHolderType.BANNER_TYPE_BAND_NO_PADDING;
        } else if ("BAN_SLD_GBC".equals(viewType)) {
            // 오늘추천 ad 구좌
            type = ViewHolderType.VIEW_TYPE_BAN_SLD_GBC;
        } else if ("FPC_P".equals(viewType)) {
            // 플로팅 셀타입 카테고리
            type = ViewHolderType.VIEW_TYPE_FPC_P;
        } else if ("BAN_TXT_IMG_GBA".equals(viewType)) {
            // 플로팅 셀타입 카테고리
            type = ViewHolderType.BANNER_TYPE_TODAYOPEN_AD;
        } else if ("BAN_TXT_NODATA".equals(viewType)) {
            // 플로팅 셀타입 카테고리
            type = ViewHolderType.VIEW_TYPE_BAN_TXT_NODATA;
        } else if ("BAN_IMG_C2_GBA".equals(viewType)) {
            // TV쇼핑 2단뷰
            type = ViewHolderType.VIEW_TYPE_BAN_IMG_C2_GBA;
        } else if ("BAN_TXT_IMG_COLOR_GBA".equals(viewType)) {
            // 적립금 안내
            type = ViewHolderType.VIEW_TYPE_BAN_TXT_IMG_COLOR_GBA;
        } else if ("API_LOGIN_BAN_TXT_IMG_COLOR_GBA".equals(viewType)) {
            // 적립금 안내(API호출)
            type = ViewHolderType.VIEW_TYPE_API_LOGIN_BAN_TXT_IMG_COLOR_GBA;
        } else if ("BAN_IMG_C5_GBA".equals(viewType)) {
            // 서비스 매장 바로가기
            type = ViewHolderType.VIEW_TYPE_BAN_IMG_C5_GBA;
        } else if ("BAN_IMG_C3_GBA".equals(viewType)) {
            // TV쇼핑 테마키워드
            type = ViewHolderType.VIEW_TYPE_BAN_IMG_C3_GBA;
        } else if ("CSP_LOGIN_BAN_IMG_GBA".equals(viewType)) {
            // 배너(CSP호출)
            type = ViewHolderType.VIEW_TYPE_CSP_LOGIN_BAN_IMG_GBA;
        } else if ("BAN_TXT_EXP_GBA".equals(viewType)) {
            // 인기검색어 추후 수정
            type = ViewHolderType.VIEW_TYPE_BAN_TXT_EXP_GBA;
        } else if ("BAN_TXT_IMG_LNK_GBA".equals(viewType)) {
            type = ViewHolderType.VIEW_TYPE_BAN_TXT_IMG_LNK_GBA;
        } else if ("PRD_C_SQ".equals(viewType)) {
            // 리뉴얼 프로덕트 리스트용 (VIEW_TYPE_API_SRL 와 동일, 개인화 아닌 Product List 용)
            type = ViewHolderType.VIEW_TYPE_PRD_C_SQ;
        } else if ("MOLOCO_PRD_C_SQ".equals(viewType)) {
            // 몰로코 이런상품 어떠세요
            type = ViewHolderType.VIEW_TYPE_MOLOCO_PRD_C_SQ;
        } else if ("PRD_C_CST_SQ".equals(viewType)) {
            type = ViewHolderType.VIEW_TYPE_PRD_C_CST_SQ;
        } else if ("PMO_T1_PREVIEW_D".equals(viewType)) {
            type = ViewHolderType.VIEW_TYPE_PMO_T1_PREVIEW_D;
        } else if ("PMO_T1_IMG".equals(viewType)) {
            type = ViewHolderType.VIEW_TYPE_PMO_T1_IMG;
        } else if ("PRD_C_B1".equals(viewType)) {
            type = ViewHolderType.VIEW_TYPE_PRD_C_B1;
        } else if ("PMO_T2_PREVIEW".equals(viewType)) {
            type = ViewHolderType.VIEW_TYPE_PMO_T2_PREVIEW;
        } else if ("PMO_T2_IMG".equals(viewType)) {
            type = ViewHolderType.VIEW_TYPE_PMO_T2_IMG;
        } else if ("PMO_T1_PREVIEW_B".equals(viewType)) {
            type = ViewHolderType.VIEW_TYPE_PMO_T1_PREVIEW_B;
        } else if ("PMO_T3_IMG".equals(viewType)) {
            type = ViewHolderType.BANNER_TYPE_RENEWAL_PMO_T3_IMAGE;
        } else if ("BAN_IMG_H000_GBD".equals(viewType)) {
            // PMO_T3_IMG 와 동일
            type = ViewHolderType.BANNER_TYPE_RENEWAL_PMO_T3_IMAGE;
        } else if ("BAN_IMG_SLD_GBA".equals(viewType)) {
            // 이미지 슬라이드 배너 (핑퐁)
            type = VIEW_TYPE_BAN_IMG_SLD_GBA;
        } else if ("BAN_MORE_GBA".equals(viewType)) {
            // 브랜드샵 개인화 더보기
            type = ViewHolderType.VIEW_TYPE_BAN_MORE_GBA;
        } else if ("PRD_PAS_SQ".equals(viewType)) {
            // 브랜드샵 개인화 550 캐러셀
            type = VIEW_TYPE_PRD_PAS_SQ;
        } else if ("BAN_NO_PRD".equals(viewType)) {
            type = VIEW_TYPE_BAN_NO_PRD;
        } else if ("BAN_TXT_CST_GBA".equals(viewType)) {
            // 색상 들어간 타이틀 뷰
            type = VIEW_TYPE_BAN_TXT_CST_GBA;
        } else if ("BAN_VIP_GBA".equals(viewType)) {
            // VIP 매장 웰컴 메세지
            type = VIEW_TYPE_BAN_VIP_GBA;
        } else if ("BAN_VIP_IMG_GBA".equals(viewType)) {
            //VIP 매장 이미지 뷰
            type = VIEW_TYPE_BAN_VIP_IMG_GBA;
        }
        else if ("BAN_VIP_BENE_GBA".equals(viewType)) {
            // VIP 매장 혜택 뷰
            type = VIEW_TYPE_BAN_VIP_BENE_GBA;
        }
        else if ("BAN_VIP_CARD_GBA".equals(viewType)) {
            // VIP 매장 할인 카드 뷰
            type = VIEW_TYPE_BAN_VIP_CARD_GBA;
        }
        else if ("GR_PRD_1_VIP_LIST_GBA".equals(viewType)) {
            // VIP 매장 1단 뷰 GBA
            type = VIEW_TYPE_GR_PRD_1_VIP_LIST_GBA;
        }
        else if ("GR_PRD_2_VIP_LIST_GBA".equals(viewType)) {
            // VIP 매장 2단뷰 GBA (이동)
            type = VIEW_TYPE_GR_PRD_2_VIP_LIST_GBA;
        }
        else if ("GR_PRD_2_VIP_LIST_GBB".equals(viewType)) {
            // VIP 매장 2단뷰 GBA (갱신)
            type = VIEW_TYPE_GR_PRD_2_VIP_LIST_GBB;
        }
        else if ("GR_PRD_1_VIP_LIST_GBB".equals(viewType)) {
            // VIP 매장 1단뷰 GBB (펼침)
            type = VIEW_TYPE_GR_PRD_1_VIP_LIST_GBB;
        }
        else if ("GR_PRD_1_VIP_LIST_GBC".equals(viewType)) {
            // VIP 매장 1단뷰 GBC (찜상품, 펼침)
            type = VIEW_TYPE_GR_PRD_1_VIP_LIST_GBC;
        }
        else if ("PRD_C_B1_VIP_GBA".equals(viewType)) {
            // C_B1 형태의 VIP 매장 뷰
            type = VIEW_TYPE_PRD_C_B1_VIP_GBA;
        }
        else if("RTS_PRD_C_CST_SQ".equals(viewType)){
            type = ViewHolderType.VIEW_TYPE_RTS_PRD_C_CST_SQ;
        }
        else if("TITLE_MOBILE_LIVE".equals(viewType)){
            type = ViewHolderType.VIEW_TYPE_TITLE_MOBILE_LIVE;
        }
        else if("PRD_MOBILE_LIVE".equals(viewType)){
            type = ViewHolderType.VIEW_TYPE_PRD_MOBILE_LIVE;
        }
        else if("PRD_ALARM_MOBILE_LIVE".equals(viewType)){
            type = ViewHolderType.VIEW_TYPE_PRD_ALARM_MOBILE_LIVE;
        }
        else if("NOTICED_MOBILE_LIVE".equals(viewType)){
            type = ViewHolderType.VIEW_TYPE_NOTICED_MOBILE_LIVE;
        }
        else if("BEST_MOBILE_LIVE".equals(viewType)){
            type = ViewHolderType.VIEW_TYPE_BEST_MOBILE_LIVE;
        }
        else if("TITLE_ADDED_SUB_MOBILE_LIVE".equals(viewType)){
            type = ViewHolderType.VIEW_TYPE_TITLE_ADDED_SUB_MOBILE_LIVE;
        }
        else if("PRD_2_MOBILE_LIVE".equals(viewType)){
            type = ViewHolderType.VIEW_TYPE_PRD_2_MOBILE_LIVE;
        }
        else if ("PMO_GSR_C_GBA".equals(viewType)) {
            type = ViewHolderType.VIEW_TYPE_PMO_GSR_C_GBA;
        }
        else if ("BAN_SLD_MOBILE_LIVE_SMALL".equals(viewType)) {
            type = VIEW_TYPE_BAN_SLD_MOBILE_LIVE_SMALL;
        }
        else if ("BAN_SLD_MOBILE_LIVE_BROAD".equals(viewType)) {
            type = VIEW_TYPE_BAN_SLD_MOBILE_LIVE_BROAD;
        }
        else if ("GR_HOME_CATE_TAB_GATE".equals(viewType)) {
            type = VIEW_TYPE_GR_HOME_CATE_TAB_GATE;
        }
        else if ("TITLE_IMG_TXT_WITH_SEPERATOR".equals(viewType)) {
            type = ViewHolderType.VIEW_TYPE_TITLE_IMG_TXT_WITH_SEPERATOR;
        }
        else if ("TAB_MOBILE_LIVE_MENU".equals(viewType)) {
            type = ViewHolderType.VIEW_TYPE_TAB_MOBILE_LIVE_MENU;
        }
        else if ("NO_DATA_MOBILE_LIVE".equals(viewType)) {
            type = ViewHolderType.VIEW_TYPE_NO_DATA_MOBILE_LIVE;
        }
        else if ("MENU_GIFT_RECOMMEND_FIX".equals(viewType)) {
            type = ViewHolderType.VIEW_TYPE_MENU_GIFT_RECOMMEND_FIX;
        }
        // 라운드 들어간 이미지, 쓸데 없는 짓일 지도 모르지만 다른데서도 쓸 것 같아서. 공통으로 뺀다. 동적으로 할 예정
        else if ("BAN_IMG_ROUNDED".equals(viewType)) {
            type = ViewHolderType.VIEW_TYPE_BAN_IMG_ROUNDED;
        }
        // 빅 슬라이딩 배너
        else if ("BAN_SLD_BIG".equals(viewType)) {
            type = ViewHolderType.VIEW_TYPE_BAN_SLD_BIG;
        }
        return type;

    }

    /**
     * @param menus menus
     */
    private void loadIconImages(List<SubMenuList> menus) {
        // 저장(외부) 권한 여부에 따른 분기 처리(from mashmellow)
        // as-is : 메모리에 저장 후 처리
        // to-be : 권한 분기 후 권한 있으면 메모리 저장, 권한 없으면 glide 사용
//		if(PermissionChecker.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED){


        // 어렵네 ㅋㅋ 10/05
        //if (menus == null && menus.size() <= 0 && menus.get(0).viewType.trim().equals("SUB_PRD_LIST_TEXT")) {
        if ((menus == null || menus.isEmpty()) || (menus.get(0).viewType != null && "SUB_PRD_LIST_TEXT".equals(menus.get(0).viewType.trim()))) {
            return;
        }


        for (SubMenuList menu : menus) {
            // off icon
            String filename = menu.sectionImgOffUrl;
            menu.sectionImgOffUrl = ImageUtil.loadImageCacheUrl(getContext(), filename);
            //Ln.i("loadIconImages : " + menu.sectionImgOffUrl);

            // on icon
            filename = menu.sectionImgOnUrl;
            menu.sectionImgOnUrl = ImageUtil.loadImageCacheUrl(getContext(), filename);
            //Ln.i("loadIconImages : " + menu.sectionImgOnUrl);
        }

    }


    /**
     * 해당 카테고리와 가까운 index를 반환한다.
     *
     * @param position position
     * @return int
     */
    private int getNearIndex(int position) {
        ShopInfo info = mAdapter.getInfo();
        int nearIndex = 0;
        if (info != null && info.categoryIndex != null) {
            for (int i = 0; i < info.categoryIndex.size(); i++) {
                if (position >= info.categoryIndex.get(i)) {
                    nearIndex = i;
                }
            }
        }

        return nearIndex;
    }

    /**
     * 버튼 상태를 변경한다.
     *
     * @param button   button
     * @param isEnable isEnable
     */
    private void setButtonState(Button button, boolean isEnable) {
        if (isEnable) {
            button.setBackgroundColor(getResources().getColor(android.R.color.white));
            button.setTextColor(getResources().getColor(R.color.brand_deal_btn_press));
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        } else {
            button.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            button.setTextColor(getResources().getColor(R.color.brand_deal_btn_default));
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        }
    }


    /**
     * classes
     */

    /**
     * 섹션 업데이트
     */
    public class GetUpdateController extends BaseAsyncController<ContentsListInfo> {
        @Inject
        protected TopSectionList tempSection = null;
        protected boolean dialogFlag = false;
        protected boolean isCacheData = false;
        protected Context context = null;
        @Inject
        protected RestClient restClient;

        boolean isFromSwipeRefresh = false;

        public GetUpdateController(Context activityContext, TopSectionList sectionList, boolean isCacheData) {
            super(activityContext);
            context = activityContext;
            tempSection = sectionList;
            this.isCacheData = isCacheData;
        }

        @Override
        protected void onPrepare(Object... params) throws Exception {
            if (params.length > 0 && params[0] != null) {
                isFromSwipeRefresh = (boolean) params[0];
            }

            if (getUserVisibleHint()) {
                if (dialog != null) {
                    dialog.dismiss();
                    dialog.setCancelable(true);
                    if (!isFromSwipeRefresh) {
                        dialog.show(); //로딩시 불렸을때 똥글뱅이 보이도록
                    } else {
                        dialog.hide(); //새로고침시 불렸을때 똥글뱅이 안보이도록
                    }
                }
            }
        }

        @Override
        protected ContentsListInfo process() throws Exception {
            // 플렉서블 매장 카테고리 탭 이미지 다운로드.
            if (tempSection.subMenuList != null && !tempSection.subMenuList.isEmpty()) {
                if (tempSection.viewType.equals(ShopInfo.TYPE_FLEXIBLE)) {
                    // 플렉서블 매장.
                    loadIconImages(tempSection.subMenuList);
                }
            }


            return (ContentsListInfo) DataUtil.getData(context, restClient, ContentsListInfo.class,
                    isCacheData, true, tempSection.sectionLinkUrl,
                    tempSection.sectionLinkParams + "&reorder=true", tempSection.sectionName);
        }

        @Override
        protected void onSuccess(ContentsListInfo listInfo) throws Exception {
            // 프로그래스바 안보이게.
            mRefreshView.setVisibility(View.GONE);


            //인위적 뷰타입 테스트를 위한 테스트 코드 bbori
            /*if(listInfo.productList.get(5)!=null){
                listInfo.productList.get(5).viewType = "PRD_ALARM_MOBILE_LIVE";//(뷰타입 임의적으로 바꾸는 곳)
                listInfo.productList.get(5).name = "쇼핑라이브 방송알림";
                listInfo.productList.get(5).imageUrl = "http://image.gsshop.com/image/59/07/59070376_B1.jpg";
                listInfo.productList.get(5).setBroadAlarmDoneYn("N");
            }*/

            boolean isPopupAvailable = false;
            try {
                // 더이상 보지 않기한 날짜와 현재 날짜 비교하여 지났으면 팝업 보여줘도 괜찮다.
                String prevDate = PrefRepositoryNamed.getString(getContext(), Keys.PREF.PREF_SHOPPY_LIVE_TODAY_PASS);
                if (isEmpty(prevDate) || DateFormat.format("yyyyMMdd", new Date()).
                        toString().compareTo(prevDate) > 0) {
                    isPopupAvailable = true;
                }
            }
            catch (NullPointerException e){
                Ln.e(e.getMessage());
            }

            // 계속 나오는것이 맞다고 답변 들어 naviId를 계속 확인해도 문제 없음.
            if ( ShopInfo.NAVIGATION_SHOPPY_LIVE.equals(tempSection.navigationId) &&
                    !TextUtils.isEmpty(listInfo.couponPopupUrl) &&
                    isPopupAvailable ) {
                WebUtils.goWeb(context, listInfo.couponPopupUrl);
            }

            // 현재 액티비티 활성화 상태 체크
            if (getActivity() != null && listInfo != null && listInfo.productList != null) {


                //for(int i = 0; i<listInfo.productList.size() ; i++){
                //listInfo.productList.get(i).viewType = "BAN_VOD_GBC";
                //}listInfo.productList.get(0).subProductList.get(0).discountRate = "10";

                // 클릭 URL 이 있으면 저장해 놓는다.
                if (!TextUtils.isEmpty(listInfo.commonClickUrl)) {
                    mCommonClickUrl = listInfo.commonClickUrl;

                    // 현재 보이는 중이라면 wise log 호출
                    if (getUserVisibleHint()) {
                        try {
                            ((AbstractBaseActivity) context).setWiseLogHttpClient(listInfo.commonClickUrl);
                        } catch (ClassCastException | NullPointerException | NoSuchMethodError e) {
                            Ln.e(e.getMessage());
                        }
                    }
                }
                updateList(tempSection, listInfo, 0, -1);
            }

            // 장바구니 카운트를 업데이트 한다.
            mActivity.setBasketcnt();
            // SwipeRefreshing 프로그래스 바 hide
            mLayoutSwipe.setRefreshing(false);
            isNowSwipeRefreshing = false;
        }

        @Override
        protected void onError(Throwable e) {
            // SwipeRefreshing 프로그래스 바 hide
            mLayoutSwipe.setRefreshing(false);
            isNowSwipeRefreshing = false;

            if (mRecyclerView != null && mAdapter.getItemCount() > 0) {
                mRefreshView.setVisibility(View.GONE);
            } else {
                mRefreshView.setVisibility(View.VISIBLE);
            }

            // 네트워크 에러팝업 중복으로 뜨는 현상 개선 (에러팝업을 SetABTestController에서 띄우지 않고 본 컨트롤러에서 띄운다.)
            super.onError(e);
        }

        @Override
        protected void onCancelled() {
            if (isFromSwipeRefresh) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            } else {
                super.onCancelled();
            }
        }
    }

    /**
     * 중카테고리 업데이트
     *
     * @param event event
     */
    public void onEvent(Events.FlexibleEvent.UpdateFlexibleShopEvent event) {
        if (getUserVisibleHint()) {
            new GetUpdateCategoryController(getActivity(), event.tab, event.listPosition, true, true).execute();
        }

    }

    public class GetUpdateCategoryController extends BaseAsyncController<ContentsListInfo> {
        @Inject
        protected RestClient restClient;
        protected final int tab;
        protected final int listPosition;
        protected final boolean dialogFlag;
        protected final boolean isCacheData;

        public GetUpdateCategoryController(Context activityContext, int tab, int listPosition, boolean flag,
                                           boolean isCacheData) {
            super(activityContext);
            context = activityContext;
            this.tab = tab;
            this.listPosition = listPosition;
            this.dialogFlag = flag;
            this.isCacheData = isCacheData;
        }

        @Override
        protected void onPrepare(Object... params) throws Exception {

            if (dialogFlag) {
                if (dialog != null) {
                    try {
                        dialog.dismiss();
                        dialog.setCancelable(true);
                        dialog.show();
                    } catch (WindowManager.BadTokenException e) {
                        Ln.e(e.getMessage());
                    }
                }
            }
        }

        @Override
        protected ContentsListInfo process() throws Exception {

            // 플렉서블 매장 카테고리 탭 이미지 다운로드.
            if (!tempSection.subMenuList.isEmpty()) {
                if (tempSection.viewType.equals(ShopInfo.TYPE_FLEXIBLE)) {
                    // 플렉서블 매장.
                    loadIconImages(tempSection.subMenuList);
                }
            }

            return (ContentsListInfo) DataUtil.getData(context, restClient, ContentsListInfo.class,
                    isCacheData, true, tempSection.subMenuList.get(this.tab).sectionLinkUrl,
                    tempSection.subMenuList.get(this.tab).sectionLinkParams + "&reorder=true");
        }

        @Override
        protected void onSuccess(ContentsListInfo listInfo) throws Exception {
            mRefreshView.setVisibility(View.GONE);
            // 현재 액티비티 활성화 상태 체크
            if (getActivity() != null && listInfo != null && listInfo.productList != null) {
                updateList(tempSection, listInfo, this.tab, this.listPosition);
            }

            mLayoutSwipe.setRefreshing(false);
            isNowSwipeRefreshing = false;
        }

        @Override
        protected void onError(Throwable e) {
            if (mRecyclerView != null && mAdapter.getItemCount() > 0) {
                mRefreshView.setVisibility(View.GONE);
            } else {
                mRefreshView.setVisibility(View.VISIBLE);
            }

            // 네트워크 에러팝업 중복으로 뜨는 현상 개선 (에러팝업을 SetABTestController에서 띄우지 않고 본 컨트롤러에서 띄운다.)
            //super.onError(e);  ex. 매직딜데이, 봄패션 처럼 카테고리있는 매장 두군데가 동시에 이벤트를 받고 업데이트할때 -> 뒤에 있는 매장이 메모리 없을경우 에러팝업뜨는 문제 임시조치
        }
    }

    protected class UrlUpdateController extends BaseAsyncController<ContentsListInfo> {

        @Inject
        protected RestClient restClient;

        private String url;
        private boolean isCacheData;
        private boolean isFromSwipeRefresh = false;

        public UrlUpdateController(Context activityContext) {
            super(activityContext);
        }

        @Override
        protected void onPrepare(Object... params) throws Exception {
            url = (String) params[0];
            isCacheData = (boolean) params[1];
            if (params.length > 2) {
                isFromSwipeRefresh = (boolean) params[2];
            }
            if (((Activity) context).hasWindowFocus() && !isFromSwipeRefresh) {
                super.onPrepare(params);
            }
        }

        @Override
        protected ContentsListInfo process() throws Exception {
            // 플렉서블 매장 카테고리 탭 이미지 다운로드.


            return (ContentsListInfo) DataUtil.getData(context, restClient, ContentsListInfo.class,
                    isCacheData, true, url + "&reorder=true", null);
        }

        @Override
        protected void onSuccess(ContentsListInfo listInfo) throws Exception {
            mRefreshView.setVisibility(View.GONE);
            mLayoutSwipe.setRefreshing(false);
            isNowSwipeRefreshing = false;

            // 현재 액티비티 활성화 상태 체크
            if (getActivity() != null && listInfo != null && listInfo.productList != null) {
                updateList(tempSection, listInfo, 0, -1);
            }

            // 장바구니 카운트를 업데이트 한다.
            mActivity.setBasketcnt();

        }

        @Override
        protected void onError(Throwable e) {
            mLayoutSwipe.setRefreshing(false);
            if (mRecyclerView != null && mAdapter.getItemCount() > 0) {
                mRefreshView.setVisibility(View.GONE);
            } else {
                mRefreshView.setVisibility(View.VISIBLE);
            }

            // 네트워크 에러팝업 중복으로 뜨는 현상 개선 (에러팝업을 SetABTestController에서 띄우지 않고 본 컨트롤러에서 띄운다.)
            super.onError(e);
        }

        @Override
        protected void onCancelled() {
            if (isFromSwipeRefresh) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            } else {
                super.onCancelled();
            }
        }
    }

    public void onEvent(Events.FlexibleEvent.RefreshCart event) {
        if (mActivity != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mActivity.setBasketcnt();
                }
            });
        }

    }

    /**
     * 광고 영역 노출 팝업을 띄운다. \
     * (FlexibleShopFragment를 상속받는 모든 fragment 대상으로 하기 위해 FlexibleShopFragment 로 위치 옮김)
     *
     * @param event
     */
    public void onEventMainThread(Events.FlexibleEvent.AdTooltipEvent event) {
        // 모든 툴팁 동작하지 않도록 수정 (20190515)
        if (true) {
            return;
        }

        if (mPopupWindow == null) {
            if (popupView == null) {
                popupView = LayoutInflater.from(getActivity()).inflate(R.layout.popup_window, null);
            }
            mPopupWindow = new PopupWindow(popupView,
                    RelativeLayout.LayoutParams.WRAP_CONTENT, StaggeredGridLayoutManager.LayoutParams.WRAP_CONTENT);
            mPopupWindow.setAnimationStyle(0);
        }

        // mPopupWindow 가 null 일 경우 상단부분에 새로 선언, 선언 했음에도(그럴리 없겠지만) null 일경우 return;
        if (mPopupWindow == null) {
            return;
        }

        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
            return;
        }

        //
        if (!event.isShow) {
            return;
        }

        if (event.rect != null) {
            PointF location = new PointF();
            final PointF anchorCenter = new PointF(event.rect.right, event.rect.centerY());
            location.x = anchorCenter.x;
            location.y = event.rect.bottom;

            mPopupWindow.showAtLocation(popupView, Gravity.TOP, (int) location.x, (int) location.y);
            mPopupWindow.update((int) location.x, (int) location.y, mPopupWindow.getWidth(), mPopupWindow.getHeight());
        }
        popupView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });
    }

    /**
     * 현재 화면에 보여지는 아이템들의 position list 를 반환한다.
     *
     * @return items position list
     */
    private ArrayList<Integer> getVisibleItemsPosition() {
        int firstViewsIds = mLayoutManager.findFirstCompletelyVisibleItemPositions(null)[0];
        int lastViewsIds = mLayoutManager.findLastCompletelyVisibleItemPositions(null)[0];

        if (firstViewsIds < 0 || lastViewsIds < 0) {
            return new ArrayList<Integer>();
        }

        ArrayList<Integer> visibleItemsPosition = new ArrayList<>();
        int visibleItemsCount = lastViewsIds - firstViewsIds + 1;
        for (int i = 0; i < visibleItemsCount; i++) {
            visibleItemsPosition.add(firstViewsIds + i);
        }

        return visibleItemsPosition;
    }

    /**
     * 상품 2초 노출 효율코드 발송을 위한 runnable
     *
     * @param context
     */
    private void setSendWiseLogRunnable(Context context) {
        // 2초 뒤에도 처음 보였던 아이템이 보인다면 효율코드 전송.
        mRunnableSendWiseLog = () -> {
            if (tempSection != null) {
                if (tempSection.navigationId.equals(ShopInfo.NAVIGATION_HOME)) {
                    if (mLastVisibleItemsPositionList.size() > 0) {
                        boolean isEqual = true;
                        ArrayList<Integer> visibleItemsPositionList = getVisibleItemsPosition();

                        for (int i = 0; i < visibleItemsPositionList.size(); i++) {
                            if (!visibleItemsPositionList.get(i).equals(mLastVisibleItemsPositionList.get(i))) {
                                isEqual = false;
                            }
                        }

                        if (isEqual) {
                            // 2초전에 보였던 뷰홀더랑 지금 보이는 뷰홀더랑 같다면 현재 보이는 뷰홀더의 position 값을 가지고 전체 뷰홀더 list 중 해당 position 의 viewType 을 가져오고
                            // 그 뷰타입이 만약 C_SQ 라면 효율코드를 보내야함.
                            for (int i = 0; i < visibleItemsPositionList.size(); i++) {
                                int visibleItemPosition = visibleItemsPositionList.get(i);
                                int viewType = mAdapter.getItemViewType(visibleItemPosition);
                                if (ViewHolderType.VIEW_TYPE_MOLOCO_PRD_C_SQ == viewType) {
                                    ((HomeActivity) context).setWiseLogHttpClient(ServerUrls.WEB.MSEQ_HOME_MOLOCO_C_SQ);

                                    // PRD_C_SQ 전체가 보임과 동시에 뷰홀더 안에 있는 RecylcerView Item 이 동시에 보임.
                                    // 그 아이템들도 효율코드 날려줘야해서 아래 for 문을 통해 이벤트를 날려준다.
                                    for (int j = 0; j < mLastVisibleItemsPositionList.size(); j++) {
                                        int visibleItemPosition2 = mLastVisibleItemsPositionList.get(j);
                                        try {
                                            SectionContentList sectionContentList = ((ShopItem) mAdapter.getItem(visibleItemPosition2)).sectionContent;
                                            if (sectionContentList != null) {
                                                if ("MOLOCO_PRD_C_SQ".equalsIgnoreCase(sectionContentList.viewType)) {
                                                    EventBus.getDefault().post(new Events.FlexibleEvent.SendPrdCSqWiseLogEvent());
                                                }
                                            }
                                        } catch (NullPointerException e) {
                                            Ln.e(e.getMessage());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        };
    }


    /**
     * 모바일라이브 방송알림 등록/해제 확정했을때 이벤트
     */
    public void onEvent(Events.AlarmRegistMLEvent event) {
        Events.AlarmRegistMLEvent alarmRegistMLEvent = EventBus.getDefault().getStickyEvent(Events.AlarmRegistMLEvent.class);
        if (alarmRegistMLEvent != null) {
            EventBus.getDefault().removeStickyEvent(alarmRegistMLEvent);
        }

        try {
            //모바일라이브 탭매장의 FlexibleFragment에서만 보내도록
            if(DisplayUtils.isValidString(myNaviId) && myNaviId.equals(event.naviId)){
                if(MOBILE_LIVE_LIST_CALLER.equals(event.caller)){
                    //등록 확정 했을때
                    if(MobileLiveAlarmDialogFragment.MOBILELIVE_ADD.equals(event.type)){
                        MLAlarm.add(getContext(), event.isNightAlarm, event.caller);
                    }
                    //해제 확정 했을때
                    else if(MobileLiveAlarmCancelDialogFragment.MOBILELIVE_DELETE.equals(event.type)){
                        MLAlarm.delete(getContext(), event.caller);
                    }
                }
            }
        }catch (Exception e){
            Ln.e(e);
        }
    }



    /**
     * 모바일라이브 방송알림 UI업데이트
     * @param event
     */
    public void onEvent(Events.AlarmUpdatetMLEvent event) { //FelxibleFragment쓰는 다른 매장이 영향받더라도 updateMLAlarmData에서 예외처리
        try {
            if (DisplayUtils.isValidString(myNaviId) && myNaviId.equals(event.naviId) && !TextUtils.isEmpty(event.eventType)) {
                //방송알림 해제 상태로 UI업데이트
                if (Events.AlarmUpdatetMLEvent.MAKE_ALARM_CANCEL.equals(event.eventType)) {
                    mAdapter.updateMLAlarmData(Events.AlarmUpdatetMLEvent.MAKE_ALARM_CANCEL);
                }
                //방송알림 등록 상태로 UI업데이트
                else if (Events.AlarmUpdatetMLEvent.MAKE_ALARM_OK.equals(event.eventType)) {
                    mAdapter.updateMLAlarmData(Events.AlarmUpdatetMLEvent.MAKE_ALARM_OK);
                }
            }
        }catch (Exception e){
            Ln.e(e);
        }
    }


    public void onEvent(Events.FlexibleEvent.RefreshTabEvent event) {
        Events.FlexibleEvent.RefreshTabEvent curationEvent = EventBus.getDefault().getStickyEvent(Events.FlexibleEvent.RefreshTabEvent.class);
        if (curationEvent != null) {
            EventBus.getDefault().removeStickyEvent(curationEvent);
        }

        if (!getUserVisibleHint()) return;

        new RefreshTabController(getContext(), mAdapter).execute(event.getUrl(), event.getRemovePosition(),
                getFlexibleViewType(event.getTabViewType()));//, event.getDelViewType());
    }

    /**
     * 탭 선택하여 갱신하기 위해 호출 후 갱신 viewtype 삭제 후 해당 위치에 갱신하여 가져온 뷰 삽입.
     */
    private class RefreshTabController extends BaseAsyncController<ContentsListInfo>{

        @Inject
        private RestClient restClient;

        private FlexibleShopAdapter mAdapter;
        private String mUrl;
        private int mStartPosition = 0;
        private int mHeaderViewType = ViewHolderType.BANNER_TYPE_NONE;
//        private ArrayList<Integer> mViewType = new ArrayList();
        private Context mContext;

        protected RefreshTabController(Context activityContext, FlexibleShopAdapter adapter) {
            super(activityContext);
            mContext = activityContext;
            mAdapter = adapter;
        }

        @Override
        protected void onPrepare(Object... params) throws Exception {
            super.onPrepare(params);

            try {
                mUrl = params[0].toString();        // 호출 URL
                mStartPosition = (int)params[1];    // 삭제하고 add 될 위치
                mHeaderViewType = (int)params[2];   // 헤더 뷰타입
//                mViewType = ( ArrayList<Integer> )params[3];         // 삭제 될 뷰타입

                if (dialog != null) {
                    dialog.setCancelable(false);
                }
            }
            catch (IndexOutOfBoundsException e) {
                Ln.e(e.getMessage());
            }
            catch (ClassCastException e) {
                Ln.e(e.getMessage());
            }
        }

        @Override
        protected ContentsListInfo process() throws Exception {
            return (ContentsListInfo) DataUtil.getData(mContext, restClient, ContentsListInfo.class, false,
                    true, mUrl, null);
        }

        @Override
        protected void onSuccess(ContentsListInfo listInfo) throws Exception {
            if (listInfo == null || mAdapter == null) {
//                onError(Throwable("Updated list is null"))
                return;
            }

            if (listInfo.productList.size() == 0) {
                return;
            }
            ArrayList<ShopItem> listShopItem = new ArrayList();
            try {
//                var removeStartPosition = -1
//                var removeCnt:Int = 0;
                // 어댑터에 있는 녀석들 다 뒤져서 삭제.
//                val tempList = mAdapter.info.contents

                Iterator iterator = mAdapter.getInfo().contents.iterator();

                // 헤더 뷰타입에 따라서 삭제할 뷰타입들을 정한다.
                ArrayList<Integer> mViewType = new ArrayList();
                if (mHeaderViewType == ViewHolderType.VIEW_TYPE_TAB_MOBILE_LIVE_MENU) {
                    mViewType = new ArrayList(Arrays.asList(ViewHolderType.VIEW_TYPE_PRD_2_MOBILE_LIVE,
                            ViewHolderType.VIEW_TYPE_NO_DATA_MOBILE_LIVE));
                }

                while(iterator.hasNext()) {
                    ShopItem itemIterator = (ShopItem) iterator.next();

                    boolean isMatched = false;
                    for (int type : mViewType){
                        if (itemIterator.type == type || itemIterator.type == type) {
                            isMatched = true;
                            break;
                        }
                    }
                    if (isMatched) {

                        // 해당 하는 녀석들 삭제.
                        int removeIndex = mAdapter.getInfo().contents.indexOf(itemIterator);
                        iterator.remove();
                        mAdapter.notifyItemRemoved(removeIndex);
                    }
                }

                boolean mNoDataLast = false;
                if (listInfo.productList.size() == 1) {
                    int type = getFlexibleViewType(listInfo.productList.get(0).viewType);
                    if (ViewHolderType.VIEW_TYPE_NO_DATA_MOBILE_LIVE == type) {
                        mNoDataLast = true;
                    }
                }

                for (int i = 0; i < listInfo.productList.size(); i++ ) {
                    ShopItem item = new ShopItem();

                    int type = getFlexibleViewType(listInfo.productList.get(i).viewType);

                    item.sectionContent = listInfo.productList.get(i);
                    item.type = type;

                    listShopItem.add(item);
                }

                // 헤더 위치를 다시 찾아서 넣는다.
                // (해당 위치가 아닌 다른 위치 삭제 될 경우 index를 넘어가는 경우가 생긴다.)

                int startIndex = 0;
                for (int i = 0; i < mAdapter.getInfo().contents.size(); i++) {
                    if (mHeaderViewType == mAdapter.getInfo().contents.get(i).type) {
                        startIndex = i + 1;
                        break;
                    }
                }

//                Ln.d("hklim insert start : " + startIndex + " / listShopItem size : " + listShopItem.size +
//                        "adapter size : " + mAdapter.itemCount)

                mAdapter.getInfo().contents.addAll(startIndex, listShopItem);

//                Ln.d("hklim insert end : " + startIndex + " / listShopItem size : " + listShopItem.size +
//                        "adapter size : " + mAdapter.itemCount)
                mAdapter.notifyItemRangeInserted(startIndex, listShopItem.size());

//                mAdapter.notifyDataSetChanged()

            }
            catch (Exception e) {
                Ln.e(e.getMessage());
            }

        }

        @Override
        protected void onError(Throwable e) {
            super.onError(e);
            Toast.makeText(mContext, getString(R.string.app_refresh), Toast.LENGTH_SHORT).show();
        }
    }

}
