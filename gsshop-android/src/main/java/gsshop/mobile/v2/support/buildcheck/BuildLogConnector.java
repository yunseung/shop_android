package gsshop.mobile.v2.support.buildcheck;

import com.gsshop.mocha.network.rest.Rest;
import com.gsshop.mocha.network.rest.RestPost;
import com.gsshop.mocha.pattern.mvc.Model;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import roboguice.inject.ContextSingleton;

/**
 * Created by azota on 2016-06-17.
 */
@ContextSingleton
@Rest(resId = R.string.server_http_root)
public class BuildLogConnector {


    @RestPost(value = ServerUrls.REST.BUILD_LOG_CHECK_API)
    public BuildLog sendBuildLog(BuildLogParam param) {
        return null;
    }

    @Model
    public static class BuildLogParam {
        /**
         * : 앱구분
         */
        public String appGbn;
        /**
         * :버전번호
         */
        public String verNo;
        /**
         * 버전명3
         */
        public String verNm;
        /**
         * :서명값
         */
        public String signVal;
        /**
         * 해쉬캅
         */
        public String hashVal;
    }
}