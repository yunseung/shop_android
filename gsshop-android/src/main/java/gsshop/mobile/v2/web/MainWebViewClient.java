/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.gsshop.mocha.web.BaseWebViewClient;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.web.handler.KakaoIntentUrlHandler;
import gsshop.mobile.v2.web.handler.WebUrlHandler;
import gsshop.mobile.v2.web.handler.WebUrlHandlerManager;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;

/**
 * 공통 {@link WebViewClient}.
 */
public class MainWebViewClient extends BaseWebViewClient {

    private final Activity activity;
    protected final long READ_TIMEOUT=6000L;

    /**
     * 500 / 404 / 400 에러 처리항 대상 페이지
     */
    public static final String EXCEPTION_TARGET_PRD = "/prd/prd.gs";
    public static final String EXCEPTION_TARGET_DEAL = "/deal/deal.gs";
    public static final String EXCEPTION_TARGET_PLAN = "/plan/shop/detail.gs";
    public static final String EXCEPTION_TARGET_JBPBRAND = "/jbp/brandMain.gs";
    public static final String EXCEPTION_TARGET_CART = "/cart/viewCart.gs";

    private static String[] EXCEPTION_TARGET_LIST = {EXCEPTION_TARGET_PRD,EXCEPTION_TARGET_DEAL,EXCEPTION_TARGET_PLAN,EXCEPTION_TARGET_CART,EXCEPTION_TARGET_JBPBRAND };

    private final String JS_INTERFACE_NAME = "AppInterface";

    public MainWebViewClient(Activity activity) {
        super(activity);
        this.activity = activity;
//        READ_TIMEOUT = activity.getResources().getInteger(R.integer.mc_read_timeout);
    }

    @Override
    public boolean handleCustomProtocol(WebView view, String url) {
        // 비어있는 주소인 경우 아무일도 하지 않음
        if (url.length() == 0) {
            return true;
        }

        // 커스텀 프로토콜은 url을 소문자로 변환하여 넘겨서 비교
        WebUrlHandler h = WebUrlHandlerManager.findCustomUrlHandler(url.toLowerCase());
        if (h != null) {
            Ln.i(url);

            if (h.handle(activity, view, url)) {
                return true;
            } else {
                // jhkim "vnd.android-dir/mms-sms" 지원하지 않는 기기의 경우, 토스트 띄움
                Uri uri = Uri.parse(url);
                Ln.i(uri.getScheme() + " " + uri.getHost());
                if ("sms".equalsIgnoreCase(uri.getHost())) {
                    Toast.makeText(view.getContext(), R.string.sms_not_support, Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
        }

        if (h != null && h.handle(activity, view, url)) {
            return true;
        }

        // 카카오 Intent 처리로직 확인.
        KakaoIntentUrlHandler handelrKakao = new KakaoIntentUrlHandler();
        if (handelrKakao.handle(activity, view, url)) {
            return true;
        }

        // nexus 7 전화기 없는 타블릿에서 ERR_UNKNOWN_URL_SCHEME 오류 처리.
        boolean b = super.handleCustomProtocol(view, url);
        if (url.startsWith(WebView.SCHEME_TEL) && !b) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));

            activityContext.startActivity(intent);
            return true;
        }

        return b;
    }

    @Override
    public boolean handleHttpProtocol(WebView view, String url) {
        WebUrlHandler h = WebUrlHandlerManager.findHttpUrlHandler(url);
        if (h != null) {
            Ln.i(url);

        }
        if (h != null && h.handle(activity, view, url)) {
            return true;
        }

        return super.handleHttpProtocol(view, url);
    }

    /**
     * 웹페이지 로딩시 네트워크 연결 관련 에러가 발생하면 다이얼로그를 띄운다.
     * <p>
     * 이 메소드는 웹페이지 내의 image, javascript, css 등을 불러오는
     * 요청에 대해서는 적용되지 않는다.
     *
     * @param view        view
     * @param errorCode   errorCode
     * @param description description
     * @param failingUrl  failingUrl
     */
    @Override
    public void onReceivedError(final WebView view, int errorCode, String description,
                                String failingUrl) {

        Ln.i(description + " : " + failingUrl);

        //네트워크 에러팝업 중복으로 뜨는 현상 개선 (와이즈로그 주소인 경우 경우 오류팝업을 띄우지 않음)
        //와이즈로그 주소가 변경되는 경우, 변경된 값에 맞게 아래 라인 변경이 필요함
        if (failingUrl.contains("wiseLog")
                || failingUrl.contains("commonClickTrac")) {
            return;
        }

        if (errorCode == ERROR_CONNECT || errorCode == ERROR_HOST_LOOKUP || errorCode == ERROR_IO
                || errorCode == ERROR_TIMEOUT) {

            if (!activity.isFinishing()) {
                preventNativeErrorPage(view);
                View refresh_layout = activity.findViewById(R.id.flexible_refresh_layout);
                //웹액티비티에서만 노출 (홈액티비티내에 구매하기 클릭시 호출되는 inner webview에서 본 콜백이 호출되는 경우 제외)
                if (isNotEmpty(refresh_layout) && activity instanceof BaseWebActivity) {
                    refresh_layout.setVisibility(View.VISIBLE);
                    //웹뷰가 표시하는 에러메시지를 노출하지 않기 위해 추가 ("ERR_INTERNET_DISCONNECTED...")
                    //웹뷰를 다시 노출로 세팅하는 위치 (BaseWebActivity클래스의 loadUrl과 onNewIntent 함수내 )
                    view.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    /**
     * 400 / 500 / 404 등의 절대적인 애들은 에러 팝업을 띄우도록 한다
     * @param view
     * @param request
     * @param errorResponse
     */
    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {


        Ln.i("getPath getStatusCode : " + request.getUrl().getPath() +" : "+ errorResponse.getStatusCode());

        //지정된 패스가 아니면 처리하면 난리 난다.. 오만가지가 들어옵니다
        //app/statistic/wiseLog
        //favicon.ico
        //mobile/cart/viewCart1.gs
        //api/us/gsshop.com/59d93d/mmapi.js

        //리퀘스트가 유효하고,  대상 패스일때,
        if(request != null && request.getUrl() != null && request.getUrl().getPath() != null && isExceptionTarget(request.getUrl().getPath()) )
        {
            //에러 코드가 유효하고,
            if(errorResponse != null)
            {
                //대상인 유효코드면?
                if(errorResponse.getStatusCode() == HttpURLConnection.HTTP_BAD_REQUEST ||
                        errorResponse.getStatusCode() == HttpURLConnection.HTTP_NOT_FOUND ||
                        errorResponse.getStatusCode() == HttpURLConnection.HTTP_INTERNAL_ERROR)
                {
                    if (!activity.isFinishing())
                    {
                        preventNativeErrorPage(view);
                        View refresh_layout = activity.findViewById(R.id.flexible_refresh_layout);
                        //웹액티비티에서만 노출 (홈액티비티내에 구매하기 클릭시 호출되는 inner webview에서 본 콜백이 호출되는 경우 제외)
                        if (isNotEmpty(refresh_layout) && activity instanceof BaseWebActivity)
                        {
                            refresh_layout.setVisibility(View.VISIBLE);
                            //웹뷰가 표시하는 에러메시지를 노출하지 않기 위해 추가 ("ERR_INTERNET_DISCONNECTED...")
                            // 웹뷰를 다시 노출로 세팅하는 위치 (BaseWebActivity클래스의 loadUrl과 onNewIntent 함수내 )
                            view.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }
        }
        super.onReceivedHttpError(view, request, errorResponse);
    }

    /**
     * WebView 네이티브 에러페이지를 띄우지 않고
     * 이전 페이지로 돌아가거나 공백페이지를 보여줌.
     *
     * @param view view
     */
    public void preventNativeErrorPage(WebView view) {
        if (view.canGoBack()) {
            view.goBack();
        } else {
            view.loadUrl(ServerUrls.WEB.BLANK);

            // 뒤로가기시 로딩못한 페이지 다시 시도하지 않게 히스토리 지움
            view.clearHistory();
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
            if (MESSAGE_PROGRESS_TIMEOUT == message.what) {
                if (progress != null && progress.isShowing()) {
                    progress.hide();
                }
            }
        }
    };

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        Ln.i("[MainWebViewClient onPageStarted]" + url);

        String urlHost = "";
        try {
            URL serverUrl = new URL(url);
            urlHost = serverUrl.getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        view.removeJavascriptInterface(JS_INTERFACE_NAME);
        //자바 스크립트 설정 변경
        if (urlHost.endsWith("gsshop.com")) {
            view.addJavascriptInterface(AndroidBridge.getInstance(activity, view), JS_INTERFACE_NAME);
        }

        super.onPageStarted(view, url, favicon);

        mTimeoutHandler.removeMessages(MESSAGE_PROGRESS_TIMEOUT);
        mTimeoutHandler.sendEmptyMessageDelayed(MESSAGE_PROGRESS_TIMEOUT, READ_TIMEOUT);
    }

    @Override
    public void onPageFinished(WebView view, String url) {

        super.onPageFinished(view, url);

    }


    /**
     * 예외처리로 패스가 일치하면 404 / 500 에 대해서 에러 팝업 띄움
     * @param path
     * @return
     */
    public static boolean isExceptionTarget(String path)
    {
        for (int i = 0; i < EXCEPTION_TARGET_LIST.length; i++) {
            String target = EXCEPTION_TARGET_LIST[i];
            if(path != null){
                if (path.contains(target)) {
                    return true;
                }
            }
        }
        return false;
    }
}
