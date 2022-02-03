package gsshop.mobile.v2.home.shop.flexible;

import com.gsshop.mocha.pattern.mvc.Model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.annotation.ParametersAreNullableByDefault;

/**
 * 서버 메뉴 구조 정보 viewType 확인 필요
 *
 * 현재는 맞출딜
 * 상품별 :  SUB_PRODUCTLIST
 * 카테고리별 : SUB_CATEGORYLIST
 *
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class SubMenuList {

    /* 대분류 매장 구분아이디 */
    public String sectionId = "";

    /**
     * 부모의 네비게이션 아이디
     */
    public String motherNavigationId = "";
    /**
     * : 네비게이션 아이디
     */
    public String navigationId = "";
    /**
     * CSLIST 등 부모의 ViewType
     */
    public String motherviewType = "";
    /**
     *  등 그룹탭 -> 매장탭 -> 서버탭 ex) SUB_상품LIST / SUB_CATEGORYLIST
     */
    public String viewType = "";
    /**
     * 서브 메뉴의 카테고리이름
     */
    public String sectionName = "";

    /**
     * API URL
     * ex) "http://m.gsshop.com/app/main/psnlrcmd",
     */
    public String sectionLinkUrl = "";
    /**
     * API URL 파라미터
     * ex)pageIdx=1&pageSize=400&version=1.1&naviId=87categoryList=1,2,3,4,5,6,내가선택한 카테고리 리스트
     */
    public String sectionLinkParams = "";
    /**
     * 와이즈 로그 URL
     * ex) http://m.gsshop.com/app/statistic/wiseLog?카테고리별
     */
    public String wiseLogUrl = "";

    /**
     * submenu 선택 아이콘 url
     */
    public String sectionImgOnUrl;

    /**
     * submenu 기본 아이콘 url
     */
    public String sectionImgOffUrl;
}