/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebBackForwardList;
import android.webkit.WebView;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ProgressBar;

import com.google.inject.Inject;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.web.WebViewControl;
import com.gsshop.mocha.web.WebViewProgressBar;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.DirectOrderView;
import gsshop.mobile.v2.menu.TabMenu;
import gsshop.mobile.v2.support.gtm.GTMAction;
import gsshop.mobile.v2.support.gtm.GTMEnum;
import gsshop.mobile.v2.util.CookieUtils;
import roboguice.util.Ln;

import static com.kakao.auth.Session.getCurrentSession;

@SuppressLint("NewApi") public class MyShopWebActivity extends BaseWebActivity {
    private WebView webView;

    @Inject
    private RestClient restClient;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web);

        // 하단 탭바에서 shadow 높이를 뺀 만큼 webview에 bottom margin 설정
        webView = (WebView) findViewById(R.id.webview);
        LayoutParams p = (LayoutParams) webView.getLayoutParams();
        p.bottomMargin = getResources().getDimensionPixelSize(R.dimen.tab_menu_height);
        webView.setLayoutParams(p);

        setupWebControl();

        setupRefreshLayout();

        loadUrl();

        //바로구매
        directOrderLayout = (DirectOrderView) findViewById(R.id.direct_order_layout);
        directOrderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOrderDirectWebView();
            }
        });

    }

    @Override
    public void loadUrl() {
        super.loadUrl();

        // 인텐트로 전달된 url이 있으면 그 url 로드. 없으면 홈 url 로드.
        String url = getIntent().getStringExtra(Keys.INTENT.WEB_URL);
        if (url != null) {
            webControl.loadUrl(addParamToUrl(getIntent()), MainApplication.customHeaders);
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
                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                        super.onReceivedError(view, errorCode, description, failingUrl);
                    }

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        Ln.i("[MyShopWebActivity shouldOverrideUrlLoading]" + url);

                        //WebActivity.java에 있는 방어로직을 여기에다가도 추가
                        // 1. 최초 tabId는 주소는 네이티브에만 적용하기로함 앱->앱 탭이동
                        // 2. 이후 tabId 웹페이지에 적용 웹 -> 앱 탭이동
                        //    이때 WebActivity에만 적용
                        //    알고 봤더니 마이쇼핑엑비티비에는 적용안했더니 죽네
                        // 3. 20170615 WebActivity에 있는 방어로직을 옮겨옴
                        //     onPageStarted -> checkMoveToHome 주석
                        if( checkMoveToHome(url) == true ) {
//                            moveToHome(url);
                            return true;
                        }
                        if (handleUrl(WEB_TYPE_DEFAULT, url)) {
                            return true;
                        } else {
                            return super.shouldOverrideUrlLoading(view, url);
                        }
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        Ln.i("[MyShopWebActivity onPageStarted]" + url);
                        //GA 화면 로깅시 url을 전달함
                        setScreenViewWithUrl(url);

                        //".../index", "...myshopInfo.gs" 주소에 대한 예외처리
                        ////WebActivity.java에 있는 방어로직을 여기에다가도 추가
                        // 1. 최초 tabId는 주소는 네이티브에만 적용하기로함 앱->앱 탭이동
                        // 2. 이후 tabId 웹페이지에 적용 웹 -> 앱 탭이동
                        //    이때 WebActivity에만 적용
                        //    알고 봤더니 마이쇼핑엑비티비에는 적용안했더니 죽네
                        // 3. 20170615 WebActivity에 있는 방어로직을 옮겨옴
                        //     onPageStarted -> checkMoveToHome 주석
                        //checkMoveToHome(url);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        if(view.canGoBack()) {
                            WebBackForwardList webBackForwardList = view.copyBackForwardList();
                            String backUrl = webBackForwardList.getItemAtIndex(webBackForwardList.getCurrentIndex() - 1).getUrl();
                            String currentUrl = webBackForwardList.getItemAtIndex(webBackForwardList.getCurrentIndex()).getUrl();
                            if(currentUrl.contains("gsshop.com/mygsshop/myshopInfo.gs") && backUrl.contains("gsshop.com/mygsshop/myshopInfo.gs")){
                                view.clearHistory();
                            }
                        }
                    }



                })
                .with(new MainWebViewChromeClient(this))
                .with(new WebViewProgressBar(
                        (ProgressBar) findViewById(R.id.mc_webview_progress_bar))).build();

        super.setupWebControl();
    }

    @Override
    protected void onPause() {
        //SNS 로그인 콜백 제거
        getCurrentSession().removeCallback(callback);
        mOAuthLoginButton.setOAuthLoginHandler(null);

        webControl.getWebChromeClient().onHideCustomView();
        CookieUtils.syncWebViewCookies(context, restClient);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //SNS 로그인 초기화(콜백등록)
        initKakao();
        initNaverLogin();

        //GTM Ecommerce에서 사용할 스크린 이름 설정
        GTMAction.setScreenName(GTMEnum.GTM_TABMENU_LABEL.MyShop.getLabel());

        //하단탭메뉴 선택으로 변경
        if (getIntent() != null)
        {
            if( !isSelectedTabFocus(TabMenu.MYSHOP) )
            {
                setTabAdjustment(TabMenu.MYSHOP);
            }
        }
    }
}
