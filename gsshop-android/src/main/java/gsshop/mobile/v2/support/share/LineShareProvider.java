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
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.net.URLEncoder;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog;
import roboguice.util.Ln;

/***
 * 라인 공유하기
 */
public class LineShareProvider implements ShareInterface {

    ShareInfo shareInfo;

    public LineShareProvider(ShareInfo shareInfo){
        this.shareInfo = shareInfo;
    }

    @Override
    public void share(Activity activity) {

        PackageManager manager = activity.getBaseContext().getPackageManager();
        Intent i = manager.getLaunchIntentForPackage("jp.naver.line.android");

        try {
            if (i == null) {
                new CustomOneButtonDialog(activity).message(R.string.sns_line_not_installed)
                        .cancelable(false)
                        .buttonClick(new CustomOneButtonDialog.ButtonClickListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                dialog.dismiss();
                            }
                        }).show();
                return;
            }

            String encodedText = URLEncoder.encode(shareInfo.getMessage() + "\r\n" + shareInfo.getLink(), "utf-8"); // 글 본문 (utf-8 urlencoded)
            Uri uri = Uri.parse("line://msg/text/" + encodedText);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            activity.startActivity(intent);


        }catch(Exception e){
            Ln.e(e);

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
