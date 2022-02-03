/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.flexible;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.recyclerview.widget.StaggeredGridLayoutManager.LayoutParams;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import gsshop.mobile.v2.AbstractBaseActivity;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.menu.BaseTabMenuActivity;
import gsshop.mobile.v2.web.WebUtils;
import gsshop.mobile.v2.web.handler.WebUrlHandler;
import gsshop.mobile.v2.web.handler.WebUrlHandlerManager;

/**
 * company footer
 *
 */
public class FlexibleBannerFooterViewHolder extends BaseViewHolder {

	// 이용약관
	private final TextView terms;
	// 개인정보 취급방침
	private final TextView privatePolicy;
	// 청소년 보호정책
	private final TextView teenPolicy;
	// 앱알림 설정
	private final TextView pushPolicy;
	// 콜센터
	private final LinearLayout txtCallCenter;
	// 사업자정보확인
	private final TextView txtTradeCommission;
	// 기업은행 채무지급 보증안내
	private final TextView txtDebtGuarantee;
	// 10x10
	private final ImageView ivFamily10;

	private final ImageView iv_family_npoint;

	/**
	 * @param itemView
	 */
	public FlexibleBannerFooterViewHolder(View itemView) {
		super(itemView);

		// 이용약관
		terms = (TextView) itemView.findViewById(R.id.txt_bottom_terms);

		// 개인정보 취급방침
		privatePolicy = (TextView) itemView.findViewById(R.id.txt_bottom_private);

		// 청소년 보호정책
		teenPolicy = (TextView) itemView.findViewById(R.id.txt_bottom_teen);

		// 앱알림 설정
		pushPolicy = (TextView) itemView.findViewById(R.id.txt_bottom_push);

		// 콜센터
		txtCallCenter = (LinearLayout) itemView.findViewById(R.id.txt_bottom_call_center);

		// 사업자정보확인
		txtTradeCommission = (TextView) itemView.findViewById(R.id.txt_bottom_trade_commission);

		// 기업은행 채무지급 보증안내
		txtDebtGuarantee = (TextView) itemView.findViewById(R.id.txt_bottom_debt_guarantee);

		//10x10
		ivFamily10 = (ImageView) itemView.findViewById(R.id.iv_family_10);

		//gs 포인트
		iv_family_npoint = (ImageView) itemView.findViewById(R.id.iv_family_npoint);
	}

	// 하단 풋터.
	@Override
	public void onBindViewHolder(final Context context, int position, ShopInfo info, String action, String label, String sectionName) {
		if(info != null) {
			LayoutParams params = (LayoutParams) itemView.getLayoutParams();
			if (info.sectionList.viewType.equals(ShopInfo.TYPE_EVENT)) {
				// 이벤트 메뉴.
				if (!params.isFullSpan()) {
					params.setFullSpan(true);
				}
			} else {
				if (params.isFullSpan()) {
					params.setFullSpan(false);
				}
			}
		}

		// 이용약관
		terms.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				WebUtils.goWeb(context, ServerUrls.WEB.HOME_FOOTER_MANUAL,
						((Activity) context).getIntent());
			}
		});

		// 개인정보 취급방침
		privatePolicy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//원본소스
				/*WebUtils.goWeb(context, ServerUrls.WEB.HOME_FOOTER_MANUAL_PRIVATE,
						((Activity) context).getIntent());*/

				//fullModalWebView적용
				String testUrl = "toapp://notabfullwebview?targetUrl="+ServerUrls.WEB.HOME_FOOTER_MANUAL_PRIVATE;
				WebUrlHandler h = WebUrlHandlerManager.findCustomUrlHandler(testUrl);
				if(h!=null){
					h.handle((Activity)context, null, testUrl);
				}

			}
		});

		// 청소년 보호정책
		teenPolicy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//원본소스
				/*WebUtils.goWeb(context, ServerUrls.WEB.HOME_FOOTER_MANUAL_TEEN,
						((Activity) context).getIntent());*/

				//fullModalWebView적용
				String testUrl = "toapp://notabfullwebview?targetUrl="+ServerUrls.WEB.HOME_FOOTER_MANUAL_TEEN;
				WebUrlHandler h = WebUrlHandlerManager.findCustomUrlHandler(testUrl);
				if(h!=null){
					h.handle((Activity)context, null, testUrl);
				}
			}
		});

		// 앱알림 설정
		pushPolicy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 설정화면으로 이동
				((BaseTabMenuActivity) context).goSetting();
			}
		});

		// 사업자정보확인
		txtTradeCommission.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				WebUtils.goWeb(context, ServerUrls.WEB.TRADE_COMMISSION_INFO,
						((Activity) context).getIntent());
			}
		});

		// 기업은행 채무지급 보증안내
		txtDebtGuarantee.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				WebUtils.goWeb(context, ServerUrls.WEB.DEBT_GUARANTEE_INFO,
						((Activity) context).getIntent());
			}
		});

		//고객센터
		txtCallCenter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("tel:18994455"));
				((Activity) context).startActivity(intent);
				//와이즈로그 전송
				((AbstractBaseActivity)context).setWiseLogHttpClient(ServerUrls.WEB.FOOTER_TEL);
			}
		});

		//10x10
		ivFamily10.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ServerUrls.WEB.FAMILY_10));
				((Activity) context).startActivity(intent);
			}
		});

		//iv_family_npoint
		iv_family_npoint.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ServerUrls.WEB.FAMILY_NPOINT));
				((Activity) context).startActivity(intent);
			}
		});
	}

}
