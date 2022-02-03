package gsshop.mobile.v2.web.productDetail.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.web.productDetail.dto.NativeProductV2;

public class ProductDetailAddPmoInfoLayout extends LinearLayout {

    private Context mContext;

    private LinearLayout mRootView;
    private boolean mHideTopLine = false;

    public ProductDetailAddPmoInfoLayout(Context context) {
        super(context);

        mContext = context;
        initLayout();
    }

    public ProductDetailAddPmoInfoLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initLayout();
    }

    public ProductDetailAddPmoInfoLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initLayout();
    }

    /**
     * 레이아웃을 인플레이트한다.
     */
    private void initLayout() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.product_detail_add_pmo_info_layout, this, true);
        initViews();
    }

    public void setProductDetailAddPmoArea(NativeProductV2.Component component) {
        mRootView.removeAllViews();
        if (component.itemList.size() > 0) {

            for (int i = 0; i < component.itemList.size(); i++) {
                ProductDetailAddPmoInfoRow row = new ProductDetailAddPmoInfoRow(mContext);
                row.setProductDetailAddPmoRow(component.itemList.get(i));
                if (mHideTopLine && i == 0) {
                    row.hideTopLine();
                }
                mRootView.addView(row);
            }
        }
    }

    public void hideTopLine() {
        mHideTopLine = true;
    }

    /**
     * inflate 된 후에 호출
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    private void initViews() {
        mRootView = findViewById(R.id.root_view);
    }
}
