package gsshop.mobile.v2.menu.navigation.Views;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * 터치시 줄어드는 뷰, 현재 필요에 의해 Cardview, ImageView 만들었으나 동일한 기능.
 * 후에 추가 확장해서 다른 종류의 뷰에서 쓰일 경우 상속 받아 사용 가능하게끔 수정.
 */
public class ImageViewReduceWhenSelected extends ImageView {

    public ImageViewReduceWhenSelected(Context context) {
        super(context);
    }

    public ImageViewReduceWhenSelected(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageViewReduceWhenSelected(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Path path = new Path();
        path.addRoundRect(new RectF(0, 0, canvas.getWidth(), canvas.getHeight()), 50, 50, Path.Direction.CW);

        canvas.clipPath(path);
        super.onDraw(canvas);
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
