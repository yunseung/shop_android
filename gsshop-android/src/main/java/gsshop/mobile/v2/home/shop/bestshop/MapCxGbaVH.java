/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.bestshop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.inject.Inject;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.ui.util.ViewUtils;

import java.util.concurrent.atomic.AtomicBoolean;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.RequestGetResult;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.flexible.FlexibleRecommendAdapter;
import gsshop.mobile.v2.user.User;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.RoboGuice;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.ConvertUtils.dp2px;

/**
 * GS X Brand 배너
 */
@SuppressLint("NewApi")
public class MapCxGbaVH extends BaseViewHolder {

    private static final int COLUMN_COUNT = 3;
    private final TextView mainText;
    private final ImageView mainImage;
    private final View moreView;
    private final View brandShopView;
    private final RecyclerView recycler;
    /**
     * 브랜드 찜버튼
     */
    private final CheckBox zzimCheck;

    private final AtomicBoolean isAddedItemDecoration = new AtomicBoolean(false);
    private FlexibleRecommendAdapter adapter;

    @Inject
    private RestClient restClient;

    /**
     * @param itemView itemView
     */
    public MapCxGbaVH(View itemView) {
        super(itemView);
        RoboGuice.getInjector(MainApplication.getAppContext()).injectMembers(this);

        mainImage = (ImageView) itemView.findViewById(R.id.image_main);
        mainText = (TextView) itemView.findViewById(R.id.text_main);
        moreView = itemView.findViewById(R.id.view_more);
        brandShopView = itemView.findViewById(R.id.view_go_brand_shop);
        recycler = (RecyclerView) itemView.findViewById(R.id.recycle_prds);
        zzimCheck = itemView.findViewById(R.id.check_gs_x_brand_zzim);

        DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), mainImage);

    }

    /*
     * bind
     */
    @Override
    public void onBindViewHolder(final Context context, int position, ShopInfo info, final String action, final String label, String sectionName) {
        final SectionContentList item = info.contents.get(position).sectionContent.subProductList.get(0);
        ImageUtil.loadImageResizeToHeight(context, item.imageUrl, mainImage, R.drawable.noimg_logo);
        mainText.setText(item.productName);
        mainImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(item.linkUrl)) {
                    WebUtils.goWeb(context, item.linkUrl);
                }
            }
        });
        if (isAddedItemDecoration.compareAndSet(false, true)) {
            recycler.setLayoutManager(new GridLayoutManager(context, COLUMN_COUNT));
            recycler.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    super.getItemOffsets(outRect, view, parent, state);

                    outRect.left = dp2px(5f);
                    outRect.top = dp2px(10f);
                    outRect.right = dp2px(5f);
                    outRect.bottom = dp2px(10f);
                }
            });
        }

        // 상품이 3개일 경우 브랜드샵 바로가기 표시.
        if(item.subProductList.size() <= COLUMN_COUNT) {
            item.isMore = false;
        }

        if (item.isMore) {
            ViewUtils.showViews(moreView);
            ViewUtils.hideViews(brandShopView);
            this.adapter = new FlexibleRecommendAdapter(context, item.subProductList, COLUMN_COUNT, action, label);
        } else {
            ViewUtils.hideViews(moreView);
            ViewUtils.showViews(brandShopView);
            this.adapter = new FlexibleRecommendAdapter(context, item.subProductList, item.subProductList.size(), action, label);
        }

        recycler.setAdapter(adapter);
        // 더보기
        moreView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtils.showViews(brandShopView);
                ViewUtils.hideViews(moreView);
                item.isMore = false;
                adapter.setLength(item.subProductList.size());
            }
        });
        // 브랜드샵 바로가기
        brandShopView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(item.linkUrl)) {
                    WebUtils.goWeb(context, item.linkUrl);
                }
            }
        });

        zzimCheck.setChecked(item.isWish);

//        zzimCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                GSXBrandBannerZZimDialogFragment dialog = GSXBrandBannerZZimDialogFragment.newInstance(isChecked, "http://m.gsshop.com/section/myWishBrandMain.gs?_=1551079805246");
//                dialog.show(((FragmentActivity) context).getSupportFragmentManager(), GSXBrandBannerZZimDialogFragment.class.getSimpleName());
//            }
//        });

        zzimCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = User.getCachedUser();
                if (user == null || user.customerNumber.length() < 2) {
                    Intent intent = new Intent(Keys.ACTION.LOGIN);
                    intent.putExtra(Keys.INTENT.NAVIGATION_ID, ShopInfo.NAVIGATION_GSX_BRAND);
                    context.startActivity(intent);

                    zzimCheck.setChecked(!zzimCheck.isChecked());
                    return;
                }

                RequestGetResult resultCode = null;
                try {
                    if (zzimCheck.isChecked()) {
                        resultCode = restClient.getForObject(item.brandWishAddUrl, RequestGetResult.class);
                    } else {
                        resultCode = restClient.getForObject(item.brandWishRemoveUrl, RequestGetResult.class);
                    }
                }
                catch (IllegalArgumentException e) {
                    Ln.e(e.getMessage());
                }

                if (resultCode != null ) {
                    if (resultCode.success) {
                        GSXBrandBannerZZimDialogFragment dialog = GSXBrandBannerZZimDialogFragment.newInstance(zzimCheck.isChecked(), resultCode.linkUrl);
                        dialog.show(((FragmentActivity) context).getSupportFragmentManager(), GSXBrandBannerZZimDialogFragment.class.getSimpleName());
                    }
                    else {
                        zzimCheck.setChecked(!zzimCheck.isChecked());
                    }
                }
                else {
                    zzimCheck.setChecked(!zzimCheck.isChecked());
                }
            }
        });
    }

}
