/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.google.inject.Inject;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.web.WebViewProgressBar;

import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.util.CookieUtils;
import roboguice.util.Ln;

/**
 * 배경이 투명한 모달 웹액티비티.
 */
@SuppressLint("NewApi")
public class TransparentModalWebActivity extends BaseWebActivity {

    @Inject
    private RestClient restClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_transparent_modal);

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

        String url = getIntent().getStringExtra(Keys.INTENT.WEB_URL);
        if (url != null) {
            webControl.loadUrl(url, MainApplication.customHeaders);
        }
    }

    @Override
    protected void setupWebControl() {
        WebView webView = (WebView) findViewById(R.id.webview);
        webView.setBackgroundColor(0);
        webControl = new WebViewControlInherited.Builder(this)
                .target(webView)
                .with(new MainWebViewClient(this) {

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        Ln.i("[TransparentModalWebActivity shouldOverrideUrlLoading]" + url);

                        if (handleUrl(WEB_TYPE_MODAL, url)) {
                            return true;
                        } else {
                            return super.shouldOverrideUrlLoading(view, url);
                        }
                    }
                })
                .with(new MainWebViewChromeClient(this) {
                    @Override
                    public void onProgressChanged(WebView view, int newProgress) {
                        super.onProgressChanged(view, newProgress);
                        if ((newProgress == 100) && webControl != null && webControl.getProgress() != null) {
                            webControl.getProgress().hide();
                        }
                    }
                })
                .with(new WebViewProgressBar((ProgressBar) findViewById(R.id.progress))).build();

        super.setupWebControl();
    }

    /**
     * 판종 등 얼랏참 뜬 경우는 프로그레스바를 숨김처리한다.
     *
     * @param event Events.HideProgressEvent
     */
    public void onEventMainThread(Events.HideProgressEvent event) {
        webControl.getProgress().hide();
    }

    @Override
    protected void onPause() {
        //웹뷰에서 쿠키를 생성한 후 앱을 종료하고, 딥링크로 해당 웹뷰에 바로 접속하면 생성했던 쿠키가 손실된다.
        //웹뷰쿠키 싱크를 수행하면 쿠키 손실을 방지할 수 있다.
        CookieUtils.syncWebViewCookies(context, restClient);
        super.onPause();
    }
}
