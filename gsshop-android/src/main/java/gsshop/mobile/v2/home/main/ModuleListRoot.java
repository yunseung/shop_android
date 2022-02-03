package gsshop.mobile.v2.home.main;

import java.util.ArrayList;

/**
 * GS Choice 에서 최초 도입된 메인 화면 상품 모듈 리스트
 *
 */
public class ModuleListRoot {

    /**
     * 매장 전체 데이타는 ajaxPageUrl
     */
    public String ajaxPageUrl = null;

    /**
     * 카테고리, 더보기 데이타는 ajaxfullUrl
     */
    public String ajaxfullUrl = null;

    /**
     * 매장 전체 데이타는 moduleList
     */
    public ArrayList<ModuleList> moduleList = null;

    /**
     * 카테고리, 더보기 데이타는 productList
     */
    public ArrayList<ModuleList> productList = null;
}
