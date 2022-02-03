/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ProgressBar;

import com.appboy.models.IInAppMessage;
import com.appboy.models.MessageButton;
import com.appboy.ui.inappmessage.AppboyInAppMessageManager;
import com.appboy.ui.inappmessage.InAppMessageCloser;
import com.appboy.ui.inappmessage.InAppMessageOperation;
import com.appboy.ui.inappmessage.listeners.IInAppMessageManagerListener;
import com.blankj.utilcode.util.EmptyUtils;
import com.google.inject.Inject;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.web.WebViewProgressBar;

import java.util.Locale;

import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.DirectOrderView;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.support.gtm.GTMAction;
import gsshop.mobile.v2.support.gtm.GTMEnum;
import gsshop.mobile.v2.util.ClickUtils;
import gsshop.mobile.v2.util.CookieUtils;
import roboguice.util.Ln;

import static com.kakao.auth.Session.getCurrentSession;
import static gsshop.mobile.v2.home.HomeActivity.psnlCurationType;
import static gsshop.mobile.v2.home.HomeActivity.showPsnlCuration;
import static gsshop.mobile.v2.web.WebUtils.isSmartCart;

/**
 * 웹페이지를 보여주는 액티비티.
 * <p>
 * 인텐트에 Keys.INTENT.WEB_URL 키를 사용하여 로딩할 웹페이지 주소를
 * 전달할 수 있다.
 */
@SuppressLint("NewApi")
public class WebActivity extends BaseWebActivity implements IInAppMessageManagerListener {

    private static final String TAG = "WebActivity";

    /**
     * 리다이렉트 검출
     */
    private RedirectChecker redirectChecker;

    @Inject
    private RestClient restClient;

    protected WebView webView;

    private boolean isBackkeyPressed = false;

    // protected String currentUrl = ""; //브레이즈 팝업 체크위해 현재 url받는다

    /**
     * 브레이즈 팝업 안뜨는곳 리스트
     */
    private String[] noBrazeUrlList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web);

        // 하단 탭바에서 shadow 높이를 뺀 만큼 webview에 bottom margin 설정
        webView = (WebView) findViewById(R.id.webview);
        LayoutParams p = (LayoutParams) webView.getLayoutParams();
        p.bottomMargin = getResources()
                .getDimensionPixelSize(R.dimen.tab_menu_height);
        webView.setLayoutParams(p);

        setupWebControl();
        //setDummyWebView();

        setupRefreshLayout();

        loadUrl();

        //위젯에서 호출된 경우 GTM 이벤트를 전달한다.
        sendGtmEvent();

        //바로구매
        directOrderLayout = (DirectOrderView) findViewById(R.id.direct_order_layout);
        directOrderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOrderDirectWebView();
            }
        });

        //브레이즈 팝업 안뜨는곳 리스트(ex. 회원가입)
        noBrazeUrlList = context.getResources().getStringArray(R.array.no_braze_list);
        //http 혹은 https로 시작하면 currentUrl에 set
        //setCurrentUrl(webControl.getWebView().getUrl());
        //리스트에 있는곳이면 브레이즈 팝업 설정하는 리스너 추가
        AppboyInAppMessageManager.getInstance().setCustomInAppMessageManagerListener(this);
    }

    /**
     * 웹뷰에 url을 로딩한다.
     */
    @Override
    public void loadUrl() {
        super.loadUrl();

        // 인텐트로 전달된 url이 있으면 그 url 로드. 없으면 홈 url 로드.
        String url = getIntent().getStringExtra(Keys.INTENT.WEB_URL);
        if (url != null) {
            String postData = getIntent().getStringExtra(Keys.INTENT.POST_DATA);
            if (TextUtils.isEmpty(postData)) {
                //GET 전송
                webControl.loadUrl(addParamToUrl(getIntent()), MainApplication.customHeaders);
            } else {
                //POST_DATA 존재하면 POST 전송
                webControl.getWebView().postUrl(addParamToUrl(getIntent()), postData.getBytes());
            }
        } else {
            handleWebException();
        }
    }

    @Override
    protected void setupWebControl() {
        webControl = new WebViewControlInherited.Builder(this)
                .target(webView)
                .with(new MainWebViewClient(this) {

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        Ln.i("[WebActivity shouldOverrideUrlLoading]" + url);
                        //http 혹은 https로 시작하면 currentUrl에 set
                        //setCurrentUrl(url);

                        // onPageStarted -> shouldOverrideUrlLoading 로 이동함 WebActivity에서만 혹시 다른곳은 문제가발생할까봐
                        // checkMoveToHome true(앱에서 해당 URL처리를 직접 할경우 일때 shouldOverrideUrlLoading true를 반화하여
                        // 기본 브라우져가 처리하지 않게 하였다.

                        //tabId 포함된 url 로딩 후 redirect된 ../index.gs는 3초간 무시
                        if (haveTabId && ClickUtils.work2(0)) {
                            haveTabId = false;
                            return true;
                        }

                        if (checkMoveToHome(url) == true) {
                            return true;
                        }

                        if (WebUtils.isNoTabPage(url) || WebUtils.isMyShop(url) ||
                                WebUtils.isTvLoginPage(url)) {
                            if (getIntent().getBooleanExtra(Keys.INTENT.CAN_CAUSE_REDIRECTING,
                                    false)
                                    && !redirectChecker.isUrlShortcut(url)
                                    && view.copyBackForwardList().getSize() > 0) {
                                getIntent().putExtra(Keys.INTENT.BACK_TO_MAIN, false);
                            }
                        }

                        if (handleUrl(WEB_TYPE_DEFAULT, url)) {
                            // [예외처리] redirect를 위해 거쳐가는 경우에는 현재 activity 종료
                            // (ex) 쇼핑알림함에서 축약 url로 단품 가는 경우
                            //                            if (getIntent().getBooleanExtra(Keys.INTENT.CAN_CAUSE_REDIRECTING,
                            //                                    false)
                            //                                    && url.indexOf("gsshop.com/jsp/jseis_withLGeshop.jsp") != -1) {
                            //                                finish();
                            //                            }

                            if (getIntent().getBooleanExtra(Keys.INTENT.CAN_CAUSE_REDIRECTING,
                                    false)
                                    && redirectChecker.isRedirect(url)) {
                                redirectChecker.clear();
                                finish();
                            }

                            redirectChecker.setStartUrl(url);
                            return true;
                        } else {
                            if (url.toLowerCase(Locale.getDefault()).contains("facebook.com/dialog/return/close")) {
                                finish();
                            }
                            redirectChecker.setStartUrl(url);
                            return super.shouldOverrideUrlLoading(view, url);
                        }

                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        Ln.i("[WebActivity onPageStarted]" + url);

                        //http 혹은 https로 시작하면 currentUrl에 set
                        //setCurrentUrl(url);

                        if (isSmartCart(url)) {
                            isPsnlCurationTarget = true;
                        }

                        //GA 화면 로깅시 url을 전달함
                        setScreenViewWithUrl(url);

                        redirectChecker.pageStarted(url);

                        //홈->바로구매->장바구니->로그인->흰화면->장바구니 상태에서 백키 클릭시 나타나는 흰화면 제거
                        //멀티상품->바로구매->장바구니->로그인->흰화면->장바구니 상태에서 백키 클릭시 나타나는 흰화면 제거
                        if (isBackkeyPressed) {
                            isBackkeyPressed = false;
                            removeBlankPage(url, BLANK_REMOVE_NOW);
                        }

                        //"…/index", "…myshopInfo.gs" 주소에 대한 예외처리
                        checkMoveToHome(url);

                        //tabId 포함된 url 로딩 후 redirect된 ../index.gs는 3초간 무시
                        ClickUtils.work2(3000);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);

                        //홈->바로구매->바로구매->로그인->흰화면->주문서 상태에서 백키 클릭시 나타나는 흰화면 미리 제거
                        //멀티상품->바로구매->바로구매->로그인->흰화면->주문서 상태에서 백키 클릭시 나타나는 흰화면 미리 제거
                        removeBlankPage(url, BLANK_REMOVE_DELAY);

                        redirectChecker.pageFinished(url);
                    }

                })
                .with(new MainWebViewChromeClient(this))
                .with(new WebViewProgressBar((ProgressBar) findViewById(R.id.mc_webview_progress_bar))).build();


        redirectChecker = new RedirectChecker(this);
        super.setupWebControl();
    }

    // 성인 인증 후 이전 페이지 닫히지 않아 닫게끔 하기 위해 상수를 이용하여 해결
    public static boolean isAdultClose = false;

    @Override
    protected void onResume() {
        super.onResume();

        //SNS 로그인 초기화(콜백등록)
        initKakao();
        initNaverLogin();
        String url = webControl.getWebView().getUrl();
        if (EmptyUtils.isNotEmpty(url)) {
            // 성인 인증 후 이전 페이지 닫히지 않아 닫게끔 하기 위해 상수를 이용하여 해결
            if ((url.toLowerCase().contains("member/adultcheck.gs") && isAdultClose) ||
                    url.toLowerCase().contains("member/confirmipinadultvalidityssl.gs?isadult=true&isalcoladultprd=y&aftercert")) {
                finish();
            }
        }

        //http 혹은 https로 시작하면 currentUrl에 set
        //setCurrentUrl(url);
        //브레이즈 팝업 설정 다시 달아주기
        //AppboyInAppMessageManager.getInstance().setCustomInAppMessageManagerListener(this);
    }

    /**
     * http 혹은 https로 시작하면 currentUrl을 바꿔준다.(toapp은 해당 안되도록)
     */
    /*public void setCurrentUrl(String url){
        if(url.startsWith(ServerUrls.HTTPS) || url.startsWith(ServerUrls.HTTP)){
            currentUrl = url;
        }
    }*/
    @Override
    protected void onPause() {
        //SNS 로그인 콜백 제거
        getCurrentSession().removeCallback(callback);
        mOAuthLoginButton.setOAuthLoginHandler(null);

        webControl.getWebChromeClient().onHideCustomView();
        //웹뷰에서 쿠키를 생성한 후 앱을 종료하고, 딥링크로 해당 웹뷰에 바로 접속하면 생성했던 쿠키가 손실된다.
        //웹뷰쿠키 싱크를 수행하면 쿠키 손실을 방지할 수 있다.
        CookieUtils.syncWebViewCookies(context, restClient);
        //브레이즈 팝업 설정 리스너 null
        //AppboyInAppMessageManager.getInstance().setCustomInAppMessageManagerListener(null);
        super.onPause();
    }

    /**
     * 위젯으로 부터 호출된 경우 GTM 이벤트를 전달한다.
     */
    protected void sendGtmEvent() {
        String widgetMenu = getIntent().getStringExtra(Keys.INTENT.WIDGET_TYPE);
        if (TextUtils.isEmpty(widgetMenu)) {
            return;
        }

        String label = "";
        if (GTMEnum.GTM_WIDGET_LABEL.LiveGoods.toString().equals(widgetMenu)) {
            label = GTMEnum.GTM_WIDGET_LABEL.LiveGoods.getLabel();
        } else if (GTMEnum.GTM_WIDGET_LABEL.Cart.toString().equals(widgetMenu)) {
            label = GTMEnum.GTM_WIDGET_LABEL.Cart.getLabel();
        } else if (GTMEnum.GTM_WIDGET_LABEL.Order.toString().equals(widgetMenu)) {
            label = GTMEnum.GTM_WIDGET_LABEL.Order.getLabel();
        }

        String action = String.format("%s_%s", GTMEnum.GTM_ACTION_HEADER,
                GTMEnum.GTM_ACTION_WIDGET_TAIL);

        if (!TextUtils.isEmpty(label)) {
            //GTM 클릭이벤트 전달
            GTMAction.sendEvent(this, GTMEnum.GTM_AREA_CATEGORY, action, label);
        }
    }

    // 멀티 채널 페이지에서 AndroidBridge 내 refreshCart() 호출하면 그 이벤트 받아서 다시 웹으로 던짐
    public void onEventMainThread(Events.FlexibleEvent.RefreshCart event) {
        if (webView != null) {
            webView.loadUrl("javascript:refreshBasketCnt()");
        }
    }

    @Override
    public void onBackPressed() {
        isBackkeyPressed = true;

        if (DirectOrderView.isVisibleDirectOrder) {
            hideOrderDirectWebView();
            return;
        }

        //장바구니에서 백키 클릭한 경우만 노출플래그 세팅
        if (isSmartCart(webView.getOriginalUrl())) {
            showPsnlCuration = true;
            psnlCurationType = HomeActivity.PsnlCurationType.CART;
        }

        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * url이 브레이즈 안뜨는곳인지 체크
     *
     * @param url
     * @param noBrazeUrl
     * @return
     */
    private boolean noBrazeCheck(String url, String noBrazeUrl) {
        String urlString = url;
        String noBrazeString = noBrazeUrl;

        urlString = stringReplace(urlString);
        noBrazeString = stringReplace(noBrazeString);

        if (urlString.startsWith(noBrazeString)) {
            return true;
        }
        return false;
    }

    private String stringReplace(String str) {
        String replaceString = str;
        String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";
        replaceString = replaceString.replaceAll(match, "");
        return replaceString;
    }


    @Override
    public boolean onInAppMessageReceived(IInAppMessage iInAppMessage) {
        return false;
    }

    @Override
    public InAppMessageOperation beforeInAppMessageDisplayed(IInAppMessage iInAppMessage) {
        /*for (String noBrazeUrl : noBrazeUrlList) {
            if (noBrazeCheck(currentUrl, noBrazeUrl)) {
                return InAppMessageOperation.DISPLAY_LATER; //브레이즈 팝업 나중에 뜨도록(지금은 안뜨도록)
            }
        }*/

        return InAppMessageOperation.DISPLAY_NOW;
    }

    @Override
    public boolean onInAppMessageClicked(IInAppMessage iInAppMessage, InAppMessageCloser inAppMessageCloser) {
        return false;
    }

    @Override
    public boolean onInAppMessageButtonClicked(MessageButton messageButton, InAppMessageCloser inAppMessageCloser) {
        return false;
    }

    @Override
    public void onInAppMessageDismissed(IInAppMessage iInAppMessage) {

    }
}
