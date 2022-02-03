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
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import gsshop.mobile.v2.R;

/**
 * 파일업로드 진행률 표시
 *
 */
public class CustomProgressDialog extends Dialog {

    private TextView txtProgress;
    private ProgressBar progressBar;
    protected Button button;

    public CustomProgressDialog(Activity activity) {
        super(activity, R.style.CustomDialog);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setOwnerActivity(activity);

        setContentView(R.layout.progress_dialog);

        txtProgress = (TextView) findViewById(R.id.txt_progress);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        button = (Button) findViewById(R.id.btn_cancel);
        button.setText(R.string.mc_cancel);

        setCancelable(true);// 백키 통한 취소 허용.
    }

    public void setProgressText(String val) {
        txtProgress.setText(val);
    }

    public void setProgress(int val) {
        progressBar.setProgress(val);
    }

    public CustomProgressDialog message(int resId) {
        TextView t = (TextView) findViewById(R.id.txt_message);
        t.setText(resId);
        return this;
    }

    public CustomProgressDialog message(String message) {
        TextView t = (TextView) findViewById(R.id.txt_message);
        t.setText(message);
        return this;
    }

    public CustomProgressDialog buttonClick(final ButtonClickListener click) {
        final CustomProgressDialog dialog = this;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.onClick(dialog);
            }
        });

        return this;
    }

    public CustomProgressDialog cancelled(final CancelListener cancel) {
        setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                cancel.onCancel((Dialog) dialog);
            }
        });
        return this;
    }

    public interface CancelListener {
        public void onCancel(Dialog dialog);
    }

    public interface ButtonClickListener {
        public void onClick(Dialog dialog);
    }

    /**
     * 버튼 클릭시 다이얼로그를 닫는다.
     */
    public static ButtonClickListener DISMISS = new ButtonClickListener() {
        @Override
        public void onClick(Dialog dialog) {
            dialog.dismiss();
        }
    };

	/**
	 * @return the button
	 */
	public Button getButton() {
		return button;
	}

	/**
	 * @param button the button to set
	 */
	public void setButton(Button button) {
		this.button = button;
	}

}
