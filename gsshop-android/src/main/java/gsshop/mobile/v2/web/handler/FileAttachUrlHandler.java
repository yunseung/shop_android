/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web.handler;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.Keys.REQCODE;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.attach.FileAttachAction;
import gsshop.mobile.v2.attach.FileAttachParamInfo;
import gsshop.mobile.v2.attach.FileAttachUtils;
import gsshop.mobile.v2.attach.MobileTalkActivity;
import gsshop.mobile.v2.menu.TabMenu;
import gsshop.mobile.v2.util.StringUtils;

/**
 * 파일첨부가 있는 웹뷰 화면(이벤트, 쇼미더카페 등)에서 앱에 처리를 요청한다.
 *
 * sample)
 * toapp://attach?caller=showmecafe{@literal &}uploadUrl=http://10.52.181.161:8080/rest/upload{@literal &}returnUrl=http://m.gsshop.com/cafe/showmecafe/CafeCommunityMain.gs?tabType=community
 * 이벤트동영상)
 * toapp://attach?caller=eventvideo{@literal &}uploadUrl=http://event.gsshop.com/cafe/common/doFileUploadApp.gs{@literal &}maxsize=300{@literal {@literal &}}category=xxxxz{@literal &}mediatype=video{@literal &}callback=appUploadVideo
 * 라이브툭 이미지첨부)
 * toapp://attach?caller=livetalk&uploadUrl=http://m.gsshop.com/cafe/common/doFileUploadApp.gs&mediatype=image&imagecount=1&callback=replyForm.appUploadImage
 *
 *
 */
public class FileAttachUrlHandler implements WebUrlHandler {

	private Activity mActivity;

	@Override
	public boolean handle(Activity activity, WebView webview, String url) {
		mActivity = activity;
		FileAttachParamInfo fileAttachParamInfo = new FileAttachParamInfo();
		Uri uri = Uri.parse(url);

		fileAttachParamInfo.setCaller(uri.getQueryParameter("caller"));
		fileAttachParamInfo.setUploadUrl(uri.getQueryParameter("uploadUrl"));
		fileAttachParamInfo.setReturnUrl(uri.getQueryParameter("returnUrl"));
		fileAttachParamInfo.setCallback(uri.getQueryParameter("callback"));
		fileAttachParamInfo.setMediatype(uri.getQueryParameter("mediatype"));
		fileAttachParamInfo.setImageCount(uri.getQueryParameter("imagecount"));
		fileAttachParamInfo.setTalkui(uri.getQueryParameter("talkui"));
		fileAttachParamInfo.setHistoryBack(uri.getQueryParameter("back"));

		String maxSize = uri.getQueryParameter("maxsize");
		if (maxSize != null && StringUtils.isNumeric(maxSize) && Integer.valueOf(maxSize) > 0) {
			fileAttachParamInfo.setMaxVideoSize(Integer.valueOf(maxSize));
		} else {
			fileAttachParamInfo.setMaxVideoSize(-1);
		}

		//카테고리값 세팅
		FileAttachAction.category = uri.getQueryParameter("category");

		MainApplication.fileAttachParamInfo = fileAttachParamInfo;

		Class<?> cls = null;
		int reqCode = 0; 
		if (FileAttachAction.ATTACH_CALLER.MOBILETALK.toString().
				equalsIgnoreCase(fileAttachParamInfo.getCaller())) {	//모바일상담
			cls = MobileTalkActivity.class;
			reqCode = REQCODE.MOBILETALK;
		} else if(FileAttachAction.ATTACH_CALLER.SHOWMECAFE.toString().equalsIgnoreCase(fileAttachParamInfo.getCaller())
				|| FileAttachAction.ATTACH_CALLER.REVIEW.toString().equalsIgnoreCase(fileAttachParamInfo.getCaller())
				|| FileAttachAction.ATTACH_CALLER.EVENTVIDEO.toString().equalsIgnoreCase(fileAttachParamInfo.getCaller())
				|| FileAttachAction.ATTACH_CALLER.IMAGESEARCH.toString().equalsIgnoreCase(fileAttachParamInfo.getCaller())){
			if("image".equals(fileAttachParamInfo.getMediatype())){
				if (FileAttachAction.ATTACH_CALLER.IMAGESEARCH.toString().equalsIgnoreCase(fileAttachParamInfo.getCaller())) {
					addPhoto(fileAttachParamInfo, true);
				}
				else {
					if(FileAttachAction.ATTACH_CALLER.SHOWMECAFE.toString().equalsIgnoreCase(fileAttachParamInfo.getCaller())) {
						fileAttachParamInfo.setImageCount("1"); // 기존에도 하나의 이미지만 허용 가능.
					}
					addPhoto(fileAttachParamInfo);
				}
			}else if("video".equals(fileAttachParamInfo.getMediatype())){

				// TODO: 2019. 11. 23.  MSLEE
				// video 이면셔, EVENTVIDEO / SHOWMECAFE 면 프로그래스바가 노출되게 설정
				// 추후 서비스 기능
				addVideo(FileAttachAction.ATTACH_CALLER.EVENTVIDEO.toString().equalsIgnoreCase(fileAttachParamInfo.getCaller())
								|| FileAttachAction.ATTACH_CALLER.REVIEW.toString().equalsIgnoreCase(fileAttachParamInfo.getCaller() ),
						fileAttachParamInfo.getMaxVideoSize());
			}
			return true;
		} else if(FileAttachAction.ATTACH_CALLER.LIVETALK.toString().equalsIgnoreCase(fileAttachParamInfo.getCaller())) {
			liveTalk_addPhoto(fileAttachParamInfo, false);
			return true;
		}

		Intent intent = new Intent(activity, cls);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);  
		intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.getTabMenu(activity.getIntent()));
		intent.putExtra("fileAttachParamInfo", fileAttachParamInfo);
		activity.startActivityForResult(intent, reqCode);

		return true;
	}

	/**
	 * 사진 추가하기
	 */
	private void addPhoto(FileAttachParamInfo fileAttachParamInfo) {
		addPhoto(fileAttachParamInfo, false);
	}

	/**
	 * 사진 추가하기 (이미지 서칭일 경우 mseq 보내야 해서 함수 추가됨)
	 */
	private void addPhoto(FileAttachParamInfo fileAttachParamInfo, boolean isImgSearch) {
		FileAttachUtils.executePhotoPopup(mActivity, Integer.parseInt(fileAttachParamInfo.getImageCount()), isImgSearch);
	}

	/**
	 * 라이브톡일때 사진/갤러리 추가하기
	 */
	private void liveTalk_addPhoto(FileAttachParamInfo fileAttachParamInfo, boolean isImgSearch) {
		FileAttachUtils.executeLiveTalkPopup(mActivity, Integer.parseInt(fileAttachParamInfo.getImageCount()), isImgSearch);
	}

	/**
	 * 비디오 추가하기
	 */
	private void addVideo(boolean isEventVideo, int maxVideoSize) {
        // 비디오 시스템 갤러리에서 받아오도록 수정.
		/*
		String action = Keys.ACTION.GALLERY;
		Intent intent = new Intent(action);
		intent.putExtra("type", "video");
		intent.putExtra("isEventVideo", isEventVideo);
		//maxVideoSize 세팅안하면 GalleryPhotoListPageFragment에 설정된 디폴트값 사용함 (200MB)
		if (maxVideoSize > 0) {
			intent.putExtra("maxVideoSize", maxVideoSize);
		}
		mActivity.startActivityForResult(intent, Keys.REQCODE.GALLERY);
		*/
		FileAttachUtils.goGalleryVideo(mActivity, REQCODE.PHOTO_VIDEO);
	}

	@Override
	public boolean match(String url) {
		return url.startsWith(ServerUrls.APP.FILEATTACH);
	}
}
