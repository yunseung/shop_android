/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.attach;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.inject.Inject;
import com.gsshop.mocha.pattern.mvc.BaseAsyncController;
import com.gsshop.mocha.ui.util.ImageUtils;

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import gsshop.mobile.v2.AbstractBaseActivity;
import gsshop.mobile.v2.Keys.REQCODE;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.support.gtm.datahub.DatahubAction;
import gsshop.mobile.v2.support.gtm.datahub.DatahubUrls;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog.ButtonClickListener;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog.CancelListener;
import gsshop.mobile.v2.support.ui.CustomTwoButtonDialog;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.util.PermissionUtils;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;
import roboguice.util.Ln;


/**
 * 파일첨부 기능 화면<br>
 * 파일첨부 기능이 있는 웹뷰화면(이벤트, 쇼미카페 등)에서 toapp://attach?...를 통해서 본 액티비티를 호출한다.
 *
 */
public class MobileTalkActivity extends AbstractBaseActivity implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 10002347L;

	/**
	 * 콜백함수에 전달할 네트워크 에러 코드
	 */
	private static final String NETWORK_ERR_CODE = "-40000";

	/**
	 * 내용입력란에 입력가능한 최대 글자수
	 */
	private static final int MAX_COMMENT_LEN = 2000;

	/**
	 * txtHeaderTitle
	 */
	@InjectView(R.id.txt_header_title)
	private TextView txtHeaderTitle;


	/**
	 * root
	 */
	@InjectView(R.id.root)
	private LinearLayout root;

	/**
	 * layoutPhoto
	 */
	@InjectView(R.id.layout_photo)
	private LinearLayout layoutPhoto;

	/**
	 * editComment
	 */
	@InjectView(R.id.edit_comment)
	private EditText editComment;

	/**
	 * textLen
	 */
	@InjectView(R.id.text_len)
	private TextView textLen;

	@InjectView(R.id.btn_save)
	private Button btn_save;


	/**
	 * errMessageParam
	 */
	@InjectResource(R.string.mobiletalk_invalid_param)
	private String errMessageParam;

	/**
	 * startInfo
	 */
	@Inject
	private MobileTalkStartInfo startInfo;

	/**
	 * sendInfo
	 */
	@Inject
	private MobileTalkSendInfo sendInfo;

	/**
	 * attachAction
	 */
	@Inject
	private FileAttachAction attachAction;

	/**
	 * MAX_ATTACH_COUNT
	 * NOTE : 멀티파일업로드를 위한 기본구조 작업, UI기획에 따라 이미지 피커를 동적으로 생성 및 삭제하는 로직 추가 필요
	 */
	private final  int MAX_ATTACH_COUNT = 5;

	/**
	 * attachIndex
	 */
	private int attachIndex = 0;

	/**
	 * fileAttachParamInfo
	 */
	private FileAttachParamInfo fileAttachParamInfo;

	/**
	 * 해더 타이틀
	 */
	private static final String HEADER_TITLE = "상담문의 작성";

	/**
	 * 필수 파라미터 정보
	 */
	private final String[] paramKey = {"domain_id", "service_type", "ref_talk_id", "node_id", "in_channel_id", "customer_id", "customer_name"};

	/**
	 * 필수 파라미터 저장 맵
	 */
	private Map<String, String> paramMap;

	/**
	 * API URL
	 */
	private String apiUrl = "";

	/**
	 * 촬영이미지 이름
	 */
	private static File cameraImage = null;

	/**
	 * uploadBitmap
	 */
	private final ArrayList<Bitmap> uploadBitmap = new ArrayList<Bitmap>();

	/**
	 * contentsResult
	 */
	private MobileTalkStartResult contentsResult;

	/**
	 * isLoading
	 */
	private boolean isLoading;

	/**
	 * talkui 입력파라미터가 없는경우 취소시 자바스크립트를 호출하지 않는다.
	 */
	private boolean isCancelCall;

	/**
	 * onCreate
	 *
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.attach_mobiletalk);

		setHeader(HEADER_TITLE);

		setupFields();

		// 클릭리스너 등록
		setClickListener();

		parseUploadUrl();

		showKeyboard();

		//배경 DIM 처리
		WindowManager.LayoutParams param = getWindow().getAttributes();
		param.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		param.dimAmount = 0.6f;
		getWindow().setAttributes(param);
	}

	private void showKeyboard() {
		// 처음 생성시 키보드 올리기
		editComment.requestFocus();
		editComment.setSelection(0);
		//        getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);

		editComment.postDelayed(new Runnable() {
			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(editComment, InputMethodManager.SHOW_IMPLICIT);
			}
		}, 100);
	}

	/**
	 * 뷰의 클릭리스너를 세팅한다.
	 */
	private void setClickListener() {
		findViewById(R.id.btn_take_photo_layout).setOnClickListener((View v) -> {
					addPhotoLayout();
				}
		);
		findViewById(R.id.btn_take_photo).setOnClickListener((View v) -> {
					addPhoto();
				}
		);
		findViewById(R.id.btn_save).setOnClickListener((View v) -> {
					saveFiles();
				}
		);
		findViewById(R.id.btn_cancel).setOnClickListener((View v) -> {
					cancelAttach();
				}
		);
	}

	/**
	 * 해더 타이틀을 세팅한다.
	 *
	 * @param strTitle 해더 타이틀
	 */
	private void setHeader(String strTitle) {
		txtHeaderTitle.setText(strTitle);
	}

	/**
	 * uploadUrl을 통해 전달받은 파라미터를 파싱하고 apiUrl을 생성한다.<br>
	 * 파라미터가 전달되지 않은 경우는 메시지를 띄우고 액티비티를 종료한다.
	 */
	private void parseUploadUrl() {
		//웹뷰에서 전달한 파라미터를 파싱한다.
		fileAttachParamInfo = (FileAttachParamInfo)getIntent().getSerializableExtra("fileAttachParamInfo");
		String url = fileAttachParamInfo.getUploadUrl();

		txtHeaderTitle.setVisibility(View.VISIBLE);


		//Talkui에 따라 보이는 부분 변경
		if("A".equalsIgnoreCase(fileAttachParamInfo.getTalkui())){
			isCancelCall = true;
//			root.setPadding(0,getResources().getDimensionPixelSize(R.dimen.mobiletalk_type_a_padding_top),0,0);
//			txtHeaderTitle.setVisibility(View.VISIBLE);
//			header_navi.setVisibility(View.GONE);
		}else if("B".equalsIgnoreCase(fileAttachParamInfo.getTalkui())){
			isCancelCall = true;
		}else{
			isCancelCall = false;
		}


		paramMap = new HashMap<String, String>();
		Uri uri = Uri.parse(url);
		for (int i=0; i<paramKey.length; i++) {

			//파라미터가 null인 경우 에러메시지 노출 후 본 액티비티 종료함 (단, customer_name은 옵션)
			if (!"customer_name".equals(paramKey[i])) {
				if (uri.getQueryParameter(paramKey[i]) == null) {
					//Ln.i("parseUploadUrl > error-param : " + paramKey[i]);
					alertMessage(errMessageParam, true);
				}
			}
			paramMap.put(paramKey[i], uri.getQueryParameter(paramKey[i]));
		}

		//API 주소 생성
		apiUrl = url.substring(0, url.indexOf('?'));
		apiUrl = Uri.parse(apiUrl).buildUpon()
				.appendQueryParameter("domain_id", paramMap.get("domain_id"))
				.appendQueryParameter("service_type", paramMap.get("service_type"))
				.build().toString();
	}

	/**
	 * 사용자에게 메시지를 노출한다.
	 *
	 * @param message 메시지
	 * @param isFinish true이면 액티비티 종료
	 */
	private void alertMessage(String message, final boolean isFinish) {
		Dialog dialog = new CustomOneButtonDialog(this).message(message)
				.buttonClick(new ButtonClickListener() {
					@Override
					public void onClick(Dialog dialog) {
						dialog.dismiss();
						if (isFinish) {
							finish();
						}
					}
				})
				.cancelled(new CancelListener() {
					@Override
					public void onCancel(Dialog dialog) {
						if (isFinish) {
							finish();
						}
					}
				});
		dialog.show();
	}

	/**
	 * setupFields
	 *
	 */
	private void setupFields() {
		//입력한 글자수 표시 및 최대글자수 초과시 입력제한
		editComment.addTextChangedListener(new TextWatcher() {
			String displayText;

			/**
			 * onTextChanged
			 *
			 * @param s
			 * @param start
			 * @param before
			 * @param count
			 */
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				int len = 0;
				try {
					len = s.toString().getBytes("EUC-KR").length;

					if(len > 0) {
						btn_save.setBackgroundResource(R.drawable.mobiletalk_button_select_background);
					}else{
						btn_save.setBackgroundResource(R.drawable.transparent);
					}

					if ( len > MAX_COMMENT_LEN) {	//최대 글자수 초과시 변경전 텍스트 세팅
						editComment.setText(displayText);
						editComment.setSelection(start);
						alertMessage(getString(R.string.mobiletalk_exceed_len, MAX_COMMENT_LEN), false);
					} else {	//최대 글자수 이하인 경우 글자수 표시
						textLen.setText(String.valueOf(len));
					}
				} catch (UnsupportedEncodingException e) {
					// 10/19 품질팀 요청
					// - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
					// - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
					// - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
					Ln.e(e);
				}
			}

			/**
			 * beforeTextChanged
			 *
			 * @param s
			 * @param start
			 * @param count
			 * @param after
			 */
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				displayText = s.toString();
			}

			/**
			 * afterTextChanged
			 * @param s
			 */
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	/**
	 * 사용자에게 화면이 보여질때
	 *
	 * @param hasFocus
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if(hasFocus){
			// 화면 보여지는 상태
			//포커스 생성
			editComment.requestFocus();
		}
		super.onWindowFocusChanged(hasFocus);
	}


	/**
	 * addPhotoLayout
	 *
	 */
	private void addPhotoLayout() {
		addPhoto();
	}

	/**
	 * addPhoto 사진 추가하기
	 *
	 */
	private void addPhoto() {
		if(isLoading){
			return;
		}

		isLoading = true;

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				isLoading = false;
			}
		}, 1000);


		if(MAX_ATTACH_COUNT <= attachIndex ){
			new CustomOneButtonDialog(this).message(getString(R.string.article_photo_validation_min_length, MAX_ATTACH_COUNT)).buttonClick(CustomOneButtonDialog.DISMISS).show();
			return;
		}

		String caller = null;

		if(fileAttachParamInfo != null){
			caller = fileAttachParamInfo.getCaller();
		}

		if(FileAttachAction.ATTACH_CALLER.MOBILETALK.toString().equalsIgnoreCase(caller)){
			//1:1 상담하기에서 사진첨부할때 (커스텀 갤러리 쓰는 방식)
			FileAttachUtils.executePhotoPopup(this, MAX_ATTACH_COUNT - attachIndex, false, true);
		}else {
			//다른데서 사진첨부할때(시스템 갤러리 쓰는 방식)
			FileAttachUtils.executePhotoPopup(this, MAX_ATTACH_COUNT - attachIndex);
		}

	}
	/**
	 * 코멘트 및 사진 저장
	 */
	private void saveFiles() {
		if(isLoading){
			return;
		}

		isLoading = true;

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				isLoading = false;
			}
		}, 1000);

		// 입력 검증
		String contents = editComment.getText().toString().trim();
		int minLength = getResources().getInteger(R.integer.attach_min_length);

		if (contents.length() < minLength) {
			String message = getString(R.string.mobiletalk_validation_min_length);
			alertMessage(message, false);
			return;
		}


		new TalkStartController(this).execute();
	}

	/**
	 * 취소버튼 클릭시 액티비티 종료
	 */
	private void cancelAttach() {
		//작성중인 글이 없거나 첨부이미지가 없으면 바로 종료
		if ("".equals(editComment.getText().toString().trim())) {
			talkCancel();
			//있으면 확인 후 졸료
		} else {
			new CustomTwoButtonDialog(this).message(R.string.mobiletalk_confirm_cancel)
					.positiveButtonClick(new ButtonClickListener() {
						@Override
						public void onClick(Dialog dialog) {
							dialog.dismiss();
							talkCancel();
						}
					}).negativeButtonClick(CustomOneButtonDialog.DISMISS).show();
		}
	}


	/**
	 * 종료시 웹에 종료 이벤트를 넘겨준다.
	 */
	public void talkCancel() {
		if(isCancelCall) {
			Intent data = new Intent();
			data.putExtra("callback", "javascript:talkCancel()");
			setResult(RESULT_CANCELED, data);
		}
		finish();

	}

	/**
	 * onActivityResult
	 *
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}

		int idx = 0;
		String imagePath = null;

		if (requestCode >= REQCODE.ATTACH_CAMERA && requestCode <= REQCODE.ATTACH_CAMERA + (MAX_ATTACH_COUNT-1)) {// 카메라로 새로 찍은 이미지
			idx = requestCode - REQCODE.ATTACH_CAMERA;
//			File imageFile = cameraImage;
			File imageFile = MainApplication.attechImagePath;
			imagePath = imageFile.getAbsolutePath();

		}else if(requestCode == REQCODE.GALLERY){
			if(MainApplication.articlePhotoes != null && !MainApplication.articlePhotoes.isEmpty()){
				new BitmapAttachController(this).execute();
			}
			return;
		}
		else if (requestCode == REQCODE.PHOTO) {
			boolean isUploaded = FileAttachUtils.uploadMediaFromGallery(this, data);

			if (isUploaded) {
				new BitmapAttachController(this).execute();
			}

			return;
		}

		//세로로 촬영한 이미지를 세로로 유지한재 이미지 사이즈 줄임
		final Bitmap previewBitmap = resizeGalleryImageToSend(imagePath, idx);

		final View photoItem = LayoutInflater.from(getApplicationContext()).inflate(R.layout.photo_items, null);

		ImageView photo = (ImageView)photoItem.findViewById(R.id.img_photo);
		ImageView img_delete_photo = (ImageView)photoItem.findViewById(R.id.img_delete_photo);
		photo.setImageBitmap(previewBitmap);


		img_delete_photo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				layoutPhoto.removeView(photoItem);
				uploadBitmap.remove(previewBitmap);
				attachIndex--;
			}
		});

		layoutPhoto.setVisibility(View.VISIBLE);
		uploadBitmap.add(previewBitmap);
		layoutPhoto.addView(photoItem);
		attachIndex++;


	}

	/**
	 * BitmapAttachController
	 *
	 */
	private class BitmapAttachController extends BaseAsyncController<Bitmap> {

		/**
		 * BitmapAttachController
		 *
		 * @param activityContext
		 */
		protected BitmapAttachController(Context activityContext) {
			super(activityContext);
		}

		/**
		 * onPrepare
		 *
		 * @param params
		 * @throws Exception
		 */
		@Override
		protected void onPrepare(Object... params) throws Exception {
			if (this.dialog != null) {
				this.dialog.dismiss();
				this.dialog.setCancelable(false);
				this.dialog.show();
			}
			//super.onPrepare(params);
		}

		/**
		 * process
		 *
		 * @return Bitmap
		 * @throws Exception
		 */
		@Override
		protected Bitmap process() throws Exception {
			for(int i = 0 ; i < MainApplication.articlePhotoes.size(); i++){

				File[] uploadFiles = new File[1];
				//이미지 리사이징
				uploadFiles[0] = ImageUtil.resizeGalleryImage(getApplicationContext(), MainApplication.articlePhotoes.get(i).fullImageUri.toString());
				final Bitmap previewBitmap = resizeGalleryImageToSend(MainApplication.articlePhotoes.get(i).fullImageUri.toString(), i);
				uploadBitmap.add(previewBitmap);

			}

			return null;
		}

		/**
		 * onSuccess
		 *
		 * @param photoImage
		 * @throws Exception
		 */
		@Override
		protected void onSuccess(Bitmap photoImage) throws Exception {

			attachIndex = 0;
			layoutPhoto.removeAllViews();
			for(int i =0 ; i < uploadBitmap.size() ; i ++){
//				final int index = i;
				final View photoItem = LayoutInflater.from(getApplicationContext()).inflate(R.layout.photo_items, null);

				ImageView photo = (ImageView)photoItem.findViewById(R.id.img_photo);
				ImageView img_delete_photo = (ImageView)photoItem.findViewById(R.id.img_delete_photo);

				final Bitmap bitmap = uploadBitmap.get(i);

				photo.setImageBitmap(bitmap);

				img_delete_photo.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						attachIndex--;
						layoutPhoto.removeView(photoItem);
						uploadBitmap.remove(bitmap);
					}
				});

				layoutPhoto.setVisibility(View.VISIBLE);
				layoutPhoto.addView(photoItem);
				attachIndex++;
			}




		}

		/**
		 * onError
		 * @param e
		 */
		@Override
		protected void onError(Throwable e) {
			super.onError(e);
		}
	}

	/**
	 * 갤러리이미지의 경우 서버로 전송하기 전에 적당히 축소된 이미지파일로
	 * 별도로 만들어둔다. (단, 원본 갤러리 이미지는 변경하지 않음)
	 *
	 * @param imagePath 이미지경로
	 * @param idx 이미지가 다수일 경우 인덱스
	 */
	private Bitmap resizeGalleryImageToSend(String imagePath, int idx) {
//		Bitmap shrinkedBitmap = FileAttachUtils.rotateAndScaleDown(imagePath, FileAttachUtils.ATTACH_IMAGE_WIDTH);
		return FileAttachUtils.rotateAndScaleDown(imagePath, FileAttachUtils.ATTACH_IMAGE_WIDTH);
	}

	/**
	 * 컨트롤러에서 다른 컨트롤러를 호출하는 것은 아키텍처 규칙에 위배되기 때문에 핸들러를 통해서 호출하도록 함
	 */
	private final Handler mHandler= new Handler(){
		@Override
		public void  handleMessage(Message msg){
			switch(msg.what){
				case 0:
					try{
						//절대 null일수 없음(이민수)
						if(!uploadBitmap.isEmpty()){
							File file = ImageUtils.bitmapToFile(uploadBitmap.get(0),FileAttachUtils.getTempAttachImage(getApplicationContext()));
							File[] uploadFiles = new File[1];
							uploadFiles[0] = file;
							sendInfo.setImageFile(uploadFiles);
							uploadBitmap.remove(0);
						}

					}catch(Exception e){
						// 10/19 품질팀 요청
						// - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
						// - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
						// - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
						Ln.e(e);
					}
					new SaveFileController(MobileTalkActivity.this).execute();

					break;
			}
		}
	};

	/**
	 * 코멘트 저장 콘트롤러
	 *
	 */
	private class TalkStartController extends BaseAsyncController<MobileTalkStartResult> {

		/**
		 * TalkStartController
		 * @param activityContext
		 */
		protected TalkStartController(Context activityContext) {
			super(activityContext);
		}

		/**
		 * onPrepare
		 *
		 * @param params
		 * @throws Exception
		 */
		@Override
		protected void onPrepare(Object... params) throws Exception {
			//super.onPrepare(params);
			if (this.dialog != null) {
				this.dialog.dismiss();
				this.dialog.setCancelable(false);
				this.dialog.show();
			}
			setStartTalkInfo();
		}

		/**
		 * process
		 *
		 * @return MobileTalkStartResult
		 * @throws Exception
		 */
		@Override
		protected MobileTalkStartResult process() throws Exception {
			return attachAction.startTalk(startInfo);
		}

		/**
		 * onSuccess
		 *
		 * @param result
		 * @throws Exception
		 */
		@Override
		protected void onSuccess(MobileTalkStartResult result) throws Exception {
			//톡저장이 성공한 경우 
			if (result.getError_code() == 0) {
				contentsResult = result;
				//첨부한 이미지가 있는 경우
				if(!uploadBitmap.isEmpty()){
					mHandler.sendEmptyMessage(0);
				}else {
					callActivityResult(String.valueOf(result.getError_code()), result.getError_message());
				}
				//GTM Datahub 이벤트 전달
				DatahubAction.sendDataToDatahub(MobileTalkActivity.this, DatahubUrls.D_1039, "");

				//톡저장이 실패한 경우	
			} else {
				callActivityResult(String.valueOf(result.getError_code()), result.getError_message());
			}
		}

		/**
		 * onError
		 *
		 * @param e
		 */
		@Override
		protected void onError(Throwable e) {
			//Ln.i("StartTalk onError");
			callActivityResult(NETWORK_ERR_CODE, "");
			//에러메시지 띄우지 않음
			//super.onError(e);
		}
	}

	/**
	 * 파일 저장 콘트롤러
	 *
	 */
	private class SaveFileController extends BaseAsyncController<MobileTalkSendResult> {
		/**
		 * SaveFileController
		 *
		 * @param activityContext
		 */
		protected SaveFileController(Context activityContext) {
			super(activityContext);
		}

		/**
		 * onPrepare
		 *
		 * @param params
		 * @throws Exception
		 */
		@Override
		protected void onPrepare(Object... params) throws Exception {
			//super.onPrepare(params);
			if (this.dialog != null) {
				this.dialog.dismiss();
				this.dialog.setCancelable(false);
				this.dialog.show();
			}
			setSendTalkInfo();
		}

		/**
		 * process
		 *
		 * @return MobileTalkSendResult
		 * @throws Exception
		 */
		@Override
		protected MobileTalkSendResult process() throws Exception {
			return attachAction.sendTalk(sendInfo);
		}

		/**
		 * onSuccess
		 *
		 * @param result
		 * @throws Exception
		 */
		@Override
		protected void onSuccess(MobileTalkSendResult result) throws Exception {

			if(result != null && result.getError_code() != 0){
				contentsResult.setError_code(result.getError_code());
				contentsResult.setError_message(result.getError_message());
			}
			//성공여부와 관계없이 자바스크립트 함수를 호출함
			//절대 null일수 없음(이민수)
			if(!uploadBitmap.isEmpty()){
				mHandler.sendEmptyMessage(0);
			}else{
				if(contentsResult != null){
					callActivityResult(String.valueOf(contentsResult.getError_code()), contentsResult.getError_message());
				}else{
					//비즈니스 로직과 상관없이 result null인경우에 대한 처리 10/05
					//NullPointerException might be thrown as 'result' is nullable here
					if(result != null) {
						callActivityResult(String.valueOf(result.getError_code()), result.getError_message());
					}
				}


			}
		}

		/**
		 * onError
		 *
		 * @param e
		 */
		@Override
		protected void onError(Throwable e) {
			//Ln.i("SendTalk onError");
			callActivityResult(NETWORK_ERR_CODE, "");
			//에러메시지 띄우지 않음
			//super.onError(e);
		}
	}

	/**
	 * 상담신청을 위한 파라미터를 세팅한다.
	 */
	private void setStartTalkInfo() {
		startInfo.setCommand("TalkStart");
		startInfo.setDomain_id(paramMap.get("domain_id"));
		startInfo.setService_type(paramMap.get("service_type"));
		startInfo.setRef_talk_id(paramMap.get("ref_talk_id"));
		startInfo.setNode_id(paramMap.get("node_id"));
		startInfo.setIn_channel_id(paramMap.get("in_channel_id"));
		startInfo.setCustomer_id(paramMap.get("customer_id"));
		startInfo.setCustomer_name(paramMap.get("customer_name") == null ? "" : paramMap.get("customer_name"));
		startInfo.setMessage(editComment.getText().toString().trim());
		startInfo.setApiUrl(apiUrl);
	}

	/**
	 * 파일첨부를 위한 파라미터를 세팅한다.
	 */
	private void setSendTalkInfo() {
		sendInfo.setCommand("TalkSend");
		sendInfo.setDomain_id(paramMap.get("domain_id"));
		sendInfo.setService_type(paramMap.get("service_type"));
		sendInfo.setCustomer_id(paramMap.get("customer_id"));
		sendInfo.setMessage("");
		sendInfo.setApiUrl(apiUrl);
	}

	/**
	 * BaseWebActivity(REQCODE.MOBILETALK)의 콜백함수를 호출한다.
	 *
	 * @param errCode 에러코드
	 * @param errMsg 에러메시지
	 */
	private void callActivityResult(String errCode, String errMsg) {
		String talkId = "";
		if (FileAttachAction.tidResult != null && FileAttachAction.tidResult.getData() != null) {
			talkId = FileAttachAction.tidResult.getData().getTalk_id();
			//톡아이디 정보 초기화
			FileAttachAction.tidResult = null;
			//Ln.i("callActivityResult : FileAttachAction.tidResult set null");
		}

		Intent data = new Intent();
		data.putExtra("callback", fileAttachParamInfo.getCallback());
		data.putExtra("talkId", talkId);
		data.putExtra("error_code", errCode);
		data.putExtra("error_message", errMsg);
		setResult(RESULT_OK, data);
		finish();
	}

	/**
	 * onDestroy
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * onBackPressed
	 */
	@Override
	public void onBackPressed() {
		if (getDrawerLayout() != null && getDrawerLayout().isDrawerOpen(getNavigationView())) {
			closeNavigationView();
			return;
		}
		cancelAttach();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
			case REQCODE.PERMISSIONS_REQUEST_STORAGE_CAMERA:
				if (PermissionUtils.verifyPermissions(this, permissions, grantResults)) {
					FileAttachUtils.startCamera(this);
				}
				break;
			case REQCODE.PERMISSIONS_REQUEST_STORAGE_GALLERY:
				if (PermissionUtils.verifyPermissions(this, permissions, grantResults)) {
					FileAttachUtils.startGallery(this, FileAttachUtils.IMAGE_COUNT);
				}
				break;
			default:
				break;
		}
	}
}
