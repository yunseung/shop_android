package gsshop.mobile.v2.home.shop.schedule;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.shop.renewal.flexible.RenewalTLineLiveViewHolder;
import gsshop.mobile.v2.home.shop.renewal.flexible.SchMapMutxxxVH;
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
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleViewType.TYPE_PRD_SPACE;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleViewType.TYPE_PRD_TIME;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleViewType.TYPE_SCHEDULE_DATE;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleViewType.TYPE_SCHEDULE_NEXT_LINK;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleViewType.TYPE_SCHEDULE_PREV_LINK;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleViewType.viewTypeMap;


public class TLinePrdAdapter extends RecyclerView.Adapter<TLineBaseViewHolder> {

    protected final Context mContext;
    private List<SchedulePrd> products;

    public TLinePrdAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public List<SchedulePrd> getProduct() {
        return products;
    }

    public void addProduct(List<SchedulePrd> tempProducts, boolean isNext) {
        if (CollectionUtils.isEmpty(products)) {
            this.products = tempProducts;

        } else if (isNext) {
            //다음편성표보기 클릭한 경우
            //붙일 데이타에서 처음에 있는 이전편성표보기셀 제거
            SchedulePrd prda = tempProducts.get(0);
            if (prda != null && "SCH_PRO_TXT_PRE".equals(prda.viewType)) {
                tempProducts.remove(0);
            }

            //기존 데이타에서 마지막에 있는 다음편성표보기셀 제거
            SchedulePrd prdb = products.get(products.size()-1);
            if (prdb != null && "SCH_PRO_TXT_NEXT".equals(prdb.viewType)) {
                products.remove(products.size()-1);
            }

            this.products.addAll(tempProducts);

        } else {
            //이전편성표보기 클릭한 경우
            //붙일 데이타에서 마지막에 있는 다음편성표보기셀 제거
            SchedulePrd prda = tempProducts.get(tempProducts.size()-1);
            if (prda != null && "SCH_PRO_TXT_NEXT".equals(prda.viewType)) {
                tempProducts.remove(tempProducts.size()-1);
            }

            //기존 데이타에서 처음에 있는 이전편성표보기셀 제거
            SchedulePrd prdb = products.get(0);
            if (prdb != null && "SCH_PRO_TXT_PRE".equals(prdb.viewType)) {
                products.remove(0);
            }

            this.products.addAll(0, tempProducts);
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
                        .inflate(R.layout.renewal_recycler_item_tv_schedule_main, parent, false);
                viewHolder = new RenewalTLineLiveViewHolder(itemView);
                break;
            case TYPE_PRD_LIVE_AB_BCV:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_item_tv_schedule_main_live, parent, false);
                viewHolder = new TLineLiveViewHolder(itemView);
                break;
            case TYPE_SCHEDULE_DATE:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.renewal_recycler_item_tv_schedule_main_text_date, parent, false);
                viewHolder = new TLineTextDateViewHolder(itemView);
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
                        .inflate(R.layout.renewal_recycler_item_tv_schedule_main, parent, false);
                viewHolder = new SchMapMutxxxVH(itemView);
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

