/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ProgressBar;

import com.appboy.models.IInAppMessage;
import com.appboy.models.MessageButton;
import com.appboy.ui.inappmessage.AppboyInAppMessageManager;
import com.appboy.ui.inappmessage.InAppMessageCloser;
import com.appboy.ui.inappmessage.InAppMessageOperation;
import com.appboy.ui.inappmessage.listeners.IInAppMessageManagerListener;
import com.blankj.utilcode.util.EmptyUtils;
import com.google.inject.Inject;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.web.WebViewProgressBar;

import java.util.Locale;

import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.DirectOrderView;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.support.gtm.GTMAction;
import gsshop.mobile.v2.support.gtm.GTMEnum;
import gsshop.mobile.v2.util.ClickUtils;
import gsshop.mobile.v2.util.CookieUtils;
import roboguice.util.Ln;

import static com.kakao.auth.Session.getCurrentSession;
import static gsshop.mobile.v2.home.HomeActivity.psnlCurationType;
import static gsshop.mobile.v2.home.HomeActivity.showPsnlCuration;
import static gsshop.mobile.v2.web.WebUtils.isSmartCart;

/**
 * 웹페이지를 보여주는 액티비티.
 *
 * 인텐트에 Keys.INTENT.WEB_URL 키를 사용하여 로딩할 웹페이지 주소를
 * 전달할 수 있다.
 */
@SuppressLint("NewApi")
public class MobileLiveNoTabWebActivity extends WebActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutParams p = (LayoutParams) webView.getLayoutParams();
        p.bottomMargin = 0;
        webView.setLayoutParams(p);

        // 기존에 기본 기존 Web Activity 를 복사한 모바일용 WebActivity를 만든다 (TAB 은 삭제)
        findViewById(R.id.layout_tab_menu).setVisibility(View.GONE);
    }
}