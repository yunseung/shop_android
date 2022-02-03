/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.webkit.WebView;

import androidx.multidex.MultiDex;

import com.appboy.Appboy;
import com.appboy.configuration.AppboyConfig;
import com.appboy.support.AppboyLogger;
import com.blankj.utilcode.util.EmptyUtils;
import com.blankj.utilcode.util.Utils;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.gsshop.mocha.BaseApplication;
import com.gsshop.mocha.RoboGuiceInitializer;
import com.gsshop.mocha.device.AppInfo;
import com.gsshop.mocha.network.cookie.SimpleCookieManager;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.network.util.ProxyUtils;
import com.gsshop.mocha.network.util.ProxyUtils.ProxyInfo;
import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;
import com.nhn.android.naverlogin.OAuthLogin;
import com.orhanobut.hawk.Hawk;
import com.tms.inappmsg.InAppMessageLifecycleCallback;
import com.tms.inappmsg.InAppMessageManager;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ai.evolv.AscendClient;
import co.ab180.airbridge.Airbridge;
import co.ab180.airbridge.AirbridgeConfig;
import gsshop.mobile.v2.attach.FileAttachParamInfo;
import gsshop.mobile.v2.attach.PhotoItem;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.HomeGroupInfo;
import gsshop.mobile.v2.intro.StartNextActivityCommand;
import gsshop.mobile.v2.library.objectcache.CacheManager;
import gsshop.mobile.v2.library.objectcache.DiskCache;
import gsshop.mobile.v2.sso.SSOControl;
import gsshop.mobile.v2.user.UserConnector.SSOSdkParam;
import gsshop.mobile.v2.util.LunaUtils;
import gsshop.mobile.v2.util.SSOUtils;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.RoboGuice;
import roboguice.inject.InjectResource;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;

//import com.clevertap.android.sdk.ActivityLifecycleCallback;
//import com.clevertap.android.sdk.CleverTapAPI;

/**
 * 메인 애플리케이션.
 */
public class MainApplication extends BaseApplication {

    /**
     *  global uncaught exception handler
     */
    private Thread.UncaughtExceptionHandler mDefaultUncaughtExceptionHandler;

    /**
     * SSO 채널코드
     */
    public static final String channelCode = "3013";

    /**
     * 상품이미지 캐싱로직 사용여부
     */
    public static boolean useNativeProduct = true;

    /**
     * 네이티브 프로덕트 무조건 쓰게 끔 하는 값 (단 useNativeProduct 가 true일 떄만) false 이면 내려오는 값에 따름.
     */
    public static boolean mustNativeProduct = false;

    /**
     * 앱 구동 후 자동로그인 수행 가능 횟수
     */
    public static int autoLoginTryLimit = 5;
    /**
     * 앱 구동 후 자동로그인 수행 횟수
     */
    public static int autoLoginTryCount = 0;
    /**
     * 자동로그인 수행가능 여부
     */
    public static boolean autoLoginTryEnabled = false;

    /**
     * 3g 과금 팝업 상태 앱이 종료할때까지 상태 유지.
     */
    public static boolean isNetworkApproved = false;
    /**
     * 사운드 뮤트
     */
    public static boolean isMute = false;
    public static boolean nativeProductIsMute = true;   // 단품 네이티브 음소거
    public static boolean isVodShopLoaded = false;      // vod 매장 로딩 유무
    public static boolean isLiveTalkMute = true;       // 라이브톡 음소거
    /**
     * 영상 재생 및 돌아올때에 영상 위치 공유를 위한 전역 변수
     */
    public static long gVideoCurrentPosition = 0;
    public static boolean gVideoIsPlaying = false;

    /**
     * 자동플레이 여부 (Y or N)
     */
    public static String isAutoPlay = "N";

    /**
     * 새벽배송 툴 팁 앱 실행중 1회 노출 flag
     */
    public static boolean isShownNightDeliveryTooltip = false;

    /**
     * naver simple login
     */
    private static String OAUTH_CLIENT_ID = "lKFdFcYB3_zG0kl3EjH6";
    private static String OAUTH_CLIENT_SECRET = "GXvuKrulRq";
    private static String OAUTH_CLIENT_NAME = "gsshop";

    /**
     * 와이즈로그 전송용 웹뷰
     */
    public static WebView wiselogWebview;

    /**
     * 초기 구동 및 속도 측정시 사용되는 기준 시간
     */
    public static long appStartTime = 0L;
    /**
     * 인트로 작업이 끝났는가.
     * <p>
     * - {@link StartNextActivityCommand} 작업이 완료될 때 true로 설정. -
     * ConfirmExitBackHandler에서 앱을 종료시킬 때 false로 설정.
     * <p>
     */
    public static boolean introCompleted = false;

    /**
     * 네비게이션 정보를 여부
     */
    public static boolean isGetHomeMenu = false;

    /**
     * Web // APP 구분 코드 앱 웹 구분???
     */
    public static boolean isAppView = false;

    /**
     * 홈액티비티에서 커맨드를 1회만 수행하기 위한 플래그
     */
    public static boolean isHomeCommandExecuted = false;

    /**
     * 현재 앱의 버전 정보
     */
    public static String appVersionName = "";

    /**
     * 와이즈로그 : 베스트딜 최초 호출시에만 'appstart=Y'파라미터를 추가하기 위한 플래그
     */
    public static boolean isBestdealCalled = false;

    /**
     * 선물하기 toapp 호출하는 함수명
     */
    public static String giftFunc = "setReceiveInfo";

    @Inject
    private PackageInfo packageInfo;

    // 해더 이름
    @InjectResource(R.array.custom_header_names)
    private String[] customHeaderNames;

    // 해더 값
    @InjectResource(R.array.custom_header_values)
    private String[] customHeaderValues;

    /**
     * 해더 저장 맵
     */
    public static Map<String, String> customHeaders = null;

//    // volley image loader
//    public static RequestQueue mVolleyQueue = null;
//    public static com.android.volley.toolbox.ImageLoader mImageLoader = null;

    /**
     * tensera
     */
//    @InjectResource(R.bool.tensera_active_by_host)
//    private boolean tenseraActiveByHost;

    /**
     * GA App Speed 측정을위한 HashMap
     */
    private final HashMap<String, TimingInfo> timingInfo = new HashMap<String, TimingInfo>();
    /**
     * 인트로 구동 속도 디파인
     */
    public static String INTRO = "INTRO";

    /**
     * 파일첨부 정보
     */
    public static FileAttachParamInfo fileAttachParamInfo;

    /**
     * 갤러리 이미지 pick
     */
    public static List<PhotoItem> articlePhotoes;

    /**
     * 사진찍기 이미지 경로
     */
    public static File attechImagePath;

    /**
     * 커스텀 사진 찍기 이미지 경로
     */
    public static String tempImagePath;

    /**
     * 갤러리로 부터 가져왔는지 여부 저장.
     */
    public static boolean isFromGallery = true;

    /**
     * 갤러리로 부터 가져온 이미지가 수정되는지 여부 저장.
     */
    public static boolean isEditFromGallery = true;

    /**
     * API Cashe 용
     */
    private final HashMap<String, HomeGroupInfo> homeGroupInfo = new HashMap<String, HomeGroupInfo>();

    /**
     * GTM Ecommerce에서 사용할 스크린 이름
     */
    public static String gtmScreenName = "";

    /**
     * 캐쉬 정보
     */
    public static DiskCache DISK_CACHE;

    /**
     * 접근권한고지 통해 들어온것인지 확인
     */
    public static boolean isAuthCheck = true;

    /**
     * 푸시를 통해 들어온것인지 확인, 지금 보니 정확한 판단은 어렵
     */
    public static boolean isPushCheck = true;

    /**
     * 접근권한 팝업을 고지할 버전코드
     * 재고지가 필요하면 배포될 버전코드로 세팅하면 됨
     * 20180802 205 SMS 권한을 위해 업데이트 20180802
     * 20180830 208 지문 권을 위해 업데이트 20180830
     */
    public static final int AUTH_NOTICE_VER_CODE = 208;

    /**
     * POC GS CHOICE 탭명 AB 테스트 타이틀값
     * 478 네비 번호
     */
    //public static String GS_CHOICE_TITLE = "";


    /**
     * private AscendClient client;
     */
    public static AscendClient client  = null;

    /**
     * 홈이 로딩된적 있는지 체크
     * default : FIRST(첫로딩) / NONE(첫로딩 이후)
     */
    public static String homeLoaded = "FIRST";

    /**
     * 백키로 왔는지 홈버튼으로 왔는지 체크
     */
    public static boolean calledFromBackKey = true;

    /**
     * 앱티마이즈 임시 플래그
     */
    public static boolean apptimizeWaitFlag = false; //HomeMenuCommand에서 execute할때 0.3초 기다릴지말지 플래그 (아직 네비 호출 안했으면 0.3초 기다린다)
    public static boolean apptimizeNaviFlag = false; //네비 호출여부 플래그

    /**
     * Hyper-Personalized Curation
     * 데이터 업데이트시 쓰일 최근본상품 prdid
     */
    public static String lastPrdId = "";

    /**
     * getHomeGroupInfo 네비정보 캐쉬 가져오기
     *
     * @param key 네비키
     * @return
     */
    public HomeGroupInfo getHomeGroupInfo(String key) {
        return homeGroupInfo.get(key);
    }

    /**
     * 캐쉬 저장
     *
     * @param key 해당 키
     * @param v   오브젝트
     */
    public void save(String key, HomeGroupInfo v) {
        homeGroupInfo.put(key, v);
    }

    /**
     * caCheHolder clear
     */
    public void clear() {
        homeGroupInfo.clear();
    }

    /**
     * 진저브레드에는 멀티 덱스 사용 안 함
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * 속도를 측정을 위한 시작 시작 시간 설정
     *
     * @param key 시작시간을 적재할 key
     *            ex) url 또는  Action 명 머가 좋을까??
     * @return boolean
     */
    public boolean setStartTiming(String key) {
        if (key != null) {
            long startTime = System.currentTimeMillis();
            if (startTime > 0) {
                TimingInfo temp = new TimingInfo(startTime);
                timingInfo.put(key, temp);
                return true;
            }
        }
        return false;
    }

    /**
     * 설정했던 타이밍에 대한 결과를 얻어 온다.
     *
     * @param key ke
     * @return long
     */
    public long getTiming(String key) {
        try {
            if (key != null) {
                long endTime = System.currentTimeMillis();
                if (endTime > 0) {
                    TimingInfo temp = timingInfo.get(key);
                    if (temp != null && temp.useflag == true) {
                        temp.useflag = false;
                        long startTime = temp.startTime;
                        if (startTime < endTime) {
                            timingInfo.remove(key);
                            return endTime - startTime;
                        }
                    }
                    timingInfo.remove(key);
                }
            }
        } catch (Exception e) {
            // 10/19 품질팀 요청
            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
            // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
            Ln.e(e);
        }
        return 0;
    }

    /**
     * 타이및 Map 삭제
     *
     * @param key key
     */
    public void removeTiming(String key) {
        if (key != null) {
            timingInfo.remove(key);
        }
    }

    @Override
    public void onCreate() {
        //clevertap
//        ActivityLifecycleCallback.register(this);
//        CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.OFF);

        super.onCreate();

        // 스레드로 빼
        // RoboGuice 초기화
        RoboGuiceInitializer.init(this, new MainModule(this));
        // debug initialzation
        DebugUtils.initDebug(this);
        // SSO 초기화
        initSSO();
        // kakao simple login init
        KakaoSDK.init(new KakaoSDKAdapter());
        // naver simple login init
        OAuthLogin.getInstance().init(getAppContext(), OAUTH_CLIENT_ID, OAUTH_CLIENT_SECRET, OAUTH_CLIENT_NAME);
        // airbridge
        AirbridgeConfig config = new AirbridgeConfig.Builder(getResources().getString(R.string.airbridge_app_name), getResources().getString(R.string.airbridge_app_token))
                .build();
        Airbridge.init(this, config);

        //TMS
        registerActivityLifecycleCallbacks(new InAppMessageLifecycleCallback());
        InAppMessageManager.getInstance().setIsInAppMsgActive(true);
        // test 용 ( 5분안에 호출시 같은 이벤트 인앱 메세지 호출 이력 있을 때에 true : 노출 , false : 비노출, 기본이 fal
//        InAppMessageManager.getInstance().setCashEventIdDisabled(true);

        //appboy
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (!"true".equals(getAppContext().getString(R.string.skip_appboy))) {
                AppboyLogger.setLogLevel(Log.ERROR);
                //configureAppboyAtRuntime();

                //브레이즈 인앱 메세지 안뜨게할 액티비티 리스트
//                Set<Class> inAppMessageBlacklist = new HashSet<>();
//
//                inAppMessageBlacklist.add(LoginActivity.class);
//                inAppMessageBlacklist.add(OrderNoTabWebActivity.class);
//                inAppMessageBlacklist.add(HomeSearchActivity.class);
//                registerActivityLifecycleCallbacks(new AppboyLifecycleCallbackListener(inAppMessageBlacklist));
            }
        }

        /**
         * tensera
         * 1. add the following to OnCreate method:
         */
//        try{
//            if (tenseraActiveByHost) {
//                Context context = getApplicationContext();
//                final TenseraConfig config = new TenseraConfig()
//                        .setPreloadConfig(this, HomeActivity.class, IntroActivity.class,
//                                true).setTenseraEnv("korea").setPackageName(getPackageName());
//                TenseraApi.init(context, config);
////            TenseraPreloadSdk.init(context);
//            } else {
//                TenseraApi.initDisabled(this);
//            }
//        }catch (Exception e)
//        {
//            //무슨일이 날줄 알고
//        }

        //global exception handler
        mDefaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        //custom exception handler
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());

        // AndroidUtilCode
        Utils.init(this);
        // Secure, simple key-value storage for android
        Hawk.init(this).build();
        // 쿠키 공유를 위한 초기화
        SimpleCookieManager.getDefault();

        //PC 크롬브라우저에서 앱의 웹뷰를 디버깅할수 있도록 함
        //운영 버전에서 일시적으로 빼도록 수정 11/10 이민수
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && AppInfo.isDebugMode(getApplicationContext())) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        */

        patchEOFException();

        // facebook Event Logger setting
        //AppEventsLogger logger = AppEventsLogger.newLogger(this);

        // setHttpProxy(ip, port);

        // volley image loader 사용안함(이민수)
//        mVolleyQueue = Volley.newRequestQueue(getApplicationContext());
//        mImageLoader = new com.android.volley.toolbox.ImageLoader(mVolleyQueue,
//                new BitmapLruImageCache(getCacheSize()));

        //이미지 로더는 기본적으로 응답을 100ms마다 몰아서 처리하게 되어있습니다.
        //이러한 배치 처리 기능을 끄면 리스트 뷰 스크롤 성능은 떨어지지만,
        //이미지가 빨리 불러와지는 것처럼 보이게 할 수 있습니다.
        //setBatchedResponseDelay(0)을 호출하여 끕니다.
        //mImageLoader.setBatchedResponseDelay(0);

        //현재 앱의 버전정보 저장
        appVersionName = AppInfo.getAppVersionName(packageInfo);

        //웹뷰 로딩시 전달할 커스텀 해더 세팅
        customHeaders = new HashMap<String, String>();
        for (int i = 0; i < customHeaderNames.length; i++) {
            customHeaders.put(customHeaderNames[i], customHeaderValues[i]);
        }


        try {
            //파일캐시 설정
            String cachePath = getCacheDir().getPath();
            File cacheFile = new File(cachePath + File.separator + getPackageName());
            DISK_CACHE = new DiskCache(cacheFile, Build.VERSION.SDK_INT, 1024 * 1024 * 2);
//			DISK_CACHE.clearCache();
        } catch (Exception e) {
            // 10/19 품질팀 요청
            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
            // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
            Ln.e(e);
        }
    }

    /**
     * SSO 초기화
     */
    private void initSSO() {
        // MainActivity 객체가 생성되지 않은 상태에서 토큰 정보를 주고 받는 경우가
        // 발생할 수 있기 때문에 Application에서 초기화를 진행한다.
        SSOControl.getInstance().initialize(getApplicationContext());

        // SSO 토큰을 검증할 지 여부를 결정정합니다.
        // 기본 속성값은 true이기 때문에 GS SHOP에서는 주석을 풀고 사용하시면 됩니다.
        SSOControl.getInstance().setVerifySSOAuthToken(false);

        // 우선 다른 앱들로부터 토큰 정보를 가져오는 것을 시도하고,
        // 만약에 가져올 수 있는 토큰 정보가 없으면 로컬에 저장되어 있는 토큰을 읽어옵니다.
        SSOControl.getInstance().loadToken();

        // SDK 내부 코드에서 Application 객체의 Context가 필요할 경우 setOnNeedContext()를 통해서 레퍼런스를 전달하도록 합니다.
        SSOControl.getInstance().setOnNeedContext(
                () -> {
                    return MainApplication.this.getApplicationContext();
                }
        );

        //토큰정보 수신
        SSOControl.getInstance().setOnToken(text -> {
            //getToken 호출 30ms~100ms 후 콜백
            if (isNotEmpty(text)) {
                SSOSdkParam result = new Gson().fromJson(text, SSOSdkParam.class);
                String ssoToken = result.getSsoAuthToken();
                Ln.d("setOnToken > ssoToken: [" + ssoToken + "]");
            }
        });

        //getToken 호출해야 위 onString 콜백 떨어짐
        SSOControl.getInstance().getToken(SSOUtils.getSSOParamType1());
    }

    /**
     * kakao simple login
     */
    private static class KakaoSDKAdapter extends KakaoAdapter {
        /**
         * Session Config에 대해서는 default값들이 존재한다.
         * 필요한 상황에서만 override해서 사용하면 됨.
         *
         * @return Session의 설정값.
         */
        @Override
        public ISessionConfig getSessionConfig() {
            return new ISessionConfig() {
                @Override
                public AuthType[] getAuthTypes() {
                    return new AuthType[]{AuthType.KAKAO_LOGIN_ALL};
                }

                @Override
                public boolean isUsingWebviewTimer() {
                    //false 리턴하면 카카오웹뷰 로딩 안되는 현상 발생
                    return true;
                }

                @Override
                public boolean isSecureMode() {
                    return false;
                }

                @Override
                public ApprovalType getApprovalType() {
                    return ApprovalType.INDIVIDUAL;
                }

                @Override
                public boolean isSaveFormData() {
                    return true;
                }
            };
        }

        @Override
        public IApplicationConfig getApplicationConfig() {
            return new IApplicationConfig() {
                @Override
                public Context getApplicationContext() {
                    return MainApplication.getAppContext();
                }
            };
        }
    }

    /**
     * HttpURLConnection을 이용하여 POST방식으로 HTTP 통신시 가끔 발생하는 java.io.EOFException 오류
     * 패치(주로 갤럭시 폰에서 발생함).
     * <p>
     * http://stackoverflow.com/questions/13182519/spring-rest-template-usage-
     * causes-eofexception https://jira.springsource.org/browse/ANDROID-102
     * http:
     * //stackoverflow.com/questions/12280629/nullpointerexception-when-using
     * -spring-resttemplate-in-android
     * http://android-developers.blogspot.fr/2011/09/androids-http-clients.html
     * http
     * ://stackoverflow.com/questions/3352424/httpurlconnection-openconnection
     * -fails-second-time
     */
    private void patchEOFException() {
        System.setProperty("http.keepAlive", "false");
    }

    /**
     * HTTP 통신내용을 보기위한 프록시 설정. WebView는 제외되며, WebView 통신내용은
     * {@link ProxyUtils#setWebViewProxy(WebView, ProxyInfo)}를 이용할 것.
     *
     * @param ip   ip
     * @param port port
     */
    @SuppressWarnings("unused")
    private void setHttpProxy(String ip, int port) {
        ProxyInfo p = new ProxyInfo(ip, port);
        ProxyUtils.setURLConnectionProxy(p);
        RestClient rest = RoboGuice.getInjector(getApplicationContext()).getInstance(
                RestClient.class);
        ProxyUtils.setRestTemplateProxy(rest, p);
    }

    /**
     * 캐시사이즈 최대값을 리턴한다.(Byte)
     *
     * @return 캐시사이즈 최대값
     */
    public int getCacheSize() {
        int memoryClass = ((ActivityManager) getApplicationContext().getSystemService(
                Context.ACTIVITY_SERVICE)).getMemoryClass();
        // 힙메모리의 1/3
//        int cashSize = 1024 * 1024 * memoryClass / 5;
        return 1024 * 1024 * memoryClass / 5;
    }

    /**
     * 와이즈로그용 웹뷰를 생성한다.
     *
     * @return 웹뷰
     */
    public static WebView getWiselogWebview() {
        if (wiselogWebview == null) {
            wiselogWebview = new WebView(getAppContext());
        }
        return wiselogWebview;
    }

    /**
     * appboy configuration
     * FCM 수정
     */
    private void configureAppboyAtRuntime() {
        AppboyConfig appboyConfig = new AppboyConfig.Builder()
                .setDefaultNotificationChannelName("Appboy Push")
                .setDefaultNotificationChannelDescription("Appboy related push")
                .setPushDeepLinkBackStackActivityEnabled(true)
                .setPushDeepLinkBackStackActivityClass(HomeActivity.class)
                .setHandlePushDeepLinksAutomatically(true).build();
        Appboy.configure(this, appboyConfig);
    }

    /**
     * 캐쉬를 초기화한다.
     */
    public static void clearCache() {
        CacheManager cacheManager = CacheManager.getInstance(DISK_CACHE);
        if (cacheManager != null) {
            try {
                cacheManager.clear();
            } catch(Exception e) {
                Ln.e(e);
            }
        }
    }

    /**
     * 명시적으로 캐치한 익셉션 외에 앱크래시 발생한 경우 루나로 전송한다.
     */
    private class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            LunaUtils.sendToLuna(getAppContext(), e, getClass().getSimpleName());
            mDefaultUncaughtExceptionHandler.uncaughtException(t, e);
        }
    }
}
