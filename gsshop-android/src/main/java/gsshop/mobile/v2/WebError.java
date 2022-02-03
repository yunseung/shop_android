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

import android.text.TextUtils;

import com.gsshop.mocha.pattern.mvc.Model;

/**
 * 에러 메세지 상세 정보 // webError
 * EC 통합 재구축으로 인해 새로 구성된 데이터 모델
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class WebError {

    private String requestId = "";
    private String errorCode = "";
    private String errorMessage = "";
    private String exceptionTrace = "";
    private String serverIp = "";
    private String serverName = "";

    private AdditionalError additionalError;

    private String status;
    private String developerMessage;
    private String assignedPerson;

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
     * @return the serverIp
     */
    public String getServerIp() {
        return serverIp;
    }

    /**
     * @param serverIp the serverIp to set
     */
    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    /**
     * @return the serverName
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * @param serverName the serverName to set
     */
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    /**
     * 에러여부를 판단한다.
     * @return true 이면 Error
     */
    public boolean isError() {

        if (TextUtils.isEmpty(errorMessage)) {
            return false;
        }
        return true;
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

}
