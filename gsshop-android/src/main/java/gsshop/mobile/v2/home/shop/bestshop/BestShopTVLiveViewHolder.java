/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.bestshop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import androidx.fragment.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.android.exoplayer2.Player;
import com.gsshop.mocha.ui.util.ViewUtils;

import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.flexible.BestDealBannerTVLiveViewHolder;
import gsshop.mobile.v2.home.shop.tvshoping.TvShopFragment;
import gsshop.mobile.v2.support.media.OnMediaPlayerController;
import gsshop.mobile.v2.support.media.OnMediaPlayerListener;
import gsshop.mobile.v2.support.media.model.MediaInfo;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.NetworkUtils;
import roboguice.util.Ln;

/**
 * TV쇼핑탭 tv live banner.
 *
 */
@SuppressLint("NewApi")
public class BestShopTVLiveViewHolder extends BestDealBannerTVLiveViewHolder implements OnMediaPlayerListener {

	//라이브톡 텍스트 롤링을 위한 변수 세팅
	private static final long TEXT_ROLLING_OFFSET_PERIOD = 2000;	//타이머 최초 구동시까지 대기 시간(2초)
	private static final long TEXT_ROLLING_INTERVAL = 3000;	//문구 교체 주기(3초)
	private Timer liveTalkTimer = null;
	private String[] liveTalkText;	//라이브톡 문구
	private int liveTalkIdx = 0;	//현재 노출된 인덱스
	private int liveTalkCnt = 0;	//문구 총 갯수
	private LinearLayout liveTalkLayout;	//라이브톡 레이아웃
	private TextSwitcher liveTalkSwitcher;
	private TextView totalCnt;
	private ViewSwitcher.ViewFactory liveTalkFactory;
	private OnMediaPlayerController playerController = null;
	private final Button play;
	private View info_view;

	private View pause;
	private View go_link;
	private View full_screen;


	private Context context;

	//GTM용 파라미터 정의
	private String action;
	private String label;
	private String sectionName;

	private boolean isMdiaPlayerResize = false;
	private Context mContext;
	/**
	 * 생성자
	 *
	 * @param itemView 레이아웃뷰
	 */
	public BestShopTVLiveViewHolder(View itemView) {
		super(itemView);

		liveTalkLayout = (LinearLayout) itemView.findViewById(R.id.live_talk);
		liveTalkSwitcher = (TextSwitcher) itemView.findViewById(R.id.switcher);
		totalCnt = (TextView) itemView.findViewById(R.id.totalCnt);
		info_view = itemView.findViewById(R.id.info_view);
		pause = itemView.findViewById(R.id.pause);
		go_link = itemView.findViewById(R.id.go_link);
		full_screen = itemView.findViewById(R.id.full_screen);
		play = (Button) itemView.findViewById(R.id.play);

		DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), info_view);
		Ln.i("registerSticky");
		EventBus.getDefault().register(this);
	}

	@Override
	public void onBindViewHolder(Context context, int position, final ShopInfo mInfo, String action, String label, String sectionName) {
		mInfo.tvLiveBanner.isTvShoping = true;
		this.mContext = context;
		super.onBindViewHolder(context, position, mInfo, action, label, sectionName);

		this.context = context;
		this.mInfo = mInfo;
		this.action = action;
		this.label = label;
		this.sectionName = sectionName;

		bindViewHolderLiveTalk();
	}

	private void bindViewHolderLiveTalk() {
		//JELLY_BEAN 이상 버전에서는 동영상플레이어 노출
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
			playerController = (OnMediaPlayerController) ((FragmentActivity) context).getSupportFragmentManager().findFragmentById(R.id.bestshop_media_player);
			if (playerController != null) {
				playerController.setOnMediaPlayerListener(this);
				MediaInfo media = playerController.getMediaInfo();
				if (media != null && mInfo.tvLiveBanner.livePlay != null) {
					media.videoId = mInfo.tvLiveBanner.livePlay.videoid;
					media.contentUri = mInfo.tvLiveBanner.livePlay.livePlayUrl;
					media.title = mInfo.tvLiveBanner.productName;
				}

				if (!isMdiaPlayerResize && playerController != null) {
					isMdiaPlayerResize = true;
					DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), playerController.getPlayerView());
				}

				if (playerController.isPlaying()) {
					root_view.setVisibility(View.GONE);
				} else {
					root_view.setVisibility(View.VISIBLE);
				}
			}
		}

		//오늘추천은 생방송 상태로 디폴트로 설정
		if( mInfo.tvLiveBanner != null ) {
			mInfo.tvLiveBanner.currentSubTabMenu = TvShopFragment.SubTabMenu.live;
		}

		//방송이 끝났을때 다음방송(livePlayYN)이 N 이면 동영상재생을 중지한다.
		if(mInfo.tvLiveBanner.livePlay != null && !"".equals(mInfo.tvLiveBanner.livePlay.livePlayUrl)
				&& "N".equalsIgnoreCase(mInfo.tvLiveBanner.livePlay.livePlayYN)){
			EventBus.getDefault().post(new Events.FlexibleEvent.TvBestLivePlayEvent(false, -1));
		}

		info_view.setVisibility(View.GONE);
//		//라이브톡 관련 정보가 모두 유효한 경우만 화면에 노출하고 텍스트 롤링을 시작한다.
//		if (mInfo.tvLiveBanner.liveTalkBanner != null
//				&& !TextUtils.isEmpty(mInfo.tvLiveBanner.liveTalkBanner.prdUrl)
//				&& mInfo.tvLiveBanner.liveTalkBanner.talkList != null
//				&& !mInfo.tvLiveBanner.liveTalkBanner.talkList.isEmpty()) {
//
//			liveTalkCnt = mInfo.tvLiveBanner.liveTalkBanner.talkList.size();
//			liveTalkText = mInfo.tvLiveBanner.liveTalkBanner.talkList.toArray(
//					new String[liveTalkCnt]);
//
//			if(mInfo.tvLiveBanner.liveTalkBanner.totalCnt > 0) {
//				if(mInfo.tvLiveBanner.liveTalkBanner.totalCnt > 999){
//					mInfo.tvLiveBanner.liveTalkBanner.totalCnt = 999;
//				}
//				totalCnt.setText(String.valueOf(mInfo.tvLiveBanner.liveTalkBanner.totalCnt));
//			}else{
//				totalCnt.setVisibility(View.GONE);
//			}
//
//			if (liveTalkFactory == null) {
//				liveTalkFactory = new ViewSwitcher.ViewFactory() {
//					@Override
// 					public View makeView() {
//						return (TextView) ((Activity) context).getLayoutInflater().inflate(R.layout.livetalk_text, null);
//					}
//				};
//				liveTalkSwitcher.setFactory(liveTalkFactory);
//			}
//
//			liveTalkSwitcher.setInAnimation(context, R.anim.livetalk_up_in);
//			liveTalkSwitcher.setOutAnimation(context, R.anim.livetalk_up_out);
//
//			//TV쇼핑탭 로딩시 라이브톡 텍스트영역 공백상태를 최소화 하기 위해 타이머 수행전에 테스트 먼저 세팅
//			if (liveTalkTimer == null) {
//				setLiveTalkRollingText();
//			}
//
//			//라이브톡 레이아웃 클릭시 라이브톡으로 이동 (아이콘+텍스트영역)
//			liveTalkLayout.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					WebUtils.goWeb(context, mInfo.tvLiveBanner.liveTalkBanner.prdUrl, ((Activity) context).getIntent());
//
//					//롤링텍스트 클릭시 와이즈로그 전송
//					((HomeActivity) context).setWiseLog(ServerUrls.WEB.LIVETALK_ROLLING_BANNER);
//
//					//GTM 클릭이벤트 전달
//					String label = GTMEnum.GTM_NONE;
//					String action = String.format("%s_%s_%s", GTMEnum.GTM_ACTION_HEADER,
//							sectionName, GTMEnum.GTM_ACTION_LIVE_LIVETALK_TAIL);
//					try {
//						label = Uri.parse(mInfo.tvLiveBanner.liveTalkBanner.prdUrl).getQuery();
//					} catch (Exception e) {
//						// 10/19 품질팀 요청
//						// - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
//						// - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
//						// - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
//						Ln.e(e);
//					}
//					GTMAction.sendEvent(context, GTMEnum.GTM_AREA_CATEGORY, action, label);
//				}
//			});
//			//라이브톡 영역을 노출한다.
//			ViewUtils.showViews(liveTalkLayout);
//		} else {
//			//API에서 라이브톡 관련 정보가 없거나 비정상적이면 라이브톡 영역을 노출하지 않는다.
//			ViewUtils.hideViews(liveTalkLayout);
//			stopRollingTextTimer();
//		}

		ViewUtils.hideViews(liveTalkLayout);

		info_view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mInfo.tvLiveBanner.livePlay != null && "N".equals(mInfo.tvLiveBanner.livePlay.livePlayYN)){
					layoutTvGoods.performClick();
				}
				info_view.setVisibility(View.GONE);
			}
		});

		pause.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				info_view.setVisibility(View.GONE);
				EventBus.getDefault().post(new Events.FlexibleEvent.TvBestLivePlayEvent(false, -1));
			}
		});
		go_link.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				info_view.setVisibility(View.GONE);
				layoutTvGoods.performClick();
			}
		});

		full_screen.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				info_view.setVisibility(View.GONE);
				playVideo((Activity)context, buildVideoParam(mInfo.tvLiveBanner.linkUrl, mInfo.tvLiveBanner.livePlay.videoid, mInfo.tvLiveBanner.livePlay.livePlayUrl));
			}
		});

		imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				layoutTvGoods.performClick();
			}
		});

		play.setOnClickListener(tvLivePlayClickListner);
	}

	/**
	 * 라이브톡 문구 교체를 위한 타이머를 시작한다.
	 */
	private void startRollingTextTimer() {
		stopRollingTextTimer();
		liveTalkTimer = new Timer();
		liveTalkTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				setLiveTalkRollingText();
			}
		}, TEXT_ROLLING_OFFSET_PERIOD, TEXT_ROLLING_INTERVAL);
	}

	/**
	 * 라이브톡 문구를 롤링시킨다.
	 */
	private void setLiveTalkRollingText() {
		((Activity)mContext).runOnUiThread(() -> {
			liveTalkIdx = liveTalkIdx < liveTalkCnt ? liveTalkIdx : 0;
			if (liveTalkCnt > 1) {
				//다음에 표시될 textview에 텍스트를 세팅하고 애니메이션을 수행하면서 textview가 교체된다.
				liveTalkSwitcher.setText(liveTalkText[liveTalkIdx]);
			} else {
				//현재 노출된 textview에 텍스트를 세팅한다. 이때 애니메이션은 수행되지 않는다.
				liveTalkSwitcher.setCurrentText(liveTalkText[liveTalkIdx]);
			}
			liveTalkIdx++;
		});
	}

	/**
	 * 라이브톡 문구 교체를 위한 타이머를 중지한다.
	 */
	private void stopRollingTextTimer() {
		if (liveTalkTimer != null) {
			liveTalkTimer.cancel();
			// 대기중이던 취소된 행위가 있는 경우 모두 제거
			liveTalkTimer.purge();
			liveTalkTimer = null;
		}
	}

	/**
	 * 라이브톡 텍스트롤링 타이머를 시작/정지 시킨다.
	 * (TvShopFragment onStart/onStop에서 이벤트를 전달한다.)
	 *
	 * @param event StartRollingLiveTalkTextEvent
	 */
	public void onEvent(Events.FlexibleEvent.StartRollingLiveTalkTextEvent event) {
		if (event.start) {
			if (liveTalkText != null && liveTalkText.length > 1) {
				startRollingTextTimer();
			}
		} else {
			stopRollingTextTimer();
		}
	}

	/**
	 * 라이브톡 텍스트롤링 영역 갱신
	 */
	@Override
	protected void updateLiveTalk() {
		bindViewHolderLiveTalk();
	}

	/**
	 * 생방송 실행.
	 *
	 * @param event event
	 */
	public void onEvent(Events.FlexibleEvent.TvBestLivePlayEvent event) {
		// auto play 설정 값은 무시한다. - 생방송 영역과 auto play 설정 영역은 서로 무관한 영역이에요 (임성남)
		if(!event.play){
			root_view.setVisibility(View.VISIBLE);
			info_view.setVisibility(View.GONE);
		}
		if (playerController != null) {
			MediaInfo media = playerController.getMediaInfo();

			if (media != null) {
				media.isPlaying = event.play;
				playerController.setMediaInfo(media);
				if(media.isPlaying) {
					playerController.playPlayer();
				} else {
					playerController.stopPlayer();
				}
			} else {
				media = new MediaInfo();
				if(mInfo.tvLiveBanner.livePlay != null) {
					media.videoId = mInfo.tvLiveBanner.livePlay.videoid;
					media.contentUri = mInfo.tvLiveBanner.livePlay.livePlayUrl;
					media.title = mInfo.tvLiveBanner.productName;
//				media.contentUri = "http://livem.gsshop.com/gsshop/_definst_/gsshop.sdp/playlist.m3u8";
//				media.title = "[코나드] 영롱해! 스탬핑 젤네일 주문하기";
					media.playerMode = MediaInfo.MODE_SIMPLE;
					media.currentPosition = 0;
					media.channel = MediaInfo.CHANNEL_MAIN_LIVE;
					media.isPlaying = event.play;
					media.lastPlaybackState = Player.STATE_IDLE;
					playerController.setMediaInfo(media);
					if(media.isPlaying) {
						playerController.playPlayer();
					} else {
						playerController.stopPlayer();
					}
				}
			}
		}
	}

	public void onEvent(Events.FlexibleEvent.TvLiveUnregisterEvent event) {
		Ln.i("registerSticky - unregister");
		EventBus.getDefault().unregister(this);
		root_view.setVisibility(View.GONE);
	}

	/**
	 * 뷰가 화면에 노출될때 발생
	 */
	@Override
	public void onViewAttachedToWindow() {

		// tv 쇼핑 위아래 스크롤을 하면 hardware codec 때문에 버퍼일이 발생
		// play를 중지하고 buffer를 클리어한 후에 생방송 실행.
		if(playerController != null) {
			MediaInfo info = playerController.getMediaInfo();
			if(info != null && info.isPlaying) {
				playerController.playPlayer();
				root_view.setVisibility(View.GONE);
				info_view.setVisibility(View.GONE);
			}
		}

		//라이브톡 텍스트롤링 타이머를 시작한다.
		if (liveTalkText != null && liveTalkText.length > 1) {
			startRollingTextTimer();
		}

		//생방송 남은시간 표시용 타이머를 시작한다.
		startTvLiveTimer();
	}

	public View.OnClickListener tvLivePlayClickListner = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			//플레이 버튼 클릭시 와이즈로그 전송
			((HomeActivity) mContext).setWiseLog(ServerUrls.WEB.BESTSHOP_LIVE_PLAY);
			NetworkUtils.confirmNetworkBillingAndShowPopup((HomeActivity) mContext, new NetworkUtils.OnConfirmNetworkListener() {
				@Override
				public void isConfirmed(boolean isConfirmed) {
					if (isConfirmed) {
						root_view.setVisibility(View.GONE);
						EventBus.getDefault().post(new Events.FlexibleEvent.TvBestLivePlayEvent(true, 0));
					}
				}

				@Override
				public void inCanceled() {}
			});
		}
	};

	/**
	 * 뷰가 화면에서 사라질때 발생
	 */
	@Override
	public void onViewDetachedFromWindow() {
//		EventBus.getDefault().unregister(this);

		//라이브톡 텍스트롤링 타이머를 시작한다.
		stopRollingTextTimer();

		//생방송 남은시간 표시용 타이머를 정지한다.
		stopTvLiveTimer();
	}

	/**
	 * OnMediaPlayerListener Methods
	 */
	@Override
	public void onTap(boolean show) {

		if(info_view.getVisibility() == View.GONE){
			info_view.setVisibility(View.VISIBLE);
		}

	}

	@Override
	public void onFullScreenClick(MediaInfo media) {

	}

	@Override
	public void onPlayed() {

	}

	@Override
	public void onPaused() {

	}

	@Override
	public void onFinished(MediaInfo media) {

	}

	@Override
	public void onError(Exception e) {
		root_view.setVisibility(View.VISIBLE);
		info_view.setVisibility(View.GONE);
	}
}
