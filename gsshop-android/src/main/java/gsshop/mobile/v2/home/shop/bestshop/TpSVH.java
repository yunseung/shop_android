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
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gsshop.mocha.ui.util.ViewUtils;

import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.ViewHolderType;
import gsshop.mobile.v2.home.shop.flexible.FlexibleRecommendAdapter;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.SwipeUtils;
import gsshop.mobile.v2.web.WebUtils;

/**
 * 기획전 - 모바일 전용 기획전.
 */
@SuppressLint("NewApi")
public class TpSVH extends BaseViewHolder {
    private static final int DIVIDER_SPACE_01 = MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.vh_blank_space_middle);
    private static final int DIVIDER_SPACE_02 = MainApplication.getAppContext().getResources().getDimensionPixelSize(R.dimen.vh_blank_space_large);
    private final View titleView;

    private final TextView mainTitleText;
    private final TextView subTitleText;

    private final ImageView arrowImage;

    private final RecyclerView recyclerView;

    /**
     * @param itemView itemView
     */
    public TpSVH(View itemView) {
        super(itemView);

        //스와이프 허용각도 확대 적용
        increaseSwipeAngle = true;

        titleView = itemView.findViewById(R.id.view_title);

        mainTitleText = (TextView) itemView.findViewById(R.id.text_title_main);
        subTitleText = (TextView) itemView.findViewById(R.id.text_title_sub);
        arrowImage = (ImageView) itemView.findViewById(R.id.image_arrow);
        recyclerView = (RecyclerView) itemView.findViewById(R.id.recycler);

        DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), recyclerView);

        /**
         * 아이템간격을 데코레이션 한다.
         *
         */
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                final int position = parent.getChildAdapterPosition(view);
                if(position == 0) {
                    outRect.left = DIVIDER_SPACE_01;
                }

                if(position == parent.getAdapter().getItemCount() - 1) {
                    outRect.right = DIVIDER_SPACE_01;
                } else {
                    outRect.right = DIVIDER_SPACE_02;
                }
            }
        });

        /**
         * resize recyclerview height
         */
        recyclerView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                    @SuppressWarnings("deprecation")
                    @Override
                    public void onGlobalLayout() {

                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            recyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }


                        if(recyclerView.getChildCount() > 0) {
                            View child = recyclerView.getChildAt(0).findViewById(R.id.root);
                            int height = child.getHeight();
                            ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
                            params.height = height;
                            recyclerView.setLayoutParams(params);
                            recyclerView.requestLayout();
                        }
                    }
                });
    }

    /*
     * bind
     */
    @Override
    public void onBindViewHolder(final Context context, int position, ShopInfo info, final String action, final String label, String sectionName) {

        final SectionContentList item = info.contents.get(position).sectionContent;

        titleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // GTM AB Test 클릭이벤트 전달
                WebUtils.goWeb(context, item.linkUrl);
            }
        });

        mainTitleText.setText(item.productName);
        if(subTitleText != null) {
            subTitleText.setText(item.promotionName);
        }

        //플렉서블 E 모바일 전용 기획전만 화살표 숨김 대상임 (모바일 전용 기획전은 대상 아님)
        if (getItemViewType() == ViewHolderType.VIEW_TYPE_TP_SA
                && TextUtils.isEmpty(item.linkUrl)) {
            ViewUtils.hideViews(arrowImage);
        } else {
            ViewUtils.showViews(arrowImage);
        }

        // 영역내 노출가능한 상품이 0개 인 경우 상품롤링 영역 전체 비노출.
        if(item.subProductList.size() <= 0) {
            ViewUtils.hideViews(recyclerView);
            return;
        }

        ViewUtils.showViews(recyclerView);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);

        recyclerView.setLayoutManager(llm);
        FlexibleRecommendAdapter adapter = new FlexibleRecommendAdapter(context, item.subProductList, action, label);
        recyclerView.setAdapter(adapter);

        recyclerView.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View v, MotionEvent event) {


                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        SwipeUtils.INSTANCE.disableSwipe();
                        break;
                }
                return false;
            }
        });




    }


}
