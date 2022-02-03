/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.push;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.text.TextUtils;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.gsshop.mocha.device.AppInfo;
import com.gsshop.mocha.network.rest.Rest;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.network.rest.RestPost;
import com.gsshop.mocha.pattern.mvc.Model;
import com.gsshop.mocha.service.push.SenderConnector;

import org.apache.http.NameValuePair;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.annotation.ParametersAreNullableByDefault;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.setting.AutoPlaySettings;
import gsshop.mobile.v2.util.CookieUtils;
import gsshop.mobile.v2.util.DeviceUtils;
import gsshop.mobile.v2.util.PrefRepositoryNamed;
import gsshop.mobile.v2.util.RestClientUtils;
import roboguice.util.Ln;

/**
 * PUSH 관련 서버 연동.
 *
 * NOTE : 사용자 관련 정보가 전송되므로 HTTPS를 적용할 것.
 */
@Singleton
@Rest(resId = R.string.server_http_root)
public class PushSenderConnector implements SenderConnector {

	private static final String TAG = "PushSenderConnector";
	
	// gcm = 01, c2dm = 02, iphone = 03
	private static final String PUSH_TYPE = "01";
	
    @Inject
    private PackageInfo packageInfo;

    @Inject
    private Context context;

    @Inject
    private RestClient restClient;

    /**
     * GCM 등록 후 onRegistered 콜백 발생시 호출되는 함수
     * urbanairship(gcm3.0) 적용하면서 onRegistered가 3회 호출되어 본 기능을 별도로 처리하기로 하여 함수 내에 수행로직 제거
     * 
     * @param registrationId GCM 등록키
     */
    @Override
    public void registerOnServer(String registrationId) {

    }

    /**
     * /push/register API에 디바이스 정보 전송
     * urbanairship(gcm3.0) 적용하면서 registerOnServer 대신 본 함수를 사용함
     * 
     */
    public void registerOnServer() {
        PushRegisterParam param = new PushRegisterParam();
        
        //wa_pcid 쿠키값 취득
        String waPcId = CookieUtils.getWaPcId(context);
        if (waPcId == null) {
            waPcId = "";
        }

        //pcid 쿠키값 취득
        String pcId = CookieUtils.getPcId(context);
        if (pcId == null) {
            pcId = "";
        }

        //Advertising ID 취득
        String aid;
        aid = DeviceUtils.getAdvertisingId();
        if(aid == null)
        {
        	aid = "";
        }
        //ad_media 쿠키값 취득
        String admedia = "";
        NameValuePair admediaPair = CookieUtils.getWebviewCookie(context, Keys.COOKIE.AD_MEDIA);
        if (admediaPair != null) {
        	admedia = admediaPair.getValue();
        }
        
        //referrer 값 취득
        //String utm = PrefRepositoryNamed.get(Keys.CACHE.REFERRER, String.class);
        String utm = PrefRepositoryNamed.get(context, Keys.CACHE.REFERRER, Keys.CACHE.REFERRER, String.class);
        if (utm == null) {
        	utm = "";
        }

        AutoPlaySettings model = AutoPlaySettings.get();
        
        //getGcmToken으로 추출되는 데이타 형태는 "fvyiZtxIiCQ:APA91bFcULQaI9N9SRczW5Mtre_hvq8Kr..."
        //서버로 deviceToken을 전송하여 테이블에 저장은 하지만, TMS서버에 저장된 deviceToken과 데이타 싱크가 맞지 않는 부분이 많아 사용하지는 않음
        String deviceToken = PrefRepositoryNamed.get(context, Keys.CACHE.TOKEN, Keys.CACHE.TOKEN, String.class);
        param.deviceToken = TextUtils.isEmpty(deviceToken) ? "" : deviceToken;

        String gsuuid = DeviceUtils.getGsuuid(context);
        //10/19 품질팀 요청
        if(gsuuid == null){
            param.gsuuid = "";
        }else{
            param.gsuuid = gsuuid;
        }
        param.deviceId = param.gsuuid; //hasedalmsdeviceid1234567890
        param.customerNumber = PushSettings.get().customerNumber;
        param.appVersion = AppInfo.getAppVersionName(packageInfo);
        param.type = PUSH_TYPE;
        param.aid = aid;
        param.admedia = admedia;
        param.utm = utm;
        param.videoAutoPlayYn = model.approve?"Y":"N";
        param.waPcid = waPcId;
        param.pcid = pcId;

        //        Ln.i(param.deviceToken +" / "+
        //                param.deviceId +
        //                " / "+ param.customerNumber +
        //                " / "+ param.appVersion);

        RestClientUtils.INSTANCE.post(restClient, param, ServerUrls.REST.PUSH_REGISTER, String.class);

        Ln.i("단말기 정보가 모바일 서버에 저장됨");
    }
    
    @Override
    public void unregisterOnServer(String registrationId) {
        // 기능 필요없음.
        throw new UnsupportedOperationException();
    }

    /**
     * PUSH 전송을 위한 사용자/단말기 정보를 서버에 저장.
     *
     * @param param param
     */
    @RestPost(value = ServerUrls.REST.PUSH_REGISTER)
    private void savePushInfo(PushRegisterParam param) {
    }

    /**
     * 서버단에 저장할 PUSH 관련 정보.
     *
     */
    @Model
    @JsonIgnoreProperties(ignoreUnknown = true)
    @ParametersAreNullableByDefault
    class PushRegisterParam {

        public String deviceToken;

        public String deviceId;

        public String gsuuid;

        public String customerNumber;

        public String type;// gcm = 01, c2dm = 02, iphone = 03

        public String appVersion;
        
        public String aid;	//광고 ID, 200byte 이하
        
        public String admedia;	//ad_media 쿠키값, 200byte 이하
        
        public String utm;	//앱 설치시 전달받는 referrer값, 200byte 이하

        public String videoAutoPlayYn;

        public String waPcid;

        public String pcid;
    }

}
