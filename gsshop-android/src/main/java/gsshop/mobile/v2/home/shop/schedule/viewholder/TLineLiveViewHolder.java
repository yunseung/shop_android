/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.schedule.viewholder;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Handler;
import androidx.fragment.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.Player;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.main.TvLiveBanner;
import gsshop.mobile.v2.home.shop.schedule.model.SchedulePrd;
import gsshop.mobile.v2.home.shop.tvshoping.TvLiveTimeLayout;
import gsshop.mobile.v2.support.media.OnMediaPlayerController;
import gsshop.mobile.v2.support.media.OnMediaPlayerListener;
import gsshop.mobile.v2.support.media.model.MediaInfo;
import gsshop.mobile.v2.support.tv.VideoParameters;
import gsshop.mobile.v2.support.ui.counter.NumberCounter;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.util.NetworkUtils;
import gsshop.mobile.v2.web.WebUtils;

import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;
import static gsshop.mobile.v2.Events.FlexibleEvent.SchLivePlayEvent;
import static gsshop.mobile.v2.ServerUrls.WEB.SCH_LIVE_MAINPRD_LIVETALK;
import static gsshop.mobile.v2.ServerUrls.WEB.SCH_LIVE_MAINPRD_VIDEO_PAUSE;
import static gsshop.mobile.v2.ServerUrls.WEB.SCH_LIVE_MAINPRD_VIDEO_PLAY;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleShopFragment.makeWiselogUrl;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleViewType.TYPE_PRD_LIVE;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleViewType.viewTypeMap;

public class TLineLiveViewHolder extends TLinePrdViewHolder implements OnMediaPlayerListener {

    //SPE 배너
    protected final ImageView spe_img;

    private OnMediaPlayerController playerController = null;
    private boolean isMdiaPlayerResize = false;
    private View info_view;
    private View info_view_bottom;
    private View pause;
    private View go_link;
    private View full_screen;

    private Context context;

    // 라이브톡
    private final View btn_live_talk;
    private final View btn_live_talk_dummy;

    protected final ImageView[] badge = new ImageView[3];

    private final View counter;
    private final LinearLayout number_counter_layout;

    public static boolean isStartAnimation = false;

    private Animation startAnimation;
    private Animation closeAnimation;
    private NumberCounter numberCounter;

    private boolean isMediaStopFromNavi = false;

    private SchedulePrd data;

    public TvLiveTimeLayout layoutTvLiveProgress;
    private TextView txt_remain_time;

    /**
     * bind/unbind 수행시에도 생방송 플레이버튼 숨김을 유지하기 위한 플래그
     */
    public static boolean isPlayButtonVisible = true;

    /**
     * @param itemView
     */
    public TLineLiveViewHolder(View itemView) {
        super(itemView);

        //SPE 배너
        spe_img = (ImageView) itemView.findViewById(R.id.spe_img);
        DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), spe_img);

        info_view = itemView.findViewById(R.id.info_view);
        info_view_bottom = itemView.findViewById(R.id.info_view_bottom);
        pause = itemView.findViewById(R.id.pause);
        go_link = itemView.findViewById(R.id.go_link);
        full_screen = itemView.findViewById(R.id.full_screen);
        info_view.setVisibility(View.GONE);

        // 라이브톡
        btn_live_talk = itemView.findViewById(R.id.btn_live_talk);
        btn_live_talk_dummy = itemView.findViewById(R.id.btn_live_talk_dummy);

        counter = itemView.findViewById(R.id.counter);
        number_counter_layout = (LinearLayout) itemView.findViewById(R.id.number_counter_layout);

        layoutTvLiveProgress = (TvLiveTimeLayout) itemView.findViewById(R.id.tv_live_progress);
        txt_remain_time = (TextView) itemView.findViewById(R.id.txt_remain_time);

        DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), info_view);

        DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), counter);

        EventBus.getDefault().register(this);
    }

    /*
     * bind
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final Context context, final int position, final SchedulePrd data) {
        super.onBindViewHolder(context, position, data);

        this.context = context;
        this.data = data;

        //spe 배너
        if (data.specialPgmInfo != null && !TextUtils.isEmpty(data.specialPgmInfo.imageUrl)) {
            ImageUtil.loadImageResize(context, data.specialPgmInfo.imageUrl, spe_img, R.drawable.noimg_logo);
            spe_img.setVisibility(View.VISIBLE);
            spe_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String finalImgLink = data.specialPgmInfo.linkUrl;
                    if (finalImgLink != null && finalImgLink.length() > 4) {
                        WebUtils.goWeb(context, finalImgLink);
                    }
                }
            });
        } else {
            spe_img.setVisibility(View.GONE);
        }

        //JELLY_BEAN 이상 버전에서는 동영상플레이어 노출
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            playerController = (OnMediaPlayerController) ((FragmentActivity) context).getSupportFragmentManager().findFragmentById(R.id.sch_media_player);
            if (playerController != null) {
                playerController.setBackgroundResource(R.color.sch_player_bg);
                playerController.setOnMediaPlayerListener(this);
                MediaInfo media = playerController.getMediaInfo();
                if (media != null && data.livePlayInfo != null) {
                    media.videoId = data.livePlayInfo.videoid;
                    media.contentUri = data.livePlayInfo.linkUrl;
                    media.title = data.product.exposPrdName;
                }

                if (!isMdiaPlayerResize && playerController != null) {
                    isMdiaPlayerResize = true;
                    DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), playerController.getPlayerView());
                }

                if (playerController.isPlaying()) {
                    root_view.setVisibility(View.GONE);
                } else {
                    root_view.setVisibility(View.VISIBLE);
                }
            }
        }

        //생방송 상품 링크주소가 없는 경우 상품상세 아이콘 숨김처리
        //0525 기획추가 공영방송 및 보험방송인경우 영상에서의 상품연결은 없음 
        if (TextUtils.isEmpty(data.product.linkUrl)
                || "Y".equals(data.publicBroadYn)
                || "Y".equals(data.product.insuYn)
                ){
            go_link.setVisibility(View.GONE);
        } else {
            go_link.setVisibility(View.VISIBLE);
        }

        // 라이브톡 버튼 노출여부
        // 라이브톡 노출시 방송알림 영역은 종만 표시되고, 미노출시 종+텍스트 표시됨
        if (data.liveTalkInfo != null) {
            btn_live_talk.setVisibility(View.VISIBLE);
            btn_live_talk_dummy.setVisibility(View.GONE);
            btn_live_talk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WebUtils.goWeb(context, data.liveTalkInfo.linkUrl, ((Activity) context).getIntent());
                    //라이브톡 버튼 효율 코드
                    ((HomeActivity) context).setWiseLogHttpClient(makeWiselogUrl(SCH_LIVE_MAINPRD_LIVETALK));
                }
            });
        } else {
            btn_live_talk.setVisibility(View.GONE);
            btn_live_talk_dummy.setVisibility(View.INVISIBLE);
        }

        //3가지 버튼이 모두 비노출인 경우 버큰영역을 숨김
        if (viewTypeMap.get(data.viewType) == TYPE_PRD_LIVE) {
            if (btn_live_talk.getVisibility() == View.GONE
                    && alarmLay.getVisibility() == View.INVISIBLE && btn_pay.getVisibility() == View.GONE) {
                btn_layout.setVisibility(View.GONE);
            } else {
                //공영상품일때는 버튼 노출 안함
                if (!"Y".equals(data.publicBroadYn)) {
                    btn_layout.setVisibility(View.VISIBLE);
                }
            }
        }

        //방송종료 콜백호출까지 대기시간 세팅
//        layoutTvLiveProgress.setCallbackInterval(100L);
        //방송종료 콜백 등록 (콜백을 등록해야 TvLiveTimeLayout 클레스에서 "방송종료" 텍스트를 표시함)
        layoutTvLiveProgress.setOnTvLiveFinishedListener(new TvLiveTimeLayout.OnTvLiveFinishedListener() {
            @Override
            public void onTvLiveFinished() {
                //방송종료인 경우만 ("TV HIT"은 아님)
                if (context.getString(R.string.home_tv_live_view_close_text).equals(txt_remain_time.getText())) {
                    /*
                    1. 방송 시청중이 아닐경우
                    -플레이 버튼 히든
                    -방송죵료 노출
                    -썸네일에서 "ON" 이미지 히든처리

                    2. 방송 시청중인 경우 (정지버튼 누르는 시점에)
                    -플레이 버튼 노출
                    -방송죵료 노출
                    -썸네일에서 "ON" 이미지 히든처리
                    */
                    //1. 방송 시청중이 아닐경우
                    if (root_view.getVisibility() == View.VISIBLE) {
                        //플레이 버튼 히든
                        play.setVisibility(View.INVISIBLE);
                        isPlayButtonVisible = false;
                        //썸네일에서 "ON" 이미지 히든처리위해 이벤트 전달
                        EventBus.getDefault().post(new Events.FlexibleEvent.OnAirEvent(false));
                    }
                }
            }
        });

        //에니메이션
        counter.setVisibility(View.GONE);
        if (data.product.salesInfo != null && data.product.salesInfo.saleRateExposeYn != null &&
                "Y".equalsIgnoreCase(data.product.salesInfo.saleRateExposeYn) && !isStartAnimation) {
            isStartAnimation = true;
            numberCounter = new NumberCounter((Activity) context, number_counter_layout);
            boolean isWithNumber = numberCounter.withNumber(Long.parseLong(getOnlyNumberString(data.product.salesInfo.ordQty)));
            startAnimation = AnimationUtils.loadAnimation(context, R.anim.count_in_alpha);
            startAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                    numberCounter.start();

                    closeAnimation = AnimationUtils.loadAnimation(context, R.anim.count_out_alpha);

                    closeAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            counter.setVisibility(View.GONE);

                            if (data.livePlayInfo != null
                                    && (!TextUtils.isEmpty(data.livePlayInfo.linkUrl)
                                        || (!TextUtils.isEmpty(data.livePlayInfo.videoid) && data.livePlayInfo.videoid.length() > 4))) {
                                if (isPlayButtonVisible) {
                                    play.setVisibility(View.VISIBLE);
                                }
                                play.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        confirmNetworkBilling((Activity) context, data.product.linkUrl);
                                    }
                                });
                            } else {
                                play.setVisibility(View.GONE);
                            }
                            layoutTvLiveProgress.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            counter.startAnimation(closeAnimation);
                        }
                    }, 1500);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            if (isWithNumber) {
                play.setVisibility(View.GONE);
                layoutTvLiveProgress.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        counter.startAnimation(startAnimation);
                        counter.setVisibility(View.VISIBLE);
                    }
                }, 100);
            } else {
                //구매수량 숫자가 유효하지 않으면 구매수량 애니메이션 없이 플레이버튼과 남은시간 표시
                play.setVisibility(View.VISIBLE);
                layoutTvLiveProgress.setVisibility(View.VISIBLE);
            }
        } else {
            counter.setVisibility(View.GONE);
            if (data.livePlayInfo != null
                    && (!TextUtils.isEmpty(data.livePlayInfo.linkUrl)
                    || (!TextUtils.isEmpty(data.livePlayInfo.videoid) && data.livePlayInfo.videoid.length() > 4))) {
                if (isPlayButtonVisible) {
                    play.setVisibility(View.VISIBLE);
                }
                play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirmNetworkBilling((Activity) context, data.product.linkUrl);
                    }
                });
            } else {
                play.setVisibility(View.GONE);
            }
            layoutTvLiveProgress.setVisibility(View.VISIBLE);
        }

        info_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                info_view.setVisibility(View.GONE);
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                info_view.setVisibility(View.GONE);
                EventBus.getDefault().post(new SchLivePlayEvent(false, -1, false));

                //방송종료인 경우만 ("TV HIT"은 아님)
                if (context.getString(R.string.home_tv_live_view_close_text).equals(txt_remain_time.getText())) {
                    //썸네일에서 "ON" 이미지 히든처리위해 이벤트 전달
                    EventBus.getDefault().post(new Events.FlexibleEvent.OnAirEvent(false));
                }

                //pause 버튼 클릭 효율코드
                ((HomeActivity) context).setWiseLogHttpClient(makeWiselogUrl(SCH_LIVE_MAINPRD_VIDEO_PAUSE));

            }
        });
        go_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                info_view.setVisibility(View.GONE);
                layoutTvGoods.performClick();
            }
        });

        full_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                info_view.setVisibility(View.GONE);
                String url = data.product.linkUrl;
                if ("Y".equals(data.publicBroadYn) || "Y".equals(data.product.insuYn)) {
                    url = "";
                }
                playVideo((Activity) context, buildVideoParam(url, data.product.mainPrdImgUrl, data.livePlayInfo.videoid, data.livePlayInfo.linkUrl));
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmNetworkBilling((Activity) context, data.product.linkUrl);
                //play 버튼 클릭 효율코드
                ((HomeActivity) context).setWiseLogHttpClient(makeWiselogUrl(SCH_LIVE_MAINPRD_VIDEO_PLAY));
            }
        });

        isMediaStopFromNavi = false;
    }

    /**
     * 문자열에서 숫자만 추출
     *
     * @param str String
     * @return String
     */
    public String getOnlyNumberString(String str) {
        if (str == null) return str;

        StringBuilder sb = new StringBuilder();
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char curChar = str.charAt(i);
            if (Character.isDigit(curChar)) sb.append(curChar);
        }

        return sb.toString();
    }

    /**
     * 전화상담이 필요한 상품인지 체크. 현재는 핸드폰이나 렌탈 상품일 때 해당.
     *
     * @param tvLiveBanner tvLiveBanner
     * @return boolean
     */
    private boolean isNeededConsultationCall(TvLiveBanner tvLiveBanner) {
        return DisplayUtils.isTrue(tvLiveBanner.isCellPhone)
                || DisplayUtils.isTrue(tvLiveBanner.isRental);
    }

    /**
     * 3G 과금 안내 팝업
     *
     * @param activity   activity
     * @param productUrl productUrl
     */
    protected void confirmNetworkBilling(final Activity activity, final String productUrl) {

         NetworkUtils.confirmNetworkBillingAndShowPopup(activity, new NetworkUtils.OnConfirmNetworkListener() {
             @Override
             public void isConfirmed(boolean isConfirmed) {
                 if (isConfirmed) {
                     EventBus.getDefault().post(new SchLivePlayEvent(true, 0, false));
                 }
             }

             @Override
             public void inCanceled() {
             }
         });
    }

    protected VideoParameters buildVideoParam(String productUrl, String imageUrl, String videoId, String livePlayUrl) {
        VideoParameters param = new VideoParameters();

        param.videoId = videoId;
        param.videoUrl = livePlayUrl;
        param.productInfoUrl = productUrl;
        param.orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        param.proudctImageUrl = imageUrl;

        if (isNotEmpty(playerController.getMediaInfo())) {
            param.isPlaying = playerController.getMediaInfo().isPlaying;
        }

        return param;
    }

    protected void playVideo(Activity activity, VideoParameters param) {
        Intent intent = new Intent(Keys.ACTION.LIVE_VIDEO_PLAYER);

        // param != null 방어로직 추가 - 비즈니스 로직에 영향이 없도록 startActivityForResult 유지하고 param 만 처리 10/05
        // if( param != null && Keys.INTENT.BASE_PLAY.equals(param.playType)) 위 로직에 있으나 param null 있을수 있다 10/05
        if (param != null) {
            intent.putExtra(Keys.INTENT.VIDEO_ID, param.videoId);
            intent.putExtra(Keys.INTENT.VIDEO_URL, param.videoUrl);
            intent.putExtra(Keys.INTENT.VIDEO_IMAGE_URL, param.proudctImageUrl);
            intent.putExtra(Keys.INTENT.VIDEO_PRD_URL, param.productInfoUrl);
            intent.putExtra(Keys.INTENT.VIDEO_ORIENTATION, param.orientation);
            intent.putExtra(Keys.INTENT.VIDEO_IS_PLAYING, param.isPlaying);
        }
        intent.putExtra(Keys.INTENT.FOR_RESULT, true);

        activity.startActivityForResult(intent, Keys.REQCODE.VIDEO);
    }

    /**
     * 생방송 실행.
     *
     * @param event event
     */
    public void onEvent(SchLivePlayEvent event) {
        if (!event.play) {
            root_view.setVisibility(View.VISIBLE);
            info_view.setVisibility(View.GONE);
        }

        if (playerController != null) {
            MediaInfo media = playerController.getMediaInfo();

            //슬라이드 노출/숨김시 동영상 정지/재생 하려면 주석제거
            //네비게이션뷰가 열리면서 동영상 정지한 경우
            /*if (event.fromNavi && !event.play && playerController.isPlaying() ) {
                isMediaStopFromNavi = true;
            }
            //네비게이션뷰가 닫힐때 재생은 네비 열리면서 정지된 경우만
            if (event.fromNavi && event.play) {
                if (!isMediaStopFromNavi) {
                    return;
                }
                root_view.setVisibility(View.GONE);
                isMediaStopFromNavi = false;
            }*/

            if (media != null) {
                media.isPlaying = event.play;
                playerController.setMediaInfo(media);
                if(media.isPlaying) {
                    playerController.playPlayer();
                } else {
                    playerController.stopPlayer();
                }
            } else {
                media = new MediaInfo();
                if (data.livePlayInfo != null) {
                    media.videoId = data.livePlayInfo.videoid;
                    media.contentUri = data.livePlayInfo.linkUrl;
                }
                media.title = data.product.exposPrdName;
                media.playerMode = MediaInfo.MODE_SIMPLE;
                media.currentPosition = 0;
                media.channel = MediaInfo.CHANNEL_MAIN_LIVE;
                media.isPlaying = event.play;
                media.lastPlaybackState = Player.STATE_IDLE;
                playerController.setMediaInfo(media);
                if(media.isPlaying) {
                    playerController.playPlayer();
                } else {
                    playerController.stopPlayer();
                }
            }
        }
    }

    public void onEvent(Events.FlexibleEvent.TvLiveUnregisterEvent event) {
        root_view.setVisibility(View.GONE);
        EventBus.getDefault().unregister(this);
    }

    /**
     * 생방송 남은시간 노출용 타이머를 시작한다.
     */
    protected void startTvLiveTimer() {
        String endTime = "";
        if (!TextUtils.isEmpty(data.broadEndDate) && !"0".equals(data.broadEndDate)) {
            endTime = data.broadEndDate;
        } else {
            stopTvLiveTimer();
        }
        layoutTvLiveProgress.updateTvLiveTime("", "", endTime);
    }

    /**
     * 생방송 남은시간 노출용 타이머를 정지한다.
     */
    protected  void stopTvLiveTimer() {
        layoutTvLiveProgress.stopTimer();
    }

    /**
     * 생방송 남은시간 노출용 타이머를 시작/정지 시킨다.
     * (TVScheduleShopFragment onStart/onStop에서 호출됨)
     *
     * @param event SchLiveTimerEvent
     */
    public void onEvent(Events.FlexibleEvent.SchLiveTimerEvent event) {
        if (event.start) {
            //생방송 남은시간 표시용 타이머 시작
            startTvLiveTimer();
        } else {
            //생방송 남은시간 표시용 타이머 정지
            stopTvLiveTimer();
        }
    }

    /**
     * 뷰가 화면에 노출될때 발생
     */
    @Override
    public void onViewAttachedToWindow() {
        //EventBus.getDefault().registerSticky(this);
        //생방송 남은시간 표시용 타이머 시작
        startTvLiveTimer();
    }

    /**
     * 뷰가 화면에서 사라질때 발생
     */
    @Override
    public void onViewDetachedFromWindow() {
        //EventBus.getDefault().unregister(this);
        //생방송 남은시간 표시용 타이머 정지
        stopTvLiveTimer();
    }


    /**
     * MediaOnPlyaerListener Methods
     */
    @Override
    public void onTap(boolean show) {
        if(info_view.getVisibility() == View.GONE){
            info_view.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFullScreenClick(MediaInfo media) {

    }


    @Override
    public void onPlayed() {

    }

    @Override
    public void onPaused() {

    }

    @Override
    public void onFinished(MediaInfo media) {

    }

    @Override
    public void onError(Exception e) {
        root_view.setVisibility(View.VISIBLE);
        info_view.setVisibility(View.GONE);
    }
}

