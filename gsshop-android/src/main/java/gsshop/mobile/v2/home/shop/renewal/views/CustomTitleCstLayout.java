package gsshop.mobile.v2.home.shop.renewal.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.view.TextViewBackLine;

public class CustomTitleCstLayout extends LinearLayout {

    private Context mContext;

    // 상단 타이틀 TextView들이 추가될 리니어 레이아웃
    private LinearLayout mLayoutAdd;

    // 하단 해쉬태그 sub 텍스트
    private TextView mTextSub;

    public CustomTitleCstLayout(Context context) {
        super(context);

        mContext = context;
        initLayout();
    }

    public CustomTitleCstLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initLayout();
    }

    public CustomTitleCstLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initLayout();
    }

    private void initLayout() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_custom_cst_title, this, true);
    }

    /**
     * inflate 된 후에 호출
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mLayoutAdd = findViewById(R.id.layout_add_title);
        mTextSub = findViewById(R.id.txt_sub);
    }

    public void setTitle(Context context, SectionContentList sectionContent) {
        mLayoutAdd.removeAllViews();

        for (SectionContentList.CustomTitle customTitle : sectionContent.customTitle) {
            if (TextUtils.isEmpty(customTitle.text)) {
                continue;
            }
            TextViewBackLine textView = new TextViewBackLine(context);
            textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            textView.setTextColor(Color.parseColor("#111111"));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            textView.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
            textView.setText(customTitle.text);
            textView.setSingleLine();
            if ("Y".equals(customTitle.point)) {
                textView.setTextLine(true);
            }
            mLayoutAdd.addView(textView);
        }

        if (mTextSub != null) {
            mTextSub.setVisibility(GONE);
        }

        if (!TextUtils.isEmpty(sectionContent.subName) && mTextSub != null) {
            mTextSub.setVisibility(VISIBLE);
            mTextSub.setText(sectionContent.subName);
        }
    }
}