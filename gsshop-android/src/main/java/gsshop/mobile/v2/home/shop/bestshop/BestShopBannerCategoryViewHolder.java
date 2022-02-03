/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.bestshop;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.util.DisplayUtils;
import roboguice.util.Ln;

/**
 *
 *
 */
public class BestShopBannerCategoryViewHolder extends BaseViewHolder {

    private static int COLUMN_COUNT = 3;
    private static int[] COLUMN_RES_IDS = {R.id.content_frame_1st, R.id.content_frame_2nd, R.id.content_frame_3rd};
    private static final Typeface TYPE_SELECTED = Typeface.create("sans-serif", Typeface.BOLD);
    private static final Typeface TYPE_UNSELECTED = Typeface.SANS_SERIF;
    private static final float TEXT_SIZE_SELECTED = 15;
    private static final float TEXT_SIZE_UNSELECTED = 14;
    protected final TableLayout categoryTable;
    protected final ImageView selectedView;
    protected final View blank;


    private final int BOARDER_SPACE;
    private final int TOUCH_SLOP;
    private final int TEXT_COLOR_SELECTED;
    private final int TEXT_COLOR_UNSELECTED;
    private final int BOARDER_COLOR_SELECTED;

    protected int currentIndex = 0;

    private ArrayList<SectionContentList> mCategories;

    /**
     * @param itemView itemView
     */
    public BestShopBannerCategoryViewHolder(View itemView) {
        super(itemView);
        categoryTable = (TableLayout) itemView.findViewById(R.id.table_category);
        selectedView = (ImageView) itemView.findViewById(R.id.view_selected);
        blank = itemView.findViewById(R.id.blank);

        TOUCH_SLOP = ViewConfiguration.get(MainApplication.getAppContext()).getScaledTouchSlop();
        BOARDER_SPACE = DisplayUtils.convertDpToPx(MainApplication.getAppContext(), 1);

        TEXT_COLOR_SELECTED = MainApplication.getAppContext().getResources().getColor(R.color.best_shop_category_item_text_selected);
        TEXT_COLOR_UNSELECTED = MainApplication.getAppContext().getResources().getColor(R.color.best_shop_category_item_text_unselected);
        BOARDER_COLOR_SELECTED = MainApplication.getAppContext().getResources().getColor(R.color.best_shop_category_item_border_selected);
    }


    /**
     * @param context context
     * @param position position
     * @param info info
     * @param action action
     * @param label label
     * @param sectionName sectionName
     */
    @Override
    public void onBindViewHolder(final Context context, final int position, final ShopInfo info,
                                 final String action, final String label, String sectionName) {

        final SectionContentList content = info.contents.get(position).sectionContent;
        mCategories = content.subProductList;

        if (mCategories == null || mCategories.isEmpty()) {
            return;
        }

        blank.setVisibility(View.GONE);
        if(position > 0){
            SectionContentList prevContent = info.contents.get(position-1).sectionContent;
            if("B_TS".equalsIgnoreCase(prevContent.viewType) || "VIEW_TYPE_B_TS2".equalsIgnoreCase(prevContent.viewType) || "B_TSC".equalsIgnoreCase(prevContent.viewType)){
                blank.setVisibility(View.VISIBLE);
            }
        }

        initCategory(context, mCategories, content.productName);

        selectedView.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {

                    @Override
                    public boolean onPreDraw() {
                        if (selectedView.getWidth() > 0) {
                            selectedView.getViewTreeObserver().removeOnPreDrawListener(this);
                            int rowPos = currentIndex / COLUMN_COUNT;
                            int columnPos = currentIndex % COLUMN_COUNT;

                            if (rowPos >= categoryTable.getChildCount()) {
                                return true;
                            }
                            TableRow row = (TableRow) categoryTable.getChildAt(rowPos);

                            if (columnPos >= row.getChildCount()) {
                                return true;
                            }

                            ViewGroup column = (ViewGroup) row.getChildAt(columnPos);


                            int left = row.getLeft() + column.getLeft();
                            int top = row.getTop() + column.getTop();
                            int right = left + column.getWidth();
                            int bottom = top + column.getHeight();

                            TextView titleText = (TextView) column.findViewById(R.id.text_title);
                            titleText.setTextColor(TEXT_COLOR_SELECTED);
                            titleText.setTypeface(TYPE_SELECTED);

                            drawSelectedRect(context, left, top, right, bottom);
                            return true;
                        }


                        return true;
                    }


                });
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
                                        if (tab >= mCategories.size()) {
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
                                            titleText.setTextColor(TEXT_COLOR_SELECTED);
                                            titleText.setTypeface(TYPE_SELECTED);
                                            EventBus.getDefault().post(new Events.FlexibleEvent.UpdateBestShopEvent(tab, mCategories.get(tab).linkUrl, mCategories.get(tab).promotionName));
                                            //와이즈로그 호출
                                            ((HomeActivity) context).setWiseLog(mCategories.get(tab).wiseLog);
                                            drawSelectedRect(context, left, top, left + width, top + height);
                                        } else {
                                            titleText.setTextColor(TEXT_COLOR_UNSELECTED);
                                            titleText.setTypeface(TYPE_UNSELECTED);
                                            //titleText.setTextSize(TEXT_SIZE_UNSELECTED);
                                            titleText.setMaxLines(1);
                                            titleText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_UNSELECTED);
                                        }
                                    }

                                }
                                return true;
                        }

                        return false;
                    }
                }

        );

    }

    public void setCategory(Context context, int index){
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
                    titleText.setTextColor(TEXT_COLOR_SELECTED);
                    titleText.setTypeface(TYPE_SELECTED);
                    titleText.setMaxLines(1);
                    titleText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_SELECTED);
                    titleText.setSelected(true);
                }else{
                    titleText.setTextColor(TEXT_COLOR_UNSELECTED);
                    titleText.setTypeface(TYPE_UNSELECTED);
                    //titleText.setTextSize(TEXT_SIZE_UNSELECTED);
                    titleText.setMaxLines(1);
                    titleText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_UNSELECTED);
                    titleText.setSelected(false);
                }

            }

        }
    }

    protected void initCategory(Context context, ArrayList<SectionContentList> categories, String selectedName) {
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
                }
//                else {
//                    ((TextView) view.findViewById(R.id.text_title)).setText(categories.get(k).productName);
//                    // selection
//                    if (selectedName.equals(categories.get(k).productName)) {
//                        currentIndex = k;
//                    }
//                }
                else {
                    TextView titleText = (TextView) view.findViewById(R.id.text_title);
                    titleText.setText(categories.get(k).productName);
                    // selection
                    if (currentIndex == k) {
                        titleText.setSelected(true);
                        titleText.setTypeface(TYPE_SELECTED);
                        //titleText.setTextSize(TEXT_SIZE_SELECTED);
                        titleText.setMaxLines(1);
                        titleText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_SELECTED);
                    }
                    else
                    {
                        titleText.setTypeface(TYPE_UNSELECTED);
                        titleText.setMaxLines(1);
                        titleText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_UNSELECTED);
                    }
                }
            }

        }
    }

    protected void drawSelectedRect(Context context, int left, int top, int right, int bottom) {

//        // Initialize a new Bitmap object
//        Bitmap bitmap = Bitmap.createBitmap(
//                categoryTable.getWidth(), // Width
//                categoryTable.getHeight(), // Height
//                Bitmap.Config.ARGB_8888 // Config
//        );
//
//        // Initialize a new Canvas instance
//        Canvas canvas = new Canvas(bitmap);
//
//        // Draw a solid color to the canvas background
//        canvas.drawColor(Color.TRANSPARENT);
//
//        // Initialize a new Paint instance to draw the Rectangle
//        Paint paint = new Paint();
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeWidth(BOARDER_SPACE);
//        paint.setColor(BOARDER_COLOR_SELECTED);
//        paint.setAntiAlias(true);
//
//        // Initialize a new Rect object
//        Rect rectangle = new Rect(
//                left, // Left
//                top, // Top
//                right, // Right
//                bottom // Bottom
//        );
//
//
//        // Finally, draw the rectangle on the canvas
//        canvas.drawRect(rectangle, paint);
//
//        // Display the newly created bitmap on app interface
//        selectedView.setImageBitmap(bitmap);
    }

}
