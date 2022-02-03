/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.todayopen;

import android.content.Context;
import android.view.View;

import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.ViewHolderType;

/**
 * 기본형 단품 item
 *
 */
public class LVH extends gsshop.mobile.v2.home.shop.flexible.LVH {
	private TodayOpenAdapter adapter;

	/**
	 * @param itemView
	 */
	public LVH(View itemView, TodayOpenAdapter adapter, String navigationId) {
		super(itemView, adapter, navigationId);
		this.adapter = adapter;
	}

	/*
	 * bind
	 */
	@Override
	public void onBindViewHolder(final Context context, int position, ShopInfo info,final String action,final String label, String sectionName) {
		//오늘오픈에서 필터가 적용되는 경우
		final SectionContentList item;
		int newPosition = 0;
		if(adapter.isFillter()){
			String[] categoryNo = adapter.getCategoryNo();
			SectionContentList filterItem = null;
			int currentIndex = 0;

			if(info.contents != null && !info.contents.isEmpty() && info.contents.get(0).type == ViewHolderType.BANNER_TYPE_BAND) {
				newPosition = position - 1;
			}
			for(int i = 0 ; i < info.contents.size() ; i ++){
				for(String category : categoryNo){
					if(info.contents.get(i).sectionContent != null
							&& info.contents.get(i).sectionContent.cateGb != null && info.contents.get(i).sectionContent.cateGb.equals(category)) {
						if(currentIndex == newPosition) {
							filterItem = info.contents.get(i).sectionContent;
							break;
						}
						currentIndex++;
					}
				}
				if(filterItem != null){
					break;
				}
			}
			item = filterItem;

			if(newPosition + 1 == adapter.getFilterCount() - 1){
				adapter.setFooterPosition(newPosition);
			}
		}else{
			item = info.contents.get(newPosition).sectionContent;
		}

		if(item == null){
			return;
		}

		super.bindItem(context,item,action,label);


	}

}
