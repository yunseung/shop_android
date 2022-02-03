/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.support.promotion;

import android.content.Context;

import com.google.inject.Inject;
import com.gsshop.mocha.network.rest.RestClient;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.util.PrefRepositoryNamed;
import gsshop.mobile.v2.util.RestClientUtils;
import roboguice.inject.ContextSingleton;
import roboguice.util.Ln;


/**
 * Promotion 정보
 */
@ContextSingleton
public class PromotionAction {

	@Inject 
	private Context context;

    @Inject
    private RestClient restClient;

    /**
     * Promotion 정보를 새로 갱신한다.
     *
     * 캐시된 Promotion 정보를 지우고 서버에서 새로 조회하여 캐싱.
     */
    public void getPromotionInfo() {
    	PromotionInfo info = null;

    	PrefRepositoryNamed.remove(context, Keys.CACHE.PROMOTION_INFO, Keys.CACHE.PROMOTION_INFO);
    	
        try {
            info = RestClientUtils.INSTANCE.get(restClient, ServerUrls.REST.PROMOTION, PromotionInfo.class);
        } catch (Exception e) {
            Ln.e(e, "Promotion 정보 조회 실패");
            return;
        }

        //info.cachePromotionInfo();
        PrefRepositoryNamed.save(context, Keys.CACHE.PROMOTION_INFO, Keys.CACHE.PROMOTION_INFO, info);
        //Ln.i("Promotion 정보 갱신됨");
    }

}
