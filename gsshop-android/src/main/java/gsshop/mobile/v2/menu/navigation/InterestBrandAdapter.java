/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.menu.navigation;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.web.WebUtils;

import static gsshop.mobile.v2.util.StringUtils.trim;

/**
 * 관심 카테고리 어뎁터.
 *
 */
public class InterestBrandAdapter extends RecyclerView.Adapter<InterestBrandAdapter.RecommendViewHolder> {

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
	public InterestBrandAdapter(Context context, ArrayList<CateContentList> subProductList, String action, String label) {
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
				.inflate(R.layout.interest_brand_item, viewGroup, false);

		return new RecommendViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(RecommendViewHolder holder, final int position) {
		final CateContentList item = subProductList.get(position);
		try{
			Glide.with(mContext).load(trim(item.imageUrl)).placeholder(R.drawable.brand_no_android).diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.brand_image);
		}catch (Exception e){}
		holder.brand_name.setText(item.title);

		holder.brand_layout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 관심 브랜드 호출, 첫 호출과 Back키를 구분하기 위해, 타임 시퀀스 파라미터 추가
				// ex) http://sm21.gsshop.com/section/jbp/brandMain.gs?
				// jbpBrandCd=1000000020&mseq=412154&fromApp=Y&_=1486970084963
				WebUtils.goWeb(mContext, Uri.parse(item.linkUrl).buildUpon().
						appendQueryParameter("_", String.valueOf(System.currentTimeMillis())).build().toString());

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

		public ImageView brand_image;
		public TextView brand_name;
		public View brand_layout;

		public RecommendViewHolder(View itemView) {
			super(itemView);
			brand_layout = itemView.findViewById(R.id.brand_layout);
			brand_image = (ImageView) itemView.findViewById(R.id.brand_image);
			brand_name = (TextView) itemView.findViewById(R.id.brand_name);
		}
	}
}