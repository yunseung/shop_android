/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gsshop.mocha.core.exception.ApplyExceptionHandler;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.shop.ViewHolderType;

/**
 * 
 *
 */
public class FlexibleShopBrandStickeyTabAdapter extends FlexibleShopAdapter
		implements StickyRecyclerHeadersAdapter<FlexibleBannerBrandTabViewHolder> {

	/**
	 * @param context
	 */
	public FlexibleShopBrandStickeyTabAdapter(Context context) {
		super(context);
	}

	@Override
	public long getHeaderId(int position) {
		switch (getItemViewType(position)) {
		case ViewHolderType.BANNER_TYPE_BAND:
		case ViewHolderType.BANNER_TYPE_BRAND_ROLL_IMAGE:
		case ViewHolderType.VIEW_TYPE_SE:
		case ViewHolderType.BANNER_TYPE_ROLL_ONE_IMAGE:
			return -1;
		default:
			return 0;
		}

	}

	@Override
	public FlexibleBannerBrandTabViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
		// Event Menu banner
		View itemView = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.home_row_type_fx_brand_tabs_banner, parent, false);
		return new FlexibleBannerBrandTabViewHolder(itemView);

	}

	@Override
	public void onBindHeaderViewHolder(FlexibleBannerBrandTabViewHolder holder, int position) {
		holder.onBindViewHolder(getContext(), position, getInfo(), null, null, null);
	}

}
