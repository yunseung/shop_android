package gsshop.mobile.v2.web.productDetail.views;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.support.gtm.AMPEnum;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.productDetail.ProductDetailWebActivityV2;
import gsshop.mobile.v2.web.productDetail.dto.NativeProductV2;

public class ProductDetailCardPmoInfoLayout extends LinearLayout {

    private Context mContext;

    private RelativeLayout mRootView;
    private ImageView mIvCardImage;
    private LinearLayout mLlCardPmoInfoArea;
    private ImageView mIvMore;

    public ProductDetailCardPmoInfoLayout(Context context) {
        super(context);

        mContext = context;
        initLayout();
    }

    public ProductDetailCardPmoInfoLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initLayout();
    }

    public ProductDetailCardPmoInfoLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initLayout();
    }

    /**
     * 레이아웃을 인플레이트한다.
     */
    private void initLayout() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.product_detail_card_pmo_info_layout, this, true);
        initViews();
    }

    public void setProductDetailCardPmoArea(NativeProductV2.Component component) {

        if (component.cardList.size() > 0 && !TextUtils.isEmpty(component.cardList.get(0).pmoText.get(0).textValue)) {
            mRootView.setVisibility(View.VISIBLE);
            if (component.cardList.size() == 1 && component.cardList.get(0).templateType.equalsIgnoreCase("savedMoneyInfo")) {
                ImageUtil.loadImage(mContext, component.cardImgUrl, mIvCardImage, R.drawable.icon_mileage);
            } else {
                ImageUtil.loadImage(mContext, component.cardImgUrl, mIvCardImage, R.drawable.icon_card);
            }

            if (!TextUtils.isEmpty(component.addInfoUrl)) {
                mIvMore.setVisibility(View.VISIBLE);

                mRootView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((ProductDetailWebActivityV2) mContext).goToLink(component.addInfoUrl);

                        ProductDetailWebActivityV2.prdAmpSend(AMPEnum.AMP_PRD_cardPmoInfo_addInfoUrl);
                    }
                });
            }

            mLlCardPmoInfoArea.removeAllViews();
            for (int i = 0; i < component.cardList.size(); i++) {
                ProductDetailCardPmoInfoRow cardPmoInfoRow = new ProductDetailCardPmoInfoRow(mContext);
                mLlCardPmoInfoArea.addView(cardPmoInfoRow);

                if (component.cardList.get(i).pmoText != null) {
                    ((ProductDetailWebActivityV2)mContext).setTextInfoList(cardPmoInfoRow.getPmoTxtTextView(), component.cardList.get(i).pmoText);
                }
                if (component.cardList.get(i).pmoPrc != null) {
                    ((ProductDetailWebActivityV2)mContext).setTextInfoList(cardPmoInfoRow.getPmoPrcTextView(), component.cardList.get(i).pmoPrc);
                }

                if (i >= 1) {
                    cardPmoInfoRow.setTopMargin();
                }
            }
        } else {
            mRootView.setVisibility(View.GONE);
        }
    }

    public RelativeLayout getRooView() {
        return mRootView;
    }

    public void hideTopLine() {
        (findViewById(R.id.view_top_line)).setVisibility(View.GONE);
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
        mIvCardImage = findViewById(R.id.iv_card_image);
        mLlCardPmoInfoArea = findViewById(R.id.ll_card_pmo_info);
        mIvMore = findViewById(R.id.iv_more);

    }
}
