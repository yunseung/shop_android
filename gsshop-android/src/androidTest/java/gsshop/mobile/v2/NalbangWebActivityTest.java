package gsshop.mobile.v2;

import android.content.Intent;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import gsshop.mobile.v2.web.NalbangWebActivity;

/**
 * NalbangWebActivity test case
 */
@RunWith(AndroidJUnit4.class)
public class NalbangWebActivityTest {

//    private static final String VOD_URI = "toapp://movetonalbang?topapi=http%3A%2F%2Fm.gsshop.com%2Fapp%2Fsection%2Fnalbang%2F672%2F19083358%3Fmseq%3D407213%26onAirYn%3DN&bottomurl=http%3A%2F%2Fm.gsshop.com%2Fsection%2Fnalbang%2F672%2F19083358%3Fmseq%3D407213%26onAirYn%3DN";
    private static final String VOD_URI = "toapp://movetonalbang?topapi=http%3A%2F%2Fm.gsshop.com%2Fapp%2Fsection%2Fnalbang%2F1196%2F21600495&bottomurl=http%3A%2F%2Fm.gsshop.com%2Fsection%2Fnalbang%2F1196%2F21600495%3FonAirYn%3DN%26noHeaderFlag%3DN%26mseq%3DA00212-L_NB-2";
    private static final String LIVE_URI = "toapp://movetonalbang?topapi=http%3A%2F%2Fm.gsshop.com%2Fapp%2Fsection%2Fnalbang%2F674%2F0%3FonAirYn%3DY%26t%3D&bottomurl=http%3A%2F%2Fm.gsshop.com%2Fsection%2Fnalbang%2F674%2F0%3FonAirYn%3DY%26t%3D";

    @Rule
    public IntentsTestRule<NalbangWebActivity> mActivityRule = new IntentsTestRule<>(NalbangWebActivity.class, false, false);

    @Test
    public void testNalbangVOD() throws InterruptedException {
        Intent intent = new Intent();
        intent.putExtra(Keys.INTENT.NALBANG_LINK, VOD_URI);
        mActivityRule.launchActivity(intent);

        // wait for activity finished
        while(!mActivityRule.getActivity().isFinishing()) {
            TimeUnit.SECONDS.sleep(1);
        }
    }

    @Test
     public void testNalbangLive() throws InterruptedException {
        Intent intent = new Intent();
        intent.putExtra(Keys.INTENT.NALBANG_LINK, LIVE_URI);
        mActivityRule.launchActivity(intent);

        // wait for activity finished
        while(!mActivityRule.getActivity().isFinishing()) {
            TimeUnit.SECONDS.sleep(1);
        }
    }

    @Test
    public void testNalbangSM21Live() throws InterruptedException {
        String url = LIVE_URI.replace("m.gsshop.com", "asm.gsshop.com");
        Intent intent = new Intent();
        intent.putExtra(Keys.INTENT.NALBANG_LINK, url);
        mActivityRule.launchActivity(intent);

        // wait for activity finished
        while(!mActivityRule.getActivity().isFinishing()) {
            TimeUnit.SECONDS.sleep(1);
        }

    }
}
