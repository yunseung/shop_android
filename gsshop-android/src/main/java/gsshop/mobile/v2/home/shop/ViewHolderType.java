/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop;

/**
 *
 *
 */
public interface ViewHolderType {

	// flexible shop banner type constants
	int BANNER_TYPE_NONE = -1;
	// 띠배너.
	int BANNER_TYPE_BAND = 0;
	// 이미지 배너.
	int BANNER_TYPE_IMAGE = 1;
	// 단품 배너.
	int VIEW_TYPE_L = 2;
	// 타이틀 배너.
	int BANNER_TYPE_TITLE = 3;
	// 카테고리 배너.
	int VIEW_TYPE_FXCLIST = 4;
	// 핫 배너.
	int VIEW_TYPE_B_MIA = 5;
	// 롤링 이미지 배너.
	int VIEW_TYPE_B_SIC = 6;
	// 롤링 이미지 배너 - 왼쪽/오른쪽 preview 포함.
	int VIEW_TYPE_SE = 15;
	// todays hot 배너.
	int VIEW_TYPE_SL = 7;
	// 풋터 배너.
	int BANNER_TYPE_FOOTER = 9;
	// tv live banner
	int BANNER_TYPE_TV_LIVE = 10;
	// tv live dual banner
	int BANNER_TYPE_TV_LIVE_DUAL = 101;
	// no1 deal banner
	int VIEW_TYPE_DSL_X = 12;
	// theme deal banner
	int VIEW_TYPE_TSL = 13;
	// event category
	int VIEW_TYPE_BAN_CX_CATE_GBA = 14;
	// best deal category
	int VIEW_TYPE_SUB_SEC_LINE = 16;

	// rolling 배너에서 이미지가 1개인 case
	int BANNER_TYPE_ROLL_ONE_IMAGE = 11;

	int BANNER_TYPE_BRAND_CATEGORY = 17;

	int BANNER_TYPE_BRAND_CONTENT= 18;

	// 롤링 이미지 배너(브랜드관).
	int BANNER_TYPE_BRAND_ROLL_IMAGE = 19;

	int BANNER_TYPE_BRAND_DIVIDER = 20;

	// 동영상 뷰타입
	int VIEW_TYPE_ML = 21;

	// 추천딜 뷰타입
	int VIEW_TYPE_SRL = 22;

	// TV 방송배너 뷰타입
	int BANNER_TYPE_TV_ITEM = 23;

	// TV 방송배너 뷰타입
	int VIEW_TYPE_B_HIM = 24;
	// TV 방송배너 뷰타입
	int VIEW_TYPE_PC = 25;
	// TV 방송배너 뷰타입
	int VIEW_TYPE_SPL = 26;
	// TV 방송배너 뷰타입
	int VIEW_TYPE_SPC = 27;

	// 마트장보기 카테고리 배너.
	int VIEW_TYPE_SUB_PRD_LIST_TEXT = 28;
	// 마트장보기 빠른장보기
	int VIEW_TYPE_B_IG4XN = 29;

	int VIEW_TYPE_BTL = 30;

	int VIEW_TYPE_TAB_SL = 31;

	// 타이틀 배너.
	int BANNER_MART_TYPE_TITLE = 32;

	//TV쇼핑 카테고리 탭
	int VIEW_TYPE_TCF = 33;
	/**
	 * best shop
	 */
	// 베스트 샵 타이틀 베너
	int VIEW_TYPE_B_TS2 = 35;
	// 플로팅 셀타입 카테고리
	int VIEW_TYPE_FPC_S = 36;
	// 단품 2개
	int VIEW_TYPE_BFP = 37;
	int VIEW_TYPE_BAN_IMG_C2_GBB = 83;
	int VIEW_TYPE_MAP_CX_GBB = 88;
	// 실시간 인기 검색어
	int VIEW_TYPE_RPS = 38;


	/**
	 * 기획전
	 */
	// 기획전 1 - 빅브랜드위크
	int VIEW_TYPE_MAP_SLD_C3_GBA = 39;
	// 기획전 2 - 주차별 테마에 따른 기획전 (배너 + 상품1개 조합)
	int VIEW_TYPE_BP_O = 40;
	// 기획전 4 - 모바일 전용 기획전
	int VIEW_TYPE_TP_S = 41;
	// 플렉서블 E
	int VIEW_TYPE_TP_SA = 61;

	int VIEW_TYPE_PDV = 42;

	int VIEW_TYPE_B_TSC = 44;

	int VIEW_TYPE_B_IT = 45;

	/**
	 * 날방
	 */
	// 날방 live banner
	int BANNER_TYPE_NALBANG_LIVE = 46;
	// 이미지 배너. (상하좌우 여백 포함)
	int VIEW_TYPE_B_ISS = 47;
	// 날방 해시태그 tab 배너.
	int BANNER_TYPE_NALBANG_HASH_TAG_TAB = 48;
	// nalbang product banner
	int BANNER_TYPE_NALBANG_PRD = 49;

	/**
	 * 숏방 상품
	 */
	int VIEW_TYPE_SBT = 50;

	/**
	 * 숏방 롤링 배너
	 */
	int VIEW_TYPE_SSL = 51;

	// 숏방 tab 배너.
	int BANNER_TYPE_SHORTBANG_HASH_TAG_TAB = 52;

	// 숏방 더보기 버튼
	int BANNER_TYPE_SHORTBANG_READ_MORE = 53;

	// 날방 더보기 버튼
	int BANNER_TYPE_NALBANG_READ_MORE = 54;

	// 날방 더보기 버튼
	int BANNER_TYPE_SHORTBANG_EMPTY = 55;

	/**
	 * 숏방 배너
	 */
	int BANNER_TYPE_SHORTBANG_BANNER = 56;

	// TEXT 배너.
	int VIEW_TYPE_B_CM = 57;

	/**
	 * new rolling image
	 */
	int BANNER_TYPE_ROLL_IMAGE_NEW_GBA = 58;

	// 롤링 이미지 배너.
	int VIEW_TYPE_BAN_SLD_GBB = 60;


	/**
	 * ai shop
	 */
	// 타이틀 배너
	int VIEW_TYPE_BAN_MUT_H55_GBA = 62;
	// 단품
	int VIEW_TYPE_BAN_IMG_SQUARE_GBA = 63;
	int VIEW_TYPE_MAP_MUT_CATEGORY_GBA = 64;

	// 띠배너 아래에 공백없음.
	int BANNER_TYPE_BAND_NO_PADDING = 65;

	// DSL_A2
	int VIEW_TYPE_DSL_A2 = 66;

	// 오늘 추천 ad 구좌 BAN_SLD_GBC
	int VIEW_TYPE_BAN_SLD_GBC = 67;

	// MAP_CX_GBA gs x brand 배너
	int VIEW_TYPE_MAP_CX_GBA = 68;

	int BANNER_TYPE_TODAYOPEN_AD = 69;

	int VIEW_TYPE_FPC_P = 70;

	int VIEW_TYPE_BAN_TXT_NODATA = 71;

	// 백화점탭 => B_DH
	int VIEW_TYPE_B_DHS = 72;

	// TV쇼핑 2단뷰
	int VIEW_TYPE_BAN_IMG_C2_GBA = 73;

	// 적립금 안내
	int VIEW_TYPE_BAN_TXT_IMG_COLOR_GBA = 74;

	// TV쇼핑 테마키워드
	int VIEW_TYPE_BAN_IMG_C3_GBA = 75;

	// gs x brand category
	int BAN_CX_SLD_CATE_GBA = 78;

	// 적립금 안내 (API 호출용)
	int VIEW_TYPE_API_LOGIN_BAN_TXT_IMG_COLOR_GBA = 79;

	// best deal catetory (API 호출용)
	int VIEW_TYPE_API_SUB_SEC_LINE = 80;

	// 추천딜 뷰타입 (API 호출용)
	int VIEW_TYPE_API_SRL = 81;

	// 카드 할인 안내
	int VIEW_TYPE_MAP_CX_TXT_GBA = 82;

	// 지금 best Checkbox 배너
	int VIEW_TYPE_BAN_TXT_CHK_GBA = 84;

	// 배너 (CSP 호출용)
	int VIEW_TYPE_CSP_LOGIN_BAN_IMG_GBA = 85;

	// GS Super 용 타이틀 바 (당일배송)
	int VIEW_TYPE_BAN_IMG_GSF_GBA = 86;

	// GS Super 용 배송위치 변경 배너
	int VIEW_TYPE_BAN_GSF_LOC_GBA = 87;

	// GS Super 용 자동 이미지 롤링
	int BANNER_TYPE_GS_SUPER_SLD_GBD = 89;

	// GS Super 용 Toast 팝업
	int TOAST_TYPE_GS_SUPER_TIME = 90;

    // GS Super 용 자동 롤링 이미지 배너
    int BANNER_TYPE_GS_SUPER_ROLL_MENU = 91;

    // best deal live dual banner (horizontal)
	int BANNER_TYPE_TV_LIVE_A = 92;

	// best deal live dual banner (vertical)
	int BANNER_TYPE_TV_LIVE_B = 93;

	// vod horizontal
	int VIEW_TYPE_BAN_VOD_GBA = 94;

	// vod vertical
	int VIEW_TYPE_BAN_VOD_GBB = 95;

	// vod header (인기순, 추천순 정렬)
	int BANNER_TYPE_VOD_ORD = 96;

	// void img sliding banner (핑퐁)
	int VIEW_TYPE_BAN_IMG_SLD_GBA = 97;

	// vod 여백
	int VIEW_TYPE_BAN_ORD_GBA_SPACE = 98;

	// vod square
	int VIEW_TYPE_BAN_VOD_GBC = 99;

	// 인기 검색어 배너
	int VIEW_TYPE_BAN_TXT_EXP_GBA = 100;

	// GS Super 용 타이틀 바 (당일배송+새벽)
	int VIEW_TYPE_BAN_IMG_GSF_GBB = 108;

	// GS Super 용 타이틀 바 (새벽배송+당일)
	int VIEW_TYPE_BAN_IMG_GSF_GBC = 109;

	// GS Super 탭메뉴 용 (해당 뷰타입들 중 첫번째 것)
	int VIEW_TYPE_MAP_CX_GBB_TAB = 110;

	// GS Super 용 타이틀 바 (택배)
	int VIEW_TYPE_BAN_IMG_GSF_GBD = 111;

	// GS Super 용 타이틀 바 (택배 + 새벽)
	int VIEW_TYPE_BAN_IMG_GSF_GBE = 112;

	// 모바일라이브
	int BANNER_TYPE_MOBILE_LIVE = 119;

	int VIEW_TYPE_MAP_CX_TXT_GBB = 123;

	// 내일 TV 카드 할인 정보 뷰홀더.
	int BANNER_TYPE_VOD_CARD_POPUP = 124;

	// 홈 화면 서비스 매장 바로가기.
	int VIEW_TYPE_BAN_IMG_C5_GBA = 125;

	// 타이틀 배너2 (B_TS 에서 변형).
	int VIEW_TYPE_BAN_TXT_IMG_LNK_GBB = 134;

	// GS X Brand 개인화 탭. 배너 + 2단 상품 있는 ViewType 2020-06-16 추가
	int VIEW_TYPE_GR_PMO_T2 = 139;
	int VIEW_TYPE_PMO_T2_A = 140;
	int VIEW_TYPE_GR_PMO_T2_MORE = 141;

	// 브랜드샵 개인화 매장의 브랜드샵 바로가기 배너버튼
	int VIEW_TYPE_BAN_MORE_GBA = 142;
	// 브랜드샵 개인화 매장의 550 캐로셀 영역
	int VIEW_TYPE_PRD_PAS_SQ = 143;
	// 브랜드샵 상품이 없을때 보여지는 영역
	int VIEW_TYPE_BAN_NO_PRD = 144;

	// 2열 타이틀.
	int VIEW_TYPE_BAN_TXT_CST_GBA = 149;

	/**
	 * VIP 매장
	 */

	// VIP 매장 웰컴 메세지
	int VIEW_TYPE_BAN_VIP_GBA = 151;

	// VIP DAY 배너 (이미지 배너)
	int VIEW_TYPE_BAN_VIP_IMG_GBA = 152;

	// VIP 쇼핑 혜택 배너
	int VIEW_TYPE_BAN_VIP_BENE_GBA = 153;

	// VIP 쇼핑 카드 할인 정보
	int VIEW_TYPE_BAN_VIP_CARD_GBA = 154;

	// 개인화 아이템 찜한 상품, 매직딜데이 그룹
	int VIEW_TYPE_GR_PRD_1_VIP_LIST_GBA = 155;

	// 최근 검색어 추천 상품, 선호 브랜드 인기 상품 그룹 (2단 뷰 4개 상품, 이동형)
	int VIEW_TYPE_GR_PRD_2_VIP_LIST_GBA = 156;

	// 최근 검색어 추천 상품, 선호 브랜드 인기 상품 그룹 (2단 뷰 4개 상품, 갱신형)
	int VIEW_TYPE_GR_PRD_2_VIP_LIST_GBB = 157;

	// 재구매 상품 / 관련 추천 상품 타이틀 + 부상품 UI
	int VIEW_TYPE_GR_PRD_1_VIP_LIST_GBB = 158;

	// 찜한 브랜드 소식 VIP 타이틀을 가지고 있는 C_B1 형태
	int VIEW_TYPE_PRD_C_B1_VIP_GBA = 159;

	// 쇼핑라이브 매장 타이틀
	int VIEW_TYPE_TITLE_MOBILE_LIVE = 160;

	// 쇼핑라이브 오늘의 라이브
	int VIEW_TYPE_PRD_MOBILE_LIVE = 161;

	// 쇼핑라이브 방송알림
	int VIEW_TYPE_PRD_ALARM_MOBILE_LIVE = 162;

	// 쇼핑라이브 라이브 예고
	int VIEW_TYPE_NOTICED_MOBILE_LIVE = 163;

	// 쇼핑라이브 시청 베스트 10
	int VIEW_TYPE_BEST_MOBILE_LIVE = 164;

	// 쇼핑라이브 라이브 모아보기 카테고리
	int VIEW_TYPE_TITLE_ADDED_SUB_MOBILE_LIVE =165;

	// 쇼핑라이브 2단 상품
	int VIEW_TYPE_PRD_2_MOBILE_LIVE = 166;

	// 와인 타이틀
	int VIEW_TYPE_BAN_TITLE_WINE = 167;

	// 와인 매장 이미지 슬라이더
	int VIEW_TYPE_BAN_SLD_WINE = 168;

	// 와인 매장 메뉴 슬라이더
	int VIEW_TYPE_MENU_SLD_WINE = 169;

	// 와인 매장 PMO 스타일 리스트
	int VIEW_TYPE_PRD_PMO_LIST_WINE =170;

	// 와인 매장 C_SQ 스타일 리스트
	int VIEW_TYPE_PRD_C_SQ_LIST_WINE = 171;

	// 와인 매장 검색어 형태 배너
	int VIEW_TYPE_BAN_TXT_EXP_WINE = 172;

	// 와인매장 앵커 형태 탭
	int VIEW_TYPE_TAB_ANCH_WINE = 173;
	int VIEW_TYPE_TAB_ANCH_WINE_TOP = 174;

	// 와인매장 2단뷰
	int VIEW_TYPE_PRD_2_WINE = 175;

	// GS Retail 브랜드 구좌
	int VIEW_TYPE_PMO_GSR_C_GBA = 176;

	// 와인 매장 더보기
	int VIEW_TYPE_BAN_VIEW_MORE_WINE = 177;

	// VIP 찜상품
	int VIEW_TYPE_GR_PRD_1_VIP_LIST_GBC = 178;

	// 쇼핑라이브 현재 방송중 상품 구좌
	int VIEW_TYPE_BAN_SLD_MOBILE_LIVE_BROAD = 179;

	// 쇼핑라이브 미니 롤링 배너.
	int VIEW_TYPE_BAN_SLD_MOBILE_LIVE_SMALL = 180;

	// 뷰티 매장 640 롤링 배너
	int VIEW_TYPE_BAN_BEAUTY_SLD = 181;

	// 뷰티 매장 타임세일 좌우 이동 배너
	int VIEW_TYPE_BAN_BEAUTY_SLD_TIME_SALE = 182;

	// 뷰티 매장 필터 2개
	int VIEW_TYPE_TAB_BEAUTY_MENU_BEST = 183;

	// 뷰티 매장 2단 뷰 (BEST)
	int VIEW_TYPE_PRD_2_BEAUTY_BEST = 184;

	// 뷰티 매장 더보기 뷰
	int VIEW_TYPE_BAN_VIEW_MORE_BEAUTY = 185;

	// 뷰티 매장 1단 탭 뷰
	int VIEW_TYPE_TAB_BEAUTY_MENU = 186;

	// 뷰티 매장 좌우 롤링 배너 (우측 아이템 노출)
	int VIEW_TYPE_BAN_BEAUTY_SLD_TODAY = 187;

	// 뷰티 매장 하단 2단짜리 브랜드 좌우 이동 배너
	int VIEW_TYPE_GR_BEAUTY_SLD_BRAND = 188;

	// 뷰티 매장 카테고리 그리드형 배너
	int VIEW_TYPE_GR_BEAUTY_CATE = 189;

	// 홈 매장 카테고리 게이트
	int VIEW_TYPE_GR_HOME_CATE_TAB_GATE = 190;

	// 공통 매장 새로운 타이틀 유형.
	int VIEW_TYPE_TITLE_IMG_TXT_WITH_SEPERATOR = 191;

	int VIEW_TYPE_PRD_BEAUTY_SLD_WEEKLY_EVENT = 192;

	// 뷰티 매장 2단 뷰 (내일도착)
	int VIEW_TYPE_PRD_2_BEAUTY_TOMM = 193;

	// no data
	int VIEW_TYPE_NO_DATA_BEST = 194;
	int VIEW_TYPE_NO_DATA_TOMM = 195;

	// 뷰티 매장 내일도착 이미지 배너
	int VIEW_TYPE_BAN_IMG_BEAUTY_TOMM = 196;

	// 모바일라이브 탭
	int VIEW_TYPE_TAB_MOBILE_LIVE_MENU = 197;

	// 모바일라이브 NO DATA
	int VIEW_TYPE_NO_DATA_MOBILE_LIVE = 198;

	// 홈 매장 퀵메뉴
	int VIEW_TYPE_MENU_GIFT_RECOMMEND_FIX = 199;

	/********************** 오늘의 선택 **********************/
	// 라운드 들어간 작은 크기의 슬라이드 리스트
	int VIEW_TYPE_BAN_SLD_ROUNDED_SMALL = 200;

	// 라운드 들어간 큰 크기의 정사각 슬라이드 리스트
	int VIEW_TYPE_BAN_SLD_ROUNDED_BIG = 201;

	// 그룹 (탭 이미지, 2단뷰로 이루어진 오늘의 선택용 그룹
	int VIEW_TYPE_GR_TAB_SEL_TODAY = 202;

	// 오늘의 선택 용 타이틀
	int VIEW_TYPE_BAN_TS_TXT_SUB_GBA = 203;

	// 오늘의 선택하면서 추가 했지만 혹시 다른데에서 또 쓸거 같아 공통에 추가 (라운드 들어간 이미지)
	int VIEW_TYPE_BAN_IMG_ROUNDED = 204;

	// 오늘의 선택, 리사이클러 그룹
	int VIEW_TYPE_GR_TAB_TODAY_SEL = 205;

	/******************** GS Exclusive **********************/
	// 빅배너 자동롤링
	int VIEW_TYPE_BAN_SLD_BIG = 206;

	// 기존 L타입 그룹형 GR_BRD_GBA 의 C타입 형태
	int VIEW_TYPE_GR_BRD_GBA_FXC = 207;

	// 2단 탭
	int VIEW_TYPE_BAN_TAB_SEL = 208;

	// 기존 L타입 좌우 자유 이동형 BAN_SLD_GBG 의 C 타입 형태
	int VIEW_TYPE_BAN_SLD_GBG_FXC = 209;



	/**********************************
	 *		메인개편 (1000번대)
	 **********************************/
	int VIEW_TYPE_PRD_1_640 = 1001;
	int VIEW_TYPE_PRD_2 = 1002;

	// best deal live
	int BANNER_TYPE_HOME_TV_LIVE = 1003;

	// best deal data
	int BANNER_TYPE_HOME_TV_DATA = 1004;

	// 내일TV
	int VIEW_TYPE_BRD_VOD = 1005;

	/**
	 * 프로모션 컴포넌트 TIER 1
	 */
	int VIEW_TYPE_PMO_T1_PREVIEW_D = 1006;
	int VIEW_TYPE_PMO_T1_PREVIEW_B = 1007;
	int VIEW_TYPE_PMO_T1_IMG = 1008;

	 // VIEW_TYPE_API_SRL 랑 같은 상품 캐러셀
	int VIEW_TYPE_PRD_C_SQ = 1009;

	/**
	 * 가로 캐로셀. (PRD_C_B1)
	 */
	int VIEW_TYPE_PRD_C_B1 = 1010;

	/**
	 * T2_PREVIEW => 위에 이미지 아래 작은 캐로셀
	 * T2_IMAGE => 위에 이미지만 있는 애
	 * *** 같은 뷰홀더(PmoT2xxxVH.java)를 쓴다 ***
	 */
	int VIEW_TYPE_PMO_T2_PREVIEW = 1011;
	int VIEW_TYPE_PMO_T2_IMG = 1012;
	int BANNER_TYPE_RENEWAL_PMO_T3_IMAGE = 1013;
	int VIEW_TYPE_PMO_T2_IMG_C = 1014;

	// 메인개편 정사각
	int VIEW_TYPE_PRD_1_550 = 1015;

	/**
	 * 홈 T3 이미지 AB테스트
	 */
	int BANNER_TYPE_RENEWAL_PMO_T3_IMAGE_AB = 1019;

	/**
	 * 홈 하단 개인화구좌 UI AB test를 위한
	 * 550기반의 상품 컴포넌트 추가 (편성표 부상품 UI와 동일)
	 */
	int VIEW_TYPE_PRD_1_LIST = 1020;

	// 2열 타이틀 C_SQ
	int VIEW_TYPE_PRD_C_CST_SQ = 1025;

	// 실시간 스트리밍 C_SQ
	int VIEW_TYPE_RTS_PRD_C_CST_SQ = 1028;

	// 몰로코 이런상품어떠세요
	int VIEW_TYPE_MOLOCO_PRD_C_SQ = 1027;













	/**************************************************************************************
	 * L타입 (C타입 기존 100번대와 개편후 1000번대 혼용사용으로 혼동 위험 있어 2000번대로 변경)
	 *  - C타입에도 있는 타입 재외 -
	 **************************************************************************************/
	// 플렉서블 배너 GS Choice
	int VIEW_TYPE_BAN_IMG_H000_GBC = 2001;

	//하단 여백있는 배너
	int VIEW_TYPE_BAN_IMG_H000_GBD = 2002;

	// rolling image banner (큐레이션 모듈 type1)
	int VIEW_TYPE_BAN_SLD_GBE = 2003;

	// rolling image banner (큐레이션 모듈 type2)
	int VIEW_TYPE_BAN_SLD_GBF = 2004;

	// 시그니처 매장 가로 캐로셀
	int VIEW_TYPE_BAN_SLD_GBG = 2005;

	/**
	 * 시그니처매장 상품 뷰홀더
	 */
	int VIEW_TYPE_GR_BRD_GBA = 2006;

	/**
	 * 시그니처매장 브랜드 더보기 뷰홀더
	 */
	int VIEW_TYPE_BAN_API_MORE_GBA = 2007;

	// gs choice category
	int VIEW_TYPE_TAB_SLD_GBA = 2008;

	// 내일도착 (새벽배송) Sticky header (GS Fresh 형태)
	int VIEW_TYPE_TAB_SLD_GBB = 2009;

	// 상품배너 (정사각 상품)
	int VIEW_TYPE_BAN_MUT_GBA = 2010;

	// 내일도착 새벽배송 gateway 타이틀 링크 텍스트..?
	int VIEW_TYPE_BAN_TXT_IMG_LNK_GBA = 2011;

	// 데이타 없음 표시용
	int BANNER_TYPE_NO_DATA = 2012;

	/**
	 * 새벽 배송 이미지 중앙 / 왼쪽 / 오른쪽 정렬
	 */
	int BANNER_TYPE_FIXED_80_IMG_C_GBA = 2013;
	int BANNER_TYPE_FIXED_80_IMG_L_GBA = 2014;
	int BANNER_TYPE_FIXED_80_IMG_R_GBA = 2015;

	// 내일도착 (새벽배송) Sub product list
	int VIEW_TYPE_MAP_CX_GBC = 2016;

	/********************
	 * GS 혜택 뷰타입 5개
	 ********************/

	// GS 혜택 롤링 이미지 (텍스트) 영역
	int VIEW_TYPE_TXT_IMG_SLD_GBA = 2017;

	// GS 혜택 개편 헤더 일반 영역
	int VIEW_TYPE_TAB_ANK_GBA = 2018;

	// GS 혜택 개편 헤더 첫번째 영역
	int VIEW_TYPE_TAB_ANK_GBA_1ST = 2019;

	// GS 혜택 카테고리 이미지
	int VIEW_TYPE_IMG_CX_GBA = 2020;

	// GS 혜택 일반 이미지
	int VIEW_TYPE_IMG_TXT_GBA = 2021;

	/*************
	 * GS 혜택 끝
	 *************/

	// 새벽배송 매장 새로운 앵커 (좌우 플리킹 가능, 최상단 한 개만 존재하는 앵커)
	int VIEW_TYPE_TAB_SLD_ANK_GBA = 2022;

	// 새벽배송 매장 변경, 더보기를 그려주는 것이 아닌 새로 추가 하는 로직.
	int VIEW_TYPE_SLD_GBB_MORE = 2023;

	// 내일도착 앵커 형태.
	int VIEW_TYPE_TAB_SLD_ANK_GBB = 2024;

	// 앵커형 이미지 아이템 배너
	int VIEW_TYPE_TAB_IMG_ANCH_GBA = 2025;

	/**
	 * tv 신규 매장 뷰타입 3개
	 * TXT_SUB_GBA => 타이틀
	 * PRD_VOD_LIST => VOD 가 들어올 수 있는 640 상품 타입
	 * PRD_MLT_GBA => 3열 grid 및 더보기
	 * VIEW_TYPE_PRD_NO_VOD_LIST => 21.01.21 배포할 모바일라이브 임시매장 타입
	 */
	int VIEW_TYPE_BAN_TXT_SUB_GBA = 2026;
	int VIEW_TYPE_PRD_VOD_LIST = 2027;
	int VIEW_TYPE_PRD_MLT_GBA = 2028;

	// L타입 상품 뷰 상단에 포함 될 타이틀
	int VIEW_TYPE_BAN_TITLE_PRD_GBA = 2029;

	/**
	 * 홈 이미지 통합배너(indicator있는 롤링이미지배너)
	 */
	int BANNER_TYPE_ROLL_IMAGE_NEW_GBH = 1029;

}
