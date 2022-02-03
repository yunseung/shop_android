/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.intro;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.CountDownTimer;
import android.view.View;

import com.google.inject.Inject;
import com.gsshop.mocha.device.AppInfo;
import com.gsshop.mocha.pattern.chain.BaseCommand;
import com.gsshop.mocha.pattern.chain.CommandChain;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog;
import gsshop.mobile.v2.support.ui.CustomTwoButtonDialog;
import gsshop.mobile.v2.support.version.AppVersionModel;
import gsshop.mobile.v2.support.version.VersionAction;
import gsshop.mobile.v2.util.MarketUtils;
import gsshop.mobile.v2.util.ThreadUtils;
import roboguice.util.Ln;

/**
 * 최신버전 체크.
 *
 * 버전체크 실패시에도 앱은 정상 구동시킴
 *
 * - 버전체크 실패하면 web 서버상의 oops.html 확인
 * 		- Y		.gsshop.com 공사중페이지 이동
 * 			- 	m.gsshop.com 공사중으로 포워딩이 일어난다. 만약 오탐이였다면 메인이 뜬다.
 * 	    - 그외	다음 스텝 chain.next(activity);
 * 	    	-	서비스 진입 시킴
 *		- error	cdn 서버상의 oops.html 확인
 * 			- web 서버상의 oops.html 에러이면 http://apperror.gsshop.com/oops.html 확인
 * 				- Y		http://apperror.gsshop.com/apperror.html 띄운다.
 * 					- cdn에 있는 html 페이지
 * 				- 그외	다음 스텝 chain.next(activity);
 * 					- 서비스 진입 시킴
 * 				- error 다음 스텝 chain.next(activity);
 * 					- 서비스 진입 시킴
 *
 * X version exception :  m../oops exception : appe../oops Y
 * O version exception :  m../oops exception : appe../oops etc
 * O version exception :  m../oops exception : appe../oops exception
 * O version exception :  m../oops Y  	(사이드 확인)
 * O version exception :  m../oops etc   (사이드 확인)
 *
 */
public class VersionCheckCommand extends BaseCommand {
	/**
	 * VersionCheckCommand 버전 액션
	 */
	@Inject
	private VersionAction versionAction;

	/**
	 * 패키지 정보
	 */
	@Inject
	private PackageInfo packageInfo;

	/**
	 * 업데이트 관련 팝업
	 */
	private Dialog dialog;

	/**
	 * pre-loaded앱을 위한 변수
	 */
	private ProgressDialog progressDialog;
	/**
	 * 딜레이 카운트
	 */
	private CountDownTimer delayTimer;
	/**
	 * 딜레이 타입
	 */
	private final int delayTime = 60;	//초

	/**
	 * CDN(IDC_ERROR) 에러 타입
	 */
	private final String IDC_ERROR_STRING = "IDC_ERROR";

	/**
	 * NoTabWebActivity에서 공사중페이지에 "재시작" 버튼을 노출하기 위한 구분자
	 */
	public static final String REFERRER_VERSION_CHECK_COMMAND = "version_check_command";

	/**
	 * VersionCheckCommand execute
	 * @param activity
	 * @param chain
     */
	@Override
	public void execute(Activity activity, CommandChain chain) {
		
		injectMembers(activity);

		ThreadUtils.INSTANCE.runInBackground(() -> {
			AppVersionModel versionInfo = null;

			try {
				//버전정보 API를 호출하여 취득한 정보를 로컬에 저장한다.
				versionInfo = versionAction.getAppVersionInfo();

			} catch (Exception e) {
				Ln.e(e);
				// com.google.gson.JsonSyntaxException: java.lang.IllegalStateException (와이파이가 인증이 필요한 경우에 파싱이 정상적으로 되지 않아 발생)
				// NOTE : 앱 실행이 원활하지 않습니다 팝업 이외의 다른 네트워크 상태 팝업 노출 필요
				try {
					if ("Y".equalsIgnoreCase(versionAction.getOopsHtml().trim())) {
						//공사중 웹뷰 페이지를 띄운다.
						showUnderConstruction(activity,true,null);
					} else {
						//버전체크 실패시 다음 커맨드로 진행시킴
						// 07/01 oops.html Y 가 아닌거지 에러가 난 상황은 아니다. 그래도 다음 스텝으로 이동해야 할까?
						chain.next(activity);
					}
				} catch (Exception e1) {

					try {
						//leems 20160701 서버 완전히 자빠졌을때를 위한 대비책
						//1. 버전도 안되고
						//2. 웁스도 안되면
						//3. http://apperror.gsshop.com/apperror.html 파일을 본다
						//4. "Y" 이면
						//5. "http://apperror.gsshop.com/mc_parking.html" 이동한다 임시 공사중이다.
						String tempError;
						tempError = versionAction.getAppErrorHtml().trim();
						if (  tempError != null && tempError.indexOf(IDC_ERROR_STRING) != -1 ){
							//CDN 공사중 웹뷰 페이지를 띄운다.
							showUnderConstruction(activity,false,ServerUrls.WEB.CDN_PARKING_URL);

						} else {
							//CDN oops Y가 아니면 다은 커맨드로 진행
							chain.next(activity);
						}
					} catch (Exception e2) {
						Ln.e(e2);
						//CDN oops 실패시 다은 커맨드로 진행
						chain.next(activity);
					}

					//Ln.e(e1);
					//버전체크 실패시 다음 커맨드로 진행시킴
					//chain.next(activity);
				}
				return;
			}

			// 환경설정화면에서 버전정보가 사용되므로 캐시처리
			versionAction.cacheAppVersionInfo(versionInfo);

			//앱의 버전코드 취득
			int versionCode = AppInfo.getAppVersionCode(packageInfo);

			//강제 업데이트 대상
			//10/19 품질팀 요청
			if (versionInfo != null && versionInfo.force != null && versionCode < Integer.parseInt(versionInfo.force)) {
				alertForceUpdateAvailable(activity, versionInfo.forcemsg);
				return;
			}

			//선택 업데이트 대상
			//10/19 품질팀 요청
			if (versionInfo != null && versionInfo.choicemsg != null && versionCode < Integer.parseInt(versionInfo.choice)) {
				alertUpdateAvailable(activity, chain, versionInfo.choicemsg);
				return;
			}

			//강제, 선택 어떤 대상도 아니면 다음 커맨드 수행
			//키바나 수집 로그로 앱크래쉬 방지 (java.lang.IndexOutOfBoundsException: Index: 18, Size: 18)
			try {
				chain.next(activity);
			} catch (IndexOutOfBoundsException e) {
				Ln.e(e);
			}
		});
	}

	/**
	 * 공사중 웹뷰 페이지를 띄운다. -> 메인화면을 띄운다.
	 * @param activity 대상
	 * @param isMain 메인 유무
	 * @param url 전달 URL
     */
	private void showUnderConstruction(Activity activity,boolean isMain,String url) {
		//백버튼 클릭시 해당 액티비티를 닫고나서, 메인으로 이동할지 구분하는 플래그
		boolean backToMain = activity.getIntent().getBooleanExtra(Keys.INTENT.BACK_TO_MAIN, false);
		//탭메뉴로 부터 호출되었는지 구분하는 플래그
		boolean fromTabMain = activity.getIntent().getBooleanExtra(Keys.INTENT.FROM_TAB_MENU, true);
		Intent intent = new Intent(Keys.ACTION.NO_TAB_WEB);
		intent.putExtra(Keys.INTENT.FROM_TAB_MENU, fromTabMain);
		intent.putExtra(Keys.INTENT.BACK_TO_MAIN, backToMain);
		intent.putExtra(Keys.INTENT.REFERRER, REFERRER_VERSION_CHECK_COMMAND);

		/**
		 * url 없거나 너무 짧은 경우에는 무조건 메인 이동
		 */
		if(isMain == false && url != null)
		{
			intent.putExtra(Keys.INTENT.WEB_URL, url);
		}
		else
		{
			intent.putExtra(Keys.INTENT.WEB_URL, ServerUrls.getHttpRoot());
		}


		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		activity.startActivity(intent);
		activity.finish();
	}

	/**
	 * 최신 버전 업그레이드 안내 팝업을 띄운다.
	 *
	 * @param activity 액티비티
	 * @param chain 커맨드 체인
	 */
	private void alertUpdateAvailable(final Activity activity, final CommandChain chain, String msg) {
		ThreadUtils.INSTANCE.runInUiThread(() -> {
			dialog = new CustomTwoButtonDialog(activity).title(R.string.update_title)
					.message(msg).positiveButtonLabel(R.string.update_now)
					.negativeButtonLabel(R.string.update_later)
					.positiveButtonClick(new CustomOneButtonDialog.ButtonClickListener() {
						@Override
						public void onClick(Dialog dialog) {
							dialog.dismiss();
							MarketUtils.goMarket(activity);
							activity.finish();
						}
					}).negativeButtonClick(new CustomOneButtonDialog.ButtonClickListener() {
						@Override
						public void onClick(Dialog dialog) {
							dialog.dismiss();
							chain.next(activity);
						}
					}).cancelled(new CustomOneButtonDialog.CancelListener() {
						@Override
						public void onCancel(Dialog dialog) {
							chain.next(activity);
						}
					});
			dailogShow(activity);
		});
	}

	/**
	 * 강제 업그레이드 안내 팝업을 띄운다.
	 *`
	 * @param activity 액티비티
	 * @param msg 강제 업데이트 메시지
	 */
	private void alertForceUpdateAvailable(final Activity activity, String msg) {
		ThreadUtils.INSTANCE.runInUiThread(() -> {
			dialog = new CustomOneButtonDialog(activity).title(R.string.update_title)
					.message(msg);
			((CustomOneButtonDialog)dialog).getButton().setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					MarketUtils.goMarket(activity);
					dialog.dismiss();
					activity.finish();
				}
			});
			dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					dialog.dismiss();
					activity.finish();
				}
			});
			dailogShow(activity);
		});
	}

	/**
	 * onCancelled
	 */
	@Override
	public void onCancelled() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}

    	if (progressDialog != null) {
    		progressDialog.dismiss();
    	}
    	
		if (delayTimer != null) {
			delayTimer.cancel();	
		}
		
		super.onCancelled();
	}
	
	/**
	 * 업데이트 팝업을 노출한다.
	 * 
	 * @param activity 액티비티
	 */
	private void dailogShow(final Activity activity) {
		dialog.show();
	}
}
