/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.renewal.flexible;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gsshop.mocha.ui.util.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.renewal.utils.SetDtoUtil;
import gsshop.mobile.v2.home.shop.renewal.utils.dto.ProductInfo;
import gsshop.mobile.v2.library.viewpager.InfiniteViewPager;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.util.SwipeUtils;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;
import static gsshop.mobile.v2.home.shop.BaseViewHolder.IMG_CACHE_RPL3_FROM;
import static gsshop.mobile.v2.home.shop.BaseViewHolder.IMG_CACHE_RPL_TO;

/**
 * 추천딜 리스트 어뎁터.
 */
public class RenewalFlexiblePromotionAdapter extends RecyclerView.Adapter<RenewalFlexiblePromotionAdapter.RecommendViewHolder> {

    private int length;
    private List<SectionContentList> subProductList;
    private final Context mContext;

    private static final String IS_MORE = "PMO_T1_PREVIEW_B_MORE";
    private final static String COMMON_RENTALTEXT = "월 렌탈료";
    private final static String COMMON_DISPLAY_RENTALTEXT = "월";

    /**
     * @param context
     * @param subProductList
     * @param action
     * @param label
     */
    public RenewalFlexiblePromotionAdapter(Context context, List<SectionContentList> subProductList, String action, String label) {
        this(context, subProductList, -1, action, label);
    }

    /**
     * 리스트이 length 추가.
     *
     * @param context
     * @param subProductList
     * @param length
     * @param action
     * @param label
     */
    public RenewalFlexiblePromotionAdapter(Context context, List<SectionContentList> subProductList, int length, String action, String label) {
        this.mContext = context;
        this.subProductList = subProductList;
        this.length = length;

    }

    public void setItem(ArrayList<SectionContentList> subProductList) {
        this.subProductList = subProductList;
    }

    @Override
    public RecommendViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.renewal_recycler_item_img_background_list, viewGroup, false);

        return new RecommendViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecommendViewHolder holder, final int position) {
        final SectionContentList item = subProductList.get(position);

        try {
            if (IS_MORE.equals(item.viewType)) {

                holder.layout_product.setVisibility(View.GONE);
                holder.layout_read_more.setVisibility(View.VISIBLE);

//                ImageView imageView = holder.layout_read_more.findViewById(R.id.img_read_more);
//                if (EmptyUtils.isNotEmpty(imageView) && EmptyUtils.isNotEmpty(item.imageUrl)) {
//                    ImageUtil.loadImageFit(mContext, item.imageUrl, imageView, R.drawable.renewal_btn_more);
//                }

            } else {
                holder.layout_product.setVisibility(View.VISIBLE);
                holder.layout_read_more.setVisibility(View.GONE);

                holder.setImageForCache(item.imageUrl);

                ImageUtil.loadImageFit(mContext, item.imageUrl, holder.product_img, R.drawable.no_img_550);

                TextView info_text = holder.info_text;
                info_text.setText(item.productName);

                TextView price = holder.price;
                TextView priceUnit = holder.price_unit;
                priceUnit.setText(item.exposePriceText);

                final ProductInfo info = SetDtoUtil.setDto(item);

                if(("true".equals(info.deal) && ("R".equals(info.productType)/* 렌탈*/ ||
                        "T".equals(info.productType)/*여행*/ || "U".equals(info.productType)/*딜 시공*/ ||
                        "S".equals(info.productType)/*상품 시공*/)) ||
                        ("false".equals(info.deal) && "R".equals(info.productType)/* 렌탈*/)) {
                    //항목1 무언가 써있으면???
                    // 렌탈 에서만 rRentalText 를 비교한다.
                    if ( "R".equals(info.productType) && info.rentalText != null && !info.rentalText.isEmpty() ) {
                        //항목1에 "월 렌탈료"가 써있으면 -> "월"로 변경
                        //API에서 항목1에 "월"이 내려올 수 있는 조건이 추가되어 "월"도 "월 렌탈료"와 동일로직으로 처리하도록 조건 추가
                        if (COMMON_RENTALTEXT.equals(item.rentalText) ||
                                COMMON_DISPLAY_RENTALTEXT.equals(item.rentalText)) {
                            if(price != null) {
                                price.setText(COMMON_DISPLAY_RENTALTEXT + " ");
                                price.setVisibility(View.VISIBLE);
                            }

                            //"월" + 항목2(rRentalPrice) 값이 있으면 뿌린다
                            if (info.rentalPrice != null && !info.rentalPrice.isEmpty() && !info.rentalPrice.startsWith("0")) {
                                if( price != null) {
                                    price.append(info.rentalPrice);
                                    price.setVisibility(View.VISIBLE);
                                }
                                // 원자 지우기 ,
                                ViewUtils.hideViews(priceUnit);
                            }
                            else {
                                //렌탈이지만 "월 렌탈료"가 안써있으면 다 우지고 "상담신청상품" 세긴다.
                                if( price != null) {
                                    price.setText(R.string.common_rental_title);
                                    price.setVisibility(View.VISIBLE);
                                }
                                ViewUtils.hideViews(priceUnit);
                            }
                        }
                        // else 는 무조건 엔터 칩시다 접었을 때 보기가 힘듭니다.
                        else {//렌탈인데 월이 아니면,
                            if (info.rentalPrice != null && !info.rentalPrice.isEmpty() && !info.rentalPrice.startsWith("0")) {
                                if( price != null) {
                                    price.setText(info.rentalPrice);
                                    price.setVisibility(View.VISIBLE);
                                }
                                // rRentalPrice 게 없으니 "월" 영역 지우기 // 원자 지우기 ,
                                ViewUtils.hideViews(priceUnit);
                            }
                            else {
                                //"월" 을 지우고, "상담신청상품" 세긴다.
                                if( price != null) {
                                    price.setText(R.string.common_rental_title);
                                    price.setVisibility(View.VISIBLE);
                                }
                                // rRentalPrice 게 없으니 "월" 영역 지우기 // 원자 지우기 ,
                                ViewUtils.hideViews(priceUnit);
                            }
                        }
                    }

                    // 월이 안붙는 무형 상품에 대해서 수정, RentalPrice 노출
                    else if (info.rentalPrice != null && !info.rentalPrice.isEmpty() && !info.rentalPrice.startsWith("0")) {
                        if (price != null) {
                            price.setText(info.rentalPrice);
                            price.setVisibility(View.VISIBLE);
                        }
                        // rRentalPrice 게 없으니 "월" 영역 지우기 // 원자 지우기 ,
                        ViewUtils.hideViews(priceUnit);
                    }
                    else {
                        //렌탈이지만 "월 렌탈료"가 안써있으면 다 지우고 "상담 전용 상품입니다." 세긴다.
                        //txtPrice.setText("상담 전용 상품입니다.");
                        if( price != null) {
                            price.setText(R.string.common_rental_title);
                            price.setVisibility(View.VISIBLE);
                        }

                        // rRentalPrice 게 없으니 "월" 영역 지우기 // 원자 지우기 ,
                        ViewUtils.hideViews(priceUnit);
                    }
                }
                else {
                    //보험일때, 계속 추가 할게요
                    if("I".equals(info.productType) ) {
                        //보험이면 타이틀만 보이자
                        //layout_price.setVisibility(View.GONE);
                        // Null pointer 발생하는 경우 있음. null이라도 안죽는 함수로 변경
                        ViewUtils.hideViews(price, priceUnit);
                    }
                    else{
                        if (price != null) {
                            price.setText(info.salePrice);
                            price.setVisibility(View.VISIBLE);
                            priceUnit.setVisibility(View.VISIBLE);
                        }
                    }
                }

                try {
                    String strTitle = info_text.getText() != null ? info_text.getText().toString() : "";
                    String strPrice = price.getText() != null ? price.getText().toString() : "";
                    String strPriceUnit = priceUnit.getText() != null ? priceUnit.getText().toString() : "";

                    holder.root.setContentDescription(strTitle + strPrice + strPriceUnit);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        holder.root.setAccessibilityDelegate(new View.AccessibilityDelegate() {
                            @Override
                            public void onPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
                                super.onPopulateAccessibilityEvent(host, event);
                                event.setContentDescription(strTitle + strPrice + strPriceUnit);
                            }
                        });
                    }
                }
                catch (NullPointerException e) {
                    Ln.e(e.getMessage());
                }
            }

            holder.root.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    holder.root.setScaleX(1);
                    holder.root.setScaleY(1);
                    Intent intent = new Intent();
                    intent.putExtra(Keys.INTENT.IMAGE_URL, holder.getImageForCache().replace(IMG_CACHE_RPL3_FROM, IMG_CACHE_RPL_TO));
                    intent.putExtra(Keys.INTENT.HAS_VOD, item.hasVod);
                    WebUtils.goWeb(mContext, item.linkUrl, intent);

                    //20151115 이민수 GA GTM 트래킹 코드 삽입 ( 베딜의 추천딜 )
                    //                String tempLabel = String.format("%s_%s_%s_%s", label, category, position, item.linkUrl);
                    //                GTMAction.sendEvent(mContext, GTMEnum.GTM_AREA_CATEGORY, action, tempLabel);
                }
            });

            View.OnTouchListener touchListener = new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        SwipeUtils.INSTANCE.disableSwipe();
                        view.setScaleX(0.96f);
                        view.setScaleY(0.96f);
                    }
                    return false;
                }
            };

            holder.root.setOnTouchListener(touchListener);
        }
        catch (NullPointerException e) {
            Ln.e(e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return subProductList == null ? 0 : length < 0 ? subProductList.size() : length;
    }

    public void setLength(int length) {
        if(this.length != length) {
            this.length = length;
            notifyDataSetChanged();
        }
    }

    /**
     * 뷰홀더
     */
    public static class RecommendViewHolder extends RecyclerView.ViewHolder {

        public View root;
        public LinearLayout layout_product;
        public LinearLayout layout_read_more;
        public ImageView product_img;
        public TextView info_text;
        public TextView price;
        public TextView price_unit;
        public String mImgForCache;

        public RecommendViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.root);
            layout_product = (LinearLayout) itemView.findViewById(R.id.layout_product);
            layout_read_more = (LinearLayout) itemView.findViewById(R.id.layout_read_more);
            product_img = (ImageView) itemView.findViewById(R.id.product_img);
            info_text = (TextView) itemView.findViewById(R.id.info_text);
            price = (TextView) itemView.findViewById(R.id.price);
            price_unit = (TextView) itemView.findViewById(R.id.price_unit);
        }

        public void setImageForCache(String url) {
            this.mImgForCache = url;
        }

        public String getImageForCache() {
            return isNotEmpty(mImgForCache) ? mImgForCache : "";
        }
    }
}