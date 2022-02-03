package gsshop.mobile.v2.web.productDetail.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import gsshop.mobile.v2.R;

public class ProductDetailPromotionInfoRow extends LinearLayout {
    private Context mContext;
    private ImageView mIvGradeImg;
    private TextView mTvGradeText;
    private LinearLayout mRootView;

    public ProductDetailPromotionInfoRow(Context context) {
        super(context);
        this.mContext = context;
        initLayout();
    }

    public ProductDetailPromotionInfoRow(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initLayout();
    }

    public ProductDetailPromotionInfoRow(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initLayout();
    }


    private void initLayout() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.product_detail_promotion_info_row, this, true);
        initViews();
    }

    /**
     * inflate 된 후에 호출
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    private void initViews() {
        mIvGradeImg = findViewById(R.id.iv_grade);
        mTvGradeText = findViewById(R.id.tv_grade);
        mRootView = findViewById(R.id.root_view);
    }

    public LinearLayout getRootView() {
        return mRootView;
    }

    public TextView getGradeTextView() {
        return mTvGradeText;
    }

    public ImageView getGradeImageView() {
        return mIvGradeImg;
    }
}
