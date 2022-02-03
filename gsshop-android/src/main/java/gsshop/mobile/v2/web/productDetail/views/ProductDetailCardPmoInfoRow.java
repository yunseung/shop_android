package gsshop.mobile.v2.web.productDetail.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.util.DisplayUtils;

public class ProductDetailCardPmoInfoRow extends LinearLayout {

    private Context mContext;

    private TextView mTvPmoTxt;                 // 어떤 혜택인지 뿌려줄 텍스트뷰. 카드, 적립금 등.
    private TextView mTvPmoPrc;                 // 혜택 금액, 할인율 등.
    private LinearLayout mRootView;

    public ProductDetailCardPmoInfoRow(Context context) {
        super(context);

        mContext = context;
        initLayout();
    }

    public ProductDetailCardPmoInfoRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initLayout();
    }

    public ProductDetailCardPmoInfoRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initLayout();
    }

    /**
     * 레이아웃을 인플레이트한다.
     */
    private void initLayout() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.product_detail_card_pmo_info_row, this, true);
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
        mTvPmoTxt = findViewById(R.id.tv_pmo_txt);
        mTvPmoPrc = findViewById(R.id.tv_pmo_prc);
        mRootView = findViewById(R.id.root_view);
    }

    public TextView getPmoTxtTextView() {
        return mTvPmoTxt;
    }

    public TextView getPmoPrcTextView() {
        return mTvPmoPrc;
    }

    public void setTopMargin() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mRootView.getLayoutParams();
        params.topMargin = DisplayUtils.convertDpToPx(mContext, 4);
        mRootView.setLayoutParams(params);
    }
}
