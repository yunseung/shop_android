package gsshop.mobile.v2.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gsshop.mocha.ui.back.BackKeyHandler;
import com.gsshop.mocha.ui.back.WebPageBackHandler;
import com.gsshop.mocha.web.WebViewControl;
import com.gsshop.mocha.web.WebViewProgressBar;

import java.util.Locale;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.menu.TabMenu;
import gsshop.mobile.v2.web.AndroidBridge;
import gsshop.mobile.v2.web.BaseWebActivity;
import gsshop.mobile.v2.web.CustomWebView;
import gsshop.mobile.v2.web.MainWebViewChromeClient;
import gsshop.mobile.v2.web.MainWebViewClient;
import gsshop.mobile.v2.web.WebUtils;
import gsshop.mobile.v2.web.WebViewControlInherited;
import roboguice.util.Ln;

import static gsshop.mobile.v2.MainApplication.useNativeProduct;
import static gsshop.mobile.v2.web.WebUtils.isNativeProduct;

/**
 *
 * 바로 주문 전용 뷰
 *
 * Created by jhkim on 16. 3. 30..
 *
 */
public class DirectOrderView extends FrameLayout{

    /**
     * 바로주문 웹뷰
     */
    public WebViewControlInherited webControl;
    private static final String JS_INTERFACE_NAME = "AppInterface";

    private static final int WEB_TYPE_DEFAULT = 0;
    private static final int WEB_TYPE_NO_TAB = 1;
    private static final int WEB_TYPE_MY_SHOP = 2;
    private static final int WEB_TYPE_ORDER = 5;
    private static final int WEB_TYPE_DIRECTORDER = 6;
    private static final int WEB_TYPE_MOBILE_LIVE = 7;

    /**
     * 바로 주문 활성화 여부
     */
    public static boolean isVisibleDirectOrder = false;

    private BackKeyHandler backHandler;

    private Activity activity;

    private CustomWebView directOrderWebView;

    /**
     * 생성자
     * @param context context
     */
    public DirectOrderView(Context context) {
        super(context);
    }

    /**
     * 생성자
     * @param context context
     * @param attrs AttributeSet
     * @param defStyle 스타일
     */
    public DirectOrderView(Context context, AttributeSet attrs, int defStyle) {
        this(context, null);
    }

    public DirectOrderView(Context context, AttributeSet attrs) {
        super(context, null);

    }

    /**
     * initDirectOrderView 기본 초기화 로직
     * @param activity activity
     * @param url url
     */
    public void initDirectOrderView(Activity activity, String url){
        this.activity = activity;
        directOrderWebView = (CustomWebView) activity.findViewById(R.id.direct_order_view);
        directOrderWebView.setBackgroundColor(0);
        setupWebControl(activity);

        if(!TextUtils.isEmpty(url)){
            webControl.loadUrl(url, MainApplication.customHeaders);
        }
    }

    /**
     * web page progress bar time out
     */
    private static final int MESSAGE_PROGRESS_TIMEOUT = 0x2;

    /* Handler to update the time once a second in interactive mode. */
    private final Handler mTimeoutHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            if (!isTimedOut) {
                return;
            }

            if (MESSAGE_PROGRESS_TIMEOUT == message.what) {
                Toast.makeText(activity, "불러오기 실패했습니다.", Toast.LENGTH_SHORT).show();
                closeThisView();
            }
        }
    };

    private void closeThisView() {
        if (activity instanceof HomeActivity) {
            ((HomeActivity) activity).hideOrderDirectWebView();
        } else if (activity instanceof BaseWebActivity) {
            ((BaseWebActivity) activity).hideOrderDirectWebView();
        }
    }

    // 타임아웃 체크 변수
    boolean isTimedOut = true;
    private void setupWebControl(final Activity activity) {

//        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);

        webControl = new WebViewControlInherited.Builder(activity)
                .target(directOrderWebView)
                .with(new MainWebViewClient(activity) {

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        isTimedOut = true;
                        mTimeoutHandler.removeMessages(MESSAGE_PROGRESS_TIMEOUT);
                        mTimeoutHandler.sendEmptyMessageDelayed(MESSAGE_PROGRESS_TIMEOUT, READ_TIMEOUT);
                    }

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        Ln.i("[DirectOrderWebView shouldOverrideUrlLoading]" + url);
                        //구매 옵션 액티비티가 단품과 같은 노탭 액티비티 이므로 페이지 이동이 아닌 새창 띄우기 위함

                        // 웹뷰 닫는거 만들어 -> handler 추가
                        if (ServerUrls.APP.CLOSE.equals(url)) {
                            closeThisView();
                            return true;
                        }

                        downAnimation(activity);

                        // WEB_TYPE_DIRECTORDER 새로 생성(기존 사용하던 WEB_TYPE_NOTAB 사용 시, 상품 상세 페이지 안 뜨는 버그 발생)
                        if (handleUrl(activity, WEB_TYPE_DIRECTORDER, url)) {
                            // 페이지 이동 발생하면 창 닫기
                            return true;
                        } else {
//                            downAnimation(activity);
                            return super.shouldOverrideUrlLoading(view, url);
                        }
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        // 페이지 불러오기가 종료되었을 경우에는 timeout false
                        isTimedOut = false;
                    }

                    @Override
                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                        super.onReceivedError(view, errorCode, description, failingUrl);

                        Toast.makeText(activity, "불러오기 실패했습니다.", Toast.LENGTH_SHORT).show();
                        closeThisView();
                    }
                }).with(new MainWebViewChromeClient(activity) {
                    @Override
                    public void onProgressChanged(WebView view, int newProgress) {
                        super.onProgressChanged(view, newProgress);
                        //newProgress값이 87~100 범위에서 늦어지는 현상 및 100이 호출되지 않거나 늦게 호출되는 현상이 발생하여 대응코드 삽입
                        if ((newProgress == 100) && webControl != null && webControl.getProgress() != null) {
                            webControl.getProgress().hide();
                        }
                    }
                }).with(new WebViewProgressBar((ProgressBar) findViewById(R.id.progress))).build();

        if (webControl != null) {
            backHandler = new WebPageBackHandler(activity, webControl.getWebView());
            // 웹뷰 오른쪽 여백 없애기
            webControl.getWebView().setVerticalScrollbarOverlay(true);
            webControl.getWebView().setScrollContainer(true);
            webControl.getWebView().getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

            webControl.getWebView().getSettings().setUseWideViewPort(true);
            webControl.getWebView().getSettings().setLoadWithOverviewMode(true);
            //이건 몰까
            webControl.getWebView().getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                webControl.getWebView().getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.setAcceptCookie(true);
                cookieManager.setAcceptThirdPartyCookies(webControl.getWebView(), true);
            }
            //webview 시스템 폰트 사이즈 고정(보류)
            //이놈은 BaseWebActivity를 상속받지 않음
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                float scale = getResources().getConfiguration().fontScale;
                //1.2 보다 크면
                if(scale > 1.1) // 1.2 이상보다 크면 MAX 110
                    webControl.getWebView().getSettings().setTextZoom(110);
                else if(scale < 1.0) // 1.0 보다 작으면 MIN 90
                    webControl.getWebView().getSettings().setTextZoom(90);
                else                // 그외 무조건 100 ( 고정 )
                {
                    // 그외 경우가 91~109 라고 판단하여 설정하지 않는다. 시스테을 그대로 따른다.
                    // 이유 : 100이라고 설정하면 100설정하기 전보다 아주 작지만 크거나 작아 보인다. 원인은 모르겠다.
                    //mWebControl.getWebView().getSettings().setTextZoom(100);
                }

            }

            webControl.getWebView().addJavascriptInterface(AndroidBridge.getInstance(activity, webControl.getWebView()), JS_INTERFACE_NAME);

        }
    }

    private boolean handleUrl(Activity activity, int currentWebType, String origUrl){
        boolean handled = true;

        String url = origUrl.toLowerCase(Locale.getDefault());

        // 이동할 url을 체크해서 web activity type 결정
        // modal web은 "toapp://modal"로 내려오기 때문에 따로 처리 안함.
        int nextWebType;
        if (WebUtils.isNoTabPage(url)) {
            nextWebType = WEB_TYPE_NO_TAB;
        } else if (WebUtils.isMyShop(url)) {
            nextWebType = WEB_TYPE_MY_SHOP;
        } else if (WebUtils.isOrder(url)) {
            nextWebType = WEB_TYPE_ORDER;
        } else if (WebUtils.isMobileLiveUrl(url)) {
            nextWebType = WEB_TYPE_MOBILE_LIVE;
        }
        else {
            nextWebType = WEB_TYPE_DEFAULT;
        }

        // web page이면
        if (url.startsWith("http")) {
            // gsshop 내의 page이면
            if (url.contains("gsshop.com")) { // FIXME : jisun host 체크 루틴 정리하기
                if (currentWebType == nextWebType || url.contains("error")
                        || url.contains("estimatewritecheck.gs")) { // 상품평 작성 불가 웹 팝업 처리
                    // 현재 url과 다음 url이 같은 web type이거나,
                    // error 페이지로 redirect되는 경우는
                    // 여기서 다른 web activity로 가지 않고 호출한 webView에서 처리
                    handled = false;
                } else {
                    // 다른 web type의 url이면 해당 activity로 이동
                    switch (nextWebType) {
                        case WEB_TYPE_NO_TAB:
                            if (useNativeProduct) {
                                if (isNativeProduct(url)) {
                                    //native 단품
                                    WebUtils.goNativeProduct(activity, origUrl);
                                } else {
                                    //web 단품
                                    WebUtils.goNoTabWebActivity(activity, origUrl);
                                }
                            } else {
                                Intent noTabIntent = new Intent(Keys.ACTION.NO_TAB_WEB);
                                noTabIntent.putExtra(Keys.INTENT.WEB_URL, origUrl);
                                noTabIntent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.getTabMenu(activity.getIntent()));
                                noTabIntent.putExtra(Keys.INTENT.BACK_TO_MAIN, activity.getIntent().getBooleanExtra(Keys.INTENT.BACK_TO_MAIN, false));
                                activity.startActivity(noTabIntent);
                            }
                            break;
                        case WEB_TYPE_MY_SHOP:
                            Intent myShopIntent = new Intent(Keys.ACTION.MY_SHOP_WEB);
                            myShopIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            myShopIntent.putExtra(Keys.INTENT.WEB_URL, origUrl);
                            myShopIntent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.getTabMenu(activity.getIntent()));
                            activity.startActivity(myShopIntent);
                            break;
                        case WEB_TYPE_ORDER:
                            Intent orderIntent = new Intent(Keys.ACTION.ORDER_WEB);

                            // ! not 유의
                            if( WebUtils.isOrderTabCheck(origUrl) == false ) {
                                orderIntent.setAction(Keys.ACTION.ORDER_NOTAB_WEB);
                            }
                            orderIntent.putExtra(Keys.INTENT.WEB_URL, origUrl);
                            orderIntent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.getTabMenu(activity.getIntent()));
                            activity.startActivity(orderIntent);
                            break;
                        case WEB_TYPE_MOBILE_LIVE :
                            Intent mobileLiveNoTabActivity = new Intent(Keys.ACTION.MOBILELIVE_NO_TAB_WEB);
                            mobileLiveNoTabActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            mobileLiveNoTabActivity.putExtra(Keys.INTENT.WEB_URL, origUrl);
                            mobileLiveNoTabActivity.putExtra(Keys.INTENT.TAB_MENU, TabMenu.getTabMenu(activity.getIntent()));
                            activity.startActivity(mobileLiveNoTabActivity);
                            break;
                        default:
                            Intent intent = new Intent(Keys.ACTION.WEB);
                            intent.putExtra(Keys.INTENT.WEB_URL, origUrl);
                            intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.getTabMenu(activity.getIntent()));
                            activity.startActivity(intent);
                            break;
                    }
                }
            } else {
                // 외부망이면
                handled = false; // 여기서 처리하지 않고 호출한 webView에서 처리
            }
        } else {
            handled = false; // 여기서 처리하지 않고 호출한 webView에서 처리
        }
        return handled;
    }

    public void upAnimation(Activity activity, String url){
        // 구매하기 웹에서 애니메이션 보여줄 예정 애니메이션 삭제
        try{
            initDirectOrderView(activity, url);
            setVisibility(View.VISIBLE);

            isVisibleDirectOrder = true;
        }catch(Exception e){
            Ln.i(e);
        }

    }

    public void downAnimation(Activity activity){
        // 구매하기 웹에서 애니메이션 보여줄 예정 애니메이션 삭제
        try {
            if (isVisibleDirectOrder) {
                isVisibleDirectOrder = false;
                setVisibility(View.GONE);
                directOrderWebView.loadUrl(ServerUrls.WEB.BLANK);
            }
        }catch(Exception e){
            Ln.i(e);
        }

    }

}
