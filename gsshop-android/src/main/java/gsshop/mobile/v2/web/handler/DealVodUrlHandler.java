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
 * VOD 동영상 재생 요청 처리.
 *
 * - 링크 형식 : toapp://vod?videoid=동영상번호{@literal &}url=동영상파일주소{@literal &}prdid=상품ID
 * - 예제 : toapp://vod?videoid=12345678{@literal &}url=http://mobilevod.gsshop.com/beauty/5416.mp4{@literal &}prdid=329323
 *
 */
public class DealVodUrlHandler extends LiveTVUrlHandler {

    @Override
    public boolean match(String url) {
        return url.startsWith(ServerUrls.APP.DEALVOD);
    }

    @Override
    protected VideoParameters buildVideoParam(String url) {
        Uri uri = Uri.parse(url);

        VideoParameters param = new VideoParameters();
        param.videoId = uri.getQueryParameter("videoid");
        param.videoUrl = uri.getQueryParameter("url");
        param.productInfoUrl = uri.getQueryParameter("targeturl");
        return param;
    }

}
