package gsshop.mobile.v2.home.shop.schedule.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import gsshop.mobile.v2.R;

/**
 * tv 편성표 Date View Holder
 */

public class DateViewHolder extends RecyclerView.ViewHolder {

    public final TextView dayText;
    public final TextView weekText;

    public DateViewHolder(View itemView) {
        super(itemView);
        dayText = (TextView) itemView.findViewById(R.id.txt_tv_schedule_date_day);
        weekText = (TextView) itemView.findViewById(R.id.txt_tv_schedule_date_week);
    }
}
