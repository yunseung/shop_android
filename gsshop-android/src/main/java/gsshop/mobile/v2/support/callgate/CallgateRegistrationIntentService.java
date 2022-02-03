package gsshop.mobile.v2.support.callgate;

/* calltgate 삭제
import android.app.IntentService;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;

import com.callgate.launcher.CallgateConstants;
import com.callgate.launcher.LauncherLinker;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.util.PrefRepositoryNamed;
import roboguice.util.Ln;
*/

/**
 * Visual ARS 런처의 초기화 작업 수행시 단말 이용자를 찾을 수 없는 경우와  onTokenRefresh가 수행되는 두가지 경우에 대해
 * FCM Token 생성 후 Callgate 서버에 이용자 등록을 수행한다.
 * 위에서 언급한 두가지 경우는 앱 설치 후 최초 실행하는 경우와 보안등의 이유로 FCM 모듈이 Token 재발급이 필요함을 Broadcast 하는 경우다.
 */
public class CallgateRegistrationIntentService { /*extends IntentService {*/
// calltgate 삭제
//	private static final String TAG = "CallgateRegistrationIntentService";
//
//	private LauncherLinker launcherLinker;
//
//	public CallgateRegistrationIntentService() {
//		super("CallgateRegistrationIntentService");
//	}
//
//	public CallgateRegistrationIntentService(String name) {
//		super(name);
//	}
//
//	@Override
//	protected void onHandleIntent(Intent intent) {
//		if (intent == null)
//			return;
////		InstanceID instanceID = InstanceID.getInstance(this);
//		String token = null;
//		//콜게이트로 부터 발급받은 아이디 추출 "[GSSHOP]"
//		String launcherId = getString(R.string.visual_ars_launcher_id);
//		//Sender ID 추출
//		String senderId = getString(R.string.mc_push_sender_id);
//
//		token = PrefRepositoryNamed.get(getApplicationContext(), Keys.CACHE.TOKEN, Keys.CACHE.TOKEN, String.class);
//
//		if (token == null) {
//			try {
//				token = FirebaseInstanceId.getInstance().getToken(senderId, "FCM");
//			} catch (IOException e) {
//				Ln.e(e.toString());
//			}
//		}
//
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//			if (intent.getAction().equals(CallgateConstants.CALLGATE_ACTION_PUSH_REGISTRATION)) {
//				Ln.i("CallGate : CALLGATE_ACTION_PUSH_REGISTRATION : " + token);
//				/* Application 실행 시점 * 만약 getToken()을 하지 않았다면,
//				 * SharedPreferences 등에 저장된 token을 broadcast하여 MainActivity에 전달한다.
//				 */
//				//Start Callgate Logic
//
//				Intent broadcast_intent = new Intent(CallgateConstants.CALLGATE_ACTION_BROADCAST_TOKEN);
//				broadcast_intent.putExtra("token", token);
//				broadcast_intent.putExtra("package", getPackageName());
//				sendBroadcast(broadcast_intent);
//			} else if (intent.getAction().equals(CallgateConstants.CALLGATE_ACTION_PUSH_UPDATE)) {
//				Ln.i("CallGate : CALLGATE_ACTION_PUSH_UPDATE : " + token);
//				// 콜게이트 Service 제공 시점
//				LauncherLinker launcherLinker = new LauncherLinker(getApplicationContext());
//
//				/* LauncherLinker.checkUserInformation()
//				 * 1st param : Customer Launcher ID (사전에 콜게이트가 제공)
//				 * 2nd param : Push Token
//				 */
//				launcherLinker.checkUserInformation(launcherId, token);
//			}
//		}
//
//		//토큰 유효성 체크
//		if (TextUtils.isEmpty(token)) {
//			return;
//		}
//
//		//토큰 로컬에 저장
//		PrefRepositoryNamed.save(this, Keys.CACHE.TOKEN, Keys.CACHE.TOKEN, token);
//	}
}
