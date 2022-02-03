package gsshop.mobile.v2.home.shop.renewal.views;

import android.content.Context;
import android.graphics.Canvas;
import androidx.appcompat.widget.AppCompatTextView;
import android.text.SpannedString;
import android.text.TextUtils;
import android.util.AttributeSet;

import static gsshop.mobile.v2.util.StringUtils.countSpaces;

/**
 * Created by gsshop on 2016. 9. 7..
 */
public class TwoLineTextViewSetEllipsizePossibly extends AppCompatTextView {
    /**
     * 라인 구분자
     */
    private static final String lineSep = System.getProperty("line.separator");

    public TwoLineTextViewSetEllipsizePossibly(Context context) {
        super(context);
    }

    public TwoLineTextViewSetEllipsizePossibly(Context context, AttributeSet attrs) {
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
        if (getWidth() <= 0) {
            return;
        }

        // 아래 setText로 인해 본함수 재호출시 스킵
        if(getText().toString().contains(lineSep)) {
            return;
        }

        if (getText() instanceof SpannedString) {
            //스패너블 스트링
            setSpannedString();
        } else {
            //일반 스트링
            setString();
        }
    }

    /**
     * 스트링을 처리한다.
     */
    private void setString() {
        int textWidth = getWidth();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();

        if (textWidth > 0) {
            // line break;
            int availableWidth = textWidth - (paddingLeft + paddingRight);
            // first line
            String text = (String) getText();
            int end = getPaint().breakText(text, true, availableWidth, null);

            if (end < text.length()) {
                String firstLine = text.substring(0, end);
                String nextLine = text.substring(end);
                String thirdLine = "";
                // second line
                end = getPaint().breakText(nextLine, true, availableWidth, null);
                if (end < nextLine.length()) {
                    nextLine = nextLine.substring(0, end);
                    thirdLine = text.substring(end);
                }

                if(!firstLine.contains("\n") && !nextLine.contains("\n")) {
                    String twoLineText = firstLine + '\n' + nextLine.trim();

                    // 위에서 두번째 라인까지 잘라버리니 ... 표시가 필요한 부분에서 정상적으로 표시되지 않는다.
                    if (thirdLine.length() > 0) {
                        twoLineText = twoLineText + '\n' + thirdLine;
                    }

                    setMaxLines(2);
                    setText(twoLineText);
                }
            }
        }
    }

    /**
     * Spannable 스트링을 처리한다.
     */
    private void setSpannedString() {
        int textWidth = getWidth();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();

        //가로폭
        int availableWidth = textWidth - (paddingLeft + paddingRight);

        //표시할 텍스트 취득
        SpannedString sText = (SpannedString)getText();

        //가로폭에 표시할 수 있는 마지막 텍스트 위치 취득
        int end1 = getPaint().breakText(sText.toString(), true, availableWidth, null);

        //가로폭에 모든 텍스트 표시가 가능한 경우 (두줄표시 필요없음)
        if (end1 >= sText.length()) {
            return;
        }

        CharSequence nextLine = sText.subSequence(end1, sText.length());
        CharSequence firstLine = sText.subSequence(0, end1);
        CharSequence thirdLine = "";

        int cnt = countSpaces(firstLine.toString());

        //breakText가 알려주는 표시가능 위치와 실제 표시될수 있는 텍스트가 상이한 경우 발생
        //firstLine내에 스페이스 3개 이상인 경우 (예: GS fresh 매실 양념 돼지불고기 400G×2팩)
        //위 경우에 해당하면 end1 임의 조정함
       if (cnt >= 3) {
            end1 = end1 - 1;
            firstLine = sText.subSequence(0, end1);
            nextLine = sText.subSequence(end1, sText.length());
        }
        //두번째 라인 가로폭에 표시할 수 있는 마지막 텍스트 위치 취득
        int end2 = getPaint().breakText(nextLine.toString(), true, availableWidth, null);

        //두번째 라인이 가로폭을 초과하면 초과하는 글자 제거
        if (end2 < nextLine.length()) {
            nextLine = nextLine.subSequence(0, end2);

            //텍스트 끝내 "..." 표시 필요하면 아래 로직 필요
            thirdLine = sText.subSequence(end1 + end2, sText.length());

            if (thirdLine.length() > 0) {
                nextLine = TextUtils.concat(nextLine, lineSep, thirdLine);
            }
        }

        //두번째 라인 스페이스 제거
        if (nextLine.toString().startsWith(" ")) {
            nextLine = nextLine.subSequence(1, end2);
        }

        //라인구분자 추가하여 텍스트 표시
        CharSequence cs = TextUtils.concat(firstLine, lineSep, nextLine);
        setText(cs);
    }
}
