/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.support.wiselog;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.google.inject.Inject;
import com.gsshop.mocha.network.rest.RestClient;

import java.util.ArrayList;

import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.util.CookieUtils;
import gsshop.mobile.v2.util.ThreadUtils;
import roboguice.inject.ContextSingleton;
import roboguice.util.Ln;

import static gsshop.mobile.v2.MainApplication.getWiselogWebview;


@ContextSingleton
public class WiseLogAction {

    private RestClient restClient;

    @Inject
    public WiseLogAction(RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * 와이즈로그 서버에 로그 정보를 전달한다. (해더에 cookie 및 user-agent 포함됨)
     *
     * @param clickUrl 와이즈로그 URL
     */
    public void setWiseLog(String clickUrl){
        ThreadUtils.INSTANCE.runInBackground(() -> {
            try {
                if(!TextUtils.isEmpty(clickUrl)) {
                    //Ln.e("clickUrl(Rest) : " + clickUrl);
                    CookieUtils.syncAndcopyWebViewCookiesToRestClient(MainApplication.getAppContext(), restClient, clickUrl, String.class);
                }
            } catch(Exception e) {
                //e.printStackTrace();
            }
        });
    }

    // wise log 를 sequencial 하게 쏘기 위한 리스트
    private ArrayList<String> mListSend = new ArrayList<>();

    // runnable를 핸들 해줄 핸들러
    private Handler mHandlerList = null;
    // 실제 wise log를 쏠 Runnable
    private Runnable runListUrl = new Runnable() {
        @Override
        public void run() {
            if (mListSend != null && mListSend.size() > 0) {
                Ln.d("runListUrl clickUrl(HttpClient)222 : " + mListSend.get(0) + " | 1 / "  +mListSend.size() );
                getWiselogWebview().loadUrl(mListSend.get(0));
                mListSend.remove(0);
                if (mListSend.size() > 0) {
                    mHandlerList.postDelayed(this, 500);
                }
                else {
                    mHandlerList.removeCallbacks(runListUrl);
                    mHandlerList = null;
                }
            }
        }
    };

    /**
     * 와이즈로그 서버에 로그 정보를 전달한다. (해더에 user-agent 포함됨)
     *
     * @param clickUrl 와이즈로그 URL
     */
    public void setWiseLogHttpClient(final String clickUrl) {
        try {
            if(!TextUtils.isEmpty(clickUrl)) {
                Log.d("WiseLog Click URL", "clickUrl(HttpClient) : " + clickUrl);

                //웹뷰 사용
                getWiselogWebview().clearCache(true);

                mListSend.add(clickUrl);

                if (mHandlerList == null) {
                    mHandlerList = new Handler();
                    mHandlerList.postDelayed(runListUrl, 300);
                }

                //RestClient 사용
                //CookieUtils.syncAndcopyWebViewCookiesToRestClient(MainApplication.getAppContext(), restClient, clickUrl, String.class);

                //HttpClient 사용
                //서버에서 2번 호출되는 현상때문에 HttpClient로 교체
                /*HttpClient http = new DefaultHttpClient();
                HttpParams params = http.getParams();
                HttpConnectionParams.setConnectionTimeout(params, 5000);
                HttpConnectionParams.setSoTimeout(params, 5000);
                HttpGet httpGet = new HttpGet(clickUrl);
                httpGet.setHeader("User-Agent", System.getProperty("http.agent") +
                        MainApplication.getAppContext().getString(R.string.mc_rest_user_agent_additional));
                http.execute(httpGet);*/
            }
        } catch (Exception e) {
            Ln.e(e);
        }
    }
}
