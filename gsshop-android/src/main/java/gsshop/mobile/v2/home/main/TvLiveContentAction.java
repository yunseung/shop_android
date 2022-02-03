/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.main;

import android.content.Context;

import com.google.inject.Inject;

import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.net.URISyntaxException;

import roboguice.inject.ContextSingleton;

@ContextSingleton
public class TvLiveContentAction {
    @Inject
    private TvLiveContentConnector tvLiveContentConnector;

    /**
     * TvLiveContent  조회.(앱 시작시 Home Struct 온 결과에 API 있음)
     * TV 컨텐츠의 경우 캐쉬하지 않는것이 좋을것으로 판단.
     *
     * @return TvLiveContent
     * @param context context
     * @param queryUrl queryUrl
     * @throws IOException IOException
     * @throws URISyntaxException  URISyntaxException
     * @throws RestClientException  RestClientException
     */
    public TvLiveContent getListInfo(Context context, String queryUrl) throws IOException,
            RestClientException, URISyntaxException {
        return tvLiveContentConnector.getTvLiveContent(context, queryUrl);
    }

    /**
     * TvLiveContent 캐시.
     * @param queryUrl / TvLiveContent
     */
    //public void cacheTvLiveContent(String queryUrl, TvLiveContent tvLiveContent) {
    //    PrefRepositoryNamed.save(queryUrl, tvLiveContent);
    //}

    /**
     * 로컬에 캐시된 정보를 조회한다.
     * @return
     */
    //public TvLiveContent getTvLiveContent(String queryUrl) {
    //    return PrefRepositoryNamed.get(queryUrl, TvLiveContent.class);
    //}

}
