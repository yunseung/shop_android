package gsshop.mobile.v2.home.shop.network;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;

import com.google.inject.Inject;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.pattern.mvc.BaseAsyncController;
import com.gsshop.mocha.pattern.mvc.Model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.http.HttpEntity;

import java.net.URI;

import javax.annotation.ParametersAreNullableByDefault;

import gsshop.mobile.v2.home.main.Basket;
import gsshop.mobile.v2.util.CookieUtils;

/*
 		Post 방식으로 Http 전송하기
	*/
public class BasketPostAsyncController extends BaseAsyncController<BasketPostAsyncController.CartResult> {

    private Context mContext;

    private String mUrl;
    private String mIsFresh;
    private HttpEntity<Object> mRequestEntity;

//    private List<NameValuePair> mRequestData;

    private OnPostAsyncListener mListener = null;

    @Inject
    protected RestClient restClient;

    public BasketPostAsyncController(Context context) {
        super(context);
        mContext = context;
    }

    public BasketPostAsyncController(Context context, OnPostAsyncListener listener) {
        super(context);
        mContext = context;
        mListener = listener;
    }

    public interface OnPostAsyncListener {
        void onResult(CartResult result);
    }

    public void setListener(OnPostAsyncListener listener) {
        mListener = listener;
    }

    @Override
    protected void setDialog(Dialog dialog) {
        super.setDialog(null);
    }

    @Override
    protected void onPrepare(Object... params) throws Exception {
        super.onPrepare(params);

        mUrl = ((Basket) params[0]).url;
        mIsFresh = ((Basket) params[0]).isFresh;
        String strQueryParamCache = Uri.parse(mUrl).getQueryParameter("cache");

        mRequestEntity = (HttpEntity<Object>) params[1];
//        mRequestData = (List<NameValuePair>) params[2];
    }

    @Override
    protected CartResult process() throws Exception {
        CartResult resultCart = null;
        resultCart = restClient.postForObject(new URI(mUrl), mRequestEntity, CartResult.class);
        resultCart.isFresh = mIsFresh;

        // 혹시 postForObject 안될는 경우를 위해 만들어 놓았음. 쓸일은 없을 것 같지만.
//        DefaultHttpClient client = new DefaultHttpClient();
//        client.setCookieStore(getWebViewCookieToBasicCookieStore());
//
//        HttpPost httpPost = new HttpPost(mUrl);
//        try {
//            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(mRequestData, "UTF-8");
//            httpPost.setEntity(entity);
//            HttpResponse response = client.execute(httpPost);
//
//            Ln.d("response : " + response.toString());
//        }
//        catch (ClientProtocolException e) {
//            Ln.e(e.getMessage());
//        } catch (IOException e) {
//            Ln.e(e.getMessage());
//        }

        return resultCart;
    }

    @Override
    protected void onSuccess(CartResult result) {
        CookieUtils.copyRestClientCookiesToWebView(context, restClient);

        if (mListener != null) {
            mListener.onResult(result);
        }
    }

    /**
     * 혹시 postForObject 안될는 경우를 위해 만들어 놓았음. 쓸일은 없을 것 같지만.
     * private static final String COOKIE_DOMAIN = "gsshop.com";
     * private BasicCookieStore getWebViewCookieToBasicCookieStore() {
     * List<NameValuePair> cookies = WebViewCookieManager.getCookies(
     * mContext.getApplicationContext(), COOKIE_DOMAIN);
     * <p>
     * // ecid 값에서 따옴표를 제거한다.
     * // start
     * String ecid_cookie = "";
     * NameValuePair removeTarget = null;
     * if (cookies != null) {
     * for (NameValuePair c : cookies) {
     * if (c.getName().endsWith("ecid")) {
     * ecid_cookie = c.getValue();
     * ecid_cookie = ecid_cookie.replace("\"", "");
     * removeTarget = c;
     * } // if(c.getN
     * }
     * }
     * <p>
     * if (removeTarget != null) {
     * cookies.remove(removeTarget);
     * BasicNameValuePair newCookie = new BasicNameValuePair("ecid", ecid_cookie);
     * cookies.add(newCookie);
     * }
     * //end
     * <p>
     * String restCookieDomain = CookieUtils.getRestCookieDomain();
     * <p>
     * BasicCookieStore cookieStore = new BasicCookieStore();
     * BasicClientCookie clientCookie;
     * for (NameValuePair pair : cookies) {
     * clientCookie = new BasicClientCookie(pair.getName(), pair.getValue());
     * clientCookie.setDomain(restCookieDomain);
     * cookieStore.addCookie(clientCookie);
     * }
     * <p>
     * return cookieStore;
     * }
     **/

    @Model
    @JsonIgnoreProperties(ignoreUnknown = true)
    @ParametersAreNullableByDefault
    public class CartResult {

        public abstract class ReturnCode {
            public static final String SUCCESS = "S"; // 성공
            public static final String ERROR = "E"; // 에러
            public static final String ADULT = "C"; // 성인인증 필요
            public static final String CART = "M"; // 장바구니 수정 필요 (장바구니로 이동)
            public static final String SELECT_LOCATION = "NRD"; // 마트 배송지 선택 필요 ( 배송지 팝업 노출 시켜야 함)
            public static final String ALEADY_EXISTS = "EXP"; // 장바구니에 상품 존재
        }

        private String retCd;
        private String retMsg;
        private String retUrl;
        private String isFresh;

        /**
         * @return the rtnUrl
         */
        public String getRtnUrl() {
            return retUrl;
        }

        /**
         * @param rtnUrl the rtnUrl to set
         */
        public void setRtnUrl(String rtnUrl) {
            this.retUrl = rtnUrl;
        }

        /**
         * @return the retMsg
         */
        public String getRetMsg() {
            return retMsg;
        }

        /**
         * @param retMsg the retMsg to set
         */
        public void SetRetMsg(String retMsg) {
            this.retMsg = retMsg;
        }

        /**
         * @return the retCd
         */
        public String getRetCd() {
            return retCd;
        }

        /**
         * @param retCd the retCd to set
         */
        public void setRetCd(String retCd) {
            this.retCd = retCd;
        }

        /**
         * @return the isFresh
         */
        public String getIsFresh() {
            return isFresh;
        }

    }
}