/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.util.CspBannerModel;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.util.StaticCsp;
import gsshop.mobile.v2.web.WebUtils;

/**
 * CSP 이미지 배너.
 *
 * 참고
 * - root를 gone 처리시 영역이 그대로 유지되기 때문에 layout_content를 사용하여 영역을 노출/숨김 처리함
 */
@SuppressLint("NewApi")
public class CspLoginBanImgGbaVH extends BaseViewHolder {

	private Context context;
	private final LinearLayout layout_content;
	private final ImageView main_img;

	private String naviId;
	private CspBannerModel item;

	/**
	 * @param itemView
	 */
	public CspLoginBanImgGbaVH(View itemView) {
		super(itemView);

		layout_content = itemView.findViewById(R.id.layout_content);
		main_img = itemView.findViewById(R.id.main_img);

		item = new CspBannerModel();
	}

	@Override
	public void onBindViewHolder(final Context context, int position, ShopInfo info,
								 final String action, final String label, String sectionName) {
		super.onBindViewHolder(context, position, info, action, label, sectionName);

		this.context = context;
		this.naviId = info.naviId;

		layout_content.setVisibility(View.GONE);

		//접근성 세팅
		final SectionContentList content = info.contents.get(position).sectionContent;
		if (!TextUtils.isEmpty(content.productName)) {
			main_img.setContentDescription(content.productName);
		}

		setContents(false);
	}

	/**
	 * 화면에 노출할 컨텐츠를 세팅한다.
	 *
	 * @param isFromEvent if true, 이벤트를 통해 호출된 경우
	 */
	private void setContents(boolean isFromEvent) {
		//네비게이션 아이디가 일치하지 않거나 이미지 주소가 없으면 숨김처리
		if (!naviId.equals(item.P) || TextUtils.isEmpty(item.I)) {
			layout_content.setVisibility(View.GONE);
			return;
		}

		//노출에 대한 효율측정은 이벤트 수신 시 1회만
		//상하 스크롤하면서 노출/숨김 반복되는 경우는 측정 안함 (bind 함수 통하면 측정안함)
		if (isFromEvent) {
			if (StaticCsp.getInstance() != null && !TextUtils.isEmpty(item.AID)) {
				StaticCsp.getInstance().setEmitActivity(item.AID, "V");
			}
		}

		layout_content.setVisibility(View.VISIBLE);
		ImageUtil.loadImageResize(context, item.I, main_img, R.drawable.noimg_logo);
		main_img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!TextUtils.isEmpty(item.LN)) {
					WebUtils.goWeb(context, item.LN);
					//효율측정
					if (StaticCsp.getInstance() != null && !TextUtils.isEmpty(item.AID)) {
						StaticCsp.getInstance().setEmitActivity(item.AID, "C");
					}
				}
			}
		});
	}

	/**
	 * CSP 배너 이벤트 수신
	 *
	 * @param event CspEvent
	 */
	public void onEvent(final Events.CspEvent event) {
		//스티키이벤트 제거
		Events.CspEvent stickyEvent = EventBus.getDefault().getStickyEvent(Events.CspEvent.class);
		if (stickyEvent != null) {
			EventBus.getDefault().removeStickyEvent(stickyEvent);
		}

		item.P = event.naviId;
		item.I = event.imageUrl;
		item.LN = event.linkUrl;
		item.AID = event.aid;

		setContents(true);
	}

	/**
	 * 뷰가 화면에 노출될때 발생
	 */
	@Override
	public void onViewAttachedToWindow() {
		EventBus.getDefault().registerSticky(this);
	}

	/**
	 * 뷰가 화면에서 사라질때 발생
	 */
	@Override
	public void onViewDetachedFromWindow() {
		EventBus.getDefault().unregister(this);
	}
}
