/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.search;

import com.google.inject.Singleton;
import com.gsshop.mocha.network.rest.Rest;
import com.gsshop.mocha.network.rest.RestGet;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;

/**
 * 연관검색어 API에서 받아오기
 */
@Singleton
@Rest(resId = R.string.server_http_root)
public class RecentRecommandConnector {

    @RestGet(ServerUrls.REST.RECENT_RECOMMAND)
    public RecentRecommandList getRecentRecommand() {
        return null;
    }
}
