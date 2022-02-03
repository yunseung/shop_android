/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.nalbang;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.gsshop.mocha.ui.util.ViewUtils;

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

/**
 *
 *
 */
public class ShortbangBannerCategoryTabViewHolder extends BaseViewHolder {
    private static final int[] FRAME_RES_IDS = {R.id.frame_1st, R.id.frame_2st, R.id.frame_3st, R.id.frame_4st, R.id.frame_5st, R.id.frame_6st};
    private static final int[] COLUMN_RES_IDS = {R.id.text_title_1st, R.id.text_title_2nd, R.id.text_title_3rd, R.id.text_title_4th, R.id.text_title_5th, R.id.text_title_6th};
    private static final Typeface TYPE_SELECTED = Typeface.create("sans-serif", Typeface.BOLD);
    private static final Typeface TYPE_UNSELECTED = Typeface.SANS_SERIF;

    private final TableLayout hashtagTable;
    private final ImageView selectedView;

    private final View tagCountView;
    private final TextView tagText;
    private final TextView countText;

    protected final int BOARDER_SPACE;
    protected final int TOUCH_SLOP;
    protected final int TEXT_COLOR_SELECTED;
    protected final int TEXT_COLOR_UNSELECTED;
    protected int BOARDER_COLOR_SELECTED;


    /**
     * @param itemView
     */
    public ShortbangBannerCategoryTabViewHolder(View itemView) {
        super(itemView);
        hashtagTable = (TableLayout) itemView.findViewById(R.id.table_hashtag);
        selectedView = (ImageView) itemView.findViewById(R.id.view_selected);

        tagCountView = itemView.findViewById(R.id.view_hashtag_count);
        tagText = (TextView) itemView.findViewById(R.id.text_hashtag);
        countText = (TextView) itemView.findViewById(R.id.text_count);

        TOUCH_SLOP = ViewConfiguration.get(MainApplication.getAppContext()).getScaledTouchSlop();
        BOARDER_SPACE = DisplayUtils.convertDpToPx(MainApplication.getAppContext(), 1);

        TEXT_COLOR_SELECTED = MainApplication.getAppContext().getResources().getColor(R.color.best_shop_category_item_text_selected);
        TEXT_COLOR_UNSELECTED = MainApplication.getAppContext().getResources().getColor(R.color.best_shop_category_item_text_unselected);
        BOARDER_COLOR_SELECTED = MainApplication.getAppContext().getResources().getColor(R.color.shortbang_shop_tab_item_border_selected);
    }


    /**
     * @param context
     * @param position
     * @param info
     * @param action
     * @param label
     * @param sectionName
     */
    @Override
    public void onBindViewHolder(final Context context, final int position, final ShopInfo info,
                                 final String action, final String label, String sectionName) {

        final SectionContentList content = info.contents.get(position).sectionContent;
        final ArrayList<SectionContentList> categories = content.subProductList;

        if(info.shortbangTabIndex ==-1) {
            tagText.setText("#" + info.hashTagName);
            countText.setText(""+info.hashTagCount);
            ViewUtils.showViews(tagCountView);
        } else {
            ViewUtils.hideViews(tagCountView);
        }
        if (categories == null || categories.isEmpty()) {
            return;
        }

        initHashtagTable(context, categories);

        selectedView.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {

                    @Override
                    public boolean onPreDraw() {
                        if (selectedView.getWidth() > 0) {
                            selectedView.getViewTreeObserver().removeOnPreDrawListener(this);
                            int rowPos = 0;
                            int columnPos = 0;
                            if (info.shortbangTabIndex >= 0) {
                                rowPos = info.shortbangTabIndex / COLUMN_RES_IDS.length;
                                columnPos = info.shortbangTabIndex % COLUMN_RES_IDS.length;
                            }

                            if (rowPos >= hashtagTable.getChildCount()) {
                                return true;
                            }
                            TableRow row = (TableRow) hashtagTable.getChildAt(rowPos);

                            if (columnPos >= row.getChildCount()) {
                                return true;
                            }

                            View column = row.getChildAt(columnPos);


                            int left = row.getLeft() + column.getLeft();
                            int top = row.getTop() + column.getTop();
                            int right = left + column.getWidth();
                            int bottom = top + column.getHeight();

                            TextView text = (TextView) column.findViewById(COLUMN_RES_IDS[columnPos]);
                            text.setTextColor(TEXT_COLOR_SELECTED);
                            text.setTypeface(TYPE_SELECTED);

                            drawSelectedRect(context, left, top, right, bottom);
                            return true;
                        }


                        return true;
                    }


                });
        // selection
        hashtagTable.setOnTouchListener(
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

                                for (int i = 0; i < hashtagTable.getChildCount(); i++) {
                                    TableRow row = (TableRow) hashtagTable.getChildAt(i);
                                    for (int j = 0; j < row.getChildCount(); j++) {
                                        int tab = (i * COLUMN_RES_IDS.length) + j;
                                        if (tab >= categories.size()) {
                                            return false;
                                        }
                                        View column = row.getChildAt(j);
                                        int left = row.getLeft() + column.getLeft();
                                        int top = row.getTop() + column.getTop();
                                        int width = column.getWidth();
                                        int height = column.getHeight();
                                        Rect rect = new Rect(left, top, left + width, top + height);

                                        TextView text = (TextView) column.findViewById(COLUMN_RES_IDS[j]);
                                        if (rect.contains(x, y)) {
                                            // 동일 tab이 선택되면 무시.
                                            if(tab !=info.shortbangTabIndex) {
                                                EventBus.getDefault().post(new Events.FlexibleEvent.ShortbangTabUpdate(tab));
                                                //와이즈로그 호출
                                                ((HomeActivity) context).setWiseLog(categories.get(tab).wiseLog);
                                            }
                                        } else {
                                            text.setTextColor(TEXT_COLOR_UNSELECTED);
                                            text.setTypeface(TYPE_UNSELECTED);
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

    protected void initHashtagTable(Context context, ArrayList<SectionContentList> categories) {
        if (hashtagTable.getChildCount() > 0) {
            hashtagTable.removeAllViews();
        }
        int rowCount = (categories.size() / COLUMN_RES_IDS.length) + (categories.size() % COLUMN_RES_IDS.length > 0 ? 1 : 0);
        // row
        for (int i = 0; i < rowCount; i++) {
            TableRow row = (TableRow) LayoutInflater.from(context).inflate(R.layout.home_row_type_fx_nalbang_shop_hashtag_table_row, null);
            hashtagTable.addView(row);

            // column
            for (int j = 0; j < COLUMN_RES_IDS.length; j++) {
                FrameLayout layout = (FrameLayout) row.findViewById(FRAME_RES_IDS[j]);
                TextView TextView = (TextView) row.findViewById(COLUMN_RES_IDS[j]);
                int k = (i * COLUMN_RES_IDS.length) + j;
                // first position
                if (k >= categories.size()) {
                    TextView.setBackgroundResource(android.R.color.transparent);
                    layout.setVisibility(View.GONE);
                } else {
                    TextView.setText(categories.get(k).productName);
                }
            }

        }
    }

    protected void drawSelectedRect(Context context, int left, int top, int right, int bottom) {

        // Initialize a new Bitmap object
        Bitmap bitmap = Bitmap.createBitmap(
                hashtagTable.getWidth(), // Width
                hashtagTable.getHeight(), // Height
                Bitmap.Config.ARGB_8888 // Config
        );

        // Initialize a new Canvas instance
        Canvas canvas = new Canvas(bitmap);

        // Draw a solid color to the canvas background
        canvas.drawColor(Color.TRANSPARENT);

        // Initialize a new Paint instance to draw the Rectangle
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(BOARDER_SPACE);
        paint.setColor(BOARDER_COLOR_SELECTED);
        paint.setAntiAlias(true);

        // Initialize a new Rect object
        Rect rectangle = new Rect(
                left, // Left
                top, // Top
                right, // Right
                bottom // Bottom
        );


        // Finally, draw the rectangle on the canvas
        canvas.drawRect(rectangle, paint);

        // Display the newly created bitmap on app interface
        selectedView.setImageBitmap(bitmap);
    }

}