/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.review;

import com.google.inject.Inject;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.pattern.mvc.Model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.net.URISyntaxException;

import javax.annotation.ParametersAreNullableByDefault;

import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.WebError;
import gsshop.mobile.v2.util.CookieUtils;
import roboguice.inject.ContextSingleton;

/**
 * 상품평 커넥터
 *
 */
@ContextSingleton
public class ReviewConnector {

    @Inject
    private RestClient restClient;

    /**
     * 상품평 정보를 서버에서 조회해서 가져옴.
     *
     * @param queryString 쿼리스트링 변수
     * @throws Exception
     */
    public ReviewInfoSet getReviewInfo(String queryString) throws RestClientException,
            URISyntaxException {

        // queryString을 인코딩하지 않기 위해 URI를 직접 이용함.
        //2013.12.24 parksegun        
        // 수정인지 아니면 초기화인지 구분이 필요하다.
        //확인필요. 진짜 messageId로 수정여부를 판단 한다.
        boolean edit = queryString.contains("messageId");

        String url = ServerUrls.REST.HTTP_PREFIX
                + (edit ? ServerUrls.REST.REVIEW_READ_EDIT : ServerUrls.REST.REVIEW_READ_INIT)
                + "?" + queryString + "&format=json";

        //상품평 작성화면 이동시 로그인 풀리는 현상을 개선
        //restClient쿠키의 ecid등 로그인 정보가 유실된 상태에 발생하므로 웹뷰쿠키를 복사해준다.
        CookieUtils.syncWebViewCookiesToRestClient(MainApplication.getAppContext(), restClient);

        return restClient.getForObject(new URI(url), ReviewInfoSet.class);
    }

    /**
     * (신규) 상품평 정보를 서버에 저장.
     *
     * NOTE : 업로드할 파일이 없어도 무조건 요청 content-type을
     * multipart/form-data로 보내야 함. application/x-www-form-urlencoded으로
     * 보내면 서버단에서 오류발생.
     *
     * @param formData
     * @return
     * @throws RestClientException
     * @throws URISyntaxException
     */
    public SaveReviewResult saveReview(MultiValueMap<String, Object> formData)
            throws RestClientException, URISyntaxException {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Connection", "Close");
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap> requestEntity = new HttpEntity<MultiValueMap>(formData, headers);

        String url = ServerUrls.REST.HTTP_PREFIX + ServerUrls.REST.REVIEW_NEW_SAVE;
        return restClient.postForObject(new URI(url), requestEntity, SaveReviewResult.class);
    }

    /**
     * 2013.12.31 parksegun 신규추가
     * (수정) 상품평 정보를 서버에 저장.
     *
     * NOTE : 업로드할 파일이 없어도 무조건 요청 content-type을
     * multipart/form-data로 보내야 함. application/x-www-form-urlencoded으로
     * 보내면 서버단에서 오류발생.
     *
     * @param formData
     * @return
     * @throws RestClientException
     * @throws URISyntaxException
     */
    public SaveReviewResult updateReview(MultiValueMap<String, Object> formData)
            throws RestClientException, URISyntaxException {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Connection", "Close");
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap> requestEntity = new HttpEntity<MultiValueMap>(formData, headers);

        //20131230 parksegun
        //REVIEW_UPDATE
        String url = ServerUrls.REST.HTTP_PREFIX + ServerUrls.REST.REVIEW_UPDATE;
        return restClient.postForObject(new URI(url), requestEntity, SaveReviewResult.class);
    }

    @Model
    @JsonIgnoreProperties(ignoreUnknown = true)
    @ParametersAreNullableByDefault
    public static class SaveReviewResult {

        private String rtnUrl;
        private WebError webError;

        /**
         * @return the rtnUrl
         */
        public String getRtnUrl() {
            return rtnUrl;
        }

        /**
         * @param rtnUrl the rtnUrl to set
         */
        public void setRtnUrl(String rtnUrl) {
            this.rtnUrl = rtnUrl;
        }

        /**
         * @return the webError
         */
        public WebError getWebError() {
            return webError;
        }

        /**
         * @param webError the webError to set
         */
        public void setWebError(WebError webError) {
            this.webError = webError;
        }

    }

}
