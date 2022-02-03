/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.schedule.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.gsshop.mocha.core.exception.ApplyExceptionHandler;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.shop.schedule.model.SchedulePrd;


public class TLineTextMoreViewHolder extends TLineBaseViewHolder {

	private final TextView titleText;

	/**
	 * @param itemView
	 */
	public TLineTextMoreViewHolder(View itemView) {
		super(itemView);
		titleText = (TextView) itemView.findViewById(R.id.text_title);
	}

	/*
	 * bind
	 */
	@Override
	public void onBindViewHolder(final Context context, int position, final SchedulePrd data) {
		String title = data.broadLink.broadLeftText;
		titleText.setText(title);
	}
}
