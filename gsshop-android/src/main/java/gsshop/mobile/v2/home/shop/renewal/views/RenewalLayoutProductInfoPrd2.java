package gsshop.mobile.v2.home.shop.renewal.views;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gsshop.mocha.ui.util.ViewUtils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.nio.charset.Charset;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.main.paramsBasket;
import gsshop.mobile.v2.home.shop.network.BasketPostAsyncController;
import gsshop.mobile.v2.home.shop.renewal.utils.SetDtoUtil;
import gsshop.mobile.v2.home.shop.renewal.utils.dto.ProductInfo;
import gsshop.mobile.v2.user.User;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.util.Ln;

/**
 * 리뉴얼 되는 상품정보 레이아웃 (공통으로 쓰기 위해 레이아웃 상속받아 선언)
 */
public class RenewalLayoutProductInfoPrd2 extends RenewalLayoutProductInfo {

    private Button mBtnCart;
    private String mNavigationId;
    private TextView mTxtTitleOneLine;
    private TextView mTxtTitlePadding;
    private LinearLayout mLayoutTitle;

    public RenewalLayoutProductInfoPrd2(Context context) {
        super(context);
    }

    public RenewalLayoutProductInfoPrd2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RenewalLayoutProductInfoPrd2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initLayout() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.renewal_layout_product_info_prd_2, this, true);
    }

    @Override
    protected void initViews() {
        super.initViews();
        mBtnCart = findViewById(R.id.btn_cart);
        mTxtTitleOneLine = findViewById(R.id.txt_title_one_line);
        mTxtTitlePadding = findViewById(R.id.txt_title_padding);
        mLayoutTitle = findViewById(R.id.layout_title);
    }

    public void setViews(ProductInfo info, SetDtoUtil.BroadComponentType componentType, String navigationId) {
        mNavigationId = navigationId;
        setViews(info, componentType);
    }

    @Override
    protected void setNeedCart(boolean isNeed) {
        super.setNeedCart(true);
    }

    @Override
    public void setViews(ProductInfo info, SetDtoUtil.BroadComponentType componentType) {
        if (info.basket != null) {
            if (!TextUtils.isEmpty(info.basket.url)) {
                mBtnCart.setVisibility(View.VISIBLE);
                mBtnCart.setOnClickListener(v -> {
                    User user = User.getCachedUser();
                    if (user == null || user.customerNumber.length() < 2) {
                        Intent intent = new Intent(Keys.ACTION.LOGIN);
                        intent.putExtra(Keys.INTENT.FOR_GS_SUPER, true);
                        intent.putExtra(Keys.INTENT.NAVIGATION_ID, mNavigationId);
                        mContext.startActivity(intent);
                    } else {
                        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<String, Object>();
                        String baseketUrl = info.basket.url;
                        if (WebUtils.isExternalUrl(baseketUrl)) {
                            try {
                                Uri uri = Uri.parse(baseketUrl);
                                String host = uri.getHost();
                                if (WebUtils.DIRECT_ORD_HOST.equals(host)) {
                                    String query = Uri.parse(baseketUrl).getEncodedQuery();
                                    ((HomeActivity)mContext).visibleOrderDirectWebView(query);
                                }
                            }
                            catch (NullPointerException e) {
                                Ln.e(e.getMessage());
                            }
                        } else {
                            for (paramsBasket basket : info.basket.params) {
                                formData.add(basket.name, basket.value);
                            }

                            HttpHeaders headers = new HttpHeaders();
                            headers.set("Connection", "Close");

                            MediaType mediaType = new MediaType("application", "x-www-form-urlencoded", Charset.forName("UTF-8"));
                            headers.setContentType(mediaType);

                            org.springframework.http.HttpEntity<MultiValueMap> requestEntity =
                                    new org.springframework.http.HttpEntity<>(formData, headers);

                            BasketPostAsyncController.OnPostAsyncListener postListener =
                                    result -> onCartSelected(result);

                            new BasketPostAsyncController(mContext, postListener).execute(info.basket, requestEntity, postListener);
                        }
                    }
                });
            } else {
                mBtnCart.setVisibility(View.GONE);
            }
        }

        super.setViews(info, componentType);

        if (SetDtoUtil.BroadComponentType.mobilelive_prd_list_type1.equals(componentType)) {
            //모바일라이브 메인 상품은 상품명 1줄 표시
            mTxtTitleOneLine.setText(txtTitle.getText());
            ViewUtils.hideViews(txtTitle);
            ViewUtils.showViews(mTxtTitleOneLine);

            //상단 마진 0
            LayoutParams lp = (LayoutParams) mLayoutTitle.getLayoutParams();
            lp.topMargin = DisplayUtils.convertDpToPx(mContext, 0);
            lp.bottomMargin = DisplayUtils.convertDpToPx(mContext, 0);
            mLayoutTitle.setLayoutParams(lp);

            ViewUtils.hideViews(mTxtTitlePadding);
        }
    }

    /**
     * 장바구니 눌렀을때 동작.
     *
     * @param result
     */
    private void onCartSelected(BasketPostAsyncController.CartResult result) {
        switch (result.getRetCd()) {
            case BasketPostAsyncController.CartResult.ReturnCode.SUCCESS: // 성공
                EventBus.getDefault().post(new Events.FlexibleEvent.CartPopupEvent(
                        Events.FlexibleEvent.CartPopupEvent.STATE_SUCCESS, true, result.getIsFresh())
                );
                break;
            case BasketPostAsyncController.CartResult.ReturnCode.ALEADY_EXISTS: // 이미 존재
                EventBus.getDefault().post(new Events.FlexibleEvent.CartPopupEvent(
                        Events.FlexibleEvent.CartPopupEvent.STATE_ALREADY_EXISTS, true, result.getIsFresh())
                );
                break;
            case BasketPostAsyncController.CartResult.ReturnCode.ERROR: // 실패
            case BasketPostAsyncController.CartResult.ReturnCode.ADULT: // 성인 인증 필요
            case BasketPostAsyncController.CartResult.ReturnCode.CART: //장바구니 수정 필요 : 장바구니로 이동해야 함
                //리프레시를 막아아 햐면 별도 플래그 세팅 필요
                sendBasicPopupEvent(result);
                break;
            case BasketPostAsyncController.CartResult.ReturnCode.SELECT_LOCATION: // 마트 배송지 선택 필요 ( 배송지 팝업 노출 시켜야 함)
                //새벽배송
                sendBasicPopupEvent(result);
                break;
        }
    }

    /**
     * 배송지변경 등 팝업을 노출한다.
     *
     * @param result CartResult
     */
    private void sendBasicPopupEvent(BasketPostAsyncController.CartResult result) {
        EventBus.getDefault().post(new Events.BasicPopupEvent(
                result.getRetMsg(), result.getRtnUrl(),
                mContext.getString(R.string.common_cancel),
                mContext.getString(R.string.common_ok),
                1000));
    }
}
