/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web.handler;

import android.net.Uri;

import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.support.tv.VideoParameters;

/**
 * Live Streaming 동영상 재생 요청 처리.
 *
 * - 링크 형식 : toapp://livestreaming?type=UI표시용구분자{@literal &}videoid=동영상번호{@literal &}url=동영상파일주소{@literal &}targeturl=상품페이지
 *           type값은 동영상 위에 표시할 UI가 상이할 경우를 대비한 구분자임 (값이 넘어오지 않을 경우 디폴트 값은 Keys.INTENT.PRD_PLAY)
 * - 예제 : toapp://livestreaming?type=PRD_PLAY{@literal &}videoid=12345678{@literal &}url=http://mobilevod.gsshop.com/beauty/5416.mp4
 * 			{@literal &}targeturl=http://m.gsshop.com/prd/prd.gs?fromApp=Y{@literal &}prdid=329323
 * - 주의 : 웹에서 targeturl을 인코딩해서 호출해야 함 (인코딩 안할 경우 두번째 파라미터부터 누락됨)
 *
 */
public class LiveStreamingUrlHandler extends LiveTVUrlHandler {

    @Override
    public boolean match(String url) {
        return url.startsWith(ServerUrls.APP.LIVE_STREAMING);
    }

    @Override
    protected VideoParameters buildVideoParam(String url) {
        Uri uri = Uri.parse(url);

        VideoParameters param = new VideoParameters();
        param.videoId = uri.getQueryParameter("videoid");
        param.videoUrl = uri.getQueryParameter("url");
        param.productInfoUrl = uri.getQueryParameter("targeturl");
        //웹에서 접근시 디폴트 재생
        param.isPlaying = true;

        return param;
    }

}
