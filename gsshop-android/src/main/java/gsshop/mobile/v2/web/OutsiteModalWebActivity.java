/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gsshop.mocha.web.WebViewControl;
import com.gsshop.mocha.web.WebViewProgressBar;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.Keys.REQCODE;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.menu.RunningActivityManager;
import roboguice.util.Ln;

/**
 * 모달창 형태의 웹화면.
 * 하단 탭메뉴를 포함하지 않음.
 * BackBtn 추가.
 *
 * #본 클래스에서 처리하는 custom url
 * 1.toapp://outsitemodal?title=xxx{@literal &}url=xxx
 */
public class OutsiteModalWebActivity extends BaseWebActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setContentView(R.layout.web_outsite_modal);
        RunningActivityManager.addActivity(this);

        setupWebControl();

        setupRefreshLayout();

        loadUrl();

        // modal web title

        String title = getIntent().getStringExtra(Keys.INTENT.WEB_TITLE);
        //Default 값은 app_name(GS SHOP)
        title = TextUtils.isEmpty(title) ? getString(R.string.app_name) : title;

        TextView txtTitle = (TextView) findViewById(R.id.txt_title);
        txtTitle.setText(title);

        //AbstractBaseActivity에 있는 것을 쓰려 했지만.
        // 아래 함수에서 Back 버튼에 대한 설정도 설정되지만, backBtn이 finish다...
        // 그래서 BackBtn 클릭시에 동작을 이 Activity에서 따로 설정.
//        setHeaderView(title);

        // webview 스크롤에 따른 top버튼 처리
        final CustomWebView webView = (CustomWebView) findViewById(R.id.webview);
        final ImageButton btnTop = (ImageButton) findViewById(R.id.btn_top);
        btnTop.setVisibility(View.GONE);

//      QC에서 btnTop 미출력으로 요청.
        /*
        webView.setOnScrolledListener(new OnScrolledListener() {

            @Override
            public void onScrolled(int l, int t, int oldl, int oldt) {
                if (t > 100) {
                    if (!btnTop.isShown()) {
                        showView(btnTop);
                    }
                } else {
                    if (btnTop.isShown()) {
                        hideView(btnTop);
                    }
                }
            }

        });

        btnTop.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // FIXME : jisun "webView.scrollTo(0, 0);"는 
                // scroll이 되고 있는 중에 top버튼 누르면 제대로 동작 안해서 
                // javascript 코드 이용
                webView.loadUrl("javascript:$(window).scrollTop(0);");
                hideView(btnTop);
            }

        });
        */
    }


    /**
     * 웹뷰에 url을 로딩한다.
     */
    @Override
    public void loadUrl() {
        super.loadUrl();

        // 인텐트로 전달된 url이 있으면 그 url 로드. 없으면 홈 url 로드.
        String url = getIntent().getStringExtra(Keys.INTENT.WEB_URL);
        if (TextUtils.isEmpty(url)) {
            Ln.i("모달 창 웹주소가 지정되지 않았습니다. 메인홈 주소를 사용합니다.");
            //이럴때 어떻게 하지???? by leems
            url = ServerUrls.WEB.HOME;
        }

        webControl.loadUrl(url, MainApplication.customHeaders);
    }

    private void showView(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(300);
        view.clearAnimation();
        view.setVisibility(View.VISIBLE);
        view.setAnimation(anim);
    }

    private void hideView(View view) {
        AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(300);
        view.clearAnimation();
        view.setAnimation(anim);
        view.setVisibility(View.GONE);
    }

    @Override
    protected void setupWebControl() {
        webControl = new WebViewControlInherited.Builder(this)
                .target((WebView) findViewById(R.id.webview))
                .with(new MainWebViewClient(this) {

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        Ln.i("[ModalWebActivity shouldOverrideUrlLoading]" + url);

                        if (handleUrl(WEB_TYPE_MODAL, url)) {
                            return true;
                        } else {
                            return super.shouldOverrideUrlLoading(view, url);
                        }

                    }

                	@Override
                	public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        Ln.i("[OutsiteModalWebActivity onPageStarted]" + url);
                		super.onPageStarted(view, url, favicon);
                        //GA 화면 로깅시 url을 전달함
                        setScreenViewWithUrl(url);
                	}

                    @Override
                    public void onPageFinished(WebView view, String url) {
                    	super.onPageFinished(view, url);
                        //new CustomOneButtonDialog(ModalWebActivity.this).message(url).buttonClick(CustomOneButtonDialog.DISMISS).show();
                    }

                })
                .with(new MainWebViewChromeClient(this))
                .with(new WebViewProgressBar(
                        (ProgressBar) findViewById(R.id.mc_webview_progress_bar))).build();

        super.setupWebControl();
    }

    /**
     * 
     * @param requestCode requestCode
     * @param resultCode resultCode
     * @param intent intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
        case REQCODE.LAUNCH_OTHER_APP:
            // 다른 앱을 실행시켰다가 돌아오면 modal web은 종료
            finish();
            break;
        default:
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }

    /**
     * 헤더 x 버튼 처리
     * @param v v
     */
    public void onBackClicked(View v) {
        super.onBackPressed();
    }

    /**
     * 헤더 x 버튼 처리
     * @param v v
     */
    public void onCloseClicked(View v) {
        finish();
    }

    /**
     * onNewIntent() 무시
     * @param intent intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        // do nothing..
    }

    /**
     * 모달창 닫힐 때 스르륵 닫히는 효과를 적용.
     */
    @Override
    protected void performFinishTransition() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
