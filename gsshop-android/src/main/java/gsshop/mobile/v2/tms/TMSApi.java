package gsshop.mobile.v2.tms;

import android.content.Context;

import com.tms.sdk.api.request.DeviceCert;
import com.tms.sdk.api.request.Login;
import com.tms.sdk.api.request.Logout;
import com.tms.sdk.api.request.NewMsg;
import com.tms.sdk.api.request.ReadMsg;
import com.tms.sdk.bean.APIResult;

import roboguice.util.Ln;

/**
 * pms api
 * @author erzisk
 * @version
 * [2013.10.08] PMS sdk 적용<br>
 * [2013.10.11] android-query -> volley 로 library 변경<br>
 * [2013.10.11] PushPopupFragment에서 link parsing관련 수정<br>
 * [2013.10.21 10:31] inbox접근 시 readMsg요청 위치 변경<br>
 * [2013.10.21 11:23] PMS sdk 적용<br>
 * [2013.10.21 14:52] PushPopupFragment삭제, 전부 GSPushPopupActivity로 넘김<br>
 * [2013.10.22 09:06] PMS sdk 적용<br>
 * [2013.10.24 10:29] GS API call 빈도 변경 (GSApi.java)<br>
 * [2013.10.25 14:22] PMS sdk 적용<br>
 * [2013.10.29 17:04] PMS sdk 적용<br>
 * [2013.10.29 17:04] login시 setCustId추가<br>
 * [2013.11.06 16:10] PMS sdk 적용<br>
 * [2013.11.06 16:44] GSApi call 부분 변경, custId와 loginedCustId가 다를경우도 true<br>
 * [2013.11.06 17:17] logout시 userData삭제<br>
 * [2013.11.15 11:02] GSAPI 호출 시 volley의 error response에서도 callback수행하게끔 변경<br>
 * [2013.11.19 15:35] push popup finish thread 추가 (inbox에서 클릭하여 나온 popup은 auto finish가 없음)<br>
 * [2013.11.19 20:43] PMS sdk 적용<br>
 * [2013.11.21 09:01] 메시지 읽음 시점 변경<br>
 * [2013.11.26 13:08] PMS sdk 적용<br>
 * [2013.12.03 15:27] PMS sdk 적용<br>
 * [2013.12.04 14:43] pushp popup 지속시간 변경 5초 -> 7초<br>
 * [2013.12.04 17:23] 메시지 리스트 화면에 접근할 때, 무조건 전체 메시지 가져오게 변경<br>
 * [2013.12.05 15:42] PMS sdk 적용<br>
 * [2013.12.11 16:09] PMS sdk 적용<br>
 * [2013.12.12 19:19] PMS sdk 적용<br>
 * [2013.12.16 10:39] PMS sdk 적용<br>
 * [2013.12.18 16:19] PMS sdk 적용<br>
 * [2013.12.19 11:02] PMS sdk 적용<br>
 * [2013.12.27 15:08] PMS sdk 적용<br>
 */
public class TMSApi {

    /**
     * 로그 태그
     */
    private static final String TAG = "[" + TMSApi.class.getName() + "]";

    /**
     * mContext
     */
    private final Context mContext;

    /**
     * KEY_AUTO_FINISH
     */
    public static final String KEY_AUTO_FINISH = "auto_finish";

    /**
     * NEW_MSG_PAGE_ROW
     */
    public static final String NEW_MSG_PAGE_ROW = "50";

    /**
     * KEY_APP_LINK
     */
    public static final String KEY_APP_LINK = "l";

    /**
     * PMSApi
     * @param context context
     */
    public TMSApi(Context context) {
        this.mContext = context;
        Ln.i("==================build date 201312271508======================");
    }

    /**
     * device cert
     * @param apiCallback APICallback
     */
    public void deviceCert(final DeviceCert.Callback apiCallback) {
        new DeviceCert(mContext).request(apiCallback);
    }

    /**
     * login pms
     *
     * @param custId 고객번호
     * @param callback 고객번호
     */
    public void loginTms(final String custId, final Login.Callback callback) {

        new Login(mContext).request(custId, new Login.Callback() {

            @Override
            public void response(APIResult apiResult, String notiFlag, String mktFlag) {
                newMsg((NewMsg.Callback) null);
            }
        });
    }

    /**
     * newMsg
     * callback에 readmsg할 대상이 존재하면 readmsg수행 후 callback수행
     * @param apiCallback apiCallback
     */
    public void newMsg(final NewMsg.Callback apiCallback) {
        new NewMsg(mContext).request((NewMsg.Callback) (apiResult, msgList) -> {
            // 뉴 메세지 후에는 readMsg하지 않는다. 기존에 가져온거 모두 리드 처리하게끔 했지만 문제 생겨 삭제 210726 hklim
            // callback
            if (apiCallback != null) {
                apiCallback.response(apiResult, msgList);
            }
        });
    }

    /**
     * logoutPms
     */
    public void logoutTms() {
        new Logout(mContext).request((apiResult, notiFlag, mktFlag) -> {
        });
    }
}
