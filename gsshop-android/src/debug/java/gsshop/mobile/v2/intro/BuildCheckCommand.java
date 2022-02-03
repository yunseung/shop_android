package gsshop.mobile.v2.intro;

import android.app.Activity;

import com.gsshop.mocha.pattern.chain.BaseCommand;
import com.gsshop.mocha.pattern.chain.CommandChain;

import gsshop.mobile.v2.util.ThreadUtils;
import roboguice.util.Ln;

/**
 * Created by azota on 2016-06-17.
 *
 * 디버그 모드에서는 앱 위변조 체크를 수행하지 않는다.
 */
public class BuildCheckCommand extends BaseCommand{
    @Override
    public void execute(Activity activity, CommandChain chain) {
        super.injectMembers(activity);

        Ln.i("BuildCheckCommand : debug");

        ThreadUtils.INSTANCE.runInBackground(() -> chain.next(activity));
    }
}
