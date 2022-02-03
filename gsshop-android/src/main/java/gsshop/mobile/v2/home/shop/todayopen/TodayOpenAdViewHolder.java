/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.todayopen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gsshop.mocha.ui.util.ViewUtils;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;

/**
 * 오늘오픈 ad
 *
 */
@SuppressLint("NewApi")
public class TodayOpenAdViewHolder extends BaseViewHolder {
	//	private final LinearLayout root;
	private final TextView text_title;
	private final TextView text_count;
	private final ImageView ad_image;
	private final View view_click;

	/**
	 * @param itemView
	 */
	public TodayOpenAdViewHolder(View itemView) {
		super(itemView);
		text_title = (TextView) itemView.findViewById(R.id.text_title);
		text_count = (TextView) itemView.findViewById(R.id.text_count);
		ad_image = (ImageView) itemView.findViewById(R.id.ad_image);
		view_click = itemView.findViewById(R.id.view_click);

		// Text UI 삭제 20190122
        ViewUtils.hideViews(text_title);
		ViewUtils.hideViews(text_count);
	}

	@Override
	public void onBindViewHolder(final Context context, int position, ShopInfo info,
								 final String action, final String label, String sectionName) {

		SectionContentList item = info.contents.get(position).sectionContent;
        // Text UI 삭제 20190122
		if(!TextUtils.isEmpty(item.productName)) {
			text_count.setVisibility(View.VISIBLE);
			text_count.setText(item.productName);
		}
		if(!TextUtils.isEmpty(item.promotionName)) {
			text_title.setVisibility(View.VISIBLE);
			text_title.setText(item.promotionName);
		}

		/* 20190514 클릭시 아무 동작 하지 않도록 수정
		view_click.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int[] location = new int[2];
				ad_image.getLocationOnScreen(location);
				RectF rect = new RectF(location[0], location[1], location[0] + ad_image.getMeasuredWidth(), location[1] + ad_image.getMeasuredHeight());
				EventBus.getDefault().post(new Events.FlexibleEvent.AdTooltipEvent(rect,true));
			}
		});
		*/
	}

    @Override
    public void onViewAttachedToWindow() {
        super.onViewAttachedToWindow();
    }

    @Override
    public void onViewDetachedFromWindow() {
        super.onViewDetachedFromWindow();

        EventBus.getDefault().post(new Events.FlexibleEvent.AdTooltipEvent(null,false));
    }
}