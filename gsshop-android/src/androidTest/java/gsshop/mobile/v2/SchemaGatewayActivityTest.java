package gsshop.mobile.v2;

import android.content.Intent;
import android.net.Uri;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import gsshop.mobile.v2.support.scheme.SchemeGatewayActivity;
import gsshop.mobile.v2.user.LoginOption;
import gsshop.mobile.v2.user.TokenCredentialsNew2;

/**
 * SchemeGatewayActivity test case
 */
@RunWith(AndroidJUnit4.class)
public class SchemaGatewayActivityTest {
    @Rule
    public IntentsTestRule<SchemeGatewayActivity> mActivityRule = new IntentsTestRule<>(SchemeGatewayActivity.class, false, false);

    TokenCredentialsNew2 secureToken = TokenCredentialsNew2.get();

    @Before
    public void setUp() throws Exception {
        /*secureToken.authToken="5c423d3437f3e3d585ee54543f85698e45db8851ae9d80dcfbfbc34d18d0b6400b8874758138132b9754119b3c014662322186037a58c8ed0032f33527c1fba42a7aac6590b5234f1906badc38c2d1711c2c5164a48fa82413fc0327610b35b7";
        secureToken.deviceId="3f7cb967-becf-3dae-8306-dc45cd8a5e73";
        secureToken.loginId="9093e0d9a4077c4aa58ad2a389a01cd0822f6a8b25b026504d2d900bc4728e75";
        secureToken.seriesKey="ed0dcce90c0023aefe75e8cc22f389fa210322fcf41c64dba0734e925b050650dd41c45c500a84782f46abde295d9e592eb76429c06432d892f8d942db7bd0a5a384e30ec27d69949fcb83a9e684ca75d52ae33c0dff4a22bf86ac4ebbbcd346";
        PrefRepositoryNamed.save(Keys.PREF.TOKEN_CREDENTIALS_NEW, secureToken);*/
        new LoginOption().save();

    }

    /**
     * 2017-08-24
     * 웹뷰 geolocation
     *
     * @throws InterruptedException
     */
    @Test
    public void testSchemaGatewayGeoLocation() throws InterruptedException {
//        String url = "gsshopmobile://home?https://10.52.221.226/map";
        String url = "gsshopmobile://home?https://developer.mozilla.org/en-US/docs/Web/API/Geolocation/Using_geolocation";

        Intent intent = new Intent();
        intent.setData(Uri.parse(url));
        mActivityRule.launchActivity(intent);

        // wait for activity finished
        while (true) {
            TimeUnit.SECONDS.sleep(1);
        }
    }
    /**
     * 2017-05-18
     * <p>
     * tv 편셩표 매장 바로가기.
     *
     * @throws InterruptedException
     */
    @Test
    public void testSchemaGatewayGoTVScheduleShop() throws InterruptedException {
        String url = "gsshopmobile://home?http://m.gsshop.com/index.gs?tabId=323";
//        url = "gsshopmobile://home?http://m.gsshop.com/index.gs?tabId=53";
        Intent intent = new Intent();
        intent.setData(Uri.parse(url));
        mActivityRule.launchActivity(intent);

        // wait for activity finished
        while (true) {
            TimeUnit.SECONDS.sleep(1);
        }
    }

    /**
     * 2016-05-04
     * <p>
     * 페이스북 연동 테스트
     *
     * @throws InterruptedException
     */
    @Test
    public void testSchemaGatewayFacebook() throws InterruptedException {
        String url = "gsshopmobile://home?http://m.gsshop.com/jsp/jseis_withLGeshop.jsp?media=LFM&ecpid=20544693&utm_source=facebookdpa&utm_medium=banner&utm_campaign=retargeting";
        Intent intent = new Intent();
        intent.setData(Uri.parse(url));
        mActivityRule.launchActivity(intent);

        // wait for activity finished
        while (true) {
            TimeUnit.SECONDS.sleep(1);
        }
    }

    /**
     * 2016-02-02
     * <p>
     * 다음 제휴. euc-kr 인코딩 처리 오류 발생
     *
     * @throws InterruptedException
     */
    @Test
    public void testSchemaDaumGatewayMarketing() throws InterruptedException {
        String url = "gsshopmobile://home?http://asm.gsshop.com/search/searchSect.gs?ab=b&tq=%C8%C4%B5%E5%C7%CA%C5%CD&utm_medium=paid_search&utm_source=daum&utm_campaign=SA_Household_Appliances&BSCPN=PFCT&DMKW=%ED%9B%84%EB%93%9C%ED%95%84%ED%84%B0&DMSKW=%ED%9B%84%EB%93%9C%ED%95%84%ED%84%B0%EA%B5%90%EC%B2%B4&DMCOL=MOBILESA&fromWith=YS";
        Intent intent = new Intent();
        intent.setData(Uri.parse(url));
        mActivityRule.launchActivity(intent);

        // wait for activity finished
        while (true) {
            TimeUnit.SECONDS.sleep(1);
        }
    }

    /**
     * 2017-03-13
     * <p>
     * 네이버 제휴. euc-kr 인코딩 처리 오류 발생
     *
     * @throws InterruptedException
     */
    @Test
    public void testSchemaNaverGatewayMarketing() throws InterruptedException {
        String url = "gsshopmobile://home?http://m.gsshop.com/jsp/jseis_with301Gate.jsp?media=LEZ&gourl=http://m.gsshop.com/search/searchSect.gs?tq=%BF%F8%B4%F5%BA%EA%B6%F3&utm_medium=paid_search&utm_source=naver&utm_campaign=SA_Underwear&mac_ad_key=25301316";
//        url = "gsshopmobile://home?http://m.gsshop.com/jsp/jseis_with301Gate.jsp?media=LEZ&gourl=http%3A%2F%2Fm.gsshop.com%2Fsearch%2FsearchSect.gs%3Ftq%3D%25BF%25F8%25B4%25F5%25BA%25EA%25B6%25F3%26utm_medium%3Dpaid_search%26utm_source%3Dnaver%26utm_campaign%3DSA_Underwear%26mac_ad_key%3D25301316";
        Intent intent = new Intent();
        intent.setData(Uri.parse(url));
        mActivityRule.launchActivity(intent);

        // wait for activity finished
        while (true) {
            TimeUnit.SECONDS.sleep(1);
        }
    }

    @Test
    public void testSchemaGatewayKakao() throws InterruptedException {
        String url = "gsshopmobile://home?https://auth.kakao.com/kakao_accounts/view/login?continue=https%3A%2F%2Fkauth.kakao.com%2Foauth%2Fauthorize%3Fclient_id%3D7fc439c7837247f086ea796d87369231%26approval_type%3Dindividual%26redirect_uri%3Dkakao7fc439c7837247f086ea796d87369231%253A%252F%252Foauth%26response_type%3Dcode";
        Intent intent = new Intent();
        intent.setData(Uri.parse(url));
        mActivityRule.launchActivity(intent);

        // wait for activity finished
        while (true) {
            TimeUnit.SECONDS.sleep(1);
        }
    }

    /**
     * 2016-02-18
     * <p>
     * kakaolink로 라이브톡 공유하면 글이 등록 되지 않는 현상 발생
     *
     * @throws InterruptedException
     */
    @Test
    public void testSchemaGatewayKakaoLinkLiveTalk() throws InterruptedException {
        String url = "kakao891cea206a1ce9af85341bb32ca1e37f://kakaolink?url=gsshopmobile://home?http://m.gsshop.com/section/livetalk/sns/201602181340%3Futm_source%3Dkakao%26utm_medium%3Dsns%26utm_campaign%3Dsharekakao";
        Intent intent = new Intent();
        intent.setData(Uri.parse(url));
        mActivityRule.launchActivity(intent);

        // wait for activity finished
        while (true) {
            TimeUnit.SECONDS.sleep(1);
        }
    }

    /**
     * 2016-03-11
     * <p>
     * 날방 공유 백 버튼 오류 발생.
     *
     * @throws InterruptedException
     */
    @Test
    public void testSchemaGatewayNalbang() throws InterruptedException {
        String url = "kakao891cea206a1ce9af85341bb32ca1e37f://kakaolink?url=gsshopmobile://home?http://m.gsshop.com/section/nalbang/sns//852/19471598?utm_source=kakao&utm_medium=sns&utm_campaign=sharekakao";
        Intent intent = new Intent();
        intent.setData(Uri.parse(url));
        mActivityRule.launchActivity(intent);

        // wait for activity finished
        while (true) {
            TimeUnit.SECONDS.sleep(1);
        }
    }

}
