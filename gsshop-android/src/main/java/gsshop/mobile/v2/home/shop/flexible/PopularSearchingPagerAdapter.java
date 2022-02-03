package gsshop.mobile.v2.home.shop.flexible;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.gsshop.mocha.ui.util.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.web.WebUtils;

import static com.blankj.utilcode.util.EmptyUtils.isEmpty;
import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;
import static gsshop.mobile.v2.home.shop.flexible.BanTxtExpGbaVH.ITEM_NUM_PER_PAGE;

/**
 * 인기검색어 배너 adapter
 */
public class PopularSearchingPagerAdapter extends PagerAdapter {

    private final Context context;

    /**
     * 인기검색어 데이타
     */
    private List<SectionContentList> list;

    public PopularSearchingPagerAdapter(Context context, List<SectionContentList> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list.isEmpty()) {
            return 0;
        }
        double dPageSize = list.size() / (double) ITEM_NUM_PER_PAGE;
        return (int)Math.ceil(dPageSize);
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = ((Activity) context).getLayoutInflater()
                .inflate(R.layout.home_row_type_fx_popular_searching_banner_viewpager, null);

        List<View> listView = new ArrayList<View>();

        View item1 = view.findViewById(R.id.item1);
        View item2 = view.findViewById(R.id.item2);
        View item3 = view.findViewById(R.id.item3);
        View item4 = view.findViewById(R.id.item4);
        View item5 = view.findViewById(R.id.item5);

        listView.add(0, item1);
        listView.add(1, item2);
        listView.add(2, item3);
        listView.add(3, item4);
        listView.add(4, item5);

        for ( int i = position * ITEM_NUM_PER_PAGE; i < (position + 1) * ITEM_NUM_PER_PAGE; i++ ) {
            int idx = i % ITEM_NUM_PER_PAGE;

            View itemView = listView.get(idx);
            TextView txtRank = itemView.findViewById(R.id.txt_vp_rank);
            TextView txtKeyword = itemView.findViewById(R.id.txt_vp_keyword);
            View layRankStateNew = itemView.findViewById(R.id.lay_rank_state_new);
            View layRankStateUp = itemView.findViewById(R.id.lay_rank_state_up);
            View layRankStateDown = itemView.findViewById(R.id.lay_rank_state_down);
            View layRankStateSame = itemView.findViewById(R.id.lay_rank_state_same);
            View viewLine = itemView.findViewById(R.id.txt_vp_line);
            ViewUtils.hideViews(txtRank, txtKeyword, layRankStateNew, layRankStateUp, layRankStateDown, layRankStateSame);

            //마지막 항목 하단라인은 표시하지 않음
            if (idx == 4) {
                ViewUtils.hideViews(viewLine);
            }

            if (i < list.size()) {
                SectionContentList data = list.get(i);
                if (isEmpty(data)) {
                    continue;
                }

                txtRank.setText(data.saleQuantity);
                txtKeyword.setText(data.productName);
                ViewUtils.showViews(txtRank, txtKeyword);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        WebUtils.goWeb(context, data.linkUrl);
                    }
                });

                TextView txtRankState = null;
                String rankState = data.discountRateText;
                if ("n".equalsIgnoreCase(rankState)) {
                    //새로진입
                    ViewUtils.showViews(layRankStateNew);
                } else if ("u".equalsIgnoreCase(rankState)) {
                    //상승
                    ViewUtils.showViews(layRankStateUp);
                    txtRankState = (TextView) itemView.findViewById(R.id.txt_rank_state_up);
                } else if ("d".equalsIgnoreCase(rankState)) {
                    //하락
                    ViewUtils.showViews(layRankStateDown);
                    txtRankState = (TextView) itemView.findViewById(R.id.txt_rank_state_down);
                } else if ("f".equalsIgnoreCase(rankState)) {
                    //변화없음
                    ViewUtils.showViews(layRankStateSame);
                }

                if (isNotEmpty(txtRankState)) {
                    txtRankState.setText(data.discountRate);
                }
            } else {
                //데이타가 20개가 안되는 경우 공백으로 유지
                ViewUtils.hideViews(itemView);
            }
        }

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    public void setItem(List<SectionContentList> itemList) {
        this.list = itemList;
    }
}