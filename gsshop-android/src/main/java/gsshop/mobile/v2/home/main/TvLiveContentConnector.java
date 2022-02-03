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
public class TvLiveContentConnector {

    @Inject
    private RestClient restClient;

    public TvLiveContent getTvLiveContent(Context context, String queryUrl)
            throws RestClientException, URISyntaxException {
    	
        //CookieUtils.syncWebViewCookiesToRestClient(context, restClient);
//        TvLiveContent aa = restClient.getForObject(new URI(queryUrl), TvLiveContent.class);
        //CookieUtils.copyRestClientCookiesToWebView(context, restClient);
        
//        TvLiveContent aa  = (TvLiveContent)CookieUtils.syncAndcopyWebViewCookiesToRestClient(context, restClient, queryUrl, TvLiveContent.class);
        
        return (TvLiveContent)CookieUtils.syncAndcopyWebViewCookiesToRestClient(context, restClient, queryUrl, TvLiveContent.class);
    }
}
