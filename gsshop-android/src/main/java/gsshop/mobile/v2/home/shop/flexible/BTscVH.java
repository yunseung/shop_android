/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gsshop.mocha.core.exception.ApplyExceptionHandler;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.renewal.views.CommonTitleLayout;

/**
 * 타이틀 배너.
 *
 */
public class BTscVH extends BaseViewHolder {

	private final LinearLayout mRoot;
	private final CommonTitleLayout mCommonTitleLayout;

	/**
	 * @param itemView
	 */
	public BTscVH(View itemView) {
		super(itemView);
		mRoot = (LinearLayout) itemView.findViewById(R.id.root);
		mCommonTitleLayout = itemView.findViewById(R.id.common_title_layout);
	}

	/* bind */
	@Override
	public void onBindViewHolder(final Context context, int position, ShopInfo info, String action, String label, String sectionName ) {
		mRoot.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
		if ("B_TSC".equals(info.contents.get(position).sectionContent.viewType)) {
			mRoot.setBackgroundColor(context.getResources().getColor(android.R.color.white));
		}

		mCommonTitleLayout.setCommonTitle(this, info.contents.get(position).sectionContent);

	}

}
