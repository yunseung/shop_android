package gsshop.mobile.v2.home.shop.bestdeal;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.util.GlobalTimer;
import gsshop.mobile.v2.util.TimeRemaining;


/**
 * 홈매장 모바일라이브 영역 남은시간 표시용
 *
 */
public class BestdealMLTimeLayout extends LinearLayout {

    private static final String TAG = "BestdealMLTimeLayout";

    private Context mContext;

    private TextView mTxtRemainTimeLive;
    private TextView mTxtRemainTimeLiveP;

    public String endDate;

    // 방송종료 시점을 알기 위한 listener
    private OnTvLiveFinishedListener mOnTvLiveFinishedListener;

    public BestdealMLTimeLayout(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
        this.mContext = context;
    }

    public BestdealMLTimeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public BestdealMLTimeLayout(Context context) {
        this(context, null);
        this.mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTxtRemainTimeLive = (TextView) findViewById(R.id.txt_remain_time_live_play);
        mTxtRemainTimeLiveP = (TextView) findViewById(R.id.txt_remain_time_live_pause);
    }

    /**
     * 방송 시작 시간과 종료 시간을 설정.
     * 방송중이 아니면 startTime과 endTime이 invaild string으로 온다.
     * @param startTime startTime
     * @param endTime endTime
     */
    private void initTvLiveTime(String startTime, String endTime) {
        this.endDate = endTime;
    }

    /**
     * 시간 설정하고 화면 update를 위한 타이머를 시작
     *
     * @param startTime startTime
     * @param endTime endTime
     */
    public void updateTvLiveTime(String startTime, String endTime) {
        if("".equals(startTime) && "".equals(endTime)){
            return;
        }

        initTvLiveTime(startTime, endTime);
        startTimer();
    }

    /**
     * 타이머를 시작한다.
     */
    public void startTimer() {
        try {
            EventBus.getDefault().unregister(this);
            EventBus.getDefault().register(this);
        }catch(Exception e){
            //Ln.e(e);
        }
        GlobalTimer.getInstance().startTimer();
        updateRemainTime();
    }

    /**
     * 타이머를 중지한다.
     */
    public void stopTimer() {
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(Events.TimerEvent event) {
        updateRemainTime();
    }

    /**
     * 현재 시간을 체크해서 남은 시간을 표시한다.
     */
    private void updateRemainTime() {
        ((Activity)mContext).runOnUiThread(() -> {
            try{
                TimeRemaining timeRemaining = new TimeRemaining(endDate);
                if(timeRemaining.isAfterTime()){
                    // 방송 중
                    if (mTxtRemainTimeLive != null) {
                        mTxtRemainTimeLive.setText(timeRemaining.getDisplayTime(TimeRemaining.DisplayType.HOUR));
                    }
                    if (mTxtRemainTimeLiveP != null) {
                        //Log.e("TAG", "updateRemainTime : live - pause");
                        mTxtRemainTimeLiveP.setText(timeRemaining.getDisplayTime(TimeRemaining.DisplayType.HOUR));
                    }
                }else{
                    // 방송 종료 전후
                    if (mTxtRemainTimeLive != null) {
                        mTxtRemainTimeLive.setText(R.string.home_best_deal_view_close_text);
                    }
                    if (mTxtRemainTimeLiveP != null) {
                        mTxtRemainTimeLiveP.setText(R.string.home_best_deal_view_close_text);
                    }
                    if (mOnTvLiveFinishedListener != null) {
                        stopTimer();
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
            }
        });
    }

    /**
     * 생방송종료 콜백용 인터페이스를 세팅한다.
     *
     * @param listener OnTvLiveFinishedListener
     */
    public void setOnTvLiveFinishedListener(OnTvLiveFinishedListener listener) {
        mOnTvLiveFinishedListener = listener;
    }

    /**
     * 생방송종료 콜백용 인터페이스
     */
    public interface OnTvLiveFinishedListener  {
        public void onTvLiveFinished();
    }
}
