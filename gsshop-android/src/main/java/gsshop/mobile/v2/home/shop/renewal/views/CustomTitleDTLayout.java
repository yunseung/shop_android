package gsshop.mobile.v2.home.shop.renewal.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.shape.RoundedCornerTreatment;

import java.util.ArrayList;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.user.User;
import gsshop.mobile.v2.util.DisplayUtils;

/**
 * PRD_C_CST_SQ 뷰타입 타이틀 레이아웃
 */
public class CustomTitleDTLayout extends LinearLayout {
    private Context mContext;

    // 상단 타이틀
    private TextView mTextTitle;

    // 하단 해쉬태그 sub 텍스트
    private TextView mTextSubTitle;

    /**
     * 뷰타입 define
     */
    private String RTS_PRD_C_CST_SQ_VIEWTYPE = "RTS_PRD_C_CST_SQ";


    public CustomTitleDTLayout(Context context) {
        super(context);
        mContext = context;
        initLayout();
    }

    public CustomTitleDTLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initLayout();
    }

    public CustomTitleDTLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initLayout();
    }

    private void initLayout() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_custom_dt_title, this, true);
    }

    /**
     * inflate 된 후에 호출
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTextTitle = findViewById(R.id.tv_title);
        mTextSubTitle = findViewById(R.id.tv_subtitle);
    }

    public void setTitle(Context context, SectionContentList sectionContent) {
        mTextTitle.setVisibility(GONE);
        mTextSubTitle.setVisibility(GONE);

        SpannableStringBuilder userNameBuilder = new SpannableStringBuilder(); // 고객님
        SpannableStringBuilder titleBuilder = new SpannableStringBuilder(); // 타이틀
        SpannableStringBuilder resultBuilder = new SpannableStringBuilder(); // 고객님 + 타이틀

        //비로그인 -> 고객님의 // 로그인 -> OOO님의 처리
        if("Y".equals(sectionContent.useName)){
            User user = User.getCachedUser();
            String userName = "";
            if(user != null){
                userName = user.getUserName() + mContext.getString(R.string.user);
                userNameBuilder.append(userName);

            }else{
                userName = mContext.getString(R.string.customer) + mContext.getString(R.string.user);
                userNameBuilder.append(userName);
            }
            int index = userNameBuilder.toString().indexOf(userName);
            userNameBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#111111")), index, index + userName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            userNameBuilder.setSpan(new StyleSpan(Typeface.BOLD), index, index + userName.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }else{
            userNameBuilder.append("");
        }

        //타이틀 텍스트 붙이기
        //ex) [찜한, #가벼운신발, 상품과 비교해보세요]
        if(sectionContent.customTitle != null && sectionContent.customTitle.size() > 0){
            for (SectionContentList.CustomTitle customTitle : sectionContent.customTitle) {
                if (DisplayUtils.isValidString(customTitle.text)) {
                    titleBuilder.append(customTitle.text);

                    int index = titleBuilder.toString().indexOf(customTitle.text);

                    //스트리밍일때만 타이틀 텍스트 스타일 다름
                    if(RTS_PRD_C_CST_SQ_VIEWTYPE.equals(sectionContent.viewType)){
                        if("Y".equals(customTitle.point)){
                            titleBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#0497a4")), index, index + customTitle.text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }else{
                            titleBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#111111")), index, index + customTitle.text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }else{
                        if("Y".equals(customTitle.point)){
                            titleBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#0497a4")), index, index + customTitle.text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            titleBuilder.setSpan(new StyleSpan(Typeface.BOLD), index, index + customTitle.text.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }else{
                            titleBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#111111")), index, index + customTitle.text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            titleBuilder.setSpan(new StyleSpan(Typeface.BOLD), index, index + customTitle.text.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }
                }
            }

            resultBuilder = userNameBuilder.append(titleBuilder);

            if(RTS_PRD_C_CST_SQ_VIEWTYPE.equals(sectionContent.viewType)){
                mTextTitle.setTextSize(16);
            }else{
                mTextTitle.setTextSize(18);
            }
            mTextTitle.setText(resultBuilder);
            mTextTitle.setVisibility(VISIBLE);
        }else{
            mTextTitle.setVisibility(GONE);
        }

        //서브타이틀 텍스트 붙이기
        //ex) customSubTitle 있는경우 : [#데일리룩, #편한, #부드러운]
        if(sectionContent.customSubTitle != null && sectionContent.customSubTitle.size() > 0){
            mTextSubTitle.setText("");//혹시나 subName 남아있을경우 대비 리셋
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder();

            for (SectionContentList.CustomSubTitle customSubTitle : sectionContent.customSubTitle) {
                if (DisplayUtils.isValidString(customSubTitle.text)) {
                    String addPaddingText = " " + customSubTitle.text + " "; //텍스트 패딩때문에 앞뒤여백 추가
                    stringBuilder.append(addPaddingText);

                    int index = stringBuilder.toString().indexOf(addPaddingText);

                    if("sharp".equals(customSubTitle.type)){
                        stringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#03919d")), index, index + addPaddingText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        stringBuilder.setSpan(new StyleSpan(Typeface.BOLD), index, index + addPaddingText.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        stringBuilder.setSpan(new BackgroundColorSpan(Color.parseColor("#effafb")), index, index + addPaddingText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }else{
                      //sharp 이외의 타입이 생길경우 처리
                    }
                }
                stringBuilder.append(" ");
            }

            mTextSubTitle.setText(stringBuilder);
            mTextSubTitle.setVisibility(VISIBLE);
        }
        //ex) subName 있는 경우(customSubTitle없고) : "실시간 스트리밍"
        else if(DisplayUtils.isValidString(sectionContent.subName)){
            mTextSubTitle.setText("");//혹시나 customSubTitle 남아있을경우 대비 리셋
            mTextSubTitle.setText(sectionContent.subName);
            mTextSubTitle.setTextSize(22);
            mTextSubTitle.setTextColor(Color.parseColor("#111111"));
            mTextSubTitle.setVisibility(VISIBLE);
        }else{
            mTextSubTitle.setVisibility(GONE);
        }
    }
}