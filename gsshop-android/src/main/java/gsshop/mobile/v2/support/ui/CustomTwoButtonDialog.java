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
import android.widget.LinearLayout;
import android.widget.TextView;

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
public class CustomTwoButtonDialog extends Dialog {

    protected Button positiveButton;
    protected Button negativeButton;
    private LinearLayout layoutTitle;

    public CustomTwoButtonDialog(Activity activity) {
        super(activity, R.style.CustomDialog);
        setOwnerActivity(activity);
        setContentView(R.layout.dialog_two_button);
        setCancelable(true);// 백키 통한 취소 허용.

        positiveButton = (Button) findViewById(R.id.btn_ok);
        negativeButton = (Button) findViewById(R.id.btn_cancel);

        layoutTitle = (LinearLayout) findViewById(R.id.layout_title);
        layoutTitle.setVisibility(View.GONE);
    }

    public CustomTwoButtonDialog cancelable(boolean flag) {
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

    public CustomTwoButtonDialog title(int resId) {
        layoutTitle.setVisibility(View.VISIBLE);

        TextView t = (TextView) findViewById(R.id.txt_title);
        t.setText(resId);
        return this;
    }

    public CustomTwoButtonDialog title(String title) {
        layoutTitle.setVisibility(View.VISIBLE);

        TextView t = (TextView) findViewById(R.id.txt_title);
        t.setText(title);
        return this;
    }

    public CustomTwoButtonDialog message(int resId) {
        TextView t = (TextView) findViewById(R.id.txt_message);
        t.setText(resId);
        return this;
    }

    public CustomTwoButtonDialog message(String message) {
        TextView t = (TextView) findViewById(R.id.txt_message);
        t.setText(message);
        return this;
    }

    public CustomTwoButtonDialog positiveButtonLabel(int resId) {
        positiveButton.setText(resId);
        return this;
    }

    public CustomTwoButtonDialog negativeButtonLabel(int resId) {
        negativeButton.setText(resId);
        return this;
    }

    public CustomTwoButtonDialog negativeButtonLabel(String label) {
        negativeButton.setText(label);
        return this;
    }

    public CustomTwoButtonDialog positiveButtonLabel(String label) {
        positiveButton.setText(label);
        return this;
    }

    public CustomTwoButtonDialog positiveButtonClick(final ButtonClickListener click) {
        final CustomTwoButtonDialog dialog = this;
        positiveButton.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.onClick(dialog);
            }
        });

        return this;
    }

    public CustomTwoButtonDialog negativeButtonClick(final ButtonClickListener click) {
        final CustomTwoButtonDialog dialog = this;
        negativeButton.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.onClick(dialog);
            }
        });

        return this;
    }

    public CustomTwoButtonDialog cancelled(final CancelListener cancel) {
        setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                cancel.onCancel((Dialog) dialog);
            }
        });
        return this;
    }

}
