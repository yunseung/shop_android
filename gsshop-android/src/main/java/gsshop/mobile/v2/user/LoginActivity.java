/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.user;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ajalt.reprint.core.AuthenticationFailureReason;
import com.github.ajalt.reprint.core.AuthenticationListener;
import com.github.ajalt.reprint.core.Reprint;
import com.google.inject.Inject;
import com.gsshop.mocha.pattern.mvc.BaseAsyncController;
import com.gsshop.mocha.ui.util.ViewUtils;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.usermgmt.LoginButton;
import com.kakao.util.exception.KakaoException;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.data.OAuthErrorCode;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.Keys.NonMemberLoginType;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.flexible.FlexibleBannerFooterViewHolder;
import gsshop.mobile.v2.menu.BaseTabMenuActivity;
import gsshop.mobile.v2.menu.TabMenu;
import gsshop.mobile.v2.menu.navigation.NavigationManager;
import gsshop.mobile.v2.push.PushAction;
import gsshop.mobile.v2.search.SoftKeyboardDectectorView;
import gsshop.mobile.v2.setting.FingerPrintSettings;
import gsshop.mobile.v2.support.airbridge.ABAction;
import gsshop.mobile.v2.support.gtm.AMPAction;
import gsshop.mobile.v2.support.gtm.AMPEnum;
import gsshop.mobile.v2.support.gtm.datahub.DatahubAction;
import gsshop.mobile.v2.support.gtm.datahub.DatahubUrls;
import gsshop.mobile.v2.support.ui.ClearButtonTextWatcher;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog;
import gsshop.mobile.v2.support.ui.CustomTwoButtonDialog;
import gsshop.mobile.v2.support.ui.FingerPrintDialog;
import gsshop.mobile.v2.util.CustomAnimationDrawable;
import gsshop.mobile.v2.util.MaskingUtil;
import gsshop.mobile.v2.util.PrefRepositoryNamed;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isEmpty;
import static com.kakao.auth.Session.getCurrentSession;
import static gsshop.mobile.v2.R.id.edit_login_id;
import static gsshop.mobile.v2.R.string.user;
import static gsshop.mobile.v2.support.gtm.AMPEnum.AMP_CLICK_LOGIN_TYPE;
import static gsshop.mobile.v2.support.gtm.AMPEnum.AMP_CLICK_LOGIN_TYPE_KEY;
import static gsshop.mobile.v2.support.gtm.AMPEnum.AMP_CLICK_LOGIN_TYPE_VAL_ARS;
import static gsshop.mobile.v2.support.gtm.AMPEnum.AMP_CLICK_LOGIN_TYPE_VAL_FINGER_PRINT;
import static gsshop.mobile.v2.support.gtm.AMPEnum.AMP_CLICK_LOGIN_TYPE_VAL_KAKAO;
import static gsshop.mobile.v2.support.gtm.AMPEnum.AMP_CLICK_LOGIN_TYPE_VAL_NAVER;
import static gsshop.mobile.v2.support.gtm.AMPEnum.AMP_CLICK_LOGIN_TYPE_VAL_NORMAL;
import static gsshop.mobile.v2.support.gtm.AMPEnum.AMP_CLICK_LOGIN_TYPE_VAL_PHONE;

/**
 * 사용자 로그인 화면.
 */
@SuppressWarnings("unused")
public class LoginActivity extends BaseTabMenuActivity {

    /**
     * kakao simple login
     */
    private SessionCallback callback;

    /**
     * naver client 정보를 넣어준다.
     */
    private OAuthLogin mOAuthLoginInstance;

    @InjectView(R.id.btn_naver_sdk_login)
    OAuthLoginButton mOAuthLoginButton;

    @InjectView(R.id.btn_kakao_sdk_login)
    LoginButton mKakaoLoginButton;

    @Inject
    private Context mContext;

    @InjectView(edit_login_id)
    private AutoCompleteTextView editLoginId;

    @InjectView(R.id.edit_password)
    private EditText editLoginPassword;

//    @InjectView(R.id.text_password)
//    private TextView text_password;

    @InjectView(R.id.txt_email_cert)
    private TextView mTvEmailCert;

    @InjectView(R.id.checkbox_keep_login)
    private CheckBox checkBoxKeepLoginId;

    @InjectView(R.id.btn_delete_login_id)
    private ImageButton btnClearLoginId;

    @InjectView(R.id.btn_delete_password)
    private ImageButton btnClearPassword;

//    @InjectView(R.id.btn_show_password)
//    private View btn_show_password;

    @InjectView(R.id.tv_nonmember_guide)
    private View mTvNonmemberGuide;

    @InjectView(R.id.ll_non_member_order)
    private LinearLayout LlNonMemberOrder;

    @InjectView(R.id.keyboard_button)
    private ViewGroup keyboardButton;

    @InjectView(R.id.btn_login)
    private TextView btnLogin;

    @InjectView(R.id.text_id_error_message)
    private TextView mTextIdErrorMessage;
    @InjectView(R.id.ll_id_error_area)
    private LinearLayout mLlIdErrorArea;

    @InjectView(R.id.text_password_error_message)
    private TextView mTextPasswordErrorMessage;
    @InjectView(R.id.ll_password_error_area)
    private LinearLayout mLlPasswordErrorArea;

    @Inject
    private UserAction userAction;

    @Inject
    private PushAction pushAction;

    @InjectResource(R.string.web_uri_utf_8)
    private final String uriEncoding = "utf-8";

    private int nonMemberLoginType;

    private String targetUrl;

    private String targetMsg;

    @InjectView(R.id.layout_tab_menu)
    private View layout_tab_menu;

    @InjectView(R.id.footer_frame)
    private ViewGroup footerFrame;
    private BaseViewHolder footerViewHolder;

    private int footerHeight;

    private int header_height;

    @InjectView(R.id.scrollview)
    private ScrollView scrollview;

    @InjectView(R.id.easy_login_layout)
    private View easyLoginLayout;

    @InjectView(R.id.keyboard_image)
    private ImageView keyboardImage;

    @InjectView(R.id.id_line_hide)
    private View id_line_hide;
    @InjectView(R.id.id_line_hide_right)
    private View id_line_hide_right;
    @InjectView(R.id.id_line_hide_left)
    private View id_line_hide_left;


    /**
     * SNS로그인 결과 팝업
     */
    private Dialog dialogAuth;
    private Dialog dialogMapping;

    private FingerPrintDialog fingerPrintDialog;
    private boolean isFingerprintEnable;

    @InjectView(R.id.root)
    private ViewGroup root;

    @InjectView(R.id.transition_image)
    private ImageView transition_image;
    private int loginErrorCount;

    /**
     * 로그인 종류
     */
    public enum LOGIN_TYPE {
        TYPE_LOGIN,  //GS로그인 수행시
        TYPE_OAUTH,  //SNS로그인->GS로그인 수행시
        TYPE_MAPPING //SNS로그인->회원가입->GS로그인 수행시
    }

    /**
     * SNS 종류
     */
    public enum SNS_TYPE {
        KA,   //카카오
        NA,   //네이버
        GA    //구글(현재 사용안함)
    }

    /**
     * SNS_TYPE 변수
     */
    private static SNS_TYPE snsType;

    private boolean isFingerprint;

    private boolean isKeyboardShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        snsType = null;

        setHeaderView(R.string.login_title);

        footerHeight = getResources().getDimensionPixelSize(R.dimen.login_footer_height);
        header_height = getResources().getDimensionPixelSize(R.dimen.header_height) * 2;
        targetUrl = getIntent().getStringExtra(Keys.INTENT.WEB_URL);
        targetMsg = getIntent().getStringExtra(Keys.INTENT.WEB_TARGET_MSG);
        nonMemberLoginType = getIntent().getIntExtra(Keys.INTENT.NON_MEMBER_LOGIN_TYPE,
                NonMemberLoginType.NORMAL);
        loginErrorCount = 0;
        setupLoginFields();
        EventBus.getDefault().register(pushAction);

        //GTM Datahub 이벤트 전달
        DatahubAction.sendDataToDatahub(this, DatahubUrls.D_1038, "");

        // 인증메일 받기 TEXT 부분 spannable 처리.
        Spannable spannable = new SpannableString(mTvEmailCert.getText().toString());
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#444444")), spannable.length() - 7, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTvEmailCert.setText(spannable);

        // footer 설정
        // 회사정보 풋터.
        View itemView = LayoutInflater.from(this).inflate(R.layout.home_bottom, null);

        footerViewHolder = new FlexibleBannerFooterViewHolder(itemView);
        footerViewHolder.onBindViewHolder(this, 0, null, null, null, null);
        footerFrame.addView(itemView);
        //소프트키보드 동작상태를 알기위해 추가
        final SoftKeyboardDectectorView softKeyboardDecector = new SoftKeyboardDectectorView(this);
        addContentView(softKeyboardDecector, new FrameLayout.LayoutParams(-1, -1));

        softKeyboardDecector.setOnShownKeyboard(new SoftKeyboardDectectorView.OnShownKeyboardListener() {

            @Override
            public void onShowSoftKeyboard() {
                imageKeyboardShow();
            }
        });

        //키보드가 화면에서 내려갔을때
        softKeyboardDecector.setOnHiddenKeyboard(new SoftKeyboardDectectorView.OnHiddenKeyboardListener() {

            @Override
            public void onHiddenSoftKeyboard() {
                imageKeyboardHide();
            }
        });

        keyboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollview.scrollTo(0, 0);
                if (keyboardImage.getVisibility() == View.VISIBLE) {
                    keyboardImage.setVisibility(View.GONE);
                    easyLoginLayout.setVisibility(View.VISIBLE);
                    transition_image.setBackgroundResource(R.drawable.keyboard_off_an);
                    keyboardButton.setContentDescription("키보드 이미지로 보기");
                } else {
                    keyboardImage.setVisibility(View.VISIBLE);
                    easyLoginLayout.setVisibility(View.GONE);
                    transition_image.setBackgroundResource(R.drawable.keyboard_on_an);
                    keyboardButton.setContentDescription("키보드 이미지로 보기 숨기기");
                }
            }
        });

        // 클릭리스너 등록
        setClickListener();

        //지문인식 매핑 된경우
        if (FingerPrintSettings.get().isFingerprintMapping) {
            fingerprintLoginCheck();
        }

        //앰플리듀드 로그인 페이지 진입 ( 페이지 뷰 )
        try {
            AMPAction.sendAmpEvent(AMPEnum.AMP_VIEW_LOGIN);
        } catch (Exception e) {
            Ln.e(e);
        }

        Toast.makeText(this, R.string.login_info, Toast.LENGTH_SHORT).show();

        //와이즈로그 호출
        setWiseLogHttpClient(ServerUrls.WEB.LOGIN_SHOW);
    }

    /**
     * 뷰의 클릭리스너를 세팅한다.
     */
    private void setClickListener() {
        findViewById(R.id.btn_ars_members_login).setOnClickListener((View v) -> {
                    tvMembersLogin();
                }
        );
        findViewById(R.id.btn_kakao_login).setOnClickListener((View v) -> {
                    kakaoLoginButton();
                }
        );
        findViewById(R.id.btn_naver_login).setOnClickListener((View v) -> {
                    naverLoginButton();
                }
        );
        findViewById(R.id.fingerprint_login).setOnClickListener((View v) -> {
                    fingerprintLoginCheck();
                }
        );
        findViewById(R.id.notice).setOnClickListener((View v) -> {
                    notice();
                }
        );
        findViewById(R.id.customer_service).setOnClickListener((View v) -> {
                    customerService();
                }
        );
        findViewById(R.id.setting).setOnClickListener((View v) -> {
                    setting();
                }
        );
        findViewById(R.id.txt_keep_login_id).setOnClickListener((View v) -> {
                    keepLoginIdCheck();
                }
        );
        findViewById(R.id.btn_login).setOnClickListener((View v) -> {
                    login();
                }
        );
        findViewById(R.id.tv_join_member).setOnClickListener((View v) -> {
                    joinMember();
                }
        );
        findViewById(R.id.txt_email_cert).setOnClickListener((View v) -> {
                    emailCert();
                }
        );
        findViewById(R.id.btn_delete_login_id).setOnClickListener((View v) -> {
                    clearLoginId();
                }
        );
        findViewById(R.id.btn_delete_password).setOnClickListener((View v) -> {
                    clearPassword();
                }
        );
        findViewById(R.id.btn_non_member_order_query).setOnClickListener((View v) -> {
                    orderQuery();
                }
        );
        findViewById(R.id.ll_non_member_order).setOnClickListener((View v) -> {
                    orderPage();
                }
        );
        findViewById(R.id.txt_find_id).setOnClickListener((View v) -> {
                    findLoginId();
                }
        );
        findViewById(R.id.txt_find_pw).setOnClickListener((View v) -> {
                    findPassword();
                }
        );
        findViewById(R.id.btn_phone_login).setOnClickListener((View v) -> {
                    loginPhone();
                }
        );
    }

    private void imageKeyboardShow() {
        scrollview.setPadding(0, 0, 0, 0);
        layout_tab_menu.setVisibility(View.GONE);
        keyboardButton.setVisibility(View.VISIBLE);

        CustomAnimationDrawable cad = new CustomAnimationDrawable(
                (AnimationDrawable) getResources().getDrawable(
                        R.drawable.login_keyboard_animation)) {
            @Override
            public void onAnimationStart() {
            }

            @Override
            public void onAnimationFinish() {
                if (keyboardImage.getVisibility() == View.VISIBLE) {
                    transition_image.setBackgroundResource(R.drawable.keyboard_on_an);
                } else {
                    transition_image.setBackgroundResource(R.drawable.keyboard_off_an);
                }
            }
        };

        transition_image.setBackgroundDrawable(cad);

        cad.setOneShot(true);
        cad.start();

    }


    private void imageKeyboardHide() {
        scrollview.setPadding(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.tab_menu_height));
        layout_tab_menu.setVisibility(View.VISIBLE);
        keyboardImage.setVisibility(View.GONE);
        easyLoginLayout.setVisibility(View.VISIBLE);
        if (keyboardButton.getVisibility() == View.INVISIBLE) {
            return;
        }
        keyboardButton.setVisibility(View.INVISIBLE);
        transition_image.setBackgroundResource(R.drawable.login_keyboard_animation);
        final AnimationDrawable drawable =
                (AnimationDrawable) transition_image.getBackground();

        drawable.stop();
    }

    public void initKakao() {
        callback = new SessionCallback();
        // removes kakao's login keys
        if (Session.getCurrentSession().isOpened()) {
            Session.getCurrentSession().close();
        }
        getCurrentSession().addCallback(callback);
        Session.getCurrentSession().checkAndImplicitOpen();
    }

    /**
     * naver simple login
     */
    public void initNaverLogin() {
        mOAuthLoginInstance = OAuthLogin.getInstance();
        // removes naver's login keys
//        mOAuthLoginInstance.logout(context);
        mOAuthLoginButton.setOAuthLoginHandler(mOAuthLoginHandler);
    }

    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            String accessToken = getCurrentSession().getAccessToken();
            String refreshToken = getCurrentSession().getRefreshToken();
            StringBuilder builder = new StringBuilder();
            builder.append(accessToken);
            builder.append("\n");
            builder.append(refreshToken);
            Ln.i("access token: [" + accessToken + "]");
            Ln.i("refresh token [" + refreshToken + "]");

            SimpleCredentials model = new SimpleCredentials();
            model.snsAccess = accessToken;
            model.snsRefresh = refreshToken;
            model.snsTyp = SNS_TYPE.KA.toString();
            model.loginTyp = LOGIN_TYPE.TYPE_OAUTH.toString();
            snsType = SNS_TYPE.KA;
            loginWithSns(model);
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            // ignore hardware back key & link deny button
            if (exception != null && exception.getErrorType() != KakaoException.ErrorType.CANCELED_OPERATION) {
                Ln.e(exception);
            }
        }
    }


    //지문인식 리스너
    private AuthenticationListener authenticationListener = new AuthenticationListener() {
        @Override
        public void onSuccess(int i) {
            fingerPrintDialog.dismiss();
            //남아있는 로그인세션 정보 모두 제거
            userAction.invalidateUserSession();

            LoginOption option = LoginOption.get();
            if (option != null) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fingerprintLogin();
                    }
                }, 100);

            }
        }

        @Override
        public void onFailure(AuthenticationFailureReason authenticationFailureReason, boolean b, CharSequence charSequence, int i, int i1) {
            if (authenticationFailureReason == AuthenticationFailureReason.LOCKED_OUT) {
                fingerPrintDialog.title(R.string.fp_locked);
                fingerPrintDialog.buttonLabel(R.string.common_ok);
                fingerPrintDialog.setLoginmessage(getString(R.string.fp_fail_count_over));
            } else {
                fingerPrintDialog.title(R.string.common_retry);
            }
        }
    };


    /**
     * startOAuthLoginActivity() 호출시 인자로 넘기거나, OAuthLoginButton 에 등록해주면 인증이 종료되는 걸 알 수 있다.
     */
    private OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
        @Override
        public void run(boolean success) {
            if (success) {
                String accessToken = mOAuthLoginInstance.getAccessToken(mContext);
                String refreshToken = mOAuthLoginInstance.getRefreshToken(mContext);
                long expiresAt = mOAuthLoginInstance.getExpiresAt(mContext);
                String tokenType = mOAuthLoginInstance.getTokenType(mContext);
                StringBuilder builder = new StringBuilder();
                builder.append(accessToken).append("\n").append(refreshToken);

                SimpleCredentials model = new SimpleCredentials();
                model.snsAccess = accessToken;
                model.snsRefresh = refreshToken;
                model.snsTyp = SNS_TYPE.NA.toString();
                model.loginTyp = LOGIN_TYPE.TYPE_OAUTH.toString();
                snsType = SNS_TYPE.NA;
                loginWithSns(model);

            } else {
                OAuthErrorCode authError = mOAuthLoginInstance.getLastErrorCode(mContext);
                Ln.i("authError: " + authError);
                // ignore hardware back button & accept cancel button
                if (authError != OAuthErrorCode.CLIENT_USER_CANCEL
                        && authError != OAuthErrorCode.SERVER_ERROR_ACCESS_DENIED) {
                    String errorCode = mOAuthLoginInstance.getLastErrorCode(mContext).getCode();
                    String errorDesc = mOAuthLoginInstance.getLastErrorDesc(mContext);
                    Toast.makeText(mContext, "errorCode:" + errorCode + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    /**
     * tv 회원 로그인
     */
    public void tvMembersLogin() {
        setWiseLogHttpClient(ServerUrls.WEB.TV_MEMBERS_LOGIN);

        //앰플리튜드
        try {
            JSONObject temp = new JSONObject();
            temp.put(AMP_CLICK_LOGIN_TYPE_KEY, AMP_CLICK_LOGIN_TYPE_VAL_ARS);
            AMPAction.sendAmpEventProperties(AMP_CLICK_LOGIN_TYPE, temp);
        } catch (Exception e) {
            //무시 하도록
        }

        goWeb(ServerUrls.WEB.URL_TV_MEMBERS_LOGIN, targetUrl);
    }

    public void kakaoLoginButton() {
        //SNS로그인은 LOLLIPOP 부터 가능
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            showOldVerMsg();
            return;
        }

        if (Session.getCurrentSession().isOpened()) {
            Session.getCurrentSession().close();
        }

        /**
         * kakao 웹뷰에서 간편로그인 html을 그러주지 못하는 kakao 자체 버그로 먼저 naver 웹뷰를 실행하고 그 후에 kakao 웹뷰를 실행한다
         */
        new BaseAsyncController<Void>(mContext) {
            @Override
            protected void onPrepare(Object... params) throws Exception {
                super.onPrepare(params);
                context.startActivity(new Intent(context, DummyAppBrowserActivity.class));
            }

            @Override
            protected Void process() throws Exception {
                TimeUnit.MILLISECONDS.sleep(400);
                return null;
            }

            @Override
            protected void onSuccess(Void aVoid) throws Exception {
                mKakaoLoginButton.performClick();
                super.onSuccess(aVoid);
            }
        }.execute();

        //와이즈로그 호출
        setWiseLogHttpClient(ServerUrls.WEB.SNS_KA_LOGIN);

        //앰플리튜드
        try {
            JSONObject temp = new JSONObject();
            temp.put(AMP_CLICK_LOGIN_TYPE_KEY, AMP_CLICK_LOGIN_TYPE_VAL_KAKAO);
            AMPAction.sendAmpEventProperties(AMP_CLICK_LOGIN_TYPE, temp);
        } catch (Exception e) {
            //무시 하도록
        }
    }

    public void naverLoginButton() {
        //SNS로그인은 LOLLIPOP 부터 가능
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            showOldVerMsg();
            return;
        }

        mOAuthLoginButton.performClick();

        //와이즈로그 호출
        setWiseLogHttpClient(ServerUrls.WEB.SNS_NA_LOGIN);

        //앰플리튜드
        try {
            JSONObject temp = new JSONObject();
            temp.put(AMP_CLICK_LOGIN_TYPE_KEY, AMP_CLICK_LOGIN_TYPE_VAL_NAVER);
            AMPAction.sendAmpEventProperties(AMP_CLICK_LOGIN_TYPE, temp);
        } catch (Exception e) {
            //무시 하도록
        }
    }

    public void fingerprintLoginCheck() {
        //와이즈로그 호출
        setWiseLogHttpClient(ServerUrls.WEB.FINGERPRINT_LOGIN);

        //앰플리튜드
        try {
            JSONObject temp = new JSONObject();
            temp.put(AMP_CLICK_LOGIN_TYPE_KEY, AMP_CLICK_LOGIN_TYPE_VAL_FINGER_PRINT);
            AMPAction.sendAmpEventProperties(AMP_CLICK_LOGIN_TYPE, temp);
        } catch (Exception e) {
            //무시 하도록
        }

        //지문로그인 관련
        Reprint.initialize(getApplicationContext());
        //휴대폰에 지문인식 기능이 없으면 지문로그인 불가 팝업 노출
        //띄울때 햇더니 중복 된다 그래서 없어질때 넣었다.
        if (!Reprint.isHardwarePresent()) {
            new CustomOneButtonDialog(this).message(R.string.fp_unregistered).buttonClick(new CustomOneButtonDialog.ButtonClickListener() {
                @Override
                public void onClick(Dialog dialog) {
                    setWiseLogHttpClient(ServerUrls.WEB.FINGERPRINT_UNHARDWARD);
                    dialog.dismiss();
                }
            }).cancelled(new CustomOneButtonDialog.CancelListener() {
                @Override
                public void onCancel(Dialog dialog) {
                    setWiseLogHttpClient(ServerUrls.WEB.FINGERPRINT_UNHARDWARD);
                }
            }).show();
            return;
        }

        //휴대폰에 저장된 지문이 없다면
        //띄울때 햇더니 중복 된다 그래서 없어질때 넣었다.
        if (!Reprint.hasFingerprintRegistered()) {
            new CustomOneButtonDialog((Activity) mContext).message(R.string.fp_unsetting).buttonClick(new CustomOneButtonDialog.ButtonClickListener() {
                @Override
                public void onClick(Dialog dialog) {
                    setWiseLogHttpClient(ServerUrls.WEB.FINGERPRINT_UNREGISTER);
                    dialog.dismiss();
                }
            }).cancelled(new CustomOneButtonDialog.CancelListener() {
                @Override
                public void onCancel(Dialog dialog) {
                    setWiseLogHttpClient(ServerUrls.WEB.FINGERPRINT_UNREGISTER);
                }
            }).show();

            /*
            new FingerPrintDialog(this).setNotSupportmessage().buttonLabel(R.string.common_ok).buttonClick(new FingerPrintDialog.ButtonClickListener() {
                @Override
                public void onClick(Dialog dialog) {
                    dialog.dismiss();
                }
            }).show();
            */
            return;
        }

        FingerPrintSettings fingerPrintSettings = FingerPrintSettings.get();
        if (!fingerPrintSettings.isFingerprintMapping) {

            new CustomTwoButtonDialog(this).message(R.string.fp_need_login).positiveButtonLabel(R.string.fp_go_login).negativeButtonLabel(R.string.fp_next_login)
                    .positiveButtonClick(new CustomOneButtonDialog.ButtonClickListener() {
                        @Override
                        public void onClick(Dialog dialog) {
                            dialog.dismiss();
                            showKeyboard();
                            isFingerprint = true;
                            setWiseLogHttpClient(ServerUrls.WEB.FINGERPRINT_LOGIN_CLICK);
                        }
                    }).negativeButtonClick(CustomOneButtonDialog.DISMISS).show();
        } else {

            TokenCredentialsNew2 tokenCredentials = TokenCredentialsNew2.get();

            if (tokenCredentials != null && tokenCredentials.authToken != null) {
                String userName = "";
                if (fingerPrintSettings.loginId != null && !"".equals(fingerPrintSettings.loginId)) {
                    userName = MaskingUtil.maskLoginId(fingerPrintSettings.loginId) + context.getString(user) + ",\r\n";
                }

                fingerPrintDialog = new FingerPrintDialog(this);

                fingerPrintDialog.setLoginmessage(userName + getString(R.string.fp_on_sensor))
                        .buttonClick(new FingerPrintDialog.ButtonClickListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                dialog.dismiss();
                            }
                        }).show();

                fingerPrintDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Reprint.cancelAuthentication();
                    }
                });

                Reprint.authenticate(authenticationListener);
            } else {
                new CustomTwoButtonDialog(this).message(R.string.fp_need_login).positiveButtonLabel(R.string.fp_go_login).negativeButtonLabel(R.string.fp_next_login)
                        .positiveButtonClick(new CustomOneButtonDialog.ButtonClickListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                dialog.dismiss();
                                showKeyboard();
                                setWiseLogHttpClient(ServerUrls.WEB.FINGERPRINT_LOGIN_CLICK);
                                isFingerprint = true;
                            }
                        }).negativeButtonClick(CustomOneButtonDialog.DISMISS).show();
            }
        }
    }

    public void notice() {
        goWeb(ServerUrls.WEB.HOME_FOOTER_NOTICE);
    }

    public void customerService() {
        goWeb(ServerUrls.WEB.HOME_FOOTER_CUSTOMER_SERVICE);
    }

    public void setting() {
        goSetting();
    }


    /**
     * 메시지 팝업을 노출한다.
     */
    private void showOldVerMsg() {
        new CustomOneButtonDialog(this).message(R.string.sns_login_version_old)
                .cancelable(false)
                .buttonClick(new CustomOneButtonDialog.ButtonClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                    }
                }).show();
    }


    @Override
    protected void onResume() {
        super.onResume();

        //SNS 로그인 콜백 초기화(콜백등록)
        initKakao();
        initNaverLogin();

        //지문인식 다이얼로그가 떠있는 상태이면 지문 리스너 시작
        if (isFingerprintEnable && fingerPrintDialog != null && fingerPrintDialog.isShowing()) {
            Reprint.initialize(getApplicationContext());
            Reprint.authenticate(authenticationListener);
            isFingerprintEnable = false;
        }
    }

    /**
     * 아이디 입력창에 포커스를 위치시키고 키보드를 노출한다.
     */
    private void showKeyboard() {
        editLoginId.requestFocus();
        editLoginId.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editLoginId, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 100);
    }

    @Override
    protected void onPause() {
        //SNS 로그인 콜백 제거
        getCurrentSession().removeCallback(callback);
        mOAuthLoginButton.setOAuthLoginHandler(null);

        Reprint.cancelAuthentication();
        isFingerprintEnable = true;

        super.onPause();
    }

    @Override
    protected void onDestroy() {

        // '아이디저장' 해제시 로그인정보 모두 제거.
        // (로그인을 시도하지 않고 저장된 아이디를 지우고 싶은 경우)
        if (checkBoxKeepLoginId != null && checkBoxKeepLoginId.isChecked() == false && !FingerPrintSettings.get().isFingerprintMapping) {
            TokenCredentialsNew2.remove();
        }

        EventBus.getDefault().unregister(pushAction);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 20140212 parksegun EC통합 재구축
     * 보안 문제에 따른 간편로그인 제거
     */
    private void setupLoginFields() {

        ArrayList<String> domain = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.email_domain)));
        DomainFilterAdapter adapter = new DomainFilterAdapter(this, R.layout.login_email_dropdown_item, domain);
        editLoginId.setAdapter(adapter);
        editLoginId.addTextChangedListener(new ClearButtonTextWatcher(btnClearLoginId));
        editLoginPassword.addTextChangedListener(new ClearButtonTextWatcher(btnClearPassword));
        checkBoxKeepLoginId.setChecked(true);
        checkBoxKeepLoginId.setEnabled(true);

        editLoginId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editLoginId.getText().length() > 0 && editLoginPassword.getText().length() > 0) {
                    btnLogin.setBackgroundResource(R.drawable.bg_login_focus_button);
                    try {
                        btnLogin.setTextColor(getResources().getColor(R.color.login_button_text_focus));
                    } catch (NoSuchMethodError e) {
                        Ln.e(e.toString());
                    }
                } else {
                    btnLogin.setBackgroundResource(R.drawable.bg_login_button);
                    try {
                        btnLogin.setTextColor(getResources().getColor(R.color.login_button_text_default));
                    } catch (NoSuchMethodError e) {
                        Ln.e(e.toString());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        editLoginPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editLoginId.getText().length() > 0 && editLoginPassword.getText().length() > 0) {
                    btnLogin.setBackgroundResource(R.drawable.bg_login_focus_button);
                    try {
                        btnLogin.setTextColor(getResources().getColor(R.color.login_button_text_focus));
                    } catch (NoSuchMethodError e) {
                        Ln.e(e.toString());
                    }
                } else {
                    btnLogin.setBackgroundResource(R.drawable.bg_login_button);
                    try {
                        btnLogin.setTextColor(getResources().getColor(R.color.login_button_text_default));
                    } catch (NoSuchMethodError e) {
                        Ln.e(e.toString());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        editLoginPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    id_line_hide.setVisibility(View.VISIBLE);
                    id_line_hide_right.setVisibility(View.VISIBLE);
                    id_line_hide_left.setVisibility(View.VISIBLE);
                } else {
                    id_line_hide.setVisibility(View.GONE);
                    id_line_hide_right.setVisibility(View.GONE);
                    id_line_hide_left.setVisibility(View.GONE);
                }
            }
        });

        if (targetMsg != null && !"".equals(targetMsg))
            editLoginId.setText(targetMsg);

        editLoginId.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mLlIdErrorArea.setVisibility(View.GONE);
                        if (editLoginId.getText().length() > 1 && editLoginId.getText().toString().contains("@")) {
                            if (!editLoginId.isPopupShowing()) {
                                editLoginId.showDropDown();
                            }
                        }
                }

                return false;
            }
        });

        editLoginPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mLlPasswordErrorArea.setVisibility(View.GONE);
                }
                return false;
            }
        });

        checkBoxKeepLoginId.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    //자동로그인 해제시 와이즈로그 호출
                    setWiseLogHttpClient(ServerUrls.WEB.KEEP_LOGIN_UNCHECK);
                }
            }
        });

        /**
         * 하단 문구 링크 걸기
         */
        // 하단 버튼 변경
        // NOTE : isAdultCheck 값이 isDirectOrderLogin 보다 더 우선순위 높음.
        if (nonMemberLoginType == NonMemberLoginType.ADULT_CHECK) {
            //성인인증 레이아웃 노출
//            ViewUtils.showViews(adult_layout);
        } else if (nonMemberLoginType == NonMemberLoginType.DIRECT_ORDER) {

            ViewUtils.showViews(mTvNonmemberGuide);
            ViewUtils.showViews(LlNonMemberOrder);
        } else {

        }

        editLoginId.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    editLoginPassword.requestFocus();
                }
                return false;
            }
        });

    }

    private void keepLoginIdCheck() {
        if (checkBoxKeepLoginId.isEnabled()) {
            checkBoxKeepLoginId.performClick();
        }
    }

    private void login() {
        final String loginId = editLoginId.getText().toString();
        String wordPass = editLoginPassword.getText().toString();

        // 입력값 검증 아이디
        if (TextUtils.isEmpty(loginId)) {
            mLlIdErrorArea.setVisibility(View.VISIBLE);
            mLlPasswordErrorArea.setVisibility(View.GONE);
            mTextIdErrorMessage.setText(R.string.login_invalid_id);
            mTextIdErrorMessage.requestFocus();
            return;
        }

        // 입력값 검증 패스워드
        if (TextUtils.isEmpty(wordPass)) {
            mLlPasswordErrorArea.setVisibility(View.VISIBLE);
            mLlIdErrorArea.setVisibility(View.GONE);
            mTextPasswordErrorMessage.setText(R.string.login_invalid_password);
            mTextPasswordErrorMessage.requestFocus();
            return;
        }


        //로그인버튼 클릭시 와이즈로그 호출
        setWiseLogHttpClient(ServerUrls.WEB.LOGIN_CLICK);

        //앰플리튜드
        try {
            JSONObject temp = new JSONObject();
            temp.put(AMP_CLICK_LOGIN_TYPE_KEY, AMP_CLICK_LOGIN_TYPE_VAL_NORMAL);
            AMPAction.sendAmpEventProperties(AMP_CLICK_LOGIN_TYPE, temp);
        } catch (Exception e) {
            //무시 하도록
        }

        SimpleCredentials model = new SimpleCredentials();
        model.loginId = loginId;
        model.password = wordPass;

        model.loginTyp = LOGIN_TYPE.TYPE_LOGIN.toString();

        //메모리에서 패스워드 정보 제거 11/14 변수명 변경
        wordPass = "";

        editLoginPassword.setText("");

        LoginOption option = new LoginOption();
        option.keepLogin = checkBoxKeepLoginId.isChecked();
        option.easyLogin = false;

        new LoginController(this).execute(model, option);
    }

    /**
     * SNS 로그인 수행
     *
     * @param model SimpleCredentials
     */
    private void loginWithSns(SimpleCredentials model) {
        LoginOption option = new LoginOption();
        option.keepLogin = checkBoxKeepLoginId.isChecked();
        option.easyLogin = false;

        new LoginController(this).execute(model, option);
    }

    private void joinMember() {
        goWeb(ServerUrls.WEB.JOIN_USER);
        //와이즈로그 호출
        setWiseLogHttpClient(ServerUrls.WEB.LOGIN_JOIN_USER);
    }

    private void emailCert() {
        goWeb(ServerUrls.WEB.EMAIL_CERT);
        //와이즈로그 호출
        setWiseLogHttpClient(ServerUrls.WEB.LOGIN_EMAIL_CERT);
    }

    private void clearLoginId() {
        editLoginId.getText().clear();
    }

    private void clearPassword() {
        editLoginPassword.getText().clear();
    }

    private void orderQuery() {
        String encodedReturnUrl = "";
        if (!TextUtils.isEmpty(targetUrl)) {
            encodedReturnUrl = encodingQuery(targetUrl);
        }
        goWeb(ServerUrls.WEB.NON_MEMBER_SHIPPING + "&returnurl=" + encodedReturnUrl);
        //와이즈로그 호출
        setWiseLogHttpClient(ServerUrls.WEB.LOGIN_NO_MEM_ORDER);
    }

    private void orderPage() {
        String encodedReturnUrl = "";
        if (!TextUtils.isEmpty(targetUrl)) {
            encodedReturnUrl = encodingQuery(targetUrl);
        }
        goWeb(ServerUrls.WEB.NON_MEMBER_ORDER + "&returnurl=" + encodedReturnUrl);
        //와이즈로그 호출 어떻게 할까?
        setWiseLogHttpClient(ServerUrls.WEB.LOGIN_NO_MEM_ORDER);
    }


    private void loginFailedDialog(String message, String url, String type) {
        if ("30".equals(type) || "31".equals(type)) {
            ++loginErrorCount;
            if (loginErrorCount >= 3) {
                message = "아이디 또는 비밀번호를 다시 확인하세요.";
                loginErrorCount = 0;
                Dialog dialog = new CustomOneButtonDialog(this).message("로그인이 계속 실패하네요.\n아이디/비밀번호 찾기를\n사용해보세요~").moveTypeUrl(this,
                        url, type);
                dialog.show();
            }
            mLlPasswordErrorArea.setVisibility(View.VISIBLE);
            mLlIdErrorArea.setVisibility(View.GONE);
            mTextPasswordErrorMessage.setText(message);
            mTextPasswordErrorMessage.requestFocus();
            return;
        }


        Dialog dialog = new CustomOneButtonDialog(this).message(message).moveTypeUrl(this,
                url, type);
        dialog.show();
    }

    /**
     * 로그인결과 데이타를 뷰에 매핑시킨다.
     *
     * @param resourceId    리소스파일 아이디
     * @param retOauthState 로그인결과 데이타
     * @return
     */
    private View setSnsLoginResultData(int resourceId, RetOauthState retOauthState) {
        View view = LayoutInflater.from(mContext).inflate(resourceId, null);
        TextView txtTitle = (TextView) view.findViewById(R.id.txt_title);
        TextView txtTop = (TextView) view.findViewById(R.id.txt_top);
        TextView txtEmail = (TextView) view.findViewById(R.id.txt_email);
        TextView txtBottom = (TextView) view.findViewById(R.id.txt_bottom);
        TextView txtGroupMsg = (TextView) view.findViewById(R.id.txt_groupmsg);

        txtTitle.setText(retOauthState.title);
        txtTop.setText(retOauthState.msgTop);
        if (txtEmail != null) {
            txtEmail.setText(retOauthState.email);
            txtEmail.setPaintFlags(txtEmail.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }
        txtBottom.setText(retOauthState.msgBottom);
        if (txtGroupMsg != null) {
            txtGroupMsg.setText(retOauthState.groundMsg);
        }
        return view;
    }

    /**
     * 회원가입이력(TYPE_EQUAL), 이메일ID 통합이력(TYPE_MAPPING)이 있는 경우에 팝업을 띄운다.
     *
     * @param retOauthState 로그인결과 데이타
     */
    private void snsLoginMappingDialog(final RetOauthState retOauthState) {
        View view = setSnsLoginResultData(R.layout.confirm_sns_login_type_mapping, retOauthState);

        //msgTop값이 없는 경우 msgTop 영역을 비노출한다.
        if (TextUtils.isEmpty(retOauthState.msgTop)) {
            ((TextView) view.findViewById(R.id.txt_top)).setVisibility(View.GONE);
        }

        dialogMapping = makeSnsAlertDialog(mContext, view, getResources().getString(R.string.sns_login_btn_password_find),
                getResources().getString(R.string.sns_login_btn_login), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goWeb(retOauthState.searchUrl);
                        dialogMapping.dismiss();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogMapping.dismiss();
                        showKeyboard();
                        //와이즈로그 호출
                        setWiseLogHttpClient(ServerUrls.WEB.SNS_NA_POPUP_LOGIN);
                    }
                });
        dialogMapping.show();
    }

    /**
     * 계정인증완료(TYPE_OAUTH) 팝업을 띄운다.
     *
     * @param retOauthState 로그인결과 데이타
     * @param snsTyp        SNS타입("NA" or "KA")
     */
    private void snsLoginOAuthDialog(final RetOauthState retOauthState, final String snsTyp) {
        View view = setSnsLoginResultData(R.layout.confirm_sns_login_type_oauth, retOauthState);
        dialogAuth = makeSnsAlertDialog(mContext, view, getResources().getString(R.string.sns_login_btn_login),
                getResources().getString(R.string.login_join), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogAuth.dismiss();
                        showKeyboard();
                        //와이즈로그 호출
                        if (SNS_TYPE.KA.toString().equals(snsTyp)) {
                            setWiseLogHttpClient(ServerUrls.WEB.SNS_KA_POPUP_LOGIN);
                        } else if (SNS_TYPE.NA.toString().equals(snsTyp)) {
                            setWiseLogHttpClient(ServerUrls.WEB.SNS_NA_POPUP_LOGIN);
                        }
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goWeb(retOauthState.joinUrl);
                        dialogAuth.dismiss();
                    }
                });
        dialogAuth.show();
    }

    /**
     * 문의(TYPE_TEL, TYPE_ERROR) 팝업을 띄운다.
     *
     * @param retOauthState 로그인결과 데이타
     */
    private void snsLoginTelDialog(RetOauthState retOauthState) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.alert_sns_login, null);
        Dialog dialog = makeSnsConfirmDialog(mContext, retOauthState.msgTop);
        dialog.show();
    }

    /**
     * RetOauthState객체가 NULL인 경우 팝업을 띄운다.
     */
    private void snsLoginTelDialog() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.alert_sns_login, null);
        Dialog dialog = makeSnsConfirmDialog(mContext, getResources().getString(R.string.sns_login_error));
        dialog.show();
    }

    private class LoginController extends BaseAsyncController<UserConnector.LoginResult> {
        private SimpleCredentials model;
        private LoginOption option;
        private final Context context;

        public LoginController(Context context) {
            super(context);
            this.context = context;
        }

        @Override
        protected void onPrepare(Object... params) throws Exception {
            //super.onPrepare(params);

            imageKeyboardHide();
            if (this.dialog != null) {
                //this.dialog.dismiss();
                this.dialog.setCancelable(false);
                this.dialog.show();
            }

            model = (SimpleCredentials) params[0];
            option = (LoginOption) params[1];
        }

        @Override
        protected UserConnector.LoginResult process() throws Exception {
            return userAction.loginNew(model, option);
        }

        @Override
        protected void onSuccess(UserConnector.LoginResult result) throws Exception {
            if (result.isSuccs()) {
                if (isFingerprint) {
                    //지문인증 최초 로그인
                    FingerPrintSettings fingerPrintSettings = FingerPrintSettings.get();
                    fingerPrintSettings.isFingerprintMapping = true;
                    fingerPrintSettings.loginId = result.getLoginId();
                    fingerPrintSettings.save();
                    setWiseLogHttpClient(ServerUrls.WEB.FINGERPRINT_MAPPING);
                    Toast.makeText(mContext, "지문로그인이 연동 되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    //일반로그인식 지문인식 매핑 해제
                    FingerPrintSettings fingerPrintSettings = FingerPrintSettings.get();
                    fingerPrintSettings.isFingerprintMapping = false;
                    fingerPrintSettings.loginId = "";
                    fingerPrintSettings.save();
                }
                goTargetUrl(targetUrl);
                //로그인 성공시 표시할 메시지가 있으면 토스트로 띄운다.
                if (!TextUtils.isEmpty(result.getSuccMsg())) {
                    EventBus.getDefault().post(new Events.LoggedInToastEvent(result.getSuccMsg()));
                }

                boolean isCheckFPPopupShow = PrefRepositoryNamed.getBoolean(
                        LoginActivity.this,
                        Keys.PREF.PREF_IS_SHOW_CHECK_FP,
                        Keys.PREF.PREF_IS_SHOW_CHECK_FP, true);
                FingerPrintSettings fingerPrintSettings = FingerPrintSettings.get();

                // ARS 가입 비밀번호 변경이 필요한 경우
                if ("Y".equalsIgnoreCase(result.getPasswdTvNeedChgYn())) {
                    EventBus.getDefault().post(new Events.LoggedInUpdatePassEvent(result.getpasswdTvChgUrl(), true));
                }
                //비밀번호 변경이 필요한 경우
                // 20181017 ARS 가입 비밀번호 변경 확인하면 기존 비밀번호 변경 확인 로직은 확인하지 않음 - hklm -
                else if ("Y".equalsIgnoreCase(result.getPasswdNeedChgYn())) {
                    EventBus.getDefault().post(new Events.LoggedInUpdatePassEvent(result.getPasswdChgUrl(), false));
                }
                //로그인 성공시 최초 한번 지문 로그인 연동 확인 (ID /PW 입력 로그인의 경우) 180일 비밀번호 변경 팝업 출력시 미출력
                else if (isCheckFPPopupShow && TextUtils.isEmpty(result.getSnsTyp())
                        && Reprint.isHardwarePresent() && Reprint.hasFingerprintRegistered() // 하드웨어랑 지문 인식 지원하고
                        && !fingerPrintSettings.isFingerprintMapping) {       // 현재 매핑 상태가 아니고

                    // EventBus를 사용하는 이유가 있었음... 팝업을 해당 Activity에서 띄울수가 없음.
                    EventBus.getDefault().post(new Events.PopupFingerPrintEvent(true));
                }

                //airbridge
                ABAction.measureABSignIn(result.getCustNo());

            } else {
                //SNS로그인 관련 신규 추가된 에러코드
                if ("40".equals(result.getRetTyp())) {
                    if (result.getRetOauthState() == null
                            || TextUtils.isEmpty(result.getRetOauthState().loginUrl)) {
                        snsLoginTelDialog();
                        return;
                    }

                    //SNS로그인 후 에러코드 회신할 경우 웹에서 처리
                    //MSLEE 타켓 URL이 존재하는경우
                    String encodedReturnUrl = "";
                    if (!TextUtils.isEmpty(targetUrl)) {
                        encodedReturnUrl = encodingQuery(targetUrl);
                    }
                    goWeb(result.getRetOauthState().loginUrl + "&returnurl=" + encodedReturnUrl);

                    /*RetOauthState.LOGIN_RESULT_TYPE type = RetOauthState.LOGIN_RESULT_TYPE.valueOf(result.getRetOauthState().type);
                    switch (type) {
                        case TYPE_EQUAL:
                        case TYPE_MAPPING:
                            snsLoginMappingDialog(result.getRetOauthState());
                            break;
                        case TYPE_OAUTH:
                            snsLoginOAuthDialog(result.getRetOauthState(), result.getSnsTyp());
                            break;
                        case TYPE_TEL:
                        case TYPE_ERROR:
                            snsLoginTelDialog(result.getRetOauthState());
                            break;
                        default:
                            snsLoginTelDialog(result.getRetOauthState());
                            break;
                    }*/

                } else {

                    if ("30".equals(result.getRetTyp())) {
                        //비밀번호 불일치
                        setWiseLogHttpClient(ServerUrls.WEB.LOGIN_ID_PASSWORD_FAILURE);
                    } else if ("31".equals(result.getRetTyp())) {
                        //아이디 불일치
                        setWiseLogHttpClient(ServerUrls.WEB.LOGIN_ID_PASSWORD_FAILURE);
                    } else if ("10".equals(result.getRetTyp())) {
                        //비밀번호 강제 변경 대상
                        setWiseLogHttpClient(ServerUrls.WEB.LOGIN_PASSWORD_CHANGE_TARGET);

                        //10일때 특수 케이스 targetUrl을 추가한다
                        String encodedReturnUrl = "";
                        if (!TextUtils.isEmpty(targetUrl)) {
                            encodedReturnUrl = encodingQuery(targetUrl);
                            result.setRetUrl(result.getRetUrl() + "&returnurl=" + encodedReturnUrl);
                        }
                        if (checkBoxKeepLoginId.isChecked())
                            result.setRetUrl(result.getRetUrl() + "&appAutoLoginFlg=Y");
                    }
                    //10번일경우에 위에서 타켓 URL을 추가한다.
                    loginFailedDialog(result.getErrMsg(), result.getRetUrl(), result.getRetTyp());
                }
            }
        }

        @Override
        protected void onError(Throwable e) {
            super.onError(e);
            new CustomOneButtonDialog(LoginActivity.this).message(R.string.login_error_msg)
                    .cancelable(false)
                    .buttonClick(new CustomOneButtonDialog.ButtonClickListener() {
                        @Override
                        public void onClick(Dialog dialog) {
                            dialog.dismiss();
                        }
                    }).show();
        }
    }

    /**
     * 이전 웹액티비티에서 타겟 웹페이지를 로딩.
     * <p>
     * 이전 웹액티비티가 없으면 새 웹액티비티 시작
     * (Keys.INTENT.FOR_RESULT 인텐트로 판단)
     *
     * @param url url
     */
    private void goTargetUrl(String url) {
        String targetUrl = url;
        if (targetUrl == null) {
            //finish(); // URL이 없으면...
            //return;
            targetUrl = ServerUrls.getHttpRoot();
        }

        Intent intent = new Intent();
        intent.putExtra(Keys.INTENT.WEB_URL, targetUrl);

        if (getIntent().getBooleanExtra(Keys.INTENT.FOR_SIMPLE_LOGIN, false)
                || getIntent().getBooleanExtra(Keys.INTENT.FROM_ZZIM_BUTTON, false)
                || getIntent().getBooleanExtra(Keys.INTENT.FROM_PERSONAL_DEAL, false)) {
            setResult(RESULT_OK, intent);
        } else if (getIntent().getBooleanExtra(Keys.INTENT.FOR_RESULT, false)) {
            //로그인 완료 후 띄울 url 전달 (숏방에서 사용)
            EventBus.getDefault().post(new Events.DirectOrderAfterLoginEvent(targetUrl));
            //로그인 완료 후 띄울 url 전달 (네이티브 단품에서 사용)
            EventBus.getDefault().post(new Events.EventProductDetail.GoWebAfterLoginEvent(targetUrl));
            // 이전 웹액티비티에서 해당 페이지로 이동
            setResult(RESULT_OK, intent);
        } else if (getIntent().getBooleanExtra(Keys.INTENT.FROM_MOBILE_LIVE, false)) {
            //모바일라이브 전체화면에서 로그인한 경우
            //로그인 완료 후 웰컴메시지 호출용
            EventBus.getDefault().post(new Events.MobileLiveAfterLoginEvent());
        } else if (getIntent().getBooleanExtra(Keys.INTENT.FROM_SETTING, false)) {
            //설정에서 로그인한 경우 메인 띄우지 않고 설정화면을 유지함
        } else if (getIntent().getBooleanExtra(Keys.INTENT.LEFT_NAVIGATION, false)) {
            //왼쪽 네비게이션에서 호출한 경우 이전화면으로 되돌아감.
            EventBus.getDefault().post(new Events.NavigationReflashEvent());
            NavigationManager.isNavigationLogin = true;
            finish();
            return;
        } else if (getIntent().getBooleanExtra(Keys.INTENT.FOR_GS_SUPER, false)) {
            EventBus.getDefault().post(new Events.FlexibleEvent.UpdateGSSuperEvent(
                    -1, null, ""));
            setResult(RESULT_OK, intent);
        } else if (getIntent().getBooleanExtra(Keys.INTENT.FROM_NATIVE_PRODUCT, false)) {
            //네이티브단품에서 로그인한 경우
        } else {
            boolean fromTabMenu = TabMenu.fromTabMenu(getIntent());
            intent.putExtra(Keys.INTENT.FROM_TAB_MENU, fromTabMenu);

            //비로그인 상태에서 스키마로 접속한 경우,
            //스키마게이트웨이->로그인화면->웹화면 순으로 실행되는데, 실행된 웹액티비티에서 백키를 클릭할 경우 메인화면이 뜨지 않고 앱이 종료됨
            //BACK_TO_MAIN 플래그를 세팅하여 앱이 종료되지 않고 메인이 노출되도록 함
            try {
                Uri uri = Uri.parse(targetUrl);
                String backToMain = uri.getQueryParameter(Keys.INTENT.BACK_TO_MAIN);
                if ("Y".equals(backToMain)) {
                    getIntent().putExtra(Keys.INTENT.BACK_TO_MAIN, true);
                }
            } catch (Exception e) {
                // 10/19 품질팀 요청
                // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
                // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
                // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
                Ln.e(e);
            }

            // 새 웹액티비티 시작
            WebUtils.goWeb(this, targetUrl, getIntent(), true, fromTabMenu);
        }

        //로그인시 네비게이션 카테고리 데이터 삭제
        finish();
    }

    private void goExternalWeb(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    private String encodingQuery(String q) {
        try {
            // NOTE : 현재 서버에서 euc-kr로 인코딩된 파라미터를 받음
            // 20181019 (hklim) LoginActivity에서 encoding은 기존 euc-kr에서 utf-8로 변경 테스트 진행 후 commit 예졍
            return URLEncoder.encode(q, uriEncoding);
        } catch (UnsupportedEncodingException e) {
            Ln.e(e);
            return q;
        }
    }

    private void findLoginId() {
        goWeb(ServerUrls.WEB.FIND_LOGIN_ID);
        //와이즈로그 호출
        setWiseLogHttpClient(ServerUrls.WEB.LOGIN_FIND_ID);
    }

    private void findPassword() {
        goWeb(ServerUrls.WEB.FIND_PASSWORD);
        //와이즈로그 호출
        setWiseLogHttpClient(ServerUrls.WEB.LOGIN_FIND_PWD);
    }

    /**
     * 휴대폰 로그인
     */
    private void loginPhone() {
        setWiseLogHttpClient(ServerUrls.WEB.PHONE_LOGIN_CLICK);

        //앰플리튜드
        try {
            JSONObject temp = new JSONObject();
            temp.put(AMP_CLICK_LOGIN_TYPE_KEY, AMP_CLICK_LOGIN_TYPE_VAL_PHONE);
            AMPAction.sendAmpEventProperties(AMP_CLICK_LOGIN_TYPE, temp);
        } catch (Exception e) {
            //무시 하도록
        }

        goWeb(ServerUrls.WEB.PHONE_LOGIN, targetUrl);
    }

    private void goWeb(String url, String returnUrl) {

        String encodedReturnUrl = "";

        if (TextUtils.isEmpty(returnUrl)) {
            String strTargetTabId = getIntent().getStringExtra(Keys.INTENT.NAVIGATION_ID);
            if (!TextUtils.isEmpty(strTargetTabId)) {
                returnUrl = ServerUrls.WEB.MOVE_SHOP_FROM_TABID_URL + strTargetTabId;
            }
        }

        if (!TextUtils.isEmpty(returnUrl)) {
            encodedReturnUrl = encodingQuery(returnUrl);
        }

        goWeb(url + "&returnurl=" + encodedReturnUrl);
    }

    private void goWeb(String url) {
        WebUtils.goWeb(this, url);
        EventBus.getDefault().post(new Events.NavigationCloseEvent());
    }

    private void goWeb(String url, Intent intent) {
        WebUtils.goWeb(this, url, intent);
        EventBus.getDefault().post(new Events.NavigationCloseEvent());
    }

    /**
     * sns login alert & confirm dialog
     */
    public static Dialog makeSnsAlertDialog(Context context, View contentView, String btnTitle1, String btnTitle2, View.OnClickListener btnListener1, View.OnClickListener btnListener2) {
        final Dialog dialog = new Dialog(context, R.style.CustomDialog);
        Window window = dialog.getWindow();
        dialog.setCancelable(true);// 백키 통한 취소 허용.

        dialog.setContentView(R.layout.alert_sns_login);
        ViewGroup contentFrame = (ViewGroup) dialog.findViewById(R.id.content_frame);

        TextView btn1 = (TextView) dialog.findViewById(R.id.button_alert01);
        TextView btn2 = (TextView) dialog.findViewById(R.id.button_alert02);
        dialog.findViewById(R.id.button_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn1.setText(btnTitle1);
        btn1.setOnClickListener(btnListener1);
        btn2.setText(btnTitle2);
        btn2.setOnClickListener(btnListener2);

        contentFrame.addView(contentView);

        return dialog;
    }

    public static Dialog makeSnsConfirmDialog(final Context context, String content) {
        final Dialog dialog = new Dialog(context, R.style.CustomDialog);
        Window window = dialog.getWindow();
        dialog.setCancelable(true);// 백키 통한 취소 허용.
        dialog.setContentView(R.layout.confirm_sns_login);

        dialog.findViewById(R.id.button_phone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //탈퇴회원의 경우 팝업닫고 다른 계정으로 로그인시 매핑되지 않도록 한다.
                snsType = null;
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("tel:18994455"));
                context.startActivity(intent);
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.button_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snsType = null;
                dialog.dismiss();
            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                snsType = null;
                dialog.dismiss();
            }
        });

        ((TextView) dialog.findViewById(R.id.text_content)).setText(content);

        return dialog;
    }


    private void fingerprintLogin() {

        TokenCredentialsNew2 tokenCredentials = TokenCredentialsNew2.get();
        if (tokenCredentials != null && tokenCredentials.authToken != null) {
            UserConnector.LoginResult result = null;
            try {
                result = userAction.auth(tokenCredentials);
            } catch (Exception e) {
                Ln.e(e);
            }
            if (isEmpty(result) || !result.isSuccs()) {
                new CustomOneButtonDialog(this).message("로그인 정보가 유효하지 않습니다\r\n다시 로그인 해주세요.").buttonClick(new CustomOneButtonDialog.ButtonClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        isFingerprint = true;
                        FingerPrintSettings fingerPrintSettings = FingerPrintSettings.get();
                        fingerPrintSettings.isFingerprintMapping = false;
                        fingerPrintSettings.save();
                    }
                }).show();
            } else {
                setWiseLogHttpClient(ServerUrls.WEB.FINGERPRINT_LOGIN_SUCCESS);
                goTargetUrl(targetUrl);
                //로그인 성공시 표시할 메시지가 있으면 토스트로 띄운다.
                if (!TextUtils.isEmpty(result.getSuccMsg())) {
                    EventBus.getDefault().post(new Events.LoggedInToastEvent(result.getSuccMsg()));
                }
                // ARS 가입 비밀번호 변경이 필요한 경우
                else if ("Y".equalsIgnoreCase(result.getPasswdTvNeedChgYn())) {
                    EventBus.getDefault().post(new Events.LoggedInUpdatePassEvent(result.getpasswdTvChgUrl(), true));
                }
                //비밀번호 변경이 필요한 경우
                // 20181017 ARS 가입 비밀번호 변경 확인하면 기존 비밀번호 변경 확인 로직은 확인하지 않음 - hklm -
                if ("Y".equalsIgnoreCase(result.getPasswdNeedChgYn())) {
                    EventBus.getDefault().post(new Events.LoggedInUpdatePassEvent(result.getPasswdChgUrl(), false));
                }
            }
        }
    }
}
