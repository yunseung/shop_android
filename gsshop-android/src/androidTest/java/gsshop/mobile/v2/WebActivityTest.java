package gsshop.mobile.v2;

import android.content.Intent;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import gsshop.mobile.v2.web.WebActivity;

/**
 * WebActivity test case
 */
@RunWith(AndroidJUnit4.class)
public class WebActivityTest {

    @Rule
    public IntentsTestRule<WebActivity> mActivityRule = new IntentsTestRule<>(WebActivity.class, false, false);

    /**
     * exo player 테서트
     */
    @Test
    public void testExoPlayerVOD() throws InterruptedException {
        String url = "toapp://vod?url=http://mobilevod.gsshop.com/beauty/5416.mp4&prdid=329323";
        Intent intent = new Intent();
        intent.putExtra(Keys.INTENT.WEB_URL, url);
        mActivityRule.launchActivity(intent);

        // wait for activity finished
        while(!mActivityRule.getActivity().isFinishing()) {
            TimeUnit.SECONDS.sleep(1);
        }
    }













































    @Test
    public void testExoPlayerStreaming() throws InterruptedException {
        String url = "toapp://vod?url=http://livem.gsshop.com:80/gsshop/_definst_/gsshop.sdp/playlist.m3u8&prdid=329323";
        Intent intent = new Intent();
        intent.putExtra(Keys.INTENT.WEB_URL, url);
        mActivityRule.launchActivity(intent);

        // wait for activity finished
        while(!mActivityRule.getActivity().isFinishing()) {
            TimeUnit.SECONDS.sleep(1);
        }
    }
    /**
     * bright cove video id 테스트
     */
    @Test
    public void testBrightCoveStartTime() throws InterruptedException {
        String url = "toapp://vod?videoid=5987457795001&prdid=329323&starttime=20400";
        Intent intent = new Intent();
        intent.putExtra(Keys.INTENT.WEB_URL, url);
        mActivityRule.launchActivity(intent);

        // wait for activity finished
        while(!mActivityRule.getActivity().isFinishing()) {
            TimeUnit.SECONDS.sleep(1);
        }
    }

    @Test
    public void testBrightCoveNoStartTime() throws InterruptedException {
        String url = "toapp://vod?videoid=5987457795001&prdid=329323";
        Intent intent = new Intent();
        intent.putExtra(Keys.INTENT.WEB_URL, url);
        mActivityRule.launchActivity(intent);

        // wait for activity finished
        while(!mActivityRule.getActivity().isFinishing()) {
            TimeUnit.SECONDS.sleep(1);
        }
    }


    /**
     * 이벤트 대용량 동영상 업로드
     *
     * @throws InterruptedException
     */
    @Test
    public void testMovieUpload() throws InterruptedException {
        //String url = "http://tevent.gsshop.com/test/test_event2.jsp";
        String url = "http://asm.gsshop.com/event/2016_05/campaign_shoppinghost.jsp";
        Intent intent = new Intent();
        intent.putExtra(Keys.INTENT.WEB_URL, url);
        mActivityRule.launchActivity(intent);

        // wait for activity finished
        while(!mActivityRule.getActivity().isFinishing()) {
            TimeUnit.SECONDS.sleep(1);
        }
    }

    /**
     * euc-kr 인코딩 검색어.
     *
     * @throws InterruptedException
     */
    @Test
    public void testWebMarketing() throws InterruptedException {
        String url = "http://asm.gsshop.com/search/searchSect.gs?ab=b&tq=%C8%C4%B5%E5%C7%CA%C5%CD&utm_medium=paid_search&utm_source=daum&utm_campaign=SA_Household_Appliances&BSCPN=PFCT&DMKW=%ED%9B%84%EB%93%9C%ED%95%84%ED%84%B0&DMSKW=%ED%9B%84%EB%93%9C%ED%95%84%ED%84%B0%EA%B5%90%EC%B2%B4&DMCOL=MOBILESA&fromWith=YS";
        Intent intent = new Intent();
        intent.putExtra(Keys.INTENT.WEB_URL, url);
        mActivityRule.launchActivity(intent);

        // wait for activity finished
        while(!mActivityRule.getActivity().isFinishing()) {
            TimeUnit.SECONDS.sleep(1);
        }
    }

    /**
     * video player vod 테스트.
     * play & pause : 가로/세로 모드로 화면 전환이 일어나면 플레이 위치 복원.
     *
     *
     * @throws InterruptedException
     */
    @Test
    public void testVideoPlayerVOD() throws InterruptedException {
        String url = "toapp://dealvod?url=http://image.gsshop.com/mi09/deal/vod/20160212145924919050.mp4&targeturl=http://m.gsshop.com/deal/deal.gs?dealno=20258744&mseq=a00054-l_d-6&fromapp=y";
         url = "toapp://dealvod?url=http://mobilevod.gsshop.com/beauty/cafeshowme/580885ca-111e-4511-9262-c3e7581839d2.mp4&targeturl=http://m.gsshop.com/deal/deal.gs?dealno=20258744&mseq=a00054-l_d-6&fromapp=y";
         url = "toapp://dealvod?url=http://mobilevod.gsshop.com/beauty/cafeshowme/580885ca-111e-4511-9262-c3e7581839d2.mp4&targeturl=http://m.gsshop.com/deal/deal.gs?dealno=20258744&mseq=a00054-l_d-6&fromapp=y";
         url = "toapp://vod?url=http://livem.gsshop.com:80/gsshop/_definst_/gsshop.sdp/playlist.m3u8&prdid=329323";
         url = "toapp://vod?url=http://livem.gsshop.com:80/gsshop/_definst_/gsshop.sdp/playlist.m3u8&prdid=329323";
        Intent intent = new Intent();
        intent.putExtra(Keys.INTENT.WEB_URL, url);
        mActivityRule.launchActivity(intent);

        // wait for activity finished
        while(!mActivityRule.getActivity().isFinishing()) {
            TimeUnit.SECONDS.sleep(1);
        }
    }



    /**
     * video player streaming 테스트.
     * play & pause : 가로/세로 모드로 화면 전환이 일어나면 플레이 위치 복원.
     *
     *
     * @throws InterruptedException
     */
    @Test
    public void testVideoPlayerStreaming() throws InterruptedException {
//        String url = "toapp://dealvod?url=http://livem.gsshop.com/gsshop/_definst_/gsshop.sdp/playlist.m3u8&targeturl=http://m.gsshop.com/deal/deal.gs?dealno=20258744&mseq=a00054-l_d-6&fromapp=y";
        String url = "toapp://livestreaming?url=http://livem.gsshop.com/gsshop/_definst_/gsshop.sdp/playlist.m3u8&targeturl=http://m.gsshop.com/deal/deal.gs?dealno=20258744&mseq=a00054-l_d-6&fromapp=y";
        Intent intent = new Intent();
        intent.putExtra(Keys.INTENT.WEB_URL, url);
        mActivityRule.launchActivity(intent);

        // wait for activity finished
        while(!mActivityRule.getActivity().isFinishing()) {
            TimeUnit.SECONDS.sleep(1);
        }
    }

    /**
     * video player streaming 테스트.
     * play & pause : 가로/세로 모드로 화면 전환이 일어나면 플레이 위치 복원.
     *
     *
     * @throws InterruptedException
     */
    @Test
    public void testVideoPlayerStreamingRstp() throws InterruptedException {
        // rtsp
        String url = "toapp://livestreaming?type=PRD_PLAY&url=rtsp://livem.gsshop.com:554/gsshop/_definst_/gsshop.sdp&targeturl=";

        // hls
        url = "toapp://livestreaming?type=PRD_PLAY&url=http://livem.gsshop.com/gsshop/_definst_/gsshop.sdp/playlist.m3u8&targeturl=";
        url = "toapp://livestreaming?url=http://livem.gsshop.com/gsshop/_definst_/gsshop.sdp/playlist.m3u8&targeturl=http://m.gsshop.com/main/tv/tvLiveMain?mseq=A00054-C-IMG";

        Intent intent = new Intent();
        intent.putExtra(Keys.INTENT.WEB_URL, url);
        mActivityRule.launchActivity(intent);

        // wait for activity finished
        while(!mActivityRule.getActivity().isFinishing()) {
            TimeUnit.SECONDS.sleep(1);
        }
    }

    /**
     * web video 전체화면 테스트
     *
     *
     * @throws InterruptedException
     */
    @Test
    public void testFullScreenVideoWebPlayer() throws InterruptedException {
        String url = "http://10.52.214.125/mc/n_prd/n_ui_deal_vod.html";

        Intent intent = new Intent();
        intent.putExtra(Keys.INTENT.WEB_URL, url);
        mActivityRule.launchActivity(intent);

        // wait for activity finished
        while(!mActivityRule.getActivity().isFinishing()) {
            TimeUnit.SECONDS.sleep(1);
        }
    }

    /**
     * 숏방 동영상.
     *
     *
     * @throws InterruptedException
     */
    @Test
    public void testShortbangVod() throws InterruptedException {
        String url = "toapp://dealvod?url=http://mobilevod.gsshop.com/livedeal/20151127092816522102.mp4&targeturl=http://m.gsshop.com/deal/deal.gs?dealno=20258744";

        Intent intent = new Intent();
        intent.putExtra(Keys.INTENT.WEB_URL, url);
        mActivityRule.launchActivity(intent);

        // wait for activity finished
        while(!mActivityRule.getActivity().isFinishing()) {
            TimeUnit.SECONDS.sleep(1);
        }
    }

    /**
     * 웹뷰에서 SNS Dialog 띄우기 (날방, 라이브톡, 쇼미카페 등)
     *
     *
     * @throws InterruptedException
     */
    @Test
    public void testSnsDialogShow() throws InterruptedException {
        String url = "toapp://snsshow";

        Intent intent = new Intent();
        intent.putExtra(Keys.INTENT.WEB_URL, url);
        mActivityRule.launchActivity(intent);

        // wait for activity finished
        while(!mActivityRule.getActivity().isFinishing()) {
            TimeUnit.SECONDS.sleep(1);
        }
    }

}
