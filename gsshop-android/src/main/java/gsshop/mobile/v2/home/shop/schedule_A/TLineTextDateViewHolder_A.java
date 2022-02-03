/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.schedule_A;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.shop.schedule.model.SchedulePrd;
import gsshop.mobile.v2.home.shop.schedule.viewholder.TLineBaseViewHolder;
import gsshop.mobile.v2.util.DisplayUtils;

/**
 * SCH_BAN_MUT_DATE 뷰타입(편성표 날짜)의 뷰홀더
 */
public class TLineTextDateViewHolder_A extends TLineBaseViewHolder {

	private final TextView txtDate;
	private SchedulePrd dateData;
	private FrameLayout layDate;

	/**
	 * @param itemView
	 */
	public TLineTextDateViewHolder_A(View itemView) {
		super(itemView);
		txtDate = (TextView) itemView.findViewById(R.id.txt_date);
		layDate = (FrameLayout)itemView.findViewById(R.id.lay_date);
	}

	/*
	 * bind
	 */
	@Override
	public void onBindViewHolder(final Context context, int position, final SchedulePrd data) {
		dateData = data;
		// 날짜뷰타입 날짜 표시 (ex. 10.25 월)
		if(DisplayUtils.isValidString(data.month) && DisplayUtils.isValidString(data.day) && DisplayUtils.isValidString(data.week)){
			txtDate.setText(data.month + "." + data.day + " " + data.week);
		}
	}

	public SchedulePrd getData(){
		return dateData == null ? null : dateData;
	}
}
