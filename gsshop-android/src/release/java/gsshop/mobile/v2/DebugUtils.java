package gsshop.mobile.v2;

import android.content.Context;

import roboguice.util.Ln;

/**
 * 빌드를 Debug mode로 설정하면 디버깅 코드가 포함.
 */
public abstract class DebugUtils {
    public static void initDebug(Context context) {
        //모카에서 BaseConfig() 호출되어 4로 설정된다 다시 한번 호출해서 디버그 레벨을 6으로 조정한다.
        //릴리즈 모드에서는 Ln.e 와 Ln.a 만 찍히며, 디버그 모드는 다 찍힘
        Ln.getConfig().setLoggingLevel(6);
    }
}
