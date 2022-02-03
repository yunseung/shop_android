package gsshop.mobile.v2.web.productDetail.views;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.core.content.ContextCompat;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.support.gtm.AMPEnum;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;
import gsshop.mobile.v2.web.productDetail.ProductDetailWebActivityV2;
import gsshop.mobile.v2.web.productDetail.dto.NativeProductV2;

public class ProductDetailTitleLayout extends LinearLayout {
    private Context mContext;

    private LinearLayout mLlBrandArea;                          // 브랜드 영역
    private ImageView mIvBrandIcon;
    private TextView mTvBrandName;                              // 브랜드 이름

    private TextView mTvPromotion;                      // 상품 이름 위에 올라가 있는 부가 정보보
    private TextView mTvExpoName;                                // 상품 이름
    private TextView mTvSubInfo;                          // 원산지 표기

    private LinearLayout mLlRatingArea;                         // 별점 표시 영역
    private AppCompatRatingBar mRatingBar;                      // 별점
    private TextView mTvReviewText;                               // 별점 텍스트
    private Button mBtnReviewMore;

    private LinearLayout mLlBadgeInfo;
    private TextView mTvEngInfoText;

    /**
     * 찜 아이콘
     */
    private CheckBox mCbLike;

    public ProductDetailTitleLayout(Context context) {
        super(context);
        this.mContext = (ProductDetailWebActivityV2) context;
        initLayout();
    }

    public ProductDetailTitleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = (ProductDetailWebActivityV2) context;
        initLayout();
    }

    private void initLayout() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.product_detail_title_layout, this, true);
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
     * 타이틀영역 세팅한다.
     *
     * @param component
     */
    public void setProductDetailTitleArea(NativeProductV2.Component component) {
        if (component.brandInfo != null) {
            mLlBrandArea.setVisibility(View.VISIBLE);
            ImageUtil.loadImageBadge(mContext, component.brandInfo.brandLogoUrl, mIvBrandIcon, R.drawable.noimage_78_78, ImageUtil.BaseImageResolution.HD);
            if (component.brandInfo.brandTitle != null) {
                ((ProductDetailWebActivityV2) mContext).setTextInfoList(mTvBrandName, component.brandInfo.brandTitle);
            }
            mLlBrandArea.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    WebUtils.goWeb(mContext, component.brandInfo.brandLinkUrl);

                    ProductDetailWebActivityV2.prdAmpSend(AMPEnum.AMP_PRD_PRDNMINFO_BRANDINFO);
                }
            });
        }

        // badgeInfo (현재는 와인매장에 한해 사용됨)
        if (component.badgeInfo != null && component.badgeInfo.size() > 0) {
            mLlBadgeInfo.setVisibility(View.VISIBLE);
            for (int i = 0; i < component.badgeInfo.size(); i++) {
                String badge = component.badgeInfo.get(i);
                TextView tvBadge = (TextView) View.inflate(mContext, R.layout.text_view_line_e5e5e5, null);
                tvBadge.setText(badge);

                if (i > 0) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    params.leftMargin = DisplayUtils.convertDpToPx(mContext, 2);
                    tvBadge.setLayoutParams(params);
                }

                mLlBadgeInfo.addView(tvBadge);
            }
        } else {
            mLlBadgeInfo.setVisibility(View.GONE);
        }

        //////////////// 상품 정보 영역
        if (component.promotionText != null) {
            ((ProductDetailWebActivityV2) mContext).setTextInfoList(mTvPromotion, component.promotionText);
        }

        if (component.engInfoText != null) {
            mTvEngInfoText.setVisibility(View.VISIBLE);
            ((ProductDetailWebActivityV2) mContext).setTextInfoList(mTvEngInfoText, component.engInfoText);
        } else {
            mTvEngInfoText.setVisibility(View.GONE);
        }

        ((ProductDetailWebActivityV2) mContext).setTextInfoList(mTvExpoName, component.expoName);

        ((ProductDetailWebActivityV2) mContext).setTextInfoList(mTvSubInfo, component.subInfoText);                          // 상품 하단 부가정보

        if (component.reviewInfo != null) {
            mLlRatingArea.setVisibility(View.VISIBLE);
            ((ProductDetailWebActivityV2) mContext).setTextInfoList(mTvReviewText, component.reviewInfo.reviewText);

            if (!TextUtils.isEmpty(component.reviewInfo.reviewPoint)) {
                mRatingBar.setRating(Float.parseFloat(component.reviewInfo.reviewPoint));
            }

            if (!TextUtils.isEmpty(component.reviewInfo.linkUrl)) {
                mBtnReviewMore.setVisibility(View.VISIBLE);
                mBtnReviewMore.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((ProductDetailWebActivityV2) mContext).goToLink(component.reviewInfo.linkUrl);

                        ProductDetailWebActivityV2.prdAmpSend(AMPEnum.AMP_PRD_prdNmInfo_reviewInfo);
                    }
                });
            }
        }

        mLlRatingArea.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ProductDetailWebActivityV2) mContext).goToLink(component.reviewInfo.linkUrl);

                ProductDetailWebActivityV2.prdAmpSend(AMPEnum.AMP_PRD_prdNmInfo_reviewInfo);
            }
        });
    }

    private void initViews() {
        mLlBrandArea = findViewById(R.id.ll_brand_area);
        mIvBrandIcon = findViewById(R.id.iv_brand_icon);
        mTvBrandName = findViewById(R.id.tv_brand_name);

        mTvPromotion = findViewById(R.id.tv_promotion);
        mTvExpoName = findViewById(R.id.tv_expo_name);
        mTvSubInfo = findViewById(R.id.tv_sub_info);

        mLlRatingArea = findViewById(R.id.ll_rating_area);
        mRatingBar = findViewById(R.id.rating_stars);
        mTvReviewText = findViewById(R.id.tv_review_text);
        mBtnReviewMore = findViewById(R.id.btn_more);

        mLlBadgeInfo = findViewById(R.id.ll_badge_info);
        mTvEngInfoText = findViewById(R.id.tv_eng_info_text);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
