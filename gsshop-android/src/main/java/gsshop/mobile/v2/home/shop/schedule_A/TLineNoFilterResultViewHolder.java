/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.schedule_A;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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
 * 편성정보 없는 경우 노출할 뷰
 */
public class TLineNoFilterResultViewHolder extends TLineBaseViewHolder {

	private TextView txtDate; // 날짜
	private TextView txtDay; // 요일
	private TextView frontWave; // 앞 물결(ex. ~ 10.25 월)
	private TextView backWave; // 뒤 물결(ex. 10.25 월 ~)
	private SchedulePrd dateData;
	private LinearLayout layNoResultDate;

	/**
	 * @param itemView
	 */
	public TLineNoFilterResultViewHolder(View itemView) {
		super(itemView);
		txtDate = (TextView) itemView.findViewById(R.id.txt_date_no_result);
		txtDay = (TextView) itemView.findViewById(R.id.txt_day_no_result);
		frontWave = (TextView) itemView.findViewById(R.id.front_wave);
		backWave = (TextView) itemView.findViewById(R.id.back_wave);
		layNoResultDate = (LinearLayout)itemView.findViewById(R.id.lay_no_result_date);
	}

	/*
	 * bind
	 */
	@Override
	public void onBindViewHolder(final Context context, int position, final SchedulePrd data) {
		dateData = data;
		frontWave.setVisibility(View.GONE);
		backWave.setVisibility(View.GONE);

		// 조건에 맞는 상품 없습니다 날짜 표시 (ex. 10.25 월)
		if(DisplayUtils.isValidString(data.month) && DisplayUtils.isValidString(data.day) && DisplayUtils.isValidString(data.week)){
			txtDate.setText(data.month + "." + data.day + " ");
			txtDay.setText(data.week);
			layNoResultDate.setVisibility(View.VISIBLE);
		}else{
			//20일치 전체기간중 상품 0개인경우 or 실제 data값이 잘못내려온경우
			layNoResultDate.setVisibility(View.INVISIBLE);
		}

		//~이전없음 앞에 물결표시
		if(data.preNoResult){
			frontWave.setVisibility(View.VISIBLE);
		}else{
			frontWave.setVisibility(View.GONE);
		}

		//이후~없음 뒤에 물결표시
		if(data.nextNoResult){
			backWave.setVisibility(View.VISIBLE);
		}else{
			backWave.setVisibility(View.GONE);
		}
	}

	public SchedulePrd getData(){
		return dateData == null ? null : dateData;
	}
}
