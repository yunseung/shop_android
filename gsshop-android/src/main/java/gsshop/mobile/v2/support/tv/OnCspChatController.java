package gsshop.mobile.v2.support.tv;

/**
 * CSP 관련 인터페이스
 */
public interface OnCspChatController {
    /**
     * 콜백을 지정
     * @param callback
     */
    default void setOnChatDataListener(OnCspChatListener callback) {}
}
