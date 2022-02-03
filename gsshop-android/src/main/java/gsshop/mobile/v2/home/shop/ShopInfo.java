/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop;

import com.google.android.exoplayer2.Player;

import java.util.ArrayList;
import java.util.List;

import gsshop.mobile.v2.home.TopSectionList;
import gsshop.mobile.v2.home.main.MobileLiveBanner;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.main.TvLiveBanner;
import gsshop.mobile.v2.home.main.TvLiveDealBanner;
import gsshop.mobile.v2.home.shop.schedule.model.SchedulePrd;

/**
 *
 *
 */
public class ShopInfo {

	public static final String TYPE_TODAY_OPEN = "FCLIST";
	public static final String TYPE_TV_SHOPPING = "TDCLIST";
	public static final String TYPE_EVENT = "EILIST";
	public static final String TYPE_VOD = "VODLIST";
	public static final String TYPE_BESTDEAL = "TCLIST";
	public static final String TYPE_FLEXIBLE = "FXCLIST";
	public static final String TYPE_BRAND = "BCLIST";
	public static final String TYPE_DEPARTMENT = "DFCLIST";
	public static final String TYPE_NALBANG = "NTFCLIST";
	public static final String TYPE_SHORTBANG = "STFCLIST";
	public static final String TYPE_TV_SCHEDULE = "SLIST";
	public static final String TYPE_GS_SUPER = "SUPLIST";
	public static final String TYPE_GS_CHOICE = "NFXCLIST";
	public static final String TYPE_L_EVENT = "EFXCLIST";
	public static final String TYPE_VIP = "VIPLIST";
	public static final String TYPE_SHOPPING_LIVE = "MLCLIST";
	public static final String TYPE_WINE = "WINELIST";
	public static final String TYPE_BEAUTY = "BTLIST";
	public static final String TYPE_TOMORROW_SELECT = "TSLIST";
	public static final String TYPE_GS_EXCLUSIVE = "EXCLIST"; 		// 기존 L타입 익스클루시브를 C타입으로 변경...

	public static final String NAVIGATION_BEST_NOW = "204";        // 지금 BEST 내비게이션 ID 해당 값 변경되지 않음 확인 후 해당 값으로 구분을 위해 사용
	public static final String NAVIGATION_TODAY_OPEN = "61";        // 오늘 오픈 내비게이션 ID 해당 값 변경되지 않음 확인 후 해당 값으로 구분을 위해 사용
	public static final String NAVIGATION_GS_SUPER = "481";			// GS Fresh
	public static final String NAVIGATION_GSX_BRAND = "210";		// GSX Brand
	public static final String NAVIGATION_HOME = "54";				// 홈매장
	public static final String NAVIGATION_SHOPPY_LIVE = "577";		// 샤피라이브 매장

	// 탭 위.
	public int tabIndex;

	// 숏방탭
	public int shortbangTabIndex;

	public TvLiveBanner tvLiveBanner; // TV생방송

	public TvLiveBanner dataLiveBanner; // 데이타방송

	public MobileLiveBanner mobileLiveBanner; // 모바일라이브

	public MobileLiveBanner mobileLiveDefaultBanner; // 모바일라이브 (방송안내 정보 표시)

	public TvLiveDealBanner tvLiveDealBanner; // 날방생방송

	public TopSectionList sectionList = null;

	public ArrayList<Integer> categoryIndex = new ArrayList<Integer>();

	/**
	 * 컨텐츠 리스트
	 */
	public List<ShopItem> contents;

	/**
	 * hash tag
	 */
	public int hashTagCount;
	public String hashTagName;


	public String ajaxPageUrl;

	/**
	 * navigation Id
	 */
	public String naviId;

	/**
	 * 카테고리 리스트(숏방)
	 */
	public ArrayList<SectionContentList> categoryContentList;

	public static class ShopItem {
		// 상품 타입.
		public int type;

		// icon tabbar indicator
		public int indicator = 0;

		// video player state
		public int playbackState = Player.STATE_IDLE;

		/**
		 * 컨텐츠 리스트
		 */
		public SectionContentList sectionContent;

		/**
		 * 카테고리 리스트(숏방)
		 */
		public ArrayList<SectionContentList> categoryContentList;

		/**
		 * 편성표 컨텐츠 리스트
		 */
		public SchedulePrd schedulePrd;

		/**
		 * 컨텐츠 리스트에서 해당 컨텐츠을 찾기 위한 고유키로 타임스탬프 사용
		 */
		public long timestamp = 0L;
	}

}
