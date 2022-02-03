/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.review;

import android.content.Context;
import android.text.TextUtils;

import com.google.inject.Inject;
import com.gsshop.mocha.network.util.HttpUtils;

import org.springframework.core.io.FileSystemResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.net.URISyntaxException;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.review.ReviewConnector.SaveReviewResult;
import gsshop.mobile.v2.user.User;
import roboguice.inject.ContextSingleton;
import roboguice.util.Ln;

/**
 * 상품평 액션.
 *
 */
@ContextSingleton
public class ReviewAction {

	/**
	 * 태그명
	 */
	private static final String TAG = "ReviewAction";
	
    @Inject
    private Context context;

    @Inject
    private ReviewConnector reviewConnector;
    

    /**
     * 상품평 신규 작성 또는 수정을 위한 조회.
     *
     * @param urlQueryString 상품평 조회 웹페이지 호출시 전달하는 쿼리스트링.
     * ex) prdid=7166410&order_num=553295879&lineNum=1&save_root=androOrd&messageId=84581341&modify=N
     *
     * @return
     * @throws ReviewInvalidException, ReviewImageLoadException, URISyntaxException
     */
    public ReviewInfoV2 getReview(String urlQueryString) throws ReviewInvalidException, URISyntaxException {
        String url = urlQueryString;
        //상품평 API에서 로그인여부 체크 기준을 쿠키에서 appCustNo 파라미터로 변경
        url += "&appCustNo=" + (User.getCachedUser() != null ? User.getCachedUser().customerNumber : "");

        //UserAgent값 파라미터로 전달
        String userAgent = context.getString(R.string.mc_rest_user_agent_additional);
        if (!TextUtils.isEmpty(userAgent)) {
            String[] aryUserAgent = userAgent.split(",");
            if (aryUserAgent.length == 4) {
                url += "&appVersion=" + aryUserAgent[1] + "&appCode=" + aryUserAgent[2] + "&appGB=" + aryUserAgent[3];
            }
        }

        //2013.12.27 parksegun
        ReviewInfoSet reviewInfoset = reviewConnector.getReviewInfo(url);
        ReviewInfoV2 reviewInfo = reviewInfoset.get_CONTENT_KEY();

        //2013.12.27 parksegun Error는 2가지 패턴 일수 있다. "WebError만 있는경우와 contents안에 Error가 있는경우." 대한 대비가 필요함.
        if(reviewInfoset.getWebError() != null && reviewInfoset.getWebError().isError()) {
            throw new ReviewInvalidException().setUserMessage(reviewInfoset.getWebError().getErrorMessage());
        }

        try {
            //서버단 상품 이미지를 메모리에 로드
            reviewInfo.setProductImage(HttpUtils.getBitmap(reviewInfo.getPrdImg()));
        } catch (IOException e) {
            // ignore
        }
        
        return reviewInfo;
    }

    /**
     * 상품평 저장.
     * 2013.12.31 parksegun 상품평 저장시 AS-IS와 달리 구성됨.
     * @param reviewInfo
     * @throws Exception 
     */
    public SaveReviewResult saveReview(ReviewInfoV2 reviewInfo) {
        
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<String, Object>();
        
        String priFix = "prdRevw.";
        // 2013.12.27 parksegun
        
        formData.add(priFix + "prdCd", reviewInfo.getPrdCd());
        formData.add(priFix + "exposPrdNm", reviewInfo.getExposPrdNm());
        formData.add(priFix + "ordNo", reviewInfo.getHiddenData().getOrdNo());

        //NOTE : 폼 전송방식인 경우 파일을 제외한 나머지 파라미터는 모두 String 타입이어야 한다.        
        formData.add(priFix + "evalItmNm1", "" + reviewInfo.getEvalItmNm1());
        formData.add(priFix + "evalItmNm2", "" + reviewInfo.getEvalItmNm2());
        formData.add(priFix + "evalItmNm3", "" + reviewInfo.getEvalItmNm3());
        formData.add(priFix + "evalItmNm4", "" + reviewInfo.getEvalItmNm4());
        
        formData.add(priFix + "evalItmVal1", "" + reviewInfo.getEvalItmVal1());
        formData.add(priFix + "evalItmVal2", "" + reviewInfo.getEvalItmVal2());
        formData.add(priFix + "evalItmVal3", "" + reviewInfo.getEvalItmVal3());
        formData.add(priFix + "evalItmVal4", "" + reviewInfo.getEvalItmVal4());
        
        formData.add(priFix + "ordItemOptNm1", "" + reviewInfo.getHiddenData().getOrdItemOptNm1()); 
        formData.add(priFix + "ordItemOptNm2", "" + reviewInfo.getHiddenData().getOrdItemOptNm2()); 
        
        formData.add(priFix + "hptstFlg", "" + reviewInfo.getHiddenData().getHptstFlg()); //해피테스터
        formData.add(priFix + "prdrevwWritPath", "" + "mobilePrd"); //saveRoot
        String prdBody = reviewInfo.getPrdrevwBody(); 
        if(prdBody == null || prdBody.isEmpty()){
        	formData.add(priFix + "prdrevwTitle", "" + "");
        } else {
        	formData.add(priFix + "prdrevwTitle", "" + reviewInfo.getPrdrevwBody().substring(0,( 
        			reviewInfo.getPrdrevwBody().length() > 50 ? 50 : reviewInfo.getPrdrevwBody().length()))); // 타이틀은 본문의 50글자
        }
        formData.add(priFix + "ordItemOptExposFlg", "" + reviewInfo.getHiddenData().getOrdItemOptExposFlg());
        formData.add(priFix + "prdrevwBody",   "" + reviewInfo.getPrdrevwBody().replaceAll("\n", "<br>")); // 첨부파일 처리
        formData.add(priFix + "prdrevwTypCd", "" + reviewInfo.getPrdrevwTypCd() );
        formData.add(priFix + "hptstPmoNo", "" + reviewInfo.getHiddenData().getHptstPmoNo());
        //수정이면...
        if(reviewInfo.getHiddenData().isReviewUpdate()) {
            formData.add(priFix + "prdrevwId", "" + reviewInfo.getHiddenData().getPrdrevwId());
        }
       
        SaveReviewResult result = null;
        if (reviewInfo.getReviewImageFiles() != null) {
        	// image는 atachFileGbn 가 2이다.
            formData.add(priFix + "atachFileGbn", "2"); 
            for (int i=0; i<reviewInfo.getReviewImageFiles().length; i++) {
            	if (reviewInfo.getReviewImageFiles()[i] != null) {
            		//Ln.i("saveReview > prdRevwImage[" + i + "] : " + reviewInfo.getReviewImageFiles()[i].getPath());
            		formData.add("prdRevwImage", new FileSystemResource(reviewInfo.getReviewImageFiles()[i]));
            	}
            }
        }
        else
        {
            // text만 있는 경우 1이다.
            formData.add(priFix + "atachFileGbn", "1"); 
        }

        //상품평 API에서 로그인여부 체크 기준을 쿠키에서 appCustNo 파라미터로 변경
        formData.add(priFix + "appCustNo", User.getCachedUser() != null ? User.getCachedUser().customerNumber : "");
        //UserAgent값 파라미터로 전달
        String userAgent = context.getString(R.string.mc_rest_user_agent_additional);
        if (!TextUtils.isEmpty(userAgent)) {
            String[] aryUserAgent = userAgent.split(",");
            if (aryUserAgent.length == 4) {
                formData.add(priFix + "appVersion", aryUserAgent[1]);
                formData.add(priFix + "appCode", aryUserAgent[2]);
                formData.add(priFix + "appGB", aryUserAgent[3]);
            }
        }

        // EC통합 재구축 20140224 parksegun
        // Android 4.3 에서 상품평 저장시 오류 발생. 이에 대한 방어 코드 적용
        try{
            //2013.12.31 parksegun 신규저장/업데이트 저장 선택
            result = reviewInfo.getHiddenData().isReviewUpdate() ? reviewConnector.updateReview(formData) : reviewConnector.saveReview(formData);
            //isfail = false;
        }
        catch(Exception ex)
        {
            //Android 4.3만 적용함. 4.3이 아니면 그냥 throw한다.
            if (android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                try {
                    result = reviewInfo.getHiddenData().isReviewUpdate() ? reviewConnector.updateReview(formData) : reviewConnector.saveReview(formData);
                } catch (Exception e1) {
                    Ln.e(e1);
                }
            }
        }
        //2013.12.31 parksegun
        if(result.getWebError() != null && result.getWebError().isError()){
        	//Ln.i("saveReview > Error : " + result.getWebError().getErrorCode() + " | " + result.getWebError().getErrorMessage());
            throw new ReviewInvalidException().setUserMessage(result.getWebError().getErrorMessage());
        }
        return result;
    }
}
