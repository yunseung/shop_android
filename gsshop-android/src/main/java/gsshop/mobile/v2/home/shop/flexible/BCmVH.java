/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.gsshop.mocha.core.exception.ApplyExceptionHandler;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.web.WebUtils;

/**
 * 텍스트 배너.
 *
 */
public class BCmVH extends BaseViewHolder {
	final View root;
	final TextView text_product;
	/**
	 * @param itemView
	 */
	public BCmVH(View itemView) {
		super(itemView);
		root = itemView.findViewById(R.id.root);
		text_product = (TextView) itemView.findViewById(R.id.text_product);
	}

	/* bind */
	@Override
	public void onBindViewHolder(final Context context, int position, ShopInfo info,String action, String label, String sectionName ) {
		final String productName = info.contents.get(position).sectionContent.productName;
		final String linkUrl = info.contents.get(position).sectionContent.linkUrl;

		text_product.setText(productName);

		root.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				WebUtils.goWeb(context, linkUrl, ((Activity) context).getIntent());
				// GTM AB Test 클릭이벤트 전달
//				GTMAction.sendEvent(context, GTMEnum.GTM_AREA_CATEGORY, action, label);
			}
		});

	}

}
