package gsshop.mobile.v2.attach.customcamera;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class CameraAspectFrameLayout extends FrameLayout {

    private double targetAspectRatio = -1.0;        // initially use default window size

    public CameraAspectFrameLayout(Context context) {
        super(context);
    }

    public CameraAspectFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAspectRatio(double aspectRatio) {
        if (aspectRatio < 0) {
            throw new IllegalArgumentException();
        }

        if (targetAspectRatio != aspectRatio) {
            targetAspectRatio = aspectRatio;
            requestLayout();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (targetAspectRatio > 0) {
            int initialWidth = MeasureSpec.getSize(widthMeasureSpec);
            int initialHeight = MeasureSpec.getSize(heightMeasureSpec);

            // padding
            int horizontalPadding = getPaddingLeft() + getPaddingRight();
            int verticalPadding = getPaddingTop() + getPaddingBottom();
            initialWidth -= horizontalPadding;
            initialHeight -= verticalPadding;

            double viewAspectRatio = (double) initialWidth / initialHeight;
            double aspectDifference = targetAspectRatio / viewAspectRatio - 1;

            if (Math.abs(aspectDifference) < 0.01) {
                //no changes
            }
            else {
                int changedHeight = initialHeight;
                int changedWidth = initialWidth;

                if (aspectDifference > 0) {
                    changedHeight = (int) (initialWidth / targetAspectRatio);
                }
                else {
                    changedWidth = (int) (initialHeight * targetAspectRatio);
                }
                changedWidth += horizontalPadding;
                changedHeight += verticalPadding;

                if (aspectDifference > 0) {
                    changedWidth *= ((float) initialHeight / (float) changedHeight);
                }
                else {
                    changedHeight *= ((float) initialWidth / (float) changedWidth);
                }

                if (changedWidth > initialWidth) {
                    initialWidth = changedWidth;
                }

                if (changedHeight > initialHeight) {
                    initialHeight = changedHeight;
                }

                widthMeasureSpec = MeasureSpec.makeMeasureSpec(initialWidth, MeasureSpec.EXACTLY);
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(initialHeight, MeasureSpec.EXACTLY);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
