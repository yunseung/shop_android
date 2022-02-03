package gsshop.mobile.v2.home.util.crop.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.ImageView;

import gsshop.mobile.v2.home.util.crop.RotateBitmap;

abstract public class ImageViewTouchBase extends ImageView {
    private static final float SCALE_RATE = 1.25F;
    protected Matrix baseMatrix = new Matrix();
    protected Matrix suppMatrix = new Matrix();
    private final Matrix displayMatrix = new Matrix();
    private final float[] matrixValues = new float[9];
    protected final RotateBitmap bitmapDisplayed = new RotateBitmap((Bitmap)null, 0);
    int thisWidth = -1;
    int thisHeight = -1;
    float maxZoom;
    private Runnable onLayoutRunnable;
    protected Handler handler = new Handler();
    private Recycler recycler;

    public ImageViewTouchBase(Context context) {
        super(context);
        this.init();
    }

    public ImageViewTouchBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public ImageViewTouchBase(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.init();
    }

    public void setRecycler(Recycler recycler) {
        this.recycler = recycler;
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.thisWidth = right - left;
        this.thisHeight = bottom - top;
        Runnable r = this.onLayoutRunnable;
        if (r != null) {
            this.onLayoutRunnable = null;
            r.run();
        }

        if (this.bitmapDisplayed.getBitmap() != null) {
            this.getProperBaseMatrix(this.bitmapDisplayed, this.baseMatrix, true);
            this.setImageMatrix(this.getImageViewMatrix());
        }

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4 && event.getRepeatCount() == 0) {
            event.startTracking();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == 4 && event.isTracking() && !event.isCanceled() && this.getScale() > 1.0F) {
            this.zoomTo(1.0F);
            return true;
        } else {
            return super.onKeyUp(keyCode, event);
        }
    }

    public void setImageBitmap(Bitmap bitmap) {
        this.setImageBitmap(bitmap, 0);
    }

    private void setImageBitmap(Bitmap bitmap, int rotation) {
        super.setImageBitmap(bitmap);
        Drawable d = this.getDrawable();
        if (d != null) {
            d.setDither(true);
        }

        Bitmap old = this.bitmapDisplayed.getBitmap();
        this.bitmapDisplayed.setBitmap(bitmap);
        this.bitmapDisplayed.setRotation(rotation);
        if (old != null && old != bitmap && this.recycler != null) {
            this.recycler.recycle(old);
        }

    }

    public void clear() {
        this.setImageBitmapResetBase((Bitmap)null, true);
    }

    public void setImageBitmapResetBase(Bitmap bitmap, boolean resetSupp) {
        this.setImageRotateBitmapResetBase(new RotateBitmap(bitmap, 0), resetSupp);
    }

    public void setImageRotateBitmapResetBase(final RotateBitmap bitmap, final boolean resetSupp) {
        int viewWidth = this.getWidth();
        if (viewWidth <= 0) {
            this.onLayoutRunnable = new Runnable() {
                public void run() {
                    setImageRotateBitmapResetBase(bitmap, resetSupp);
                }
            };
        } else {
            if (bitmap.getBitmap() != null) {
                this.getProperBaseMatrix(bitmap, this.baseMatrix, true);
                this.setImageBitmap(bitmap.getBitmap(), bitmap.getRotation());
            } else {
                this.baseMatrix.reset();
                this.setImageBitmap((Bitmap)null);
            }

            if (resetSupp) {
                this.suppMatrix.reset();
            }

            this.setImageMatrix(this.getImageViewMatrix());
            this.maxZoom = this.calculateMaxZoom();
        }
    }

    public void center() {
        Bitmap bitmap = this.bitmapDisplayed.getBitmap();
        if (bitmap != null) {
            Matrix m = this.getImageViewMatrix();
            RectF rect = new RectF(0.0F, 0.0F, (float)bitmap.getWidth(), (float)bitmap.getHeight());
            m.mapRect(rect);
            float height = rect.height();
            float width = rect.width();
            float deltaX = 0.0F;
            float deltaY = 0.0F;
            deltaY = this.centerVertical(rect, height, deltaY);
            deltaX = this.centerHorizontal(rect, width, deltaX);
            this.postTranslate(deltaX, deltaY);
            this.setImageMatrix(this.getImageViewMatrix());
        }
    }

    private float centerVertical(RectF rect, float height, float deltaY) {
        int viewHeight = this.getHeight();
        if (height < (float)viewHeight) {
            deltaY = ((float)viewHeight - height) / 2.0F - rect.top;
        } else if (rect.top > 0.0F) {
            deltaY = -rect.top;
        } else if (rect.bottom < (float)viewHeight) {
            deltaY = (float)this.getHeight() - rect.bottom;
        }

        return deltaY;
    }

    private float centerHorizontal(RectF rect, float width, float deltaX) {
        int viewWidth = this.getWidth();
        if (width < (float)viewWidth) {
            deltaX = ((float)viewWidth - width) / 2.0F - rect.left;
        } else if (rect.left > 0.0F) {
            deltaX = -rect.left;
        } else if (rect.right < (float)viewWidth) {
            deltaX = (float)viewWidth - rect.right;
        }

        return deltaX;
    }

    private void init() {
        this.setScaleType(ScaleType.MATRIX);
    }

    public float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(this.matrixValues);
        return this.matrixValues[whichValue];
    }

    public float getScale(Matrix matrix) {
        return this.getValue(matrix, 0);
    }

    public float getScale() {
        return this.getScale(this.suppMatrix);
    }

    private void getProperBaseMatrix(RotateBitmap bitmap, Matrix matrix, boolean includeRotation) {
        float viewWidth = (float)this.getWidth();
        float viewHeight = (float)this.getHeight();
        float w = (float)bitmap.getWidth();
        float h = (float)bitmap.getHeight();
        matrix.reset();
        float widthScale = Math.min(viewWidth / w, 3.0F);
        float heightScale = Math.min(viewHeight / h, 3.0F);
        float scale = Math.min(widthScale, heightScale);
        if (includeRotation) {
            matrix.postConcat(bitmap.getRotateMatrix());
        }

        matrix.postScale(scale, scale);
        matrix.postTranslate((viewWidth - w * scale) / 2.0F, (viewHeight - h * scale) / 2.0F);
    }

    protected Matrix getImageViewMatrix() {
        this.displayMatrix.set(this.baseMatrix);
        this.displayMatrix.postConcat(this.suppMatrix);
        return this.displayMatrix;
    }

    public Matrix getUnrotatedMatrix() {
        Matrix unrotated = new Matrix();
        this.getProperBaseMatrix(this.bitmapDisplayed, unrotated, false);
        unrotated.postConcat(this.suppMatrix);
        return unrotated;
    }

    protected float calculateMaxZoom() {
        if (this.bitmapDisplayed.getBitmap() == null) {
            return 1.0F;
        } else {
            float fw = (float)this.bitmapDisplayed.getWidth() / (float)this.thisWidth;
            float fh = (float)this.bitmapDisplayed.getHeight() / (float)this.thisHeight;
            return Math.max(fw, fh) * 4.0F;
        }
    }

    protected void zoomTo(float scale, float centerX, float centerY) {
        if (scale > this.maxZoom) {
            scale = this.maxZoom;
        }

        float oldScale = this.getScale();
        float deltaScale = scale / oldScale;
        this.suppMatrix.postScale(deltaScale, deltaScale, centerX, centerY);
        this.setImageMatrix(this.getImageViewMatrix());
        this.center();
    }

    protected void zoomTo(float scale, final float centerX, final float centerY, final float durationMs) {
        final float incrementPerMs = (scale - this.getScale()) / durationMs;
        final float oldScale = this.getScale();
        final long startTime = System.currentTimeMillis();
        this.handler.post(new Runnable() {
            public void run() {
                long now = System.currentTimeMillis();
                float currentMs = Math.min(durationMs, (float)(now - startTime));
                float target = oldScale + incrementPerMs * currentMs;
                zoomTo(target, centerX, centerY);
                if (currentMs < durationMs) {
                    handler.post(this);
                }

            }
        });
    }

    protected void zoomTo(float scale) {
        float cx = (float)this.getWidth() / 2.0F;
        float cy = (float)this.getHeight() / 2.0F;
        this.zoomTo(scale, cx, cy);
    }

    protected void zoomIn() {
        this.zoomIn(1.25F);
    }

    protected void zoomOut() {
        this.zoomOut(1.25F);
    }

    protected void zoomIn(float rate) {
        if (this.getScale() < this.maxZoom) {
            if (this.bitmapDisplayed.getBitmap() != null) {
                float cx = (float)this.getWidth() / 2.0F;
                float cy = (float)this.getHeight() / 2.0F;
                this.suppMatrix.postScale(rate, rate, cx, cy);
                this.setImageMatrix(this.getImageViewMatrix());
            }
        }
    }

    protected void zoomOut(float rate) {
        if (this.bitmapDisplayed.getBitmap() != null) {
            float cx = (float)this.getWidth() / 2.0F;
            float cy = (float)this.getHeight() / 2.0F;
            Matrix tmp = new Matrix(this.suppMatrix);
            tmp.postScale(1.0F / rate, 1.0F / rate, cx, cy);
            if (this.getScale(tmp) < 1.0F) {
                this.suppMatrix.setScale(1.0F, 1.0F, cx, cy);
            } else {
                this.suppMatrix.postScale(1.0F / rate, 1.0F / rate, cx, cy);
            }

            this.setImageMatrix(this.getImageViewMatrix());
            this.center();
        }
    }

    protected void postTranslate(float dx, float dy) {
        this.suppMatrix.postTranslate(dx, dy);
    }

    protected void panBy(float dx, float dy) {
        this.postTranslate(dx, dy);
        this.setImageMatrix(this.getImageViewMatrix());
    }

    public interface Recycler {
        void recycle(Bitmap var1);
    }
}
