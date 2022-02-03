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
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ProgressBar;

import com.google.inject.Inject;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.web.WebViewProgressBar;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.util.CookieUtils;
import roboguice.util.Ln;

/**
 * 주문서 전용 웹액티비티.
 *
 * 인텐트에 Keys.INTENT.WEB_URL 키를 사용하여 로딩할 웹페이지 주소를
 * 전달할 수 있다.
 */
@SuppressLint("NewApi")
public class OrderNoTabWebActivity extends BaseWebActivity {

	private static final String TAG = "OrderNoTabWebActivity";

    //주문서작성 화면인지 주문완료 화면인지 구분자
    private boolean isOrderComplete = false;

    @Inject
    private RestClient restClient;

    private WebView webView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web);

        //MSLEE TEST
        //탭바 숨기고
        findViewById(R.id.layout_tab_menu).setVisibility(View.GONE);


        // 하단 탭바에서 shadow 높이를 뺀 만큼 webview에 bottom margin 설정
        webView = (WebView) findViewById(R.id.webview);
        LayoutParams p = (LayoutParams) webView.getLayoutParams();
        //p.bottomMargin = getResources().getDimensionPixelSize(R.dimen.tab_menu_height);
        p.bottomMargin = 0;
        webView.setLayoutParams(p);
        
        setupWebControl();
        //setDummyWebView();

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
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        Ln.i("[OrderNoTabWebActivity shouldOverrideUrlLoading]" + url);
                        return super.shouldOverrideUrlLoading(view, url);
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        Ln.i("[OrderNoTabWebActivity onPageStarted]" + url);

                        //주문서작성 화면과 주문완료 화면에서 백키처리를 구분하기 위해 플래그 세팅
                        if (WebUtils.isOrderComplete(url)) {
                            isOrderComplete = true;
                        }


                        //GA 화면 로깅시 url을 전달함
                        setScreenViewWithUrl(url);
                        
                        super.onPageStarted(view, url, favicon);

                        //".../index", "...myshopInfo.gs" 주소에 대한 예외처리
                        if (checkMoveToHome(url)) {
//                            moveToHome(url);
                        }
                    }
                })
                .with(new MainWebViewChromeClient(this))
                .with(new WebViewProgressBar((ProgressBar) findViewById(R.id.mc_webview_progress_bar))).build();
        super.setupWebControl();

        //주문서 관련된 브라우징에 관련하여 폰트 100으로 고정
        //조건이 특이하다고 생각되면 else에 이유 참고 부탁드립니다.
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
    
    @Override
    protected void onPause() {
    	//웹뷰에서 쿠키를 생성한 후 앱을 종료하고, 딥링크로 해당 웹뷰에 바로 접속하면 생성했던 쿠키가 손실된다.
    	//웹뷰쿠키 싱크를 수행하면 쿠키 손실을 방지할 수 있다.
        CookieUtils.syncWebViewCookies(context, restClient);
    	super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (isOrderComplete) {
            //주문완료 화면에서 백키 클릭시 액티비티 종료
            finish();
        } else {
            //주문서작성 화면에서 백키 클릭시 히스토리백 사용
            super.onBackPressed();
        }
    }
}
