/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.util;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.gsshop.mocha.network.rest.RestClient;

import org.springframework.web.client.RestClientException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.library.objectcache.CacheManager;
import gsshop.mobile.v2.library.objectcache.PutCallback;
import gsshop.mobile.v2.push.PushSettings;
import roboguice.util.Ln;

public class DataUtil {

	/**
	 * 
	 * url ? param 유의하여 한다. * param 에는 절대 ? 가 들어가면 안된다. 
	 * 
	 * @param context
	 * @param restClient
	 * @param clazz
	 * @param isCacheData
	 * @param isGtmParam
	 * @param url
	 * @param param
	 * @param sectionName
	 * @return
	 * @throws RestClientException
	 * @throws URISyntaxException
	 */
	public static Object getData(Context context, RestClient restClient, Class<?> clazz, boolean isCacheData,
			boolean isGtmParam, String url, String param, String sectionName) throws RestClientException, URISyntaxException{

		CacheManager cacheManager = null;
		boolean isCache = isCacheData;
		if(MainApplication.DISK_CACHE == null){
			isCache = false;
		}else{
			cacheManager = CacheManager.getInstance(MainApplication.DISK_CACHE);
		}
		Object data;
		if (isGtmParam && !TextUtils.isEmpty(sectionName)) {
			/*
			//AB Test 파라미터 세팅
			abTestParam = "&type=A";
			//매크로값이 섹션이름을 포함하는 경우 (abImageObj1 매크로예 : 베스트딜_A, 베스트딜_B...)
			if (MainApplication.gtmParam.get("abImageObj1") != null
					&& MainApplication.gtmParam.get("abImageObj1").contains(sectionName)) {
				abTestParam = "&type=" + MainApplication.gtmParam.get("abImageVer1");
			}
			if (MainApplication.gtmParam.get("abImageObj2") != null
					&& MainApplication.gtmParam.get("abImageObj2").contains(sectionName)) {
				abTestParam = "&type=" + MainApplication.gtmParam.get("abImageVer2");
			}
			*/
		} else if (isGtmParam && TextUtils.isEmpty(sectionName)) {
			/*
			abTestParam = "&type=A";
			*/
		}
		
		//url에 "?"가 이미 존재할 경우 예외처리 추가
		String fullUrl;
		if (url.indexOf('?') == -1) {
			fullUrl = url+ "?" + param;// + abTestParam;
		} else {
			fullUrl = url+ "&" + param;// + abTestParam;
		}

		if(TextUtils.isEmpty(param)){
			fullUrl = url;
		}

		// push 설정 확인.
		PushSettings model = PushSettings.get();
		Uri uri = Uri.parse(fullUrl);
		if(uri.getQueryParameterNames().contains("pushReceiveYn")) {
			fullUrl = StringUtils.replaceUriParameter(uri, "pushReceiveYn", model.approve? "Y": "N").toString();
		}

		//adId 설정 확인
		if(uri.getQueryParameterNames().contains("adid")) {
			fullUrl = StringUtils.replaceUriParameter(uri, "adid",  DeviceUtils.getAdvertisingId()).toString();
		}

		// brdTime 설정 확인
		try{
			//sm21에서만 동작 하도록 수정, openDate 전용 빌드일때는 N로 설정함
			String openDateMenuFlag = MainApplication.getAppContext().getString(R.string.openDateMenuFlag);
			if("Y".equals(openDateMenuFlag)) {
				//brdTime 처리 util
				String brdTime = getCacheHomeTabBrdTime();
				//brdTime 존재하면 ""포함 , 기존 URL brdTime= 없을때만 사용자가 정의한 brdTime 붙인다.
				if (!TextUtils.isEmpty(brdTime) && !fullUrl.contains(ServerUrls.REST.BRD_TIME_FORMAT)) {
					//항상 ? 포함된 상태라고 판단 navigation?version=4.0 항상 있으니까
					//fullUrl = fullUrl + "&" + brdTime;
					if (fullUrl.indexOf('?') == -1) {
						fullUrl = fullUrl+ "?" + brdTime;
					} else {
						fullUrl = fullUrl+ "&" + brdTime;
					}
				}
			}
		}catch (Exception e)
		{
			//ignore
		}

		//Ln.i("##### fullUrl : " + fullUrl);
		
		
		//캐쉬가 적용되어야 하는 경우
		//10/19 품질팀 요청
		if(isCache && cacheManager != null)
		{
			String myObject = cacheManager.getObjectString(url+param, Object.class);
			if ( myObject != null && !"".equals(myObject)) {
				data = new Gson().fromJson(myObject, clazz);
			}
			else
			{
				data = CookieUtils.syncAndcopyWebViewCookiesToRestClient(context, restClient, fullUrl, clazz);
				cacheManager.putAsync(url+param, data, CacheManager.ExpiryTimes.ONE_MINUTE.asSeconds(), false, new PutCallback() {
					@Override
					public void onSuccess() {
					}
					@Override
					public void onFailure(Exception e) {
						Ln.e(e,"DISK_CACHE","Exception : ");
					}
				});
			}
		}
		else
		{
			data = CookieUtils.syncAndcopyWebViewCookiesToRestClient(context, restClient, fullUrl, clazz);			
		}

		return data;
	}

	public static Object getData(Context context, RestClient restClient, Class<?> clazz, boolean isNewData,
			boolean isGtmParam, String url, String param) throws RestClientException, URISyntaxException{
		//sectionName값 세팅없이 호출한 경우 디폴트로 ""세팅
		return getData(context, restClient, clazz, isNewData, isGtmParam, url, param, "");
	}
	
	public static Object getData(Context context, RestClient restClient, Class<?> clazz, boolean isNewData,
			boolean isGtmParam, String url) throws RestClientException, URISyntaxException{
		return getData(context, restClient, clazz, isNewData, isGtmParam, url, "");
	}

	/**
	 * BrdTime 값 가져오기
	 * @return
	 */
	public static String getCacheHomeTabBrdTime()
	{
		return PrefRepositoryNamed.get(MainApplication.getAppContext(), Keys.CACHE.HOME_TAB_BRD_TIME, String.class);
	}

	/**
	 * copyFile
	 *
	 * @param src 원본
	 * @param dst 대상
	 */
	static public boolean copyFile(File src, File dst) throws IOException {
		FileInputStream fis = null;
		FileChannel ic = null;

		FileOutputStream fos = null;
		FileChannel oc = null;

		boolean result = true;

		try {
			fis = new FileInputStream(src);
			ic = fis.getChannel();

			fos = new FileOutputStream(dst);
			oc = fos.getChannel();

			long size = ic.size();
			ic.transferTo(0, size, oc);
		} catch (Exception e) {
			// 10/19 품질팀 요청
			// - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
			// - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
			// - 보편적 으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
			Ln.e(e);
			result = false;
		} finally {
			if(oc != null) {
				oc.close();
			}
			if(ic != null) {
				ic.close();
			}
			if(fos != null) {
				fos.close();
			}
			if(fis != null) {
				fis.close();
			}
			return result;
		}
	}
}
