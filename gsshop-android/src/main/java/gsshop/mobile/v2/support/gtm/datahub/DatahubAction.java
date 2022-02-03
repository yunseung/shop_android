package gsshop.mobile.v2.support.gtm.datahub;

import android.content.Context;
import android.text.TextUtils;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.user.User;
import gsshop.mobile.v2.util.CookieUtils;
import roboguice.util.Ln;

/**
 * Datahub와 통신을 위한 클래스
 *
 */
public class DatahubAction {

	//Datahub에 url 전송시 인코딩 타입 정의
	public static final String uriEncoding = "utf-8";
	
	//ReferrerReceiver, SchemeGateway에서 media값이 존재할 경우 아래 변수에 세팅한다.
	public static String MEDIA_TYPE = "";

    /**
     * DataHub 로깅을 위해 url을 호출한다.
     * 
     * @param context 컨텍스트
     * @param url DataHub url
     * @param param 파라미터
     */
    public static void sendDataToDatahub(Context context, String url, String param) {
		if (TextUtils.isEmpty(url)) {
			return;
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				String fullUrl = "";
				//pcid 쿠키값
				String pcid = "";
				NameValuePair pcidPair = CookieUtils.getWebviewCookie(context, "pcid");
				if (pcidPair != null) {
					pcid = pcidPair.getValue();
				}
				//고객번호
				String catvid = User.getCachedUser() != null ? User.getCachedUser().customerNumber : "";
				//고정값
				String domain = "m.gsshop.com";
				//고정값
				String service_type = "MC";
				//mediatype값은 install-referrer 또는 SchemeGateway에서 추출한다.
				//위 두곳에서 추출된 값이 있으면 사용하고, 없으면 ecid쿠키값을 사용한다.
				String mediatype = TextUtils.isEmpty(MEDIA_TYPE) ?
						CookieUtils.getWebviewCookieFromEcid(context, Keys.COOKIE.MEDIA_TYPE) : MEDIA_TYPE;
				//고정값
				String appmediatype = "BT";

				try {
					//URL에 GET 파라미터 추가
					fullUrl = String.format(url + "?pcid=%s&catvid=%s&domain=%s&service_type=%s&mediatype=%s&appmediatype=%s&%s",
							pcid, catvid, domain, service_type, mediatype, appmediatype, param);

					//URL 호출 (서버에서 2번 호출되는 현상 발견됨)
					//HttpUtils.getContent(DatahubUrls.WRAPPER_URL + fullUrl, 5000, 5000);

					//서버에서 2번 호출되는 현상때문에 HttpClient로 교체
					HttpClient http = new DefaultHttpClient();
					HttpParams params = http.getParams();
					HttpConnectionParams.setConnectionTimeout(params, 5000);
					HttpConnectionParams.setSoTimeout(params, 5000);
					HttpGet httpGet = new HttpGet(DatahubUrls.WRAPPER_URL + fullUrl);
					httpGet.setHeader("User-Agent", System.getProperty("http.agent") +
							context.getString(R.string.mc_rest_user_agent_additional));
					http.execute(httpGet);
					//Ln.i("[sendDataToDatahub] " + DatahubUrls.WRAPPER_URL + fullUrl);
				} catch (Exception e) {
					//데이타허브 서버에서 존재하는 URL에 대해서도 항상 404를  리턴하기때문에 FileNotFoundException 발생함 (이슈없음)
					// 10/19 품질팀 요청
					// - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
					// - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
					// - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
					Ln.e(e);
				}
			}
		}).start();
	}
}
