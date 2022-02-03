package gsshop.mobile.v2.mobilelive;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.util.GlobalTimer;
import gsshop.mobile.v2.util.TimeRemaining;
import roboguice.util.Ln;

/**
 * 모바일 라이브 layout 처리(남은 시간 등)
 *
 */
public class MobileLiveTimeLayout extends LinearLayout {
    private static final String TAG = "TvLiveTimeLayout";
    private static final long DELAY_MILLIS = 1000; // timer interval
    private static final String TV_LIVE_BROAD_TIME_FORMAT = "yyyyMMddHHmmss";

    private Context mContext;

    private TextView mTxtRemainTime;    //생방송 남은 시간 (HH:mm:ss)

    public String endDate;

    private long callbackInterval = 1000L;

    // 방송종료 시점을 알기 위한 listener
    private OnTvLiveFinishedListener mOnTvLiveFinishedListener;

    // 시간표시 타입
    TimeRemaining.DisplayType displayType;

    public MobileLiveTimeLayout(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
        this.mContext = context;
    }

    public MobileLiveTimeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public MobileLiveTimeLayout(Context context) {
        this(context, null);
        this.mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTxtRemainTime = (TextView) findViewById(R.id.txt_remain_time);
    }

//    /**
//     * 방송종료 콜백 인터벌타임을 세팅한다.
//     *
//     * @param interval 콜백호출시까지 대기시간(milliseconds)
//     */
//    public void setCallbackInterval(long interval) {
//        this.callbackInterval = interval;
//    }

    /**
     * 종료 시간을 설정.
     * 방송중이 아니면 startTime과 endTime이 invaild string으로 온다.
     * @param endTime endTime
     */
    private void initTvLiveTime(String endTime) {
        this.endDate = endTime;
    }

    /**
     * 시간 설정하고 화면 update를 위한 타이머를 시작
     *
     * @param endTime endTime
     */
    public void updateTvLiveTime(String endTime) {
        if("".equals(endTime) && "".equals(endTime)){
            return;
        }
        initTvLiveTime(endTime);
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
            Ln.e(e);
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
                    String strRmTime = timeRemaining.getDisplayTime(displayType);

                    // 방송 중
                    mTxtRemainTime.setText(strRmTime);

                    String[] tempTime = strRmTime.split(":");
                    if(tempTime.length > 2) {
                        mTxtRemainTime.setContentDescription("남은시간 " + tempTime[0] + "시간" + tempTime[1] + "분" + tempTime[2] + "초");
                    }
                    else if (tempTime.length > 1) {
                        mTxtRemainTime.setContentDescription("남은시간 " + tempTime[0] + "분" + tempTime[1] + "초");
                    }
                    else {
                        mTxtRemainTime.setContentDescription("남은시간 " + strRmTime);
                    }
                }else{
                    // 방송 종료 전후
                    mTxtRemainTime.setText("");
                    mTxtRemainTime.setContentDescription("남은시간 없음");
                    stopTimer();
                    if (mOnTvLiveFinishedListener != null) {
                        new Handler().postDelayed(() -> mOnTvLiveFinishedListener.onTvLiveFinished(), callbackInterval);
                    }
                }
            } catch (Exception e) {
                // 10/19 품질팀 요청
                // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
                // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
                // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
                Ln.e(e);
            }
        });
    }

    /**
     * 방송종료 콜백용 인터페이스를 세팅한다.
     *
     * @param listener OnTvLiveFinishedListener
     */
    public void setOnTvLiveFinishedListener(OnTvLiveFinishedListener listener) {
        mOnTvLiveFinishedListener = listener;
    }

    /**
     * 방송종료 콜백용 인터페이스
     */
    public interface OnTvLiveFinishedListener  {
        public void onTvLiveFinished();
    }

    /**
     * set displayType
     *
     * @param displayType 시간표시형태
     */
    public void setDisplayType(TimeRemaining.DisplayType displayType) {
        this.displayType = displayType;
    }
}
