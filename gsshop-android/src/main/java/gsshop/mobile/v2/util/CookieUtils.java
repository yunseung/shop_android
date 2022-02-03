/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.web.WebViewCookieManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.Keys.COOKIE;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isEmpty;

/**
 * 쿠키 관련 유틸.
 *
 * 주의)
 * -쿠키값은 서버에서만 변경하는 것을 원칙으로 한다.
 *  최근검색어 처럼 불가피한 경우에는 웹뷰 또는 API를 통해 쿠키값을 동기화하는 방법이 필요하다.
 *  (왜냐하면 앱에서 쿠키를 생성할 경우 expire-date정보를 추출할 수 없어 세션쿠키로 생성되기 때문에)
 * -존재하는 쿠키를 overwrite하면 expire-date가 존재하는 쿠키도 세션쿠키로 변경된다.
 *  이런 이유로 opyRestClientCookiesToWebView는 사용하지 않는다.
 *
 * TODO
 * -검색어 쿠키(search)는 앱에서 수정할때 세션쿠키로 변경됨. expire-date를 서버와 동기화 하는 방법 필요 (웹뷰호출, API 등)
 * -opyRestClientCookiesToWebView 수행하지 않기 때문에 API 호출 후 웹뷰로 복사가 필요한 쿠키는 별도 처리 필요
 * -ecid의 "\"" 제거 로직이 아직도 필요한지 확인하고 필요없으면 해당 로직 제거
 *
 */
public abstract class CookieUtils {

	private static final String TAG = "CookieUtils";

	//쿠키관리를 위한 도메인
	private static final String COOKIE_DOMAIN = "gsshop.com";
	//restClient에서는 도메인에 "http://"가 추가되어야 쿠키를 정상적으로 read/write 할 수 있다.
	private static final String HTTP_COOKIE_DOMAIN = ServerUrls.HTTP + COOKIE_DOMAIN;
	//webview -> restClient 쿠키 복사시 도메인 앞에 "."이 포함되어야 한다.
	private static final String HTTP_COOKIE_DOMAIN_DOT = ServerUrls.HTTP + "." + COOKIE_DOMAIN;

	/**
	 * {@link RestClient}의 모든 쿠키를 지우고,
	 * {@link WebView}의 모든 쿠키를 {@link RestClient}에 추가한다.
	 * (WebView ={@literal >} RestClient으로의 쿠키 동기화)
	 *
	 * @param context
	 * @param restClient
	 */
	public static void syncWebViewCookiesToRestClient(Context context, RestClient restClient) {
		copyWebViewCookiesToRestClient(context, restClient);
	}

	public static Object syncAndcopyWebViewCookiesToRestClient(Context context, RestClient restClient, String url, Class<?> clazz) throws RestClientException, URISyntaxException{
		Object returnClazz = null;
		synchronized (CookieUtils.class) {

			/**
			 * [이민수]
			 * WEB <-> REST 쿠키 공유시, 쿠키가 중복으로 생성된다 이유는 내가 군 쿠키가 아니기 때문이다.
			 * 이것을 해결한다면, 복사해도 된다.
			 * 참고로 이것을 하지 않으면 무슨일이 발생할까???
			 * 플랫폼 사업팀의 재정렬 측정이 정상적으로 이루어지지 않을것으로 판단되다
			 */

			try {
				syncWebViewCookiesToRestClient(context, restClient);

				if (url.contains("atm.gsshop.com")) {
					SimpleClientHttpRequestFactory tempFactory = new SimpleClientHttpRequestFactory();
					tempFactory.setConnectTimeout(120000); // 2분
					tempFactory.setReadTimeout(120000);    // 2분

					restClient.setRequestFactory(tempFactory);
				}

				returnClazz = restClient.getForObject(new URI(url), clazz);
			}
			catch (ResourceAccessException e) {
				Ln.e(e.getMessage());

				// 타임아웃
				try {
					NetworkUtils.showUnstableAlert((Activity) context);
				}
				catch (ClassCastException e2) {
					Ln.e(e2.getMessage());
				}
			}

			//아래는 웹뷰의 expire-date가 존재하는 쿠키가 세션쿠키로 변경될 수 있다.
			//로그인 외에 API 호출후 아래를 수행해야 하는 경우는 확인 후 해당 쿠키만 추가하는 방향으로 진행해야 함
			//copyRestClientCookiesToWebView(context, restClient);
		}
		return returnClazz;
	}

	/**
	 * {@link WebView}의 모든 쿠키를 {@link RestClient}에 추가한다.
	 * 2014.03.04 parksegun EC 통합 재구축
	 * 톰켓 서버 최신 버전에서 cookie내에 특수문자가 있으면 "(따옴표)로 묶어 처리함. 이로 인해 Spring RestClient Copy 시 "(따옴표) 가 더 추가되어 ""(쌍따옴표)가 붙어 인식 오류가 발생한다.
	 *
	 * @param context
	 * @param restClient
	 */
	public static void copyWebViewCookiesToRestClient(Context context, RestClient restClient) {
		List<NameValuePair> cookies = WebViewCookieManager.getCookies(
				context.getApplicationContext(), COOKIE_DOMAIN);

		ArrayList<NameValuePair> newCookie = new ArrayList<>();

		for (NameValuePair c : cookies) {
			String tempValue = c.getValue();
			tempValue = tempValue.replace("\"", "");
			newCookie.add(new BasicNameValuePair(c.getName(), tempValue));
//			Ln.d("hklim cookie (" + c.getName() + ") : " + tempValue);
//			if (c.getName().contains("pcid")) {
//				Ln.d("hklim pcid cookie : " + tempValue);
//			}
		}
		// 덮어 쓰기가 되지 않아 갱신되지 않는 문제가 존재한다.
		// 결국 삭제 후 재설정하는 방법으로 해야한다.
//		if (Build.VERSION.SDK_INT >= 26) {
		//오레오 이상 쿠키 추가시 도메인 변경으로 api 요청시 쿠키가 중복전송됨 (두개 도메인:.gsshop.com and m.gsshop.com)
		//아래 제거로직 타면 m.gsshop.com 쿠키만 전송됨
		restClient.removeAllCookies();
//		}
		restClient.addCookies(getRestCookieDomain(), newCookie);
	}

	/**
	 * OS 버전별 도메인 정보를 반환한다.
	 *
	 * @return 도메인
	 */
	public static String getRestCookieDomain() {
		//오레오는 도메인앞에 "." 이 있으면 쿠키를 read/wirte 할수 없다. (host.domain 형태여야 함)
		return Build.VERSION.SDK_INT >= 26 ? ServerUrls.getHttpRoot() : HTTP_COOKIE_DOMAIN_DOT;
	}

	/**
	 * {@link RestClient}이 보유한 모든 쿠키를 {@link WebView}에 추가한다.
	 *
	 * REST 통신에 의해 인증된 세션을 WebView 내부 통신에도 적용하기 위한 용도로 사용한다.
	 * 필요시 {@link WebViewCookieManager#removeAllSessionCookies(Context)}를
	 * 먼저 실행하여 기존 세션쿠키를 삭제해주도록 한다.
	 *
	 * @param context
	 * @param restClient
	 */
	public static void copyRestClientCookiesToWebView(Context context, RestClient restClient) {
		List<String> cookies = restClient.getCookies(getRestCookieDomain());

		/*if (cookies != null && AppInfo.isDebugMode(context.getApplicationContext())) {
			for (String c : cookies) {
				Ln.e("copyRestClientCookiesToWebView RestClient 쿠키 : " + c);
			}
		}*/

		WebViewCookieManager.addCookies(context.getApplicationContext(), COOKIE_DOMAIN, cookies);

		//appmediatype 쿠키값 확인
		checkAppMediaTypeCookie(context);
	}

	/**
	 * 테스트 용
	 * @param context
	 * @param restClient
	 */

	public static void setWaPcidToWebView(Context context, RestClient restClient, String pcid) {
		List<String> cookies = restClient.getCookies(HTTP_COOKIE_DOMAIN);

		ArrayList<String> tempCookies = null;
		if (cookies != null){// && AppInfo.isDebugMode(context.getApplicationContext())) {
			tempCookies = new ArrayList<>();
			boolean isExist = false;
			for (int i=0; i<cookies.size(); i++) {
				String c = cookies.get(i);
				Ln.d("copyRestClientCookiesToWebView RestClient 쿠키 : " + c);
				if (c.indexOf("wa_pcid=") > -1) {
					isExist = true;
					String tempStr = "wa_pcid=" + pcid + "; domain=.gsshop.com; path=/";
					tempCookies.add(tempStr);
				}
				else {
					tempCookies.add(c);
				}
			}
			if (!isExist) {
				String tempStr = "wa_pcid=" + pcid + "; domain=.gsshop.com; path=/";
				tempCookies.add(tempStr);
			}
		}

		WebViewCookieManager.addCookies(context.getApplicationContext(), COOKIE_DOMAIN, tempCookies);

		//appmediatype 쿠키값 확인
		checkAppMediaTypeCookie(context);
	}

	/**
	 * appmediatype 쿠키값이 없거나 규약된 값이 아니면 규약된 값으로 변경한다.
	 *
	 * @param context 컨텍스트
	 */
	public static void checkAppMediaTypeCookie(Context context) {
		String appMediaTypeCode = context.getResources().getString(R.string.mc_webview_device_type);
		NameValuePair amtCookie = getWebviewCookie(context.getApplicationContext(), Keys.COOKIE.APP_MEDIA_TYPE);
		if (amtCookie == null || !appMediaTypeCode.equals(amtCookie.getValue())) {
			setWebviewCookie(context, new BasicNameValuePair(Keys.COOKIE.APP_MEDIA_TYPE, appMediaTypeCode));
			//Ln.i("appmediatype reset");
		}
	}

	/**
	 * 주어진 이름을 갖는 WebView 쿠키를 RestClient에 추가한다.
	 *
	 * @param context
	 * @param restClient
	 * @param cookieName
	 */
	public static void copyWebViewCookieToRestClient(Context context, RestClient restClient,
													 String cookieName) {
		NameValuePair c = WebViewCookieManager.getCookie(context.getApplicationContext(),
				COOKIE_DOMAIN, cookieName);
		if (c == null) {
			return;
		}

		restClient.addCookie(COOKIE_DOMAIN, c);
	}

	/**
	 * WebView에 간편주문 인증 쿠키가 있는가?
	 *
	 * @param context
	 * @return
	 */
	public static boolean hasQuickOrderCookieInWebView(Context context) {
		NameValuePair c = WebViewCookieManager.getCookie(context.getApplicationContext(),
				COOKIE_DOMAIN, Keys.COOKIE.QUICK_ORDER);
		return c != null;
	}

	/**
	 * UA Tracker(setMainClickEvent) 호출에 필요한 파라미터(쿠키값)를 취득한다.
	 * @param context
	 * @return
	 */
	public static String getWaPcId(Context context) {
		String wapcId = "0";
		NameValuePair c = WebViewCookieManager.getCookie(context.getApplicationContext(),
				COOKIE_DOMAIN, COOKIE.WA_PCID);
		if (c != null) {
			wapcId = c.getValue();
		}
		return wapcId;
	}

	/**
	 * UA Tracker(setMainClickEvent) 호출에 필요한 파라미터(쿠키값)를 취득한다.
	 * @param context
	 * @return
	 */
	public static String getPcId(Context context) {
		String pcId = "0";
		NameValuePair c = WebViewCookieManager.getCookie(context.getApplicationContext(),
				COOKIE_DOMAIN, COOKIE.PCID);
		if (c != null) {
			pcId = c.getValue();
		}
		return pcId;
	}


	public static String getAdult(Context context) {
		String ADULT = "0";
		NameValuePair c = WebViewCookieManager.getCookie(context.getApplicationContext(),
				COOKIE_DOMAIN, COOKIE.ADULT);
		if (c != null) {
			ADULT = c.getValue();
		}
		return ADULT;
	}

	/**
	 * ecid 쿠키문자열에서 cookieName에 해당하는 값을 추출한다.
	 *
	 * @param context 컨텍스트
	 * @param cookieName 추출할 쿠키이름
	 * @return cookieName에 해당하는 쿠키값, 값이 없으면 "" 리턴
	 */
	public static String getWebviewCookieFromEcid(Context context, String cookieName) {
		String ecid = "";
		String ret = "";

		NameValuePair c = WebViewCookieManager.getCookie(context.getApplicationContext(),
				COOKIE_DOMAIN, COOKIE.LOGIN_SESSION);
		if (c != null) {
			ecid = c.getValue();
		}

		List<String> items = Arrays.asList(ecid.split("\\s*~\\s*"));
		for (String item : items) {
			//Ln.i("item : " + item);
			String[] ary = item.split("=");
			if (cookieName.equals(ary[0])) {
				ret = ary[1];
				break;
			}
		}

		return ret;
	}

	/**
	 * 웹뷰쿠키에 파라미터(cookieInfo)로 전달받은 쿠키를 추가한다.
	 *
	 * @param context 컨텍스트
	 * @param cookieInfo 웹뷰쿠키에 추가할 NameValuePair 포맷의 쿠키 정보
	 */
	public static void setWebviewCookie(Context context, NameValuePair cookieInfo) {
		if (cookieInfo != null) {
			List<String> listCookies = new ArrayList<String>();
			String newCookie = String.format(cookieInfo.getName() + "=%s; domain=%s; path=/",
					cookieInfo.getValue(), COOKIE_DOMAIN);
			listCookies.add(newCookie);
			WebViewCookieManager.addCookies(context.getApplicationContext(), COOKIE_DOMAIN,
					listCookies);
		}
	}

	/**
	 * 쿠키 이름에 대한 쿠키정보(NameValuePair)를 취득한다.
	 *
	 * @param context 컨텍스트
	 * @param cookieName 쿠키이름
	 * @return cookieName에 대한 NameValuePair (값이 존재하지 않으면 null 리턴)
	 */
	public static NameValuePair getWebviewCookie(Context context, String cookieName) {
		if (isEmpty(context)) {
			return null;
		}
		return WebViewCookieManager.getCookie(context.getApplicationContext(),
				COOKIE_DOMAIN, cookieName);
	}

	/**
	 * RestClient 쿠키정보를 표시한다.
	 *
	 * @param restClient RestClient object
	 * @param tag caller object
	 */
	public static void showRestClientCookies(RestClient restClient, String tag) {
		List<String> cookies = restClient.getCookies(getRestCookieDomain());

		if (cookies != null) {
			for (String c : cookies) {
				Ln.e(tag + " RestClient 쿠키 : " + c);
			}
		}
	}

	/**
	 * webview의 cookie를 RestClient에 복사
	 * @param context
	 * @param restClient
	 */
	public static void copyWebViewCookieToRestClientCookieDomain(Context context, RestClient restClient, String cookieName) {
		NameValuePair c = WebViewCookieManager.getCookie(context.getApplicationContext(),
				COOKIE_DOMAIN, cookieName);
		if (c == null) {
			return;
		}

		restClient.addCookie(getRestCookieDomain(), c);
	}

	/**
	 * Webview 쿠키정보를 표시한다.
	 *
	 * @param context Context
	 * @param tag caller object
	 */
	public static void showWebviewCookies(Context context, String tag) {
		List<NameValuePair> cookies = WebViewCookieManager.getCookies(context, COOKIE_DOMAIN);

		if (cookies != null) {
			for (NameValuePair c : cookies) {
				String name = c.getName();
				String value = c.getValue();
				// 검색어 string만 decodin해서 로깅하기 위한 코드
				//                if (name.equals(Keys.COOKIE.SEARCH)) {
				//                    try {
				//                        value = URLDecoder.decode(value, "utf-8");
				//                    } catch (UnsupportedEncodingException e) {
				//                        e.printStackTrace();
				//                    }
				//                }
				Ln.e(tag + " Webview 쿠키 [" + name + "]" + value);
			}
		}
	}

	/**
	 * 쿠키에서 search(최근검색어 리스트)만 리셋한다. 새로운 search string이 없으면<br>
	 * search를 삭제만 하고, 있으면 삭제한 후 새로운 string으로 추가한다.
	 * @param context
	 * @param newSearchString
	 *     변경하고자 하는 string(최근검색어 리스트)<br>
	 *     쿠키 형식에 맞게, 최근검색어가 뒤로 가고 '@'로 구분한 후 utf-8로 encoding된 상태.
	 */
	public static void resetSearchAddDateCookie(Context context, String newSearchString) {
		List<NameValuePair> cookies = WebViewCookieManager.getCookies(context, COOKIE_DOMAIN);

		// search의 index를 찾아 삭제
		int searchIndex = -1;
		for (int i = 0; i < cookies.size(); i++) {
			// 반영할 쿠키가 기존 search 에서 searchAddDate로 변경됨.
			if (cookies.get(i).getName().equals(COOKIE.SEARCH_ADD_DATE)) {
				searchIndex = i;
				break;
			}
		}
		if (searchIndex >= 0 && searchIndex < cookies.size()) {
			cookies.remove(searchIndex);
		}

		// 새로운 검색어리스트가 null이면 deleteAll로 동작(쿠키에서 search가 삭제됨)
		if (newSearchString != null) {
			// searchString은 '@'와 검색어를 utf-8롤 변경한 것. 최근 검색어가 뒤로 간다.
			setWebviewCookie(context, new BasicNameValuePair(Keys.COOKIE.SEARCH_ADD_DATE, newSearchString));
		}
	}

	public static void resetSearchCookie(Context context, String newSearchString) {
		List<NameValuePair> cookies = WebViewCookieManager.getCookies(context, COOKIE_DOMAIN);

		// search의 index를 찾아 삭제
		int searchIndex = -1;
		for (int i = 0; i < cookies.size(); i++) {
			// 반영할 쿠키가 기존 search 에서 searchAddDate로 변경됨.
			if (cookies.get(i).getName().equals(COOKIE.SEARCH)) {
				searchIndex = i;
				break;
			}
		}
		if (searchIndex >= 0 && searchIndex < cookies.size()) {
			cookies.remove(searchIndex);
		}

		// 새로운 검색어리스트가 null이면 deleteAll로 동작(쿠키에서 search가 삭제됨)
		if (newSearchString != null) {
			// searchString은 '@'와 검색어를 utf-8롤 변경한 것. 최근 검색어가 뒤로 간다.
			setWebviewCookie(context, new BasicNameValuePair(Keys.COOKIE.SEARCH, newSearchString));
		}
	}

	/**
	 * 메모리와 쿠키저장소의 쿠키를 동기화 시킨다.<br>
	 * 웹뷰에서 쿠키를 생성한 후 앱을 종료하고, 딥링크로 해당 웹뷰에 바로 접속하면 생성했던 쿠키가 손실된다.<br>
	 * 이때 본 함수를 호출하면 쿠키 손실을 막을 수 있다. (예:럭키백에 오늘그만보기 클릭시 생성되는 쿠기)
	 *
	 * @param context 컨텍스트
	 * @param restClient RestClient
	 */
	public static void syncWebViewCookies(Context context, RestClient restClient) {
		CookieSyncManager cookieSyncManager = android.webkit.CookieSyncManager
				.createInstance(context);
		cookieSyncManager.sync();
	}

	public static void changeWebviewCookie(@NonNull Context context, @NonNull String cookieName, @NonNull String cookieValue) {
		List<NameValuePair> cookies = WebViewCookieManager.getCookies(
				context.getApplicationContext(), COOKIE_DOMAIN);

		WebViewCookieManager.removeCookies(context.getApplicationContext(), COOKIE_DOMAIN);

		ArrayList<String> newWebviewCookie = new ArrayList<String>();
		for (NameValuePair item : cookies) {
			if(cookieName.equalsIgnoreCase(item.getName())) {
				newWebviewCookie.add(cookieName + "=" + cookieValue);
			}
			else {
				newWebviewCookie.add(item.getName() + "=" + item.getValue());
			}
		}

		WebViewCookieManager.addCookies(context.getApplicationContext(), COOKIE_DOMAIN, newWebviewCookie);

		List<NameValuePair> cookies2 = WebViewCookieManager.getCookies(
				context.getApplicationContext(), COOKIE_DOMAIN);

		//appmediatype 쿠키값 확인
		checkAppMediaTypeCookie(context);
	}

	/**
	 * 쿠키에서 pcid(최근검색어 리스트)만 리셋한다. 새로운 newPcId string이 없으면<br>
	 * newPcId 삭제만 하고, 있으면 삭제한 후 새로운 string으로 추가한다.
	 * @param context
	 * @param newPcId
	 */
	public static void resetPcIdCookie(Context context, String newPcId) {
		//List<NameValuePair> cookies = WebViewCookieManager.getCookies(context, ServerUrls.HTTP_ROOT);
		List<NameValuePair> cookies = WebViewCookieManager.getCookies(context, COOKIE_DOMAIN);
//		List<String> listCookies = new ArrayList<String>();

		// search의 index를 찾아 삭제
		int pcidIndex = -1;
		for (int i = 0; i < cookies.size(); i++) {
			if (cookies.get(i).getName().equals(Keys.COOKIE.PCID)) {
				pcidIndex = i;
				break;
			}
		}
		if (pcidIndex >= 0 && pcidIndex < cookies.size()) {
			cookies.remove(pcidIndex);
		}

		// 새로운 검색어리스트가 null이면 deleteAll로 동작(쿠키에서 search가 삭제됨)
		if (newPcId != null) {
			// searchString은 '@'와 검색어를 utf-8롤 변경한 것. 최근 검색어가 뒤로 간다.
			setWebviewCookie(context, new BasicNameValuePair(Keys.COOKIE.PCID, newPcId));
		}
	}
}