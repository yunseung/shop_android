package gsshop.mobile.v2.support.ui;

import android.content.Context;
import android.graphics.Canvas;
import androidx.appcompat.widget.AppCompatTextView;
import android.text.SpannedString;
import android.text.TextUtils;
import android.util.AttributeSet;

/**
 * 글자단위 두줄표시 클래스.
 * SpannableStringBuilder 사용하여 setText 하는 경우 사용함.
 * 두번째 이상 텍스트 플래그는 다음과 같이 세팅 필요 (Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
 *
 * Examples,
 * <pre>
 * <code>
 *  String txt1 = "택배배송 가능, 새벽배송 불가한 배송지인 경우";
 * 	String txt2 = "7시 새벽도착 (추가 배송비 2,000원)";
 * 	SpannableStringBuilder sBuilder = new SpannableStringBuilder();
 * 	sBuilder.append(txt1).append(txt2);
 * 	sBuilder.setSpan(new TextAppearanceSpan(context, R.style.Green), 0, txt1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
 * 	sBuilder.setSpan(new TextAppearanceSpan(context, R.style.Red), txt1.length(), txt1.length() + txt2.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
 * 	txtTitle.setText(sBuilder);
 * </code>
 * </pre>
 */
public class TwoLineSpannableTextView extends AppCompatTextView {
    /**
     * 라인 구분자
     */
    private static final String lineSep = System.getProperty("line.separator");

    public TwoLineSpannableTextView(Context context) {
        super(context);
    }

    public TwoLineSpannableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawTwoLine();
    }

    /**
     * 텍스트뷰 가로폭에 맞게 라인 구분자를 추가한다.
     */
    private void drawTwoLine() {
        int textWidth = getWidth();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();

        if (textWidth <= 0) {
            return;
        }

        //가로폭
        int availableWidth = textWidth - (paddingLeft + paddingRight);

        // 아래 setText로 인해 본함수 재호출시 스킵
        if(getText().toString().contains(lineSep)) {
            return;
        }

        //표시할 텍스트 취득
        SpannedString sText = (SpannedString)getText();

        //가로폭에 표시할 수 있는 마지막 텍스트 위치 취득
        int end = getPaint().breakText(sText.toString(), true, availableWidth, null);

        //가로폭에 모든 텍스트 표시가 가능한 경우 (두줄표시 필요없음)
        if (end >= sText.length()) {
            return;
        }

        CharSequence nextLine = sText.subSequence(end, sText.length());
        CharSequence firstLine = sText.subSequence(0, end);

        //두번째 라인 가로폭에 표시할 수 있는 마지막 텍스트 위치 취득
        end = getPaint().breakText(nextLine.toString(), true, availableWidth, null);
        //두번째 라인이 가로폭을 초과하면 초과하는 글자 제거
        if (end < nextLine.length()) {
            nextLine = nextLine.subSequence(0, end);
        }

        //라인구분자 추가하여 텍스트 표시
        CharSequence cs = TextUtils.concat(firstLine, lineSep, nextLine);
        setText(cs);
    }
}
