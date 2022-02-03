package gsshop.mobile.v2.attach.customcamera;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;

import com.github.florent37.camerafragment.configuration.Configuration;
import com.github.florent37.camerafragment.internal.enums.Flash;
import com.github.florent37.camerafragment.listeners.CameraFragmentResultAdapter;
import com.github.florent37.camerafragment.listeners.CameraFragmentStateAdapter;
import com.gsshop.mocha.core.activity.BaseFragmentActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.attach.FileAttachUtils;
import gsshop.mobile.v2.attach.PhotoItem;
import gsshop.mobile.v2.attach.gallery.LocalAlbum;
import gsshop.mobile.v2.attach.gallery.LocalMediaItem;
import gsshop.mobile.v2.support.image.ImageCache;
import gsshop.mobile.v2.util.PermissionUtils;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

public class CameraCustomPreviewActivity extends BaseFragmentActivity {
    private static final int DEFAULT_IMG_CNT = 1;
    public static final String FRAGMENT_TAG = "camera";

    @InjectView(R.id.img_preview_to_gallery)
    ImageView imgToGallery;
    @InjectView(R.id.chk_preview_flash)
    CheckBox chkFlash;
    @InjectView(R.id.text_top)
    TextView txtTop;
//    private boolean isShutterClicked = false;

    public void onShutterClicked() {
//        if (isShutterClicked) {
//            return;
//        }
//        isShutterClicked = true;

        final CameraPreviewFragment cameraPreviewFragment = getCameraFragment();
        if (cameraPreviewFragment != null) {

            File mediaStorageDir = ImageCache.getDiskCacheDir(getApplicationContext(), "image");

            String stamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

            //10/19 품질팀 요청
            if(stamp == null){
                stamp = "";
            }
            cameraPreviewFragment.takePhotoOrCaptureVideo(new CameraFragmentResultAdapter() {
                  @Override
                  public void onVideoRecorded(String filePath) {
                      finish();
                  }

                  @Override
                  public void onPhotoTaken(byte[] bytes, String filePath) {
//                      MainApplication.attechImagePath = new File(filePath);
                      MainApplication.tempImagePath = filePath;
                      setResult(RESULT_OK, getIntent());
                      finish();
                  }
              }, mediaStorageDir.getPath(), "image_" + stamp);
        }
    }

    public void onCloseClicked() {
        finish();
    }

    public void onToGalleryClicked() {
//        갤러리로 가는 로직 추가.
//        ActivityUtils.goGallery(this, Keys.REQCODE.PHOTO);
        FileAttachUtils.IMAGE_COUNT = DEFAULT_IMG_CNT;
        if(PermissionUtils.isPermissionGrantedForStorageRead(this)){
            FileAttachUtils.startGallery(this, DEFAULT_IMG_CNT, true);
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                , Keys.REQCODE.PERMISSIONS_REQUEST_STORAGE_GALLERY_FROM_IMG_SEARCH);
        }
    }

    public void onFlashChkClicked() {
        final CameraPreviewFragment cameraFragment = getCameraFragment();
        if (cameraFragment != null) {
            if (chkFlash.isChecked()) {
                cameraFragment.setFlash(Flash.FLASH_ON);
            } else {
                cameraFragment.setFlash(Flash.FLASH_OFF);
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_preview);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            finish();
        }

        // 카메라를 추가한다.
        addCamera();

        initView();

        // 클릭리스너 등록
        setClickListener();
    }

    private void initView() {
        final CameraPreviewFragment cameraFragment = getCameraFragment();

//        두줄 중 첫번째 줄을 삭제해달라는 요청 위치는 고정.
//        SpannableStringBuilder strBuilder = new SpannableStringBuilder(getString(R.string.camera_preview_top_default));
////        ForegroundColorSpan backColorSpan = new ForegroundColorSpan(Color.parseColor("#6032c7"));
//        BackgroundColorSpan backColorSpon = new BackgroundColorSpan(Color.parseColor("#6032c7"));
//        strBuilder.setSpan(backColorSpon, 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        txtTop.setText(strBuilder);

        chkFlash.setChecked(false);
        if (cameraFragment != null) {
            cameraFragment.setFlash(Flash.FLASH_OFF);
        }

        // Thumbnail 이미지
        try {
            LocalAlbum album = new LocalAlbum(getApplication(), true);

            List<LocalMediaItem> items = album.getMediaItem();
            List<PhotoItem> result = new ArrayList<PhotoItem>();
            if (items != null && !items.isEmpty()) {
                LocalMediaItem item = items.get(0);
                Uri fullImageUri = Uri.parse(item.filePath);

                PhotoItem newItem = new PhotoItem(item.id, fullImageUri, item.bucketDisplayName);
//                imgToGallery.setImageBitmap(newItem.getThumbnail(this));
                imgToGallery.setBackground(
                        new BitmapDrawable(getResources(), newItem.getThumbnail(this)));
            }
        }
        catch (Exception e) {
            Ln.e(e.getMessage());
            imgToGallery.setImageResource(R.drawable.noimg_list);
        }
    }

    /**
     * 뷰의 클릭리스너를 세팅한다.
     */
    private void setClickListener() {
        findViewById(R.id.btn_preview_shutter).setOnClickListener((View v) -> {
                    onShutterClicked();
                }
        );
        findViewById(R.id.btn_preview_close).setOnClickListener((View v) -> {
                    onCloseClicked();
                }
        );
        findViewById(R.id.view_preview_to_gallery).setOnClickListener((View v) -> {
                    onToGalleryClicked();
                }
        );
        findViewById(R.id.chk_preview_flash).setOnClickListener((View v) -> {
                    onFlashChkClicked();
                }
        );
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    private void addCamera() {
        final CameraPreviewFragment cameraFragment = CameraPreviewFragment.newInstance(new Configuration.Builder()
                .setCamera(Configuration.CAMERA_FACE_REAR).build());

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, cameraFragment, FRAGMENT_TAG)
                .commitAllowingStateLoss();

        if (cameraFragment != null) {
            cameraFragment.setStateListener(new CameraFragmentStateAdapter() {
                @Override
                public void onCurrentCameraBack() {
                    super.onCurrentCameraBack();
                }

                @Override
                public void onCurrentCameraFront() {
                    super.onCurrentCameraFront();
                }
            });
        }
    }

    private CameraPreviewFragment getCameraFragment() {
        return (CameraPreviewFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Keys.REQCODE.PERMISSIONS_REQUEST_STORAGE_GALLERY_FROM_IMG_SEARCH :
                if (PermissionUtils.verifyPermissions(this, permissions, grantResults)) {
                    FileAttachUtils.startGallery(this, FileAttachUtils.IMAGE_COUNT, true);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Keys.REQCODE.GALLERY :
                if (resultCode == RESULT_CANCELED) {
                    break;
                }
                Intent intent = getIntent();
                if (intent == null) {
                    intent = new Intent();
                }
                intent.putExtra(Keys.INTENT.INTENT_GALLERY_PARAM, true);
                setResult(resultCode, intent);
                finish();
                break;
        }
    }
}
