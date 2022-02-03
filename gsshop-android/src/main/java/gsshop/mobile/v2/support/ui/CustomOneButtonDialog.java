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
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.Keys.ACTION;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.util.MarketUtils;

/**
 * R.layout.dialog_two_button 사용하고 cancel 버튼은 숨김.
 *
 * 기본 설정
 * - cancelable : false
 * - 버튼 라벨 : R.string.ok
 *
 */
public class CustomOneButtonDialog extends Dialog {

    protected Button button;
    private LinearLayout layoutTitle;

    public CustomOneButtonDialog(Activity activity) {
        super(activity, R.style.CustomDialog);
        setOwnerActivity(activity);
        setContentView(R.layout.dialog_two_button);
        setCancelable(true);// 백키 통한 취소 허용.
        button = (Button) findViewById(R.id.btn_ok);
        button.setText(R.string.mc_ok);
        button.setBackgroundResource(R.drawable.bg_dialog_round_bottom_one);

        layoutTitle = (LinearLayout) findViewById(R.id.layout_title);
        layoutTitle.setVisibility(View.GONE);

        Button btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnCancel.setVisibility(View.GONE);
        View viewBtnDivider = findViewById(R.id.view_btn_divider);
        viewBtnDivider.setVisibility(View.GONE);
    }

    public CustomOneButtonDialog cancelable(boolean flag) {
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

    public CustomOneButtonDialog title(int resId) {
        layoutTitle.setVisibility(View.VISIBLE);

        TextView t = (TextView) findViewById(R.id.txt_title);
        t.setText(resId);
        return this;
    }

    public CustomOneButtonDialog title(String title) {
        layoutTitle.setVisibility(View.VISIBLE);

        TextView t = (TextView) findViewById(R.id.txt_title);
        t.setText(title);
        return this;
    }

    public CustomOneButtonDialog message(int resId) {
        TextView t = (TextView) findViewById(R.id.txt_message);
        t.setText(resId);
        return this;
    }

    public CustomOneButtonDialog message(String message) {
        TextView t = (TextView) findViewById(R.id.txt_message);
        t.setText(message);
        return this;
    }

    public CustomOneButtonDialog htmlMessage(String message) {
        TextView t = (TextView) findViewById(R.id.txt_message);
        t.setText(Html.fromHtml(message));
        return this;
    }

    public CustomOneButtonDialog gravityLeft() {
        TextView t = (TextView) findViewById(R.id.txt_message);
        t.setGravity(Gravity.LEFT);
        return this;
    }

    public CustomOneButtonDialog buttonLabel(int resId) {
        button.setText(resId);
        return this;
    }

    public CustomOneButtonDialog buttonClick(final ButtonClickListener click) {
        final CustomOneButtonDialog dialog = this;
        button.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.onClick(dialog);
            }
        });

        return this;
    }

    public CustomOneButtonDialog finishActivityOnButtonClick(final Activity activity) {
        buttonClick(new ButtonClickListener() {
            @Override
            public void onClick(Dialog dialog) {
                dialog.dismiss();
                activity.finish();
            }
        });
        return this;
    }

    public CustomOneButtonDialog finishActivityOnCancelled(final Activity activity) {
        setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                activity.finish();
            }
        });
        return this;
    }

    public CustomOneButtonDialog cancelled(final CancelListener cancel) {
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
     * 강제 업데이트 
     */
    public CustomOneButtonDialog moveMarket(final Activity activity) {
        buttonClick(new ButtonClickListener() {
            @Override
            public void onClick(Dialog dialog) {
                MarketUtils.goMarket(activity);
                dialog.dismiss();
                activity.finish();
            }
        });
        return this;
    }

    /**
     * Login Type goUrl
     */
    public CustomOneButtonDialog moveTypeUrl(final Activity activity, final String url,
            final String type) {
        buttonClick(new ButtonClickListener() {
            @Override
            public void onClick(Dialog dialog) {
                dialog.dismiss();
                if ("10".equals(type)) {
                    Intent intent = new Intent(ACTION.WEB);
                    intent.putExtra(Keys.INTENT.WEB_URL, url);
                    intent.putExtra(Keys.INTENT.TAB_MENU, true);
                    activity.startActivity(intent);
                } else if ("20".equals(type)) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    activity.startActivity(intent);
                }
            }
        });
        return this;
    }

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
    
    /*private void goWeb(String url) {
        Intent intent = new Intent(ACTION.WEB);
        intent.putExtra(Keys.INTENT.WEB_URL, url);
        intent.putExtra(Keys.INTENT.TAB_MENU, true);
        startActivity(intent);
    }*/
}
