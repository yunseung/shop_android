/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.bestshop;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gsshop.mocha.ui.util.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.ImageBadge;
import gsshop.mobile.v2.home.main.ProductInfoUtil;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;

/**
 *
 *
 */
public class BanImgC2GbbVH extends BestShopBannerPrdViewHolder {

    private ImageView[] image_badge = new ImageView[2];
    private final View bottomDivider;

    /**
     * @param itemView itemView
     */
    public BanImgC2GbbVH(View itemView) {
        super(itemView);
        image_badge[0] = (ImageView)itemView.findViewById(R.id.content_frame_1st).findViewById(R.id.image_badge);
        image_badge[1] = (ImageView)itemView.findViewById(R.id.content_frame_2nd).findViewById(R.id.image_badge);

        bottomDivider = itemView.findViewById(R.id.view_bottom_divider);
    }

    @Override
    public void onBindViewHolder(final Context context, final int position, final ShopInfo info,
                                 final String action, final String label, String sectionName) {
        super.onBindViewHolder(context,position,info,action,label,sectionName);

        ArrayList<SectionContentList> subProductList = info.contents.get(position).sectionContent.subProductList;

        if(subProductList != null && !subProductList.isEmpty()){
            if("Deal".equals(subProductList.get(0).dealProductType)){
                image_badge[0].setVisibility(View.VISIBLE);
            }else{
                image_badge[0].setVisibility(View.GONE);
            }

            updateProduct(context, itemView.findViewById(R.id.content_frame_1st).findViewById(R.id.view_product), subProductList.get(0));
        }

        if(subProductList != null && subProductList.size() > 1){
            if("Deal".equals(subProductList.get(1).dealProductType)){
                image_badge[1].setVisibility(View.VISIBLE);
            }else{
                image_badge[1].setVisibility(View.GONE);
            }

            updateProduct(context, itemView.findViewById(R.id.content_frame_2nd).findViewById(R.id.view_product), subProductList.get(1));
        }

        // bottom line
        if(position < info.contents.size() -2 ) {
            ViewUtils.hideViews(bottomDivider);
        } else {
            ViewUtils.showViews(bottomDivider);
        }

    }

    private void updateProduct(final Context context, View contentView, final SectionContentList item) {
        /*
        badge 표시, 이미지 뱃지가 존재하면 이미지 뱃지 우선, 없을 경우 text badge
         */
        View badgeContainerRWImg = contentView.findViewById(R.id.view_rw_img);
        LinearLayout layoutAttach = contentView.findViewById(R.id.layout_attach_badge);

        //방송시간
        TextView text_brd_time = (TextView) contentView.findViewById(R.id.text_brd_time);
        if (!TextUtils.isEmpty(item.etcText1)) {
            text_brd_time.setText(item.etcText1);
            ViewUtils.showViews(text_brd_time);
        } else {
            ViewUtils.hideViews(text_brd_time);
        }

        //방송중 구매가능
        TextView text_air_buy = (TextView) contentView.findViewById(R.id.text_air_buy);
        if (text_air_buy != null) {
            if ("AIR_BUY".equals(item.imageLayerFlag)) {
                ViewUtils.showViews(text_air_buy);
            } else {
                ViewUtils.hideViews(text_air_buy);
            }
        }

        if (item.rwImgList != null && !item.rwImgList.isEmpty()) {
            // 이미지 뱃지가 존재할 경우
            ViewUtils.showViews(badgeContainerRWImg);
            layoutAttach.setVisibility(View.INVISIBLE);

            ImageView[] badge = new ImageView[3];

            badge[0] = (ImageView) contentView.findViewById(R.id.badge_1);
            badge[1] = (ImageView) contentView.findViewById(R.id.badge_2);
            badge[2] = (ImageView) contentView.findViewById(R.id.badge_3);

            for (int i=0; i<badge.length; i++) {
                badge[i].setVisibility(View.GONE);
            }

            for(int i=0; i< item.rwImgList.size() ; i++) {
                //3개를 초과하는 뱃지는 무시
                if (i >= badge.length) {
                    break;
                }

                badge[i].layout(0, 0, 0, 0);
                ImageUtil.loadImage(context, item.rwImgList.get(i),badge[i] , 0);

                badge[i].setVisibility(View.VISIBLE);
            }
        }
        else if (item.imgBadgeCorner.RB != null && !item.imgBadgeCorner.RB.isEmpty()){
            // 이미지 뱃지가 없고 텍스트 뱃지가 존재할 경우
            ViewUtils.showViews(layoutAttach);
            ViewUtils.hideViews(badgeContainerRWImg);

            layoutAttach.removeAllViews();

            final List<ImageBadge> listBadgeBottom = item.imgBadgeCorner.RB;

            if (listBadgeBottom.size() > 0) {
                layoutAttach.setVisibility(View.VISIBLE);
                for (int i = 0; i < listBadgeBottom.size(); i++) {
                    if (i > 2) {
                        break;
                    }
                    TextView txtBadge = new TextView(context);
                    txtBadge.setTextAppearance(context, R.style.GSChoiceStatText);
                    txtBadge.setText(listBadgeBottom.get(i).text);
                    layoutAttach.addView(txtBadge);
                    if (i + 1 < listBadgeBottom.size() && i < 2) {
                        ImageView imageView = new ImageView(context);

                        LinearLayout.LayoutParams imageLParams = new LinearLayout.LayoutParams(DisplayUtils.convertDpToPx(context, 3), DisplayUtils.convertDpToPx(context, 3));
                        int marginImageBadge = DisplayUtils.convertDpToPx(context, 5);
                        imageLParams.setMargins(marginImageBadge, 0, marginImageBadge, 0);
                        imageView.setLayoutParams(imageLParams);
                        imageView.setBackgroundResource(R.drawable.dot_grey);
                        layoutAttach.addView(imageView);
                    }
                }
            }
        }
        else {
            // 이미지, 텍스트 뱃지 둘다 없을 경우
            layoutAttach.setVisibility(View.INVISIBLE);
            ViewUtils.hideViews(badgeContainerRWImg);
        }

        // sales quantity
        TextView salesQuantityText = (TextView) contentView.findViewById(R.id.text_sales_quantity);
        salesQuantityText.setText("");
        salesQuantityText.setText(ProductInfoUtil.getSaleQuantityText(item));

    }

}
