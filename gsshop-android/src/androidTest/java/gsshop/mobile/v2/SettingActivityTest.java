package gsshop.mobile.v2;

import android.content.Context;
import android.text.TextUtils;
import android.widget.CheckBox;

import androidx.test.espresso.web.webdriver.DriverAtoms;
import androidx.test.espresso.web.webdriver.Locator;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.google.common.truth.Truth;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import gsshop.mobile.v2.setting.SettingActivity;
import gsshop.mobile.v2.user.User;
import gsshop.mobile.v2.user.UserAction;
import roboguice.RoboGuice;
import roboguice.inject.InjectResource;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.web.sugar.Web.onWebView;
import static androidx.test.espresso.web.webdriver.DriverAtoms.clearElement;
import static androidx.test.espresso.web.webdriver.DriverAtoms.findElement;
import static androidx.test.espresso.web.webdriver.DriverAtoms.webClick;

/**
 * SettingActivity test class
 */
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SettingActivityTest {

    @Rule
    public ActivityTestRule<SettingActivity> mActivityRule = new ActivityTestRule<>(SettingActivity.class);

    @Inject
    private Context mContext;

    @Inject
    UserAction userAction;

    @InjectResource(R.string.setting_login)
    private String TEXT_LOG_IN;

    @InjectResource(R.string.setting_logout)
    private String TEXT_LOG_OUT;

    private static final String KAKAO_EMAIL = BuildConfig.KAKAO_EMAIL;
    private static final String KAKAO_PASSWORD = BuildConfig.KAKAO_PASSWORD;

    private static final String NAVER_ID = BuildConfig.NAVER_ID;
    private static final String NAVER_PW = BuildConfig.NAVER_PW;

    private static final String GS_ID = BuildConfig.GS_ID;
    private static final String GS_PW = BuildConfig.GS_PW;
    private CountDownLatch mLatch;

    @Before
    public void setUp() throws Exception {
        RoboGuice.getInjector(mActivityRule.getActivity()).injectMembers(this);
        mLatch = new CountDownLatch(1);
        TimeUnit.MILLISECONDS.sleep(800);
    }

    @After
    public void tearDown() throws Exception {
        TimeUnit.MILLISECONDS.sleep(800);
        mLatch.countDown();

    }


    @Test
    public void test00SettingActivity() throws Exception {
        while (false && !mActivityRule.getActivity().isFinishing()) {
            TimeUnit.MILLISECONDS.sleep(1600);
        }
    }

    @Test
    public void test01LoginSettingActivity() throws Exception {
        // log out
        userAction.logout(true);
        TimeUnit.MILLISECONDS.sleep(800);
        Truth.assertThat(User.getCachedUser()).isNull();

        onView(withId(R.id.tv_login_text)).perform(click());
        TimeUnit.MILLISECONDS.sleep(200);
        login();
        TimeUnit.MILLISECONDS.sleep(1600);

        onView(withText(R.string.sns_login_link)).check(matches(isDisplayed()));
    }


    @Test
    public void test02NaverUnLinkAfterLink() throws Exception {

        User user = User.getCachedUser();
        if (user != null && !TextUtils.isEmpty(user.customerNumber)) {
            if (((CheckBox) mActivityRule.getActivity().findViewById(R.id.check_sns_naver)).isChecked()) {
                // unlink
                onView(withId(R.id.button_sns_naver)).perform(click());
                TimeUnit.MILLISECONDS.sleep(400);
                onView(withText(R.string.mc_ok)).perform(click());
                TimeUnit.MILLISECONDS.sleep(1600);
                onView(withId(R.id.check_sns_naver)).check(matches(isNotChecked()));
            }
            // log out
            userAction.logout(true);
            TimeUnit.MILLISECONDS.sleep(800);
            user = User.getCachedUser();
            Truth.assertThat(user).isNull();
        }

        // naver link
        onView(withId(R.id.tv_login_text)).perform(click());
        linkNaver(true);
        TimeUnit.MILLISECONDS.sleep(3200);
        onView(withText(R.string.sns_login_btn_login)).check(matches(isDisplayed()));

        // login
        onView(withText(R.string.sns_login_btn_login)).perform(click());
        onView(withId(R.id.edit_login_id)).check(matches(isDisplayed()));
        login();
        TimeUnit.MILLISECONDS.sleep(1600);
        onView(withText(R.string.sns_login_link)).check(matches(isDisplayed()));
        onView(withId(R.id.check_sns_naver)).check(matches(isChecked()));

        // unlink
        onView(withId(R.id.button_sns_naver)).perform(click());
        TimeUnit.MILLISECONDS.sleep(400);
        onView(withText(R.string.mc_ok)).perform(click());
        TimeUnit.MILLISECONDS.sleep(1600);
        onView(withId(R.id.check_sns_naver)).check(matches(isNotChecked()));
    }

    @Test
    public void test03NaverUnLinkWithoutLink() throws Exception {

        User user = User.getCachedUser();
        if (user != null && !TextUtils.isEmpty(user.customerNumber)) {
            if (((CheckBox) mActivityRule.getActivity().findViewById(R.id.check_sns_naver)).isChecked()) {
                // unlink
                onView(withId(R.id.button_sns_naver)).perform(click());
                TimeUnit.MILLISECONDS.sleep(400);
                onView(withText(R.string.mc_ok)).perform(click());
                TimeUnit.MILLISECONDS.sleep(1600);
                onView(withId(R.id.check_sns_naver)).check(matches(isNotChecked()));
            }
            // log out
            userAction.logout(true);
            TimeUnit.MILLISECONDS.sleep(800);
            user = User.getCachedUser();
            Truth.assertThat(user).isNull();
        }

        // naver link
        onView(withId(R.id.tv_login_text)).perform(click());
        linkNaver(false);
        TimeUnit.MILLISECONDS.sleep(3200);
        onView(withText(R.string.sns_login_btn_login)).check(matches(isDisplayed()));

        // login
        onView(withText(R.string.sns_login_btn_login)).perform(click());
        onView(withId(R.id.edit_login_id)).check(matches(isDisplayed()));
        login();
        TimeUnit.MILLISECONDS.sleep(1600);
        onView(withText(R.string.sns_login_link)).check(matches(isDisplayed()));
        onView(withId(R.id.check_sns_naver)).check(matches(isChecked()));

        // unlink
        onView(withId(R.id.button_sns_naver)).perform(click());
        TimeUnit.MILLISECONDS.sleep(400);
        onView(withText(R.string.mc_ok)).perform(click());
        TimeUnit.MILLISECONDS.sleep(1600);
        onView(withId(R.id.check_sns_naver)).check(matches(isNotChecked()));
    }

    /**
     * kakao unlink
     */
    @Test
    public void test04KakaoUnLink() throws Exception {

        User user = User.getCachedUser();
        if (user != null && !TextUtils.isEmpty(user.customerNumber)) {
            if (((CheckBox) mActivityRule.getActivity().findViewById(R.id.check_sns_kakao)).isChecked()) {
                // unlink
                onView(withId(R.id.button_sns_kakao)).perform(click());
                onView(withText(R.string.mc_ok)).perform(click());
                TimeUnit.MILLISECONDS.sleep(1600);
                onView(withId(R.id.check_sns_kakao)).check(matches(isNotChecked()));
            }
            // log out
            userAction.logout(true);
            TimeUnit.MILLISECONDS.sleep(800);
            user = User.getCachedUser();
            Truth.assertThat(user).isNull();
        }

        // kakao link
        onView(withId(R.id.tv_login_text)).perform(click());
        linkKakao();
        TimeUnit.MILLISECONDS.sleep(3200);
        onView(withText(R.string.sns_login_btn_login)).check(matches(isDisplayed()));

        // login
        onView(withText(R.string.sns_login_btn_login)).perform(click());
        onView(withId(R.id.edit_login_id)).check(matches(isDisplayed()));
        login();
        TimeUnit.MILLISECONDS.sleep(1600);
        onView(withText(R.string.sns_login_link)).check(matches(isDisplayed()));
        onView(withId(R.id.check_sns_kakao)).check(matches(isChecked()));

        // unlink
        onView(withId(R.id.button_sns_kakao)).perform(click());
        TimeUnit.MILLISECONDS.sleep(400);
        onView(withText(R.string.mc_ok)).perform(click());
        TimeUnit.MILLISECONDS.sleep(1600);
        onView(withId(R.id.check_sns_kakao)).check(matches(isNotChecked()));
    }

    public void login() throws InterruptedException {
        onView(withId(R.id.edit_login_id)).perform(clearText(), typeText(GS_ID));
        onView(withId(R.id.edit_password)).perform(clearText(), typeText(GS_PW));
        onView(withId(R.id.btn_login)).perform(scrollTo(), click());
    }

    public void linkNaver(boolean link) throws InterruptedException {
        onView(withId(R.id.btn_naver_login)).perform(scrollTo(), click());

        if(link) {
            // naver login
            onWebView()
                    .withNoTimeout()
                    // Find the input element by NAME
                    .withElement(findElement(Locator.ID, "id"))
                    // Clear previous input
                    .perform(clearElement())
                    // Enter text into the input element
                    .perform(DriverAtoms.webKeys(NAVER_ID))
                    // Find the input element by NAME
                    .withElement(findElement(Locator.ID, "pw"))
                    // Clear previous input
                    .perform(clearElement())
                    // Enter text into the input element
                    .perform(DriverAtoms.webKeys(NAVER_PW));

            onWebView()
                    // Find the input element by CLASS NAME
                    .withElement(findElement(Locator.CLASS_NAME, "btn_global"))
                    .perform(webClick());

            TimeUnit.MILLISECONDS.sleep(800);

            // naver link
            onWebView()
                    // Find the input element by CLASS NAME
                    .withElement(findElement(Locator.CLASS_NAME, "oauth_accept_btn"))
                    .perform(webClick());
        }
    }

    private void linkKakao() throws InterruptedException {
        onView(withId(R.id.btn_kakao_login)).perform(scrollTo(), click());

        TimeUnit.MILLISECONDS.sleep(400);

        onWebView()
                .withNoTimeout()
                // Find the input element by NAME
                .withElement(findElement(Locator.NAME, "email"))
                // Clear previous input
                .perform(clearElement())
                // Enter text into the input element
                .perform(DriverAtoms.webKeys(KAKAO_EMAIL))
                // Find the input element by NAME
                .withElement(findElement(Locator.NAME, "password"))
                // Clear previous input
                .perform(clearElement())
                // Enter text into the input element
                .perform(DriverAtoms.webKeys(KAKAO_PASSWORD));
        onWebView()
                // Find the input element by ID
                .withElement(findElement(Locator.ID, "signin-form"))
                // Find the input element by CLASS NAME
                .withElement(findElement(Locator.CLASS_NAME, "submit"))
                .perform(webClick());

        TimeUnit.MILLISECONDS.sleep(800);

        // cancel
        onWebView()
                .withNoTimeout()
                // Find the input element by NAME
                .withElement(findElement(Locator.ID, "acceptButton"))
                // Clear previous input
                .perform(webClick());
    }
}
