package gsshop.mobile.v2.home.shop.bestdeal;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.renewal.views.CommonTitleLayout;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;

/**
 * 2019.08.29 yun.
 * 내일 tv 카드 할인정보 뷰홀더 추가.
 */
public class BanImgC5GbaVH extends BaseViewHolder {

    private ArrayList<LinearLayout> mLlType = new ArrayList<>();

    private ArrayList<RelativeLayout> mRlServiceAreaList_t1 = new ArrayList<>();
    private ArrayList<TextView> mTxtProductList_t1 = new ArrayList<>();
    private ArrayList<ImageView> mImgList_t1 = new ArrayList<>();
    private ArrayList<View> mViewSpaceArrayList = new ArrayList<>();

    private ArrayList<View> mRlServiceAreaList_t2 = new ArrayList<>();
    private ArrayList<TextView> mTxtPromotionList_t2 = new ArrayList<>();
    private ArrayList<TextView> mTxtProductList2_2 = new ArrayList<>();
    private ArrayList<ImageView> mImgList_t2 = new ArrayList<>();

    private View mBackTop;

    private final CommonTitleLayout mCommonTitleLayout;

    public BanImgC5GbaVH(View itemView, String naviId) {
        super(itemView);

        mCommonTitleLayout = itemView.findViewById(R.id.common_title_layout);

        // 3 개 이상일 경우
        mLlType.add(itemView.findViewById(R.id.service_type_1));
        mLlType.add(itemView.findViewById(R.id.service_type_2));

        mRlServiceAreaList_t1.add(itemView.findViewById(R.id.service_1));
        mRlServiceAreaList_t1.add(itemView.findViewById(R.id.service_2));
        mRlServiceAreaList_t1.add(itemView.findViewById(R.id.service_3));
        mRlServiceAreaList_t1.add(itemView.findViewById(R.id.service_4));
        mRlServiceAreaList_t1.add(itemView.findViewById(R.id.service_5));

        mImgList_t1.add(itemView.findViewById(R.id.img_service_1));
        mImgList_t1.add(itemView.findViewById(R.id.img_service_2));
        mImgList_t1.add(itemView.findViewById(R.id.img_service_3));
        mImgList_t1.add(itemView.findViewById(R.id.img_service_4));
        mImgList_t1.add(itemView.findViewById(R.id.img_service_5));

        mTxtProductList_t1.add(itemView.findViewById(R.id.txt_service_1));
        mTxtProductList_t1.add(itemView.findViewById(R.id.txt_service_2));
        mTxtProductList_t1.add(itemView.findViewById(R.id.txt_service_3));
        mTxtProductList_t1.add(itemView.findViewById(R.id.txt_service_4));
        mTxtProductList_t1.add(itemView.findViewById(R.id.txt_service_5));

        mViewSpaceArrayList.add(itemView.findViewById(R.id.space_2));
        mViewSpaceArrayList.add(itemView.findViewById(R.id.space_3));
        mViewSpaceArrayList.add(itemView.findViewById(R.id.space_4));
        mViewSpaceArrayList.add(itemView.findViewById(R.id.space_5));
        mViewSpaceArrayList.add(itemView.findViewById(R.id.space_6));
        ////////////////////////////////////////////////////////////////////////////


        // 2 개일 경우
        mRlServiceAreaList_t2.add(itemView.findViewById(R.id.service_6));
        mRlServiceAreaList_t2.add(itemView.findViewById(R.id.service_7));

        mImgList_t2.add(itemView.findViewById(R.id.img_service_6));
        mImgList_t2.add(itemView.findViewById(R.id.img_service_7));

        mTxtPromotionList_t2.add(itemView.findViewById(R.id.txt_type2_1st_1));
        mTxtPromotionList_t2.add(itemView.findViewById(R.id.txt_type2_2nd_1));

        mTxtProductList2_2.add(itemView.findViewById(R.id.txt_type2_1st_2));
        mTxtProductList2_2.add(itemView.findViewById(R.id.txt_type2_2nd_2));

        mBackTop = itemView.findViewById(R.id.back_top);
        ////////////////////////////////////////////////////////////////////////////
    }

    @Override
    public void onBindViewHolder(Context context, int position, ShopInfo info, String action, String label, String sectionName) {
        super.onBindViewHolder(context, position, info, action, label, sectionName);
        final SectionContentList item = info.contents.get(position).sectionContent;
        if (TextUtils.isEmpty(item.productName) && TextUtils.isEmpty(item.promotionName) && TextUtils.isEmpty(item.imageUrl)) {
            if( mCommonTitleLayout != null) {
                mCommonTitleLayout.setVisibility(View.GONE);
            }
        }
        else {
            if( mCommonTitleLayout != null) {
                mCommonTitleLayout.setVisibility(View.VISIBLE);
                mCommonTitleLayout.setCommonTitle(this, item);
            }
        }

        if (item.subProductList.size() > 2) { // 상품이 3 개 이상일 경우.
            mBackTop.setVisibility(View.GONE);

            mLlType.get(0).setVisibility(View.VISIBLE);
            mLlType.get(1).setVisibility(View.GONE);

            if (item.subProductList.size() == 5) {
                for (ImageView view : mImgList_t1) {
                    DisplayUtils.changeViewSizeInDp(context, view, 52, 52);
                }
            }

            for (int i = 0; i < item.subProductList.size(); i++) {
                mRlServiceAreaList_t1.get(i).setVisibility(View.VISIBLE);
                mViewSpaceArrayList.get(i).setVisibility(View.VISIBLE);
                mRlServiceAreaList_t1.get(i).setTag(item.subProductList.get(i).linkUrl);
                ImageUtil.loadImageFit(context, item.subProductList.get(i).imageUrl, mImgList_t1.get(i), R.drawable.ban_img_c5_gba_skleton);
                mTxtProductList_t1.get(i).setText(item.subProductList.get(i).productName);

                mRlServiceAreaList_t1.get(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        WebUtils.goWeb(context, view.getTag().toString());
                    }
                });
            }
        } else { // 상품이 2 개일 경우.
            mLlType.get(0).setVisibility(View.GONE);
            mLlType.get(1).setVisibility(View.VISIBLE);

            mBackTop.setVisibility(View.VISIBLE);
            mCommonTitleLayout.setSeperateDisable(false);
            mCommonTitleLayout.setBackgroundColor(Color.parseColor("#00000000"));
            mCommonTitleLayout.setRootBackgroundColor(Color.parseColor("#00000000"));
            mCommonTitleLayout.setTitleTxtColor(Color.parseColor("#ffffff"));

            for (int i = 0; i < item.subProductList.size(); i++) {
                mRlServiceAreaList_t2.get(i).setTag(item.subProductList.get(i).linkUrl);
                ImageUtil.loadImageFit(context, item.subProductList.get(i).imageUrl, mImgList_t2.get(i), R.drawable.ban_img_c5_gba_skleton);

                // 상단 promotion text 가 없을 수도 있음. 그럴 경우 하단 product text 가운데 정렬
                if (item.subProductList.get(i).promotionName.isEmpty()) {
                    mTxtPromotionList_t2.get(i).setVisibility(View.GONE);
                } else {
                    mTxtPromotionList_t2.get(i).setVisibility(View.VISIBLE);
                    mTxtPromotionList_t2.get(i).setText(item.subProductList.get(i).promotionName);
                }

                mTxtProductList2_2.get(i).setText(item.subProductList.get(i).productName);

                mRlServiceAreaList_t2.get(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        WebUtils.goWeb(context, view.getTag().toString());
                    }
                });
            }
        }
    }
}
