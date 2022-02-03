/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2;

import android.app.Activity;
import android.graphics.RectF;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.attach.PhotoItem;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.shop.PlayerAction;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.user.User;

/**
 * {@link EventBus}를 통해 모듈간 이벤트를
 * 주고받기 위한 이벤트 클래스 모음.
 * <p>
 * NOTE : https://github.com/greenrobot/EventBus
 * <p>
 * TODO 이벤트를 던지고 받는 영역을 프리젠테이션 레이어로 한정지을 것인가?
 * (현재는 액션에서도 던지고 받고 있음)
 */
public class Events {

    /**
     * 로그인 이벤트
     */
    public static final class LoggedInEvent {
        /**
         * 로그인 이벤트 전달 파라미터
         */
        public User user;

        /**
         * 생성자
         *
         * @param user
         */
        public LoggedInEvent(User user) {
            this.user = user;
        }
    }

    /**
     * 로그아웃 이벤트
     */
    public static final class LoggedOutEvent {
    }

    /**
     * 자동로그인 재시도 완료 이벤트
     */
    public static final class AutoLoggedInEvent {
        public boolean isSuccess = false;

        public AutoLoggedInEvent(boolean isSuccess) {
            this.isSuccess = isSuccess;
        }
    }

    /**
     * 자동 로그인 성공 이벤트 (갱신 해 주어야 하는 경우가 존재하기 때문에 추가)
     */
    public static final class AutoLogInSuccessEvent {
    }

    /**
     * 자동로그인 재시도 수행필요 이벤트
     */
    public static final class AutoLogInRetryEvent {
    }

    /**
     * 로그완료시 토스트 메시지 노출
     */
    public static final class LoggedInToastEvent {
        public String msg;

        public LoggedInToastEvent(String msg) {
            this.msg = msg;
        }
    }

    /**
     * 로그인 완료시 비밀번호변경 화면 노출
     */
    public static final class LoggedInUpdatePassEvent {
        public String url;
        public boolean isTvMember = false;

        public LoggedInUpdatePassEvent(String url, boolean isTvMember) {
            this.url = url;
            this.isTvMember = isTvMember;
        }
    }

    /**
     * 바이오 정보(지문인식) 연동 확인 팝업 노출
     */
    public static final class PopupFingerPrintEvent {
        public boolean isOn = false;

        public PopupFingerPrintEvent(boolean inOn) {
            this.isOn = isOn;
        }
    }

    /**
     * 뱃지 새로고침 이벤트
     */
    public static final class BadgeInfoFlushedEvent {
    }

    /**
     * gallery event
     */
    public static abstract class GalleryEvent {
        /**
         * 글라이드 로드 이벤트 버스
         */
        public static final class LoadImageGridEvent {
            /**
             * 대상 아이템
             */
            public List<PhotoItem> items;

            /**
             * 생성자
             *
             * @param items 대상
             */
            public LoadImageGridEvent(List<PhotoItem> items) {
                this.items = items;
            }

        }

        /**
         * 백 이벤트
         */
        public static final class GoBackEvent {

        }

    }

    /**
     * flexible event
     */
    public static abstract class FlexibleEvent {

        /**
         * 시그니처매장 브랜드 더보기 이벤트
         */
        public static class SignatureBrandMoreEvent {
            public String url;
            public int position;

            public SignatureBrandMoreEvent(String url, int position) {
                this.url = url;
                this.position = position;
            }
        }

        /**
         * 방송종료 이벤트
         */
        public static final class BroadCloseEvent {
        }

        /**
         * GSChoice 뷰홀더와 해더 포지션 동기화용
         * fvPosition 과 offset이 0보다 작으면 위치는 이동하지 않는다.
         */
        public static class GSChoiceSyncPositionEvent {
            public String navigationId;
            public int offset = 0;
            public int fvPosition = 0;
            public int selectedItemIndex = 0;

            public GSChoiceSyncPositionEvent(String navigationId, // 일치하는 Navi ID
                                             int offset, // 이동할 offset,
                                             int fvPosition, // 처음 보여줄 position
                                             int selItemIndex ) {// 이동할 index
                this.navigationId = navigationId;
                this.offset = offset;
                this.fvPosition = fvPosition;
                this.selectedItemIndex = selItemIndex;
            }
        }

        /**
         * GSChoice 카테고리 스크롤 정지용
         */
        public static class GSChoiceCateStopScrollEvent {
        }

        /**
         * GSChoice 매장 스크롤 정지용
         */
        public static class GSChoiceStopScrollEvent {
        }

        /**
         * GSChoice 데이타 자동 페이징용
         */
        public static final class GSChoiceCategoryEvent {
            public boolean isHeader;
            public String url;

            public GSChoiceCategoryEvent(boolean isHeader, String url) {
                this.isHeader = isHeader;
                this.url = url;
            }
        }

        /**
         * StartRollingNo1BestDealBannerEvent 대카테고리 자동 롤링용
         */
        public static final class StartRollingGSChoiceRollBannerEvent {
            public boolean start;

            public StartRollingGSChoiceRollBannerEvent(boolean start) {
                this.start = start;
            }
        }

        /**
         * 업데이트 이벤트
         */
        public static final class UpdateBestShopEvent {
            /**
             * 탭 인덱스 정보
             */
            public final int tab;
            /**
             * 대상 URL
             */
            public final String url;

            /**
             * 탭 네임
             */
            public final String tabName;

            /**
             * 사전에 불리웠는지 확인을 위한 변수 추가, 이 변수가 true일 때에
             * Cache 확인 true일 때에 이미 불린 fragment에 대해서는 다시 부르지 않는다.
             */
            public final boolean isCache;

            /**
             * 당겨서 새로고침 했을 때 여부
             */
            public final boolean isSwipeRefresh;

            /**
             * 생성자
             *
             * @param tab     인덱스
             * @param url     유알엘
             * @param tabName 탭 네임
             */
            public UpdateBestShopEvent(int tab, String url, String tabName) {
                this(tab, url, tabName, true);
            }

            /**
             * @param isCache 캐시 데이터 여부
             */
            public UpdateBestShopEvent(int tab, String url, String tabName, boolean isCache) {
                this(tab, url, tabName, isCache, false);
            }

            /**
             * 당겨서 새로고침 여부
             *
             * @param isSwipeRefresh
             */
            public UpdateBestShopEvent(int tab, String url, String tabName, boolean isCache, boolean isSwipeRefresh) {
                this.tab = tab;
                this.url = url;
                this.tabName = tabName;
                this.isCache = isCache;
                this.isSwipeRefresh = isSwipeRefresh;
            }
        }

        /**
         * 플렉서블 매장 업데이트 이벤트
         */
        public static final class UpdateFlexibleShopEvent {


            /**
             * 선택 탭
             *
             * @param tab
             * @param listPosition
             */
            public UpdateFlexibleShopEvent(int tab, int listPosition) {
                this.tab = tab;
                this.listPosition = listPosition;
            }

            /**
             * 탭 인덱스 정보
             */
            public final int tab;
            /**
             * 리스트 위치.
             */
            public final int listPosition;
        }


        /**
         * GSX 카테고리 클릭 이벤트
         */
        public static final class GsXClickEvent {
            public GsXClickEvent (int position) {
                mPosition = position;
            }
            private final int mPosition;
            public int getPosition() {return mPosition;}
        }

        /**
         * Row 업데이트 이벤트?
         */
        public static final class UpdateRowEvent {
            /**
             * 로우 인덱스
             */
            public int row;

            /**
             * 생성자
             *
             * @param row 대상로우
             */
            public UpdateRowEvent(int row) {
                this.row = row;
            }
        }

        /**
         * StartRollingNo1BestDealBannerEvent
         */
        public static final class StartRollingNo1BestDealBannerEvent {
            /**
             * 시작 여부
             */
            public boolean start;

            /**
             * 생성자,
             *
             * @param start Flag
             */
            public StartRollingNo1BestDealBannerEvent(boolean start) {
                this.start = start;
            }
        }

        /**
         * 동영상 재생 이벤트
         */
        public static final class VideoPlayEvent {
            /**
             * 재생 여부
             */
            public final boolean play;
            /**
             * 인덱스
             */
            public final int position;

            /**
             * 생성자
             *
             * @param play     play여부
             * @param position 인덱스
             */
            public VideoPlayEvent(boolean play, int position) {
                this.play = play;
                this.position = position;
            }
        }

        /**
         * StartRollingLiveTalkTextEvent
         */
        public static final class StartRollingLiveTalkTextEvent {
            /**
             * 시작 여부
             */
            public boolean start;

            /**
             * 생성자
             *
             * @param start
             */
            public StartRollingLiveTalkTextEvent(boolean start) {
                this.start = start;
            }
        }

        /**
         * TV쇼핑탭 초기화 이벤트
         */
        public static final class ResetItemEvent {
            /**
             * 생성자
             */
            public ResetItemEvent() {
            }
        }

        /**
         * 애드뷰 이벤트
         */
        public static final class AdTooltipEvent {
            /**
             * 위치
             */
            public final RectF rect;
            /**
             * 보이기
             */
            public final boolean isShow;

            public AdTooltipEvent(RectF rect, boolean isShow) {
                this.rect = rect;
                this.isShow = isShow;
            }
        }

        /**
         * GSSuper 장바구니 팝업 이벤트
         */
        public static final class CartPopupEvent {
            public final static int STATE_SUCCESS = 0;
            public final static int STATE_ALREADY_EXISTS = 1;
            /**
             * 위치
             */
            public final int state;
            /**
             * 보이기
             */
            public final boolean isShow;

            public String isFresh;

            public CartPopupEvent(int state, boolean isShow, String isFresh) {
                this.state = state;
                this.isShow = isShow;
                this.isFresh = isFresh;
            }
        }

        /**
         * 업데이트 이벤트 (GS super 전용)
         */
        public static final class UpdateGSSuperEvent {
            /**
             * 탭 인덱스 정보
             */
            public final int tab;
            /**
             * 대상 URL
             */
            public final String url;

            /**
             * 탭 네임
             */
            public final String tabName;

            /**
             * 생성자
             *
             * @param tab     인덱스
             * @param url     유알엘
             * @param tabName 탭 네임
             */
            public UpdateGSSuperEvent(int tab, String url, String tabName) {
                this.tab = tab;
                this.url = url;
                this.tabName = tabName;
            }

            public UpdateGSSuperEvent() {
                this.tab = -1;
                this.url = null;
                this.tabName = null;
            }
        }

        /**
         * gs fresh 매장 내 검색 keyword clear
         */
        public static class GSFreshClearKeywordEvent {
            public final Activity activity;

            public GSFreshClearKeywordEvent(Activity activity) {
                this.activity = activity;
            }
        }

        /**
         * GSSuper 탭메뉴 클릭 이벤트 전달
         */
        public static final class GSSuperSectionClickEvent {
            public String sectionCode;

            public GSSuperSectionClickEvent(String sectionCode) {
                this.sectionCode = sectionCode;
            }
        }

        /**
         * GSSuper 스크롤시 탭메뉴 선택영역 동기화용
         */
        public static final class GSSuperSectionSyncEvent {
            public String sectionCode;

            public GSSuperSectionSyncEvent(String sectionCode) {
                this.sectionCode = sectionCode;
            }
        }

        /**
         * GSSuper 매장 스크롤 정지용
         */
        public static class GSSuperStopScrollEvent {
        }

        /**
         * 앵커를 커먼으로 쓰게끔 하기 위해서 흠 나중에 정리가 필요해 보인다.
         */
        public static abstract class EventWine {
            public static class StopScrollEvent {
            }

            public static final class SectionClickEvent {
                public String tabSeq;
                public SectionClickEvent(String tabSeq) {
                    this.tabSeq = tabSeq;
                }
            }

            public static final class SectionSyncEvent {
                public String sectionCode;
                public boolean mIsUp;

                public SectionSyncEvent(String sectionCode, boolean isUp) {
                    this.sectionCode = sectionCode;
                    this.mIsUp = isUp;
                }
            }

            public static class WineClearKeywordEvent {
                public final Activity activity;

                public WineClearKeywordEvent(Activity activity) {
                    this.activity = activity;
                }
            }
        }

        /**
         * GSSuper 매장 당일배송/새벽배송 전환용
         */
        public static final class GSSuperToogleEvent {
            public final String url;

            public GSSuperToogleEvent(String url) {
                this.url = url;
            }
        }

        /**
         * TVLiveBanner(Hoder) 동영상 이벤트
         */
        public static class TvLivePlayEvent {
            /**
             * 재생 여부
             */
            public final boolean play;
            /**
             * 위치
             */
            public final int position;

            /**
             * 생성자
             *
             * @param play     여부
             * @param position 위치
             */
            public TvLivePlayEvent(boolean play, int position) {
                this.play = play;
                this.position = position;
            }
        }

        /**
         * BestShopTvLiveViewHolder 재생 이벤트
         */
        public static class TvBestLivePlayEvent {
            /**
             * 재생 여부
             */
            public final boolean play;
            /**
             * 위치
             */
            public final int position;

            /**
             * 생성자
             *
             * @param play     플레이 여부
             * @param position 위치
             */
            public TvBestLivePlayEvent(boolean play, int position) {
                this.play = play;
                this.position = position;
            }
        }

        /**
         * bestdeal player controll event
         */
        public static class BestdealLivePlayEvent {
            public enum PLAY_STATE {
                PLAY, PAUSE, STOP
            }

            /**
             * 재생 여부
             */
            public final PLAY_STATE playState;
            /**
             * 호출한 곳
             */
            public final int playerIdx;


            /**
             * 생성자
             *
             * @param playState
             * @param playerIdx
             */
            public BestdealLivePlayEvent(PLAY_STATE playState, int playerIdx) {
                this.playState = playState;
                this.playerIdx = playerIdx;
            }
        }

        /**
         * mobilelive player controll event
         */
        public static class MobileLivePlayEvent {
            public enum PLAY_STATE {
                PLAY, PAUSE, STOP
            }

            /**
             * 재생 여부
             */
            public final PLAY_STATE playState;


            /**
             * 생성자
             *
             * @param playState
             */
            public MobileLivePlayEvent(PLAY_STATE playState) {
                this.playState = playState;
            }
        }

        /**
         * bestdeal 생방송과 모바일라이브간 트리거용 이벤트
         */
        public static class BestdealTriggerEvent {
            public enum BROAD_TYPE {
                LIVE, //생방송 or 데이타방송
                MOBILELIVE  //모바일라이브 방송
            }

            /**
             * 방송종류
             */
            public final BROAD_TYPE broadType;

            /**
             * 생성자
             *
             * @param broadType
             */
            public BestdealTriggerEvent(BROAD_TYPE broadType) {
                this.broadType = broadType;
            }
        }

        /**
         * TLineLiveViewHolder(Hoder) 동영상 이벤트
         */
        public static class SchLivePlayEvent {
            /**
             * 재생 여부
             */
            public final boolean play;
            /**
             * 위치
             */
            public final int position;
            /**
             * 네비게이션으로부터 호출여부
             */
            public boolean fromNavi = false;

            /**
             * 생성자
             *
             * @param play     여부
             * @param position 위치
             */
            public SchLivePlayEvent(boolean play, int position, boolean fromNavi) {
                this.play = play;
                this.position = position;
                this.fromNavi = fromNavi;
            }
        }

        /**
         * TV편성표 남은시간 노출용 타이머 시작/정지 이벤트
         */
        public static final class SchLiveTimerEvent {
            /**
             * 시작 여부
             */
            public boolean start;

            /**
             * 생성자
             *
             * @param start 시작 여부
             */
            public SchLiveTimerEvent(boolean start) {
                this.start = start;
            }
        }

        /**
         * 생방송 남은시간 노출용 타이머 시작/정지 이벤트
         */
        public static final class TvLiveTimerEvent {
            /**
             * 시작 여부
             */
            public boolean start;

            /**
             * 생성자
             *
             * @param start 시작 여부
             */
            public TvLiveTimerEvent(boolean start) {
                this.start = start;
            }
        }

        /**
         * 생방송 남은시간 노출용 타이머 시작/정지 이벤트 (날방)
         */
        public static final class NalbangTimerEvent {
            /**
             * 시작 여부
             */
            public boolean start;

            /**
             * 생성자
             *
             * @param start 시작 여부
             */
            public NalbangTimerEvent(boolean start) {
                this.start = start;
            }
        }

        /**
         * NalbangHashTagUpdate
         */
        public static final class NalbangHashTagUpdate {
            /**
             * 대상 헤쉬 태그
             */
            public final String hashTag;

            /**
             * 생성자
             *
             * @param hashTag
             */
            public NalbangHashTagUpdate(String hashTag) {
                this.hashTag = hashTag;
            }
        }

        /**
         * NalbangHashTagTabUpdate
         */
        public static final class NalbangHashTagTabUpdate {
            /**
             * 대상 해쉬 태그 탭 인덱스 정보
             */
            public final int tab;

            /**
             * 생성자
             *
             * @param tab 대상 인덱스
             */
            public NalbangHashTagTabUpdate(int tab) {
                this.tab = tab;
            }
        }

        /**
         * ShortbangTabUpdate
         */
        public static final class ShortbangTabUpdate {
            /**
             * 대상 인덱스
             */
            public final int tab;

            /**
             * 생성자
             *
             * @param tab 탭 인덱스
             */
            public ShortbangTabUpdate(int tab) {
                this.tab = tab;
            }
        }

        /**
         * ShortbangReadmore
         */
        public static final class ShortbangReadmore {

            /**
             * 숏방 플레그먼트 더보기
             */
            public ShortbangReadmore() {
            }
        }

        /**
         * NalbangReadmore
         */
        public static final class NalbangReadmore {

            /**
             * 날방 플레그먼트 더보기
             */
            public NalbangReadmore() {
            }
        }

        /**
         * GSSuperReadmore
         */
        public static final class GSSuperReadmore {

            /**
             * GS Super 플레그먼트 더보기
             */
            public GSSuperReadmore() {
            }
        }

        /**
         * TvLiveUnregisterEvent
         */
        public static final class TvLiveUnregisterEvent {
        }

        /**
         * 바로구매 버튼 클릭 시, tv쇼핑 배너 최상단으로 이동시키는 이벤트
         */
        public static final class DirectBuyEvent {
            /**
             * shopInfo
             */
            public ShopInfo shopInfo;
            /**
             * 포지션
             */
            public int position;

            /**
             * 생성자
             *
             * @param position 위치
             * @param shopInfo 구조체 정보
             */
            public DirectBuyEvent(int position, ShopInfo shopInfo) {
                this.position = position;
                this.shopInfo = shopInfo;
            }
        }

        /**
         * 장바구니 카운트 업데이트 이벤트
         */
        public static final class RefreshCart {
        }


        /**
         * 타이머가 완료 되었음을 알려주는 이벤트 버스 이름
         */
        public static class SendPrdCSqWiseLogEvent {
        }


        /**
         * 편성요약 영역 "ON"버튼 노출/숨김 처리용 이벤트
         */
        public static final class OnAirEvent {
            /**
             * 노출유무
             */
            public boolean isVisible;

            /**
             * 생성자
             *
             * @param isVisible 노출유무
             */
            public OnAirEvent(boolean isVisible) {
                this.isVisible = isVisible;
            }
        }

        public static final class MoveSubGroupCdEvent {
            public String mGroupCd;

            public MoveSubGroupCdEvent(String groupCd) {
                this.mGroupCd = groupCd;
            }
        }

        public static final class RefreshTabEvent {
            private String mUrl;
            private int mRemovePosition;
            private int mClickPosition;
            private String mHeaderViewType;
//            private ArrayList mDelViewType;

            /**
             * 탭을 선택했을때에 기존 데이터를 삭제하고 갱신한 데이터를 넣는다.
             * @param url               갤신 URL
             * @param clickPosition     선택한 탭 위치
             * @param removePosition    삭제할 위치
             * @param tabViewType       탭 뷰타입
             */
            public RefreshTabEvent(String url, int clickPosition, int removePosition,
                                   String tabViewType){ //, ArrayList delViewType) {
                mUrl = url;
                mClickPosition = clickPosition;
                mRemovePosition = removePosition;
                mHeaderViewType = tabViewType;
//                mDelViewType = delViewType;
            }
            public String getUrl() {return mUrl;}
            public int getRemovePosition() {return mRemovePosition;}
            public int getClickPosition() {return mClickPosition;}
            public String getTabViewType() {return mHeaderViewType;}
//            public ArrayList getDelViewType() {return mDelViewType;}
        }
    }

    /**
     * 키보드 보이기/감추기 이벤트.
     */
    public static final class NalTalkEvent {
        /**
         * 키보드 보이기 유무
         */
        public boolean show;

        /**
         * 생성자
         *
         * @param show 유무
         */
        public NalTalkEvent(boolean show) {
            this.show = show;
        }
    }

    /**
     * 라이브톡 키보드 보이기/감추기 이벤트.
     */
    public static final class LiveTalkEvent {
        /**
         * 여부
         */
        public boolean show;

        /**
         * 생성자
         *
         * @param show 여부
         */
        public LiveTalkEvent(boolean show) {
            this.show = show;
        }
    }

    /**
     * 와이즈로그 이벤트.
     */
    public static final class WiseLogEvent {
        /**
         * 와이즈로그 호출 Url
         */
        public String url;

        /**
         * 생성자
         *
         * @param url
         */
        public WiseLogEvent(String url) {
            this.url = url;
        }
    }

    /**
     * 메인탭이동 이벤트.
     */
    public static final class MainTabEvent {

        /**
         * 탭의 유니크 정보 00053, 00054등
         */
        public String sectionCode;

        /**
         * 생성자
         *
         * @param sectionCode 탭의 유니크 정보
         */
        public MainTabEvent(String sectionCode) {
            this.sectionCode = sectionCode;
        }
    }

    /**
     * 메인탭 확대/축소 이벤트
     */
    public static final class MainTabExpandEvent {
        /**
         * 더보기 (true), 접기(false)
         */
        public boolean expand = true;

        public MainTabExpandEvent(boolean expand) {
            this.expand = expand;
        }
    }

    /**
     * 메인탭이동 이벤트.
     */
    public static final class ShortbangMoveTopEvent {
    }

    /**
     * 프로그레스 숨김 이벤트.(바로구매에서 사용)
     */
    public static final class HideProgressEvent {
    }

    /**
     * 숏방 바로구매 (로그인 후 호출)
     */
    public static final class DirectOrderAfterLoginEvent {

        /**
         * 바로그 구매 호출 대상
         */
        public String url;

        /**
         * 생성자
         *
         * @param url 대상 Url
         */
        public DirectOrderAfterLoginEvent(String url) {
            this.url = url;
        }
    }

    /**
     * 모바일라이브 채팅화면 (로그인 후 호출)
     */
    public static class MobileLiveAfterLoginEvent {
    }

    /**
     * 타이머가 완료 되었음을 알려주는 이벤트 버스 이름
     */
    public static class TimerEvent {
    }

    /**
     * 하단탭메뉴에 최근 본 상품 갱신 이벤트
     */
    public static final class LastPrdUpdateEvent {
    }

    /**
     * 네비게이션 이벤트
     */
    public static final class NavigationCloseEvent {
    }

    public static final class NavigationCloseDrawerEvent {
    }

    public static final class NavigationUpdateEvent {
    }

    public static final class NavigationReflashEvent {
    }

    public static final class NavigationLoginEvent {
    }

    public static final class NavigationDefaultEvent {
    }

    public static final class WebHistoryBackEvent {
    }

    /**
     * 새벽배송 툴팁을 띄우기 위한 이벤트
     */
    public static final class NightDeliveryTooltipEvent {
        public boolean isShow;

        public NightDeliveryTooltipEvent(boolean isShow) {
            this.isShow = isShow;
        }
    }

    /**
     * TV편성표 방송알림 등록 이벤트
     */
    public static class AlarmRegistEvent {
        public String caller;
        public String prdId;
        public String prdName;
        public String period;
        public String times;

        public AlarmRegistEvent(String caller, String prdId, String prdName, String period, String times) {
            this.caller = caller;
            this.prdId = prdId;
            this.prdName = prdName;
            this.period = period;
            this.times = times;
        }
    }

    /**
     * TV편성표 방송알림 업데이트 이벤트
     */
    public static class AlarmUpdatetEvent {
        public String prdId;
        public boolean isRegisted;

        public AlarmUpdatetEvent(String prdId, boolean isRegisted) {
            this.prdId = prdId;
            this.isRegisted = isRegisted;
        }
    }

    /**
     * 모바일라이브 방송알림 등록 이벤트
     */
    public static class AlarmRegistMLEvent {
        public String caller; //모라 생방송 플레이어 or 쇼핑라이브 탭매장 구분위한 caller
        public String type; //등록 or 해제 구분을위한 type
        public String prdId;
        public String prdName;
        public String naviId;
        public boolean isNightAlarm;


        public AlarmRegistMLEvent(String caller, String type, String prdId, String prdName, String naviId, boolean isNightAlarm) {
            this.caller = caller;
            this.type = type;
            this.prdId = prdId;
            this.prdName = prdName;
            this.naviId = naviId;
            this.isNightAlarm = isNightAlarm;
        }
    }

    /**
     * 모바일라이브 탭매장 신설
     * 모바일라이브 알람여부에 따른 UI업데이트 용도의 이벤트
     */
    public static class AlarmUpdatetMLEvent {
        public String eventType;
        public boolean isRegisted;
        public String naviId;

        public static String MAKE_ALARM_OK = "makeAlarmOk"; //알람등록한 상태로 UI업데이트
        public static String MAKE_ALARM_CANCEL = "makeAlarmCancel"; //알람해제한 상태로 UI업데이트

        public AlarmUpdatetMLEvent(String eventType, boolean isRegisted, String naviId) {
            this.eventType = eventType;
            this.isRegisted = isRegisted;
            this.naviId = naviId;
        }
    }

    /**
     * CSP 이벤트.
     */
    public static final class CspEvent {
        /**
         * 네비게이션 아이디
         */
        public String naviId;

        /**
         * 이미지 주소
         */
        public String imageUrl;

        /**
         * 링크 주소
         */
        public String linkUrl;

        /**
         * 상품명 (접근성 용도)
         */
        public String productName;

        /**
         * 효율측정값
         */
        public String aid;

        /**
         * 생성자
         *
         * @param naviId   네비게이션 아이디
         * @param imageUrl 이미지 주소
         * @param linkUrl  링크 주소
         * @param aid      효율측정값
         */
        public CspEvent(String naviId, String imageUrl, String linkUrl, String aid) {
            this.naviId = naviId;
            this.imageUrl = imageUrl;
            this.linkUrl = linkUrl;
            this.aid = aid;
        }
    }

    /**
     * 기본 팝업 이벤트
     */
    public static final class BasicPopupEvent implements Serializable {
        public final String contents;
        public final String url;
        public final String strBtnLeft, strBtnRight;
        public final int delayMillis;
        public final int btnNumber;

        public BasicPopupEvent(String contents, String url, String btnLeft, String btnRight, int delayMillis) {
            this(contents, url, btnLeft, btnRight, delayMillis, -1);
        }

        public BasicPopupEvent(String contents, String url, String btnLeft, String btnRight, int delayMillis, int btnNumber) {
            this.contents = contents;
            this.url = url;
            this.strBtnLeft = btnLeft;
            this.strBtnRight = btnRight;
            this.delayMillis = delayMillis;
            this.btnNumber = btnNumber;
        }
    }

    /**
     * vod 매장 이벤트
     */
    public static final class VodShopPlayerEvent {

        public enum VodPlayerAction {
            VOD_SCROLLING,  // 리스트 스크롤중
            VOD_STOP,   // 동영상 중지
            VOD_AUTO_PLAY,   // 자동 동영상 실행 - 나머지 실행중인 동영상은 일시멈춤
            VOD_BUTTON_PLAY,    // 버튼 눌러 동영상 실행.
            VOD_PAUSE,  // 동영상 일시정지
            VOD_MOBILE_DATA,  // 3G 과금 팝업 표시되는 플레이어는 빼고 나머지 플레이어는 popup 중지.
            VOD_MUTE,                // 글로벌 사운드 mute
            VOD_BACK_TO_FOR, //background->forground
            VOD_INIT_OTHERS
        }

        public final VodPlayerAction action;
        public final int position;
        public final boolean on;

        public final int firstPosition;
        public final int lastPosition;

        public VodShopPlayerEvent(VodPlayerAction action) {
            this(action, -1, false, -1, -1);
        }

        public VodShopPlayerEvent(VodPlayerAction action, boolean on) {
            this(action, -1, on, -1, -1);
        }

        public VodShopPlayerEvent(VodPlayerAction action, int position) {
            this(action, position, false, -1, -1);
        }

        public VodShopPlayerEvent(VodPlayerAction action, int firstPosition, int lastPosition) {
            this(action, -1, false, firstPosition, lastPosition);
        }

        public VodShopPlayerEvent(VodPlayerAction action, int position, boolean on, int firstPosition, int lastPosition) {
            this.action = action;
            this.position = position;
            this.on = on;
            this.firstPosition = firstPosition;
            this.lastPosition = lastPosition;
        }

    }

    /**
     * 이미지캐싱 시작 신호
     */
    public static class ImageCacheStartEvent {
        /**
         * 네비게이션 아이디
         */
        public String naviId;

        public ImageCacheStartEvent(String naviId) {
            this.naviId = naviId;
        }
    }

    /**
     * 딜단품 이벤트
     */
    public static final class EventProductDetail {
        public static final class HideHeaderEvent {
            public boolean isHide = false;

            public HideHeaderEvent(boolean isHide) {
                this.isHide = isHide;
            }
        }

        public static final class BroadAlertEvent {
            public boolean isOn = false;

            public BroadAlertEvent(boolean isOn) {
                this.isOn = isOn;
            }
        }

        /**
         * 단품 웹뷰에서 layerPopup 이 떴을 경우에,
         * 네이티브 영역으로 web-layerPopup 에서 native 로 전달되는 터치를 막기 위한 이벤트.
         */
        public static final class LayerPopupEvent {
            public boolean isOpen = false;
            /**
             * 딜 자세히보기때에만 딤을 노출하지 않기 위한 플래그
             */
            public boolean enableTitleDim = true;

            public LayerPopupEvent(boolean isOpen) {
                this.isOpen = isOpen;
            }

            public LayerPopupEvent(boolean isOpen, boolean enableTitleDim) {
                this.isOpen = isOpen;
                this.enableTitleDim = enableTitleDim;
            }
        }

        /**
         * 단품 웹뷰에서 dim이 없는 layerPopup을 호출 했을 때에 실행되는 이벤트
         */
        public static final class NoDimmLayerOpenEvent {
            public boolean isOpen = false;

            public NoDimmLayerOpenEvent(boolean isOpen) {
                this.isOpen = isOpen;
            }
        }

        /**
         * 찜 성공/실패 여부 이벤트
         */
        public static final class ZzimResponseEvent {
            public boolean isChecked = true;

            public ZzimResponseEvent(boolean isChecked) {
                this.isChecked = isChecked;
            }
        }

        // 현재 단품 플레이 정지.
        public static final class PlayStopEvent {
        }

        public static final class MutePlayerEvent {
            public boolean mIsMute = true;

            public MutePlayerEvent(boolean isMute) {
                mIsMute = isMute;
            }
        }

        public static final class NativeTouchEvent {
            public boolean throwTouch = false;

            public NativeTouchEvent(boolean throwTouch) {
                this.throwTouch = throwTouch;
            }
        }

        /**
         * 로그인 후 장바구니, 구매하기 웹뷰 호출용
         */
        public static final class GoWebAfterLoginEvent {
            public String url;

            public GoWebAfterLoginEvent(String url) {
                this.url = url;
            }
        }

    }

    /**************************************
     *      GS 혜택 리뉴얼
     ***************************************/
    /**
     * flexible event
     */
    public static abstract class EventOnEvent {
        /**
         * GS 혜택 탭메뉴 클릭 이벤트 전달
         */
        public static final class EventSectionClickEvent {
            public String tabSeq;
            public String navigationId;

            public EventSectionClickEvent(String navigationId, String tabSeq) {
                this.tabSeq = tabSeq;
                this.navigationId = navigationId;
            }
        }

        public static final class EventSectionSyncEvent {
            public String sectionCode;
            public String navigationId;

            public EventSectionSyncEvent(String navigationId, String sectionCode) {
                this.sectionCode = sectionCode;
                this.navigationId = navigationId;
            }
        }
    }

    /**
     * 당겨서 새로고침 시에 이동하면 기존에 refresh 하던 녀석 초기화.
     */
    public static class EventSwipeRefreshStop {
    }

    /**
     * 동영상 풀스크린에서 매장뷰홀더로 와이즈로그 이벤트를 받기 위한 이벤트
     */
    public static final class VideoActionEvent {
        public PlayerAction playerAction;
        public String videoId;
        public int playTime;
        public int totalTime;

        public VideoActionEvent(PlayerAction playerAction, String videoId, int playTime, int totalTime) {
            this.playerAction = playerAction;
            this.videoId = videoId;
            this.playTime = playTime;
            this.totalTime = totalTime;
        }
    }

    /**
     * L 타입 앵커 추적 이벤트
     */
    public static final class ClickEventLtypeAnchor {

        public int selectedPosition;
        public String tabSeq;
        public String navigationId;
        public int offsetDp;

        public ClickEventLtypeAnchor(String navigationId, int position, String tabSeq, int offsetDp) {
            this.selectedPosition = position;
            this.tabSeq = tabSeq;
            this.navigationId = navigationId;
            this.offsetDp = offsetDp;
        }
    }

    /**
     * 매장 리로딩이 필요한 경우 사용
     */
    public static class RefreshShopEvent {
    }

    /**
     * PRD_C_CST_SQ 뷰타입
     * 상품 -> 홈, 장바구니 -> 홈 이벤트
     */
    public static final class psnlCurationEvent{
        public HomeActivity.PsnlCurationType psnlCurationType; // 상품 -> 홈 이면 "PRD", 장바구니 -> 홈 이면 "CART"
        /**
         * true : 캐쉬된 데이터 보지 않고, API호출후, 데이터를 캐쉬하고 화면은 그리지 않는다.
         * false : 캐쉬된 데이터가 있으면 API 호출 안함, 없으면? API 호출후,  데이터를 캐쉬하지 않고 화면은 그리겠다.
         */
        public boolean prdCacheFlag = false; //API호출후, 데이터를 캐쉬하고 화면은 그리지 않는다. TRUE

        public psnlCurationEvent(HomeActivity.PsnlCurationType _psnlCurationType,boolean _prdCacheFlag){
            this.psnlCurationType = _psnlCurationType;
            this.prdCacheFlag = _prdCacheFlag;
        }
    }

    /**
     * 쇼핑 라이브 갱신 이벤트 (다음 방송 시간 타이머가 0이 될때에 갱신 수행, isTimedOut이 false 이면 해당 리스트만 갱신)
     */
    public static abstract class ShoppingLiveEvent {

        public static final class MobileLiveSldRefreshEvent {
            public boolean isTimedOut;

            public MobileLiveSldRefreshEvent(boolean isTimedOut) {
                this.isTimedOut = isTimedOut;
            }
        }

        public static final class LivePlayEvent {
            public boolean isPlay;
            public LivePlayEvent(boolean _isPlay) {
                this.isPlay = _isPlay;
            }
        }

        public static final class RemoveLivePlayerEvent {
            public int mPosition;
            public RemoveLivePlayerEvent(int position) {
                this.mPosition = position;
            }
        }
    }

    /**
     * 뷰티 탭 이벤트
     */
    public static abstract class BeautyShopEvent {
        /**
         * 뷰티 탭을 직접 선택했을때 갱신 이벤트
         */
        public static final class BeautyRefreshEvent {
            private String mUrl;
            private int mRemovePosition;
            private Integer mClickPositionTom;
            private List<Integer> mClickPositionBest;
            private int mDelViewType;
            public BeautyRefreshEvent(String url, Integer clickPosition, List<Integer> clickBest,
                                      int removePosition, int delViewType) {
                mUrl = url;
                mClickPositionTom = clickPosition;
                mClickPositionBest = clickBest;
                mRemovePosition = removePosition;
                mDelViewType = delViewType;
            }
            public String getUrl() {return mUrl;}
            public int getRemovePosition() {return mRemovePosition;}
            public int getClickPositionTom() {return mClickPositionTom;}
            public List<Integer> getClickPositionBest() {return mClickPositionBest;}
            public int getDelViewType() {return mDelViewType;}
        }

        /**
         * 더보기를 통해서 이동 했을 때의 이벤트
         */
        public static final class BeautyMoveTabEvent{
            private String mTabSeq;
            private String mLinkUrl;
            public BeautyMoveTabEvent(String tabSeq, String linkUrl) {
                mTabSeq = tabSeq;
                mLinkUrl = linkUrl;
            }
            public String getTabSeq() {return mTabSeq;}
            public String getLinkUrl() {return mLinkUrl;}
        }
    }

    /**
     * GS Exclusive 매장 이벤트
     */
    public static abstract class GSExcusiveEvent {
        public static final class TabRefreshEvent{
            private String url;
            public TabRefreshEvent(String _url) {
                url = _url;
            }
            public String getUrl() {return url;}
        }
    }
}
