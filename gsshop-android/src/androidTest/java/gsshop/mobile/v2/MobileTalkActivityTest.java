package gsshop.mobile.v2;

import android.content.Intent;
import android.net.Uri;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import gsshop.mobile.v2.attach.FileAttachAction;
import gsshop.mobile.v2.attach.FileAttachParamInfo;
import gsshop.mobile.v2.attach.MobileTalkActivity;
import gsshop.mobile.v2.util.StringUtils;

/**
 * WebActivity test case
 */
@RunWith(AndroidJUnit4.class)
public class MobileTalkActivityTest {

    @Rule
    public IntentsTestRule<MobileTalkActivity> mActivityRule = new IntentsTestRule<>(MobileTalkActivity.class, false, false);

    /**
     * 이벤트 대용량 동영상 업로드
     *
     * @throws InterruptedException
     */
    @Test
    public void testMobileTalk() throws InterruptedException {
        //String url = "http://tevent.gsshop.com/test/test_event2.jsp";
        String url = "toapp://attach?caller=mobiletalk&uploadUrl=http%3A%2F%2Fasm.gsshop.com%2Fproxy%3Fdomain_id%3DNODE0000000001%26service_type%3DSVTLK%26ref_talk_id%3DTCKT0003649633%26node_id%3DNODE0000000004VA%26in_channel_id%3DCHNL0000000002%26customer_id%3D11807639%26customer_name%3D%EC%A0%95*%EB%B9%88&callback=setConsultPopRtnVal&talkui=A";

        FileAttachParamInfo fileAttachParamInfo = new FileAttachParamInfo();
        Uri uri = Uri.parse(url);

        fileAttachParamInfo.setCaller(uri.getQueryParameter("caller"));
        fileAttachParamInfo.setUploadUrl(uri.getQueryParameter("uploadUrl"));
        fileAttachParamInfo.setReturnUrl(uri.getQueryParameter("returnUrl"));
        fileAttachParamInfo.setCallback(uri.getQueryParameter("callback"));
        fileAttachParamInfo.setMediatype(uri.getQueryParameter("mediatype"));
        fileAttachParamInfo.setImageCount(uri.getQueryParameter("imagecount"));
        fileAttachParamInfo.setTalkui(uri.getQueryParameter("talkui"));

        String maxSize = uri.getQueryParameter("maxsize");
        if (maxSize != null && StringUtils.isNumeric(maxSize) && Integer.valueOf(maxSize) > 0) {
            fileAttachParamInfo.setMaxVideoSize(Integer.valueOf(maxSize));
        } else {
            fileAttachParamInfo.setMaxVideoSize(-1);
        }

        //카테고리값 세팅
        FileAttachAction.category = uri.getQueryParameter("category");

        MainApplication.fileAttachParamInfo = fileAttachParamInfo;


        Intent intent = new Intent();

        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("fileAttachParamInfo", fileAttachParamInfo);

        mActivityRule.launchActivity(intent);

        // wait for activity finished
        while(!mActivityRule.getActivity().isFinishing()) {
            TimeUnit.SECONDS.sleep(1);
        }
    }


}
