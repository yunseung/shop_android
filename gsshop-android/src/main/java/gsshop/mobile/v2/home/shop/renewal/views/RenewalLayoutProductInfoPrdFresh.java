package gsshop.mobile.v2.home.shop.renewal.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import gsshop.mobile.v2.R;

/**
 * 리뉴얼 되는 상품정보 레이아웃 (공통으로 쓰기 위해 레이아웃 상속받아 선언)
 */
public class RenewalLayoutProductInfoPrdFresh extends RenewalLayoutProductInfo {

    public RenewalLayoutProductInfoPrdFresh(Context context) {
        super(context);
    }

    public RenewalLayoutProductInfoPrdFresh(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RenewalLayoutProductInfoPrdFresh(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initLayout() {
//        super.initLayout();
        LayoutInflater inflater =(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.renewal_layout_product_info_prd_2_fresh, this, true);
    }
}
