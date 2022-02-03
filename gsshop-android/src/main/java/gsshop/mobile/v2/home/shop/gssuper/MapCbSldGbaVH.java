/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.gssuper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.text.Editable;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gsshop.mocha.ui.util.ViewUtils;

import java.util.List;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.ShopInfo.ShopItem;
import gsshop.mobile.v2.support.ui.ClearButtonTextWatcher;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog;
import gsshop.mobile.v2.support.ui.RecyclerItemClickListener;
import gsshop.mobile.v2.util.SwipeUtils;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;
import static com.blankj.utilcode.util.KeyboardUtils.hideSoftInput;
import static com.blankj.utilcode.util.KeyboardUtils.showSoftInput;
import static gsshop.mobile.v2.search.SearchNavigation.encodingQuery;
import static gsshop.mobile.v2.util.StringUtils.trim;

/**
 * gs fresh 카테고리.
 */
public class MapCbSldGbaVH extends BaseViewHolder {

    /**
     * 상품 카테고리
     */
    private final RecyclerView recycler;
    /**
     * 매장 검색.
     */
    private final View searchView;
    private final Button searchButton;
    private final EditText keywordEdit;
    private final ImageButton clearButton;

    /**
     * @param itemView
     */
    public MapCbSldGbaVH(View itemView) {
        super(itemView);

        //스와이프 허용각도 확대 적용
        increaseSwipeAngle = true;

        recycler = itemView.findViewById(R.id.recycler_gs_fresh_category);

        searchView = itemView.findViewById(R.id.view_gs_supper_search);
        searchButton = itemView.findViewById(R.id.btn_gs_fresh_search);
        keywordEdit = itemView.findViewById(R.id.edit_gs_fresh_search_keyword);
        clearButton = itemView.findViewById(R.id.btn_gs_fresh_clear_keyword);

        recycler.setLayoutManager(new GridLayoutManager(itemView.getContext(), 2, GridLayoutManager.HORIZONTAL, false));
        /**
         * 리스트가 맨 왼쪽이나 오른쪽에 오면 매장이 swipe 비활성화.
         */
        recycler.setOnTouchListener(new View.OnTouchListener() {
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

        // keyword clear
        ViewUtils.hideViews(clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keywordEdit.getText().clear();
            }
        });

        //텍스트가 추가되거나 삭제될때 동작된다.
        keywordEdit.addTextChangedListener(new ClearButtonTextWatcher(clearButton) {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);

                if (s.length() > 0) {
                    // 키워드 폰트
                    keywordEdit.setTypeface(null, Typeface.BOLD);
                    keywordEdit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                } else {
                    // hint 폰트
                    keywordEdit.setTypeface(Typeface.DEFAULT);
                    keywordEdit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);

                }
            }
        });

    }

    /**
     * 검색창의 텍스트를 지움.
     */
    @Override
    public void onViewAttachedToWindow() {
        super.onViewAttachedToWindow();
            EventBus.getDefault().register(this);
    }

    @Override
    public void onViewDetachedFromWindow() {
        super.onViewDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(Events.FlexibleEvent.GSFreshClearKeywordEvent event) {
        keywordEdit.getText().clear();
        keywordEdit.clearFocus();
        hideSoftInput(event.activity);

    }

    @Override
    public void onBindViewHolder(final Context context, int position, ShopInfo info,
                                 final String action, final String label, String sectionName) {
        super.onBindViewHolder(context, position, info, action, label, sectionName);


        final ShopItem content = info.contents.get(position);

        if (content.sectionContent.subProductList == null) {
            return;
        }

        // 카테고리 리스트
        recycler.setAdapter(new Adapter(context, content.sectionContent.subProductList));

        /**
         * 매장 내 검색.
         */
        keywordEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // 키패드에서 검색키(돋보기모양)를 누른 경우 검색 처리
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchKeyword(context, content.sectionContent.linkUrl);
                    return true;
                }
                return false;
            }
        });

        recycler.addOnItemTouchListener(new RecyclerItemClickListener(context, (view, position1) -> {
            WebUtils.goWeb(context, Uri.parse(content.sectionContent.subProductList.get(position1).linkUrl).
                    buildUpon().appendQueryParameter("_", String.valueOf(System.currentTimeMillis())).build().toString());
        }));

        //mslee 후레쉬 돋보기 버튼 기능 제거
        //searchButton.setOnClickListener(new View.OnClickListener() {
        //   @Override
        //    public void onClick(View v) {
        //        searchKeyword(context, content.sectionContent.linkUrl);
        //    }
        //});

    }

    /**
     * 키워드 검색
     */
    private void searchKeyword(Context context, String linkUrl) {
        hideSoftInput((Activity) context);
        String keyword = keywordEdit.getText().toString().trim();
        if (isNotEmpty(keyword)) {
            //검색키워드 초기화
            keywordEdit.getText().clear();
            keywordEdit.clearFocus();

            // 시작부분의 %, / 특수문자 제거
            String searchKey = keyword.replaceFirst("^[/%]+", "");
            WebUtils.goWeb(context, linkUrl + encodingQuery(searchKey));
        } else {
            Dialog dialog = new CustomOneButtonDialog((Activity) context)
                    .message(R.string.search_description_search_main).buttonClick(new CustomOneButtonDialog.ButtonClickListener() {
                        @Override
                        public void onClick(Dialog dialog) {
                            dialog.dismiss();
                            keywordEdit.requestFocus();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    showSoftInput((Activity) context);
                                }
                            }, 100);

                        }
                    });
            dialog.show();
        }
    }

    private static class Adapter extends RecyclerView.Adapter<ItemViewHolder> {

        public final Context context;
        public final List<SectionContentList> list;

        private Adapter(Context context, List<SectionContentList> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View viewItem = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_item_gs_fresh_category, parent, false);
            return new ItemViewHolder(viewItem);
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {
            try {
                Glide.with(context).load(trim(list.get(position).imageUrl)).diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.brand_no_android).into(holder.itemImage);
            } catch (Exception er) {
                Ln.e(er.getMessage());
            }

            //test 용
            holder.itemText.setText(list.get(position).productName);
        }

        @Override
        public int getItemCount() {
            return isNotEmpty(list) ? list.size() : 0;
        }
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {

        private final ImageView itemImage;
        private final TextView itemText;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.image_gs_fresh_category_item);
            itemText = itemView.findViewById(R.id.text_gs_fresh_category_item);

        }
    }

}
