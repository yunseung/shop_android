package gsshop.mobile.v2.support.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import gsshop.mobile.v2.util.MigrationUtils;

/**
 * 앱 업데이트 완료 시 호출되는 BroadcastReceiver
 */
public class PackageReplacedReceiver extends BroadcastReceiver {
	
    @Override
    public void onReceive(Context context, Intent intent) {
        if (context != null && intent != null && Intent.ACTION_MY_PACKAGE_REPLACED.equals(intent.getAction())) {
            MigrationUtils.migratePref();
        }
    }
}
