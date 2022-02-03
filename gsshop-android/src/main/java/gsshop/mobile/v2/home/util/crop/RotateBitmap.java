package gsshop.mobile.v2.home.util.crop;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class RotateBitmap {
    private Bitmap bitmap;
    private int rotation;

    public RotateBitmap(Bitmap bitmap, int rotation) {
        this.bitmap = bitmap;
        this.rotation = rotation % 360;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public int getRotation() {
        return this.rotation;
    }

    public Bitmap getBitmap() {
        return this.bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Matrix getRotateMatrix() {
        Matrix matrix = new Matrix();
        if (this.bitmap != null && this.rotation != 0) {
            int cx = this.bitmap.getWidth() / 2;
            int cy = this.bitmap.getHeight() / 2;
            matrix.preTranslate((float)(-cx), (float)(-cy));
            matrix.postRotate((float)this.rotation);
            matrix.postTranslate((float)(this.getWidth() / 2), (float)(this.getHeight() / 2));
        }

        return matrix;
    }

    public boolean isOrientationChanged() {
        return this.rotation / 90 % 2 != 0;
    }

    public int getHeight() {
        if (this.bitmap == null) {
            return 0;
        } else {
            return this.isOrientationChanged() ? this.bitmap.getWidth() : this.bitmap.getHeight();
        }
    }

    public int getWidth() {
        if (this.bitmap == null) {
            return 0;
        } else {
            return this.isOrientationChanged() ? this.bitmap.getHeight() : this.bitmap.getWidth();
        }
    }

    public void recycle() {
        if (this.bitmap != null) {
            this.bitmap.recycle();
            this.bitmap = null;
        }

    }
}
