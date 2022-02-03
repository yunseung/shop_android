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
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.gsshop.mocha.device.AppInfo;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.pattern.chain.Command;
import com.gsshop.mocha.pattern.chain.CommandExecutor;
import com.gsshop.mocha.pattern.mvc.Model;
import com.gsshop.mocha.ui.util.ViewUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gsshop.mobile.v2.AbstractBaseActivity;
import gsshop.mobile.v2.AbstractTMSService;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.ParallelHandler;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.ServerUrls.WEB;
import gsshop.mobile.v2.push.PushFcmRequestTokenCommand;
import gsshop.mobile.v2.push.PushSettings;
import gsshop.mobile.v2.support.gtm.datahub.DatahubAction;
import gsshop.mobile.v2.support.gtm.datahub.DatahubUrls;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog.ButtonClickListener;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog.CancelListener;
import gsshop.mobile.v2.support.ui.PushConfirmDialog;
import gsshop.mobile.v2.util.DateUtils;
import gsshop.mobile.v2.util.DeviceUtils;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.MigrationUtils;
import gsshop.mobile.v2.util.PrefRepositoryNamed;
import gsshop.mobile.v2.util.RestClientUtils;
import gsshop.mobile.v2.util.ThreadUtils;
import gsshop.mobile.v2.web.MainWebViewClient;
import gsshop.mobile.v2.web.WebViewControlInherited;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * 스플래시(인트로) 화면.
 * <p>
 * ※ 필독
 * 본 액티비티에서 수행하는 커맨드중 일부는 BaseTabMenuActivity.restoreStateAfterKilledBySystem에서도 함께 수행해야 한다.
 * (OutOfMemoryError 대응)
 * <p>
 * ※ 수행하는 작업 - 네트워크 연결상태 확인 - 최신 버전 업데이트 확인 - PUSH 관련 정보 저장 - 로그인 처리(로그인 유지 상태인
 * 경우) - 뱃지 정보 조회 - 메인 액티비티로 이동 - ...
 * <p>
 * NOTE : android:launchMode="singleTop"
 */
public class IntroActivity extends AbstractBaseActivity
        implements AuthDialogFragment.OnAuthConfirmListener {

    public static ParallelHandler parallelHandler;

    private Activity mContext;

    @Inject
    private PackageInfo packageInfo;

    @Inject
    private RestClient restClient;

    /**
     * IntroActivity commandExecutor
     */
    private CommandExecutor commandExecutor;

    /**
     * dialog
     */
    private Dialog dialog;

    /**
     * dialogfragment
     */
    private AuthDialogFragment authDialogFragment;

    /**
     * bgImage
     */
    @InjectView(R.id.intro_bg)
    private ImageView bgImage;


    /**
     * 인트로 회원 등급
     */
    public List<IntroImageInfo.AppIntroTxt> appIntroTxt;

    @InjectView(R.id.view_into_grade)
    public View view_into_grade;

    @InjectView(R.id.text_intro_grade)
    public TextView gradeText;

    @InjectView(R.id.text_intro_name)
    public TextView introNameText;

    @InjectView(R.id.text_intro_message)
    public TextView messageText;

    @InjectView(R.id.view_intro_round_line)
    public View roundLineView;

    /**
     * sm일때만 인트로 이미지 우측하단에 작게 표시
     */
    @InjectView(R.id.text_sm21)
    public TextView txtSm21;

    /**
     * mWebControl
     */
    private WebViewControlInherited webControl;

    /**
     * IntroActivity onCreate
     *
     * @param savedInstanceState savedInstanceState
     */
    /**
     * tensera
     * 1. Add TenseraApi.preloader().handleSplashActivity(this) to void onCreate(Bundle savedInstanceState) before calling the super.onCreate(Bundle savedInstaceState):
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        try {
//            TenseraApi.preloader().handleSplashActivity(this);
//        } catch (Exception e) {
//            //무슨일이 벌어질까?
//        }
        super.onCreate(savedInstanceState);

        setContentView(R.layout.intro);

        this.mContext = this;

        //Pref 마이그레이션
        MigrationUtils.migratePref();

        //프로모션 팝업 플래그 초기화 (프로모션 팝업은 홈화면에서 한번만 노출되어야 함)
        if (!MainApplication.isHomeCommandExecuted) {
            PrefRepositoryNamed.saveBoolean(MainApplication.getAppContext(), Keys.CACHE.PROMOTION, false);
            PrefRepositoryNamed.saveBoolean(MainApplication.getAppContext(), Keys.CACHE.PUSH_APPROVE, false);
        }

        //NOTE : StrictMode 영향도 확인 후 제거 필요함
        //네트워크 통신을 위한 보정코드 추가 (KT GIGABeacon 개발가이드 3.3 항목)
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        // NOTE : 커맨드 목록. 추가된 순서대로 실행되므로 순서를 함부로 바꾸지 말 것.
        List<Class<? extends Command>> commands = new ArrayList<Class<? extends Command>>();

        // 이미 인트로 작업이 완료되어 메인 탭 화면으로 이동한 경우(introCompleted == true)에는
        // 네트워크 체크, 업데이트 체크 등의 작업을 진행하지 않는다.
        if (!MainApplication.introCompleted ) {

            //인트로 속도 측정
            //2015-04-15 속도 부하로 인해 주석 Speed
            try {
                MainApplication mainApp = (MainApplication) getApplicationContext();
                mainApp.setStartTiming(MainApplication.INTRO);
            } catch (Exception e) {
                // 10/19 품질팀 요청
                // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
                // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
                // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
                Ln.e(e);
                // 익셉션 발생시 무시
            }

            //인트로 이미지 세팅
            setIntroImage();

            // 가장 처음으로 AdvertisingId 를 가져오게끔 한다. (사용할때마다 얻어오면 Pending 가능성 있음)
            commands.add(GetAdvIDCommand.class);

            //위 두개는 인트로에 꼭 있어야한다
            commands.add(NetworkCheckCommand.class);

            commands.add(LunaCommand.class);

            commands.add(ApptimizeRunExp.class);
            commands.add(ApptimizeCommand.class); //앱티 초기화 next  1.4초
            commands.add(ApptimizeTABNAME2Command.class); //next 0.2초
            //commands.add(ApptimizeCategoryCommand.class);
            //commands.add(ApptimizeHOMESUBCommand.class);
            commands.add(ApptimizeContentsCommand.class);
            //commands.add(ApptimizeCarouselCommand.class);
            //commands.add(ApptimizeScheduleCommand.class);
            //commands.add(ApptimizeTodayOpenCommand.class);
            //commands.add(ApptimizeIMGCommand.class); //next   0.2초
            //commands.add(ApptimizeIMGMSCommand.class); //next
            commands.add(ApptimizeEndCommand.class); // next all end

            //FCM 토큰 등록 및 프리퍼런스에 저장
			//FCM : 토큰 갱신 방법 변경
			commands.add(PushFcmRequestTokenCommand.class);

            // 인트로 이미지 가져오는 커맨드
            commands.add(IntroImageCommand.class);

            //tm14에서는 버전체크 안하도록
            if (!ServerUrls.getHttpRoot().contains("atm.gsshop.com") ||
                    !ServerUrls.getHttpRoot().contains("tm14.gsshop.com")) {
                commands.add(VersionCheckCommand.class);
            }
            //Amplitude.getInstance().logEvent("APP_OPEN");

            //amp (자동로그인 트래킹을 위해 LoginCommand 이전에 초기화 수행)
            commands.add(AmpCommand.class);

            //이벤트 등 딥링크로 접속한 경우 로그인 유지가 안되어 홈액티비티에서 인트로 액티비티로 다시 이동
            commands.add(LoginCommand.class);

            commands.add(FacebookCommand.class);
            commands.add(CriteoCommand.class);
            //CSP 버전을 체크 하여 서비스를 체크 할지 않할지 결정한다. 프리퍼에 저장
            commands.add(CSPCommand.class);


            // 앱 실행시 pcid 쿠키를 생성하기 위해 호출
//			callAppStartUrl();

            //접근권한 고지 팝업 노출
            showFirstAuthPopup();
        }

        MainApplication.isAppView = true;
        commands.add(HomeMenuCommand.class);
        // 28 (안드로이드 9.0) 이상버전이면 속도가 빠르기 때문에 홈 매장 사전 캐시를 진행 하도록 한다.
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            commands.add(DefaultShopCommand.class);
        }
        commands.add(StartNextActivityCommand.class);

        commandExecutor = new CommandExecutor(this, commands);
        parallelHandler = new ParallelHandler(commandExecutor); //ApptimizeExp 2,3,4 병렬처리를 위해 생성
        commandExecutor.execute();
    }

    /**
     * 0. check permission
     * 1. 받아놓은 인트로 이미지 있는지 체크 없으면 인트로 이미지를 받고 있으면 인트로 이미지 그림
     * 2. 없으면 서버로부터 인트로 이미지를 받아서 파일로 저장(항상 같은 경로 같은 파일명 사용)
     * 3. check validation (date)
     */
    public void setIntroImage() {
        try {
            // internal storage 사용으로 퍼미션과 상관없음
            File imgFile = new File(getFilesDir() + File.separator + getResources().getString(R.string.intro_img_filename));

            // 인트로 이미지 파일 존재하고 기간 내 일 경우, 받아놓은 인트로 이미지를 그림
            // 디폴트 이미지는 layout에 지정되어 있음

            // imgFile.exists ()만 false 인 경우 prf file 삭제 코드가 필요할것으로 판단

            if (imgFile.exists() && IntroImageInfo.isValidDate()) {
                // glide 보다 살짝 느림(매번)
                final Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                bgImage.setImageBitmap(myBitmap);
            }

            //sm일때만 테스트 표식 보이도록 (앱이름에 . 없애는대신 추가)
            if (ServerUrls.getHttpRoot().contains("asm.gsshop.com")) {
                txtSm21.setVisibility(View.VISIBLE);
            }else{
                txtSm21.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            // 10/19 품질팀 요청
            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
            // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
            Ln.e(e);
        }
    }


    /**
     * onNewIntent
     *
     * @param newIntent Intent
     */
    @Override
    protected void onNewIntent(Intent newIntent) {
        super.onNewIntent(newIntent);

        // 새로 요청된 url로 이동하기 위해 WEB_URL 인텐트 값 갱신
        String targetUrl = newIntent.getStringExtra(Keys.INTENT.WEB_URL);
        if (targetUrl != null) {
            getIntent().putExtra(Keys.INTENT.WEB_URL, targetUrl);
        }

        int targetTabMenu = newIntent.getIntExtra(Keys.INTENT.TAB_MENU, -1);
        if (targetTabMenu > -1) {
            getIntent().putExtra(Keys.INTENT.TAB_MENU, targetTabMenu);
        }

        // MenuHostHandler에서 WebActivity로 가는 경우 IntroActivity에서 redirecting 관련
        // intent 값이 유실되어서 추가로 처리.
        // 현재는 onNewIntent()를 거치지 않지만 추후 가능성 고려.
        if (newIntent.getBooleanExtra(Keys.INTENT.CAN_CAUSE_REDIRECTING, false)) {
            getIntent().putExtra(Keys.INTENT.CAN_CAUSE_REDIRECTING, true);
        }

        //이때 MenuHostHandler에서 세팅한 _from_tab_menu, _back_to_main 값이 누락되어 세팅해줌
        getIntent().putExtra(Keys.INTENT.BACK_TO_MAIN, newIntent.getBooleanExtra(Keys.INTENT.BACK_TO_MAIN, false));
        getIntent().putExtra(Keys.INTENT.FROM_TAB_MENU, newIntent.getBooleanExtra(Keys.INTENT.FROM_TAB_MENU, true));
    }

    /**
     * MenuHostHandler에서 activit 분기하는 경우 IntroActivity에서 intent 값이 유실되기 때문에 추가로 처리
     *
     * @param intent Intent
     */
    @Override
    public void startActivity(Intent intent) {
        // web page redirecting
        if (getIntent().getBooleanExtra(Keys.INTENT.CAN_CAUSE_REDIRECTING, false)) {
            intent.putExtra(Keys.INTENT.CAN_CAUSE_REDIRECTING, true);
        }

        // move to specific section
        String sectionCode = getIntent().getStringExtra(Keys.INTENT.SECTION_CODE);
        if (DisplayUtils.isValidString(sectionCode)) {
            intent.putExtra(Keys.INTENT.SECTION_CODE, sectionCode);
        }
        String navigationId = getIntent().getStringExtra(Keys.INTENT.NAVIGATION_ID);
        if (DisplayUtils.isValidString(navigationId)) {
            intent.putExtra(Keys.INTENT.NAVIGATION_ID, navigationId);
        }
        super.startActivity(intent);
    }

    /**
     * 백키로 종료되지 않게 함.
     */
    @Override
    public void onBackPressed() {
        // 백키로 종료되지 않게 함.
    }

    /**
     * 접근권한 고지 팝업
     */
    private void showFirstAuthPopup() {
        ThreadUtils.INSTANCE.runInUiThread(() -> {
            //이전버전 프리퍼런스 제거 (접근권한 고지 다시 띄우는 경우)
            if (AuthConfirmInfo.isNeedReShow()) {
                AuthConfirmInfo.remove();
            }

            //버전코드 저장
            PackageInfo pi = AppInfo.getPackageInfo(MainApplication.getAppContext());
            PrefRepositoryNamed.save(MainApplication.getAppContext(), Keys.CACHE.VERSION_CODE, AppInfo.getAppVersionCode(pi));

            //프리퍼런스 취득2
            AuthConfirmInfo authConfirmInfo = AuthConfirmInfo.get();
            if (authConfirmInfo == null) {
                //없으면 신규 생성
                authConfirmInfo = new AuthConfirmInfo();
                //버전세팅 (프리퍼런스 제거하지 않기 위해)
                authConfirmInfo.save();
            }

            //접근권한 팝업 노출
            if (!authConfirmInfo.isShow) {
                MainApplication.isAuthCheck = false;
                authDialogFragment = AuthDialogFragment.newInstance();
                authDialogFragment.setCancelable(false);
                authDialogFragment.show(getSupportFragmentManager(), IntroActivity.class.getSimpleName());
            }

            //앱 구동시 이전 API 전송실패건에 대해 재전송 시도
            if (!authConfirmInfo.isSendToServer) {
                sendAuthConfirm(true);
            }
        });
    }

    /**
     * PUSH 알림을 수신할지 사용자에게 물음.
     */
    private void showFirstPushApprovePopup() {
        ThreadUtils.INSTANCE.runInUiThread(() -> {
            // NOTE : 설정정보는 마이그레이션 완료후에 가져와야 함
            // PUSH허용 팝업을 보여준 적이 없으면 팝업 보여줌
            if (!PushSettings.get().shownApprovePopup) {
                MainApplication.isPushCheck = false;
                dialog = new PushConfirmDialog(mContext)
                        .positiveButtonClick(new ButtonClickListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                dialog.dismiss();
                                updatePushApproveSetting(true);
                            }
                        }).negativeButtonClick(new ButtonClickListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                dialog.dismiss();
                                updatePushApproveSetting(false);
                            }
                        }).cancelled(new CancelListener() {
                            @Override
                            public void onCancel(Dialog dialog) {
                                updatePushApproveSetting(false);
                            }
                        });
                dialog.show();

                //인트로에서 푸시동의 화면이 노출된 경우는 메인에서 푸시유도팝업을 노출하지 않음
                PrefRepositoryNamed.saveBoolean(MainApplication.getAppContext(), Keys.CACHE.PUSH_APPROVE, true);
            }
        });
    }

    /**
     * updatePushApproveSetting
     *
     * @param approve 수신 여부
     */
    private void updatePushApproveSetting(boolean approve) {

        PushSettings settings = PushSettings.get();
        settings.approve = approve;
        settings.shownApprovePopup = true;
        settings.save();

        MainApplication.isPushCheck = true;

        if (approve) {
            Toast.makeText(this, getResources().getString(R.string.msg_push_on) + "GS SHOP (" + DateUtils.getToday("yyyy.MM.dd") + ")", Toast.LENGTH_SHORT).show();
            //GTM Datahub 이벤트 전달
            DatahubAction.sendDataToDatahub(this, DatahubUrls.D_1001, "");
        } else {
            Toast.makeText(this, getResources().getString(R.string.msg_push_off) + "GS SHOP (" + DateUtils.getToday("yyyy.MM.dd") + ")", Toast.LENGTH_SHORT).show();
            //GTM Datahub 이벤트 전달
            DatahubAction.sendDataToDatahub(this, DatahubUrls.D_1002, "");
        }

        // TODO: 2016. 10. 21. MSLEE : 개선은 필요함
        // 박과장님 꼼꼼함에 탄복;
        // 아래주석은 홈엑티비에 있는 주석입니다
        // 속도 개선 위해 인트로에 있떤 푸시 수신여부 설정을 홈으로 옮기면서 인트로시점에 피엠에스를 세팅해서 실패함
        //  IntroActivity에서 "좋아요/싫어요"를 반영하여 수행한 pushSettings이 TMS 서버에 적용이 안됨
        //  이에 대한 보완책으로 HomeActivity에서 pushSettings을 다시 수행해줌
        AbstractTMSService.pushSettings(this, settings);
    }

    /**
     * 앱 실행시 특정 쿠키를 생성하기 위해 URL을 호출한다.
     */
    private void callAppStartUrl() {
        webControl = new WebViewControlInherited.Builder(this).target((WebView) findViewById(R.id.webView))
                .with(new MainWebViewClient(this) {

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        Ln.i("[IntroActivity onPageStarted]" + url);
                        super.onPageStarted(view, url, favicon);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                    }

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        Ln.i("[IntroActivity shouldOverrideUrlLoading]" + url);
                        return super.shouldOverrideUrlLoading(view, url);
                    }
                }).build();

        try {
            webControl.clearCache();
            webControl.getWebView().post(new Runnable() {
                @Override
                public void run() {
                    webControl.loadUrl(WEB.APP_START, MainApplication.customHeaders);
                }
            });
        } catch (Exception e) {
            // 10/19 품질팀 요청
            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
            // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
            Ln.e(e);
        }
    }

    /**
     * onDestroy
     */
    @Override
    protected void onDestroy() {
        // 진행중인 작업 취소.
        if (commandExecutor != null) {
            commandExecutor.cancel();
        }
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        super.onDestroy();
    }

    /**
     * 권한확인여부 정보를 전송한다.
     *
     * @param isConfirm 확인여부
     */
    private void sendAuthConfirm(boolean isConfirm) {
        ThreadUtils.INSTANCE.runInBackground(() -> {
            AuthConfirmParam param = new AuthConfirmParam();
            String gsuuid = DeviceUtils.getGsuuid(MainApplication.getAppContext());
            param.gsuuid = gsuuid == null ? "" : gsuuid;
            param.appGB = "01";
            param.appVersion = AppInfo.getAppVersionName(packageInfo);
            param.osVersion = Build.VERSION.RELEASE;
            param.osGB = "A";
            param.confirmYN = isConfirm ? "Y" : "N";

            //API 전송 전 플래그 실패로 초기화
            AuthConfirmInfo authConfirmInfo = AuthConfirmInfo.get();
            if (authConfirmInfo != null) {
                authConfirmInfo.isSendToServer = false;
                authConfirmInfo.save();
            }

            try {
                AuthConfirmResult result = RestClientUtils.INSTANCE.post(restClient, param, ServerUrls.REST.AUTH_CONFIRM, AuthConfirmResult.class);
                if (result != null && "S".equalsIgnoreCase(result.result)) {
                    //API 전송 성공 시 재전송 안하도록 값 세팅
                    AuthConfirmInfo authInfo = AuthConfirmInfo.get();
                    if (authInfo != null) {
                        authInfo.isSendToServer = true;
                        authInfo.save();
                    }
                }
            } catch (Exception e) {
                Ln.e(e);
            }
        });
    }

    /**
     * 접근권한 팝업에서 확인버튼 클릭시 수행되는 콜백
     */
    @Override
    public void onAuthConfirmed() {
        //앱구동시 최초 1회만 노출하기 위해 값 저장
        AuthConfirmInfo authConfirmInfo = AuthConfirmInfo.get();
        if (authConfirmInfo != null) {
            authConfirmInfo.isShow = true;
            authConfirmInfo.save();
        }
        //푸시동의팝업 노출
        showFirstPushApprovePopup();
        //접근권한 API 호출
        sendAuthConfirm(true);
        //인트로화면 대기시키는 플래그 해제
        MainApplication.isAuthCheck = true;
    }

    /**
     * 접근권한 팝업에서 나중에 보기 버튼 클릭시 수행되는 콜백
     */
    @Override
    public void onAuthAfterShow() {
        showFirstPushApprovePopup();
        MainApplication.isAuthCheck = true;
    }

    /**
     * 인트로 등급 메시지 출력
     */
    public void displayIntroGradeMessage(String grade, String customName) {
        ThreadUtils.INSTANCE.runInUiThread(() -> {
            String fGrade = grade;
            String GRADE_BASIC = "BASIC";

            if (isEmpty(appIntroTxt)) {
                return;
            }

            // 일반 등급은 빈값으로 설정 된다.
            if ("".equalsIgnoreCase(fGrade)) {
                fGrade = GRADE_BASIC;
            }

            if (fGrade != null)
                fGrade = fGrade.toUpperCase();

            HashMap<String, IntroImageInfo.AppIntroTxt> mapIntro = new HashMap<>();
            for (IntroImageInfo.AppIntroTxt introTxt : appIntroTxt) {
                mapIntro.put(introTxt.grade, introTxt);
            }

            // 로그인 유저
            boolean isFounded = false;
            if (isNotEmpty(fGrade)) {
                view_into_grade.setVisibility(View.VISIBLE);
//            for (IntroImageInfo.AppIntroTxt introTxt : appIntroTxt) {
                IntroImageInfo.AppIntroTxt introTxt = mapIntro.get(fGrade);
                if (introTxt == null) {
                    introTxt = mapIntro.get(GRADE_BASIC);
                }

                if (introTxt != null) {
                    isFounded = true;
                    if (TextUtils.isEmpty(introTxt.mainTitle)) {
                        introTxt.mainTitle = "고객님";
                    }

                    if (TextUtils.isEmpty(introTxt.subTitle)) {
                        introTxt.subTitle = getString(R.string.intro_defaul_txt);
                    }

                    introNameText.setText(String.format("%s %s", customName, introTxt.mainTitle));
                    messageText.setText(introTxt.subTitle);

                    if (introTxt.grade.equalsIgnoreCase(GRADE_BASIC)) {
                        gradeText.setVisibility(View.GONE);
                    }
                    else {
                        gradeText.setVisibility(View.VISIBLE);
                        gradeText.setText(introTxt.grade);
                        if (!TextUtils.isEmpty(introTxt.fontColor))
                            gradeText.setTextColor(Color.parseColor("#" + introTxt.fontColor));
                    }
                    if (!TextUtils.isEmpty(introTxt.fontColor)) {
                        introNameText.setTextColor(Color.parseColor("#" + introTxt.fontColor));
                        messageText.setTextColor(Color.parseColor("#" + introTxt.fontColor));
                    }
                }
            }

            if(!isFounded) {
                IntroImageInfo.AppIntroTxt introTxt = mapIntro.get(GRADE_BASIC);

                view_into_grade.setVisibility(View.GONE);
                try {
                    if (TextUtils.isEmpty(introTxt.subTitle)) {
                        introTxt.subTitle = getString(R.string.intro_defaul_txt);
                    }
                    messageText.setText(introTxt.subTitle);
                    if (!TextUtils.isEmpty(introTxt.fontColor))
                        messageText.setTextColor(Color.parseColor("#" + introTxt.fontColor));
                }
                catch (NullPointerException e) {
                    Ln.e(e.getMessage());
                }
                catch (IllegalArgumentException e) {
                    Ln.e(e.getMessage());
                }
            }

            /**
             * 등급 문구를 읽을 수 있게 화면을 잠시 정지한다.
             */
            ViewUtils.showViews(roundLineView);
        });
    }

    /**
     * 접근권한 API 호출시 POST로 전송할 데이타 모델
     */
    @Model
    private static class AuthConfirmParam {
        /**
         * 기기 고유번호
         */
        public String gsuuid;

        /**
         * "01" : GS SHOP, "21":TV
         */
        public String appGB;

        /**
         * OS 버전
         */
        public String osVersion;

        /**
         * OS 구분("A":안드로이드, "I":아이폰)
         */
        public String osGB;

        /**
         * 버전네임 (예:"5.7.2")
         */
        public String appVersion;

        /**
         * 접근권한 고지 확인여부 ("Y" or "N")
         */
        public String confirmYN;
    }

    /**
     * 접근권한 API 호출결과 데이타 모델
     */
    @Model
    private static class AuthConfirmResult {
        /**
         * "S":성공 or "F":실패
         */
        public String result;
    }
}
