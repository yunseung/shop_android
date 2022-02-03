/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.support.gtm.GTMAction;
import gsshop.mobile.v2.support.gtm.GTMEnum;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;

/**
 * 이미지 배너.
 *
 */
@SuppressLint("NewApi")
public class BtlVH extends BaseViewHolder {
	private final LinearLayout root;

	private final LinearLayout[] program_layout = new LinearLayout[2];
	private final ImageView[] program_Image = new ImageView[2];
	private final TextView[] program_name = new TextView[2];
	private final TextView[] program_sub_title = new TextView[2];
	private final TextView[] program_description = new TextView[2];


	/**
	 * @param itemView
	 */
	public BtlVH(View itemView) {
		super(itemView);
		root = (LinearLayout) itemView.findViewById(R.id.root);

		program_layout[0] = (LinearLayout)itemView.findViewById(R.id.program_layout_1);
		program_layout[1]= (LinearLayout)itemView.findViewById(R.id.program_layout_2);

		program_Image[0] = (ImageView)itemView.findViewById(R.id.program_Image_1);
		program_Image[1] = (ImageView)itemView.findViewById(R.id.program_Image_2);

		program_name[0] = (TextView)itemView.findViewById(R.id.program_name_1);
		program_name[1] = (TextView)itemView.findViewById(R.id.program_name_2);

		program_sub_title[0] = (TextView)itemView.findViewById(R.id.program_sub_title_1);
		program_sub_title[1] = (TextView)itemView.findViewById(R.id.program_sub_title_2);

		program_description[0] = (TextView)itemView.findViewById(R.id.program_description_1);
		program_description[1] = (TextView)itemView.findViewById(R.id.program_description_2);

		for(ImageView programImage : program_Image){
			DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), programImage);
		}
	}

	/* 이미지 배너. bind */
	@Override
	public void onBindViewHolder(final Context context, int position, ShopInfo info,
								 final String action, final String label, String sectionName) {
		final ArrayList<SectionContentList> item = info.contents.get(position).sectionContent.subProductList;
		if(item == null){
			return;
		}
		for (int i = 0; i < item.size(); i++) {
			final int index = i;
			if (index >= program_layout.length) {
				break;
			}
			program_name[index].setText(item.get(index).productName);
			program_sub_title[index].setText(item.get(index).promotionName);
			program_description[index].setText(item.get(index).saleQuantityText);

			program_layout[index].setVisibility(View.VISIBLE);
			ImageUtil.loadImageResize(context, item.get(index).imageUrl, program_Image[index], R.drawable.noimg_tv);

			program_layout[index].setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					WebUtils.goWeb(context, item.get(index).linkUrl);
					String tempLabel = String.format("%s_%s_%s", label, String.valueOf(index), item.get(index).linkUrl);
					GTMAction.sendEvent(context, GTMEnum.GTM_AREA_CATEGORY, action, tempLabel);
				}
			});

		}
	}

}
