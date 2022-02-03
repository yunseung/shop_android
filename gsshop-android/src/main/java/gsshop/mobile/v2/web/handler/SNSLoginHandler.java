/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web.handler;

import android.app.Activity;
import android.app.Dialog;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.WebView;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog;
import gsshop.mobile.v2.web.BaseWebActivity;

/**
 * 회원가입시 SNS 로그인 후 정보전달
 *
 * ex) toapp://snslogin?snsType=[KA|NA]&clearYn=[Y|N]
 * - snsType : KA(카카오) NA(네이버)
 * - clearYn : Y(다른 계정으로 로그인 가능하도록 SNS 로그아웃)
 *           : 카카오의 경우 계정선택 팝업이 항상 뜨므로 별도 작업 필요 없음, 네이버만 필요.
 */
public class SNSLoginHandler implements WebUrlHandler {

    @Override
    public boolean handle(Activity activity, WebView webview, String url) {
        Uri uri = Uri.parse(url);
        String snsType = uri.getQueryParameter("snsType");
        String clearYn = uri.getQueryParameter("clearYn");

        //SNS로그인은 LOLLIPOP 부터 가능
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            if(activity instanceof  BaseWebActivity) {
                showOldVerMsg(activity);
            }
            return true;
        }

        if (TextUtils.isEmpty(snsType)) {
            if(activity instanceof  BaseWebActivity) {
                ((BaseWebActivity) activity).snsLoginFailed("");
            }
            return true;
        }

        if(activity instanceof  BaseWebActivity) {
            if ("NA".equalsIgnoreCase(snsType)) {
                ((BaseWebActivity) activity).authNaver(clearYn);
            } else if ("KA".equalsIgnoreCase(snsType)) {
                ((BaseWebActivity) activity).authKakao(clearYn);
            } else {
                ((BaseWebActivity) activity).snsLoginFailed("");
            }
        }

        return true;
    }

    @Override
    public boolean match(String url) {
        return url.startsWith(ServerUrls.APP.SNS_LOGIN);
    }

    /**
     * 메시지 팝업을 노출한다.
     *
     * * @param activity 액티비티
     */
    private void showOldVerMsg(Activity activity) {
        new CustomOneButtonDialog(activity).message(R.string.sns_login_version_old)
                .cancelable(false)
                .buttonClick(new CustomOneButtonDialog.ButtonClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                    }
                }).show();
    }
}
