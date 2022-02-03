/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.search;

import com.gsshop.mocha.network.rest.Rest;
import com.gsshop.mocha.network.rest.RestGet;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import roboguice.inject.ContextSingleton;

/**
 * 검색/카테고리 관련 서버 연동.
 * http://mt.gsshop.com/apis/v2.6/category/listAllnew
 */
@ContextSingleton
//@Rest(resId = R.string.server_http_root, value = "/apis/v2.6")
@Rest(resId = R.string.server_http_root)
public class SearchConnector {

    /**
     * 연관 검색어 목록조회
     * @param query 파라미터 이름 바꾸지 말 것!
     */
    @RestGet(ServerUrls.REST.RELATED_KEYWORD_LIST + "?query={query}")
    public RelatedKeywordList getRelatedKeywords(String query) {
        return null;
    }

    /**
     * 인기 검색어 목록조회
     */
    @RestGet(ServerUrls.REST.POPULAR_KEYWORD_LIST)
    public PopularKeywordList getPopularKeywords() {
        return null;
    }

    /**
     * 검색 AB 값 확인
     * @return
     */
    @RestGet(ServerUrls.REST.SEARCH_AB)
    public String getSearchAB() { return null; }
}
