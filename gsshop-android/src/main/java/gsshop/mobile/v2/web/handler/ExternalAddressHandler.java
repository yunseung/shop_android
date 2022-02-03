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
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.webkit.WebView;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog;

/**
 * toapp://address?func1=setReceiveInfo 프로토콜 처리.
 *
 * toapp://browser? func2 = 파라미터 두개짜리 펑션 이름이 온다 .  setNumber 이후의 setReceiveInfo(이민수, 01050586808) 한다.
 * 주소록 연동이 필요한 규격이 추가될수 있음
 * 자바스크립트 호출 함수를 toapp 에서 받자는 의미 자바스크립트 규격 하드 코딩 안할려고
 *
 * (Intent.ACTION_VIEW를 통해 외부앱 호출)
 */
public class ExternalAddressHandler implements WebUrlHandler {

    @Override
    public boolean handle(Activity activity, WebView webview, String url) {
        // ex) toapp://address?func1=setReceiveInfo
        String func2 = "";

        if(url != null) {
            Uri uri = Uri.parse(url);
            func2 = uri.getQueryParameter("func2");
            MainApplication.giftFunc = func2;
            try {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI); // 사람 이름과 전화번호
                activity.startActivityForResult(intent, Keys.REQCODE.ADDRESS);//
            } catch (ActivityNotFoundException e) {
                Dialog dialog = new CustomOneButtonDialog(activity)
                        .message(R.string.gift_error_msg).buttonClick(new CustomOneButtonDialog.ButtonClickListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                dialog.dismiss();
                            }
                        });
                dialog.show();
                return false;

            }
        }
        return true;
    }

    @Override
    public boolean match(String url) {
        return url.startsWith(ServerUrls.APP.OPEN_ADDRESS);
    }

}
