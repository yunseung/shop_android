/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.schedule.viewholder;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.springframework.http.HttpEntity;

import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.shop.schedule.TVScheduleShopFragment;
import gsshop.mobile.v2.home.shop.schedule.model.Product;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;

import static gsshop.mobile.v2.ServerUrls.WEB.SCH_LIVE_SUBPRD_ALRAM_CANCEL;
import static gsshop.mobile.v2.ServerUrls.WEB.SCH_LIVE_SUBPRD_ALRAM_REG;
import static gsshop.mobile.v2.ServerUrls.WEB.SCH_SUBPRD_ALRAM_CANCEL;
import static gsshop.mobile.v2.ServerUrls.WEB.SCH_SUBPRD_ALRAM_REG;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleShopFragment.makeFormData;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleShopFragment.makeWiselogUrl;
import static gsshop.mobile.v2.home.shop.schedule.viewholder.TLineBaseViewHolder.AIR_BUY;
import static gsshop.mobile.v2.home.shop.schedule.viewholder.TLineBaseViewHolder.SOLD_OUT;


public class TLineSubViewHolder {

	private final View prdView;
	private final ImageView prdImage;
	private final TextView prdComment;

	private final TextView titleText;
	private final TextView priceText;
	private final TextView wonText;

	//방송알림
	private final LinearLayout alarmLay;
	private final View alarmOff;
	private final View alarmOn;

	//구매하기
	protected final Button btn_pay;

	private final View bottomLine;

	private View itemView;

	/**
	 * @param view
	 */
	public TLineSubViewHolder(View view) {
		this.itemView = view;

		prdView = itemView.findViewById(R.id.view_prd);
		prdImage = (ImageView) itemView.findViewById(R.id.image_prd);
		prdComment = (TextView) itemView.findViewById(R.id.txt_comment);
		titleText = (TextView) itemView.findViewById(R.id.text_title);
		priceText = (TextView) itemView.findViewById(R.id.text_price);
		wonText = (TextView) itemView.findViewById(R.id.text_won);

		//방송알림
		alarmLay = (LinearLayout) itemView.findViewById(R.id.lay_alarm);
		alarmOff = (View) itemView.findViewById(R.id.alarm_off);
		alarmOn = (View) itemView.findViewById(R.id.alarm_on);

		//구매하기
		btn_pay = (Button) itemView.findViewById(R.id.btn_pay);

		//하단라인
		bottomLine = (View) itemView.findViewById(R.id.bottom_line);

		DisplayUtils.resizeAtViewToScreen(MainApplication.getAppContext(), prdImage);
		DisplayUtils.resizeWidthAtViewToScreen(MainApplication.getAppContext(), prdComment);
	}

	public View onBindViewHolder(final Context context, int position, final Product data, final String pgmLiveYn) {

		//상품 이미지
		ImageUtil.loadImage(context, data.subPrdImgUrl, prdImage, R.drawable.noimg_list);

		//일시품절
		prdComment.setVisibility(View.VISIBLE);
		if(AIR_BUY.equalsIgnoreCase(data.imageLayerFlag)){
			prdComment.setText(R.string.layer_flag_air_buy);
		}else if(SOLD_OUT.equalsIgnoreCase(data.imageLayerFlag)){
			prdComment.setText(R.string.layer_flag_sold_out);
		}else{
			prdComment.setVisibility(View.GONE);
		}

		//상품명
		if (!TextUtils.isEmpty(data.exposPrdName)) {
			//텍스트가 붙어있는 경우 다음라인으로 표시되는 현상 개선
			titleText.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
				@Override
				public boolean onPreDraw() {
					int textWidth = titleText.getWidth();
					int paddingLeft = titleText.getPaddingLeft();
					int paddingRight = titleText.getPaddingRight();
					if (textWidth > 0) {
						// line break;
						titleText.getViewTreeObserver().removeOnPreDrawListener(this);

						int availableWidth = textWidth - (paddingLeft + paddingRight);
						// first line
						int end = titleText.getPaint().breakText(data.exposPrdName, true, availableWidth, null);
						Spannable wordtoSpan;
						if (end < data.exposPrdName.length()) {
							String firstLine = data.exposPrdName.substring(0, end);
							String nextLine = data.exposPrdName.substring(end);
							// second line
							end = titleText.getPaint().breakText(nextLine, true, availableWidth, null);
							if (end < nextLine.length()) {
								nextLine = nextLine.substring(0, end) + '\n' + nextLine.substring(end);
							}
							wordtoSpan = new SpannableString(firstLine + '\n' + nextLine.trim());
						} else {
							wordtoSpan = new SpannableString(data.exposPrdName);
						}

						titleText.setText(wordtoSpan);
					}
					return true;
				}
			});
		}

		//가격
		if (!TextUtils.isEmpty(data.broadPrice)) {
			priceText.setText(DisplayUtils.getFormattedNumber(data.broadPrice));
		}
		if (!TextUtils.isEmpty(data.exposePriceText)) {
			wonText.setText(data.exposePriceText);
		}

		//서브일때 가격영역은 방송가 가격영역만 존재한다.
		//렌탈상품 일때는 가격영역에 rentalPrice 뿌리고 원 영역은 제거한다
		if ("Y".equals(data.rentalYn)) {
			priceText.setText(data.rentalPrice);
			wonText.setVisibility(View.GONE);
		}

		//방송알림
		alarmLay.setVisibility(View.VISIBLE);
		if ("Y".equals(data.broadAlarmDoneYn)) {
			alarmOff.setVisibility(View.GONE);
			alarmOn.setVisibility(View.VISIBLE);
		} else if ("N".equals(data.broadAlarmDoneYn)) {
			alarmOff.setVisibility(View.VISIBLE);
			alarmOn.setVisibility(View.GONE);
		} else {
			//"Y", "N" 이외 값은 알람영역 숨김
			alarmLay.setVisibility(View.GONE);
		}

		alarmLay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String url = "";
				String type = "";
				String mseqUrl = "";
				HttpEntity<Object> requestEntity = makeFormData(
						data.prdId, data.exposPrdName, null, null);

				if ("Y".equals(data.broadAlarmDoneYn)) {
					//방송알림 취소
					url = ServerUrls.getHttpRoot() + ServerUrls.REST.TV_SCHEDULE_ALARM_DELETE;
					type = "delete";
					//방송 알림 취소 효율 코드
					mseqUrl = "Y".equals(pgmLiveYn) ? makeWiselogUrl(SCH_LIVE_SUBPRD_ALRAM_CANCEL) : makeWiselogUrl(SCH_SUBPRD_ALRAM_CANCEL);
				} else if ("N".equals(data.broadAlarmDoneYn)) {
					//방송알림 설정 팝업
					url = ServerUrls.getHttpRoot() + ServerUrls.REST.TV_SCHEDULE_ALARM_QUERY;
					type = "query";
					//방송 알림 등록 효율 코드
					mseqUrl = "Y".equals(pgmLiveYn) ? makeWiselogUrl(SCH_LIVE_SUBPRD_ALRAM_REG) : makeWiselogUrl(SCH_SUBPRD_ALRAM_REG);
				}
				new TVScheduleShopFragment.BroadAlarmUpdateController(context).execute(url, requestEntity, type);
				((HomeActivity) context).setWiseLogHttpClient(mseqUrl);
			}
		});

		// 바로구매
		if (data.directOrdInfo != null) {
			btn_pay.setVisibility(View.VISIBLE);
			if (TextUtils.isEmpty(data.directOrdInfo.text)) {
				//서버에서 텍스트가 안내려 온 경우 디폴트 문구 세팅
				btn_pay.setText(context.getString(R.string.home_tv_live_btn_tv_pay_text));
			} else {
				btn_pay.setText(data.directOrdInfo.text);
			}
			btn_pay.setTag(DisplayUtils.getFullUrl(data.directOrdInfo.linkUrl));
			btn_pay.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					WebUtils.goWeb(context, v.getTag().toString());
				}
			});
		} else {
			btn_pay.setVisibility(View.GONE);
		}

		if (!TextUtils.isEmpty(data.linkUrl)) {
			prdView.setTag(DisplayUtils.getFullUrl(data.linkUrl));
			prdView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					WebUtils.goWeb(context, v.getTag().toString());
				}
			});
		}

		return itemView;
	}
}
