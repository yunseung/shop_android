/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.attach;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;

import com.google.inject.Inject;
import com.gsshop.mocha.network.rest.ProgressFileSystemResource;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.attach.FileAttachConnector.ShowmeAttachResult;
import gsshop.mobile.v2.support.ui.CustomProgressDialog;
import gsshop.mobile.v2.util.ThreadUtils;
import roboguice.inject.ContextSingleton;
import roboguice.inject.InjectResource;
import roboguice.util.Ln;

/**
 * 첨부파일 저장(모바일상담, 쇼미카페, 이벤트 등) 액션.
 *
 */
@ContextSingleton
public class FileAttachAction {
	private static final String TAG = "FileAttachAction";

	/**
	 * 첨부기능을 사용하는 주체에 대한 구분자
	 */
	public static enum ATTACH_CALLER {
		MOBILETALK,	//모바일상담
		SHOWMECAFE,	//쇼미카페
		EVENTVIDEO,	//이벤트비디오
		WEEKLY,		//위클리
		REVIEW,		//상품평
		LIVETALK,	//라이브톡
		IMAGESEARCH	//이미지서칭
	}

	/**
	 * 톡아이디 발급결과 저장변수
	 */
	static MobileTalkStartResult tidResult = null;

	/**
	 * 파일전송 취소 플래그
	 */
	private boolean isCancel = false;

	/**
	 * category
	 */
	public static String category;

	/**
	 * CustomProgressDialog
	 */
	private CustomProgressDialog pDialog;

	/**
	 * errMessage
	 */
	@InjectResource(R.string.error_server)
	private String errMessage;

	/**
	 * attachConnector
	 */
	@Inject
	private FileAttachConnector attachConnector;

	/**
	 * context
	 */
	@Inject
	private Context context;

	/**
	 * 최초 상담신청 후 마지막 메시지 번호에 +1한 값을 리턴한다.
	 *
	 * @return 마지막 메시지 번호
	 */
	private int getLastMessageId() {
		ArrayList<MobileTalkStartDataMsg> msgs = tidResult.getData().getMessages();
		int lastSeq = 0;
		//int cnt = msgs.size();
		//getMessages() 값이 없을 경우 0 리턴
		//Change this condition so that it does not always evaluate to "false" 조건으로 변경
		//if (msgs == null || cnt == 0) {
		if (msgs == null || msgs.isEmpty()) {
			return lastSeq;
		}

		for (MobileTalkStartDataMsg msg : msgs) {
			if (msg.getSeq() > lastSeq) {
				lastSeq = msg.getSeq();
			}
		}
		return lastSeq + 1;
	}

	/**
	 * 사용자 입력내용 저장
	 *
	 * @param startInfo cmd 파라미터에 세팅할 값
	 * @return MobileTalkStartResult
	 * @throws Exception Exception
	 */
	public MobileTalkStartResult startTalk(MobileTalkStartInfo startInfo) throws Exception {
		MultiValueMap<String, Object> formData = new LinkedMultiValueMap<String, Object>();


		//모델객체를 json스트링으로 변환
		ObjectMapper mapper = new ObjectMapper();
		String cmd = mapper.writeValueAsString(startInfo);
		//Ln.e("[TalkStart]" + cmd);

		formData.add("cmd", cmd);

		tidResult = attachConnector.startTalk(formData, startInfo.getApiUrl());

		//Ln.i("TalkStart > result : " + mapper.writeValueAsString(tidResult));
		//Ln.i("TalkStart > error_code : " + tidResult.getError_code());
		//Ln.i("TalkStart > error_message : " + tidResult.getError_message());
		if (tidResult == null
				|| tidResult.getData() == null
				|| TextUtils.isEmpty(tidResult.getData().getTalk_id())
				|| tidResult.getError_code() != 0) {
    		//throw new FileAttachInvalidException().setUserMessage(errMessage);
		} else {
			//Ln.i("TalkStart > talk_id : " + tidResult.getData().getTalk_id());
		}

		return tidResult;
	}

	/**
	 * 모바일상담 파일첨부를 위해 파라미터 세팅 후 saveFiles를 호출하여 파일을 전송한다.
	 *
	 * @param sendInfo cmd 파라미터에 세팅할 값
	 * @return MobileTalkSendResult
	 * @throws Exception Exception 을 받아도 무방
	 */
	public MobileTalkSendResult sendTalk(MobileTalkSendInfo sendInfo) throws Exception {
		//StartTalk API 호출을 통해 발급된 톡아이디 세팅
		sendInfo.setTalk_id(tidResult.getData().getTalk_id());
		//StartTalk API 호출을 통해 발급된 seq중 최대값에 1을 더한 값을 세팅
		sendInfo.setSeq(getLastMessageId());

		//모델객체를 json스트링으로 변환
		ObjectMapper mapper = new ObjectMapper();
		String cmd = mapper.writeValueAsString(sendInfo);

		//공통모듈 saveFiles 호출을 위해 파라미터 세팅
		FileAttachInfoV2 attachInfo = new FileAttachInfoV2();
		attachInfo.setCaller("mobiletalk");
		attachInfo.setComment(cmd);
		attachInfo.setImageFile(sendInfo.getImageFile());

		//결과코드 세팅
//    	MobileTalkSendResult attachResult = saveFiles(attachInfo, sendInfo.getApiUrl());

		//Ln.i("TalkSend > result : " + mapper.writeValueAsString(attachResult));
		//Ln.i("TalkSend > error_code : " + attachResult.getError_code());
		//Ln.i("TalkSend > error_msg : " + attachResult.getError_message());

		return saveFiles(attachInfo, sendInfo.getApiUrl());
	}

	/**
	 * showMesaveFiles
	 *
	 * @param attachInfo
	 * @param uploadUrl
	 * @param isEventVideo
	 * @return ShowmeAttachResult
	 * @throws Exception 익셉션
	 */
	public ShowmeAttachResult showMesaveFiles(FileAttachInfoV2 attachInfo, String uploadUrl, final boolean isEventVideo) throws Exception {

		MultiValueMap<String, Object> formData = new LinkedMultiValueMap<String, Object>();
		ShowmeAttachResult result = new ShowmeAttachResult();
		isCancel = false;

		String attachUrl = null;
		//10/19 품질팀 요청
		if(attachInfo == null){
			return result;
		}

		//호출자별로 파라미터 분기처리
		AttachParam attachParam;
		if (ATTACH_CALLER.MOBILETALK.toString().equalsIgnoreCase(attachInfo.getCaller())) {
			//파라미터 이름 세팅
			attachParam = new AttachParam("caller", "cmd", "file");
			formData.add(attachParam.getCaller(), attachInfo.getCaller());
			formData.add(attachParam.getContent(),   "" + attachInfo.getComment().replaceAll("\n", "<br>")); // 첨부파일 처리
//		} else if (ATTACH_CALLER.SHOWMECAFE.toString().equalsIgnoreCase(attachInfo.getCaller())) {
//			attachParam = new AttachParam("caller", "mbdContent", "file");
		} else if (ATTACH_CALLER.WEEKLY.toString().equalsIgnoreCase(attachInfo.getCaller())) {

			attachParam = new AttachParam("caller", "mbdContent", "img_upload_file");
			formData.add(attachParam.getCaller(), attachInfo.getCaller());
			formData.add(attachParam.getContent(),   "" + attachInfo.getComment().replaceAll("\n", "<br>")); // 첨부파일 처리
		} else if (ATTACH_CALLER.LIVETALK.toString().equalsIgnoreCase(attachInfo.getCaller())) {
			formData.add("imgType", attachInfo.getImageType());
		}

		if (attachInfo.getImageFile() != null) {
			//formData.add("file1", new FileSystemResource(attachInfo.getImageFile()));
			File[] files = attachInfo.getImageFile();
			for (int i = 0; i < files.length; i++) {
				if (files[i] != null) {
					String fileName = URLEncoder.encode(files[i].getName(), "UTF-8");
					attachUrl = new StringBuilder().append(uploadUrl).append("?").append("fileName=").append(fileName).append("&category=").append(category).toString();

					formData.add(fileName, new ProgressFileSystemResource(files[i], new ProgressFileSystemResource.OnUploadListener() {

						/**
						 * onProgressUpdate
						 *
						 * @param v
						 */
						@Override
						public void onProgressUpdate(float v) {
							if (isEventVideo) {
								updateProgress(v);
							}
						}

						/**
						 * isCancel
						 *
						 * @return
						 */
						@Override
						public boolean isCancel() {
								return isCancel;
							}
					}));
				}
			}

			try {
				if (isEventVideo) {
					showProgress();
				}

				result =  attachConnector.saveFiles(formData, attachUrl, isEventVideo);

				if (isEventVideo) {
					hideProgress();
				}
			} catch (Exception ex) {
				Ln.e(ex);
				throw ex;
			}
		}
		if (isCancel) {
			result.result = "cancel";
		}
		return result;
	}

	/**
	 * showProgress
	 */
	private void showProgress() {
		ThreadUtils.INSTANCE.runInUiThread(() -> {
			pDialog = new CustomProgressDialog((Activity) context)
					.message("업로드중")
					.buttonClick(dialog -> {
						isCancel = true;
						pDialog.dismiss();
					}).cancelled(new CustomProgressDialog.CancelListener() {
						@Override
						public void onCancel(Dialog dialog) {
							isCancel = true;
							pDialog.dismiss();
						}
					});
			pDialog.show();
		});
	}

	/**
	 * hideProgress
	 */
	private void hideProgress() {
		ThreadUtils.INSTANCE.runInUiThread(() -> {
			if (pDialog != null) {
				pDialog.dismiss();
			}
		});
	}

	/**
	 * updateProgress
	 * @param f float
	 */
	private void updateProgress(float f) {
		ThreadUtils.INSTANCE.runInUiThread(() -> {
			if (pDialog != null) {
				pDialog.setProgress((int) f);
				pDialog.setProgressText(String.format("%.1f", f) + "%");
			}
		});
	}

	/**
	 * 첨부파일 및 사용자 입력내용 저장 (모바일상담)
	 *
	 * @param attachInfo 파일첨부 정보
	 * @param uploadUrl 파일업로드 주소
	 * @return MobileTalkSendResult
	 * @throws Exception 익셉션 발생시 이걸 사용하시오 (Exception 을 사용해도 무방)
	 */
	public MobileTalkSendResult saveFiles(FileAttachInfoV2 attachInfo, String uploadUrl) throws Exception {
		MultiValueMap<String, Object> formData = new LinkedMultiValueMap<String, Object>();
		MobileTalkSendResult result = null;
		//10/19 품질팀 요청
		if(attachInfo == null){
			return result;
		}
		//호출자별로 파라미터 분기처리
		AttachParam attachParam = null;
		if (ATTACH_CALLER.MOBILETALK.toString().equalsIgnoreCase(attachInfo.getCaller())) {
			//파라미터 이름 세팅
			attachParam = new AttachParam("caller", "cmd", "file");
		} else if (ATTACH_CALLER.SHOWMECAFE.toString().equalsIgnoreCase(attachInfo.getCaller())) {
			attachParam = new AttachParam("caller", "mbdContent", "file");
		} else if (ATTACH_CALLER.WEEKLY.toString().equalsIgnoreCase(attachInfo.getCaller())) {
			attachParam = new AttachParam("caller", "mbdContent", "img_upload_file");
		} else{
			//타입이 정상이 아닌경우
		}

		// attachParam null 인경우가 발생할수 있다 방어로직 생성
		// NullPointerException might be thrown as 'attachParam' is nullable 10/05
		if( attachParam != null  ) {
			formData.add(attachParam.getCaller(), attachInfo.getCaller());
			formData.add(attachParam.getContent(), "" + attachInfo.getComment());

			if (attachInfo.getImageFile() != null) {
				File[] files = attachInfo.getImageFile();
				for (int i = 0; i < files.length; i++) {
					if (files[i] != null) {
						formData.add(attachParam.getFile(), new FileSystemResource(files[i]));
					}
//				else {
//        			//formData.add("files", null);
//        		}
				}
			}
		}

		try {
			result =  attachConnector.saveFile(formData, uploadUrl);
		} catch (Exception ex) {
			Ln.e(ex);
			throw ex;
		}

		return result;
	}

	private class AttachParam {
		/**
		 * caller
		 */
		private String caller = "";
		/**
		 * content
		 */
		private String content = "";
		/**
		 * file
		 */
		private String file = "";

		/**
		 * AttachParam
		 *
		 * @param caller
		 * @param content
		 * @param file
		 */
		public AttachParam(String caller, String content, String file) {
			this.caller = caller;
			this.content = content;
			this.file = file;
		}

		/**
		 * @return the caller
		 */
		public String getCaller() {
			if(caller == null){
				return "";
			}
			return caller;
		}

		/**
		 * @param caller the caller to set
		 */
		public void setCaller(String caller) {
			this.caller = caller;
		}

		/**
		 * @return the content
		 */
		public String getContent() {
			return content;
		}

		/**
		 * @param content the content to set
		 */
		public void setContent(String content) {
			this.content = content;
		}

		/**
		 * @return the file
		 */
		public String getFile() {
			return file;
		}

		/**
		 * @param file the file to set
		 */
		public void setFile(String file) {
			this.file = file;
		}
	}
}
