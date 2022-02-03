/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.renewal.flexible;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import com.gsshop.mocha.core.exception.ApplyExceptionHandler;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.shop.schedule.model.SchedulePrd;
import gsshop.mobile.v2.util.StringUtils;
import gsshop.mobile.v2.web.WebUtils;

import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;

/**
 * 편성표 주상품(라이브) V2
 */
public class RenewalTLineLiveViewHolder extends SchMapMutxxxVH {

    /**
     * @param itemView
     */
    public RenewalTLineLiveViewHolder(View itemView) {
        super(itemView);

        EventBus.getDefault().register(this);
    }

    @Override
    public void onBindViewHolder(final Context context, final int position, final SchedulePrd data) {
        super.onBindViewHolder(context, position, data);

        img_brd_type.setVisibility(View.VISIBLE);
        lay_cv_remain_time.setVisibility(View.VISIBLE);
        img_cv_play.setVisibility(View.VISIBLE);

        // 재생버튼 클릭시 상세화면 이동 (자동재생 플래그 추가)
        if (isNotEmpty(data.product.playUrl)) {
            lay_cv_remain_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String linkUrl = StringUtils.addUriParameter(Uri.parse(data.product.playUrl),
                            Keys.PARAM.VOD_PLAY, "LIVE").toString();
                    WebUtils.goWeb(context, linkUrl);
                }
            });
        }

        // TV생방송 progress
        cv_remain_time.setOnTvLiveFinishedListener(new RenewalBroadTimeLayout.OnTvLiveFinishedListener() {
            @Override
            public void onTvLiveFinished() {
                doLiveFinished();
            }
        });


        txt_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebUtils.goWeb(context, data.product.directOrdInfo.linkUrl);
                //vod stop event
                EventBus.getDefault().post(new Events.VodShopPlayerEvent(Events.VodShopPlayerEvent.VodPlayerAction.VOD_STOP, position));
            }
        });

    }

    /**
     * 방송종료 콜백 호출시 화면 UI를 변경한다.
     */
    private void doLiveFinished() {
        //방송종료인 경우만 ("TV HIT"은 아님)
        if (context.getString(R.string.home_tv_live_view_close_text).equals(cv_remain_time.getText())) {
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
            //방송 종료시 재생버튼 숨기고 "방송종료" 텍스트만 노출
            img_cv_play.setVisibility(View.GONE);
            //썸네일에서 "ON" 이미지 히든처리위해 이벤트 전달
            EventBus.getDefault().post(new Events.FlexibleEvent.OnAirEvent(false));
        }
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
        cv_remain_time.updateTvLiveTime(RenewalBroadTimeLayout.DisplayType.schedule, data.broadStartDate, endTime);
    }

    /**
     * 생방송 남은시간 노출용 타이머를 정지한다.
     */
    protected void stopTvLiveTimer() {
        cv_remain_time.stopTimer();
    }

    public void onEvent(Events.FlexibleEvent.TvLiveUnregisterEvent event) {
        EventBus.getDefault().unregister(this);
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
     * 방송종료 이벤트를 수신시 재생버튼을 비노출한다.
     * (방송종료 이후에 편성표 진입한 경우 재생버튼이 setOnTvLiveFinishedListener 콜백호출 전까지 노출되는 현상 개선용)
     *
     * @param event BroadCloseEvent
     */
    public void onEvent(Events.FlexibleEvent.BroadCloseEvent event) {
        //"방송종료"이면서 이벤트를 받았을때만 GONE처리
        if (context.getString(R.string.home_tv_live_view_close_text).equals(cv_remain_time.getText())){
            img_cv_play.setVisibility(View.GONE);
        }
    }

    /**
     * 뷰가 화면에 노출될때 발생
     */
    @Override
    public void onViewAttachedToWindow() {
        //생방송 남은시간 표시용 타이머를 시작한다.
        startTvLiveTimer();
    }

    /**
     * 뷰가 화면에서 사라질때 발생
     */
    @Override
    public void onViewDetachedFromWindow() {
        //생방송 남은시간 표시용 타이머를 정지한다.
        stopTvLiveTimer();
    }
}

