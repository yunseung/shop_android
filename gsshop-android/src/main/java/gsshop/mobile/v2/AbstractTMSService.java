/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.appboy.Appboy;
import com.appboy.enums.NotificationSubscriptionType;
import com.tms.sdk.ITMSConsts;
import com.tms.sdk.TMS;
import com.tms.sdk.api.request.DeviceCert;
import com.tms.sdk.api.request.Login;
import com.tms.sdk.api.request.Logout;
import com.tms.sdk.api.request.SetConfig;
import com.tms.sdk.bean.APIResult;
import com.tms.sdk.common.util.NotificationConfig;

import gsshop.mobile.v2.push.PushSettings;
import gsshop.mobile.v2.tms.InboxActivity;
import gsshop.mobile.v2.tms.PushNotiReceiver;
import gsshop.mobile.v2.tms.TMSApi;
import gsshop.mobile.v2.util.CookieUtils;
import gsshop.mobile.v2.util.DeviceUtils;
import gsshop.mobile.v2.util.LogUtils;
import gsshop.mobile.v2.util.PrefRepositoryNamed;
import roboguice.util.Ln;

/**
 * PMS 연동
 *
 */
public abstract class AbstractTMSService {

    /**
     * tmsApi 접근자
     */
    private static TMSApi tmsApi = null;

    /**
     * pms 엔진 접근자
     */
    private static TMS tms = null;

    /**
     * mContext
     */
    private static Context mContext = null;

    /**
     * 인앱 메세지 이벤트 등록
     */
    public static class EventInAppMessage {
        /**
         * 메인 탭 이동 및 다른 탭 이동 시 이벤트
         */
        public static String EVENT_MAIN_TAB = "2000";

        /**
         * 인앱 메세지 이벤트 호출 딜레이 시간
         */
        public static int EVNET_MAIN_TAB_DELAY = 2000;
    }

    /**
     * 고객 번호 셋팅
     * @param custid custid
     */
    public static void setCustID(String custid) {
        try {
            if (tms == null)
            {
                if (mContext == null) {
                    mContext = MainApplication.getAppContext();
                }
                tms = TMS.getInstance(mContext);
            }
            tms.setCustId(custid);
        } catch (Exception e) {
            // NOTE : handle exception
            // 10/19 품질팀 요청
            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
            // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
            Ln.d(e);
        }
    }

    /**
     * 초기호 로직
     * @param context context
     */
    public static synchronized void init(Context context) {
        long startTime = System.currentTimeMillis();

        try {
            mContext = context;
            tmsApi = new TMSApi(mContext);

            //wa_wapcid 쿠키값 취득
            String wapcId = CookieUtils.getWaPcId(context);
            //pcid 쿠키값 취득
            String pcId = CookieUtils.getPcId(context);

            // GCM 설정 및 Push 설정 getInstance 2번째 파라미터는 프로젝트 ID 로 추후 신규 할당 필요
            tms = TMS.getInstance(mContext);

            tms.setDeviceId(DeviceUtils.getUuid(mContext));
            tms.setGoogleID(DeviceUtils.getAdvertisingId());
            tms.setWapcId(wapcId == null ? "":wapcId);
            tms.setPCID(pcId == null ? "":pcId);
            String token = PrefRepositoryNamed.get(mContext, Keys.CACHE.TOKEN, Keys.CACHE.TOKEN, String.class);

            if (token != null) {
                tms.setPushToken(token);
            }

            TMS.Builder tmsBuilder = new TMS.Builder();
            tmsBuilder.setFirebaseSenderId("633890543958");
            tmsBuilder.setServerAppKey("582f837031d546f1a101");
//            tmsBuilder.setServerUrl("https://tmsdev.gsshop.com/msg-api/");
            tmsBuilder.setServerUrl("https://tms31.gsshop.com/msg-api/");
            tmsBuilder.setDebugTag("GSSHOP");
            // 디버그 로그 실제 배포시에는 안나가게끔 수정.
            tmsBuilder.setDebugEnabled(false);
            tmsBuilder.setNotificationConfig(
                    new NotificationConfig.Builder().
                            setLargeIcon(ITMSConsts.NOTIFICATION_NO_LARGE_ICON).        // 푸시 메세지 우측에 큰 아이콘 설정 (없음)
                            setClickListener(Keys.ACTION.NOTIFICATION, PushNotiReceiver.class).  // 수신한 푸시 선택시 이동할 액션 및 클래스 선언
                            setShowPopupActivity(false).                                //푸시 수신시 팝업 액티비티 표시 여부
                            setShowPopupActivityWhenForegroundStateOnly(true).          //true : 다른 앱 실행시 노출 안시킴
                            create()
            );
            tmsBuilder.build(mContext);

//            PMSUtil.setNotificationChannel(mContext, mContext.getString(R.string.NOTIFICATION_CHANNEL_ID),
//                    mContext.getString(R.string.app_name));

            try {
                //위 PMSUtil.setGCMToken에서 GCMToken이 정상적으로 세팅되지 않으면, deviceCert는 2초간격으로 12회 반복함
                //반복 수행 중 GCMToken이 정상적으로 세팅되면 deviceCert도 정상 수행되고 반복수행을 종료함
                tmsApi.deviceCert(apiResult -> {
//                    Ln.d("hklim deviceCert result : " + apiResult.toString());
                });
            } catch (Exception e) {
                // 10/19 품질팀 요청
                // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
                // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
                // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
                Ln.e(e);
            }
        } catch (Exception e) {
            // 10/19 품질팀 요청
            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
            // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
            Ln.e(e);
        }

        LogUtils.printExeTime("AbstractTMSService.init", startTime);
    }

    public static void cert() {
        tmsApi.deviceCert(new DeviceCert.Callback() {
            @Override
            public void response(APIResult apiResult) {
            }
        });
    }

    /**
     * loginTMS
     *
     * @param context context
     * @param loginID 고객번호
     */
    public static void loginPMS(Context context, String loginID) {
        loginPMS(context, loginID, null);
    }
    public static void loginPMS(Context context, String loginID, Login.Callback callback) {
        try {
            if (context == null || loginID == null)
            {
                return;
            }
            if (tmsApi == null)
            {
                tmsApi = new TMSApi(mContext);
            }
            try {
                //로그인 완료시 deviceId, waPcid값을 세팅한다.
                String wapcId = CookieUtils.getWaPcId(context);	//wa_pcid 쿠키값 취득
                String pcId = CookieUtils.getPcId(context);	//pcid 쿠키값 취득
                tms = TMS.getInstance(mContext);
                tms.setDeviceId(DeviceUtils.getUuid(mContext));
                tms.setWapcId(wapcId == null ? "":wapcId);
                tms.setPCID(pcId == null ? "":pcId);
                tmsApi.loginTms(loginID, callback);
            } catch (Exception e) {
                // 10/19 품질팀 요청
                // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
                // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
                // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
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

    /**
     * 로그아웃 인터 페이스
     * @param context context
     */
    public static void logoutPMS(Context context) {
        try {
            if (context == null)
            {
                return;
            }
            mContext = context;
            new Logout(mContext).request((apiResult, s, s1) -> {
            });
        } catch (Exception e) {
            // 10/19 품질팀 요청
            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
            // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
            Ln.e(e);
        }
    }

    /**
     * showMessageBox
     *
     * @param context context
     */
    public static void showMessageBox(Context context) {
        try {
            //클리어탑
            Intent intent = new Intent(context, InboxActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ((Activity)context).startActivity(intent);
        } catch (Exception e) {
            // 10/19 품질팀 요청
            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
            // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
            Ln.e(e);
        }
    }

    /**
     * 앱 종료시 Pms Clear
     * TODO: 2016. 10. 21. MSLEE
     * 지금은 시간은 없지만 사용하지 않고 있다 하지 않았을때의 리스크를 확인하자
     */
    public static void pmsClear() {
        try {
            if (tms != null)
            {
                TMS.clear();
            }
        } catch (Exception e) {
            // 10/19 품질팀 요청
            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
            // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
            Ln.e(e);
        }
    }

    /**
     * pms push 설정 상태 저장
     * 알림 메세지 수신 여부 설정 API
     * ex)  new SetConfig(this).request("Y", "N", null);
     * @param context context
     * @param model model
     */
    public static void pushSettings(Context context, PushSettings model) {
        pushSettings(context, model, null);
    }

    public static void pushSettings(Context context, PushSettings model, SetConfig.Callback callback) {
        try {
            if (model != null) {
                TMS.Builder builder = new TMS.Builder();
                NotificationConfig.Builder config = new NotificationConfig.Builder();

                if (builder != null && config != null) {
                    config.setShowPopupActivity(model.showPopup);
                    config.setWakeLockScreen(model.screenOn);
                    config.setRing(model.sound);
                    config.setVibrate(model.vibration);
                    builder.setNotificationConfig(config.create());
                }

                String strApprove = model.approve ? "Y" : "N";
//                String strApproveAd = model.approveAd == "Y" ? "Y" : "N";

                // 설정 기본 과 광고를 모두 하나로 설정
                new SetConfig(context).request(strApprove, strApprove, callback);
            }
        } catch (Exception e) {
            // 10/19 품질팀 요청
            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
            // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
            Ln.d(e);
        }
    }

    public static void pushSettingAppboy(boolean approve) {
        if (approve){
            //appboy 수신허용
            if (!"true".equals(mContext.getString(R.string.skip_appboy))) {
                Appboy.getInstance(mContext).getCurrentUser().setPushNotificationSubscriptionType(NotificationSubscriptionType.SUBSCRIBED);
                Appboy.getInstance(mContext).requestImmediateDataFlush();
            }
        }
        else{
            //appboy 수신거부
            if (!"true".equals(mContext.getString(R.string.skip_appboy))) {
                Appboy.getInstance(mContext).getCurrentUser().setPushNotificationSubscriptionType(NotificationSubscriptionType.UNSUBSCRIBED);
                Appboy.getInstance(mContext).requestImmediateDataFlush();
            }

//                    new SetConfig(context).request("Y", "N", (SetConfig.Callback) null);
        }
    }
}
