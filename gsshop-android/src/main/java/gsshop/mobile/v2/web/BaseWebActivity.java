/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.inject.Inject;
import com.gsshop.mocha.core.util.ActivityUtils;
import com.gsshop.mocha.network.util.HttpUtils;
import com.gsshop.mocha.pattern.mvc.BaseAsyncController;
import com.gsshop.mocha.ui.back.BackKeyHandler;
import com.gsshop.mocha.ui.back.WebPageBackHandler;
import com.gsshop.mocha.ui.util.ImageUtils;
import com.gsshop.mocha.web.WebViewControl;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.usermgmt.LoginButton;
import com.kakao.util.exception.KakaoException;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.data.OAuthErrorCode;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import co.ab180.airbridge.Airbridge;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.Keys.ACTION;
import gsshop.mobile.v2.Keys.REQCODE;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.attach.FileAttachAction;
import gsshop.mobile.v2.attach.FileAttachAction.ATTACH_CALLER;
import gsshop.mobile.v2.attach.FileAttachConnector.ShowmeAttachResult;
import gsshop.mobile.v2.attach.FileAttachInfoV2;
import gsshop.mobile.v2.attach.FileAttachUtils;
import gsshop.mobile.v2.home.DirectOrderView;
import gsshop.mobile.v2.home.shop.schedule.TVScheduleShopFragment;
import gsshop.mobile.v2.home.util.crop.Crop;
import gsshop.mobile.v2.menu.BaseTabMenuActivity;
import gsshop.mobile.v2.menu.TabMenu;
import gsshop.mobile.v2.support.sns.SnsV2DialogFragment;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog.ButtonClickListener;
import gsshop.mobile.v2.support.ui.CustomTwoButtonDialog;
import gsshop.mobile.v2.user.LoginActivity;
import gsshop.mobile.v2.util.LunaUtils;
import gsshop.mobile.v2.util.PermissionUtils;
import gsshop.mobile.v2.util.ThreadUtils;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;
import static com.kakao.auth.Session.getCurrentSession;
import static gsshop.mobile.v2.MainApplication.useNativeProduct;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleShopFragment.isCalledFromBanner;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleShopFragment.scheduleBroadType;
import static gsshop.mobile.v2.web.WebUtils.BROADTYPE_PARAM_KEY;
import static gsshop.mobile.v2.web.WebUtils.GROUPCODE_PARAM_KEY;
import static gsshop.mobile.v2.web.WebUtils.LSEQ_PARAM_KEY;
import static gsshop.mobile.v2.web.WebUtils.MEDIA_PARAM_KEY;
import static gsshop.mobile.v2.web.WebUtils.OPEN_URL_PARAM_KEY;
import static gsshop.mobile.v2.web.WebUtils.TABID_PARAM_KEY;
import static gsshop.mobile.v2.web.WebUtils.isNativeProduct;

public abstract class BaseWebActivity extends BaseTabMenuActivity implements SnsV2DialogFragment.OnSnsSelectedListener {
    private static final String TAG = "BaseWebActivity";

    private static final String JS_INTERFACE_NAME = "AppInterface";

    //킷캣 미만버전에서 파일선택시 콜백함수 처리
    public static ValueCallback<Uri> mUploadMessage;
    public static ValueCallback<Uri[]> mUploadMessageLollipop;
    //킷캣 이외 버전의 경우 네이티브 업로드 기능 사용여부 플래그
    public static boolean useNativeUpload = false;

    // webview를 가진 여러 acitivty들을 구분하는 값
    protected static final int WEB_TYPE_DEFAULT = 0;
    protected static final int WEB_TYPE_NO_TAB = 1;
    protected static final int WEB_TYPE_MY_SHOP = 2;
    protected static final int WEB_TYPE_CATEGORY = 3;
    protected static final int WEB_TYPE_MODAL = 4;
    protected static final int WEB_TYPE_ORDER = 5;
    protected static final int WEB_TYPE_NALBANG = 6;
    protected static final int WEB_TYPE_LIVETALK = 7;
    protected static final int WEB_TYPE_DIRECT_ORDER = 8;
    protected static final int WEB_TYPE_TV_MEMBERS = 9;
    protected static final int WEB_TYPE_MOBILE_LIVE = 10;

    // blank page 제거 타입
    protected static final int BLANK_REMOVE_NOW = 0;
    protected static final int BLANK_REMOVE_DELAY = 1;

    //패키지명 정의
    private final static String PCK_KAKAOTALK = "com.kakao.talk";
    private final static String PCK_KAKAOSTORY = "com.kakao.story";
    private final static String PCK_LINE = "jp.naver.line.android";
    private final static String PCK_FACEBOOK = "com.facebook.katana";
    private final static String PCK_TWITTER = "com.twitter.android";
    private final static String PCK_PINTEREST = "com.pinterest";

    /**
     * 리로드 검출
     */
    private NonReloadChecker nonReloadChecker;

    /**
     * 최상위 메인 페이지 주소 패턴.
     * ServerUrls. 로 위치 변경
     */
    //protected static final String ROOT_URL_PATTERN = ServerUrls.HOST + "/index";

    /**
     * 마이쇼핑 주소 패턴 패턴
     */
    //private static final String MYSHOPINFO_URL_PATTERN = ServerUrls.HOST + "/mygsshop/myshopInfo.gs";

    //구매하기 or 바로구매 -> 로그인 후 호출되는 url
    private static final String BASKET_URL = "addbasketforward.gs";

    //백키 클릭시 홈으로 이동하도록 할 예외처리 URL 리스트
    //URL 추가시 "/" 까지 입력해야 함
    private final List<String> backToHomeUrlList = Arrays.asList(
            "/ocpCert.gs"); //회원가입완료 화면에서 백키 클릭시 홈화면 노출

    private final List<String> backToRefreshNavi = Arrays.asList(
            "/myshopinfo.gs"); //회원가입완료 화면에서 백키 클릭시 홈화면 노출

    //바로구매
    protected DirectOrderView directOrderLayout;

    protected WebViewControlInherited webControl;
    protected BackKeyHandler backHandler;

    //이미지 롱클릭 저장 관련 변수 선언
    //저장할 이미지 URL
    private String productImgUrl = "";
    private static final String PRODUCT_IMAGE_URL = "productImgUrl";

    /**
     * 위치서비스 권한 허용시 사용할 변수
     */
    public static String geiOrigin;
    public static GeolocationPermissions.Callback geoCallback;
    public static boolean callbackSkipFlag = false;

    /**
     * 로딩할 url 값에 tabId가 포함되어 있는지 여부
     * 서버는 url에 tabId가 포함되어 있으면 redirect 시킴 (../index.gs?a=1&tabId=567 -> ../index.gs)
     * 앱에서는 redirect된 ../index.gs에 대해서는 액션수행없이 무시해야 함.
     */
    protected boolean haveTabId = false;

    @InjectResource(R.integer.update_check_connection_timeout)
    protected int connectionTimeout;

    @InjectResource(R.integer.update_check_read_timeout)
    protected int readTimeout;

    /**
     * kakao simple login
     */
    public SessionCallback callback;

    /**
     * naver client 정보를 넣어준다.
     */
    private OAuthLogin mOAuthLoginInstance;

    @InjectView(R.id.btn_naver_sdk_login)
    OAuthLoginButton mOAuthLoginButton;

    @InjectView(R.id.btn_kakao_sdk_login)
    LoginButton mKakaoLoginButton;

    /**
     * 데이터를 불러오는중 오류가 났을때 표시되는 새로고침 레이아웃
     */
    protected View mRefreshView;

    /**
     * 새로고침 버튼, 웹으로 보기 버튼 (네트워크 장애)
     */
    protected FrameLayout mBtnRefresh, mBtnGoWeb;

    public void visibleOrderDirectWebView(String url) {
        directOrderLayout.upAnimation(this, url);
    }

    public void hideOrderDirectWebView() {
        directOrderLayout.downAnimation(this);
    }

    /**
     * 자식클래스에서 url 로딩전 웹뷰를 노출로 세팅한다.
     */
    public void loadUrl() {
        if (isNotEmpty(webControl)) {
            //onReceivedError에서 비노출로 세팅하므로 다시 노출로 변경
            webControl.getWebView().setVisibility(View.VISIBLE);
        }
    }

    protected void setupRefreshLayout() {
        mRefreshView = findViewById(R.id.flexible_refresh_layout);

        // 새로고침 버튼 셋팅(버튼 클릭시 데이터를 다시 불러온다.)
        mBtnRefresh = (FrameLayout) findViewById(R.id.btn_refresh);
        mRefreshView.setVisibility(View.GONE);
        if (isNotEmpty(mBtnRefresh)) {
            mBtnRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    mRefreshView.setVisibility(View.GONE);
                    loadUrl();
                }
            });
        }
        // 웹으로 보기 버튼 셋팅(버튼 클릭시 GS SHOP mobile web 으로 간다.)
        mBtnGoWeb = findViewById(R.id.btn_go_web);
        if (isNotEmpty(mBtnGoWeb)) {
            mBtnGoWeb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent goWebIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ServerUrls.MOBILE_WEB));
                    startActivity(goWebIntent);
                }
            });
        }
    }

    protected void setupWebControl() {
        if (webControl != null) {
//			String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
            backHandler = new WebPageBackHandler(this, webControl.getWebView());

            // 웹뷰 컨텍스트 메뉴 활성화
            registerForContextMenu(webControl.getWebView());

            // 웹뷰 오른쪽 여백 없애기
            webControl.getWebView().setVerticalScrollbarOverlay(true);
            webControl.getWebView().setScrollContainer(true);

            //웹뷰 캐시 적용
			/*mWebControl.getWebView().getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
			mWebControl.getWebView().getSettings().setAllowFileAccess(true);
			mWebControl.getWebView().getSettings().setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
			mWebControl.getWebView().getSettings().setAppCacheEnabled(true);*/
            //모바일에 특성항 한번에 가져오는 것이 적합하지 않기 때문에 캐쉬를 사용하지 않는 방법이 널리 통용 되는 퍼포먼스 향상 방법이다.
            webControl.getWebView().getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

            //webView 속도 향상
            //단품 > Q&A > 더보기 > 10개 더보기 버튼 클릭시 프로그레스바가 멈추는 현상 발생하여 주석처리
            //mWebControl.getWebView().setLayerType(View.LAYER_TYPE_HARDWARE, null);

            //페이지 자체가 큰경우 확대된 화면 상태로 로드 하지 않도록 처리 하는 방법
            webControl.getWebView().getSettings().setUseWideViewPort(true);
            webControl.getWebView().getSettings().setLoadWithOverviewMode(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                webControl.getWebView().getSettings().setMediaPlaybackRequiresUserGesture(false);
            }
            //이건 몰까
            webControl.getWebView().getSettings().setLayoutAlgorithm(LayoutAlgorithm.NORMAL);

            //pickup service
            webControl.getWebView().getSettings().setGeolocationEnabled(true);

            //HTTPS > HTTP 전송시 내장 브라우저에서 block 시켜 데이터 전송이 안되는 문제 개선 (결제 및 아이핀 인증시 흰화면 표시거나 오류페이지로 넘어감)
            //참고 : http://kalesst.blogspot.kr/2015/01/android-50-lollipop-webview-issue.html
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                webControl.getWebView().getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.setAcceptCookie(true);
                cookieManager.setAcceptThirdPartyCookies(webControl.getWebView(), true);
            }

            //webview 시스템 폰트 사이즈 고정(보류)
            //이걸 가져다 안쓰는 애들은 확인해야 한다
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                //webview 시스템 폰트 사이즈 고정(보류)
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    float scale = getResources().getConfiguration().fontScale;
                    //1.2 보다 크면
                    if (scale > 1.1) // 1.1 이상보다 크면 MAX 110
                        webControl.getWebView().getSettings().setTextZoom(100);
                    else if (scale < 1.0) // 1.0 보다 작으면 MIN 90
                        webControl.getWebView().getSettings().setTextZoom(100);
                    else {
                        // 그외 경우가 91~109 라고 판단하여 설정하지 않는다. 시스테을 그대로 따른다.
                        // 이유 : 100이라고 설정하면 100설정하기 전보다 아주 작지만 크거나 작아 보인다. 원인은 모르겠다.
                        webControl.getWebView().getSettings().setTextZoom(100);
                    }
                }
            }

            //설정되어 있는지 확인 필요
            //settings.setDatabaseEnabled(true);
            //설정되어 있는지 확인 필요
            //settings.setDomStorageEnabled(true);
            // window open 을 사용할수 있도록 설정
            //settings.setJavaScriptCanOpenWindowsAutomatically(true);

            //웹뷰와 앱간 인터페이스 정의
            webControl.getWebView().addJavascriptInterface(AndroidBridge.getInstance(this, webControl.getWebView()), JS_INTERFACE_NAME);

            // Airbridge 2.5.0 적용
            webControl.getWebView().getSettings().setJavaScriptEnabled(true);
            webControl.getWebView().getSettings().setDomStorageEnabled(true);
            Airbridge.setJavascriptInterface(webControl.getWebView(), getString(R.string.airbridge_web_token));

            //acecounter
            //앱 내부에서 html을 적재(asset 폴더)하여 사용하는 경우 쿠키를 사용할 수 있는 추가 설정 필요
            CookieManager.setAcceptFileSchemeCookies(true);

            //리로드 하면 안되는 대상 체크 관련
            nonReloadChecker = new NonReloadChecker(this);
        }
    }

    /**
     * webView를 띄우려는데 url이 없을 경우, home으로 이동하게 하는 예외 처리.
     */
    protected void handleWebException() {
        if (MainApplication.isAppView) {
            Intent intent = new Intent(Keys.ACTION.APP_HOME);
            intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.HOME);
            intent.putExtra(Keys.INTENT.FROM_TAB_MENU, true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            webControl.loadUrl(ServerUrls.WEB.HOME, MainApplication.customHeaders);
        }

    }

    /**
     * NOTE : WebView의 메소드는 UI 쓰레드에서 호출해야 한다.
     *
     * @param event event
     */
    public void onEventMainThread(Events.LoggedInEvent event) {
        if (webControl != null && webControl.getWebView() != null) {

            // 리로드 체커가 비상적인 경우에 대한 처리 && 현재 브라우져의 url 처리
            if (nonReloadChecker != null && webControl.getWebView().getUrl() != null) {
                //리로드 체커에서 검출된 url 리로드 하면 안된다
                if (nonReloadChecker.isNonReload(webControl.getWebView().getUrl())) {
                    return;
                }
            }

            webControl.getWebView().reload();
        }
    }

    /**
     * NOTE : WebView의 메소드는 UI 쓰레드에서 호출해야 한다.
     *
     * @param event event
     */
    public final void onEventMainThread(Events.LoggedOutEvent event) {
        if (webControl != null && webControl.getWebView() != null) {
            webControl.getWebView().reload();
        }
    }

    /**
     * 히스토리 백을 수행하는 이벤트
     *
     * @param event event
     */
    public final void onEventMainThread(Events.WebHistoryBackEvent event) {
        if (webControl.getWebView().canGoBackOrForward(-1)) {
            webControl.getWebView().goBack();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            productImgUrl = savedInstanceState.getString(PRODUCT_IMAGE_URL);
        }
    }

    @Override
    protected void onPause() {
        if (webControl != null) {
            webControl.pauseWebView();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webControl != null) {
            webControl.resumeWebView();

            if (webControl.getWebView() != null) {
                try {
                    webControl.getWebView().loadUrl("javascript:refreshBasketCnt()");
                } catch (Exception e) {
                    // 10/19 품질팀 요청
                    // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
                    // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
                    // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
                    Ln.e(e);
                }
            }

        }
    }

    @Override
    protected void onDestroy() {
        if (backHandler != null) {
            backHandler.destroy();
        }

        super.onDestroy();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (webControl != null) {
            webControl.destroy();
        }
    }

    @Override
    public void onBackPressed() {
        if (getDrawerLayout() != null && getDrawerLayout().isDrawerOpen(getNavigationView())) {
            closeNavigationView();
            return;
        }

        //백키 클릭시 홈으로 이동해야 하는 url인 경우 홈으로 이동한다.
        if (isBackToHomeUrl()) {
            GetHomeGroupListInfo();
            return;
        }

        //리프레시뷰 노출상태에서는 백키 클릭시 액티비티 종료
        if (isNotEmpty(mRefreshView) && mRefreshView.getVisibility() == View.VISIBLE) {
            super.onBackPressed();
            return;
        }

        if (backHandler != null) {
            if (backHandler.handle()) {
                return;
            }
        }

        super.onBackPressed();
    }

    /**
     * 백버튼 클릭시 필요한 선행처리를 수행한다.
     */
    protected void onBackPressedBefore() {
        //SNS공유, 푸시 등 외부에서 호출된 경우
        if (getIntent().getBooleanExtra(Keys.INTENT.BACK_TO_MAIN, false)) {
            if (MainApplication.isHomeCommandExecuted) {
                //메인화면이 떠있는 상태면 나만 종료
                getIntent().putExtra(Keys.INTENT.BACK_TO_MAIN, false);
            } else {
                //메인화면이 떠있지 않는 상태면 나종료 && 메인 띄움
                backHandler = null;
            }
        }
    }

    /**
     * 웹액티비티가 살아있는 상태에서 다른 액티비티로부터 새 주소 로딩을
     * 요청받는 경우 처리.
     *
     * @param intent intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (webControl != null) {
            String nextUrl = intent.getStringExtra(Keys.INTENT.WEB_URL);
            if (nextUrl == null) {
                return;
            }

            //아래 goBackOrForward(-3)을 사용하는 이유는
            //결제완료 후 결제완료페이지에서 하드웨어 백키 클릭시 홈화면으로 이동시키기 위함
            //ISP 결제 진행후 콜백에 의해 다시 웹화면으로 진입하는 경우
            if (nextUrl.startsWith(ServerUrls.WEB.PRE_ISP_CONFIRM_ORDER)) {
                // 뒤로가기시 다시 결제페이지로 돌아오지 않게 하기 위해.
                if (webControl.getWebView().canGoBackOrForward(-3)) {
                    webControl.getWebView().goBackOrForward(-3);
                }
            }

            //KAKAO 결제 진행후 콜백에 의해 다시 웹화면으로 진입하는 경우
            //TODO : 카카오결제 테스하면서 하드웨어 백키 클릭 테스트 필요함. 필요에 따라 -3값 조정 필요
            //2015-01-07 박영환, 카카오 결제시 오류 발생하여 아래 로직 주석처리 (20~30건당 1건 발생)
			/*if (nextUrl.startsWith(ServerUrls.WEB.PRE_KAKAO_CONFIRM_ORDER)) {
                // 뒤로가기시 다시 결제페이지로 돌아오지 않게 하기 위해.
                if (mWebControl.getWebView().canGoBackOrForward(-3)) {
                    mWebControl.getWebView().goBackOrForward(-3);
                }
            }*/

            //onReceivedError에서 비노출로 세팅하므로 다시 노출로 변경
            webControl.getWebView().setVisibility(View.VISIBLE);
            mRefreshView.setVisibility(View.GONE);

            webControl.loadUrl(nextUrl, MainApplication.customHeaders);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == Activity.RESULT_CANCELED) {
            //카메라/갤러리 팝업이 뜬 상태에서 백키 클릭시 웹뷰화면 행걸리는 현상 대응 코드 (안드로이드 특정버전에서만 발생, 4.3&???)
            if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(null);
                mUploadMessage = null;
            }
        }

        switch (requestCode) {
            case REQCODE.LOGIN: // 로그인 성공 후 타겟 웹페이지 로딩
            case REQCODE.VIDEO: // 동영상 재생 중 상품보기 웹페이지 로딩
            case REQCODE.MODAL: // 모달창에서 닫은 후 타겟 웹페이지 로딩
                if (intent == null) {
                    break;
                }

                String nextUrl = intent.getStringExtra(Keys.INTENT.WEB_URL);
                if (nextUrl == null) {
                    break;
                }

                if (webControl != null) {
                    webControl.loadUrl(nextUrl, MainApplication.customHeaders);
                }
                break;
            case REQCODE.REVIEW: // 리뷰 등록후 팝업 처리 및 웹페이지 로딩
                if (resultCode != RESULT_OK) {
                    break;
                }

                if (intent != null) {
                    if (intent.getBooleanExtra(Keys.CACHE.REVIEW_SAVE, true)) {
                        Toast.makeText(getApplicationContext(), R.string.review_success_modify,
                                Toast.LENGTH_SHORT).show(); // 수정
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.review_success_save,
                                Toast.LENGTH_SHORT).show(); // 작성
                    }
                }
                if (webControl != null && webControl.getWebView() != null) {
                    webControl.getWebView().reload();
                }
                break;
            case REQCODE.EVENT: // 이벤트 더미페이지 호출
                if (intent == null) {
                    break;
                }

                String eventUrl = intent.getStringExtra(Keys.INTENT.WEB_URL);
                if (eventUrl == null) {
                    break;
                }

                try {
                    setEventMsg(eventUrl);
                } catch (IOException e) {
                    Ln.e(e.getLocalizedMessage());
                }

                break;

            case REQCODE.IMAGE_PICKER_LOLLIPOP: // 이미지피커에서 호출 5.0 이상
                if (resultCode != RESULT_OK || mUploadMessageLollipop == null) {
                    mUploadMessageLollipop.onReceiveValue(null);
                    mUploadMessageLollipop = null;
                    break;
                }

                mUploadMessageLollipop.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                mUploadMessageLollipop = null;

                //웹에서 브릿지 함수를 호출한 경우만 네이티브 파일 업로드 기능 수행
                //브릿지 함수를 호출하지 않은 경우에는 웹에서 파일 업로드 기능을 구현하면 됨
                if (useNativeUpload) {
                    if (isCamera(intent)) {
                        WebViewImageUpload.getInstance(this, webControl.getWebView()).updateContent();
                    } else {
                        Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
                        WebViewImageUpload.getInstance(this, webControl.getWebView()).updateContent(result);
                    }
                    useNativeUpload = false;
                }

                break;
            case REQCODE.IMAGE_PICKER: // 이미지피커에서 호출 (kitkat 이외 버전)
                if (resultCode != RESULT_OK) {
                    break;
                }

                Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();

                if (mUploadMessage == null) {
                    break;
                }

                if (isCamera(intent)) {
                    mUploadMessage.onReceiveValue(Uri.fromFile(WebViewImagePicker.PHOTO_FILE));
                } else {
                    mUploadMessage.onReceiveValue(result);
                }
                mUploadMessage = null;

                //웹에서 브릿지 함수를 호출한 경우만 네이티브 파일 업로드 기능 수행
                //브릿지 함수를 호출하지 않은 경우에는 웹에서 파일 업로드 기능을 구현하면 됨
                if (useNativeUpload) {
                    if (isCamera(intent)) {
                        WebViewImageUpload.getInstance(this, webControl.getWebView()).updateContent();
                    } else {
                        WebViewImageUpload.getInstance(this, webControl.getWebView()).updateContent(result);
                    }
                    useNativeUpload = false;
                }

                break;
            case REQCODE.KITKAT_IMAGE_PICKER: // 이미지피커에서 호출 (kitkat 버전)
                if (resultCode != RESULT_OK) {
                    break;
                }

                Uri kResult = intent == null || resultCode != RESULT_OK ? null : intent.getData();
                if (isCamera(intent)) {
                    WebViewImageUpload.getInstance(this, webControl.getWebView()).updateContent();
                } else {
                    WebViewImageUpload.getInstance(this, webControl.getWebView()).updateContent(kResult);
                }
                break;
            case REQCODE.FILEATTACH: // 파일첨부 완료 후 리턴페이지로 이동
                if (resultCode != RESULT_OK) {
                    break;
                }
                if (webControl != null) {
                    String caller = intent.getStringExtra("caller");
                    String callback = intent.getStringExtra("returnUrl");

                    if (caller.equals(ATTACH_CALLER.SHOWMECAFE.toString()) || caller.equals(ATTACH_CALLER.REVIEW.toString()) ||
                            caller.equals(ATTACH_CALLER.IMAGESEARCH.toString())) {
                        if (!TextUtils.isEmpty(callback)) {
                            //						mWebControl.getWebView().loadUrl("javascript:" + callback + "('" + talkId + "')");
                        }
                    } else {
                        webControl.getWebView().loadUrl(intent.getStringExtra("returnUrl"));
                    }


                }
                break;
            case REQCODE.MOBILETALK: // 모바일상담 글등록/파일첨부 완료 후 웹뷰 리로드 또는 웹뷰스크립트 함수 호출
                //취소시
                if (resultCode == RESULT_CANCELED) {
                    if (intent != null) {
                        String callback = intent.getStringExtra("callback");
                        if (callback != null && !"".equals(callback)) {
                            webControl.getWebView().loadUrl(callback);
                        }
                    }
                    break;
                }
                if (resultCode != RESULT_OK) {

                    break;
                }

                if (webControl != null) {
                    //페이지 리로드
                    //mWebControl.getWebView().reload();
                    //자바스크립트 함수 호출
                    String callback = intent.getStringExtra("callback");
                    String talkId = intent.getStringExtra("talkId");
                    String errorCode = intent.getStringExtra("error_code");
                    String errorMsg = intent.getStringExtra("error_message");
                    if (errorMsg != null && errorMsg.length() > 0) {
                        errorMsg = errorMsg.replaceAll("\n", "<br>");
                    }
                    //Ln.i("MOBILETALK onActivityResult : callback=" + callback + ", " + "talkId=" + talkId + ", errorCode=" + errorCode + ", errorMsg=" + errorMsg);
                    if (!TextUtils.isEmpty(callback)) {
                        webControl.getWebView().loadUrl("javascript:" + callback + "('" + talkId + "', '" + errorCode + "', '" + errorMsg + "')");
                    }
                }
                break;

            case REQCODE.ATTACH_CUSTOM_CAMERA:
                if (resultCode != RESULT_OK) {
                    break;
                }

                // 서칭 바로부터 시작 되었다면 모든 동작 후에 Post로 보내야 하기 때문에 BaseTabMenuActivity에 선언해 놓은 곳을 탄다.
                boolean isFromSearching = false;
                if (intent != null) {
                    isFromSearching = intent.getBooleanExtra(Keys.INTENT.INTENT_IS_FROM_SEARCHING, false);
                }
                if (isFromSearching) {
                    super.onActivityResult(requestCode, resultCode, intent);
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
                    photoCrop(MainApplication.articlePhotoes.get(0).fullImageUri.toString(), true);
                } else {
                    MainApplication.isEditFromGallery = false;
                    photoCrop(MainApplication.tempImagePath, true);
                }
                break;

            case REQCODE.ATTACH_CAMERA: //카메라 호출 후 호출
                MainApplication.isFromGallery = false;
                if (resultCode != RESULT_OK) {
                    break;
                }
                try {
                    File[] uploadFilesCamera = new File[1];

                    //세로로 촬영한 이미지를 세로로 유지한재 이미지 사이즈 줄임
                    final Bitmap previewBitmap = resizeGalleryImageToSend(MainApplication.attechImagePath.getAbsolutePath(), 0);

                    FileAttachInfoV2 attachInfoCamera = new FileAttachInfoV2();

                    if (FileAttachAction.ATTACH_CALLER.LIVETALK.toString().equalsIgnoreCase(
                            MainApplication.fileAttachParamInfo.getCaller())) {
                        //이미지 가로/세로 비율정보 세팅(S:정사각 H:가로>세로 V:세로>가로)
                        attachInfoCamera.setImageType(FileAttachUtils.getImageRatioInfo(previewBitmap));

                        //라이브톡에서 파일전송 하는 경우 이미지용량 200K 이하로 조정한다.
                        int quality = FileAttachUtils.getImageQuality(previewBitmap);
                        uploadFilesCamera[0] = FileAttachUtils.bitmapToFile(previewBitmap, FileAttachUtils.getTempAttachImage(context), quality);
                    } else {
                        uploadFilesCamera[0] = ImageUtils.bitmapToFile(previewBitmap, MainApplication.attechImagePath);
                    }

                    attachInfoCamera.setImageFile(uploadFilesCamera);
                    new ShowMeFileController(this, attachInfoCamera).execute(false);
					/*
					history back 하는 구문 삭제 20190611
					if ("Y".equals(MainApplication.fileAttachParamInfo.getHistoryBack())) {
						if (mWebControl.getWebView().canGoBackOrForward(-1)) {
							mWebControl.getWebView().goBack();
						}

						// 히스토리 백이 완료될 시간을 주기위해 1초 후에 업로드를 실행한다.
						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {
								new ShowMeFileController(BaseWebActivity.this, attachInfoCamera).execute(false);
							}
						}, 1000);
					}
					else {
						new ShowMeFileController(this, attachInfoCamera).execute(false);
					}
					*/
                } catch (NullPointerException e) {
                    //사진이 없는경우 // 카메라호출후 뒤로가기 버튼을 누를경우
                    break;
                } catch (Exception e) {
                    // 10/19 품질팀 요청
                    // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
                    // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
                    // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
                    Ln.e(e);
                }
                break;

            case REQCODE.PHOTO:
                if (resultCode != RESULT_OK) {
                    break;
                }
                intent.putExtra("REQCODE", requestCode);
                boolean isUploadedPhoto = FileAttachUtils.uploadMediaFromGallery(context, intent);
                if (isUploadedPhoto) {
                    uploadIndex = 0;
                    photoUpload(MainApplication.articlePhotoes.get(uploadIndex).fullImageUri.toString(), false);
                }
                break;

            case REQCODE.PHOTO_VIDEO:
                if (resultCode != RESULT_OK) {
                    break;
                }
                intent.putExtra("REQCODE", requestCode);
                boolean isUploaded = FileAttachUtils.uploadMediaFromGallery(context, intent);

                if (isUploaded) {
                    File[] imageFiles = new File[MainApplication.articlePhotoes.size()];
                    FileAttachInfoV2 attachInfo = new FileAttachInfoV2();
                    for (int i = 0; i < imageFiles.length; i++) {
                        File[] uploadFiles = new File[1];
                        uploadFiles[0] = new File(MainApplication.articlePhotoes.get(i).fullImageUri.toString());
                        attachInfo.setImageFile(uploadFiles);
                        new ShowMeFileController(this, attachInfo).execute(true);
                    }
                }
                break;

            case REQCODE.GALLERY: // 갤러리 이미지 선택후 호출
                if (resultCode != RESULT_OK) {
                    break;
                }

                if (MainApplication.articlePhotoes != null) {
                    MainApplication.isFromGallery = true;
                    String type = intent.getStringExtra("type");

                    // 동영상 선택시
                    if ("video".equals(type) || "eventvideo".equals(type)) {
                        File[] imageFiles = new File[MainApplication.articlePhotoes.size()];
                        FileAttachInfoV2 attachInfo = new FileAttachInfoV2();
                        for (int i = 0; i < imageFiles.length; i++) {
                            File[] uploadFiles = new File[1];
                            uploadFiles[0] = new File(MainApplication.articlePhotoes.get(i).fullImageUri.toString());
                            attachInfo.setImageFile(uploadFiles);
                            new ShowMeFileController(this, attachInfo).execute("eventvideo".equals(type));
                        }
                    } else { // 사진 선택시
//						boolean isImageSearch = intent.getBooleanExtra("isImageSearch", false);

                        // 사진 업로드 시작 할 때에 uploadIndex을 0으로 초기화
                        uploadIndex = 0;
                        photoUpload(MainApplication.articlePhotoes.get(uploadIndex).fullImageUri.toString(), false);

                        // 히스토리 백이 Y일 경우 히스토리 백 후 1초 후 업로드 실행.
						/*
						history back 하는 구문 삭제 20190611
						if ("Y".equals(MainApplication.fileAttachParamInfo.getHistoryBack())) {
							if (mWebControl.getWebView().canGoBackOrForward(-1)) {
								mWebControl.getWebView().goBack();
							}

							// 히스토리 백이 완료될 시간을 주기위해 1초 후에 업로드를 실행한다.
							new Handler().postDelayed(new Runnable() {

								@Override
								public void run() {
									photoUpload(MainApplication.articlePhotoes.get(uploadIndex).fullImageUri.toString(), false);
								}
							}, 1000);
						}
						else {
							photoUpload(MainApplication.articlePhotoes.get(uploadIndex).fullImageUri.toString(), false);
						}
						*/
                    }

                }
                break;
            case REQCODE.GPS: // GPS
                if (resultCode != Activity.RESULT_OK) {
                    //GPS ON 시스템팝업에서 취소 클릭시 "onGeolocationPermissionsShowPrompt" 콜백 호출되고
                    //GPS ON 시스템팝업이 다시 떠서 예외처리.
                    //다른 처리방법이 있는지는 추가로 확인이 필요함
                    callbackSkipFlag = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            callbackSkipFlag = false;
                        }
                    }, 3000);
                }
                break;

            case REQCODE.PHOTO_EDIT:
                if (intent != null && intent.getBooleanExtra(Keys.INTENT.INTENT_IS_FROM_SEARCHING, false)) {
                    super.onActivityResult(requestCode, resultCode, intent);
                    break;
                }

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
                uploadIndex = 0;
                photoUpload(Crop.getOutput(intent).getPath(), true);
                break;

            case REQCODE.HOME_SEARCHING:
                if (resultCode != RESULT_OK) {
                    break;
                }
                startCustomCamera(true);
                break;
            case REQCODE.ADDRESS:
                try {
                    if (intent != null) {
                        Cursor cursor = getContentResolver().query(intent.getData(), new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null);
                        if (cursor != null) {
                            cursor.moveToFirst();
                            String name = cursor.getString(0);
                            String number = cursor.getString(1);
                            cursor.close();

                            webControl.getWebView().loadUrl("javascript:" + MainApplication.giftFunc + "('" + name + "','" + number + "')");
                        }
                        break;
                    }
                } catch (Exception e) {

                }

            default:
                super.onActivityResult(requestCode, resultCode, intent);
                break;
        }
    }

    /**
     * 갤러리이미지의 경우 서버로 전송하기 전에 적당히 축소된 이미지파일로
     * 별도로 만들어둔다. (단, 원본 갤러리 이미지는 변경하지 않음)
     *
     * @param imagePath 이미지경로
     * @param idx       이미지가 다수일 경우 인덱스
     * @return Bitmap
     */
    private Bitmap resizeGalleryImageToSend(String imagePath, int idx) {
        return FileAttachUtils.rotateAndScaleDown(imagePath, FileAttachUtils.ATTACH_IMAGE_WIDTH);
    }

    /**
     * 업로드 순서
     */
    private int uploadIndex;

    /**
     * 이미지 크롭 기능
     *
     * @param filePathImg 원조 이미지 경로
     */
    public void photoCrop(String filePathImg, boolean isFromCamera) {
        FileAttachUtils.photoCrop(BaseWebActivity.this, filePathImg, isFromCamera);
    }

    /**
     * 사진을 업로드 한다.
     *
     * @param imgPath   업로드할 이미지 경로
     * @param isImgEdit 해당 이미지가 수정본인지 여부
     */
    private void photoUpload(String imgPath, boolean isImgEdit) {
        FileAttachInfoV2 attachInfo = FileAttachUtils.photoToFile(context, imgPath);
        if (attachInfo != null) {
            new ShowMeFileController(this, attachInfo, isImgEdit).execute(false);
        }
    }

    /**
     * 다음 업로드 파일이 존재할경우 다음 사진을 서버에 업로드 한다.
     * 크롭의 경우 하나의 이미지만 업로드 하고 있기 때문에 Next가 불릴일이 없지만 추후 변경되면 이 함수도 수정되어야 한다..
     */
    private void photoUploadNext() {

        if (MainApplication.articlePhotoes != null) {
            if (MainApplication.articlePhotoes.size() > uploadIndex) {
                File[] uploadFiles = new File[1];
                FileAttachInfoV2 attachInfo = new FileAttachInfoV2();

                Bitmap shrinkedBitmap = FileAttachUtils.rotateAndScaleDown(MainApplication.articlePhotoes.get(uploadIndex).fullImageUri.toString(), FileAttachUtils.ATTACH_IMAGE_WIDTH);
                //BitMap -> file 로
                try {
                    uploadFiles[0] = ImageUtils.bitmapToFile(shrinkedBitmap, FileAttachUtils.getTempAttachImage(context));
                } catch (IOException e) {
                    // 10/19 품질팀 요청
                    // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
                    // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
                    // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
                    Ln.e(e);
                    //uploadFiles[0] = ImageUtil.resizeGalleryImage(getApplicationContext(), MainApplication.articlePhotoes.get(uploadIndex).fullImageUri.toString());
                    //실패 했을땐???
                }
                attachInfo.setImageFile(uploadFiles);
                new ShowMeFileController(this, attachInfo).execute(false);
            }
        }
    }

    /**
     * 코멘트 및 파일 저장 콘트롤러
     */
    private class ShowMeFileController extends BaseAsyncController<ShowmeAttachResult> {

        @Inject
        private FileAttachAction attachAction;
        private final FileAttachInfoV2 attachInfo;

        boolean isEventVideo = false;
        boolean isImageEdit = false;

        protected ShowMeFileController(Context activityContext, FileAttachInfoV2 attachInfo) {
            super(activityContext);
            this.attachInfo = attachInfo;
        }

        protected ShowMeFileController(Context activityContext, FileAttachInfoV2 attachInfo, boolean isImageEdit) {
            super(activityContext);
            this.attachInfo = attachInfo;
            this.isImageEdit = isImageEdit;
        }

        @Override
        protected void onPrepare(Object... params) throws Exception {
            super.onPrepare(params);
            this.dialog.dismiss();

            //이벤트 동영상 업로드의 경우는 별도 프로그레스바 노출
            this.isEventVideo = (boolean) params[0];
            if (!isEventVideo) {
                this.dialog.dismiss();
                this.dialog.setCancelable(false);
                this.dialog.show();
            }

            attachInfo.setCaller(MainApplication.fileAttachParamInfo.getCaller());
        }

        @Override
        protected ShowmeAttachResult process() throws Exception {
            return attachAction.showMesaveFiles(attachInfo, MainApplication.fileAttachParamInfo.getUploadUrl(), isEventVideo);
        }

        /**
         * 업로드 완료시 웹뷰의 자바스크립트를 호출하여 알린다.
         */
        @Override
        protected void onSuccess(ShowmeAttachResult result) throws Exception {
            ++uploadIndex;
            if (result.getResult() != null && "success".equals(result.getResult())) {
                String strLoadUrl = "javascript:"
                        + MainApplication.fileAttachParamInfo.getCallback()
                        + "('" + result.getResult() + "',"
                        + "'" + result.getTmpFileName() + "',"
                        + "'" + result.getRealFileName() + "',"
                        + "'" + result.getFileUrl() + "',"
                        + "'" + result.getImgType() + "',"
                        + "'" + result.getThumbnail() + "'"
                        + ")";

				if (isNotEmpty(webControl.getWebView())) {
					webControl.getWebView().loadUrl(strLoadUrl);
				}
			}
			//다음 첨부파일을 업로드한다.
			photoUploadNext();
		}
	}

    /**
     * 인텐트 데이타가 카메라로 부터 호출된 것인지 갤러리로 부터 호출된 것인지 구분한다.
     *
     * @param data 인텐트
     * @return 카메라 촬영으로 이미지를 취득한 경우 true, 그외 false
     */
    protected boolean isCamera(Intent data) {
        boolean isCamera;
        if (data == null) {
            isCamera = true;
        } else {
            final String action = data.getAction();
            if (action == null) {
                isCamera = false;
            } else {
                isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            }
        }

        return isCamera;
    }

    public void setEventMsg(String eventUrl) throws IOException {
        URL url = new URL(eventUrl);
        URLConnection conn = url.openConnection();
        conn.setConnectTimeout(connectionTimeout);
        conn.setReadTimeout(readTimeout);
        conn.connect();
        // byte[] contents = FileCopyUtils.copyToByteArray(conn.getInputStream());
    }

    /**
     * 웹뷰 이미지 롱클릭시 이미지 저장 팝업 보여줌
     *
     * @param menu     menu
     * @param v        v
     * @param menuInfo menuInfo
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        if (v != webControl.getWebView()) {
            super.onCreateContextMenu(menu, v, menuInfo);
            return;
        }

		final WebView.HitTestResult hitTestResult = webControl.getWebView().getHitTestResult();
		if (hitTestResult != null
				&& (hitTestResult.getType() == WebView.HitTestResult.IMAGE_TYPE || hitTestResult
				.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE)) {
			Dialog dialog = new CustomTwoButtonDialog(this).message(R.string.webview_confirm_save_image)
					.positiveButtonClick(new ButtonClickListener() {
						@Override
						public void onClick(Dialog dialog) {
							productImgUrl = hitTestResult.getExtra();

							if(PermissionUtils.isPermissionGrantedForStorageWrite(context)) {
								if (!TextUtils.isEmpty(productImgUrl)) {
									new SaveImageController(BaseWebActivity.this).execute(productImgUrl);
									if (!productImgUrl.startsWith(ServerUrls.HTTP)
											&& !productImgUrl.startsWith(ServerUrls.HTTPS)) {
										//키바나 확인용 코드 (MalformedURLException: unknown protocol: data)
										String linkUrl = webControl.getWebView().getUrl();
										LunaUtils.sendToLuna(context, new Exception(productImgUrl+ "_" + linkUrl), "HitTest");
									}
								}
							}else {
								// 저장 권한 없는 경우, 저장관련 권한 요청
								ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
										Manifest.permission.WRITE_EXTERNAL_STORAGE}, Keys.REQCODE.PERMISSIONS_REQUEST_STORAGE);
							}
							dialog.dismiss();
						}
					}).negativeButtonClick(CustomOneButtonDialog.DISMISS);

			//키바나 수집 로그로 앱크래쉬 방지 (java.lang.RuntimeException: Adding window failed)
			try {
				dialog.show();
			} catch (Exception e) {
				Ln.e(e);
			}
		}
	}

    /**
     * 웹뷰 이미지 저장 콘트롤러.
     * <p>
     * 리모트의 이미지를 로컬로 다운받은후 SD카드 Pictures 폴더에 저장한다.
     */
    private class SaveImageController extends BaseAsyncController<File> {
        private String imageUrl;
        private final String FILE_PREFIX = "GSSHOP_" + SystemClock.currentThreadTimeMillis();
        private final File SAVE_DIR = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        protected SaveImageController(Context activityContext) {
            super(activityContext);
        }

        @Override
        protected void onPrepare(Object... params) throws Exception {
            super.onPrepare(params);
            imageUrl = (String) params[0];
        }

        @Override
        protected File process() throws Exception {
            String fileName = FILE_PREFIX;

            if (imageUrl.toLowerCase(Locale.getDefault()).endsWith(".png"))
                fileName += ".png";
            else if (imageUrl.toLowerCase(Locale.getDefault()).endsWith(".gif"))
                fileName += ".gif";
            else
                fileName += ".jpg";

            return HttpUtils.getFile(imageUrl, new File(SAVE_DIR, fileName));
        }

        @Override
        protected void onSuccess(File t) throws Exception {
            // 생성된 이미지를 갤러리에서 인식할 수 있게 미디어스캐닝 진행요청
            Intent i = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            i.setData(Uri.parse(ContentResolver.SCHEME_FILE + "://" + t.getCanonicalPath()));
            sendBroadcast(i);

            String message = getString(R.string.webview_success_save_image, t.getCanonicalPath());
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * WebActivity 호출
     *
     * @param url url
     */
    protected void goDefaultWebActivity(String url) {
        Intent intent = new Intent(ACTION.WEB);
        intent.putExtra(Keys.INTENT.WEB_URL, url);
        intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.getTabMenu(getIntent()));
        startActivity(intent);
    }

    /**
     * TvMembersWebActivity 호출
     *
     * @param url url
     */
    protected void goTvMembersWebActivity(String url) {
        Intent intent = new Intent(ACTION.TV_MEMBERS_WEB);
        intent.putExtra(Keys.INTENT.WEB_URL, url);
        // 해당 extra가 전달받는 activity에서 필요 없어 보이지만 후에 필요한 경우가 생길 수 있음.
        intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.getTabMenu(getIntent()));
        intent.putExtra(Keys.INTENT.BACK_TO_MAIN, getIntent().getBooleanExtra(Keys.INTENT.BACK_TO_MAIN, false));
        startActivity(intent);
    }

    /**
     * goMyShopWebActivity 호출
     *
     * @param url url
     */
    protected void goMyShopWebActivity(String url) {
        Intent intent = new Intent(ACTION.MY_SHOP_WEB);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Keys.INTENT.WEB_URL, url);
        intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.getTabMenu(getIntent()));
        startActivity(intent);
    }

    /**
     * MyShopWebActivity 호출
     *
     * @param url url
     */
    protected void goMobileLiveActivity(String url) {
        Intent intent = new Intent(ACTION.MOBILELIVE_NO_TAB_WEB);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Keys.INTENT.WEB_URL, url);
        intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.getTabMenu(getIntent()));
        startActivity(intent);
    }

    /**
     * OrderpWebActivity 호출
     *
     * @param url url
     */
    protected void goOrderWebActivity(String url) {
        Intent intent = new Intent(ACTION.ORDER_WEB);

        // ! not 유의
        if (WebUtils.isOrderTabCheck(url) == false)
            intent.setAction(ACTION.ORDER_NOTAB_WEB);

        intent.putExtra(Keys.INTENT.WEB_URL, url);
        intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.getTabMenu(getIntent()));
        startActivity(intent);
    }

    /**
     * chooser 띄움
     * TODO : jisun default app 선택하는 거 어떻게 나오게 하지??
     *
     * @param url url
     */
    protected void showAppChooser(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        Intent chooser = Intent.createChooser(intent, "Open with");
        startActivityForResult(chooser, REQCODE.LAUNCH_OTHER_APP);
    }

    /**
     * web activity들에서 다음 url을 호출할 때 webType에 맞게 처리되도록 함
     *
     * @param currentWebType : 이 method를 호출한 web activity의 type
     * @param origUrl        : 이동할 url
     * @return : 이 method내에서 url 이동이 처리되었으면 true, 아니면 false. false이면 호출한 webView에서 처리함.
     */
    protected boolean handleUrl(int currentWebType, String origUrl) {
        boolean handled = true;

        String url = origUrl.toLowerCase(Locale.getDefault());

        // 이동할 url을 체크해서 web activity type 결정
        // modal web은 "toapp://modal"로 내려오기 때문에 따로 처리 안함.
        int nextWebType;
        if (WebUtils.isNoTabPage(url)) {
            nextWebType = WEB_TYPE_NO_TAB;
        } else if (WebUtils.isMyShop(url)) {
            nextWebType = WEB_TYPE_MY_SHOP;
        } else if (WebUtils.isOrder(url)) {
            nextWebType = WEB_TYPE_ORDER;
        } else if (WebUtils.isTvLoginPage(url)) {
            nextWebType = WEB_TYPE_TV_MEMBERS;
        }
        else if (WebUtils.isMobileLiveUrl(url)) {
            nextWebType = WEB_TYPE_MOBILE_LIVE;
        }
        else {
            nextWebType = WEB_TYPE_DEFAULT;
        }

        // web page이면
        if (url.startsWith("http")) {
            // gsshop 내의 page이면
            if (url.contains("gsshop.com")) { // FIXME : jisun host 체크 루틴 정리하기
                if (currentWebType == nextWebType || url.contains("error")
                        || url.contains("estimatewritecheck.gs")) { // 상품평 작성 불가 웹 팝업 처리
                    if (useNativeProduct) {
                        if (isNativeProduct(url)) {
                            //푸시, 제휴(jseis_withLGeshop.jsp) 등의 경우 NoTabWebActivity에서 제휴페이지 로딩 후 단품네티티브로 이동
                            //이때 NoTabWebActivity는 닫고 이동함
                            finish();
                            //native 단품
                            WebUtils.goNativeProduct((Activity) context, origUrl);
                        } else {
                            handled = false;
                        }
                    } else {
                        // 현재 url과 다음 url이 같은 web type이거나,
                        // error 페이지로 redirect되는 경우는
                        // 여기서 다른 web activity로 가지 않고 호출한 webView에서 처리
                        handled = false;
                    }
                } else {
                    // 다른 web type의 url이면 해당 activity로 이동
                    switch (nextWebType) {
                        case WEB_TYPE_NO_TAB:
                            if (useNativeProduct) {
                                if (isNativeProduct(url)) {
                                    //native 단품
                                    WebUtils.goNativeProduct((Activity) context, origUrl);
                                } else {
                                    //web 단품
                                    WebUtils.goNoTabWebActivity((Activity) context, origUrl);
                                }
                            } else {
                                WebUtils.goNoTabWebActivity((Activity) context, origUrl);
                            }
                            break;
                        case WEB_TYPE_MY_SHOP:
                            goMyShopWebActivity(origUrl);
                            break;
                        case WEB_TYPE_ORDER:
                            goOrderWebActivity(origUrl);
                            break;
                        case WEB_TYPE_TV_MEMBERS:
                            goTvMembersWebActivity(origUrl);
                            break;
                        case WEB_TYPE_MOBILE_LIVE :
                            goMobileLiveActivity(origUrl);
                            break;
                        default:
                            goDefaultWebActivity(origUrl);
                    }
                }
            } else {
                // 외부망이면
                handled = false; // 여기서 처리하지 않고 호출한 webView에서 처리
            }
        } else {
            handled = false; // 여기서 처리하지 않고 호출한 webView에서 처리
        }
        return handled;
    }

    /**
     * 웹뷰 url에 필요한 파라미터를 추가한다.
     * (현재는 BACK_TO_MAIN 플래그만 추가가 필요한 상태임)
     *
     * @param intent 인텐트
     * @return 파라미터가 추가된 url
     */
    protected String addParamToUrl(Intent intent) {
        String url = intent.getStringExtra(Keys.INTENT.WEB_URL);
        //스마트카트와 마이샵에 대해서만 url에 파라미터를 추가한다. (나머지 url은 주문 관련하여 영향을 줄 수 있으므로 적용 안함)
        if (!WebUtils.isMyShop(url) && !WebUtils.isSmartCart(url)) {
            return url;
        }

        boolean backToMain = intent.getBooleanExtra(Keys.INTENT.BACK_TO_MAIN, false);
        if (backToMain) {
            try {
                Uri uri = Uri.parse(url);
                //BACK_TO_MAIN 파라미터가 이미 추가된 경우에는 중복해서 추가하지 않도록 함
                if (TextUtils.isEmpty(uri.getQueryParameter(Keys.INTENT.BACK_TO_MAIN))) {
                    String addFlag;
                    if (TextUtils.isEmpty(uri.getQuery())) {
                        addFlag = "?";
                    } else {
                        addFlag = "&";
                    }
                    url = url + addFlag + Keys.INTENT.BACK_TO_MAIN + "=Y";
                }
            } catch (Exception e) {
                // 10/19 품질팀 요청
                // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
                // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
                // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
                Ln.e(e);
            }
        }
        //Ln.i("################# addParamToUrl : " + url);
        return url;
    }

    /**
     * url값이 "http://m.gsshop.com/index"인 경우와 ".../mygsshop/myshopInfo.gs"인 경우 예외처리
     * <p>
     * 20170601 버전 에 적용 내용
     * checkMoveToHome 해당 펑션이 pageStart()에 존재 shouldOverrideUrlLoading() 이동
     * 분명히 과거에 어떠한 이유가 있어서 pageStart에 호출했을것으로 예상되나 알수 없음
     * 사고를 방지 하기 위해 shouldOverrideUrlLoading()에서 호출하는 로직은 WebActivity에만 적용했다.
     * 수정시 webActivity() -> shouldOverrideUrlLoading() ->  checkMoveToHome 주석 확인 필요
     *
     * @param url 웹뷰에서 로딩할 url
     */
    protected boolean checkMoveToHome(String url) {
        haveTabId = false;
        //"http://m.gsshop.com/" 형태의 주소도 앱홈으로 이동하도옥 조건 추가
        //toapp 으로 호출되는 경우는 무시
//		if (!url.startsWith(ServerUrls.APP.TOAPP) && url.indexOf(ServerUrls.ROOT_URL_PATTERN) != -1
//				&& url.indexOf(ServerUrls.URL_PATTERN_GO_URL) == -1
        /*|| (ServerUrls.HTTP_ROOT + "/").equals(url) ) {*/

        int nRootUrlIndex = url.indexOf(ServerUrls.ROOT_URL_PATTERN);
        boolean isStartToApp = url.startsWith(ServerUrls.APP.TOAPP);
        int nGoUrlIndex = url.indexOf(ServerUrls.URL_PATTERN_GO_URL);

        if (!isStartToApp && nRootUrlIndex != -1) { //&& nGoUrlIndex == -1 && !"http://m.gsshop.com/index.gs".equals(url)) {
            Intent intent;
            //웹에서도 메인이 구동되면서 탭이 이동되어야 하는 경우 분기를 태움
            if (MainApplication.isAppView) {
                //m.gsshop.com/index.gs?tabid=xxx&mseq= 처리를 위하여 tabid / mseq 뽑아 내고,
                Uri uri = Uri.parse(url);
                //String mseqParam = null;
                String tabIdParam = null;
                String broadTypeParam = null;
                String openUrlParam = null;
                String lseqParam = null;
                String mediaParam = null;
                String groupParam = null;
                try {
                    uri = Uri.parse(url);
                    //mseqParam = uri.getQueryParameter(MSEQ_PARAM_KEY);
                    tabIdParam = uri.getQueryParameter(TABID_PARAM_KEY);
                    broadTypeParam = uri.getQueryParameter(BROADTYPE_PARAM_KEY);
                    openUrlParam = uri.getQueryParameter(OPEN_URL_PARAM_KEY);
                    lseqParam = uri.getQueryParameter(LSEQ_PARAM_KEY);
                    mediaParam = uri.getQueryParameter(MEDIA_PARAM_KEY);
                    groupParam = uri.getQueryParameter(GROUPCODE_PARAM_KEY);
                } catch (Exception e) {
                    Ln.d(e);
                }

                // TabID Param을 가져 오지 못하는경우가 발생 (인코딩 문제로) 이에 URL을 확인.
                if (TextUtils.isEmpty(tabIdParam)) {
                    String[] arrStrParams = url.split("&");
                    for (String strParam : arrStrParams) {
                        int idxTabID = strParam.indexOf(TABID_PARAM_KEY + "=");
                        if (idxTabID != -1) {
                            tabIdParam = strParam.substring(idxTabID);

                            String[] arrStrTabid = tabIdParam.split("=");

                            if (arrStrTabid.length < 2) {
                                tabIdParam = "";
                                break;
                            }

                            try {
                                tabIdParam = arrStrTabid[1];
                            } catch (IndexOutOfBoundsException e) {
                                Ln.e(e.getMessage());
                                tabIdParam = "";
                                break;
                            }

                            if (!TextUtils.isDigitsOnly(tabIdParam)) {
                                tabIdParam = "";
                            }

                            break;
                        }
                    }
                }

                //#53에서 53 뽑아 내고, home 패턴과(.gsshop.com/index.gs) 동일함을 체크 : 이미 홈패턴임
                //2017년 5월 11일 isValidNumberString 제거
                if (tabIdParam != null) {

                    haveTabId = true;

                    //편성표 진입시 생방 or 데이타 세팅 (디폴트 생방)
                    isCalledFromBanner = true;
                    if (TVScheduleShopFragment.ScheduleBroadType.DATA.name().equalsIgnoreCase(broadTypeParam)) {
                        scheduleBroadType = TVScheduleShopFragment.ScheduleBroadType.DATA;
                    } else {
                        scheduleBroadType = TVScheduleShopFragment.ScheduleBroadType.LIVE;
                    }

                    if (MainApplication.isAppView) {
                        //앱이 이미 실행되어 있는 경우 인트로 화면 로딩없이 바로 홈화면으로 이동
                        intent = new Intent(Keys.ACTION.APP_HOME);
                    } else {
                        //앱이 실행중이 아닐 경우 인트로부터 정상적인 프로세스로 실행
                        intent = ActivityUtils.getMainActivityIntent(context.getApplicationContext());
                    }

                    intent.putExtra(Keys.INTENT.NAVIGATION_ID, tabIdParam);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                } else {
                    intent = new Intent(Keys.ACTION.APP_HOME);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                }

                if (!TextUtils.isEmpty(lseqParam)) {
                    intent.putExtra(Keys.INTENT.INTENT_LSEQ_PARAM, lseqParam);
                }

                if (!TextUtils.isEmpty(mediaParam)) {
                    intent.putExtra(Keys.INTENT.INTENT_MEDIA_PARAM, mediaParam);
                }

                if (!TextUtils.isEmpty(groupParam)) {
                    intent.putExtra(Keys.INTENT.GROUP_CODE_ID, groupParam);
                }

                intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.HOME);
                intent.putExtra(Keys.INTENT.FROM_TAB_MENU, true);
                intent.putExtra(Keys.INTENT.INTENT_OPEN_URL, openUrlParam);

                startActivity(intent);
                finish();
                return true;

            } else {
                intent = new Intent(Keys.ACTION.APP_HOME);
                intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.HOME);
                intent.putExtra(Keys.INTENT.FROM_TAB_MENU, true);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return true;
            }
        }
        return false;
    }

    /**
     * 리다이렉트 페이지(횐화면)를 제거한다.<br>
     * -아래 케이스에 대한 처리를 수행한다.<br>
     * -1.단품-{@literal >}구매하기,장바구니-{@literal >}로그인-{@literal >}주문서.장바구니-{@literal >}백키 클릭시 흰화면 제거<br>
     * -2.홈-{@literal >}바로구매-{@literal >}바로구매,장바구니-{@literal >}로그인-{@literal >}주문서.장바구니-{@literal >}백키 클릭시 흰화면 제거<br>
     * -3.멀티상품-{@literal >}바로구매-{@literal >}바로구매,장바구니-{@literal >}로그인-{@literal >}주문서.장바구니-{@literal >}백키 클릭시 흰화면 제거<br>
     *
     * @param url        웹뷰에서 로딩하는 url
     * @param removeType 흰화면 제거 종류
     */
    protected void removeBlankPage(String url, int removeType) {
        String segment = Uri.parse(url).getLastPathSegment();
        if (!TextUtils.isEmpty(segment) && BASKET_URL.equalsIgnoreCase(segment)) {
            switch (removeType) {
                case BLANK_REMOVE_NOW:
                    removeBlankPageNow();
                    break;
                case BLANK_REMOVE_DELAY:
                    removeBlankPageAfterDelay();
                    break;
            }
        }
    }

	/**
	 * 웹뷰 goBack 또는 액티비티 종료를 수행한다. (딜레이를  주고 수행)
	 */
	private void removeBlankPageAfterDelay() {
		ThreadUtils.INSTANCE.runInUiThread(() -> {
			if (webControl != null && webControl.getWebView() != null && webControl.getWebView().getUrl() != null) {
				//delay후에 웹뷰 url이 BASKET_URL인지를 체크한다.
				//BASKET_URL이 아니라면 다른 url로 리다이렉트된 경우므로 이때는 goBackOrFinish를 수행하지 않는다.
				String segment =  Uri.parse(webControl.getWebView().getUrl()).getLastPathSegment();
				if (!TextUtils.isEmpty(segment) && BASKET_URL.equalsIgnoreCase(segment)) {
					goBackOrFinish();
				}
			}
		}, 3000);
	}

    /**
     * 웹뷰 goBack 또는 액티비티 종료를 수행한다. (딜레이 없이 수행)
     */
    private void removeBlankPageNow() {
        if (webControl != null && webControl.getWebView() != null) {
            goBackOrFinish();
        }
    }

    /**
     * 웹뷰 goBack 또는 액티비티 종료를 수행한다.
     */
    private void goBackOrFinish() {
        if (webControl.getWebView().canGoBackOrForward(-1)) {
            webControl.getWebView().goBack();
        } else {
            finish();
        }
    }

    /**
     * 웹뷰 url이 홈화면 이동 대상인지 여부를 반환한다.
     *
     * @return 홈화면 이동 대상인지 여부
     */
    private boolean isBackToHomeUrl() {
        boolean retVal = false;
        if (webControl != null && webControl.getWebView() != null && webControl.getWebView().getUrl() != null) {
            String segment = "/" + Uri.parse(webControl.getWebView().getUrl()).getLastPathSegment();
            retVal = !TextUtils.isEmpty(segment) && backToHomeUrlList.contains(segment);
        }
        return retVal;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PRODUCT_IMAGE_URL, productImgUrl);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQCODE.PERMISSIONS_REQUEST_STORAGE:
                if (PermissionUtils.verifyPermissions(this, permissions, grantResults)) {
                    if (!TextUtils.isEmpty(productImgUrl)) {
                        new SaveImageController(BaseWebActivity.this).execute(productImgUrl);
                    }
                }
                break;
            case REQCODE.PERMISSIONS_REQUEST_STORAGE_CAMERA:
                if (PermissionUtils.verifyPermissions(this, permissions, grantResults)) {
                    FileAttachUtils.startCamera(this);
                }
                break;
            case REQCODE.PERMISSIONS_REQUEST_STORAGE_CAMERA_CUSTOM:
                if (PermissionUtils.verifyPermissions(this, permissions, grantResults)) {
                    FileAttachUtils.startCamera(this, true);
                }
                break;
            case REQCODE.PERMISSIONS_REQUEST_STORAGE_CAMERA_CUSTOM_FROM_SEARCH:
                if (PermissionUtils.verifyPermissions(this, permissions, grantResults)) {
                    FileAttachUtils.startCamera(this, true, true);
                }
                break;
            case REQCODE.PERMISSIONS_REQUEST_STORAGE_GALLERY:
            case REQCODE.PERMISSIONS_REQUEST_STORAGE_GALLERY_FROM_IMG_SEARCH:
                if (PermissionUtils.verifyPermissions(this, permissions, grantResults)) {
                    boolean isImageSearching = false;
                    if (requestCode == REQCODE.PERMISSIONS_REQUEST_STORAGE_GALLERY_FROM_IMG_SEARCH) {
                        isImageSearching = true;
                    }
                    FileAttachUtils.startGallery(this, FileAttachUtils.IMAGE_COUNT, isImageSearching);
                }
                break;
            case REQCODE.PHOTO_VIDEO:
                if (PermissionUtils.verifyPermissions(this, permissions, grantResults)) {
                    FileAttachUtils.goGalleryVideo(this, REQCODE.PHOTO_VIDEO);
                }
                break;
            //pickup service
            case REQCODE.PERMISSIONS_REQUEST_LOCATION:
                boolean isPermitted = PermissionUtils.verifyPermissions(this, permissions, grantResults);
                if (geoCallback != null && geiOrigin != null) {
                    geoCallback.invoke(geiOrigin, isPermitted, false);
                }
                if (isPermitted) {
                    showGPSEnablePopup((Activity) context);
                } else {
                    //GPS ON 시스템팝업에서 취소 클릭시 "onGeolocationPermissionsShowPrompt" 콜백 호출되고
                    //GPS ON 시스템팝업이 다시 떠서 예외처리.
                    //다른 처리방법이 있는지는 추가로 확인이 필요함
                    callbackSkipFlag = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            callbackSkipFlag = false;
                        }
                    }, 3000);
                }
                break;
            case REQCODE.PERMISSIONS_REQUEST_SMS:
                if (PermissionUtils.verifyPermissions(this, permissions, grantResults)) {
                    //별도 수행할 작업 없음
                }
                break;
            default:
                break;
        }
    }

    /**
     * SNS팝업에서 SNS를 선택했을때 호출되는 콜백
     *
     * @param shareType SNS 종류
     */
    @Override
    public void onSnsSelected(SnsV2DialogFragment.SHARE_TYPE shareType) {
        if (SnsV2DialogFragment.SHARE_TYPE.KakaoTalk.equals(shareType)) { //카카오톡이 selected
            checkAppInstall(PCK_KAKAOTALK, shareType, R.string.sns_kakaotalk_not_installed);

        } else if (SnsV2DialogFragment.SHARE_TYPE.KakaoStory.equals(shareType)) { //카카오스토리가 selected
            checkAppInstall(PCK_KAKAOSTORY, shareType, R.string.sns_kakaostory_not_installed);

        } else if (SnsV2DialogFragment.SHARE_TYPE.Line.equals(shareType)) { //라인이 selected
            checkAppInstall(PCK_LINE, shareType, R.string.sns_line_not_installed);

        } else if (SnsV2DialogFragment.SHARE_TYPE.Twitter.equals(shareType)) { //트위터가 selected
            checkAppInstall(PCK_TWITTER, shareType, R.string.sns_twitter_not_installed);

        } else if (SnsV2DialogFragment.SHARE_TYPE.Pinterest.equals(shareType)) { //핀터레스트가 selected
            checkAppInstall(PCK_PINTEREST, shareType, R.string.sns_pinterest_not_installed);

        } else if (SnsV2DialogFragment.SHARE_TYPE.SMS.equals(shareType)) { //SMS가 selected
            if (webControl != null && webControl.getWebView() != null) {
                webControl.loadUrl("javascript:" + shareType.getScriptName() + "()");
            }
        } else if (SnsV2DialogFragment.SHARE_TYPE.Url.equals(shareType)) { //Url 복사가 selected
            if (webControl != null && webControl.getWebView() != null) {
                webControl.loadUrl("javascript:" + shareType.getScriptName() + "()");
            }
        } else if (SnsV2DialogFragment.SHARE_TYPE.Etc.equals(shareType)) { //다른 앱 선택하기가 selected
            if (webControl != null && webControl.getWebView() != null) {
                webControl.loadUrl("javascript:" + shareType.getScriptName() + "()");
            }
        }
        //페이스북의 경우 다른 시나리오를 탄다
        //설치시: 페이스북SDK 호출,  미설치시 : 페이스북 Web 공유 API 호출
        else if (SnsV2DialogFragment.SHARE_TYPE.Facebook.equals(shareType)) { //페이스북이 selcted
            Ln.i("shareType : " + shareType.toString().toLowerCase() + ", scriptName : " + shareType.getScriptName());
            if (webControl != null && webControl.getWebView() != null) {
                webControl.loadUrl("javascript:" + shareType.getScriptName() + "()");
            }
        }
    }

    /**
     * 앱 설치유무 판단 / 미설치시 팝업띄움
     *
     * @param packageName  //패키지명
     * @param noAppMessage //미설치시 팝업 문구
     * @return
     */
    public void checkAppInstall(String packageName, SnsV2DialogFragment.SHARE_TYPE share_type, int noAppMessage) {
        try {
            //앱 설치
            PackageInfo i = this.getPackageManager().getPackageInfo(packageName, PackageManager.GET_CONFIGURATIONS);
            Ln.i("shareType : " + share_type.toString().toLowerCase() + ", scriptName : " + share_type.getScriptName());
            if (webControl != null && webControl.getWebView() != null) {
                webControl.loadUrl("javascript:" + share_type.getScriptName() + "()");
            }
        } catch (Exception e) {
            //미설치시 팝업 띄움
            new CustomOneButtonDialog(this).message(noAppMessage)
                    .cancelable(false)
                    .buttonClick(new CustomOneButtonDialog.ButtonClickListener() {
                        @Override
                        public void onClick(Dialog dialog) {
                            dialog.dismiss();
                        }
                    }).show();
        }
    }

    /**
     * SNS팝업에서 닫기버튼을 선택했을때 호출되는 콜백
     */
    @Override
    public void onCloseSelected() {
        //필요시 사용 예정
    }

    /**
     * GPS 활성화 팝업을 노출한다.
     *
     * @param activity 액티비티
     */
    public static void showGPSEnablePopup(final Activity activity) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(MainApplication.getAppContext())
                .addApi(LocationServices.API).build();
        googleApiClient.connect();
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                .checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result
                        .getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Ln.i("GPS SUCCESS");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Ln.i("GPS RESOLUTION_REQUIRED");
                        try {
                            // GPS on/off 팝업 노출
                            status.startResolutionForResult(activity, REQCODE.GPS);

                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Ln.i("GPS SETTINGS_CHANGE_UNAVAILABLE");
                        break;
                }
            }
        });
    }

    /**
     * 카카오 로그인 초기화
     */
    public void initKakao() {
        callback = new SessionCallback();
        getCurrentSession().addCallback(callback);
        // 초기화 수행시 checkAndImplicitOpen 실행하면, 조건 만족시 SDK 자체적으로 백그라운드 로그인 수행 후
        // SessionCallback 호출하여 오동작 발행가능 (로그인 후 MyShopWebActivity 로딩시 발생)
        //Session.getCurrentSession().checkAndImplicitOpen();
    }

    /**
     * 네이버 로그인 초기화
     */
    public void initNaverLogin() {
        mOAuthLoginInstance = OAuthLogin.getInstance();
        mOAuthLoginButton.setOAuthLoginHandler(mOAuthLoginHandler);
    }

    /**
     * 네이버 로그인 수행
     *
     * @param clearYn "Y"면 로그아웃 수행
     */
    public void authNaver(String clearYn) {
        if ("Y".equalsIgnoreCase(clearYn)) {
            //다른 계정 선택할 수 있도록 로그아웃. 로그아웃 수행안하면 기존 로그인했던 계정으로 계속 로그인 수행함
            mOAuthLoginInstance.logout(context);
        }
        mOAuthLoginButton.performClick();
    }

    /**
     * 카카오 로그인 수행
     *
     * @param clearYn "Y"면 로그아웃 수행
     *                카카오의 경우 계정선택 팝업이 항상 노출되므로 로그아웃 수행을 별도로 하지 않음
     */
    public void authKakao(String clearYn) {
        if (Session.getCurrentSession().isOpened()) {
            Session.getCurrentSession().close();
        }

        mKakaoLoginButton.performClick();
    }

    /**
     * SNS 로그인 실패시 스크립트 함수를 호출한다.
     *
     * @param snsType SNS 종류
     */
    public void snsLoginFailed(String snsType) {
        if (webControl != null && webControl.getWebView() != null) {
            webControl.getWebView().loadUrl(String.format("javascript:memberAction('%s', '%s', '%s', '%s')", "FAIL", "", "", snsType));
        }
    }

    /**
     * 네이버 SNS로그인 콜백
     */
    private OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
        @Override
        public void run(boolean success) {
            if (success) {
                String accessToken = mOAuthLoginInstance.getAccessToken(context);
                String refreshToken = mOAuthLoginInstance.getRefreshToken(context);
                if (webControl != null && webControl.getWebView() != null) {
                    webControl.getWebView().loadUrl(String.format("javascript:memberAction('%s', '%s', '%s', '%s')",
                            "SUCC", accessToken, refreshToken, LoginActivity.SNS_TYPE.NA.toString()));
                }
            } else {
                OAuthErrorCode authError = mOAuthLoginInstance.getLastErrorCode(context);
                Ln.i("authError: " + authError);
                // ignore hardware back button & accept cancel button
                if (authError != OAuthErrorCode.CLIENT_USER_CANCEL
                        && authError != OAuthErrorCode.SERVER_ERROR_ACCESS_DENIED) {
                    String errorCode = mOAuthLoginInstance.getLastErrorCode(context).getCode();
                    String errorDesc = mOAuthLoginInstance.getLastErrorDesc(context);
                    Toast.makeText(context, "errorCode:" + errorCode + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();
                    snsLoginFailed(LoginActivity.SNS_TYPE.NA.toString());
                }
            }
        }
    };

    /**
     * 카카오 SNS로그인 콜백
     */
    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            String accessToken = getCurrentSession().getTokenInfo().getAccessToken();
            String refreshToken = getCurrentSession().getTokenInfo().getRefreshToken();
            if (webControl != null && webControl.getWebView() != null) {
                webControl.getWebView().loadUrl(String.format("javascript:memberAction('%s', '%s', '%s', '%s')",
                        "SUCC", accessToken, refreshToken, LoginActivity.SNS_TYPE.KA.toString()));
            }
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {

            // ignore hardware back key & link deny button
            if (exception != null && exception.getErrorType() != KakaoException.ErrorType.CANCELED_OPERATION) {
                Ln.e(exception);
                snsLoginFailed(LoginActivity.SNS_TYPE.KA.toString());
            }
        }
    }

}