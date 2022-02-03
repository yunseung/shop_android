/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.search;

import android.content.Context;

import com.google.inject.Inject;
import com.gsshop.mocha.network.rest.RestClient;

import java.util.ArrayList;

import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.util.RestClientUtils;
import roboguice.inject.ContextSingleton;
import roboguice.util.Ln;


/**
 * 추천연관검색어 API받아오기
 */
@ContextSingleton
public class RecentRecommandAction {

	@Inject 
	private Context context;

    @Inject
    private RestClient restClient;

    @Inject
    private RecentRecommandConnector recentRecommandConnector;

    //aspectj 제거로 커넥터를 사용 못하기 때문에 아래함수로 대체
    public ArrayList<RecentRecommandInfo> getRecentRecommand() {
       RecentRecommandList temp = null;
        try {
            temp = recentRecommandConnector.getRecentRecommand();
            if(temp != null && temp.list != null){
                return temp.list;
            }
        } catch (Exception e) {
            Ln.e(e, "추천연관검색어 정보 조회 실패");
            return null;
        }
        return null;
    }

    /**
     * 추천연관검색어를 조회한다.
     *
     * @return RecentRecommandList
     */
    public RecentRecommandList getRecentRecommandV2() {
        return RestClientUtils.INSTANCE.get(restClient, ServerUrls.REST.RECENT_RECOMMAND, RecentRecommandList.class);
    }
}
