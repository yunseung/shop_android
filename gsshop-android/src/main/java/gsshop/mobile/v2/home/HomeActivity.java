/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gsshop.mobile.v2.home;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieListener;
import com.appboy.Appboy;
import com.appboy.models.outgoing.AppboyProperties;
import com.apptimize.Apptimize;
import com.facebook.applinks.AppLinkData;
import com.google.android.material.appbar.AppBarLayout;
import com.google.inject.Inject;
import com.gsshop.mocha.device.AppInfo;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.pattern.chain.Command;
import com.gsshop.mocha.pattern.chain.CommandExecutor;
import com.gsshop.mocha.pattern.mvc.BaseAsyncController;
import com.nineoldandroids.view.ViewHelper;
import com.tms.inappmsg.InAppMessageManager;
import com.tms.inappmsg.listener.InAppMessageListener;
import com.tms.inappmsg.model.InAppMessage;
import com.tms.sdk.api.request.SetConfig;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nonnull;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.AbstractTMSService;
import gsshop.mobile.v2.ApptimizeExpManager;
import gsshop.mobile.v2.ApptimizeTabNameExp;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.Keys.ACTION;
import gsshop.mobile.v2.Keys.CACHE;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.bestdeal.BestDealShopFragment;
import gsshop.mobile.v2.home.shop.bestshop.BestShopFragment;
import gsshop.mobile.v2.home.shop.department.DepartmentStoreFragment;
import gsshop.mobile.v2.home.shop.event.EventShopFragment;
import gsshop.mobile.v2.home.shop.flexible.beauty.BeautyShopFragment;
import gsshop.mobile.v2.home.shop.flexible.exclusive.ExclusiveFragment;
import gsshop.mobile.v2.home.shop.flexible.shoppinglive.ShoppingLiveShopFragment;
import gsshop.mobile.v2.home.shop.flexible.todaysel.TodaySelectFragment;
import gsshop.mobile.v2.home.shop.flexible.wine.WineShopFragment;
import gsshop.mobile.v2.home.shop.gssuper.GSSuperFragment;
import gsshop.mobile.v2.home.shop.ltype.GSChoiceShopFragment;
import gsshop.mobile.v2.home.shop.nalbang.CategoryDataHolder;
import gsshop.mobile.v2.home.shop.nalbang.NalbangShopFragment;
import gsshop.mobile.v2.home.shop.schedule.TVScheduleBroadAlarmDialogFragment;
import gsshop.mobile.v2.home.shop.schedule.TVScheduleShopFragment;
import gsshop.mobile.v2.home.shop.schedule_A.TVScheduleShopFragment_A;
import gsshop.mobile.v2.home.shop.tvshoping.MainShopFragment;
import gsshop.mobile.v2.home.shop.tvshoping.TvShopFragment;
import gsshop.mobile.v2.home.shop.vod.VodShopFragment;
import gsshop.mobile.v2.intro.BadgeCommand;
import gsshop.mobile.v2.intro.PushRegisterCommand;
import gsshop.mobile.v2.intro.RemarketingCommand;
import gsshop.mobile.v2.library.quickreturn.QuickReturnInterface;
import gsshop.mobile.v2.library.viewpager.InfiniteFragmentAdapter;
import gsshop.mobile.v2.library.viewpager.InfiniteViewPager;
import gsshop.mobile.v2.menu.BaseTabMenuActivity;
import gsshop.mobile.v2.menu.TabMenu;
import gsshop.mobile.v2.push.PushSettings;
import gsshop.mobile.v2.push.PushUtils;
import gsshop.mobile.v2.search.RecentRecommandAction;
import gsshop.mobile.v2.search.RecentRecommandInfo;
import gsshop.mobile.v2.search.RecentRecommandList;
import gsshop.mobile.v2.search.SearchNavigation;
import gsshop.mobile.v2.support.airbridge.ABAction;
import gsshop.mobile.v2.support.gtm.AMPAction;
import gsshop.mobile.v2.support.gtm.AMPEnum;
import gsshop.mobile.v2.support.gtm.GTMAction;
import gsshop.mobile.v2.support.gtm.GTMEnum;
import gsshop.mobile.v2.support.gtm.datahub.DatahubAction;
import gsshop.mobile.v2.support.gtm.datahub.DatahubUrls;
import gsshop.mobile.v2.support.pmopopup.PmoNoShowForeverPrefInfo;
import gsshop.mobile.v2.support.pmopopup.PmoNoShowTodayPrefInfo;
import gsshop.mobile.v2.support.pmopopup.PmoPopupCheckResult;
import gsshop.mobile.v2.support.pmopopup.PmoPopupInfo;
import gsshop.mobile.v2.support.pmopopup.PmoPopupuActivity;
import gsshop.mobile.v2.support.promotion.PromotionAction;
import gsshop.mobile.v2.support.promotion.PromotionInfo;
import gsshop.mobile.v2.support.promotion.PromotionMediumActivity;
import gsshop.mobile.v2.support.promotion.PromotionSmallActivity;
import gsshop.mobile.v2.tms.PushApproveActivity;
import gsshop.mobile.v2.user.User;
import gsshop.mobile.v2.util.CookieUtils;
import gsshop.mobile.v2.util.CustomToast;
import gsshop.mobile.v2.util.DateUtils;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.LogUtils;
import gsshop.mobile.v2.util.LunaUtils;
import gsshop.mobile.v2.util.PermissionUtils;
import gsshop.mobile.v2.util.PrefRepositoryNamed;
import gsshop.mobile.v2.util.StaticCsp;
import gsshop.mobile.v2.util.ThreadUtils;
import gsshop.mobile.v2.web.MainWebViewClient;
import gsshop.mobile.v2.web.WebUtils;
import gsshop.mobile.v2.web.WebViewControlInherited;
import roboguice.inject.InjectResource;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isEmpty;
import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;
import static gsshop.mobile.v2.MainApplication.useNativeProduct;
import static gsshop.mobile.v2.support.gtm.AMPEnum.AMP_ACTION_VISIT_PREFIX;
import static gsshop.mobile.v2.util.LunaUtils.autoLoginState;
import static gsshop.mobile.v2.web.WebUtils.LSEQ_PARAM_KEY;
import static gsshop.mobile.v2.web.WebUtils.MEDIA_PARAM_KEY;
import static gsshop.mobile.v2.web.WebUtils.goWeb;

/********************************************************************
 * call gate 및 call after, ars, tensera 관련 등 사용하지 않는
 * 라이브러리들의 주석 및 코드 삭제 하였습니다.
 * 20/07/28 오전 10시 (관련하여 검색 필요시 히스토리에서 해당 시간 검색)
 *******************************************************************/

/**
 * 기본이 되는 메인화면 acvitiy
 */
@SuppressLint("NewApi")
public class HomeActivity extends BaseTabMenuActivity implements QuickReturnInterface,
        TVScheduleBroadAlarmDialogFragment.OnAlarmListener {
    private static final String TAG = "HomeActivity";

    /**
     * collapsable header
     */
    private AppBarLayout appbar_header;

    private static Context mContext;
    private WebViewControlInherited webControl;
    private HomePagerAdapter mPagerAdapter;
    private InfiniteFragmentAdapter wrappedAdapter;

    /**
     * 탭바와 리스트를 가지고 잇는 페이
     */
    public InfiniteViewPager mPager;
    private RecyclerTabLayout mTabPageIndicator;
    private RecyclerTabAdapter mTabAdapter;

    private ArrayList<GroupSectionList> groupSectionArrayList = new ArrayList<GroupSectionList>();
    // GroupSectionList 중에서 홈 화면 정보만 따로 저장
    private GroupSectionList mainSection = new GroupSectionList();
    // mainSection의 sectionList
    private static ArrayList<TopSectionList> sectionList = new ArrayList<TopSectionList>();
    private static ArrayList<String> CONTENT;
    private static ArrayList<String> CONTENT_PUB;
    private static ArrayList<String> CONTENT_PRV;
    private int mainTab = 0;    //사용자가 클릭한 탭 인덱스 저장 (링크를 통한 탭이동도 포함됨)
    private int defaultTab = 0; //앱 로딩시 노출한 탭 인덱스 저장
    private boolean hasPrivateShop = false; //개인화매장 존재 여부
    public static int lastSelectedPosition = -1;  //마지막 선택한 매장 포지션(real position)

    //앱구동시 최초 호출되는 베스트딜 wiselogUrl에 추가되는 파라미터 정의
    private static final String BESTDEAL_EATRA_PARAM = "&appstart=Y";

    /**
     * 특정 그룹의 특정섹션으로 바로 가는 경우에 대한 flag
     */
    public static boolean jumpToSpecificSection = false;
    /**
     * 메인화면 로딩여부 플래그
     */
    public static boolean isMainLoaded;

    /**
     * 탭메뉴 더보기/접기 상태 저장 (홈메뉴 클릭시 상태를 유지하기 위함)
     */
    public static boolean isTabMenuExpanded = false;

    // 프로모션 관련
    private PromotionInfo promotionInfo;
    // 프로모션 팝업 종류 정의
    private static final String PROMOTION_SMALL_TYPE = "S"; //small size
    private static final String PROMOTION_MEDIUM_TYPE = "M"; //medium size
    private static final String PROMOTION_LARGE_TYPE = "B"; //large size
    private static final String PUSH_APPROVE_TYPE = "P"; //push approve

    //사이렌 버튼 노출대상 정의 (테스트계정 및 임직원)
    public static final List<String> listCusClass = Arrays.asList("02", "05");
    public static final String sirenEndDate = "20151110";

    //문자열 구분자 (제어문자 사용)
    private char separator = (char) 0x1E;

    private String currentwiseLogUrl = "";

    private View tab_layout;
    private ImageView mIvTooltipCategory;

    private View mQuickReturnFooterLinearLayout;

    public static boolean isHomeActivityReady;

    private DirectOrderView directOrderLayout;

    @Inject
    protected SearchNavigation searchNavigation;

    //상단 탭메뉴(베스트딜, 이벤트, 의류딜...) 이벤트 로깅 스킵 플래그
    public static boolean preventEventLogging = false;
    //이벤트 로깅 스킵 유효시간 (ms)
    public static int PREVENT_THRESHOLD_TIME = 1500;

    public boolean isMoveSearch = false;

    /**
     * 콜게이트로 부터 발급받은 아이디 추출 "[GSSHOP]"
     */
    @InjectResource(R.string.visual_ars_launcher_id)
    private String launcherId;

    /**
     * senderId
     */
    @InjectResource(R.string.mc_push_sender_id)
    private String senderId;

    /**
     * token
     */
    private String mToken;

    /**
     * 홈 메뉴 로고 ***화려하게*** 표시 해줄 로티 애니메이션
     */
    private LottieAnimationView mAnimationHomeLogo;

    /**
     * 기존 홈 메뉴 로고 (이미지)
     */
    private View mViewHomeLogo;

    /**
     * 로고 애니메이션 반복 횟수
     */
    private static final int LOGO_ANIMATION_REPEAT = 0;
    /**
     * 로고 애니메이션 시작 하기 까지 걸리는 시간 (1.5초)
     */
    private static final int LOGO_ANIMATION_DELAY = 1500;
    /**
     * 전화권한팝업 1회만 노출시키기 위한 플래그
     */
    private static boolean isPermissionRequested = false;

    /**
     * GSFresh refresh 여부를 확인하기 위한 플래그
     */
    private static boolean isGSFreshReload = false;

    /**
     * open_After 에 전달 받은 전호 번호 디폴트는 4545 이지만 전달 받은 전화 번호가 있으면 그걸 사용
     */
    @InjectResource(R.string.txt_ars_number)
    private String arsNumber;

    /**
     * 커넥티드 서비스
     */
    public StaticCsp mCsp = null;

    /**
     * 개인화 버튼 (csp)
     */
    private FrameLayout layoutCsp;
    private ImageButton btnCsp;
    private TextView textview_csp;

    private static HashMap<String, ClsParamsSendByTabID> mMapLseqMachingByTabID = new HashMap<>();     //LSEQ 라는 파라메터가 추가 됨, 가지고 있다가 최초 한번 탭 선택시에 같이 보내줌 (20190625 hklim)
    private static class ClsParamsSendByTabID {
        public String mLseq = null;
        public String mMedia = null;
        public ClsParamsSendByTabID(String lesq, String media) {
            mLseq = lesq;
            mMedia = media;
        }
    }


    /**
     * 최근검색어 기반으로한 연관검색어
     */
    @Inject
    private RecentRecommandAction action;
    private TextView tv_related_text;
    private ArrayList<RecentRecommandInfo> recentRecommandList;
    private View home_top_search;
    private String searchABType = "df"; //df = 기본값

    /**
     * Hyper-Pesonalized Curation 노출여부 플래그
     */
    public static boolean showPsnlCuration;

    /**
     * Hyper-Pesonalized Curation 타입 (호출자: 단품 or 장바구니)
     */
    public enum PsnlCurationType {
        PRD, CART, RTS
    }
    public static PsnlCurationType psnlCurationType;

    /**
     * 매장 처음과 마지막 연결에 필요한 정보 (무한루프 효과)
     */
    private GestureDetector mGestureDetector;
    public static boolean mSwipeToLeft = false;



    /**
     * tensera
     * 2. Add onWindowFocusChanged(boolean hasFocus)
     */
    @Override
    public void onWindowFocusChanged(boolean b) {
        super.onWindowFocusChanged(b);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isHomeActivityReady = false;
        LogUtils.printExeTime("HomeActivity", MainApplication.appStartTime);

        setContentView(R.layout.home_app);

        appbar_header = findViewById(R.id.appbar_header);
        layoutCsp = (FrameLayout) findViewById(R.id.layout_csp);
        btnCsp = (ImageButton) findViewById(R.id.btn_csp);
        textview_csp = (TextView) findViewById(R.id.textview_csp);
        tv_related_text = (TextView)findViewById(R.id.tv_related_text);
        home_top_search = (View)findViewById(R.id.home_top_search);
        mIvTooltipCategory = findViewById(R.id.iv_tooltip_category);
        // TMS 뷰 추가를 위한 설정.
        mViewHomeLogo = findViewById(R.id.button_refresh);
        mAnimationHomeLogo = (LottieAnimationView)findViewById(R.id.animation_home_logo);

        // 1초후 애니메이션 재생
        ThreadUtils.INSTANCE.runInUiThread(() -> setLogoAnimation(), LOGO_ANIMATION_DELAY);

        //이벤트 로깅 스킵
        //앱로딩시 & 하단 홈 탭메뉴를 통해 홈화면 로딩시 베스트딜 이벤트 로깅을 스킵한다.
        preventLoggingForThreshold();

        mContext = this;

        setWebView();
        setIndicator();

        try{
            //커넥티드 서비스 생성 ( 이것보다 화면이 빠르다?? 설마.. )
            boolean cspFlag = PrefRepositoryNamed.getBoolean(MainApplication.getAppContext(), CACHE.CSP_BREAK_FLAG, false);
            if(cspFlag)
            {
                mCsp = StaticCsp.getInstance(this, wiseLog, mCspTextViewHandler, layoutCsp, btnCsp, textview_csp);
                mCsp.setServiceVisible(cspFlag);
            }
        }catch (Exception e)
        {
            //메인인데 죽으면 안되자나 그냥 걸자 최상위로
        }

        mPagerAdapter = new HomePagerAdapter(getSupportFragmentManager(), mContext);
        mPager = (InfiniteViewPager) findViewById(R.id.pager);
        // 양 옆으로 페이지 하나씩 만 미리 로딩
        mPager.setOffscreenPageLimit(1);
        wrappedAdapter = new InfiniteFragmentAdapter(mPagerAdapter);
        mPager.setAdapter(wrappedAdapter);

//        contentDescription 이 탭 선택시 마다 전체 탭 갯수 및 선택 탭 index를 말해주는 문제가 있어 읽어주지 않도록 수정.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            mPager.setAccessibilityDelegate(new View.AccessibilityDelegate() {
                @Override
                public void onPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
                    super.onPopulateAccessibilityEvent(host, event);

                    if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_SELECTED ||
                            event.getEventType() == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
                        event.setContentDescription("");
                    }
                }
            });
        }

        mTabPageIndicator = findViewById(R.id.indicator);

        setIndicatorTab();

        //		trySetupSwipeRefresh();

        //홈액티비티에서 최초 1회만 수행하기 위해서 isHomeCommandExecuted 플래그를 사용한다.
        if (!MainApplication.isHomeCommandExecuted) {
            final List<Class<? extends Command>> commands = new ArrayList<Class<? extends Command>>();

            //보안 취약성 20160617 by 이민수
            //보안 취약성 제거 20170601 배포 얘정 by 이민수 - 서버 부하?
            //commands.add(BuildCheckCommand.class);
            //push register api call
            commands.add(PushRegisterCommand.class);
            commands.add(RemarketingCommand.class);
            commands.add(BadgeCommand.class);

            new Handler().postDelayed(() -> {
                //이벤트 등 딥링크로 접속한 경우 로그인 유지가 안되어 doAutoLogin을 인트로 액티비티로 이동
                //doAutoLogin();
                CommandExecutor commandExecutor = new CommandExecutor((Activity) mContext, commands);
                commandExecutor.execute();

                doBackgroundCommandUiThread();
            }, 1000);

            MainApplication.isHomeCommandExecuted = true;

            //인트로 속도 측정
            try {
                MainApplication mainApp = (MainApplication) getApplicationContext();
                long valueTime = mainApp.getTiming(MainApplication.INTRO);
                //10초가 넘는건 이상한 경우다. !
                if (valueTime > 0 && valueTime < 10000) {
                    setNativePageTiming(valueTime, MainApplication.INTRO);
                }
                //Ln.e( "HomeActivity > valueTime : " + valueTime);
            } catch (Exception e) {
                // 10/19 품질팀 요청
                // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
                // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
                // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
                Ln.e(e);
            }


            /**
             * 페이스북 디퍼드 립링크를 위한 콜백 함수
             * 정상적으로 디퍼드 딥링크가 도착했다면, 아래 콜백에서 1회에 한해서 로직 수행
             * WEB URL 이동 처리 CAN_CAUSE_REDIRECTING 참고
             */
            AppLinkData.fetchDeferredAppLinkData(this, new AppLinkData.CompletionHandler() {
                        @Override
                        public void onDeferredAppLinkDataFetched(AppLinkData appLinkData) {
                            //스키마가 포함되어서 호출됨
                            if (appLinkData != null) {

                                try {
                                    //테스트시 사용했던 URL : 운영에서도 해당 URL 과 같은 형태로 내려올 예정
                                    //String url = "gsshopmobile://home?http://m.gsshop.com/jsp/jseis_withLGeshop.jsp?media=LFM&ecpid=20544693&utm_source=facebookdpa&utm_medium=banner&utm_campaign=retargeting";
                                    String url = appLinkData.getTargetUri().toString();
                                    Ln.d("appLinkData " + url);

                                    //전달된 App link를 gsshopmobile://home?<-- 떼서 Web 주소만 처리한다.
                                    Uri uri = Uri.parse(url);
                                    Intent intent = new Intent();
                                    intent.putExtra(Keys.INTENT.CAN_CAUSE_REDIRECTING, true);
                                    WebUtils.goWeb(mContext, uri.getQuery(), intent);
                                } catch (Exception e) {
                                    Ln.d(e);
                                }

                            } else {
                                Ln.d("appLinkData Null");
                            }
                        }
                    }
            );

        }

        //GTM 클릭이벤트 전달 (위젯에서 검색을 통해 등어온 경우)
        String widgetMenu = getIntent().getStringExtra(Keys.INTENT.WIDGET_TYPE);
        if (GTMEnum.GTM_WIDGET_LABEL.Search.toString().equals(widgetMenu)) {
            String action = String.format("%s_%s", GTMEnum.GTM_ACTION_HEADER,
                    GTMEnum.GTM_ACTION_WIDGET_TAIL);
            GTMAction.sendEvent(this, GTMEnum.GTM_AREA_CATEGORY,
                    action,
                    GTMEnum.GTM_WIDGET_LABEL.Search.getLabel());
        }

        //BaseTabMenuActivity에서 등록한 이벤트버스 해제
        EventBus.getDefault().unregister(this);
        //스티키방식으로 이벤트버스 재등록 (라이브톡 화면노출에 대한 와이즈로그 호출용)
        EventBus.getDefault().registerSticky(this);

        directOrderLayout = (DirectOrderView) findViewById(R.id.direct_order_layout);

        directOrderLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOrderDirectWebView();
            }
        });

        // 권한 요청.
        if (!isPermissionRequested) {
            requestPermission();
            setNotificationChannel();
            isPermissionRequested = true;
        }
        //tts 추후 반영 코드
        //tts = new TextToSpeech(this,this);

        //airbridge
        ABAction.measureHomeView();

        setPromotionInfo();

        // 클릭리스너 등록
        setClickListener();

        //최근검색어 기반으로한 연관검색어 리스트의 0번째를 검색창에 보여준다.
        getRecommandKeyword();
        //돋보기 버튼 클릭시 추천연관검색어 페이지로 이동하도록
        home_top_search.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(DisplayUtils.isValidString(tv_related_text.getText().toString())){
                    searchNavigation.search(HomeActivity.this, tv_related_text.getText().toString(), null,ServerUrls.WEB.SEARCH_MAIN_RECOMMAND,ServerUrls.WEB.SEARCH_ABPARAM + searchABType, GTMEnum.GTM_SEARCH_ACTION.SEARCH_RECOMMAND);
                    EventBus.getDefault().post(new Events.NavigationCloseEvent());
                }
            }
        });

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); //키패드가 밀어올리는 형태
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // 기존 가지고 있는 인텐트와 받아온 인텐트에 차이가 있어 setIntent로 현재 인텐트를 파라메터의 인텐트로 설정.
        setIntent(intent);

        setIndicator();
        mPagerAdapter = new HomePagerAdapter(getSupportFragmentManager(), mContext);
        wrappedAdapter = new InfiniteFragmentAdapter(mPagerAdapter);
        mPager.setAdapter(wrappedAdapter);
        setIndicatorTab();

        //airbridge
        ABAction.measureHomeView();
    }

    /**
     * 최근검색어 기반으로한 연관검색어 리스트의 0번째를 검색창에 보여준다.
     */
    public void getRecommandKeyword(){
        ThreadUtils.INSTANCE.runInBackground(() -> {
            try {
                RecentRecommandList result = action.getRecentRecommandV2(); //API에서 연관검색어 리스트 받아오기
                if (isEmpty(result) || isEmpty(result.list) || isEmpty(result.list.get(0))) {
                    return;
                }
                recentRecommandList = result.list;
                setRecommandKeyword();
            } catch (Exception e) {
                Ln.e(e);
            }
        });
    }

    public void setRecommandKeyword(){
        ThreadUtils.INSTANCE.runInUiThread(() -> {
            try{
                if(DisplayUtils.isValidString(recentRecommandList.get(0).rtq)){
                    tv_related_text.setText(recentRecommandList.get(0).rtq);
                }
            }catch (Exception e){
                Ln.e(e);
            }
        });
    }

    /**
     * 뷰의 클릭리스너를 세팅한다.
     */
    private void setClickListener() {
        findViewById(R.id.button_navi).setOnClickListener((View v) -> {
                    onNavi();
                }
        );
        findViewById(R.id.button_refresh).setOnClickListener((View v) -> {
                    onRefresh();
                }
        );
        findViewById(R.id.button_search).setOnClickListener((View v) -> {
                    onSearch();
                }
        );
        findViewById(R.id.button_cart).setOnClickListener((View v) -> {
                    onCart();
                }
        );
    }

    /**
     * 해더영역을 노출한다.
     */
    public void expandHeader() {
        appbar_header.setExpanded(true, true);
    }

    /**
     * navigation menu
     */
    public void onNavi() {
        //callLeftNavigation();
        //메인 상단 네비 클릭시 와이즈로그 전송 (REST방식호출)
        //setWiseLogHttpClient(ServerUrls.WEB.MAINTOP_NAVI);

        //카테고리 웹으로 전환
        Intent intent = new Intent(ACTION.WEB);
        intent.putExtra(Keys.INTENT.WEB_URL, ServerUrls.WEB.TOP_CATEGORY_TAB);
        intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.CATEGORY);
        startActivity(intent);

    }

    /**
     * refresh button
     */
    public void onRefresh() {
        //캐시 삭제
        clearCacheWithDelay();

        GetHomeGroupListInfo();

        //숏방데이타 초기화
        CategoryDataHolder.putCategoryData(null);

        //GTM 클릭이벤트 전달
        String action = String.format("%s_%s", GTMEnum.GTM_ACTION_HEADER,
                GTMEnum.GTM_ACTION_LOGO_TAIL);
        GTMAction.sendEvent(context, GTMEnum.GTM_AREA_CATEGORY,
                action,
                GTMEnum.GTM_LABEL_CLICK);

        //메인 상단 로고 클릭시 와이즈로그 전송
        setWiseLog(ServerUrls.WEB.MAINTOP_LOGO);
    }

    private void clearCacheWithDelay() {
        ThreadUtils.INSTANCE.runInUiThread(() -> MainApplication.clearCache(), 2000);
    }

    public void onSearch() {
        if (!isMoveSearch) {
            isMoveSearch = true;
            EventBus.getDefault().post(new Events.FlexibleEvent.TvLivePlayEvent(false, -1));

            new Handler().postDelayed(() -> isMoveSearch = false, 300);

            Intent intent = new Intent(ACTION.SEARCH);
            intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.HOME);
            intent.putExtra(Keys.INTENT.KEYWORD_LIST, recentRecommandList);
            startActivityForResult(intent, Keys.REQCODE.HOME_SEARCHING);

            //메인상단 검색 입력박스 클릭시 와이즈로그 전송 (REST방식호출)
            setWiseLogHttpClient(ServerUrls.WEB.MAINTOP_SEARCH);

            //GTM Datahub 이벤트 전달
            DatahubAction.sendDataToDatahub(mContext, DatahubUrls.D_1016, "");

        }

    }

    public void onCart() {

        //음성 테스트 코드
        //tts = new TextToSpeech(this,this);
        //speakOut(0);

        // GS Fresh 매장일 경우에만 장바구니 클릭 시 장바구니 내에 GS Fresh tab 선택된 화면 노출하기 위한 분기.
        if (mCurrentNavigationId.equalsIgnoreCase("481")) {
            WebUtils.goWeb(context, ServerUrls.WEB.FRESH_SMART_CART_TOP);
            setWiseLogHttpClient(ServerUrls.WEB.GS_SUPER_GO_CART);
        } else {
            WebUtils.goWeb(context, ServerUrls.WEB.SMART_CART);
        }
        //GTM 노출이벤트 전달
        String action = String.format("%s_%s", GTMEnum.GTM_ACTION_HEADER,
                GTMEnum.GTM_ACTION_CART_TAIL);
        GTMAction.sendEvent(HomeActivity.this, GTMEnum.GTM_AREA_CATEGORY,
                action,
                ServerUrls.WEB.SMART_CART);
    }

    /**
     * 백그라운드 UI쓰레드에서  커맨드를 수행한다.
     */
    private void doBackgroundCommandUiThread() {
        //deviceCert를 GCM 등록 소요시간을 감안하여 3초 후에 수행
        //GCM 등록이 3초가 더 걸리더라도 deviceCert가 2초간격으로 12회 반복수행하는 동안
        //CustomUAInstanceIDListenerService.onTokenRefresh에서 PMS에 GCM Token을 세팅하기때문에 동작에는 이상 없음
        try {
            new Handler().postDelayed(() -> {
                AbstractTMSService.init(getApplicationContext());

                //IntroActivity에서 "좋아요/싫어요"를 반영하여 수행한 pushSettings이 PMS 서버에 적용이 안됨
                //이에 대한 보완책으로 HomeActivity에서 pushSettings을 다시 수행해줌
                PushSettings push = PushSettings.get();

                new SetConfig(context).request(push.approve, push.approve, (apiResult1, s, s1) -> {
//                    Ln.d("doBackgroundCommandUiThread SetConfig result : " + push.approve + " / " + apiResult1.getMsg());
                });


                //  기존 푸시세팅 위치를 상단 init 에서 deviceCert 후 수행하게끔 수정.

                String token = PrefRepositoryNamed.get(mContext, CACHE.TOKEN, CACHE.TOKEN, String.class);

                //appboy
                if (!"true".equals(mContext.getString(R.string.skip_appboy))) {
                    Appboy.getInstance(getApplicationContext()).registerAppboyPushMessages(token);
                    User user = User.getCachedUser();
                    if (user != null && user.customerNumber != null && user.customerNumber.length() >= 2) {
                        Appboy.getInstance(getApplicationContext()).changeUser(user.customerNumber);
                        Appboy.getInstance(getApplicationContext()).requestImmediateDataFlush();
                    }
                }

                //인트로에서 수행과 중복 방지를 위해 일정시간 후부터 수행하도록 함
                MainApplication.autoLoginTryEnabled = true;

            }, 3000);
        } catch (Exception e) {
            // 10/19 품질팀 요청
            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
            // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
            Ln.e(e);
        }
    }

    private void setNotificationChannel() {
        String ebay = "e111bay111korea";
        String a = "111";
        String b = "1";

        int index = 0;

        char[] temp = ebay.toCharArray();

        for (int i = 0; i < temp.length; i++) {
            if (temp[i] == '1') {

            }
        }


        String strChannelID = MainApplication.getAppContext().getString(R.string.NOTIFICATION_CHANNEL_ID); // 채널 ID (바뀌지 않음)
        // 기존 PMS 채널 기본 0으로 생성하고 있는 구문 있어 해당 채널 삭제( 통합을 위한 )
        PushUtils.deleteNotificationChannel(MainApplication.getAppContext(), "0");
        PushUtils.createNotificationChannel(MainApplication.getAppContext(), // context
                strChannelID /*채널 ID*/, getString(R.string.app_name) /*채널 이름*/,
                "GS SHOP 공통 Push message" /*채널 설명*/,
                NotificationManager.IMPORTANCE_MAX /* 중요도, 높을 경우 폰이 깨어난다. */,
                false /* Badge 노출 여부, PMS 에서도 노출하지 않는다. */);
    }

    /**
     * 상단 인디케이터탭을 설정
     */
    private void setIndicatorTab() {
        tab_layout = findViewById(R.id.layout_tab_menu);

        mTabAdapter = new RecyclerTabAdapter(mPager);
        mTabAdapter.setTextStyle(Typeface.BOLD);
        mTabAdapter.setTextColor(context.getResources().getColor(R.color.tab_indicator_text_selected));

        mTabPageIndicator.setIndicatorHeight(DisplayUtils.convertDpToPx(this, 3));
        mTabPageIndicator.setUpWithAdapter(mTabAdapter);

        if (RecyclerTabLayout.IS_SET_TOUCH_BACK) {
            mTabPageIndicator.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        mTabPageIndicator.setTouchUp();
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        mTabPageIndicator.setTouchDown(motionEvent.getX());
                    }
                    return false;
                }
            });
        }

        final View leftArrow = findViewById(R.id.left_arrow);
        final View rightArrow = findViewById(R.id.right_arrow);

        //본 Detector에서 플리킹 종류(좌 or 우)를 저장 후 onPageSelected에서
        //페이지를 강제 이동하여 무한루프 효과를 준다
        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                mSwipeToLeft = velocityX < 0 ? true : false;
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
        mPager.setOnTouchListener((v, event) -> mGestureDetector.onTouchEvent(event));

        //탭 페이지가 변경될때마다 좌우 쉐도우와 텍스트 색상을 변경한다.
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                int realCount = ((InfiniteFragmentAdapter) mPager.getAdapter()).getRealCount();
                //개인화매장이 존재하는 경우
                if (hasPrivateShop) {
                    //현재 선택된 페이지가 "더보기"(or "접기")인 경우 다음 또는 이전매장으로 강제 이동시킴(무한루프 효과)
                    if (position % realCount == realCount - 1) {
                        new Handler().postDelayed(() -> mPager.setCurrentItem(position + (mSwipeToLeft ? 1 : -1)), 100);
                        return;
                    }
                }

                final int newPosition = position % realCount;
                lastSelectedPosition = newPosition;
                EventBus.getDefault().post(new Events.FlexibleEvent.AdTooltipEvent(null,false));
                // 페이지 변경 시 기존에 동작하고 있는 SwipeRefresh 있는지 확인하여 cancel
                EventBus.getDefault().postSticky(new Events.EventSwipeRefreshStop());
                showMenuLayout();

                // test 용 ( 5분 기다리지 않는다. )
//                InAppMessageManager.getInstance().setCashEventIdDisabled(true);

                setTmsAddView();
                // 탭 페이지 변경될 때마다, (클릭 이던, 슬라이드던 페이지 변경시에 호출)
                new Handler().postDelayed(new Runnable() {
                          @Override
                          public void run() {
                              InAppMessageManager.getInstance().registInAppMessageEvent(AbstractTMSService.EventInAppMessage.EVENT_MAIN_TAB);
                          }
                      }, AbstractTMSService.EventInAppMessage.EVNET_MAIN_TAB_DELAY);

                // test
//                InAppMessage inAppMessage = new InAppMessage();
//                inAppMessage.event_id = "event";
//                inAppMessage.camp_Id = "01";
//                inAppMessage.msgUrl = "https://tms31.gsshop.com/TMS/inapp/2021062900023.html";
//                InAppMessageManager.getInstance().displayInAppMessage(inAppMessage);
//                Ln.d("hklim addOnPageChangeListener InAppMessageManager 2000");

                //sectionList != null 로직 변경 10/06
                if (sectionList != null) {
                    if (newPosition >= sectionList.size()) {
                        return;
                    }

                    // 2020.01.22 yun. GS Fresh 에서만 장바구니 버튼 클릭시 Fresh 장바구니 탭을 보여주기 위해 현재 매장 navi id 를 저장해둠.
                    mCurrentNavigationId = sectionList.get(newPosition).navigationId;
                    //////////////////////////////////////////////////////////////////////////////////////////////
                    if (newPosition < (sectionList.size() / 2)) {
                        leftArrow.setVisibility(View.GONE);
                        rightArrow.setVisibility(View.VISIBLE);
                    } else if (newPosition >= sectionList.size() - 2) {
                        leftArrow.setVisibility(View.VISIBLE);
                        rightArrow.setVisibility(View.GONE);
                    } else {
                        leftArrow.setVisibility(View.VISIBLE);
                        rightArrow.setVisibility(View.VISIBLE);
                    }
                    mPagerAdapter.notifyDataSetChanged();

                    if (newPosition >= 0 && sectionList.size() > newPosition
                            && sectionList.get(newPosition) != null) {

                        //HOME의 서브메뉴 클릭 이벤트 전달 (TV쇼핑, 베스트딜...)
                        String wiseLogUrl = sectionList.get(newPosition).wiseLogUrl;
                        //베스트딜 최초 로딩시에는 규약된 파라미터 추가
                        if (!MainApplication.isBestdealCalled
                                && TODAY_RECOMMEND_SECTION_CODE.equals(sectionList.get(newPosition).sectionCode)) {
                            wiseLogUrl += BESTDEAL_EATRA_PARAM;
                            MainApplication.isBestdealCalled = true;
                        }
                        //이거 메인 스레드 잡어 먹나봄.
                        sendMainAppboyEvent("store_transfer",sectionList.get(newPosition).navigationId);

                        //웹소켓 현재 탭정보 전달과
                        //todo csp
                        try{
                            if(mCsp != null && mCsp.isServiceVisible) {
                                mCsp.setCurrentTabId(sectionList.get(newPosition).navigationId);
                                mCsp.messagePop(sectionList.get(newPosition).navigationId);
                            }
                        }catch(Exception e)
                        {
                            Ln.e(e.getMessage());
                        }

                        // mapper 에서 현재 navigationID 에 해당하는 lseq가 존재할 경우 wiseLogUrl 뒤에 추가 후 해당 lseq값 mapper에서 삭제.
                        ClsParamsSendByTabID clsParamsByTabID = mMapLseqMachingByTabID.get(sectionList.get(newPosition).navigationId);
                        mMapLseqMachingByTabID.remove(sectionList.get(newPosition).navigationId);
                        if(clsParamsByTabID != null) {
                            if (!TextUtils.isEmpty(clsParamsByTabID.mLseq)) {
                                wiseLogUrl += "&" + LSEQ_PARAM_KEY + "=" + clsParamsByTabID.mLseq;
                            }
                            if (!TextUtils.isEmpty(clsParamsByTabID.mMedia)) {
                                wiseLogUrl += "&" + MEDIA_PARAM_KEY + "=" + clsParamsByTabID.mMedia;
                            }
                        }

                        setWiseLog(wiseLogUrl);
                        /*
                            메인 매장에서 단품 이동 후 돌아왔을때 와이즈로그 트래킹 생략한다 onRestart 생략
                        */
                        //AMPAction.sendAmpEvent(AMP_ACTION_VISIT_PREFIX + sectionList.get(newPosition).sectionName);
                        try{
                            JSONObject eventProperties = new JSONObject();
                            eventProperties.put("cateName", "");

                            String sectionLinkParamsArray[] = sectionList.get(newPosition).sectionLinkParams.split("&");
                            if(sectionLinkParamsArray != null){
                                for(int i=0 ; i< sectionLinkParamsArray.length ; i++){
                                    if(sectionLinkParamsArray[i] != null && sectionLinkParamsArray[i].startsWith(AMPEnum.AMP_AB_INFO)){
                                        eventProperties.put(AMPEnum.AMP_AB_INFO, sectionLinkParamsArray[i]);
                                    }
                                }
                            }

                            //탭명변경 AB테스트 진행중이라면 -> 변경된 메인탭명, 서브탭명으로 앱티마이즈명, 앰플리튜드 효율 보냄
                            ApptimizeTabNameExp baseExp = (ApptimizeTabNameExp) ApptimizeExpManager.findExpInstance(ApptimizeExpManager.TABNAME2);
                            if(baseExp!=null){
                                if(sectionList.get(newPosition).navigationId.equals(baseExp.getApptiTarget())){
                                    //앱티마이즈 효율
                                    Apptimize.track(AMPEnum.APPTI_VIEW_TABNAME);

                                    //앰플리튜드 타입 프로퍼티 추가
                                    eventProperties.put(AMPEnum.AMP_AB_INFO, baseExp.getType());
                                }
                            }

                            //탭명변경 AB테스트를 진행했어도 효율은 원본으로 보내야하므로
                            AMPAction.sendAmpEventProperties(AMP_ACTION_VISIT_PREFIX + sectionList.get(newPosition).apptiSectionName,eventProperties);

                        }catch (Exception e){
                            //무시 하도록
                        }

                        //GTM Screen View 로깅
                        GTMAction.openScreen(mContext, sectionList.get(newPosition).sectionName);

                        //GTM Click Event 로깅 (상단 그룹매장의 하위 카테고리 클릭시:Home>이벤트,TV쇼핑,베스트딜...)
                        if (!preventEventLogging) {
                            GTMAction.sendEvent(mContext, GTMEnum.GTM_AREA_CATEGORY,
                                    GTMEnum.GTM_2DEPTH_ACTION,
                                    sectionList.get(newPosition).sectionName);
                        }

                        //GTM Ecommerce에서 사용할 스크린 이름 설정
                        GTMAction.setScreenName(sectionList.get(newPosition).sectionName);

                        //단품딜에서 돌아왔을때 로그를 보내기위해 와이즈로그 주소를 가지고 있는다.
                        currentwiseLogUrl = sectionList.get(newPosition).wiseLogUrl;

                        //매장팝업
                        if (!TextUtils.isEmpty(sectionList.get(newPosition).pmoPopupCheckUrl)
                                && sectionList.get(newPosition).pmoPopupInfo != null
                                && !TextUtils.isEmpty(sectionList.get(newPosition).pmoPopupInfo.linkUrl)
                                && !TextUtils.isEmpty(sectionList.get(newPosition).pmoPopupInfo.imageUrl)
                                && !TextUtils.isEmpty(sectionList.get(newPosition).pmoPopupInfo.dsplSeq)) {
                            new Handler().postDelayed(() -> showPmoPopup(sectionList.get(newPosition).pmoPopupCheckUrl, sectionList.get(newPosition).pmoPopupInfo), 300);
                        }

                        if (ShopInfo.NAVIGATION_GS_SUPER.equals(sectionList.get(newPosition).navigationId)) {
                            CustomToast.setGSSuperIsShowNow(true);

                            if (CustomToast.getGSSuperToast() == null) {
                                CustomToast.makeGSSuperToast(HomeActivity.this, null);
                            }
                            if (CustomToast.getGSSuperToast() != null) {
                                CustomToast.showGSSuperToast(HomeActivity.this, true);
                            }
                            TopSectionList listItem = sectionList.get(newPosition);
                            int currentPosition = getCurrentViewPosition();
                            String linkUrl = listItem.sectionLinkUrl + "?" + listItem.sectionLinkParams;

                        }
                        else {
                            CustomToast.setGSSuperIsShowNow(false);
                            CustomToast.dismissGSToast();
                        }


                        if (ShopInfo.NAVIGATION_SHOPPY_LIVE.equals(sectionList.get(newPosition).navigationId)) {
                            EventBus.getDefault().postSticky(new Events.ShoppingLiveEvent.LivePlayEvent(true));
                        } else {
                            EventBus.getDefault().postSticky(new Events.ShoppingLiveEvent.LivePlayEvent(false));
                        }
                    }

                    //플리킹시 뷰페이저 내용 노출안되는 현상 개선
                    doRequestLayout();

                    //상품이미지 캐시작업수행 이벤트 전달
                    if (useNativeProduct) {
                        new Handler().postDelayed(() -> EventBus.getDefault().post(new Events.ImageCacheStartEvent(mCurrentNavigationId)), 1000);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // to do
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // to do
                EventBus.getDefault().post(new Events.FlexibleEvent.AdTooltipEvent(null,false));
//				mPagerAdapter.getItem(mPager.getCurrentItem()).setUserVisibleHint(mPagerAdapter.getItem(mPager.getCurrentItem()).getUserVisibleHint());
                showMenuLayout();
            }
        });

        InAppMessageManager.getInstance().setInAppMessageListener(new InAppMessageListener() {
            @Override
            public void closeInAppMessagePopupOnScreen(InAppMessage inAppMessage, String s, Bundle bundle, String data) {
                String wiseLog = ServerUrls.WEB.MSEQ_CLICK_INAPP_MESSAGE + "&campid=";

                if (inAppMessage != null) {
                    if (!TextUtils.isEmpty(inAppMessage.camp_Id)) {
                        wiseLog += inAppMessage.camp_Id;
                    }
                }
                setWiseLogHttpClient(wiseLog);

                if (!TextUtils.isEmpty(data)) {
                    String link = data.replace("humuson://close?url=", "");     // url= 다음에 있는 값은 전부 가져온다.
                    link = link.replace("gsshopmobile://home?", "");            // gsshopmobile://home? 뒤에 있는 부분이 실제 URL 이다.

                    goWeb(context, link);
                }
            }
        });

        //앱 실행시 sectionCode가 존재하면 해당하는 섹션으로 이동할 수 있도록 세팅한다.
        setMaintabFromSectionCode(getIntent().getStringExtra(Keys.INTENT.SECTION_CODE));
        //앱 실행시 navigationId가 존재하면 해당하는 섹션으로 이동할 수 있도록 세팅한다.
        //setMaintabFromNavigationId 해당 하는 섹션을 찾을 true
        setMaintabFromNavigationId(getIntent().getStringExtra(Keys.INTENT.NAVIGATION_ID));

        // L sequence 변수, 최초 한번 사용을 위해 가지고 있는다. key는 navigation ID
        mMapLseqMachingByTabID.put(getIntent().getStringExtra(Keys.INTENT.NAVIGATION_ID),
                new ClsParamsSendByTabID(getIntent().getStringExtra(Keys.INTENT.INTENT_LSEQ_PARAM),
                        getIntent().getStringExtra(Keys.INTENT.INTENT_MEDIA_PARAM)));

        //GroupCode가 존재하면 해당 groupcode 탭으로 이동한다.
        setSubTabFromGroupCd(getIntent().getStringExtra(Keys.INTENT.GROUP_CODE_ID));

        mPager.setCurrentItem((mPagerAdapter.getCount()) * 10 + mainTab, false);

        //숨겨지는 상하단 메뉴를 설정한다.
        mQuickReturnFooterLinearLayout = findViewById(R.id.layout_tab_menu);
    }

    /**
     * 뷰페이저 컨텐츠를 그리도록 요청한다.
     */
    private void doRequestLayout() {
        ThreadUtils.INSTANCE.runInUiThread(() -> mPager.requestLayout(), 500);
    }

    /**
     * 매장팝업을 노출한다.
     *
     * @param pmoPopupCheckUrl 팝업 대상여부 확인 API
     * @param pmoPopupInfo 팝업정보
     */
    private void showPmoPopup(String pmoPopupCheckUrl, PmoPopupInfo pmoPopupInfo) {
        String custNo = "";
        User user = User.getCachedUser();
        if (user == null || TextUtils.isEmpty(user.customerNumber)) {
            //로그인 안한 경우 API 호출 안함
            return;
        } else {
            //고객번호 추가
            custNo =  user.customerNumber;
        }

        //다시안보기 버튼 클릭여부 확인
        if (!PmoNoShowForeverPrefInfo.isHide(pmoPopupInfo.dsplSeq)) {
            //닫기 버튼 클릭여부 확인
            if (PmoNoShowTodayPrefInfo.isHide(pmoPopupInfo.dsplSeq)) {
                return;
            }
        } else {
            return;
        }

        //프로모션팝업 대상여부 조회 API 호출
        new BaseAsyncController<PmoPopupCheckResult>(context) {

            private String url;
            private String custNo;
            private PmoPopupInfo pmoPopupInfo;

            @Inject
            private RestClient restClient;

            @Override
            protected void onPrepare(Object... params) throws Exception {
                this.url = (String) params[0];
                this.custNo = (String) params[1];
                this.pmoPopupInfo = (PmoPopupInfo) params[2];
            }

            @Override
            protected PmoPopupCheckResult process() throws Exception {
                //WebView 쿠키를 RestClient에 복사
                CookieUtils.syncWebViewCookiesToRestClient(context, restClient);
                return restClient.postForObject(new URI(url),
                        getPmoPopupHttpEntity(pmoPopupInfo.dsplSeq, custNo), PmoPopupCheckResult.class);
            }

            @Override
            protected void onSuccess(PmoPopupCheckResult result) throws Exception {
                super.onSuccess(result);
                if (result != null && "true".equalsIgnoreCase(result.data)) {
                    Intent i = new Intent(context, PmoPopupuActivity.class);
                    i.putExtra("pmoPopupInfo", pmoPopupInfo);
                    startActivity(i);
                }
            }

            @Override
            protected void onError(Throwable e) {
                //매장팝업은 에러발생시 무시
            }
        }.execute(pmoPopupCheckUrl, custNo, pmoPopupInfo);
    }

    /**
     * 프로모션팝업 대상여부 조회 API 호출을 위한 HttpEntity를 구한다.
     *
     * @param dsplSeq 프로모션번호
     * @param custNo 고객번호
     * @return HttpEntity
     */
    private HttpEntity<MultiValueMap> getPmoPopupHttpEntity(String dsplSeq, String custNo) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Connection", "Close");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<String, Object>();
        formData.add("dsplSeq", dsplSeq);
        formData.add("custNo", custNo);
        return new HttpEntity<MultiValueMap>(formData, headers);
    }

    /**
     * 상단과 하단의 메뉴를 표시한다.
     */
    private void showMenuLayout() {
        ViewHelper.setTranslationY(tab_layout, 0);
    }

    /**
     * @return the mContext
     */
    public static Context getmContext() {
        return mContext;
    }

    /**
     * navigationId에 해당하는 섹션으로 이동한다.
     *
     * @param navigationId API에서 전달받은 섹션별 고유 아이디
     */
    public void moveMaintabFromNavigationId(String navigationId) {
        //앱 실행시 navigationId가 존재하면 해당하는 섹션으로 이동할 수 있도록 세팅한다.
        if (setMaintabFromNavigationId(navigationId) == true) {
            //뷰페이저내에 탭이동 찾은 탭이 있으면 이동 하겠음
            changeViewpagerPosition();
        } else {
            if (isTabMenuExpanded) {
                return;
            }
            //navigationId가 개인화매장에 존재하면 펼쳐서 탭이동
            if (hasPrivateTab(navigationId)) {
                //탭메뉴 펼치고
                EventBus.getDefault().post(new Events.MainTabExpandEvent(true));
                //탭메뉴 이동
                new Handler().postDelayed(() -> {
                    moveMaintabFromNavigationId(navigationId);
                }, 500);
            }
        }
    }

    public void setSubTabFromGroupCd(String groupCd) {
        if (TextUtils.isEmpty(groupCd)) {
            return;
        }

        // pager request에 500딜레이가 걸려있음. 이에 해당 이벤트는 갱신 시점 고려하여 1200딜레이.
        new Handler().postDelayed(() -> EventBus.getDefault().post(new Events.FlexibleEvent.MoveSubGroupCdEvent(groupCd)), 1200);

    }

    /**
     * navigationId 이동시
     * <p>
     * homeActivity에만 유효
     *
     * @param mseq API에서 전달받은 섹션별 고유 아이디
     */
    public void moveMaintabFromNavigationIdMseq(String mseq) {
        //와이즈로그 누락현상 대응 (2017.07.12)
        //동일한 웹뷰로 짧은 시간내 2회 이상 호출시 마지막 호출만 유효함
        //따라서 탭메뉴 슬라이딩시 호출하는 와이즈로그 웹뷰와 구분하기 위해 여기서는 다른웹뷰 사용
        setWiseLogHttpClient(ServerUrls.WEB.WISE_CLICK_URL + "?mseq=" + mseq);
    }

    /**
     *
     * AB테스트 시에 apptiInfo를 전달하기 위해 생성
     * @param mseq API에서 전달받은 섹션별 고유 아이디
     */
    public void moveMaintabFromNavigationIdMseq_apptiInfo(String mseq, String apptiInfo) {
        //와이즈로그 누락현상 대응 (2017.07.12)
        //동일한 웹뷰로 짧은 시간내 2회 이상 호출시 마지막 호출만 유효함
        //따라서 탭메뉴 슬라이딩시 호출하는 와이즈로그 웹뷰와 구분하기 위해 여기서는 다른웹뷰 사용
        setWiseLogHttpClient(ServerUrls.WEB.WISE_CLICK_URL + "?mseq=" + mseq + "&" + "?apptiInfo=" + apptiInfo);
    }


    /**
     * navigationId에 해당하는 섹션으로 이동하도록 설정한다.
     * 푸시발송을 통해 스키마로 앱구동시, 또는 웹에서 호출시 사용된다.
     *
     * @param navigationId API에서 전달받은 섹션별 고유 아이디
     * @return 같은 섹션 찾음 유무
     */
    private boolean setMaintabFromNavigationId(String navigationId) {
        if (DisplayUtils.isValidString(navigationId) && isNotEmpty(CONTENT)) {
            for (int i = 0; i < CONTENT.size(); i++) {
                String[] contents = CONTENT.get(i).split(Character.toString(separator));
                if (contents.length >= 4 && navigationId.equals(contents[3])) {
                    mainTab = i;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * navigationId에 해당하는 개인화매장이 존재하는지 확인한다.
     *
     * @param navigationId API에서 전달받은 섹션별 고유 아이디
     * @return 존재하면 true 리턴
     */
    private boolean hasPrivateTab(String navigationId) {
        if (DisplayUtils.isValidString(navigationId) && isNotEmpty(CONTENT_PRV)) {
            for (int i = 0; i < CONTENT_PRV.size(); i++) {
                String[] contents = CONTENT_PRV.get(i).split(Character.toString(separator));
                if (contents.length >= 4 && navigationId.equals(contents[3])) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * sectionCode에 해당하는 섹션으로 이동한다.
     *
     * @param sectionCode API에서 전달받은 섹션별 고유 아이디
     */
    public void moveMaintabFromSectionCode(String sectionCode) {
        //앱 실행시 sectionCode가 존재하면 해당하는 섹션으로 이동할 수 있도록 세팅한다.
        setMaintabFromSectionCode(sectionCode);
        //뷰페이저내에 탭이동
        changeViewpagerPosition();
    }

    /**
     * 섹션코드에 해당하는 섹션으로 이동하도록 설정한다.
     * 푸시발송을 통해 스키마로 앱구동시, 또는 웹에서 호출시 사용된다.
     *
     * @param sectionCode API에서 전달받은 섹션별 고유 아이디 (sectionCode 필드)
     */
    private void setMaintabFromSectionCode(String sectionCode) {
        if (DisplayUtils.isValidString(sectionCode) && isNotEmpty(CONTENT)) {
            for (int i = 0; i < CONTENT.size(); i++) {
                String[] contents = CONTENT.get(i).split(Character.toString(separator));
                if (contents.length >= 5 && sectionCode.equals(contents[4])) {
                    mainTab = i;
                    break;
                }
            }
        }
    }

    /**
     * 뷰페이저의 포지션을 변경한다.
     */
    private void changeViewpagerPosition() {
        int realCount = ((InfiniteFragmentAdapter) mPager.getAdapter()).getRealCount();
        int currentItem = mPager.getCurrentItem();

        int index = currentItem / realCount;
        int movePosition = index * realCount + mainTab;

        if (currentItem > movePosition) {
            mPager.setCurrentItem(movePosition + realCount);
        } else {
            mPager.setCurrentItem(movePosition);
        }
        // TMS 네비게이션 ID 에 의해 이동시에 TMS 인앱 메세지 이벤트 호출
//        InAppMessageManager.getInstance().registInAppMessageEvent(AbstractTMSService.EventInAppMessage.EVENT_MAIN_TAB);

        new Handler().postDelayed(() -> mPager.requestLayout(), 500);
    }

    @Inject
    private PromotionAction promotionAction;

    public void setPromotionInfo() {
        ThreadUtils.INSTANCE.runInBackground(() -> {
            //promotionInfo = PromotionInfo.getCachedPromotionInfo();
            promotionAction.getPromotionInfo();

            promotionInfo = PrefRepositoryNamed.get(context, CACHE.PROMOTION_INFO,
                    CACHE.PROMOTION_INFO, PromotionInfo.class);

            if (promotionInfo == null) {
                return;
            }

            //프로모션 이미지 주소가 없는 경우 팝업 띄우지 않음
            if ("Y".equals(promotionInfo.result)
                    && !TextUtils.isEmpty(promotionInfo.imgurl)) {

                //프로모션
                boolean bHidePromotion = PrefRepositoryNamed.getBoolean(MainApplication.getAppContext(), CACHE.PROMOTION, false);
                String day = PrefRepositoryNamed.get(MainApplication.getAppContext(), CACHE.PROMOTION_DAY, String.class);
                if (day != null) {
                    //하루가 지나지 않은 경우는 팝업을 노출하지 않음
                    if (DateUtils.getDifferenceDays(day, DateUtils.getToday("yyyyMMdd")) < 1) {
                        bHidePromotion = true;
                    }
                }

                //푸시동의
                boolean bHidePush = PrefRepositoryNamed.getBoolean(MainApplication.getAppContext(), CACHE.PUSH_APPROVE, false);
                String pDay = PrefRepositoryNamed.get(MainApplication.getAppContext(), CACHE.PUSH_APPROVE_DAY, String.class);
                if (pDay != null) {
                    //하루가 지나지 않은 경우는 팝업을 노출하지 않음
                    if (DateUtils.getDifferenceDays(pDay, DateUtils.getToday("yyyyMMdd")) < 7) {
                        bHidePush = true;
                    }
                }
                PushSettings model = PushSettings.get();
                if (model != null) {
                    //이미 푸시동의한 사용자에게는 노출 안함
                    if (model.approve) {
                        bHidePush = true;
                    }
                }

                if (PROMOTION_SMALL_TYPE.equalsIgnoreCase(promotionInfo.bannertype)) {
                    if (!bHidePromotion) {
                        Intent i = new Intent(context, PromotionSmallActivity.class);
                        i.putExtra("promotionInfo", promotionInfo);
                        startActivity(i);
                    }
                } else if (PROMOTION_MEDIUM_TYPE.equalsIgnoreCase(promotionInfo.bannertype)) {
                    if (!bHidePromotion) {
                        Intent i = new Intent(context, PromotionMediumActivity.class);
                        i.putExtra("promotionInfo", promotionInfo);
                        startActivity(i);
                    }
                } else if (PROMOTION_LARGE_TYPE.equalsIgnoreCase(promotionInfo.bannertype)) {
                    //BIC 타입은 기능적용 안하기로 함 (2014.08.25)
            /*if (!bHidePromotion) {
                Intent i = new Intent(this, PromotionLargeActivity.class);
                i.putExtra("promotionInfo", promotionInfo);
                startActivity(i);
            }*/
                } else if (PUSH_APPROVE_TYPE.equalsIgnoreCase(promotionInfo.bannertype)) {
                    //푸시유도팝업
                    if (!bHidePush) {
                        Intent i = new Intent(context, PushApproveActivity.class);
                        i.putExtra("promotionInfo", promotionInfo);
                        startActivity(i);
                    }
                }
            }
        });
    }

    /**
     * 프로모션 팝업 링크타입에 따라 해당 액션을 수행한다.
     *
     * @param linkType L:webview E:external browser S:schema
     */
    public void promotionLinkAction(String linkType) {
        if ("L".equalsIgnoreCase(linkType)) {
            goLinkWeb();
        } else if ("E".equalsIgnoreCase(linkType)) {
            goExternalLinkWeb();
        } else if ("S".equalsIgnoreCase(linkType)) {
            goSchemaLinkWeb();
        }
    }

    // Promotion Link
    private void goLinkWeb() {
        WebUtils.goWeb(this, promotionInfo.link, getIntent());
    }

    // Promotion External Link
    private void goExternalLinkWeb() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.parse(promotionInfo.link);
            intent.setData(uri);
            startActivity(intent);
        } catch (Exception e) {
            // 10/19 품질팀 요청
            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
            // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
            Ln.e(e);
        }
    }

    // Promotion Schema Link
    private void goSchemaLinkWeb() {
        Intent intent = new Intent(ACTION.WEB);
        intent.putExtra(Keys.INTENT.WEB_URL, promotionInfo.link);
        context.startActivity(intent);
    }


    private ArrayList<TopSectionList> getSelectedTopSectionList() {
        if (groupSectionArrayList != null && !groupSectionArrayList.isEmpty()) {
            return groupSectionArrayList.get(0).sectionList;
        } else {
            return null;
        }
    }

    /**
     * 방송알림 등록버튼 클릭 콜백
     */
    @Override
    public void onRegister(String caller, String prdId, String prdName, String period, String times) {
        EventBus.getDefault().post(new Events.AlarmRegistEvent(caller, prdId, prdName, period, times));
    }

    public void setWebView() {
        webControl = new WebViewControlInherited.Builder(this).target((WebView) findViewById(R.id.mainWebView))
                .with(new MainWebViewClient(this)).build();
        //.with(new MainWebViewChromeClient(this)).build();
    }

    /**
     * 앱보이 각 메인 매장 이동 이벤트 트래킹
     * 백그라운로 동작 하도록 어노테이션 적용
     * @param eventName
     * @param naviID
     */
    public void sendMainAppboyEvent (String eventName,String naviID)
    {
        ThreadUtils.INSTANCE.runInBackground(() -> {
            String PRO_APPBOY_NAVI = "navigationId";
            try {
                if(TextUtils.isEmpty(eventName))
                {
                    return;
                }
                AppboyProperties eventProperties = new AppboyProperties();
                eventProperties.addProperty(PRO_APPBOY_NAVI,naviID);
                Appboy.getInstance(mContext).logCustomEvent(eventName,eventProperties);
            }catch (Exception e)
            {
            }
        });
    }

    /**handleMessage
     * gsshop.mobile.v2.home.shop.bestshop
     * gsshop.mobile.v2.home.shop.flexible
     * gsshop.mobile.v2.home.shop.nalbang 안의 Fragment 및 ViewHoler 에서
     * ex)((HomeActivity) context).setWiseLog(categories.get(tab).wiseLog) 호출한다.
     *
     * 구 로직이므로 사용시 유의
     * 2017 5월 20일 이후( left네비게이션 효율코드 포함 )
     * AbstractBaseActivity -> setWiseLogHttpClient 사용
     *
     * @param clickUrl
     */
    public void setWiseLog(String clickUrl) {
        try {
            if (!TextUtils.isEmpty(clickUrl)) {
                webControl.clearCache();
                webControl.loadUrl(clickUrl, MainApplication.customHeaders);
                //Ln.e("clickUrl(Webview) : " + clickUrl);
            }
        } catch (Exception e) {
            // 10/19 품질팀 요청
            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
            // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
            Ln.e(e);
        }
    }

    /**
     * 탭메뉴를 더보기 상태로 변경한다.
     */
    private void expandTabMenu() {
        //탭메뉴 초기화 후 고정매장 추가
        CONTENT.clear();
        CONTENT.addAll(CONTENT_PUB);
        if (hasPrivateShop) {
            //개인화매장이 존재하는 경우 개인화매장 추가 후 마지막에 "접기" 추가
            CONTENT.addAll(CONTENT_PRV);
            CONTENT.add(getString(R.string.more_close) + separator + "" + separator + "");
        }
    }

    /**
     * 탭메뉴를 접기 상태로 변경한다.
     * #참고
     * -접기메뉴 바로전 아이템이 열린상태에서 접기를 클릭하는 경우 ViewPager.java 내 코드 중
     * -setCurrentItemInternal을 호출하여 페이지를 이동시키는 현상이 있음
     * -접기 클릭시 홈으로 이동이키면 위 현상은 이슈가 되지 않음
     *
     * @param fromEvent 이벤트에서 호출된 경우와 홈메뉴로 호출된 경우를 구분하기 위한 플래그
     *                  이벤트에서 호출된 경우란 사용자가 더보기/접기를 클릭한 경우
     */
    private void collapseTabMenu(boolean fromEvent) {
        //탭메뉴 초기화 후 고정매장 추가
        CONTENT.clear();
        CONTENT.addAll(CONTENT_PUB);
        if (hasPrivateShop) {
            //개인화 매장이 존재하는 경우 마지막에 "더보기" 추가
            CONTENT.add(getString(R.string.common_more) + separator + "" + separator + "");
        }

        if (fromEvent) {
            if (lastSelectedPosition > CONTENT_PUB.size() - 1) {
                //개인화매장이 노출된 상태에서 접기하는 경우 디폴트매장으로 이동
                //사용자가 이동한 블럭만큼 이동 (행현상 방지 위해)
                int tabIdx = defaultTab + mTabAdapter.getItemCount() * (mPager.getCurrentItem() / mTabAdapter.getItemCount() + 1);
                mPager.setCurrentItem(tabIdx, false);
            } else {
                //고정매장이 노출된 상태에서 접기하는 경우 고정매장 유지
                //case1.고정매장 중앙정렬
                //mTabPageIndicator.scrollToTab(finalPrevCurrentItem);
                //case2.스크롤을 마지막으로 이동 (더보기 메뉴 노출시킴)
                mTabPageIndicator.scrollToPosition(mTabPageIndicator.getAdapter().getItemCount() - 1);
                //아래 이슈 개선
                //https://jira.gsenext.com/browse/SQA-2218
                new Handler().postDelayed(() -> {
                    int tabIdx = lastSelectedPosition + mTabAdapter.getItemCount() * (mPager.getCurrentItem() / mTabAdapter.getItemCount() + 1);
                    mPager.setCurrentItem(tabIdx, false);
                }, 500);
            }
        }
    }

    /**
     * 섹션코드에 해당하는 메인탭으로 이동한다.
     *
     * @param event Events.MainTabEvent
     */
    public final void onEventMainThread(Events.MainTabEvent event) {
        moveMaintabFromSectionCode(event.sectionCode);
    }

    /**
     * 메인탭을 더보기/접기 한다.
     *
     * @param event Events.MainTabExpandEvent
     */
    public final void onEventMainThread(Events.MainTabExpandEvent event) {
        isTabMenuExpanded = event.expand;

        if (event.expand) {
            expandTabMenu();
            //아래 이슈 개선
            //https://jira.gsenext.com/browse/SQA-2218
            new Handler().postDelayed(() -> {
                int tabIdx = lastSelectedPosition + mTabAdapter.getItemCount() * (mPager.getCurrentItem() / mTabAdapter.getItemCount() + 1);
                mPager.setCurrentItem(tabIdx, false);
            }, 500);
        } else {
            collapseTabMenu(true);
        }
        mPagerAdapter.notifyDataSetChanged();
        mTabAdapter.notifyDataSetChanged();
    }

    /**
     * 장바구니 숫자를 갱신한다.
     * (장바구니 숫자 세팅이 resume 함수에서 수행되기 때문에, 로그인 완료시점이 resume 호출보다 늦은 장바구니 숫자가 표시안됨.
     * 이에 대한 보완책으로 로그인 완료시 장바구니 숫자를 세팅하는 로직 추가)
     *
     * @param event event
     */
    public final void onEventMainThread(Events.LoggedInEvent event) {
        setBasketcnt();
        doBackgroundCommandUiThread();
    }

    /**
     * 와이즈로그 더미페이지 호출
     *
     * @param event WiseLogEvent
     */
    public void onEvent(Events.WiseLogEvent event) {
        Events.WiseLogEvent stickyEvent = (Events.WiseLogEvent) EventBus.getDefault().getStickyEvent(Events.WiseLogEvent.class);
        if (stickyEvent != null) {
            EventBus.getDefault().removeStickyEvent(stickyEvent);
        }
        setWiseLog(event.url);
    }

    /**
     * 자동로그인을 재시도한다.
     *
     * @param event event
     */
    public void onEvent(Events.AutoLogInRetryEvent event) {
        Ln.i("### onEvent > AutoLogInRetryEvent");
        autoLoginState = LunaUtils.AutoLoginState.RE_TRYING;
        doAutoLoginWhenNetworkActivated(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webControl != null) {
            webControl.resumeWebView();
        }
        if (sectionList != null && mPager != null) {
            if (mPager.getCurrentItem() >= 0 && sectionList.size() > mPager.getCurrentItem()
                    && sectionList.get(mPager.getCurrentItem()) != null) {
                //GTM Ecommerce에서 사용할 스크린 이름 설정
                GTMAction.setScreenName(sectionList.get(mPager.getCurrentItem()).sectionName);
            }
        }

//		new GetPromotionSearchController(this).execute();
        isHomeActivityReady = true;
        //HomeActivity 나타났지만, 홈이 아닌 상태면 탭을 변경시킨다.
        if (getIntent() != null) {
            //내가 탭 으로 이동되었으면 구지 할필요가 없는데 내가 탭이동되었다는 값을 유지 할까??
            //지금 선택된게 홈이 맞냐???
            if (!isSelectedTabFocus(TabMenu.HOME)) {
                setTabAdjustment(TabMenu.HOME);
            }
        }

        // 20200806 1차 햄버거 메뉴 호출 제거 작업 2차때에 완전 삭제
//        updateNavigationCntents();

        //todo csp 컨트롤
        //웹소켓 컨트롤
        //웹뷰의 mWebControl.getWebView().onPause(); 보다 늦게 호출 되면 안되네
        new Handler().postDelayed(() -> {
            //끊어져 있을때만 동작한다..

            if(mCsp != null && mCsp.isServiceVisible)
            {
                //리줌시 큐에 현재탭을 넣어 둔다.
                mCsp.messagePush(mCsp.getCurrentTabId());

                if(!mCsp.isSocketConnect())
                {
                    mCsp.initSocket();
                }
            }
        }, 800);


        //현재보이는 화면이 홈이고
        //Hyper-Pesonalized Curation 노출조건에 해당되고
        if(ShopInfo.NAVIGATION_HOME.equals(mCurrentNavigationId) && showPsnlCuration && "Y".equals(getHomeGroupInfo().appUseUrl.dtReqAcceptYn)){
            //한번 보이고나서는 false로 세팅
            showPsnlCuration = false;

            //백키로 돌아온 경우 : isUpdatedEnded = true -> 지연없음
            //홈버튼으로 돌아온 경우 : isUpdatedEnded = false -> 2초지연
            if(MainApplication.calledFromBackKey){
                //2번쨰 파라미터 true : API호출후, 데이터를 캐쉬하고 화면은 그리지 않는다.
                //2번쨰 파라미터 false : API호출후, 데이터를 캐쉬하지 않고, 화면은 그리겠다.
                EventBus.getDefault().post(new Events.psnlCurationEvent(psnlCurationType,false));
            }else{
                MainApplication.calledFromBackKey = true;
                //2번쨰 파라미터 true : API호출후, 데이터를 캐쉬하고 화면은 그리지 않는다.
                //2번쨰 파라미터 false : API호출후, 데이터를 캐쉬하지 않고, 화면은 그리겠다.
                new Handler().postDelayed(() -> EventBus.getDefault().post(new Events.psnlCurationEvent(psnlCurationType,false)), 2000);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (webControl != null) {
            webControl.pauseWebView();
        }

        //todo csp 컨트롤
        if(mCsp != null) {
            mCsp.globalSocketOff();
        }
        //핸들러 param 을  처리하자 계속 5초 되도록
        mCspTextViewHandler.removeMessages(StaticCsp.CLOSEALL_MESSAGE);
        mCspTextViewHandler.sendEmptyMessageDelayed(StaticCsp.CLOSEALL_MESSAGE,0);

        isGSFreshReload = true;
    }

    /**
     * 메인/그룹매장 -{@literal >} 단품/딜 -{@literal >} back 키 또는 왼쪽 상단의 "뒤로가기" 선택시
     * wise log를 출력하기 위해 처리
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        // 메인 매장에서 단품 이동 후 돌아왔을때 와이즈로그 트래킹
        if (currentwiseLogUrl != null && !"".equals(currentwiseLogUrl)) {
            setWiseLog(currentwiseLogUrl);
        }

        showMenuLayout();

    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (webControl != null) {
            webControl.destroy();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            if (mPager != null)
                mPager.removeAllViews();

        } catch (Exception e) {
            // 10/19 품질팀 요청
            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
            // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
            Ln.e(e);
        }

        //소켓 컨트롤
        //todo csp 컨트롤
        if(mCsp != null) {
            mCsp.globalSocketOff();
        }

        super.onDestroy();
    }

    private void setIndicator() {
        if (isEmpty(getHomeGroupInfo())) {
            return;
        }

        groupSectionArrayList = getHomeGroupInfo().groupSectionList;
        if (isNotEmpty(groupSectionArrayList)) {
            MainApplication.isAppView = true;
            mainSection = groupSectionArrayList.get(0);
            sectionList = mainSection.sectionList;
            //10/19 품질팀 요청
            if (sectionList != null && !sectionList.isEmpty()) {
                int pubLoopCnt = 0;
                CONTENT = new ArrayList<>();
                CONTENT_PUB = new ArrayList<>();
                CONTENT_PRV = new ArrayList<>();
                for (int i = 0; i < sectionList.size(); i++) {
                    try {
                        //원본유지
                        sectionList.get(i).apptiSectionName = sectionList.get(i).sectionName;

                        //navigationId 유효성 체크
                        if (sectionList.get(i).navigationId != null && !"0".equals(sectionList.get(i).navigationId.trim())) {
                            //해당 클래스 유효성 체크
                            ApptimizeTabNameExp baseExp = (ApptimizeTabNameExp) ApptimizeExpManager.findExpInstance(ApptimizeExpManager.TABNAME2);
                            if(baseExp != null){
                                if(ApptimizeExpManager.TYPE_O.equals(baseExp.getType())){
                                    //디폴트
                                }else{
                                    if(baseExp.getApptiTarget() != null && !TextUtils.isEmpty(baseExp.getApptiTarget())
                                            && baseExp.getApptiTarget().equals(sectionList.get(i).navigationId)){ //탭명변경 AB테스트 - 이름바꿀 탭의 네비번호 비교
                                        //메인탭명 변경
                                        if(baseExp.getApptiMainTabName() != null && !TextUtils.isEmpty(baseExp.getApptiMainTabName()) && !"null".equals(baseExp.getApptiMainTabName())){
                                            sectionList.get(i).sectionName = baseExp.getApptiMainTabName() ;
                                        }

                                        //서브탭명 변경
                                        if(baseExp.getApptiSubTabName() != null && !TextUtils.isEmpty(baseExp.getApptiSubTabName()) && !"null".equals(baseExp.getApptiSubTabName())){
                                            sectionList.get(i).sectionTopName = baseExp.getApptiSubTabName() ;
                                        }
                                    }
                                }
                            }
                        }

                    }catch (Exception e){
                        //catch에는 뭘 적으면 좋을까/
                    }

                    if (TextUtils.isEmpty(sectionList.get(i).sectionName)) {
                        sectionList.get(i).sectionName = " ";
                    }

                    String sectionInfo = sectionList.get(i).sectionName + separator + sectionList.get(i).sectionTopName
                            + separator + sectionList.get(i).sectionNewImgUrl + separator + sectionList.get(i).navigationId + separator + sectionList.get(i).sectionCode;
                    if (sectionList.get(i).isPublicSection) {
                        //고정매장 저장
                        CONTENT_PUB.add(sectionInfo);
                        if (mainSection.defaultNavigationId.equals(sectionList.get(i).navigationId)) {
                            defaultTab = pubLoopCnt;
                            mainTab = defaultTab;
                        }
                        pubLoopCnt++;
                    } else {
                        //개인화매장 저장
                        CONTENT_PRV.add(sectionInfo);
                    }
                }

                hasPrivateShop = CONTENT_PRV.size() > 0;

                if (isMainLoaded) {
                    //상단, 하단 홈 클릭시 탭메뉴 더보기/접기 이전상태 유지
                    if (isTabMenuExpanded) {
                        expandTabMenu();
                    } else {
                        collapseTabMenu(false);
                    }
                } else {
                    //메인 최초 진입시
                    CONTENT = (ArrayList<String>) CONTENT_PUB.clone();
                    if (hasPrivateShop) {
                        //개인화매장이 존재하는 경우만 "더보기" 추가
                        CONTENT.add(getString(R.string.common_more) + separator + "" + separator + "");
                    }
                    setPrivateLanding(sectionList);
                }

                isMainLoaded = true;
            }
        }
    }

    /**
     * 앱 구동 후 최초 랜딩 시 homeNavigationId 매장으로 이동 (개인화 매장)
     * homeNavigationId 값과 일치하는 매장이 없는 경우, defaultNavigationId 매장으로 이동
     *
     * @param sectionList ArrayList<TopSectionList>
     */
    private void setPrivateLanding(ArrayList<TopSectionList> sectionList) {
        if (isEmpty(mainSection.homeNavigationId)) {
            return;
        }
        for (int i = 0; i < sectionList.size(); i++) {
            if (mainSection.homeNavigationId.equals(sectionList.get(i).navigationId)) {
                mainTab = i;
            }
        }
    }

    /**
     * HomePagerAdapter
     */
    public class HomePagerAdapter extends FragmentStatePagerAdapter {

        /**
         * HomePagerAdapter 생성자
         *
         * @param fm  android.support.v4.app.FragmentManager
         * @param ctx Context
         */
        public HomePagerAdapter(FragmentManager fm, Context ctx) {
            super(fm);
        }

        @Override
        public int getCount() {
            if (isEmpty(CONTENT)) {
                return 0;
            }
            return CONTENT.size();
        }

        /**
         * HomePagerAdapter getItems
         *
         * @param position position
         * @return getItem
         */
        @Override
        public MainShopFragment getItem(int position) {
            int newPosition = position % wrappedAdapter.getRealCount();

            //펼친상태에서 마지막 매장 클릭시 로딩되는 우측 "접기"매장은 빈화면으로 처리
            if (newPosition >= getSelectedTopSectionList().size()) {
                return BestShopFragment.newInstance(-1);
            }

            MainShopFragment f;
            //오늘오픈
            if (getSelectedTopSectionList().get(newPosition).viewType.equals(ShopInfo.TYPE_TODAY_OPEN)) {
                f = BestShopFragment.newInstance(newPosition);
            } else if (getSelectedTopSectionList().get(newPosition).viewType.equals(ShopInfo.TYPE_TV_SHOPPING)) {
                f = TvShopFragment.newInstance(newPosition);
            } else if (getSelectedTopSectionList().get(newPosition).viewType.equals(ShopInfo.TYPE_EVENT)) {
                // 이벤트)
                f = EventShopFragment.newInstance(newPosition);
            } else if (getSelectedTopSectionList().get(newPosition).viewType.equals(ShopInfo.TYPE_BESTDEAL)) {
                // 베스트 딜.
                f = BestDealShopFragment.newInstance(newPosition);
            } else if (getSelectedTopSectionList().get(newPosition).viewType.equals(ShopInfo.TYPE_VOD)) {
                // 베스트 딜.
                f = VodShopFragment.newInstance(newPosition);
            } else if (getSelectedTopSectionList().get(newPosition).viewType.equals(ShopInfo.TYPE_DEPARTMENT)) {
                // 5.5 로 바뀌면서 오늘오픈 매장이 Department DFCLIST -> FXCLIST 로 변경됨. DepartmentStoreFragment 사용 부분이 사라짐.
                // 굳이 BestShopFragment로 변경하지 않아도 됨에 따라 원복
                f = DepartmentStoreFragment.newInstance(newPosition);
            } else if (getSelectedTopSectionList().get(newPosition).viewType.equals(ShopInfo.TYPE_NALBANG) || getSelectedTopSectionList().get(newPosition).viewType.equals(ShopInfo.TYPE_SHORTBANG)) {
                //날방
                f = NalbangShopFragment.newInstance(newPosition);
            } else if (getSelectedTopSectionList().get(newPosition).viewType.equals(ShopInfo.TYPE_TV_SCHEDULE)) {
                if(ApptimizeExpManager.findExpNameType(ApptimizeExpManager.SCHEDULE, ApptimizeExpManager.TYPE_A, ApptimizeExpManager.SCHEDULE_TARGET)){
                    // A타입
                    f = TVScheduleShopFragment_A.newInstance(newPosition);
                }else{
                    // 오리지널
                    f = TVScheduleShopFragment.newInstance(newPosition);
                }
            }
            else if (ShopInfo.TYPE_GS_SUPER.equals(getSelectedTopSectionList().get(newPosition).viewType)) {
                // GS Super
                f = GSSuperFragment.newInstance(newPosition);
            }
            else if (ShopInfo.TYPE_GS_CHOICE.equals(getSelectedTopSectionList().get(newPosition).viewType)) {
                // GS CHOICE
                f = GSChoiceShopFragment.newInstance(newPosition);
            }
            else if (ShopInfo.TYPE_L_EVENT.equals(getSelectedTopSectionList().get(newPosition).viewType)) {
                // 이벤트)
                f = GSChoiceShopFragment.newInstance(newPosition);
            }
            else if (ShopInfo.TYPE_VIP.equals(getSelectedTopSectionList().get(newPosition).viewType)) {
                // VIP Lounge 매장
                f = BestShopFragment.newInstance(newPosition);
            }
            else if (ShopInfo.TYPE_SHOPPING_LIVE.equals(getSelectedTopSectionList().get(newPosition).viewType)) {
                // VIP Lounge 매장
                f = ShoppingLiveShopFragment.newInstance(newPosition);
            }
            else if (ShopInfo.TYPE_WINE.equals(getSelectedTopSectionList().get(position).viewType)) {
                f = WineShopFragment.newInstance(position);
            }
            else if (ShopInfo.TYPE_BEAUTY.equals(getSelectedTopSectionList().get(position).viewType)) {
                f = BeautyShopFragment.newInstance(position);
            }
            else if (ShopInfo.TYPE_TOMORROW_SELECT.equals(getSelectedTopSectionList().get(position).viewType)) {
                f = TodaySelectFragment.newInstance(position);
            }
            else if (ShopInfo.TYPE_GS_EXCLUSIVE.equals(getSelectedTopSectionList().get(position).viewType)) {
                f = ExclusiveFragment.newInstance(newPosition);
            }
            else {
                f = BestShopFragment.newInstance(newPosition);
            }

            return f;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return CONTENT.get(position);
        }

        /**
         * DestroyItem을 Override하여 부모의 destroyItem을 호출 못하도록 막음
         *
         * @param container container
         * @param position  position
         * @param object    object
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // super.destroyItem(container, position, object);
        }

    }

    /**
     * @return getCurrentViewPosition
     */
    @Override
    public int getCurrentViewPosition() {
        return mPager.getCurrentItem() % ((InfiniteFragmentAdapter) mPager.getAdapter()).getRealCount();
    }

    /**
     * @return getFooterLayout
     */
    @Override

    public View getFooterLayout() {
        return mQuickReturnFooterLinearLayout;
    }


    /**
     * 0827 사이렌이 없어지고 그 자리에 CSP레이아웃 탑재
     * 리스트에서는 위아래만 컨트롤하고 나머지는 homeActity에서 관리한다
     * @return
     */
    @Override
    public View getCspLayout() { return layoutCsp; }

    private void setHomeGroupInfo() {
        if (!MainApplication.isGetHomeMenu) {
            AssetManager mgr = getAssets();
            ObjectMapper mapper = new ObjectMapper();
            HomeGroupInfo homeGroupInfo = null;
            BufferedInputStream bin = null;
            InputStream fin = null;
            try {
                fin = mgr.open("navigation.json");
                bin = new BufferedInputStream(fin);
                homeGroupInfo = mapper.readValue(bin, HomeGroupInfo.class);

            } catch (IOException e) {
                // 10/19 품질팀 요청
                // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
                // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
                // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
                Ln.e(e);
            } finally {
                try {

                    if (bin != null) {
                        bin.close();
                    }

                    if (fin != null) {
                        fin.close();
                    }
                } catch (IOException ioe) {
                    Ln.e(ioe);
                }
            }

            PrefRepositoryNamed.save(MainApplication.getAppContext(), CACHE.HOME_GROUP_INFO, homeGroupInfo);
            MainApplication.isGetHomeMenu = true;

            new Handler().postDelayed(() -> {
                //					getNetworkHomeGroupInfo();
            }, StaticCsp.MESSAGE_SHOW_TIME);
        }

    }

    /**
     * logo 애니메이션을 셋 한다. (애니메이션 완료 후 기존 로고 보이도록)
     */
    private void setLogoAnimation() {
        if (mViewHomeLogo == null)
            mViewHomeLogo = findViewById(R.id.button_refresh);

        if (mAnimationHomeLogo == null)
            mAnimationHomeLogo = (LottieAnimationView)findViewById(R.id.animation_home_logo);

        mAnimationHomeLogo.setRepeatCount(LOGO_ANIMATION_REPEAT);
        mAnimationHomeLogo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onRefresh();
            }
        });

        mAnimationHomeLogo.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mViewHomeLogo.setVisibility(View.INVISIBLE);
                mAnimationHomeLogo.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mViewHomeLogo.setVisibility(View.VISIBLE);
                mAnimationHomeLogo.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mViewHomeLogo.setVisibility(View.VISIBLE);
                mAnimationHomeLogo.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });

        mAnimationHomeLogo.setFailureListener(new LottieListener<Throwable>() {
            @Override
            public void onResult(Throwable result) {
                // fail 나면 이쪽으로 떨어진다.
                mViewHomeLogo.setVisibility(View.VISIBLE);
                mAnimationHomeLogo.setVisibility(View.INVISIBLE);
            }
        });

        String strImageServer = getHomeGroupInfo().appUseUrl.biImgUrl;
        String strImageEndDate = getHomeGroupInfo().appUseUrl.biImgEndDate;
        String strToday = DateUtils.getToday("yyyyMMdd");

        long dateDifference = DateUtils.getDifferenceDays(strToday, strImageEndDate);
//        long dateDifferenceReverse = DateUtils.getDifferenceDays(strImageEndDate, strToday);

        if (!URLUtil.isValidUrl(strImageServer) || (dateDifference < 0) ) {
            mViewHomeLogo.setVisibility(View.VISIBLE);
            mAnimationHomeLogo.setVisibility(View.INVISIBLE);
            return;
        }

        try {
            mAnimationHomeLogo.setAnimationFromUrl(strImageServer);
            mAnimationHomeLogo.playAnimation();
            mAnimationHomeLogo.setVisibility(View.VISIBLE);
        }
        catch (IllegalArgumentException | IllegalStateException e) {
            mViewHomeLogo.setVisibility(View.VISIBLE);
            mAnimationHomeLogo.setVisibility(View.INVISIBLE);
            Ln.e(e.getMessage());
        }
    }

    /**
     * GTM, Datahub 로깅을 스킵하기 위해 플래그를 세팅한다.
     * 예)하단 홈 탭메뉴를 클릭하면  기존에는 하단홈+상단Home+베스트딜 3개 이벤트가 로깅되었으나
     * 개선된 버전에서는 하단홈만 로깅되어야 한다.
     */
    private void preventLoggingForThreshold() {
        preventEventLogging = true;

        new Handler().postDelayed(() -> preventEventLogging = false, PREVENT_THRESHOLD_TIME);
    }

    /**
     * 싸이렌 버튼을 회원타입에 따라 노출/숨김 처리한다.(테스트계정 및 임직원에게만 노출)
     *
     * @param btnSiren 싸이렌버튼
     */
    public void setSirenIconVisibility(ImageView btnSiren) {
        //사이렌 버튼 노출 정의 및 클릭이벤트 정의 (임직원에게만 노출)
        String cusClass = CookieUtils.getWebviewCookieFromEcid(context, Keys.COOKIE.CUSCLASS);
        if (listCusClass.contains(cusClass)) {
            btnSiren.setVisibility(View.VISIBLE);
        } else {
            btnSiren.setVisibility(View.GONE);
        }
    }

    /**
     * 홈 왼쪽 하단 사이렌 버튼 노출 관련
     *
     * @param layoutSiren  layoutSiren
     * @param btnSiren     btnSiren
     * @param tooltipSiren tooltipSiren
     */
    public void setSirenLayoutVisibility(View layoutSiren, ImageView btnSiren, ImageView tooltipSiren) {

        //싸이렌 제거
        try {
            layoutSiren.setVisibility(View.GONE);
        }
        catch (NullPointerException e) {
            Ln.e(e.getMessage());
        }
        /*
        //사이렌 버튼 노출 정의 및 클릭이벤트 정의 (임직원에게만 노출)
        String cusClass = CookieUtils.getWebviewCookieFromEcid(context, Keys.COOKIE.CUSCLASS);
        if (listCusClass.contains(cusClass)) {
            layoutSiren.setVisibility(View.VISIBLE);
            btnSiren.setVisibility(View.VISIBLE);
            if (DateUtils.getDifferenceDays(DateUtils.getToday("yyyyMMdd"), sirenEndDate) <= 0) {
                tooltipSiren.setVisibility(View.GONE);
            }
        } else {
            layoutSiren.setVisibility(View.GONE);
        }
        */
    }

    /**
     * 싸이렌 페이지로 이동한다.
     */
    public void goSirenPage() {
        PackageInfo info = AppInfo.getPackageInfo(context);
        String param = "?appver=" + AppInfo.getAppVersionName(info);
        String url = ServerUrls.WEB.EMP_CIREN + param;
        WebUtils.goWeb(context, url);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        switch (requestCode) {
            case Keys.REQCODE.VIDEO: // 동영상 재생 중 상품보기 웹페이지 로딩
                if (resultCode == RESULT_CANCELED) {
                    return;
                }

                if (intent == null) {
                    break;
                }

                String nextUrl = intent.getStringExtra(Keys.INTENT.WEB_URL);
                if (nextUrl == null) {
                    break;
                }
                WebUtils.goWeb(context, nextUrl);
                break;
            case Keys.REQCODE.LOGIN:
                if (resultCode == RESULT_CANCELED) {
                    return;
                }

                String url = intent.getStringExtra(Keys.INTENT.WEB_URL);
                WebUtils.goWeb(this, url, getIntent(), true, TabMenu.fromTabMenu(getIntent()));
                break;

            case Keys.REQCODE.ATTACH_CUSTOM_CAMERA :
            case Keys.REQCODE.PHOTO_EDIT :
            case Keys.REQCODE.HOME_SEARCHING :
                super.onActivityResult(requestCode, resultCode, intent);
                break;

            case Keys.REQCODE.WEBVIEW :
                if (resultCode == RESULT_CANCELED) {
                    return;
                }
                // Webview Activity에서 Return
//                CookieUtils.showWebviewCookies(getmContext(), "쿠키 비교 2 !!!");

                String returnUrl = intent.getStringExtra(Keys.INTENT.WEB_URL);
                if (!returnUrl.contains("http://") && !returnUrl.contains("https://")) {
                    if (returnUrl.contains(WebUtils.TABID_PARAM_KEY)) {
//                        wrappedAdapter.notifyDataSetChanged();
                        String[] paramsReturn = returnUrl.split("&");
                        for(String paramReturn : paramsReturn) {
                            if (paramReturn.contains(WebUtils.TABID_PARAM_KEY + "=")) {
                                paramReturn = paramReturn.replace(WebUtils.TABID_PARAM_KEY + "=", "");
                                if (!TextUtils.isEmpty(paramReturn)) {
                                    for (TopSectionList listItem : getSelectedTopSectionList()) {
                                        Ln.d("listItem id : " + listItem.navigationId);
                                        if (paramReturn.equals(listItem.navigationId)) {
                                            moveMaintabFromNavigationId(paramReturn);

//                                            int realCount = ((InfiniteFragmentAdapter) mPager.getAdapter()).getRealCount();
                                            int currentPosition = getCurrentViewPosition();
                                            String linkUrl = listItem.sectionLinkUrl + "?" + listItem.sectionLinkParams;

                                            // Super는 따로 생성..
                                            if (ShopInfo.NAVIGATION_GS_SUPER.equals(paramReturn)) {
                                                //GS프레시몰 > 장바구니 > 배송지선택 경우도 매장리프레시가 필요함
                                                //RefreshShopEvent를 프레그먼트에서 수신하여 처리하는 로직 추가에 따라
                                                //아래코드는 매장 중복 갱신을 피하기 위해 주석처리
                                                /*EventBus.getDefault().post(new Events.FlexibleEvent.UpdateGSSuperEvent(
                                                        currentPosition, linkUrl, listItem.sectionName));*/
                                            }
                                            else {
                                                EventBus.getDefault().post(new Events.FlexibleEvent.UpdateBestShopEvent(
                                                        currentPosition, linkUrl, listItem.sectionName));
                                            }
                                            break;
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
                break;
        }
    }

    /**
     * visibleOrderDirectWebView 바로구매 웹뷰 애니메니션
     *
     * @param url
     */
    public void visibleOrderDirectWebView(String url) {
        directOrderLayout.upAnimation(this, url);
    }

    /**
     * hideOrderDirectWebView
     */
    public void hideOrderDirectWebView() {
        directOrderLayout.downAnimation(this);
        EventBus.getDefault().post(new Events.FlexibleEvent.StartRollingNo1BestDealBannerEvent(true));
    }

    @Override
    public void onBackPressed() {
        if (getDrawerLayout() != null && getDrawerLayout().isDrawerOpen(getNavigationView())) {
            closeNavigationView();
            return;
        }

        if (DirectOrderView.isVisibleDirectOrder) {
            hideOrderDirectWebView();
        } else {
            exitHandler.handle();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @Nonnull String[] permissions, @Nonnull int[] grantResults) {
        Ln.i("CallGate : onRequestPermissionsResult");
        switch (requestCode) {
            case Keys.REQCODE.PERMISSIONS_REQUEST_PHONE:
                //사용자가 모든 권한을 허용한 경우
                if (PermissionUtils.verifyPermissions(this, permissions, grantResults)) {
                    if (mToken != null && mToken.length() > 0) {
                        // READ_PHONE_STATE 권한 허가되면 Callgate 이용자 등록
//                        launcherLinker.checkUserInformation(launcherId, mToken);  // callgate 구문 삭제
                    }
                }
                break;

            case Keys.REQCODE.PERMISSIONS_REQUEST_STORAGE_CAMERA_CUSTOM :
            case Keys.REQCODE.PERMISSIONS_REQUEST_STORAGE_CAMERA_CUSTOM_FROM_SEARCH :
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;

            default:
                break;
        }
    }

    private void requestPermission() {
        //권한체크
        if (!PermissionUtils.isPermissionGrantedForPhone(this)) {
            Ln.i("RequestPermission : !PermissionUtils.isPermissionGrantedForPhone");

            //전화 권한 없는 경우 권한요청 팝업 노출
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE}, Keys.REQCODE.PERMISSIONS_REQUEST_PHONE);
        }
    }

    private android.os.Handler mCspTextViewHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == StaticCsp.CLOSE_MESSAGE) {
                if(textview_csp != null){
                    Animation a = AnimationUtils.loadAnimation(mContext, R.anim.anim_guide_left_in);
                    a.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            textview_csp.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    a.reset();
                    textview_csp.clearAnimation();
                    textview_csp.startAnimation(a);
                }
            }
            else if(msg.what == StaticCsp.CLOSEALL_MESSAGE)
            {
                mCspTextViewHandler.removeMessages(StaticCsp.CLOSE_MESSAGE);
                layoutCsp.setVisibility(View.GONE);
                btnCsp.setVisibility(View.GONE);
                textview_csp.setVisibility(View.GONE);
            }
        }
    };

    private void setCategoryTooltip() {
        mIvTooltipCategory.animate()
                .alpha(1.0f)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mIvTooltipCategory.setVisibility(View.VISIBLE);
                    }
                });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mIvTooltipCategory.animate()
                        .translationY(mIvTooltipCategory.getHeight())
                        .alpha(0.0f)
                        .setDuration(500)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                mIvTooltipCategory.setVisibility(View.GONE);
                            }
                        });
            }
            //너무 안보인다 아웃 터치가 될것 같다, 더더 길게 하거나 ;;
        }, 10000);
    }

    /**
     * TMS 설정에 Addview 할 viewGroup을 전달해 준다.
     */
    public void setTmsAddView() {
        InAppMessageManager.getInstance().setParentView(findViewById(R.id.home_root_view));
    }
}
