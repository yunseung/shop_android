package gsshop.mobile.v2.home.shop.schedule.viewholder;

//**

import android.content.Context;
import android.view.View;

import gsshop.mobile.v2.home.shop.BaseViewHolderV2;
import gsshop.mobile.v2.home.shop.schedule.model.SchedulePrd;

/**
 * 기본 view holder
 */
public abstract class TLineBaseViewHolder extends BaseViewHolderV2 {

    public final static String AIR_BUY = "AIR_BUY";
    public final static String SOLD_OUT = "SOLD_OUT";
    public final static String TO_SALE = "TO_SALE";
    public final static String SALE_END = "SALE_END";

    /**
     * @param itemView
     */
    public TLineBaseViewHolder(View itemView) {
        super(itemView);
    }

    public void onBindViewHolder(Context context, int position, SchedulePrd info) {};

    //public void onViewAttachedToWindow() {}
    //public void onViewDetachedFromWindow() {}
}