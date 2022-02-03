package gsshop.mobile.v2.web.productDetail.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.web.productDetail.ProductDetailWebActivityV2;
import gsshop.mobile.v2.web.productDetail.dto.NativeProductV2;

public class ProductDetailCouponPmoInfoLayout extends LinearLayout {

    private Context mContext;

    private TextView mTvPmoTxt;
    private TextView mTvPmoPrc;
    private LinearLayout mLlCouponDownloadArea;

    public ProductDetailCouponPmoInfoLayout(Context context) {
        super(context);

        mContext = context;
        initLayout();
    }

    public ProductDetailCouponPmoInfoLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initLayout();
    }

    public ProductDetailCouponPmoInfoLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initLayout();
    }

    /**
     * 레이아웃을 인플레이트한다.
     */
    private void initLayout() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.product_detail_coupon_pmo_layout, this, true);
        initViews();
    }

    /**
     * inflate 된 후에 호출
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void setProductDetailCouponAreaPmo(NativeProductV2.Component component) {
        ((ProductDetailWebActivityV2)mContext).setTextInfoList(mTvPmoTxt, component.pmoText);
        ((ProductDetailWebActivityV2)mContext).setTextInfoList(mTvPmoPrc, component.pmoPrc);

        mLlCouponDownloadArea.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ProductDetailWebActivityV2) mContext).goToLink(component.pmoUrl);
            }
        });
    }

    private void initViews() {
        mTvPmoTxt = findViewById(R.id.tv_pmo_txt);
        mTvPmoPrc = findViewById(R.id.tv_pmo_prc);
        mLlCouponDownloadArea = findViewById(R.id.ll_coupon_download_area);
    }
}
