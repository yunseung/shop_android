package gsshop.mobile.v2.web.productDetail.views;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.util.ClickUtils;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.productDetail.ProductDetailWebActivityV2;
import gsshop.mobile.v2.web.productDetail.dto.NativeProductV2;

public class ProductDetailAddPmoInfoRow extends LinearLayout {

    private Context mContext;

    private RelativeLayout mRootView;

    private ImageView mIvAdditionalImage;
    private ImageView mIvMore;
    private TextView mTvPmoTxt;                 // 어떤 혜택인지 뿌려줄 텍스트뷰. 카드, 적립금 등.

    public ProductDetailAddPmoInfoRow(Context context) {
        super(context);

        mContext = context;
        initLayout();
    }

    public ProductDetailAddPmoInfoRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initLayout();
    }

    public ProductDetailAddPmoInfoRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initLayout();
    }

    /**
     * 레이아웃을 인플레이트한다.
     */
    private void initLayout() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.product_detail_add_pmo_info_row, this, true);
        initViews();
    }

    public void setProductDetailAddPmoRow(NativeProductV2.ItemList itemList) {

        if (itemList != null) {
            mRootView.setVisibility(View.VISIBLE);
            ImageUtil.loadImage(mContext, itemList.iConUrl, mIvAdditionalImage, R.drawable.icon_prd_default);

            if (!TextUtils.isEmpty(itemList.linkUrl)) {
                mIvMore.setVisibility(View.VISIBLE);
                mRootView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //1초 이내의 클릭액션 무시
                        if (ClickUtils.work(1000)) {
                            return;
                        }

                        ((ProductDetailWebActivityV2) mContext).goToLink(itemList.linkUrl);
                    }
                });
            }

            ((ProductDetailWebActivityV2)mContext).setTextInfoList(mTvPmoTxt, itemList.pmoText);
        } else {
            mRootView.setVisibility(View.GONE);
        }
    }

    public void hideTopLine() {
        ((View)findViewById(R.id.view_top_line)).setVisibility(View.GONE);
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
        mRootView = findViewById(R.id.root_view);
        mIvMore = findViewById(R.id.iv_more);
        mIvAdditionalImage = findViewById(R.id.iv_additional_pmo_image);
    }

    public void setTopMargin() {
        LayoutParams params = (LayoutParams) mRootView.getLayoutParams();
        params.topMargin = DisplayUtils.convertDpToPx(mContext, 4);
        mRootView.setLayoutParams(params);
    }
}
