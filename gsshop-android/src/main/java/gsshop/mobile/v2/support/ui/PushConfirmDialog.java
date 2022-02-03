/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.support.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog.ButtonClickListener;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog.CancelListener;

/**
 * R.layout.dialog_two_button
 *
 * 기본 설정
 * - cancelable : false
 * - POSITIVE 버튼 라벨 : R.string.ok
 * - NEGATIVE 버튼 라벨 : R.string.cancel
 */
public class PushConfirmDialog extends Dialog {

    protected Button positiveButton;
    protected Button negativeButton;

    public PushConfirmDialog(Activity activity) {
        super(activity, android.R.style.Theme_Translucent_NoTitleBar);
        setOwnerActivity(activity);
        setContentView(R.layout.push_dialog);
        setCancelable(true);// 백키 통한 취소 허용.

        positiveButton = (Button) findViewById(R.id.btn_ok);
        negativeButton = (Button) findViewById(R.id.btn_cancel);
    }

    public PushConfirmDialog positiveButtonClick(final ButtonClickListener click) {
        final PushConfirmDialog dialog = this;
        positiveButton.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.onClick(dialog);
            }
        });

        return this;
    }

    public PushConfirmDialog negativeButtonClick(final ButtonClickListener click) {
        final PushConfirmDialog dialog = this;
        negativeButton.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.onClick(dialog);
            }
        });

        return this;
    }

    public PushConfirmDialog cancelled(final CancelListener cancel) {
        setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                cancel.onCancel((Dialog) dialog);
            }
        });
        return this;
    }

}
