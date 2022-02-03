/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.menu.navigation;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.web.WebUtils;

/**
 * 관심 카테고리 어뎁터.
 *
 */
public class InterestCategoriesAdapter extends RecyclerView.Adapter<InterestCategoriesAdapter.RecommendViewHolder> {

	private ArrayList<CateContentList> subProductList;
	private final Context mContext;
	private final String action;
	private final String label;
	private String category;

	/**
	 *
	 * @param context
	 * @param subProductList
	 * @param action
	 * @param label
	 */
	public InterestCategoriesAdapter(Context context, ArrayList<CateContentList> subProductList, String action, String label) {
		mContext = context;
		this.subProductList = subProductList;
		this.action = action;
		this.label = label;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setItem(ArrayList<CateContentList> subProductList){
		this.subProductList = subProductList;
	}

	@Override
	public RecommendViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		View itemView = LayoutInflater.from(viewGroup.getContext())
				.inflate(R.layout.interest_categorys_item, viewGroup, false);

		return new RecommendViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(RecommendViewHolder holder, final int position) {
		final String item = subProductList.get(position).title;

		TextView info_text = holder.info_text;
		info_text.setText(item);

		info_text.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				WebUtils.goWeb(mContext, subProductList.get(position).linkUrl);
				EventBus.getDefault().post(new Events.NavigationCloseEvent());
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

		public TextView info_text;

		public RecommendViewHolder(View itemView) {
			super(itemView);
			info_text = (TextView) itemView.findViewById(R.id.text);
		}
	}
}