package gsshop.mobile.v2.home.util.crop.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.util.DisplayUtils;
import roboguice.util.Ln;

public class HighlightView {
    public static final int GROW_NONE = 1;
    public static final int GROW_LEFT_EDGE = 2;
    public static final int GROW_RIGHT_EDGE = 4;
    public static final int GROW_TOP_EDGE = 8;
    public static final int GROW_BOTTOM_EDGE = 16;
    public static final int MOVE = 32;
    private static final int DEFAULT_HIGHLIGHT_COLOR = -13388315;
    private static final float HANDLE_RADIUS_DP = 12.0F;
    private static final float OUTLINE_DP = 2.0F;
    RectF cropRect;
    Rect drawRect;
    Matrix matrix;
    private RectF imageRect;
    private final Paint outsidePaint = new Paint();
    private final Paint outlinePaint = new Paint();
    private final Paint handlePaint = new Paint();
    private View viewContext;
    private boolean showThirds;
    private boolean showCircle;
    private int highlightColor;
    private ModifyMode modifyMode;
    private HandleMode handleMode;
    private boolean maintainAspectRatio;
    private float initialAspectRatio;
//    private float handleRadius;
    private float outlineWidth;
    private boolean isFocused;

    public HighlightView(View context) {
        this.modifyMode = ModifyMode.None;
        this.handleMode = HandleMode.Always;
        this.viewContext = context;
        this.initStyles(context.getContext());
    }

    private void initStyles(Context context) {
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.cropImageStyle, outValue, true);
        TypedArray attributes = context.obtainStyledAttributes(outValue.resourceId, R.styleable.CropImageView);

        try {
            this.showThirds = attributes.getBoolean(R.styleable.CropImageView_showThirds, false);
            this.showCircle = attributes.getBoolean(R.styleable.CropImageView_showCircle, false);
            this.highlightColor = attributes.getColor(R.styleable.CropImageView_highlightColor, Color.WHITE);
            this.handleMode = HandleMode.values()[attributes.getInt(R.styleable.CropImageView_showHandles,
                    1)]; //0 : change, 1 : always, 2 : never
        } finally {
            attributes.recycle();
        }

    }

    public void setup(Matrix m, Rect imageRect, RectF cropRect, boolean maintainAspectRatio) {
        this.matrix = new Matrix(m);
        this.cropRect = cropRect;
        this.imageRect = new RectF(imageRect);
        this.maintainAspectRatio = maintainAspectRatio;
        this.initialAspectRatio = this.cropRect.width() / this.cropRect.height();
        this.drawRect = this.computeLayout();
        this.outsidePaint.setARGB(90, 0, 0, 0);
        this.outlinePaint.setStyle(Paint.Style.STROKE);
        this.outlinePaint.setAntiAlias(true);
        this.outlineWidth = this.dpToPx(0);
        this.handlePaint.setColor(this.highlightColor);
        this.handlePaint.setStyle(Paint.Style.FILL);
        this.handlePaint.setAntiAlias(true);
//        this.handleRadius = this.dpToPx(5.0F); // 반지름 5
        this.modifyMode = ModifyMode.None;
    }

    private float dpToPx(float dp) {
        return dp * this.viewContext.getResources().getDisplayMetrics().density;
    }

    protected void draw(Canvas canvas) {
        canvas.save();
        Path path = new Path();
        this.outlinePaint.setStrokeWidth(this.outlineWidth);
        if (!this.hasFocus()) {
            this.outlinePaint.setColor(Color.TRANSPARENT);
            canvas.drawRect(this.drawRect, this.outlinePaint);
        } else {
            Rect viewDrawingRect = new Rect();
            this.viewContext.getDrawingRect(viewDrawingRect);
            path.addRect(new RectF(this.drawRect), Path.Direction.CW);
            this.outlinePaint.setColor(Color.TRANSPARENT);
            if (this.isClipPathSupported(canvas)) {
                canvas.clipPath(path, Region.Op.DIFFERENCE);
                canvas.drawRect(viewDrawingRect, this.outsidePaint);
            } else {
                this.drawOutsideFallback(canvas);
            }

            canvas.restore();
            canvas.drawPath(path, this.outlinePaint);
            if (this.showThirds) {
                this.drawThirds(canvas);
            }

            if (this.showCircle) {
                this.drawCircle(canvas);
            }

            if (this.handleMode == HandleMode.Always || this.handleMode == HandleMode.Changing && this.modifyMode == ModifyMode.Grow) {
                this.drawHandles(canvas);
            }

        }

    }

    private void drawOutsideFallback(Canvas canvas) {
        canvas.drawRect(0.0F, 0.0F, (float)canvas.getWidth(), (float)this.drawRect.top, this.outsidePaint);
        canvas.drawRect(0.0F, (float)this.drawRect.bottom, (float)canvas.getWidth(), (float)canvas.getHeight(), this.outsidePaint);
        canvas.drawRect(0.0F, (float)this.drawRect.top, (float)this.drawRect.left, (float)this.drawRect.bottom, this.outsidePaint);
        canvas.drawRect((float)this.drawRect.right, (float)this.drawRect.top, (float)canvas.getWidth(), (float)this.drawRect.bottom, this.outsidePaint);
    }

    @SuppressLint({"NewApi"})
    private boolean isClipPathSupported(Canvas canvas) {
        if (Build.VERSION.SDK_INT == 17) {
            return false;
        } else if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT <= 15) {
            return !canvas.isHardwareAccelerated();
        } else {
            return true;
        }
    }

    /*
    private void drawHandles(Canvas canvas) {
        canvas.drawCircle((float)this.drawRect.left, (float)this.drawRect.top, this.handleRadius, this.handlePaint);
        canvas.drawCircle((float)this.drawRect.right, (float)this.drawRect.top, this.handleRadius, this.handlePaint);
        canvas.drawCircle((float)this.drawRect.left, (float)this.drawRect.bottom, this.handleRadius, this.handlePaint);
        canvas.drawCircle((float)this.drawRect.right, (float)this.drawRect.bottom, this.handleRadius, this.handlePaint);
    }
    */

    private void drawHandles(Canvas canvas) {
        Bitmap bitmapLT = BitmapFactory.decodeResource(viewContext.getResources(), R.drawable.frame_lt);
        Bitmap bitmapRT = BitmapFactory.decodeResource(viewContext.getResources(), R.drawable.frame_rt);
        Bitmap bitmapLB = BitmapFactory.decodeResource(viewContext.getResources(), R.drawable.frame_lb);
        Bitmap bitmapRB = BitmapFactory.decodeResource(viewContext.getResources(), R.drawable.frame_rb);

        int cornerWH = DisplayUtils.convertDpToPx(viewContext.getContext(), 15);

        /*
        Rect rectDstLT = new Rect(this.drawRect.left, this.drawRect.top, cornerWH, cornerWH);
        Rect rectDstRT = new Rect(this.drawRect.right - cornerWH, this.drawRect.top, cornerWH, cornerWH);
        Rect rectDstLB = new Rect(this.drawRect.left, this.drawRect.bottom - cornerWH, cornerWH, cornerWH);
        Rect rectDstRB = new Rect(this.drawRect.right - cornerWH, this.drawRect.bottom - cornerWH, cornerWH, cornerWH);

        canvas.drawBitmap(bitmapLT, null, rectDstLT, null);
        canvas.drawBitmap(bitmapRT, null, rectDstRT, null);
        canvas.drawBitmap(bitmapLB, null, rectDstLB, null);
        canvas.drawBitmap(bitmapRB, null, rectDstRB, null);
        */

        bitmapLT = Bitmap.createScaledBitmap(bitmapLT, DisplayUtils.convertDpToPx(viewContext.getContext(), 15),
                DisplayUtils.convertDpToPx(viewContext.getContext(), 15), true);
        bitmapRT = Bitmap.createScaledBitmap(bitmapRT, DisplayUtils.convertDpToPx(viewContext.getContext(), 15),
                DisplayUtils.convertDpToPx(viewContext.getContext(), 15), true);
        bitmapLB = Bitmap.createScaledBitmap(bitmapLB, DisplayUtils.convertDpToPx(viewContext.getContext(), 15),
                DisplayUtils.convertDpToPx(viewContext.getContext(), 15), true);
        bitmapRB = Bitmap.createScaledBitmap(bitmapRB, DisplayUtils.convertDpToPx(viewContext.getContext(), 15),
                DisplayUtils.convertDpToPx(viewContext.getContext(), 15), true);

        canvas.drawBitmap(bitmapLT, this.drawRect.left, this.drawRect.top, null);
        canvas.drawBitmap(bitmapRT, this.drawRect.right - cornerWH, this.drawRect.top, null);
        canvas.drawBitmap(bitmapLB, this.drawRect.left, this.drawRect.bottom - cornerWH, null);
        canvas.drawBitmap(bitmapRB, this.drawRect.right - cornerWH, this.drawRect.bottom - cornerWH, null);

    }

    private void drawThirds(Canvas canvas) {
        this.outlinePaint.setStrokeWidth(0);
        float xThird = (float)((this.drawRect.right - this.drawRect.left) / 3);
        float yThird = (float)((this.drawRect.bottom - this.drawRect.top) / 3);
        canvas.drawLine((float)this.drawRect.left + xThird, (float)this.drawRect.top, (float)this.drawRect.left + xThird, (float)this.drawRect.bottom, this.outlinePaint);
        canvas.drawLine((float)this.drawRect.left + xThird * 2.0F, (float)this.drawRect.top, (float)this.drawRect.left + xThird * 2.0F, (float)this.drawRect.bottom, this.outlinePaint);
        canvas.drawLine((float)this.drawRect.left, (float)this.drawRect.top + yThird, (float)this.drawRect.right, (float)this.drawRect.top + yThird, this.outlinePaint);
        canvas.drawLine((float)this.drawRect.left, (float)this.drawRect.top + yThird * 2.0F, (float)this.drawRect.right, (float)this.drawRect.top + yThird * 2.0F, this.outlinePaint);
    }

    private void drawCircle(Canvas canvas) {
        this.outlinePaint.setStrokeWidth(0);
        canvas.drawOval(new RectF(this.drawRect), this.outlinePaint);
    }

    public void setMode(ModifyMode mode) {
        if (mode != this.modifyMode) {
            this.modifyMode = mode;
            this.viewContext.invalidate();
        }

    }

    public int getHit(float x, float y) {
        Rect r = this.computeLayout();
        float hysteresis = 50.0F;
        int retval = GROW_NONE;

        if ( Math.abs((float)r.left - x) < hysteresis ||
                Math.abs((float)r.right - x) < hysteresis ||
                Math.abs((float)r.top - y) < hysteresis ||
                Math.abs((float)r.bottom - y) < hysteresis ) {

            if (x < (r.left + r.width() / 2)) {
                retval |= GROW_LEFT_EDGE;
            } else {
                retval |= GROW_RIGHT_EDGE;
            }

            if (y < (r.top + r.height() / 2)) {
                retval |= GROW_TOP_EDGE;
            } else {
                retval |= GROW_BOTTOM_EDGE;
            }
        }
        /*
        boolean verticalCheck = y >= (float)r.top - 40.0F && y < (float)r.bottom + 40.0F;
        boolean horizCheck = x >= (float)r.left - 40.0F && x < (float)r.right + 40.0F;

        if (Math.abs((float)r.left - x) < 50.0F) {
            retval |= GROW_LEFT_EDGE;
        }

        if (Math.abs((float)r.right - x) < 50.0F) {
            retval |= GROW_RIGHT_EDGE;
        }

        if (Math.abs((float)r.top - y) < 50.0F) {
            retval |= GROW_TOP_EDGE;
        }

        if (Math.abs((float)r.bottom - y) < 50.0F) {
            retval |= GROW_BOTTOM_EDGE;
        }
        */
        if (retval == 1 && r.contains((int)x, (int)y)) {
            retval = MOVE;
        }

        return retval;
    }

    void handleMotion(int edge, float dx, float dy) {
        Rect r = this.computeLayout();
//        Ln.d("handleMotion edge : " + edge + " / CR W : " + cropRect.width() + " / R W : " + r.width() + "CR H : " + cropRect.height() + " / R H : " + r.height());
        if (edge == MOVE) {
//            Ln.d("handleMotion, moveBy dx : " + dx + " /dy : " + dy);
            this.moveBy(dx * (this.cropRect.width() / (float)r.width()), dy * (this.cropRect.height() / (float)r.height()));
        } else {
            // edge 가 가로.
            if ((6 & edge) == 0) {
                dx = 0.0F;
            }

            // edge가 세로.
            if ((24 & edge) == 0) {
                dy = 0.0F;
            }

            float xDelta = dx * (this.cropRect.width() / (float)r.width());
            float yDelta = dy * (this.cropRect.height() / (float)r.height());
//            this.growBy((float)((edge & 2) != 0 ? -1 : 1) * xDelta, (float)((edge & 8) != 0 ? -1 : 1) * yDelta);
            this.growBy((float)((edge & 2) != 0 ? -1 : 1), xDelta, (float)((edge & 8) != 0 ? -1 : 1), yDelta);
        }

    }

    void moveBy(float dx, float dy) {
        Rect invalRect = new Rect(this.drawRect);
        this.cropRect.offset(dx, dy);
        this.cropRect.offset(Math.max(0.0F, this.imageRect.left - this.cropRect.left), Math.max(0.0F, this.imageRect.top - this.cropRect.top));
        this.cropRect.offset(Math.min(0.0F, this.imageRect.right - this.cropRect.right), Math.min(0.0F, this.imageRect.bottom - this.cropRect.bottom));
        this.drawRect = this.computeLayout();
        invalRect.union(this.drawRect);
//        invalRect.inset(-((int)this.handleRadius), -((int)this.handleRadius));
        this.viewContext.invalidate(invalRect);
    }

    /**
     * 영역이 커지는 위치와 사이즈
     * @param edgeDirectionX minus : 좌측, plus : 우측
     * @param dx 좌우 커지는 정도
     * @param edgeDirectionY minus : 상측, plus : 하측
     * @param dy 상하 커지는 정도
     */
    void growBy(float edgeDirectionX, float dx, float edgeDirectionY, float dy) {

        boolean isLeft = edgeDirectionX > 0 ? false : true;
        boolean isTop = edgeDirectionY > 0 ? false : true;

//        float dX = Math.abs(dx);
//        float dY = Math.abs(dy);

        RectF r = new RectF(this.cropRect);

        if (isLeft) {
            r.left += dx;
            if (r.left < imageRect.left) {
                r.left = imageRect.left;
            }
        }
        else {
            r.right += dx;
            if (r.right > imageRect.right) {
                r.right = imageRect.right;
            }
        }

        if (isTop) {
            r.top += dy;
            if (r.top < imageRect.top) {
                r.top = imageRect.top;
            }
        }
        else {
            r.bottom += dy;
            if (r.bottom > imageRect.bottom) {
                r.bottom = imageRect.bottom;
            }
        }

        this.cropRect.set(r);
        this.drawRect = this.computeLayout();
        this.viewContext.invalidate();
    }

    void growBy(float dx, float dy) {
        // dx 가 + 면 우측 - 면 좌측 선택
        // dy 가 + 면 아래 - 면 위 선택
        // 정비율로 커지는지 확인.
        Ln.d("handleMotion, growBy maintainAspectRatio : " + maintainAspectRatio);
        Ln.d("handleMotion, growBy dx : " + dx + " /dy : " + dy);
        if (this.maintainAspectRatio) {
            if (dx != 0.0F) {
                dy = dx / this.initialAspectRatio;
            } else if (dy != 0.0F) {
                dx = dy * this.initialAspectRatio;
            }
        }

        RectF r = new RectF(this.cropRect);
        if (dx > 0.0F && r.width() + 2.0F * dx > this.imageRect.width()) {
            dx = (this.imageRect.width() - r.width()) / 2.0F;
            if (this.maintainAspectRatio) {
                dy = dx / this.initialAspectRatio;
            }
        }

        if (dy > 0.0F && r.height() + 2.0F * dy > this.imageRect.height()) {
            dy = (this.imageRect.height() - r.height()) / 2.0F;
            if (this.maintainAspectRatio) {
                dx = dy * this.initialAspectRatio;
            }
        }

        r.inset(-dx, -dy);
        float widthCap = 25.0F;
        if (r.width() < 25.0F) {
            r.inset(-(25.0F - r.width()) / 2.0F, 0.0F);
        }

        float heightCap = this.maintainAspectRatio ? 25.0F / this.initialAspectRatio : 25.0F;
        if (r.height() < heightCap) {
            r.inset(0.0F, -(heightCap - r.height()) / 2.0F);
        }

        if (r.left < this.imageRect.left) {
            r.offset(this.imageRect.left - r.left, 0.0F);
        } else if (r.right > this.imageRect.right) {
            r.offset(-(r.right - this.imageRect.right), 0.0F);
        }

        if (r.top < this.imageRect.top) {
            r.offset(0.0F, this.imageRect.top - r.top);
        } else if (r.bottom > this.imageRect.bottom) {
            r.offset(0.0F, -(r.bottom - this.imageRect.bottom));
        }

        this.cropRect.set(r);
        this.drawRect = this.computeLayout();
        this.viewContext.invalidate();
    }

    public Rect getScaledCropRect(float scale) {
        return new Rect((int)(this.cropRect.left * scale), (int)(this.cropRect.top * scale), (int)(this.cropRect.right * scale), (int)(this.cropRect.bottom * scale));
    }

    private Rect computeLayout() {
        RectF r = new RectF(this.cropRect.left, this.cropRect.top, this.cropRect.right, this.cropRect.bottom);
        this.matrix.mapRect(r);
        return new Rect(Math.round(r.left), Math.round(r.top), Math.round(r.right), Math.round(r.bottom));
    }

    public void invalidate() {
        this.drawRect = this.computeLayout();
    }

    public boolean hasFocus() {
        return this.isFocused;
    }

    public void setFocus(boolean isFocused) {
        this.isFocused = isFocused;
    }

    static enum HandleMode {
        Changing,
        Always,
        Never;

        private HandleMode() {
        }
    }

    static enum ModifyMode {
        None,
        Move,
        Grow;

        private ModifyMode() {
        }
    }
}
