/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.support.version;

import android.content.Context;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.network.util.HttpUtils;

import gsshop.mobile.v2.R;
import roboguice.inject.ContextSingleton;
import roboguice.inject.InjectResource;

/**
 *	버전정보, 업데이트 메시지 취득 및 관리 클래스
 *
 */
@ContextSingleton
public class ApptimizeAction {

	@InjectResource(R.integer.update_check_connection_timeout)
	private int connectionTimeout;

	@InjectResource(R.integer.update_check_read_timeout)
	private int readTimeout;

	@Inject
	private Context context;

	@Inject
	private RestClient restClient;

	/**
	 * @return VersionAction
	 * @throws Exception 이걸 받아서 처리 하시오 ~
     */
	public ApptimizeExpModel getApptiInfo() throws Exception {
		ApptimizeExpModel apptimizeExpModel = getApptiInfoAPI();

		return apptimizeExpModel;
	}

	/**
	 *
	 * @return 버전 모델
	 * @throws Exception 익셉션 발생시 이걸 받아서 처리 하시오
     */
	private ApptimizeExpModel getApptiInfoAPI() throws Exception {
		//Content-Type=text/html인 경우
		String ret = HttpUtils.getContent("http://image.gsshop.com/pingpong/csp/apptimize/run.html", connectionTimeout, readTimeout);
		return new Gson().fromJson(ret, ApptimizeExpModel.class);
		//Content-Type=application/json인 경우
		//return restClient.getForObject(new URI(ServerUrls.WEB.LATEST_VERSION), CSPVersionModel.class);
	}
}
