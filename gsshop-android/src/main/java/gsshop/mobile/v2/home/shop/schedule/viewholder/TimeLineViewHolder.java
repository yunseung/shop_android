package gsshop.mobile.v2.home.shop.schedule.viewholder;

import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gsshop.mocha.ui.util.ViewUtils;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.shop.schedule.model.TVScheduleModel;
import gsshop.mobile.v2.home.shop.tvshoping.TvLiveTimeLayout;

import static gsshop.mobile.v2.util.StringUtils.trim;

/**
 * TimeLine View Holder classes
 */

public abstract class TimeLineViewHolder {

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
        public final ImageView thumbImage;
        public final View selectionView;
        public final TextView timeText;
        public final TextView titleText;
        public final View onAirLabel;
        public final TvLiveTimeLayout layoutTvLiveProgress;

        public TimeLineThumbViewHolder(View itemView) {
            super(itemView);
            thumbImage = (ImageView) itemView.findViewById(R.id.img_tv_schedule_time_onair_thumb);
            selectionView = itemView.findViewById(R.id.view_tv_schedule_time_onair_selection);
            timeText = (TextView) itemView.findViewById(R.id.txt_remain_time);
            titleText = (TextView) itemView.findViewById(R.id.txt_tv_schedule_time_onair_title);
            layoutTvLiveProgress = (TvLiveTimeLayout) itemView.findViewById(R.id.tv_live_progress);
            onAirLabel = itemView.findViewById(R.id.img_tv_shedule_time_onair_label);
        }

        @Override
        public void bindViewHolder(TVScheduleModel.ScheduleTimeLineThumb thumb, boolean selected, boolean onAirVisible) {
            ViewUtils.hideViews(onAirLabel);
            Glide.with(itemView.getContext()).load(trim(thumb.product.subPrdImgUrl)).diskCacheStrategy(DiskCacheStrategy.NONE).into(thumbImage);
            final String productName = thumb.product.prdName;
            titleText.setText(thumb.product.prdName);
            timeText.setText(thumb.startTime);
            selectionView.setSelected(selected);

            titleText.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    int textWidth = titleText.getWidth();
                    int paddingLeft = titleText.getPaddingLeft();
                    int paddingRight = titleText.getPaddingRight();
                    if (textWidth > 0) {
                        // line break;
                        titleText.getViewTreeObserver().removeOnPreDrawListener(this);

                        int availableWidth = textWidth - (paddingLeft + paddingRight);
                        // first line
                        int end = titleText.getPaint().breakText(productName, true, availableWidth, null);
                        Spannable wordtoSpan;
                        if (end < productName.length()) {
                            String firstLine = productName.substring(0, end);
                            wordtoSpan = new SpannableString(firstLine);
                        } else {
                            wordtoSpan = new SpannableString(productName);
                        }

                        titleText.setText(wordtoSpan);
                    }
                    return true;
                }
            });
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
            if(onAirVisible) {
                ViewUtils.hideViews(timeText);
                ViewUtils.showViews(onAirLabel);
            } else {
                ViewUtils.showViews(timeText);
                ViewUtils.hideViews(onAirLabel);
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
