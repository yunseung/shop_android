package gsshop.mobile.v2.home.shop.schedule_A;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gsshop.mocha.core.exception.ApplyExceptionHandler;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.shop.schedule.model.SchedulePrd;
import gsshop.mobile.v2.home.shop.schedule.viewholder.TLineBaseViewHolder;
import gsshop.mobile.v2.home.shop.schedule.viewholder.TLineImageBannerViewHolder;
import gsshop.mobile.v2.home.shop.schedule.viewholder.TLineLiveViewHolder;
import gsshop.mobile.v2.home.shop.schedule.viewholder.TLineNoDataViewHolder;
import gsshop.mobile.v2.home.shop.schedule.viewholder.TLinePrdViewHolder_ab_bcv;
import gsshop.mobile.v2.home.shop.schedule.viewholder.TLineSpaceViewHolder;
import gsshop.mobile.v2.home.shop.schedule.viewholder.TLineTextDateViewHolder;
import gsshop.mobile.v2.home.shop.schedule.viewholder.TLineTextMoreViewHolder;
import gsshop.mobile.v2.home.shop.schedule.viewholder.TLineTextTimeViewHolder;

import static gsshop.mobile.v2.home.shop.schedule.TVScheduleViewType.TYPE_PRD_BAN_W540;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleViewType.TYPE_PRD_GOV;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleViewType.TYPE_PRD_INSU;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleViewType.TYPE_PRD_LIVE;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleViewType.TYPE_PRD_LIVE_AB_BCV;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleViewType.TYPE_PRD_MAIN;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleViewType.TYPE_PRD_MAIN_AB_BCV;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleViewType.TYPE_PRD_NO_DATA;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleViewType.TYPE_PRD_NO_RESULTS;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleViewType.TYPE_PRD_SPACE;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleViewType.TYPE_PRD_TIME;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleViewType.TYPE_SCHEDULE_DATE;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleViewType.TYPE_SCHEDULE_NEXT_LINK;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleViewType.TYPE_SCHEDULE_PREV_LINK;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleViewType.viewTypeMap;


public class TLinePrdAdapter_A extends RecyclerView.Adapter<TLineBaseViewHolder>{

    protected final Context mContext;
    private List<SchedulePrd> products;

    public TLinePrdAdapter_A(Context mContext) {
        this.mContext = mContext;
    }

    public List<SchedulePrd> getProduct() {
        return products;
    }


    /**
     * TV편성표 AB테스트
     * A타입일때는 -> 이전편성표 / 다음편성표 로직 없어도 되니까 제거
     * @param tempProducts
     */
    public void addProduct(List<SchedulePrd> tempProducts) {
        if(tempProducts != null ){
            this.products = tempProducts;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return viewTypeMap.get(products.get(position).viewType);
    }

    @Override
    public TLineBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TLineBaseViewHolder viewHolder = null;
        View itemView;

        switch (viewType) {
            case TYPE_SCHEDULE_PREV_LINK:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_item_tv_schedule_main_text_more_prev, parent, false);
                viewHolder = new TLineTextMoreViewHolder(itemView);
                break;
            case TYPE_SCHEDULE_NEXT_LINK:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_item_tv_schedule_main_text_more_next, parent, false);
                viewHolder = new TLineTextMoreViewHolder(itemView);
                break;
            case TYPE_PRD_LIVE:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.renewal_recycler_item_tv_schedule_main_a, parent, false);
                viewHolder = new RenewalTLineLiveViewHolder_A(itemView);
                break;
            case TYPE_PRD_LIVE_AB_BCV:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_item_tv_schedule_main_live, parent, false);
                viewHolder = new TLineLiveViewHolder(itemView);
                break;
            case TYPE_SCHEDULE_DATE:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.renewal_recycler_item_tv_schedule_main_text_date_a, parent, false);
                viewHolder = new TLineTextDateViewHolder_A(itemView);
                break;
            case TYPE_PRD_TIME:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_item_tv_schedule_main_text_time, parent, false);
                viewHolder = new TLineTextTimeViewHolder(itemView);
                break;
            case TYPE_PRD_BAN_W540:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.renewal_recycler_item_tv_schedule_main_image_banner_w540, parent, false);
                viewHolder = new TLineImageBannerViewHolder(itemView);
                break;
            case TYPE_PRD_INSU:
            case TYPE_PRD_GOV:
            case TYPE_PRD_MAIN:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.renewal_recycler_item_tv_schedule_main_a, parent, false);
                viewHolder = new RenewalTLinePrdViewHolder_A(itemView);
                break;
            case TYPE_PRD_SPACE:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.renewal_recycler_item_tv_schedule_main_space, parent, false);
                viewHolder = new TLineSpaceViewHolder(itemView);
                break;
            case TYPE_PRD_NO_DATA:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.renewal_recycler_item_tv_schedule_main_no_data, parent, false);
                viewHolder = new TLineNoDataViewHolder(itemView);
                break;
            case TYPE_PRD_NO_RESULTS: //"조건에 맞는 상품이 없습니다" 뷰타입
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.tv_schedule_filter_no_result, parent, false);
                viewHolder = new TLineNoFilterResultViewHolder(itemView);
                break;

            /**
             * TV편성표 VOD상품아이콘 노출 AB테스트 by hanna
             */
            case TYPE_PRD_MAIN_AB_BCV:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_item_tv_schedule_main_main_ab_bcv, parent, false);
                viewHolder = new TLinePrdViewHolder_ab_bcv(itemView);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TLineBaseViewHolder holder, int position) {
        holder.onBindViewHolder(mContext, position, products.get(position));
    }

    @Override
    public int getItemCount() {
        return this.products == null ? 0 : this.products.size();
    }

    @Override
    public void onViewAttachedToWindow(TLineBaseViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.onViewAttachedToWindow();
    }

    @Override
    public void onViewDetachedFromWindow(TLineBaseViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.onViewDetachedFromWindow();
    }
}

