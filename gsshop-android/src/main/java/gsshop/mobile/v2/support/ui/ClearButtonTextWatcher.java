/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.support.ui;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.gsshop.mocha.ui.util.ViewUtils;

/**
 * {@link EditText}에 글자 입력시 클리어 버튼이
 * 나타나고 글자를 모두 지우면 클리어 버튼이 사라지게 한다.
 *
 */
public class ClearButtonTextWatcher implements TextWatcher {

    private View clearButtonView;

    /**
     *
     * @param clearButtonView (X) 표시의 클리어 버튼뷰
     */
    public ClearButtonTextWatcher(View clearButtonView) {
        this.clearButtonView = clearButtonView;
    }

    /**
     * 라이브톡 개선 후 clearButtonView사라짐
     */
    public ClearButtonTextWatcher() {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() > 0) {
            ViewUtils.showViews(clearButtonView);
        } else {
            ViewUtils.hideViews(clearButtonView);
        }
    }

}
