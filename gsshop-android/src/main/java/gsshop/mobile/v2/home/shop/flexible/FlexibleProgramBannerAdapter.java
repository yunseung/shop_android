/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.support.gtm.GTMAction;
import gsshop.mobile.v2.support.gtm.GTMEnum;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;

/**
 * 추천딜 리스트 어뎁터.
 *
 */
public class FlexibleProgramBannerAdapter extends RecyclerView.Adapter<FlexibleProgramBannerAdapter.RecommendViewHolder> {

	private final ArrayList<SectionContentList> subProductList;
	private final Context mContext;
	private final String action;
	private final String label;

	/**
	 *
	 * @param context
	 * @param subProductList
	 * @param action
	 * @param label
	 */
	public FlexibleProgramBannerAdapter(Context context, ArrayList<SectionContentList> subProductList, String action, String label) {
		mContext = context;
		this.subProductList = subProductList;
		this.action = action;
		this.label = label;

	}

	@Override
	public RecommendViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		View itemView = LayoutInflater.from(viewGroup.getContext())
				.inflate(R.layout.program_item, viewGroup, false);

		return new RecommendViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(RecommendViewHolder holder, final int position) {
		final SectionContentList item = subProductList.get(position);
		
		ImageUtil.loadImage(mContext, item.imageUrl, holder.product_img, R.drawable.noimg_tv);
		
		TextView info_text = holder.info_text;
		info_text.setText(item.promotionName);

		holder.root.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				WebUtils.goWeb(mContext, item.linkUrl);

				//20151115 이민수 GA GTM 트래킹 코드 삽입 ( 베딜의 추천딜 )
				String tempLabel = String.format("%s_%s_%s", label, position, item.linkUrl);
				GTMAction.sendEvent(mContext, GTMEnum.GTM_AREA_CATEGORY, action, tempLabel);
			}
		});
	}

	@Override
	public int getItemCount() {
		return subProductList == null ? 0 : subProductList.size();
	}

	/**
	 * 뷰홀더
	 *
	 */
	public static class RecommendViewHolder extends RecyclerView.ViewHolder {

		public LinearLayout root;
		public ImageView product_img;
		public TextView info_text;
		public RelativeLayout product_layout;

		public RecommendViewHolder(View itemView) {
			super(itemView);
			root = (LinearLayout) itemView.findViewById(R.id.root);
			product_img = (ImageView) itemView.findViewById(R.id.product_img);
			info_text = (TextView) itemView.findViewById(R.id.info_text);
			product_layout = (RelativeLayout) itemView.findViewById(R.id.product_layout);

			DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), root);
			DisplayUtils.resizeAtViewToScreen(MainApplication.getAppContext(), product_layout);

		}
	}
}