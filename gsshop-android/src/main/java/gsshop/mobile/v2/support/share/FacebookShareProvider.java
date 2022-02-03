/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.support.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import gsshop.mobile.v2.util.EncryptUtils;
import gsshop.mobile.v2.web.WebUtils;

/***
 * 페이스북 공유하기
 */
public class FacebookShareProvider implements ShareInterface {

    ShareInfo shareInfo;
    public FacebookShareProvider(ShareInfo shareInfo){
        this.shareInfo = shareInfo;
    }

    @Override
    public void share(Activity activity) {
        if(!FacebookSdk.isInitialized()){
            FacebookSdk.sdkInitialize(activity);
        }

        //Link만 API정보 사용하고 나머지는 meta 정보를 추출하여 공유함
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(shareInfo.getLink()))
                .setQuote(shareInfo.getMessage())
                .setContentTitle("")
                .setContentDescription("")
                .setImageUrl(null)
                .build();

        if(isAppInstalled(activity, "com.facebook.katana")) {
            ShareDialog shareDialog = new ShareDialog(activity);
            shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
        }else{

            //웹 prdUrl 재활용 할경우 재 인코딩해야 한다. ( 파라미터가 2개인경우 1개가 짤린다 ) 08/16
                WebUtils.goWeb( activity, "https://facebook.com/sharer.php?t=" + shareInfo.getMessage() + "&u="
                        + EncryptUtils.urlEncode(shareInfo.getLink()));
        }
    }

    public boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

            @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
    }


}
