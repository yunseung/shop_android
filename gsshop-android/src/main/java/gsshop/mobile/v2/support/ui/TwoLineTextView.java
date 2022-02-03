package gsshop.mobile.v2.support.ui;

import android.content.Context;
import android.graphics.Canvas;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;

import roboguice.util.Ln;

/**
 * Created by gsshop on 2016. 9. 7..
 */
public class TwoLineTextView extends AppCompatTextView {
    public TwoLineTextView(Context context) {
        super(context);
    }

    public TwoLineTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawTwoLine();
    }

    private void drawTwoLine() {
        int textWidth = getWidth();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        if (textWidth > 0) {
            // line break;
            int availableWidth = textWidth - (paddingLeft + paddingRight);
            String text = "";
            try {
                // first line
                text = (String) getText();
            } catch (ClassCastException e) {
                Ln.e(e);
                return;
            }
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
                String twoLineText = firstLine + '\n' + nextLine.trim();
                    setText(twoLineText);
                }
            }
        }
    }


}
