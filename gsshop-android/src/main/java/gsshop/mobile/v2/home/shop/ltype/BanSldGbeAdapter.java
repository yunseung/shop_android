package gsshop.mobile.v2.home.shop.ltype;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.gsshop.mocha.ui.util.ViewUtils;

import java.util.List;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.ImageBadge;
import gsshop.mobile.v2.home.main.ProductInfoUtil;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;

import static gsshop.mobile.v2.util.ImageUtil.BaseImageResolution.HD;

/**
 * 롤링배너 어댑터 pager adapter
 */
public class BanSldGbeAdapter extends PagerAdapter {

    private final Context context;
    private final List<SectionContentList> list;
    private final String action;
    private final String label;

    protected final SpannableStringBuilder titleStringBuilder = new SpannableStringBuilder();
    private final ForegroundColorSpan discountColorSpan = new ForegroundColorSpan(Color.parseColor("#666666"));
    private final AbsoluteSizeSpan valueSizeSpan = 	new AbsoluteSizeSpan(MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.valueTextSize), false);

    public BanSldGbeAdapter(Context context, List<SectionContentList> list, String action, String label) {
        this.context = context;
        this.list = list;
        this.action = action;
        this.label = label;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = ((Activity) context).getLayoutInflater()
                .inflate(R.layout.home_row_type_choice_roll_banner_item, null);

        titleStringBuilder.clear();
        View viewInner = view.findViewById(R.id.layout_inner);
        RelativeLayout layout_img_main = (RelativeLayout)view.findViewById(R.id.layout_img_main);
        // 메인 이미지
        ImageView mainImage = (ImageView) view.findViewById(R.id.image_main);
        // new가 표시될 뱃지
        ImageView badgeNew = (ImageView) view.findViewById(R.id.top_badge);

        // 정보 레이아웃 : 타이틀
        TextView nameTitle = (TextView) view.findViewById(R.id.text_title);
        // 정보 레이아웃 : 서브 타이틀
        TextView nameTitleSub = (TextView) view.findViewById(R.id.text_sub_title);
        // 정보 레이아웃 : 퍼센티지 표시
        TextView txt_badge_per = (TextView) view.findViewById(R.id.txt_badge_per);
        // 정보 레이아웃 : 가격
        TextView txtPrice = (TextView) view.findViewById(R.id.txt_price);
        // 정보 레이아웃 : 원화 표시
        TextView txtPriceWon = (TextView) view.findViewById(R.id.txt_price_won);
        // 정보 레이아웃 : 할인 전 가격 표시
        TextView txtPriceBase = (TextView) view.findViewById(R.id.txt_price_base);
        txtPriceBase.setPaintFlags(txtPriceBase.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        LinearLayout view_info = (LinearLayout) view.findViewById(R.id.view_info);

        LinearLayout layoutAttach = (LinearLayout) view.findViewById(R.id.layout_attach_badge);
        // 정보 레이아웃 : 상태 정보 1
//        TextView txtBadge1 = (TextView) view.findViewById(R.id.text_badge_1);
//        ImageView viewSeperator = (ImageView) view.findViewById(R.id.view_seperator);
//        // 정보 레이아웃 : 상태 정보 2
//        TextView txtBadge2 = (TextView) view.findViewById(R.id.text_badge_2);


        // 구매(판매) 갯수 표시 영역
        LinearLayout layoutSalesQuantity = (LinearLayout) view.findViewById(R.id.layout_sales_quantity);
        TextView txtSalesQuantity = (TextView) view.findViewById(R.id.txt_sales_quantity);
        TextView txtSalesQuantityStr = (TextView) view.findViewById(R.id.txt_sales_quantity_str);
        TextView txtSalesQuantitySubStr = (TextView) view.findViewById(R.id.txt_sales_quantity_sub_str);

        txt_badge_per.setText("");

        badgeNew.setVisibility(View.GONE);

        // 리스트 갯수가 하나일때의 디자인은 기존과 다름. 상단의 Text 가 길이만큼만 나온다... 뭐이런...
        if (list.size() == 1) {
            viewInner.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

            RelativeLayout.LayoutParams parmasNameTitle = (RelativeLayout.LayoutParams)nameTitle.getLayoutParams();
            parmasNameTitle.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
            parmasNameTitle.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            parmasNameTitle.bottomMargin = 0;
            parmasNameTitle.rightMargin = DisplayUtils.convertDpToPx(context, 60);
            nameTitle.setLayoutParams(parmasNameTitle);

            view_info.setPadding(DisplayUtils.convertDpToPx(context, 15), 0, DisplayUtils.convertDpToPx(context, 15), DisplayUtils.convertDpToPx(context, 15));

            RelativeLayout.LayoutParams paramBadge = (RelativeLayout.LayoutParams)badgeNew.getLayoutParams();
            paramBadge.leftMargin = DisplayUtils.convertDpToPx(context, 15);
            badgeNew.setLayoutParams(paramBadge);

            View viewRoot = view.findViewById(R.id.root);
            if ( viewRoot != null ) {
                viewRoot.setBackgroundColor(Color.WHITE);
            }
            RelativeLayout.LayoutParams paramsLayoutMainImg = (RelativeLayout.LayoutParams)layout_img_main.getLayoutParams();
            paramsLayoutMainImg.height = paramsLayoutMainImg.width;
            layout_img_main.setLayoutParams(paramsLayoutMainImg);
        }
        else {
            RelativeLayout.LayoutParams parmasNameTitle = (RelativeLayout.LayoutParams)nameTitle.getLayoutParams();
            parmasNameTitle.rightMargin = 0;
            nameTitle.setLayoutParams(parmasNameTitle);
        }

        final SectionContentList item = list.get(position);

        DisplayUtils.resizeHeightAtViewToScreenSize(context, viewInner);
        DisplayUtils.resizeHeightAtViewToScreenSize(context, layout_img_main);
        DisplayUtils.resizeHeightAtViewToScreenSize(context, mainImage);
//        이미지 크기를 정사각형으로, loadImageResize 하면서 이미지 사이즈 조절은 될 것이나 위치도 가운데에 오게끔 해야하기 때문에

        ImageUtil.loadImageResize(context, item.imageUrl, mainImage, R.drawable.noimg_logo);

//        RelativeLayout.LayoutParams paramsMainImage = (RelativeLayout.LayoutParams)mainImage.getLayoutParams();
//        paramsMainImage.height = paramsMainImage.width;
//        mainImage.setLayoutParams(paramsMainImage);

        if (item.imgBadgeCorner != null && item.imgBadgeCorner.LT != null && !item.imgBadgeCorner.LT.isEmpty()){
            final ImageBadge badge = item.imgBadgeCorner.LT.get(0);
            if (badge != null && !"".equals(badge.imgUrl)) {
                ViewUtils.showViews(badgeNew);
                ImageUtil.loadImageBadge(context, badge.imgUrl, badgeNew, R.drawable.transparent, HD);
            }
        }

        final List<ImageBadge> listBadgeBottom = item.imgBadgeCorner.RB;

        if (listBadgeBottom.size() > 0) {
            layoutAttach.setVisibility(View.VISIBLE);
            for (int i = 0; i < listBadgeBottom.size(); i++) {
                TextView txtBadge = new TextView(context);
                txtBadge.setTextAppearance(context, R.style.GSChoiceStatText);
                txtBadge.setText(listBadgeBottom.get(i).text);
                layoutAttach.addView(txtBadge);
                if (i < listBadgeBottom.size() - 1) {
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
        else {
            layoutAttach.setVisibility(View.GONE);
        }

        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                WebUtils.goWeb(context, item.linkUrl);

//                EventBus.getDefault().post(new Events.FlexibleEvent.StartRollingGSChoiceRollBannerEvent(false));
                //GTM 클릭이벤트 전달
//                String tempLabel = String.format("%s_%s_%s", label, position, item.linkUrl);
//                GTMAction.sendEvent(context, GTMEnum.GTM_AREA_CATEGORY, action, tempLabel);
            }
        });


        if (DisplayUtils.isValidString(item.exposPrSntncNm)) {
            titleStringBuilder.append(item.exposPrSntncNm);
        }

       if (!TextUtils.isEmpty(titleStringBuilder)) {
           nameTitle.setText(titleStringBuilder);
       }
       else {
           if (list.size() == 1) {
               ViewUtils.hideViews(nameTitle);
           }
       }

       if (DisplayUtils.isValidString(item.productName)) {
            nameTitleSub.setText(item.productName);
       }

        view.setContentDescription(item.exposPrSntncNm);

        // 렌탈상품(렌탈이면 가격앞에 월자 붙임)
        if (DisplayUtils.isTrue(item.isRental)) {
            if(item.salePrice == null || "".equals(item.salePrice) || "0".equals(item.salePrice)){
                txtPrice.setText("");
                txtPriceWon.setText("");
            }else {
                txtPrice.setText(context.getString(R.string.month) + DisplayUtils.getFormattedNumber(item.salePrice));
                txtPriceWon.setText(item.exposePriceText);
                txtPrice.setVisibility(View.VISIBLE);
                txtPriceWon.setVisibility(View.VISIBLE);
            }
        }
        else if (item.productType != null && "C".equals(item.productType)) {
            txt_badge_per.setText("");
            txtPrice.setText(DisplayUtils.getFormattedNumber(item.salePrice));
            txtPriceWon.setText(item.exposePriceText);
            txtPrice.setVisibility(View.VISIBLE);
            txtPriceWon.setVisibility(View.VISIBLE);

            txtPrice.setPadding(0,0,0,0);
        }else {
            // 아니면 가격 보이게
            // price

            if (DisplayUtils.isValidString(item.salePrice)) {
                // 가격
                txtPrice.setText(DisplayUtils.getFormattedNumber(item.salePrice));
                txtPriceWon.setText(item.exposePriceText);
                txtPrice.setVisibility(View.VISIBLE);
                txtPriceWon.setVisibility(View.VISIBLE);
            } else {
                txtPrice.setVisibility(View.GONE);
                txtPriceWon.setVisibility(View.GONE);
            }
        }

        // 퍼센트값으로 표현되는 할인률이 있을 경우에만 base price 표시
        // base price가 0이거나 salePrice가 basePrice보다 크면 basePrice 표시 안함
        int salePrice = DisplayUtils.getNumberFromString(item.salePrice);
        int basePrice = DisplayUtils.getNumberFromString(item.basePrice);
        // 할인률
        if (item.discountRate == null) {
            item.discountRate = "0";
        }
        if (Integer.parseInt(item.discountRate) > Keys.ZERO_DISCOUNT_RATE) {
            if (DisplayUtils.isValidNumberStringExceptZero(item.basePrice)
                    && (salePrice < basePrice)) {
                txtPriceBase.setText(DisplayUtils.getFormattedNumber(item.basePrice) +
                        item.exposePriceText);
                txtPriceBase.setVisibility(View.VISIBLE);
                txt_badge_per.setVisibility(View.VISIBLE);
                txt_badge_per.setText(item.discountRate + "%");
            }
        }else {
            if (item.basePrice != null && !"".equals(item.basePrice)
                    && !"0".equals(item.basePrice)) {
                txtPriceBase.setText(DisplayUtils.getFormattedNumber(item.basePrice) +
                        item.exposePriceText);
                txtPriceBase.setVisibility(View.VISIBLE);
            }
            txtPriceBase.setVisibility(View.GONE);
            txt_badge_per.setVisibility(View.GONE);
        }

        //판매 수량
        ProductInfoUtil.setSaleQuantity(item, layoutSalesQuantity, txtSalesQuantity,
                    txtSalesQuantityStr, txtSalesQuantitySubStr);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        // must be overridden else throws exception as not overridden.
        collection.removeView((View) view);
    }
}