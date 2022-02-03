package gsshop.mobile.v2;

import android.content.Intent;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import gsshop.mobile.v2.review.ReviewActivity;

/**
 * LoginActivity test case
 */
@RunWith(AndroidJUnit4.class)
public class ReviewActivityTest {
    @Rule
    public IntentsTestRule<ReviewActivity> mActivityRule = new IntentsTestRule<>(ReviewActivity.class, false, false);

    @Test
    public void testSave() throws Exception {

    }

    @Test
    public void testEdit() throws InterruptedException {
        String url = "messageId=91284401&inflowPath=mgntModify&prdCd=3033841&format=json&save_root=mobilePrd&order_num=&lineNum=&modify=&prdid=3033841";
        Intent intent = new Intent();
        intent.putExtra(Keys.INTENT.REVIEW_QUERY_STRING, url);
        mActivityRule.launchActivity(intent);

        // wait for activity finished
        while(!mActivityRule.getActivity().isFinishing()) {
            TimeUnit.SECONDS.sleep(1);
        }
    }

}
