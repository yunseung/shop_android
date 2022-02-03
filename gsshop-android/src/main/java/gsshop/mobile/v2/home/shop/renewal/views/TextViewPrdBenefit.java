package gsshop.mobile.v2.home.shop.renewal.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import gsshop.mobile.v2.home.main.ImageBadge;
import gsshop.mobile.v2.home.main.SectionContentList;
import roboguice.util.Ln;

/**
 * Created by gsshop on 2016. 9. 7..
 */
public class TextViewPrdBenefit extends AppCompatTextView {
    private final static String BENEFITS_SEPERATOR_LINE = " | ";
    private final static String BENEFITS_SEPERATOR_DOT = " · ";
    // 볼드 처리인지 확인 위한 상수
    private final static String STYLETYPE_BOLD = "BOLD";
    private final static String COLOR_BENEFITS_DEFAULT = "80111111";
    
    public TextViewPrdBenefit(Context context) {
        super(context);
    }

    public TextViewPrdBenefit(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private SectionContentList mItem = null;

    public void setData(SectionContentList item) {
        mItem = item;

        // 한번 클리어
        
        SpannableStringBuilder strBuilderSourcing = new SpannableStringBuilder(); //[TV쇼핑], [백화점]
        SpannableStringBuilder strBuilderFooter = new SpannableStringBuilder(); //무료배송, 무이자

        if (item.allBenefit == null && item.source == null) {
            return;
        }

        if (item.allBenefit != null && !item.allBenefit.isEmpty()) {

            for (ImageBadge badge : item.allBenefit) {
                if (badge != null && badge.text != null && !badge.text.isEmpty()) {
                    if (strBuilderFooter.length() > 0) {
                        SpannableStringBuilder dot_stringBuilder = new SpannableStringBuilder();
                        dot_stringBuilder.append(BENEFITS_SEPERATOR_DOT);

                        //benefit_seperator_dot 색상 적용
                        dot_stringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#4c111111")), 0, BENEFITS_SEPERATOR_DOT.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        strBuilderFooter.append(dot_stringBuilder);
                    }
                    strBuilderFooter.append(badge.text);
                }
            }
        }

        if (item.source != null && (!item.source.text.isEmpty() || item.source.text == null)) {

            //소싱에 대한 컬러 확인 디파인 필요
            String sourceColor = COLOR_BENEFITS_DEFAULT;
            //유효하면 변경
            if (item.source.type != null && !item.source.type.isEmpty())
                sourceColor = item.source.type;

            strBuilderSourcing.append(item.source.text);

            // styleTpye 확인하여 BOLD처리
            if (STYLETYPE_BOLD.equals(item.source.styleType)) {
                strBuilderSourcing.setSpan(new StyleSpan(Typeface.BOLD), 0, item.source.text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            strBuilderSourcing.setSpan(new ForegroundColorSpan(Color.parseColor("#" + sourceColor)), 0, item.source.text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            if (strBuilderFooter.length() > 0) {
                //benefit_seperator_line 색상 지정
                SpannableStringBuilder seperator_stringBuilder = new SpannableStringBuilder();
                seperator_stringBuilder.append(BENEFITS_SEPERATOR_LINE);
                seperator_stringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#33111111")), 0, BENEFITS_SEPERATOR_LINE.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                strBuilderSourcing.append(seperator_stringBuilder);
            }
        }

        if (strBuilderFooter.length() > 0) {

            strBuilderSourcing.append(strBuilderFooter);

            String strText = strBuilderSourcing.toString();
            for (ImageBadge badge : item.allBenefit) {
                int index = strText.indexOf(badge.text);
                if (index >= 0 && !TextUtils.isEmpty(badge.type)) {

                    try {
                        strBuilderSourcing.setSpan(new ForegroundColorSpan(Color.parseColor("#" + badge.type)),
                                index, index + badge.text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    catch (IllegalArgumentException | IndexOutOfBoundsException e) {
                        Ln.e(e.getMessage());
                    }
                }

                if (STYLETYPE_BOLD.equals(badge.styleType)) {
                    try {
                        strBuilderSourcing.setSpan(new StyleSpan(Typeface.BOLD), index, index + badge.text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    catch (IllegalArgumentException | IndexOutOfBoundsException e) {
                        Ln.e(e.getMessage());
                    }
                }
            }
            this.setText(strBuilderSourcing);
        }
        else {
            this.setText(strBuilderSourcing);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int lineCnt = this.getLineCount();
        if (lineCnt > 1) {
            String full_text = this.getText().toString();
            //str0 = 0번째 줄의 텍스트
            String str0 = full_text.substring(this.getLayout().getLineStart(0), this.getLayout().getLineEnd(0));
            //str1 = 1번째 줄의 텍스트
            String str1 = full_text.substring(this.getLayout().getLineStart(1), this.getLayout().getLineEnd(1));


            //                  새로 그리는 타이밍에서 타이밍 이슈가 발생해서 두번 줄바꿈 하는 문제가 발생하는 것으로 보임 이에 \n이 있을 경우 줄바꿈 로직 수행하지 않음.
            if (str0.contains("\n") || str1.contains("\n")) {
                return;
            }

            //******************
            // 점 삭제하는 부분
            //******************

            if (str1.startsWith(" · ")) {
                str1 = str1.substring(3, str1.length());
            } else if (str1.startsWith("· ")) {
                str1 = str1.substring(2, str1.length());
            } else {
                if (str0.endsWith("· ")) {
                    str0 = str0.substring(0, str0.length() - 3);
                } else if (str0.endsWith("·")) {
                    str0 = str0.substring(0, str0.length() - 2);
                } else {
                    String tempStr = "";
                    int num = str0.length() - 2;
                    for (; num > 0; num--) {
                        tempStr = str0.substring(num, num + 2);
                        if (!TextUtils.isEmpty(tempStr) &&
                                ("· ".equals(tempStr) || " ·".equals(tempStr))) {
                            try {
                                String tempStr2 = str0.substring(0, num);
                                str1 = str0.substring(num + 2, str0.length()) + str1;
                                str0 = tempStr2;
                            } catch (IndexOutOfBoundsException e) {
                                Ln.e(e.getMessage());
                            }
                            break;
                        }
                    }
                }
            }

            // 가로가 짧아 줄바꿈이 한번 더 이루어질 경우를 대비하여 두번쨰 줄도 삭제
            if (str1.endsWith("· ")) {
                str1 = str1.substring(0, str1.length() - 3);
            } else if (str1.endsWith("·")) {
                str1 = str1.substring(0, str1.length() - 2);
            }

            this.setText(str0 + "\n" + str1);

            SpannableStringBuilder sourcingStringBuilder2 = new SpannableStringBuilder();
            String strText = this.getText().toString();
            sourcingStringBuilder2.append(strText);

            if (mItem == null) {
                this.setText(sourcingStringBuilder2);
                return;
            }

            //점처리 이후 소싱부분  Bold처리
            if (mItem.source != null && !mItem.source.text.isEmpty()) {

                //소싱에 대한 컬러 확인 디파인 해야 될것 같은디 ;;
                String sourceColor = "7a111111";
                //유효하면 변경
                if (mItem.source.type != null && !mItem.source.type.isEmpty()) {
                    sourceColor = mItem.source.type;
                }

                try {
                    if (STYLETYPE_BOLD.equals(mItem.source.styleType)) {
                        sourcingStringBuilder2.setSpan(new StyleSpan(Typeface.BOLD), 0,
                                mItem.source.text.length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    sourcingStringBuilder2.setSpan(new ForegroundColorSpan(Color.parseColor("#" + sourceColor)), 0,
                            mItem.source.text.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    //benefit_seperator_line 색상 지정
                    sourcingStringBuilder2.setSpan(new ForegroundColorSpan(Color.parseColor("#7a111111")),
                            mItem.source.text.length() + 1,
                            mItem.source.text.length() + 3,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                catch (IndexOutOfBoundsException e) {
                    Ln.e(e.getMessage());
                }
            }
            if (mItem.allBenefit.size() > 0) {
                for (ImageBadge badge : mItem.allBenefit) {
                    int index = strText.indexOf(badge.text);

                    if (index >= 0 && !TextUtils.isEmpty(badge.type)) {
                        try {
                            sourcingStringBuilder2.setSpan(new ForegroundColorSpan(Color.parseColor("#" + badge.type)),
                                    index, index + badge.text.length(),
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            //benefit_seperator_dot 색상 적용
                            sourcingStringBuilder2.setSpan(new ForegroundColorSpan(Color.parseColor("#7a111111")),
                                    index + badge.text.length() + 1, index + badge.text.length() + 3,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
                            Ln.e(e.getMessage());
                        }
                    }
                    if (STYLETYPE_BOLD.equals(badge.styleType)) {
                        try {
                            sourcingStringBuilder2.setSpan(new StyleSpan(Typeface.BOLD), index, index + badge.text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        catch (IllegalArgumentException | IndexOutOfBoundsException e) {
                            Ln.e(e.getMessage());
                        }
                    }
                }
            }
            // 무조건 setText 해도 된다. 여기 까지 온거는 이미 줄바꿈이 일어 난 것이기 때문에.
            this.setText(sourcingStringBuilder2);
        }
    }

}
