/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.StringUtils;
import com.google.inject.Inject;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.ui.util.ViewUtils;

import java.util.List;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.main.ImageBadge;
import gsshop.mobile.v2.home.main.ProductInfoUtil;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.support.tv.VideoParameters;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.util.NetworkUtils;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.RoboGuice;

import static gsshop.mobile.v2.R.id.badge_1;
import static gsshop.mobile.v2.R.id.badge_2;
import static gsshop.mobile.v2.R.id.badge_3;

/**
 * TV쇼핑 2단뷰
 * 다른 매장에 본 뷰타입이 꽂혀도 노출한다.
 */
public class BanImgC2GbaVH extends BaseViewHolder {

    @Inject
    private RestClient restClient;

    private final View[] contentViews = new View[2];
    private int position = -1;

    /**
     * @param itemView itemView
     */
    public BanImgC2GbaVH(View itemView) {
        super(itemView);
        RoboGuice.getInjector(MainApplication.getAppContext()).injectMembers(this);

        contentViews[0] = itemView.findViewById(R.id.content_frame_1st).findViewById(R.id.view_product);
        contentViews[1] = itemView.findViewById(R.id.content_frame_2nd).findViewById(R.id.view_product);

        DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), contentViews[0].findViewById(R.id.image_prd));
        DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), contentViews[1].findViewById(R.id.image_prd));
        DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), contentViews[0].findViewById(R.id.dim));
        DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), contentViews[1].findViewById(R.id.dim));
    }

    /**
     * @param context     context
     * @param position    position
     * @param info        info
     * @param action      action
     * @param label       label
     * @param sectionName sectionName
     */
    @Override
    public void onBindViewHolder(final Context context, final int position, final ShopInfo info,
                                 final String action, final String label, String sectionName) {

        this.position = position;
        List<SectionContentList> products = info.contents.get(position).sectionContent.subProductList;

        if (products == null || products.isEmpty()) {
            ViewUtils.hideViews(contentViews[0], contentViews[1]);
            return;
        }

        if (products.size() <= 1) {
            ViewUtils.hideViews(contentViews[1]);
        }
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i) != null) {
                ViewUtils.showViews(contentViews[i]);
                updateProduct(context, contentViews[i], products.get(i));
            } else {
                ViewUtils.hideViews(contentViews[i]);
            }
        }
    }

    /**
     * 비디오 플레이어를 실행한다.
     *
     * @param activity 액티비티
     * @param param 비디오 파라미터
     */
    protected void playVideo(Activity activity, VideoParameters param) {
        Intent intent = new Intent(Keys.ACTION.LIVE_VIDEO_PLAYER);
        if(param != null) {
            intent.putExtra(Keys.INTENT.VIDEO_ID, param.videoId);
            intent.putExtra(Keys.INTENT.VIDEO_URL, param.videoUrl);
            intent.putExtra(Keys.INTENT.VIDEO_PRD_URL, param.productInfoUrl);
            intent.putExtra(Keys.INTENT.VIDEO_PRD_WISELOG_URL, param.productWiselogUrl);
        }
        intent.putExtra(Keys.INTENT.FOR_RESULT, true);

        activity.startActivityForResult(intent, Keys.REQCODE.VIDEO);
    }

    /**
     * 비디오 파라미터를 생성한다.
     *
     * @param productUrl 상품링크 주소
     * @param videoId 비디오번호
     * @param livePlayUrl 비디오 주소
     * @param wiselogUrl 와이즈로그 주소
     * @return 비디오 파라미터
     */
    protected VideoParameters buildVideoParam(String productUrl, String videoId, String livePlayUrl, String videoTime, String wiselogUrl) {
        VideoParameters param = new VideoParameters();
        param.videoId = videoId;
        param.videoUrl = livePlayUrl;
        param.productInfoUrl = productUrl;
        param.productWiselogUrl = wiselogUrl;
        return param;
    }

    /**
     * 상품영역을 세팅한다.
     *
     * @param context 컨텍스트
     * @param contentView 컨텐츠뷰
     * @param item 상품정보
     */
    private void updateProduct(final Context context, final View contentView, final SectionContentList item) {
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebUtils.goWeb(context, item.linkUrl);
            }
        });

        //재생버튼 (dealMcVideoUrlAddr 값이 존재하는 경우 노출)
        Button btn_play = (Button) contentView.findViewById(R.id.btn_play);
        //재생시간 (dealMcVideoUrlAddr과 videoTime 값이 모두 존재하는 경우 노출)
        TextView text_video_time = (TextView) contentView.findViewById(R.id.text_video_time);
        //딤 (재생버튼 노출 룰과 동일하게 노출)
        View dim = contentView.findViewById(R.id.dim);

        ViewUtils.hideViews(btn_play, dim, text_video_time);

        if (!StringUtils.isEmpty(item.dealMcVideoUrlAddr)
                || (!TextUtils.isEmpty(item.videoid) && item.videoid.length() > 4)) {
            contentView.findViewById(R.id.image_prd).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((HomeActivity) context).setWiseLogHttpClient(item.wiseLog);
                    NetworkUtils.confirmNetworkBillingAndShowPopup((HomeActivity) context, new NetworkUtils.OnConfirmNetworkListener() {
                        @Override
                        public void isConfirmed(boolean isConfirmed) {
                            if (isConfirmed) {
                                playVideo((Activity) context, buildVideoParam(item.linkUrl, item.videoid, item.dealMcVideoUrlAddr, item.videoTime, item.wiseLog2));
                            }
                        }

                        @Override
                        public void inCanceled() {
                        }
                    });
                }
            });
            ViewUtils.showViews(btn_play, dim);

            if (!TextUtils.isEmpty(item.videoTime)) {
                text_video_time.setText(item.videoTime);
                ViewUtils.showViews(text_video_time);
            }
        } else {
            contentView.findViewById(R.id.image_prd).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    contentView.performClick();
                }
            });
        }

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

        // image url - imageUrl
        ImageView prdImage = (ImageView) contentView.findViewById(R.id.image_prd);
        ImageUtil.loadImageFit(context, item.imageUrl, prdImage, R.drawable.noimg_list);

        // productName
        final String productName = item.productName;
        String departName = null;
        String departNameColor = null;

        prdImage.setContentDescription(productName);
        //방어로직 추가
        if (item.infoBadge != null && item.infoBadge.TF != null && !item.infoBadge.TF.isEmpty()) {
            departName = item.infoBadge.TF.get(0).text;
            departNameColor = item.infoBadge.TF.get(0).type;
        }

        final TextView nameText = (TextView) contentView.findViewById(R.id.text_name);

        if (departName == null || departName.length() <= 0) {
            nameText.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    int textWidth = nameText.getWidth();
                    int paddingLeft = nameText.getPaddingLeft();
                    int paddingRight = nameText.getPaddingRight();
                    if (textWidth > 0) {
                        // line break;
                        nameText.getViewTreeObserver().removeOnPreDrawListener(this);

                        int availableWidth = textWidth - (paddingLeft + paddingRight);
                        // first line
                        int end = nameText.getPaint().breakText(productName, true, availableWidth, null);
                        Spannable wordtoSpan;
                        if (end < productName.length()) {
                            String firstLine = productName.substring(0, end);
                            String nextLine = productName.substring(end);
                            // second line
                            end = nameText.getPaint().breakText(nextLine, true, availableWidth, null);
                            if (end < nextLine.length()) {
                                nextLine = nextLine.substring(0, end) + '\n' + nextLine.substring(end);
                            }
                            wordtoSpan = new SpannableString(firstLine + '\n' + nextLine.trim());
                        } else {
                            wordtoSpan = new SpannableString(productName);
                        }

                        nameText.setText(wordtoSpan);
                    }
                    return true;
                }
            });
        } else {
            // line break;
            final String fullName = departName + productName;
            final int departLength = departName.length();

            int departColor = 0;
            if(gsshop.mobile.v2.util.StringUtils.checkHexColor("#" + departNameColor)) {
              departColor = Color.parseColor("#" + departNameColor);
            }


            final int finalDepartColor = departColor;
            nameText.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    int textWidth = nameText.getWidth();
                    int paddingLeft = nameText.getPaddingLeft();
                    int paddingRight = nameText.getPaddingRight();
                    if (textWidth > 0) {
                        nameText.getViewTreeObserver().removeOnPreDrawListener(this);

                        int availableWidth = textWidth - (paddingLeft + paddingRight);
                        // first line
                        int end = nameText.getPaint().breakText(fullName, true, availableWidth, null);
                        Spannable wordtoSpan;
                        if (end < productName.length()) {
                            String firstLine = fullName.substring(0, end);
                            String nextLine = fullName.substring(end);
                            // second line
                            end = nameText.getPaint().breakText(nextLine, true, availableWidth, null);
                            if (end < nextLine.length()) {
                                nextLine = nextLine.substring(0, end) + '\n' + nextLine.substring(end);
                            }
                            wordtoSpan = new SpannableString(firstLine + '\n' + nextLine.trim());
                        } else {
                            wordtoSpan = new SpannableString(fullName);
                        }

                        if(finalDepartColor != 0) {
                            wordtoSpan.setSpan(new ForegroundColorSpan(finalDepartColor), 0, departLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        nameText.setText(wordtoSpan);
                    }
                    return true;
                }
            });


        }

        // basePrice
        // discountRate
        TextView baseText = (TextView) contentView.findViewById(R.id.text_base);
        if (Integer.parseInt(item.discountRate) > Keys.ZERO_DISCOUNT_RATE) {
            ViewUtils.showViews(baseText);
            baseText.setPaintFlags(baseText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            baseText.setText(item.basePrice + context.getString(R.string.won));
        } else {
            ViewUtils.hideViews(baseText);
        }

        // valueText
        TextView valueText = (TextView) contentView.findViewById(R.id.text_value);
        String value = item.valueText;
        if (value != null && value.length() > 0) {
            ViewUtils.showViews(valueText);
            valueText.setText(value);
        } else {
            ViewUtils.hideViews(valueText);
        }

        // salePrice
        TextView priceText = (TextView) contentView.findViewById(R.id.text_price);
        priceText.setText(item.salePrice);

        // exposePriceText
        TextView notationText = (TextView) contentView.findViewById(R.id.text_notation);
        notationText.setText(item.exposePriceText);

        // promotion
        String promotion = item.promotionName;
        TextView promotionText = (TextView) contentView.findViewById(R.id.text_promotion);
        if (promotion != null && promotion.length() > 0) {
            ViewUtils.showViews(promotionText);
            promotionText.setText("(" + promotion + ")");
        } else {
            ViewUtils.hideViews(promotionText);
        }

        // sales quantity
        TextView salesQuantityText = (TextView) contentView.findViewById(R.id.text_sales_quantity);
        salesQuantityText.setText("");
        salesQuantityText.setText(ProductInfoUtil.getSaleQuantityText(item));

        // imgBadgeCorner
        TextView badgeCornerText = (TextView) contentView.findViewById(R.id.text_badge_corner);
        badgeCornerText.setVisibility(View.GONE);
        // badge
        ImageView[] badge = new ImageView[3];
        badge[0] = (ImageView) contentView.findViewById(badge_1);
        badge[1] = (ImageView) contentView.findViewById(badge_2);
        badge[2] = (ImageView) contentView.findViewById(badge_3);
        for (int i=0; i<badge.length; i++) {
            badge[i].setVisibility(View.GONE);
        }
        //rwImgList 값이 우선, 없는경우 imgBadgeCorner 조회
        if (item.rwImgList != null && !item.rwImgList.isEmpty()) {
            for (int i = 0; i < item.rwImgList.size(); i++) {
                //3개를 초과하는 뱃지는 무시
                if (i >= badge.length) {
                    break;
                }
                badge[i].layout(0, 0, 0, 0);
                ImageUtil.loadImage(context, item.rwImgList.get(i), badge[i], 0);
                badge[i].setVisibility(View.VISIBLE);
            }
        } else if (item.imgBadgeCorner != null
                && item.imgBadgeCorner.RB != null
                && !item.imgBadgeCorner.RB.isEmpty()) {

            SpannableStringBuilder imgBadgeCorner = new SpannableStringBuilder();
            for ( ImageBadge imageBadge : item.imgBadgeCorner.RB) {
                String badgeStr = "";
                if ("freeDlv".equals(imageBadge.type)) {
                    badgeStr = StringUtils.isEmpty(imageBadge.text) ? "무료배송" : imageBadge.text;
                } else if("freeInstall".equals(imageBadge.type)) {
                    badgeStr = StringUtils.isEmpty(imageBadge.text) ? "무료설치" : imageBadge.text;
                } else if("todayClose".equals(imageBadge.type)) {
                    badgeStr = StringUtils.isEmpty(imageBadge.text) ? "마감임박" : imageBadge.text;
                } else if("Reserves".equals(imageBadge.type)) {
                    badgeStr = StringUtils.isEmpty(imageBadge.text) ? "적립금" : imageBadge.text;
                } else if("interestFree".equals(imageBadge.type)) {
                    badgeStr = StringUtils.isEmpty(imageBadge.text) ? "무이자" : imageBadge.text;
                } else if("etc".equals(imageBadge.type)) {
                    badgeStr = StringUtils.isEmpty(imageBadge.text) ? "" : imageBadge.text;
                }
                if (!StringUtils.isEmpty(badgeStr)) {
                    imgBadgeCorner.append(badgeStr).append(" ");
                }
            }
            badgeCornerText.setVisibility(View.VISIBLE);
            badgeCornerText.setText(imgBadgeCorner);
        }
    }
}
