package gsshop.mobile.v2.user;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.gsshop.mocha.ui.util.ViewUtils;
import com.nhn.android.naverlogin.OAuthLoginDefine;
import com.nhn.android.naverlogin.connection.NetworkState;
import com.nhn.android.naverlogin.connection.gen.OAuthQueryGenerator;
import com.nhn.android.naverlogin.data.OAuthLoginData;
import com.nhn.android.naverlogin.ui.OAuthLoginImage;
import com.nhn.android.naverlogin.ui.OAuthWebviewUrlUtil;
import com.nhn.android.naverlogin.ui.view.OAuthLoginLayoutNaverAppDownloadBanner;
import com.nhn.android.naverlogin.util.DeviceAppInfo;
import com.nhn.android.naverlogin.util.OAuthLoginUiUtil;

// 네이버앱이 없는 경우 OAuth 인증시 사용하는 WebView를 포함한 Activity
/*
 * @author naver
 */
public class DummyAppBrowserActivity extends Activity implements View.OnClickListener {
    private static final String TAG = OAuthLoginDefine.LOG_TAG + "DummyAppBrowserActivity";


    private static final String INSTANCE_STATE_WEBVIEW_RUN_ONLY_ONCE = "IsLoginActivityStarted";
    private static final String INSTANCE_STATE_IS_VISIBLE_BANNER = "isVisibleBanner";


    public class OAuthLoginInAppBrowserInIntentData {
        // 아래 3개는 로그인 페이지 불러올때 쓸 데이터
        public static final String INTENT_PARAM_KEY_CLIENT_ID = "ClientId";
        public static final String INTENT_PARAM_KEY_CALLBACK_URL = "ClientCallbackUrl";
        public static final String INTENT_PARAM_KEY_STATE = "state";
        // 아래 2개는 네이버앱을 통한 로그인시 전달됨
        public static final String INTENT_PARAM_KEY_APP_NAME = "app_name";
        public static final String INTENT_PARAM_KEY_OAUTH_SDK_VERSION = "oauth_sdk_version";
        // 아래 2개는 동의 창의 내용을 직접 받아와서 보여주는 경우 사용됨
        public static final String INTENT_PARAM_KEY_AGREE_FORM_URL = "agreeFormUrl";
        public static final String INTENT_PARAM_KEY_AGREE_FORM_CONTENT = "agreeFormContent";
    }

    public class OAuthLoginInAppBrowserOutIntentData {
        public static final String RESULT_CALLBACK 	= "RESULT_CALLBACK";
    }

    private Context mContext;

    // byte 형태의 button image
    private byte[] drawableByteBottomBackGroundImg;
    private byte[] drawableByteCloseBtnImg;


    // UI 객체들
    private OAuthLoginLayoutNaverAppDownloadBanner		mNaverDownloadBanner;
    private ImageView 		mImgSeperator;
    private ImageView 		mImgCloseButton;
    private WebView 		mWebView;
    private ProgressBar 	mWebviewProgressbar;
    private LinearLayout mWholeView;
    private LinearLayout 	mNaviBar;

    // oauth 인증 과정 및 결과 전달에 필요한 변수들
    public String 			mInOAuthUrl;
    private String 			mWebViewContent;

    // oauthlogin data
    private OAuthLoginData 	mOAuthLoginData;

    private boolean 		mIsLoginActivityStarted = false;

    /*
     * 갤럭시s5 android 5.0.0 이고, 3rd-party 앱이 multi-window 기능을 쓰는 경우 이 activity 의 orientation 이 지정되지 않고 네아로 SDK 4.1.1 이하에서 로그인시도시
     * onCreate() 가 2번 실행되는 현상이 있어서 orientation 을 고정한다. (네아로 4.1.3 버젼에서 호출될땐 추가적인 파라미터를 받아서 orientation 고정되지 않도록 한다.)
     */
    private String mOAuthSdkVersion;
    private boolean mFixActivityPortrait = true;
    private boolean mVisibleNaverAppDownloadBanner = true;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initIntentData();
        initSavedInstanceStateData(savedInstanceState);
        initView(savedInstanceState);

        if (OAuthLoginDefine.DEVELOPER_VERSION) {
            //Log.d(TAG, "webview onCreate() fix:" + mFixActivityPortrait);
        }

        if (mFixActivityPortrait) {
			/*
			 * 안드로이드 5.0.0 에서 네아로 SDK 4.1.1 미만 버젼사용한 앱이 네이버앱 호출하여 네아로 로그인하는 경우,
			 * 로그인한 뒤 화면 회전하고 3rd-party 앱으로 되돌아가면 onCreate() 다시 실행되는 문제가 있어서
			 * 중간의 투명 activity 는 portrait 로 고정하였음.
			 */
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        // size 변경되는 걸 알 수 있는 listener 등록
        registerSizeChangeListener();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        initSavedInstanceStateData(savedInstanceState);

        if (OAuthLoginDefine.DEVELOPER_VERSION) {
            //Log.d(TAG, "webview onRestoreInstanceState() first:" + mIsLoginActivityStarted + ", sdk:" + mOAuthSdkVersion + ", fix:" + mFixActivityPortrait);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (OAuthLoginDefine.DEVELOPER_VERSION) {
            //Log.d(TAG, "webview onSaveInstanceState()");
        }

        outState.putBoolean(INSTANCE_STATE_WEBVIEW_RUN_ONLY_ONCE, mIsLoginActivityStarted);

        if (mWebView != null) {
            mWebView.saveState(outState);
        }

        // activity portrait 관련 값
        outState.putString("SdkVersionCalledFrom", mOAuthSdkVersion);
        outState.putBoolean("IsFixActivityPortrait", mFixActivityPortrait);

        if (mVisibleNaverAppDownloadBanner && null != mNaverDownloadBanner && mNaverDownloadBanner.getVisibility() == View.VISIBLE) {
            outState.putBoolean(INSTANCE_STATE_IS_VISIBLE_BANNER, true);
        } else {
            outState.putBoolean(INSTANCE_STATE_IS_VISIBLE_BANNER, false);
        }
    }


    private void initIntentData() {
        mContext = DummyAppBrowserActivity.this;

        if (getIntent() != null) {
            String clientId = this.getIntent().getStringExtra(OAuthLoginInAppBrowserInIntentData.INTENT_PARAM_KEY_CLIENT_ID);
            String callbackUrl = this.getIntent().getStringExtra(OAuthLoginInAppBrowserInIntentData.INTENT_PARAM_KEY_CALLBACK_URL);
            String state = this.getIntent().getStringExtra(OAuthLoginInAppBrowserInIntentData.INTENT_PARAM_KEY_STATE);

            String locale = DeviceAppInfo.getBaseInstance().getLocaleString(mContext);
            String network = NetworkState.getNetworkState(mContext);

            mOAuthLoginData = new OAuthLoginData(clientId, null, callbackUrl, state);
            mInOAuthUrl = new OAuthQueryGenerator().generateRequestInitUrl(clientId, mOAuthLoginData.getInitState(), callbackUrl, locale, network);

            mOAuthSdkVersion = getIntent().getStringExtra(OAuthLoginInAppBrowserInIntentData.INTENT_PARAM_KEY_OAUTH_SDK_VERSION);
            mFixActivityPortrait = OAuthLoginUiUtil.isFixActivityPortrait(mOAuthSdkVersion);
        }
    }

    private void initSavedInstanceStateData(Bundle savedInstanceState) {

        if (null != savedInstanceState) {
            mIsLoginActivityStarted = savedInstanceState.getBoolean(INSTANCE_STATE_WEBVIEW_RUN_ONLY_ONCE);

            if (mWebView != null) {
                mWebView.restoreState(savedInstanceState);
            }

            // activity portrait 관련 값
            mOAuthSdkVersion = savedInstanceState.getString("SdkVersionCalledFrom");
            mFixActivityPortrait = savedInstanceState.getBoolean("IsFixActivityPortrait");
            mVisibleNaverAppDownloadBanner = savedInstanceState.getBoolean(INSTANCE_STATE_IS_VISIBLE_BANNER);
        }

    }



    private void runOnlyOnce() {
        // webview 에 설정하는건 한번만 실행
        if (getIntent() != null) {
            // 동의 페이지의 내용을 이미 httpclient로 받아온 경우 그걸 그대로 보여준다
            String url = this.getIntent().getStringExtra(OAuthLoginInAppBrowserInIntentData.INTENT_PARAM_KEY_AGREE_FORM_URL);
            if (!TextUtils.isEmpty(url)) {
                mInOAuthUrl = url;
            }

            mWebViewContent = this.getIntent().getStringExtra(OAuthLoginInAppBrowserInIntentData.INTENT_PARAM_KEY_AGREE_FORM_CONTENT);
        }

        if (TextUtils.isEmpty(mWebViewContent)) {
            if (OAuthLoginDefine.DEVELOPER_VERSION) {
                //Log.d(TAG, "webview url -> " + mInOAuthUrl);
            }
            mWebView.loadUrl(mInOAuthUrl);
        } else {
            if (OAuthLoginDefine.DEVELOPER_VERSION) {
                //Log.d(TAG, "webview url -> " + mInOAuthUrl);
                //Log.d(TAG, "webview content -> " + mWebViewContent);
            }
            mWebView.loadDataWithBaseURL(mInOAuthUrl, mWebViewContent, "text/html", null, null);
        }

    }


    @Override
    public void onResume() {
        super.onResume();

        // pause 는 안하지만 혹시 다른 곳에서 pause 했을 수도 있으니 들어오면서 resume 한다.
        if (mWebView != null) {
            mWebView.resumeTimers();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                mWebView.onResume();
            }
        }

        // resumeTimer() 를 실행하고 있음에도 불구하고 webview 의 javascript 가 실행안되는 경우가 발견되어 (갤럭시S2 4.0.3) onCreate() 에서 실행하던  runOnlyOnce() 메쏘드를 onResume() 의 webview.resumeTimers() 이후에 동작하게 했음.
        // 추가로 webview.onResume() 과 onPause() 도 실행하게 함
        if (false == mIsLoginActivityStarted) {
            if (OAuthLoginDefine.DEVELOPER_VERSION) {
                //Log.d(TAG, "webview onResume() first");
            }

            mIsLoginActivityStarted = true;
            runOnlyOnce();
        }


        if (OAuthLoginDefine.DEVELOPER_VERSION) {
            //Log.d(TAG, "webview onResume()");
        }
    }


    @Override
    public void onPause() {
        super.onPause();

        if (mWebView != null) {
            //mWebView.pauseTimers();	// 이거 호출하면 네앱이나 3rd-party 앱에 영향 미칠 수 있음

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                mWebView.onPause();
            }
        }

        if (OAuthLoginDefine.DEVELOPER_VERSION) {
            //Log.d(TAG, "webview onPause()");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (OAuthLoginDefine.DEVELOPER_VERSION) {
            //Log.d(TAG, "webview onDestroy()");
        }

        drawableByteBottomBackGroundImg = null;
        drawableByteCloseBtnImg = null;

        if (mWebView != null) {
            mWebView.stopLoading();

            if (mWholeView != null) {
                mWholeView.removeView(mWebView);
            }
            mWebView.removeAllViews();
            mWebView.destroy();
        }

    }


    @SuppressWarnings("deprecation")
    @SuppressLint("SetJavaScriptEnabled")
    private void initView(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        drawableByteBottomBackGroundImg = OAuthLoginImage.hexToByteArray(OAuthLoginImage.drawableByteStrBottomBackGroundImg);
        drawableByteCloseBtnImg = OAuthLoginImage.hexToByteArray(OAuthLoginImage.drawableByteStrCloseBtnImg);

        mWebviewProgressbar = new ProgressBar(DummyAppBrowserActivity.this, null, android.R.attr.progressBarStyleHorizontal);
        mWebviewProgressbar.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 5));
        mWebviewProgressbar.setVisibility(View.GONE);
        mWebviewProgressbar.setMax(100);

        mWebView = new WebView(DummyAppBrowserActivity.this);
        mWebView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0F));
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setVerticalScrollbarOverlay(true);
        mWebView.setHorizontalScrollbarOverlay(true);
        mWebView.setWebViewClient(new InAppWebViewClient());
        mWebView.setWebChromeClient(new InAppWebChromeClient());
        mWebView.setDownloadListener(mDefaultDownloadListener);
        mWebView.addJavascriptInterface(new InAppBrowserJavascriptInterface(this), "AndroidLoginWebView");
        String ua = mWebView.getSettings().getUserAgentString() + " " + DeviceAppInfo.getBaseInstance().getUserAgent(DummyAppBrowserActivity.this);
        mWebView.getSettings().setUserAgentString(ua);

        mNaviBar = new LinearLayout(DummyAppBrowserActivity.this);
        LinearLayout.LayoutParams param4navibar = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, (int) OAuthLoginImage.convertDpToPixel((float) 40, mContext));
        mNaviBar.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        mNaviBar.setLayoutParams(param4navibar);
        mNaviBar.setOrientation(LinearLayout.HORIZONTAL);
        Drawable bt = new BitmapDrawable(getResources(), BitmapFactory.decodeByteArray(drawableByteBottomBackGroundImg, 0, drawableByteBottomBackGroundImg.length));

        try {
            if (Build.VERSION.SDK_INT >= 16) {
                mNaviBar.setBackground(bt);
            } else {
                mNaviBar.setBackgroundDrawable(bt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mImgSeperator = new ImageView(DummyAppBrowserActivity.this);
        mImgSeperator.setLayoutParams(new LinearLayout.LayoutParams((int) OAuthLoginImage.convertDpToPixel(1, mContext), LayoutParams.FILL_PARENT));
        mImgSeperator.setBackgroundColor(Color.argb(255, 0, 0, 0));
        mImgSeperator.invalidate();

        mImgCloseButton = new ImageView(DummyAppBrowserActivity.this);
        mImgCloseButton.setLayoutParams(new LinearLayout.LayoutParams(OAuthLoginImage.getScreenWidth((Activity)mContext)/4, (int) OAuthLoginImage.convertDpToPixel((float) 32 * 2 / 3, mContext)));
        Drawable closeIcon = new BitmapDrawable(getResources(), BitmapFactory.decodeByteArray(drawableByteCloseBtnImg, 0, drawableByteCloseBtnImg.length));
        mImgCloseButton.setImageDrawable(closeIcon);

        mImgCloseButton.setClickable(true);
        mImgCloseButton.setOnClickListener(DummyAppBrowserActivity.this);

        mNaviBar.addView(mImgSeperator);
        mNaviBar.addView(mImgCloseButton);

        if (OAuthLoginDefine.MARKET_LINK_WORKING && mVisibleNaverAppDownloadBanner) {
            mNaverDownloadBanner = new OAuthLoginLayoutNaverAppDownloadBanner(DummyAppBrowserActivity.this);
        }

        ViewUtils.hideViews(mWebviewProgressbar, mNaviBar, mNaverDownloadBanner);
        mWebView.setBackgroundColor(Color.parseColor(("#d8000000")));
        mWholeView = new LinearLayout(DummyAppBrowserActivity.this);
        mWholeView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        mWholeView.setOrientation(LinearLayout.VERTICAL);
        if (OAuthLoginDefine.MARKET_LINK_WORKING && null != mNaverDownloadBanner && mVisibleNaverAppDownloadBanner) {
            mWholeView.addView(mNaverDownloadBanner);
        }
        mWholeView.addView(mWebviewProgressbar);
        mWholeView.addView(mWebView);
        mWholeView.addView(mNaviBar);

        setContentView(mWholeView);
    }

    /*
     * keyboard가 올라오면 하단의 navi-bar를 없앤다
     * <br/> 없애는 이유 : webview activity를 종료하는 X버튼을 사용자가 keyboard 닫는 버튼으로 착각하기 때문
     */
    private void registerSizeChangeListener() {
        final View activityRootView = mWholeView;
        // ref : http://stackoverflow.com/questions/2150078/how-to-check-visibility-of-software-keyboard-in-android
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Rect r = new Rect();
                        // r will be populated with the coordinates of your view that area still visible.
                        activityRootView.getWindowVisibleDisplayFrame(r);

                        int heightDiff = activityRootView.getRootView().getHeight() - (r.bottom - r.top);
                        if (heightDiff > 100) { // if more than 100 pixels, its probably a keyboard...
//                            mNaviBar.setVisibility(View.GONE);
                        } else {
//                            mNaviBar.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }



    @Override
    public void onClick(View view) {
        if (view == mImgCloseButton) {
            finish();
        }
    }


    private class InAppWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (mWebviewProgressbar != null) {
                //mWebviewProgressbar.setProgress(newProgress);
            }
        }
    }

    public class InAppBrowserJavascriptInterface {
        Context context;
        InAppBrowserJavascriptInterface(Context c) {
            context = c;
        }

        @JavascriptInterface
        public void closeWebView() {
            ((Activity) context).finish();
        }
    }

    /// URL 에 따른 WebView의 동작을 정의해둔 클래스
    /**
     * URL에 따라 {@link DummyAppBrowserActivity}를 종료시키거나, 기본 웹 브라우져를 실행시키거나, token을 얻어오는 동작을 하게됨
     * @author naver
     *
     */
    private class InAppWebViewClient extends WebViewClient {
        private String preUrl = "";

        public InAppWebViewClient() {
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (OAuthLoginDefine.DEVELOPER_VERSION) {
                //Log.d(TAG, "[star] pre url : " + preUrl);
                //Log.d(TAG, "[star]     url : " + url);
            }

            if (OAuthWebviewUrlUtil.isFinalUrl(false, preUrl, url)) {
                mWebView.stopLoading();
                finish();
                return ;
            }

            if (OAuthWebviewUrlUtil.returnWhenAuthorizationDone(mContext, preUrl, url, mOAuthLoginData)) {
                mWebView.stopLoading();
                return ;
            }

            super.onPageStarted(view, url, favicon);

            if (mWebviewProgressbar != null) {
                //mWebviewProgressbar.setVisibility(View.VISIBLE);
            }
        }

        /**
         * shouldOverrideUrlLoading은 post나 header redirect 방식의 페이지 이동 감지 못함
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (OAuthLoginDefine.DEVELOPER_VERSION) {
                //Log.d(TAG, "[over] pre url : " + preUrl);
//                Log.d(TAG, "[over]     url : " + url);
            }

            if (OAuthWebviewUrlUtil.isFinalUrl(true, preUrl, url)) {
                mWebView.stopLoading();
                finish();
                return true;
            }

            if (OAuthWebviewUrlUtil.returnWhenAuthorizationDone(mContext, preUrl, url, mOAuthLoginData)) {
                return true;
            }

            if (loadBrowser(url) == true) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                Uri u = Uri.parse(url);
                i.setData(u);
                startActivity(i);
                return true;
            }

            view.loadUrl(url);
            preUrl = url;
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            if (mWebviewProgressbar != null) {
                //mWebviewProgressbar.setVisibility(View.GONE);
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            if (mWebviewProgressbar != null) {
                //mWebviewProgressbar.setVisibility(View.GONE);
            }

            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public void onFormResubmission(WebView view, final Message dontResend, final Message resend) {
            super.onFormResubmission(view, dontResend, resend);
        }
    }


    private boolean loadBrowser(String url) {
        if (url.length() <= 0 || url.contentEquals("about:blank")) {
            return false;
        } else if (url.startsWith("https://nid.naver.com")) {
            //nid.naver.com 으로 시작하는 링크는 인앱 웹뷰이다.

            if (url.startsWith("https://nid.naver.com/mobile/user/help/idInquiry.nhn") ||
                    url.startsWith("https://nid.naver.com/mobile/user/help/pwInquiry.nhn") ||
                    url.startsWith("https://nid.naver.com/user/mobile_join.nhn") ) {
                //그 중에서 아이디찾기, 비번찾기, 회원가입은 외부브라우저로 띄우도록 한다.
                return true;
            } else {
                return false;
            }

        } else if (url.startsWith("https://nid.naver.com/nidlogin.logout") ||
                url.startsWith("http://nid.naver.com/nidlogin.logout")) {
            return false;
        } else if (url.contains("/sso/logout.nhn") ||
                url.contains("/sso/cross-domain.nhn") ||
                url.contains("/sso/finalize.nhn")) {
            return false;

        } else if (url.startsWith("http://cc.naver.com") || url.startsWith("http://cr.naver.com")) {
            return false;
        } else if (url.startsWith("https://cert.vno.co.kr")) {
            // 나이스 신용평가
            return false;
        } else if (url.startsWith("https://ipin.ok-name.co.kr")) {
            // 코리아 크레딧뷰로
            return false;
        } else if (url.startsWith("https://ipin.siren24.com")) {
            // 서신평
            return false;
        }

        return true;
    }


    final DownloadListener mDefaultDownloadListener = new DownloadListener() {
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
			/* test required */
            Intent viewIntent = new Intent(Intent.ACTION_VIEW);

            viewIntent.setDataAndType(Uri.parse(url), mimetype);

            try {
                startActivity(viewIntent); //caution : In case of the context not being attached a activity, this is useless
            } catch (Throwable th) {
                th.printStackTrace();
                try {
                    viewIntent.setData(Uri.parse(url));
                    startActivity(viewIntent);
                } catch (Exception ex) {
                }
            }
        }
    };

}