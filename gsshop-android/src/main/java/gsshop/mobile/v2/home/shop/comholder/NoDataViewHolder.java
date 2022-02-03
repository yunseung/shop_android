/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.comholder;

import android.content.Context;
import android.view.View;

import java.util.List;

import gsshop.mobile.v2.home.main.ModuleList;
import gsshop.mobile.v2.home.shop.BaseViewHolderV2;

/**
 * 데이타 없음 표시용
 *
 */
public class NoDataViewHolder extends BaseViewHolderV2 {

	/**
	 * @param itemView
	 */
	public NoDataViewHolder(View itemView) {
		super(itemView);
	}

	@Override
	public void onBindViewHolder(final Context context, int position, List<ModuleList> contents) {

	}

}
