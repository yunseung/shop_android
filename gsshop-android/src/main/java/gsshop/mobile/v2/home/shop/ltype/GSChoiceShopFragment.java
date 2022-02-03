package gsshop.mobile.v2.home.shop.ltype;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.inject.Inject;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.pattern.mvc.BaseAsyncController;
import com.gsshop.mocha.ui.util.ViewUtils;
import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.AbstractBaseActivity;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.home.CustomRecyclerView;
import gsshop.mobile.v2.home.GroupSectionList;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.HomeGroupInfo;
import gsshop.mobile.v2.home.TopSectionList;
import gsshop.mobile.v2.home.main.ModuleList;
import gsshop.mobile.v2.home.main.ModuleListRoot;
import gsshop.mobile.v2.home.shop.BaseViewHolderV2;
import gsshop.mobile.v2.home.shop.ViewHolderType;
import gsshop.mobile.v2.home.shop.event.renewal.Holder.EventShopTabViewHolder;
import gsshop.mobile.v2.home.shop.tvshoping.MainShopFragment;
import gsshop.mobile.v2.library.quickreturn.QuickReturnRecyclerViewOnScrollListener;
import gsshop.mobile.v2.library.quickreturn.QuickReturnType;
import gsshop.mobile.v2.library.quickreturn.library.QuickReturnUtils;
import gsshop.mobile.v2.support.ui.BugFixedStaggeredGridLayoutManager;
import gsshop.mobile.v2.util.ClickUtils;
import gsshop.mobile.v2.util.DataUtil;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.NetworkUtils;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;
import static com.blankj.utilcode.util.EmptyUtils.isEmpty;
import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;
import static com.gsshop.mocha.BaseApplication.getAppContext;
import static gsshop.mobile.v2.MainApplication.useNativeProduct;
import static gsshop.mobile.v2.home.shop.ViewHolderType.VIEW_TYPE_BAN_TITLE_PRD_GBA;
import static gsshop.mobile.v2.home.shop.ViewHolderType.VIEW_TYPE_BAN_TXT_CST_GBA;

public class GSChoiceShopFragment extends MainShopFragment {

    protected static final String ARG_PARAM_POSITION = "_arg_param_position";

    /**
     * API 호출자
     */
    public enum CALLER {
        CATEGORY, //카테고리 선택시
        READMORE, //스크롤 하단 이동시
        BRANDMORE, //브랜드 더보기 클릭시
        DEFAULT //매장진입시
    }

    /**
     * Event header 탭과 하단상품 매핑용
     */
    public Map<String, Integer> tabPrdMapLType = new HashMap<String, Integer>();
    public Map<String, Integer> tabPrdMapEvent = new HashMap<String, Integer>();

    // anchGba는 앵커에만 정상적인 값이 들어오기 때문에 앵커일 때에만 map 추가
    public ArrayList<String> arrAnchTabSeq = new ArrayList<>();
    /**
     * 전체 상하 리스트
     */
    @InjectView(R.id.recycler_gschoice_goods)
    protected CustomRecyclerView mRecyclerView;

    /**
     * 갱신 뷰
     */
    @InjectView(R.id.flexible_refresh_layout)
    private View mRefreshView;

    /**
     * 가장 상단으로 이동 버튼
     */
    @InjectView(R.id.btn_gschoice_top)
    private ImageView topButton;

    /**
     * 리스트 개수 표시 뷰
     */
    @InjectView(R.id.frame_gschoice_count)
    private View countView;

    /**
     * 새로고침 버튼
     */
    @InjectView(R.id.btn_refresh)
    private FrameLayout mBtnRefresh;
    /**
     * 웹으로 이동 버튼 (네트워크 장애 시)
     */
    @InjectView(R.id.btn_go_web)
    private FrameLayout mBtnGoWeb;

    protected StaggeredGridLayoutManager mLayoutManager;

    protected GSChoiceShopAdapter mAdapter;

    protected boolean isPageLoading = false;

    protected TopSectionList sectionList = null;

    protected LinearLayout header;
    protected BaseViewHolderV2 headerViewHolder;

    /**
     * 2단뷰의 경우 리스트 가공을 위해 위치를 가지고 있는다.
     */
    private int mPositionFirstMapCxGbc = -1;
    private int mPositionFirstPrd2 = -1;

    // 헤더 위치를 따라가기 위해서 값을 저장해 놓는다.
    private int headerPosition = -1;

    /**
     * 매장 네비게이션 id (비교 위해 필요)
     */
    protected String mNavigationID = "";

    /**
     * 더보기 API 주소
     * 매장 로딩 후 최초 더보기는 ajaxTabPrdListUrl 사용하고 카테고리 변경후 부터는 API결과의 ajaxfullUrl 사용
     */
    private String readMoreUrl = "";

    /**
     * 당겨서 새로고침 할 레이아웃
     */
    protected SwipeRefreshLayout mLayoutSwipe;

    // quick return
    protected QuickReturnRecyclerViewOnScrollListener scrollListener;

    // 팝업 윈도우
    private PopupWindow mPopupWindowCart;
    private View mPopupViewGSSuper;
    // 장바구니 팝업 출력을 위한 핸들러 선언
    private Runnable mRunnablePopupclose;
    private Handler mHandlerPopupClose;

    // 마지막 tabseq 확인용
    private String mLastTabSeq;

    public static GSChoiceShopFragment newInstance(int position) {
        GSChoiceShopFragment fragment = new GSChoiceShopFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public GSChoiceShopFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPosition = getArguments().getInt(ARG_PARAM_POSITION);
        }

        HomeGroupInfo homeGroupInfo = AbstractBaseActivity.getHomeGroupInfo();
        GroupSectionList groupSectionList = homeGroupInfo.groupSectionList.get(0);


        //키바나 수집 로그로 앱크래쉬 방지  (java.lang.IndexOutOfBoundsException: Index: 18, Size: 18)
        //본 익셉션은 HomeMenuCommand 수정으로 해결이 예상되어 7.23 배포 후 로그수집여부 확인 예정
        //7.23 배포 후 발생건수는 현저히 줄었으나 완전히 사라지지는 않음(23일~28일 3건 발생) -> 예외처리 추가함
        try {
            sectionList = groupSectionList.sectionList.get(mPosition); // 홈 하위 하나의 인덱스 요청
            mNavigationID = sectionList.navigationId;
        } catch (IndexOutOfBoundsException e) {
            Ln.e(e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gschoice_shop, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EventBus.getDefault().register(this);

        if (isEmpty(sectionList)) {
            return;
        }

        mLayoutManager = new BugFixedStaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(mLayoutManager);

        setAdapter();

        header = view.findViewById(R.id.header);
        header.setVisibility(View.GONE);

        ViewUtils.hideViews(mRefreshView, topButton);
        ViewUtils.hideViews(mRefreshView, topButton);

        setScrollListener();

        mBtnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                drawFragment();
            }
        });

        mBtnGoWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goWebIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ServerUrls.MOBILE_WEB));
                startActivity(goWebIntent);
            }
        });

        // 플렉서블 뿐 아니라 L타입 영역에도 터치시 이벤트가 추가되어야 한다.
        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent event) {
                if (rv == null || event.getAction() != MotionEvent.ACTION_UP) {
                    return false;
                }
                try {
                    View view = rv.findChildViewUnder(event.getX(), event.getY());
                    BaseViewHolderV2 vh = (BaseViewHolderV2) mRecyclerView.getChildViewHolder(view);
                    if (vh.getItemViewType() == ViewHolderType.VIEW_TYPE_API_SRL ||
                            vh.getItemViewType() == ViewHolderType.VIEW_TYPE_PRD_C_SQ ||
                            vh.getItemViewType() == ViewHolderType.VIEW_TYPE_MOLOCO_PRD_C_SQ ||
                            vh.getItemViewType() == ViewHolderType.VIEW_TYPE_PRD_C_CST_SQ ||
                            vh.getItemViewType() == ViewHolderType.VIEW_TYPE_PMO_T1_PREVIEW_D ||
                            vh.getItemViewType() == ViewHolderType.VIEW_TYPE_PMO_T1_PREVIEW_B ||
                            vh.getItemViewType() == ViewHolderType.VIEW_TYPE_PRD_C_B1
                    ) {
                        vh.setOnTouchUp();
                    }
                } catch (ClassCastException exception) {
                    Ln.e(exception.getMessage());
                } catch (NullPointerException exception) {
                    Ln.e(exception.getMessage());
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });

        setQuickReturn();

        drawFragment();

        // 당겼을 때 동작 등 추가 위한 함수
        setRefreshLayout(view);

        mPopupViewGSSuper = view;
        View viewPopupCart = LayoutInflater.from(getActivity()).inflate(R.layout.layout_popup_cart, null);
        mPopupWindowCart = new PopupWindow(viewPopupCart,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindowCart.setAnimationStyle(0);

        this.mHandlerPopupClose = new Handler();
        this.mRunnablePopupclose = () -> {
            if (mPopupWindowCart.isShowing()) {
                mPopupWindowCart.dismiss();
            }
        };
    }

    protected void setAdapter() {
        try {
            mAdapter = new GSChoiceShopAdapter(getContext(), sectionList.navigationId);
            mRecyclerView.setAdapter(mAdapter);
        } catch (Exception e) {
            Ln.e(e);
        }
    }

    protected void setScrollListener() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == SCROLL_STATE_DRAGGING) {
                    EventBus.getDefault().post(new Events.FlexibleEvent.GSChoiceCateStopScrollEvent());
                }

                //스크롤 멈춘시점에 이미지 캐싱 진행
                if (useNativeProduct) {
                    if (newState == SCROLL_STATE_IDLE) {
                        EventBus.getDefault().post(new Events.ImageCacheStartEvent(sectionList.navigationId));
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                traceHeaderView(dy > 0);

                traceLoadMore(dy > 0);
            }

            private void traceLoadMore(boolean isUp) {
                if (headerPosition < 0 || !isUp) {
                    return;
                }

                int lvPosition = mLayoutManager.findLastVisibleItemPositions(null)[0];
                if (lvPosition >= mLayoutManager.getItemCount() - 2) {
                    if (ClickUtils.work(1000)) {
                        return;
                    }
                    if (isNotEmpty(readMoreUrl)) {
                        //스크롤 하단 접근시 데이타를 추가로 로딩한다.
                        new GetUpdateController(getActivity()).execute(readMoreUrl, "", "", false, false, -1, CALLER.READMORE);
                    }
                }
            }
        });
    }

    /**
     * 해더 뷰홀더를 검색하여 노출/숨김 처리를 수행한다.
     *
     * @param isUp if true, scroll up
     */
    private void traceHeaderView(boolean isUp) {
        // headerViewHolder가 정의 되어 있지 않다면 트레이스 하지 말아야 한다.
        if (headerViewHolder == null) {
            return;
        }

        if ((header.getVisibility() == View.VISIBLE  && isUp) || // 헤더 a 의 경우 앵커가 아니기 때문에 보이고 있으면서 올리고 있을때는 계산할 필요가 없어
                (header.getVisibility() != View.VISIBLE && !isUp)) { // 앵커이거나 아니거나 상관없이 내리고 있는데 헤더가 보이지 않는다면 계산할 필요가 없다.
            if (!(headerViewHolder instanceof TabSldAnkGbaVH) &&
                    !(headerViewHolder instanceof EventShopTabViewHolder) &&
                    !(headerViewHolder instanceof TabSldAnkGbbVH) &&
                    !(headerViewHolder instanceof TabImgAnchGbaVH)
            ) {
                return;
            }
        }

        int firstVisiblePosition = mLayoutManager.findFirstVisibleItemPositions(null)[0];
        int lastVisiblePosition = mLayoutManager.findLastVisibleItemPositions(null)[0];

        // traceHeaderViewProc 하기 전에 리스트가 올라가는 상황인데 위에 이미 스티키 뷰가 지나가는지 확인해야한다.
        // 아래에서는 차일드만 확인해서 빠르게 넘어가면 잡지 못한다.
        View v = mLayoutManager.findViewByPosition(firstVisiblePosition);

        // 이벤트 일경우. (GS 혜택일 경우)
        if (headerViewHolder instanceof EventShopTabViewHolder) {
            if (isNotEmpty(v)) {
                int position = mRecyclerView.getChildAdapterPosition(v);
//            Ln.d("traceHeaderView firstVisible adapter position lastVisiblePosition : " + lastVisiblePosition);
                for (int i=position; i > 0; i-- ) {
                    if (mAdapter.getItemViewType(i) == ViewHolderType.VIEW_TYPE_TAB_ANK_GBA ||
                            mAdapter.getItemViewType(i) == ViewHolderType.VIEW_TYPE_TAB_ANK_GBA_1ST) {
                        header.setVisibility(View.VISIBLE);

                        String sectionCode = mAdapter.getItemSectionCode(i);
                        if (sectionCode != null) {
                            EventBus.getDefault().postSticky(new Events.EventOnEvent.EventSectionSyncEvent(mNavigationID, sectionCode));
                        }
                        return;
                    }
                }
            }
            header.setVisibility(View.GONE);
            return;
        }

        // GS 혜택 외일 경우.
        if (isNotEmpty(v) && isUp) {
            int position = mRecyclerView.getChildAdapterPosition(v);
//            Ln.d("traceHeaderView firstVisible adapter position lastVisiblePosition : " + lastVisiblePosition);
            for (int i = 0; i <= position; i++) {
                if (mAdapter.isHeaderView(mAdapter.getItemViewType(i))) {

                    header.setVisibility(View.VISIBLE);

                    if (mAdapter.getItemViewType(i) == ViewHolderType.VIEW_TYPE_TAB_SLD_ANK_GBA ||
                            mAdapter.getItemViewType(i) == ViewHolderType.VIEW_TYPE_TAB_SLD_ANK_GBB ||
                            mAdapter.getItemViewType(i) == ViewHolderType.VIEW_TYPE_TAB_IMG_ANCH_GBA ) {
                        // 앵커면 헤더 보이고 여부를 떠나서 앵커 동작까지 해주어야 한다.
                        break;
                    }
                    else {
                        return;
                    }
                }
            }
        }

//        Ln.d("traceHeaderView traceHeaderView lastVisiblePosition : " + lastVisiblePosition);
        for (int i = firstVisiblePosition; i < lastVisiblePosition; i++) {
            traceHeaderViewProc(i, isUp);
            traceHeaderViewSelect(i, isUp);
        }

    }

    /**
     * 해더 뷰홀더를 검색하여 노출/숨김 처리를 수행한다. (실행로직)
     *
     * @param position 뷰홀더 포지션
     * @return true : 상태 변경됨 / false : 상태 변경되지 않음.
     */
    private void traceHeaderViewProc(int position, boolean isUp) {
        // 리사이클러 뷰의 사각형.
        Rect rvRect = new Rect();
        mRecyclerView.getGlobalVisibleRect(rvRect);

        // 해당 아이템 뷰의 크기.
        Rect rowRect = new Rect();
        View v = mLayoutManager.findViewByPosition(position);
        if (isEmpty(v)) {
            return;
        }
        BaseViewHolderV2 vh = (BaseViewHolderV2) mRecyclerView.getChildViewHolder(v);
        vh.itemView.getGlobalVisibleRect(rowRect);

        if (mAdapter.isHeaderView(vh.getItemViewType())
            || vh instanceof TabSldAnkGbaVH) {

            int rowHeight = vh.itemView.getHeight();

            if (rowRect.top <= rvRect.top && isUp) {
                header.setVisibility(View.VISIBLE);
            } else if ((rowRect.bottom - rvRect.top) >= rowHeight && !isUp) {
                header.setVisibility(View.GONE);
                if (vh instanceof TabSldAnkGbaVH ||
                    vh instanceof TabSldAnkGbbVH ||
                    vh instanceof TabImgAnchGbaVH) {
                    vh.setSelectedItem(0);
                }
            }
        }
    }

    /**
     * 기존에는 헤더 선택과 헤더 보일지 여부를 같이 정했는데, 자꾸 이상한 조건의 헤더가 들어오니까 조건도 바뀌고 점점 복잡해 짐에 따라서 선택과 visible 여부를 나누어 놓음.
     * @param position
     * @param isUp
     * @return
     */
    private boolean traceHeaderViewSelect(int position, boolean isUp) {
        Rect rvRect = new Rect();
        mRecyclerView.getGlobalVisibleRect(rvRect);

        // 해당 아이템 뷰의 크기.
        Rect rowRect = new Rect();
        View v = mLayoutManager.findViewByPosition(position);
        if (isEmpty(v)) {
            return false;
        }
        BaseViewHolderV2 vh = (BaseViewHolderV2) mRecyclerView.getChildViewHolder(v);
        vh.itemView.getGlobalVisibleRect(rowRect);

        /*
        신규 혜택 일때만 위치 비교 후 선택 되게끔 한다.
         */
        if (headerViewHolder instanceof TabImgAnchGbaVH) {
            String fixedTabSeq = null;
//            for ( String key : arrAnchTabSeq) {
            int topOfList = rvRect.top;
            try {
                int headerHeight = ((TabImgAnchGbaVH) headerViewHolder).mLayoutOver3.getVisibility() == View.VISIBLE ?
                        DisplayUtils.convertDpToPx(getContext(), 110) : DisplayUtils.convertDpToPx(getContext(), 88);
                topOfList += headerHeight;

                for( int i = 0; i <arrAnchTabSeq.size(); i ++ ) {
                    // 올라가는 중일때에는
                    if(isUp) {
                        String key = arrAnchTabSeq.get(i);
                        // 현재 위치의 포지션이 넘어갈때에 탭을 바꿔준다.

                        Integer tempPrdMapLType = tabPrdMapLType.get(key);
                        if (isEmpty(tempPrdMapLType)) {
                            continue;
                        }

                        if (position >= tempPrdMapLType &&
                                rowRect.top < topOfList) {
                            fixedTabSeq = key;
                        }
                    }
                    // 아래로 내려가는 중일때에는
                    else {
                        int prevI = i - 1 < 0 ? 0 : i - 1;
                        String key = arrAnchTabSeq.get(i);
                        String keyPrev = arrAnchTabSeq.get(prevI);

                        Integer tempPrdMapLType = tabPrdMapLType.get(key);
                        Integer tempPrdMapLTypePrev = tabPrdMapLType.get(keyPrev);
                        if (isEmpty(tempPrdMapLType) || isEmpty(tempPrdMapLTypePrev)) {
                            continue;
                        }

                        if (position <= tempPrdMapLType &&
                                position > tempPrdMapLTypePrev &&
                                rowRect.top > topOfList) {
                            fixedTabSeq = keyPrev;
                            break;
                        }
                    }
                }
            }
            catch (NullPointerException | ClassCastException e) {
                Ln.e(e.getMessage());
            }

            if (!TextUtils.isEmpty(fixedTabSeq) ) {
                if (fixedTabSeq.equalsIgnoreCase(mLastTabSeq)) {
                    return true;
                }
                else if (headerViewHolder.setSelectedTabPosition(fixedTabSeq)) {
                    mLastTabSeq = fixedTabSeq;
                    return true;
                }
            }
        }
        // 신규 혜택 외의 리스트
        else {
            String tabSeq = vh.getTabSeq();
            if (!TextUtils.isEmpty(tabSeq)) {

                // 마지막 Tab 확인하여 같으면 바꾸는 동작 하지 않는다.
                if(tabSeq.equalsIgnoreCase(mLastTabSeq)) {
                    return true;
                }

                int screenHeight = DisplayUtils.getScreenHeight();
                if (rowRect.top < screenHeight) {
                    if (headerViewHolder.setSelectedTabPosition(tabSeq)) {
                        mLastTabSeq = tabSeq;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void onEventMainThread(Events.FlexibleEvent.CartPopupEvent event) {
        // GSChoiceShopFragment 의 새벽배송 매장에서 장바구니 버튼 클릭시 장바구니 이벤트가 연속 두 번 들어옴을 막기 위한 예방 코드.
        // 왜 두 번 들어오는지 찾게되면 아래 if문 삭제.
        if (ClickUtils.work2(200)) {
            return;
        }
        // ========================================
        //      팝업 윈도우 설정
        // ========================================
        if (mPopupWindowCart.isShowing()) {
            mPopupWindowCart.dismiss();
        }

        View viewPopup = mPopupWindowCart.getContentView();
        viewPopup.setClickable(true);
        ImageView imgSuccess = viewPopup.findViewById(R.id.img_cart_main_success);
        ImageView imgAlreadyExists = viewPopup.findViewById(R.id.img_cart_main_already_exist);
        ImageView imgClose = viewPopup.findViewById(R.id.img_cart_main_close);
        View viewBack = viewPopup.findViewById(R.id.layout_back);

        if (event.state == Events.FlexibleEvent.CartPopupEvent.STATE_SUCCESS) {
            imgSuccess.setVisibility(View.VISIBLE);
            imgAlreadyExists.setVisibility(View.GONE);
        } else if (event.state == Events.FlexibleEvent.CartPopupEvent.STATE_ALREADY_EXISTS) {
            imgAlreadyExists.setVisibility(View.VISIBLE);
            imgSuccess.setVisibility(View.GONE);
        }

        if (mPopupViewGSSuper != null) {
            mPopupWindowCart.showAtLocation(mPopupViewGSSuper, Gravity.FILL, 0, 0);
        }

        mActivity.setBasketcnt();

        // 리스너 동작
        View.OnClickListener listenerCart = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindowCart.dismiss();
                // 장바구니 이동

                // GS_SUPER_GO_CART mseq 에 대한 url을 FRESH_SMART_CART에 추가하여 따로 로그를 찍을 필요가 없어졌다.
                //((AbstractBaseActivity) getContext()).setWiseLogHttpClient(ServerUrls.WEB.GS_SUPER_GO_CART);

                // isFresh 가 내려오는 경우에는 isFresh 값에 따라 분기해주고 내려오지 않으면
                // 기존 로직대로 무조건 Fresh 장바구니로 보낸다
                if (event.isFresh != null) {
                    if ("Y".equalsIgnoreCase(event.isFresh)) {
                        WebUtils.goWeb(getContext(), ServerUrls.WEB.FRESH_SMART_CART_TOP);
                    } else {
                        WebUtils.goWeb(getContext(), ServerUrls.WEB.SMART_CART);
                    }
                } else {
                    WebUtils.goWeb(getContext(), ServerUrls.WEB.FRESH_SMART_CART_TOP);
                }
            }
        };

        View.OnClickListener listenerClose = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindowCart.dismiss();
            }
        };

        imgSuccess.setOnClickListener(listenerCart);
        imgAlreadyExists.setOnClickListener(listenerCart);
        imgClose.setOnClickListener(listenerClose);
        viewBack.setOnClickListener(listenerClose);

        mHandlerPopupClose.postDelayed(mRunnablePopupclose, 3000);

        mPopupWindowCart.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mHandlerPopupClose.removeCallbacks(mRunnablePopupclose);
            }
        });
    }

    /**
     * 카테고리 선택시 데이타를 취득한다.
     *
     * @param event GSChoiceCategoryEvent
     */
    public void onEvent(Events.FlexibleEvent.GSChoiceCategoryEvent event) {
        if (getUserVisibleHint()) {
            new GetUpdateController(getActivity()).execute(event.url, "", "", false, event.isHeader, mAdapter.getHeaderPosition(), CALLER.CATEGORY);
        }
    }

    /**
     * 시그니쳐매장 내 브랜드 더보기 클릭시 이벤트 수신
     *
     * @param event SignatureBrandMoreEvent
     */
    public void onEvent(Events.FlexibleEvent.SignatureBrandMoreEvent event) {
        if (getUserVisibleHint()) {
            new GetUpdateController(getActivity()).execute(event.url, "", "", false, false, event.position, CALLER.BRANDMORE);
        }
    }

    /**
     * 카테고리 스크롤시 매장 스크롤을 중지한다.
     * (카테고리 로딩 후 상단으로 위치잡기 위해)
     *
     * @param event GSChoiceStopScrollEvent
     */
    public void onEvent(Events.FlexibleEvent.GSChoiceStopScrollEvent event) {
        mRecyclerView.stopScroll();
    }

    @Override
    public void drawFragment() {
        new GetUpdateController(getActivity()).execute(sectionList.sectionLinkUrl,
                sectionList.sectionLinkParams, sectionList.sectionName, true, false, -1, CALLER.DEFAULT);
    }

    /**
     * refresh layout 설정 (당겼을 때 동작 등)
     */

    private GetUpdateController mSwipeRefreshController = null;

    private void setRefreshLayout(View view) {
        mLayoutSwipe = view.findViewById(R.id.layout_refresh);
        mLayoutSwipe.setColorSchemeColors(Color.parseColor("#BED730"));
        mLayoutSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mSwipeRefreshController != null) {
                    mSwipeRefreshController = null;
                }
                // 프래그먼트 다시 그림.
                mSwipeRefreshController = new GetUpdateController(getActivity());
                mSwipeRefreshController.execute(sectionList.sectionLinkUrl,
                        sectionList.sectionLinkParams, sectionList.sectionName, true, false, -1, CALLER.DEFAULT, true);
            }
        });
    }

    /**
     * 매장을 리프레시한다.
     *
     * @param event RefreshShopEvent
     */
    public void onEvent(Events.RefreshShopEvent event) {
        if (getUserVisibleHint()) {
            header.setVisibility(View.GONE);
            mRecyclerView.scrollToPosition(0);
            new Handler().postDelayed(() -> drawFragment(), 500);
        }
    }

    public void onEvent(Events.EventSwipeRefreshStop event) {
        Events.EventSwipeRefreshStop stickyEvent = (Events.EventSwipeRefreshStop) EventBus.getDefault().getStickyEvent(Events.EventSwipeRefreshStop.class);
        if (stickyEvent != null) {
            EventBus.getDefault().removeStickyEvent(stickyEvent);
        }
        if (mSwipeRefreshController != null) {
            mSwipeRefreshController.cancel();
        }

        if (isNotEmpty(mLayoutSwipe)) {
            mLayoutSwipe.setRefreshing(false);
        }
    }

    protected int getViewType(String viewType) {
        int type = ViewHolderType.BANNER_TYPE_NONE;
        if ("BAN_IMG_H000_GBC".equals(viewType)) {
            // GS Choice 상단 배너.
            type = ViewHolderType.VIEW_TYPE_BAN_IMG_H000_GBC;
        } else if ("BAN_IMG_H000_GBD".equals(viewType)) {
            // 하단 여백있는 배너
            type = ViewHolderType.VIEW_TYPE_BAN_IMG_H000_GBD;
        } else if ("BAN_SLD_GBE".equals(viewType)) {
            // GS Choice 정사각 슬라이드 상품
            type = ViewHolderType.VIEW_TYPE_BAN_SLD_GBE;
        } else if ("BAN_SLD_GBF".equals(viewType)) {
            // GS Choice 가로 슬라이드 (소형)
            type = ViewHolderType.VIEW_TYPE_BAN_SLD_GBF;
        } else if ("BAN_SLD_GBG".equals(viewType)) {
            // 시그니처매장 가로 캐로셀
            type = ViewHolderType.VIEW_TYPE_BAN_SLD_GBG;
        } else if ("GR_BRD_GBA".equals(viewType)) {
            // 시그니처매장 상품
            type = ViewHolderType.VIEW_TYPE_GR_BRD_GBA;
        } else if ("BAN_API_MORE_GBA".equals(viewType)) {
            // 시그니처매장 브랜드 더보기
            type = ViewHolderType.VIEW_TYPE_BAN_API_MORE_GBA;
        } else if ("TAB_SLD_GBA".equals(viewType)) {
            // GS Choice 스티키 헤더 슬라이드 배너
            type = ViewHolderType.VIEW_TYPE_TAB_SLD_GBA;
        } else if ("TAB_SLD_GBB".equals(viewType)) {
            // GS Choice 스티키 헤더 슬라이드 배너 B
            type = ViewHolderType.VIEW_TYPE_TAB_SLD_GBB;
        } else if ("BAN_MUT_GBA".equals(viewType)) {
            // GS Choice 스티키 헤더 서브 상품 배너
            type = ViewHolderType.VIEW_TYPE_BAN_MUT_GBA;
        } else if ("BAN_TXT_IMG_LNK_GBA".equals(viewType)) {
            // 타이틀 뷰 형태
            type = ViewHolderType.VIEW_TYPE_BAN_TXT_IMG_LNK_GBA;
        } else if ("NO_DATA".equals(viewType)) {
            // 데이타 없음 표시용
            type = ViewHolderType.BANNER_TYPE_NO_DATA;
        } else if ("BAN_IMG_F80_C_GBA".equals(viewType)) {
            type = ViewHolderType.BANNER_TYPE_FIXED_80_IMG_C_GBA;
        } else if ("BAN_IMG_F80_L_GBA".equals(viewType)) {
            type = ViewHolderType.BANNER_TYPE_FIXED_80_IMG_L_GBA;
        } else if ("BAN_IMG_F80_R_GBA".equals(viewType)) {
            type = ViewHolderType.BANNER_TYPE_FIXED_80_IMG_R_GBA;
        } else if ("MAP_CX_GBC".equals(viewType)) {
            type = ViewHolderType.VIEW_TYPE_MAP_CX_GBC;
        }
        // 기존 C 타입에서 사용되던 뷰타입들이 L 타입에도 추가된 것들. (2020. 01. 02. yun).
        else if ("BAN_TXT_IMG_LNK_GBA".equals(viewType)) {
            type = ViewHolderType.VIEW_TYPE_BAN_TXT_IMG_LNK_GBA;
        } else if ("PRD_C_SQ".equals(viewType)) {
            // 리뉴얼 프로덕트 리스트용 (VIEW_TYPE_API_SRL 와 동일, 개인화 아닌 Product List 용)
            type = ViewHolderType.VIEW_TYPE_PRD_C_SQ;
        } else if ("MOLOCO_PRD_C_SQ".equals(viewType)) {
            // 몰로코 이런상품 어떠세요
            type = ViewHolderType.VIEW_TYPE_MOLOCO_PRD_C_SQ;
        }
        else if ("PRD_C_CST_SQ".equals(viewType)) {
            type = ViewHolderType.VIEW_TYPE_PRD_C_CST_SQ;
        }
        else if ("B_TS".equals(viewType)) {
            // "텍스트소배너" = B_TS (productName )
            type = ViewHolderType.BANNER_TYPE_TITLE;
        } else if ("BAN_TXT_IMG_LNK_GBB".equals(viewType)) {
            // yunseung* 위 B_TS 가 변한것. B_TS 일단 지우지 않는다. 2019.10.11.... 나중에 시간이 흐른뒤에도 B_TS 안쓰면 지울것.
            type = ViewHolderType.VIEW_TYPE_BAN_TXT_IMG_LNK_GBB;
        } else if ("PRD_C_B1".equals(viewType)) {
            type = ViewHolderType.VIEW_TYPE_PRD_C_B1;
        } else if ("BAN_IMG_C5_GBA".equals(viewType)) {
            // 서비스 매장 바로가기
            type = ViewHolderType.VIEW_TYPE_BAN_IMG_C5_GBA;
        } else if ("API_SRL".equals(viewType)) {
            // 추천딜 (API호출)
            type = ViewHolderType.VIEW_TYPE_API_SRL;
        } else if ("PRD_1_550".equals(viewType)) {
            // 단품 1개 정사각
            type = ViewHolderType.VIEW_TYPE_PRD_1_550;
        } else if ("PRD_1_640".equals(viewType)) {
            // 단품 1개 직사각
            type = ViewHolderType.VIEW_TYPE_PRD_1_640;
        } else if ("PRD_2".equals(viewType)) {
            // 단품 2개 new
            type = ViewHolderType.VIEW_TYPE_PRD_2;
        } else if ("PMO_T1_PREVIEW_B".equals(viewType)) {
            type = ViewHolderType.VIEW_TYPE_PMO_T1_PREVIEW_B;
        }
        // 이미지 슬라이드 iOS 문제 맞추기 위해 GBB를 추가 하지만 같은 타입으로 사용.
        else if ("BAN_TXT_IMG_SLD_GBA".equals(viewType) ||
                "BAN_TXT_IMG_SLD_GBB".equals(viewType)) {
            // GS 혜택 리뉴얼 (이미지 + 텍스트 슬라이드)
            type = ViewHolderType.VIEW_TYPE_TXT_IMG_SLD_GBA;
        } else if ("BAN_IMG_CX_GBA".equals(viewType)) {
            // GS 혜택 리뉴얼 이미지 게이트 메뉴 배너
            type = ViewHolderType.VIEW_TYPE_IMG_CX_GBA;
        } else if ("TAB_ANK_GBA".equals(viewType)) {
            // GS 혜택 리뉴얼 헤더 앵커
            type = ViewHolderType.VIEW_TYPE_TAB_ANK_GBA;
        } else if ("BAN_IMG_TXT_GBA".equals(viewType)) {
            // GS 혜택 리뉴얼 이미지 + 텍스트 배너
            type = ViewHolderType.VIEW_TYPE_IMG_TXT_GBA;
        } else if ("TAB_SLD_ANK_GBA".equals(viewType)) {
            // 새벽배송 매장 새로운 앵커 (좌우 플리킹 가능, 최상단 한 개만 존재하는 앵커)
            type = ViewHolderType.VIEW_TYPE_TAB_SLD_ANK_GBA;
        } else if ("TAB_SLD_ANK_GBB".equals(viewType)) {
            // 내일도착 앵커 형태
            type = ViewHolderType.VIEW_TYPE_TAB_SLD_ANK_GBB;
        } else if ("TAB_SLD_GBB_MORE".equals(viewType)) {
            // 새벽배송 더보기
            type = ViewHolderType.VIEW_TYPE_SLD_GBB_MORE;
        } else if ("TAB_IMG_ANCH_GBA".equals(viewType)) {// hklim 이미지 앵커형 수정 포인트
            // 이미지 아이템 형태 앵커
            type = ViewHolderType.VIEW_TYPE_TAB_IMG_ANCH_GBA;
        } else if ("BAN_TXT_SUB_GBA".equals(viewType)) {
            // tv 신규 매장 타이틀
            type = ViewHolderType.VIEW_TYPE_BAN_TXT_SUB_GBA;
        } else if ("PRD_VOD_LIST".equals(viewType)) {
            // tv 신규 매장 640 vod 및 기본상품
            type = ViewHolderType.VIEW_TYPE_PRD_VOD_LIST;
        } else if ("PRD_MLT_GBA".equals(viewType)) {
            // tv 신규 매장 3열 그리드 및 더보기
            type = ViewHolderType.VIEW_TYPE_PRD_MLT_GBA;
        } else if ("BAN_TXT_CST_GBA".equals(viewType)) {
            // 색상 들어간 타이틀 뷰
            type = VIEW_TYPE_BAN_TXT_CST_GBA;
        } else if ("BAN_TITLE_PRD_GBA".equals(viewType)) {
            // 앵커 형태 상품들 상단 타이틀
            type = VIEW_TYPE_BAN_TITLE_PRD_GBA;
        }
        else if ("PMO_T3_IMG".equals(viewType)) {
            // 하단 여백있는 배너 (H0000_GBD와 동일)
            type = ViewHolderType.VIEW_TYPE_BAN_IMG_H000_GBD;
        }
        else {
            type = ViewHolderType.BANNER_TYPE_NONE;
        }
        return type;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (getView() != null) {
            if (isVisibleToUser) {
                //네트워크 지연알림이 노출된 매장에 사용자가 다시 진입 시 자동갱신
                if (NetworkUtils.isNetworkAvailable(getContext()) && mRefreshView.getVisibility() == View.VISIBLE) {
                    mBtnRefresh.performClick();
                }
                else {
                    //시그니처매장 동영상 정지용
                    EventBus.getDefault().post(new Events.VodShopPlayerEvent(Events.VodShopPlayerEvent.VodPlayerAction.VOD_STOP));
                }
            }
        }
    }

    public enum ENUM_TYPE_HEADER {
        TYPE_RENEWAL, // 갱신형
        TYPE_ANCHOR  // 앵커형
    }


    /**
     * 매장 전체를 로딩한다.
     *
     * @param data ModuleListRoot
     */
    protected void updateList(ModuleListRoot data) {
        if (!TextUtils.isEmpty(data.ajaxPageUrl)) {
            mAdapter.setAjaxPageUrl(data.ajaxPageUrl);
        }

        if (isEmpty(data.moduleList)) {
            return;
        }

        mPositionFirstMapCxGbc = -1;
        // 2단뷰가 들어오는데 계속 1depth로 들어오니까 2depth로 만들어 주기위한 추가 작업이 필요하다.
        mPositionFirstPrd2 = -1;

        List<ModuleList> contents = new ArrayList<ModuleList>();

        int temp = 0;

        ArrayList<String> tabSeqList = new ArrayList<>();
        ENUM_TYPE_HEADER typeHeader = ENUM_TYPE_HEADER.TYPE_ANCHOR;

        // 전체 모듈리스트를 재가공
        for (int i = 0; i < data.moduleList.size(); i++) {
            ModuleList item = data.moduleList.get(i);

            if (isEmpty(item)) {
                continue;
            }

            boolean isExistSeq = false;
            if (tabSeqList.indexOf(item.tabSeq) >= 0 && !TextUtils.isEmpty(item.tabSeq)) {
                isExistSeq = true;
            }

            int type = getViewType(item.viewType);
            // ViewType 이 GBC 일 경우에 하나만 놔두고 나머지는 기존 하나의 sub로 모두 넣어준다.
            if (type == ViewHolderType.VIEW_TYPE_MAP_CX_GBC) {      // GS Choice 및 헤더 아래 부분
                if (mPositionFirstMapCxGbc >= 0 && contents.get(mPositionFirstMapCxGbc).subProductList.size() < 2
                        && contents.size() - 1 <= mPositionFirstMapCxGbc) {
                    // 기존 VIEW_TYPE_MAP_CX_GBC 이 있으면 sub에 넣어주고 새로 생성되지 않게 하기 위해. none으로 변경.
                    if (contents.get(mPositionFirstMapCxGbc).subProductList == null) {
                        contents.get(mPositionFirstMapCxGbc).subProductList = new ArrayList<>();
                    }
                    contents.get(mPositionFirstMapCxGbc).subProductList.add(item);
                    type = ViewHolderType.BANNER_TYPE_NONE;
                } else {
                    // 기존 sub가 없을 경우 새로 만들어서 sub에 값을 넣어줌
                    ModuleList tempList = item;
                    item = new ModuleList();
                    item.viewType = tempList.viewType;
                    item.subProductList = new ArrayList<>();
                    item.subProductList.add(tempList);
                }
            }

            // 2단뷰 생성할때마다 여기 추가해 주는것은 너무 번거롭다. 데이터 가공을 해야 사용가능하기 때문에 어쩔수 없긴 하지만.
            if (type == ViewHolderType.VIEW_TYPE_PRD_2) {

                if (mPositionFirstPrd2 >= 0
                        && contents.get(mPositionFirstPrd2).subProductList.size() < 2
                        && contents.size() - 1 <= mPositionFirstPrd2
                        // tabSeq 확인해서 tabSeq 가 바뀌면 홀수 여도 none 으로 해주고 새로 추가해 주어야 한다.
                        && (TextUtils.isEmpty(item.tabSeq) || (!TextUtils.isEmpty(item.tabSeq) && isExistSeq))
                )
                {
                    // 기존 BANNER_TYPE_MAP_CX_GBC 이 있으면 sub에 넣어주고 새로 생성되지 않게 하기 위해. none으로 변경.
                    if (contents.get(mPositionFirstPrd2).subProductList == null) {
                        contents.get(mPositionFirstPrd2).subProductList = new ArrayList<>();
                    }
                    contents.get(mPositionFirstPrd2).subProductList.add(item);
                    type = ViewHolderType.BANNER_TYPE_NONE;
                } else {
                    // 기존 sub가 없을 경우 새로 만들어서 sub에 값을 넣어줌
                    ModuleList tempList = item;
                    item = new ModuleList();
                    item.viewType = tempList.viewType;
                    item.tabSeq = tempList.tabSeq;
                    item.subProductList = new ArrayList<>();
                    item.subProductList.add(tempList);
                }
            }

            // 상속 받는 곳에서 타입 재설정을 가능하게 하기 위한 함수 호출,
            type = setAdditionalUpdate(type, item, i);

            // 640 캐로셀의 아이템이 1개일 경우 C_B1 어뎁터를 그대로 쓰면 허전하게 나오는 문제를 해결하기 위해
            // 타이틀 + 640 prd1 상품으로 가공
            if (type == ViewHolderType.VIEW_TYPE_PRD_C_B1 && item.productList.size() == 1) {
                // title.
                ModuleList tempList = item;
                tempList.viewType = "BAN_TXT_IMG_LNK_GBA";
                tempList.tempViewType = ViewHolderType.VIEW_TYPE_BAN_TXT_IMG_LNK_GBA;
                tempList.bdrBottomYn = "Y";
                contents.add(tempList);
                // 640 prd 1
                tempList = new ModuleList();
                tempList.tempViewType = ViewHolderType.VIEW_TYPE_PRD_1_640;
                tempList.viewType = "PRD_1_640";
                tempList.productList = item.productList;
                tempList.isInPList = true;
                contents.add(tempList);
            } else if (type != ViewHolderType.BANNER_TYPE_NONE) {
                item.tempViewType = type;
                contents.add(item);

                if (type == ViewHolderType.VIEW_TYPE_MAP_CX_GBC) {
                    mPositionFirstMapCxGbc = contents.size() - 1;
                }

                if (type == ViewHolderType.VIEW_TYPE_PRD_2) {
                    mPositionFirstPrd2 = contents.size() - 1;

                    // tabSeq 비교를 위해 기존의 tabseq가 있는지 확인해준다.
                    if (!isExistSeq && !TextUtils.isEmpty(item.tabSeq)) {
                        tabSeqList.add(item.tabSeq);
                        tabPrdMapLType.put(item.tabSeq, contents.size() - 1);
                    }
                }

                if (!isExistSeq && !TextUtils.isEmpty(item.tabSeq)) {
                    tabSeqList.add(item.tabSeq);
                    tabPrdMapLType.put(item.tabSeq, contents.size() - 1);
                }

                if (type == ViewHolderType.VIEW_TYPE_TAB_IMG_ANCH_GBA) {
                    for (ModuleList itemList : item.moduleList) {
                        arrAnchTabSeq.add(itemList.tabSeq);
                    }
                }

                /**
                 * 헤더종류에 따른 헤더 설정 부분
                 */
                if (mAdapter.isHeaderView(type) || type == ViewHolderType.VIEW_TYPE_TAB_ANK_GBA_1ST) {

                    typeHeader = setHeaderView(type);

                    headerViewHolder.setIsHeader(true);
                    headerViewHolder.onBindViewHolder(getContext(), contents.size() - 1, contents);

                    header.removeAllViews();
                    header.addView(headerViewHolder.itemView);
                    //더보기 주소 세팅
                    readMoreUrl = item.ajaxTabPrdListUrl;
                }
            }

            if (i == data.moduleList.size() - 1 && mPositionFirstPrd2 >= 0 && typeHeader == ENUM_TYPE_HEADER.TYPE_RENEWAL) {
                if (headerViewHolder instanceof TabSldGbbVH) {
                    contents.get(mPositionFirstPrd2).moreBtnUrl = ((TabSldGbbVH)headerViewHolder).getMoreBtnUrl();
                }
            }
        }

        //푸터추가
        addFooter(contents);

        mAdapter.setInfo(contents);
        mAdapter.notifyDataSetChanged();
        headerPosition = mAdapter.getHeaderPosition();

        //매장스크롤이 멈추는 시점에만 캐시작업을 수행하므로 매장로딩 후 처음에 여기서 이벤트 전달하여 캐시 수행
        if (useNativeProduct) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    EventBus.getDefault().post(new Events.ImageCacheStartEvent(sectionList.navigationId));
                }
            }, 1000);
        }
    }

    /**
     * 헤더 뷰의 유형을 설정한다.
     * @param type
     * @return
     */
    private ENUM_TYPE_HEADER setHeaderView(int type) {
        ENUM_TYPE_HEADER typeHeader = ENUM_TYPE_HEADER.TYPE_ANCHOR;
        //해더인 경우 뷰 바인딩
        if (type == ViewHolderType.VIEW_TYPE_TAB_SLD_GBA) {
            View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.recycler_item_gschoice_category, null);
            headerViewHolder = new TabSldGbaVH(itemView, sectionList.navigationId);
        } else if (type == ViewHolderType.VIEW_TYPE_TAB_SLD_GBB) {
            typeHeader = ENUM_TYPE_HEADER.TYPE_RENEWAL;

            View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.recycler_item_gschoice_category_b, null);
            headerViewHolder = new TabSldGbbVH(itemView, sectionList.navigationId);
        } else if (type == ViewHolderType.VIEW_TYPE_TAB_SLD_ANK_GBB) {
            typeHeader = ENUM_TYPE_HEADER.TYPE_ANCHOR;

            View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.recycler_item_gschoice_category, null);
            headerViewHolder = new TabSldAnkGbbVH(itemView, sectionList.navigationId);
        } else if (type == ViewHolderType.VIEW_TYPE_TAB_SLD_ANK_GBA) {
            typeHeader = ENUM_TYPE_HEADER.TYPE_ANCHOR;

            View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.recycler_item_gschoice_category_b, null);
            headerViewHolder = new TabSldAnkGbaVH(itemView, sectionList.navigationId);
        } else if (type == ViewHolderType.VIEW_TYPE_TAB_IMG_ANCH_GBA) {
            typeHeader = ENUM_TYPE_HEADER.TYPE_ANCHOR;

            View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.home_row_type_l_menu_img_cx_gba, null);
            headerViewHolder = new TabImgAnchGbaVH(itemView, sectionList.navigationId);
        }
        else if (type == ViewHolderType.VIEW_TYPE_TAB_ANK_GBA_1ST) {
            typeHeader = ENUM_TYPE_HEADER.TYPE_ANCHOR;

            View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.home_row_type_l_sticky_type_event_tab, null);
            headerViewHolder = new EventShopTabViewHolder(itemView, sectionList.navigationId);

            try {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)header.getLayoutParams();
                params.height = DisplayUtils.convertDpToPx(getContext(), 46);
                header.setLayoutParams(params);
                header.removeAllViews();
                header.addView(headerViewHolder.itemView);
            } catch (NullPointerException e) {
                Ln.e(e);
            }
        }
        return typeHeader;
    }

    /**
     * 카테고리 선택시 카테고리 소속 상품을 갱신한다.
     *
     * @param data     ModuleListRoot
     * @param isHeader if true, header
     */
    protected void updateListCategory(ModuleListRoot data, boolean isHeader, int tabPosition) {

        List<ModuleList> contents = new ArrayList<ModuleList>();

        if (isNotEmpty(data.productList)) {
            //더보기 주소 세팅
            readMoreUrl = data.ajaxfullUrl;

            mPositionFirstMapCxGbc = -1;
            mPositionFirstPrd2 = -1;

            for (int i = 0; i < data.productList.size(); i++) {
                ModuleList item = data.productList.get(i);
                int type = getViewType(item.viewType);

                if (type == ViewHolderType.VIEW_TYPE_MAP_CX_GBC) {
                    if (mPositionFirstMapCxGbc >= 0 && contents.get(mPositionFirstMapCxGbc).subProductList.size() < 2
                            && contents.size() - 1 <= mPositionFirstMapCxGbc) {
                        // 기존 VIEW_TYPE_MAP_CX_GBC 이 있으면 sub에 넣어주고 새로 생성되지 않게 하기 위해. none으로 변경.
                        if (contents.get(mPositionFirstMapCxGbc).subProductList == null) {
                            contents.get(mPositionFirstMapCxGbc).subProductList = new ArrayList<>();
                        }
                        contents.get(mPositionFirstMapCxGbc).subProductList.add(item);
                        type = ViewHolderType.BANNER_TYPE_NONE;

                        // 마지막 리스트면 마지막 리스트 찾아서 morebtnurl 넣어줌.
                        if (i == data.productList.size() - 1 && mPositionFirstMapCxGbc >= 0) {
                            if (headerViewHolder instanceof TabSldGbbVH) {
                                contents.get(mPositionFirstPrd2).moreBtnUrl = ((TabSldGbbVH)headerViewHolder).getMoreBtnUrl();
                            }
                        }
                    } else {
                        // 기존 sub가 없을 경우 새로 만들어서 sub에 값을 넣어줌
                        ModuleList tempList = item;
                        item = new ModuleList();
                        item.viewType = tempList.viewType;
                        item.subProductList = new ArrayList<>();
                        item.subProductList.add(tempList);

                        // 마지막 리스트 이면 지금 추가될 리스트에 morebtnurl 넣어줌.
                        if (i == data.productList.size() - 1 && mPositionFirstMapCxGbc >= 0) {
                            if (headerViewHolder instanceof TabSldGbbVH) {
                                item.moreBtnUrl = ((TabSldGbbVH)headerViewHolder).getMoreBtnUrl();
                            }
                        }
                    }
                }

                // 2단뷰 생성할때마다 여기 추가해 주는것은 너무 번거롭다. 데이터 가공을 해야 사용가능하기 때문에 어쩔수 없긴 하지만.
                if (type == ViewHolderType.VIEW_TYPE_PRD_2) {
                    if (mPositionFirstPrd2 >= 0 && contents.get(mPositionFirstPrd2).subProductList.size() < 2
                            && contents.size() - 1 <= mPositionFirstPrd2) {
                        // 기존 BANNER_TYPE_MAP_CX_GBC 이 있으면 sub에 넣어주고 새로 생성되지 않게 하기 위해. none으로 변경.
                        if (contents.get(mPositionFirstPrd2).subProductList == null) {
                            contents.get(mPositionFirstPrd2).subProductList = new ArrayList<>();
                        }
                        contents.get(mPositionFirstPrd2).subProductList.add(item);
                        type = ViewHolderType.BANNER_TYPE_NONE;

                        if (i == data.productList.size() - 1 && mPositionFirstPrd2 >= 0) {
                            if (headerViewHolder instanceof TabSldGbbVH) {
                                contents.get(mPositionFirstPrd2).moreBtnUrl = ((TabSldGbbVH)headerViewHolder).getMoreBtnUrl();
                            }
                        }
                    } else {
                        // 기존 sub가 없을 경우 새로 만들어서 sub에 값을 넣어줌
                        ModuleList tempList = item;
                        item = new ModuleList();
                        item.viewType = tempList.viewType;
                        item.subProductList = new ArrayList<>();
                        item.subProductList.add(tempList);

                        // 마지막 리스트 이면 지금 추가될 리스트에 morebtnurl 넣어줌.
                        if (i == data.productList.size() - 1 && mPositionFirstPrd2 >= 0) {
                            if (headerViewHolder instanceof TabSldGbbVH) {
                                item.moreBtnUrl = ((TabSldGbbVH)headerViewHolder).getMoreBtnUrl();
                            }
                        }
                    }
                }

                type = setAdditionalUpdate(type, item, i);

                if (type != ViewHolderType.BANNER_TYPE_NONE) {
                    item.tempViewType = type;
                    contents.add(item);
                    if (type == ViewHolderType.VIEW_TYPE_MAP_CX_GBC) {
                        mPositionFirstMapCxGbc = contents.size() - 1;
                    }
                    if (type == ViewHolderType.VIEW_TYPE_PRD_2) {
                        mPositionFirstPrd2 = contents.size() - 1;
                    }
                }
                if (type == ViewHolderType.BANNER_TYPE_NO_DATA) {
                    readMoreUrl = "";
                }
            }
        } else {
            readMoreUrl = "";
        }

        //푸터추가
        addFooter(contents);

//        int adapterPosition = mAdapter.getHeaderPosition();
//        int adapterPositionB = mAdapter.getHeaderBPosition();

        //이전 카테고리 데이타 제거 후 신규데이타 추가
        mAdapter.addInfo(contents, tabPosition);

        if (isHeader) {
            //해더인 경우는 첫번째 아이템으로 위치 이동
            scrollTopCategoryItem();
        }
    }

    /**
     * 이벤트 스티키해더로 사용할 첫번째 뷰홀더 확인용
     */
    protected boolean isFirstAnchor = true;

    /**
     * GS 혜택 에서 첫번째 앵커만 1st로 설정 해 준다.
     * @param type
     * @param content
     * @param position
     * @return
     */
    protected int setAdditionalUpdate(int type, ModuleList content, int position) {
        if (isFirstAnchor && type == ViewHolderType.VIEW_TYPE_TAB_ANK_GBA) {
            type = ViewHolderType.VIEW_TYPE_TAB_ANK_GBA_1ST;
            isFirstAnchor = false;
        }

        if (type == ViewHolderType.VIEW_TYPE_TAB_ANK_GBA
                || type == ViewHolderType.VIEW_TYPE_TAB_ANK_GBA_1ST) {
            if (isEmpty(tabPrdMapEvent.get(content.tabSeq))) {
                //동일 카테고리가 여러개인 경우 최상위 위치만 저장
                tabPrdMapEvent.put(content.tabSeq, position);
            }
        }

        return type;
    }

    private void scrollTopCategoryItem() {
        new Handler().postDelayed(() -> {
            mLayoutManager.scrollToPositionWithOffset(headerPosition, 0);
        }, 1000);
    }

    /**
     * 하단으로 스크롤시 리스트 하단에 상품을 추가한다.
     *
     * @param data ModuleListRoot
     */
    public void updateListReadMore(ModuleListRoot data) {

        if (isEmpty(data.productList)) {
            readMoreUrl = "";
            return;
        }

        //NO_DATA 내려오는 경우는 데이타 없는 경우와 동일함
        if (data.productList.size() == 1) {
            ModuleList c = data.productList.get(0);
            int type = getViewType(c.viewType);
            if (type == ViewHolderType.BANNER_TYPE_NO_DATA) {
                readMoreUrl = "";
                return;
            }
        }

        //더보기 주소 세팅
        readMoreUrl = data.ajaxfullUrl;

        //마지막에 푸터가있으면 삭제
        int lastIdx = mAdapter.getItemCount() - 1;
        if (lastIdx > 0 && mAdapter.getInfo().get(lastIdx).tempViewType == ViewHolderType.BANNER_TYPE_FOOTER) {
            mAdapter.getInfo().remove(lastIdx);
        }

        List<ModuleList> contents = new ArrayList<ModuleList>();
        mPositionFirstPrd2 = -1;
//        for (ModuleList c : data.productList) {

        for (int i = 0; i < data.productList.size(); i++) {
            ModuleList item = data.productList.get(i);
            int type = getViewType(item.viewType);

            if (type == ViewHolderType.VIEW_TYPE_MAP_CX_GBC) {
                if (mPositionFirstMapCxGbc >= 0 && contents.get(mPositionFirstMapCxGbc).subProductList.size() < 2
                        && contents.size() - 1 <= mPositionFirstMapCxGbc) {
                    // 기존 VIEW_TYPE_MAP_CX_GBC 이 있으면 sub에 넣어주고 새로 생성되지 않게 하기 위해. none으로 변경.
                    if (contents.get(mPositionFirstMapCxGbc).subProductList == null) {
                        contents.get(mPositionFirstMapCxGbc).subProductList = new ArrayList<>();
                    }
                    contents.get(mPositionFirstMapCxGbc).subProductList.add(item);
                    type = ViewHolderType.BANNER_TYPE_NONE;
                } else {
                    // 기존 sub가 없을 경우 새로 만들어서 sub에 값을 넣어줌
                    ModuleList tempList = item;
                    item = new ModuleList();
                    item.viewType = tempList.viewType;
//                    if (headerViewHolderB != null) {
//                        item.moreBtnUrl = headerViewHolderB.getMoreBtnUrl();
//                    }
                    item.subProductList = new ArrayList<>();
                    item.subProductList.add(tempList);
                }
            }

            if (type == ViewHolderType.VIEW_TYPE_PRD_2) {
                if (mPositionFirstPrd2 >= 0 && contents.get(mPositionFirstPrd2).subProductList.size() < 2
                        && contents.size() - 1 <= mPositionFirstPrd2) {
                    // 기존 BANNER_TYPE_MAP_CX_GBC 이 있으면 sub에 넣어주고 새로 생성되지 않게 하기 위해. none으로 변경.
                    if (contents.get(mPositionFirstPrd2).subProductList == null) {
                        contents.get(mPositionFirstPrd2).subProductList = new ArrayList<>();
                    }
                    contents.get(mPositionFirstPrd2).subProductList.add(item);
                    type = ViewHolderType.BANNER_TYPE_NONE;
                } else {
                    // 기존 sub가 없을 경우 새로 만들어서 sub에 값을 넣어줌
                    ModuleList tempList = item;
                    item = new ModuleList();
                    item.viewType = tempList.viewType;
                    item.subProductList = new ArrayList<>();
                    item.subProductList.add(tempList);
                }
            }

            if (type != ViewHolderType.BANNER_TYPE_NONE) {
                item.tempViewType = type;
                contents.add(item);
                if (type == ViewHolderType.VIEW_TYPE_MAP_CX_GBC) {
                    mPositionFirstMapCxGbc = contents.size() - 1;
                }
                if (type == ViewHolderType.VIEW_TYPE_PRD_2) {
                    mPositionFirstPrd2 = contents.size() - 1;
                }
            }
        }

        //푸터추가
        addFooter(contents);

        int prevCount = mAdapter.getItemCount();
        mAdapter.addInfo(contents);
        mAdapter.notifyItemRangeInserted(prevCount, contents.size());
    }

    /**
     * 브랜드 더보기 클릭시 GrBrdMoreVH 상단에 상품을 추가한다.
     *
     * @param data ModuleListRoot
     * @param position 데이타 삽입 위치
     */
    public void updateListBrandMore(ModuleListRoot data, int position) {
        if (isEmpty(data.moduleList)) {
            return;
        }

        List<ModuleList> contents = new ArrayList<ModuleList>();
        for (int i = 0; i < data.moduleList.size(); i++) {
            ModuleList item = data.moduleList.get(i);
            int type = getViewType(item.viewType);
            if (type != ViewHolderType.BANNER_TYPE_NONE) {
                item.tempViewType = type;
                contents.add(item);
            }
        }

        mAdapter.insertInfo(contents, position);
        mAdapter.notifyItemRangeInserted(position, contents.size());
    }

    /**
     * UpdateController, ModuleList는 변경될 수 있습니다.
     */
    public class GetUpdateController extends BaseAsyncController<ModuleListRoot> {

        private Context context;
        private String url;
        private String params;
        private String sectionName;
        private boolean isCacheData = false;
        private boolean isHeader = false;
        private int tabPosition = -1;
        private CALLER caller;
        private boolean isFromSwipeRefresh = false;

        @Inject
        protected RestClient restClient;

        public GetUpdateController(Context activityContext) {
            super(activityContext);
            this.context = activityContext;
        }

        @Override
        protected void onPrepare(Object... params) throws Exception {
            this.url = (String) params[0];
            this.params = (String) params[1];
            this.sectionName = (String) params[2];
            this.isCacheData = (boolean) params[3];
            this.isHeader = (boolean) params[4];
            this.tabPosition = (int) params[5];
            this.caller = (CALLER) params[6];

            if (params.length > 7) {
                isFromSwipeRefresh = (boolean) params[7];
            }

            if (getUserVisibleHint()) {
                if (dialog != null) {
                    dialog.dismiss();
                    dialog.setCancelable(true);
                    if (!isFromSwipeRefresh) {
                        dialog.show(); //로딩시 불렸을때 똥글뱅이 보이도록
                    } else {
                        dialog.hide(); //새로고침시 불렸을때 똥글뱅이 안보이도록
                    }
                }
            }

            ViewUtils.hideViews(mRefreshView);
        }

        @Override
        protected ModuleListRoot process() throws Exception {
            return (ModuleListRoot) DataUtil.getData(context, restClient, ModuleListRoot.class,
                    isCacheData, true, url, params + "&reorder=true", sectionName);
        }

        @Override
        protected void onSuccess(ModuleListRoot moduleList) throws Exception {
            isPageLoading = false;

            // SwipeRefreshing 프로그래스 바 hide
            mLayoutSwipe.setRefreshing(false);

            if (isEmpty(moduleList)) {
                if (caller == CALLER.DEFAULT) {
                    //매장로딩인 경우만 노출
                    ViewUtils.showViews(mRefreshView);
                }
            } else {
                ViewUtils.hideViews(mRefreshView);
                if (caller == CALLER.CATEGORY) {
                    updateListCategory(moduleList, isHeader, tabPosition);
                } else if (caller == CALLER.READMORE) {
                    updateListReadMore(moduleList);
                } else if (caller == CALLER.BRANDMORE) {
                    updateListBrandMore(moduleList, tabPosition);
                } else {
                    updateList(moduleList);
                }
            }

            // 당겨서 새로고침 했을 경우에는 탭도 처음 위치로 옮겨준다.
            if (isFromSwipeRefresh) {
                // 헤더 영역 붙잡고 해도 처음으로 가지 않는 문제 있어 처음으로 돌리고 헤더도 안보이게 설정
                mLayoutManager.scrollToPositionWithOffset(0, 0);
                header.setVisibility(View.GONE);

                // 해당 값이 0보다 크다는 이야기는 데이터 가공할 때에prd2 가 있는 상태이며 이는 새벽 배송이다.
                if (mAdapter.getHeaderPosition() > 0) {
                    headerViewHolder.setSelectedItem(0);
                }
            }
        }

        @Override
        protected void onError(Throwable e) {
            isPageLoading = false;

            // SwipeRefreshing 프로그래스 바 hide
            mLayoutSwipe.setRefreshing(false);

            //매장로딩인 경우만 노출
            if (caller == CALLER.DEFAULT) {
                ViewUtils.showViews(mRefreshView);
            }
            //매장로딩 또는 카테고리 로딩인 경우만 노출
            if (caller == CALLER.DEFAULT || caller == CALLER.CATEGORY) {
                super.onError(e);
            }

            //브랜드 더보기의 경우 네트워크 오류팝업 노출
            if (caller == CALLER.BRANDMORE) {
                NetworkUtils.showUnstableAlert(getActivity());
            }
        }

        @Override
        protected void onCancelled() {
            if (isFromSwipeRefresh) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            } else {
                super.onCancelled();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBundle("restore", saveState());
    }

    /**
     * 앱내 데이터가 날아가거나 백그라운드로 앱이 넘어갔을때 데이터 복구를 위해 사용
     *
     * @return Bundle
     */
    public Bundle saveState() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("restore", true);
        bundle.putInt("position", mCoordinator.getCurrentViewPosition());
        return bundle;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 퀵 리턴 메뉴 설정
     */
    private void setQuickReturn() {
        // quick return
        // 숭겨질 상단 레이아웃의 세로 사이즈
        int headerHeight = getResources().getDimensionPixelSize(R.dimen.main_header_height);
        // 숭겨질 하단 레이아웃의 세로 사이즈
        int footerHeight = getResources().getDimensionPixelSize(R.dimen.main_footer_height);
        int indicatorHeight = QuickReturnUtils.dp2px(mActivity, 4);
        int headerTranslation = -headerHeight + indicatorHeight;
        int footerTranslation = -footerHeight + indicatorHeight;

        //0827 사이렌이 없어지고 그 자리에 CSP레이아웃 탑재
        scrollListener = new QuickReturnRecyclerViewOnScrollListener.Builder(QuickReturnType.HEADER_FOOTER)
                .footer(getActivity(), mCoordinator.getFooterLayout(), topButton, countView, mCoordinator.getCspLayout())
                .minHeaderTranslation(headerTranslation)
                .minFooterTranslation(-footerTranslation).isSnappable(true).build();
        // 퀵 리턴뷰 리스너 설정

        mRecyclerView.addOnScrollListener(scrollListener);
        scrollListener.registerExtraOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int firstVisibleItemPosition = ((StaggeredGridLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPositions(null)[0];
                if (firstVisibleItemPosition > 1) {
                    ViewUtils.showViews(topButton);
                } else if (firstVisibleItemPosition <= 1) {
                    ViewUtils.hideViews(topButton);
                }
            }
        });

        // //상단으로 버튼 클릭시
        topButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mRecyclerView.stopScroll();
                mLayoutManager.scrollToPosition(0);
                ViewUtils.hideViews(topButton, header);

                //탑버튼 클릭시 해더영역 노출
                ((HomeActivity) getContext()).expandHeader();

                ViewHelper.setTranslationY(topButton, 0);

                ViewHelper.setTranslationY(mCoordinator.getFooterLayout(), 0);
            }
        });
    }

    @Override
    public void onPause() {
        EventBus.getDefault().post(new Events.FlexibleEvent.StartRollingGSChoiceRollBannerEvent(false));
        super.onPause();
    }

    @Override
    public void onResume() {
        EventBus.getDefault().post(new Events.FlexibleEvent.StartRollingGSChoiceRollBannerEvent(true));
        super.onResume();
    }

    private void addFooter(List<ModuleList> contents) {
        //푸터추가
        ModuleList footer = new ModuleList();
        footer.tempViewType = ViewHolderType.BANNER_TYPE_FOOTER;
        contents.add(footer);
    }

    /**
     * 섹션 클릭시 해당 포지션으로 이동한다.
     *
     * @param event ClickEventLtypeAnchor
     */
    public void onEvent(Events.ClickEventLtypeAnchor event) {
        //스티키이벤트 제거
        Events.ClickEventLtypeAnchor stickyEvent = EventBus.getDefault().getStickyEvent(Events.ClickEventLtypeAnchor.class);
        if (stickyEvent != null) {
            EventBus.getDefault().removeStickyEvent(stickyEvent);
        }

        if (event.navigationId != null && !event.navigationId.equals(mNavigationID)) {
            return;
        }
        if (isNotEmpty(tabPrdMapLType.get(event.tabSeq))) {
            header.setVisibility(View.VISIBLE);
            int pos = tabPrdMapLType.get(event.tabSeq);

            int offset = DisplayUtils.convertDpToPx(getAppContext(), event.offsetDp);
            mLayoutManager.scrollToPositionWithOffset(pos, offset);

            if (headerViewHolder instanceof TabImgAnchGbaVH) {
                headerViewHolder.setSelectedItem(event.selectedPosition);
            }
        }
    }

    /**
     * 섹션 클릭시 해당 포지션으로 이동한다.
     * (이벤트 (GS헤택) 용)
     * @param event EventSectionClickEvent
     */
    public void onEvent(Events.EventOnEvent.EventSectionClickEvent event) {
        if (event.navigationId != null && !event.navigationId.equals(mNavigationID)) {
            return;
        }
        if (isNotEmpty(tabPrdMapEvent.get(event.tabSeq))) {
            header.setVisibility(View.VISIBLE);
            int pos = tabPrdMapEvent.get(event.tabSeq);
            mLayoutManager.scrollToPositionWithOffset(pos, 0);
        }
    }
}
