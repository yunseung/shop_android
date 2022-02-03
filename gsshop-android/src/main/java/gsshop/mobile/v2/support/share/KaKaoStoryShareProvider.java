/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.support.share;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;

import java.util.HashMap;
import java.util.Map;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog;

/***
 * 카카오스토리  공유하기
 */
public class KaKaoStoryShareProvider implements ShareInterface {

    ShareInfo shareInfo;
    public KaKaoStoryShareProvider(ShareInfo shareInfo){
        this.shareInfo = shareInfo;
    }

    @Override
    public void share(Activity activity) {

        Map<String, Object> urlInfoAndroid = new HashMap<String, Object>(1);
        urlInfoAndroid.put("title", shareInfo.getSubject());
        urlInfoAndroid.put("desc", shareInfo.getMessage());
        urlInfoAndroid.put("imageurl", new String[] {shareInfo.getImageurl()});
        urlInfoAndroid.put("type", "article");

        // Recommended: Use application context for parameter.
        StoryLink storyLink = StoryLink.getLink(activity);

        // check, intent is available.
        if (!storyLink.isAvailableIntent()) {
            new CustomOneButtonDialog(activity).message(R.string.sns_kakaostory_not_installed)
                    .cancelable(false)
                    .buttonClick(new CustomOneButtonDialog.ButtonClickListener() {
                        @Override
                        public void onClick(Dialog dialog) {
                            dialog.dismiss();
                        }
                    }).show();
            return;
        }

        storyLink.openKakaoLink(activity,
                shareInfo.getLink(),
                activity.getPackageName(),
                "1.0",
                "GSSHOP",
                "UTF-8",
                urlInfoAndroid);

    }

            @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
    }


}
