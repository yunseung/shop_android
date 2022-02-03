/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.ltype;

import android.content.Context;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.home.main.ModuleList;
import gsshop.mobile.v2.home.shop.flexible.BaseRollViewHolder;

/**
 * 롤링 베이스 뷰 홀더
 *
 */
public class BaseRollViewHolderLtype extends BaseRollViewHolder {

	/**
	 * @param itemView
	 */
	public BaseRollViewHolderLtype(View itemView) {
		super(itemView);
	}

	@Override
	public void onEvent(Events.FlexibleEvent.StartRollingNo1BestDealBannerEvent event) {}

	@Override
	public void onBindViewHolder(Context context, int position, List<ModuleList> moduleList) {
		super.onBindViewHolder(context, position, moduleList);

		mContext = context;

		ModuleList content = moduleList.get(position);

		if (content.moduleList == null) {
			return;
		}

		if(content.rollingDelay > 0 && content.moduleList != null && content.moduleList.size() > 1) {
			setAutoRollingStart(content.rollingDelay);
		}
		else{
			setAutoRollingStop();
		}
	}
}
