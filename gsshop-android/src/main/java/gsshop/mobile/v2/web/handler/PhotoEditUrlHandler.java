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

import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.attach.FileAttachParamInfo;
import gsshop.mobile.v2.web.BaseWebActivity;
import roboguice.util.Ln;

/**
 * 현재 액티비티 종료.
 * 모달 창에서 사용됨.
 */
public class PhotoEditUrlHandler implements WebUrlHandler {

    @Override
    public boolean handle(Activity activity, WebView webview, String url) {

        if (url != null) {
            Uri uri = Uri.parse(url);

            FileAttachParamInfo fileAttachParamInfo = new FileAttachParamInfo();
            fileAttachParamInfo.setCaller(uri.getQueryParameter("caller"));
            fileAttachParamInfo.setUploadUrl(uri.getQueryParameter("uploadUrl"));
            fileAttachParamInfo.setImageType(uri.getQueryParameter("imgType"));
            fileAttachParamInfo.setMediatype(uri.getQueryParameter("mediatype"));
            fileAttachParamInfo.setCallback(uri.getQueryParameter("callback"));
            fileAttachParamInfo.setImageCount(uri.getQueryParameter("imagecount"));
            fileAttachParamInfo.setHistoryBack(uri.getQueryParameter("back"));
            MainApplication.fileAttachParamInfo = fileAttachParamInfo;

            if (activity instanceof BaseWebActivity) {
                BaseWebActivity webActivity = (BaseWebActivity)activity;
                // 이미지 검색은 0번째 하나의 이미지만 저장 및 crop 하기 때문에.
                MainApplication.isEditFromGallery = MainApplication.isFromGallery;
                try {
                    if (!MainApplication.isFromGallery) {
                        webActivity.photoCrop(MainApplication.attechImagePath.getAbsolutePath(), false);
                    } else {
                        webActivity.photoCrop(MainApplication.articlePhotoes.get(0).fullImageUri.toString(), false);
                    }
                }
                catch (NullPointerException e) {
                    Ln.e(e.getMessage());
                }
            }
        }

        return true;
    }

    @Override
    public boolean match(String url) {
        return url.startsWith(ServerUrls.APP.PHOTOEDIT);
    }

}
