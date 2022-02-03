package gsshop.mobile.v2.home.shop.vod;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;

/**
 * 2019.08.29 yun.
 * 내일 tv 카드 할인정보 뷰홀더 추가.
 */
public class MapCxTxtGbbVH extends BaseViewHolder {

    private TextView mTxtTitle, mTxtDate, mCardName1, mCardName2, mDiscountRate1, mDiscountRate2, mPer1, mPer2, mCardDescription = null;
    private LinearLayout mLlSecondCardArea = null;

    public MapCxTxtGbbVH(View itemView) {
        super(itemView);

        mTxtTitle = itemView.findViewById(R.id.txt_title);
        mTxtDate = itemView.findViewById(R.id.txt_date);
        mCardName1 = itemView.findViewById(R.id.txt_card_name_1);
        mCardName2 = itemView.findViewById(R.id.txt_card_name_2);
        mDiscountRate1 = itemView.findViewById(R.id.txt_discount_rate1);
        mDiscountRate2 = itemView.findViewById(R.id.txt_discount_rate2);
        mPer1 = itemView.findViewById(R.id.per1);
        mPer2 = itemView.findViewById(R.id.per2);
        mLlSecondCardArea = itemView.findViewById(R.id.ll_second_card);
        mCardDescription = itemView.findViewById(R.id.txt_card_description);
    }

    @Override
    public void onBindViewHolder(Context context, int position, ShopInfo info, String action, String label, String sectionName) {
        super.onBindViewHolder(context, position, info, action, label, sectionName);
        final SectionContentList item = info.contents.get(position).sectionContent;
        mTxtTitle.setText(item.productName);
        mTxtDate.setText(item.promotionName);

        // 카드가 하나일 경우 ex 신한카드 & 즉시할인 이렇게 내려오기 때문에 split 해서 뿌려줌.
        if (item.subProductList.get(0).productName.contains("&")) {
                mCardName1.setText(item.subProductList.get(0).productName.trim().split("&")[0]);
                mCardDescription.setText(item.subProductList.get(0).productName.trim().split("&")[1]);
        } else {
            mCardName1.setText(item.subProductList.get(0).productName);
            mCardDescription.setVisibility(View.GONE);
        }

        mDiscountRate1.setText(item.subProductList.get(0).discountRate);
        mDiscountRate1.setTextColor(Color.parseColor("#" + item.subProductList.get(0).etcText1));
        mPer1.setTextColor(Color.parseColor("#" + item.subProductList.get(0).etcText1));

        // 카드가 갯수에 따른 분기 (1개 또는 2개)
        if (item.subProductList.size() > 1) {
            mLlSecondCardArea.setVisibility(View.VISIBLE);
            mCardName2.setText(item.subProductList.get(1).productName);
            mDiscountRate2.setText(item.subProductList.get(1).discountRate);
            mDiscountRate2.setTextColor(Color.parseColor("#" + item.subProductList.get(1).etcText1));
            mPer2.setTextColor(Color.parseColor("#" + item.subProductList.get(1).etcText1));
        } else {
            mLlSecondCardArea.setVisibility(View.GONE);
        }
    }
}
