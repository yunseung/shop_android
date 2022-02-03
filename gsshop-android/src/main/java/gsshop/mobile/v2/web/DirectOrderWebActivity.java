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
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.gsshop.mocha.ui.util.ViewUtils;
import com.gsshop.mocha.web.WebViewProgressBar;

import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

/**
 * 바로구매 웹액티비티.
 */
@SuppressLint("NewApi")
public class DirectOrderWebActivity extends BaseWebActivity {

    private static final String TAG = "DirectOrderWebActivity";

    @InjectView(R.id.view_order)
    private View orderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_directorder);

        ViewUtils.hideViews(orderView);
        setupWebControl();

        setupRefreshLayout();

        // 클릭리스너 등록
        setClickListener();

        loadUrl();

        upAnimation();
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
        WebView webView = (WebView) findViewById(R.id.direct_order_view);
        webView.setBackgroundColor(0);
        webControl = new WebViewControlInherited.Builder(this)
                .target(webView)
                .with(new MainWebViewClient(this) {

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        Ln.i("[DirectOrderWebActivity shouldOverrideUrlLoading]" + url);

                        finish();

                        if (handleUrl(WEB_TYPE_DIRECT_ORDER, url)) {
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
     * 뷰의 클릭리스너를 세팅한다.
     */
    private void setClickListener() {
        findViewById(R.id.root_view).setOnClickListener((View v) -> {
                    onClickRootView();
                }
        );
    }

    @Override
    public void finish() {
        if(orderView != null){
            DirectOrderWebActivity.super.finish();
        }else{
            super.finish();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }

    /**
     * 판종 등 얼랏참 뜬 경우는 프로그레스바를 숨김처리한다.
     *
     * @param event Events.HideProgressEvent
     */
    public void onEventMainThread(Events.HideProgressEvent event) {
        webControl.getProgress().hide();
    }

    private void upAnimation() {
        ViewUtils.showViews(orderView);
    }

    /**
     * 외부영역 클릭시 종료
     */
    private void onClickRootView() {
        finish();
    }
}
