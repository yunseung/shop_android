package gsshop.mobile.v2.intro;

import android.app.Activity;

import com.gsshop.mocha.pattern.chain.CommandChain;

import gsshop.mobile.v2.ApptimizeExpManager;

/**
 * 홈 이미지 캐로셀 AB테스트
 */
public class ApptimizeCarouselCommand extends ApptimizeBaseCommand {
    @Override
    public void execute(Activity activity, CommandChain chain) {
        chain.next(activity);
        apptimizeBase(activity, chain, ApptimizeExpManager.CAROUSEL);
    }
}
