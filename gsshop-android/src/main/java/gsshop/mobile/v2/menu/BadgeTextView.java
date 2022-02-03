package gsshop.mobile.v2.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class BadgeTextView extends TextView {

    private static final int MAX_BAGDE_COUNT = 99;

    public BadgeTextView(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    public BadgeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BadgeTextView(Context context) {
        this(context, null);
    }

    private void init(Context context) {
        if (isInEditMode()) {
            setVisibility(View.VISIBLE);
        } else {
            setVisibility(View.INVISIBLE);
        }
    }


    /**
     * count 설정하고, 자릿수에 따라 padding 결정.
     *
     * @param count count
     */
    public void setContents(int count) {
        if (count > 0) {
            // max count 이상이면 '(max count)+'로 표시
            String strCount;
            if (count > MAX_BAGDE_COUNT) {
                strCount = Integer.toString(MAX_BAGDE_COUNT) + "+";
            } else {
                strCount = Integer.toString(count);
            }

            setText(strCount);

            setVisibility(View.VISIBLE);
        } else {
            setVisibility(View.GONE);
        }
    }

    /**
     * badge contents가 string일 때
     *
     * @param str str
     */
    public void setContents(String str) {
        setText(str);
        if (str == null || str.length() == 0) {
            setVisibility(View.GONE);
        } else {
            setVisibility(View.VISIBLE);
        }
    }

}
