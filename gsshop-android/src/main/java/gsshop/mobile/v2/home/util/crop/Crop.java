//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package gsshop.mobile.v2.home.util.crop;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.R;

public class Crop {
    public static final int REQUEST_CROP = 6709;
    public static final int REQUEST_PICK = 9162;
    public static final int RESULT_ERROR = 404;
    private Intent cropIntent = new Intent();

    public static Crop of(Uri source, Uri destination, boolean isGoBack, boolean isSizeFit) {
        return new Crop(source, destination, isGoBack, isSizeFit);
    }

    private Crop(Uri source, Uri destination, boolean isGoBack, boolean isSizeFit) {
        this.cropIntent.setData(source);
        this.cropIntent.putExtra("output", destination);
        this.cropIntent.putExtra("isGoBack", isGoBack);
        this.cropIntent.putExtra("isSizeFit", isSizeFit);
    }

    private Crop(Uri source, Uri destination, boolean isGoBack) {
        this.cropIntent.setData(source);
        this.cropIntent.putExtra("output", destination);
        this.cropIntent.putExtra("isGoBack", isGoBack);
    }

    public Crop withAspect(int x, int y) {
        this.cropIntent.putExtra("aspect_x", x);
        this.cropIntent.putExtra("aspect_y", y);
        return this;
    }

    public Crop asSquare() {
        this.cropIntent.putExtra("aspect_x", 1);
        this.cropIntent.putExtra("aspect_y", 1);
        return this;
    }

    public Crop withMaxSize(int width, int height) {
        this.cropIntent.putExtra("max_x", width);
        this.cropIntent.putExtra("max_y", height);
        return this;
    }

    public void start(Activity activity) {
        this.start(activity, Keys.REQCODE.PHOTO_EDIT);
    }

    public void start(Activity activity, int requestCode) {
        activity.startActivityForResult(this.getIntent(activity), requestCode);
    }

//    public void start(Context context, Fragment fragment) {
//        this.start(context, (Fragment)fragment, Keys.REQCODE.PHOTO_EDIT);
//    }
//
//    public void start(Context context, android.support.v4.app.Fragment fragment) {
//        this.start(context, (android.support.v4.app.Fragment)fragment, Keys.REQCODE.PHOTO_EDIT);
//    }

//    @TargetApi(11)
//    public void start(Context context, Fragment fragment, int requestCode) {
//        fragment.startActivityForResult(this.getIntent(context), requestCode);
//    }
//
//    public void start(Context context, android.support.v4.app.Fragment fragment, int requestCode) {
//        fragment.startActivityForResult(this.getIntent(context), requestCode);
//    }

    public Intent getIntent(Activity context) {
        this.cropIntent.setClass(context, CropActivity.class);
        cropIntent.putExtra(Keys.INTENT.INTENT_IS_FROM_CAMERA,
                context.getIntent().getBooleanExtra(Keys.INTENT.INTENT_IS_FROM_CAMERA, false));
        cropIntent.putExtra(Keys.INTENT.INTENT_GALLERY_PARAM,
                context.getIntent().getBooleanExtra(Keys.INTENT.INTENT_GALLERY_PARAM, false));
        cropIntent.putExtra(Keys.INTENT.INTENT_IS_FROM_SEARCHING,
                context.getIntent().getBooleanExtra(Keys.INTENT.INTENT_IS_FROM_SEARCHING, false));
        return this.cropIntent;
    }

//    public Intent getIntent(Context context) {
//        this.cropIntent.setClass(context, CropActivity.class);
//        return this.cropIntent;
//    }

    public static Uri getOutput(Intent result) {
        return (Uri)result.getParcelableExtra("output");
    }

    public static Throwable getError(Intent result) {
        return (Throwable)result.getSerializableExtra("error");
    }

    public static void pickImage(Activity activity) {
        pickImage(activity, 9162);
    }

    public static void pickImage(Context context, Fragment fragment) {
        pickImage(context, (Fragment)fragment, 9162);
    }

    public static void pickImage(Context context, androidx.fragment.app.Fragment fragment) {
        pickImage(context, (androidx.fragment.app.Fragment)fragment, 9162);
    }

    public static void pickImage(Activity activity, int requestCode) {
        try {
            activity.startActivityForResult(getImagePicker(), requestCode);
        } catch (ActivityNotFoundException var3) {
            showImagePickerError(activity);
        }

    }

    @TargetApi(11)
    public static void pickImage(Context context, Fragment fragment, int requestCode) {
        try {
            fragment.startActivityForResult(getImagePicker(), requestCode);
        } catch (ActivityNotFoundException var4) {
            showImagePickerError(context);
        }

    }

    public static void pickImage(Context context, androidx.fragment.app.Fragment fragment, int requestCode) {
        try {
            fragment.startActivityForResult(getImagePicker(), requestCode);
        } catch (ActivityNotFoundException var4) {
            showImagePickerError(context);
        }

    }

    private static Intent getImagePicker() {
        return (new Intent("android.intent.action.GET_CONTENT")).setType("image/*");
    }

    private static void showImagePickerError(Context context) {
        Toast.makeText(context, R.string.crop_pick_error, Toast.LENGTH_SHORT).show();
    }

    interface Extra {
        String ASPECT_X = "aspect_x";
        String ASPECT_Y = "aspect_y";
        String MAX_X = "max_x";
        String MAX_Y = "max_y";
        String ERROR = "error";
    }
}
