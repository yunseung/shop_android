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
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.user.User;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.util.StringUtils;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.util.Ln;

/**
 * 텍스트 배너.
 *
 */
public class BanTxtImgColorGbaVH extends BaseViewHolder {
	final View root;
	final TextView text_reward;
	final ImageView reward_image;

	SpannableStringBuilder priceStringBuilder = new SpannableStringBuilder();
	ForegroundColorSpan discountColorSpan;
	private static final StyleSpan boldSpan =  new StyleSpan(Typeface.BOLD);
	/**
	 * @param itemView
	 */
	public BanTxtImgColorGbaVH(View itemView) {
		super(itemView);
		root = itemView.findViewById(R.id.root);
		text_reward = (TextView) itemView.findViewById(R.id.text_reward);
		reward_image = (ImageView)itemView.findViewById(R.id.reward_image);
	}

	/* bind */
	@Override
	public void onBindViewHolder(final Context context, int position, ShopInfo info,String action, String label, String sectionName ) {
		final SectionContentList item = info.contents.get(position).sectionContent;
		//productName 정보가 없으면 화면 표시안함 ("API_" 뷰타입의 경우는 본 셀을 제거함)
		if (TextUtils.isEmpty(item.productName)) {
			root.setVisibility(View.GONE);
			return;
		}

		ImageUtil.loadImage(context, item.imageUrl, reward_image, R.drawable.ic_coin_android);

		priceStringBuilder.clear();
		User user = User.getCachedUser();
		if (user != null) {
			priceStringBuilder.append(user.getUserName());
		}
		priceStringBuilder.append(item.productName ).append(" ");

		int startLength = priceStringBuilder.length();
		priceStringBuilder.append(item.salePrice);
		priceStringBuilder.append(item.exposePriceText).append(" ");
		priceStringBuilder.append(item.valueText);
		try {
			if(item.etcText1 != null && StringUtils.checkHexColor("#" + item.etcText1)) {
				discountColorSpan = new ForegroundColorSpan(Color.parseColor("#" + item.etcText1));
				priceStringBuilder.setSpan(discountColorSpan, startLength, startLength + item.salePrice.length() ,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			priceStringBuilder.setSpan(boldSpan, startLength, startLength + item.salePrice.length() ,
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		}catch(Exception e){
			Ln.e(e);
		}
		text_reward.setText(priceStringBuilder);

		if(!URLUtil.isValidUrl(item.linkUrl)){
			text_reward.setCompoundDrawables(null,null,null,null);
		}


		root.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				WebUtils.goWeb(context, item.linkUrl, ((Activity) context).getIntent());
				// GTM AB Test 클릭이벤트 전달
//				GTMAction.sendEvent(context, GTMEnum.GTM_AREA_CATEGORY, action, label);
			}
		});

	}

}
