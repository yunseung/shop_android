package gsshop.mobile.v2.home.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.TimeRemaining;
import roboguice.RoboGuice;
import roboguice.util.Ln;

/**
 * TV생방송 layout 처리(progress bar, 남은 시간 등)
 *
 */
public class TvLiveProgressLayout extends LinearLayout {
	private static final String TAG = "TvLiveProgressLayout";
	private static final long DELAY_MILLIS = 1000; // timer interval
	private static final String TV_LIVE_BROAD_TIME_FORMAT = "yyyyMMddHHmmss";

	private TextView mTxtTvLive;
	private TextView mTxtRemainTime;
	private ProgressBar mProgressBar;

	private long startTimeL = 0L;
	private long endTimeL = 0L;

	private Context mContext;

	// 방송종료 시점을 알기 위한 listener
	private OnTvLiveFinishedListener mOnTvLiveFinishedListener;

	// 방송 종료 5분전에 "주문돌파"로 판매수량 텍스트를 바꿈(한 번만 체크)
	private static final long TV_LIVE_ORDER_QUANTITY_EMPHASIZED_MINUTE = 5;
	private boolean mOrderQuantityEmphasized;
	private OnTvLiveEmphasizedTimeListener mOnTvLiveEmphasizedTimeListener;
	private long mEmphasizeTimedMills; // 5분의 milliseconds 값
	private static TimerTask mTask = null;

	private String endDate;

	public TvLiveProgressLayout(Context context, AttributeSet attrs, int defStyle) {
		this(context, attrs);
	}

	public TvLiveProgressLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public TvLiveProgressLayout(Context context) {
		this(context, null);
	}

	private void init(Context context) {
		RoboGuice.getInjector(context).injectMembers(this);
		mContext = context;

		if (!isInEditMode()) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final View view = inflater.inflate(R.layout.home_tv_live_layout, this, true);

			mTxtTvLive = (TextView) view.findViewById(R.id.txt_tv_live);
			mTxtRemainTime = (TextView) view.findViewById(R.id.txt_remain_time);
			mProgressBar = (ProgressBar) view.findViewById(R.id.prgressbar);
		}
		mOrderQuantityEmphasized = false;
		mEmphasizeTimedMills = TimeUnit.MINUTES.toMillis(TV_LIVE_ORDER_QUANTITY_EMPHASIZED_MINUTE);
	}

	/**
	 * 방송 시작 시간과 종료 시간을 설정. 방송중이 아니면 startTime과 endTime이 invaild string으로 온다.
	 *
	 * @param startTime
	 * @param endTime
	 */
	private void initTvLiveTime(String startTime, String endTime) {
		this.endDate = endTime;
		if (DisplayUtils.isValidString(startTime) && DisplayUtils.isValidString(endTime)) {
			SimpleDateFormat sdf = new SimpleDateFormat(TV_LIVE_BROAD_TIME_FORMAT,
					Locale.getDefault());
			try {
				startTimeL = sdf.parse(startTime).getTime();
				endTimeL = sdf.parse(endTime).getTime();
			} catch (ParseException e) {
				Ln.e("[ParseException]" + e.getLocalizedMessage());
			}
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		super.onWindowFocusChanged(hasWindowFocus);
		// activity가 focus를 가지고, tv 영역이 screen에 보이는 상태이면 timer 동작
		if (hasWindowFocus && startTimeL > 0L && endTimeL > 0L && getLocalVisibleRect(new Rect())) {
			//Ln.i("Timer - onWindowFocusChanged runTimer");
			runTimer();
		} else {
			//Ln.i("Timer - onWindowFocusChanged stopTimer");
			// stopTimer();
		}
	}

	/**
	 * 시간 설정하고 화면 update를 위한 타이머를 시작
	 *
	 * @param startTime
	 * @param endTime
	 */
	public void updateTvLiveTime(String broadType, String startTime, String endTime) {
		initTvLiveTime(startTime, endTime);

		mTxtTvLive.setText(broadType); // TV생방송, TV베스트 등
		// activity가 focus를 가지고, tv 영역이 screen에 보이는 상태이면 timer 동작
		if (startTimeL > 0L && endTimeL > 0L && getLocalVisibleRect(new Rect())) {
			runTimer();
		}
	}

	public void runTimer() {

		// timer
		stopTimer();
		mTask = new TimerTask() {

			@Override
			public void run() {
				//NOTE Auto-generated method stub
				//Ln.i("Timer runTimer");
				setTvLiveProgessAndTime();

			}
		};

		new Timer().schedule(mTask, DELAY_MILLIS, DELAY_MILLIS);
	}

	public void stopTimer() {

		if (mTask != null) {
			//Ln.i("Timer stopTimer");
			mTask.cancel();
			mTask = null;
		}
	}

	/**
	 * 현재 시간을 체크해서 방송 진행률(progress bar)과 남은 시간을 표시
	 */
	private void setTvLiveProgessAndTime() {
		final long currentTimeL = System.currentTimeMillis();

		if ((currentTimeL >= startTimeL && currentTimeL <= endTimeL)) {
			// 방송 중
			final StringBuilder sb = new StringBuilder();
			sb.append(getRemaintTimeText(currentTimeL)).append(" ")
					.append(mContext.getString(R.string.tv_live_remained));

			((Activity) mContext).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					mTxtRemainTime.setText(sb.toString());
					mProgressBar.setVisibility(View.VISIBLE);
					setProgressBar(currentTimeL);
				}
			});

			// 남은 시간인 5분 이내이면 알림 실행(한 번만)
			if ((endTimeL - currentTimeL) <= mEmphasizeTimedMills && !mOrderQuantityEmphasized) {
				if (mOnTvLiveEmphasizedTimeListener != null) {
					((Activity) mContext).runOnUiThread(new Runnable() {

						@Override
						public void run() {
							mOnTvLiveEmphasizedTimeListener.onTvLiveEmphasizedTime();
						}
					});

					mOrderQuantityEmphasized = true;
				}
			}
		} else {
			// 방송 종료 전후
			((Activity) mContext).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					mTxtRemainTime.setText("");
					mProgressBar.setVisibility(View.INVISIBLE);
				}
			});

			stopTimer();

			if (mOnTvLiveFinishedListener != null) {
				((Activity) mContext).runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mOnTvLiveFinishedListener.onTvLiveFinished();
					}
				});

			}

			mOrderQuantityEmphasized = false;
		}
	}

	/**
	 * 방송이 진행 중일 때, 남은 시간을 가져온다.
	 *
	 * @param currentTimeL
	 * @return
	 */
	private String getRemaintTimeText(long currentTimeL) {
		try{
			TimeRemaining timeRemaining = new TimeRemaining(endDate);
			return timeRemaining.getDisplayTime(TimeRemaining.DisplayType.MINUTE);
		} catch (Exception e) {
			// 10/19 품질팀 요청
			// - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
			// - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
			// - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
			Ln.e(e);
		}
		return  "";
//		long remainTimeL = endTimeL - currentTimeL;
//		int seconds = (int) (remainTimeL / 1000) % 60;
//		int minutes = (int) ((remainTimeL / (1000 * 60)));
//
//		return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
	}

	/**
	 * 방송이 진행 중일 때, 진행률을 progress bar에 표시한다.
	 *
	 * @param currentTimeL
	 */
	private void setProgressBar(long currentTimeL) {
		long playedTimeL = currentTimeL - startTimeL;
		long totalTimeL = endTimeL - startTimeL;

		float playedPercent = (float) playedTimeL / totalTimeL * 100;

		mProgressBar.setProgress((int) playedPercent);
	}

	/**
	 * 현재 상태가 방송중인지 아닌지 체크
	 *
	 * @return
	 */
	public boolean isPlaying() {
		long currentTimeL = System.currentTimeMillis();

		if (currentTimeL >= startTimeL && currentTimeL <= endTimeL) {
			return true;
		} else {
			return false;
		}
	}

	public void setOnTvLiveFinishedListener(OnTvLiveFinishedListener listener) {
		mOnTvLiveFinishedListener = listener;
	}

	/**
	 * 방송이 끝났음을 알려주기 위한 interface
	 */
	public interface OnTvLiveFinishedListener {
		public void onTvLiveFinished();
	}

	public void setOnTvLiveEmphasizedTimeListener(OnTvLiveEmphasizedTimeListener listener) {
		mOnTvLiveEmphasizedTimeListener = listener;
	}

	/**
	 * 방송 끝나기 직전임을 알려주기 위한 interface(현재는 5분 전에 알려줌).
	 */
	public interface OnTvLiveEmphasizedTimeListener {
		public void onTvLiveEmphasizedTime();
	}

}
