/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.renewal.flexible;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apptimize.Apptimize;

import org.json.JSONException;
import org.json.JSONObject;

import gsshop.mobile.v2.ApptimizeExpManager;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.TvLiveBanner;
import gsshop.mobile.v2.home.shop.schedule.model.Product;
import gsshop.mobile.v2.intro.ApptimizeCommand;
import gsshop.mobile.v2.support.gtm.AMPAction;
import gsshop.mobile.v2.support.gtm.AMPEnum;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;

import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;

/**
 * 편성표 부상품 V2
 */
public class RenewalTLineSubViewHolder extends RenewalPriceInfoBottomViewHolder {

	/**
	 * 부상품섬네일과 가격 전체영역
	 */
	private final View prdView;

	/**
	 * 부상품 섬네일
	 */
	private final ImageView prdImage;

	/**
	 * 방송중구매가능, 일시품절 표시영역
	 */
	private final TextView prdComment;

	/**
	 * 상품이미지 우하단 재생버튼 (생방이 아닌 경우만 노출)
	 */
	protected ImageView imgPlay;

	//방송알림
	private final LinearLayout alarmLay;
	private final View alarmOff;
	private final View alarmOn;

	private View itemView;

	private TextView text_brd_time;

	private LinearLayout txt_review_area;
	/**
	 * @param view
	 */
	public RenewalTLineSubViewHolder(View view) {
		super(view);

		this.itemView = view;

		prdView = itemView.findViewById(R.id.view_prd);
		prdImage = itemView.findViewById(R.id.image_prd);
		prdComment = itemView.findViewById(R.id.txt_comment);
		imgPlay = itemView.findViewById(R.id.img_play);

		//방송알림
		alarmLay = itemView.findViewById(R.id.lay_alarm);
		alarmOff = itemView.findViewById(R.id.alarm_off);
		alarmOn = itemView.findViewById(R.id.alarm_on);

		text_brd_time = itemView.findViewById(R.id.text_brd_time);

		txt_review_area = itemView.findViewById(R.id.txt_review_area);

		//
		//DisplayUtils.resizeAtViewToScreen(MainApplication.getAppContext(), prdImage);
		//DisplayUtils.resizeWidthAtViewToScreen(MainApplication.getAppContext(), prdComment);
	}

	public View onBindViewHolder(final Context context, int position, final Product data, final String pgmLiveYn) {
        this.context = context;

		//상품 이미지
		ImageUtil.loadImage(context, data.subPrdImgUrl, prdImage, R.drawable.noimage_166_166);

		//방송중구매가능, 일시품절 표시
		//구매하기 버튼이 노출되는 경우는 버튼에, 아닌 경우는 섬네일 하단에 표시
		if (prdComment != null) {
			prdComment.setVisibility(View.GONE);
			if (data.directOrdInfo == null) {
				prdComment.setVisibility(View.VISIBLE);
				if (AIR_BUY.equalsIgnoreCase(data.imageLayerFlag)) {
					prdComment.setText(R.string.layer_flag_air_buy);
				} else if (SOLD_OUT.equalsIgnoreCase(data.imageLayerFlag)) {
					prdComment.setText(R.string.layer_flag_sold_out);
				}
			}
		}

		//가격표시용 공통모듈에 맞게 데이타 변경
		TvLiveBanner tvLiveBanner = new TvLiveBanner();
		tvLiveBanner.rProductName = data.exposPrdName;
		tvLiveBanner.rSalePrice = data.broadPrice;
		tvLiveBanner.rBasePrice = data.salePrice;
		tvLiveBanner.rExposePriceText = data.exposePriceText;
		tvLiveBanner.rDiscountRate = data.priceMarkUp;				//숫자 입니다
		tvLiveBanner.rLinkUrl = data.linkUrl;


		//렌탈관련
		//방송 구조체에는 있습니다 렌탈 프라이스가 렌탈 상품 관
		tvLiveBanner.rProductType = "Y".equals(data.rentalYn) ? "R" : "P";
		tvLiveBanner.rRentalText = data.rentalText;
		tvLiveBanner.rentalPrice = data.rentalPrice;

		//바로구매 여부 확인
		//편성표에서 공통
/*		if (isNotEmpty(data.directOrdInfo)) {
			tvLiveBanner.rDirectOrdInfo = new DirectOrdInfo();
			tvLiveBanner.rDirectOrdInfo.text = data.directOrdInfo.text;
			tvLiveBanner.rDirectOrdInfo.linkUrl = data.directOrdInfo.linkUrl;
		}*/

		//라이브톡 여부 확인
		//편성표에서 공통
/*		if (isNotEmpty(data.liveTalkInfo)) {
			tvLiveBanner.rLiveTalkYn = "Y";
			tvLiveBanner.rLiveTalkText = data.liveTalkInfo.text;
			tvLiveBanner.rLiveTalkUrl = data.liveTalkInfo.linkUrl;
		}*/

		//방송알림 여부 확인
		//편성표에서 공통
/*
		tvLiveBanner.rBroadAlarmDoneYn = data.broadAlarmDoneYn;     //방송 알림 여부
		tvLiveBanner.rExposPrdName = data.exposPrdName;			    //방송 알림에 필요
*/


		//렌탈 상품 관련
		//방송 구조체에는 있습니다 렌탈 프라이스가 렌탈 상품 관
		tvLiveBanner.rRentalText = data.rentalText;
		tvLiveBanner.rRentalPrice = data.rentalPrice;
		//편성표는 보험 어찌 처리한대???
		tvLiveBanner.rProductType = "Y".equals(data.insuYn) ? "I" : "P";
		tvLiveBanner.rProductType = "Y".equals(data.rentalYn) ? "R" : tvLiveBanner.rProductType;

		//혜택 관련
		tvLiveBanner.rAllBenefit = data.allBenefit;
		tvLiveBanner.rSource = data.source;

		//솔드 아웃 관련 방송중 구매가능관련
		//생방송에 현재는 없다. 하지만 편성표에는 있다.
		tvLiveBanner.rImageLayerFlag = data.imageLayerFlag;

		//상품평 관련
		tvLiveBanner.rAddTextLeft = data.addTextLeft;
		tvLiveBanner.rAddTextRight = data.addTextRight;

		//브렌드 관련
		tvLiveBanner.rBrandNm = data.brandNm;

		// 딜 여부 편성표는 무조건 상품
		tvLiveBanner.deal = "false";

		super.bindViewHolder(tvLiveBanner, position,null);

/*		// 재생버튼 클릭시 상세화면 이동 (자동재생 플래그 추가)
		if (isNotEmpty(data.linkUrl)) {
			imgPlay.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String linkUrl = StringUtils.replaceUriParameter(Uri.parse(data.linkUrl),
							Keys.PARAM.VOD_PLAY, "Y").toString();
					WebUtils.goWeb(context, linkUrl);
				}
			});
		}*/


		//부상품 전체영역에 대한 링크
		if (isNotEmpty(data.linkUrl)) {
			prdView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					WebUtils.goWeb(context, data.linkUrl);

					// TV편성표 AB테스트 주상품 효율
					// AB대상이면서 O타입, AB대상이면서 A타입만 해당되도록
					if( ApptimizeCommand.ABINFO_VALUE.contains(ApptimizeExpManager.SCHEDULE)){
						//앰플리튜드를 위한 코드
						try {
							JSONObject eventProperties = new JSONObject();
							eventProperties.put(AMPEnum.AMP_ACTION_NAME, AMPEnum.AMP_CLICK_SCHEDULE_PRD);

							if(data != null) {
								eventProperties.put(AMPEnum.AMP_PRD_CODE, data.prdId);
								eventProperties.put(AMPEnum.AMP_PRD_NAME, data.prdName);
								eventProperties.put(AMPEnum.AMP_AB_DETAIL_TYPE, ApptimizeExpManager.SUB_PRD);
								eventProperties.put(AMPEnum.AMP_AB_INFO, ApptimizeCommand.ABINFO_VALUE);
							}

							AMPAction.sendAmpEventProperties(AMPEnum.AMP_CLICK_SCHEDULE_PRD,eventProperties);
						} catch (JSONException exception){

						}
						//앱티마이즈를 위한 코드
						Apptimize.track(AMPEnum.AMP_CLICK_SCHEDULE_PRD);
					}
				}
			});
		}


		//버튼 세개 다 없으면 버튼 감싸는 레이아웃 GONE
		if (lay_live_talk.getVisibility() == View.GONE && lay_alarm.getVisibility() == View.GONE && txt_buy.getVisibility() == View.GONE) {
			lay_button_container.setVisibility(View.GONE);
		}

		text_brd_time.setVisibility(View.GONE);
		if(data.broadTimeText != null && DisplayUtils.isValidString(data.broadTimeText)){
			text_brd_time.setText(data.broadTimeText);
			text_brd_time.setVisibility(View.VISIBLE);
		}


		return itemView;
	}
}
