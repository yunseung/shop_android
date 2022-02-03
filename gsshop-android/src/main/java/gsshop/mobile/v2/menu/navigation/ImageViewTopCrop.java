package gsshop.mobile.v2.menu.navigation;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 이미지뷰에서 상단바 영역과 오른쪽 영역을 crop해서 보여주기 위한 imageView
 */
public class ImageViewTopCrop extends ImageView
{
    int status_bar_height = 0;

    int fitWidth = 0;

    public ImageViewTopCrop(Context context)
    {
        super(context);
        getStatusBarHeight();
        setScaleType(ScaleType.MATRIX);
    }

    public ImageViewTopCrop(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        getStatusBarHeight();
        setScaleType(ScaleType.MATRIX);
    }

    public ImageViewTopCrop(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        getStatusBarHeight();
        setScaleType(ScaleType.MATRIX);
    }

    /**
     * 상단바 크기를 구한다.
     */
    private void getStatusBarHeight(){
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            status_bar_height = getResources().getDimensionPixelSize(resourceId);
        }

        fitWidth =  (int)(getResources().getDisplayMetrics().widthPixels * 90.0 / 100.0);
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        if (getDrawable() == null) {
            return super.setFrame(l, t, r, b);
        }
        Matrix matrix = getImageMatrix();

        float scale;
        int viewWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int viewHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        int drawableWidth = getDrawable().getIntrinsicWidth();
        int drawableHeight = getDrawable().getIntrinsicHeight();

        if (drawableWidth * viewHeight > drawableHeight * viewWidth) {
            scale = (float) viewHeight / (float) drawableHeight;
        } else {
            scale = (float) viewWidth / (float) drawableWidth;
        }

        //상단바영역과 오른쪽영역을 제외하고 매트릭스를 설정한다.
        RectF drawableRect = new RectF(0, drawableHeight - (viewHeight / scale), fitWidth, drawableHeight);
        RectF viewRect = new RectF(0, -status_bar_height, viewWidth, viewHeight);
        matrix.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.FILL);


        setImageMatrix(matrix);

        return super.setFrame(l, t, r, b);
    }
}
