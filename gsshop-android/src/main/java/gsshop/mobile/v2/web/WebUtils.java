/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.URLUtil;

import com.gsshop.mocha.core.util.ActivityUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.Keys.ACTION;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.shop.schedule.TVScheduleShopFragment;
import gsshop.mobile.v2.menu.BaseTabMenuActivity;
import gsshop.mobile.v2.menu.TabMenu;
import gsshop.mobile.v2.setting.SettingActivity;
import gsshop.mobile.v2.util.ClickUtils;
import gsshop.mobile.v2.util.DisplayUtils;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isEmpty;
import static gsshop.mobile.v2.AbstractBaseActivity.getHomeGroupInfo;
import static gsshop.mobile.v2.MainApplication.useNativeProduct;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleShopFragment.isCalledFromBanner;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleShopFragment.scheduleBroadType;

/**
 * Web 화면 관련 utils
 *
 */
public class WebUtils {

	private final static String EXTERNAL_TAG = "external://";

	private final static String LIVETALK_HOST = "livetalk";
	private final static String NALBANG_HOST = "nalbang";
	private final static String TOP_API_PARAM_KEY = "topapi";
	private final static String BOTTOM_API_PARAM_KEY = "bottomurl";

	public final static String DIRECT_ORD_HOST = "directOrd";
	public final static String PRE_PRD_HOST = "prePrd";
	//8/6일 네비케이션(햄버그) 호출 되지 않도록
	//private final static String NAVI_SHOW_HOST = "leftnavi";
	private final static String LOGIN_HOST = "login";
	private final static String SETTING_HOST = "setting";

	public final static String MSEQ_PARAM_KEY = "mseq";
	public final static String TABID_PARAM_KEY = "tabId";
	public final static String BROADTYPE_PARAM_KEY = "broadType";
	public final static String WISELOG_PARAM_KEY = "wiselog";
	public final static String GROUPCODE_PARAM_KEY = "groupCd";

	// 단품 네이티브 관련 파람
	public final static String PRD_NATIVE_IS_WEB_PARAM_KEY = "isweb";	// 단품 네이티브화로 인해 param에 isWeb이면 무조건 web으로 이동.ㅋ
	public final static String PRD_NATIVE_IS_PRELOAD = "ispre";		// 단품 네이티브 시 이미지 프리로드 여부

	//모달
	public final static String EXTMODAL_HOST = "extmodal";
	public final static String FULLMODAL_WEB_HOST = "fullwebview";
	public final static String EXTERNAL_WEB_HOST = "browser";
	public final static String OUTSITEMODAL_WEB_HOST = "outsitemodal";
	public final static String MODAL_WEB_HOST = "modalWebview";

	//AB테스트시 apptiInfo를 위한 변수
	public final static String APPTIINFO_PARAM_KEY = "apptiInfo";

	//주문서 하단탭 유무 AB 테스트를 위한 파라미터 실허 이후 삭제 필요
	public static final String TABYN_PARAM_KEY= "tabYn";


	// 추가 (openUrl)
	public final static String OPEN_URL_PARAM_KEY = "openUrl";

	// 추가 (lseq)
	public final static String LSEQ_PARAM_KEY = "lseq";

	// 추가 됨 (media)
	public final static String MEDIA_PARAM_KEY = "media";

	private final static String URL_TV_LOGIN_LOWER_CASE = "/member/tvlogin.gs";

	// 모바일 라이브 웹뷰인지 여부 확인.
	public static final String URL_MOBILE_LIVE = "/shop/section/mobilelive/webplayer";

	// 지금 Best 전체보기 체크박스
	public final static String BEST_NOW_CHK_IS_ALL_VIEW_PARAM_KEY = "isAllView";

	//단품에서 구매하기 클릭시 주문서 노출순서
	//로그인 : addOrdSht.gs -> ordShtGate.g s-> ordSht.gs
	//비로그인 : 로그인완료 -> addBasketForward.gs -> addOrdSht.gs -> ordShtGate.g s-> ordSht.gs
	//주문서 페이지 정보 (주문서 최초노출화면)
	private static final String ORDER_URL = "addordsht.gs";

	//주문완료 페이지 정보
	private static final List<String> ORDER_COMPLETE_URLS = Arrays.asList(
			"confirmorder.gs"		//주문완료화면
	);

	/**
	 * 탬 메뉴를 포함하지 않는 페이지 구분
	 * @param url url
	 * @return boolean
	 */
	public static boolean isNoTabPage(String url) {
		return url.contains("/deal.gs?") || url.contains("/prd.gs?");
	}

	/**
	 * 네이티브 단품 대상인지 확인
	 * jseis_withLGeshop.jsp 형태의 푸시, 광고등 URL처럼 파라미터로 붙은 /prd.gs는 무시함
	 *
	 * @param url url
	 * @return boolean 네이티브 대상이면 true
	 */
	public static boolean isNativeProduct(String url) {
		if (isEmpty(url)) {
			return false;
		}

		Uri uri = Uri.parse(url);
		String isWebParam = uri.getQueryParameter(PRD_NATIVE_IS_WEB_PARAM_KEY);

		if (MainApplication.mustNativeProduct) {
			return true;
		}

		boolean isNative = false;

		//23버전 이상일 때만 단품 네이티브 호출
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			return false;
		}

		// 그런데 url에 prd.gs 나 deal.gs 가 있으면 이건 상품 혹은 딜이니까 단품 네이티브화 activity로 이동.;
		if (uri.getPath().contains("/prd.gs") || uri.getPath().contains("/deal.gs")) {
			isNative = true;
		}
		else {
			isNative = false;
		}

		// 초기에 받는 값 중에 해당 값이 Y 여야만 네이티브 단품.
		if(isNative && "Y".equalsIgnoreCase(getHomeGroupInfo().appUseUrl.applyNativeWeb))
		{
			isNative = true;
		}
		else {
			isNative = false;
		}

		if (isNative && !"Y".equalsIgnoreCase(isWebParam)) {
			// webParam이 Y 가 아니일 때 면서(비거나 N 이나 뭐 아무거나 그 밖에)
			isNative = true;
		}
		else {
			isNative = false;
		}

		return isNative;
	}

	/**
	 * TvLogin Url 여부 확인.
	 * @param url url
	 * @return boolean
	 */
	public static boolean isTvLoginPage(String url) {
		url = url.toLowerCase();
		return url.contains(URL_TV_LOGIN_LOWER_CASE);
	}

	/**
	 * 날방 url인지 여부를 반환한다.
	 *
	 * @param url url
	 * @return boolean
	 */
	public static boolean isNalbang(String url) {
		return url.contains(ServerUrls.APP.NALBANG_WEB);
	}

	/**
	 * url을 체크해서 WebActivity, NoTabWebActivity 등으로 분기
	 * native 영역에서 web activity들을 띄울 때만 사용
	 * 
	 * @param context context
	 * @param url url
	 */
	public static void goWeb(Context context, String url) {
		goWeb(context, url, null, false, false);
		if( context instanceof BaseTabMenuActivity )
		{
			((BaseTabMenuActivity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		}
	}

	/**
	 * url을 체크해서 WebActivity, NoTabWebActivity 등으로 분기
	 * native 영역에서 web activity들을 띄울 때만 사용
	 *
	 * @param context context
	 * @param url url
	 * @param wiseLog wiseLog
	 */
	public static void goWeb(Context context, String url, String wiseLog) {
		goWeb(context, url, null, false, false);
		if( context instanceof BaseTabMenuActivity )
		{
			((BaseTabMenuActivity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		}
	}

	/**
	 * url을 체크해서 WebActivity, NoTabWebActivity 등으로 분기
	 * native 영역에서 web activity들을 띄울 때만 사용
	 *
	 * @param context context
	 * @param url url
	 * @param data data
	 */
	public static void goWeb(Context context, String url, Intent data) {
		goWeb(context, url, data, false, false);
		if( context instanceof BaseTabMenuActivity )
		{
			((BaseTabMenuActivity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		}
	}

	public static void geWebDelay(Context context, String url, Intent data, boolean isLogin,
							 boolean fromTopMenu) {
		if (ClickUtils.detailViewWrok(1500)) {
			return;
		}
		goWeb(context, url, data, isLogin, fromTopMenu);
	}

	/**
	 * url을 체크해서 WebActivity, NoTabWebActivity 등으로 분기
	 * native 영역에서 web activity들을 띄울 때만 사용
	 *
	 * @param context context
	 * @param url url
	 * @param data data
	 * @param isLogin isLogin
	 * @param fromTopMenu fromTopMenu
	 */
	public static void goWeb(Context context, String url, Intent data, boolean isLogin,
			boolean fromTopMenu) {
		boolean isFromTop = fromTopMenu;
		//"external://..." 형태의 url에 대한 처리를 수행한다.
		if (isExternalUrl(url)) {
			try {
				Uri uri = Uri.parse(url);
				String host = uri.getHost();

				if (LIVETALK_HOST.equals(host)) {
					//라이브톡 화면으로 이동
					goLiveTalk(context, Uri.parse(url).getEncodedQuery());
				} else if (NALBANG_HOST.equals(host)) {
					//날방 화면으로 이동
					goNalbang(context, Uri.parse(url).getEncodedQuery());
				} else if (DIRECT_ORD_HOST.equals(host)) {
					((HomeActivity)context).visibleOrderDirectWebView(Uri.parse(url).getEncodedQuery());
				} else if (LOGIN_HOST.equals(host)) {
					//로그인 화면으로 이동
					Intent intent = new Intent(Keys.ACTION.LOGIN);
					intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.HOME);
					context.startActivity(intent);
				} else if (SETTING_HOST.equals(host)) {
					//설정 화면으로 이동
					Intent intent = new Intent(context, SettingActivity.class);
					intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.HOME);
					context.startActivity(intent);
				} else if (MODAL_WEB_HOST.equals(host)) {
					//배경이 투명한 모달 웹뷰
					Intent intent = new Intent(ACTION.TRANSPARENT_MODAL_WEB);
					intent.putExtra(Keys.INTENT.WEB_URL, uri.getQuery());
					context.startActivity(intent);
				}
				/*//모달
				else if (EXTMODAL_HOST.equals(host)) {
					String title = "";
					String targetUrl = "";
					try {
						Uri geturi = Uri.parse(url);
						title = geturi.getQueryParameter("title");
						targetUrl = geturi.getQueryParameter("url");
					} catch (Exception e) {
						Ln.e(e);
					}

					Intent intent = new Intent(context, ModalWebActivity.class);
					intent.putExtra(Keys.INTENT.WEB_TITLE, title);
					intent.putExtra(Keys.INTENT.WEB_URL, targetUrl);

					// activity 중복 실행방지
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
					((Activity)context).startActivityForResult(intent, Keys.REQCODE.MODAL);

				}else if (FULLMODAL_WEB_HOST.equals(host)) {
					Uri geturi = Uri.parse(url);
					String targetUrl = geturi.getQueryParameter("targetUrl");

					// 웹뷰를 종료함
					if ("Y".equalsIgnoreCase(uri.getQueryParameter("closeYn"))) {
						((Activity)context).finish();
					}

					Intent intent = new Intent(((Activity)context), FullModalWebActivity.class);
					intent.putExtra(Keys.INTENT.WEB_URL, targetUrl);
					((Activity)context).startActivityForResult(intent, Keys.REQCODE.MODAL);


				}else if (EXTERNAL_WEB_HOST.equals(host)) {
					if(url != null) {
						String externalUrl = url.substring(("external://"+ EXTERNAL_WEB_HOST + "?").length());
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(externalUrl));
						try {
							context.startActivity(intent);
						} catch (ActivityNotFoundException e) {
							// TODO alert 처리? (이 주소를 처리할 앱을 찾을 수 없습니다...)
							Ln.e(e);
						}
					}

				}else if (OUTSITEMODAL_WEB_HOST.equals(host)) {
					String title = "";
					String targetUrl = "";

					try {
						Uri geturi = Uri.parse(url);
						title = geturi.getQueryParameter("title");
						targetUrl = geturi.getQueryParameter("url");
					} catch (Exception e) {
						Ln.e(e);
					}

					Intent intent = new Intent(((Activity)context), OutsiteModalWebActivity.class);
					intent.putExtra(Keys.INTENT.WEB_TITLE, title);
					intent.putExtra(Keys.INTENT.WEB_URL, targetUrl);

					// activity 중복 실행방지
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

					((Activity)context).startActivityForResult(intent, Keys.REQCODE.MODAL);
				}*/
				else {
			        //maintab 이외의 경우는 모두 ACTION_VIEW를 호출하고, url이 포맷에 맞지 않는 경우는 아무 동작도 하지 않는다.(무시한다.)
			        //예) external://tel:010xxxxyyyy, external://sms:010xxxxyyyy
					int externalIndex = url.indexOf(EXTERNAL_TAG);
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(url.substring(externalIndex + EXTERNAL_TAG.length() , url.length())));
					context.startActivity(intent);
				}
			} catch (Exception e) {
				// 10/19 품질팀 요청
				// - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
				// - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
				// - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
				Ln.e(e);
			}
			return;
		}

		if (DisplayUtils.isValidString(url) && URLUtil.isValidUrl(url)) {
			Intent intent = new Intent();


			//url = "http://m.gsshop.com/index.gs?tabId=&mseq=A00054-B_TABB2-1&fromApp=Y"; //가만히
			//url = "http://m.gsshop.com/index.gs?tabId=1234&mseq=A00054-B_TABB2-1&fromApp=Y"; // 가만히
			//url = "http://m.gsshop.com/index.gs?mseq=A00054-B_TABB2-1&fromApp=Y"; // http://m.gsshop.com/index.gs 브라우징
			//url = "http://m.gsshop.com/index.gs?tabId=54mseq=A00054-B_TABB2-1&fromApp=Y"; // 54정상 이동

			//m.gsshop.com/index.gs?tabId=xxx&mseq= 처리를 위하여 tabid / mseq 뽑아 내고,
			Uri uri = Uri.parse(url);
			String mseqParam = uri.getQueryParameter(MSEQ_PARAM_KEY);
			String tabIdParam = uri.getQueryParameter(TABID_PARAM_KEY);
			String groupCdParam = uri.getQueryParameter(GROUPCODE_PARAM_KEY);
			String broadTypeParam = uri.getQueryParameter(BROADTYPE_PARAM_KEY);
			String openUrlParam = uri.getQueryParameter(OPEN_URL_PARAM_KEY);

			//AB테스트에 쓰이는 apptiInfo를 위한 변수
			String apptiInfoParam = uri.getQueryParameter(APPTIINFO_PARAM_KEY);

			if (WebUtils.isNoTabPage(url)) {
				if (useNativeProduct) {
					if (isNativeProduct(url)) {
						//native 단품
						intent.setAction(ACTION.PRODUCT_DETAIL_NATIVE_WEB);
					} else {
						//web 단품
						intent.setAction(ACTION.NO_TAB_WEB);
					}
				} else {
					intent.setAction(ACTION.NO_TAB_WEB);
				}
			} else if(WebUtils.isTvLoginPage(url)) {
				intent.setAction(ACTION.TV_MEMBERS_WEB);
			} else if (WebUtils.isMyShop(url)) {
				// 하단탭바 외에는 앱에서 마이쇼핑을 갈 일이 없는 것 같지만 일단 처리
				// 마이쇼핑을 호출할 때에는 항상 clear_top
				intent.setAction(ACTION.MY_SHOP_WEB);
				intent.putExtra(Keys.INTENT.FROM_TAB_MENU, true);
				isFromTop = true;
			}
			else if (WebUtils.isMobileLiveUrl(url)) {
				intent.setAction(ACTION.MOBILELIVE_NO_TAB_WEB);
			}

			//home 패턴과(.gsshop.com/index.gs) 동일함을 체크
			//2017년 5월 11일 isValidNumberString 제거
			else if( url != null && url.contains(ServerUrls.ROOT_URL_PATTERN) && tabIdParam != null )
			{
				//편성표 진입시 생방 or 데이타 세팅 (디폴트 생방)
				isCalledFromBanner = true;
				if (TVScheduleShopFragment.ScheduleBroadType.DATA.name().equalsIgnoreCase(broadTypeParam)) {
					scheduleBroadType = TVScheduleShopFragment.ScheduleBroadType.DATA;
				} else {
					scheduleBroadType = TVScheduleShopFragment.ScheduleBroadType.LIVE;
				}

				//HomeActivity 아닐 경우에 대한 예외처리 (HomeActivity아닌 경우에도 goweb 불러질수 잇다 )
				//내가 메인탭이냐(HomeActivity)
					// moveMaintabFromNavigationId(53) 수행
				// 	아니면 (날방위에 스만트 카트등의 homeActitity(메인탭이 없는놈 놈의 경우에 대한 처리
					//스키마 게이트웨이의 처리처럼 홈으로 이동하면서 탭이동을 수행
				if ( context instanceof HomeActivity ) {
					//탭이동 전에 mseq 브라우져에 호출
					if(apptiInfoParam != null){
						((HomeActivity) context).moveMaintabFromNavigationIdMseq_apptiInfo(mseqParam, apptiInfoParam);
					}else{
						((HomeActivity) context).moveMaintabFromNavigationIdMseq(mseqParam);
					}

					//탭이동
					((HomeActivity) context).moveMaintabFromNavigationId(tabIdParam);

					if (!TextUtils.isEmpty(groupCdParam)) {
						((HomeActivity) context).setSubTabFromGroupCd(groupCdParam);
					}
					// JB Category에서 홈이동시에 goIntent가 아닌 탭 이동만 되기 때문에 네비게이션 바를 닫아준다.
					EventBus.getDefault().post(new Events.NavigationCloseDrawerEvent());
					return;
				}
				else
				{
					if (MainApplication.isAppView) {
						//앱이 이미 실행되어 있는 경우 인트로 화면 로딩없이 바로 홈화면으로 이동
						intent = new Intent(Keys.ACTION.APP_HOME);
					} else {
						//앱이 실행중이 아닐 경우 인트로부터 정상적인 프로세스로 실행
						intent = ActivityUtils.getMainActivityIntent(context.getApplicationContext());
					}
					intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.HOME);
					intent.putExtra(Keys.INTENT.FROM_TAB_MENU, true);
					intent.putExtra(Keys.INTENT.NAVIGATION_ID, tabIdParam);
					intent.putExtra(Keys.INTENT.GROUP_CODE_ID, groupCdParam);
					intent.putExtra(Keys.INTENT.INTENT_OPEN_URL, openUrlParam);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
					return;
				}

			}
			else {
				intent.setAction(ACTION.WEB);
			}


			if (isLogin || isFromTop) {
				// login 후 다시 단품,딜 화면으로 갈 때에는 항상 clearTop
				// login이 아닐 때에는 fromTopMenu인 경우에만 clearTop
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			}
			intent.putExtra(Keys.INTENT.WEB_URL, url);
			if (data != null) {
				intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.getTabMenu(data));
				intent.putExtra(Keys.INTENT.CAN_CAUSE_REDIRECTING,
						data.getBooleanExtra(Keys.INTENT.CAN_CAUSE_REDIRECTING, false));
	            //비로그인 상태에서 스키마로 접속한 경우,
	            //스키마게이트웨이->로그인화면->웹화면 순으로 실행되는데, 실행된 웹액티비티에서 백키를 클릭할 경우 메인화면이 뜨지 않고 앱이 종료됨
	            //BACK_TO_MAIN 플래그를 세팅하여 앱이 종료되지 않고 메인이 노출되도록 함
	        	if (data.getBooleanExtra(Keys.INTENT.BACK_TO_MAIN, false)) {
	        		intent.putExtra(Keys.INTENT.BACK_TO_MAIN, true);
	        	}
				//값이 존재하면 웹뷰에서 POST 전송
				intent.putExtra(Keys.INTENT.POST_DATA, data.getStringExtra(Keys.INTENT.POST_DATA));
	        	//네이티브 단품에서 사용
				intent.putExtra(Keys.INTENT.IMAGE_URL, data.getStringExtra(Keys.INTENT.IMAGE_URL));
				intent.putExtra(Keys.INTENT.HAS_VOD, data.getStringExtra(Keys.INTENT.HAS_VOD));
			}

			try {
//				controller 들에서 Result를 설정해서 Return 하는데 ActivityForResult 로 안되어 있어 Result 설정에 의미가 없다.
//				이에 어차피 context는 HomeActivty 이기 때문에 Activity로 캐스트 하여 startActivityForResult, class cast 에 문제 있을 경우 그냥 start
				Activity activity = (Activity) context;
				activity.startActivityForResult(intent, Keys.REQCODE.WEBVIEW);
			}
			catch ( ClassCastException e ) { // activity가 아니다. HomeActivity 가 무조건 Context일 텐데.. 여길 타는 일은 없긴할 것 같다.
				Ln.e(e.getMessage());
			}
			catch ( NullPointerException e ) {
				Ln.e(e.getMessage());
			}
		}
	}

	/**
	 * 라이브톡 화면으로 이동한다.
	 *
	 * @param context 컨텍스트
	 * @param url 라이브톡주소(toapp://movetolivetalk?topapi=...{@literal &}bottomurl=...)
     */
	private static void goLiveTalk(Context context, String url) {
		Uri uri = Uri.parse(url);
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			// no tab webview
			String apiUrl = uri.getQueryParameter(TOP_API_PARAM_KEY);
			Intent intent = new Intent(Keys.ACTION.NO_TAB_WEB);
			intent.putExtra(Keys.INTENT.WEB_URL, apiUrl);
			intent.putExtra(Keys.INTENT.REFERRER, LIVETALK_HOST);
			context.startActivity(intent);
		} else {
			// 날방 webview
			Intent intent = new Intent(ACTION.LIVETALK_WEB);
			intent.putExtra(Keys.INTENT.LIVETALK_LINK, url);
			context.startActivity(intent);
		}
	}

	/**
	 * 날방 화면으로 이동한다.
	 *
	 * @param context 컨텍스트
	 * @param url 날방주소(toapp://movetonalbang?
	 *               topapi=http%3A%2F%2Fm.gsshop.com%2Fapp%2Fsection%2Fnalbang%2F672%2F19083358%3Fmseq%3D407213%26onAirYn%3DN
	 *               {@literal &}bottomurl=http%3A%2F%2Fm.gsshop.com%2Fsection%2Fnalbang%2F672%2F19083358%3Fmseq%3D407213%26onAirYn%3DN)
	 */
	private static void goNalbang(Context context, String url) {
		Uri uri = Uri.parse(url);
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			// no tab webview
			String bottomUrl = uri.getQueryParameter(BOTTOM_API_PARAM_KEY);
			Intent intent = new Intent(Keys.ACTION.NO_TAB_WEB);
			intent.putExtra(Keys.INTENT.WEB_URL, bottomUrl);
			intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.HOME);
			context.startActivity(intent);
		} else {
			// 날방 webview
			Intent intent = new Intent(ACTION.NALBANG_WEB);
			intent.putExtra(Keys.INTENT.NALBANG_LINK, url);
			context.startActivity(intent);
		}
	}

	/**
	 * url로만 마이쇼핑 여부 체크
	 * 
	 * @param url url
	 * @return boolean
	 */
	public static boolean isMyShop(String url) {
		if (TextUtils.isEmpty(url)) {
			return false;
		}
		return url.contains(ServerUrls.WEB.MY_SHOP.split("\\?")[0]);
	}

	/**
	 * url로만 주문서 페이지 여부 체크
	 *
	 * @param url 비교할 url
	 * @return 주문서 url을 포함한 경우 true 리턴
	 */
	public static boolean isOrder(String url) {
		if (TextUtils.isEmpty(url)) {
			return false;
		}
		return url.contains(ORDER_URL);
	}

	/**
	 * url로만 주문완료 페이지 여부 체크
	 *
	 * @param url 비교할 url
	 * @return 주문서 url을 포함한 경우 true 리턴
	 */
	public static boolean isOrderComplete(String url) {
		if (TextUtils.isEmpty(url)) {
			return false;
		}
		for (String order : ORDER_COMPLETE_URLS) {
			if (url.toLowerCase(Locale.getDefault()).contains(order)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * url로 스마트카드인지 여부 체크
	 * 
	 * @param url url
	 * @return 스마트카트 주소이면  true 리턴
	 */
	public static boolean isSmartCart(String url) {
		if (TextUtils.isEmpty(url)) {
			return false;
		}
		return url.contains(ServerUrls.WEB.SMART_CART.split("\\?")[0]);
	}
	
	/**
	 * "external://..." 형태의 url인지 체크한다.
	 * 
	 * @param url 링크주소
	 * @return external 형태의 url이면 true 리턴
	 */
	public static boolean isExternalUrl(String url) {
		return !TextUtils.isEmpty(url) && url.contains(EXTERNAL_TAG);
	}

	public static boolean isMobileLiveUrl(String url) {
		return !TextUtils.isEmpty(url) && url.contains(URL_MOBILE_LIVE);
	}

	/**
	 * 파라미터에서 tabYn = N 하단탭 없음, 그외에는 하단탭을 노출한다.
	 * 이미 주문서 인지 판단된 다음에 호출하도록 권장한다.
	 *
	 * 참고 : abType=“A” expId=“AB_TAB_ORDER” 사전에 약속 햇다 건들면 안되지만 있는지 판단해야 되는 버전
	 * 안드로이드 User-Aget 올려야함 ios 판단
	 *
	 * @param url
	 * @return	true 면 탭 있는놈 false
	 */
	public static boolean isOrderTabCheck(String url)
	{
		//파라미터에서 tabYn = Y 를 찾는다.
		Uri uri = null;
		String tabYn = null;
		if(url != null)
		{
			try{
				uri = Uri.parse(url);
				tabYn = uri.getQueryParameter(WebUtils.TABYN_PARAM_KEY);
				if( "N".equals(tabYn) )
				{
					return false;
				}
			}catch (Exception e)
			{

			}
		}
		return true;
	}

	private static String prevUrl = "";
	/**
	 * 네이티브 단품 호출
	 *
	 * @param context Activity Context
	 * @param url 상품주소
	 */
	public static void goNativeProduct(Activity context, String url) {
		prevUrl = prevUrl == null ? "" : prevUrl;
		// 동일한 url 중복 호출 되는 문제도 있고 리다이렉트 처럼 바로 단품으로 가는 경우도 있어 이전 URL 확인하도록 추가.
		if (ClickUtils.work(1500) && prevUrl.equals(url)) {
			return;
		}
		prevUrl = url;
		Intent intent = new Intent(ACTION.PRODUCT_DETAIL_NATIVE_WEB);
		intent.putExtra(Keys.INTENT.WEB_URL, url);
		intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.getTabMenu(context.getIntent()));
		intent.putExtra(Keys.INTENT.BACK_TO_MAIN, MainApplication.isHomeCommandExecuted ? false : true);
		context.startActivity(intent);
	}


	/**
	 * NoTabWebActivity 호출
	 *
	 * @param context Activity Context
	 * @param url 상품주소
	 */
	public static void goNoTabWebActivity(Activity context, String url) {
		Intent intent = new Intent(ACTION.NO_TAB_WEB);
		intent.putExtra(Keys.INTENT.WEB_URL, url);
		intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.getTabMenu(context.getIntent()));
		intent.putExtra(Keys.INTENT.BACK_TO_MAIN, context.getIntent().getBooleanExtra(Keys.INTENT.BACK_TO_MAIN, false));
		context.startActivity(intent);
	}
}
