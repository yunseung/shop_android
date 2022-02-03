/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.appboy.Appboy;
import com.appboy.models.outgoing.AppboyProperties;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.gsshop.mocha.device.NetworkStatus;

import org.json.JSONObject;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.AbstractTMSService;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.HomeGroupInfoAction;
import gsshop.mobile.v2.push.PushSettings;
import gsshop.mobile.v2.support.gtm.AMPAction;
import gsshop.mobile.v2.support.gtm.GTMAction;
import gsshop.mobile.v2.user.LoginOption;
import gsshop.mobile.v2.user.TokenCredentialsNew2;
import gsshop.mobile.v2.user.User;
import gsshop.mobile.v2.user.UserAction;
import gsshop.mobile.v2.util.DeviceUtils;
import roboguice.RoboGuice;
import roboguice.util.Ln;


/**
 * 웹에서 앱의 함수를 호출할수 있는 기능을 제공한다.
 *
 */
public class AndroidBridge {
	private static final String TAG = "AndroidBridge";

	private final Context context;

	private WebView webview;

	@Inject
	private UserAction userAction;

	@Inject
	HomeGroupInfoAction homeGroupInfoAction;

	public AndroidBridge(Context context) {
		this(context, null);
	}

	private static AndroidBridge bridgeInstance;

	public static AndroidBridge getInstance(Context context, WebView webview) {
		if (bridgeInstance == null) {
			bridgeInstance = new AndroidBridge(context, webview);
		}

		return bridgeInstance;
	}

	public AndroidBridge(Context context, WebView webview) {
		RoboGuice.getInjector(context).injectMembers(this);
		this.context = context;
		this.webview = webview;
	}

	/**
	 * 카카오 페이 설치 여부 확인
	 *
	 * @param packageName 패키지명
	 * @return 카카오페이 사용 불가 버전 return -1, 카카오톡 미설치 return 0, 카카오페이 사용가능 return 1
	 */
	@JavascriptInterface
	public int checkKAKAOPayAvailable(final String packageName) {
    /*
    * VersionName 4.6.6 이상 인 경우만 카카오 페이가 실행 됩니다. 카카오페이가 탑재되지 않은 하위버전은 결제 호출시 아무런 동작이 일어나지 않습니다. ==> 사용자에게 카톡 업데이트 안내
    */
		int MinVersion = 466; // 최소 사용 가능 버전

		try {
			PackageInfo i = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_CONFIGURATIONS);
			String version = i.versionName;
			if (Integer.parseInt(version.replace(".", "")) < MinVersion) {
				return -1; // 카카오 페이 사용 불가 버전 466 미만
			} else {
				return 1; // 카카오 페이 사용 가능 466 이상
			}
		} catch (NameNotFoundException e) {
			return 0; // 카카오 페이 미설치
		}
	}

	/**
	 * 패키지 이름으로 앱의 설치여부를 확인한다.
	 *
	 * @param packageName	패키지 이름
	 * @return	설치되어 있으면 true 리턴
	 */
	@JavascriptInterface
	public boolean isInstalledApp(final String packageName) {
		PackageManager pkgMgr = context.getPackageManager();

		try {
			pkgMgr.getPackageInfo(packageName, PackageManager.GET_CONFIGURATIONS);
			return true;
		} catch (Exception e) {
			// 10/19 품질팀 요청
			// - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
			// - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
			// - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
			Ln.e(e);
			return false;
		}
	}

	/**
	 * 푸시 수신여부를 리턴한다.
	 *
	 * @return	수신허용인 경우 true 리턴
	 */
	@JavascriptInterface
	public boolean isPushApproved() {
		boolean ret = false;

		try {
			PushSettings model = PushSettings.get();
			ret = model.approve;
		} catch (Exception e) {
			// 10/19 품질팀 요청
			// - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
			// - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
			// - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
			Ln.e(e);
		}

		return ret;
	}

	/**
	 * 푸시 수신여부를 설정한다.
	 *
	 * @param approveYN Y:수신 N:거부
	 */
	@JavascriptInterface
	public void setPushApprove(String approveYN) {
		boolean approve;

		if (approveYN == null) {
			return;
		}

		if ("Y".equalsIgnoreCase(approveYN)) {
			approve = true;
		} else if ("N".equalsIgnoreCase(approveYN)) {
			approve = false;
		} else {
			return;
		}

		try {
			PushSettings model = PushSettings.get();
			model.approve = approve;
			model.save();

			//PMS 설정 정보 세팅
			AbstractTMSService.pushSettings(context, model);

		} catch (Exception e) {
			// 10/19 품질팀 요청
			// - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
			// - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
			// - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
			Ln.e(e);
		}
	}

	/**
	 * 디바이스 아이디를 리턴한다.
	 *
	 * @return 디바이스 아이디
	 */
	@JavascriptInterface
	public String getDeviceId() {
		String deviceId = "";

		// 새로 생성해서 주다 보니 일치하지 않는 문제가 존재하여 기존 생성한 token 모델에서 꺼내서 보내준다.
		TokenCredentialsNew2 loginModel = TokenCredentialsNew2.get();

		if (loginModel == null || TextUtils.isEmpty(loginModel.deviceId)) {
			try {
				deviceId =  DeviceUtils.getUuid(context);
			} catch (Exception e) {
				// 10/19 품질팀 요청
				// - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
				// - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
				// - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
				Ln.e(e);
			}
		}
		else {
			deviceId = loginModel.deviceId;
		}

		return deviceId;
	}

	/**
	 * 앱버전을 리턴한다.
	 *
	 * @return 앱버전
	 */
	@JavascriptInterface
	public String getAppVersion() {
		String appVersion = "";

		try {
			appVersion = MainApplication.appVersionName;
		} catch (Exception e) {
			// 10/19 품질팀 요청
			// - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
			// - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
			// - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
			Ln.e(e);
		}

		return appVersion;
	}

	/**
	 * 구글 ADID를 리턴한다.
	 *
	 * @return ADID
	 */
	@JavascriptInterface
	public String getAdvertisingId() {
		return DeviceUtils.getAdvertisingId();
	}

	/**
	 * 이미지를 선택할수 있는 앱리스트를 팝업으로 띄운다.
	 *
	 * @param uploadUrl 서버 업로드 URL
	 * @param thumbnailId 섬네일 ID
	 * @param callback 콜백함수명
	 * @param isKitKet	Y:킷캣 N:그외
	 */
	@JavascriptInterface
	public void openFileChooser(String uploadUrl, String thumbnailId, String callback, String isKitKet) {
		WebViewImageUpload.getInstance(context, webview).openFileChooser(uploadUrl, thumbnailId, callback, isKitKet);
	}

	/**
	 * 웹에서 전자상거래 정보(구매)를 전달받어 GTM 이벤트로 로깅한다.<br>
	 * 예) 웹 구매상품 정보 (상품명, 가격 등)
	 *
	 * @param action 구매정보 (거래번호, 세금, 배송비 등), json format
	 * @param products 상품정보 (상품명, 가격, 브랜드 등), json array format
	 * @param screenName 스크린이름 (베스트딜, 오늘오픈, 카테고리 등)
	 */
	@JavascriptInterface
	public void measureGTMPurchase(String action, String products, String screenName) {
		GTMAction.sendPurchases(context, action, products, screenName);
	}

	/**
	 * 웹 이벤트를 전달받아 GTM 이벤트로 로깅한다.<br>
	 * 예) 웹 구매하기 버튼 클릭, 웹 장바구니 버튼 클릭 등
	 *
	 * @param category 카테고리명
	 * @param action 액션명
	 * @param label 라벨명
	 */
	@JavascriptInterface
	public void measureGTMEvent(String category, String action, String label) {
		GTMAction.sendEvent(context, category, action, label);
	}

	/**
	 * 페이스북에 이벤트를 전달한다.<br>
	 * 현재 트래킹중인 이벤트는 상품노출, 장바구니이동, 구매 3가지임
	 *
	 * @param eventName 이벤트명
	 * @param contentIds 상품번호(예:"[\"1234\",\"5678\"]")
	 * @param valueToSum 상품가격의 합산액
	 */
	public void measureFBAction(String eventName, String contentIds, double valueToSum) {
		//Ln.i("measureFBAction : " + eventName + ", " + contentIds + ", " + valueToSum);

		//facebook sdk는 Minimum SDK "API 15: Android 4.0.3"부터 지원됨
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
			return;
		}

		final String CURRENCY = "KRW";	//통화단위
		final String CONTENT_TYPE = "product";	//컨텐츠 유형

		try {
			AppEventsLogger logger = AppEventsLogger.newLogger(context);

			//파라미터 세팅
			Bundle parameters = new Bundle();
			parameters.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, CURRENCY);
			parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, CONTENT_TYPE);
			parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, contentIds);

			//이벤트 전송
			logger.logEvent(eventName, valueToSum, parameters);

		} catch (Exception e) {
			// 10/19 품질팀 요청
			// - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
			// - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
			// - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
			Ln.e(e);
		}
	}

	/**
	 * 페이스북에 상품노출 이벤트를 전달한다.
	 *
	 * @param contentIds 상품번호
	 * @param valueToSum 합계금액
	 */
	@JavascriptInterface
	public void measureFBViewContent(String contentIds, String valueToSum) {
		//렌탈상품등은 valueToSum값이 ""로 전달될 수 있어 예외처리 추가
		String value = valueToSum;
		if (TextUtils.isEmpty(value)) {
			value = "0";
		}

		//facebook 제거
		//measureFBAction(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, "[\"" + contentIds + "\"]", Double.parseDouble(value));

		//tensera
//		try{
//			TenseraReporterHelper.reportContentView(contentIds,valueToSum);
//		}catch (Exception e)
//		{
//			//무슨일이 일어날까
//		}

	}

	/**
	 * 앱에서 로그인상태로 설정 후 성공여부를 리턴한다.<br>
	 * 웹에서 이미 로그인 프로세스를 수행했기 때문에 앱에서 실제 로그인 프로세스는 수행하지 않는다.
	 *
	 * @param loginId 사용자 아이디
	 * @param userName 사용자 이름
	 * @param customerNumber 고객번호
	 * @return 로그인상태 설정이 성공한 경우 true 리턴
	 */
	@JavascriptInterface
	public boolean login_interface(String loginId, String userName, String customerNumber) {
		boolean ret = false;

		try {
			//사용자 정보 세팅
			User user = new User();
			user.loginId = loginId;
			user.setUserName(userName);
			user.customerNumber = customerNumber;
			user.cacheUser();

			//자동로그인 설정 해제
			LoginOption option = new LoginOption();
			option.keepLogin = false;
			option.save();

			//로그인 완료시 PMS API 호출 (고객 아이디 로컬 셋팅)
			AbstractTMSService.setCustID(user.customerNumber);

			AbstractTMSService.loginPMS(context, user.customerNumber);

			//하단푸터에 있는 로그인 버튼을 로그아웃 버튼으로 변경하기 위해 이벤트 호출

			//2016/07/27 웹에서(회원) 로그인 요청시에 새로고침을 하지 않는다. 현재 푸터 하단 로그인 버튼도 없어진 상태
			//EventBus.getDefault().post(new Events.LoggedInEvent(user));

			ret = true;
		} catch (Exception e) {
			// 10/19 품질팀 요청
			// - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
			// - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
			// - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
			Ln.e(e);
		}

		return ret;
	}

	/**
	 * 앱에서 로그아웃 수행 후 성공여부를 리턴한다.<br>
	 * 실제 로그아웃 프로세스를 수행한다.<br>
	 * 현재는 항상 true 리턴, 로그아웃 프로세스가 종료될때까지 기다렸다 리턴해야 할 경우 코드 수정 필요
	 *
	 * @return	로그아웃 수행이 성공한 경우 true 리턴
	 */
	@JavascriptInterface
	public synchronized boolean logout_interface() {
		boolean ret = true;
		//네트웍 기능을 사용하기때문에 스레드로 처리

		new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					// 20190403 refresh 하면서 의도하지 않은 동작이 수행됨 (toapp://login) 이에 refresh 여부 false로 변경
					userAction.logout(false);

					//new UserAction(context).logout(true);
				} catch (Exception e) {
					// 10/19 품질팀 요청
					// - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
					// - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
					// - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
					Ln.e(e);
				}
			}
		}).start();

		//웹 호출자에 리턴하기 전 getHomeGroupInfo 수행을 위한 딜레이 추가
		try {
			wait(1000);
		} catch (InterruptedException e) {
			Ln.e(e);
		}

		return ret;
	}

	/**
	 * 날방 입력창 보이기/감추기 인터페이스.
	 * @param showYN showYN
	 */
	@JavascriptInterface
	public void showNalTalkInputBox(String showYN) {
		if("Y".equalsIgnoreCase(showYN)) {
			// 키보드 보이기.
			EventBus.getDefault().post(new Events.NalTalkEvent(true));
		} else {
			// 키보드 감추기.
			EventBus.getDefault().post(new Events.NalTalkEvent(false));

		}
	}

	/**
	 * 라이브톡 입력창 보이기/감추기 인터페이스.
	 * @param showYN showYN
	 */
	@JavascriptInterface
	public void showLiveTalkInputBox(String showYN) {
		if("Y".equalsIgnoreCase(showYN)) {
			// 키보드 보이기.
			EventBus.getDefault().post(new Events.LiveTalkEvent(true));
		} else if("N".equalsIgnoreCase(showYN)) {
			// 키보드 감추기.
			EventBus.getDefault().post(new Events.LiveTalkEvent(false));
		} else {
			//정의된 값이 아니면 아무 작업도 하지 않는다.
		}
	}

	/**
	 * 클립 보드 복사 기능 (API level 11부터 지원)
	 * user-Agent version 141 부터 개발되었으나 웹은 143 이상으로 처리하고 있음(여러 공유하기 기능들과 통일을 위해서 그랬을까요?)
	 * 검수자 : 정수빈
	 * @param url 		복사될 URL
	 * @return			성공 결과
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@JavascriptInterface
	public boolean sendClipData(String url) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			return false;
		}
		ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData clip = ClipData.newPlainText("label", url);
		clipboard.setPrimaryClip(clip);
		Toast.makeText(context, R.string.bridge_clipboard, Toast.LENGTH_SHORT).show();
		return  true;
	}

	/**
	 * 클립보드 복사 인터 펫이스  198 이상 동작
	 * @param url 복사될 대상 URL 아닐수 있음
	 * @param title 복사될때 표시되는 메세지
	 * @return
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@JavascriptInterface
	public boolean sendClipDataTitle(String url,String title) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			return false;
		}
		if(title == null || title.length() < 1)
		{
			//AS-IS 꺼로 돌아라
			sendClipData(url);
			return false;
		}
		ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData clip = ClipData.newPlainText("label", url);
		clipboard.setPrimaryClip(clip);
		Toast.makeText(context, title, Toast.LENGTH_SHORT).show();
		return  true;
	}

	/**
	 * 바로구매 팝업에서 장바구니 담기 시 장바구니 쿠키 바로 갱신되어 표시하는 인터페이스
	 */
	@JavascriptInterface
	public void refreshCart() {
		EventBus.getDefault().post(new Events.FlexibleEvent.RefreshCart());
	}

	/**
	 * 최근 본 상품리스트의 변경이 이루어진 상태를 APP에게 알려주어 상태를 갱신할수 있도록 하는 규격
	 */
	@JavascriptInterface
	public void lastPrdUpdate() {
		EventBus.getDefault().post(new Events.LastPrdUpdateEvent());
	}


	/**
	 * 웹 이벤트를 전달받아 AMP 이벤트로 로깅한다.<br>
	 *
	 * @param eventName
	 * @param eventProperties
	 */
	@JavascriptInterface
	public void sendAMPEventProperties(String eventName, String eventProperties)
	{
		AMPAction.sendAmpEvent(eventName,eventProperties);
	}

	/**
	 * 웹 이벤트를 전달받아 AMP 이벤트로 로깅한다.<br>
	 *
	 * @param eventName
	 */
	@JavascriptInterface
	public void sendAMPEvent(String eventName)
	{
		AMPAction.sendAmpEvent(eventName);
	}

	/**
	 * 웹 이벤트를 전달받아 app boy 이벤트로 로깅한다
	 * 회원가입 : sdk_memberJoin / 161이상
	 * @param eventName
	 */
	@JavascriptInterface
	public void sendAppboyEvent(String eventName)
	{
		//Log.e("AAA","sendAppboyEvent : " + eventName);
		try {
			if( TextUtils.isEmpty(eventName) )
			{
				return;
			}
			Appboy.getInstance(context).logCustomEvent(eventName);

		}catch (Exception e)
		{
		}
	}
	/**
	 * 웹 이벤트를 전달받아 app boy 이벤트로 로깅한다
	 * 주문완료 sdk_checkout / 161이상
	 * @param eventName
	 */
	@JavascriptInterface
	public void sendAppboyEventProperties(String eventName,String eventProperties)
	{
		//Log.e("AAA","sendAppboyEventProperties : " + eventName);
		try {
			if(TextUtils.isEmpty(eventName) || TextUtils.isEmpty(eventProperties) )
			{
				return;
			}
			JSONObject jsonObject = new JSONObject(eventProperties);
			Appboy.getInstance(context).logCustomEvent(eventName,new AppboyProperties(jsonObject));

			//앱이 반복해서 보내기 서버랑 협의 해야 한다.
			/*
			JSONArray jsonArray = new JSONArray(eventProperties);
			//JSONObject jsonObject = new JSONObject(eventProperties);
			for (int i = 0; i < jsonArray.length(); i++){
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				Log.e("AAA","sendAppboyEventProperties : " + jsonArray.toString());
				Appboy.getInstance(context).logCustomEvent(eventName,new AppboyProperties(jsonObject));

			}
			 */

		}catch (Exception e)
		{
			Ln.d(e);
		}
	}

	/**
	 * web->app 퍼미션 요청 브릿지
	 * 165 이상에서만 호출됨
	 *
	 *  ex) onPermissionShowPrompt("member", "SMS")
	 *
	 * @param origin 웹에서 컨텐츠를 요청 하는 주체 "member" / order / shop
	 * @param permitionType 실제 퍼미션 명을 넣고 싶었으나 명시적 사용 좋을것으로 판단 우린 구글이 아니니까
	 */
	@JavascriptInterface
	public void onPermissionShowPrompt(String origin,String permitionType) {
		//권한체크
		// 구글 정책으로 인해 SMS Receiver 부분 삭제 20190304
	}

	/**
	 *  web->app 퍼미션 요청 브릿지
	 * 181 이상에서만 호출됨
	 *
	 * wifi 여부를 반환
	 * 단. 모바일 데이터가 이미 동의가 되어 있는 wifi 켜져 있지 않더라도, true 반환
	 *
	 * @param confirmFlag
	 * "Y"
	 * 데이터 동의도 봐주세요
	 * ex) wifi X 동의 O  = true
	 * @return
	 * true : wifi O || 동의 O
	 * false : wifi X || 동의 X ===> 불분명할때,
	 */

	@JavascriptInterface
	public boolean isWifiDataConfirm(String confirmFlag)
	{
		if(context != null) {
			// 동의 여부를 볼텐가 ??
			if("Y".equals(confirmFlag))
			{
				// 데이터 동의가 되어 있으면? wifi 여부 보지 않는다.
				if(MainApplication.isNetworkApproved)
				{
					//동의가 되었으니 true
					return true;
				}
				else{
					//동의가 안되어 있으면 wifi 상태를 반환
					return NetworkStatus.isWifiConnected(context);
				}

			}
			//동의 여부 안보면 wifi 상태를 반환
			return NetworkStatus.isWifiConnected(context);
		}
		//혹시라도 컨텐스트가 유효하지 않으면 wifi 상태가 불분명 할테니 fasle 반환
		return false;
	}

	/**
	 * 동의 여부값 반환
	 *
	 * @return
	 * true : 동의된 상태 (앱 종료시 초기화)
	 * fasel : 알수 없거나 동의된 상태가 아님
	 */
	@JavascriptInterface
	public boolean isDataConfirm()
	{
		if(context != null)
		{
			// 데이터 동의가 되어 있으면? wifi 여부 보지 않는다.
			if(MainApplication.isNetworkApproved)
			{
				//동의가 되었으니 true
				return true;
			}
		}
		//혹시라도 컨텐스트가 유효하지 않으면 false , 동의가 아니여도 false
		return false;
	}

	/**********************************************
	 *  딜 단품 영역 시작
	 **********************************************/

	/**
	 * 딜 단품에 방송알림 켜기
	 */
	@JavascriptInterface
	public void broadAlertSet() {
		EventBus.getDefault().post(new Events.EventProductDetail.BroadAlertEvent(true));
	}

	/**
	 * 딜 단품에 방송알림 끄기
	 */
	@JavascriptInterface
	public void broadAlertCancel() {
		EventBus.getDefault().post(new Events.EventProductDetail.BroadAlertEvent(false));
	}

	/**
	 * 딜-> 상품선택 할때 상단 네이티브 헤더를 숨기기 위한 이벤트
	 */
	@JavascriptInterface
	public void hideHeader() {
		EventBus.getDefault().post(new Events.EventProductDetail.HideHeaderEvent(true));
	}

	/**
	 * 딜-> 상품 -> ‘닫기’ 할때 상단 네이티브 헤더를 보이기 위한 이벤트
	 */
	@JavascriptInterface
	public void showHeader() {
		EventBus.getDefault().post(new Events.EventProductDetail.HideHeaderEvent(false));
	}

    /**
     * 툴팁 (카드할인 정보, 배송정보, 무이자 등) 뜰 때 navigation 영역 dim 처리하기 위함. (web 이랑 동일하게 하기 위함)
     */
	@JavascriptInterface
	public void layerOpen() {
		EventBus.getDefault().post(new Events.EventProductDetail.LayerPopupEvent(true));
	}

    /**
     * 툴팁 (카드할인 정보, 배송정보, 무이자 등) 뜰 때 navigation 영역 dim 처리하기 위함. (web 이랑 동일하게 하기 위함)
     */
	@JavascriptInterface
	public void layerClose() {
		EventBus.getDefault().post(new Events.EventProductDetail.LayerPopupEvent(false));
	}

	/**
	 * 딜 자세히보기 OPEN 시 호출 (미니플레이어, navigation bar 없애는 기능)
	 */
	@JavascriptInterface
	public void noDimmLayerOpen() {
		EventBus.getDefault().post(new Events.EventProductDetail.NoDimmLayerOpenEvent(true));
	}

	/**
	 * 딜 자세히보기 Close 시 호출
	 */
	@JavascriptInterface
	public void noDimmLayerClose() {
		EventBus.getDefault().post(new Events.EventProductDetail.NoDimmLayerOpenEvent(false));
	}

	/**
	 * 찜 ON 성공시 호출
	 */
	@JavascriptInterface
	public void zzimOk() {
		EventBus.getDefault().post(new Events.EventProductDetail.ZzimResponseEvent(true));
	}

	/**
	 * 찜 OFF 성공시 호출
	 */
	@JavascriptInterface
	public void zzimCancel() {
		EventBus.getDefault().post(new Events.EventProductDetail.ZzimResponseEvent(false));
	}

	/**
	 * 영상 멈춤
	 */
	@JavascriptInterface
	public void webPlay() {
		EventBus.getDefault().post(new Events.EventProductDetail.PlayStopEvent());
	}

	/**
	 * 영상 mute
	 */
	@JavascriptInterface
	public void webMute() {
		EventBus.getDefault().post(new Events.EventProductDetail.MutePlayerEvent(true));
	}

	/**
	 * 영상 mute
	 */
	@JavascriptInterface
	public void webSound() {
		EventBus.getDefault().post(new Events.EventProductDetail.MutePlayerEvent(false));
	}

	/**
	 * 영상 최초 재생 시에 LTE 노출 여부 승인 했는지 여부
	 */
	@JavascriptInterface
	public void noDataWarning() {
		MainApplication.isNetworkApproved = true;
	}

	/**
	 * 영상 최초 재생 시에 LTE 노출 여부 승인 했는지 여부
	 */
	@JavascriptInterface
	public void showDataWarning() {
		MainApplication.isNetworkApproved = false;
	}

	@JavascriptInterface
	public void webTouchCoordinatesId(String id) {
		if (id.equalsIgnoreCase("appNativeDiv")) {
			EventBus.getDefault().post(new Events.EventProductDetail.NativeTouchEvent(true));
		} else {
			EventBus.getDefault().post(new Events.EventProductDetail.NativeTouchEvent(false));
		}
	}

	/**********************************************
	 *  딜 단품 영역 끝
	 **********************************************/
}