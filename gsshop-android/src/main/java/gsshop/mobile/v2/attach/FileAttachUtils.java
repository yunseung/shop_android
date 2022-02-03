/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.attach;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.gsshop.mocha.device.SystemInfo;
import com.gsshop.mocha.ui.util.ImageUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.AbstractBaseActivity;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.attach.customcamera.CameraCustomPreviewActivity;
import gsshop.mobile.v2.home.util.crop.Crop;
import gsshop.mobile.v2.support.image.ImageCache;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog;
import gsshop.mobile.v2.util.PermissionUtils;
import gsshop.mobile.v2.web.LiveTalkWebActivity;
import roboguice.util.Ln;


/**
 * FileAttachUtils
 */
public abstract class FileAttachUtils {

    /**
     * 새로 찍거나 갤러리에서 불러온 원본이미지를
     * 서버로 전송하기 전에 적당히 축소하는데 사용되는
     * 이미지의 최대 가로폭.
     */
    public static final int ATTACH_IMAGE_WIDTH = 600;

    /**
     * 라이브톡에서 파일전송시 이미지용량 최대 크기 (200KB)
     */
    public static final int ATTACH_IMAGE_MAX_SIZE = 1024 * 200;

    /**
     * 첨부할 이미지 수
     */
    public static int IMAGE_COUNT = 1;

    private static final String DEFAULT_IMG_UPLOAD_URL = "";


    public static ImageButton btn_take_photo;
    public static ImageButton btn_gallery;

    /**
     * @param context
     * @return
     */
    public static File getTempAttachImage(Context context) {
        return new File(context.getCacheDir(), new Date().getTime() + ".jpg");
    }

    /**
     * Create a File for saving an image or video
     *
     * @param context 컨텍스트
     * @return 촬영후 저장할 파일객체
     */
    public static File getTempCameraImage(Context context) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

//		File mediaStorageDir = new File(context.getExternalCacheDir(), "image");
        File mediaStorageDir = ImageCache.getDiskCacheDir(context, "image");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        //10/19 품질팀 요청
        if (mediaStorageDir == null) {
            return null;
        }
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        // Create a media file name
        String stamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

        //10/19 품질팀 요청
        if (stamp == null) {
            stamp = "";
        }
        return new File(mediaStorageDir.getPath() + File.separator + "image_" + stamp + ".jpg");
    }

    /**
     * EXIF 정보를 회전각도로 변환한다.
     *
     * @param exifOrientation EXIF 회전각
     * @return 실제 각도
     */
    public static int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    /**
     * <pre>
     * 이미지 메타정보를 참고하여 필요시 회전시킨 후 이미지 사이즈를 줄인다.
     *
     * - 가로/세로 비율은 유지된다.
     * - 이미지 축소 후 원본 비트맵은 recycle된다.
     * - 메모리가 부족한 경우 그냥 원본을 반환하다.
     *
     * </pre>
     *
     * @param imagePath 이미지 경로
     * @param maxWidth  최대 이미지 너비(픽셀)
     * @return Bitmap
     */
    public static Bitmap rotateAndScaleDown(String imagePath, int maxWidth) {
        Bitmap bitMap = BitmapFactory.decodeFile(imagePath);
        //Ln.i("width : " + bitMap.getWidth() + ", height : " + bitMap.getHeight());

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath);
        } catch (IOException e1) {
            // 10/19 품질팀 요청
            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
            // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
            Ln.e(e1);
            //오류발생시 비트맵을 그대로 반환한다.
            return bitMap;
        }
        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int exifDegree = exifOrientationToDegrees(exifOrientation);
        //Ln.i("exifDegree : " + exifDegree);
        // 이미지를 상황에 맞게 회전시킨다.
        Bitmap rBitmap = ImageUtils.rotate(bitMap, exifDegree);
        // 이미지 사이즈를 줄인다.
        return ImageUtils.scaleDown(rBitmap, maxWidth);
    }

    /**
     * 마시멜로우부터 권한 강화로 인해 카메라 기능 사용 시, 권한 상태에 따른 분기 처리
     * 카메라 기능이 필요한 클래스에서 사용 가능하도록 공통으로 뺌
     *
     * @param activity 사진찍기, 앨범에서 가져오기 팝업 띄울 activity
     * @param count    업로드 가능한 이미지 수
     */
    public static void executePhotoPopup(final Activity activity, final int count) {
        executePhotoPopup(activity, count, false, false);
    }

    public static void executePhotoPopup(final Activity activity, final int count, final boolean isImgSearch) {
        executePhotoPopup(activity, count, isImgSearch, false);
    }

    public static void executePhotoPopup(final Activity activity, final int count, final boolean isImgSearch, boolean isCustomGallery) {
        // 후면 카메라 없는 단말기인 경우 곧바로 갤러리로 이동.
        if (!SystemInfo.hasCameraFeature()) {
            goGalleryImage(activity, count, Keys.REQCODE.PHOTO);
            return;
        }

        // 이미지 검색인 경우 팝업 띄우지 않고 바로 CustomCamera 실행
        if (isImgSearch) {
            // handler로 들어온 경우이며 Main 에서만 호출하도록 수정.
//			((AbstractBaseActivity)activity).setWiseLogHttpClient(ServerUrls.WEB.IMAGE_SEARCH_TAKE_PHOTO);
            if (PermissionUtils.isPermissionGrantedForStorageRead(activity) &&
                    PermissionUtils.isPermissionGrantedForCamera(activity)) {    // 저장 퍼미션 있음
                startCamera(activity, true);
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, Keys.REQCODE.PERMISSIONS_REQUEST_STORAGE_CAMERA_CUSTOM);
            }
            return;
        }

        AlertDialog.Builder d;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            d = new AlertDialog.Builder(activity, R.style.Theme_Alert_Dialog);
        } else {
            d = new AlertDialog.Builder(activity);
        }
        d.setTitle(activity.getString(R.string.review_photo_upload));
        d.setItems(R.array.review_photo_type, new DialogInterface.OnClickListener() {
            /**
             * 카메라, 저장 퍼미션 모두 있어야 사용 가능!
             * @param dialog
             * @param whichButton
             */
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                if (whichButton == 0) {// 사진 찍기
                    // 이미지 서치일 경우 mseq 보내기

                    // jhkim 마시멜로우 권한 강화 처리 추가, 카메라 퍼미션 확인 추가.
                    if (PermissionUtils.isPermissionGrantedForStorageRead(activity) && PermissionUtils.isPermissionGrantedForCamera(activity)) {    // 저장 퍼미션 있음
                        // 카메라앱 호출
                        startCamera(activity);
                    } else {
                        // 저장 권한 없는 경우, 저장관련 권한 요청
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, Keys.REQCODE.PERMISSIONS_REQUEST_STORAGE_CAMERA);
                    }
                } else if (whichButton == 1) {// 사진 불러오기
                    // jhkim 저장 권한 있는 경우, 갤러리 시작
                    // 저장 권한 없는 경우, 권한 설명과 허용 팝업 띄움
                    if (PermissionUtils.isPermissionGrantedForStorageRead(activity)) {

                        if(isCustomGallery){
                            //1:1 모바일상담에서 사진첨부시 커스텀 갤러리 사용(멀티첨부 가능)
                            startGallery(activity, count, isImgSearch, true);
                        }else{
                            //그 이외에 사진첨부시 시스템 갤러리 사용(멀티첨부 불가능)
                            startGallery(activity, count, isImgSearch);
                        }
//						goGalleryImage(activity, Keys.REQCODE.PHOTO);
                    } else {
                        IMAGE_COUNT = count;
                        // No explanation needed, we can request the permission.

                        int reqCode = Keys.REQCODE.PERMISSIONS_REQUEST_STORAGE_GALLERY;

                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, reqCode);
                        Log.d(getClass().getSimpleName(), "permission popup storage");
                    }
                }

                dialog.dismiss();
            }
        }).setNegativeButton(R.string.mc_cancel, null);
        d.show();

    }


    public static void executeLiveTalkPopup(final Activity activity, final int count, final boolean isImgSearch) {
        if (activity instanceof LiveTalkWebActivity) {
            btn_take_photo = activity.findViewById(R.id.btn_take_photo);
            btn_gallery = activity.findViewById(R.id.btn_gallery);

            btn_take_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 사진 찍기
                    // 이미지 서치일 경우 mseq 보내기

                    // jhkim 마시멜로우 권한 강화 처리 추가, 카메라 퍼미션 확인 추가.
                    if (PermissionUtils.isPermissionGrantedForStorageRead(activity) && PermissionUtils.isPermissionGrantedForCamera(activity)) {    // 저장 퍼미션 있음
                        // 카메라앱 호출
                        startCamera(activity);
                    } else {
                        // 저장 권한 없는 경우, 저장관련 권한 요청
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, Keys.REQCODE.PERMISSIONS_REQUEST_STORAGE);
                    }
                }
            });

            btn_gallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 사진 불러오기
                    // jhkim 저장 권한 있는 경우, 갤러리 시작
                    // 저장 권한 없는 경우, 권한 설명과 허용 팝업 띄움
                    if (PermissionUtils.isPermissionGrantedForStorageRead(activity)) {
                        startGallery(activity, count, isImgSearch);
//						goGalleryImage(activity, Keys.REQCODE.PHOTO);
                    } else {
                        IMAGE_COUNT = count;
                        // No explanation needed, we can request the permission.

                        int reqCode = Keys.REQCODE.PERMISSIONS_REQUEST_STORAGE_GALLERY;

                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, reqCode);
                        Log.d(getClass().getSimpleName(), "permission popup storage");
                    }
                }
            });
        }
    }

    /**
     * 시스템 갤러리 쓰는 경우
     * @param activity
     * @param count
     */
    public static void startGallery(Activity activity, int count) {
        startGallery(activity, count, false);
    }

    /**
     * 시스템 갤러리 쓰는 경우
     * @param activity
     * @param count
     * @param isImageSearch
     */
    public static void startGallery(Activity activity, int count, boolean isImageSearch){
        startGallery(activity, count, isImageSearch, false);
    }

    /**
     * startGallery
     * @param activity
     * @param count
     * @param isImageSearch
     */
    public static void startGallery(Activity activity, int count, boolean isImageSearch, boolean isCustomGallery) {
        if (MainApplication.fileAttachParamInfo == null) {
//			FileAttachParamInfo fileAttachParamInfo = new FileAttachParamInfo();
            MainApplication.fileAttachParamInfo = new FileAttachParamInfo();
        }

        MainApplication.fileAttachParamInfo.setImageCount(Integer.toString(count));
        if (!isImageSearch)
            MainApplication.articlePhotoes = null;

        // 내부 커스텀 갤러리를 쓰려면 아래 값을 true로 설정.
        if (isCustomGallery) {
            String action = Keys.ACTION.GALLERY;
            Intent intent = new Intent(action);
            intent.putExtra("type", "image");
            intent.putExtra("isImageSearch", isImageSearch);

            activity.startActivityForResult(intent, Keys.REQCODE.GALLERY);
        } else {
            goGalleryImage(activity, count, Keys.REQCODE.PHOTO);
        }
    }

    /**
     * startCamera
     *
     * @param activity
     */
    public static void startCamera(Activity activity) {
        startCamera(activity, false);
    }

    public static void startCamera(Activity activity, boolean isCustom) {
        startCamera(activity, isCustom, false);
    }

    public static void startCamera(Activity activity, boolean isCustom, boolean isFromSearching) {
        //Ln.i("startCamera");
        // 카메라앱 호출
        // 커스텀 사진 찍기가 아니면 앨범에서 선택했던 사진정보 초기화
        if (!isCustom)
            MainApplication.articlePhotoes = null;

        final Intent takePictureIntent;
        if (isCustom) {
            Ln.d("CallCustomCamera startCamera : " + System.currentTimeMillis());
            takePictureIntent = new Intent(activity.getApplicationContext(), CameraCustomPreviewActivity.class);
            takePictureIntent.putExtra(Keys.INTENT.INTENT_IS_FROM_SEARCHING, isFromSearching);
        } else {
            takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        if (takePictureIntent != null) {
            if (isCustom) {
//						attechImagePath 경로 바꾸면 안된다. 사진찍고 Edit 후 바꾸어야 한다.
                activity.startActivityForResult(takePictureIntent, Keys.REQCODE.ATTACH_CUSTOM_CAMERA);
            } else {
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = getTempCameraImage(activity);
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        MainApplication.attechImagePath = photoFile;
                        Uri photoURI = FileProvider.getUriForFile(activity,
                                "gsshop.mobile.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        activity.startActivityForResult(takePictureIntent, Keys.REQCODE.ATTACH_CAMERA);
                    }
                }
            }
        }

        // 카메라앱 호출
//		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
//		Intent intent = new Intent();
//		intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
//		// create a file to save the image
//		File imageFile = FileAttachUtils.getTempCameraImage(activity);
//		MainApplication.attechImagePath = imageFile;
//
//		Uri photoURI = FileProvider.getUriForFile(activity, "gsshop.mobile.fileprovider", imageFile);
//		// set the image file name
//		intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//		activity.startActivityForResult(intent, Keys.REQCODE.ATTACH_CAMERA);
    }

    /**
     * 비트맵을 바이트 데이터로 변환한다.
     *
     * @param bitmap  비트맵
     * @param quality 품질(0~100)
     * @return 압축된 바이트배열
     */
    public static byte[] bitmapToBytes(Bitmap bitmap, int quality) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        return stream.toByteArray();
    }

    /**
     * 바이트배열을 비트맵 데이타로 변환한다.
     *
     * @param byteArray 바이트배열
     * @return 변환된 비트맵 데이타
     */
    public static Bitmap byteArrayToBitmap(byte[] byteArray) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        return bitmap;
    }

    /**
     * 비트맵을 파일로 저장한다.
     * <p>
     * - 이미지 포맷 : JPEG
     * - 이 메소드 호출 후 필요시 bitmap을 recycle하는 책임은 호출자에 있다.
     * - toFile로 지정된 파일이 이미 존재하는 경우 비트맵으로 대체된다.
     *
     * @param bitmap  비트맵
     * @param toFile  대상 파일 포인터
     * @param quality 품질
     * @return 파일 포인터
     * @throws Exception Exception
     */
    public static File bitmapToFile(Bitmap bitmap, File toFile, int quality) throws Exception {

        FileOutputStream fos = new FileOutputStream(toFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
        fos.flush();
        fos.close();

        return toFile;
    }

    /**
     * 이미지 가로/세로 비율정보를 확인한다.
     *
     * @param bitmap 비트맵
     * @return S:정사각 H:가로>세로 V:세로>가로
     */
    public static String getImageRatioInfo(Bitmap bitmap) {
        if (bitmap.getWidth() > bitmap.getHeight()) {
            return "H";
        } else if (bitmap.getWidth() < bitmap.getHeight()) {
            return "V";
        } else {
            return "S";
        }
    }

    /**
     * 이미지를 ATTACH_IMAGE_MAX_SIZE 이하로 줄이기 위한 quality값을 구한다.
     *
     * @param bitmap 소스 비트맵
     * @return targetQuality
     */
    public static int getImageQuality(Bitmap bitmap) {
        int baseQuality = 95;
        int qualityStep = 10;
        int targetQuality = baseQuality;

        //라이브톡에서 파일전송 하는 경우 이미지용량 200K 이하로 조정한다.
        byte[] resizedByte = ImageUtils.bitmapToBytes(bitmap);
        long resizedBitmapSize = resizedByte.length;

        while (resizedBitmapSize > ATTACH_IMAGE_MAX_SIZE) {

            resizedByte = FileAttachUtils.bitmapToBytes(bitmap, targetQuality);
            resizedBitmapSize = resizedByte.length;

            targetQuality -= qualityStep;

            if (targetQuality < 0) {
                break;
            }
        }

        if (targetQuality < baseQuality) {
            targetQuality += qualityStep;
        } else {
            targetQuality = 100;
        }

        Ln.i("targetQuality : " + targetQuality);
        Ln.i("resizedBitmapSize : " + resizedBitmapSize);

        return targetQuality;
    }

    public static void photoCrop(Activity context, String filePathImg) {
        photoCrop(context, filePathImg, false);
    }

    public static void photoCrop(Activity context, String filePathImg, boolean isFromCamera) {

        String filePath = context.getFilesDir().getAbsolutePath() + File.separator + "img_crop_temp.png";
        Ln.d("photoCrop filePath : " + filePath);
//			MainApplication.articlePhotoes.get(uploadIndex).fullImageUri;
        // 크롭 시작
        File fileBasicImg = new File(filePathImg);
        File fileTemp = new File(filePath);

        try {
            fileTemp.createNewFile();

            boolean isGoBack = false;

            if (MainApplication.fileAttachParamInfo == null) {
                MainApplication.fileAttachParamInfo = new FileAttachParamInfo();
                MainApplication.fileAttachParamInfo.setHistoryBack("N");
            }

            // Crop은 하나의 이미지만, 이에 카운트는 무조건 1이다.
            MainApplication.fileAttachParamInfo.setImageCount("1");

            if (TextUtils.isEmpty(MainApplication.fileAttachParamInfo.getUploadUrl())) {
                String addrFileUpload = AbstractBaseActivity.getHomeGroupInfo().appUseUrl.imgSearchUploadUrl;
                if (TextUtils.isEmpty(addrFileUpload)) {
                    addrFileUpload = "https://m.gsshop.com/main/visenze/fileUpload.gs";
                }

                MainApplication.fileAttachParamInfo.setUploadUrl(addrFileUpload);
            }

            if ("Y".equals(MainApplication.fileAttachParamInfo.getHistoryBack())) {
                isGoBack = true;
            }

            Intent intent = context.getIntent();
            if (intent == null) {
                intent = new Intent();
            }
            intent.putExtra(Keys.INTENT.INTENT_IS_FROM_CAMERA, isFromCamera);

            Crop.of(Uri.fromFile(fileBasicImg),
                    Uri.fromFile(fileTemp), isGoBack, !MainApplication.isEditFromGallery).start(context);
        } catch (IOException e) {
            Ln.e(e.getMessage());
        }
    }

    /**
     * 사진 이미지를 파일로
     *
     * @return
     */
    public static FileAttachInfoV2 photoToFile(Context context, String imgPath) {

        File[] uploadFiles = new File[1];
        FileAttachInfoV2 attachInfo = new FileAttachInfoV2();

        //이미지 회전하고, 스케일 다운하고,
        Bitmap shrinkedBitmap = FileAttachUtils.rotateAndScaleDown(imgPath, FileAttachUtils.ATTACH_IMAGE_WIDTH);

        //BitMap -> file 로
        try {
            if (FileAttachAction.ATTACH_CALLER.LIVETALK.toString().equalsIgnoreCase(
                    MainApplication.fileAttachParamInfo.getCaller())) {
                //이미지 가로/세로 비율정보 세팅(S:정사각 H:가로>세로 V:세로>가로)
                attachInfo.setImageType(FileAttachUtils.getImageRatioInfo(shrinkedBitmap));

                //라이브톡에서 파일전송 하는 경우 이미지용량 200K 이하로 조정한다.
                int quality = FileAttachUtils.getImageQuality(shrinkedBitmap);
                uploadFiles[0] = FileAttachUtils.bitmapToFile(shrinkedBitmap, FileAttachUtils.getTempAttachImage(context), quality);
            } else {
                uploadFiles[0] = ImageUtils.bitmapToFile(shrinkedBitmap, FileAttachUtils.getTempAttachImage(context));
            }
        } catch (Exception e) {
            // 10/19 품질팀 요청
            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
            // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
            Ln.e(e);
        }

        attachInfo.setImageFile(uploadFiles);
        return attachInfo;
    }

    public static void goGalleryImage(Activity activity, int cnt, int reqCode) {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        if (cnt != 1) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
//		intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        activity.startActivityForResult(intent, reqCode);
    }

    public static void goGalleryVideo(Activity activity, int reqCode) {
        // handler로 들어온 경우이며 Main 에서만 호출하도록 수정.
        if (PermissionUtils.isPermissionGrantedForStorageRead(activity)) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_PICK);
            intent.setType("video/*");
            activity.startActivityForResult(intent, reqCode);
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, reqCode);
        }
        return;
    }

    /**
     * 카메라 또는 갤러리로부터 받은 url정보를 경로(string) 정보로 변환한다.
     *
     * @param uri 이미지파일 uri
     * @return 실제 경로
     */
    @TargetApi(19)
    public static String getRealPathFromUri(Context context, Uri uri) {

        String filePath = "";

        final String[] imageColumns = {MediaStore.Images.Media.DATA};

        String scheme = uri.getScheme();
        if ("content".equalsIgnoreCase(scheme)) {
            Cursor imageCursor = context.getContentResolver().query(uri, imageColumns, null, null, null);

            try {
                if (imageCursor.moveToFirst()) {
                    filePath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                }
            } catch (IllegalStateException e) {
                Ln.e(e);
            }
        }
        else if ( uri.getPath().contains(":") ) {
            //:이 존재하는 경우
            String wholeID = DocumentsContract.getDocumentId(uri);

            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];

            String[] column = {MediaStore.Images.Media.DATA};

            // where id is equal to
            String sel = MediaStore.Images.Media._ID + "=?";

            Cursor cursor = context.getContentResolver().
                    query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            column, sel, new String[]{id}, null);


            int columnIndex = cursor.getColumnIndex(column[0]);

            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }

            cursor.close();

        } else {
            //:이 존재하지 않을경우
            filePath = uri.getPath();
        }

//	    File file = new File( filePath );

        return filePath;
    }

    public static boolean uploadMediaFromGallery(Context context, Intent data) {
        int reqCode = data.getIntExtra("REQCODE", -1);

        // REQ CODE 가 들어있지 않으면 이미지로 간주
        if (reqCode < 0) {
            reqCode = Keys.REQCODE.PHOTO;
        }

        ClipData clipData = data.getClipData();
        Uri intentData = data.getData();

        ArrayList<PhotoItem> photoItems = new ArrayList<>();

        // 파일 확장명 확인하여 일치 하지 않을경우 팝업을 띄워주기 위함.
        boolean isCorrectFileExtention = true;
        boolean isCorrectFile = true;
        if (clipData != null) {
            int imgCnt = clipData.getItemCount();
            try {
                int maxImgCnt = Integer.parseInt(MainApplication.fileAttachParamInfo.getImageCount());
                if (imgCnt > maxImgCnt) {
                    imgCnt = maxImgCnt;
                    // 이미지 초과분 미첨부 메세지 출력
                    EventBus.getDefault().post(new Events.BasicPopupEvent(
                            context.getString(R.string.upload_max_image_number),
                            null, null, null, 0));
                }
            } catch (NumberFormatException e) {
                Ln.e(e.getMessage());
            }

            for (int i = 0; i < imgCnt; i++) {
                ClipData.Item item = clipData.getItemAt(i);
                Uri uri = item.getUri();
//						Ln.e("hklim 가져온 ImageUri : "+ uri.toString() + " \n " + uri.getPath());

                String filePath = FileAttachUtils.getRealPathFromUri(context, uri);

                // 파일 확장자 일치 여부 확인.
                boolean isCorrent = true;
                if (reqCode == Keys.REQCODE.PHOTO) {
                    isCorrent = isCorrectImageFile(filePath);
                } else {
                    isCorrent = isCorrectMp4File(filePath);
                }

                // 파일 확장자가 일치 한다 했을 때
                if (isCorrent) {
                    // 비디오 일 경우라면
                    if (reqCode == Keys.REQCODE.PHOTO_VIDEO) {
                        boolean checkFileSize = isCorrectVideo(filePath);
                        if (!checkFileSize) {
                            isCorrectFile = false;
                            continue;
                        }
                    }
                } else {
                    isCorrectFileExtention = false;
                    continue;
                }

                Uri fileFullUri = Uri.parse(filePath);
                String fileFolderPath = filePath.substring(0, filePath.lastIndexOf("/"));
                PhotoItem tempItem = new PhotoItem(fileFullUri, fileFolderPath);
                photoItems.add(tempItem);
//						uris.add(uri);
            }
        } else if (intentData != null) {
            String filePath = FileAttachUtils.getRealPathFromUri(context, intentData);

            // 파일 확장자 일치 여부 확인.
            if (reqCode == Keys.REQCODE.PHOTO) {
                isCorrectFileExtention = isCorrectImageFile(filePath);
            } else {
                isCorrectFileExtention = isCorrectMp4File(filePath);
                isCorrectFile = isCorrectVideo(filePath);
            }

            if (isCorrectFileExtention && isCorrectFile) {
                Uri fileFullUri = Uri.parse(filePath);
                String fileFolderPath = filePath.substring(0, filePath.lastIndexOf("/"));
                PhotoItem tempItem = new PhotoItem(fileFullUri, fileFolderPath);
                photoItems.add(tempItem);
            }
        }

        // 올바른 확장자가 아니라면 팝업을 띄워준다.
        if (!isCorrectFileExtention) {
            if (reqCode == Keys.REQCODE.PHOTO) {
                // 사진
                new CustomOneButtonDialog((Activity) context).message(R.string.article_photo_file_format).buttonClick(CustomOneButtonDialog.DISMISS).show();
            } else {
                // 영상
                new CustomOneButtonDialog((Activity) context).message(R.string.article_video_file_format).buttonClick(CustomOneButtonDialog.DISMISS).show();
            }
        }
        // 파일 크기가 너무 크다면.
        else if (!isCorrectFile) {
            if (reqCode == Keys.REQCODE.PHOTO_VIDEO) {
                int maxVideoSize = MainApplication.fileAttachParamInfo.getMaxVideoSize();
                if (maxVideoSize < 0) {
                    maxVideoSize = 200;
                }
                // 파일크기가 오버됐다는 팝업을 띄워준다.
                new CustomOneButtonDialog((Activity) context).message(context.getString(R.string.article_video_size_over, maxVideoSize))
                        .buttonClick(CustomOneButtonDialog.DISMISS).show();
            }
        }

        // photoItems 사이즈가 0 이어도 설정한다.
        MainApplication.articlePhotoes = photoItems;
        if (photoItems.size() > 0) {
//			저장할 미디어 아이템이 있으면 true
            return true;
        }
//		없으면 false
        return false;
    }

    /**
     * 경로의 파일 확장자가 jpg / png 가 맞으면 true 다르면 false
     * @param filePath
     * @return
     */
    public static boolean isCorrectImageFile(String filePath) {
        try {
            String fileExtention = filePath.substring(filePath.lastIndexOf(".") + 1);
            // 파일 확장자 검사 위한
            if (fileExtention != null) {
                if ("jpg".equalsIgnoreCase(fileExtention.toLowerCase()) ||
                        "png".equalsIgnoreCase(fileExtention.toLowerCase())) {
                    return true;
                }
                else {
                    return false;
                }
            }
            else {
                return false;
            }
        } catch (IndexOutOfBoundsException e){
            Ln.e(e.getMessage());
            return false;
        } catch (NullPointerException ne){
            Ln.e(ne.getMessage());
            return false;
        }
    }

    /**
     * 경로의 파일 확장자가 mp4 가 맞으면 true 다르면 false
     *
     * @param filePath
     * @return
     */
    public static boolean isCorrectMp4File(String filePath) {
        try {
            String fileExtention = filePath.substring(filePath.lastIndexOf(".") + 1);
            // 파일 확장자 검사 위한
            if (fileExtention != null) {
                if ("mp4".equalsIgnoreCase(fileExtention.toLowerCase())) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (IndexOutOfBoundsException e) {
            Ln.e(e.getMessage());
            return false;
        }
    }

    /**
     * 해당 파일 경로의 파일 적합한지 / 너무 큰 비디오 파일인지 확인한다.
     *
     * @param filePath
     * @return
     */
    public static boolean isCorrectVideo(String filePath) {
        File tempFile = new File(filePath);
        if (tempFile == null) {
            return false;
        }
        int maxVideoFileSize = MainApplication.fileAttachParamInfo.getMaxVideoSize();

        if (maxVideoFileSize < 0) {
            maxVideoFileSize = 200; // default 크기 200 메가 (as - is와 동일)
        }
        // 파일 사이즈가 지정한 최대 파일 사이즈보다 크다면
        if (tempFile.length() > maxVideoFileSize * 1024 * 1024) {
            return false;
        } else {
            return true;
        }
    }
}
