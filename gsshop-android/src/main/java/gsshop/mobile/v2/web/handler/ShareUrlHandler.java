/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web.handler;

import android.app.Activity;
import android.net.Uri;
import android.webkit.WebView;

import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.support.share.ShareFactory;
import gsshop.mobile.v2.support.share.ShareInfo;
import gsshop.mobile.v2.support.share.ShareInterface;

/**
 * 공유하기(기타) url을 처리한다.
 *
 * sample)
 * toapp://etc_shere?sourceurl=http://m.gsshop.com{@literal &}description=설명0
 */

public class ShareUrlHandler implements WebUrlHandler {
    @Override
    public boolean handle(Activity activity, WebView webview, String url) {
        Uri data = Uri.parse(url);
        ShareInfo shareInfo = new ShareInfo();
        shareInfo.setTarget(data.getQueryParameter("target"));
        shareInfo.setLink(data.getQueryParameter("link"));
        shareInfo.setSubject(data.getQueryParameter("subject"));
        shareInfo.setMessage(data.getQueryParameter("message"));
        shareInfo.setImageurl(data.getQueryParameter("imageurl"));

        shareInfo.setShare_title(data.getQueryParameter("share_title"));

        ShareInterface shareInterface = ShareFactory.getShareProvider(shareInfo);
        if(shareInterface != null) {
            shareInterface.share(activity);
        }

        return true;
    }

    @Override
    public boolean match(String url) {
        return url.startsWith(ServerUrls.APP.SHARE);
    }
}
