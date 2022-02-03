/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.review;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

import com.gsshop.mocha.core.exception.DefaultExceptionHandler;

import gsshop.mobile.v2.support.ui.CustomOneButtonDialog;
import roboguice.inject.ContextSingleton;

/**
 * 상품평 조회시 발생하는 에러 처리.
 *
 */
@ContextSingleton
public class ReviewExceptionHandler extends DefaultExceptionHandler {

    @Override
    protected void doMessage(Context context, Throwable t, String message) {
        if (context instanceof Activity) {
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final Activity activity = (Activity) context;
                    Dialog dialog = new CustomOneButtonDialog(activity).message(message)
                            .finishActivityOnCancelled(activity).finishActivityOnButtonClick(activity);
                    dialog.show();
                }
            });

            return;
        }

        super.doMessage(context, t, message);
    }
}
