/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.support.ui;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog.ButtonClickListener;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog.CancelListener;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * R.layout.dialog_two_button
 *
 * 기본 설정
 * - cancelable : false
 * - POSITIVE 버튼 라벨 : R.string.ok
 * - NEGATIVE 버튼 라벨 : R.string.cancel
 */
public class FinishButtonNormalDialog extends Dialog {

    protected Button positiveButton;
    protected Button negativeButton;
    private final LinearLayout layoutTitle;

    public FinishButtonNormalDialog(Activity activity) {
        super(activity, R.style.CustomDialog);
        setOwnerActivity(activity);
        setContentView(R.layout.dialog_finish_normal_button);
        setCancelable(true);// 백키 통한 취소 허용.

        positiveButton = (Button) findViewById(R.id.btn_ok);
        negativeButton = (Button) findViewById(R.id.btn_cancel);

        layoutTitle = (LinearLayout) findViewById(R.id.layout_title);
        layoutTitle.setVisibility(View.GONE);
    }

    public FinishButtonNormalDialog cancelable(boolean flag) {
        setCancelable(flag);
        return this;
    }

    @Override
    public final void setTitle(CharSequence title) {
        throw new UnsupportedOperationException("Use title() method.");
    }

    @Override
    public final void setTitle(int res) {
        throw new UnsupportedOperationException("Use title() method.");
    }

    public FinishButtonNormalDialog title(int resId) {
        layoutTitle.setVisibility(View.VISIBLE);

        TextView t = (TextView) findViewById(R.id.txt_title);
        t.setText(resId);
        return this;
    }

    public FinishButtonNormalDialog title(String title) {
        layoutTitle.setVisibility(View.VISIBLE);

        TextView t = (TextView) findViewById(R.id.txt_title);
        t.setText(title);
        return this;
    }

    public FinishButtonNormalDialog message(int resId) {
        TextView t = (TextView) findViewById(R.id.txt_message);
        t.setText(resId);
        return this;
    }

    public FinishButtonNormalDialog message(String message) {
        TextView t = (TextView) findViewById(R.id.txt_message);
        t.setText(message);
        return this;
    }

    public FinishButtonNormalDialog positiveButtonLabel(int resId) {
        positiveButton.setText(resId);
        return this;
    }

    public FinishButtonNormalDialog negativeButtonLabel(int resId) {
        negativeButton.setText(resId);
        return this;
    }

    public FinishButtonNormalDialog positiveButtonClick(final ButtonClickListener click) {
        final FinishButtonNormalDialog dialog = this;
        positiveButton.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.onClick(dialog);
            }
        });

        return this;
    }

    public FinishButtonNormalDialog negativeButtonClick(final ButtonClickListener click) {
        final FinishButtonNormalDialog dialog = this;
        negativeButton.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.onClick(dialog);
            }
        });

        return this;
    }

    public FinishButtonNormalDialog cancelled(final CancelListener cancel) {
        setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                cancel.onCancel((Dialog) dialog);
            }
        });
        return this;
    }

}
