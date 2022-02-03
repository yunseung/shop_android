/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.search;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import java.util.ArrayList;
import java.util.LinkedList;

import gsshop.mobile.v2.R;
import roboguice.util.Ln;

import static gsshop.mobile.v2.search.SearchAction.SEARCH_SPLIT_SEPERATOR;

/**
 * 연관검색어 목록 어댑터.
 *
 */
public class RelatedKeywordListAdapter extends BaseAdapter {

    /**
     * 검색어 일치하는 부분 색상
     */
    private final int matchedColor = Color.parseColor("#111111");

    private static final StyleSpan matchedStyle =  new StyleSpan(Typeface.BOLD);

    private final LayoutInflater inflater;

    public String keyword;
    
    private final ArrayList<KeywordPair> keywordPairs = new ArrayList<KeywordPair>();

    private final OnClickListener keywordClickListener;

    private SearchAction searchAction;

    private Context mContext;

    public RelatedKeywordListAdapter(Context context, OnClickListener keywordClickListener) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.keywordClickListener = keywordClickListener;

        mContext = context;
        searchAction = new SearchAction();
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

    public void setItems(RelatedKeywordList keywords) {
        keywordPairs.clear();

        if (keywords == null) {
            return;
        }

        keyword = keywords.query;
        
        int size = Math.max(keywords.backSize, keywords.frontSize);
        for (int i = 0; i < size; i++) {
            KeywordPair pair = new KeywordPair(i < keywords.frontSize ? keywords.front.get(i)
                    : null, i < keywords.backSize ? keywords.back.get(i) : null);
            keywordPairs.add(pair);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int length = keyword.trim().length();
        View v = convertView;
        if (v == null) {
            v = inflater.inflate(R.layout.related_keyword_row, null);
        }
        KeywordPair pair = getItem(position);

        // 왼쪽 항목
        // 현재 왼쪽(Front만 사용하며 back부분은 사용안하지만 확장성을 위해 남겨둠)
        View layoutLeft = v.findViewById(R.id.layout_left);

        layoutLeft.setOnClickListener(pair.left == null ? null : keywordClickListener);
        layoutLeft.setTag(pair.left);
        TextView txtKeywordLeft = (TextView) v.findViewById(R.id.txt_keyword_left);

        if (pair.left != null) {
            //하단 모서리 라운딩 처리하면서 마지막 라인 비노출
            v.findViewById(R.id.vw_line).setVisibility(View.VISIBLE);
            if (position == keywordPairs.size()-1) {
                v.findViewById(R.id.vw_line).setVisibility(View.GONE);
            }
            txtKeywordLeft.setText(pair.left, BufferType.SPANNABLE);

            Spannable s = (Spannable) txtKeywordLeft.getText();
            try {
            	//검색어를 포함하는 경우만 폰트색 지정
                //while문이 무한루프에 빠지지 않도록 length값이 0보다 큰경우만 수행
            	if (txtKeywordLeft.getText().toString().contains(keyword.trim()) && length > 0) {

                    int index = pair.left.indexOf(keyword);
                    while(index > -1) {
                        s.setSpan(new ForegroundColorSpan(matchedColor), index, index + length,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        s.setSpan(matchedStyle, index, index + length,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        index = pair.left.indexOf(keyword, index + length);
                    }

            	}
            } catch (IndexOutOfBoundsException e) {
                //ignored

                // 10/19 품질팀 요청
                // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
                // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
                // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
                Ln.e(e);
            }

            try {
                TextView txtKeywordRight = v.findViewById(R.id.txt_keyword_right);
                txtKeywordRight.setText("");

                LinkedList<RecentKeyword> recentKeywords = searchAction.getRecentDateKeywords(mContext);

                for (int i = 0; i < recentKeywords.size(); i++) {
                    RecentKeyword keywordTemp = recentKeywords.get(i);
                    String[] arrKeyword = keywordTemp.keyword.split(SEARCH_SPLIT_SEPERATOR);
                    if (arrKeyword.length > 1 && arrKeyword[0] != null) {
                        if (arrKeyword[0].trim().equalsIgnoreCase(pair.left)) {
                            if (arrKeyword[1] != null) {
                                txtKeywordRight.setText(arrKeyword[1]);
                            }
                        }
                    }
                }
            }
            catch ( NullPointerException e) {
                Ln.e(e.getMessage());
            }

        } else {
            txtKeywordLeft.setText(null);
        }
        return v;
    }

    private class KeywordPair {
        public KeywordPair(String left, String right) {
            this.left = left;
            this.right = right;
        }

        public String left;
        public String right;
    }

}
