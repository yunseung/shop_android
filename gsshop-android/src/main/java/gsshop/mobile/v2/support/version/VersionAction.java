/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.support.version;

import android.content.Context;
import android.content.pm.PackageInfo;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.gsshop.mocha.device.AppInfo;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.network.util.HttpUtils;

import java.net.URI;

import gsshop.mobile.v2.Keys.CACHE;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.home.HomeGroupInfoAction;
import gsshop.mobile.v2.util.GsShopException;
import gsshop.mobile.v2.util.PrefRepositoryNamed;
import gsshop.mobile.v2.util.StringUtils;
import roboguice.inject.ContextSingleton;
import roboguice.inject.InjectResource;

/**
 *	버전정보, 업데이트 메시지 취득 및 관리 클래스
 *
 */
@ContextSingleton
public class VersionAction {

	@InjectResource(R.integer.update_check_connection_timeout)
	private int connectionTimeout;

	@InjectResource(R.integer.update_check_read_timeout)
	private int readTimeout;

	@Inject
	private Context context;

	@Inject
	private RestClient restClient;

	/**
	 * 현재앱버전 및 최신버전을 새로 조회한다.(앱 시작시 사용됨)
	 * 과장님 확인 예정
	 * @return VersionAction
	 * @throws Exception 이걸 받아서 처리 하시오 ~
     */
	public AppVersionModel getAppVersionInfo() throws Exception {
		AppVersionModel appVersionModel = getVersionInfo();

		appVersionModel.currentVersionName = AppInfo.getAppVersionName(AppInfo.getPackageInfo(context));

		//데이타 검증 (포맷에 맞지 않는 버전정보인 경우 익셉션을 발생시켜 다음 커맨드를 수행하도록 한다.)
		if (!StringUtils.isNumeric(appVersionModel.choice)
				|| !StringUtils.isNumeric(appVersionModel.force)
				|| !StringUtils.isNumeric(appVersionModel.vername.replaceAll("[.]", ""))
				|| !StringUtils.isNumeric(appVersionModel.vercode)) {
			//호출 함수 쪽에서 처리하도록 런타임 익셉션 발생 MSLEE 20161219
			throw new GsShopException("getVersionInfo value is invalid");
		}

		return appVersionModel;
	}

	/**
	 * 버전정보를 프리퍼런스에 저장한다.
	 *
	 * @param appVersionModel AppVersionModel
	 */
	public void cacheAppVersionInfo(AppVersionModel appVersionModel) {
		// NOTE : 앱이 백그라운드로 이동 후에 OS필요에 따라
		// 강제로 메모리 해제될 수 있으므로 MemoryRepository를 통해 메모리에 캐시하지 않고
		// PrefRepository를 통해 설정파일에 저장하였다.
		PrefRepositoryNamed.save(context, CACHE.APP_VERSION, CACHE.APP_VERSION, appVersionModel);
	}

	/**
	 * 로컬에 캐시된 버전정보를 조회한다.
	 *
	 * @return AppVersionModel
	 */
	public AppVersionModel getCachedAppVersionInfo() {
		return PrefRepositoryNamed.get(context, CACHE.APP_VERSION, CACHE.APP_VERSION, AppVersionModel.class);
	}

	/**
	 * 서버에 설정된 버전정보를 취득한다.
	 *
	 * 참고:CERTIFY_GSSHOP 와이파이에 접속된 경우 RestClientException을 throw한다.
	 * (no suitable HttpMessageConverter)
	 *
	 * @return 버전 모델
	 * @throws Exception 익셉션 발생시 이걸 받아서 처리 하시오
     */
	public AppVersionModel getVersionInfo() throws Exception {

		int appver = 0;
		try{
			PackageInfo pi = AppInfo.getPackageInfo(MainApplication.getAppContext());
			appver=AppInfo.getAppVersionCode(pi);

		}catch (Exception e)
		{
			;
		}

		//Content-Type=text/html인 경우
		String ret = HttpUtils.getContent(ServerUrls.WEB.LATEST_VERSION + "?" + HomeGroupInfoAction.APPVER + "=" + appver , connectionTimeout, readTimeout);
		return new Gson().fromJson(ret, AppVersionModel.class);
		//Content-Type=application/json인 경우
		//return restClient.getForObject(new URI(ServerUrls.WEB.LATEST_VERSION), AppVersionModel.class);
	}

	/**
	 * 서버가 현재 공사중인지 여부를 확인한다.
	 *
	 * @return "Y" or "N" or html code(CERTIFY_GSSHOP 와이파이에 접속된 경우)
	 * @throws Exception Exception
	 */
	public String getOopsHtml() throws Exception {
		URI	uri = new URI(ServerUrls.OOPS_URL);
		return restClient.getForObject(uri, String.class);
	}

	/**
	 * 서버가 현재 공사중인지 여부를 확인 할수 없을때, cdn의 공사중여부를 확인한다
	 * @return
	 * @throws Exception
	 */
	public String getAppErrorHtml() throws Exception {
		URI uri = new URI(ServerUrls.APPERROR_OOPS_URL);
		return restClient.getForObject(uri, String.class);
	}
}
