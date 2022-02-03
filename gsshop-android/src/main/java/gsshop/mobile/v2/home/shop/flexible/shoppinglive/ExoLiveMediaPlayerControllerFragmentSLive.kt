package gsshop.mobile.v2.home.shop.flexible.shoppinglive

import android.os.Bundle
import androidx.fragment.app.Fragment
import gsshop.mobile.v2.support.media.exoplayer.ExoLiveMediaPlayerControllerFragment

class ExoLiveMediaPlayerControllerFragmentSLive : ExoLiveMediaPlayerControllerFragment() {
    // onResume 시에 플레이를 계속 할지 여부
    private var isMute = true;

    companion object {
        fun newInstance(videoUrl: String?, isPlaying: Boolean): Fragment {
            val fragment = ExoLiveMediaPlayerControllerFragmentSLive()
            val args = Bundle()
            args.putString(ARG_PARAM_VIDEO_URL, videoUrl)
            args.putBoolean(ARG_PARAM_IS_PLAYING, isPlaying)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onResume() {
        super.onResume()

        if (isPlayWhenOnResume) {
            setMute(isMute)
        }
    }

    override fun setMute(on: Boolean) {
        super.setMute(on)
        // 기존 볼륨이 볼륨을 기존 볼륨 설정하고 있는데 이렇게 하면 타이밍 이슈 발생한다.
        if (player != null) {
            if (on) {
                player.volume = 0f
            } else {
                player.volume = 1f
            }
        }
        isMute = on
    }
}