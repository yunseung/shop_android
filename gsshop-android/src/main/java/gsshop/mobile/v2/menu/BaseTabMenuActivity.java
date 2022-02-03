/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.menu;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.inject.Inject;
import com.gsshop.mocha.pattern.chain.Command;
import com.gsshop.mocha.pattern.chain.CommandExecutor;
import com.gsshop.mocha.ui.back.BackKeyHandler;

import org.apache.http.NameValuePair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;
import gsshop.mobile.v2.AbstractBaseActivity;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.Keys.ACTION;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.attach.FileAttachConnector;
import gsshop.mobile.v2.attach.FileAttachInfoV2;
import gsshop.mobile.v2.attach.FileAttachUtils;
import gsshop.mobile.v2.attach.FileUploadController;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.shop.nalbang.CategoryDataHolder;
import gsshop.mobile.v2.home.util.crop.Crop;
import gsshop.mobile.v2.intro.DummyCommand;
import gsshop.mobile.v2.intro.FacebookCommand;
import gsshop.mobile.v2.intro.GetAdvIDCommand;
import gsshop.mobile.v2.menu.navigation.InterestCategorySettingDialogFragment;
import gsshop.mobile.v2.search.HomeSearchActivity;
import gsshop.mobile.v2.setting.SettingActivity;
import gsshop.mobile.v2.support.badge.BadgeAction;
import gsshop.mobile.v2.support.badge.BadgeBuilder;
import gsshop.mobile.v2.user.LoginOption;
import gsshop.mobile.v2.user.TokenCredentialsNew2;
import gsshop.mobile.v2.user.User;
import gsshop.mobile.v2.user.UserAction;
import gsshop.mobile.v2.user.UserConnector;
import gsshop.mobile.v2.util.ClickUtils;
import gsshop.mobile.v2.util.CookieUtils;
import gsshop.mobile.v2.util.LunaUtils;
import gsshop.mobile.v2.util.PermissionUtils;
import gsshop.mobile.v2.util.ThreadUtils;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isEmpty;
import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;
import static gsshop.mobile.v2.MainApplication.autoLoginTryCount;
import static gsshop.mobile.v2.MainApplication.autoLoginTryEnabled;
import static gsshop.mobile.v2.MainApplication.autoLoginTryLimit;
import static gsshop.mobile.v2.ServerUrls.LAST_PRD_IMAGE;
import static gsshop.mobile.v2.home.HomeActivity.psnlCurationType;
import static gsshop.mobile.v2.home.HomeActivity.showPsnlCuration;
import static gsshop.mobile.v2.util.LunaUtils.autoLoginState;
import static gsshop.mobile.v2.util.SSOUtils.hasSSOToken;
import static gsshop.mobile.v2.util.StringUtils.trim;

/**
 * 추상 탭메뉴 액티비티.
 *
 * 어떤 탭메뉴 버튼을 선택상태로 만들지 결정하기 위해
 * Intent에 Keys.INTENT.TAB_MENU/TabMenu.TAB_MENU_XXX 값을
 * key/value 쌍으로 전달해야 한다.
 *
 * NOTE : TabActivity를 사용하지 않은 이유
 * ={@literal >} TabActivity를 사용하면 탭메뉴를 쉽게 구성할 수 있으나
 * ActivityGroup의 특성상 sub activity 관리가
 * 복잡하고 부자연스러운 부분이 많아 개별 액티비티별로
 * 탭메뉴를 각각 구성하는 방식을 적용하였다.
 * 그러나 탭메뉴를 별도 구성하는 방식도 탭메뉴 이동시마다
 * 메뉴를 다시 구성하거나 선택된 메뉴 정보를 전달해주어야
 * 하는 등의 불편한 점이 존재한다.
 *
 */
public abstract class BaseTabMenuActivity extends AbstractBaseActivity implements InterestCategorySettingDialogFragment.OnInterestCategorySettingListener {
    private static final String TAG = "BaseTabMenuActivity";

    protected BackKeyHandler exitHandler;

    protected View[] tabMenuButtons = new View[TabMenu.maxTabIndex];

    protected BadgeBuilder badgeBuilder;

    @Inject
    protected BadgeAction badgeAction;

    public String publicKey = "";

    @Inject
    public Context context;

    @Inject
    private UserAction userAction;

    /**
     * 최근 본 상품 디폴트 아이콘 표시 영역
     */
    private ImageView lastPrdDefault;

    /**
     * 최근 본 상품 상품이미지 표시 영역
     */
    private FrameLayout lastPrdCircle;

    /**
     * 최근 본 상품 상품이미지 표시용 이미지뷰
     */
    private CircleImageView imgTabLastPrd;

    private Activity mActivity;

    //홈화면 띄우면서 선택할 매장 섹션코드
    protected String TARGET_SECTION_CODE = "";

    //오늘추천 섹션코드
    protected static final String TODAY_RECOMMEND_SECTION_CODE = "#396185";

    //숏방 섹션코드
    protected static final String SHORTBANG_SECTION_CODE = "#A00270";

    //TV편성표 네비게이션 아이디
    public static final String TV_SCHEDULE_NAVI_ID = "323";
    //모바일라이브 탭매장 신설 네비게이션 아이디
    public static final String MOBILE_LIVE_NAVI_ID = "577";

    //최근 본 상품 쿠키이름
    private static final String COOKIE_LAST_PRD_KEY = "lastprdid";
    //최근 본 상품 쿠키스트링 구분자
    private static final String COOKIE_LAST_PRD_SEPERATOR = "%25";
    //최근 본 상품 이미지 TAIL
    private static final String LAST_PRD_IMG_TAIL = "_V1.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        //exitHandler = new ConfirmExitBackHandler(this);
        exitHandler = new ExitTwiceBackHandler(this);
        RunningActivityManager.addActivity(this);

        // 시스템에 의해 앱 프로세스가 강제로 종료된 후에 재시작된 경우
        boolean restartedAfterKilledBySystem = (savedInstanceState != null
                && !MainApplication.introCompleted);
        if (restartedAfterKilledBySystem) {
            restoreStateAfterKilledBySystem(savedInstanceState);
            Ln.i("시스템에 의해 앱 프로세스가 강제로 종료되어 일부 앱 상태를 복구하였습니다.");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        setBasketcnt();
        setLastPrdImage(true);
        // myshop bage
        if (badgeBuilder != null) {
            badgeBuilder.buildBadges();
        }
    }

    /**
     * 메모리가 부족해 OS에 의해 앱 프로세스가 강제로 종료된 이후
     * 앱을 재실행시 이전 상태 복구 작업.
     *
     * TODO : IntroActivity부터 새로 시작하는 것도 방법. 그러나 하위 액티비티의 onCreate()에서
     * super.onCreate() 이후의 나머지 코드가 실행되지 않게 적절히 return해줘야 함.
     * NOTE : 예를들어 IntroRequiredException을 던져서 ExceptionHandler에서 IntroActivity를
     * 시작하는 것도 시도해봤으나 Exception처리 애스펙트가 에러를 먹어버려 super.onCreate() 이후의
     * 코드가 실행되는 것을 막지 못하는 문제가 있었음. 별도의 애스펙트 정의도 고려해볼만함.
     * @param savedInstanceState The savedInstanceState.
     */
    protected void restoreStateAfterKilledBySystem(Bundle savedInstanceState) {
        // OutOfMemoryError가 발생한 경우 백그라운드로 로그인을 수행한다.
        // 인트로에서 수행하는 커맨드도 함께 초기화한다.
        User user = User.getCachedUser();
        if (user != null) {
            doAutoLogin();
            List<Class<? extends Command>> commands = new ArrayList<Class<? extends Command>>();
            commands.add(FacebookCommand.class);
            commands.add(GetAdvIDCommand.class);
            commands.add(DummyCommand.class);
            CommandExecutor commandExecutor = new CommandExecutor(this, commands);
            commandExecutor.execute();
        }
    }

    /**
     * 자동로그인을 수행한다.
     */
    private void doAutoLogin() {
        ThreadUtils.INSTANCE.runInBackground(() -> {
            TokenCredentialsNew2 tokenCredentials = TokenCredentialsNew2.get();
            if ((tokenCredentials != null && tokenCredentials.authToken != null)
                    || hasSSOToken()) {
                UserConnector.LoginResult result = null;
                try {
                    result = userAction.auth(tokenCredentials);
                } catch (Exception e) {
                    Ln.e(e);
                    return;
                }
                if(isNotEmpty(result) && !result.isSuccs()){
                    alertLoginFailed();
                }
            }
        });
    }

    private void alertLoginFailed() {
        ThreadUtils.INSTANCE.runInUiThread(() -> Toast.makeText(context, R.string.login_intro_fail, Toast.LENGTH_LONG).show());
    }

    /**
     * 탭메뉴를 통해 불려진 액티비티이면 {@link #exitHandler}를 통해 처리하고
     * 아니면 그냥 액티비티를 종료한다.
     */
    @Override
    public void onBackPressed() {
        if (getDrawerLayout() != null && getDrawerLayout().isDrawerOpen(getNavigationView())) {
            closeNavigationView();
            return;
        }
        //앱의 포그라운드/백그라운드 전환에 사용되는 변수
        AbstractBaseActivity.isBackPressed = true;

        //앱이 실행되지 않은 상태에서 외부로부터 호출된 액티비티(푸시, 카카오링크 등)는 백버튼 클릭시
        //이동할 곳이 없으므로 메인화면으로 이동하도록 처리함
        //isTaskRoot 조건추가: 스택상 나보다 아래에 액티비티가 없는 경우에도 백키 클릭시 홈 띄움
        //-스키마(광고 등) > 이벤트웹뷰 > 단품 > 백키 > 백키 클릭시 홈이 안뜨고 앱종료 되는 현상 개선
        if (getIntent().getBooleanExtra(Keys.INTENT.BACK_TO_MAIN, false)
                || isTaskRoot()) {
            Intent intent = new Intent(Keys.ACTION.APP_HOME);
            intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.HOME);
            intent.putExtra(Keys.INTENT.FROM_TAB_MENU, true);
            intent.putExtra(Keys.INTENT.SECTION_CODE, TARGET_SECTION_CODE);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            TARGET_SECTION_CODE = "";
        }

        finish();
    }

    /**
     * 탭메뉴를 구성하고 Intent를 통해 전달된 특정 탭메뉴 버튼을 선택된 상태로 설정한다.
     *
     * TODO setupTabMenu()를 왜 onCreate()가 아닌 onContentChanged()에서 호출하는가?
     */
    @Override
    public void onContentChanged() {
        super.onContentChanged();

        if ( !(context instanceof HomeSearchActivity) )
        {
            setupTabMenu();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(badgeAction);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(badgeAction);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (exitHandler != null) {
            exitHandler.destroy();
        }

        RunningActivityManager.removeActivity(this);
        super.onDestroy();
    }

    /**
     * 뱃지 정보가 갱신되면 뱃지UI를 새로 그림.
     * @param event event
     */
    public final void onEventMainThread(Events.BadgeInfoFlushedEvent event) {
        if( badgeBuilder != null )
        {
            badgeBuilder.buildBadges();
        }
    }

    /**
     * 최근 본 상품 이미지를 다시 로드한다.
     *
     * @param event event
     */
    public final void onEventMainThread(Events.LastPrdUpdateEvent event) {
        setLastPrdImage(false);
    }

    /**
     * 지정된 탭이 셀렉트 되어 있는지 확인
     * @param tabMenu tabMenu
     * @return boolean
     */
    protected boolean isSelectedTabFocus(int tabMenu)
    {
        if( tabMenuButtons != null &&  tabMenu > TabMenu.tabStartIndex && tabMenu < TabMenu.maxTabIndex)
        {
            if( tabMenuButtons[tabMenu] != null )
            {
                if ( tabMenuButtons[tabMenu].isSelected())
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 지정된 탭으로 조정하라
     * @param tabMenu tabMenu
     */
    protected void setTabAdjustment(int tabMenu) {

        if( tabMenuButtons != null )
        {
            //전부 해제
            tabMenuButtons[TabMenu.HOME].setSelected(false);
            tabMenuButtons[TabMenu.CATEGORY].setSelected(false);
            //tabMenuButtons[TabMenu.SEARCH].setSelected(false);
            tabMenuButtons[TabMenu.MYSHOP].setSelected(false);
            tabMenuButtons[TabMenu.ZZIMPRD].setSelected(false);
            tabMenuButtons[TabMenu.LASTPRD].setSelected(false);
            if( tabMenu > TabMenu.tabStartIndex && tabMenu < TabMenu.maxTabIndex)
            {
                if( tabMenuButtons[tabMenu] != null )
                {
                    // TODO: 2016. 12. 26.
                    // 이것만 막아도 하일라이트가 안되려나? 이민수
                    //tabMenuButtons[tabMenu].setSelected(true);
                }
                else
                {
                    //기본은 홈
                    //tabMenuButtons[TabMenu.HOME].setSelected(true);
                }
            }
            else
            {
                //기본은 홈
                //tabMenuButtons[TabMenu.HOME].setSelected(true);
            }
        }
    }

    /**
     * Intent를 통해 전달된 특정 탭메뉴 버튼을 선택된 상태로 설정.
     */
    private void setupTabMenu() {

        lastPrdDefault = (ImageView) findViewById(R.id.last_prd_default);
        lastPrdCircle = (FrameLayout) findViewById(R.id.last_prd_circle);
        imgTabLastPrd = (CircleImageView) findViewById(R.id.iv_tab_last_prd);

//        BadgeTextView myshopBadge = (BadgeTextView) findViewById(R.id.txt_myshop_badge);
        View myshopBadge = findViewById(R.id.img_myshop_badge);
        badgeBuilder = new BadgeBuilder(myshopBadge, this);
        badgeBuilder.buildBadges();

        //빈화면 클릭시 상품이 클릭되는현상을 방지하기 위해 해당코드 추가
        View tabMenu = findViewById(R.id.layout_tab_menu);
        if(tabMenu != null) {
            findViewById(R.id.layout_tab_menu).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
        tabMenuButtons[TabMenu.HOME] = findViewById(R.id.btn_tab_home);
        tabMenuButtons[TabMenu.CATEGORY] = findViewById(R.id.btn_tab_category);
        tabMenuButtons[TabMenu.MYSHOP] = findViewById(R.id.btn_tab_myshop);
        tabMenuButtons[TabMenu.ZZIMPRD] = findViewById(R.id.btn_tab_zzim_prd);
        tabMenuButtons[TabMenu.LASTPRD] = findViewById(R.id.btn_tab_last_prd);

        int tabMenuId = TabMenu.getTabMenu(getIntent());
        //tabMenuButtons[tabMenuId].setSelected(true);
        tabMenuButtons[TabMenu.HOME].setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtils.work(3000)) {
                    return;
                }
                if(isSwipeRefreshing()){
                    return;
                }

                setWiseLogHttpClient(ServerUrls.WEB.TAB_HOME);

                //숏방데이타 초기화
                CategoryDataHolder.putCategoryData(null);

                //toggleTabMenu(v);

                //홈버튼은 여러 액티비티가 사용한는 공통영역이므로 Hyper-Pesonalized Curation 대상인 경우만
                //노출 플래그 세팅함 (현재 요건은 장바구니에서 홈이동시에만)
                if (isPsnlCurationTarget) {
                    showPsnlCuration = true;
                    psnlCurationType = HomeActivity.PsnlCurationType.CART;
                }

                GetHomeGroupListInfo(false);

                //Hyper Personalized Curation(DT과제)
                //백키로 돌아왔을때 -> 지연없이 이벤트 보내고
                //홈버튼 돌아왔을때 -> 2초 지연후 이벤트 보내는
                //구분용 플래그
                MainApplication.calledFromBackKey = false;
            }
        });


        tabMenuButtons[TabMenu.CATEGORY].setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ACTION.WEB);
                intent.putExtra(Keys.INTENT.WEB_URL, ServerUrls.WEB.BOTTOM_CATEGORY_TAB);
                intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.CATEGORY);
                startActivity(intent);

                //20200728 네이티브 효율 주석
                //setWiseLogHttpClient(ServerUrls.WEB.TAB_NAVI);

                //20200728 네이티브 호출 부분 주석
                //callLeftNavigation();
            }
        });

        tabMenuButtons[TabMenu.MYSHOP].setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                badgeBuilder.hideMyshopBadge(true);

                Intent intent = new Intent(ACTION.WEB);
                intent.putExtra(Keys.INTENT.WEB_URL, ServerUrls.WEB.MY_SHOP_TAB);
                intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.MYSHOP);
                startActivity(intent);

                //마이샵을 전용 엑티비티에서 제외
                //Intent intent = new Intent(ACTION.MY_SHOP_WEB);
                //intent.putExtra(Keys.INTENT.WEB_URL, ServerUrls.WEB.MY_SHOP_TAB);
                //intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.MYSHOP);
                //내가 마이샵인 경우 FLAG_ACTIVITY_SINGLE_TOP
                //if(context instanceof MyShopWebActivity) {
                //    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                //}// 내가 마이샵이 아닌 경우 FLAG_ACTIVITY_CLEAR_TOP
                //else {
                //    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //}
                //startActivity(intent);

                //로그인 성공시 캐쉬 처리
                //(개인화 데이터가 갱신되도록 5분 캐쉬는 유지
                // 상하단 리플래쉬는 과거 개인화탭(디폴트/홈) 네비게이션이였다.
                MainApplication.clearCache();
            }
        });

        tabMenuButtons[TabMenu.ZZIMPRD].setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // ACTION.WEB 디폴트 웹뷰 속성을 메네페스트에서 singleTop 로 변경
                Intent intent = new Intent(ACTION.WEB);
                intent.putExtra(Keys.INTENT.WEB_URL, ServerUrls.WEB.ZZIM_PRD);
                intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.ZZIMPRD);
                startActivity(intent);
            }
        });

        tabMenuButtons[TabMenu.LASTPRD].setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // ACTION.WEB 디폴트 웹뷰 속성을 메네페스트에서 singleTop 로 변경
                // 웹뷰에서 로딩할 url에 랜덤값 파라미터를 추가한다. + 최근본상품에 랜덤값이 들어가야 처음과 나중을 서버에서 구분할수 있다
                Intent intent = new Intent(ACTION.WEB);
                intent.putExtra(Keys.INTENT.WEB_URL, Uri.parse(ServerUrls.WEB.LAST_PRD).buildUpon().
                        appendQueryParameter("_", String.valueOf(System.currentTimeMillis())).build().toString());
                intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.LASTPRD);
                startActivity(intent);
            }
        });
    }

    /**
     * 앱구동시 자동로그인이 실패하여 재시도가 필요한 대상인지 확인한다.
     *
     * @return if true, 재시도 대상
     */
    public boolean isAutoLoginTarget() {
        //인트로에서 실패 후 바로 시도 스킵. 홈 구동 3초 후 enable 됨)
        //RE_TRYING 상태는 시도 허용
        if (autoLoginState != LunaUtils.AutoLoginState.RE_TRYING) {
            if (!autoLoginTryEnabled) {
                Ln.i("### autoLoginTryEnabled is false");
                return false;
            }
        }

        //수행횟수에 제한을 둠
        if (autoLoginTryCount >= autoLoginTryLimit) {
            Ln.i("### over autoLoginTryLimit : " + autoLoginTryCount);
            return false;
        }

        //ssoToken 값이 존재하면 keepLogin, tokenCredentials 등 값에 관계없이 무조건 자동로그인 수행 대상
        if (hasSSOToken()) {
            Ln.i("### has ssoToken");
            return true;
        }

        //로그인화면에서 자동로그인 체크하지 않은 경우 스킵
        LoginOption option = LoginOption.get();
        if (isEmpty(option) || !option.keepLogin) {
            Ln.i("### option == null || !option.keepLogin");
            return false;
        }

        // AutoLogin 실패 자 뿐 아니라 성공자도 login 요청이 오면 재로그인 실행 토록 수정. 이에 해당 조건 삭제.
        // 로그인된 상태면 스킵
//        User user = User.getCachedUser();
//        if (isNotEmpty(user) && isNotEmpty(user.customerNumber)) {
//            Ln.i("### user != null && user.customerNumber.length() >= 2");
//            return false;
//        }

        //토큰정보 없는 경우 스킵
        TokenCredentialsNew2 tokenCredentials = TokenCredentialsNew2.get();
        if (isEmpty(tokenCredentials) || isEmpty(tokenCredentials.authToken)) {
            Ln.i("### tokenCredentials == null || tokenCredentials.authToken == null");
            return false;
        }

        return true;
    }

    /**
     * 자동로그인을 수행한다.
     *
     * @param moveToMain if true, 홈으로 이동
     */
    public void doAutoLoginWhenNetworkActivated(boolean moveToMain) {
        ThreadUtils.INSTANCE.runInBackground(() -> {
            Ln.i("### doAutoLoginWhenNetworkActivated");

            //웹뷰 리다이렉트 등의 페이지는 짧은 시간내 중복호출이 들어옴. 이경우 무시.
            if (ClickUtils.work3(3000)) {
                Ln.i("### skip duplicate call");
                return;
            }

            if (!isAutoLoginTarget()) {
                return;
            }

            autoLoginTryCount++;

            UserConnector.LoginResult result = null;
            try {
                result = userAction.auth(TokenCredentialsNew2.get());
            } catch (Exception e) {
                //자동로그인 재시도 실패한 경우 별도 노티는 하지 않음
                Ln.e(e);
            }

            //CUSTOM_TRY 경우만 이벤트 전달
            if (moveToMain) {
                if (isNotEmpty(result) && result.isSuccs()) {
                    Ln.i("### doAutoLoginWhenNetworkActivated > success");
                    EventBus.getDefault().post(new Events.AutoLoggedInEvent(true));
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            GetHomeGroupListInfo();
                        }
                    }, 0);
                } else {
                    Ln.i("### doAutoLoginWhenNetworkActivated > error");
                    EventBus.getDefault().post(new Events.AutoLoggedInEvent(false));
                }
            }
        });
    }

    public boolean isSwipeRefreshing(){
        return false;
    }

    /**
     * 최근 본 상품의 존재 여부에 따라 뷰를 노출/숨김 처리한다.
     *
     * @param existLastPrd 최근본상품이 존재하는 경우 true
     */
    private void setLastPrdVisible(boolean existLastPrd) {
        if (existLastPrd) {
            lastPrdDefault.setVisibility(View.GONE);
            lastPrdCircle.setVisibility(View.VISIBLE);
        } else {
            lastPrdDefault.setVisibility(View.VISIBLE);
            lastPrdCircle.setVisibility(View.GONE);
        }
    }

    /**
     * 하단 푸터 "앱알림 설정" 탭시 설정화면으로 이동
     */
    public void goSetting() {
        Intent intent = new Intent(this, SettingActivity.class);
        intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.MYSHOP);
        startActivity(intent);
    }

    /**
     * 하단탭바에 최근 본 상품 이미지를 노출한다.
     */
    private void setLastPrdImage(boolean resumeFlag) {
        //하단탭 레이아웃을 include 하지 않은 액티비티에서는 수행안함
        if (lastPrdDefault == null || lastPrdCircle == null|| imgTabLastPrd == null) {
            return;
        }

        String lastPrdId = getLastPrdIdFromCookie();

        if(!resumeFlag){
            MainApplication.lastPrdId = lastPrdId;
        }

        //lastPrdId에서 이미지 패스를 추출하기 위한 최소길이는 4
        if (TextUtils.isEmpty(lastPrdId) || lastPrdId.length() < 4) {
            //최근 본 상품정보가 유효한 값이 아니면 디폴트 이미지 노출
            setLastPrdVisible(false);
            return;
        }
        //이미지 추출시 3뎁스는 상품코드가 8, 11자일 경우만 나머지는 다 2뎁스.
        String firstDir;
        String secondDir;
        String thirdDir;
        String lastPrdImgUrl ;
        if(lastPrdId.length() == 8 || lastPrdId.length() == 11)
        {
            firstDir = lastPrdId.substring(0, 2);
            secondDir = lastPrdId.substring(2, 4);
            thirdDir = lastPrdId.substring(4, 6);
            lastPrdImgUrl = String.format("%s/%s/%s/%s/%s%s", LAST_PRD_IMAGE, firstDir, secondDir,thirdDir, lastPrdId, LAST_PRD_IMG_TAIL);

        }
        else
        {
            firstDir = lastPrdId.substring(0, 2);
            secondDir = lastPrdId.substring(2, 4);
            lastPrdImgUrl = String.format("%s/%s/%s/%s%s", LAST_PRD_IMAGE, firstDir, secondDir, lastPrdId, LAST_PRD_IMG_TAIL);

        }

        setLastPrdVisible(true);
        try {
            Glide.with(context).load(trim(lastPrdImgUrl)).diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.transparent).dontAnimate().into(imgTabLastPrd);
        } catch (Exception er) {
            Ln.e(er.getMessage());
        }
    }

    /**
     * 최근 본 상품의 아이디값을 쿠키로부터 추출한다.
     *
     * @return 상품아이디, 값이 없으면 null 리턴
     */
    public String getLastPrdIdFromCookie() {
        NameValuePair lastPrdPair = CookieUtils.getWebviewCookie(context, COOKIE_LAST_PRD_KEY);
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


    /*
     *  gs shop 패키지 task 가져오기.
     */
    private ActivityManager.RunningTaskInfo getTask() {
        ActivityManager mgr = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = mgr.getRunningTasks(Integer.MAX_VALUE);

        String packageName = MainApplication.getAppContext().getPackageName();
        for (ActivityManager.RunningTaskInfo task : tasks) {
            if(task.baseActivity.getPackageName().equals(packageName)) {
                return task;
            }
        }

        return null;
    }

    protected void startCustomCamera() {
        startCustomCamera(false);
    }

    protected void startCustomCamera(boolean isFromSearching) {

        if(PermissionUtils.isPermissionGrantedForStorageRead(this) &&
                PermissionUtils.isPermissionGrantedForCamera(this)) {    // 저장 퍼미션 있음
            Ln.d("startCustomCamera : isFromSearching " + isFromSearching);
            FileAttachUtils.startCamera(this, true, isFromSearching);
        }
        else {
            if (isFromSearching) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA}, Keys.REQCODE.PERMISSIONS_REQUEST_STORAGE_CAMERA_CUSTOM_FROM_SEARCH);
            }
            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA}, Keys.REQCODE.PERMISSIONS_REQUEST_STORAGE_CAMERA_CUSTOM);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case Keys.REQCODE.ATTACH_CUSTOM_CAMERA:
                // WebActivity에만 있는 크롭 및 업로드를 밖으로 뺐다. 하지만 기존에 사용하고 있는 부분은
                // 건드릴 경우 사이드 이펙트 발생할 우려 있어 Utils에 있는 함수를 사용하는 것은 추후 고려 사항
                if (resultCode != RESULT_OK) {
                    break;
                }

                if (intent != null) {
                    Intent thisIntent = getIntent();
                    thisIntent.putExtra(Keys.INTENT.INTENT_GALLERY_PARAM, intent.getBooleanExtra(Keys.INTENT.INTENT_GALLERY_PARAM, false));
                    thisIntent.putExtra(Keys.INTENT.INTENT_IS_FROM_SEARCHING, intent.getBooleanExtra(Keys.INTENT.INTENT_IS_FROM_SEARCHING, false));
                    setIntent(thisIntent);
                }

                if (intent != null && intent.getBooleanExtra(Keys.INTENT.INTENT_GALLERY_PARAM, false)) {
                    MainApplication.isEditFromGallery = true;
                    FileAttachUtils.photoCrop(this, MainApplication.articlePhotoes.get(0).fullImageUri.toString(), true);
                } else {
                    MainApplication.isEditFromGallery = false;
                    FileAttachUtils.photoCrop(this, MainApplication.tempImagePath, true);
                }
                break;
            case Keys.REQCODE.PHOTO_EDIT:
                if (resultCode != RESULT_OK) {

                    if (intent == null) {
                        intent = new Intent();
                    }

                    boolean isFromCamera = intent.getBooleanExtra(Keys.INTENT.INTENT_IS_FROM_CAMERA, false);
                    if (isFromCamera) {
                        startCustomCamera(intent.getBooleanExtra(Keys.INTENT.INTENT_IS_FROM_SEARCHING, false));
                    }

                    break;
                }
                MainApplication.isFromGallery = MainApplication.isEditFromGallery;
                if (MainApplication.tempImagePath != null)
                    MainApplication.attechImagePath = new File(MainApplication.tempImagePath);

                // 업로드를 여러장 할때를 대비해 필요한 변수,
                // 해당 콜백에서는 초기화 필요 없지만. 만약을 위해 초기화
                FileAttachInfoV2 attachInfo = FileAttachUtils.photoToFile(context,
                        Crop.getOutput(intent).getPath());
                if (attachInfo != null) {
                    // WebActivity에만 있는 크롭 및 업로드를 밖으로 뺐다. 하지만 기존에 사용하고 있는 부분은
                    // 건드릴 경우 사이드 이펙트 발생할 우려 있어 Utils에 있는 함수를 사용하는 것은 추후 고려 사항
                    new FileUploadController(this, attachInfo, true, new FileUploadController.OnUploadResultListener() {
                        @Override
                        public void onResult(FileAttachConnector.ShowmeAttachResult result) {
                            String urlReturn = getHomeGroupInfo().appUseUrl.imgSearchResultUrl;

                            if (TextUtils.isEmpty(urlReturn)) {
                                urlReturn = "http://m.gsshop.com/main/visenze/searchResult.gs?imgUrl=";
                            }

                            String urlPost = urlReturn + result.getFileUrl();
                            Intent data = new Intent();
                            data.putExtra(Keys.INTENT.POST_DATA, urlPost);

                            WebUtils.goWeb(context, urlPost, data);
                        }
                    }).execute(false);
                }
                break;

            case Keys.REQCODE.HOME_SEARCHING:
                if (resultCode != RESULT_OK) {
                    break;
                }

                startCustomCamera(true);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case Keys.REQCODE.PERMISSIONS_REQUEST_STORAGE_CAMERA_CUSTOM :
                if (PermissionUtils.verifyPermissions(this, permissions, grantResults)) {
                    FileAttachUtils.startCamera(this, true);
                }
                break;

            case Keys.REQCODE.PERMISSIONS_REQUEST_STORAGE_CAMERA_CUSTOM_FROM_SEARCH :
                if (PermissionUtils.verifyPermissions(this, permissions, grantResults)) {
                    FileAttachUtils.startCamera(this, true, true);
                }
                break;

            default:
                break;
        }
    }

    //토글 기능 삭제
    /*
    private void toggleTabMenu(View selectedTabMenu) {
        for (View b : tabMenuButtons) {
            b.setSelected(b == selectedTabMenu);
        }
    }
    */

    /*
    private void toggleTabMenuClear(View selectedTabMenu) {
        for (View b : tabMenuButtons) {
            b.setSelected(false);
        }
    }
    */
}
