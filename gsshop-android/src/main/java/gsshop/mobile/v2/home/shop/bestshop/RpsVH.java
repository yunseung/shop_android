/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.bestshop;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import com.greenfrvr.hashtagview.HashtagView;

import java.util.ArrayList;
import java.util.List;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.util.Ln;


/**
 *
 *
 */
public class RpsVH extends BaseViewHolder {

    private final TextView titleText;
    private final HashtagView hashtagView;

    private static final int[] colors = {
            Color.parseColor("#f8abce"),
            Color.parseColor("#e1afd6"),
            Color.parseColor("#cbb2dc"),
            Color.parseColor("#acbae6"),
            Color.parseColor("#9ebfec"),
            Color.parseColor("#8cc4ee"),
            Color.parseColor("#82c7e5"),
            Color.parseColor("#82d7e5"),
            Color.parseColor("#7ecbc7"),
            Color.parseColor("#7ecfb4"),
            Color.parseColor("#84cfa2"),
            Color.parseColor("#95cc9d"),
            Color.parseColor("#a9ca91"),
            Color.parseColor("#bbd08b"),
            Color.parseColor("#cdd488"),
            Color.parseColor("#ddce84"),
            Color.parseColor("#eec183"),
            Color.parseColor("#fab583"),
            Color.parseColor("#faae91"),
            Color.parseColor("#faa8a4"),};

    /**
     * @param itemView itemView
     */
    public RpsVH(View itemView) {
        super(itemView);

        titleText = (TextView) itemView.findViewById(R.id.text_popular_title);
        hashtagView = (HashtagView) itemView.findViewById(R.id.hashtags);
        hashtagView.setTypeface(Typeface.SANS_SERIF);
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

        SectionContentList content = info.contents.get(position).sectionContent;
        final ArrayList<SectionContentList> tags = content.subProductList;


        if (content.productName.length() > 0) {
            titleText.setText(content.productName);
        } else {
            titleText.setText(R.string.best_shop_populate_keyword);
        }

        if (tags.isEmpty()) {
            return;
        }

        final List<String> keywords = new ArrayList<>();
        for (SectionContentList tag : tags) {
            keywords.add(tag.productName);
        }

        // keyword list
        hashtagView.setData(keywords, new HashtagView.DataTransform<String>() {

            @Override
            public CharSequence prepare(String s, View view) {
                int i = keywords.indexOf(s);
                if (i >= 0) {
                    i %= colors.length;
                    Ln.i("i: " + i);
                    view.setBackgroundColor(colors[i]);
                }

                return "#" + s;
            }
        });

        // link click
        hashtagView.removeListeners();
        hashtagView.addOnTagClickListener(new HashtagView.TagsClickListener() {
            @Override
            public void onItemClicked(Object o) {
                if (tags != null && tags.isEmpty()) {
                    return;
                }

                for (SectionContentList tag : tags) {
                    if (tag.productName.equals(o)) {
                        Ln.i("url : " + tag.linkUrl);
                        WebUtils.goWeb(context, tag.linkUrl);
                        break;
                    }

                }
            }
        });
    }

}
