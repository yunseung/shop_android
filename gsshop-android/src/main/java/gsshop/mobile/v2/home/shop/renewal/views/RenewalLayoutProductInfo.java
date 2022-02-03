package gsshop.mobile.v2.home.shop.renewal.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.EmptyUtils;
import com.gsshop.mocha.ui.util.ViewUtils;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.ProductInfoUtil;
import gsshop.mobile.v2.home.shop.renewal.utils.SetDtoUtil;
import gsshop.mobile.v2.home.shop.renewal.utils.dto.ProductInfo;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.util.Ln;

/**
 * 리뉴얼 되는 상품정보 레이아웃 (공통으로 쓰기 위해 레이아웃 상속받아 선언)
 */
public class RenewalLayoutProductInfo extends LinearLayout {

    private final static String COMMON_RENTALTEXT = "월 렌탈료";
    private final static String COMMON_DISPLAY_RENTALTEXT = "월";

    //가격영역에 상담신청상품을 표시할 경우 폰트사이즈 변경을 위해
    private static final float COUNSELING_FONT_SIZE = 16;
    private static final float PRICE_FONT_SIZE = 19;

    /**
     * Context
     */
    protected Context mContext;

    //가격 표시 영역
    private TextView txt_base_price;
    private TextView txt_base_price_unit;
    private TextView txtPrice;
    private TextView txt_price_unit;
    private TextView txt_discount_rate; //할인율
    private TextView txt_rental_text;

    //상품평점
    private TextView txt_review_avr;
    //상품카운트
    private TextView txt_review_count;
    private Button mBtnCart;

    //브랜드명 관련
    private TextView txt_brand_name;
    //상품명 표시 영역
    protected TextView txtTitle;

    // 혜택 영역
    private RenewalLayoutProductBenefitInfo mLayoutBenefit;

    private View mViewReview;

    // 이건 왜 멤버로 놔뒀는지 모르겠지만..
    private SpannableStringBuilder priceStringBuilder = new SpannableStringBuilder();

    private View mViewProduct;

    // 이 레이아웃을 상속받은 여러 뷰 중 카트 영역이 필요한가를 판단하는 flag..
    private boolean mIsNeedCart = false;

    private LinearLayout mLayoutBottom;

    //가격, 상품평 영역
    private LinearLayout mLlPriceReview;

    //PrdCB1VIPGbaVH 용도 (상품명 아래 브랜드이동 영역)
    private LinearLayout mLlBrandShop;
    private TextView mTxtBrandShop;

    /**
     * 기본 생성자
     *
     * @param context
     */
    public RenewalLayoutProductInfo(Context context) {
        this(context, null);
    }

    public RenewalLayoutProductInfo(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RenewalLayoutProductInfo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initLayout();
    }

    /**
     * 최초 inflate 될 layout을 설정, inflate 되는 놈이 바뀌면 상속 받아서 super 호출 하지 말고 상속받는 initLayout에서 다른 레이아웃 xml을 inflate
     */
    protected void initLayout() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.renewal_layout_product_info, this, true);
    }

    /**
     * inflate 된 후에 호출
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initViews();
    }

    public void clickBuyButton() {
        if (mLayoutBenefit != null) {
            mLayoutBenefit.txt_buy.performClick();
        }
    }

    public TextView getBuyButton() {
        return mLayoutBenefit == null? null : mLayoutBenefit.txt_buy;
    }

    /**
     * 최초 이닛할 수 있는 뷰를 init
     */
    protected void initViews() {
        setNeedCart(false);

        txtTitle = (TextView) findViewById(R.id.txt_title);
        txt_base_price = (TextView) findViewById(R.id.txt_base_price);
        txt_base_price_unit = (TextView) findViewById(R.id.txt_base_price_unit);
        txtPrice = (TextView) findViewById(R.id.txt_price);
        txt_price_unit = (TextView) findViewById(R.id.txt_price_unit);
        txt_discount_rate = (TextView) findViewById(R.id.txt_discount_rate);
        txt_rental_text = (TextView) findViewById(R.id.txt_rental_text);

        //상품평 관련
        txt_review_avr = findViewById(R.id.txt_review_avr);
        txt_review_count = findViewById(R.id.txt_review_count);

        mBtnCart = findViewById(R.id.btn_cart);

        //브랜드명 관련
        txt_brand_name = (TextView) findViewById(R.id.txt_brand_name);
        //1단 640 하단조정을 위한 더미뷰
        mLayoutBenefit = findViewById(R.id.layout_benefit);
        // 리뷰 (상품평) 영역
        mViewReview = findViewById(R.id.txt_review_area);

        mViewProduct = findViewById(R.id.product_layout);

        mLayoutBottom = findViewById(R.id.layout_bottom);

        mLlPriceReview = findViewById(R.id.ll_price_review);

        mLlBrandShop = findViewById(R.id.ll_brand_shop);
        mTxtBrandShop = (TextView) findViewById(R.id.txt_brand_shop);
    }

    protected void setNeedCart(boolean isNeed) {
        mIsNeedCart = isNeed;
    }

    /**
     * 뷰 설정 하는 함수, 인자값은 설정할 뷰의 개수 만큼?
     */
    public void setViews(ProductInfo info) {
        setViews(info, null);
    }

    public void setViews(ProductInfo info, SetDtoUtil.BroadComponentType componentType) {
        try {
            txt_discount_rate.setPadding(0, 0, 0, 0);
            priceStringBuilder.clear();
            //상품정보 표시 영역
            txtTitle.setText("");

            //상품정보 가격 표시 영역
            txtPrice.setText("");
            txt_discount_rate.setText("");
            txt_rental_text.setText("");
            txt_rental_text.setVisibility(View.GONE);
            txt_base_price.setVisibility(View.GONE);
            txt_base_price_unit.setVisibility(View.GONE);

            txtPrice.setTextSize(PRICE_FONT_SIZE);
        } catch (NullPointerException e) {
            Ln.e(e.getMessage());
        }

        //가격, 혜택영역에 브랜드샵 영역 노출
        ViewUtils.hideViews(mLlBrandShop);
        if (SetDtoUtil.BroadComponentType.product_c_b1_vip_gba.equals(componentType)) {
            ViewUtils.hideViews(mLlPriceReview, mLayoutBenefit);
            ViewUtils.showViews(mLlBrandShop);
            if (EmptyUtils.isNotEmpty(info.brandNm)) {
                mTxtBrandShop.setText(info.brandNm);
                if (EmptyUtils.isNotEmpty(info.brandLinkUrl)) {
                    mLlBrandShop.setOnClickListener(v -> WebUtils.goWeb(mContext, info.brandLinkUrl));
                }
            } else {
                mLlBrandShop.setVisibility(View.INVISIBLE);
            }
        }

        if (SetDtoUtil.BroadComponentType.signature.equals(componentType) ||
                SetDtoUtil.BroadComponentType.tv_new.equals(componentType) ||
                SetDtoUtil.BroadComponentType.mobilelive_prd_list_type1.equals(componentType) ||
                SetDtoUtil.BroadComponentType.mobilelive_prd_list_type2.equals(componentType)) {
            LayoutParams params = (LayoutParams) mLayoutBottom.getLayoutParams();

            if (SetDtoUtil.BroadComponentType.tv_new.equals(componentType)) {
                //탑마진
                params.topMargin = DisplayUtils.convertDpToPx(mContext, 0);
            }
            else {
                //탑마진
                params.topMargin = DisplayUtils.convertDpToPx(mContext, -3);
                //혜택 상단 마진 (노트10에서 무이자 텍스트 하단 짤림현상 개선)
                if (mLayoutBenefit != null) {
                    LayoutParams params3 = (LayoutParams) mLayoutBenefit.getLayoutParams();
                    params3.topMargin = DisplayUtils.convertDpToPx(mContext, 0);
                    mLayoutBenefit.setLayoutParams(params3);
                }
            }

            mLayoutBottom.setLayoutParams(params);

            //상품명과 가격사이 마진
            if (EmptyUtils.isNotEmpty(mLlPriceReview)) {
                LayoutParams params2 = (LayoutParams) mLlPriceReview.getLayoutParams();
                params2.topMargin = DisplayUtils.convertDpToPx(mContext, 2);
                mLlPriceReview.setLayoutParams(params2);
            }
            //상품명 폰트크기
            txt_brand_name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            //가격
            txtPrice.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            txt_base_price_unit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            txt_discount_rate.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            txt_base_price.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            txt_base_price_unit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            txt_price_unit.setTextSize(TypedValue.COMPLEX_UNIT_DIP,14 );
        }
        // 브랜드 명 노출
        if (txt_brand_name != null) {
            // 브랜드 이름은 Spannable로 해서 해당 view는 gone
            txt_brand_name.setVisibility(View.GONE);
        }

        if (DisplayUtils.isValidString(info.productName) && txtTitle != null) {
            //쇼핑라이브 상풀리스트 화면은 상품명 2줄 노출
            if (SetDtoUtil.BroadComponentType.mobilelive_prd_list_type2.equals(componentType)) {
                txtTitle.setMaxLines(2);
                String prdName = info.productName;
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < prdName.length(); i++) {
                    sb.append(prdName.charAt(i));
                    sb.append("\u200B");
                }
                info.productName = sb.toString();
            }

            txtTitle.setText(info.productName);

            if (DisplayUtils.isValidString(info.brandNm) &&
                    !SetDtoUtil.BroadComponentType.product_c_b1_vip_gba.equals(componentType)) {
                SpannableStringBuilder titleStringBuilder = new SpannableStringBuilder();
                titleStringBuilder.append(info.brandNm);
                titleStringBuilder.setSpan(new StyleSpan(Typeface.BOLD), 0, titleStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                titleStringBuilder.append(" " + info.productName);
                txtTitle.setText(titleStringBuilder);
            } else {
                txtTitle.setText(info.productName);
            }
        }

        //상품 상품평점
        if (txt_review_avr != null) {
            if (DisplayUtils.isValidString(info.addTextLeft)) {
                txt_review_avr.setVisibility(View.VISIBLE);
                txt_review_avr.setText(info.addTextLeft);
                txt_review_avr.setTextColor(Color.parseColor("#a3111111")); //제플린에는 99111111이지만 구두로 변경
            } else {
                txt_review_avr.setVisibility(View.GONE);
            }
        }

        //상품 상품평카운트
        if (txt_review_count != null) {
            if (DisplayUtils.isValidString(info.addTextRight)) {
                txt_review_count.setVisibility(View.VISIBLE);
                txt_review_count.setText(info.addTextRight);
                txt_review_count.setTextColor(Color.parseColor("#7a111111"));
            } else {
                txt_review_count.setVisibility(View.GONE);
            }
        }

        //상품 상품평평점 + 상품카운트 = 전체 영역 처리
        if (DisplayUtils.isValidString(info.addTextLeft) && DisplayUtils.isValidString(info.addTextRight)) {
            ViewUtils.showViews(txt_review_avr, txt_review_count);
        }

        if (mIsNeedCart) {
            if (txt_review_avr != null && txt_review_count != null && mBtnCart != null) {
                if (txt_review_avr.getVisibility() != VISIBLE && txt_review_count.getVisibility() != VISIBLE && mBtnCart.getVisibility() != VISIBLE) {
                    ViewUtils.hideViews(mViewReview);
                } else {
                    ViewUtils.showViews(mViewReview);
                }
            } else {
                ViewUtils.hideViews(mViewReview);
            }
        } else {
            if (txt_review_avr != null && txt_review_count != null) {
                if (txt_review_avr.getVisibility() != VISIBLE && txt_review_count.getVisibility() != VISIBLE) {
                    ViewUtils.hideViews(mViewReview);
                } else {
                    ViewUtils.showViews(mViewReview);
                }
            } else {
                ViewUtils.hideViews(mViewReview);
            }
        }


        // 퍼센트값으로 표현되는 할인률이 있을 경우에만 base price 표시
        // base price가 0이거나 salePrice가 basePrice보다 크면 basePrice 표시 안함
        int salePrice = DisplayUtils.getNumberFromString(info.salePrice);
        int basePrice = DisplayUtils.getNumberFromString(info.basePrice);

        // 베이스가 처리로직: 할인률 있으면
        if (info.discountRate != null && !info.discountRate.isEmpty() && Integer.parseInt(info.discountRate) > Keys.ZERO_DISCOUNT_RATE
                && !SetDtoUtil.BroadComponentType.product_c_b1.equals(componentType)) {
            //베이스가 로직==찌익그은가, 찌익 그은가는 할인율이 없거나, 세일가보다 비싸면 안보여야함 디폴트는 안보임
            if (DisplayUtils.isValidNumberStringExceptZero(info.basePrice) && (salePrice < basePrice)) {
                if (txt_base_price != null) {
                    txt_base_price.setText(DisplayUtils.getFormattedNumber(info.basePrice));
                    txt_base_price.setVisibility(View.VISIBLE);
                    txt_base_price.setPaintFlags(txt_base_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }

                if (txt_base_price_unit != null) {
                    txt_base_price_unit.setText(info.exposePriceText);
                    txt_base_price_unit.setVisibility(View.VISIBLE);
                }

                if (txt_discount_rate != null) {
                    txt_discount_rate.append(ProductInfoUtil.getDiscountCommon(mContext, info.discountRate));
                    txt_discount_rate.setEllipsize(null);
                    txt_discount_rate.setVisibility(View.VISIBLE);
                }
            } else {
                ViewUtils.hideViews(txt_base_price, txt_base_price_unit, txt_discount_rate);
            }
        }

        // 세일 price 처리 로직 : 있으면 그려 / else 면 안그려
        if (DisplayUtils.isValidString(info.salePrice)) {
            String salePriceText = DisplayUtils.getFormattedNumber(info.salePrice);
            if (txt_price_unit != null) {
                txt_price_unit.setText(info.exposePriceText);
                txt_price_unit.setVisibility(View.VISIBLE);
            }

            // 굳이 StringBuilder에 append 해서 다시 textView에다 append하는 이유는 모르겠음.
            priceStringBuilder.append(salePriceText);
            if (txtPrice != null) {
                txtPrice.append(priceStringBuilder);
                txtPrice.setVisibility(View.VISIBLE);
            }
        } else {
            ViewUtils.hideViews(txtPrice, txt_price_unit);
        }

        // deal 상품이면서 렌탈, 여행, 시공, 딜 등의 무형 상품일 때 ( 렌탈이 아닌경우에는 rRentalText 없다고 확답 받음 )
        // 혹은 deal 상품이 아니면서 렌탈일 때
        if (("true".equals(info.deal) && ("R".equals(info.productType)/* 렌탈*/ ||
                "T".equals(info.productType)/*여행*/ || "U".equals(info.productType)/*딜 시공*/ ||
                "S".equals(info.productType)/*상품 시공*/)) ||
                ("false".equals(info.deal) && "R".equals(info.productType)/* 렌탈*/)) {
            //항목1 무언가 써있으면???
            // 렌탈 에서만 rRentalText 를 비교한다.
            if ("R".equals(info.productType) && info.rentalText != null && !info.rentalText.isEmpty()) {
                //항목1에 "월 렌탈료"가 써있으면 -> "월"로 변경
                //API에서 항목1에 "월"이 내려올 수 있는 조건이 추가되어 "월"도 "월 렌탈료"와 동일로직으로 처리하도록 조건 추가
                if (COMMON_RENTALTEXT.equals(info.rentalText) ||
                        COMMON_DISPLAY_RENTALTEXT.equals(info.rentalText)) {
                    if (txt_rental_text != null) {
                        txt_rental_text.setText(COMMON_DISPLAY_RENTALTEXT);
                        txt_rental_text.setVisibility(View.VISIBLE);
                    }

                    //"월" + 항목2(rRentalPrice) 값이 있으면 뿌린다
                    if (info.rentalPrice != null && !info.rentalPrice.isEmpty() && !info.rentalPrice.startsWith("0")) {
                        if (txtPrice != null) {
                            txtPrice.setText(info.rentalPrice);
                            txtPrice.setVisibility(View.VISIBLE);
                        }
                        // 원자 지우기 ,
                        ViewUtils.hideViews(txt_price_unit);
                    } else {
                        //렌탈이지만 "월 렌탈료"가 안써있으면 다 우지고 "상담신청상품" 세긴다.
                        if (txtPrice != null) {
                            txtPrice.setText(R.string.common_rental_title);
                            txtPrice.setVisibility(View.VISIBLE);
                            txtPrice.setTextSize(COUNSELING_FONT_SIZE);
                        }
                        ViewUtils.hideViews(txt_rental_text, txt_price_unit);
                    }
                }
                // else 는 무조건 엔터 칩시다 접었을 때 보기가 힘듭니다.
                else {//렌탈인데 월이 아니면,
                    if (info.rentalPrice != null && !info.rentalPrice.isEmpty() && !info.rentalPrice.startsWith("0")) {
                        if (txtPrice != null) {
                            txtPrice.setText(info.rentalPrice);
                            txtPrice.setVisibility(View.VISIBLE);
                        }
                        // rRentalPrice 게 없으니 "월" 영역 지우기 // 원자 지우기 ,
                        ViewUtils.hideViews(txt_rental_text, txt_price_unit);
                    } else {
                        //"월" 을 지우고, "상담신청상품" 세긴다.
                        if (txtPrice != null) {
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
            else if (info.rentalPrice != null && !info.rentalPrice.isEmpty() && !info.rentalPrice.startsWith("0")) {
                if (txtPrice != null) {
                    txtPrice.setText(info.rentalPrice);
                    txtPrice.setVisibility(View.VISIBLE);
                }
                // rRentalPrice 게 없으니 "월" 영역 지우기 // 원자 지우기 ,
                ViewUtils.hideViews(txt_rental_text, txt_price_unit);
            } else {
                //렌탈이지만 "월 렌탈료"가 안써있으면 다 지우고 "상담 전용 상품입니다." 세긴다.
                //txtPrice.setText("상담 전용 상품입니다.");
                if (txtPrice != null) {
                    txtPrice.setText(R.string.common_rental_title);
                    txtPrice.setVisibility(View.VISIBLE);
                    txtPrice.setTextSize(COUNSELING_FONT_SIZE);
                }

                // rRentalPrice 게 없으니 "월" 영역 지우기 // 원자 지우기 ,
                ViewUtils.hideViews(txt_rental_text, txt_price_unit);
            }
        } else {
            //렌탈 아닌데 상담 전용 상품 뜨는경우가 없다고 하자
            ViewUtils.hideViews(txt_rental_text);

            //보험일때, 계속 추가 할게요
            if ("I".equals(info.productType)) {
                //보험이면 타이틀만 보이자
                //layout_price.setVisibility(View.GONE);
                // Null pointer 발생하는 경우 있음. null이라도 안죽는 함수로 변경
                ViewUtils.hideViews(txtPrice, txt_price_unit, txt_base_price, txt_base_price_unit, txt_discount_rate);
            } else {
                if (txtPrice != null) {
                    txtPrice.setText(info.salePrice);
                    txtPrice.setVisibility(View.VISIBLE);
                    txt_price_unit.setVisibility(View.VISIBLE);
                }
            }
        }

        // 혜택 뷰에 값 설정.
        if (componentType == null) {
            componentType = SetDtoUtil.BroadComponentType.product;
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
        } catch (NullPointerException e) {
            Ln.e(e.getMessage());
        }

        if (mLayoutBenefit != null) {
            mLayoutBenefit.setViews(info, componentType);
        }
    }
}
