package gsshop.mobile.v2.web;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;

/**
 * 
 * 카메라앱, 갤러리앱 등 사진을 저장 또는 선택할 수 있는 앱목록을 팝업으로 띄운다.
 * 
 */
public class WebViewImagePicker {

	//카메라로 쵤영한 사진을 저장할 디렉토로
	public static final String DIRECTORY_PHOTO_PATH = Environment.getExternalStorageDirectory() + File.separator + "upload";
	//카메라로 쵤영한 사진의 파일정보
	public static File PHOTO_FILE = null;
	
	/**
	 * @param context context
	 * @return Intent
	 */
	public static Intent getImageIntent(Context context) {
		final Uri outputFileUri;

		//디렉토리가 존재하지 않으면 생성
		File directory = new File(DIRECTORY_PHOTO_PATH);
		if (!directory.exists()) {
			directory.mkdir();
		}
		
		//사진촬영 후 저장될 파일명 세팅
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
		PHOTO_FILE = new File(directory, dateFormat.format(new Date()) + ".jpg");
		outputFileUri = Uri.fromFile(PHOTO_FILE);

	    // 카메라
	    final List<Intent> cameraIntents = new ArrayList<Intent>();
	    final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    final PackageManager packageManager = context.getPackageManager();
	    final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
	    for(ResolveInfo res : listCam) {
	        final String packageName = res.activityInfo.packageName;
	        final Intent intent = new Intent(captureIntent);
	        intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
	        intent.setPackage(packageName);
	        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
	        cameraIntents.add(intent);
	    }

	    // 갤러리, 사진 등
	    final Intent galleryIntent = new Intent();
	    galleryIntent.setType("image/*");
	    galleryIntent.setAction(Intent.ACTION_PICK);

	    final Intent chooserIntent = Intent.createChooser(galleryIntent, "사진 올리기");

	    // 팝업에 표시되는 인텐트에 카메라를 추가한다.
	    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));
	    
	    return chooserIntent;
	}
}
