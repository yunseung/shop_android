/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web.handler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;

import com.google.inject.Inject;

import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.Keys.NonMemberLoginType;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.menu.BaseTabMenuActivity;
import gsshop.mobile.v2.menu.TabMenu;
import gsshop.mobile.v2.user.UserAction;
import gsshop.mobile.v2.util.LunaUtils;
import gsshop.mobile.v2.web.productDetail.ProductDetailWebActivityV2;

import static gsshop.mobile.v2.util.LunaUtils.autoLoginState;

/**a
 * 로그인 앱화면 요청.
 *
 * ex) http://tftm.gsshop.com/member/toAppLogin.gs?param=http://tftm.gsshop.com/mygsshop/myOrderList.gs
 * ex) toapp://login?isPrdOrder=Y&isAdult=Y{@literal &}param=http://m.gsshop.com/mygsshop/myOrderList.gs?a=A{@literal &}b=b
 *
 * NOTE :
 * - 웹과의 약속에 의해 쿼리스트링은 URL인코딩되어 있지 않음.
 * - param 파라미터는 맨 마지막에 있어야함.
 *   즉, 위 예에서 param 값은 http://m.gsshop.com/mygsshop/myOrderList.gs?a=A{@literal &}b=b 까지임.
 */
public class LoginUrlHandler implements WebUrlHandler {

    private static final String DIRECT_ORDER_PARAM = "isPrdOrder=Y";

    private static final String ADULT_PARAM = "isAdult=Y";

    private static final String TARGET_URL_PARAM = "param=";

    //파싱하는 방식으 위의것들과 다름
    private static final String TARGET_MSG_PARAM = "msg=";

    //파싱하는 방식으 위의것들과 다름
    private static final String MSG_PARAM = "msg";

    @Inject
    private UserAction userAction;

    public String publicKey = "";

    public Activity m_activity;
    public WebView m_webview;
    public String m_url;
    public Context context;

    /**
     * 타이머 타스트
     */
    private TimerTask timerTask;

    /**
     * 자동로그인 수행을 위한 최대 대기시간
     */
    private static long WAITING_SECOND_FOR_AUTOLOGIN = 3000;

    @Override
    public boolean handle(Activity activity, WebView webview, String url) {
        //NOTE : url 쿼리스트링이 표준에 따라 올바로 인코딩된 경우에 사용가능.
        //Uri uri = Uri.parse(url);

        EventBus.getDefault().unregister(this);
        EventBus.getDefault().register(this);

        m_activity = activity;
        m_webview = webview;
        m_url = url;

        Uri uri = Uri.parse(url);
        boolean isExternalCall = "Y".equalsIgnoreCase(uri.getQueryParameter("closeYn"));
        if (isExternalCall) {
            if (!(activity instanceof HomeActivity)) {
                activity.finish();
            }
        }

        if (m_activity instanceof ProductDetailWebActivityV2 && ((ProductDetailWebActivityV2)m_activity).isAutoLoginTarget()) {
            //네이티브 단품
            autoLoginState = LunaUtils.AutoLoginState.CUSTOM_TRYING;
            ((ProductDetailWebActivityV2)m_activity).doAutoLoginWhenNetworkActivated(true);
        } else if (m_activity instanceof BaseTabMenuActivity && ((BaseTabMenuActivity)m_activity).isAutoLoginTarget()) {
            ////네이티브 단품 외 액티비티
            //자동로그인 재시도 대상이면 자동로그인 수행 WAITING_SECOND_FOR_AUTOLOGIN 후 로그인화면 노출
            //WAITING_SECOND_FOR_AUTOLOGIN 시간내 자동로그인 성공하면 홈화면으로 이동
            autoLoginState = LunaUtils.AutoLoginState.CUSTOM_TRYING;
            ((BaseTabMenuActivity)m_activity).doAutoLoginWhenNetworkActivated(true);
        } else {
            //자동로그인 재시도 대상이 아니면 바로 로그인화면 노출
            WAITING_SECOND_FOR_AUTOLOGIN = 0;
        }

        goLoginTask(WAITING_SECOND_FOR_AUTOLOGIN);

        return true;
    }

    /**
     * 자동로그인 성공/실패 이벤트를 수신한다.
     *
     * @param event AutoLoggedInEvent
     */
    public void onEvent(Events.AutoLoggedInEvent event) {
        if (event.isSuccess) {
            //자동로그인 성공시 로그인화면 노출할 필요 없음 (홈으로 이동)
            if (timerTask != null) {
                timerTask.cancel();
                timerTask = null;
            }
            EventBus.getDefault().unregister(this);
        } else {
            //자동로그인 실패시 로그인화면 바로 노출(서버응답 받은 실패)
            //타입아웃 등 익셉션 발생으로 실패한 경우는 본 블럭을 타지 않으므로 WAITING_SECOND_FOR_AUTOLOGIN 시간 후 로그인화면 노출
            goLoginTask(0);
        }
    }

    /**
     * 웹뷰 로딩 타스크를 생성한다.
     *
     * @param delay
     */
    private void goLoginTask(long delay) {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        timerTask = new TimerTask() {
            @Override
            public void run() {
                m_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        goLogin();
                    }
                });
            }
        };

        new Timer().schedule(timerTask, delay);
    }

    /**
     * 탭메뉴 또는 Non-web액티비티에서 웹의 302 redirect(HTTP/1.1 302 Moved Temporarily)에 의해
     * 로그인화면이 요청된 경우인가. (original url이 null이고 이전 back 웹페이지가 없음).
     *
     * @param webview
     */
    private boolean redirectedByNonWeb(WebView webview) {
        return (webview.getOriginalUrl() == null && !webview.canGoBack());
    }

    @Override
    public boolean match(String url) {
        return url.startsWith(ServerUrls.APP.LOGIN);
    }

    private void goLogin() {
        EventBus.getDefault().unregister(this);

        Intent intent;
        intent = new Intent(Keys.ACTION.LOGIN);
        
        intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.getTabMenu(m_activity.getIntent()));

        // 바로주문을 통해서 넘어온 요청인가?
        if (m_url.contains(DIRECT_ORDER_PARAM)) {
            intent.putExtra(Keys.INTENT.NON_MEMBER_LOGIN_TYPE, NonMemberLoginType.DIRECT_ORDER);
        }

        // 성인인증용 로그인 요청인가?
        if (m_url.contains(ADULT_PARAM)) {
            // 바로주문 유형을 덮어씀.
            intent.putExtra(Keys.INTENT.NON_MEMBER_LOGIN_TYPE, NonMemberLoginType.ADULT_CHECK);
        }

        // 타겟주소(로그인 후 띄워줄 URL) 지정된 경우
        if (m_url.indexOf(TARGET_URL_PARAM) > -1) {
            String targetUrl = m_url.substring(m_url.indexOf(TARGET_URL_PARAM)
                    + TARGET_URL_PARAM.length());
            intent.putExtra(Keys.INTENT.WEB_URL, targetUrl);
        }

        // id입력창에 표시될 메세지 지정된 경우
        //  "msg="; 으로 유무 판단
        //  "msg" getQueryParameter 으로 아이디를 뽑아냄
        if (m_url.indexOf(TARGET_MSG_PARAM) > -1) {
            Uri uri = Uri.parse(m_url);
            String targetMsg = uri.getQueryParameter(MSG_PARAM);
            //String targetMsg = m_url.substring(m_url.indexOf(TARGET_MSG_PARAM) + TARGET_MSG_PARAM.length());
            intent.putExtra(Keys.INTENT.WEB_TARGET_MSG, targetMsg);
        }

        // (1) Non웹액티비티에서 로그인 요청된 경우.
        // 로그인 후 새 웹액티비티에서 타겟 웹페이지 로딩함
        if (redirectedByNonWeb(m_webview)) {
            boolean fromTabMenu = TabMenu.fromTabMenu(m_activity.getIntent());
            intent.putExtra(Keys.INTENT.FROM_TAB_MENU, fromTabMenu);

            if (fromTabMenu) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }

            m_activity.startActivity(intent);
            m_activity.finish();
        } else {
            // (2) 웹액티비티에서 로그인 요청된 경우.
            // 로그인 후 원래 웹액티비티에서 타겟 웹페이지를 로딩함
            intent.putExtra(Keys.INTENT.FOR_RESULT, true);
            m_activity.startActivityForResult(intent, Keys.REQCODE.LOGIN);
        }

    }
}
