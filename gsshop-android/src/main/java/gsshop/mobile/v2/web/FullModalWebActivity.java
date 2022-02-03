/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.gsshop.mocha.web.WebViewControl;
import com.gsshop.mocha.web.WebViewProgressBar;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.menu.RunningActivityManager;
import roboguice.util.Ln;

/**
 * 모달창 형태의 전체 웹화면.(상단 타이틀 영역 없음)
 * 하단 탭메뉴를 포함하지 않음.
 *
 * #본 클래스에서 처리하는 custom url
 * ex) toapp://fullwebview?targetUrl=http://...
 */
public class FullModalWebActivity extends BaseWebActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setContentView(R.layout.web_full_modal);
        RunningActivityManager.addActivity(this);

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
        if (!TextUtils.isEmpty(url)) {
            webControl.loadUrl(url, MainApplication.customHeaders);
        }
    }

    @Override
    protected void setupWebControl() {
        webControl = new WebViewControlInherited.Builder(this)
                .target((WebView) findViewById(R.id.webview))
                .with(new MainWebViewClient(this) {

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        Ln.i("[FullModalWebActivity shouldOverrideUrlLoading]" + url);

                        if (handleUrl(WEB_TYPE_MODAL, url)) {
                            return true;
                        } else {
                            return super.shouldOverrideUrlLoading(view, url);
                        }

                    }

                	@Override
                	public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        Ln.i("[FullModalWebACtivity onPageStarted]" + url);
                		super.onPageStarted(view, url, favicon);
                        //GA 화면 로깅시 url을 전달함
                        setScreenViewWithUrl(url);
                	}

                    @Override
                    public void onPageFinished(WebView view, String url) {
                    	super.onPageFinished(view, url);
                    }

                })
                .with(new MainWebViewChromeClient(this))
                .with(new WebViewProgressBar(
                        (ProgressBar) findViewById(R.id.mc_webview_progress_bar))).build();

        super.setupWebControl();
    }

    /**
     * 모달창 닫힐 때 스르륵 닫히는 효과를 적용.
     */
    @Override
    protected void performFinishTransition() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
