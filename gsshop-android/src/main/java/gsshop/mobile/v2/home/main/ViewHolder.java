/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.main;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Home{@literal &}그룹매장에서 사용하는 공통 ViewHolder
 *
 */
public class ViewHolder {
	public TextView expand_control_close;
	public ImageView arrow;
	public Button btn_no1;
	public LinearLayout rowGoods;
	public TextView txtSubtitle;
	public TextView txtTitle;
	public TextView txtPrice;
	public TextView txtPriceWon;
	public TextView txtBasePrice;
	public LinearLayout layoutBasePrice;
	public TextView txtBasePriceWon;
	public TextView txtSalesQuantity;
	public TextView txtSalesQuantityStr;
	public TextView txtSalesQuantitySubStr;
	public LinearLayout layoutSalesQuantity;
	public TextView txtBadge;
	public TextView txtBadgePer;
	public LinearLayout layoutBadge;
	public View btnPlay;
	public ImageView mainImg;
	public TextView layoutSoldout;
	public RelativeLayout layoutBottom;
	public LinearLayout layoutBottomDetail;
	public LinearLayout layoutPromotionName;
	public View bottomViewLine;
	public View[] page_items = new View[10];
	public LinearLayout tv_data_category;
	public TextView promotionName;
	public TextView txt_badge_per;
	public TextView valueInfo;
	public TextView txt_sales_quantity;
	public ImageView[] footer_badge;
	public ImageView delivery_image;
	public ImageView top_badge;
	public View main_layout;
	public View layout_bottom;
	public View promotion_layout;
	public View line_top;
	public View line_bottom;
	public TextView txt_base_price;
	public TextView txt_price_unit;
}
