package gsshop.mobile.v2.home.util.crop.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Iterator;

import gsshop.mobile.v2.home.util.crop.CropActivity;

public class CropImageView extends ImageViewTouchBase {
    public ArrayList<HighlightView> highlightViews = new ArrayList();
    public HighlightView motionHighlightView;
    Context context;
    private float lastX;
    private float lastY;
    private int motionEdge;
    private int validPointerId;

    public CropImageView(Context context) {
        super(context);
    }

    public CropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CropImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setViewContext(Context context) {
        this.context = context;
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (this.bitmapDisplayed.getBitmap() != null) {
            Iterator var6 = this.highlightViews.iterator();

            while(var6.hasNext()) {
                HighlightView hv = (HighlightView)var6.next();
                hv.matrix.set(this.getUnrotatedMatrix());
                hv.invalidate();
                if (hv.hasFocus()) {
                    this.centerBasedOnHighlightView(hv);
                }
            }
        }

    }

    protected void zoomTo(float scale, float centerX, float centerY) {
        super.zoomTo(scale, centerX, centerY);
        Iterator var4 = this.highlightViews.iterator();

        while(var4.hasNext()) {
            HighlightView hv = (HighlightView)var4.next();
            hv.matrix.set(this.getUnrotatedMatrix());
            hv.invalidate();
        }

    }

    protected void zoomIn() {
        super.zoomIn();
        Iterator var1 = this.highlightViews.iterator();

        while(var1.hasNext()) {
            HighlightView hv = (HighlightView)var1.next();
            hv.matrix.set(this.getUnrotatedMatrix());
            hv.invalidate();
        }

    }

    protected void zoomOut() {
        super.zoomOut();
        Iterator var1 = this.highlightViews.iterator();

        while(var1.hasNext()) {
            HighlightView hv = (HighlightView)var1.next();
            hv.matrix.set(this.getUnrotatedMatrix());
            hv.invalidate();
        }

    }

    protected void postTranslate(float deltaX, float deltaY) {
        super.postTranslate(deltaX, deltaY);
        Iterator var3 = this.highlightViews.iterator();

        while(var3.hasNext()) {
            HighlightView hv = (HighlightView)var3.next();
            hv.matrix.postTranslate(deltaX, deltaY);
            hv.invalidate();
        }

    }

    public boolean onTouchEvent(@NonNull MotionEvent event) {
        CropActivity cropImageActivity = (CropActivity)this.context;
        if (cropImageActivity.isSaving()) {
            return false;
        } else {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Iterator var3 = this.highlightViews.iterator();

                    HighlightView hv;
                    int edge;
                    do {
                        if (!var3.hasNext()) {
                            return true;
                        }

                        hv = (HighlightView)var3.next();
                        edge = hv.getHit(event.getX(), event.getY());
                    } while(edge == 1);

                    this.motionEdge = edge;
                    this.motionHighlightView = hv;
                    this.lastX = event.getX();
                    this.lastY = event.getY();
                    this.validPointerId = event.getPointerId(event.getActionIndex());
                    this.motionHighlightView.setMode(edge == 32 ? HighlightView.ModifyMode.Move : HighlightView.ModifyMode.Grow);
                    break;
                case MotionEvent.ACTION_UP:
                    if (this.motionHighlightView != null) {
                        this.centerBasedOnHighlightView(this.motionHighlightView);
                        this.motionHighlightView.setMode(HighlightView.ModifyMode.None);
                    }

                    this.motionHighlightView = null;
                    this.center();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (this.motionHighlightView != null && event.getPointerId(event.getActionIndex()) == this.validPointerId) {
                        this.motionHighlightView.handleMotion(this.motionEdge, event.getX() - this.lastX, event.getY() - this.lastY);
                        this.lastX = event.getX();
                        this.lastY = event.getY();
                    }

                    if (this.getScale() == 1.0F) {
                        this.center();
                    }
            }

            return true;
        }
    }

    private void ensureVisible(HighlightView hv) {
        Rect r = hv.drawRect;
        int panDeltaX1 = Math.max(0, this.getLeft() - r.left);
        int panDeltaX2 = Math.min(0, this.getRight() - r.right);
        int panDeltaY1 = Math.max(0, this.getTop() - r.top);
        int panDeltaY2 = Math.min(0, this.getBottom() - r.bottom);
        int panDeltaX = panDeltaX1 != 0 ? panDeltaX1 : panDeltaX2;
        int panDeltaY = panDeltaY1 != 0 ? panDeltaY1 : panDeltaY2;
        if (panDeltaX != 0 || panDeltaY != 0) {
            this.panBy((float)panDeltaX, (float)panDeltaY);
        }

    }

    private void centerBasedOnHighlightView(HighlightView hv) {
        Rect drawRect = hv.drawRect;
        float width = (float)drawRect.width();
        float height = (float)drawRect.height();
        float thisWidth = (float)this.getWidth();
        float thisHeight = (float)this.getHeight();
        float z1 = thisWidth / width * 0.6F;
        float z2 = thisHeight / height * 0.6F;
        float zoom = Math.min(z1, z2);
        zoom *= this.getScale();
        zoom = Math.max(1.0F, zoom);
        if ((double)(Math.abs(zoom - this.getScale()) / zoom) > 0.1D) {
            float[] coordinates = new float[]{hv.cropRect.centerX(), hv.cropRect.centerY()};
            this.getUnrotatedMatrix().mapPoints(coordinates);
            this.zoomTo(zoom, coordinates[0], coordinates[1], 300.0F);
        }

        this.ensureVisible(hv);
    }

    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        Iterator var2 = this.highlightViews.iterator();

        while(var2.hasNext()) {
            HighlightView highlightView = (HighlightView)var2.next();
            highlightView.draw(canvas);
        }

    }

    public void add(HighlightView hv) {
        this.highlightViews.add(hv);
        this.invalidate();
    }
}
