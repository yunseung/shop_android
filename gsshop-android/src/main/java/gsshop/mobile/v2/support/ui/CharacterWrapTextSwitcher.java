package gsshop.mobile.v2.support.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextSwitcher;

public class CharacterWrapTextSwitcher extends TextSwitcher {
    public CharacterWrapTextSwitcher(Context context) {
        super(context);
    }

    public CharacterWrapTextSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setText(CharSequence text) {
        super.setText(text.toString().replace(" ", "\u00A0"));
    }
}
