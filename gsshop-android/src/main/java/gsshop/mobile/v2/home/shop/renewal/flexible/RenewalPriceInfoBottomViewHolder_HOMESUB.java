/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.renewal.flexible;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gsshop.mocha.ui.util.ViewUtils;

import org.springframework.http.HttpEntity;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.main.ImageBadge;
import gsshop.mobile.v2.home.main.ProductInfoUtil;
import gsshop.mobile.v2.home.main.TvLiveBanner;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.renewal.views.TextViewWithListenerWhenOnDraw;
import gsshop.mobile.v2.home.shop.schedule.TVScheduleShopFragment;
import gsshop.mobile.v2.home.shop.schedule.model.DirectOrdInfo;
import gsshop.mobile.v2.home.shop.schedule.viewholder.TLineBaseViewHolder;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isEmpty;
import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;
import static gsshop.mobile.v2.ServerUrls.WEB.RN_SCH_VOD_ALRAM;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleShopFragment.makeFormData;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleShopFragment.scheduleBroadType;


/**
 * 홈부상품에만 쓰이는 뷰홀더
 * 텍스트크기 및 다른부분이있어 따로 뷰홀더 생성
 */
@SuppressLint("NewApi")
public class RenewalPriceInfoBottomViewHolder_HOMESUB extends TLineBaseViewHolder {

    public final static String COMMON_RENTALTEXT = "월 렌탈료";
    public final static String COMMON_DISPLAY_RENTALTEXT = "월";

    private final static String BENEFITS_SEPERATOR_LINE = " | ";
    private final static String BENEFITS_SEPERATOR_DOT = " · ";
    private final static String COLOR_BENEFITS_DEFAULT = "80111111";
    private final static String STYLETYPE_BOLD = "BOLD";

    //가격영역에 상담신청상품을 표시할 경우 폰트사이즈 변경을 위해
    private static final float COUNSELING_FONT_SIZE = 14;
    //private static final float PRICE_FONT_SIZE = 19;

    // 컴포넌트 형태
    public enum BroadComponentType {
        live,           //생방송       구매하기
        data,           //데이타방송     구매하기
        mobilelive,     //모바일라이브    구매하기
        schedule,       //TV편성표        구매하기
        vod,             //내일TV         구매하기
        product         //상품            없다.
    }
    //컴포넌트 형태별 구분처리의 필요성이 없으면 삭제예정
    protected BroadComponentType broadComponentType = BroadComponentType.product;

    /**
     * 내가 어느 탭에 속하는 뷰홀더인지
     */
    protected String myNaviId;

    /**
     * 컨텍스트
     */
    protected Context context;

    /**
     * 데이타 모델
     */
    protected ShopInfo mInfo;


    protected LinearLayout tag_container; // 혜택영역 & 버튼영역 포함하는 전체
    private LinearLayout layout_price; // 가격영역만 레이아웃

    protected LinearLayout lay_button_container;

    //가격 영역 + 상품평 표시 영역
    private TextView badge_layout;

    //가격 표시 영역
    private TextView txt_base_price;
    private TextView txt_base_price_unit;
    private TextView txtPrice;
    private TextView txt_price_unit;
    private TextView txt_discount_rate; //할인율
    protected TextViewWithListenerWhenOnDraw txt_benefit_container; // TV쇼핑 | 무료배송, 무이자
    private TextView txt_rental_text;

    //상품평점
    public TextView txt_review_avr;
    //상품카운트
    public TextView txt_review_count;

    //브랜드명 관련
    private TextView txt_brand_name;
    //상품명 표시 영역
    protected TextView txtTitle;

    //일시품절, 방송중 구매가능
    protected TextView txt_comment;


    private SpannableStringBuilder priceStringBuilder = new SpannableStringBuilder();
    private SpannableStringBuilder sourcingStringBuilder = new SpannableStringBuilder(); //[TV쇼핑], [백화점]
    private SpannableStringBuilder footer_text_1 = new SpannableStringBuilder(); //무료배송, 무이자

    //방송알림 라이브톡 바로구매 버튼 영역
    protected TextView txt_buy; //바로구매
    protected LinearLayout lay_alarm; //알람
    protected View alarm_on;
    protected View alarm_off;
    protected LinearLayout lay_live_talk; //라이브톡
    private View mViewProduct;

    /**
     * 생성자
     *
     * @param itemView 레이아웃뷰
     */
    public RenewalPriceInfoBottomViewHolder_HOMESUB(View itemView) {
        super(itemView);

        txtTitle = (TextView) itemView.findViewById(R.id.txt_title);
        txt_base_price = (TextView) itemView.findViewById(R.id.txt_base_price);
        txt_base_price_unit = (TextView) itemView.findViewById(R.id.txt_base_price_unit);
        txtPrice = (TextView) itemView.findViewById(R.id.txt_price);
        txt_price_unit = (TextView) itemView.findViewById(R.id.txt_price_unit);
        txt_discount_rate = (TextView) itemView.findViewById(R.id.txt_discount_rate);
        txt_benefit_container = (TextViewWithListenerWhenOnDraw) itemView.findViewById(R.id.txt_benefit_container);
        tag_container = itemView.findViewById(R.id.tag_container);
        layout_price = itemView.findViewById(R.id.layout_price);
        txt_rental_text = (TextView) itemView.findViewById(R.id.txt_rental_text);
        lay_button_container = itemView.findViewById(R.id.lay_button_container);
        mViewProduct = itemView.findViewById(R.id.product_layout);

        //상품평 관련
        txt_review_avr = itemView.findViewById(R.id.txt_review_avr);
        txt_review_count = itemView.findViewById(R.id.txt_review_count);

        //브랜드명 관련
        txt_brand_name = (TextView) itemView.findViewById(R.id.txt_brand_name);

        //바로구매
        txt_buy = itemView.findViewById(R.id.txt_buy);

        //알람
        lay_alarm = itemView.findViewById(R.id.lay_alarm);
        alarm_on = itemView.findViewById(R.id.alarm_on);
        alarm_off = itemView.findViewById(R.id.alarm_off);

        //라이브톡
        lay_live_talk = itemView.findViewById(R.id.lay_live_talk);

        //일시품절, 방송중 구매가능
        txt_comment = itemView.findViewById(R.id.txt_comment);


    }

    public void bindViewHolder(TvLiveBanner tvLiveBanner, int position, View contentView) {
        if(contentView != null){
            txtTitle = contentView.findViewById(R.id.txt_title);
            txt_base_price = contentView.findViewById(R.id.txt_base_price);
            txt_base_price_unit = contentView.findViewById(R.id.txt_base_price_unit);
            txtPrice = contentView.findViewById(R.id.txt_price);
            txt_price_unit = contentView.findViewById(R.id.txt_price_unit);
            txt_discount_rate = contentView.findViewById(R.id.txt_discount_rate);
            txt_benefit_container = contentView.findViewById(R.id.txt_benefit_container);
            tag_container = contentView.findViewById(R.id.tag_container);
            layout_price = contentView.findViewById(R.id.layout_price);
            txt_rental_text = contentView.findViewById(R.id.txt_rental_text);
            lay_button_container = contentView.findViewById(R.id.lay_button_container);

            //상품평 관련
            txt_review_avr = contentView.findViewById(R.id.txt_review_avr);
            txt_review_count = contentView.findViewById(R.id.txt_review_count);

            //브랜드명 관련
            txt_brand_name = contentView.findViewById(R.id.txt_brand_name);

            //바로구매
            txt_buy = contentView.findViewById(R.id.txt_buy);

            //알람
            lay_alarm = contentView.findViewById(R.id.lay_alarm);
            alarm_on = contentView.findViewById(R.id.alarm_on);
            alarm_off = contentView.findViewById(R.id.alarm_off);

            //라이브톡
            lay_live_talk = contentView.findViewById(R.id.lay_live_talk);

            //일시품절, 방송중 구매가능
            txt_comment = contentView.findViewById(R.id.txt_comment);

            //2단일때만 혜택영역 혜택영역 margin 초기화
            if (txt_benefit_container != null) {
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams)txt_benefit_container.getLayoutParams();
                marginLayoutParams.setMargins(0,0,0,0);
                txt_benefit_container.setLayoutParams(marginLayoutParams);
            }
        }

        try {
            txt_discount_rate.setPadding(0, 0, 0, 0);
            priceStringBuilder.clear();
            //상품정보 표시 영역
            txtTitle.setText("");

            //상품정보 가격 표시 영역
            txtPrice.setText("");
            txt_discount_rate.setText("");
            txt_benefit_container.setText("");
            if (txt_rental_text != null) {
                txt_rental_text.setText("");
                txt_rental_text.setVisibility(View.GONE);
            }
            txt_base_price.setVisibility(View.GONE);
            txt_base_price_unit.setVisibility(View.GONE);

            //상품정보 혜택 표시 영역 ( 소싱 포함 )
            txt_benefit_container.setVisibility(View.INVISIBLE);//혜택영역(무료배송·무이자·적립금), GONE시키면 구매하기 버튼 위치 바뀐다.
            //footer_text_1.setLength(0); //무료배송·무이자·적립금
            footer_text_1.clear();
            sourcingStringBuilder.clear();

           //txtPrice.setTextSize(TypedValue.COMPLEX_UNIT_DIP, PRICE_FONT_SIZE);
        }
        catch (NullPointerException e) {
            Ln.e(e.getMessage());
        }

        //바로구매
        //버튼이 있을때(구매하기, 일시품절, 방송중 구매가능)
        if (isNotEmpty(txt_comment)) {
            txt_comment.setVisibility(View.GONE);
        }

        if(!BroadComponentType.product.equals(broadComponentType) && tvLiveBanner.rDirectOrdInfo != null
                && tvLiveBanner.rLinkUrl != null && !tvLiveBanner.rLinkUrl.isEmpty() ) {

//            txt_buy null 체크 추가.
            if (txt_buy != null) {
                txt_buy.setVisibility(View.VISIBLE);
                txt_buy.setBackgroundResource(R.drawable.renewal_bg_buy);

                //구매하기 버튼 유효 하다 && 실제 구매하기 버튼에 텍스트가 잘못되었을까봐 예외처리 && else 서버에서 준 텍스트 뿌려(상담, 구매, 예약??)
                if(tvLiveBanner.rDirectOrdInfo.linkUrl == null || TextUtils.isEmpty(tvLiveBanner.rDirectOrdInfo.linkUrl)) {
                    // iOS와 로직 맞춤 DirectOrdInfo.linkUrl 없으면 그냥 버튼 안보임.
                    txt_buy.setVisibility(View.GONE);
                }
                else {
                    // DirectOrdInfo.linkUrl 있으면 text 비교하여 비어있으면 구매하기, 없으면 text 노출
                    txt_buy.setVisibility(View.VISIBLE);
                    if(tvLiveBanner.rDirectOrdInfo.linkUrl == null || TextUtils.isEmpty(tvLiveBanner.rDirectOrdInfo.text)) {
                        txt_buy.setText(R.string.layer_flag_buy);
                    }
                    else {
                        txt_buy.setText(tvLiveBanner.rDirectOrdInfo.text);
                    }
                }

                //일단 구매하기 처리는 했으나, rImageLayerFlag 우선순으로 엎어친다
                //구매하기 버튼이 유효한 경우 구매하기 버튼에, 아닌 경우 이미지 레이아웃에 표시한다.
                if (tvLiveBanner.rImageLayerFlag != null && DisplayUtils.isValidString(tvLiveBanner.rImageLayerFlag)) {
                    if (SOLD_OUT.equalsIgnoreCase(tvLiveBanner.rImageLayerFlag)) {
                        //구매하기버튼에 "일시품절" 표시한다.
                        txt_buy.setText(R.string.layer_flag_sold_out);
                        txt_buy.setVisibility(View.VISIBLE);
                        txt_buy.setBackgroundResource(R.drawable.renewal_bg_air_buy);

                    }
                    else if (AIR_BUY.equalsIgnoreCase(tvLiveBanner.rImageLayerFlag)) {
                        //구매하기버튼에 방송중 구매가능 표시한다.
                        txt_buy.setText(R.string.layer_flag_air_buy);
                        txt_buy.setVisibility(View.VISIBLE);
                        //버튼 회색 처리
                        txt_buy.setBackgroundResource(R.drawable.renewal_bg_air_buy);
                    }
                    txt_buy.setOnClickListener(null);
                }

                //버튼종류(구매하기, 일시품정 등)에 관계없이 아래순서로 링크 적용
                //우선순위 1.tvLiveBanner.rDirectOrdInfo.linkUrl, 2.tvLiveBanner.rLinkUrl
                txt_buy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        WebUtils.goWeb(context, isNotEmpty(tvLiveBanner.rDirectOrdInfo.linkUrl) ?
                                DisplayUtils.getFullUrl(tvLiveBanner.rDirectOrdInfo.linkUrl) : tvLiveBanner.rLinkUrl);
                    }
                });
            }
        }
        else {//버튼이 없을때(일시품절, 방송중 구매가능)
            if (txt_buy != null) {
                txt_buy.setOnClickListener(null);
            }

            if(tvLiveBanner.rImageLayerFlag != null && DisplayUtils.isValidString(tvLiveBanner.rImageLayerFlag))
            {
                if(SOLD_OUT.equalsIgnoreCase(tvLiveBanner.rImageLayerFlag)){
                    if(BroadComponentType.product.equals(broadComponentType))
                    {
                        if (isNotEmpty(txt_comment)) {
                            txt_comment.setText(R.string.layer_flag_sold_out);
                            txt_comment.setVisibility(View.VISIBLE);
                        }
                    }
                    else{
                        //구매하기버튼에 "일시품절" 표시한다.
                        if (isNotEmpty(txt_buy)) {
                            txt_buy.setText(R.string.layer_flag_sold_out);
                            txt_buy.setVisibility(View.VISIBLE);
                            txt_buy.setBackgroundResource(R.drawable.renewal_bg_air_buy);
                        }
                    }
                }else if(AIR_BUY.equalsIgnoreCase(tvLiveBanner.rImageLayerFlag)){

                    if(BroadComponentType.product.equals(broadComponentType))
                    {
                        if (isNotEmpty(txt_comment)) {
                            txt_comment.setText(R.string.layer_flag_air_buy);
                            txt_comment.setVisibility(View.VISIBLE);
                        }
                    }
                    else{
                        //구매하기버튼에 방송중 구매가능 표시한다.
                        if (isNotEmpty(txt_buy)) {
                            txt_buy.setText(R.string.layer_flag_air_buy);
                            txt_buy.setVisibility(View.VISIBLE);
                            //버튼 회색 처리
                            txt_buy.setBackgroundResource(R.drawable.renewal_bg_air_buy);
                        }
                    }
                }
                else{
                    //directOrdInfo에 링크Url이 없으면서 imageLayerFlag ="" 비어있는경우
                    if (isNotEmpty(txt_buy)) {
                        txt_buy.setVisibility(View.GONE);
                    }
                }
            }
            else{
                //directOrdInfo에 링크Url이 없으면서 imageLayerFlag ="" 비어있는경우
                if (isNotEmpty(txt_buy)) {
                    txt_buy.setVisibility(View.GONE);
                }
            }

        }

        // 라이브톡 버튼 노출여부
        // 라이브톡 노출시 방송알림 영역은 종만 표시되고, 미노출시 종+텍스트 표시됨
        if (tvLiveBanner.rLiveTalkYn != null && "Y".equals(tvLiveBanner.rLiveTalkYn) ) {
            if (isNotEmpty(lay_live_talk)) {

                if(broadComponentType == BroadComponentType.live){
                    lay_live_talk.setVisibility(View.GONE);
                }else{
                    lay_live_talk.setVisibility(View.VISIBLE);
                }

                lay_live_talk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        WebUtils.goWeb(context, tvLiveBanner.rLiveTalkUrl, ((Activity) context).getIntent());

                        //라이브톡 버튼 효율 코드
                        if (broadComponentType == BroadComponentType.live) {
                            //생방송
                            ((HomeActivity) context).setWiseLogHttpClient(ServerUrls.WEB.RN_BESTSHOP_LIVE_TALK);
                        } else if (broadComponentType == BroadComponentType.schedule) {
                            //편성표
                            ((HomeActivity) context).setWiseLogHttpClient(ServerUrls.WEB.RN_SCH_LIVE_TALK);
                        }
                    }
                });
            }
        } else {
            if (isNotEmpty(lay_live_talk)) {
                lay_live_talk.setVisibility(View.GONE);
            }
        }

        //방송알림
        //rBroadAlarmDoneYn -> 알람버튼 보이냐 안보이냐가 아닌 // 알람버튼이 VISIBLE이지만 눌린상태냐 아니냐
        if ("Y".equals(tvLiveBanner.rBroadAlarmDoneYn)) {
            alarm_off.setVisibility(View.GONE);
            alarm_on.setVisibility(View.VISIBLE);
            lay_alarm.setVisibility(View.VISIBLE);
        } else if ("N".equals(tvLiveBanner.rBroadAlarmDoneYn)) {
            alarm_off.setVisibility(View.VISIBLE);
            alarm_on.setVisibility(View.GONE);
            lay_alarm.setVisibility(View.VISIBLE);
        } else {
            //"Y", "N" 이외 값은 알람영역 숨김
            lay_alarm.setVisibility(View.GONE);
        }

        //방송알림
        lay_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "";
                String type = "";
                String mseqUrl= "";
                HttpEntity<Object> requestEntity = makeFormData(tvLiveBanner.rPrdId, tvLiveBanner.rExposPrdName, null, null);

                // 방송 알림 등록 / 취소 효율 코드
                if ("Y".equals(tvLiveBanner.rBroadAlarmDoneYn)) {
                    //방송알림 취소
                    url = ServerUrls.getHttpRoot() + ServerUrls.REST.TV_SCHEDULE_ALARM_DELETE;
                    type = "delete";
                } else if ("N".equals(tvLiveBanner.rBroadAlarmDoneYn)) {
                    //방송알림 설정 팝업
                    url = ServerUrls.getHttpRoot() + ServerUrls.REST.TV_SCHEDULE_ALARM_QUERY;
                    type = "query";
                }
                new TVScheduleShopFragment.BroadAlarmUpdateController(context).execute(url, requestEntity, type);

                String wiselogUrl;
                if ("Y".equalsIgnoreCase(tvLiveBanner.rPgmLiveYn)) {
                    //생방송 (live or data)
                    wiselogUrl = scheduleBroadType == TVScheduleShopFragment.ScheduleBroadType.LIVE ?
                                ServerUrls.WEB.RN_SCH_LIVE_ALRAM : ServerUrls.WEB.RN_SCH_DATA_ALRAM;
                } else {
                    //VOD
                    wiselogUrl = RN_SCH_VOD_ALRAM;
                }
                ((HomeActivity) context).setWiseLogHttpClient(wiselogUrl);
            }
        });

        //모든 버튼이 GONE이면 감싸는 영역도 GONE
        try {
            if (txt_buy.getVisibility() == View.GONE && lay_alarm.getVisibility() == View.GONE &&
                    lay_live_talk.getVisibility() == View.GONE) {
                lay_button_container.setVisibility(View.GONE);
            }
        }
        catch (NullPointerException e) {
            Ln.e(e.getMessage());
        }

        // 브랜드명 노출
        if (txt_brand_name != null) {
            // 브랜드 이름은 Spannable로 해서 해당 view는 gone
            txt_brand_name.setVisibility(View.GONE);
        }

        if (DisplayUtils.isValidString(tvLiveBanner.rProductName) && txtTitle != null) {
            txtTitle.setText(tvLiveBanner.rProductName);
            if (DisplayUtils.isValidString(tvLiveBanner.rBrandNm)) {
                SpannableStringBuilder titleStringBuilder = new SpannableStringBuilder();
                titleStringBuilder.append(tvLiveBanner.rBrandNm);
                titleStringBuilder.setSpan(new StyleSpan(Typeface.BOLD), 0, titleStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                titleStringBuilder.append(" " + tvLiveBanner.rProductName);
                txtTitle.setText(titleStringBuilder);
            } else {
                txtTitle.setText(tvLiveBanner.rProductName);
            }
        }

        //상품 상품평점
        if (txt_review_avr != null) {
            if (DisplayUtils.isValidString(tvLiveBanner.rAddTextLeft)) {
                txt_review_avr.setVisibility(View.VISIBLE);
                txt_review_avr.setText(tvLiveBanner.rAddTextLeft);
                txt_review_avr.setTextColor(Color.parseColor("#a3111111")); //제플린에는 99111111이지만 구두로 변경
            } else {
                txt_review_avr.setVisibility(View.GONE);
            }
        }

        //상품 상품평카운트
        if (txt_review_count != null) {
            if (DisplayUtils.isValidString(tvLiveBanner.rAddTextRight)) {
                txt_review_count.setVisibility(View.VISIBLE);
                txt_review_count.setText(tvLiveBanner.rAddTextRight);
                txt_review_count.setTextColor(Color.parseColor("#7a111111"));
            } else {
                txt_review_count.setVisibility(View.GONE);
            }
        }

        //상품 상품평평점 + 상품카운트 = 전체 영역 처리
        if (DisplayUtils.isValidString(tvLiveBanner.rAddTextLeft) && DisplayUtils.isValidString(tvLiveBanner.rAddTextRight)){
            ViewUtils.showViews(txt_review_avr, txt_review_count);
        }


		// 퍼센트값으로 표현되는 할인률이 있을 경우에만 base price 표시
        // base price가 0이거나 salePrice가 basePrice보다 크면 basePrice 표시 안함
        int salePrice = DisplayUtils.getNumberFromString(tvLiveBanner.rSalePrice);
		int basePrice = DisplayUtils.getNumberFromString(tvLiveBanner.rBasePrice);

		// 베이스가 처리로직: 할인률 있으면
        if ( tvLiveBanner.rDiscountRate != null && !tvLiveBanner.rDiscountRate.isEmpty() && Integer.parseInt(tvLiveBanner.rDiscountRate) > Keys.ZERO_DISCOUNT_RATE) {
            //베이스가 로직==찌익그은가, 찌익 그은가는 할인율이 없거나, 세일가보다 비싸면 안보여야함 디폴트는 안보임
            if (DisplayUtils.isValidNumberStringExceptZero(tvLiveBanner.rBasePrice) && (salePrice < basePrice)) {
                if (txt_base_price != null) {
                    txt_base_price.setText(DisplayUtils.getFormattedNumber(tvLiveBanner.rBasePrice));
                    txt_base_price.append(tvLiveBanner.rExposePriceText);
                    txt_base_price.setVisibility(View.VISIBLE);
                    txt_base_price.setPaintFlags(txt_base_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }

                if (txt_discount_rate != null) {
                    txt_discount_rate.append(ProductInfoUtil.getDiscountCommon(context, tvLiveBanner.rDiscountRate));
                    txt_discount_rate.setEllipsize(null);
                    txt_discount_rate.setVisibility(View.VISIBLE);
                }
            }
            else{
                ViewUtils.hideViews(txt_base_price, txt_base_price_unit, txt_discount_rate);
            }
        }

        // 세일 price 처리 로직 : 있으면 그려 / else 면 안그려
        if (DisplayUtils.isValidString(tvLiveBanner.rSalePrice)) {
            String salePriceText = DisplayUtils.getFormattedNumber(tvLiveBanner.rSalePrice);
            if (txt_price_unit != null) {
                txt_price_unit.setText(tvLiveBanner.rExposePriceText);
                txt_price_unit.setVisibility(View.VISIBLE);
            }

            priceStringBuilder.append(salePriceText);
            if (txtPrice != null) {
                txtPrice.append(priceStringBuilder);
                txtPrice.setVisibility(View.VISIBLE);
            }
        } else {
            ViewUtils.hideViews(txtPrice, txt_price_unit);
        }

        /**
         * 다 처리하고, 상품 종류에 따른 처리를 하는것이 맞는가 일단은 가격 표시 영역의 그룹은 잘 해놨기 때문에 추후에 문제가 발생하면 처리가 가능할거라 본다
         */

        // deal 상품이면서 렌탈, 여행, 시공, 딜 등의 무형 상품일 때 ( 렌탈이 아닌경우에는 rRentalText 없다고 확답 받음 )
        // 혹은 deal 상품이 아니면서 렌탈일 때
        if(("true".equals(tvLiveBanner.deal) && ("R".equals(tvLiveBanner.rProductType)/* 렌탈*/ ||
                "T".equals(tvLiveBanner.rProductType)/*여행*/ || "U".equals(tvLiveBanner.rProductType)/*딜 시공*/ ||
                "S".equals(tvLiveBanner.rProductType)/*상품 시공*/)) ||
            ("false".equals(tvLiveBanner.deal) && "R".equals(tvLiveBanner.rProductType)/* 렌탈*/)) {
            //항목1 무언가 써있으면???
            // 렌탈 에서만 rRentalText 를 비교한다.
            if ( "R".equals(tvLiveBanner.rProductType) && tvLiveBanner.rRentalText != null && !tvLiveBanner.rRentalText.isEmpty() ) {
                //항목1에 "월 렌탈료"가 써있으면 -> "월"로 변경
                //API에서 항목1에 "월"이 내려올 수 있는 조건이 추가되어 "월"도 "월 렌탈료"와 동일로직으로 처리하도록 조건 추가
                if (COMMON_RENTALTEXT.equals(tvLiveBanner.rRentalText) ||
                        COMMON_DISPLAY_RENTALTEXT.equals(tvLiveBanner.rRentalText)) {
                    if(txt_rental_text != null) {
                        txt_rental_text.setText(COMMON_DISPLAY_RENTALTEXT);
                        txt_rental_text.setVisibility(View.VISIBLE);
                    }

                    //"월" + 항목2(rRentalPrice) 값이 있으면 뿌린다
                    if (tvLiveBanner.rRentalPrice != null && !tvLiveBanner.rRentalPrice.isEmpty() && !tvLiveBanner.rRentalPrice.startsWith("0")) {
                        if( txtPrice != null) {
                            txtPrice.setText(tvLiveBanner.rRentalPrice);
                            txtPrice.setVisibility(View.VISIBLE);
                        }
                        // 원자 지우기 ,
                        ViewUtils.hideViews(txt_price_unit);
                    }
                    else {
                        //렌탈이지만 "월 렌탈료"가 안써있으면 다 우지고 "상담 전용 상품입니다." 세긴다.
                        //txtPrice.setText("상담 전용 상품입니다.");
                        if( txtPrice != null) {
                            txtPrice.setText(R.string.common_rental_title);
                            txtPrice.setVisibility(View.VISIBLE);
                            txtPrice.setTextSize(COUNSELING_FONT_SIZE);
                        }
                        ViewUtils.hideViews(txt_rental_text, txt_price_unit);
                    }
                }
                // else 는 무조건 엔터 칩시다 접었을 때 보기가 힘듭니다.
                else {//렌탈인데 월이 아니면,
                    if (tvLiveBanner.rRentalPrice != null && !tvLiveBanner.rRentalPrice.isEmpty() && !tvLiveBanner.rRentalPrice.startsWith("0")) {
                        if( txtPrice != null) {
                            txtPrice.setText(tvLiveBanner.rRentalPrice);
                            txtPrice.setVisibility(View.VISIBLE);
                        }
                        // rRentalPrice 게 없으니 "월" 영역 지우기 // 원자 지우기 ,
                        ViewUtils.hideViews(txt_rental_text, txt_price_unit);
                    }
                    else {
                        //"월" 을 지우고, "상담 전용 상품입니다." 세긴다.
                        //txtPrice.setText("상담 전용 상품입니다.");
                        if( txtPrice != null) {
                            txtPrice.setText(R.string.common_rental_title);
                            txtPrice.setVisibility(View.VISIBLE);
                            txtPrice.setTextSize(COUNSELING_FONT_SIZE);
                        }
                        // rRentalPrice 게 없으니 "월" 영역 지우기 // 원자 지우기 ,
                        ViewUtils.hideViews(txt_rental_text, txt_price_unit);
                    }
                }
            }

            // 월이 안붙는 무형 상품에 대해서 수정, RentalPrice 노출
            else if (tvLiveBanner.rRentalPrice != null && !tvLiveBanner.rRentalPrice.isEmpty() && !tvLiveBanner.rRentalPrice.startsWith("0")) {
                if (txtPrice != null) {
                    txtPrice.setText(tvLiveBanner.rRentalPrice);
                    txtPrice.setVisibility(View.VISIBLE);
                }
                // rRentalPrice 게 없으니 "월" 영역 지우기 // 원자 지우기 ,
                ViewUtils.hideViews(txt_rental_text, txt_price_unit);
            }
            else {
                //렌탈이지만 "월 렌탈료"가 안써있으면 다 지우고 "상담 전용 상품입니다." 세긴다.
                //txtPrice.setText("상담 전용 상품입니다.");
                if( txtPrice != null) {
                    txtPrice.setText(R.string.common_rental_title);
                    txtPrice.setVisibility(View.VISIBLE);
                    txtPrice.setTextSize(COUNSELING_FONT_SIZE);
                }

                // rRentalPrice 게 없으니 "월" 영역 지우기 // 원자 지우기 ,
                ViewUtils.hideViews(txt_rental_text, txt_price_unit);
            }
        }
        else {
            //렌탈 아닌데 상담 전용 상품 뜨는경우가 없다고 하자
            ViewUtils.hideViews(txt_rental_text);

            //보험일때, 계속 추가 할게요
            if("I".equals(tvLiveBanner.rProductType) ) {
                //보험이면 타이틀만 보이자
                //layout_price.setVisibility(View.GONE);
                // Null pointer 발생하는 경우 있음. null이라도 안죽는 함수로 변경
                ViewUtils.hideViews(txtPrice, txt_price_unit, txt_base_price, txt_base_price_unit, txt_discount_rate);
            }
            else{
                if (txtPrice != null) {
                    txtPrice.setText(tvLiveBanner.rSalePrice);
                    txtPrice.setVisibility(View.VISIBLE);
                    txt_price_unit.setVisibility(View.VISIBLE);
                }
            }
        }

        //릴레이캐시, 무료배송, 무이자 (혜택영역)
        if (tvLiveBanner.rAllBenefit != null && !tvLiveBanner.rAllBenefit.isEmpty()) {
            int index = 0;
            for (ImageBadge badge : tvLiveBanner.rAllBenefit) {
                if (badge != null && badge.text != null && !badge.text.isEmpty()) {
                    if (footer_text_1.length() > 0) {
                        SpannableStringBuilder dot_stringBuilder = new SpannableStringBuilder();
                        dot_stringBuilder.append(BENEFITS_SEPERATOR_DOT);

                        //benefit_seperator_dot 색상 적용
                        dot_stringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#4c111111")), 0, BENEFITS_SEPERATOR_DOT.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        footer_text_1.append(dot_stringBuilder);
                    }
                    footer_text_1.append(badge.text);
                }
                index++;
            }
        }

        /**
         * 소싱(TV쇼핑, 백화점) & 혜택
         */

        //broadComponentType이 product일때만 소싱 append하도록!
        // 이경우 TV 상품이 안나오면 되기때문에 tvLiveBanner.rSource.text 를 빈값으로. -> 이 구문 빠져야 함.
//        if(!BroadComponentType.product.equals(broadComponentType) && tvLiveBanner.rSource != null){
//            tvLiveBanner.rSource.text = "";
//        }


        if (footer_text_1.length() <= 0 && (tvLiveBanner.rSource == null || tvLiveBanner.rSource.text.isEmpty() || tvLiveBanner.rSource.text == null)) {
            if (txt_benefit_container != null) {
                txt_benefit_container.setVisibility(View.INVISIBLE);
            }
        }
        else {
            if (tvLiveBanner.rSource != null && (!tvLiveBanner.rSource.text.isEmpty() || tvLiveBanner.rSource.text == null)) {

                //소싱에 대한 컬러 확인 디파인 필요
                String sourceColor = COLOR_BENEFITS_DEFAULT;
                //유효하면 변경
                if (tvLiveBanner.rSource.type != null && !tvLiveBanner.rSource.type.isEmpty())
                    sourceColor = tvLiveBanner.rSource.type;

                //[TV쇼핑] 부분만 BOLD처리
                sourcingStringBuilder.append(tvLiveBanner.rSource.text);
                // styleTpye 확인하여 BOLD처리
                if (STYLETYPE_BOLD.equals(tvLiveBanner.rSource.styleType)) {
                    sourcingStringBuilder.setSpan(new StyleSpan(Typeface.BOLD), 0, tvLiveBanner.rSource.text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                sourcingStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#" + sourceColor)), 0, tvLiveBanner.rSource.text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

//                tag_container.setVisibility(View.VISIBLE);
//                txt_benefit_container.setVisibility(View.VISIBLE);
                ViewUtils.showViews(tag_container, txt_benefit_container);
                if (txt_benefit_container != null) {
                    txt_benefit_container.setText(sourcingStringBuilder);
                }

                if (footer_text_1.length() > 0) {
                    //benefit_seperator_line 색상 지정
                    SpannableStringBuilder seperator_stringBuilder = new SpannableStringBuilder();
                    seperator_stringBuilder.append(BENEFITS_SEPERATOR_LINE);
                    seperator_stringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#33111111")), 0, BENEFITS_SEPERATOR_LINE.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    sourcingStringBuilder.append(seperator_stringBuilder);
                }
            }
            //broadComponentType이 product 아닌경우 소싱을 append 하지 않는다!
            if (footer_text_1.length() > 0) {
                ViewUtils.showViews(tag_container, txt_benefit_container);

                sourcingStringBuilder.append(footer_text_1);

                String strText = sourcingStringBuilder.toString();
                for (ImageBadge badge : tvLiveBanner.rAllBenefit) {
                    int index = strText.indexOf(badge.text);
                    if (index >= 0 && !TextUtils.isEmpty(badge.type)) {
                        try {
                            sourcingStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#" + badge.type)), index, index + badge.text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        catch (IllegalArgumentException e) {
                            Ln.e(e.getMessage());
                        }
                        catch (IndexOutOfBoundsException e) {
                            Ln.e(e.getMessage());
                        }
                    }
                    if (STYLETYPE_BOLD.equals(badge.styleType)) {
                        try {
                            sourcingStringBuilder.setSpan(new StyleSpan(Typeface.BOLD), index, index + badge.text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        catch (IllegalArgumentException e) {
                            Ln.e(e.getMessage());
                        }
                        catch (IndexOutOfBoundsException e) {
                            Ln.e(e.getMessage());
                        }
                    }
                }
                if (txt_benefit_container != null) {
                    txt_benefit_container.setText(sourcingStringBuilder);
                }
            }
        }

        try {
            String strTitle = txtTitle.getText() != null ? txtTitle.getText().toString() : "";
            String strPrice = txtPrice.getText() != null ? txtPrice.getText().toString() : "";
            String strPriceUnit = txt_price_unit.getText() != null ? txt_price_unit.getText().toString() : "";

            mViewProduct.setContentDescription(strTitle + strPrice + strPriceUnit);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                mViewProduct.setAccessibilityDelegate(new View.AccessibilityDelegate() {
                    @Override
                    public void onPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
                        super.onPopulateAccessibilityEvent(host, event);
                        event.setContentDescription(strTitle + strPrice + strPriceUnit);
                    }
                });
            }
        }
        catch (NullPointerException e) {
            Ln.e(e.getMessage());
        }


        /**
         * 중간 점 처리하는 부분 / UI스레드 사용
         */

        final TvLiveBanner tempTvLiveBanner = tvLiveBanner;

        final TextViewWithListenerWhenOnDraw tempTextView = txt_benefit_container;


        if (tempTextView != null) {
            tempTextView.setOnDrawListener(new TextViewWithListenerWhenOnDraw.OnDrawListener() {
                public void onDraw(View view) {
                    int lineCnt = tempTextView.getLineCount();
                    if (lineCnt > 1) {
                        String full_text = tempTextView.getText().toString();
                        //str0 = 0번째 줄의 텍스트
                        String str0 = full_text.substring(tempTextView.getLayout().getLineStart(0), tempTextView.getLayout().getLineEnd(0));
                        //str1 = 1번째 줄의 텍스트
                        String str1 = full_text.substring(tempTextView.getLayout().getLineStart(1), tempTextView.getLayout().getLineEnd(1));


    //                  새로 그리는 타이밍에서 타이밍 이슈가 발생해서 두번 줄바꿈 하는 문제가 발생하는 것으로 보임 이에 \n이 있을 경우 줄바꿈 로직 수행하지 않음.
                        if (str0.contains("\n") || str1.contains("\n")) {
                            return;
                        }

                        //******************
                        // 점 삭제하는 부분
                        //******************

                        if (str1.startsWith(" · ")) {
                            str1 = str1.substring(3, str1.length());
                        } else if (str1.startsWith("· ")) {
                            str1 = str1.substring(2, str1.length());
                        } else {
                            if (str0.endsWith("· ")) {
                                str0 = str0.substring(0, str0.length() - 3);
                            } else if (str0.endsWith("·")) {
                                str0 = str0.substring(0, str0.length() - 2);
                            } else {
                                String tempStr = "";
                                int num = str0.length() - 2;
                                for (; num > 0; num--) {
                                    tempStr = str0.substring(num, num + 2);
                                    if (!TextUtils.isEmpty(tempStr) &&
                                            ("· ".equals(tempStr) || " ·".equals(tempStr))) {
                                        try {
                                            String tempStr2 = str0.substring(0, num);
                                            str1 = str0.substring(num + 2, str0.length()) + str1;
                                            str0 = tempStr2;
                                        } catch (IndexOutOfBoundsException e) {
                                            Ln.e(e.getMessage());
                                        }
                                        break;
                                    }
                                }
                            }
                        }

                        // 가로가 짧아 줄바꿈이 한번 더 이루어질 경우를 대비하여 두번쨰 줄도 삭제
                        if (str1.endsWith("· ")) {
                            str1 = str1.substring(0, str1.length() - 3);
                        } else if (str1.endsWith("·")) {
                            str1 = str1.substring(0, str1.length() - 2);
                        }

                        tempTextView.setText(str0 + "\n" + str1);

                        SpannableStringBuilder sourcingStringBuilder2 = new SpannableStringBuilder();
                        String strText = tempTextView.getText().toString();
                        sourcingStringBuilder2.append(strText);

                        //점처리 이후 소싱부분  Bold처리
                        if (tempTvLiveBanner.rSource != null && !tempTvLiveBanner.rSource.text.isEmpty()) {

                            //소싱에 대한 컬러 확인 디파인 해야 될것 같은디 ;;
                            String sourceColor = "80111111";
                            //유효하면 변경
                            if (tempTvLiveBanner.rSource.type != null && !tempTvLiveBanner.rSource.type.isEmpty()) {
                                sourceColor = tempTvLiveBanner.rSource.type;
                            }

                            if (STYLETYPE_BOLD.equals(tempTvLiveBanner.rSource.styleType)) {
                                sourcingStringBuilder2.setSpan(new StyleSpan(Typeface.BOLD), 0, tempTvLiveBanner.rSource.text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            sourcingStringBuilder2.setSpan(new ForegroundColorSpan(Color.parseColor("#" + sourceColor)), 0, tempTvLiveBanner.rSource.text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            //benefit_seperator_line 색상 지정
                            sourcingStringBuilder2.setSpan(new ForegroundColorSpan(Color.parseColor("#33111111")), tempTvLiveBanner.rSource.text.length() + 1, tvLiveBanner.rSource.text.length() + 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        if (tempTvLiveBanner.rAllBenefit.size() > 0) {
                            for (ImageBadge badge : tempTvLiveBanner.rAllBenefit) {
                                int index = strText.indexOf(badge.text);
                                if (index >= 0 && !TextUtils.isEmpty(badge.type)) {
                                    try {
                                        sourcingStringBuilder2.setSpan(new ForegroundColorSpan(Color.parseColor("#" + badge.type)), index, index + badge.text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        //benefit_seperator_dot 색상 적용
                                        sourcingStringBuilder2.setSpan(new ForegroundColorSpan(Color.parseColor("#4c111111")), index + badge.text.length() + 1, index + badge.text.length() + 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    } catch (IllegalArgumentException e) {
                                        Ln.e(e.getMessage());
                                    } catch (IndexOutOfBoundsException e) {
                                        Ln.e(e.getMessage());
                                    }
                                }
                                if (STYLETYPE_BOLD.equals(badge.styleType)) {
                                    try {
                                        sourcingStringBuilder2.setSpan(new StyleSpan(Typeface.BOLD), index, index + badge.text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    }
                                    catch (IllegalArgumentException e) {
                                        Ln.e(e.getMessage());
                                    }
                                    catch (IndexOutOfBoundsException e) {
                                        Ln.e(e.getMessage());
                                    }
                                }
                            }
                        }
                        // 무조건 setText 해도 된다. 여기 까지 온거는 이미 줄바꿈이 일어 난 것이기 때문에.
                        tempTextView.setText(sourcingStringBuilder2);
                    }
                }
            });
        }
    }

    /**
     * 새로운 가격정책을 적용하기 위해 신규필드로 기존의 가격정보를 변환한다.
     * 대상 : live, data, mobilelive
     *
     * 방송 컴포넌트 가격의 경우 rentalText / rentalPrice 사용하면 된다 .
     * 상품
     * @param tvLiveBanner 생방송 model
     * @return 변경된 model
     */
    protected TvLiveBanner convertLivePriceData(TvLiveBanner tvLiveBanner) {

        if (isEmpty(tvLiveBanner)) {
            return null;
        }

        //가격표시용 공통모듈에 맞게 데이타 변경
        TvLiveBanner convData = tvLiveBanner;
        //상품정보
        convData.rProductName = tvLiveBanner.productName;
        convData.rLinkUrl = tvLiveBanner.linkUrl;

        //가격정보
        convData.rSalePrice = tvLiveBanner.salePrice;
        convData.rBasePrice = tvLiveBanner.basePrice;
        convData.rExposePriceText = tvLiveBanner.exposePriceText;
        convData.rDiscountRate = tvLiveBanner.priceMarkUp;    //이놈이 퍼센트인데 ;;;

        //바로구매 노출 여부
        //생방송 -> 공통
        if (isNotEmpty(tvLiveBanner.btnInfo3)) {
            convData.rDirectOrdInfo = new DirectOrdInfo();
            convData.rDirectOrdInfo.text = tvLiveBanner.btnInfo3.text;
            convData.rDirectOrdInfo.linkUrl = tvLiveBanner.btnInfo3.linkUrl;
        }

        //라이브톡 노출 여부
        //생방송 -> 공통으로 넘길때
        convData.rLiveTalkYn = tvLiveBanner.liveTalkYn;
        convData.rLiveTalkText = tvLiveBanner.liveTalkText;
        convData.rLiveTalkUrl = tvLiveBanner.liveTalkUrl;

        //방송알림 여부 확인 방송알리미가 없네;
        //편성표에서 공통
        //tvLiveBanner.rBroadAlarmDoneYn = data.product.broadAlarmDoneYn;     //방송 알림 여부
        //tvLiveBanner.rExposPrdName = data.product.exposPrdName;			    //방송 알림에 필요

        //렌탈 상품 관련
        convData.rRentalText = tvLiveBanner.rentalText;
        convData.rRentalPrice = tvLiveBanner.rentalPrice;
        //생방송은 보험 어찌 처리한대???
        convData.rProductType = "true".equals(tvLiveBanner.isRental) ? "R" : "P";

        //혜택관련
        convData.rAllBenefit = tvLiveBanner.allBenefit;
        convData.rSource = tvLiveBanner.source;

        //솔드 아웃 관련 방송중 구매가능관련
        //생방송에 현재는 없다.
        convData.rImageLayerFlag = null;

        //상품평 관련
        convData.rAddTextLeft = tvLiveBanner.addTextLeft;
        convData.rAddTextRight = tvLiveBanner.addTextRight;

        //브렌드 관련
        convData.rBrandNm = tvLiveBanner.brandNm;

        // 딜 여부 방송상품은 무조건 상품.
        convData.deal = "false";

        return convData;
    }
}
