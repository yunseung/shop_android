package gsshop.mobile.v2.support.ui;

import android.content.Context;
import android.graphics.Canvas;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;

import roboguice.util.Ln;

/**
 * TwoLineTextView2, setText를 불러서 텍스트를 지정할때 2 라인 텍스트로 만든다.
 */
public class TwoLineTextView2 extends AppCompatTextView {
    public TwoLineTextView2(Context context) {
        super(context);
    }

    public TwoLineTextView2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(drawTwoLine(text), type);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private CharSequence drawTwoLine(CharSequence charSequence) {
        String text = (String) charSequence;
        CharSequence twoLineText = text;
        int textWidth = getWidth();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        if (textWidth > 0) {
            // line break;
            int availableWidth = textWidth - (paddingLeft + paddingRight);
            // first line
            int end = getPaint().breakText(text, true, availableWidth, null);

            if (end < text.length()) {
                String firstLine = text.substring(0, end);
                String nextLine = text.substring(end);
                // second line
                end = getPaint().breakText(nextLine, true, availableWidth, null);
                if (end < nextLine.length()) {
                    nextLine = nextLine.substring(0, end);
                }
                if(!firstLine.contains("\n") && !nextLine.contains("\n")) {
                    twoLineText = firstLine + '\n' + nextLine.trim();
                }
            }
        }
        return twoLineText;
    }


}
