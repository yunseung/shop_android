package gsshop.mobile.v2.support.airbridge;

import co.ab180.airbridge.Airbridge;
import co.ab180.airbridge.event.StandardEventCategory;
import roboguice.util.Ln;

/**
 * airbridge 트래킹용 클래스
 *
 */
public class ABAction {

    /**
     * 홈화면조회 이벤트를 로깅한다.
     */
    public static void measureHomeView() {
        try {
            Airbridge.trackEvent(StandardEventCategory.HOME_VIEW);
        } catch (Exception e) {
            Ln.e(e);
        }
    }

    /**
     * airbridge 회원가입 이벤트를 로깅한다.
     *
     * @param customerNo 고객번호
     */
    /* 사용하지 않음
    public static void measureABSignUp(String customerNo) {
        try {
            SignUpEvent event = new SignUpEvent()
                    .setUserEmail("")
                    .setUserId(customerNo);
            AirBridge.getTracker().sendEvent(event);
        } catch (Exception e) {
            Ln.e(e);
        }
    }
    */

    /**
     * airbridge 로그인 이벤트를 로깅한다.
     *
     * @param customerNo 고객번호
     */
    public static void measureABSignIn(String customerNo) {
        try {
            Airbridge.getCurrentUser().setId(customerNo);
            Airbridge.trackEvent(StandardEventCategory.SIGN_IN);
        } catch (Exception e) {
            Ln.e(e);
        }
    }

    /**
     * airbridge 장바구니 담기 이벤트를 로깅한다.
     *
     * @param value 총금액
     * @param products 상품리스트
     */
    /* 사용하지 않음.
    public static void measureABAddToCart(int value, List<Product> products) {
        try {
            //DSP 연동용
            AddToCartEvent event = new AddToCartEvent()
                    .setCartId(UUID.randomUUID().toString())
                    .setTotalValue(value)
                    .addProducts(products);
            AirBridge.getTracker().sendEvent(event);
        } catch (Exception e) {
            Ln.e(e);
        }
    }
    */

    /**
     * airbridge 구매완료 이벤트를 로깅한다.
     *
     * @param action 구매정보 (거래번호, 세금, 배송비 등), json format
     * @param products 상품정보 (상품명, 가격, 브랜드 등), json array format
     */
    /* 사용하지 않음.
    public static void measureABPurchase(String action, String products) {
        ObjectMapper mapper = new ObjectMapper();
        //actionField 저장변수
        Map<String, Object> actionMap = new HashMap<String, Object>();
        //products 저장변수
        List<Map<String, Object>> productsList = new ArrayList<Map<String, Object>>();
        List<Product> abProducts = new ArrayList<Product>();

        try {
            //json to map
            actionMap = mapper.readValue(action, new TypeReference<HashMap<String, Object>>() {});
            //json to list map
            productsList = mapper.readValue(products, new TypeReference<ArrayList<HashMap<String, Object>>>() {});
            for (Map<String, Object> map : productsList) {
                Product abProduct = new Product();
                if (map.get("id") != null) {
                    abProduct.setProductId(map.get("id").toString());
                }
                if (map.get("name") != null) {
                    abProduct.setName(map.get("name").toString());
                }
                if (map.get("price") != null) {
                    abProduct.setPrice(Float.parseFloat(map.get("price").toString()));
                }
                if (map.get("quantity") != null) {
                    abProduct.setQuantity(Integer.parseInt(map.get("quantity").toString()));
                }
                abProduct.setCurrency("KRW");
                abProducts.add(abProduct);
            }

            //DSP 연동용
            PurchaseEvent event = new PurchaseEvent(abProducts)
                    .setTransactionId(actionMap.get("id").toString())
                    .setTotalValue(Integer.parseInt(actionMap.get("revenue").toString()))
                    .setInAppPurchased(false);
            AirBridge.getTracker().sendEvent(event);

        } catch (Exception e) {
            Ln.e(e);
        }
    }
    */
}
