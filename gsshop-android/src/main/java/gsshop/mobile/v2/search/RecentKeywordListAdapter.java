/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gsshop.mobile.v2.R;
import roboguice.util.Ln;

import static gsshop.mobile.v2.search.SearchAction.SEARCH_SPLIT_SEPERATOR;

/**
 * 최근 검색어 목록 어댑터.
 *
 */
public class RecentKeywordListAdapter extends BaseAdapter {

    private final LayoutInflater inflater;

    private final ArrayList<RecentKeywordPair> keywordPairs = new ArrayList<RecentKeywordPair>();

    private final OnClickListener keywordClickListener;

    public RecentKeywordListAdapter(Context context, OnClickListener keywordClickListener) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.keywordClickListener = keywordClickListener;
    }

    public void setItems(List<RecentKeyword> keywords) {
        keywordPairs.clear();

        if (keywords == null) {
            return;
        }

        int size = keywords.size();
        for (int i = 0; i < size; i++) {
            RecentKeyword keywordLeft = null;
            // 두줄로 했다가 한줄로 재 변경. 20190222 fix by hklim
//            RecentKeyword keywordRight = null;
            try {
                keywordLeft = keywords.get(i);
                keywordLeft = new RecentKeyword(keywordLeft.keyword.split(SEARCH_SPLIT_SEPERATOR)[0], keywordLeft.type);
//                if (i < size)
//                    keywordRight = keywords.get(i++);
            }
            catch (IndexOutOfBoundsException e) {
                Ln.e(e.getMessage());
            }
            catch (NullPointerException e) {
                Ln.e(e.getMessage());
            }
//            RecentKeywordPair pair = new RecentKeywordPair(keywordLeft, keywordRight);
            RecentKeywordPair pair = new RecentKeywordPair(keywordLeft);
            keywordPairs.add(pair);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder viewHolder;

        if (v == null) {
            v = inflater.inflate(R.layout.recent_keyword_row, null);
            viewHolder = new ViewHolder();
            viewHolder.txtKeywordLeft = (TextView) v.findViewById(R.id.txt_keyword_left);
            viewHolder.btnDelLeft = (ImageButton) v.findViewById(R.id.btn_del_left);
            viewHolder.txtKeywordRight = (TextView) v.findViewById(R.id.txt_keyword_right);
            viewHolder.btnDelRight = (ImageButton) v.findViewById(R.id.btn_del_right);
            viewHolder.viewRight = v.findViewById(R.id.layout_right);
            viewHolder.divider =  v.findViewById(R.id.divider);
            v.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) v.getTag();
        }

        if (viewHolder.viewRight != null)
            viewHolder.viewRight.setVisibility(View.GONE);

        RecentKeywordPair pair = getItem(position);

        // 왼쪽 항목
        viewHolder.txtKeywordLeft.setText(pair.left.keyword);
        viewHolder.txtKeywordLeft.setTag(pair.left);
        viewHolder.txtKeywordLeft.setOnClickListener(keywordClickListener);

        viewHolder.btnDelLeft.setTag(pair.left);
        viewHolder.btnDelLeft.setOnClickListener(keywordClickListener);

        if (pair.right != null) {
            if (viewHolder.viewRight != null)
                viewHolder.viewRight.setVisibility(View.VISIBLE);

            viewHolder.txtKeywordRight.setText(pair.right.keyword);
            viewHolder.txtKeywordRight.setTag(pair.right);
            viewHolder.txtKeywordRight.setOnClickListener(keywordClickListener);

            viewHolder.btnDelRight.setTag(pair.right);
            viewHolder.btnDelRight.setOnClickListener(keywordClickListener);
        }

        //마지막 라인 비노출
        viewHolder.divider.setVisibility(View.VISIBLE);
        if (position == keywordPairs.size()-1) {
            viewHolder.divider.setVisibility(View.GONE);
        }

        return v;
    }

    @Override
    public int getCount() {
        return keywordPairs.size();
    }

    @Override
    public RecentKeywordPair getItem(int position) {
        return keywordPairs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    private class RecentKeywordPair {
        public RecentKeywordPair(RecentKeyword left, RecentKeyword right) {
            this.left = left;
            this.right = right;
        }

        public RecentKeywordPair(RecentKeyword left) {
            this.left = left;
        }

        public RecentKeyword left;
        public RecentKeyword right;
    }

    private class ViewHolder {
        public TextView txtKeywordLeft;
        public ImageButton btnDelLeft;
        public TextView txtKeywordRight;
        public ImageButton btnDelRight;
        public View viewRight;
        public View divider;
    }
}
