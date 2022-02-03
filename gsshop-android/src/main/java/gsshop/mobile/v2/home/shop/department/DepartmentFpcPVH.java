/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.department;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.Typeface;
import androidx.fragment.app.FragmentActivity;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.gsshop.mocha.ui.util.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.bestshop.BestShopFilterDialogFragment;
import roboguice.util.Ln;

/**
 *
 * DepartmentFpcPVH 신규 생성
 *
 */
public class DepartmentFpcPVH extends BaseViewHolder {

    private static int COLUMN_COUNT = 3;
    private static int[] COLUMN_RES_IDS = {R.id.content_frame_1st, R.id.content_frame_2nd, R.id.content_frame_3rd};
    private static final Typeface TYPE_SELECTED = Typeface.create("sans-serif", Typeface.BOLD);
    private static final Typeface TYPE_UNSELECTED = Typeface.SANS_SERIF;

    //dp 가 아닌 다른 값으로 바뀐다면 사용하는곳에서 바꾼다.
    private static final float TEXT_SIZE_SELECTED = 15;
    private static final float TEXT_SIZE_UNSELECTED = 14;


    private final TableLayout categoryTable;
    private final TextView filterText;
    private final View filterView;

    private final int TOUCH_SLOP;

    protected static int currentIndex = 0;

    private List<SectionContentList> mCategories;

    /**
     * @param itemView itemView
     */
    public DepartmentFpcPVH(View itemView) {
        super(itemView);
        categoryTable = (TableLayout) itemView.findViewById(R.id.table_category);
        filterText = (TextView) itemView.findViewById(R.id.text_filter);
        filterView = itemView.findViewById(R.id.view_filter);

        TOUCH_SLOP = ViewConfiguration.get(MainApplication.getAppContext()).getScaledTouchSlop();

        try {
            EventBus.getDefault().register(this);
        }catch (Exception e){}
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
        final ArrayList<SectionContentList> categories = content.subProductList;

        if (categories == null || categories.isEmpty()) {
            return;
        }

        initCatagory(context, categories, content.promotionName);

        // selection
        categoryTable.setOnTouchListener(
                new View.OnTouchListener()

                {
                    private int downX = -1;
                    private int downY = -1;

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                downX = (int) event.getX();
                                downY = (int) event.getY();
                                return true;
                            case MotionEvent.ACTION_UP:
                                int x = (int) event.getX();
                                int y = (int) event.getY();
                                if (Math.abs(x - downX) > TOUCH_SLOP || Math.abs(y - downY) > TOUCH_SLOP) {
                                    return false;
                                }

                                for (int i = 0; i < categoryTable.getChildCount(); i++) {
                                    TableRow row = (TableRow) categoryTable.getChildAt(i);
                                    for (int j = 0; j < row.getChildCount(); j++) {
                                        int tab = (i * COLUMN_COUNT) + j;
                                        if (tab >= categories.size()) {
                                            return false;
                                        }
                                        ViewGroup column = (ViewGroup) row.getChildAt(j);
                                        int left = row.getLeft() + column.getLeft();
                                        int top = row.getTop() + column.getTop();
                                        int width = column.getWidth();
                                        int height = column.getHeight();
                                        Rect rect = new Rect(left, top, left + width, top + height);

//                                        Ln.i("left : " + left + ", top : " + top + ", width : " + width + ", height : " + height);

                                        TextView titleText = (TextView) column.findViewById(R.id.text_title);
                                        if (rect.contains(x, y)) {
                                            currentIndex = tab;
                                            info.tabIndex = tab;
                                            titleText.setSelected(true);
                                            titleText.setTypeface(TYPE_SELECTED);
                                            //titleText.setTextSize(TEXT_SIZE_SELECTED);
                                            titleText.setEllipsize(TextUtils.TruncateAt.END);
                                            titleText.setMaxLines(1);
                                            titleText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_SELECTED);

                                            filterText.setSelected(false);
                                            EventBus.getDefault().post(new Events.FlexibleEvent.UpdateBestShopEvent(tab, categories.get(tab).linkUrl, categories.get(tab).promotionName));
                                            //와이즈로그 호출
                                            ((HomeActivity) context).setWiseLog(categories.get(tab).wiseLog);
                                        } else {
                                            titleText.setSelected(false);
                                            titleText.setTypeface(TYPE_UNSELECTED);
                                            //titleText.setTextSize(TEXT_SIZE_UNSELECTED);
                                            titleText.setEllipsize(TextUtils.TruncateAt.END);
                                            titleText.setMaxLines(1);
                                            titleText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_UNSELECTED);
                                        }
                                    }

                                }
                                return true;
                            default:
                                break;
                        }

                        return false;
                    }
                }

        );

        setCategory(content.index);
//        setCategory(currentIndex);
    }

    protected void initCatagory(final Context context, final List<SectionContentList> categories, final String selectedName) {
        if (selectedName.equals("전체")) {
            currentIndex = 0;
        }
        mCategories = categories;
        if (categories.get(currentIndex).subProductList.size() > 0) {
            ViewUtils.showViews(filterView);


            filterText.setText(selectedName);
            filterText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BestShopFilterDialogFragment cateDialog = BestShopFilterDialogFragment.newInstance(categories.get(currentIndex).subProductList, selectedName);
                    cateDialog.show(((FragmentActivity) context).getSupportFragmentManager(), BestShopFilterDialogFragment.class.getSimpleName());
                }
            });
        } else {
            ViewUtils.hideViews(filterView);
        }

        Ln.i("currentIndex : " + currentIndex);
        Ln.i("currentIndex : " + selectedName);


        if (categoryTable.getChildCount() > 0) {
            categoryTable.removeAllViews();
        }
        int rowCount = (categories.size() / COLUMN_COUNT) + (categories.size() % COLUMN_COUNT > 0 ? 1 : 0);
        // row
        for (int i = 0; i < rowCount; i++) {
            TableRow row = (TableRow) LayoutInflater.from(context).inflate(R.layout.home_row_type_fx_best_shop_category_row_new, null);
            categoryTable.addView(row);

            // column
            for (int j = 0; j < COLUMN_COUNT; j++) {
                View view = row.findViewById(COLUMN_RES_IDS[j]);
                int k = (i * COLUMN_COUNT) + j;
                // first position
                if (k >= categories.size()) {
                    view.setBackgroundResource(android.R.color.transparent);
                } else {
                    TextView titleText = (TextView) view.findViewById(R.id.text_title);
                    titleText.setText(categories.get(k).productName);
                    // selection
                    if (currentIndex == k) {
                        titleText.setSelected(true);
                        titleText.setTypeface(TYPE_SELECTED);
                        //titleText.setTextSize(TEXT_SIZE_SELECTED);
                        titleText.setEllipsize(TextUtils.TruncateAt.END);
                        titleText.setMaxLines(1);
                        titleText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_SELECTED);
                    }
                    else{
                        titleText.setTypeface(TYPE_UNSELECTED);
                        //titleText.setTextSize(TEXT_SIZE_UNSELECTED);
                        titleText.setEllipsize(TextUtils.TruncateAt.END);
                        titleText.setMaxLines(1);
                        titleText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_UNSELECTED);
                    }

                }
            }

        }
    }

    public void setCategory(int index){
        for (int i = 0; i < categoryTable.getChildCount(); i++) {
            TableRow row = (TableRow) categoryTable.getChildAt(i);
            for (int j = 0; j < row.getChildCount(); j++) {
                int tab = (i * COLUMN_COUNT) + j;
                ViewGroup column = (ViewGroup) row.getChildAt(j);
                int left = row.getLeft() + column.getLeft();
                int top = row.getTop() + column.getTop();
                int width = column.getWidth();
                int height = column.getHeight();
                Rect rect = new Rect(left, top, left + width, top + height);

//                                        Ln.i("left : " + left + ", top : " + top + ", width : " + width + ", height : " + height);

                TextView titleText = (TextView) column.findViewById(R.id.text_title);

                if(tab == index){
                    currentIndex = tab;
                    titleText.setSelected(true);
                    titleText.setTypeface(TYPE_SELECTED);
                    //titleText.setTextSize(TEXT_SIZE_SELECTED);
                    titleText.setEllipsize(TextUtils.TruncateAt.END);
                    titleText.setMaxLines(1);
                    titleText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_SELECTED);
                }else{
                    titleText.setSelected(false);
                    titleText.setTypeface(TYPE_UNSELECTED);
                    //titleText.setTextSize(TEXT_SIZE_UNSELECTED);
                    titleText.setEllipsize(TextUtils.TruncateAt.END);
                    titleText.setMaxLines(1);
                    titleText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_UNSELECTED);
                }

            }

        }
    }


//    public void onEventMainThread(Events.FlexibleEvent.UpdateBestShopEvent event) {
//        if (getUserVisibleHint()) {
//            setCategory(event.tab);
//        }
//    }

}
