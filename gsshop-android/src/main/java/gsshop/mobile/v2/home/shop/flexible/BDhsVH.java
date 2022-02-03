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
public class BDhsVH extends BaseViewHolder {
	private final TextView program_text;
	private final ImageView[] program_Image = new ImageView[6];
	private final View[] program_layout = new View[6];

	private final LinearLayout expand_control_layout;
	private final LinearLayout center_image_layout;
	private final LinearLayout bottom_image_layout;

	private final View first_line, expand_bootom_view;


	/**
	 * @param itemView
	 */
	public BDhsVH(View itemView) {
		super(itemView);
		program_text = (TextView)itemView.findViewById(R.id.program_text);


		program_layout[0] = itemView.findViewById(R.id.program_layout_1);
		program_layout[1] = itemView.findViewById(R.id.program_layout_2);
		program_layout[2] = itemView.findViewById(R.id.program_layout_3);
		program_layout[3] = itemView.findViewById(R.id.program_layout_4);
		program_layout[4] = itemView.findViewById(R.id.program_layout_5);
		program_layout[5] = itemView.findViewById(R.id.program_layout_6);


		program_Image[0] = (ImageView)itemView.findViewById(R.id.program_Image_1);
		program_Image[1] = (ImageView)itemView.findViewById(R.id.program_Image_2);
		program_Image[2] = (ImageView)itemView.findViewById(R.id.program_Image_3);
		program_Image[3] = (ImageView)itemView.findViewById(R.id.program_Image_4);
		program_Image[4] = (ImageView)itemView.findViewById(R.id.program_Image_5);
		program_Image[5] = (ImageView)itemView.findViewById(R.id.program_Image_6);


		expand_control_layout = (LinearLayout)itemView.findViewById(R.id.expand_control_layout);
		center_image_layout = (LinearLayout)itemView.findViewById(R.id.center_image_layout);
		bottom_image_layout = (LinearLayout)itemView.findViewById(R.id.bottom_image_layout);
		first_line = itemView.findViewById(R.id.first_line);
		expand_bootom_view = itemView.findViewById(R.id.expand_bootom_view);

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

		center_image_layout.setVisibility(View.VISIBLE);
		expand_control_layout.setVisibility(View.VISIBLE);

		if("B_IT".equals(info.contents.get(position).sectionContent.viewType)){
			bottom_image_layout.setVisibility(View.GONE);

			if(info.contents.get(position).sectionContent.productName == null || "".equals(info.contents.get(position).sectionContent.productName)){
				expand_control_layout.setVisibility(View.GONE);
			}

			if(item.size() >= 3){
				center_image_layout.setVisibility(View.VISIBLE);
			}else{
				center_image_layout.setVisibility(View.GONE);
			}
			if(expand_bootom_view != null) {
				expand_bootom_view.setVisibility(View.GONE);
			}
		}else if("B_DHS".equals(info.contents.get(position).sectionContent.viewType)){
			bottom_image_layout.setVisibility(View.GONE);
			center_image_layout.setVisibility(View.GONE);
			expand_control_layout.setVisibility(View.GONE);
			first_line.setVisibility(View.GONE);
			if(item.size() < 2){
				program_layout[1].setVisibility(View.GONE);
			}
			if(expand_bootom_view != null) {
				expand_bootom_view.setVisibility(View.VISIBLE);
			}
		}else{
			bottom_image_layout.setVisibility(View.VISIBLE);
			if(expand_bootom_view != null) {
				expand_bootom_view.setVisibility(View.GONE);
			}
		}

		program_text.setText(info.contents.get(position).sectionContent.productName);
		for(int i = 0 ; i < item.size() ; i ++){
			final int index = i;
			if(index >= program_Image.length){
				break;
			}
			ImageUtil.loadImageResize(context, item.get(index).imageUrl, program_Image[index], R.drawable.noimg_logo);
			program_Image[index].setOnClickListener(new View.OnClickListener() {
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
