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
import android.webkit.WebView;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ProgressBar;

import com.appboy.models.IInAppMessage;
import com.appboy.models.MessageButton;
import com.appboy.ui.inappmessage.AppboyInAppMessageManager;
import com.appboy.ui.inappmessage.InAppMessageCloser;
import com.appboy.ui.inappmessage.InAppMessageOperation;
import com.appboy.ui.inappmessage.listeners.IInAppMessageManagerListener;
import com.google.inject.Inject;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.web.WebViewControl;
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
public class OrderWebActivity extends BaseWebActivity{

	private static final String TAG = "OrderWebActivity";

    //주문서작성 화면인지 주문완료 화면인지 구분자
    private boolean isOrderComplete = false;

    @Inject
    private RestClient restClient;

    private WebView webView;
    
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

        //브레이즈 팝업 설정하는 리스너 추가
        //AppboyInAppMessageManager.getInstance().setCustomInAppMessageManagerListener(this);
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
                        Ln.i("[OrderWebActivity shouldOverrideUrlLoading]" + url);
                        return super.shouldOverrideUrlLoading(view, url);
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        Ln.i("[OrderWebActivity onPageStarted]" + url);

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
    }

    @Override
    protected void onResume() {
        //AppboyInAppMessageManager.getInstance().setCustomInAppMessageManagerListener(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
    	//웹뷰에서 쿠키를 생성한 후 앱을 종료하고, 딥링크로 해당 웹뷰에 바로 접속하면 생성했던 쿠키가 손실된다.
    	//웹뷰쿠키 싱크를 수행하면 쿠키 손실을 방지할 수 있다.
        CookieUtils.syncWebViewCookies(context, restClient);

        //AppboyInAppMessageManager.getInstance().setCustomInAppMessageManagerListener(null);
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

    /*//브레이즈 팝업 도착했을때
    @Override
    public boolean onInAppMessageReceived(IInAppMessage iInAppMessage) {
        return false;
    }

    //브레이즈 팝업 보여지기 전
    @Override
    public InAppMessageOperation beforeInAppMessageDisplayed(IInAppMessage iInAppMessage) {
        return InAppMessageOperation.DISPLAY_LATER; //브레이즈 팝업 나중에 뜨도록(지금은 안뜨도록)
    }

    //브레이즈 팝업 클릭
    @Override
    public boolean onInAppMessageClicked(IInAppMessage iInAppMessage, InAppMessageCloser inAppMessageCloser) {
        return false;
    }

    //브레이즈 팝업 버튼 클릭
    @Override
    public boolean onInAppMessageButtonClicked(MessageButton messageButton, InAppMessageCloser inAppMessageCloser) {
        return false;
    }

    //브레이즈 팝업 해제되었을때
    @Override
    public void onInAppMessageDismissed(IInAppMessage iInAppMessage) {

    }*/
}
