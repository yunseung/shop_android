package gsshop.mobile.v2.web.productDetail.views;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.support.gtm.AMPEnum;
import gsshop.mobile.v2.util.ClickUtils;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.web.WebUtils;
import gsshop.mobile.v2.web.productDetail.ProductDetailWebActivityV2;
import gsshop.mobile.v2.web.productDetail.dto.NativeProductV2;

import static com.blankj.utilcode.util.EmptyUtils.isEmpty;
import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;
import static gsshop.mobile.v2.ServerUrls.WEB.MSEQ_DEAL_NATIVE_SHARE;
import static gsshop.mobile.v2.ServerUrls.WEB.MSEQ_DEAL_NATIVE_ZZIM;
import static gsshop.mobile.v2.ServerUrls.WEB.MSEQ_PRD_NATIVE_CAL_ALREADY;
import static gsshop.mobile.v2.ServerUrls.WEB.MSEQ_PRD_NATIVE_GO_SAVE_SHIPPING;
import static gsshop.mobile.v2.ServerUrls.WEB.MSEQ_PRD_NATIVE_SHARE;
import static gsshop.mobile.v2.ServerUrls.WEB.MSEQ_PRD_NATIVE_ZZIM;

public class ProductDetailPriceLayout extends LinearLayout {
    private Context mContext;

    private LinearLayout mRootView;
    private TextView mTvPriceText1;
    private TextView mTvPriceText2;
    private TextView mTvPriceText3;
    private TextView mTvPriceText4;
    private TextView mTvPriceText5;
    private TextView mTvPreCalculate;
    private ImageView mIvDiscountInfo;
    private Button mBtnShare;

    private ArrayList<TextView> mTvPriceTextList = new ArrayList<>();

    /**
     * 찜 아이콘
     */
    private ImageView mIvLike;

    private LinearLayout mLlDiscountInfoArea;
    private TextView mTvDiscountText;
    private TextView mTvOriginPrc;

    private LinearLayout mLlAdditionalInfoArea;

    public ProductDetailPriceLayout(Context context) {
        super(context);
        this.mContext = (ProductDetailWebActivityV2) context;
        initLayout();
    }

    public ProductDetailPriceLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = (ProductDetailWebActivityV2) context;
        initLayout();
    }

    private void initLayout() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.product_detail_price_layout, this, true);

        EventBus.getDefault().register(this);
        initViews();
    }

    /**
     * inflate 된 후에 호출
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

    }

    /**
     * 가격영역을 세팅한다.
     *
     * @param component
     */
    public void setProductDetailPriceArea(NativeProductV2.Component component) {

        if (!TextUtils.isEmpty(component.preCalcurateUrl)) {
            mTvPreCalculate.setVisibility(View.VISIBLE);
        }
        // 가격 영역
        for (int i = 0; i < component.priceInfo.size(); i++) {
            mTvPriceTextList.get(i).setVisibility(View.VISIBLE);
            setTextInfo(mTvPriceTextList.get(i), component.priceInfo.get(i));
        }

        // 할인율 영역
        if (component.discountInfo != null && component.discountInfo.originPrc.size() > 0) {
            ((ProductDetailWebActivityV2) mContext).setTextInfoList(mTvDiscountText, component.discountInfo.discountText);
            ((ProductDetailWebActivityV2) mContext).setTextInfoList(mTvOriginPrc, component.discountInfo.originPrc);
            mTvOriginPrc.setPaintFlags(mTvOriginPrc.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            mLlDiscountInfoArea.setVisibility(View.VISIBLE);
        } else {
            mLlDiscountInfoArea.setVisibility(View.GONE);
        }

        // 추가정보 영역 (유류 할증료, 여행사 정보 등)
        mLlAdditionalInfoArea.removeAllViews();
        if (component.additionalList.size() > 0) {
            mLlAdditionalInfoArea.setVisibility(View.VISIBLE);
            for (List<NativeProductV2.TxtInfo> additionalList : component.additionalList) {
                ProductDetailAdditionalInfoRow additionalInfoRow = new ProductDetailAdditionalInfoRow(mContext);
                mLlAdditionalInfoArea.addView(additionalInfoRow);

                ((ProductDetailWebActivityV2) mContext).setTextInfoList(additionalInfoRow.getInfoTextView(), additionalList);
            }
        }

        //찜 세팅
        setFavorite(component);

        //공유하기 세팅
        setShare(component);

        // 미리계산
        setPreCalculate(component);

        // 할인정보
        setDiscountInfo(component);
    }

    private void setPreCalculate(NativeProductV2.Component component) {
        mTvPreCalculate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                WebUtils.goWeb(mContext, component.preCalcurateUrl);

                ProductDetailWebActivityV2.prdAmpSend(AMPEnum.AMP_PRD_saleInfo_preCalcurateUrl);

                // mseq
                ((ProductDetailWebActivityV2) mContext).setWiseLogHttpClient(MSEQ_PRD_NATIVE_CAL_ALREADY);
            }
        });
    }

    private void setDiscountInfo(NativeProductV2.Component component) {
        if (component.discountInfo == null)
            return;

        if (TextUtils.isEmpty(component.discountInfo.discountAddInfoUrl)) {
            mIvDiscountInfo.setVisibility(View.GONE);
        } else {
            mIvDiscountInfo.setVisibility(View.VISIBLE);
            mLlDiscountInfoArea.setOnClickListener(v -> {
                //중복클릭 방지
                if (ClickUtils.work(1000)) {
                    return;
                }

                String scriptName = component.discountInfo.discountAddInfoUrl;
                if (isNotEmpty(scriptName)) {
                    //스크립트 호출
                    ((ProductDetailWebActivityV2) mContext).goToLink(scriptName);

                    ProductDetailWebActivityV2.prdAmpSend(AMPEnum.AMP_PRD_saleInfo_discountAddInfoUrl);
                }
            });
        }
    }

    /**
     * 텍스트뷰의 속성을 세팅한다.
     *
     * @param tv      세팅할 텍스트뷰
     * @param txtInfo API에서 전달받은 정보
     */
    private void setTextInfo(TextView tv, NativeProductV2.TxtInfo txtInfo) {
        if (isEmpty(txtInfo)) {
            return;
        }
        tv.setText(txtInfo.textValue);
        tv.setTypeface(null, "Y".equalsIgnoreCase(txtInfo.boldYN) ? Typeface.BOLD : Typeface.NORMAL);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Float.parseFloat(txtInfo.size));
        tv.setTextColor(DisplayUtils.parseColor(txtInfo.textColor));
        tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                WebUtils.goWeb(mContext, txtInfo.linkUrl);
                ((ProductDetailWebActivityV2) mContext).setWiseLogHttpClient(MSEQ_PRD_NATIVE_GO_SAVE_SHIPPING);
            }
        });
    }

    /**
     * 공유하기를 세팅한다.
     *
     * @param component NativeProductV2.Component
     */
    private void setShare(NativeProductV2.Component component) {
        mBtnShare.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ProductDetailWebActivityV2) mContext).goToLink(component.shareRunUrl);

                // mseq
                if(((ProductDetailWebActivityV2) mContext).getIsDeal()) {
                    ((ProductDetailWebActivityV2) mContext).setWiseLogHttpClient(MSEQ_DEAL_NATIVE_SHARE);
                }
                else {
                    ((ProductDetailWebActivityV2) mContext).setWiseLogHttpClient(MSEQ_PRD_NATIVE_SHARE);
                }
            }
        });
    }

    /**
     * 찜 정보를 세팅한다.
     *
     * @param component NativeProductV2.Component
     */
    private void setFavorite(NativeProductV2.Component component) {
        if (isEmpty(component) || isEmpty(component.favoriteYN) || "X".equalsIgnoreCase(component.favoriteYN)) {
            //찜영역 비노출
            mIvLike.setVisibility(View.GONE);
            return;
        }

        boolean isFavorite = "Y".equalsIgnoreCase(component.favoriteYN);
        mIvLike.setBackgroundResource(isFavorite ? R.drawable.icon_like_on : R.drawable.icon_like_off);
        mIvLike.setVisibility(View.VISIBLE);

        mIvLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //중복클릭 방지
                if (ClickUtils.work(1000)) {
                    return;
                }

                if (!((ProductDetailWebActivityV2) mContext).isLoggedIn()) {
                    ((ProductDetailWebActivityV2) mContext).goToLogin(mContext);
                    return;
                }

                String scriptName = component.favoriteRunUrl;
                if (isNotEmpty(scriptName)) {
                    //스크립트 호출
                    ((ProductDetailWebActivityV2) mContext).goToLink(scriptName);
                }

                ProductDetailWebActivityV2.prdAmpSend(AMPEnum.AMP_PRD_saleInfo_favoriteRunUrl);

                // mseq
                if(((ProductDetailWebActivityV2) mContext).getIsDeal()) { // deal
                    ((ProductDetailWebActivityV2) mContext).setWiseLogHttpClient(MSEQ_DEAL_NATIVE_ZZIM);
                }
                else { // prd
                    ((ProductDetailWebActivityV2) mContext).setWiseLogHttpClient(MSEQ_PRD_NATIVE_ZZIM);
                }
            }
        });
    }

    /**
     * 찜 ON/OFF 수행 후 성공/실패 결과를 이벤트로 받는다.
     *
     * @param event ZzimResponseEvent
     */
    public void onEventMainThread(Events.EventProductDetail.ZzimResponseEvent event) {
        mIvLike.setBackgroundResource(event.isChecked ? R.drawable.icon_like_on : R.drawable.icon_like_off);
    }

    private void initViews() {
        mRootView = findViewById(R.id.root_view);
        mTvPriceText1 = findViewById(R.id.tv_price_text_1);
        mTvPriceText2 = findViewById(R.id.tv_price_text_2);
        mTvPriceText3 = findViewById(R.id.tv_price_text_3);
        mTvPriceText4 = findViewById(R.id.tv_price_text_4);
        mTvPriceText5 = findViewById(R.id.tv_price_text_5);
        mTvPriceTextList.add(mTvPriceText1);
        mTvPriceTextList.add(mTvPriceText2);
        mTvPriceTextList.add(mTvPriceText3);
        mTvPriceTextList.add(mTvPriceText4);
        mTvPriceTextList.add(mTvPriceText5);

        mTvPreCalculate = findViewById(R.id.tv_pre_calculate);
        mBtnShare = findViewById(R.id.btn_share);
        mIvLike = findViewById(R.id.iv_like);
        mIvDiscountInfo = findViewById(R.id.iv_discount_info);

        mLlDiscountInfoArea = findViewById(R.id.ll_discount_info_area);
        mTvDiscountText = findViewById(R.id.tv_discount_text);
        mTvOriginPrc = findViewById(R.id.tv_origin_prc);

        mLlAdditionalInfoArea = findViewById(R.id.ll_additional_info);
    }

    /**
     * 영역별 기본 마진은 12dp 인데, 가격영역 아래 프로모션 영역이 들어올 경우에만 하단 마진을 8dp 로 바꾼다.
     * 프로모션 영역이 multiLine 일 경우 행간 마진 4dp 를 포함하고 있기 때문.
     */
    public void setBottomMargin() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mRootView.getLayoutParams();
        params.bottomMargin = DisplayUtils.convertDpToPx(mContext, 8);
        mRootView.setLayoutParams(params);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }
}
