/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.gssuper;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.gsshop.mocha.core.exception.ApplyExceptionHandler;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.ViewHolderType;

/**
 * 띠 이미지 배너.
 *
 */
public class BanImgGsfGbaVH extends BaseViewHolder {

	/**
	 * 로고 이미지
	 */
	private ImageView img_logo;

	/**
	 * @param itemView
	 */
	public BanImgGsfGbaVH(View itemView) {
		super(itemView);
		img_logo = itemView.findViewById(R.id.img_logo);
	}

	/**
	 * bind
	 */
	@Override
	public void onBindViewHolder(final Context context, int position, ShopInfo info,
								 final String action, final String label, String sectionName) {
		ShopInfo.ShopItem item = info.contents.get(position);

		if (item.type == ViewHolderType.VIEW_TYPE_BAN_IMG_GSF_GBD) {
			//택배배송
			img_logo.setImageResource(R.drawable.fresh_logo_01_2_and);
		} else {
			//default 당일배송
			img_logo.setImageResource(R.drawable.fresh_logo_01_and);
		}
	}
}
