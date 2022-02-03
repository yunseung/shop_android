package gsshop.mobile.v2.web.productDetail.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import gsshop.mobile.v2.R;

public class ProductDetailAdditionalInfoRow extends LinearLayout {
    private Context mContext;
    private TextView mTvInfo;

    public ProductDetailAdditionalInfoRow(Context context) {
        super(context);
        this.mContext = context;
        initLayout();
    }

    public ProductDetailAdditionalInfoRow(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initLayout();
    }

    public ProductDetailAdditionalInfoRow(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initLayout();
    }


    private void initLayout() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.product_detail_additional_info_row, this, true);
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
        mTvInfo = findViewById(R.id.tv_info);
    }

    public TextView getInfoTextView() {
        return mTvInfo;
    }
}
