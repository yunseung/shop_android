package gsshop.mobile.v2.home.shop.ltype;

import android.content.Context;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.TextView;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import roboguice.util.Ln;

public class CommonPriceUtil {
    private CommonPriceUtil() {}

    private static class InnerInstanceClass {
        private static final CommonPriceUtil instance = new CommonPriceUtil();
    }

    public static CommonPriceUtil getInstance() {
        return InnerInstanceClass.instance;
    }

    public void setPriceRule(Context context, SectionContentList product, View root, TextView productInfo, TextView tvPrice, TextView tvPriceUnit) {

        tvPriceUnit.setVisibility(View.GONE);
        //렌탈일때,
        if(("true".equals(product.deal) && ("R".equals(product.productType)/* 렌탈*/ ||
                "T".equals(product.productType)/*여행*/ || "U".equals(product.productType)/*딜 시공*/ ||
                "S".equals(product.productType)/*상품 시공*/)) ||
                ("false".equals(product.deal) && "R".equals(product.productType)/* 렌탈*/)) {

            tvPrice.setVisibility(View.VISIBLE);

            // 렌탈 에서만 rRentalText 를 비교한다.
            if ( "R".equals(product.productType) && product.rentalText != null && !product.rentalText.isEmpty() ) {
                //항목1에 "월 렌탈료"가 써있으면 -> "월"로 변경
                if (context.getString(R.string.commne_rental_unit).equals(product.rentalText) ||
                        context.getString(R.string.commne_rental_unit_ex).equals(product.rentalText)) {

                    AbsoluteSizeSpan bigSizeSpan = new AbsoluteSizeSpan(19);
                    AbsoluteSizeSpan smalSizeSpan = new AbsoluteSizeSpan(16);

                    SpannableStringBuilder spanStringBuilder = new SpannableStringBuilder();
                    spanStringBuilder.append(context.getString(R.string.commne_rental_unit) + " ");
                    spanStringBuilder.setSpan(smalSizeSpan, 0, spanStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    //"월" + 항목2(rRentalPrice) 값이 있으면 뿌린다
                    if (!TextUtils.isEmpty(product.mnlyRentCostVal) && !product.mnlyRentCostVal.startsWith("0")) {
                        spanStringBuilder.append(product.mnlyRentCostVal);
                        spanStringBuilder.setSpan(bigSizeSpan, spanStringBuilder.length() - product.mnlyRentCostVal.length(),
                                spanStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tvPrice.setText(spanStringBuilder);
                    }
                    else {
                        //렌탈이지만 "월 렌탈료"가 안써있으면 다 우지고 "상담 전용 상품입니다." 세긴다.
                        //txtPrice.setText("상담 전용 상품입니다.");
                        tvPrice.setText(R.string.common_rental_title);
                        tvPrice.setTextSize(16);
                    }
                }
                else {//렌탈인데 월이 아니면,
                    if (!TextUtils.isEmpty(product.mnlyRentCostVal) && !product.mnlyRentCostVal.startsWith("0")) {
                        tvPrice.setText(product.mnlyRentCostVal);
                        tvPrice.setTextSize(19);
                    }
                    else {
                        // "상담 전용 상품입니다." 세긴다.
                        //txtPrice.setText("상담 전용 상품입니다.");
                        tvPrice.setText(R.string.common_rental_title);
                        tvPrice.setTextSize(16);
                    }
                }
            }
            else if (!TextUtils.isEmpty(product.mnlyRentCostVal) && !product.mnlyRentCostVal.startsWith("0")) {
                tvPrice.setText(product.mnlyRentCostVal);
                tvPrice.setTextSize(19);
            }
            else {
                //렌탈이지만 "월 렌탈료"가 안써있으면 다 우지고 "상담 전용 상품입니다." 세긴다.
                //txtPrice.setText("상담 전용 상품입니다.");
                tvPrice.setText(R.string.common_rental_title);
                tvPrice.setTextSize(16);
            }
        }
        else {
            //보험일때, 계속 추가 할게요
            if ("I".equals(product.productType)) {
                //보험이면 타이틀만 보이자
                tvPrice.setVisibility(View.GONE);
            } else {
                // 그 외의 경우 sale price를 뿌려주어야 한다.
                tvPrice.setVisibility(View.VISIBLE);
                tvPriceUnit.setVisibility(View.VISIBLE);
                tvPriceUnit.setText(product.exposePriceText);

                tvPrice.setText(product.salePrice);
                tvPrice.setTextSize( 19);

            }
        }

        try {
            String strTitle = "";
            if (productInfo != null) {
                strTitle = productInfo.getText() != null ? productInfo.getText().toString() : "";
            }
            String strPrice = tvPrice.getText() != null ? tvPrice.getText().toString() : "";
            String strPriceUnit = tvPriceUnit.getText() != null ? tvPriceUnit.getText().toString() : "";

            root.setContentDescription(strTitle + strPrice + strPriceUnit);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                String finalStrTitle = strTitle;
                root.setAccessibilityDelegate(new View.AccessibilityDelegate() {
                    @Override
                    public void onPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
                        super.onPopulateAccessibilityEvent(host, event);
                        event.setContentDescription(finalStrTitle + strPrice + strPriceUnit);
                    }
                });
            }
        }
        catch (NullPointerException e) {
            Ln.e(e.getMessage());
        }
    }

}
