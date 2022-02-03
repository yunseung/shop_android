package gsshop.mobile.v2.menu.navigation.Views;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import androidx.cardview.widget.CardView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CardViewReduceWhenSelected extends CardView {
    public CardViewReduceWhenSelected(Context context) {
        super(context);
    }

    public CardViewReduceWhenSelected(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CardViewReduceWhenSelected(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            reduceWithAnim();
        }
        else if (event.getAction() == MotionEvent.ACTION_UP ||
                event.getAction() == MotionEvent.ACTION_CANCEL) {
            growWithAnim();
        }
        return true;
    }

    private void growWithAnim() {
        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(this,
                "scaleX", 1f);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(this,
                "scaleY", 1f);
        scaleUpX.setDuration(300);
        scaleUpY.setDuration(300);
        AnimatorSet scaleUp = new AnimatorSet();

        scaleUp.play(scaleUpX).with(scaleUpY);
        scaleUp.start();
    }

    private void reduceWithAnim() {
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(this,
                "scaleX", 0.95f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(this,
                "scaleY", 0.95f);
        scaleDownX.setDuration(200);
        scaleDownY.setDuration(200);
        AnimatorSet scaleDown = new AnimatorSet();

        scaleDown.play(scaleDownX).with(scaleDownY);
        scaleDown.start();
    }

    public void setIniSize() {
        this.setScaleX(1f);
        this.setScaleY(1f);
    }

}
