/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web.handler;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;

import com.gsshop.mocha.device.NetworkStatus;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.Keys.REQCODE;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.support.tv.VideoParameters;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog.ButtonClickListener;
import gsshop.mobile.v2.support.ui.CustomTwoButtonDialog;
import gsshop.mobile.v2.util.CookieUtils;
import gsshop.mobile.v2.util.NetworkUtils;

/**
 * 생방송 RTSP 스트리밍 요청 처리.
 * <p>
 * - rtsp://livem.gsshop.com:554/gsshop/_definst_/gsshop.sdp
 *
 * 참고
 * - "RTSP" 스트리밍은 웹에서 호출 안하기로 함 (2018.04.02)
 * - LIVE_TV 일때도 과금팝업 처리 영향있다
 */
public class LiveTVUrlHandler implements WebUrlHandler {

    @Override
    public boolean handle(Activity activity, WebView webview, String url) {
        boolean wifiConnected = NetworkStatus.isWifiOrEthernetConnected(activity
                .getApplicationContext());

        String noDataWarningFlg = "N";
        String globalConfirm = "N";

        if(url != null){
            try{
                Uri uri = Uri.parse(url);
                if(uri != null){
                    noDataWarningFlg = uri.getQueryParameter("noDataWarningFlg");
                    globalConfirm = uri.getQueryParameter("globalConfirm");
                }

            }catch (Exception e){

            }

        }

        if (wifiConnected || MainApplication.isNetworkApproved || "Y".equals(globalConfirm)) {

            playVideo(activity, buildVideoParam(url), true);
            if("Y".equals(globalConfirm)){
                MainApplication.isNetworkApproved = true;
            }

        } else{
            if("Y".equals(noDataWarningFlg)){
                playVideo(activity, buildVideoParam(url), false);
            }else{
                NetworkUtils.confirmNetworkBillingAndShowPopup(activity, new NetworkUtils.OnConfirmNetworkListener() {
                    @Override
                    public void isConfirmed(boolean isConfirmed) {
                        if (isConfirmed) {
                            playVideo(activity, buildVideoParam(url),true);
                            MainApplication.isNetworkApproved = true;
                        }
                    }

                    @Override
                    public void inCanceled() {}
                });
            }
        }

        return true;
    }

    @Override
    public boolean match(String url) {
        return url.startsWith(ServerUrls.APP.LIVE_TV);
    }

    private void playVideo(Activity activity, VideoParameters param, boolean isplaying) {
        Intent intent = new Intent(Keys.ACTION.LIVE_VIDEO_PLAYER);
        //10/13 null 체크 이민수
        //10/19 품질팀 요청
        if (param != null) {
            if (param.videoId != null) {
                intent.putExtra(Keys.INTENT.VIDEO_ID, param.videoId);
            }
            if (param.videoUrl != null) {
                intent.putExtra(Keys.INTENT.VIDEO_URL, param.videoUrl);
            }
            if (param.productInfoUrl != null) {
                intent.putExtra(Keys.INTENT.VIDEO_PRD_URL, param.productInfoUrl);
            }

            if (param.videoStartTime != null) {
                intent.putExtra(Keys.INTENT.VIDEO_START_TIME, param.videoStartTime);
            }

            if(isplaying){
                intent.putExtra(Keys.INTENT.VIDEO_IS_PLAYING, true);
            }else{
                intent.putExtra(Keys.INTENT.VIDEO_IS_PLAYING, false);
            }

        }
        intent.putExtra(Keys.INTENT.FOR_RESULT, true);


        activity.startActivityForResult(intent, REQCODE.VIDEO);
    }

    protected VideoParameters buildVideoParam(String url) {
        VideoParameters param = new VideoParameters();

        param.videoUrl = url;
        param.productInfoUrl = ServerUrls.WEB.LIVE_TV;

        return param;
    }
}
