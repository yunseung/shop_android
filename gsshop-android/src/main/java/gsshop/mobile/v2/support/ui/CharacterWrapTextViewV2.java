package gsshop.mobile.v2.support.ui;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * 문자단위로 줄바꿈하는 텍스트뷰
 */
public class CharacterWrapTextViewV2 extends AppCompatTextView {
    public CharacterWrapTextViewV2(Context context) {
        super(context);
    }

    public CharacterWrapTextViewV2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CharacterWrapTextViewV2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            sb.append(text.charAt(i));
            sb.append("\u200B");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        super.setText(sb, type);
    }
}
