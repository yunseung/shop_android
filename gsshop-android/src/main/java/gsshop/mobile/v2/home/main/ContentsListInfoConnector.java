/*
 * Copyright(C) 2013 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.main;

import android.content.Context;

import com.google.inject.Inject;
import com.gsshop.mocha.network.rest.Rest;
import com.gsshop.mocha.network.rest.RestClient;

import org.springframework.web.client.RestClientException;

import java.net.URISyntaxException;

import gsshop.mobile.v2.util.CookieUtils;
import roboguice.inject.ContextSingleton;

@ContextSingleton
@Rest
public class ContentsListInfoConnector {

    @Inject
    private RestClient restClient;

    /**
     *각 셀의 컨텐츠 영역을 조회해서 가져옴.
     *
     * @param params 쿼리스트링 변수
     * @param context context
     * @param queryUrl queryUrl
     * @return ContentsListInfo
     * @throws RestClientException RestClientException
     * @throws URISyntaxException URISyntaxException
     */
    public ContentsListInfo getContentsListInfo(Context context, String queryUrl, String params)
            throws RestClientException, URISyntaxException {
        // queryString을 인코딩하지 않기 위해 URI를 직접 이용함.
        String url = queryUrl + "?" + params;
        
//        ContentsListInfo aa = (ContentsListInfo)CookieUtils.syncAndcopyWebViewCookiesToRestClient(context, restClient, url, ContentsListInfo.class);
        
//        CookieUtils.syncWebViewCookiesToRestClient(context, restClient);
//        ContentsListInfo aa = restClient.getForObject(new URI(url), ContentsListInfo.class);
        //CookieUtils.copyRestClientCookiesToWebView(context, restClient);
        return (ContentsListInfo)CookieUtils.syncAndcopyWebViewCookiesToRestClient(context, restClient, url, ContentsListInfo.class);
    }
}
