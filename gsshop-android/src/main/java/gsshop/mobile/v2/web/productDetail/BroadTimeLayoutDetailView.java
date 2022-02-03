package gsshop.mobile.v2.web.productDetail;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.shop.renewal.flexible.RenewalBroadTimeLayout;
import gsshop.mobile.v2.util.GlobalTimer;
import gsshop.mobile.v2.util.TimeRemaining;


/**
 * 방송 남은시간 표시용 V2
 *
 */
public class BroadTimeLayoutDetailView extends RenewalBroadTimeLayout {

    private static final String TAG = "BroadTimeLayoutDetailView";

    private boolean isMp4 = false;

    public BroadTimeLayoutDetailView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public BroadTimeLayoutDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BroadTimeLayoutDetailView(Context context) {
        super(context);
    }

    private OnLiveTimeRefreshListener mListener;

    /**
     * MP4 타이머를 시작한다.
     */
    public void startMP4Timer() {
        isMp4 = true;
        try {
            EventBus.getDefault().unregister(this);
            EventBus.getDefault().register(this);
        }catch(Exception e){
            //Ln.e(e);
        }
        GlobalTimer.getInstance().startTimer();
        updateRemainMp4Time();
    }

    private void updateRemainMp4Time() {
        mListener.onTimeRefresh(null);
    }

    /**
     * 현재 시간을 체크해서 남은 시간을 표시한다.
     */
    @Override
    protected void updateRemainTime() {
        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try{
                    TimeRemaining timeRemaining = new TimeRemaining(endDate);
                    if(timeRemaining.isAfterTime()){
                        // 방송 중
                        if (mTxtRemainTime != null) {
                            String strTimeRemaining = timeRemaining.getDisplayTime(TimeRemaining.DisplayType.HOUR);
                            mTxtRemainTime.setText(strTimeRemaining);
                            if (mListener != null) {
                                mListener.onTimeRefresh(strTimeRemaining);
                            }
                        }
                    }else{
                        // 방송 종료 전후
                        if (mTxtRemainTime != null) {
                            mTxtRemainTime.setText(R.string.home_tv_live_view_close_da_text);
//                    EventBus.getDefault().post(new Events.FlexibleEvent.BroadCloseEvent());
                        }
                        if (mListener != null) {
                            mListener.onBroadCastFinished();
                            mListener.onTimeRefresh(getContext().getString(R.string.home_tv_live_view_close_da_text));
                        }
                        stopTimer();
                        if (mOnTvLiveFinishedListener != null) {
                            //방송 재호출시 부하를 줄이기 위해 4초에서 14초 사이의 랜덤값으로 api를 호출한다.
                            int randomInteger = (new Random().nextInt(10) + 4 )* 1000;
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mOnTvLiveFinishedListener.onTvLiveFinished();
                                }
                            }, randomInteger);
                        }
                    }
                } catch (Exception e) {
                    // 10/19 품질팀 요청
                    // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
                    // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
                    // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
                    //Ln.e(e);
                    // 방송 파싱에 문제가 있을 때에 mTxtRemainTime 텍스트 뷰를 보여주지 않는다.
                    mTxtRemainTime.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onEventMainThread(Events.TimerEvent event) {
        if (isMp4) {
            updateRemainMp4Time();
        }
        else {
            updateRemainTime();
        }
    }

    public void setListener(OnLiveTimeRefreshListener listener) {
        mListener = listener;
    }

    public interface OnLiveTimeRefreshListener {
        public void onTimeRefresh(String remainTime);

        public void onBroadCastFinished();
    }
}
