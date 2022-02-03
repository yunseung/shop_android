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
import android.webkit.WebView;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ProgressBar;

import com.google.inject.Inject;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.web.WebViewControl;
import com.gsshop.mocha.web.WebViewProgressBar;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.util.AppFinishUtils;
import gsshop.mobile.v2.util.CookieUtils;
import roboguice.util.Ln;

import static gsshop.mobile.v2.intro.VersionCheckCommand.REFERRER_VERSION_CHECK_COMMAND;

/**
 * 단품/딜 화면일 경우 하단탭바를 숨기기 위한 activity.
 *
 */
public class TvMembersActivity extends BaseWebActivity {

    private static final String TAG = "TvMembersActivity";

    @Inject
    private RestClient restClient;

    private WebView webView;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web);

        // 탭바 숨기고
        findViewById(R.id.layout_tab_menu).setVisibility(View.GONE);

        // 공사중페이지 재시작 버튼 비노출
        findViewById(R.id.btn_restart).setVisibility(View.GONE);

        // webView 영역 늘리기
        webView = (WebView) findViewById(R.id.webview);
        LayoutParams p = (LayoutParams) webView.getLayoutParams();
        p.bottomMargin = 0;
        webView.setLayoutParams(p);

        setupWebControl();

        setupRefreshLayout();

        loadUrl();
    }

    /**
     * 웹뷰에 url을 로딩한다.
     */
    @Override
    public void loadUrl() {
        super.loadUrl();

        // 인텐트로 전달된 url이 있으면 그 url 로드. 없으면 홈 url 로드.
        String url = getIntent().getStringExtra(Keys.INTENT.WEB_URL);
        if (REFERRER_VERSION_CHECK_COMMAND.equalsIgnoreCase(getIntent().getStringExtra(Keys.INTENT.REFERRER))) {
            loadUrlWebview(url);
            //공사중 페이지인 경우 버튼 노출
            findViewById(R.id.btn_restart).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_restart).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //앱 재시작
                    AppFinishUtils.finishApp(TvMembersActivity.this, true);
                }
            });
        } else {
            loadUrlWebview(url);
        }
    }

    /**
     * 웹뷰에 url을 로딩한다.
     * url정보가 유효하지 않으면 홈액티비티를 띄운다.
     *
     * @param url 웹뷰에 로딩할 url
     */
    private void loadUrlWebview(String url) {
        if (url != null) {
            webControl.loadUrl(url, MainApplication.customHeaders);
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
                        Ln.i("[TvMembersActivity shouldOverrideUrlLoading]" + url);

                        int checkIndex = -1;
                        if (!((checkIndex = url.indexOf("=http:")) > -1)) {
                            checkIndex = url.indexOf("=https:");
                        }
                        if (checkIndex > -1) {
                            String strPre = url.substring(0, checkIndex + 1);
                            String strSub = url.substring(checkIndex + 1);
                            try {
                                strSub = URLEncoder.encode(strSub, "utf-8");
                            } catch (UnsupportedEncodingException e) {
                                Ln.e(e.getMessage());
                            }
                            url = strPre + strSub;
                        }

                        // onPageStarted -> shouldOverrideUrlLoading 로 이동함 WebActivity에서만 혹시 다른곳은 문제가발생할까봐
                        // checkMoveToHome true(앱에서 해당 URL처리를 직접 할경우 일때 shouldOverrideUrlLoading true를 반화하여
                        // 기본 브라우져가 처리하지 않게 하였다
                        if( checkMoveToHome(url) == true ) {
//                            moveToHome(url);
                            return true;
                        }

                        boolean result = handleUrl(WEB_TYPE_TV_MEMBERS, url);

                        if (result) {
                            return true;
                        } else {
                            return super.shouldOverrideUrlLoading(view, url);
                        }
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        // 기존에 존재하던 구문, 예외 처리 이기 때문에 삭제 / 변경 하지 않음
                        removeBlankPage(url, BLANK_REMOVE_DELAY);

                        super.onPageFinished(view, url);
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        Ln.i("[TvMembersActivity onPageStarted]" + url);

                        super.onPageStarted(view, url, favicon);
                    }

                })
                .with(new MainWebViewChromeClient(this))
                .with(new WebViewProgressBar(
                        (ProgressBar) findViewById(R.id.mc_webview_progress_bar))).build();

        super.setupWebControl();
    }

    @Override
    protected void onPause() {
        // 이것이 필요 할까?
        //// TODO: 2016. 12. 23. : 이거 출처를 확인 제거 및 공통 반영
        //mWebControl.getWebChromeClient().onHideCustomView();
        //웹뷰에서 쿠키를 생성한 후 앱을 종료하고, 딥링크로 해당 웹뷰에 바로 접속하면 생성했던 쿠키가 손실된다.
        //웹뷰쿠키 싱크를 수행하면 쿠키 손실을 방지할 수 있다.
        CookieUtils.syncWebViewCookies(context, restClient);
        super.onPause();



    }

    @Override
    public void onBackPressed() {

        if(webControl != null && webControl.getWebChromeClient() != null){
            if(((MainWebViewChromeClient)webControl.getWebChromeClient()).isShowCustomView()){
                webControl.getWebChromeClient().onHideCustomView();
            }else{
                super.onBackPressed();
            }
        }

    }

}
