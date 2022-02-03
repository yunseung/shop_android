/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.bestshop;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.google.inject.Inject;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.pattern.mvc.BaseAsyncController;

import java.text.NumberFormat;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.RequestGetResult;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.user.User;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;

/**
 * 전체 배스트 시간 추가된 텍스트 베너
 */
public class PmoT2AVH extends BaseViewHolder {
    private ImageView mIvImg;
    private TextView mTvBrandName, mTvSubInfo, mTvZzimCnt, mTvViewMore;
    private CheckBox mCbZzim;
    private int mZzimCnt;

    /**
     * @param itemView itemView
     */
    public PmoT2AVH(View itemView) {
        super(itemView);

        mIvImg = itemView.findViewById(R.id.iv_img);
        mTvBrandName = itemView.findViewById(R.id.tv_brand_name);
        mTvSubInfo = itemView.findViewById(R.id.tv_sub_info);
        mTvZzimCnt = itemView.findViewById(R.id.tv_zzim_cnt);
        mTvViewMore = itemView.findViewById(R.id.tv_view_more);
        mCbZzim = itemView.findViewById(R.id.cb_zzim);

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

        final SectionContentList content = info.contents.get(position).sectionContent;

        ImageUtil.loadImage(context, content.imageUrl, mIvImg, R.drawable.noimage_375_188);
        mTvBrandName.setText(content.productName);
        mTvSubInfo.setText(content.promotionName);
        if (!TextUtils.isEmpty(content.wishCnt)) {
            mZzimCnt = Integer.parseInt(content.wishCnt.replaceAll(",", ""));
            if (mZzimCnt > 0) {
                mTvZzimCnt.setVisibility(View.VISIBLE);
                mTvZzimCnt.setText(NumberFormat.getInstance().format(mZzimCnt));
            } else {
                mTvZzimCnt.setVisibility(View.GONE);
            }
        }
        mCbZzim.setChecked(content.isWish);
        final String brandWishAddUrl = content.brandWishAddUrl;
        final String brandWishRemoveUrl = content.brandWishRemoveUrl;

        mTvViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebUtils.goWeb(context, content.linkUrl);
            }
        });

        mCbZzim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = User.getCachedUser();
                if (user == null || user.customerNumber.length() < 2) {
                    Intent intent = new Intent(Keys.ACTION.LOGIN);
                    intent.putExtra(Keys.INTENT.NAVIGATION_ID, ShopInfo.NAVIGATION_GSX_BRAND);
                    context.startActivity(intent);

                    mCbZzim.setChecked(!mCbZzim.isChecked());
                    return;
                }

                if (mCbZzim.isChecked()) {
                    new PromotionZzimController(context).execute(brandWishAddUrl);
                } else {
                    new PromotionZzimController(context).execute(brandWishRemoveUrl);
                }
            }
        });
    }

    private class PromotionZzimController extends BaseAsyncController<RequestGetResult> {
        private String url;
        private final Context mContext;

        @Inject
        private RestClient restClient;

        public PromotionZzimController(Context context) {
            super(context);
            mContext = context;
        }

        @Override
        protected void onPrepare(Object... params) throws Exception {
            if (this.dialog != null) {
                this.dialog.setCancelable(false);
                this.dialog.show();
            }

            url = (String) params[0];
        }

        @Override
        protected RequestGetResult process() throws Exception {
            return restClient.getForObject(url, RequestGetResult.class);
        }

        @Override
        protected void onSuccess(final RequestGetResult result) throws Exception {
            setZzimResult(context, result);
        }
    }

    private void setZzimResult(Context context, RequestGetResult result) {
        if (result != null) {
            if (result.success) {
                GSXBrandBannerZZimDialogFragment dialog = GSXBrandBannerZZimDialogFragment.newInstance(mCbZzim.isChecked(), result.linkUrl);
                dialog.show(((FragmentActivity) context).getSupportFragmentManager(), GSXBrandBannerZZimDialogFragment.class.getSimpleName());
                if (mCbZzim.isChecked()) {
                    mZzimCnt++;
                } else {
                    mZzimCnt--;
                }
                if (mZzimCnt > 0) {
                    mTvZzimCnt.setVisibility(View.VISIBLE);
                    mTvZzimCnt.setText(NumberFormat.getInstance().format(mZzimCnt));
                } else {
                    mTvZzimCnt.setVisibility(View.GONE);
                }
            } else {
                mCbZzim.setChecked(!mCbZzim.isChecked());
            }
        } else {
            mCbZzim.setChecked(!mCbZzim.isChecked());
        }
    }
}
