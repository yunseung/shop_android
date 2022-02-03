/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.drawerlayout.widget.DrawerLayout;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.gsshop.mocha.core.activity.BaseFragmentActivity;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.pattern.mvc.BaseAsyncController;

import org.apache.http.NameValuePair;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.HomeGroupInfo;
import gsshop.mobile.v2.home.HomeGroupInfoAction;
import gsshop.mobile.v2.home.shop.nalbang.CategoryDataHolder;
import gsshop.mobile.v2.home.shop.nalbang.ShortbangActivity;
import gsshop.mobile.v2.intro.IntroActivity;
import gsshop.mobile.v2.menu.BadgeTextView;
import gsshop.mobile.v2.menu.TabMenu;
import gsshop.mobile.v2.menu.navigation.CateContentList;
import gsshop.mobile.v2.menu.navigation.DrawerLayoutHorizontalSupport;
import gsshop.mobile.v2.menu.navigation.MassageCardLayout;
import gsshop.mobile.v2.menu.navigation.NavigationManager;
import gsshop.mobile.v2.menu.navigation.NavigationResult;
import gsshop.mobile.v2.search.HomeSearchActivity;
import gsshop.mobile.v2.setting.SettingActivity;
import gsshop.mobile.v2.support.badge.BadgeInfo;
import gsshop.mobile.v2.support.gtm.GTMAction;
import gsshop.mobile.v2.support.gtm.datahub.DatahubAction;
import gsshop.mobile.v2.support.gtm.datahub.DatahubUrls;
import gsshop.mobile.v2.support.permission.UpdatePassActivity;
import gsshop.mobile.v2.support.wiselog.WiseLogAction;
import gsshop.mobile.v2.user.LoginActivity;
import gsshop.mobile.v2.user.PopupBasicActivity;
import gsshop.mobile.v2.user.User;
import gsshop.mobile.v2.user.UserAction;
import gsshop.mobile.v2.util.CookieUtils;
import gsshop.mobile.v2.util.DataUtil;
import gsshop.mobile.v2.util.DateUtils;
import gsshop.mobile.v2.util.EncryptUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.util.LunaUtils;
import gsshop.mobile.v2.util.PrefRepositoryNamed;
import gsshop.mobile.v2.util.ThreadUtils;
import gsshop.mobile.v2.web.DirectOrderWebActivity;
import gsshop.mobile.v2.web.FullModalWebActivity;
import gsshop.mobile.v2.web.ModalWebActivity;
import gsshop.mobile.v2.web.MyShopWebActivity;
import gsshop.mobile.v2.web.NoTabWebActivity;
import gsshop.mobile.v2.web.OrderWebActivity;
import gsshop.mobile.v2.web.TransparentModalWebActivity;
import gsshop.mobile.v2.web.WebActivity;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isEmpty;
import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;
import static gsshop.mobile.v2.home.HomeActivity.showPsnlCuration;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleShopFragment.scheduleBroadType;


/**
 * 앱의 모든 액티비티가 상속받는 공통기본 액티비티.
 * <p>
 * - 액티비티 간 이동시 화면 전환 효과 제거(하위 액티비티에서 변경가능)
 */
public abstract class AbstractBaseActivity extends BaseFragmentActivity {

    protected ImageButton btnBack;
    protected TextView txtTitle;
    protected TextView txtRightMenu;
    private TextView mTxtNaviTitle;

    private static String mServerType;

    @Inject
    protected HomeGroupInfoAction homeGroupInfoAction;

    @Inject
    protected RestClient restClient;

    @Inject
    protected WiseLogAction wiseLog;

    /**
     * 백그라운드/포그라운드 전환 확인에 사용되는 변수
     */
    public static boolean isAppWentToBg = false;
    /**
     * 윈도오 전환 확인에 사용되는 변수
     */
    public static boolean isWindowFocused = false;
    /**
     * 백키 선택 유무에 대한 변수(사용처가 애매함) 11/14 추가 로직에 사용할 경우 재검토 필요
     */
    public static boolean isBackPressed = false;

    /**
     * 네이티브 페이지 정의
     */
    public static String NATIVE_PAGE = "NATIVE_PAGE"; //인트로등등
    /**
     * 안드로이드 / 아이오에스 구분
     */
    public static String ANDROID_VAR = "ANDROID"; //인트로

    //최근 본 상품 쿠키이름
    public static final String COOKIE_LAST_PRD_KEY = "lastprdid";
    //최근 본 상품 쿠키스트링 구분자
    public static final String COOKIE_LAST_PRD_SEPERATOR = "%25";
    //최근 본 상품 이미지 TAIL
    public static final String LAST_PRD_IMG_TAIL = "_T1.jpg";

    // 마지막 비밀번호 변경 일자
    private static final int DATE_PWD_LAST_CHANED = 30;

    //네비게이션 Drawer레이아웃
    private DrawerLayoutHorizontalSupport drawerLayout;
    private static ArrayList<DrawerLayoutHorizontalSupport> mArrDrawerLayout;
    //네비게이션뷰
    private View navigation_view;
    //네비게이션 레이아웃
    private RelativeLayout image_layout;
    //네비게이션 팝업 이미지
    private ImageView navigationPopupImage;
    //해더 오른쪽 메뉴 클릭 리스너
    HeaderMenuClickListener menuClickListener;

    /**
     * 네트웍 에러시 보여줄 뷰
     */
    private View error_view;

    /**
     * 메세지카드 레이아웃
     */
    private MassageCardLayout message_card_layout;


    /**
     * 레이아웃 순서변경을 위한 그룹 뷰
     */
    private ViewGroup scene_container;

    /**
     * 스크롤 컨테이너
     */
    private ScrollView scene_root;

    /**
     * 네비게이션 배경 뷰
     */
    private ImageView background_view, background_color_view;

    /**
     * 네비게이션 메세지닫기 버튼
     */
    private Button message_card_close;

    /**
     * 새로고침 버튼, 웹으로 보기 버튼 (네트워크 장애 시)
     */
    private FrameLayout mBtnRefresh, mBtnGoWeb;

    private CateContentList[] interstCategories = null;

    /**
     * 네비게이션 관리 매니저
     */
    private NavigationManager navigationManager;

    /**
     * 네비게이션 상단 레이아웃
     */
    private RelativeLayout navigationTopLayout;

    /**
     * isNewintent가 호출되었는지 여부
     */
    private boolean isNewintent = false;

    private Context mContext;

    /**
     * 로그인 완료시 토스트 중복노출 방지 플래그
     */
    private static boolean isShowToast = false;

    /**
     * 비밀번호 변경팝업 중복노출 방지 플래그
     */
    private static boolean isShowUpdatePass = true;

    /**
     * 지문인식 접근 확인 팝업 중복노출 방지 플래그
     */
    private static boolean isShowCheckFP = true;

    /**
     * 기본 팝업 중복노출 방지 플래그
     */
    private static boolean isShowBasic = true;

    /**
     * 네비게이션 갱신을 매번 하지 않도록 하기 위한 플래그
     */
    private static boolean isNewNavigation = true;

    /**
     * 네비게이션 ID 여기서 가지고 있게끔 Home이 아니라
     */
    protected static String mCurrentNavigationId = "";

    /**
     * 사용자 액션이 Hyper-Pesonalized Curation 노출 대상인지 여부 플래그
     * - 현재는 단품과 장바구니에서 홈버튼 클릭, 백키클릭 액션이 대상임)
     */
    protected boolean isPsnlCurationTarget = false;

    //웹뷰액티비티 정보
    private final List<String> listWebview = Arrays.asList(
            WebActivity.class.getSimpleName(),
            NoTabWebActivity.class.getSimpleName(),
            MyShopWebActivity.class.getSimpleName(),
            ModalWebActivity.class.getSimpleName(),
            FullModalWebActivity.class.getSimpleName(),
            TransparentModalWebActivity.class.getSimpleName(),
            OrderWebActivity.class.getSimpleName());

    /**
     * 해더 오른쪽 메뉴가 필요한 경우 본 함수를 호출하여 세팅
     *
     * @param menuName 표시할 텍스트
     * @param callback HeaderMenuClickListener
     */
    protected void setHeaderRightMenu(String menuName, HeaderMenuClickListener callback) {
        menuClickListener = callback;
        txtRightMenu = (TextView) findViewById(R.id.txt_right_menu);
        txtRightMenu.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                menuClickListener.rightMenuClick();
            }

        });

        setHeaderRightMenuName(menuName);
        txtRightMenu.setVisibility(View.VISIBLE);
    }

    private void clearCacheWithDelay() {
        ThreadUtils.INSTANCE.runInUiThread(() -> MainApplication.clearCache(), 2000);
    }

    /**
     * 해더 오른쪽 메뉴의 이름을 세팅한다.
     *
     * @param menuName 메뉴이름
     */
    protected void setHeaderRightMenuName(String menuName) {
        txtRightMenu.setText(menuName);
    }

    /**
     * header를 가진 activity들은 이 method로 title 등을 설정 setContentView() 이후에 호출
     *
     * @param titleResId 타이틀 resource
     * @param showBack   뒤로가기 버튼 여부
     */
    protected void setHeaderView(int titleResId, boolean showBack) {
        setHeaderBackBtn(showBack);
        txtTitle = (TextView) findViewById(R.id.txt_header_title);
        txtTitle.setText(titleResId);
    }

    @Override
    public void setContentView(int layoutResID) {
        if (!isNavigationActivity()) {
            super.setContentView(layoutResID);
            return;
        }

        if (mArrDrawerLayout == null) {
            mArrDrawerLayout = new ArrayList<>();
        }

        //화면에 뷰를 추가시킬때 네비게이션뷰 안에 컨텐츠를 추가시킨다.
        drawerLayout = (DrawerLayoutHorizontalSupport) getLayoutInflater().inflate(R.layout.activity_base, null);
        navigation_view = drawerLayout.findViewById(R.id.navigation_view);
        FrameLayout activityContainer = (FrameLayout) drawerLayout.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);
        super.setContentView(drawerLayout);

        drawerLayout.setScrimColor(Color.TRANSPARENT);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        //네비게이션뷰 크기는 화면의 90%로 셋팅 -> 100%로 변경
//        int width = (int)(getResources().getDisplayMetrics().widthPixels * 90.0 / 100.0);
        int width = getResources().getDisplayMetrics().widthPixels;
        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) navigation_view.getLayoutParams();
        params.width = width;
        navigation_view.setLayoutParams(params);

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // vod 동영상 일시멈춤
                EventBus.getDefault().post(new Events.VodShopPlayerEvent(Events.VodShopPlayerEvent.VodPlayerAction.VOD_PAUSE));
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        if (isNavigationActivity()) {
            initNavigationView();
        }

        mArrDrawerLayout.add(drawerLayout);

        //네이게이션 뷰 업데이트
        if (navigationManager != null && navigationManager.getCategoryGroupLayout() != null && navigationManager.getCategoryGroupLayout().isCategoryRefresh()) {
            if (NavigationManager.getCacheData() != null) {
                NavigationResult result = NavigationManager.getCacheData();
                if (result != null && result.appCmmCategory != null && result.appCmmCategory.cateContentList != null) {
                    setCategoryView(result);
                }
            }
        }
    }

    /**
     * 네비게이션을 호출한다.
     * 8/6 호출되지 않도록 제거 추후 모든 소스 제거거
     */
    /*
    public void callLeftNavigation() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMetodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            EventBus.getDefault().post(new Events.FlexibleEvent.AdTooltipEvent(null, false));
        } catch (Exception e) {
        }

        //네비게이션 데이터를 호출한다.
        //호출조건 1.최초 1회, 2.리프레시화면상태에서 네트워크 연결된 경우, 3.네트워크 끊긴 경우
        //네트워크가 끊긴 상태에서도 이미 로딩된 컨텐츠는 노출해도 된다면 3번조건 제거하면 됨
        if (isNewNavigation
                || (NetworkUtils.isNetworkAvailable(mContext) && error_view.getVisibility() == View.VISIBLE)
                || !NetworkUtils.isNetworkAvailable(mContext)) {
            updateNavigationCntents();
            isNewNavigation = false;
        }

        //네비게이션을 연다
        openNavigationView(true);

        //앰플리듀드 이벤트 태킹 ( 카테고리 화면 )

//        20200806 1차 햄버거 메뉴 호출 제거 작업 2차때에 완전 삭제
//        try {
//            //카테고리 페이지 뷰 이벤트
//            AMPAction.sendAmpEvent(AMP_VIEW_LEFTNAVI);
//        } catch (Exception e) {
//
//        }

    }
    */
    @Override
    public void onBackPressed() {
        //뒤로가기 버튼을 눌렀을때 네비게이션이 열려있는 상태이면 네비게이션을 닫는다.
        if (drawerLayout != null && drawerLayout.isDrawerOpen(navigation_view)) {
            closeNavigationView();
            return;
        }
        super.onBackPressed();
    }

    /**
     * 네비게이션뷰을 초기화 한다.
     */
    private void initNavigationView() {
        navigation_view = drawerLayout.findViewById(R.id.navigation_view);
        background_view = (ImageView) navigation_view.findViewById(R.id.background_view);
        background_color_view = (ImageView) navigation_view.findViewById(R.id.background_color_view);
        mTxtNaviTitle = (TextView) navigation_view.findViewById(R.id.txt_navi_top);

        initNavigationTitle();

//        #Navigation 개선 백그라운드 색상 고정 hklim
        background_color_view.setBackgroundColor(Color.WHITE);

        message_card_layout = (MassageCardLayout) navigation_view.findViewById(R.id.message_card_layout);
        message_card_close = (Button) navigation_view.findViewById(R.id.message_card_close);
        image_layout = (RelativeLayout) navigation_view.findViewById(R.id.image_layout);
        navigationPopupImage = (ImageView) navigation_view.findViewById(R.id.image);
        navigationTopLayout = (RelativeLayout) navigation_view.findViewById(R.id.top_layout);
        error_view = navigation_view.findViewById(R.id.flexible_refresh_layout);
        mBtnRefresh = (FrameLayout) navigation_view.findViewById(R.id.btn_refresh);
        mBtnGoWeb = (FrameLayout) navigation_view.findViewById(R.id.btn_go_web);

        scene_root = (ScrollView) navigation_view.findViewById(R.id.scene_root);
        scene_container = (ViewGroup) navigation_view.findViewById(R.id.scene_container);
        scene_container.removeAllViews();

        navigationManager = new NavigationManager(scene_root, scene_container, drawerLayout, restClient);
        navigationManager.createNavigationView(this, scene_container);
        navigationManager.displayCategory();

        OnClickListener clickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {

                    //메세지카드 닫기
                    case R.id.message_card_close:
                        message_card_layout.close();
                        break;

                    //navigationTopLayout 밑에 깔려있는 뷰가 호출되지 않기위해 OnClickListener 설정
                    case R.id.top_layout:
                        break;

                    //홈버튼 클릭시
                    case R.id.gs_logo:
                        GetHomeGroupListInfo();
                        //메인 상단 로고 클릭시 와이즈로그 전송
                        setWiseLogHttpClient(ServerUrls.WEB.NAVI_LOGO);
                        break;

                    //네비게이션 닫기 클릭시
                    case R.id.close:
                        if (drawerLayout != null && drawerLayout.isDrawerOpen(navigation_view)) {
                            closeNavigationView();
                            //메인 상단 닫기버튼 클릭시 와이즈로그 전송
                            setWiseLogHttpClient(ServerUrls.WEB.NAVI_CLOSE);
                        }
                        break;

                    //검색버튼 클릭시
                    case R.id.icon_search:
                        EventBus.getDefault().post(new Events.FlexibleEvent.TvLivePlayEvent(false, -1));

                        Intent intent = new Intent(Keys.ACTION.SEARCH);
                        intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.HOME);
                        startActivityForResult(intent, Keys.REQCODE.HOME_SEARCHING);

                        //메인상단 검색 입력박스 클릭시 와이즈로그 전송
                        setWiseLogHttpClient(ServerUrls.WEB.NAVI_SEARCH);

                        //GTM Datahub 이벤트 전달
                        DatahubAction.sendDataToDatahub(mContext, DatahubUrls.D_1016, "");
                        break;

                    //새로고침 버큰 클릭시
                    case R.id.btn_refresh:
                        HomeGroupInfo homeGroupInfo = getHomeGroupInfo();
                        new NavigationController(mContext, error_view, true).execute(homeGroupInfo.leftNavigation.personalApiUrl);
                        break;

                    //웹으로 보기 버큰 클릭시
                    case R.id.btn_go_web:
                        Intent goWebIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ServerUrls.MOBILE_WEB));
                        startActivity(goWebIntent);
                        break;

                    //메세지카드 외부영역을 클릭시 메세지카드 닫기
                    case R.id.message_card_layout:
                        message_card_layout.close();
                        break;

                    // 설정 이동 버튼 클릭시
                    case R.id.icon_setting:
                        goSetting();
                        setWiseLogHttpClient(ServerUrls.WEB.NAVI_SETTING);
                        break;
                }
            }
        };

        message_card_close.setOnClickListener(clickListener);
        navigationTopLayout.setOnClickListener(clickListener);
        navigation_view.findViewById(R.id.gs_logo).setOnClickListener(clickListener);
        navigation_view.findViewById(R.id.close).setOnClickListener(clickListener);
        navigation_view.findViewById(R.id.icon_setting).setOnClickListener(clickListener);
        navigation_view.findViewById(R.id.icon_search).setOnClickListener(clickListener);
        mBtnRefresh.setOnClickListener(clickListener);
        mBtnGoWeb.setOnClickListener(clickListener);
        message_card_layout.setOnClickListener(clickListener);

        //메세지카드 사이즈는 화면크기의 80%크기
        int message_card_size = (int) (getResources().getDisplayMetrics().widthPixels * 80.0 / 100.0);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) navigationPopupImage.getLayoutParams();
        params.width = message_card_size;
        params.height = message_card_size;
        navigationPopupImage.setLayoutParams(params);

        RelativeLayout.LayoutParams paramsLayout = (RelativeLayout.LayoutParams) image_layout.getLayoutParams();
        paramsLayout.width = message_card_size;
        paramsLayout.height = message_card_size;
        image_layout.setLayoutParams(paramsLayout);

        //좌우로 스와이프할때 리스트영역을 제외하기 하기 위해 추가
        drawerLayout.setScrollInsideView(navigation_view, scene_root);
    }

    private void initNavigationTitle() {
        ThreadUtils.INSTANCE.runInUiThread(() -> {
            try {
                if (isEmpty(navigation_view)) {
                    return;
                }
                View viewLoginClick = navigation_view.findViewById(R.id.layout_txt_navi_top);
                ImageView top_arrow = navigation_view.findViewById(R.id.top_arrow);
                User user = User.getCachedUser();
                String strCustomer = "";
                if (user == null || TextUtils.isEmpty(user.customerNumber)) {
                    strCustomer = getString(R.string.navi_default_top_text);
                    if (viewLoginClick != null) {
                        viewLoginClick.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                goLogin();
                                setWiseLogHttpClient(ServerUrls.WEB.NAVI_LOGIN);
                            }
                        });
                    }
                    top_arrow.setVisibility(View.VISIBLE);
                } else {
                    String strGrade = user.getUserGrade();
                    top_arrow.setVisibility(View.GONE);
                    if (strGrade != null) {
                        strCustomer += strGrade;
                    }
                    strCustomer += (" " + user.getUserName() + "님");
                    if (viewLoginClick != null)
                        viewLoginClick.setOnClickListener(null);
                }

                mTxtNaviTitle.setText(strCustomer);
            } catch (NullPointerException e) {
                Ln.e(e);
            }
        }, 100);
    }

    /**
     * 연재 액티비티가 네비게이션을 사용하는 액티비티인지 여부 리턴
     */
    public boolean isNavigationActivity() {
        if (this instanceof IntroActivity || this instanceof HomeSearchActivity) {
            return false;
        }
        return true;
    }

    /**
     * 네비게이션 데이터 호출
     */
    public void updateNavigationCntents() {
        new NavigationController(mContext, error_view, false).execute(getHomeGroupInfo().leftNavigation.personalApiUrl);
    }

    /**
     * back 버튼 존재여부를 명시하지 않으면 default로 show로 설정
     *
     * @param titleResId 타이틀ID
     */
    protected void setHeaderView(int titleResId) {
        setHeaderView(titleResId, true);
    }

    protected void setHeaderView(String strTitle) {
        setHeaderBackBtn(true);

        txtTitle = (TextView) findViewById(R.id.txt_header_title);
        txtTitle.setText(strTitle);
    }

    /**
     * 헤더 back 버튼 설정. 탭 메뉴에서 호출되었을 경우에는 숨김.
     *
     * @param show 버튼보이기
     */
    private void setHeaderBackBtn(boolean show) {
        btnBack = (ImageButton) findViewById(R.id.btn_header_back);
        btnBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                onClickHeaderBackBtn();
            }

        });
        if (show) {
            btnBack.setVisibility(View.VISIBLE);
        } else {
            btnBack.setVisibility(View.GONE);
        }
    }

    /**
     * 해더 백키 클릭시 액션 정의
     */
    protected void onClickHeaderBackBtn() {
        finish();
    }

    @Override
    protected void onDestroy() {
        //키바나로그 재현이 안되어 우선 앱종료 방지 처리
        try {
            super.onDestroy();
        } catch (Exception e) {
            // 루나 로그 전송
            LunaUtils.sendToLuna(mContext, e, null);
            Ln.e(e);
        }
        EventBus.getDefault().unregister(this);
    }

    /**
     * header title만 변경할 때 사용
     *
     * @param titleResId 타이틀ID
     */
    protected void setHeaderTitle(int titleResId) {
        txtTitle.setText(titleResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        // TEST 앱 (배포용 제외) 인 경우 서버정보를 임의로 바꿨을 때 재실행시 바꿨던 서버를 유지해주기위한 테스트용 코드
        if (!BuildConfig.FLAVOR_stage.equalsIgnoreCase("m")) {
            switch (PrefRepositoryNamed.getString(AbstractBaseActivity.this, Keys.PREF.PREF_NAME_TEST_SERVER, Keys.PREF.PREF_TEST_SERVER)) {
                case "m":
                    mServerType = "m";
                    ServerUrls.TEMP_WEB_ROOT = "http://m.gsshop.com";
                    ServerUrls.TEMP_HTTP_ROOT = "http://m.gsshop.com";
                    ServerUrls.TEMP_HTTPS_ROOT = "https://m.gsshop.com";
                    ServerUrls.CSP_SERVICE_ROOT = "https://csp.gsshop.com";
                    break;
                case "asm":
                    mServerType = "asm";
                    ServerUrls.TEMP_WEB_ROOT = "http://asm.gsshop.com";
                    ServerUrls.TEMP_HTTP_ROOT = "http://asm.gsshop.com";
                    ServerUrls.TEMP_HTTPS_ROOT = "https://asm.gsshop.com";
                    ServerUrls.CSP_SERVICE_ROOT = "https://csp.gsshop.com";
                    break;
                case "atm":
                    mServerType = "atm";
                    ServerUrls.TEMP_WEB_ROOT = "http://atm.gsshop.com";
                    ServerUrls.TEMP_HTTP_ROOT = "http://atm.gsshop.com";
                    ServerUrls.TEMP_HTTPS_ROOT = "https://atm.gsshop.com";
                    ServerUrls.CSP_SERVICE_ROOT = "https://cspdev.innolab.us";
                    break;
                case "tm14":
                    mServerType = "tm14";
                    ServerUrls.TEMP_WEB_ROOT = "http://tm14.gsshop.com";
                    ServerUrls.TEMP_HTTP_ROOT = "http://tm14.gsshop.com";
                    ServerUrls.TEMP_HTTPS_ROOT = "https://tm14.gsshop.com";
                    ServerUrls.CSP_SERVICE_ROOT = "https://cspdev.innolab.us";
                    break;
                case "am":
                    mServerType = "am";
                    ServerUrls.TEMP_WEB_ROOT = "http://asm.gsshop.com";
                    ServerUrls.TEMP_HTTP_ROOT = "http://asm.gsshop.com";
                    ServerUrls.TEMP_HTTPS_ROOT = "https://asm.gsshop.com";
                    ServerUrls.CSP_SERVICE_ROOT = "https://csp.gsshop.com";
                    break;
            }
        } else {
            PrefRepositoryNamed.remove(AbstractBaseActivity.this, Keys.PREF.PREF_NAME_TEST_SERVER, Keys.PREF.PREF_TEST_SERVER);
        }

        /**
         * Login 화면이 아래에서 부터 나오게끔 하는 로직 추가됨.
         */
        boolean isShowFromBottom = getIntent().getBooleanExtra(Keys.INTENT.INTENT_FROM_BOTTOM, false);
        if (isShowFromBottom) {
            overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.slide_out_to_top);
        } else {
            if (isAnimationSkip(this)) {
                overridePendingTransition(0, 0);
            } else {
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        }
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // onNewIntent로 호출될때의 애니메이션 스킵 리스트 호출 ** 마이쇼핑은 OnNewIntent에서는 애니메이션 호출 안함
        // onNewIntent로 호출된다면 이미 화면에 MyshopoWeb엑티비티이다, 마이쇼핑은 스탄다드 런치 속성으로 호출하는 쪽에서 컨트롤한다
        // 메뉴베이스엑티비트 호출시에 클리어탑/싱글탑으로 분기하여 해당 처리를 수행하였다
        if (isOnNewAnimationSkip(this)) {
            overridePendingTransition(0, 0);
        } else {
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

        //페이지이동시 새로운 activity를 사용하지 않고 기존 액티비티를 재사용하는경우
        //네비게이션을 닫고 이동
        isNewintent = true;
        if (drawerLayout != null && drawerLayout.isDrawerOpen(navigation_view)) {
            closeNavigationView();
        }
    }

    /**
     * performStartTransition() 적용
     */
    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        performStartTransition();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        //백키로 상위 액티비티가 종료된 경우에는
        isWindowFocused = hasFocus;
        if (isBackPressed && !hasFocus) {
            isBackPressed = false;
            isWindowFocused = true;
        }
    }

    /**
     * performFinishTransition() 적용
     */
    @Override
    public void finish() {
        super.finish();

        if (isAnimationSkip(this)) {
            overridePendingTransition(0, 0);
        } else {
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    //앱푸시 PushApproveActivity 애니메이션
    public void finish_push() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AbstractBaseActivity.super.finish();
                overridePendingTransition(0, 0);
            }
        }, 400);
    }


    @Override
    protected void onStart() {
        super.onStart();
        startGTMTracking();
        if (isAppWentToBg) {
            isAppWentToBg = false;
            //백그라운드에서 포그라운드로 앱이 전환될때 필요코드 삽입

            //VOD 매장에서 동영상 재생중 백->포 전환시 컨트롤영역 미노출 현상 대응
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    EventBus.getDefault().post(new Events.VodShopPlayerEvent(Events.VodShopPlayerEvent.VodPlayerAction.VOD_BACK_TO_FOR));
                }
            }, 2000);
        }

        //홈과 대상 액티비티 중간에 다른 액티비티가 있는 경우 Hyper-Pesonalized Curation 대상에서 제외
        //중간 액티비티가 있다면 onStart 호출됨
        if (!(this instanceof HomeActivity)) {
            showPsnlCuration = false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //홈버튼 클릭시 onWindowFocusChanged에서 isWindowFocused값이 false로 세팅됨
        //단, 홈>웹액티비티 형태의 상태에서 백키를 누르면 홈이 노출되는데 이때는 백그라운드 상태로 인식하면 안되기 때문에
        //onWindowFocusChanged에서 함수에서 예외처리를 함
        if (!isWindowFocused) {
            isAppWentToBg = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 20200806 1차 햄버거 메뉴 호출 제거 작업 2차때에 완전 삭제
        /*
        if (isNavigationActivity()) {
            if (!isNewintent) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateNavigation();
                    }
                }, 500);
            } else {
                isNewintent = false;
            }
        }
         */
    }

    @Override
    protected void onPause() {
        //키바나로그 재현이 안되어 우선 앱종료 방지 처리
        try {
            super.onPause();
        } catch (Exception e) {
            // 루나 로그 전송
            LunaUtils.sendToLuna(mContext, e, null);
            Ln.e(e);
        }
    }

    protected void startGTMTracking() {
        try {
            //WebView activity는 url을 로깅하기 때문에 여기서는 화면명을 별도로 로깅하지 않음
            if (listWebview.contains(getClass().getSimpleName().trim())) {
                return;
            }
            //GTM 화면로깅
            GTMAction.openScreen(this, getClass().getSimpleName().trim().replace("Activity", ""));
        } catch (Exception e) {
            // 10/19 품질팀 요청
            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
            Ln.e(e);
        }
    }

    /**
     * 활성화면 이름을 웹뷰 url 값으로 로깅한다.
     *
     * @param url 웹뷰가 로딩하는 url
     */
    protected void setScreenViewWithUrl(String url) {
        try {
            //GTM 화면로깅
            GTMAction.openScreen(this, url);
        } catch (Exception e) {
            // 10/19 품질팀 요청
            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
            Ln.e(e);
        }
    }

    /**
     * 다른 액티비티로 전환시 화면 전환 효과 제거
     */
    protected void performStartTransition() {
        overridePendingTransition(R.anim.no_transition, R.anim.no_transition);
    }

    /**
     * 현재 액티비티 종료시 화면 전환 효과 제거
     */
    protected void performFinishTransition() {
        overridePendingTransition(R.anim.no_transition, R.anim.no_transition);
    }

    /**
     * 네이티브 페이지 로딩 속도 측정
     *
     * @param value 걸린 시간
     * @param label INTRO 인트로 페이지, 각 섹션(베스트딜,의류딜,여성커리어)로그인 페이지, 상품평, 쇼핑알림, 검색 메인, 카테고리 메인
     */
    public void setNativePageTiming(long value, String label) {
        try {
            if (value > 0) {
                setTiming(NATIVE_PAGE, value, ANDROID_VAR, label);
            }
        } catch (Exception e) {
            Ln.e(e);
        }
    }

    private final static String ANALYTICS_PARAMS_CATEGORY = "category";
    private final static String ANALYTICS_PARAMS_VALUE = "value";
    private final static String ANALYTICS_PARAMS_VARIABLE = "variable";
    private final static String ANALYTICS_PARAMS_LABEL = "label";

    /**
     * GA App Speed 공통 API
     * <p>
     * Web Speed 측정과
     * App (네이티브) 화면 로딩 측정
     *
     * @param category 카테고리
     * @param value    값
     * @param variable variable
     * @param label    label
     */
    private void setTiming(String category, long value, String variable, String label) {
        // Tracker 변경으로 이름으로 기존과 동일하게 보내면 된다.
        Bundle parmas = new Bundle();
        // Key를 그냥 통일성 있게 선언해서 보냄.
        parmas.putString(ANALYTICS_PARAMS_CATEGORY, category);
//        parmas.putLong(FirebaseAnalytics.Param.VALUE, value);
        parmas.putLong(ANALYTICS_PARAMS_VALUE, value);
        parmas.putString(ANALYTICS_PARAMS_VARIABLE, variable);
        parmas.putString(ANALYTICS_PARAMS_LABEL, label);

        FirebaseAnalytics.getInstance(this).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, parmas);
    }

    /**
     * 해더 오른쪽 메뉴 클릭 리스너
     */
    public interface HeaderMenuClickListener {
        /**
         * 해더 오른쪽 메뉴 클릭 리스너(인터페이스)
         */
        public void rightMenuClick();
    }

    /**
     * onCreate 액티비티 전환시 애니메이션 예외 여부를 확인한다.
     *
     * @return 예외대상 액티비티이면 true 리턴
     */
    private boolean isAnimationSkip(Context activity) {
        return activity instanceof HomeActivity
                || activity instanceof ShortbangActivity
                || activity instanceof HomeSearchActivity
                || activity instanceof UpdatePassActivity
                || activity instanceof DirectOrderWebActivity
                || activity instanceof PopupBasicActivity;
    }

    /**
     * onNewIntent 액티비티 전환시 애니메이션 예외 여부를 확인한다.
     *
     * @return 예외대상 액티비티이면 true 리턴
     */
    private boolean isOnNewAnimationSkip(Context activity) {
        return activity instanceof HomeActivity
                || activity instanceof ShortbangActivity
                || activity instanceof HomeSearchActivity
                || activity instanceof MyShopWebActivity
                || activity instanceof WebActivity;
    }

    /**
     * 최근 본 상품의 아이디값을 쿠키로부터 추출한다.
     *
     * @return 상품아이디, 값이 없으면 null 리턴
     */
    public String getLastPrdIdFromCookie() {
        NameValuePair lastPrdPair = CookieUtils.getWebviewCookie(this, COOKIE_LAST_PRD_KEY);
        if (lastPrdPair == null || TextUtils.isEmpty(lastPrdPair.getValue())) {
            return null;
        }

        String lastPrds = lastPrdPair.getValue();
        String[] lastPrdList = lastPrds.split(COOKIE_LAST_PRD_SEPERATOR);
        if (lastPrdList.length > 0) {
            return lastPrdList[0];
        } else {
            return null;
        }
    }

    /**
     * 스크린샷을 저정한다.
     *
     * @return
     */
    public ByteArrayOutputStream getScreenShot() {
        View rootView = getWindow().getDecorView();
        rootView.setDrawingCacheEnabled(true);  //화면에 뿌릴때 캐시를 사용하게 한다
        Bitmap screenBitmap = rootView.getDrawingCache();   //캐시를 비트맵으로 변환

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        screenBitmap.compress(Bitmap.CompressFormat.JPEG, 0, stream);

        rootView.setDrawingCacheEnabled(false);
        screenBitmap.recycle();

        return stream;
    }

    /**
     * 쿠키의 정보를 바탕으로
     * 장바구니 카운트를 표시한다
     */
    public void setBasketcnt() {
        BadgeTextView badgeTextView = (BadgeTextView) findViewById(R.id.basketcnt_badge);
        if (badgeTextView != null) {
            badgeTextView.setVisibility(View.GONE);

            NameValuePair pair = CookieUtils.getWebviewCookie(this, "cartcnt");
            if (pair != null) {
                String value = pair.getValue();

                try {
                    int count = Integer.parseInt(value.trim());
                    if (count > 99) {
                        count = 99;
                    }
                    if (count > 0) {
                        badgeTextView.setText(String.valueOf(count));
                        badgeTextView.setVisibility(View.VISIBLE);
                    } else {
                        badgeTextView.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    badgeTextView.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * 네비게이션 닫기(x) 저장(o) 이벤트, 기존에 닫기가 NavigationCloseDrawerEvent 로 변경.
     *
     * @param event
     */
    public void onEvent(Events.NavigationCloseEvent event) {
//      포지션 저장만 하고 닫는 동작은 안할꺼다. Close 동작은 오픈할때에 하게끔 수정.
        saveNavigationPosition();
    }

    public void onEvent(Events.NavigationCloseDrawerEvent event) {
//      기존에 네비게이션 닫는 이벤트가 메뉴 이동 로직 개선에 의해 실제로 닫는 이벤트가 없어짐. 이에 NavigationCloseDrawerEvent 새로 작성
        closeNavigationView();
    }

    /**
     * 네비게이션 업데이트 이벤트
     *
     * @param event
     */
    public void onEvent(Events.NavigationUpdateEvent event) {
        new NavigationController(mContext, error_view, false).execute(getHomeGroupInfo().leftNavigation.personalApiUrl);
    }

    /**
     * 네비게이션 새로고침 이벤트
     *
     * @param event
     */
    public void onEventMainThread(Events.NavigationReflashEvent event) {
        initNavigationTitle();
        if (navigationManager != null && navigationManager.getCategoryGroupLayout() != null) {
            navigationManager.clearNavigationCategory();
            navigationManager.getCategoryGroupLayout().allCategoryClose();
        }
    }

    public void GetHomeGroupListInfo() {
        GetHomeGroupListInfo(true);
    }

    public void GetHomeGroupListInfo(boolean isNavigationRefresh) {
        // 기 선택된 편성표종류(생방송 or 마이샵) 초기화
        scheduleBroadType = null;
        // TV쇼핑탭 이벤트버스 unregister
        EventBus.getDefault().post(new Events.FlexibleEvent.TvLiveUnregisterEvent());
        new HomeGroupListInfoController(this, isNavigationRefresh).execute();
    }

    private class HomeGroupListInfoController extends BaseAsyncController<HomeGroupInfo> {

        private Context context;
        boolean isNavigationRefresh = true;

        public HomeGroupListInfoController(Context context) {
            super(context);
            isNavigationRefresh = true;
        }

        public HomeGroupListInfoController(Context context, boolean navigationRefresh) {
            super(context);
            this.context = context;
            this.isNavigationRefresh = navigationRefresh;
        }

        @Override
        protected void onPrepare(Object... params) throws Exception {
            //super.onPrepare(params);
            //LoginUrlHandler 통해 자동로그인성공 후 홈으로 이동하는 경우 간혹 BadTokenException 발생하는 경우 있어 체크로직 추가
            if (((Activity) context).hasWindowFocus()) {
                if (this.dialog != null) {
                    //this.dialog.dismiss();
                    this.dialog.setCancelable(false);
                    this.dialog.show();
                }
            }
        }

        @Override
        protected HomeGroupInfo process() throws Exception {
            /**
             *
             * 상황을 보고 태워야 겠음 ;;
             * BaseTabMenuActivity.java 에서 호출되는 경우에는 true 호출하여 네트워크를 태운다.
             */

            return homeGroupInfoAction.getHomeGroupInfo(context, true);
        }

        @Override
        protected void onSuccess(HomeGroupInfo homeGroupInfo) throws Exception {
            if (homeGroupInfo != null && homeGroupInfo.groupSectionList != null) { // 훔구조 데이터 유효성 체크
                // APP / WEB 타입 정의
                MainApplication.isAppView = true;
                //homeGroupInfoAction.cacheHomeGroupInfo(homeGroupInfo); // 홈 API 결과 캐쉬
            } else {
                MainApplication.isAppView = false;
            }

            String action;

            if (MainApplication.isAppView) {
                action = Keys.ACTION.APP_HOME;
            } else {
                action = Keys.ACTION.WEB;
            }

            startHome(action, isNavigationRefresh);
        }

        @Override
        protected void onError(Throwable e) {
            startHome(Keys.ACTION.APP_HOME, isNavigationRefresh);
        }
    }

    /**
     * 홈 화면을 다시 실행한다.
     *
     * @param action              앱 or 웹
     * @param isNavigationRefresh 네비게이션 초기화 여부
     */
    private void startHome(String action, boolean isNavigationRefresh) {
        if (isNavigationRefresh) {
            NavigationManager.isNewHomeGroup = true;
            EventBus.getDefault().post(new Events.NavigationReflashEvent());
        }
        Intent intent = new Intent(action);
        intent.putExtra(Keys.INTENT.WEB_URL, ServerUrls.WEB.HOME);
        intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.HOME);
        intent.putExtra(Keys.INTENT.INTENT_REFRESH_NAVI, isNavigationRefresh);
        //매장갯수 변경 시 HomeActivity onNewIntent 호출 통해 뷰페이저 notifyDataSetChanged 관련 익셉션 제거
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    /**
     * 네비게이션 컨트롤러
     */
    protected class NavigationController extends BaseAsyncController<NavigationResult> {
        private String url;
        private final Context mContext;
        private View error_view;
        private boolean isDialog;

        public NavigationController(Context context, View error_view, boolean isDialog) {
            super(context);
            mContext = context;
            this.error_view = error_view;
            this.isDialog = isDialog;
        }

        @Override
        protected void onPrepare(Object... params) throws Exception {
            if (dialog != null && isDialog) {
                dialog.dismiss();
                dialog.setCancelable(true);
                dialog.show();
            }
            //데이터를 호출할때 카테고리정보를 넘긴다.
            StringBuilder urlString = new StringBuilder();
            String[] ids = new Gson().fromJson(PrefRepositoryNamed.get(MainApplication.getAppContext(),
                    Keys.CACHE.INTEREST_CATEGORY_JSON_STRING, String.class), String[].class);
            urlString.append((String) params[0]);

            if (ids != null) {
                urlString.append("&lcatelist=");
                for (String id : ids) {
                    urlString.append(id).append(",");
                }
            }
            url = urlString.toString();
        }

        @Override
        protected NavigationResult process() throws Exception {
            //네비게이션 백그라운드 이미지를 미리 다운받는다. - 수정되어야 한다. background img? navigation 변경으로 삭제 되어야? hklim
            if (getHomeGroupInfo().leftNavigation.tpBgImg != null && !"".equals(getHomeGroupInfo().leftNavigation.tpBgImg)) {
                String tpBgImgPath = ImageUtil.loadImageCacheUrl(mContext, getHomeGroupInfo().leftNavigation.tpBgImg);
                PrefRepositoryNamed.saveString(mContext, "navigation", "tpBgImgPath", tpBgImgPath);
            } else {
                PrefRepositoryNamed.saveString(mContext, "navigation", "tpBgImgPath", "");
            }
            return (NavigationResult) DataUtil.getData(context, restClient, NavigationResult.class,
                    false, false, url);
        }

        @Override
        protected void onSuccess(final NavigationResult result) throws Exception {
            if (error_view != null) {
                scene_root.setVisibility(View.VISIBLE);
                error_view.setVisibility(View.GONE);
            }
            NavigationManager.cacheData(result);
            if (result != null && result.appCmmCategory != null && result.appCmmCategory.cateContentList != null) {
                setCategoryView(result);
            }
        }

        @Override
        protected void onError(Throwable e) {
            if (error_view != null) {
                scene_root.setVisibility(View.GONE);
                error_view.setVisibility(View.VISIBLE);
            }
            Ln.e(e);

        }

        @Override
        protected void onFinally() {
            super.onFinally();
            if (dialog != null) {
                dialog.dismiss();
            }
            setBasketcnt();
        }
    }


    /**
     * 네비게이션 카테고리를 추가
     *
     * @param result
     */
    protected void setCategoryView(final NavigationResult result) {
        interstCategories = new CateContentList[result.appCmmCategory.cateContentList.size()];
        result.appCmmCategory.cateContentList.toArray(interstCategories);
        navigationManager.addCaterory(result, true);
    }

    public static HomeGroupInfo getHomeGroupInfo() {
        return PrefRepositoryNamed.get(MainApplication.getAppContext(), Keys.CACHE.HOME_GROUP_INFO, HomeGroupInfo.class);
    }

    protected boolean isMyshopBadge() {
        BadgeInfo badge = BadgeInfo.getCachedBadgeInfo();
        if (badge != null) {
            if (badge.myshop) {
                String lastConsumedDate = PrefRepositoryNamed.get(MainApplication.getAppContext(),
                        Keys.PREF.BADGE_MYSHOP_CONSUMED_DATE, String.class);
                if (lastConsumedDate == null || today().compareTo(lastConsumedDate) > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 관심 카테고리 설정 닫기 callback
     */
    public void onCloseCategorySetting() {
        new NavigationController(mContext, error_view, true).execute(getHomeGroupInfo().leftNavigation.personalApiUrl);
    }

    /**
     * 네비게이션 업데이트
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void updateNavigation() {
        if (navigationManager != null && navigationManager.getCategoryGroupLayout() != null && navigationManager.getCategoryGroupLayout().isCategoryRefresh()) {
            navigationManager.displayCategory();
            if (navigationManager.getCategoryGroupLayout() != null) {
                if (NavigationManager.isNewHomeGroup) {
                    NavigationManager.isNewHomeGroup = false;
                    NavigationManager.currentCategoryName = null;
                }
                navigationManager.getCategoryGroupLayout().updateCategory();
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (scene_root != null) {
                scene_root.setScrollY(NavigationManager.getCurrentScrollPosition());
            }
        }

        if (NavigationManager.isNavigationLogin) {
            NavigationManager.isNavigationLogin = false;
            new NavigationController(mContext, error_view, false).execute(getHomeGroupInfo().leftNavigation.personalApiUrl);
        }
    }

    public void hideMyshopBadge() {
        BadgeInfo badge = BadgeInfo.getCachedBadgeInfo();
        if (badge == null) {
            return;
        }

        // 최종 확인일자를 설정에 저장.
        if (badge.myshop) {
            PrefRepositoryNamed.save(MainApplication.getAppContext(), Keys.PREF.BADGE_MYSHOP_CONSUMED_DATE, today());
        }
        badge.myshop = false;
    }

    private String today() {
        return (String) android.text.format.DateFormat.format("yyyyMMdd", new java.util.Date());
    }

    /**
     * 와이즈로그 전송 (HttpClient사용)
     *
     * @param url 호출주소
     */
    public void setWiseLogHttpClient(String url) {
        wiseLog.setWiseLogHttpClient(url);

    }

    /**
     * 와이즈로그 전송 (restClient사용)
     *
     * @param url 호출주소
     */
    public void setWiseLogRest(String url) {
        wiseLog.setWiseLog(url);
    }

    /**
     * 네비게이션 DrawerLayout 리턴
     */
    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

    public void openNavigationView(boolean isAnimation) {
        if (navigationManager != null) {
            navigationManager.loadSavedPosition();
        }
//        for (DrawerLayoutHorizontalSupport tempDrawer : mArrDrawerLayout) {
        // Drawerlayout 들을 모두 닫는다.
        for (int i = 0; i < mArrDrawerLayout.size(); i++) {

            if (drawerLayout != null && !drawerLayout.equals(mArrDrawerLayout.get(i))) {
                mArrDrawerLayout.get(i).closeDrawers();
//                mArrDrawerLayout.remove(i);
            }
        }
        // 마지막 DrawerLayout은 남아야 하기 때문에 clear후 add
        mArrDrawerLayout.clear();
        mArrDrawerLayout.add(drawerLayout);
        drawerLayout.openDrawer(navigation_view, isAnimation);
    }

    public void closeNavigationView() {
        // 클로스 할때에도 스크롤 포지션을 지정.
        // pause 했다가 들어올때 뿐 아니라 새로 고침시에 기존에 기억되고 있는 위치를 지정해야하기 때문.
        saveNavigationPosition();
        if (isNotEmpty(drawerLayout)) {
            drawerLayout.closeDrawer(navigation_view);
        }
    }

    public void saveNavigationPosition() {
        if (scene_root != null) {
            NavigationManager.setCurrentScrollPosition(scene_root.getScrollY());
        }
        if (navigationManager != null) {
            navigationManager.saveGSXPosition();
            navigationManager.savePartnersPosition();
            navigationManager.saveThemePosition();
        }
    }

    /**
     * 네비게이션 뷰 리턴
     */
    public View getNavigationView() {
        return navigation_view;
    }

    /**
     * 설정 화면으로 이동
     */
    private void goSetting() {
        goToClass(SettingActivity.class, TabMenu.MYSHOP, false);
    }

    /**
     * 로그인 화면으로 이동
     */
    private void goLogin() {
        goToClass(LoginActivity.class, -1, true);
    }

    /**
     * 네비게이션에서 각 화면으로 이동 할 때 사용.
     *
     * @param clazz
     * @param tabMenu
     * @param isShowFromBottom
     */
    private void goToClass(final Class<?> clazz, final int tabMenu, final boolean isShowFromBottom) {
        try {
            if (drawerLayout != null && drawerLayout.isDrawerOpen(navigation_view)) {
                saveNavigationPosition();
            }
            Intent intent = new Intent(AbstractBaseActivity.this, clazz);
            intent.putExtra(Keys.INTENT.INTENT_FROM_BOTTOM, isShowFromBottom);
//                        intent.putExtra(Keys.INTENT.LEFT_NAVIGATION, true);
            if (tabMenu > 0) {
                intent.putExtra(Keys.INTENT.TAB_MENU, tabMenu);
            }
            startActivity(intent);
        } catch (ClassCastException e) {
            Ln.e(e.getMessage());
        } catch (NullPointerException e) {
            Ln.e(e.getMessage());
        }
    }

    /**
     * 로그인 완료시 토스트 메시지를 띄운다.
     *
     * @param event LoggedInToastEvent
     */
    public void onEvent(Events.LoggedInToastEvent event) {
        if (!isShowToast) {
            //액티비티들 중 하나의 액티비티에서만 토스트 메시지 띄우도록 함
            //이렇게
            isShowToast = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isShowToast = false;
                }
            }, 3000);

            Toast.makeText(getApplicationContext(), event.msg, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 비밀번호 변경 팝업을 노출한다.
     *
     * @param event LoggedInUpdatePassEvent
     */
    public final void onEventMainThread(final Events.LoggedInUpdatePassEvent event) {
        if (isShowUpdatePass) {
            //액티비티들 중 하나의 액티비티에서만 팝업 띄우도록 함
            isShowUpdatePass = false;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isShowUpdatePass = true;
                }
            }, 5000);

            String encCustNo = null;
            User user = User.getCachedUser();
            if (user != null) {
                if (event.isTvMember) {
                    encCustNo = EncryptUtils.encrypt(user.customerNumber + "_TV_UP", "SHA-256");
                } else {
                    encCustNo = EncryptUtils.encrypt(user.customerNumber + "_UP", "SHA-256");
                }
            }
            String day = PrefRepositoryNamed.get(MainApplication.getAppContext(), encCustNo, String.class);
            // 비밀번호 변경 일자가 180일로 바뀌었지만 기존 90일 일때에도 30일로 확인을 진행함. 기존과 동일하게 해당 부분의 로직은 변경하지 않음 - hklim -
            if (day == null || DateUtils.getDifferenceDays(day, DateUtils.getToday("yyyyMMdd")) >= DATE_PWD_LAST_CHANED) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(Keys.ACTION.UPDATE_PASS);
                        intent.putExtra(Keys.INTENT.WEB_URL, event.url);
                        intent.putExtra(Keys.INTENT.ARS_MEMBER_VALUE, event.isTvMember);
                        startActivity(intent);
                    }
                }, 3000);
            }
        }

    }

    /**
     * 지문인식 연동 확인 팝업 이벤트
     *
     * @param event PopupFingerPrintEvent
     */
    public final void onEventMainThread(final Events.PopupFingerPrintEvent event) {
        if (isShowCheckFP) {
            //액티비티들 중 하나의 액티비티에서만 팝업 띄우도록 함
            isShowCheckFP = false;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isShowCheckFP = true;
                }
            }, 5000);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Keys.ACTION.ACTION_CHECK_FP);
                    intent.putExtra(Keys.INTENT.INTENT_IS_SHOW_CHECK_FP, event.isOn);
                    startActivity(intent);
                }
            }, 3000);
        }
    }

    /**
     * 기본 팝업 이벤트
     *
     * @param event BasicPopupEvent
     */
    public final void onEventMainThread(final Events.BasicPopupEvent event) {
        if (isShowBasic) {
            //액티비티들 중 하나의 액티비티에서만 팝업 띄우도록 함
            isShowBasic = false;
            int delayMillis = event.delayMillis;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isShowBasic = true;
                }
            }, delayMillis + 2000);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Keys.ACTION.ACTION_POPUP_BASIC);
                    final Bundle bundle = new Bundle();
                    intent.putExtra(Keys.INTENT.INTENT_POPUP_BASIC, event);
                    startActivity(intent);
                }
            }, delayMillis);
        }
    }

    /**
     * 모바일라이브 방송알림 클릭 콜백, HomeActivity뿐 아니라 AbstractBaseActivity를 상속받는 녀석들에서 호출되게끔. AbstractBaseActivity로 옮김.
     */
    public void onMLRegister(String caller, String type, String prdId, String prdName, boolean isNightAlarm) {
        EventBus.getDefault().post(new Events.AlarmRegistMLEvent(caller, type, prdId, prdName, mCurrentNavigationId, isNightAlarm));
    }

    /**
     * M 버전이 아닐 경우에만 볼륨 하단버튼을 통해 SM21, TM14, M, AM 서버를 변경할 수 있도록 만듬.
     * 2020-01-07 yun.
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!BuildConfig.FLAVOR_stage.equalsIgnoreCase("m")) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                // 팝업 화면 구성
                View serverSettingDialogView = getLayoutInflater().inflate(R.layout.popup_server_setting, null);
                final RadioGroup rgServer = serverSettingDialogView.findViewById(R.id.rg_server);
                final RadioButton rbM = serverSettingDialogView.findViewById(R.id.rb_m);
                final RadioButton rbSm21 = serverSettingDialogView.findViewById(R.id.rb_sm21);
                final RadioButton rbTm14 = serverSettingDialogView.findViewById(R.id.rb_tm14);
                final RadioButton rbAtm = serverSettingDialogView.findViewById(R.id.rb_atm);
                final RadioButton rbAm = serverSettingDialogView.findViewById(R.id.rb_am);
                final EditText etNavigationVer = serverSettingDialogView.findViewById(R.id.et_navigation_ver);
                final EditText etPcid = serverSettingDialogView.findViewById(R.id.et_pcid);
                final CheckBox chkPcid = serverSettingDialogView.findViewById(R.id.chk_pcid);
                final Button btnOk = serverSettingDialogView.findViewById(R.id.btn_ok);
                final Button btnCancel = serverSettingDialogView.findViewById(R.id.btn_cancel);

                if (TextUtils.isEmpty(mServerType)) {
                    mServerType = BuildConfig.FLAVOR_stage;
                }

                // 팝업에 라디오버튼 기본 선택 값
                switch (mServerType) {
                    case "m":
                        rbM.setChecked(true);
                        break;
                    case "asm":
                    default:
                        rbSm21.setChecked(true);
                        break;
                    case "atm":
                        rbAtm.setChecked(true);
                        break;
                    case "tm14":
                        rbTm14.setChecked(true);
                        break;
                    case "am":
                        rbAm.setChecked(true);
                }

                // 네비게이션 버전 셋팅.
                etNavigationVer.setText(ServerUrls.REST.getNavigationVer());

                etPcid.setText(CookieUtils.getWaPcId(AbstractBaseActivity.this));

                // M, SM21, TM14 라디오버튼 리스너
                rgServer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId) {
                            case R.id.rb_m:
                                mServerType = "m";
                                ServerUrls.TEMP_WEB_ROOT = "http://m.gsshop.com";
                                ServerUrls.TEMP_HTTP_ROOT = "http://m.gsshop.com";
                                ServerUrls.TEMP_HTTPS_ROOT = "https://m.gsshop.com";
                                ServerUrls.CSP_SERVICE_ROOT = "https://csp.gsshop.com";
                                break;
                            case R.id.rb_sm21:
                                mServerType = "asm";
                                ServerUrls.TEMP_WEB_ROOT = "http://asm.gsshop.com";
                                ServerUrls.TEMP_HTTP_ROOT = "http://asm.gsshop.com";
                                ServerUrls.TEMP_HTTPS_ROOT = "https://asm.gsshop.com";
                                ServerUrls.CSP_SERVICE_ROOT = "https://csp.gsshop.com";
                                break;
                            case R.id.rb_atm:
                                mServerType = "atm";
                                ServerUrls.TEMP_WEB_ROOT = "http://atm.gsshop.com";
                                ServerUrls.TEMP_HTTP_ROOT = "http://atm.gsshop.com";
                                ServerUrls.TEMP_HTTPS_ROOT = "https://atm.gsshop.com";
                                ServerUrls.CSP_SERVICE_ROOT = "https://cspdev.innolab.us";
                                break;
                            case R.id.rb_tm14:
                                mServerType = "tm14";
                                ServerUrls.TEMP_WEB_ROOT = "http://tm14.gsshop.com";
                                ServerUrls.TEMP_HTTP_ROOT = "http://tm14.gsshop.com";
                                ServerUrls.TEMP_HTTPS_ROOT = "https://tm14.gsshop.com";
                                ServerUrls.CSP_SERVICE_ROOT = "https://cspdev.innolab.us";
                                break;
                            case R.id.rb_am:
                                mServerType = "am";
                                ServerUrls.TEMP_WEB_ROOT = "http://asm.gsshop.com";
                                ServerUrls.TEMP_HTTP_ROOT = "http://asm.gsshop.com";
                                ServerUrls.TEMP_HTTPS_ROOT = "https://asm.gsshop.com";
                                ServerUrls.CSP_SERVICE_ROOT = "https://csp.gsshop.com";
                        }
                    }
                });

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(serverSettingDialogView);

                AlertDialog alertDialog = builder.create();

                btnOk.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ServerUrls.REST.TEMP_NAVIGATION_VER = etNavigationVer.getText().toString().trim();
                        PrefRepositoryNamed.saveString(AbstractBaseActivity.this, Keys.PREF.PREF_NAME_TEST_SERVER, Keys.PREF.PREF_TEST_SERVER, mServerType);
                        PrefRepositoryNamed.save(MainApplication.getAppContext(), Keys.PREF.PREF_TEST_SERVER, mServerType);
                        String strPcid = etPcid.getText().toString();
                        if (!TextUtils.isEmpty(strPcid) && chkPcid.isChecked()) {
                            CookieUtils.setWaPcidToWebView(AbstractBaseActivity.this, restClient, strPcid);
                        }
                        // 이하 소스는 HomeActivity 에 있는 GS SHOP 로고 클릭하여 refresh 하는 소스와 동일.
                        // getHomeGroupListInfo() 내부에서 위에 셋팅된 서버 주소로 재접속.
                        //캐시 삭제
                        clearCacheWithDelay();
                        GetHomeGroupListInfo();
                        //숏방데이타 초기화
                        CategoryDataHolder.putCategoryData(null);
                        new UserAction(getApplicationContext()).logout(true);
                        Toast.makeText(AbstractBaseActivity.this, "server type : " + mServerType + "\n"
                                        + "navigation ver : " + ServerUrls.REST.getNavigationVer()
                                        + "\n원활한 TEST 를 위해 재로그인 해주세요.",
                                Toast.LENGTH_LONG).show();
                        alertDialog.dismiss();
                    }
                });

                btnCancel.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
