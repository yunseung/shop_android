package gsshop.mobile.v2.home.shop.schedule_A;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;
import gsshop.mobile.v2.R;

/**
 *  TV편성표 AB테스트
 *  카테고리 필터링 기능을 위한 아답터
 */
public class ScheduleFilterAdapter extends RecyclerView.Adapter<ScheduleFilterAdapter.ScheduleFilterViewHolder> {
    private ArrayList<ScheduleCate> filterList; //카테고리 리스트
    public int selectedPosition = 0; //선택된 아이템만 강조


    public ScheduleFilterAdapter(){

    }

    public ArrayList<ScheduleCate> getFilterList(){
        return filterList;
    }

    public void setFilterList(ArrayList<ScheduleCate> filterList){
        this.filterList = filterList;
    }

    @Override
    public ScheduleFilterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tv_schedule_filter, parent, false);
        ScheduleFilterViewHolder vh = new ScheduleFilterViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ScheduleFilterViewHolder holder, int position) {
        holder.tv_filter.setText(filterList.get(position).cateNm);

        //디자인 가이드 맞추느라 양옆 빈 뷰 넣음
        if(position == 0){
            holder.left_empty_view.setVisibility(View.VISIBLE);
            holder.right_empty_view.setVisibility(View.GONE);
        }else if(position == filterList.size()-1){
            holder.left_empty_view.setVisibility(View.GONE);
            holder.right_empty_view.setVisibility(View.VISIBLE);
        }else{
            holder.left_empty_view.setVisibility(View.GONE);
            holder.right_empty_view.setVisibility(View.GONE);
        }

        //선택된 아이템만 강조
        if(selectedPosition == position){
            holder.lay_filter.setBackgroundResource(R.drawable.bg_tv_schedule_filter_selected);
            holder.img_filter_checked.setVisibility(View.VISIBLE);
        }else{
            holder.lay_filter.setBackgroundResource(R.drawable.bg_tv_schedule_filter_unselected);
            holder.img_filter_checked.setVisibility(View.GONE);
        }

        holder.lay_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = position;
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return filterList == null ? 0 : filterList.size();
    }

    //라이브, 마이샵 토글시 필터 초기화
    public void initializeFilter(){
        selectedPosition = 0;
        notifyDataSetChanged();
    }

    public static class ScheduleFilterViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_filter;
        public LinearLayout lay_filter;
        public View left_empty_view;
        public View right_empty_view;
        public ImageView img_filter_checked;

        public ScheduleFilterViewHolder(View v) {

            super(v);
            this.tv_filter = v.findViewById(R.id.tv_filter);
            this.lay_filter = v.findViewById(R.id.lay_filter);
            this.left_empty_view = v.findViewById(R.id.left_empty_view);
            this.right_empty_view = v.findViewById(R.id.right_empty_view);
            this.img_filter_checked = v.findViewById(R.id.img_filter_checked);
        }
    }
}
