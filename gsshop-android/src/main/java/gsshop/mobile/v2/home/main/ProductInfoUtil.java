/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.main;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.regex.Pattern;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.util.DisplayUtils;

/**
 * Home{@literal &}그룹매장에서 상품정보를 표시하는데 필요한 공통함수 모음
 *
 */
public class ProductInfoUtil {
	/**
	 * data에 따라 판매수량 관련 문구를 설정.
	 * 판매수량이 "256개 판매중"일 때에는 "256"은 txt_sales_quantity에, "개"는 txt_sales_quantity_str에,
	 * "판매중"은 txt_sales_quantity_sub_str에 표시. 판매수량이 숫자가 아니고 문구일 때에는 txt_sales_quantity에
	 * 문구를 표시하고 txt_sales_quantity_str, txt_sales_quantity_sub_str은 숨김.
	 *
	 * @param data data
	 * @param layoutSalesQuantity 판매수량 표시하는 전체 layout
	 * @param txtSalesQuantity txtSalesQuantity
	 * @param txtSalesQuantityStr txtSalesQuantityStr
	 * @param txtSalesQuantitySubStr txtSalesQuantitySubStr
	 */

	//private static final AbsoluteSizeSpan bigSizeSpan = new AbsoluteSizeSpan(MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.bigTextSize), false);
	//private static final AbsoluteSizeSpan smalSizeSpan = new AbsoluteSizeSpan(MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.smallTextSize), false);

	private static final AbsoluteSizeSpan bigSizeSpan = new AbsoluteSizeSpan(20, true);
	private static final AbsoluteSizeSpan smalSizeSpan = new AbsoluteSizeSpan(14, true);

	private static final AbsoluteSizeSpan bigSizeSpan_ab = new AbsoluteSizeSpan(13, true);
	private static final AbsoluteSizeSpan smalSizeSpan_ab = new AbsoluteSizeSpan(13, true);

	private static final AbsoluteSizeSpan departmentBigSizeSpan = new AbsoluteSizeSpan(MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.departmentBigTextSize), false);
	private static final AbsoluteSizeSpan departmentSmalSizeSpan = new AbsoluteSizeSpan(MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.departmentSmallTextSize), false);


	private static final AbsoluteSizeSpan bigBannerSizeSpan = new AbsoluteSizeSpan(MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.bigBannerTextSize), false);
	private static final AbsoluteSizeSpan smallBannerSizeSpan = new AbsoluteSizeSpan(MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.smallBannerTextSize), false);

	private static final StyleSpan boldSpan =  new StyleSpan(Typeface.BOLD);

	public static void setSaleQuantity(SectionContentList data, LinearLayout layoutSalesQuantity,
			TextView txtSalesQuantity, TextView txtSalesQuantityStr, TextView txtSalesQuantitySubStr) {
		if (DisplayUtils.isValidNumberStringExceptZero(data.saleQuantity)) {
			// saleQuantity가 유효한 숫자 string이면 "xxx개 판매중"
			txtSalesQuantity.setText(data.saleQuantity);
			txtSalesQuantity.setVisibility(View.VISIBLE);

			if (DisplayUtils.isValidString(data.saleQuantityText)) {
				txtSalesQuantityStr.setText(data.saleQuantityText);
				txtSalesQuantityStr.setVisibility(View.VISIBLE);
			} else {
				txtSalesQuantityStr.setVisibility(View.GONE);
			}

			if (DisplayUtils.isValidString(data.saleQuantitySubText)) {
				txtSalesQuantitySubStr.setText(data.saleQuantitySubText);
				txtSalesQuantitySubStr.setVisibility(View.VISIBLE);
			} else {
				txtSalesQuantitySubStr.setVisibility(View.GONE);
			}
		} else if (DisplayUtils.isValidString(data.saleQuantityText)) {
			// saleQuantityText가 유효하면 그 문구 그대로 보여주고 
			txtSalesQuantityStr.setText(data.saleQuantityText);
			txtSalesQuantityStr.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
			txtSalesQuantityStr.setVisibility(View.VISIBLE);

			// 다른 view는 숨김
			txtSalesQuantity.setVisibility(View.GONE);
			txtSalesQuantitySubStr.setVisibility(View.GONE);
		} else {
			// saleQuantity와 saleQuantityText 둘다 유효하지 않으면 layout 자체를 숨김
			layoutSalesQuantity.setVisibility(View.GONE);
		}
	}


	//이 util사용하는곳은 모두 체크
	public static SpannableStringBuilder getDiscountText(Context context, SectionContentList data) {
		final SpannableStringBuilder priceStringBuilder = new SpannableStringBuilder();
		if (DisplayUtils.isValidString(data.discountRateText)) {
			// discountRateText 먼저 확인해서 표시
			if (data.discountRateText.contains(Keys.DISCOUNT_RATE_TEXT_GS)) {
				// GS가
				//혹시라도 GS가가 내려오면 어떻게 할지 확인 예정
				if(Integer.parseInt(data.discountRate) > Keys.ZERO_DISCOUNT_RATE ){

					priceStringBuilder.append(data.discountRate).append(context.getString(R.string.percent));
				}
			} else if (data.discountRateText.contains(Keys.DISCOUNT_RATE_TEXT_ENCORE)) {
				// 앵콜
				priceStringBuilder.append(Keys.DISCOUNT_RATE_TEXT_ENCORE);
			} else if (data.discountRateText.contains(Keys.DISCOUNT_RATE_TEXT_ONE_PLUS_ONE)) {
				// 1+1
				priceStringBuilder.append(Keys.DISCOUNT_RATE_TEXT_ONE_PLUS_ONE);
			} else {
				// GS가, 앵콜, 1+1 아니면 그냥 내려준 대로 뿌리기
				priceStringBuilder.append(data.discountRateText);
			}
		} else if (DisplayUtils.isValidString(data.discountRate)) {
			// discountRateText가 없으면 discountRate(숫자 할인률) 체크
			if (Integer.parseInt(data.discountRate) > Keys.ZERO_DISCOUNT_RATE ) {
				//0%는 표시하지 않는다.
				// discountRate가 5보다 크면 "xx%"로 표시
				priceStringBuilder.append(data.discountRate).append(context.getString(R.string.percent));
				priceStringBuilder.setSpan(bigSizeSpan, 0, data.discountRate.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				priceStringBuilder.setSpan(smalSizeSpan, data.discountRate.length(), data.discountRate.length() + 1,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}

		return priceStringBuilder;
	}

	/**
	 * 홈 -> 내일TV 구좌 AB테스트 / AB테스트 끝나면 지울예정
	 * @param context
	 * @param data
	 * @return
	 */
	public static SpannableStringBuilder getDiscountText_ab(Context context, SectionContentList data) {
		final SpannableStringBuilder priceStringBuilder = new SpannableStringBuilder();
		if (DisplayUtils.isValidString(data.discountRateText)) {
			// discountRateText 먼저 확인해서 표시
			if (data.discountRateText.contains(Keys.DISCOUNT_RATE_TEXT_GS)) {
				// GS가
				//혹시라도 GS가가 내려오면 어떻게 할지 확인 예정
				if(Integer.parseInt(data.discountRate) > Keys.ZERO_DISCOUNT_RATE ){

					priceStringBuilder.append(data.discountRate).append(context.getString(R.string.percent));
				}
			} else if (data.discountRateText.contains(Keys.DISCOUNT_RATE_TEXT_ENCORE)) {
				// 앵콜
				priceStringBuilder.append(Keys.DISCOUNT_RATE_TEXT_ENCORE);
			} else if (data.discountRateText.contains(Keys.DISCOUNT_RATE_TEXT_ONE_PLUS_ONE)) {
				// 1+1
				priceStringBuilder.append(Keys.DISCOUNT_RATE_TEXT_ONE_PLUS_ONE);
			} else {
				// GS가, 앵콜, 1+1 아니면 그냥 내려준 대로 뿌리기
				priceStringBuilder.append(data.discountRateText);
			}
		} else if (DisplayUtils.isValidString(data.discountRate)) {
			// discountRateText가 없으면 discountRate(숫자 할인률) 체크
			if (Integer.parseInt(data.discountRate) > Keys.ZERO_DISCOUNT_RATE ) {
				//0%는 표시하지 않는다.
				// discountRate가 5보다 크면 "xx%"로 표시
				priceStringBuilder.append(data.discountRate).append(context.getString(R.string.percent));
				priceStringBuilder.setSpan(bigSizeSpan_ab, 0, data.discountRate.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				priceStringBuilder.setSpan(smalSizeSpan_ab, data.discountRate.length(), data.discountRate.length() + 1,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}

		return priceStringBuilder;
	}

	/**
	 * 할인률 문구를 생성한다.
	 * -5% 미만 비교로직 제거
	 *
	 * @param context 컨텍스트
	 * @param data 상품데이타
	 * @return 생성된 할인률 문구
	 */
	public static SpannableStringBuilder getDiscountTextV2(Context context, SectionContentList data) {
		final SpannableStringBuilder priceStringBuilder = new SpannableStringBuilder();
		if (DisplayUtils.isValidString(data.discountRateText)) {
			// discountRateText 먼저 확인해서 표시
			if (data.discountRateText.contains(Keys.DISCOUNT_RATE_TEXT_GS)) {
				// GS가
				//priceStringBuilder.append(Keys.DISCOUNT_RATE_TEXT_GS).append(Keys.DISCOUNT_RATE_TEXT_PRICE);
				//혹시라도 GS가가 내려오면 어떻게 할지 확인 예정
				if(Integer.parseInt(data.discountRate) > Keys.ZERO_DISCOUNT_RATE){
					priceStringBuilder.append(data.discountRate).append(context.getString(R.string.percent));
				}
			} else if (data.discountRateText.contains(Keys.DISCOUNT_RATE_TEXT_ENCORE)) {
				// 앵콜
				priceStringBuilder.append(Keys.DISCOUNT_RATE_TEXT_ENCORE);
			} else if (data.discountRateText.contains(Keys.DISCOUNT_RATE_TEXT_ONE_PLUS_ONE)) {
				// 1+1
				priceStringBuilder.append(Keys.DISCOUNT_RATE_TEXT_ONE_PLUS_ONE);
			} else {
				// GS가, 앵콜, 1+1 아니면 그냥 내려준 대로 뿌리기
				priceStringBuilder.append(data.discountRateText);
			}
		} else if (DisplayUtils.isValidString(data.discountRate)) {
			if (Integer.parseInt(data.discountRate) > 0) {
				priceStringBuilder.append(data.discountRate).append(context.getString(R.string.percent));
			}
		}

		return priceStringBuilder;
	}

	// 왜 사이즈를 입력 받아서 만들지 않고 각 크기별로 만들고 있는것인지 이해가 가지 않음.
	// 사이즈를 입력받는함수를 새로 작성 20190103 hklim
	public static SpannableStringBuilder getDiscountText(Context context, SectionContentList data, int sizeBig, int sizeSmall) {
		final SpannableStringBuilder priceStringBuilder = new SpannableStringBuilder();
		if (DisplayUtils.isValidString(data.discountRateText)) {
			// discountRateText 먼저 확인해서 표시
			if (data.discountRateText.contains(Keys.DISCOUNT_RATE_TEXT_GS)) {
				// GS가
				//priceStringBuilder.append(Keys.DISCOUNT_RATE_TEXT_GS).append(Keys.DISCOUNT_RATE_TEXT_PRICE);
				if(Integer.parseInt(data.discountRate)> Keys.ZERO_DISCOUNT_RATE){
					priceStringBuilder.append(data.discountRate).append(context.getString(R.string.percent));
				}
			} else if (data.discountRateText.contains(Keys.DISCOUNT_RATE_TEXT_ENCORE)) {
				// 앵콜
				priceStringBuilder.append(Keys.DISCOUNT_RATE_TEXT_ENCORE);
			} else if (data.discountRateText.contains(Keys.DISCOUNT_RATE_TEXT_ONE_PLUS_ONE)) {
				// 1+1
				priceStringBuilder.append(Keys.DISCOUNT_RATE_TEXT_ONE_PLUS_ONE);
			} else {
				// GS가, 앵콜, 1+1 아니면 그냥 내려준 대로 뿌리기
				priceStringBuilder.append(data.discountRateText);
			}
		} else if (DisplayUtils.isValidString(data.discountRate)) {
			// discountRateText가 없으면 discountRate(숫자 할인률) 체크
			if (Integer.parseInt(data.discountRate) > Keys.ZERO_DISCOUNT_RATE) {
				AbsoluteSizeSpan bigSizeSpan = new AbsoluteSizeSpan(sizeBig, true);
				AbsoluteSizeSpan smalSizeSpan = new AbsoluteSizeSpan(sizeSmall, true);

				// discountRate가 5보다 크면 "xx%"로 표시
				priceStringBuilder.append(data.discountRate).append(context.getString(R.string.percent));
				priceStringBuilder.setSpan(bigSizeSpan, 0, data.discountRate.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				priceStringBuilder.setSpan(smalSizeSpan, data.discountRate.length(), data.discountRate.length() + 1,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}

		return priceStringBuilder;
	}

	public static SpannableStringBuilder getDepartmentDiscountText(Context context, SectionContentList data) {
		final SpannableStringBuilder priceStringBuilder = new SpannableStringBuilder();
		if (DisplayUtils.isValidString(data.discountRateText)) {
			// discountRateText 먼저 확인해서 표시
			if (data.discountRateText.contains(Keys.DISCOUNT_RATE_TEXT_GS)) {
				// GS가
				//priceStringBuilder.append(Keys.DISCOUNT_RATE_TEXT_GS).append(Keys.DISCOUNT_RATE_TEXT_PRICE);

				if(Integer.parseInt(data.discountRate) > Keys.ZERO_DISCOUNT_RATE){
					priceStringBuilder.append(data.discountRate).append(context.getString(R.string.percent));
				}

			} else if (data.discountRateText.contains(Keys.DISCOUNT_RATE_TEXT_ENCORE)) {
				// 앵콜
				priceStringBuilder.append(Keys.DISCOUNT_RATE_TEXT_ENCORE);
			} else if (data.discountRateText.contains(Keys.DISCOUNT_RATE_TEXT_ONE_PLUS_ONE)) {
				// 1+1
				priceStringBuilder.append(Keys.DISCOUNT_RATE_TEXT_ONE_PLUS_ONE);
			} else {
				// GS가, 앵콜, 1+1 아니면 그냥 내려준 대로 뿌리기
				priceStringBuilder.append(data.discountRateText);
			}
		} else if (DisplayUtils.isValidString(data.discountRate)) {
			// discountRateText가 없으면 discountRate(숫자 할인률) 체크
			if (Integer.parseInt(data.discountRate) > Keys.ZERO_DISCOUNT_RATE) {
				// discountRate가 5보다 크면 "xx%"로 표시
				priceStringBuilder.append(data.discountRate).append(context.getString(R.string.percent));
				priceStringBuilder.setSpan(departmentBigSizeSpan, 0, data.discountRate.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				priceStringBuilder.setSpan(departmentSmalSizeSpan, data.discountRate.length(), data.discountRate.length() + 1,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}

		return priceStringBuilder;
	}

	public static SpannableStringBuilder getDiscountBannerText(Context context, SectionContentList data) {
		final SpannableStringBuilder priceStringBuilder = new SpannableStringBuilder();
		if (DisplayUtils.isValidString(data.discountRateText)) {
			// discountRateText 먼저 확인해서 표시
			if (data.discountRateText.contains(Keys.DISCOUNT_RATE_TEXT_GS)) {
				// GS가
				//priceStringBuilder.append(Keys.DISCOUNT_RATE_TEXT_GS).append(Keys.DISCOUNT_RATE_TEXT_PRICE);
				if(Integer.parseInt(data.discountRate) > Keys.ZERO_DISCOUNT_RATE){
					priceStringBuilder.append(data.discountRate).append(context.getString(R.string.percent));
				}
			} else if (data.discountRateText.contains(Keys.DISCOUNT_RATE_TEXT_ENCORE)) {
				// 앵콜
				priceStringBuilder.append(Keys.DISCOUNT_RATE_TEXT_ENCORE);
			} else if (data.discountRateText.contains(Keys.DISCOUNT_RATE_TEXT_ONE_PLUS_ONE)) {
				// 1+1
				priceStringBuilder.append(Keys.DISCOUNT_RATE_TEXT_ONE_PLUS_ONE);
			} else {
				// GS가, 앵콜, 1+1 아니면 그냥 내려준 대로 뿌리기
				priceStringBuilder.append(data.discountRateText);
			}
		} else if (DisplayUtils.isValidString(data.discountRate)) {
			// discountRateText가 없으면 discountRate(숫자 할인률) 체크
			if (Integer.parseInt(data.discountRate) > Keys.ZERO_DISCOUNT_RATE) {
				// discountRate가 5보다 크면 "xx%"로 표시
				priceStringBuilder.append(data.discountRate).append(context.getString(R.string.percent));
				priceStringBuilder.setSpan(bigBannerSizeSpan, 0, data.discountRate.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				priceStringBuilder.setSpan(smallBannerSizeSpan, data.discountRate.length(), data.discountRate.length() + 1,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}

		return priceStringBuilder;
	}

	/**
	 * data에 따라 판매수량 관련 문구를 설정.
	 * 판매수량이 "256개 판매중"일 때에는 "256"은 txt_sales_quantity에, "개"는 txt_sales_quantity_str에,
	 * "판매중"은 txt_sales_quantity_sub_str에 표시. 판매수량이 숫자가 아니고 문구일 때에는 txt_sales_quantity에
	 * 문구를 표시하고 txt_sales_quantity_str, txt_sales_quantity_sub_str은 숨김.
	 *
	 * @param data data
	 *  @return SpannableStringBuilder
	 */
	public static SpannableStringBuilder getSaleQuantityText(SectionContentList data) {
		final SpannableStringBuilder priceStringBuilder = new SpannableStringBuilder();

		if (DisplayUtils.isValidNumberStringExceptZero(data.saleQuantity)) {
			// saleQuantity가 유효한 숫자 string이면 "xxx개 판매중"
			priceStringBuilder.append(data.saleQuantity);

//			priceStringBuilder.setSpan(boldSpan, 0, data.saleQuantity.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			if (DisplayUtils.isValidString(data.saleQuantityText)) {
				priceStringBuilder.append(data.saleQuantityText);
			}

			if (DisplayUtils.isValidString(data.saleQuantitySubText)) {
				priceStringBuilder.append(" ").append(data.saleQuantitySubText);
			}
		} else if (DisplayUtils.isValidString(data.saleQuantityText)) {
			// saleQuantityText가 유효하면 그 문구 그대로 보여주고
			priceStringBuilder.append(data.saleQuantityText);
		}

		return priceStringBuilder;
	}

	public static boolean isNumber(String str){
		if(Pattern.matches("^[0-9]+$", str)){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * 임시 Common 함수
	 * @param context
	 * @param discountRate
	 * @return
	 */
	public static SpannableStringBuilder getDiscountCommon(Context context, String discountRate){
		final SpannableStringBuilder priceStringBuilder = new SpannableStringBuilder();

		 if (DisplayUtils.isValidString(discountRate) && DisplayUtils.isNumeric(discountRate)) {
		 	//discountRateText가 없으면 discountRate(숫자 할인률) 체크, 0%는 표시하지 않는다.

			if (Integer.parseInt(discountRate) > Keys.ZERO_DISCOUNT_RATE ) {

				// discountRate가 5보다 크면 "xx%"로 표시
				priceStringBuilder.append(discountRate).append("%");
				// 이거 왜 죽지.
				//priceStringBuilder.append(discountRate).append(context.getString(R.string.percent));

				//폰트 고정일때는 첫번째 파라미터에 smalSizeSpan_ab, bigSizeSpan_ab 사용했었음
				priceStringBuilder.setSpan(13, 0, discountRate.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				priceStringBuilder.setSpan(13, discountRate.length(), discountRate.length() + 1,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		 return priceStringBuilder;
	}
}
