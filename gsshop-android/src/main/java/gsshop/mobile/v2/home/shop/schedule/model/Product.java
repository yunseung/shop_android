/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.schedule.model;

import com.gsshop.mocha.pattern.mvc.Model;

import java.util.ArrayList;
import java.util.List;

import gsshop.mobile.v2.home.main.ImageBadge;
import gsshop.mobile.v2.home.shop.tvshoping.LiveSalesCountInfo;

/**
 * 상품정보
 */
@Model
public class Product {
	public String prdId;
	public String prdName;
	public String exposPrdName;
	public String subName; //편성표 추가될 #theme #theme..
	public String salePrice;
	public String broadPrice;
	public String exposePriceText; // default "원"
	public String priceMarkUp;  //"생방송/n모바일가",
	public String priceMarkUpType;  //: "LIVEPRICE",
	public ArrayList<String> rwImgList;
	public String mainProductYn;
	public String mainPrdImgUrl;
	public String subPrdImgUrl;
	public String insuYn;
	public String rentalYn;
	public String rentalText;  //: "",
	public String rentalPrice;  //: "",
	public String rentalEtcText;
	public String cellPhoneYn;
	public String linkUrl;
	public String playUrl;
	public DirectOrdInfo directOrdInfo;
	public LiveSalesCountInfo salesInfo;
	public String broadAlarmDoneYn;
	public String imageLayerFlag;
	public String pgmLiveYn;
	public String pgmId;
	public String saleEndFlag;
	public String softFlag;
	public String zeroFlagYn;
	public String prdClsCd;
	public String tempoutYn;
	public String onairSalePsblYn;

    /**
     * 재생버튼 노출여부에 사용 ("true" or "false" or null)
     */
	public String hasVod;

	public String videoTime; //내일TV 컨텐츠 러닝타임

	//renewal 때문에 추가
	public String badgeRTType;				// MORE : 더보기 표시 (선택시 linkUrl 로 이동), AD : '광고' 표시
	public String moreText;					// 가장 마지막 상품이 더보기 인지 여부 (API_SRL_PRD / PRD_C_SQ_PRD : 상품, API_SRL_MORE / PRD_C_SQ_MORE : 더보기)
	public List<ImageBadge> allBenefit;		//혜택을 리스트
	public ImageBadge source;				//소싱
	public String broadTimeText;			//방송 시간 표시
	public String addTextLeft;			//상품평 평점
	public String addTextRight;				//상품평 카운트
	public String brandNm;					//브렌드 네임

	public List<Product> subProductList;

	/**
	 * TV편성표 AB테스트
	 * 카테고리 필터링 기능때문에 추가
	 */
	public String cateNo; // 카테고리 번호
	public String cateNm; // 카테고리명


}
