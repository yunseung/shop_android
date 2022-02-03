package gsshop.mobile.v2.support.gtm;

import android.text.TextUtils;

import com.amplitude.api.Amplitude;
import com.appboy.Appboy;
import com.appboy.models.outgoing.AppboyProperties;

import org.json.JSONException;
import org.json.JSONObject;

import gsshop.mobile.v2.MainApplication;
import roboguice.util.Ln;

//import com.clevertap.android.sdk.CleverTapAPI;

/**
 * Created by cools on 2017. 7. 21..
 * Amp 태킹 액션
 */

public class AMPAction {

    /**
     * Amp 세션 트래킹 여부 확인
     * @param flag
     */
    public static void ampTrackSessionEvents(boolean flag) {
        //세션 이벤트 트래킹을 위해 활성화
        Amplitude.getInstance().trackSessionEvents(flag);
    }

    /**
     * 커스톰 이벤트 액션 : Tracking Event
     * 앰플리튜
     *
     * @param eventName
     * @param obj
     */
    public static void sendAmpEventProperties(String eventName,JSONObject obj) {

        Ln.i("############### AMP sendAmpEvent eventName : " +eventName);
        // event
        if (!TextUtils.isEmpty(eventName)) {
            Amplitude.getInstance().logEvent(eventName,obj);
        }
    }

    /**
     * AMP 프로퍼티 없는
     *
     * @param eventName
     */
    public static void sendAmpEvent(String eventName) {
        // event
        if (!TextUtils.isEmpty(eventName)) {
            Amplitude.getInstance().logEvent(eventName);

            //앰플리튜드와 브레이즈 기능 연계
            Ln.d("############### braze sendAmpEvent before : " + eventName );

            //예외 대상이면 앱 보이에 던지지 않는다.
            if( !AMPEnum.appBoyExceptionEvent(eventName) ) {
                Ln.d("############### braze sendAmpEvent after : " + eventName );
                Appboy.getInstance(MainApplication.getAppContext()).logCustomEvent(eventName);
            }
        }
    }

    /**
     * 샘플
     * 서버쪽 로직
     * var item = '[{"itemName":"15341797", "unitPrice":"29000", "quantity":"2", "revenue":"58000"},{"itemName":"15346155", "unitPrice":"40800", "quantity":"1", "revenue":"40800"}]';
     *
     * @JavascriptInterface
     * public void sendAMPEventProperties(String eventName, String eventProperties)
     * 해당 되는 놈을 처리 한다
     *
     * @param eventName
     * @param eventProperties
     */
    public static void sendAmpEvent(String eventName, String eventProperties)
    {

        JSONObject jsonObject = null;
        if(eventName != null && eventProperties != null)
        {
            Ln.i("############### AMP sendEvent eventName eventProperties : " + eventName + ":: "+ eventProperties);
            try {
                jsonObject = new JSONObject(eventProperties);
            } catch (JSONException e) {
                jsonObject = null;
                Ln.e(e);
            }
        }
        //eventName 정상적이지 않을 경우에는 jsonObject
        if(jsonObject != null)
        {
            //// TODO: 2017. 8. 9. 정상적이지 못한 Json 판단은?? 가능한가
            try {
                Ln.i("############### AMP sendEvent json : " + jsonObject.toString() );
                //Amplitude.getInstance().logEvent(eventName,jsonObject);
                sendAmpEventProperties(eventName,jsonObject);


                //앰플리튜드와 브레이즈 기능 연계
                Ln.d("############### braze sendAmpEventProperties before : " + eventName );
                //예외 대상이면 앱 보이에 던지지 않는다.
                if( !AMPEnum.appBoyExceptionEvent(eventName) ) {
                    Ln.d("############### braze sendAmpEventProperties after : " + eventName );
                    AppboyProperties eventProperties_app_boy = new AppboyProperties(jsonObject);
                    Appboy.getInstance(MainApplication.getAppContext()).logCustomEvent(eventName,eventProperties_app_boy);
                }

                //옵티 무브 이벤트 변환 Map 변환 되나?
                //json to map
                /*
                try {
                    Map<String, Object> map = new HashMap<String, Object>();
                    ObjectMapper mapper = new ObjectMapper();
                    map = mapper.readValue(eventProperties, new TypeReference<HashMap<String, Object>>() {});

                    Optimove.getInstance().reportEvent(eventName, map);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                */

            }catch (Exception e) {
                Ln.e(e);
            }
        }
    }
}
