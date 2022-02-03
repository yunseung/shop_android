package gsshop.mobile.v2.web.productDetail.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.flexbox.FlexboxLayout;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.productDetail.ProductDetailWebActivityV2;
import gsshop.mobile.v2.web.productDetail.dto.NativeProductV2;

public class ProductDetailPromotionInfoLayout extends LinearLayout {
    private Context mContext;

    private FlexboxLayout mFblPromotionArea;

    private View mRootView;

    public ProductDetailPromotionInfoLayout(Context context) {
        super(context);
        mContext = context;
        initLayout();
    }

    public ProductDetailPromotionInfoLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initLayout();
    }

    private void initLayout() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.product_detail_promotion_info_layout, this, true);
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
     * 프로모션 영역을 세팅한다.
     *
     * @param component
     */
    public void setProductDetailPromotionArea(NativeProductV2.Component component) {
        mFblPromotionArea.removeAllViews();
        if (component.gradePmoInfo.size() > 0) {
            mFblPromotionArea.setVisibility(View.VISIBLE);
            for (int i = 0; i < component.gradePmoInfo.size(); i++) {
                ProductDetailPromotionInfoRow promotionInfoRow = new ProductDetailPromotionInfoRow(mContext);
                mFblPromotionArea.addView(promotionInfoRow);

                String gradeTextValue = component.gradePmoInfo.get(i).gradeText.get(0).textValue.trim();
                if (("VVIP").equalsIgnoreCase(gradeTextValue)) {
                    ImageUtil.loadImageFit(mContext, component.gradePmoInfo.get(i).gradeImgUrl, promotionInfoRow.getGradeImageView(), R.drawable.icon_grade_vvip);
                } else if (("VIP").equalsIgnoreCase(gradeTextValue)) {
                    ImageUtil.loadImageFit(mContext, component.gradePmoInfo.get(i).gradeImgUrl, promotionInfoRow.getGradeImageView(), R.drawable.icon_grade_vip);
                } else {
                    ImageUtil.loadImageFit(mContext, component.gradePmoInfo.get(i).gradeImgUrl, promotionInfoRow.getGradeImageView(), R.drawable.icon_grade_gold);
                }
                ((ProductDetailWebActivityV2)mContext).setTextInfoList(promotionInfoRow.getGradeTextView(), component.gradePmoInfo.get(i).gradeText);
            }
        }
    }

    private void initViews() {
        mFblPromotionArea = findViewById(R.id.flb_promotion_info_area);
        mRootView = findViewById(R.id.root_view);
    }

    public void setBottomMargin() {
        if (mRootView != null) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mRootView.getLayoutParams();
            params.bottomMargin = 0;
            mRootView.setLayoutParams(params);
        }
    }
}
