package gsshop.mobile.v2.tms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.tms.sdk.ITMSConsts;
import com.tms.sdk.api.request.ReadMsg;
import com.tms.sdk.bean.PushMsg;

import org.json.JSONObject;

import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isEmpty;

/**
 * Notification Bar에 수신된 푸시를 클릭할 경우 호출되는 리시버
 */
public class PushNotiReceiver extends BroadcastReceiver implements ITMSConsts {

	/**
	 * 로그 태그
	 */
	private static final String TAG = "[" + PushNotiReceiver.class.getName() + "]";

	/**
	 * onReceive
	 *
	 * @param context context
	 * @param intent intent
     */
	@Override
	public void onReceive(Context context, Intent intent) {

		if (isEmpty(intent) || isEmpty(intent.getExtras())) {
			return;
		}

		PushMsg pushMsg = new PushMsg(intent.getExtras());
		
		Ln.i(TAG + pushMsg);
		
		String appLink;
		try {
			appLink = new JSONObject(pushMsg.data).getString(TMSApi.KEY_APP_LINK);
		} catch (Exception e) {
			// 10/19 품질팀 요청
			// - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
			// - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
			// - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
			Ln.e(e);
			appLink = "";
		}

		//노티바 클릭시 즉시 읽음처리 수행

		new ReadMsg(context).request(pushMsg.msgId, apiResult -> {
			// callback 이 null이면 sdk 내부에서 request 안날리는 문제 있어 수정
//			Ln.d("hklim apiResult , code : " + apiResult.getCode() + " / message : " + apiResult.getMsg());
		});

		try {
			Ln.d(TAG + "appLink:" + appLink);
			Intent httpIntent = new Intent(Intent.ACTION_VIEW);
			httpIntent.setData(Uri.parse(appLink));
			httpIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			TenseraApi.preloader().onUserEnter("PushNotiReceiver");
			context.startActivity(httpIntent);

		} catch (Exception e) {
			// 10/19 품질팀 요청
			// - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
			// - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
			// - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
			Ln.e(e);
		}
	}
}
