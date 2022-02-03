package gsshop.mobile.v2.support.media;

import android.view.View;

import com.brightcove.player.view.BaseVideoView;

import gsshop.mobile.v2.support.media.model.MediaInfo;

/**
 * 동영상 플레이 컨트롤
 */
public interface OnMediaPlayerController {
    /**
     * 미디어 정보
     * @return
     */
    MediaInfo getMediaInfo();
    void setMediaInfo(MediaInfo media);

    /**
     * 플레이어 뷰
     */
    View getPlayerView();

    /**
     * 플레이어 뷰 숨김처리
     */
    default void hidePlayerView() {}

    /**
     * baseVideoView
     */
    default BaseVideoView getBaseVideoView() {return  null;}

    /**
     * 동영상 실행여부 확인
     * @return
     */
    boolean isPlaying();

    /**
     * 동영상 실행
     */
    void playPlayer();

    /**
     * 동영상 중지
     */
    void stopPlayer();

    /**
     * Media Player Listener의 콜백을 지정
     * @param callback
     */
    default void setOnMediaPlayerListener(OnMediaPlayerListener callback) {}


    /**
     * 배경색 변경
     * @param resid
     */
    default void setBackgroundResource(int resid) {}

    /**
     * 플레이어 컨트롤 표시/숨기기
     * @param show
     */
    default void showPlayControllerView(final boolean show) {}

    /**
     * 사운드 mute
     * @param mute
     */
    default void setMute(boolean mute) {}

    default void onTapClicked() {}

    /**
     * 3g 과금 버튼 실행.
     * @param isOK
     */
    default void performMobileDataButton(boolean isOK) {}

    /**
     * 현재 플레이어 replay
     */
    default void replayPlayer() {}

    /**
     * 현재 플레이어 상태값 return
     * @return
     */
    default int getPlayerState() {return -1;}

    /**
     * 해당 영상 컨트롤러를 보여줄지 여부 설정
     * @param isVisible
     */
    default void setControllerVisibility(boolean isVisible){}

    /**
     * 현재 설정된 비디오의 높이 / 넓이 값
     * @return
     */
    default int getVideoHeight() {return -1;}
    default int getVideoWidth() {return -1;}

    /**
     * 전체화면 끝났을 때에
     */
    default void onFullscreenDisabled(boolean isBroadcastFinished){}

    /**
     * 영상의 현재 위치 반환
     * @return 현재 위치값
     */
    default long getCurrentPosition(){return 0;}

    /**
     * 영상 시작 / 일시 정지
     */
    default void setVideoStart(){}
    default void setVideoPause(){}

    /**
     * 영상 플레이 중인지 여부
     * @return
     */
    default boolean getIsPlaying(){return false;}

    /**
     * 방송 종료 여부 확인
     * @return
     */
    default boolean getIsBroadcastFinished(){return false;}

    /**
     * 영상 사이즈를 조정해 주어야 한다. (memory 관리)
     * @param isLow
     */
    default void setPlayerSize(boolean isLow){}

    /**
     * 영상 모드 변경 
     * @param resizeMode 0 : 비율 유지, 1: 꽉차게, 2 : 세로에 맞게 3 : 가로에 맞게 , 4 : ZOOM 
     */
    default void setPlayerResizeMode(int resizeMode) {}

    /**
     * 백그라운드 색상 변경
     * @param color
     */
    default void setBackgroudnColor(int color) {}
    
    /**
     * 영상 오류로 인해 재로딩 수행.
     */
    default void reloadPlayer(){}
    default void resetPlayer(){}

    /**
     * 영상 release
     */
    default void release(){}

    /**
     * 영상 빠졌다가 다시 돌아왔을 때에 영상 스타트 여부
     */
    default void setPlayerWhenOnResume(boolean isStart) {}

    default void setUseController(boolean useController) {}

    default void setUseMuteFromGlobal (boolean useMuteFromGlobal) {}
}
