package gsshop.mobile.v2.home.shop.flexible.shoppinglive;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.shop.renewal.utils.SetDtoUtil;
import gsshop.mobile.v2.home.shop.renewal.views.RenewalLayoutProductInfo;
import gsshop.mobile.v2.mobilelive.MobileLivePrdsInfoList;
import gsshop.mobile.v2.support.tv.MobileLiveChatPlayActivity;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;

import static com.blankj.utilcode.util.EmptyUtils.isEmpty;
import static gsshop.mobile.v2.Keys.ACTION.DIRECT_ORDER_WEB;
import static gsshop.mobile.v2.ServerUrls.WEB.MOBILE_LIVE_PRD_LIST_CLICK;

/**
 * 모바일라이브 상품더보기 클릭시 노출 화면
 */
public class MobileLivePrdDialogFragment extends DialogFragment {

    private List<MobileLivePrdsInfoList> prdList;

    private RecyclerView recyclerView;

    public MobileLivePrdDialogFragment() {

    }

    public static MobileLivePrdDialogFragment newInstance() {
        MobileLivePrdDialogFragment fragment = new MobileLivePrdDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Window window = getDialog().getWindow();
        //window.requestFeature(Window.FEATURE_NO_TITLE);
        super.onActivityCreated(savedInstanceState);
        // Set the background to be transparent
        window.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), android.R.color.transparent));
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        // Remove the shadow
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.dimAmount = 0.3f;
        window.setAttributes(layoutParams);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mobilelive_prd_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().getWindow().getAttributes().windowAnimations = R.style.MobileLivePrdSlide;

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_prd);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new PrdAdapter(prdList));
        DividerItemDecoration dividerVertical = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        dividerVertical.setDrawable(getContext().getResources().getDrawable(R.drawable.divider_vertical_2e535d91));
        recyclerView.addItemDecoration(dividerVertical);

        view.findViewById(R.id.button_close).setOnClickListener(v -> {
            dismiss();
        });
    }

    public void setData(List<MobileLivePrdsInfoList> prdList) {
        this.prdList = prdList;
    }

    private class PrdAdapter extends RecyclerView.Adapter<PrdViewHolder> {

        private List<MobileLivePrdsInfoList> prds;

        public PrdAdapter(List<MobileLivePrdsInfoList> prdList) {
            this.prds = prdList;
        }
        
        @Override
        public PrdViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rootView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_item_mobile_live_df_prd, parent, false);
            return new PrdViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(PrdViewHolder holder, int position) {
            final MobileLivePrdsInfoList item = prds.get(position);
            //상품 이미지
            ImageUtil.loadImage(holder.itemView.getContext(), item.imageUrl, holder.mIvProduct, R.drawable.noimage_166_166);

            //구매하기 버튼에 일시품절 표시하고 있기 때문에 여기서는 생략
            //일시품절, 방송중구매가능, 판매예정, 판매종료
            holder.mTxtSoldOut.setVisibility(View.GONE);

            //가격, 혜택 영역 세팅
            holder.mLayoutProductInfo.setViews(SetDtoUtil.setDto(item), SetDtoUtil.BroadComponentType.mobilelive_prd_list_type2);

            //구매하기버튼 세팅
            setBuyButton(holder);

            holder.itemView.setOnClickListener(v -> {
                if (WebUtils.isExternalUrl(item.directBuyUrl)) {
                    try {
                        Uri uri = Uri.parse(item.directBuyUrl);
                        if (WebUtils.DIRECT_ORD_HOST.equals(uri.getHost())) {
                            Intent intent = new Intent(DIRECT_ORDER_WEB);
                            intent.putExtra(Keys.INTENT.WEB_URL, uri.getEncodedQuery());
                            holder.itemView.getContext().startActivity(intent);
                            ((MobileLiveChatPlayActivity) getContext()).setWiseLogHttpClient(MOBILE_LIVE_PRD_LIST_CLICK);
                            return;
                        }
                    } catch (Exception e) {
                        // ignore
                    }
                }
                WebUtils.goWeb(holder.itemView.getContext(), item.directBuyUrl);
                ((MobileLiveChatPlayActivity) getContext()).setWiseLogHttpClient(MOBILE_LIVE_PRD_LIST_CLICK);
            });
        }

        /**
         * 공통모듈의 구매하기 UI를 원하는 위치에 복사하여 사용한다.
         *
         * @param holder PrdViewHolder
         */
        private void setBuyButton(PrdViewHolder holder) {
            //혜택 공통모듈의 구매하기 버튼을 원하는 위치에 추가
            TextView benefitBuyButton = holder.mLayoutProductInfo.getBuyButton();
            ViewGroup viewGroup = (ViewGroup) benefitBuyButton.getParent();
            viewGroup.removeView(benefitBuyButton);
            //클릭은 뒤로 그대로 넘김 (구매하기 클릭이 로우클릭과 동일 액션)
            benefitBuyButton.setClickable(false);
            holder.mLiBuyClone.addView(benefitBuyButton);
        }

        @Override
        public int getItemCount() {
            return isEmpty(prds) ? 0 : prds.size();
        }
    }

    public static class PrdViewHolder extends RecyclerView.ViewHolder {

        public ImageView mIvProduct;
        public TextView mTxtSoldOut;
        public RenewalLayoutProductInfo mLayoutProductInfo;
        public LinearLayout mLiBuyClone;

        public PrdViewHolder(View itemView) {
            super(itemView);
            mIvProduct = itemView.findViewById(R.id.iv_prd);
            mTxtSoldOut = itemView.findViewById(R.id.txt_sold_out);
            mLayoutProductInfo = itemView.findViewById(R.id.layout_product_info);
            mLiBuyClone = itemView.findViewById(R.id.ll_buy_clone);
        }
    }
}