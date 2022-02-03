/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;

import androidx.core.app.ActivityCompat;

import com.google.inject.Inject;
import com.gsshop.mocha.web.BaseWebChromeClient;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.Keys.REQCODE;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog.ButtonClickListener;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog.CancelListener;
import gsshop.mobile.v2.support.ui.CustomTwoButtonDialog;
import gsshop.mobile.v2.util.PermissionUtils;
import roboguice.util.Ln;

import static gsshop.mobile.v2.web.BaseWebActivity.callbackSkipFlag;
import static gsshop.mobile.v2.web.BaseWebActivity.showGPSEnablePopup;

/**
 * 공통 {@link WebChromeClient}
 */
public class MainWebViewChromeClient extends BaseWebChromeClient {

    private static final String TAG = "MainWebViewChromeClient";

    private final Activity activity;

    private Dialog dialog;

    private View mCustomView;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    private int mOriginalOrientation;

    private FrameLayout mContentView;
    private FrameLayout mFullscreenContainer;
    private int mOriginaluiOption = 0; //

    private static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


    @Inject
    public MainWebViewChromeClient(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    //pickup service
    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        boolean skip = true;
        if(skip) {
            return;
        }
        //super.onGeolocationPermissionsShowPrompt(origin, callback);
        Ln.i("onGeolocationPermissionsShowPrompt origin : " + origin);

        if (callbackSkipFlag) {
            return;
        }

        //권한체크
        if (!PermissionUtils.isPermissionGrantedForLocation(activity)) {
            Ln.i("onGeolocationPermissionsShowPrompt : !PermissionUtils.isPermissionGrantedForLocation");

            BaseWebActivity.geiOrigin = origin;
            BaseWebActivity.geoCallback = callback;

            //위치 권한 없는 경우 권한요청 팝업 노출
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Keys.REQCODE.PERMISSIONS_REQUEST_LOCATION);
        } else {
            showGPSEnablePopup(activity);
            callback.invoke(origin, true, false);
        }
    }

    /**
     * javascript confirm 팝업창 UI 변경.
     *
     * @param view    view
     * @param url     url
     * @param message message
     * @param result  result
     * @return boolean
     */
    @Override
    public boolean onJsConfirm(final WebView view, String url, String message, final JsResult result) {
        dialog = new CustomTwoButtonDialog(activity).message(message)
                .positiveButtonClick(new ButtonClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        result.confirm();
                    }
                }).negativeButtonClick(new ButtonClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        result.cancel();
                    }
                }).cancelled(new CancelListener() {
                    @Override
                    public void onCancel(Dialog dialog) {
                        result.cancel();
                    }
                });

        dialog.show();

        return true;
    }

    /**
     * javascript alert 팝업창 UI 변경.
     *
     * @param view    view
     * @param url     url
     * @param message message
     * @param result  result
     * @return boolean
     */
    @Override
    public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
        //바로구매웹뷰에서 얼랏참 뜬 경우는 프로그레스바 숨김처리
        EventBus.getDefault().post(new Events.HideProgressEvent());

        dialog = new CustomOneButtonDialog(activity).message(message)
                .buttonClick(new ButtonClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        result.confirm();
                    }
                }).cancelled(new CancelListener() {
                    @Override
                    public void onCancel(Dialog dialog) {
                        result.cancel();
                    }
                });
        dialog.show();

        return true;
    }


    /**
     * 현재 떠있는 Javascript dialog 윈도우를 닫는다.
     * <p>
     * dialog가 뜬 상태에서 웹뷰를 destroy 시키는 경우
     * dialog를 닫지 않으면 WindowLeak 에러 발생함.
     */
    @Override
    public void destroy() {
        onHideCustomView();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }

    /**
     * web page progress bar hide
     */
    private static final int PROGRESS_LIMIT = 60;

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        if ((newProgress > PROGRESS_LIMIT) && progress != null) {
            progress.hide();
        }
    }

    // For Android 5.0+
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                     FileChooserParams fileChooserParams) {
        if(!PermissionUtils.isPermissionGrantedForStorageWrite(activity)) {
            // 저장 권한 없는 경우, 저장관련 권한 요청
            // IMAGE_PICKER_PERMISSION_REQUEST 받는 곳은 없어, 왜냐하면 승인 요청만 할꺼니까.
            ActivityCompat.requestPermissions((Activity) activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQCODE.IMAGE_PICKER_PERMISSION_REQUEST);
            return false;
        }

        if (BaseWebActivity.mUploadMessageLollipop != null) {
            BaseWebActivity.mUploadMessageLollipop.onReceiveValue(null);
            BaseWebActivity.mUploadMessageLollipop = null;
        }
        BaseWebActivity.mUploadMessageLollipop = filePathCallback;

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activity.startActivityForResult(Intent.createChooser(intent, "파일 선택"), REQCODE.IMAGE_PICKER_LOLLIPOP);

//        return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
        return true;
    }

    // For Android 4.1 +
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        openFileChooser(uploadMsg);
    }

    // For Andorid 3.0 +
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
        openFileChooser(uploadMsg);
    }

    /**
     * file 태그를 클릭했을때 호출된다.<br>
     * 단, 킷캣(4.4.2) 버전에서는 호출안되므로 별도 처리가 필요하다.
     *
     * @param uploadMsg uploadMsg
     */
    // For Android < 3.0
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        BaseWebActivity.mUploadMessage = uploadMsg;
        activity.startActivityForResult(WebViewImagePicker.getImageIntent(activity), REQCODE.IMAGE_PICKER);
    }

    /**
     * 웹뷰에서 전체화면 모드 호출시 전체화면을 보여준다.
     *
     * @param view     view
     * @param callback callback
     */
    @Override
    public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            mOriginalOrientation = activity.getRequestedOrientation();
            FrameLayout decor = (FrameLayout) activity.getWindow().getDecorView();
            mFullscreenContainer = new FullscreenHolder(activity);
            mFullscreenContainer.addView(view, COVER_SCREEN_PARAMS);
            decor.addView(mFullscreenContainer, COVER_SCREEN_PARAMS);
            mCustomView = view;
            setFullscreen(true);
            mCustomViewCallback = callback;

            if(activity instanceof NoTabWebActivity){
                //되돌리는 소스 필요, 작아졌을때에 대한 개발이 필요, uiOption과 decorView의 위치(자역변수로!)
                View decorView =activity.getWindow().getDecorView();
                mOriginaluiOption = decorView.getSystemUiVisibility();
                int temp = mOriginaluiOption;
                //소프트키와 indicaor를 사라지게 하는 설정
                if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH )
                    temp |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN )
                    temp |= View.SYSTEM_UI_FLAG_FULLSCREEN;
                if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT )
                    temp |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

                //위에서말한 설정을 적용하는 코드
                decorView.setSystemUiVisibility( temp );
            }

        }

        super.onShowCustomView(view, callback);
    }

    public boolean isShowCustomView() {
        return mCustomView != null;
    }

    /**
     * 전체 화면 모드를 중지한다.
     */
    @Override
    public void onHideCustomView() {
        if (mCustomView == null) {
            return;
        }
        setFullscreen(false);
        FrameLayout decor = (FrameLayout) activity.getWindow().getDecorView();
        decor.removeView(mFullscreenContainer);
        mFullscreenContainer = null;
        mCustomView = null;
        mCustomViewCallback.onCustomViewHidden();

        if(activity instanceof NoTabWebActivity){
            //uiOption과 decorView의 위치(자역변수로!)
            View decorView =activity.getWindow().getDecorView();

            //위에서말한 설정을 적용하는 코드
            decorView.setSystemUiVisibility( mOriginaluiOption );
        }
//        activity.setRequestedOrientation(mOriginalOrientation);
    }

    /**
     * mCustomView를 풀스크린으로 설정
     *
     * @param enabled enabled
     */
    private void setFullscreen(boolean enabled) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        if (enabled) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
//            if (mCustomView != null) {
//                mCustomView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
//            } else {
//                mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
//            }
        }
        win.setAttributes(winParams);
    }

    private static class FullscreenHolder extends FrameLayout {
        public FullscreenHolder(Context ctx) {
            super(ctx);
            setBackgroundColor(ctx.getResources().getColor(android.R.color.black));
        }

        @Override
        public boolean onTouchEvent(MotionEvent evt) {
            return true;
        }
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        Ln.d(TAG + "onConsoleMessage:" + consoleMessage.message() + " -- From line "
                + consoleMessage.lineNumber() + " of " + consoleMessage.sourceId());
        return super.onConsoleMessage(consoleMessage);
    }
}
