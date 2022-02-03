/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.schedule.viewholder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.gsshop.mocha.core.exception.ApplyExceptionHandler;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.shop.schedule.model.SchedulePrd;

/**
 * 편성정보 없는 경우 노출할 뷰
 */
public class TLineNoDataViewHolder extends TLineBaseViewHolder {

	private final TextView textLiveBenefitLText;
	private final TextView textLiveBenefitRText;

	/**
	 * @param itemView
	 */
	public TLineNoDataViewHolder(View itemView) {
		super(itemView);
		textLiveBenefitLText = (TextView) itemView.findViewById(R.id.text_livebenefit_ltext);
		textLiveBenefitRText = (TextView) itemView.findViewById(R.id.text_livebenefit_rtext);
	}

	/*
	 * bind
	 */
	@Override
	public void onBindViewHolder(final Context context, int position, final SchedulePrd data) {
		textLiveBenefitLText.setVisibility(View.INVISIBLE);
		textLiveBenefitRText.setVisibility(View.INVISIBLE);

		if (data == null) {
			return;
		}

		if (!TextUtils.isEmpty(data.liveBenefitLText)) {
			textLiveBenefitLText.setText(data.liveBenefitLText);
			textLiveBenefitLText.setVisibility(View.VISIBLE);
		}
		if (!TextUtils.isEmpty(data.liveBenefitRText)) {
			textLiveBenefitRText.setText(data.liveBenefitRText);
			textLiveBenefitRText.setVisibility(View.VISIBLE);
		}
	}
}
