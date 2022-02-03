package gsshop.mobile.v2.support.ui.counter;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import gsshop.mobile.v2.R;

/**
 * 숫자 카운트.
 */
public class NumberCounter implements OnCounterFinishListener {


    private final Activity activity;
    private final ViewGroup group;
    private final Animation in;
    private final Animation out;

    private final List<DigitCounter> counters;

    private final long durationUnit;


    /**
     * 모든 숫자의 counter가 종료하면 불려짐.
     */

    private OnCounterFinishListener finishListener;

    public void setOnFinishListener(OnCounterFinishListener listener) {
        finishListener = listener;
    }


    @Override
    public void onFinish() {
        boolean finished = true;
        for (DigitCounter counter : counters) {
            if (counter.isRunning()) {
                finished = false;
                break;
            }
        }

        if (finished) {
            if (finishListener != null) {
                finishListener.onFinish();
            }
        }
    }


    public NumberCounter(Activity activity, ViewGroup group) {
        this.activity = activity;
        this.group = group;
        counters = new ArrayList<>();
        in = AnimationUtils.loadAnimation(activity, R.anim.count_up_in);
        out = AnimationUtils.loadAnimation(activity, R.anim.count_up_out);
        durationUnit = (long) (100 * 0.8);
    }


    /**
     * 카운트 갯수 지정.
     *
     * @param number number
     * @return boolean
     */
    public boolean withNumber(long number) {
        try {
            group.removeAllViews();
            counters.clear();
            String format = new DecimalFormat("##,##0").format(number);
            for (int i = 0; i < format.length(); i++) {
                if (format.charAt(i) == ',') {
                    TextView text = (TextView) activity.getLayoutInflater().inflate(R.layout.count_text, null);
                    text.setText(",");
                    group.addView(text);

                } else {
                    TextSwitcher switcher = new TextSwitcher(activity);
                    switcher.setFactory(new ViewSwitcher.ViewFactory() {
                        @Override
                        public View makeView() {
                            // Create a new TextView
                            TextView text = (TextView) activity.getLayoutInflater().inflate(R.layout.count_text, null);
                            text.setText("0");
                            return text;
                        }
                    });

                    switcher.setInAnimation(in);
                    switcher.setOutAnimation(out);
                    int digit = Integer.parseInt("" + format.charAt(i));
                    counters.add(new DigitCounter(switcher, digit, durationUnit, this));
                    group.addView(switcher);
                }
            }
            TextView text = (TextView) activity.getLayoutInflater().inflate(R.layout.count_text, null);
            text.setText(activity.getString(R.string.product_description_sale_tail_text));
            text.setTypeface(Typeface.DEFAULT);
            group.addView(text);
        }catch(Exception e){
            return false;
        }
        return true;
    }

    /**
     * 숫자 counter 시작.
     */
    public void start() {
        for (int i = counters.size() - 1; i >= 0; i--) {
            DigitCounter counter = counters.get(i);
            if (!counter.isRunning()) {
                counter.start();
            }
        }
    }

    /**
     * 숫자 카운트 cancel.
     */
    public void cancel() {
        for (DigitCounter counter : counters) {
            counter.cancel();

        }
    }


    /**
     * digit counter
     */
    private static class DigitCounter {
        private static final int STOPPED = 0;

        private static final int RUNNING = 1;

        private int mPlayingState = STOPPED;
        private final CountDownTimer countDownTimer;

        public DigitCounter(final TextSwitcher switcher, final int toNumber, final long dunit, final OnCounterFinishListener endListener) {

            if (toNumber != 0) {
                countDownTimer = new CountDownTimer((toNumber + 1) * dunit, dunit) {

                    public void onTick(long millisUntilFinished) {

                        long count = millisUntilFinished / dunit;
                        count = 1 + (toNumber - count);

                        switcher.setText("" + count);
                    }

                    public void onFinish() {
                        //폰 종류, 상태에 따라 onTick 호출횟수가 일정하지 않아 최종 노출할 숫자 마지막에 세팅
                        switcher.setText("" + toNumber);
                        mPlayingState = STOPPED;
                        endListener.onFinish();
                    }
                };
            } else {
                countDownTimer = null;
            }
        }

        public boolean isRunning() {
            return (mPlayingState == RUNNING);
        }

        public void start() {
            if (countDownTimer == null) {
                return;
            }

            mPlayingState = RUNNING;
            countDownTimer.start();
        }

        public void cancel() {
            if (countDownTimer == null) {
                return;
            }

            mPlayingState = STOPPED;
            countDownTimer.cancel();
        }
    }

}
