package gsshop.mobile.v2;

import android.content.Intent;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import gsshop.mobile.v2.home.shop.nalbang.ShortbangEventActivity;

/**
 * ShortbangEventActivity test case
 *
 * private static final String SHORTBANG_URI = "toapp://movetoshortbangevent?topapi=http://10.52.164.237/test/app/shortbangdetail.jsp&selectedcate=00&selectedvideo=11489497";
 */
@RunWith(AndroidJUnit4.class)
public class ShortbangEventActivityTest {
    private static final String SHORTBANG_URI = "external://shortbang?toapp://movetoshortbangevent?targetApi=http://asm.gsshop.com/event/common/shortbang.jsp&selectedcate=ALL&selectedvideo=148";

    @Rule
    public ActivityTestRule<ShortbangEventActivity> mActivityRule = new ActivityTestRule<>(
            ShortbangEventActivity.class);

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testShortbangEventActivity() throws InterruptedException {


        Intent intent = new Intent();
        intent.putExtra(Keys.INTENT.SHORTBANG_EVENT_LINK, SHORTBANG_URI);
        mActivityRule.launchActivity(intent);


        // wait for activity finished
        while(!mActivityRule.getActivity().isFinishing()) {
            TimeUnit.SECONDS.sleep(1);
        }
    }

}
