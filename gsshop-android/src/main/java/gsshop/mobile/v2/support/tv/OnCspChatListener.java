package gsshop.mobile.v2.support.tv;

import org.json.JSONObject;

/**
 * CSP 관련 인터페이스
 */
public interface OnCspChatListener {
    /**
     * 메세지 수신
     * @param data
     */
    void onMessaged(JSONObject data);

    /**
     * NOTI 수신
     * @param data
     */
    void onNotied(JSONObject data);

    /**
     * onStated 수신
     * @param data
     */
    void onStated(JSONObject data);

    /**
     * 채팅 환경 설정 파일
     */
    void onReceivedConfig(JSONObject data);

    /**
     * 채팅 목록
     */
    void onReceivedChats(JSONObject data);
}
