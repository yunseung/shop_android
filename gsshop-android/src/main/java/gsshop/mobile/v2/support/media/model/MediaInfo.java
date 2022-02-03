package gsshop.mobile.v2.support.media.model;

import gsshop.mobile.v2.web.productDetail.dto.NativeProductV2;

/**
 * 동영상 정보.
 */
public final class MediaInfo {
    public static final int MODE_SIMPLE = 0x10;
    public static final int MODE_FULL = 0x11;

    public static final int CHANNEL_NALBANG = 0x20;
    public static final int CHANNEL_LIVE_TALK = 0x21;
    public static final int CHANNEL_MAIN_LIVE = 0x22;

    public String title;
    public int playerMode;  // simple or full
    public int channel;     // broadcast channel

    public String videoId;
    public String contentUri;
    public long currentPosition;
    public int lastPlaybackState;
    public boolean isPlaying;
    public boolean isLandscape = true;
    public String liveNo;       //날방 번호
    public String posterImageUrl = null;   // 포스트 이미지.
    public int listPosition = -1;
    public int videoMode = NativeProductV2.VIDEO_MODE_VOD;
    public String startTime;
    public String endTime;

    public boolean isBroadcastFinished = false;
}
