/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ProgressBar;

import com.google.inject.Inject;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.pattern.mvc.BaseAsyncController;
import com.gsshop.mocha.web.WebViewControl;
import com.gsshop.mocha.web.WebViewProgressBar;

import java.util.Locale;

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
public class NoTabWebActivity extends BaseWebActivity {

    private static final String TAG = "NoTabWebActivity";

    //JellyBean 미만 버전에서 라이브톡 접근시 본 액티비티로 이동됨(동영상 플레이어 최소 지원버전 JellyBean 이상)
    //호출된 곳이 TV쇼핑탭 롤링텍스트, SNS공유버튼인 경우 인텐트에 아래 값이 세팅되어 들어옴
    private static final String REFERRER_LIVE_TALK = "livetalk";

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

        if (REFERRER_LIVE_TALK.equalsIgnoreCase(getIntent().getStringExtra(Keys.INTENT.REFERRER))) {
            //라이브톡에서 진입한 경우 btmUrl을 구해서 웹뷰를 로딩한다.
            loadLiveTalkBtmUrl(this, url);
        } else if (REFERRER_VERSION_CHECK_COMMAND.equalsIgnoreCase(getIntent().getStringExtra(Keys.INTENT.REFERRER))) {
            loadUrlWebview(url);
            //공사중 페이지인 경우 버튼 노출
            findViewById(R.id.btn_restart).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_restart).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //앱 재시작
                    AppFinishUtils.finishApp(NoTabWebActivity.this, true);
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
                        Ln.i("[NoTabWebActivity shouldOverrideUrlLoading]" + url);

                        if (handleUrl(WEB_TYPE_NO_TAB, url)) {
                            // [예외처리] 성인인증을 위해 WebActivity로  분기했을 때에는 NoTabActivity 종료
                            if (url.toLowerCase(Locale.getDefault()).contains("chkadult")) {
                                finish();
                            }
                            // [예외처리] 단품에서 dealList WebActivity로  분기했을 때에는 NoTabActivity 종료
                            if (url.toLowerCase(Locale.getDefault()).contains("deallist.gs")) {
                                finish();
                            }

                            if (url.toLowerCase(Locale.getDefault()).contains("facebook.com")) {
                                finish();
                            }
                            if (url.toLowerCase(Locale.getDefault()).contains("twitter.com/share")) {
                                finish();
                            }

                            return true;
                        } else {
                            return super.shouldOverrideUrlLoading(view, url);
                        }
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        //구매하기.장바구니->로그인->흰화면->주문서.장바구니 상태에서 백키 클릭시 나타나는 흰화면 미리 제거
                        removeBlankPage(url, BLANK_REMOVE_DELAY);

                        super.onPageFinished(view, url);
                        //new CustomOneButtonDialog(NoTabWebActivity.this).message(url).buttonClick(CustomOneButtonDialog.DISMISS).show();
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        Ln.i("[NoTabWebActivity onPageStarted]" + url);

                        super.onPageStarted(view, url, favicon);

                        //GA 화면 로깅시 url을 전달함
                        setScreenViewWithUrl(url);

                        //".../index", "...myshopInfo.gs" 주소에 대한 예외처리 가 필요 한다
                        // to do
                        //checkMoveToHome(url);
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
        WebActivity.isAdultClose = true;
        if(webControl != null && webControl.getWebChromeClient() != null){
            if(((MainWebViewChromeClient)webControl.getWebChromeClient()).isShowCustomView()){
                webControl.getWebChromeClient().onHideCustomView();
            }else{
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        WebActivity.isAdultClose = false;
                    }
                },1000);
                super.onBackPressed();
            }
        }

    }

    /**
     * 라이브톡 btmUrl을 취득해서 웹뷰에 로딩한다.
     *
     * @param context 컨텍스트
     * @param url 라이브톡 api url
     */
    private void loadLiveTalkBtmUrl(Context context, String url) {
        new BaseAsyncController<LiveTalkResult>(context) {
            private String url;

            @Override
            protected void onPrepare(Object... params) throws Exception {
                super.onPrepare(params);
                url = (String) params[0];
            }

            @Override
            protected LiveTalkResult process() throws Exception {
                return restClient.getForObject(url, LiveTalkResult.class);
            }

            @Override
            protected void onSuccess(LiveTalkResult result) throws Exception {
                if (!TextUtils.isEmpty(result.btmUrl)) {
                    webControl.loadUrl(result.btmUrl, MainApplication.customHeaders);
                } else {
                    handleWebException();
                }
            }
        }.execute(url);
    }
}
