/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.nalbang;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import activitytransition.ActivityTransitionLauncher;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.menu.TabMenu;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;


/**
 * 숏방 타이틀 배너(이미지가 1개인 경우만 사용)
 *
 */
public class FlexibleBannerShortbangBannerViewHolder extends BaseViewHolder {

	private final ImageView mainImg;
	private final TextView text_title;
	private final LinearLayout image_layout;
	private final View view_video;
	private final ImageView titleImage;
	/**
	 * @param itemView
	 */
	public FlexibleBannerShortbangBannerViewHolder(View itemView) {
		super(itemView);
		image_layout = (LinearLayout) itemView.findViewById(R.id.image_layout);
		mainImg = (ImageView) itemView.findViewById(R.id.main_img);
		text_title = (TextView) itemView.findViewById(R.id.text_title);
		view_video = itemView.findViewById(R.id.view_video);
		titleImage = (ImageView) itemView.findViewById(R.id.image_title);

		DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), titleImage);
		DisplayUtils.resizeAtViewToScreen(MainApplication.getAppContext(), mainImg);
		DisplayUtils.resizeAtViewToScreen(MainApplication.getAppContext(), image_layout);

	}

	/* bind */
	@Override
	public void onBindViewHolder(final Context context, int position, final ShopInfo info,String action, String label, String sectionName ) {
		final SectionContentList item = info.contents.get(position).sectionContent;

		//단품에서 플레이 버튼 제거
		view_video.setVisibility(View.GONE);

		// main image
		ImageUtil.loadImageFit(context, item.imageUrl, titleImage, R.drawable.noimg_logo);

		if(item.subProductList != null && !item.subProductList.isEmpty()) {
			ImageUtil.loadImage(context, item.subProductList.get(0).imageUrl, mainImg, R.drawable.noimg_tv);

			text_title.setText(item.subProductList.get(0).promotionName);

			mainImg.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
						new CustomOneButtonDialog((HomeActivity)context).message(R.string.shortbang_version_old)
								.cancelable(false)
								.buttonClick(new CustomOneButtonDialog.ButtonClickListener() {
									@Override
									public void onClick(Dialog dialog) {
										dialog.dismiss();
									}
								}).show();
						return;
					}

					final Intent intent = new Intent(context, ShortbangActivity.class);
					intent.putExtra(Keys.INTENT.SHORTBANG_LINK, item.subProductList.get(0).linkUrl);
					intent.putExtra(Keys.INTENT.SHORTBANG_PRELOAD_IMAGE, item.subProductList.get(0).imageUrl);
					intent.putExtra(Keys.INTENT.SHORTBANG_PRODUCT_INDEX, 0);
					intent.putExtra(Keys.INTENT.WEB_URL, ServerUrls.WEB.CATEGORY);
					intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.CATEGORY);

					Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							ActivityTransitionLauncher.with((HomeActivity)context).from(mainImg,"image")
									.image(ImageUtil.getImageViewBitmap(mainImg)).launch(intent);
						}
					}, 50);

					//와이즈로그 전송
					((HomeActivity) context).setWiseLog(item.subProductList.get(0).wiseLog);
				}
			});
		}
	}

}
