/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.renewal.flexible;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.inject.Inject;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.pattern.mvc.BaseAsyncController;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.ModuleList;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.main.TextImageModule;
import gsshop.mobile.v2.home.shop.BaseViewHolderV2;
import gsshop.mobile.v2.home.shop.RequestGetResult;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.bestshop.GSXBrandBannerZZimDialogFragment;
import gsshop.mobile.v2.library.viewpager.InfiniteViewPager;
import gsshop.mobile.v2.user.User;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.util.StringUtils;
import gsshop.mobile.v2.util.SwipeUtils;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.util.Ln;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;
import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;
import static gsshop.mobile.v2.MainApplication.useNativeProduct;
import static gsshop.mobile.v2.util.StringUtils.trim;

/**
 * 리뉴얼 된 홀더는 모두 새로 CP 로 생성.
 */
@SuppressLint("NewApi")
public class PmoT1xxxVH extends BaseViewHolderV2 {
    private static final int DIVIDER_SPACE_01 = MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.renewal_vh_promotion_blank_space_side);
    private static final int DIVIDER_SPACE_02 = MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.renewal_vh_promotion_blank_space_between);

    /**
     * 컨텍스트
     */
    private Context mContext;

    /**
     * 전체 영역 (Color 정보 있으면 설정)
     */
    private final View mViewBrand;

    /**
     * 상단 Main Image
     */
    private final ImageView mBackgroundBrand;

    /**
     * 하단 그라디에이션 영역 (색상 있으면 gone)
     */
    private final ImageView mGradation;

    /**
     * 상단 Zzim 영역 GS X Brand 에서만 보임.
     */
    private final LinearLayout mLlZzimArea;
    private final CheckBox mCbZzim;
    private final TextView mTvZzimCnt;
    private int mZzimCnt;

    /**
     * 상단 부분의 우하단에 더보기 버튼
     */
    private final TextView mTxtViewMore;

    /**
     * Brand가 아닐때 이미지만 노출, 이미지를 감싸는 뷰 영역
     */
    private final View mViewImage;

    /**
     * Brand가 아닐때 이미지만 노출
     */
    private final ImageView mBackgroundImage;

    /**
     * 상단 Text / img 정보 영역
     */
    private final View mViewProductInfo;

    /**
     * 상품 head(main) copy
     */
    private final TextView mTxtCopySub;

    /**
     * 상품 head 브랜드 이미지 영역 (영역 안으로 묶어야 정상적으로 브랜드 이미지 보임)
     */
    private final View mViewImgHead;

    /**
     * 상품 head 브랜드 이미지 불러올 imageview
     */
    private final ImageView mImgCopyHead;

    /**
     * 상품 head 브랜드 이미지가 없을경우 textHead 노출
     */
    private final TextView mTxtCopyHead;

    /**
     * 프로모션 카피 텍스트 첫번째
     */
    private final TextView mTxtPromotionCopyFirst;

    /**
     * 프로모션 카피 텍스트 두번쨰
     */
    private final TextView mTxtPromotionCopySecond;

    /**
     * 하단 상품 영역
     */
    private final RecyclerView mRecyclerGoods;

    private PROMOTION_TYPE mType = PROMOTION_TYPE.DEFAULT;

    private RecyclerView.ItemDecoration itemDecoration;

    // 프로모션 타입에 따라 모양이 바뀐다.
    public enum PROMOTION_TYPE {
        DEFAULT,
        BRAND,
        IMAGE,
        BRAND_GS_X
    }

    private final int COLOR_TEXT_WHITE = Color.WHITE;
    private final int COLOR_TEXT_DARK_GREY = Color.parseColor("#111111");
    private final int COLOR_TEXT_GREY = Color.parseColor("#7a111111");

    /**
     * @param itemView
     */
    public PmoT1xxxVH(View itemView, PROMOTION_TYPE type) {
        super(itemView);

        //스와이프 허용각도 확대 적용
        increaseSwipeAngle = true;

        mViewBrand = itemView.findViewById(R.id.view_brand);
        mBackgroundBrand = (ImageView) itemView.findViewById(R.id.img_background_brand);
        mGradation = (ImageView) itemView.findViewById(R.id.img_dark_gra);

        mLlZzimArea = itemView.findViewById(R.id.zzim_area);
        mCbZzim = (CheckBox) itemView.findViewById(R.id.check_brand_zzim);
        mTvZzimCnt = itemView.findViewById(R.id.tv_zzim_cnt);
        mTxtViewMore = (TextView) itemView.findViewById(R.id.txt_view_more);

        mViewImage = itemView.findViewById(R.id.view_image);
        mBackgroundImage = (ImageView) itemView.findViewById(R.id.img_background_image);

        mViewProductInfo = itemView.findViewById(R.id.layout_product_info);

        mViewImgHead = itemView.findViewById(R.id.view_img_head);
        mImgCopyHead = (ImageView) itemView.findViewById(R.id.img_head);

        mTxtCopySub = (TextView) itemView.findViewById(R.id.txt_copy_sub);
        mTxtCopyHead = (TextView) itemView.findViewById(R.id.txt_copy_head);
        mTxtPromotionCopyFirst = (TextView) itemView.findViewById(R.id.txt_copy_promotion_1);
        mTxtPromotionCopySecond = (TextView) itemView.findViewById(R.id.txt_copy_promotion_2);

        mRecyclerGoods = (RecyclerView) itemView.findViewById(R.id.recycler_goods);
        mType = type;
    }

    @Override
    public void onBindViewHolder(Context context, int position, ShopInfo info, String action, String label, String sectionName) {
        final SectionContentList item = info.contents.get(position).sectionContent.subProductList.get(0);
        this.navigationId = info.naviId;
        setView(context, item, action, label, false);
    }

    // L Type
    @Override
    public void onBindViewHolder(Context context, int position, List<ModuleList> moduleList) {
        final SectionContentList item = moduleList.get(position);
        setView(context, item, null, null, true);
    }

    private void setView(final Context context, SectionContentList item,
                         final String action, final String label, boolean isLType) {
        this.mContext = context;

        mGradation.setVisibility(View.GONE);

        // item이 null일 경우는 없지만 null일 매장이 나오지 않는 문제는 발생하면 않되기 때문에 new
        if (item == null) {
            item = new SectionContentList();
        }

        if (mType == PROMOTION_TYPE.BRAND || mType == PROMOTION_TYPE.BRAND_GS_X) {
            mViewBrand.setVisibility(View.VISIBLE);
            mViewImage.setVisibility(View.GONE);
            ImageUtil.loadImageResize(context, item.tabBgImg, mBackgroundBrand, R.drawable.noimage_375_188);
            ImageUtil.loadImageResizeToHeight(context, item.tabImg, mImgCopyHead, R.drawable.noimg_logo);
        } else {
            mViewBrand.setVisibility(View.GONE);
            mViewImage.setVisibility(View.VISIBLE);
            ImageUtil.loadImageResize(context, item.tabBgImg, mBackgroundImage, R.drawable.no_img_550);
        }

        if (mType == PROMOTION_TYPE.IMAGE) {
            mRecyclerGoods.setVisibility(View.GONE);
        } else {
//            백그라운드 색상정보 설정
            if (!TextUtils.isEmpty(item.bgColor)) {
                if (StringUtils.checkHexColor("#" + item.bgColor)) {
                    mViewBrand.setBackgroundColor(Color.parseColor("#" + (item.bgColor)));
                } else {
                    // 백그라운드 색상 정보가 정상적이지 않다면 그라데이션 영역을 노출
                    mGradation.setVisibility(View.VISIBLE);
                }
            } else {
                // 백그라운드 색상 정보가 정상적이지 않다면 그라데이션 영역을 노출
                mGradation.setVisibility(View.VISIBLE);
            }

            // 서브카피 설정 (없으면 gone)
            if (TextUtils.isEmpty(item.subName)) {
                mTxtCopySub.setVisibility(View.GONE);
            } else {
                mTxtCopySub.setVisibility(View.VISIBLE);
                mTxtCopySub.setText(item.subName);
            }

            // imageUrl이 없으면 name을 보고 name 마저 없으면 영역 gone
            mViewProductInfo.setVisibility(View.VISIBLE);
            mTxtCopyHead.setVisibility(View.GONE);
            if (TextUtils.isEmpty(item.tabImg)) {
                mViewImgHead.setVisibility(View.GONE);
                if (TextUtils.isEmpty(item.name)) {
                    mViewProductInfo.setVisibility(View.GONE);
                } else {
                    mTxtCopyHead.setVisibility(View.VISIBLE);
                    mTxtCopyHead.setText(item.name);
                }
            }
            // tabImg 있을 경우 이미지, 이미지는 이미 위에서 set 하였기 떄문에 visible만
            else {
                mViewImgHead.setVisibility(View.VISIBLE);
            }

            if (item.textImageModule == null) {
                item.textImageModule = new TextImageModule();
            }

            // 프로모션 카피1 설정
            if (TextUtils.isEmpty(item.textImageModule.title1)) {
                mTxtPromotionCopyFirst.setVisibility(View.GONE);
            } else {
                mTxtPromotionCopyFirst.setVisibility(View.VISIBLE);
                mTxtPromotionCopyFirst.setText(item.textImageModule.title1);
            }

            // 프로모션 카피2 설정
            if (TextUtils.isEmpty(item.textImageModule.title2)) {
                mTxtPromotionCopySecond.setVisibility(View.GONE);
            } else {
                mTxtPromotionCopySecond.setVisibility(View.VISIBLE);
                mTxtPromotionCopySecond.setText(item.textImageModule.title2);
            }

            //Text 색상값 설정.
            if (item.textImageModule.textColor == null) {
                item.textImageModule.textColor = "C";
            }
            if ("A".equals(item.textImageModule.textColor)) {
                mTxtCopySub.setTextColor(COLOR_TEXT_WHITE);
                mTxtCopyHead.setTextColor(COLOR_TEXT_WHITE);
                mTxtPromotionCopyFirst.setTextColor(COLOR_TEXT_DARK_GREY);
                mTxtPromotionCopySecond.setTextColor(COLOR_TEXT_DARK_GREY);
            } else if ("B".equals(item.textImageModule.textColor)) {
                mTxtCopySub.setTextColor(COLOR_TEXT_GREY);
                mTxtCopyHead.setTextColor(COLOR_TEXT_DARK_GREY);
                mTxtPromotionCopyFirst.setTextColor(COLOR_TEXT_DARK_GREY);
                mTxtPromotionCopySecond.setTextColor(COLOR_TEXT_DARK_GREY);
            } else {
                mTxtCopySub.setTextColor(COLOR_TEXT_WHITE);
                mTxtCopyHead.setTextColor(COLOR_TEXT_WHITE);
                mTxtPromotionCopyFirst.setTextColor(COLOR_TEXT_WHITE);
                mTxtPromotionCopySecond.setTextColor(COLOR_TEXT_WHITE);
            }

            // 찜 설정.
            if (item.isWishEnable) {
                mLlZzimArea.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(item.wishCnt)) {
                    mZzimCnt = Integer.parseInt(item.wishCnt.replaceAll(",", ""));
                    if (mZzimCnt > 0) {
                        mTvZzimCnt.setVisibility(View.VISIBLE);
                        mTvZzimCnt.setText(item.wishCnt);
                    } else {
                        mTvZzimCnt.setVisibility(View.GONE);
                    }
                }
                mCbZzim.setChecked(item.isWish);

                final String brandWishAddUrl = item.brandWishAddUrl;
                final String brandWishRemoveUrl = item.brandWishRemoveUrl;

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
            } else {
                mLlZzimArea.setVisibility(View.GONE);
            }

            // 더보기 설정
            if (TextUtils.isEmpty(item.linkUrl)) {
                mTxtViewMore.setVisibility(View.GONE);
            } else {
                mTxtViewMore.setVisibility(View.VISIBLE);
                final String linkUrl = item.linkUrl;
                mTxtViewMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        WebUtils.goWeb(context, linkUrl);
                    }
                });
            }

            if (itemDecoration == null) {            //아이템 간격 세팅
                itemDecoration = new SpacesItemDecoration();
                mRecyclerGoods.addItemDecoration(itemDecoration);
            }

            mRecyclerGoods.setVisibility(View.VISIBLE);
            mRecyclerGoods.setHasFixedSize(true);

            LinearLayoutManager llm = new LinearLayoutManager(context);
            llm.setOrientation(LinearLayoutManager.HORIZONTAL);

            mRecyclerGoods.setLayoutManager(llm);
            ArrayList<SectionContentList> tempList;
            if (isLType) {
                tempList = item.productList;
            } else {
                tempList = item.subProductList;
            }
            RenewalFlexiblePromotionAdapter adapter = new RenewalFlexiblePromotionAdapter(context, tempList, action, label);
            mRecyclerGoods.setAdapter(adapter);

            mRecyclerGoods.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                        case MotionEvent.ACTION_MOVE:
                            SwipeUtils.INSTANCE.disableSwipe();
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            setOnTouchUp();
                            break;
                    }
                    return false;
                }
            });

            mRecyclerGoods.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    //스크롤 멈춘시점에 이미지 캐싱 진행
                    if (useNativeProduct) {
                        if (newState == SCROLL_STATE_IDLE) {
                            EventBus.getDefault().post(new Events.ImageCacheStartEvent(navigationId));
                        }
                    }
                }
            });
        }
    }

    @Override
    public void setOnTouchUp() {
        if (mType == PROMOTION_TYPE.IMAGE) {
            return;
        }
        for (int i = 0; i < mRecyclerGoods.getChildCount(); i++) {
            mRecyclerGoods.getChildAt(i).setScaleX(1);
            mRecyclerGoods.getChildAt(i).setScaleY(1);
        }
    }

    /**
     * 아이템간격을 데코레이션 한다.
     */
    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            final int position = parent.getChildAdapterPosition(view);
            if (position == 0) {
                outRect.left = DIVIDER_SPACE_01;
            }

            if (position == parent.getAdapter().getItemCount() - 1) {
                outRect.right = DIVIDER_SPACE_01;
            } else {
                outRect.right = DIVIDER_SPACE_02;
            }
        }
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

    @Override
    public void onViewAttachedToWindow() {
        super.onViewAttachedToWindow();
        try {
            EventBus.getDefault().register(this);
        } catch (Exception e) {
            Ln.e(e);
        }
    }

    @Override
    public void onViewDetachedFromWindow() {
        super.onViewDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 이미지가 화면상에 노출된 경우에 한해 이미지 캐시작업을 수행한다.
     *
     * @param event ImageCacheStartEvent
     */
    public void onEvent(Events.ImageCacheStartEvent event) {
        if (isNotEmpty(navigationId) && !navigationId.equals(event.naviId)) {
            //내가 속한 매장에 대한 이벤트가 아니면 스킵
            return;
        }
        for (int i = 0; i < mRecyclerGoods.getChildCount(); i++) {
            View view = mRecyclerGoods.getChildAt(i);
            RenewalFlexiblePromotionAdapter.RecommendViewHolder vh =
                    (RenewalFlexiblePromotionAdapter.RecommendViewHolder) mRecyclerGoods.getChildViewHolder(view);
            if (DisplayUtils.isVisible(vh.product_img)) {
                String src = vh.getImageForCache();
                if (isNotEmpty(src) && src.contains(IMG_CACHE_RPL3_FROM)) {
                    String imgUrl = src.replace(IMG_CACHE_RPL3_FROM, IMG_CACHE_RPL_TO);
                    Glide.with(mContext).load(trim(imgUrl)).downloadOnly(IMG_CACHE_WIDTH, IMG_CACHE_HEIGHT);
                }
            }
        }
    }
}
