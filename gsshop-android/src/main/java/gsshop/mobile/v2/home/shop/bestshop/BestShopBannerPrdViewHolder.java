/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.bestshop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.inject.Inject;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.ui.util.ViewUtils;

import java.util.List;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.HomeGroupInfoAction;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog;
import gsshop.mobile.v2.user.User;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.GlobalTimer;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.util.StringUtils;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.RoboGuice;
import roboguice.util.Ln;

/**
 *
 *
 */
public class BestShopBannerPrdViewHolder extends BaseViewHolder {


    @Inject
    private RestClient restClient;

    private final View[] contentViews = new View[2];
    private int position = -1;
    private String navigagtionId = null;

    /**
     * @param itemView itemView
     */
    public BestShopBannerPrdViewHolder(View itemView) {
        super(itemView);
        RoboGuice.getInjector(MainApplication.getAppContext()).injectMembers(this);

        contentViews[0] = itemView.findViewById(R.id.content_frame_1st).findViewById(R.id.view_product);
        contentViews[1] = itemView.findViewById(R.id.content_frame_2nd).findViewById(R.id.view_product);

        DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), contentViews[0].findViewById(R.id.image_prd));
        DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), contentViews[1].findViewById(R.id.image_prd));
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

        this.navigagtionId = info.naviId;
        this.position = position;
        List<SectionContentList> products = info.contents.get(position).sectionContent.subProductList;

        setViewItems(context, products);
    }

    public void onBindViewHolder(final Context context, final int position, List<SectionContentList> products) {

        this.position = position;

        setViewItems(context, products);
    }

    private void setViewItems(final Context context, final List<SectionContentList> products) {
        if (products != null) {
            if (products.isEmpty()) {
                ViewUtils.hideViews(contentViews[0], contentViews[1]);
                return;
            }

            if (products.size() <= 1) {
                ViewUtils.hideViews(contentViews[1]);
            }
            for (int i = 0; i < products.size(); i++) {
                ViewUtils.showViews(contentViews[i]);
                updateProduct(context, contentViews[i], products.get(i));
            }
        }
    }

    @Override
    public void onViewAttachedToWindow() {
        super.onViewAttachedToWindow();
        try {
            EventBus.getDefault().register(this);
        } catch (Exception e) {
            Ln.e(e);
        }
        GlobalTimer.getInstance().startTimer();
    }

    @Override
    public void onViewDetachedFromWindow() {
        super.onViewDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(Events.TimerEvent event) {
        Ln.i("TimerEvent position: " + position);

    }

    protected void setRankText(View contentView, SectionContentList item) {

        // 순위 imgBadgeCorner.LT.text
        int rank = 0;
        //방어 로직
        if (item != null && item.imgBadgeCorner != null && item.imgBadgeCorner.LT != null && !item.imgBadgeCorner.LT.isEmpty()) {
            rank = Integer.parseInt(item.imgBadgeCorner.LT.get(0).text);
        }
        View rankFrame = contentView.findViewById(R.id.frame_rank);
        ImageView rankBestImage = (ImageView) contentView.findViewById(R.id.image_rank_best);
        ImageView rankRestImage = (ImageView) contentView.findViewById(R.id.image_rank_rest);
        TextView rankText = (TextView) contentView.findViewById(R.id.text_rank);
        rankText.setText(Integer.toString(rank));
        if( rank > 0) {
            ViewUtils.showViews(rankFrame, rankBestImage, rankText, rankRestImage);
            if (rank <= 5) {
                rankText.setTextColor(Color.parseColor("#FFFFFF"));
                ViewUtils.hideViews(rankRestImage);
            } else  {
                rankText.setTextColor(Color.parseColor("#EE2162"));
                ViewUtils.hideViews(rankBestImage);
            }
        } else {
            ViewUtils.hideViews(rankFrame, rankBestImage, rankText, rankRestImage);
        }

    }

    private void updateProduct(final Context context, View contentView, final SectionContentList item) {
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ln.i("url : " + item.linkUrl);
                WebUtils.goWeb(context, item.linkUrl);
            }
        });

        TextView text_air_buy = (TextView) contentView.findViewById(R.id.text_air_buy);

        if (text_air_buy != null) {
            // AIR_BUY
            if ("AIR_BUY".equals(item.imageLayerFlag)) {
                ViewUtils.showViews(text_air_buy);
            } else {
                ViewUtils.hideViews(text_air_buy);
            }
        }
        // image url - imageUrl
        ImageView prdImage = (ImageView) contentView.findViewById(R.id.image_prd);
        ImageUtil.loadImageFit(context, item.imageUrl, prdImage, R.drawable.noimg_logo);

        // 순위 imgBadgeCorner.LT.text
        setRankText(contentView, item);

        // productName
        final String productName = item.productName;
        String departName = null;
        String departNameColor = null;

        //방어로직 추가
        if (item.infoBadge != null && item.infoBadge.TF != null && !item.infoBadge.TF.isEmpty()) {
            departName = item.infoBadge.TF.get(0).text;
            departNameColor = item.infoBadge.TF.get(0).type;
        }

        final TextView nameText = (TextView) contentView.findViewById(R.id.text_name);

        if (departName == null || departName.length() <= 0) {
//            nameText.setText(productName);
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
            final String fullName = departName + " " + productName;
            final int departLength = departName.length();
            int departColor = 0;
            if(StringUtils.checkHexColor("#" + departNameColor)) {
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

                        wordtoSpan.setSpan(new ForegroundColorSpan(finalDepartColor), 0, departLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        nameText.setText(wordtoSpan);
                    }
                    return true;
                }
            });

        }
         try{
             //inflate되는 xml은 두장, 뷰홀더는 하나 /지금베스트와 flexible E(백화점)
             if(ShopInfo.NAVIGATION_BEST_NOW.equals(navigagtionId)){
                 TextView txt_badge_per = (TextView)contentView.findViewById(R.id.txt_badge_per);
                 if(txt_badge_per != null){
                     if(Integer.parseInt(item.discountRate) > Keys.ZERO_DISCOUNT_RATE){
                         ViewUtils.showViews(txt_badge_per);
                         txt_badge_per.setText(item.discountRate + context.getString(R.string.percent));
                     }else{
                         txt_badge_per.setText("");
                     }
                 }
             }
         }catch (Exception e){

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


        // zzim
        //해당 조건은 항상 true 이다. 그런데 고치지 않겠다 10/05 ( 문제가 되면 그떄 지우자 ) 이민수
        final CheckBox zzimCheck = (CheckBox) contentView.findViewById(R.id.check_zzim);
        boolean skip = true;
        if (skip) {
            ViewUtils.hideViews(zzimCheck);
            return;
        }

        zzimCheck.setChecked(item.isWish);
        zzimCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    User user = User.getCachedUser();
                    if (user == null || user.customerNumber.length() <= 0) {
                        // login
                        zzimCheck.setChecked(false);
                        Intent intent = new Intent(Keys.ACTION.LOGIN);
                        intent.putExtra(Keys.INTENT.FROM_ZZIM_BUTTON, true);
                        context.startActivity(intent);
                        return;
                    }

                    // zzim api
                    String url = HomeGroupInfoAction.getHomeGroupInfo().appUseUrl.wishUrl + item.prdid;
                    ZZIMResult result = restClient.getForObject(url, ZZIMResult.class);
                    if (result == null || result.retCd.equals(ZZIMResult.ERROR_CD)) {
                        zzimCheck.setChecked(false);
                        //현재 찜기능이 사용되지 않는다 실제 추가되었을때 result==null 조건이 발생할수 있다 10/05
                        // result == null 메세지가 없다
                        if (result == null) {
                            // TODO: 2016. 10. 12. : 서비스 재개시 메세지 받아야함
                            new CustomOneButtonDialog((Activity) context).message("").buttonClick(CustomOneButtonDialog.DISMISS).show();
                        } else {
                            new CustomOneButtonDialog((Activity) context).message(result.retMsg).buttonClick(CustomOneButtonDialog.DISMISS).show();
                        }

                    }
                } else {
                    User user = User.getCachedUser();
                    if (user == null || user.customerNumber.length() <= 0) {
                        // log out
                        return;
                    }

                    // zzim api
                    String url = HomeGroupInfoAction.getHomeGroupInfo().appUseUrl.wishUrl + item.prdid;
                    ZZIMResult result = restClient.getForObject(url, ZZIMResult.class);
                    if (result == null || result.retCd.equals(ZZIMResult.ERROR_CD)) {
                        //현재 찜기능이 사용되지 않는다 실제 추가되었을때 result==null 조건이 발생할수 있다 10/05
                        // result == null 메세지가 없다
                        if (result == null) {
                            // TODO: 2016. 10. 12. : 서비스 재개시 메세지 받아야함
                            new CustomOneButtonDialog((Activity) context).message("").buttonClick(CustomOneButtonDialog.DISMISS).show();
                        } else {
                            new CustomOneButtonDialog((Activity) context).message(result.retMsg).buttonClick(CustomOneButtonDialog.DISMISS).show();
                        }
                    }

                }
            }
        });

    }

    static class ZZIMResult {
        public static final String ERROR_CD = "ERR";
        public String retCd;
        public String retMsg;
        public String retUrl;
    }

}
