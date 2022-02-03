package gsshop.mobile.v2.support.buildcheck;

import android.content.Context;
import android.content.pm.PackageInfo;

import com.google.inject.Inject;
import com.gsshop.mocha.device.AppInfo;
import com.gsshop.mocha.network.rest.RestClient;

import java.io.IOException;

import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.util.BuildCheckUtils;
import gsshop.mobile.v2.util.RestClientUtils;
import roboguice.inject.ContextSingleton;
import roboguice.util.Ln;

/**
 * Created by azota on 2016-06-17.
 */
@ContextSingleton
public class BuildLogAction {

    //GS SHOP Android (TV SHOP "02")
    private static final String APP_GUBUN = "01";

    @Inject
    private PackageInfo packageInfo;

    @Inject
    private RestClient restClient;

    public BuildLog sendBuildInfo(Context context) {

        String p1 = BuildCheckUtils.getKeystoreFingerprint(context);
        String p2 = null;

        try {
            p2 = BuildCheckUtils.getDexfileHashcode(context);
        }catch (IOException e){
            Ln.e(e);
        }
        // 둘중 하나라도 못얻어오는 경우에 진입 시킴
        if ( p1 == null || p2 == null) {
            return null;
        }

        BuildLogConnector.BuildLogParam param = new BuildLogConnector.BuildLogParam();

        param.appGbn = APP_GUBUN; //GS SHOP Android
        param.verNo = String.valueOf(AppInfo.getAppVersionCode(packageInfo)); //version code
        param.verNm = AppInfo.getAppVersionName(packageInfo); // version name
        param.signVal = p1;
        param.hashVal = p2;

        //Ln.e("param : " + param.appGbn +" "+ param.verNo +" "+ param.verNm +" "+ param.signVal +" "+ param.hashVal);

        return RestClientUtils.INSTANCE.post(restClient, param, ServerUrls.REST.BUILD_LOG_CHECK_API, BuildLog.class);
    }
}