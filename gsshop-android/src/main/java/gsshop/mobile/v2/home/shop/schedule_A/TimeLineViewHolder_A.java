package gsshop.mobile.v2.home.shop.schedule_A;

import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gsshop.mocha.ui.util.ViewUtils;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.shop.schedule.model.TVScheduleModel;
import gsshop.mobile.v2.home.shop.tvshoping.TvLiveTimeLayout;
import gsshop.mobile.v2.util.DisplayUtils;

/**
 * TimeLine View Holder classes
 */

public abstract class TimeLineViewHolder_A {

    /**
     * base time line view holder
     */
    public static abstract class TimeLineBaseViewHolder extends RecyclerView.ViewHolder {

        public TimeLineBaseViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void bindViewHolder(TVScheduleModel.ScheduleTimeLineThumb thumb, boolean selected, boolean onAirVisible);

        public void onViewAttachedToWindow() {
        }

        public void onViewDetachedFromWindow() {
        }
    }

    /**
     * thumbnail time line view  holder
     */
    public static class TimeLineThumbViewHolder extends TimeLineBaseViewHolder {
        public final View selectionView;
        public final TextView timeText;
        public final TextView titleText;
        public final TextView onAirLabel;
        public final TvLiveTimeLayout layoutTvLiveProgress;


        public TimeLineThumbViewHolder(View itemView) {
            super(itemView);
            selectionView = itemView.findViewById(R.id.view_tv_schedule_time_onair_selection);
            timeText = (TextView) itemView.findViewById(R.id.txt_remain_time);
            titleText = (TextView) itemView.findViewById(R.id.txt_tv_schedule_time_onair_title);
            layoutTvLiveProgress = (TvLiveTimeLayout) itemView.findViewById(R.id.tv_live_progress);
            onAirLabel = itemView.findViewById(R.id.tv_shedule_time_onair_label);

        }

        @Override
        public void bindViewHolder(TVScheduleModel.ScheduleTimeLineThumb thumb, boolean selected, boolean onAirVisible) {
            ViewUtils.hideViews(onAirLabel);
            if(DisplayUtils.isValidString(thumb.product.brandNm)){
                titleText.setText(thumb.product.brandNm);
                titleText.setVisibility(View.VISIBLE);
            }else{
                titleText.setVisibility(View.GONE);
            }

            timeText.setText(thumb.startTime);
            selectionView.setSelected(selected);


            if(selected){
                //선택됐을때 글씨 색상 변경
                titleText.setTextColor(Color.parseColor("#ee1f60"));
                timeText.setTextColor(Color.parseColor("#ee1f60"));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    selectionView.setElevation(10);
                }

            }else{
                titleText.setTextColor(Color.parseColor("#666666"));
                timeText.setTextColor(Color.parseColor("#666666"));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    selectionView.setElevation(0);
                }
            }
        }

    }

    /**
     * on air time line view  holder
     */
    public static class TimeLineOnAirViewHolder extends TimeLineThumbViewHolder {

        public TimeLineOnAirViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bindViewHolder(TVScheduleModel.ScheduleTimeLineThumb thumb, boolean selected, boolean onAirVisible) {
            super.bindViewHolder(thumb, selected, onAirVisible);

            //라이브일때
            if(onAirVisible) {
                ViewUtils.showViews(onAirLabel);
                ViewUtils.hideViews(timeText);
                titleText.setTextColor(Color.parseColor("#ee1f60"));
                selectionView.setSelected(true); //라이브일때는 선택된것처럼 흰색이어야 하므로
            } else {
                ViewUtils.showViews(timeText);
                ViewUtils.hideViews(onAirLabel);
                titleText.setTextColor(Color.parseColor("#666666"));
                selectionView.setSelected(false);
            }
        }
    }

    /**
     * date time line view  holder
     */
    public static class TimeLineDateViewHolder extends TimeLineBaseViewHolder {
        private final TextView dateText;

        public TimeLineDateViewHolder(View itemView) {
            super(itemView);
            this.dateText = (TextView) itemView.findViewById(R.id.txt_tv_schedule_time_date);
        }

        @Override
        public void bindViewHolder(TVScheduleModel.ScheduleTimeLineThumb thumb, boolean selected, boolean onAirVisible) {
            dateText.setText(thumb.product.prdName);
        }

    }

    /**
     * dummy time line view  holder
     */
    public static class TimeLineLinkViewHolder extends TimeLineBaseViewHolder {
        public final TextView titleText;

        public TimeLineLinkViewHolder(View itemView) {
            super(itemView);
            titleText = (TextView) itemView.findViewById(R.id.txt_tv_schedule_time_link_title);
        }

        @Override
        public void bindViewHolder(TVScheduleModel.ScheduleTimeLineThumb thumb, boolean selected, boolean onAirVisible) {
            titleText.setText(thumb.product.prdName);

        }

    }
}
