package gsshop.mobile.v2.intro;

import android.app.Activity;

import com.gsshop.mocha.pattern.chain.CommandChain;

import gsshop.mobile.v2.ApptimizeExpManager;

public class ApptimizeScheduleCommand extends ApptimizeBaseCommand{

    @Override
    public void execute(Activity activity, CommandChain chain) {
        chain.next(activity);
        apptimizeBase(activity, chain, ApptimizeExpManager.SCHEDULE);
    }
}
