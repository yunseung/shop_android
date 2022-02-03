package gsshop.mobile.v2.home.shop.schedule.model;

import com.gsshop.mocha.pattern.mvc.Model;

/**
 * 방송알림 API 호출결과 모델
 */
@Model
public class BroadAlarmResult {
    public String type;
    public String errMsg;
    public String errMsgText;
    public String phoneNo;
    public String prdId;
    public String prdName;
    public String imgUrl;
    public String linkUrl;
    public String linkUrlText;
    public String[] infoTextList;

    /**
     * result type
     */
    public enum ALARM_RESULT_TYPE {
        MSG_SUCCESS, //(성공)
        ERR_NOT_LOGIN, //(로그인 필요)
        ERR_PHONE_EMPTY, //(핸드폰 번호 필요)
        ERR_MAX_ALARM_SIZE, //(방송알림 최대 등록 개수 초과)
        ERR_DUPLICATE_PRODUCT, //(이미 방송알림 등록된 상품)
        ERR_SERVER_SAVE_FAIL, //(방송알림 등록 실패)
        ERR_SERVER_DELETE_FAIL, //(방송알림 취소 실패)
        ERR_NOT_ALARM_PRODUCT, //(등록된 방송알림 상품 아님)

        ERR_DUPLICATE_MOBILELIVE, //(이미 방송알림 등록된 모라방송)
        ERR_NOT_ALARM_MOBILELIVE //(이미 방송알림 해제된 모라방송)
    }
}