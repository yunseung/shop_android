/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.push;

import android.content.Context;
import android.text.TextUtils;

import com.amplitude.api.Amplitude;
import com.amplitude.api.Identify;
import com.appboy.Appboy;
import com.google.inject.Inject;

import org.apache.http.NameValuePair;

import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.util.CookieUtils;
import gsshop.mobile.v2.util.DeviceUtils;
import roboguice.inject.ContextSingleton;
import roboguice.inject.InjectResource;
import roboguice.util.Ln;

/**
 * PUSH알림 관련 주요 작업 처리.
 */
@ContextSingleton
public class PushAction {
	private static final String GSF_PACKAGE = "com.google.android.gsf";
	
    @InjectResource(R.string.mc_push_sender_id)
    private String senderId;

    @Inject
    protected PushSenderConnector senderConnector;

    private final Context context;

    @Inject
    public PushAction(Context context) {
        this.context = context;
    }

    /**
     * PUSH 설정 및 서버단 갱신은 즉각적으로 수행할 필요가
     * 없기 때문에 Async로 처리.
     *
     * @param event event
     */
    public void onEventAsync(Events.LoggedInEvent event) {
        update(event.user.customerNumber);
    }

    /**
     * 설정에 저장된 고객번호가 없거나 주어진 고객번호와 다르면
     * 설정 및 서버에 저장.
     *
     * NOTE : 백그라운드에서 실행해야 함.
     *
     * @param customerNumber customerNumber
     */
    public void update(String customerNumber) {
        if (TextUtils.isEmpty(customerNumber)) {
            return;
        }

        PushSettings settings = PushSettings.get();
        String savedCustomerNo = settings.customerNumber;

        if (customerNumber.equals(savedCustomerNo)) {
            //return;
        }

        // 고객번호를 새로 설정에 저장.
        settings.customerNumber = customerNumber;
        settings.save();

        // 푸시서버에 디바이스 정보 전달하여 업데이트
        try {
        	senderConnector.registerOnServer();

            try {
                //appboy
                //Custom Attributes 세팅
                if (!TextUtils.isEmpty(customerNumber)) {
                    if (!"true".equals(context.getString(R.string.skip_appboy))) {
                        Appboy.getInstance(context).getCurrentUser().setCustomUserAttribute("catvid", customerNumber);
                    }
                }
                String gsuuid = DeviceUtils.getGsuuid(context);
                if (!TextUtils.isEmpty(gsuuid)) {
                    if (!"true".equals(context.getString(R.string.skip_appboy))) {
                        Appboy.getInstance(context).getCurrentUser().setCustomUserAttribute("token", gsuuid);
                    }
                }

                String gender ="";
                NameValuePair gdPair = CookieUtils.getWebviewCookie(context, "gd");   //성별
                if (gdPair != null) {
                    gender = gdPair.getValue();
                    if (!TextUtils.isEmpty(gender)) {
                        if (!"true".equals(context.getString(R.string.skip_appboy))) {
                            Appboy.getInstance(context).getCurrentUser().setCustomUserAttribute("gd", gender);
                        }
                    }
                }

                String age = "";
                NameValuePair ydPair = CookieUtils.getWebviewCookie(context, "yd");   //연령대
                if (ydPair != null) {
                    age = ydPair.getValue();
                    if (!TextUtils.isEmpty(age)) {
                        if (!"true".equals(context.getString(R.string.skip_appboy))) {
                            Appboy.getInstance(context).getCurrentUser().setCustomUserAttribute("yd", age);
                        }
                    }
                }

                if (!"true".equals(context.getString(R.string.skip_appboy))) {
                    Appboy.getInstance(context).changeUser(customerNumber);
                }

                /**
                 * Amplitude 고객정보 처리 부분 실제 로그인 이벤트는 아니다
                 *
                 */
                if (!TextUtils.isEmpty(customerNumber)) {
                    //Amp 고객번호 세팅 (AMP상의  UserID)
                    Amplitude.getInstance().setUserId(customerNumber);

                    //폰트 크기
                    String scaleString;
                    //성별/나이/폰트크기 세팅
                    try {
                        float scale = context.getResources().getConfiguration().fontScale;
                        if(scale < 0.5 || scale > 1.6)
                        {
                            scaleString = "알수없음";
                        }
                        else
                        {
                            scaleString = Float.toString(scale);
                        }
                    }
                    catch (Exception e)
                    {
                        //죽지만 말아라
                        scaleString = "알수없음";
                    }
                    //성별/나이/폰트 세팅
                    Identify identify = new Identify().set("gd", gender).set("yd", age).set("fs",scaleString);
                    Amplitude.getInstance().identify(identify);
                }
            } catch(Exception e) {
                Ln.e(e);
            }
		} catch (Exception e) {
            // 10/19 품질팀 요청
            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
            // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
            Ln.e(e);
		}
    }

}
