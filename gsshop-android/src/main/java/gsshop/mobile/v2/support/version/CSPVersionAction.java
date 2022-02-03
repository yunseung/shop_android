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
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.util.GsShopException;
import gsshop.mobile.v2.util.StringUtils;
import roboguice.inject.ContextSingleton;
import roboguice.inject.InjectResource;

/**
 *	버전정보, 업데이트 메시지 취득 및 관리 클래스
 *
 */
@ContextSingleton
public class CSPVersionAction {

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
	public CSPVersionModel getCSPVersionInfo() throws Exception {
		CSPVersionModel cspVersionModel = getVersionInfo();

		//데이타 검증 (포맷에 맞지 않는 버전정보인 경우 익셉션을 발생시켜 다음 커맨드를 수행하도록 한다.)
		if (!StringUtils.isNumeric(cspVersionModel.AOS)) {
			//호출 함수 쪽에서 처리하도록 런타임 익셉션 발생 MSLEE 20161219
			throw new GsShopException("getCSPVersionInfo value is invalid");
		}

		return cspVersionModel;
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
	public CSPVersionModel getVersionInfo() throws Exception {
		//Content-Type=text/html인 경우
		String ret = HttpUtils.getContent(ServerUrls.WS.CSP_BREAK_URL, connectionTimeout, readTimeout);
		return new Gson().fromJson(ret, CSPVersionModel.class);
		//Content-Type=application/json인 경우
		//return restClient.getForObject(new URI(ServerUrls.WEB.LATEST_VERSION), CSPVersionModel.class);
	}
}
