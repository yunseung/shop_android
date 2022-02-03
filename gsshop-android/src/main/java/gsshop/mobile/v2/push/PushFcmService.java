package gsshop.mobile.v2.push;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.appboy.Appboy;
import com.appboy.AppboyFcmReceiver;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tms.sdk.push.PushReceiver;

import org.json.JSONObject;

import java.util.Map;

import gsshop.mobile.v2.AbstractTMSService;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.util.PrefRepositoryNamed;
import roboguice.util.Ln;

/**
 * FCM 수신 받는 Service, 보통 pushReceiver로도 PMS 수신이 되지만 receiver로 수신 안되는 경우를 대비하여
 * PMS 쪽으로 메세지 발신, 기존 Callgate쪽 CallgateGcmListenerService 삭제.
 */
public class PushFcmService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Ln.i("FCM OnMessageReceived From : " + remoteMessage.getFrom());

        // Callgate와 CleverTap에서 GCM 떄 받던 데이터 형식으로 받으려고 굳이 아래와 같은 형식으로 달라고 한다.
        Bundle extras = new Bundle();
        for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
            extras.putString(entry.getKey(), entry.getValue());
        }

        /**
         * 푸시 메세지는 해당 서비스에서만 받게끔 수정, 해당 service에서 분배 해 줄 때에
         * 푸시 세팅 확인하여 거절 일 경우 각 푸시 SDK로 전달 하지 않는다.
         */
        PushSettings pushSettings = PushSettings.get();
        if(!pushSettings.approve) {
            Ln.i("FCM OnMessageReceived. Push setting denied.");
            return;
        }

        /**
         * PMS 쪽 처리를 위한 구문
         */
        try {
            Intent broadcastIntent = new Intent(this.getApplicationContext(), PushReceiver.class);
            broadcastIntent.setAction("com.google.android.c2dm.intent.RECEIVE");
            broadcastIntent.addCategory(this.getApplication().getPackageName());
            broadcastIntent.putExtra("org.mosquitto.android.mqtt.MSG", (new JSONObject(remoteMessage.getData())).toString());
            broadcastIntent.putExtra("message_id", remoteMessage.getMessageId());
            this.sendBroadcast(broadcastIntent);
        } catch (Exception e) {
            Ln.e(e.getMessage());
        }

        /**
         * AppBoy 처리 구문
         */
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent AppBoyIntent = new Intent(getApplicationContext(), AppboyFcmReceiver.class);
            AppBoyIntent.setAction("com.google.android.c2dm.intent.RECEIVE");
            AppBoyIntent.putExtras(extras);

            this.sendBroadcast(AppBoyIntent);
        }

    }

    @Override
    public void onMessageSent(String msgId) {
        Ln.i("onMessageSent() " + msgId);
        super.onMessageSent(msgId);
    }

    @Override
    public void onSendError(String s, Exception e) {
        Ln.i("onSendError() " + s + ", " + e.toString());
        super.onSendError(s, e);
    }

    @Override
    public void onNewToken(final String token) {
        super.onNewToken(token);
        Ln.i("onNewToken : " + token);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //토큰 로컬에 저장
                    PrefRepositoryNamed.save(MainApplication.getAppContext(), Keys.CACHE.TOKEN, Keys.CACHE.TOKEN, token);

                    //PMS 신규토큰으로 초기화 수행
                    AbstractTMSService.init(MainApplication.getAppContext());

                    //appboy
                    if (!"true".equals(MainApplication.getAppContext().getString(R.string.skip_appboy))) {
                        Appboy.getInstance(MainApplication.getAppContext()).registerAppboyPushMessages(token);
                    }

                } catch(Exception e) {
                    Ln.e(e.toString());
                }
            }
        }).start();

    }
}
