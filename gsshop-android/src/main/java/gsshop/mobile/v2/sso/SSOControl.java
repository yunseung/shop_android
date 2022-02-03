package gsshop.mobile.v2.sso;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.util.Log;

import org.json.JSONObject;

import gsshop.mobile.v2.sso.utils.OnNeedContext;
import gsshop.mobile.v2.sso.utils.OnStringListener;
import gsshop.mobile.v2.sso.utils.OnTaskListener;
import gsshop.mobile.v2.sso.utils.Worker;

/**
 * SSO 메인 클래스.
 * 별도의 스레드를 통해서 SSO 토큰 가져오기 및 업데이트 등을 처리한다.
 */
public class SSOControl {
    private static final String TAG = "SSOControl";

    private static final int TASK_LOAD_TOKEN = 0;
    private static final int TASK_SAVE_TOKEN = 1;
    private static final int TASK_GET_TOKEN = 2;
    private static final int TASK_REMOVE_TOKEN = 3;

    // lalavla의 경우 buildConfigField를 가져올 수 없기에 processName을 통해서 app을 구별한다.
    // lalavla의 processName은 kr.co.watson.mobile 뒤의 이름을 잘라서 버리고 사용한다.
    private static final String[] appIDs = {
            "com.gsretail.android.thepop",
            "com.gsretail.supermarket",
            "com.gsr.gs25",
            "com.gsretail.android.smapp",
            "com.gsretail.android.dalisalda",
            "com.gsretail.android.marketfor",
            "kr.co.watson.mobile",
            "gsshop.mobile.v2"
    };

    private static SSOControl instance = new SSOControl();

    private SSOControl() {
        worker_.setOnTask(new OnTaskListener() {
            @Override
            public void onTask(int code, String text) {
                Log.d(TAG, String.format("onTask - code: %d, text: %s", code, text));
                switch (code) {
                    case TASK_LOAD_TOKEN: do_loadToken(text); break;
                    case TASK_SAVE_TOKEN: do_saveToken(text); break;
                    case TASK_GET_TOKEN: do_getToken(text); break;
                    case TASK_REMOVE_TOKEN: do_removeToken(text); break;
                }
            }
        });
    }

    public static SSOControl getInstance() {
        return instance;
    }

    /**
     * Application의 onCreate에서 SSO 초기화를 진행한다.
     * @param context Application의 Context 레퍼런스
     */
    public void initialize(Context context) {
        context_ = context;

        resolver_ = new SSOResolver(context);
        dataSource_ = new SSODataSource(context);

        String appID = getProcessName(context);
        Log.d(TAG, String.format("initialize - getProcessName: %s", appID));
        for (String id : appIDs) {
            if (appID.indexOf(id) < 0) {
                addProviderUri( String.format("content://%s.SSOProvider", id));
            }
        }
    }

    /**
     * 초기화 이후 토큰을 로딩한다. 다른 앱들이 실행 중이면 해당 앱에서 가져오고,
     * 다른 앱에서 가져올 수 없으면 로컬에 저장된 토큰을 가져온다.
     */
    public void loadToken() {
        worker_.add(TASK_LOAD_TOKEN, "");
    }

    /**
     * 토큰을 저장하고 다른 앱들에게 토큰을 전송한다. (DataProvide.update)
     * @param text 저장할 토큰 정보 = {chnnlCode: ..., ssoAuthToken: ...}
     */
    public void saveToken(String text) {
        Log.d(TAG, String.format("saveToken - %s", text));
        worker_.add(TASK_SAVE_TOKEN, text);
    }

    /**
     * 토큰 정보를 가져온다. 초기화를 먼저 완료하고 사용해야 한다.
     * 토큰 정보는 OnToken 이벤트를 통해서 전달된다.
     * @param text JSON 형식의 문자열이다. 토큰을 요청한 채널코드가 포함되어 있다. = {chnnlCode: ...}
     */
    public void getToken(String text) {
        Log.d(TAG, String.format("getToken - %s", text));
        worker_.add(TASK_GET_TOKEN, text);
    }

    /**
     * 토큰을 삭제한다.
     * @param text JSON 형식의 문자열이다. 토큰을 요청한 채널코드가 포함되어 있다. = {chnnlCode: ...}
     */
    public void removeToken(String text) {
        Log.d(TAG, String.format("removeToken - %s", text));
        worker_.add(TASK_REMOVE_TOKEN, text);
    }

    /**
     * 로컬에 저장된 토큰을 가져온다.
     * @return ssoAuthToken
     */
    public String getLocalToken() {
        if (dataSource_ != null) {
            // 다른 앱에서 호출하면서 새로 실행되는 경우에 initialize()가 되지 않아서 새로 생성해야함.
            if (onNeedContext_ != null) dataSource_ = new SSODataSource(onNeedContext_.getContext());
        }
        return dataSource_.loadFromLocalStorage();
    }

    /**
     * 로컬에 토큰을 저장하고 다른 앱들에게 update 하지 않는다.
     * 다른 앱으로부터 update 된 토큰을 저장할 때 사용한다.
     * @param token 저장할 토큰
     */
    public void saveLocalToken(String token) {
        if (dataSource_ != null) {
            if (onNeedContext_ != null) dataSource_ = new SSODataSource(onNeedContext_.getContext());
        }
        dataSource_.saveToken(token);
    }

    private static String getProcessName(Context context) {
        String result = "";
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            result = packageInfo.applicationInfo.processName;
        } catch(Exception e) {
        }

        Log.d(TAG, String.format("getProcessName - processName: %s", result));
        return result;
    }

    private void addProviderUri(String uri) {
        resolver_.addProviderUri(uri);
    }

    private void do_loadToken(String text) {
        String token = resolver_.getToken();
        if (token.length() != 0) {
            dataSource_.saveToken(token);
        } else {
            dataSource_.loadFromLocalStorage();
        }
    }

    private void do_saveToken(String text) {
        String ssoAuthToken;
        if (verifySSOAuthToken_) {
            ssoAuthToken = apiClient_.verifySSOAuthToken(text);
            if (ssoAuthToken.length() == 0) return;
        } else {
            try {
                JSONObject json = new JSONObject(text);
                ssoAuthToken = json.getString("ssoAuthToken");
            } catch (Exception e) {
                ssoAuthToken = "";
            }
        }

        dataSource_.saveToken(ssoAuthToken);
        resolver_.updateToken(ssoAuthToken);
    }

    private void do_getToken(String text) {
        String chCode = "";
        try {
            JSONObject json = new JSONObject(text);
            chCode = json.getString("chnnlCode");
        } catch (Exception e) {
            Log.d(TAG, "do_getToken - 웹에서 전달된 JSON 파싱 에러");
        }

        String ssoAuthToken;
        if (verifySSOAuthToken_) {
            ssoAuthToken = apiClient_.verifySSOAuthToken(chCode, dataSource_.getToken());
            if (ssoAuthToken.length() != 0) {
                dataSource_.saveToken(ssoAuthToken);
                resolver_.updateToken(ssoAuthToken);
            }
        } else {
            ssoAuthToken = dataSource_.getToken();
        }

        String result = "{\"result\": 0}";
        try {
            JSONObject json = new JSONObject();
            json.put("result", 0);
            json.put("chnnlCode", chCode);
            if (ssoAuthToken.length() > 0) {
                json.put("result", 1);
                json.put("ssoAuthToken", ssoAuthToken);
            }
            result = json.toString();
        } catch (Exception e) {
            Log.d(TAG, "do_getToken - ");
        }

        if (onToken_ != null) onToken_.onString(result);
    }

    private void do_removeToken(String text) {
        String chCode = "";
        String mbNo = "";
        try {
            JSONObject json = new JSONObject(text);
            chCode = json.getString("chnnlCode");
            mbNo = json.getString("unityMberNo");
        } catch (Exception e) {
            Log.d(TAG, "do_getToken - 웹에서 전달된 JSON 파싱 에러");
        }

        // SSO 서버에서 토큰을 삭제할 수 없는 경우에 대한 추가 변경이 있었다.
        // 서버에서 E017 코드를 리턴할 경우 클라이언트의 토큰을 삭제하지 말라는 지침대로 변경된 코드
        if (apiClient_.removeSSOSession(chCode, dataSource_.getToken(), mbNo)) {
            dataSource_.saveToken("");
            resolver_.updateToken("");
        }
    }

    public boolean isVerifySSOAuthToken() {
        return verifySSOAuthToken_;
    }

    /**
     * SSO 토큰을 사용하기 전 SSO 서버에서 검증을 받을 것인지 여부를 정한다.
     * @param value SSO 서버 검증 여부
     */
    public void setVerifySSOAuthToken(boolean value) {
        verifySSOAuthToken_ = value;
    }

    public void setOnNeedContext(OnNeedContext event) {
        onNeedContext_ = event;
    }
    public void setOnToken(OnStringListener event) {
        onToken_ = event;
    }

    private boolean verifySSOAuthToken_ = true;

    private OnNeedContext onNeedContext_ = null;
    private OnStringListener onToken_ = null;

    private Context context_ = null;
    private SSOResolver resolver_ = null;
    private SSODataSource dataSource_ = null;
    private SSOAPIClient apiClient_ = new SSOAPIClient();
    private Worker worker_ = new Worker();
}
