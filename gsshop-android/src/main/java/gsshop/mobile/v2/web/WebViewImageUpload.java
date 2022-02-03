package gsshop.mobile.v2.web;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.webkit.WebView;

import com.gsshop.mocha.ui.util.ImageUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import gsshop.mobile.v2.Keys.REQCODE;
import gsshop.mobile.v2.attach.FileAttachUtils;
import roboguice.util.Ln;

/**
 * 
 * 킷캣에서 이미지 업로드가 안되는 현상을 해결하기 위해 만든 클래스<br>
 * 사용자가 선택한 파일을 서버로 업로드하고, 등록된 콜백함수를 호출한다.
 * 
 */
public class WebViewImageUpload {

	private static final String TAG = "WebViewImageUpload";
	
    /**
     * 새로 찍거나 갤러리에서 불러온 원본이미지를
     * 서버로 전송하기 전에 적당히 축소하는데 사용되는
     * 이미지의 최대 가로폭.
     */
    public static final int ATTACH_IMAGE_WIDTH = 320;
    
	private static WebViewImageUpload mUpload;

	private Context mContext;

	private String mIsKitKet;
	private String mUploadUrl;
	private String mThumbnailId;
	private String mCallback;
	
	private WebView mWebView;

	private WebViewImageUpload() {

	}
	
	private WebViewImageUpload(Context context) {
		this.mContext = context;
	}

	/**
	 * 싱글톤으로 인스턴스를 생성한다.
	 * 
	 * @param context 액티비티 컨텍스트
	 * @param webView 웹뷰
	 * @return WebViewImageUpload 객체
	 */
	public static final WebViewImageUpload getInstance(Context context, WebView webView) {
		if (mUpload == null) {
			mUpload = new WebViewImageUpload();
		}
		
		mUpload.mContext = context;
		mUpload.mWebView = webView;
		
		return mUpload;
	}

	/**
	 * 이미지를 선택할 수 있는 앱리스트를 팝업으로 노출한다.
	 * 
	 * @param uploadUrl 파일업로드 주소
	 * @param thumbnailId 모바일웹의 프리뷰 이미지 태그 아이디 
	 * @param callback 콜백함수명
	 * @param isKitKet 킷캣여부
	 */
	public final void openFileChooser(String uploadUrl, String thumbnailId, String callback, String isKitKet) {
		mUploadUrl = uploadUrl;
		mThumbnailId = thumbnailId;
		mCallback = callback;
		mIsKitKet = isKitKet;
		BaseWebActivity.useNativeUpload = true;
		
		if ("Y".equalsIgnoreCase(mIsKitKet)) {
			((WebActivity)mContext).startActivityForResult(WebViewImagePicker.getImageIntent(mContext), REQCODE.KITKAT_IMAGE_PICKER);
		}
	}

	/**
	 * 갤러리로부터 받은 파일정보를 통해 웹뷰 화면을 업데이트 한다.
	 * 
	 * @param uri : file path
	 */
	public final void updateContent(Uri uri) {
		if (uri == null) {
			return;
		}
		
		File file = uriToFile(uri);
		
		updateImage(file, mUploadUrl);
	}
	
	/**
	 * 카메라로부터 받은 파일정보를 통해 웹뷰 화면을 업데이트 한다.
	 */
	public final void updateContent() {
		Uri uri = Uri.fromFile(WebViewImagePicker.PHOTO_FILE);
		
		// 미디어 스캐닝 실행.
		mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
		
		File file = WebViewImagePicker.PHOTO_FILE;
		updateImage(file, mUploadUrl);
		WebViewImagePicker.PHOTO_FILE = null;
	}

	/**
	 * 파일을 업로드한 후 섬네일 이미지를 표시하기 위해 자바스크립트 함수를 호출한다.
	 * 
	 * @param file 파일객체
	 * @param uploadUrl 파일업로드 서버 주소
	 */
	private void updateImage(final File file, final String uploadUrl) {
		new AsyncTask<Void, Void, ImageUploadModel>() {
			
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			@Override
			protected ImageUploadModel doInBackground(Void... params) {
				ImageUploadModel uploadModel = null;
				try{
					uploadModel = uploadFile(file.getAbsolutePath(), uploadUrl);
				}catch (IOException e){
					Ln.e(e);
				}
				return uploadModel;
			}

			@Override
			protected void onPostExecute(ImageUploadModel result) {
				super.onPostExecute(result);
				//업로드 이후에 이미지 표시
				mWebView.loadUrl("javascript:" + mCallback + "(" + result.getResponseCode() + ", '" + result.getThumbnailId() + "', '" + result.getImageUrl() + "')");
				clearVal();
			}
		}.execute();
	}

    /**
     * 파일을 서버로 업로드한다.
     * 
     * @param sourceFileUri 소스파일의 full path
     * @param uploadUrl 파일업로드 서버 주소
     * @return ImageUploadModel
     */
    public ImageUploadModel uploadFile(String sourceFileUri, String uploadUrl) throws IOException{
        ImageUploadModel uModel = new ImageUploadModel();
        uModel.setThumbnailId(mThumbnailId);
        
        //서버주소 체크
        if (TextUtils.isEmpty(uploadUrl)) {
			//Ln.i("File upload server url is empty");
        	return uModel;
        }

        //소스파일 uri 체크
        if (TextUtils.isEmpty(sourceFileUri)) {
			//Ln.i("Source file uri is empty");
        	return uModel;
        }
        
//        String fileName = sourceFileUri;
        
        HttpURLConnection conn = null;
        DataOutputStream dos = null;  
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024; 
        
        //이미지를 리사이즈할 경우
        File sourceFile = resizeGalleryImageToSend(sourceFileUri);
        //리사이즈 하지 않을 경우
        //File sourceFile = new File(sourceFileUri); 
        
        if (!sourceFile.isFile()) {
			//Ln.i("Source File Does not exist");
        	return uModel;
        }
		FileInputStream fileInputStream = null;
        try {
			fileInputStream = new FileInputStream(sourceFile);
			URL url = new URL(uploadUrl);
			conn = (HttpURLConnection) url.openConnection(); // Open a HTTP  connection to  the URL
			conn.setDoInput(true); // Allow Inputs
			conn.setDoOutput(true); // Allow Outputs
			conn.setUseCaches(false); // Don't use a Cached Copy
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("ENCTYPE", "multipart/form-data");
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            dos = new DataOutputStream(conn.getOutputStream());
   
            dos.writeBytes(twoHyphens + boundary + lineEnd); 
            dos.writeBytes("Content-Disposition: form-data; name=\"file1\";filename=\""+ sourceFileUri + "\"" + lineEnd);
            dos.writeBytes(lineEnd);
   
            bytesAvailable = fileInputStream.available(); // create a buffer of  maximum size
   
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];
   
            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);  
               
            while (bytesRead > 0) {
            	dos.write(buffer, 0, bufferSize);
            	bytesAvailable = fileInputStream.available();
            	bufferSize = Math.min(bytesAvailable, maxBufferSize);
            	bytesRead = fileInputStream.read(buffer, 0, bufferSize);               
            }
   
            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
   
            // Responses from the server (code and message)
			int serverResponseCode = conn.getResponseCode();
			String serverResponseMessage = conn.getResponseMessage();

			//Ln.i("HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
			uModel.setResponseCode(serverResponseCode);
			 
			StringBuilder text = new StringBuilder();
			InputStreamReader in = new InputStreamReader((InputStream) conn.getContent());
			BufferedReader buff = new BufferedReader(in);
			String line;
			while ((line = buff.readLine()) != null) {
				text.append(line);
			}
			//Ln.i(text.toString());
			
			if (text.toString() != null) {
				uModel.setImageUrl(text.toString());
			}
			 
			//close the streams
			fileInputStream.close();
			dos.flush();
			dos.close();
              
        } catch (MalformedURLException me) {
			// 10/19 품질팀 요청
			// - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
			// - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
			// - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
			Ln.e(me);
        } catch (Exception e) {
			// 10/19 품질팀 요청
			// - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
			// - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
			// - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
			Ln.e(e);
        } finally {
			if(fileInputStream != null){
				fileInputStream.close();
			}
		}

        return uModel;  
    }

    /**
     * 갤러리이미지의 경우 서버로 전송하기 전에 적당히 축소된 이미지파일로
     * 별도로 만들어둔다. (단, 원본 갤러리 이미지는 변경하지 않음)
     *
     * @param imagePath 이미지경로
     * @return 리사이즈된 파일객체
     */
    private File resizeGalleryImageToSend(String imagePath) {
    	File file = null;
    	//inSampleSize 는 2 의 배수로 하면 decode*() 에서 연산이 빨라진다
        Bitmap shrinkedBitmap = ImageUtils.getSampleSizedBitmap(imagePath, ATTACH_IMAGE_WIDTH);

        try {
        	file = ImageUtils.bitmapToFile(shrinkedBitmap,
                    FileAttachUtils.getTempAttachImage(mContext));
        } catch (IOException e) {
            Ln.e(e);
        }
        
        return file;
    }
    
	/**
	 * 카메라 또는 갤러리로부터 받은 url정보를 file 정보로 변환한다.
	 * 
	 * @param uri 이미지파일 uri
	 * @return 파일객체
	 */
	@TargetApi(19)
	private File uriToFile (Uri uri) {
		
		String filePath = "";
		
		if ( uri.getPath().contains(":") ) {
			//:이 존재하는 경우		
			String wholeID = DocumentsContract.getDocumentId(uri);
	
			// Split at colon, use second item in the array
			String id = wholeID.split(":")[1];
	
			String[] column = { MediaStore.Images.Media.DATA };     
	
			// where id is equal to             
			String sel = MediaStore.Images.Media._ID + "=?";
	
			Cursor cursor = mContext.getContentResolver().
			                          query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, 
			                          column, sel, new String[]{ id }, null);
	

			int columnIndex = cursor.getColumnIndex(column[0]);
	
			if (cursor.moveToFirst()) {
			    filePath = cursor.getString(columnIndex);
			}   
	
			cursor.close();
			
		} else {
			//:이 존재하지 않을경우
		    final String[] imageColumns = {MediaStore.Images.Media.DATA };
	
		    String scheme = uri.getScheme();
		    if ( "content".equalsIgnoreCase(scheme) ) {
		    	 Cursor imageCursor = mContext.getContentResolver().query(uri, imageColumns, null, null, null);
	
				    if (imageCursor.moveToFirst()) {
				    	filePath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
				    }
		    } else {
		    	filePath = uri.getPath();
		    }
		}
	    
//	    File file = new File( filePath );
	    
	    return new File( filePath );
	}

	/**
	 * 사용 변수를 초기화한다.
	 */
	public final void clearVal() {
		mUploadUrl = null;
		mThumbnailId = null;
		mCallback = null;
		mIsKitKet = null;
	}
}
