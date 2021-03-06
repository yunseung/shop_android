/*
 * Copyright (C) 2017. Alexander Bilchuk <a.bilchuk@sandrlab.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gsshop.mobile.v2.support.media.exoplayer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;
import java.util.List;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.shop.flexible.shoppinglive.MobileLivePrdDialogFragment;
import gsshop.mobile.v2.home.shop.renewal.utils.SetDtoUtil;
import gsshop.mobile.v2.home.shop.renewal.views.RenewalLayoutProductInfoPrd2;
import gsshop.mobile.v2.mobilelive.MobileLivePrdsInfoList;
import gsshop.mobile.v2.support.gtm.AMPAction;
import gsshop.mobile.v2.support.gtm.AMPEnum;
import gsshop.mobile.v2.support.tv.MobileLiveChatPlayActivity;
import gsshop.mobile.v2.support.ui.PreviewRecyclerViewPager;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;

import static gsshop.mobile.v2.Keys.ACTION.DIRECT_ORDER_WEB;
import static gsshop.mobile.v2.ServerUrls.WEB.MOBILE_LIVE_MORE_CLICK;
import static gsshop.mobile.v2.home.shop.schedule.viewholder.TLineBaseViewHolder.AIR_BUY;
import static gsshop.mobile.v2.home.shop.schedule.viewholder.TLineBaseViewHolder.SALE_END;
import static gsshop.mobile.v2.home.shop.schedule.viewholder.TLineBaseViewHolder.SOLD_OUT;
import static gsshop.mobile.v2.home.shop.schedule.viewholder.TLineBaseViewHolder.TO_SALE;
import static gsshop.mobile.v2.support.ui.PreviewRecyclerViewPager.PreviewViewPagerViewHolder;

/**
 * ??????????????? ???????????? ??? ??????????????? ??????
 * history
 * -21.04.28 ????????? ????????? ????????????????????? ?????? ?????? ??????/?????? ????????? ?????????. ?????? ????????????????????? ??? ?????????
 */
public class PreviewMobileLivePrdAdapter extends PreviewRecyclerViewPager.PreviewViewPagerAdapter<PreviewViewPagerViewHolder> {

    private Context mContext;
    private List<MobileLivePrdsInfoList> prdList;
    private List<MobileLivePrdsInfoList> prdOriList;

    /**
     * ????????? (?????????)
     */
    private static final int VIEWHOLDER_TYPE_MORE = 0;

    public PreviewMobileLivePrdAdapter(Context context, @NonNull DisplayMetrics metrics,
                                       @NonNull List<MobileLivePrdsInfoList> prdList, String currentPrdIndex) {
        super(metrics);
        this.prdOriList = prdList;
        this.mContext = context;

        if (prdList.size() > 1) {
            this.prdList = new ArrayList<>();
            //??????1??? + ????????? ??????
            this.prdList.add(prdList.get(Integer.parseInt(currentPrdIndex)));
            //history 21.04.28  ??????
            //????????? ????????? ?????? ????????????
            /*MobileLivePrdsInfoList morePrd = new MobileLivePrdsInfoList();
            morePrd.type = 0;
            morePrd.moreCount = prdList.size() - 1;
            this.prdList.add(morePrd);*/
        } else {
            //??????1?????? ??????
            this.prdList = prdList;
        }
    }

    @Override
    public PreviewViewPagerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PreviewViewPagerViewHolder viewHolder = null;
        View itemView;
        switch (viewType) {
            case VIEWHOLDER_TYPE_MORE:
                // ?????????
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_item_mobile_live_more, parent, false);
                viewHolder = new PreviewMobileLiveMoreViewHolder(itemView);
                break;
            default:
                // ?????? (????????? default 1??? ??????)
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_item_mobile_live_prd, parent, false);
                viewHolder = new PreviewMobileLivePrdViewHolder(itemView);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PreviewViewPagerViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VIEWHOLDER_TYPE_MORE:
                bindMore((PreviewMobileLiveMoreViewHolder) holder, position);
                break;
            default:
                // ?????? (????????? default 1??? ??????)
                bindPrd((PreviewMobileLivePrdViewHolder) holder, position);
                break;
        }
    }

    /**
     * ????????? ??????
     *
     * @param holder ?????????
     * @param position ??????
     */
    private void bindMore(PreviewMobileLiveMoreViewHolder holder, int position) {
        MobileLivePrdsInfoList item = prdList.get(position);
        holder.txtMoreCount.setText("+" + item.moreCount);
        holder.layRoot.setOnClickListener(v -> {
            goPrdList();
        });
    }

    /**
     * ?????? ??????
     *
     * @param holder ?????????
     * @param position ??????
     */
    private void bindPrd(PreviewMobileLivePrdViewHolder holder, int position) {
        MobileLivePrdsInfoList item = prdList.get(position);

        //????????? ?????????
        ImageUtil.loadImageFitWithRound(mContext, item.imageUrl, holder.imgPrdSumbnail,
                DisplayUtils.convertDpToPx(mContext, 4), R.drawable.noimg_list);

        //????????????, ?????????????????????, ????????????, ????????????
        holder.txtPrdComment.setVisibility(View.GONE);
        if(SOLD_OUT.equalsIgnoreCase(item.imageLayerFlag)) {
            holder.txtPrdComment.setText(R.string.layer_flag_sold_out);
            holder.txtPrdComment.setVisibility(View.VISIBLE);
        } else if(AIR_BUY.equalsIgnoreCase(item.imageLayerFlag)) {
            holder.txtPrdComment.setText(R.string.layer_flag_air_buy);
            holder.txtPrdComment.setVisibility(View.VISIBLE);
        } else if(TO_SALE.equalsIgnoreCase(item.imageLayerFlag)) {
            holder.txtPrdComment.setText(R.string.layer_flag_to_sale);
            holder.txtPrdComment.setVisibility(View.VISIBLE);
        } else if(SALE_END.equalsIgnoreCase(item.imageLayerFlag)) {
            holder.txtPrdComment.setText(R.string.layer_flag_sale_end);
            holder.txtPrdComment.setVisibility(View.VISIBLE);
        }

        //??????1?????? ?????? ???????????? ?????????, 2??? ????????? ?????? ??????????????? ??????(???????????? ??????)
        holder.layRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (WebUtils.isExternalUrl(item.directBuyUrl)) {
                    try {
                        Uri uri = Uri.parse(item.directBuyUrl);
                        if (WebUtils.DIRECT_ORD_HOST.equals(uri.getHost())) {
                            Intent intent = new Intent(DIRECT_ORDER_WEB);
                            intent.putExtra(Keys.INTENT.WEB_URL, uri.getEncodedQuery());
                            mContext.startActivity(intent);
                            AMPAction.sendAmpEvent(AMPEnum.MOBILELIVE_CLICK_BUY);
                            return;
                        }
                    } catch (Exception e) {
                        // ignore
                    }
                }
                WebUtils.goWeb(mContext, item.directBuyUrl);
                AMPAction.sendAmpEvent(AMPEnum.MOBILELIVE_CLICK_BUY);
            }
        });

        holder.mLayoutProductInfo.setViews(SetDtoUtil.setDto(item), SetDtoUtil.BroadComponentType.mobilelive_prd_list_type1);
    }

    /**
     * ??????????????? ????????? ????????????.
     */
    private void goPrdList() {
        MobileLivePrdDialogFragment prdDialog = new MobileLivePrdDialogFragment();
        prdDialog.setData(prdOriList);
        prdDialog.show(((FragmentActivity) mContext).getSupportFragmentManager(), MobileLivePrdDialogFragment.class.getSimpleName());
        ((MobileLiveChatPlayActivity) mContext).setWiseLogHttpClient(MOBILE_LIVE_MORE_CLICK);
    }

    @Override
    public int getItemCount() {
        return prdList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return prdList.get(position).type;
    }

    /**
     * ?????? ?????????
     */
    static class PreviewMobileLivePrdViewHolder extends PreviewViewPagerViewHolder {

        View layRoot;
        ImageView imgPrdSumbnail;
        TextView txtPrdComment;
        RenewalLayoutProductInfoPrd2 mLayoutProductInfo;

        public PreviewMobileLivePrdViewHolder(View itemView) {
            super(itemView);
            layRoot = itemView.findViewById(R.id.lay_root);
            imgPrdSumbnail = itemView.findViewById(R.id.img_prd_sumbnail);
            txtPrdComment = itemView.findViewById(R.id.txt_prd_comment);
            mLayoutProductInfo = itemView.findViewById(R.id.layout_product_info);
        }
    }

    /**
     * ????????? ?????????
     */
    static class PreviewMobileLiveMoreViewHolder extends PreviewViewPagerViewHolder {

        View layRoot;
        TextView txtMoreCount;

        public PreviewMobileLiveMoreViewHolder(View itemView) {
            super(itemView);
            layRoot = itemView.findViewById(R.id.lay_root);
            txtMoreCount = itemView.findViewById(R.id.txt_more_count);
        }
    }
}
