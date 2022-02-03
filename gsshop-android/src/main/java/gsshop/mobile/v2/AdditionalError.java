/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2;

import javax.annotation.ParametersAreNullableByDefault;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.gsshop.mocha.pattern.mvc.Model;

/**
 * 에러 메세지 상세 정보 // additionalError
 * EC 통합 재구축으로 인해 새로 구성된 데이터 모델
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class AdditionalError {

    /*Error Code Start*/
    private String forwardUrl = "";
    private String forwardUrlLabel = "";
    private String errorMessageByBiz = "";
    private String errorMessageByBizList = "";
    private String forwardUrlList = "";
    private String forwardUrlLabelList = "";
    private String errorPageTitle = "";

    private String errorPage = "";
    private String returnUrl = "";
    private String returnUrlParams = "";
    private String actionType = "";

    private String forceHistoryBack = "";
    private String dustView = "";
    private String viewUri = "";

    /*Error Code End*/

    /**
     * @return the errorPage
     */
    public String getErrorPage() {
        return errorPage;
    }

    /**
     * @param errorPage the errorPage to set
     */
    public void setErrorPage(String errorPage) {
        this.errorPage = errorPage;
    }

    /**
     * @return the returnUrl
     */
    public String getReturnUrl() {
        return returnUrl;
    }

    /**
     * @param returnUrl the returnUrl to set
     */
    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    /**
     * @return the returnUrlParams
     */
    public String getReturnUrlParams() {
        return returnUrlParams;
    }

    /**
     * @param returnUrlParams the returnUrlParams to set
     */
    public void setReturnUrlParams(String returnUrlParams) {
        this.returnUrlParams = returnUrlParams;
    }

    /**
     * @return the actionType
     */
    public String getActionType() {
        return actionType;
    }

    /**
     * @param actionType the actionType to set
     */
    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    /**
     * @return the forwardUrl
     */
    public String getForwardUrl() {
        return forwardUrl;
    }

    /**
     * @param forwardUrl the forwardUrl to set
     */
    public void setForwardUrl(String forwardUrl) {
        this.forwardUrl = forwardUrl;
    }

    /**
     * @return the forwardUrlLabel
     */
    public String getForwardUrlLabel() {
        return forwardUrlLabel;
    }

    /**
     * @param forwardUrlLabel the forwardUrlLabel to set
     */
    public void setForwardUrlLabel(String forwardUrlLabel) {
        this.forwardUrlLabel = forwardUrlLabel;
    }

    /**
     * @return the errorMessageByBiz
     */
    public String getErrorMessageByBiz() {
        return errorMessageByBiz;
    }

    /**
     * @param errorMessageByBiz the errorMessageByBiz to set
     */
    public void setErrorMessageByBiz(String errorMessageByBiz) {
        this.errorMessageByBiz = errorMessageByBiz;
    }

    /**
     * @return the errorMessageByBizList
     */
    public String getErrorMessageByBizList() {
        return errorMessageByBizList;
    }

    /**
     * @param errorMessageByBizList the errorMessageByBizList to set
     */
    public void setErrorMessageByBizList(String errorMessageByBizList) {
        this.errorMessageByBizList = errorMessageByBizList;
    }

    /**
     * @return the forwardUrlList
     */
    public String getForwardUrlList() {
        return forwardUrlList;
    }

    /**
     * @param forwardUrlList the forwardUrlList to set
     */
    public void setForwardUrlList(String forwardUrlList) {
        this.forwardUrlList = forwardUrlList;
    }

    /**
     * @return the forwardUrlLabelList
     */
    public String getForwardUrlLabelList() {
        return forwardUrlLabelList;
    }

    /**
     * @param forwardUrlLabelList the forwardUrlLabelList to set
     */
    public void setForwardUrlLabelList(String forwardUrlLabelList) {
        this.forwardUrlLabelList = forwardUrlLabelList;
    }

    /**
     * @return the errorPageTitle
     */
    public String getErrorPageTitle() {
        return errorPageTitle;
    }

    /**
     * @param errorPageTitle the errorPageTitle to set
     */
    public void setErrorPageTitle(String errorPageTitle) {
        this.errorPageTitle = errorPageTitle;
    }

    /**
     * @return the forceHistoryBack
     */
    public String getForceHistoryBack() {
        return forceHistoryBack;
    }

    /**
     * @param forceHistoryBack the forceHistoryBack to set
     */
    public void setForceHistoryBack(String forceHistoryBack) {
        this.forceHistoryBack = forceHistoryBack;
    }

    /**
     * @return the dustView
     */
    public String getDustView() {
        return dustView;
    }

    /**
     * @param dustView the dustView to set
     */
    public void setDustView(String dustView) {
        this.dustView = dustView;
    }

    /**
     * @return the viewUri
     */
    public String getViewUri() {
        return viewUri;
    }

    /**
     * @param viewUri the viewUri to set
     */
    public void setViewUri(String viewUri) {
        this.viewUri = viewUri;
    }

}
