/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.schedule.viewholder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.gsshop.mocha.core.exception.ApplyExceptionHandler;

import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.shop.schedule.model.SchedulePrd;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;


/**
 * 이미지 배너.
 *
 */
@SuppressLint("NewApi")
public class TLineImageBannerViewHolder extends TLineBaseViewHolder {
	private final ImageView main_img;

	/**
	 * @param itemView
	 */
	public TLineImageBannerViewHolder(View itemView) {
		super(itemView);
		main_img = (ImageView) itemView.findViewById(R.id.main_img);
		DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), main_img);

	}

	/* 이미지 배너. bind */
	@Override
	public void onBindViewHolder(final Context context, int position, SchedulePrd data) {
		String imgUrl = data.liveBanner.imageUrl;
		ImageUtil.loadImageResize(context, imgUrl, main_img, R.drawable.noimg_logo);


	}


}
