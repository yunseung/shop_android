/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.review;

import javax.annotation.ParametersAreNullableByDefault;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import android.text.TextUtils;

import com.gsshop.mocha.pattern.mvc.Model;

/**
 * 상품평 작성 정보 V2
 * EC 통합 재구축으로 인해 새로 구성된 데이터 모델
 * HiddenData 정보 
 * 아래 변수의 대부분은 상품평 수정시 서버에서 전달받은 데이타를 가공없이 그대로 상품평 저장시 서버로 보내는데 사용되는 용도임
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class HiddenData {

    /*hidden Datas Start*/
	//상품평 고유번호(값이 존재하면 상품평 업데이트, 없거나 "0"이면 신규작성으로 판단)
    private String prdrevwId = "";
    private String ordNo = "";
    private int atachFileGbn = 0;
    private String ordItemOptNm1 = "";
    private String ordItemOptNm2 = "";
    private String hptstFlg = "";
    private String hptstPmoNo = "";
    private String prdrevwTitle = "";
    private String prdrevwBody = "";
    private String ordItemOptExposFlg = "";
    private String bestPrdrevwFlg = "";
    private String inflowPath = "";
    private String ordRetUrl = "";
    private String cnlUrl = "";

    /*hidden Datas End*/

    /**
     * @return the cnlUrl
     */
    public String getCnlUrl() {
        return cnlUrl;
    }

    /**
     * @param cnlUrl the cnlUrl to set
     */
    public void setCnlUrl(String cnlUrl) {
        this.cnlUrl = cnlUrl;
    }

    /**
     * @return the ordRetUrl
     */
    public String getOrdRetUrl() {
        return ordRetUrl;
    }

    /**
     * @param ordRetUrl the ordRetUrl to set
     */
    public void setOrdRetUrl(String ordRetUrl) {
        this.ordRetUrl = ordRetUrl;
    }

    /**
     * @return the inflowPath
     */
    public String getInflowPath() {
        return inflowPath;
    }

    /**
     * @param inflowPath the inflowPath to set
     */
    public void setInflowPath(String inflowPath) {
        this.inflowPath = inflowPath;
    }

    /*hidden Datas End*/

    /**
     * @return the prdrevwId
     */
    public String getPrdrevwId() {
        return prdrevwId;
    }

    /**
     * @param prdrevwId the prdrevwId to set
     */
    public void setPrdrevwId(String prdrevwId) {
        this.prdrevwId = prdrevwId;
    }

    /**
     * @return the ordNo
     */
    public String getOrdNo() {
        return ordNo;
    }

    /**
     * @param ordNo the ordNo to set
     */
    public void setOrdNo(String ordNo) {
        this.ordNo = ordNo;
    }

    /**
     * @return the atachFileGbn
     */
    public int getAtachFileGbn() {
        return atachFileGbn;
    }

    /**
     * @param atachFileGbn the atachFileGbn to set
     */
    public void setAtachFileGbn(int atachFileGbn) {
        this.atachFileGbn = atachFileGbn;
    }

    /**
     * @return the ordItemOptNm1
     */
    public String getOrdItemOptNm1() {
        return ordItemOptNm1;
    }

    /**
     * @param ordItemOptNm1 the ordItemOptNm1 to set
     */
    public void setOrdItemOptNm1(String ordItemOptNm1) {
        this.ordItemOptNm1 = ordItemOptNm1;
    }

    /**
     * @return the ordItemOptNm2
     */
    public String getOrdItemOptNm2() {
        return ordItemOptNm2;
    }

    /**
     * @param ordItemOptNm2 the ordItemOptNm2 to set
     */
    public void setOrdItemOptNm2(String ordItemOptNm2) {
        this.ordItemOptNm2 = ordItemOptNm2;
    }

    /**
     * @return the hptstFlg
     */
    public String getHptstFlg() {
        return hptstFlg;
    }

    /**
     * @param hptstFlg the hptstFlg to set
     */
    public void setHptstFlg(String hptstFlg) {
        this.hptstFlg = hptstFlg;
    }

    /**
     * @return the hptstPmoNo
     */
    public String getHptstPmoNo() {
        return hptstPmoNo;
    }

    /**
     * @param hptstPmoNo the hptstPmoNo to set
     */
    public void setHptstPmoNo(String hptstPmoNo) {
        this.hptstPmoNo = hptstPmoNo;
    }

    /**
     * @return the prdrevwTitle
     */
    public String getPrdrevwTitle() {
        return prdrevwTitle;
    }

    /**
     * @param prdrevwTitle the prdrevwTitle to set
     */
    public void setPrdrevwTitle(String prdrevwTitle) {
        this.prdrevwTitle = prdrevwTitle;
    }

    /**
     * @return the prdrevwBody
     */
    public String getPrdrevwBody() {
        return prdrevwBody;
    }

    /**
     * @param prdrevwBody the prdrevwBody to set
     */
    public void setPrdrevwBody(String prdrevwBody) {
        this.prdrevwBody = prdrevwBody;
    }

    /**
     * @return the ordItemOptExposFlg
     */
    public String getOrdItemOptExposFlg() {
        return ordItemOptExposFlg;
    }

    /**
     * @param ordItemOptExposFlg the ordItemOptExposFlg to set
     */
    public void setOrdItemOptExposFlg(String ordItemOptExposFlg) {
        this.ordItemOptExposFlg = ordItemOptExposFlg;
    }

    /**
     * @return the bestPrdrevwFlg
     */
    public String getBestPrdrevwFlg() {
        return bestPrdrevwFlg;
    }

    /**
     * @param bestPrdrevwFlg the bestPrdrevwFlg to set
     */
    public void setBestPrdrevwFlg(String bestPrdrevwFlg) {
        this.bestPrdrevwFlg = bestPrdrevwFlg;
    }

    /**
     * 신규 작성인가 수정(편집)인가
     * @return 수정인 경우 true.
     */
    @JsonIgnore
    public boolean isReviewUpdate() {
        if (TextUtils.isEmpty(prdrevwId)) {
            return false;
        }

        if ("0".equals(prdrevwId)) {
            return false;
        }

        return true;
    }

}
