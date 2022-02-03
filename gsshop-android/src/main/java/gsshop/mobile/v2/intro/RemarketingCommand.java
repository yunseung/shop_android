/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.intro;

import android.app.Activity;

import com.google.ads.conversiontracking.AdWordsConversionReporter;
import com.google.ads.conversiontracking.AdWordsRemarketingReporter;
import com.gsshop.mocha.pattern.chain.BaseCommand;
import com.gsshop.mocha.pattern.chain.CommandChain;

import java.util.HashMap;
import java.util.Map;

import gsshop.mobile.v2.util.LogUtils;
import gsshop.mobile.v2.util.ThreadUtils;
import roboguice.util.Ln;

/**
 * 리마케팅 앱광고
 *  
 */
public class RemarketingCommand extends BaseCommand {
    /**
	 * RemarketingCommand execute
	 * @param activity activity
	 * @param chain chain
     */
    @Override
    public void execute(Activity activity, CommandChain chain) {
    	long startTime = System.currentTimeMillis();

		injectMembers(activity);

		chain.next(activity);

		ThreadUtils.INSTANCE.runInBackground(() -> {
			try {
				//v1.1.0
				//GoogleConversionPing.recordRemarketingPing(activity.getApplicationContext(), "950877667", "nVd4CN3-hwcQ4_u0xQM", "<IntroActivity>", null);
				//v2.1
				/*
				param1 : the application context from which you are tracking a conversion event
				param2 : an ID that identifies your conversion
				param3 : an alphanumeric label that identifies your conversion
				param4 : the value of your conversion (must be specified in the currency of your AdWords account)
						 a boolean to indicate whether the conversion should fire only once or should fire multiple times.
						 We pre-populate the appropriate value for you and strongly recommend leaving the default value unchanged.
				*/
				/*AdWordsConversionReporter.reportWithConversionId(activity.getApplicationContext(),
						"950877667", "nVd4CN3-hwcQ4_u0xQM", "0", true);*/

				//v2.2
				//v2.1에서 사용한 초기화 코드 유지
				AdWordsConversionReporter.reportWithConversionId(activity.getApplicationContext(),
						"950877667", "nVd4CN3-hwcQ4_u0xQM", "0", true);

				//리마케팅을 위한 custom parameter 추가
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("action_type", "open");
				AdWordsRemarketingReporter.reportWithConversionId(
						activity.getApplicationContext(),
						"950877667",
						params);

				//앱 오픈 트래킹을 위한 코드 추가
				//1.최초 실행을 위한 코드
				AdWordsConversionReporter.reportWithConversionId(activity.getApplicationContext(),
						"954675945", "_1SACPSWwFwQ6eWcxwM", "1.00", false);
				//2.반복 실행을 위한 코드
				AdWordsConversionReporter.reportWithConversionId(activity.getApplicationContext(),
						"954675945", "gf2MCPSs1FwQ6eWcxwM", "1.00", true);

			} catch (Exception e) {
				Ln.e(e);
			}
			LogUtils.printExeTime("RemarketingCommand", startTime);
		});
    }
}
