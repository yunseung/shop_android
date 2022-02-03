/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web.handler;

import android.app.Activity;
import androidx.fragment.app.FragmentActivity;
import android.webkit.WebView;

import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.home.shop.nalbang.ShortbangActivity;
import gsshop.mobile.v2.support.sns.SnsV2DialogFragment;

/**
 * 공유하기 팝업을 띄운다.
 *
 * sample)
 * toapp://snsshow
 */

public class SnsDialogShowHandler implements WebUrlHandler {
    @Override
    public boolean handle(Activity activity, WebView webview, String url) {
        //SNS 팝업 띄움
        SnsV2DialogFragment dialog = new SnsV2DialogFragment();
        dialog.show(((FragmentActivity)activity).getSupportFragmentManager(), ShortbangActivity.class.getSimpleName());

        return true;
    }

    @Override
    public boolean match(String url) {
        return url.startsWith(ServerUrls.APP.SNSPUPOP_WEB);
    }
}
