package gsshop.mobile.v2.support.media;

import gsshop.mobile.v2.home.shop.BroadType;
import gsshop.mobile.v2.home.shop.PlayerAction;
import gsshop.mobile.v2.support.media.model.MediaInfo;

/**
 * 동영상 동작관련 인터페이스
 */
public interface OnMediaPlayerListener {

    /**
     * 동영상 플레이
     */
    default void onPlayed(){}

    /**
     * 동영상 멈춤
     */
    default void onPaused() {}

    /**
     * 동영상 멈춤 및 초기화
     */
    default void onStoped() {}

    /**
     * 동영상 사이즈 취득
     */
    default void onVideoSizeKnown() {}

    /**
     * 동영상 준비 완료
     */
    default void onVideoReady() {}

    /**
     * 재생종료
     * @param media
     */
    default void onFinished(MediaInfo media){}

    /**
     * 재생 종료와 별개로 닫기 버튼 눌렀을 때의 동작을 추가함.
     */
    default void onClosed(){}

    /**
     * 닫기 버튼과 별개로 zoom 버튼 때의 동작을 추가함.
     */
    default void onZoomed(){}

    /**
     * error exception
     *
     * @param e
     */
    default void onError(Exception e){}

    /**
     * 화면탭
     * 플레이어 영역을 탭하는 경우 사용할 콜백
     *
     * @param show
     */
    default void onTap(boolean show){}


    /**
     * 화면탭
     * 플레이어 영역을 탭하는 경우 사용할 콜백
     *
     * @param broadType 방송종류
     * @param show      노출여부
     */
    default void onTap(BroadType broadType, Boolean show) {}

    /**
     * 화면탭
     * 플레이어 영역을 탭하는 경우 사용할 콜백
     */
    default void onTap() {}

    /**
     * 전체 화면
     *
     * @param media
     */
    default void onFullScreenClick(MediaInfo media) {}


    /**
     * 플레이 버튼 click
     */
    default void onPlayButtonClicked() {
    }

    /**
     * list postion
     */
    default int getListPosition() {
        return -1;
    }

    /**
     * 버퍼링
     *
     * @param isBuffering
     */
    default void onBuffering(boolean isBuffering) {
    }

    /**
     * 날방 화면하단 날톡 노출/숨김 처리 위해
     *
     * @param isVod
     */
    default void onSetVideoType(boolean isVod) {
    }

    /**
     * 플레이어에서 mute 설정이 변경될 때 호출.
     *
     * @param on
     */
    default void onMute(boolean on) {
    }

    /**
     * 남은 시간을 표시한다.
     *
     * @param remainedTime
     */
    default void onRemainedTime(String remainedTime) {
    }

    /**
     * 와이즈로그 호출
     *
     * @param action    액션(PLAY or PAUSE etc..)
     * @param playTime  재생시간
     * @param totalTime 전체시간
     */
    default void sendWiseLog(PlayerAction action, int playTime, int totalTime) {
    }

    /**
     * 단품 네이티브 와이즈로그 호출 (단품 네이티브 추가)
     * @param action
     * @param playTime
     * @param totalTime
     */
    default void sendWiseLogPrdNative(PlayerAction action, int playTime, int totalTime) {}
    default void sendWiseLogPrdNative(PlayerAction action, boolean isLive, boolean isAuto, String videoId, int playTime, int totalTime) {}

    /**
     * 동영상의 총 재생시간 조회 용도
     *
     * @param duration 총 재생시간(ms)
     */
    default void  videoDurationChanged(int duration) {}

    /**
     * 3g 과금 표시.
     *
     * @param show
     */
    default void showMobileData(boolean show) {
    }

    /**
     * 상품보기 이동
     */
    default void onProductInfo() {
    }

    /**
     * view detached
     */
    default void onDetached() {}

    /**
     * 데이터 발생 경고창 승인
     */
    default void onNoDataWarningApproved() {}

    /**
     * brightcove fragmentOnResume callback 전달 용
     */
    default void onFragmentResume() {}
}