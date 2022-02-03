package gsshop.mobile.v2.support.gtm;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import gsshop.mobile.v2.MainApplication;
import roboguice.util.Ln;

/**
 * GTM 컨테이너와 통신을 위한 클래스
 */
public class GTMAction {
    private static final String KEY_EVENT = "event";
    private static final String KEY_ITEM = "products";
    private static final String KEY_ACTION_FIELD = "actionField";

    private static final String PURCHASE_INFO_ID = "id";
    private static final String PURCHASE_INFO_AFFILIATION = "affiliation";
    private static final String PURCHASE_INFO_REVENUE = "revenue";
    private static final String PURCHASE_INFO_TAX = "tax";
    private static final String PURCHASE_INFO_SHIPPING = "shipping";
    private static final String PURCHASE_INFO_COUPON = "coupon";

    private static final String PURCHASE_PRODUCT_ID = "id";
    private static final String PURCHASE_PRODUCT_ECOMMERCE = "ecommerce";
    private static final String PURCHASE_PRODUCT_NAME = "name";
    private static final String PURCHASE_PRODUCT_PRICE = "price";
    private static final String PURCHASE_PRODUCT_BRAND = "brand";
    private static final String PURCHASE_PRODUCT_CATEGORY = "category";
    private static final String PURCHASE_PRODUCT_VARIANT = "variant";
    private static final String PURCHASE_PRODUCT_QUANTITY = "quantity";
    private static final String PURCHASE_PRODUCT_COUPON = "coupon";

    /**
     * 컨테이너 데이타영역 변수에 값을 할당한다.
     * 
     * @param context 컨텍스트
     * @param key 데이타영역 변수명
     * @param value 변수에 할당할 값
     */
    public static void pushToDatalayer(Context context, String key, String value){
        Bundle parmas = new Bundle();
        parmas.putString(key, value);
        FirebaseAnalytics.getInstance(context).logEvent(KEY_EVENT, parmas);

//        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
//        dataLayer.push(key, value);
    }
    
    /**
     * 컨테이너 데이타영역 변수로 부터 값을 취득한다.
     * 
     * @param context 컨텍스트
     * @param key 데이타영역 변수명
     * @return 데이타영역 변수에 할당된 값
     */
    public static String getFromDatalayer(Context context, String key){
        // 부르는 곳이 없음 (java script에서 호출하는 부분도 아님)
        return "";
        /*
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        Object ret = dataLayer.get(key);
        if (ret == null) {
        	return "";
        } else {
        	return ret.toString();
        }
        */
    }

    /**
     * 전자상거래 정보(구매)를 로깅한다.
     * 
     * @param context 컨텍스트
     * @param action 구매정보 (거래번호, 세금, 배송비 등)
     * @param products 상품정보 (상품명, 가격, 브랜드 등)
     * @param screenName 스크린이름 (베스트딜, 오늘오픈, 카테고리 등)
     */
    public static void sendPurchases(Context context, String action, String products, String screenName){
//        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();

		ObjectMapper mapper = new ObjectMapper();
		//actionField 저장변수
        Map<String, Object> actionMap = new HashMap<String, Object>();
        //products 저장변수
        ArrayList<Map<String, Object>> productsList = new ArrayList<Map<String, Object>>();
        
		try {
			//json to map
			actionMap = mapper.readValue(action, new TypeReference<HashMap<String, Object>>() {});

            Bundle ecommerceBundle = new Bundle();

            Bundle params = new Bundle();
            params.putString(PURCHASE_INFO_ID, actionMap.get(PURCHASE_INFO_ID).toString());
            params.putString(PURCHASE_INFO_AFFILIATION, actionMap.get(PURCHASE_INFO_AFFILIATION).toString());
            params.putString(PURCHASE_INFO_REVENUE, actionMap.get(PURCHASE_INFO_REVENUE).toString());
            params.putString(PURCHASE_INFO_TAX, actionMap.get(PURCHASE_INFO_TAX).toString());
            params.putString(PURCHASE_INFO_SHIPPING, actionMap.get(PURCHASE_INFO_SHIPPING).toString());
            params.putString(PURCHASE_INFO_COUPON, actionMap.get(PURCHASE_INFO_COUPON).toString());

            ecommerceBundle.putParcelable(KEY_ACTION_FIELD, params);

			//json to list map
	        productsList = mapper.readValue(products, new TypeReference<ArrayList<HashMap<String, Object>>>() {});

	        ArrayList items = new ArrayList();

	        // 기존 ActionField를 따로 보내주는 곳이 없음.
            for (Map<String, Object> map : productsList) {
                Bundle product = new Bundle();
                product.putString(PURCHASE_PRODUCT_NAME, map.get(PURCHASE_PRODUCT_NAME).toString());
                product.putString(PURCHASE_PRODUCT_ID, map.get(PURCHASE_PRODUCT_ID).toString());
                product.putString(PURCHASE_PRODUCT_PRICE, map.get(PURCHASE_PRODUCT_PRICE).toString());
                product.putString(PURCHASE_PRODUCT_BRAND, map.get(PURCHASE_PRODUCT_BRAND).toString());
                product.putString(PURCHASE_PRODUCT_CATEGORY, map.get(PURCHASE_PRODUCT_CATEGORY).toString());
                product.putString(PURCHASE_PRODUCT_VARIANT, map.get(PURCHASE_PRODUCT_VARIANT).toString());
                product.putString(PURCHASE_PRODUCT_QUANTITY, map.get(PURCHASE_PRODUCT_QUANTITY).toString());
                product.putString(PURCHASE_PRODUCT_COUPON, map.get(PURCHASE_PRODUCT_COUPON).toString());
                items.add(product);
            }

            ecommerceBundle.putParcelableArrayList(KEY_ITEM, items);

//            ecommerceBundle.putLong(FirebaseAnalytics.Param.CHECKOUT_STEP, 2);
//            ecommerceBundle.putString(FirebaseAnalytics.Param.CHECKOUT_OPTION, actionMap.get(PURCHASE_INFO_AFFILIATION).toString());

            FirebaseAnalytics.getInstance(context).logEvent(PURCHASE_PRODUCT_ECOMMERCE, ecommerceBundle);
/*
	        //구매정보 세팅
	        dataLayer.push("ecommerce", DataLayer.mapOf("purchase", DataLayer.mapOf(
        	            "actionField", actionMap,
        	            "products", productsList)));

	        //구매정보는 screenOpen 이벤트 호출시 함께 전달됨
	        openScreen(context, TextUtils.isEmpty(screenName) ? MainApplication.gtmScreenName : screenName);
	        //초기화하지 않으면 openScreen 이벤트 전송시마다 구매정보를 함께 전달함
	        dataLayer.push("ecommerce", DataLayer.OBJECT_NOT_PRESENT);
*/
		} catch (Exception e) {
			// 10/19 품질팀 요청
			// - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
			// - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
			// - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
			Ln.e(e);
		}
    }
    
    /**
     * GTM Ecommerce에서 사용할 스크린 이름을 설정한다.
     * 
     * @param screenName 스크린명
     */
    public static void setScreenName(String screenName){
    	MainApplication.gtmScreenName = screenName;
        //Ln.e("############### setScreenName : " + MainApplication.gtmScreenName);
    }
    
    /**
     * 스크린오픈 이벤트를 로깅한다.
     * 
     * @param context 컨텍스트
     * @param screenName 스크린명
     */
    public static void openScreen(Context context, String screenName){
        Bundle parmas = new Bundle();
        parmas.putString("screenName", screenName);
        FirebaseAnalytics.getInstance(context).logEvent("screenOpen", parmas);
        /*
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.pushEvent("screenOpen", DataLayer.mapOf("screenName", screenName));
        */
        //Ln.e("############### openScreen : " + screenName);
    }

    /**
     * 이미지 클릭 이벤트를 로깅한다.
     * 
     * @param context 컨텍스트
     * @param category 카테고리명 
     * @param action 액션명
     * @param label 라벨명
     */
    public static void sendEvent(Context context, String category, String action, String label){
        Bundle parmas = new Bundle();
        parmas.putString("eventCategory", category);
        parmas.putString("eventAction", action);
        parmas.putString("eventLabel", label);
        FirebaseAnalytics.getInstance(context).logEvent("imgClick", parmas);

        /*
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        Map<String, Object> map  = new HashMap<String, Object>();
        map.put("eventCategory", category);
        map.put("eventAction", action);
        map.put("eventLabel", label);
        dataLayer.pushEvent("imgClick", map);
        */
        //Ln.e("############### sendEvent : " + category + " | " + action + " | " + label);
    }

    /*public static void fireEvent(Context context, String btnName){
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.pushEvent("btnClick", DataLayer.mapOf("btnName", btnName));
    }*/

    /**
     * 컨테이너정보를 최신버전으로 갱신한다.
     */
    public static void refreshContainer(){
        // 불러 주는 곳이 없음. Deprecated 해도 문제 없음.
        /*
        if(!ContainerHolderSingleton.isEmptyContainer()){
            ContainerHolderSingleton.getContainerHolder().refresh();
        }
        */
    }
}
