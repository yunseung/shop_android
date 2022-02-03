/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.review;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.gsshop.mocha.pattern.mvc.Model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.File;

import javax.annotation.ParametersAreNullableByDefault;

import gsshop.mobile.v2.AdditionalError;

/**
 * 2013.12.24
 * 상품평 작성 정보 V2
 * EC 통합 재구축으로 인해 새로 구성된 데이터 모델
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class ReviewInfoV2 {

	//상품이미지
    private String prdImg = "";
    //상품명
    private String exposPrdNm = "";
    //상품코드
    private String prdCd = "";
    //상품평 전체 평균점수
    private int prdrevwTotGrd = 0;
    //연간 할인권 지급 금액
    private int ecoinCnt = 0;
    //일반상품평:"generalProduct"(사진첨부 가능), 평가상품평:"evaluationProduct"(식품,의료기기 상품은 사진첨부 불가)
    private String prdrevwTypCd = "";
    //고객명
    private String custNm = "";
    //평가항목 이름 (디자인,품질,배송,사용만족도 or 신선도,맛,배송,서비스 등)
    private String evalItmNm1 = "";
    private String evalItmNm2 = "";
    private String evalItmNm3 = "";
    private String evalItmNm4 = "";
    //평가항목 별갯수 (0~5)
    private int evalItmVal1 = 5;
    private int evalItmVal2 = 5;
    private int evalItmVal3 = 5;
    private int evalItmVal4 = 5;
    //앱에서 사용하는 곳 없음
    private String appInstlExposFlg = "";
    //앱에서 사용하는 곳 없음
    private int repPrdCd = 0;
    //멀티파일 업로드 용도
    private String[] atachFilePathList;
    private String[] atachFileNmList; //앱에서 사용하는 곳 없음
    
    private HiddenData hiddenData;

    //상품평 내용
    private String prdrevwBody = "";
    //앱에서 사용하는 곳 없음
    private String templateKey = "";
    //앱에서 사용하는 곳 없음
    private String viewFilePath = "";

    //prdImg를 화면에 뿌리기 위해 bitmap오로 저장하기 위한 용도
    @JsonIgnore
    private Bitmap productImage;

    //기존 싱글파일 업로드 용도
    @JsonIgnore
    private File reviewImageFile;
    
    //멀티파일 업로드 용도
    @JsonIgnore
    private File reviewImageFiles[];
    
    @JsonIgnore
    public boolean hasError() {
        return TextUtils.isEmpty(errorMessage) ? false : true;
    }

    //앱에서 사용하는 곳 없음
    /*Error Code Start*/
    private String requestId = "";
    private String errorCode = "";
    private String errorMessage = "";
    private String exceptionTrace = "";
    private AdditionalError additionalError;

    private String status;
    private String developerMessage;
    private String assignedPerson;

    /*Error Code End*/

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the developerMessage
     */
    public String getDeveloperMessage() {
        return developerMessage;
    }

    /**
     * @param developerMessage the developerMessage to set
     */
    public void setDeveloperMessage(String developerMessage) {
        this.developerMessage = developerMessage;
    }

    /**
     * @return the assignedPerson
     */
    public String getAssignedPerson() {
        return assignedPerson;
    }

    /**
     * @param assignedPerson the assignedPerson to set
     */
    public void setAssignedPerson(String assignedPerson) {
        this.assignedPerson = assignedPerson;
    }

    /**
     * @return the prdImg
     */
    public String getPrdImg() {
        return prdImg;
    }

    /**
     * @param prdImg the prdImg to set
     */
    public void setPrdImg(String prdImg) {
        this.prdImg = prdImg;
    }

    /**
     * @return the exposPrdNm
     */
    public String getExposPrdNm() {
        return exposPrdNm;
    }

    /**
     * @param exposPrdNm the exposPrdNm to set
     */
    public void setExposPrdNm(String exposPrdNm) {
        this.exposPrdNm = exposPrdNm;
    }

    /**
     * @return the prdCd
     */
    public String getPrdCd() {
        return prdCd;
    }

    /**
     * @param prdCd the prdCd to set
     */
    public void setPrdCd(String prdCd) {
        this.prdCd = prdCd;
    }

    /**
     * @return the prdrevwTotGrd
     */
    public int getPrdrevwTotGrd() {
        return prdrevwTotGrd;
    }

    /**
     * @param prdrevwTotGrd the prdrevwTotGrd to set
     */
    public void setPrdrevwTotGrd(int prdrevwTotGrd) {
        this.prdrevwTotGrd = prdrevwTotGrd;
    }

    /**
     * @return the ecoinCnt
     */
    public int getEcoinCnt() {
        return ecoinCnt;
    }

    /**
     * @param ecoinCnt the ecoinCnt to set
     */
    public void setEcoinCnt(int ecoinCnt) {
        this.ecoinCnt = ecoinCnt;
    }

    /**
     * @return the prdrevwTypCd
     */
    public String getPrdrevwTypCd() {
//        return "evaluationProduct";
         return prdrevwTypCd;
    }

                                            /**
     * @param prdrevwTypCd the prdrevwTypCd to set
     */
    public void setPrdrevwTypCd(String prdrevwTypCd) {
        this.prdrevwTypCd = prdrevwTypCd;
    }

    /**
     * @return the custNm
     */
    public String getCustNm() {
        return custNm;
    }

    /**
     * @param custNm the custNm to set
     */
    public void setCustNm(String custNm) {
        this.custNm = custNm;
    }

    /**
     * @return the evalItmNm1
     */
    public String getEvalItmNm1() {
        return evalItmNm1;
    }

    /**
     * @param evalItmNm1 the evalItmNm1 to set
     */
    public void setEvalItmNm1(String evalItmNm1) {
        this.evalItmNm1 = evalItmNm1;
    }

    /**
     * @return the evalItmNm2
     */
    public String getEvalItmNm2() {
        return evalItmNm2;
    }

    /**
     * @param evalItmNm2 the evalItmNm2 to set
     */
    public void setEvalItmNm2(String evalItmNm2) {
        this.evalItmNm2 = evalItmNm2;
    }

    /**
     * @return the evalItmNm3
     */
    public String getEvalItmNm3() {
        return evalItmNm3;
    }

    /**
     * @param evalItmNm3 the evalItmNm3 to set
     */
    public void setEvalItmNm3(String evalItmNm3) {
        this.evalItmNm3 = evalItmNm3;
    }

    /**
     * @return the evalItmNm4
     */
    public String getEvalItmNm4() {
        return evalItmNm4;
    }

    /**
     * @param evalItmNm4 the evalItmNm4 to set
     */
    public void setEvalItmNm4(String evalItmNm4) {
        this.evalItmNm4 = evalItmNm4;
    }

    /**
     * @return the evalItmVal1
     */
    public int getEvalItmVal1() {
        return evalItmVal1;
    }

    /**
     * @param evalItmVal1 the evalItmVal1 to set
     */
    public void setEvalItmVal1(int evalItmVal1) {
        this.evalItmVal1 = evalItmVal1;
    }

    /**
     * @return the evalItmVal2
     */
    public int getEvalItmVal2() {
        return evalItmVal2;
    }

    /**
     * @param evalItmVal2 the evalItmVal2 to set
     */
    public void setEvalItmVal2(int evalItmVal2) {
        this.evalItmVal2 = evalItmVal2;
    }

    /**
     * @return the evalItmVal3
     */
    public int getEvalItmVal3() {
        return evalItmVal3;
    }

    /**
     * @param evalItmVal3 the evalItmVal3 to set
     */
    public void setEvalItmVal3(int evalItmVal3) {
        this.evalItmVal3 = evalItmVal3;
    }

    /**
     * @return the evalItmVal4
     */
    public int getEvalItmVal4() {
        return evalItmVal4;
    }

    /**
     * @param evalItmVal4 the evalItmVal4 to set
     */
    public void setEvalItmVal4(int evalItmVal4) {
        this.evalItmVal4 = evalItmVal4;
    }

    /**
     * @return the appInstlExposFlg
     */
    public String getAppInstlExposFlg() {
        return appInstlExposFlg;
    }

    /**
     * @param appInstlExposFlg the appInstlExposFlg to set
     */
    public void setAppInstlExposFlg(String appInstlExposFlg) {
        this.appInstlExposFlg = appInstlExposFlg;
    }

    /**
     * @return the repPrdCd
     */
    public int getRepPrdCd() {
        return repPrdCd;
    }

    /**
     * @param repPrdCd the repPrdCd to set
     */
    public void setRepPrdCd(int repPrdCd) {
        this.repPrdCd = repPrdCd;
    }

    /**
     * @return the hiddenData
     */
    public HiddenData getHiddenData() {
        return hiddenData;
    }

    /**
     * @param hiddenData the hiddenData to set
     */
    public void setHiddenData(HiddenData hiddenData) {
        this.hiddenData = hiddenData;
    }

    /**
     * @return the prdrevwBody
     */
    public String getPrdrevwBody() {
        //2014.02.03 parksegun EC통합 재구축
        // 수정시 html 태그를 줄바꿈으로 변경
        return prdrevwBody.replace("<br>", "\n");
    }

    /**
     * @param prdrevwBody the prdrevwBody to set
     */
    public void setPrdrevwBody(String prdrevwBody) {

        //2014.01.17 parksegun EC통합 재구축
        // 본분에 있는 Img 태그를 제거하여 값을 반환한다.
        // 이미지가 있을경우 본문 맨뒤에는 이미지 태그가 붙는다. 이를 제거해야함.

        if (prdrevwBody != null) {
            int tag_pos = prdrevwBody.indexOf("<br><br><img");
            if (tag_pos > 0) {
                this.prdrevwBody = prdrevwBody.substring(0, tag_pos);
            } else {
                this.prdrevwBody = prdrevwBody;
            }
        } else {
            this.prdrevwBody = prdrevwBody;
        }
    }

    /**
     * @return the templateKey
     */
    public String getTemplateKey() {
        return templateKey;
    }

    /**
     * @param templateKey the templateKey to set
     */
    public void setTemplateKey(String templateKey) {
        this.templateKey = templateKey;
    }

    /**
     * @return the viewFilePath
     */
    public String getViewFilePath() {
        return viewFilePath;
    }

    /**
     * @param viewFilePath the viewFilePath to set
     */
    public void setViewFilePath(String viewFilePath) {
        this.viewFilePath = viewFilePath;
    }

    /**
     * @return the productImage
     */
    public Bitmap getProductImage() {
        return productImage;
    }

    /**
     * @param productImage the productImage to set
     */
    public void setProductImage(Bitmap productImage) {
        this.productImage = productImage;
    }

	/**
	 * @return the reviewImageFile
	 */
	public File getReviewImageFile() {
		return reviewImageFile;
	}

	/**
	 * @param reviewImageFile the reviewImageFile to set
	 */
	public void setReviewImageFile(File reviewImageFile) {
		this.reviewImageFile = reviewImageFile;
	}

	/**
	 * @return the reviewImageFiles
	 */
	public File[] getReviewImageFiles() {
		return reviewImageFiles;
	}

	/**
	 * @param reviewImageFiles the reviewImageFiles to set
	 */
	public void setReviewImageFiles(File[] reviewImageFiles) {
		this.reviewImageFiles = reviewImageFiles;
	}

	/**
     * @return the requestId
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * @param requestId the requestId to set
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * @return the errorCode
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * @param errorCode the errorCode to set
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @param errorMessage the errorMessage to set
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * @return the exceptionTrace
     */
    public String getExceptionTrace() {
        return exceptionTrace;
    }

    /**
     * @param exceptionTrace the exceptionTrace to set
     */
    public void setExceptionTrace(String exceptionTrace) {
        this.exceptionTrace = exceptionTrace;
    }

    /**
     * @return the additionalError
     */
    public AdditionalError getAdditionalError() {
        return additionalError;
    }

    /**
     * @param additionalError the additionalError to set
     */
    public void setAdditionalError(AdditionalError additionalError) {
        this.additionalError = additionalError;
    }

	/**
	 * @return the atachFilePathList
	 */
	public String[] getAtachFilePathList() {
		return atachFilePathList;
	}

	/**
	 * @param atachFilePathList the atachFilePathList to set
	 */
	public void setAtachFilePathList(String[] atachFilePathList) {
		this.atachFilePathList = atachFilePathList;
	}

	/**
	 * @return the atachFileNmList
	 */
	public String[] getAtachFileNmList() {
		return atachFileNmList;
	}

	/**
	 * @param atachFileNmList the atachFileNmList to set
	 */
	public void setAtachFileNmList(String[] atachFileNmList) {
		this.atachFileNmList = atachFileNmList;
	}

}
