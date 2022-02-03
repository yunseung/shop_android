
/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.gssuper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.gsshop.mocha.ui.util.ViewUtils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.main.paramsBasket;
import gsshop.mobile.v2.home.shop.network.BasketPostAsyncController;
import gsshop.mobile.v2.home.shop.renewal.utils.SetDtoUtil;
import gsshop.mobile.v2.home.shop.renewal.utils.dto.ProductInfo;
import gsshop.mobile.v2.home.shop.renewal.views.RenewalLayoutProductInfoPrdFresh;
import gsshop.mobile.v2.user.User;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;
import static gsshop.mobile.v2.home.shop.BaseViewHolder.IMG_CACHE_RPL2_FROM;
import static gsshop.mobile.v2.home.shop.BaseViewHolder.IMG_CACHE_RPL_TO;

/**
 * GS Super 리스트 어뎁터.
 */
public class MapCxGbbAdapter extends
        RecyclerView.Adapter<MapCxGbbAdapter.BannerViewHolder> {

    private int mLength;
    protected List<SectionContentList> mSubProductList; // 상속받아 사용하기 위해 protected 로 변경
    private final Context mContext;
    private final String mAction;
    private final String mLabel;
    private String mNavigationId = "";

    private final ForegroundColorSpan discountColorSpan = new ForegroundColorSpan(Color.parseColor("#666666"));
    private final AbsoluteSizeSpan valueSizeSpan = new AbsoluteSizeSpan(MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.valueTextSize), false);

//    @Inject
//    private RestClient mRestClient;

    /**
     * @param context
     * @param subProductList
     * @param action
     * @param label
     */
    public MapCxGbbAdapter(Context context, List<SectionContentList> subProductList, String action, String label) {
        this(context, subProductList, -1, action, label);
    }

    /**
     * 리스트 length 추가.
     *
     * @param context
     * @param subProductList
     * @param length
     * @param action
     * @param label
     */
    public MapCxGbbAdapter(Context context, List<SectionContentList> subProductList, int length, String action, String label) {
        this(context, subProductList, length, action, label, null);
    }

    public MapCxGbbAdapter(Context context, List<SectionContentList> subProductList, int length, String action, String label, String navId) {
        this.mContext = context;
        this.mSubProductList = subProductList;
        this.mLength = length;
        this.mAction = action;
        this.mLabel = label;
        this.mNavigationId = navId;
    }

//    public void setTabIndex(int tabIndex) {
//        this.mTabIndex = tabIndex;
//    }

    public void setItem(ArrayList<SectionContentList> subProductList) {
        this.mSubProductList = subProductList;
    }

    @Override
    public BannerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.renewal_home_row_type_fx_gs_fresh_prd_item,
                        parent, false);

        //혜택이 없으면 찜버튼이 올라가는현상 때문에 여러 시도한 흔적 by hanna
        /*LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)itemView.getLayoutParams();
        params.width = LinearLayout.LayoutParams.MATCH_PARENT;
        params.height = LinearLayout.LayoutParams.MATCH_PARENT;
        itemView.setLayoutParams(params);*/

        //itemView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        return new BannerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BannerViewHolder holder, int position) {
        try {
            final SectionContentList item = mSubProductList.get(position);

            updateProduct(holder, item);
        } catch (IndexOutOfBoundsException e) {
            Ln.e(e.getMessage());
        }
    }

    private void updateProduct(BannerViewHolder holder, final SectionContentList item) {

        final View viewPrd = holder.view_product;
        final ImageView viewImg = holder.view_image;

        holder.setImageForCache(item.imageUrl);

        if ("IMG_ONLY".equals(item.viewType) && viewImg != null) {
            ViewUtils.hideViews(viewPrd);
            ViewUtils.showViews(viewImg);

            ImageUtil.loadImageResizeToHeight(mContext, item.imageUrl, viewImg, R.drawable.noimage_166_166);

            if (!TextUtils.isEmpty(item.gsAccessibilityVariable)) {
                viewImg.setContentDescription(item.gsAccessibilityVariable);
            }

            viewImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra(Keys.INTENT.IMAGE_URL, holder.getImageForCache().replace(IMG_CACHE_RPL2_FROM, IMG_CACHE_RPL_TO));
                    intent.putExtra(Keys.INTENT.HAS_VOD, item.hasVod);
                    WebUtils.goWeb(mContext, item.linkUrl, intent);
                }
            });
        } else {
            ViewUtils.showViews(viewPrd);
            ViewUtils.hideViews(viewImg);
            ImageUtil.loadImageResize(mContext, item.imageUrl, holder.image_prd, R.drawable.noimage_166_166);

            if (viewPrd != null) {
                viewPrd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra(Keys.INTENT.IMAGE_URL, holder.getImageForCache().replace(IMG_CACHE_RPL2_FROM, IMG_CACHE_RPL_TO));
                        intent.putExtra(Keys.INTENT.HAS_VOD, item.hasVod);
                        WebUtils.goWeb(mContext, item.linkUrl, intent);
                    }
                });
            }
        }

        final ImageView btnCart = holder.mLayoutProductInfo.findViewById(R.id.renewal_gsfresh_cart);

        if (btnCart != null) {
            btnCart.setVisibility(View.VISIBLE);
            btnCart.setContentDescription("장바구니");
            btnCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User user = User.getCachedUser();
                    if (user == null || user.customerNumber.length() < 2) {
                        Intent intent = new Intent(Keys.ACTION.LOGIN);
                        intent.putExtra(Keys.INTENT.FOR_GS_SUPER, true);
                        intent.putExtra(Keys.INTENT.NAVIGATION_ID, mNavigationId);
                        mContext.startActivity(intent);
                    }
                    else {
                        String baseketUrl = item.basket.url;
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
                            return;
                        }

                        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<String, Object>();
                        for (paramsBasket basket : item.basket.params) {
                            formData.add(basket.name, basket.value);
                        }

                        HttpHeaders headers = new HttpHeaders();
                        headers.set("Connection", "Close");

                        MediaType mediaType = new MediaType("application", "x-www-form-urlencoded", Charset.forName("UTF-8"));
                        headers.setContentType(mediaType);

                        org.springframework.http.HttpEntity<MultiValueMap> requestEntity =
                                new org.springframework.http.HttpEntity<MultiValueMap>(formData, headers);

                        BasketPostAsyncController.OnPostAsyncListener postListener =
                                new BasketPostAsyncController.OnPostAsyncListener() {
                                    @Override
                                    public void onResult(BasketPostAsyncController.CartResult result) {
                                        //                            Ln.d("Post Result, RetCd : " + result.getRetCd() +
                                        //                                    "\nRetMsg : " + result.getRetMsg() + "\nRetUrl" + result.getRtnUrl());
                                        onCartSelected(result);

                                    }
                                };

                        new BasketPostAsyncController(mContext, postListener).execute(item.basket, requestEntity, postListener);

                    }
                }
            });

            //가격표시용 공통모듈에 맞게 데이타 변경
            ProductInfo info = SetDtoUtil.setDto(item);
            holder.mLayoutProductInfo.setViews(info, SetDtoUtil.BroadComponentType.product);
        }

    }

    @Override
    public int getItemCount() {
        return mSubProductList == null ? 0 : mLength < 0 ? mSubProductList.size() : mLength;
    }


    public void setLength(int length) {
        if (this.mLength != length) {
            this.mLength = length;
            notifyDataSetChanged();
        }
    }

    /**
     * 카트 선택 되었을 시에 동작.
     *
     * @param result
     */
    private void onCartSelected(BasketPostAsyncController.CartResult result) {
        switch (result.getRetCd()) {
            case BasketPostAsyncController.CartResult.ReturnCode.SUCCESS: // 성공
                EventBus.getDefault().post(new Events.FlexibleEvent.CartPopupEvent(
                        Events.FlexibleEvent.CartPopupEvent.STATE_SUCCESS, true, null)
                );
                break;
            case BasketPostAsyncController.CartResult.ReturnCode.ALEADY_EXISTS: // 이미 존재
                EventBus.getDefault().post(new Events.FlexibleEvent.CartPopupEvent(
                        Events.FlexibleEvent.CartPopupEvent.STATE_ALREADY_EXISTS, true, null)
                );
                break;
            case BasketPostAsyncController.CartResult.ReturnCode.ERROR: // 실패
            case BasketPostAsyncController.CartResult.ReturnCode.ADULT: // 성인 인증 필요
            case BasketPostAsyncController.CartResult.ReturnCode.CART: //장바구니 수정 필요 : 장바구니로 이동해야 함
                //리프레시를 막아아 햐면 별도 플래그 세팅 필요
                sendBasicPopupEvent(result);
                break;
            case BasketPostAsyncController.CartResult.ReturnCode.SELECT_LOCATION: // 마트 배송지 선택 필요 ( 배송지 팝업 노출 시켜야 함)
                //GS 프레시몰
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

    /**
     * 뷰홀더
     */
    public static class BannerViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout view_product;
        public ImageView view_image;
        public ImageView image_prd;
        public RenewalLayoutProductInfoPrdFresh mLayoutProductInfo;
        public String mImgForCache;

        public BannerViewHolder(View itemView) {
            super(itemView);
            view_product = (LinearLayout) itemView.findViewById(R.id.view_product);
            view_image = (ImageView) itemView.findViewById(R.id.img_product);
            image_prd = (ImageView) itemView.findViewById(R.id.image_prd);

            mLayoutProductInfo = itemView.findViewById(R.id.layout_product_info);
        }

        public void setImageForCache(String url) {
            this.mImgForCache = url;
        }

        public String getImageForCache() {
            return isNotEmpty(mImgForCache) ? mImgForCache : "";
        }
    }
}