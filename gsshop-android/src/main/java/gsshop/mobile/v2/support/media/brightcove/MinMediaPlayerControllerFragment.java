package gsshop.mobile.v2.support.media.brightcove;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Matrix;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.brightcove.player.appcompat.BrightcovePlayerFragment;
import com.brightcove.player.display.ExoPlayerVideoDisplayComponent;
import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.mediacontroller.BrightcoveMediaController;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcoveExoPlayerTextureVideoView;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.gsshop.mocha.ui.util.ViewUtils;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.support.media.OnMediaPlayerController;
import gsshop.mobile.v2.support.media.OnMediaPlayerListener;
import gsshop.mobile.v2.support.media.model.MediaInfo;
import gsshop.mobile.v2.util.ImageUtil;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isEmpty;

/**
 * mp4 video play controller fragment
 */
@SuppressLint("NewApi")
public class MinMediaPlayerControllerFragment extends BrightcovePlayerFragment implements OnMediaPlayerController {

    private static final String ARG_PARAM_VIDEO_ID = "_arg_param_video_id";   //비디오번호
    private static final String ARG_PARAM_VIDEO_URL = "_arg_param_video_url";
    private static final String ARG_PARAM_THUMBNAIL_URL = "_arg_param_thumbnail_url";
    private static final String ARG_PARAM_A_URL = "_a";

    private boolean playerNeedsPrepare;

    private OnMediaPlayerListener callback;


    private ProgressBar loadingProgress;
    private ImageView thumbImage;
    private TextView positionText;
    private View rootView;


    // vidoe id
    private String videoId;

    // vidoe url
    private String videoUrl;

    // thumnail Url
    private String thumbUrl;

    // play Position
    private String mPosition;


    private SimpleExoPlayer simpleExoPlayer;

    public static boolean isMute = true;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    // TODO: Rename and change types and number of parameters
    public static MinMediaPlayerControllerFragment newInstance(String videoId, String videoUrl, String thumbUrl, String position) {
        MinMediaPlayerControllerFragment fragment = new MinMediaPlayerControllerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_VIDEO_ID, videoId);
        args.putString(ARG_PARAM_VIDEO_URL, videoUrl);
        args.putString(ARG_PARAM_THUMBNAIL_URL, thumbUrl);
        args.putString(ARG_PARAM_A_URL, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            videoId = getArguments().getString(ARG_PARAM_VIDEO_ID);
            videoUrl = getArguments().getString(ARG_PARAM_VIDEO_URL);
            thumbUrl = getArguments().getString(ARG_PARAM_THUMBNAIL_URL);
            mPosition = getArguments().getString(ARG_PARAM_A_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
// Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_min_media_player_controller, container, false);
        baseVideoView = result.findViewById(R.id.brightcove_video_view);
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        return result;
        // Inflate the layout for this fragment
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rootView = view.findViewById(R.id.root_view);
        loadingProgress = view.findViewById(R.id.mc_webview_progress_bar);
        thumbImage = view.findViewById(R.id.image_thumb);
        positionText = view.findViewById(R.id.text_position);

        //미디어컨트롤러 노출안함
        baseVideoView.setMediaController((BrightcoveMediaController) null);

        // thumbnail
        ImageUtil.loadImageTvLive(getActivity(), thumbUrl, thumbImage, R.drawable.noimg_logo_sb);

        showLoadingProgress(false);

        //videoId가 videoUrl보다 우선순위
        if (!TextUtils.isEmpty(videoId) && videoId.length() > 4) {
            // Get the event emitter from the SDK and create a catalog request to fetch a video from the
            // Brightcove Edge service, given a video id, an account id and a policy key.
            EventEmitter eventEmitter = baseVideoView.getEventEmitter();
            Catalog catalog = new Catalog(eventEmitter, getString(R.string.account), getString(R.string.policy));
            catalog.findVideoByID(videoId, new VideoListener() {

                // Add the video found to the queue with add().
                // Start playback of the video with start().
                @Override
                public void onVideo(Video video) {
                    Ln.v("onVideo: video = " + video);
                    baseVideoView.clear();
                    baseVideoView.add(video);
                }
            });
            Ln.i("video : " + videoId);
        } else if (!TextUtils.isEmpty(videoUrl)) {
            Video video = Video.createVideo(videoUrl);
            baseVideoView.clear();
            baseVideoView.add(video);
            Ln.i("video : " + videoUrl);
        } else {
            //여러 동영상중 1개 잘못된 경우 노출 가능한 영상은 노출
            Toast.makeText(getActivity(), R.string.video_play_error, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        baseVideoView.getEventEmitter().on(EventType.DID_SET_VIDEO, new EventListener() {
            @Override
            public void processEvent(Event event) {
                ExoPlayerVideoDisplayComponent displayComponent = (ExoPlayerVideoDisplayComponent) baseVideoView.getVideoDisplay();
                ExoPlayer exoPlayer = displayComponent.getExoPlayer();
                if (exoPlayer instanceof SimpleExoPlayer) {
                    simpleExoPlayer = (SimpleExoPlayer) exoPlayer;
                    simpleExoPlayer.addListener(new PlayerEventListener());
                    simpleExoPlayer.addVideoListener(new com.google.android.exoplayer2.video.VideoListener() {
                        @Override
                        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
                            final TextureView textureView = ((BrightcoveExoPlayerTextureVideoView) baseVideoView).getTextureView();
                            float viewWidth = rootView.getWidth();
                            float viewHeight = rootView.getMeasuredHeight();
                            if (width == 0 || height == 0 || viewWidth == 0 || viewHeight == 0) {
                                return;
                            }
                            float scaleX = 1.0f;
                            float scaleY = 1.0f;

                            float videoRatio = (float) width / height;
                            float viewRatio = viewWidth/viewHeight;

                            if(width != 0 && viewWidth != 0) {
                                scaleX = viewWidth / width;
                            }

                            if(height !=0 && viewHeight !=0) {
                                scaleY = viewHeight / height;
                            }


                            Ln.i("scale - " + scaleX + ", " + scaleY + ", video - " + width + ", " + height + ", textureView - " + viewWidth + ", " + viewHeight + ", videoAspectRatio - " + videoRatio + ", viewRatio - " + viewRatio);

                            if (videoRatio == viewRatio) {
                                scaleX = 1.0f;
                                scaleY = 1.0f;
                            } else if (scaleX > scaleY) {
                                float expectedHeight = viewWidth / videoRatio;
                                scaleY = expectedHeight / viewHeight;
                                scaleX = 1.0f;
                            } else if (scaleY > scaleX) {
                                float expectedWidth = videoRatio * viewHeight;
                                scaleX = expectedWidth / viewWidth;
                                scaleY = 1.0f;
                            }
                            Ln.i("scale - " + scaleX + ", " + scaleY + ", video - " + width + ", " + height + ", textureView - " + viewWidth + ", " + viewHeight + ", videoAspectRatio - " + videoRatio + ", viewRatio - " + viewRatio);

                            // Calculate pivot points, in our case crop from center
                            int pivotPointX = (int) (viewWidth / 2);
                            int pivotPointY = (int) (viewHeight / 2);

                            Matrix matrix = new Matrix();
                            matrix.setScale(scaleX, scaleY, pivotPointX, pivotPointY);

//                            textureView.setTransform(matrix);
                        }

                        @Override
                        public void onRenderedFirstFrame() {

                        }
                    });
                }

                //음소거는 DID_SET_VIDEO 이후에 수행해야 함
                setMute(isMute);
            }
        });


        baseVideoView.getEventEmitter().on(EventType.SET_VIDEO_STILL, new EventListener() {
            @Override
            public void processEvent(Event event) {
                URI videoStillUrl = (URI) baseVideoView.getCurrentVideo().getProperties().get("stillImageUri");
                if (isEmpty(videoStillUrl)) {
                    event.preventDefault();
                    event.stopPropagation();
                }
            }
        });


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (callback != null) {
            return;
        }

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            callback = (OnMediaPlayerListener) context;
        } catch (ClassCastException e) {
            callback = null;
        }
    }

    private class PlayerEventListener extends Player.DefaultEventListener {

        @Override
        public void onLoadingChanged(boolean isLoading) {
            /*boolean playWhenReady = simpleExoPlayer.getPlayWhenReady();
            int playbackState = simpleExoPlayer.getPlaybackState();
            if (playWhenReady) {
                showLoadingProgress(true);
            }*/
        }


        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            switch (playbackState) {
                case Player.STATE_BUFFERING:
                    if (playWhenReady) {
                        showLoadingProgress(true);
                    }
                    break;
                case Player.STATE_ENDED:
                    ViewUtils.showViews(thumbImage);
                    if (callback != null) {
                        callback.onFinished(null);
                    }
                    break;
                case Player.STATE_IDLE:
                case Player.STATE_READY:
                    if (playWhenReady) {
                        // play
                        ViewUtils.hideViews(thumbImage);
                        showLoadingProgress(false);
                        if (callback != null) {
                            callback.onPlayed();
                        }
                    } else {
                        // pause
//                    ViewUtils.showViews(thumbImage);
                    }
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onPositionDiscontinuity(@Player.DiscontinuityReason int reason) {
//            if (player.getPlaybackError() != null) {
//                // The user has performed a seek whilst in the error state. Update the resume position so
//                // that if the user then retries, playback resumes from the position to which they seeked.
//                updateStartPosition();
//            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException e) {
            playerNeedsPrepare = true;
        }

        @Override
        @SuppressWarnings("ReferenceEquality")
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        }
    }

    @Override
    public MediaInfo getMediaInfo() {
        return null;
    }

    @Override
    public void setMediaInfo(MediaInfo media) {

    }

    @Override
    public View getPlayerView() {
        return getView();
    }

    /**
     * @param callback callback
     */
    @Override
    public void setOnMediaPlayerListener(OnMediaPlayerListener callback) {
        if (callback != null) {
            this.callback = callback;
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getView() != null && !isVisibleToUser) {
            // background pause
            stopPlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // background pause
        stopPlayer();
    }

    @Override
    public void playPlayer() {
        if (getView() != null) {
            baseVideoView.start();
        }
    }

    @Override
    public void stopPlayer() {
        baseVideoView.pause();
        baseVideoView.stopPlayback();
    }


    private void showLoadingProgress(boolean show) {
        if (show) {
            ViewUtils.showViews(loadingProgress);
        } else {
            ViewUtils.hideViews(loadingProgress);
        }
    }

    /**
     * 음소거 여부를 세팅한다.
     *
     * @param mute if true, mute
     */
    @Override
    public void setMute(boolean mute) {
        isMute = mute;

        if (simpleExoPlayer == null) {
            return;
        }

        float volume = mute ? 0 : 1;
        Map properties = new HashMap<>();
        properties.put(Event.VOLUME, volume);
        baseVideoView.getEventEmitter().emit(EventType.SET_VOLUME, properties);
    }

    /**
     * player state
     *
     * @return boolean
     */
    @Override
    public boolean isPlaying() {
        if (simpleExoPlayer == null) {
            return false;
        }
        int playState = simpleExoPlayer.getPlaybackState();
        boolean playWhenReady = simpleExoPlayer.getPlayWhenReady();
        return playState == Player.STATE_READY && playWhenReady;
    }

    public boolean isPause() {
        if (simpleExoPlayer == null) {
            return false;
        }
        int playState = simpleExoPlayer.getPlaybackState();
        boolean playWhenReady = simpleExoPlayer.getPlayWhenReady();
        return playState == Player.STATE_READY && !playWhenReady;
    }

    public boolean isBuffering() {
        if (simpleExoPlayer == null) {
            return false;
        }

        int playState = simpleExoPlayer.getPlaybackState();
        boolean playWhenReady = simpleExoPlayer.getPlayWhenReady();
        return playState == Player.STATE_BUFFERING && playWhenReady;
    }

    public boolean isEnded() {
        if (simpleExoPlayer == null) {
            return false;
        }

        int playState = simpleExoPlayer.getPlaybackState();
//        boolean playWhenReady = player.getPlayWhenReady();
        return playState == Player.STATE_ENDED;
    }
}
