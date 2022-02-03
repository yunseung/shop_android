package gsshop.mobile.v2;

import android.content.Context;
import android.content.Intent;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.common.io.CharStreams;
import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.nalbang.CategoryDataHolder;
import gsshop.mobile.v2.home.shop.nalbang.ShortbangActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

/**
 * ShortbangActivity test case
 *
 * private static final String SHORTBANG_URI = "toapp://movetoshortbang?topapi=http://10.52.164.237/test/app/shortbangdetail.jsp&selectedcate=00&selectedvideo=11489497";
 */
@RunWith(AndroidJUnit4.class)
public class ShortbangActivityTest {
    private static final String SHORTBANG_URI = "external://shortbang?toapp://movetoshortbang?targetApi=http://asm.gsshop.com/app/main/shortbang/list&selectedcate=ALL&selectedvideo=148";

    @Rule
    public ActivityTestRule<ShortbangActivity> mActivityRule = new ActivityTestRule<>(
            ShortbangActivity.class);

    @Before
    public void setUp() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        String fileName = "shortbang/category.json";

        String jsonStr = CharStreams.toString(new InputStreamReader(context.getAssets().open(fileName)));

        SectionContentList[] contents = new Gson().fromJson(jsonStr, SectionContentList[].class);

        List<SectionContentList> contentList = Arrays.asList(contents);

        CategoryDataHolder.putCategoryData(contentList);
    }

    @Test
    public void testShortbangActivity() throws InterruptedException {


        Intent intent = new Intent();
        intent.putExtra(Keys.INTENT.SHORTBANG_LINK, SHORTBANG_URI);
        mActivityRule.launchActivity(intent);


        // wait for activity finished
        while(!mActivityRule.getActivity().isFinishing()) {
            TimeUnit.SECONDS.sleep(1);
        }
    }

    @Test
    public void testShortbangCategoryDialog() throws InterruptedException {


        Intent intent = new Intent();
        intent.putExtra(Keys.INTENT.SHORTBANG_LINK, SHORTBANG_URI);
        mActivityRule.launchActivity(intent);

        onView(withId(R.id.img_category)).perform(click());

        // wait for activity finished
        while(!mActivityRule.getActivity().isFinishing()) {
            TimeUnit.SECONDS.sleep(1);
        }
    }


}
