package gsshop.mobile.v2;

import android.content.Intent;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import gsshop.mobile.v2.web.LiveTalkWebActivity;

/**
 * LiveTalkWebActivity test case
 */
@RunWith(AndroidJUnit4.class)
public class LiveTalkWebActivityTest {

    private static final String LIVE_URI = "toapp://movetolivetalk?topapi=http://m.gsshop.com/app/section/nalbang/713/19094138?onAirYn=N";
    @Rule
    public IntentsTestRule<LiveTalkWebActivity> mActivityRule = new IntentsTestRule<>(LiveTalkWebActivity.class, false, false);

    @Test
    public void testLiveTalk() throws InterruptedException {
        Intent intent = new Intent();
        intent.putExtra(Keys.INTENT.LIVETALK_LINK, LIVE_URI);
        mActivityRule.launchActivity(intent);

        // wait for activity finished
        while(!mActivityRule.getActivity().isFinishing()) {
            TimeUnit.SECONDS.sleep(1);
        }
    }
}
