/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.gsshop.mocha.core.async.AsyncDialog;
import com.gsshop.mocha.core.async.AsyncDialogProvider;
import com.gsshop.mocha.core.async.BarAsyncDialog;
import com.gsshop.mocha.core.async.BarAsyncDialogProvider;
import com.gsshop.mocha.core.exception.ExceptionHandler;
import com.gsshop.mocha.service.push.SenderConnector;

import gsshop.mobile.v2.push.PushSenderConnector;
import roboguice.RoboGuice;

/**
 * 앱에 고유한 {@link RoboGuice} 모듈 설정.
 *
 *
 */
public class MainModule extends AbstractModule {

    /**
     * 딜폴트 생성자
     */
    public MainModule() {
    }

    /**
     * 생성자
     * @param context
     */
    public MainModule(Context context) {
    }

    @Override
    protected void configure() {
        bind(Dialog.class).annotatedWith(AsyncDialog.class).toProvider(AsyncDialogProvider.class);
        bind(ExceptionHandler.class).to(MainExceptionHandler.class).in(Singleton.class);
        bind(SenderConnector.class).to(PushSenderConnector.class).in(Singleton.class);
        bind(ProgressDialog.class).annotatedWith(BarAsyncDialog.class).toProvider(BarAsyncDialogProvider.class);

        // NOTE : 테스트용 mock 커넥터 연동
        //bind(SearchConnector.class).to(MockSearchConnector.class);
        //bind(UserConnector.class).to(MockUserConnector.class);
        //bind(BadgeConnector.class).to(MockBadgeConnector.class);
    }

}
