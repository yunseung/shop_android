package gsshop.mobile.v2.home.util.crop;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.opengl.GLES10;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.util.crop.view.CropImageView;
import gsshop.mobile.v2.home.util.crop.view.HighlightView;
import gsshop.mobile.v2.home.util.crop.view.ImageViewTouchBase.Recycler;
import gsshop.mobile.v2.util.DeviceUtils;
import roboguice.util.Ln;

public class CropActivity extends MonitoredActivity {
    private static final int SIZE_DEFAULT = 2048;
    private static final int SIZE_LIMIT = 4096;
    private final Handler handler = new Handler();
    private int aspectX;
    private int aspectY;
    private int maxX;
    private int maxY;
    private int exifRotation;
    private Uri sourceUri;
    private Uri saveUri;
    private boolean isSaving;
    private int sampleSize;
    private RotateBitmap rotateBitmap;
    private CropImageView imageView;
    private HighlightView cropView;

    public CropActivity() {
    }

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setupWindowFlags();
        setupViews();
        loadInput();
        if (rotateBitmap == null) {
            finish();
        } else {
            startCrop();
        }
    }

    @TargetApi(19)
    private void setupWindowFlags() {
        requestWindowFeature(1);
        if (VERSION.SDK_INT >= 19) {
            getWindow().clearFlags(67108864);
        }

    }

    private void setupViews() {
        setContentView(R.layout.activity_crop);
        imageView = (CropImageView) findViewById(R.id.crop_image);
        imageView.setViewContext(this);
        imageView.setRecycler(new Recycler() {
            public void recycle(Bitmap b) {
                b.recycle();
                System.gc();
            }
        });

        Button btnCancel = findViewById(R.id.btn_cancel);
        Intent intent = getIntent();
        if (intent == null) {
            intent = new Intent();
        }

        boolean isFromCamera = intent.getBooleanExtra(Keys.INTENT.INTENT_IS_FROM_CAMERA, false);
        if (isFromCamera) {
            btnCancel.setText(R.string.crop_back_to_camera);
        }
        else {
            btnCancel.setText(R.string.crop_cancel);
        }

        findViewById(R.id.btn_cancel).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                setResult(RESULT_CANCELED, getIntent());
                finish();
            }
        });
        findViewById(R.id.btn_done).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                onSaveClicked();
            }
        });
    }

    private void loadInput() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            aspectX = extras.getInt("aspect_x");
            aspectY = extras.getInt("aspect_y");
            maxX = extras.getInt("max_x");
            maxY = extras.getInt("max_y");
            saveUri = (Uri)extras.getParcelable("output");
        }

        sourceUri = intent.getData();
        boolean isSizeFit = extras.getBoolean("isSizeFit");

        if (sourceUri != null) {
            File file = CropUtil.getFromMediaUri(this, getContentResolver(), sourceUri);
            exifRotation = CropUtil.getExifRotation(file);
            InputStream is = null;
            Bitmap bitmap = null;
            try {
                sampleSize = calculateBitmapSampleSize(sourceUri);
                is = getContentResolver().openInputStream(sourceUri);
                Options option = new Options();
                option.inSampleSize = sampleSize;

                if( isSizeFit ) {
                    bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                    int deviceWHMax = Math.max(DeviceUtils.getDeviceWidth(getApplicationContext()), DeviceUtils.getDeviceHeight(getApplicationContext()));
                    int deviceWHMin = Math.min(DeviceUtils.getDeviceWidth(getApplicationContext()), DeviceUtils.getDeviceHeight(getApplicationContext()));

                    float ratioDisplay = (float) deviceWHMax / (float) deviceWHMin;

                    int bitmapWHMax = Math.max(bitmap.getWidth(), bitmap.getHeight());
                    int bitmapWHMin = Math.min(bitmap.getWidth(), bitmap.getHeight());

                    float bitmapRatio = (float) bitmapWHMax / (float) bitmapWHMin;

                    Matrix matrix = null;

                    if (exifRotation != 0) {
                        matrix = new Matrix();
                        matrix.postRotate(exifRotation);
                    }

                    boolean isPortrait = true;
                    if (exifRotation == 90 ||
                            exifRotation == 270) {
                        isPortrait = false;
                    }

                    float diffRatioToCut = Math.abs(ratioDisplay - bitmapRatio);
                    if (diffRatioToCut > 0.1f || !isPortrait) {
                        //                      if(false) {
                        int xCutter = 0;
                        int yCutter = 0;

                        if (!isPortrait) {
                            yCutter = (int) ((float) bitmap.getHeight() * diffRatioToCut);
                        } else {
                            xCutter = (int) ((float) bitmap.getHeight() * diffRatioToCut);
                        }
                        bitmap = Bitmap.createBitmap(bitmap, (xCutter / 4), (yCutter / 4), bitmap.getWidth() - xCutter / 2, bitmap.getHeight() - yCutter / 2, matrix, false);
                        exifRotation = 0;
                        sampleSize = 1;
                        rotateBitmap = new RotateBitmap(bitmap, 0);
                    } else {
                        rotateBitmap = new RotateBitmap(BitmapFactory.decodeStream(is, (Rect) null, option), exifRotation);
                    }
                }
                else {
                    rotateBitmap = new RotateBitmap(BitmapFactory.decodeStream(is, (Rect) null, option), exifRotation);
                }
            } catch (IOException var9) {
                Ln.e("Error reading image: " + var9.getMessage(), var9);
                setResultException(var9);
            } catch (OutOfMemoryError var10) {
                Ln.e("OOM reading image: " + var10.getMessage(), var10);
                setResultException(var10);
            } finally {
                CropUtil.closeSilently(is);
//                if (bitmap != null) {
//                    bitmap.recycle();
//                    bitmap = null;
//                }
            }
        }

    }

    private int calculateBitmapSampleSize(Uri bitmapUri) throws IOException {
        InputStream is = null;
        Options options = new Options();
        options.inJustDecodeBounds = true;

        try {
            is = getContentResolver().openInputStream(bitmapUri);
            BitmapFactory.decodeStream(is, (Rect)null, options);
        } finally {
            CropUtil.closeSilently(is);
        }

        int maxSize = getMaxImageSize();

        int sampleSize;
        for(sampleSize = 1; options.outHeight / sampleSize > maxSize || options.outWidth / sampleSize > maxSize; sampleSize <<= 1) {
        }

        return sampleSize;
    }

    private int getMaxImageSize() {
        int textureLimit = getMaxTextureSize();
        return textureLimit == 0 ? SIZE_DEFAULT : Math.min(textureLimit, SIZE_LIMIT);
    }

    private int getMaxTextureSize() {
        int[] maxSize = new int[1];
        GLES10.glGetIntegerv(3379, maxSize, 0);
        return maxSize[0];
    }

    private void startCrop() {
        if (!isFinishing()) {
            imageView.setImageRotateBitmapResetBase(rotateBitmap, true);
            CropUtil.startBackgroundJob(this, (String)null, getResources().getString(R.string.crop_wait), new Runnable() {
                public void run() {
                    final CountDownLatch latch = new CountDownLatch(1);
                    handler.post(new Runnable() {
                        public void run() {
                            if (imageView.getScale() == 1.0F) {
                                imageView.center();
                            }

                            latch.countDown();
                        }
                    });

                    try {
                        latch.await();
                    } catch (InterruptedException var3) {
                        throw new RuntimeException(var3);
                    }

                    (new Cropper()).crop();
                }
            }, handler);
        }
    }

    private void onSaveClicked() {

        boolean isGoBack = getIntent().getBooleanExtra("isGoBack", false);
        boolean isSizeFit = getIntent().getBooleanExtra("isSizeFit", false);
        if (isGoBack) {
//            EventBus.getDefault().post(new Events.WebHistoryBackEvent());
        }

        if (cropView != null && !isSaving) {
            isSaving = true;
            Rect r = cropView.getScaledCropRect((float)sampleSize);
            int width = r.width();
            int height = r.height();
            int outWidth = width;
            int outHeight = height;
            if (maxX > 0 && maxY > 0 && (width > maxX || height > maxY)) {
                float ratio = (float)width / (float)height;
                if ((float)maxX / (float)maxY > ratio) {
                    outHeight = maxY;
                    outWidth = (int)((float)maxY * ratio + 0.5F);
                } else {
                    outWidth = maxX;
                    outHeight = (int)((float)maxX / ratio + 0.5F);
                }
            }

            Bitmap croppedImage;
            try {
                croppedImage = decodeRegionCrop(r, outWidth, outHeight);
            } catch (IllegalArgumentException var8) {
                setResultException(var8);
                finish();
                return;
            }

            if (croppedImage != null) {
                imageView.setImageRotateBitmapResetBase(new RotateBitmap(croppedImage, exifRotation), true);
                imageView.center();
                imageView.highlightViews.clear();
            }

            saveImage(croppedImage);
        }
    }

    private void saveImage(final Bitmap croppedImage) {
        if (croppedImage != null) {
            CropUtil.startBackgroundJob(this, (String)null, getResources().getString(R.string.crop_saving), new Runnable() {
                public void run() {
                    saveOutput(croppedImage);
                }
            }, handler);
        } else {
            finish();
        }

    }

    private Bitmap decodeRegionCrop(Rect rect, int outWidth, int outHeight) {

        InputStream is = null;
        Bitmap croppedImage = null;
        boolean isSizeFit = getIntent().getBooleanExtra("isSizeFit", false);

        try {

            if (isSizeFit) {
                croppedImage = Bitmap.createBitmap(rotateBitmap.getBitmap(), rect.left, rect.top,
                        rect.width(), rect.height(), null, false);
            }
            else {
                is = getContentResolver().openInputStream(sourceUri);
                BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(is, false);
                int width = decoder.getWidth();
                int height = decoder.getHeight();
                Matrix matrix;
                if (exifRotation != 0) {
                    matrix = new Matrix();
                    matrix.setRotate((float) (-exifRotation));
                    RectF adjusted = new RectF();
                    matrix.mapRect(adjusted, new RectF(rect));
                    adjusted.offset(adjusted.left < 0.0F ? (float) width : 0.0F, adjusted.top < 0.0F ? (float) height : 0.0F);
                    rect = new Rect((int) adjusted.left, (int) adjusted.top, (int) adjusted.right, (int) adjusted.bottom);
                }

                try {
                    croppedImage = decoder.decodeRegion(rect, new Options());
                    if (rect.width() > outWidth || rect.height() > outHeight) {
                        matrix = new Matrix();
                        if (exifRotation % 180 == 0) {
                            matrix.postScale((float) outWidth / (float) rect.width(), (float) outHeight / (float) rect.height());
                        } else {
                            matrix.postScale((float) outHeight / (float) rect.height(), (float) outWidth / (float) rect.width());
                        }
                        croppedImage = Bitmap.createBitmap(croppedImage, 0, 0, croppedImage.getWidth(), croppedImage.getHeight(), null, true);
                    }
                } catch (IllegalArgumentException var16) {
                    throw new IllegalArgumentException("Rectangle " + rect + " is outside of the image (" + width + "," + height + "," + exifRotation + ")", var16);
                }
            }
        } catch (IOException var17) {
            Ln.e("Error cropping image: " + var17.getMessage(), var17);
            setResultException(var17);
        } catch (OutOfMemoryError var18) {
            Ln.e("OOM cropping image: " + var18.getMessage(), var18);
            setResultException(var18);
        } finally {
            clearImageView();
            CropUtil.closeSilently(is);
        }

        return croppedImage;
    }

    private void clearImageView() {
        imageView.clear();
        if (rotateBitmap != null) {
            rotateBitmap.recycle();
        }

        System.gc();
    }

    private void saveOutput(final Bitmap croppedImage) {
        if (saveUri != null) {
            OutputStream outputStream = null;

            try {
                outputStream = getContentResolver().openOutputStream(saveUri);
                if (outputStream != null) {
                    croppedImage.compress(CompressFormat.JPEG, 90, outputStream);
                }
            } catch (IOException var7) {
                setResultException(var7);
                Ln.e("Cannot open file: " + saveUri, var7);
            } finally {
                CropUtil.closeSilently(outputStream);
            }

            boolean isSizeFit = getIntent().getBooleanExtra("isSizeFit", false);
            if (!isSizeFit) {
                CropUtil.copyExifRotation(CropUtil.getFromMediaUri(this, getContentResolver(), sourceUri), CropUtil.getFromMediaUri(this, getContentResolver(), saveUri));
            }
            setResultUri(saveUri);
        }

        // Webview Activity history back을 기다리기 위한 1초후에 화면 전환.
        boolean isGoBack = getIntent().getBooleanExtra("isGoBack", false);
        int nDelay = 0;
        if (isGoBack) {
            nDelay = 1000;
        }
        handler.postDelayed(new Runnable() {
            public void run() {
                imageView.clear();
                croppedImage.recycle();
                CropActivity.this.setResult(RESULT_OK, getIntent());
                CropActivity.this.finish();
            }
        }, nDelay);
    }

    protected void onDestroy() {
        super.onDestroy();
        if (rotateBitmap != null) {
            rotateBitmap.recycle();
        }

    }

    public boolean onSearchRequested() {
        return false;
    }

    public boolean isSaving() {
        return isSaving;
    }

    private void setResultUri(Uri uri) {
        setResult(-1, (new Intent()).putExtra("output", uri));
    }

    private void setResultException(Throwable throwable) {
        setResult(404, (new Intent()).putExtra("error", throwable));
    }

    private class Cropper {
        private Cropper() {
        }

        private void makeDefault() {
            if (rotateBitmap != null) {
                HighlightView hv = new HighlightView(imageView);
                int width = rotateBitmap.getWidth();
                int height = rotateBitmap.getHeight();
                Rect imageRect = new Rect(0, 0, width, height);
                int cropWidth = Math.min(width, height) * 4 / 5;
                int cropHeight = cropWidth;
                if (aspectX != 0 && aspectY != 0) {
                    if (aspectX > aspectY) {
                        cropHeight = cropWidth * aspectY / aspectX;
                    } else {
                        cropWidth = cropWidth * aspectX / aspectY;
                    }
                }

                int x = (width - cropWidth) / 2;
                int y = (height - cropHeight) / 2;
                RectF cropRect = new RectF((float)x, (float)y, (float)(x + cropWidth), (float)(y + cropHeight));
                hv.setup(imageView.getUnrotatedMatrix(), imageRect, cropRect, aspectX != 0 && aspectY != 0);
                imageView.add(hv);
            }
        }

        public void crop() {
            handler.post(new Runnable() {
                public void run() {
                    makeDefault();
                    imageView.invalidate();
                    if (imageView.highlightViews.size() == 1) {
                        cropView = (HighlightView) imageView.highlightViews.get(0);
                        cropView.setFocus(true);
                    }

                }
            });
        }
    }
}
