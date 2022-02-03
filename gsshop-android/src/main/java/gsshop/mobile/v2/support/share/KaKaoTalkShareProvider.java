/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.support.share;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;

import gsshop.mobile.v2.util.EncryptUtils;
import roboguice.util.Ln;

import static gsshop.mobile.v2.support.scheme.MenuHostHandler.GSSHOP_SCHEMA_HOME;

/***
 * 카카오톡 공유하기
 */
public class KaKaoTalkShareProvider implements ShareInterface {

    //kakaolink v2 에서는 이미지의 크기는 너비 200픽셀 이상, 높이 200픽셀 이상이어야 합니다. (가이드내용)
    //그래서 이미지 사이즈를 2배씩 증가시켜 촤소사이즈를 200 이상으로 함
    //이미지 가로
    private static final int IMAGE_WIDTH = 400;
    //이미지 세로(기본값)
    private static final int IMAGE_HEIGHT_DEFAULT = 300;
    //이미지 세로(숏방에서 가로가 긴 이미지를 공유한 경우)
    private static final int IMAGE_HEIGHT_SHORTBANG_TYPE_A = 224;
    //이미지 세로(숏방에서 세로가 긴 이미지를 공유한 경우)
    private static final int IMAGE_HEIGHT_SHORTBANG_TYPE_B = 710;

    ShareInfo shareInfo;
    public KaKaoTalkShareProvider(ShareInfo shareInfo){
        this.shareInfo = shareInfo;
    }

    private ResponseCallback<KakaoLinkResponse> callback;

    @Override
    public void share(final Activity activity) {
        try{

            /**
             * createAndroidActionInfoBuilder Android 열었을때 ( 인코딩을 한다 )
             *
             * createiOSActionInfoBuilder iOS 열었을때 ( 인코딩을 하지 않는다 )
             *
             * 플랫폼에 따른 처리 방식을 틀리기 때문에 아래와 같이 처리한다, iOS kakao 는 자체적인 인코딩을 수행
             */
            //kakaolink v2
            callback = new ResponseCallback<KakaoLinkResponse>() {
                @Override
                public void onFailure(ErrorResult errorResult) {
                    Toast.makeText(activity, errorResult.getErrorMessage(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccess(KakaoLinkResponse result) {
                    //Toast.makeText(activity, "Successfully sent KakaoLink v2 message.", Toast.LENGTH_LONG).show();
                }
            };
            FeedTemplate params = FeedTemplate
                    .newBuilder(ContentObject.newBuilder(shareInfo.getMessage(),
                            shareInfo.getImageurl(),
                            LinkObject.newBuilder().setWebUrl(shareInfo.getLink())
                                    .setMobileWebUrl(shareInfo.getLink()).build())
                                    .setImageWidth(IMAGE_WIDTH)
                                    .setImageHeight(getImageHeight(shareInfo))
                            .build())
                    .addButton(new ButtonObject("웹으로 보기", LinkObject.newBuilder().setWebUrl(shareInfo.getLink()).setMobileWebUrl(shareInfo.getLink()).build()))
                    .addButton(new ButtonObject("앱으로 보기", LinkObject.newBuilder()
                            .setWebUrl(shareInfo.getLink())
                            .setMobileWebUrl(shareInfo.getLink())
                            .setAndroidExecutionParams("url=" + GSSHOP_SCHEMA_HOME + "?"+ EncryptUtils.urlEncode(shareInfo.getLink()))
                            .setIosExecutionParams("url=" + GSSHOP_SCHEMA_HOME + "?"+ shareInfo.getLink())
                            .build()))
                    .build();
            KakaoLinkService.getInstance().sendDefault(activity, params, callback);

        }catch (Exception e){
            // 10/19 품질팀 요청
            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
            // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
            Ln.e(e);
        }
    }

            @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
    }

    /**
     * 이미지 타입별 높이를 구한다.
     *
     * @param info ShareInfo
     * @return 이미지 높이
     */
    private int getImageHeight(ShareInfo info) {
        int height;
        switch (info.getShareImageType()) {
            case TYPE_SB_A:
                height = IMAGE_HEIGHT_SHORTBANG_TYPE_A;
                break;
            case TYPE_SB_B:
                height = IMAGE_HEIGHT_SHORTBANG_TYPE_B;
                break;
            default:
                height = IMAGE_HEIGHT_DEFAULT;
                break;
        }

        return height;
    }

}
