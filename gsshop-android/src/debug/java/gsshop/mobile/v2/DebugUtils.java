package gsshop.mobile.v2;

import android.content.Context;
import android.os.Build;
import android.webkit.WebView;

import com.facebook.stetho.Stetho;
import com.nhn.android.naverlogin.OAuthLoginDefine;

import roboguice.util.Ln;

/**
 * 빌드를 Debug mode로 설정하면 디버깅 코드가 포함.
 */
public abstract class DebugUtils {
    public static void initDebug(Context context) {
        Stetho.initializeWithDefaults(context);
        OAuthLoginDefine.DEVELOPER_VERSION = true;

        //PC 크롬브라우저에서 앱의 웹뷰를 디버깅할수 있도록 함
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                WebView.setWebContentsDebuggingEnabled(true);
            }
            catch (NoClassDefFoundError e) {
                Ln.e(e.getMessage());
            }
        }
    }
}
