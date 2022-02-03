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
import android.widget.TextView;

import java.util.ArrayList;

import gsshop.mobile.v2.R;

/**
 * 인기검색어 목록 어댑터.
 *
 */
public class PopularKeywordListAdapter extends BaseAdapter {

    //인기검색어 리스트에서 강조할 라인수 (라인당 좌/우 두개 키워드가 들어감)
    private final int EMPHASIS_LINE = 3;

    private final LayoutInflater inflater;

    public String keyword;

    private final ArrayList<KeywordPair> keywordPairs = new ArrayList<KeywordPair>();

    private final OnClickListener keywordClickListener;

    public PopularKeywordListAdapter(Context context, OnClickListener keywordClickListener) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.keywordClickListener = keywordClickListener;
    }

    @Override
    public int getCount() {
        return keywordPairs.size();
    }

    @Override
    public KeywordPair getItem(int position) {
        return keywordPairs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setItems(PopularKeywordList keywords) {
        keywordPairs.clear();

        if (keywords == null) {
            return;
        }

        int popKeywordCount = keywords.popKeyword.size();
        boolean isEmphasis;
        for (int i = 0; i < popKeywordCount; i++) {
            isEmphasis = (i < EMPHASIS_LINE * 2);
            String left = keywords.popKeyword.get(i++);
            String right = (i < popKeywordCount) ? keywords.popKeyword.get(i) : null;
            KeywordPair pair = new KeywordPair(left, right, String.valueOf(i),
                    String.valueOf(i + 1), isEmphasis);
            keywordPairs.add(pair);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = inflater.inflate(R.layout.popular_keyword_row, null);
        }

        KeywordPair pair = getItem(position);
        
        // 왼쪽 항목
        View layoutLeft = v.findViewById(R.id.layout_left);
        layoutLeft.setOnClickListener(pair.left == null ? null : keywordClickListener);
        layoutLeft.setTag(pair.left);
        TextView txtKeywordLeft = (TextView) v.findViewById(R.id.txt_keyword_left);
        TextView txtKeywordLeftRankEmphasis = (TextView) v
                .findViewById(R.id.txt_keyword_left_rank_emphasis);
        TextView txtKeywordLeftRankNormal = (TextView) v
                .findViewById(R.id.txt_keyword_left_rank_normal);

        txtKeywordLeftRankEmphasis.setVisibility(View.INVISIBLE);
        txtKeywordLeftRankNormal.setVisibility(View.INVISIBLE);

        if (pair.left != null) {
            txtKeywordLeft.setText(pair.left);

            if (pair.isEmphasis) {
                txtKeywordLeftRankEmphasis.setVisibility(View.VISIBLE);
                txtKeywordLeftRankEmphasis.setText(pair.leftRank);
            } else {
                txtKeywordLeftRankNormal.setVisibility(View.VISIBLE);
                txtKeywordLeftRankNormal.setText(pair.leftRank);
            }

        } else {
            txtKeywordLeft.setText(null);
        }

        // 오른쪽 항목
        View layoutRight = v.findViewById(R.id.layout_right);
        layoutRight.setOnClickListener(pair.right == null ? null : keywordClickListener);
        layoutRight.setTag(pair.right);
        TextView txtKeywordRight = (TextView) v.findViewById(R.id.txt_keyword_right);
        TextView txtKeywordRightRankEmphasis = (TextView) v
                .findViewById(R.id.txt_keyword_right_rank_emphasis);
        TextView txtKeywordRightRankNormal = (TextView) v
                .findViewById(R.id.txt_keyword_right_rank_normal);

        txtKeywordRightRankEmphasis.setVisibility(View.INVISIBLE);
        txtKeywordRightRankNormal.setVisibility(View.INVISIBLE);

        if (pair.right != null) {
            txtKeywordRight.setText(pair.right);

            if (pair.isEmphasis) {
                txtKeywordRightRankEmphasis.setVisibility(View.VISIBLE);
                txtKeywordRightRankEmphasis.setText(pair.rightRank);
            } else {
                txtKeywordRightRankNormal.setVisibility(View.VISIBLE);
                txtKeywordRightRankNormal.setText(pair.rightRank);
            }
        } else {
            //convertView가 재사용될 수 있으므로 내용 클리어시킴
            txtKeywordRight.setText(null);
        }

        return v;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    private class KeywordPair {
        public KeywordPair(String left, String right, String leftRank, String rightRank,
                boolean isEmphasis) {
            this.left = left;
            this.right = right;
            this.leftRank = leftRank;
            this.rightRank = rightRank;
            this.isEmphasis = isEmphasis;
        }

        private final String left;
        private final String right;
        private final String leftRank;
        private final String rightRank;
        private final boolean isEmphasis;
    }

}
