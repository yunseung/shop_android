/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.util;

import gsshop.mobile.v2.R;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 메인 ViewPager의 title strip을 표현하기 위한 view.
 *
 */
public class TabView extends LinearLayout {
    public int mIndex;

    private TextView mTxtTab;
    private View mViewSelected;

    private int selectedTextColor;
    private int unSelectedTextColor;

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public TabView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        init(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public TabView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * @param context
     */
    public TabView(Context context) {
        super(context);
        init(context);
    }

    /**
     * @param context
     */
    private void init(Context context) {
        if (!isInEditMode()) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View view = inflater.inflate(R.layout.home_tab_indicator_item, this, true);

            LayoutParams p = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            p.gravity = Gravity.BOTTOM;
            setLayoutParams(p);

            mTxtTab = (TextView) view.findViewById(R.id.txt_tab);

            // 선택 상태임을 표시해주는 view
            mViewSelected = view.findViewById(R.id.view_selected);
            mViewSelected.setVisibility(View.INVISIBLE);
        }

        selectedTextColor = context.getResources().getColor(R.color.tab_indicator_text_selected);
        unSelectedTextColor = context.getResources()
                .getColor(R.color.tab_indicator_text_unselected);
    }

    public int getIndex() {
        return mIndex;
    }

    public void setTabText(CharSequence str) {
        mTxtTab.setText(str);
    }

    @Override
    public void setSelected(boolean isSelected) {
        if (isSelected) {
            mTxtTab.setTypeface(null, Typeface.BOLD);
            mTxtTab.setTextColor(selectedTextColor);
            mViewSelected.setVisibility(View.VISIBLE);
        } else {
            mTxtTab.setTypeface(null, Typeface.NORMAL);
            mTxtTab.setTextColor(unSelectedTextColor);
            mViewSelected.setVisibility(View.INVISIBLE);
        }
        mTxtTab.setTypeface(Typeface.SANS_SERIF);
    }
}
