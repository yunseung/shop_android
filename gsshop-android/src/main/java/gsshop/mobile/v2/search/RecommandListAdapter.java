package gsshop.mobile.v2.search;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import gsshop.mobile.v2.R;

/**
 * 연관검색어 추천 어댑터
 */
public class RecommandListAdapter extends RecyclerView.Adapter<RecommandListAdapter.RecommandViewHolder> {
    private ArrayList<RecentRecommandInfo> keywordList;
    public View.OnClickListener keywordClickListener;

    public RecommandListAdapter(ArrayList<RecentRecommandInfo> keywordArr, View.OnClickListener keywordClickListener) {
        this.keywordList = keywordArr;
        this.keywordClickListener = keywordClickListener;
    }

    @Override
    public RecommandListAdapter.RecommandViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommand_keyword_row, parent, false);
        RecommandViewHolder vh = new RecommandViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecommandViewHolder viewHolder, int i) {
        viewHolder.tv_recommand_keyword.setText(keywordList.get(i).rtq);
        viewHolder.lay_recommand_keyword.setOnClickListener(keywordClickListener);
        viewHolder.lay_recommand_keyword.setTag(keywordList.get(i).rtq);

        switch (i%5) {
            case 0 :
                viewHolder.lay_recommand_keyword.setBackgroundResource(R.drawable.recent_recommand_border_1);
                break;
            case 1 :
                viewHolder.lay_recommand_keyword.setBackgroundResource(R.drawable.recent_recommand_border_2);
                break;
            case 2 :
                viewHolder.lay_recommand_keyword.setBackgroundResource(R.drawable.recent_recommand_border_3);
                break;
            case 3 :
                viewHolder.lay_recommand_keyword.setBackgroundResource(R.drawable.recent_recommand_border_4);
                break;
            case 4 :
                viewHolder.lay_recommand_keyword.setBackgroundResource(R.drawable.recent_recommand_border_5);
                break;
            default:
                break;
        }

        //디자인 가이드 맞추느라 양옆 빈 뷰 넣음
        if(i == 0){
            viewHolder.left_empty_view.setVisibility(View.VISIBLE);
            viewHolder.right_empty_view.setVisibility(View.GONE);
        }else if(i == keywordList.size()-1){
            viewHolder.left_empty_view.setVisibility(View.GONE);
            viewHolder.right_empty_view.setVisibility(View.VISIBLE);
        }else{
            viewHolder.left_empty_view.setVisibility(View.GONE);
            viewHolder.right_empty_view.setVisibility(View.GONE);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return keywordList.size();
    }


    public static class RecommandViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_recommand_keyword;
        public LinearLayout lay_recommand_keyword;
        public View left_empty_view;
        public View right_empty_view;


        public RecommandViewHolder(View v) {
            super(v);
            this.tv_recommand_keyword = v.findViewById(R.id.tv_recommand_keyword);
            this.lay_recommand_keyword = v.findViewById(R.id.lay_recommand_keyword);
            this.left_empty_view = v.findViewById(R.id.left_empty_view);
            this.right_empty_view = v.findViewById(R.id.right_empty_view);
        }
    }
}