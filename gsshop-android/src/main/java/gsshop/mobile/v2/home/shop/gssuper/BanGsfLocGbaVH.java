/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.gssuper;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog;
import gsshop.mobile.v2.user.User;
import gsshop.mobile.v2.util.CookieUtils;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.web.WebUtils;

import static com.blankj.utilcode.util.EmptyUtils.isEmpty;

/**
 * 배송지 변경 및 배송시간 안내 영역
 *
 */
public class BanGsfLocGbaVH extends BaseViewHolder {

	TextView txt_location;

	ImageView img_location_arrow;

	TextView btn_location_change, btn_location_time;

	private static boolean mPopupAlreadyShows = false;
	/**
	 * @param itemView
	 */
	public BanGsfLocGbaVH(View itemView) {
		super(itemView);

		txt_location = (TextView) itemView.findViewById(R.id.txt_location);
		img_location_arrow = (ImageView) itemView.findViewById(R.id.img_location_arrow);
		btn_location_change = (TextView) itemView.findViewById(R.id.btn_location_change);
		btn_location_time = (TextView) itemView.findViewById(R.id.btn_location_time);

		DisplayUtils.resizeAtViewToScreen(MainApplication.getAppContext(), img_location_arrow);
	}

	/**
	 * bind
	 */
	@Override
	public void onBindViewHolder(final Context context, int position, ShopInfo info,
								 final String action, final String label, String sectionName) {

		final SectionContentList item = info.contents.get(position).sectionContent;
		txt_location.setText(item.productName);
		if (item.promotionName != null) {
			img_location_arrow.setVisibility(View.VISIBLE);
			btn_location_change.setText(R.string.gs_super_location_btn_change);
			btn_location_time.setText(R.string.gs_super_location_btn_available_time);
		}
		else {
			img_location_arrow.setVisibility(View.GONE);
			btn_location_change.setText(R.string.gs_super_location_btn_select);
			btn_location_time.setText(R.string.gs_super_location_btn_description);
		}

		setData(context, item);

		if (item.etcText1 != null && !mPopupAlreadyShows) {
			mPopupAlreadyShows = true;
			new CustomOneButtonDialog((HomeActivity) context)
					.message(item.etcText1)
					.cancelable(true)

					.buttonClick(new CustomOneButtonDialog.ButtonClickListener() {
						@Override
						public void onClick(Dialog dialog) {
							CookieUtils.changeWebviewCookie(context, "gssuper", "");
							dialog.dismiss();
						}
					}).show();
		}
	}

	private void setData(final Context context, final SectionContentList item) {
		if (isEmpty(item.subProductList)) {
			return;
		}

		btn_location_change.setVisibility(View.VISIBLE);
		if (item.subProductList.size() == 1) {
			// subProductList 1개만 들어오는 경우 배송시간만 노출.
			btn_location_change.setVisibility(View.GONE);
			btn_location_time.setText(item.subProductList.get(0).productName);
			btn_location_time.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					WebUtils.goWeb(context, item.subProductList.get(0).linkUrl);
				}
			});
		} else {
			btn_location_change.setText(item.subProductList.get(0).productName);
			btn_location_change.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					User user = User.getCachedUser();
					if (user == null || user.customerNumber.length() < 2) {
						Intent intent = new Intent(Keys.ACTION.LOGIN);
						intent.putExtra(Keys.INTENT.WEB_URL, item.subProductList.get(0).linkUrl);
						context.startActivity(intent);
					}
					else {
//						Ln.d("item.subProductList.get(0).prdUrl : " + strLinkUrl);
//						CookieUtils.showWebviewCookies(context, "쿠키 비교 1 !!!");
						WebUtils.goWeb(context, item.subProductList.get(0).linkUrl);
					}
				}
			});

			btn_location_time.setText(item.subProductList.get(1).productName);
			btn_location_time.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					WebUtils.goWeb(context, item.subProductList.get(1).linkUrl);
				}
			});
		}
	}
}
