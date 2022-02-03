/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.home.shop.gssuper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.StringUtils;

import org.apache.http.NameValuePair;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.home.TopSectionList;
import gsshop.mobile.v2.home.main.ContentsListInfo;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ViewHolderType;
import gsshop.mobile.v2.home.shop.flexible.FlexibleShopFragment;
import gsshop.mobile.v2.support.ui.BugFixedStaggeredGridLayoutManager;
import gsshop.mobile.v2.util.ClickUtils;
import gsshop.mobile.v2.util.CookieUtils;
import gsshop.mobile.v2.util.CustomToast;
import gsshop.mobile.v2.util.DataUtil;
import gsshop.mobile.v2.util.KeyboardHeightObserver;
import gsshop.mobile.v2.util.KeyboardHeightProvider;
import gsshop.mobile.v2.web.WebUtils;

import static com.blankj.utilcode.util.ConvertUtils.dp2px;
import static com.blankj.utilcode.util.EmptyUtils.isEmpty;
import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;
import static com.blankj.utilcode.util.KeyboardUtils.hideSoftInput;

/**
 *
 *
 */
public class GSSuperFragment extends FlexibleShopFragment {

    // 매장로딩 API에 추가할 쿠키명
    private static final String MART_DELI_COOKIE_NAME = "martDeliFlag";

    // 장바구니 팝업 출력 시간
    private static final int TIMER_GSHOP_CART_POPUP_MILLISEC = 3000;        // 3초

    //    팝업 윈도우
    private PopupWindow mPopupWindowCart;

    private View mPopupViewGSSuper;

    // 장바구니 팝업 출력을 위한 핸들러 선언
    private Runnable mRunnable;
    private Handler mHandlerPopupClose;

    // 매장내 검색 창에서 키보드가 올라오면 스크롤을 올려 검색창을 키보드 위에 위치한다.
    private KeyboardHeightProvider mKeyboardHeightProvider;

    // 2019.08.21 yun 기준점으로 부터 얼마나 스크롤이 + - 됐는지 저장해놓는 변수. (새벽배송 툴팁 이동에 쓰여서 만들어놓음)
    private int mTotalScroll = 0;
    private int mDeliveryViewType = 0;
    private boolean mIsVisibleToUser = false;

    public static GSSuperFragment newInstance(int position) {
        //Ln.i("position : " + position);
        GSSuperFragment fragment = new GSSuperFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public GSSuperFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragmentType = FRAGMENT_TYPE.HEADER;
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        mKeyboardHeightProvider = new KeyboardHeightProvider(getActivity());
        mKeyboardHeightProvider.setKeyboardHeightObserver(new KeyboardHeightObserver() {
            @Override
            public void onKeyboardHeightChanged(int height, int orientation) {
                if (!getUserVisibleHint()) return;

                /**
                 * 매장내 검색 창에서 키보드가 올라오면 스크롤을 올려 검색창을 키보드 위에 위치한다.
                 */
                if (height > 0) {
                    // 키보드가 올라옴

                    for (int i = 0; i < mRecyclerView.getChildCount(); i++) {
                        View view = mRecyclerView.getChildAt(i);
                        BaseViewHolder vh = (BaseViewHolder) mRecyclerView.getChildViewHolder(view);
                        if (vh instanceof MapCbSldGbaVH) {
                            int position = vh.getAdapterPosition();
                            ((BugFixedStaggeredGridLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(position, -dp2px(30));
                        }
                    }

                    // delay를 주고 키보드 플래그 값을 설정한다.
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isKeyInputShow = true;
                        }
                    }, 400);
                } else {
                    isKeyInputShow = false;
                }
            }
        });
        mKeyboardHeightProvider.start();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        /**
         * gs fresh 매장이 보일때마다 이전에 타이핑한 검색창의 텍스트를 지운다.
         */
        EventBus.getDefault().post(new Events.FlexibleEvent.GSFreshClearKeywordEvent(getActivity()));
    }

    @Override
    public void onStop() {
        super.onStop();
        mKeyboardHeightProvider.close();
        hideSoftInput(getActivity());
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        mIsVisibleToUser = isVisibleToUser;

        if (getView() != null) {
            if (isVisibleToUser) {
                // gs fresh 매장이 보일때마다 이전에 타이핑한 검색창의 텍스트를 지운다.
                EventBus.getDefault().post(new Events.FlexibleEvent.GSFreshClearKeywordEvent(getActivity()));

                // mIsVisibleToUser flag 로 인해 인접탭에 의한 해당 Fragment 생성 시 툴팁 5초 타이머가 미리 돌아가기 때문에,
                // 인접탭으로 인해 미리 fragment 가 생성은 됐어도 실제로 GSSuperFragment 가 화면에 올라왔을 때 툴팁을 띄워주고 타이머를 주기 위해 이곳에도 넣어놨다.
                setNightDeliverTooltipOption(mDeliveryViewType);
            }
        }


    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Intent intent = getActivity().getIntent();
        String openUrl = intent.getStringExtra(Keys.INTENT.INTENT_OPEN_URL);

        mAdapter = new GSSuperAdapter(getActivity());
        ((GSSuperAdapter) mAdapter).setNavigationID(tempSection.navigationId);
        mRecyclerView.setAdapter(mAdapter);

        // 팝업 윈도우 설정
        mPopupViewGSSuper = view;
        View viewPopupCart = LayoutInflater.from(getActivity()).inflate(R.layout.layout_popup_cart, null);
//        View viewPopupLocation = getActivity().getLayoutInflater().inflate(R.layout.popup_webview, null);

        mPopupWindowCart = new PopupWindow(viewPopupCart,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindowCart.setAnimationStyle(0);

        if (!StringUtils.isEmpty(openUrl)) {
            WebUtils.goWeb(getContext(), openUrl);
        }

        this.mHandlerPopupClose = new Handler();
        this.mRunnable = new Runnable() {
            @Override
            public void run() {
                if (mPopupWindowCart.isShowing()) {
                    mPopupWindowCart.dismiss();
                }
            }
        };
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // keyboard 가 화면에 보이면 스크롤 할때 키보드를 내린다.
                if (isKeyInputShow && dy != 0) {
                    EventBus.getDefault().post(new Events.FlexibleEvent.GSFreshClearKeywordEvent(getActivity()));
                }

                mTotalScroll += dy;

                traceHeaderView(dy > 0);
            }
        });
    }

    /**
     * 해더 뷰홀더를 검색하여 노출/숨김 처리를 수행한다.
     *
     * @param isUp if true, scroll up
     */
    private void traceHeaderView(boolean isUp) {
        int firstVisiblePosition = mLayoutManager.findFirstVisibleItemPositions(null)[0];
        int lastVisiblePosition = mLayoutManager.findLastVisibleItemPositions(null)[0];

        for (int i = firstVisiblePosition; i <= lastVisiblePosition; i++) {
            traceHeaderViewProc(i, isUp);
        }
    }

    /**
     * 해더 뷰홀더를 검색하여 노출/숨김 처리를 수행한다. (실행로직)
     *
     * @param i 뷰홀더 포지션
     */
    private void traceHeaderViewProc(int i, boolean isUp) {
        Rect rvRect = new Rect();
        mRecyclerView.getGlobalVisibleRect(rvRect);

        Rect rowRect = new Rect();
        View v = mLayoutManager.findViewByPosition(i);
        if (isEmpty(v)) {
            return;
        }

        String sectionCode = "";

        BaseViewHolder vh = (BaseViewHolder) mRecyclerView.getChildViewHolder(v);
        if (vh.getItemViewType() == ViewHolderType.VIEW_TYPE_MAP_CX_GBB_TAB) {
            vh.itemView.getGlobalVisibleRect(rowRect);
            if (rowRect.top <= rvRect.top && isUp) {
                header.setVisibility(View.VISIBLE);
            } else if (rowRect.top > rvRect.top && !isUp) {
                header.setVisibility(View.GONE);
            }
        }

        if (vh.getItemViewType() == ViewHolderType.VIEW_TYPE_MAP_CX_GBB
                || vh.getItemViewType() == ViewHolderType.VIEW_TYPE_MAP_CX_GBB_TAB) {
            vh.itemView.getGlobalVisibleRect(rowRect);
            if (rowRect.top <= rvRect.top && isUp) {
                //스크롤 이벤트 0.3초단위 필터링
                if (ClickUtils.work(300)) {
                    return;
                }
                sectionCode = vh.getSectionCode();
                EventBus.getDefault().post(new Events.FlexibleEvent.GSSuperSectionSyncEvent(sectionCode));
            } else if (rowRect.bottom > rvRect.top && !isUp) {
                //스크롤 이벤트 0.3초단위 필터링
                if (ClickUtils.work2(300)) {
                    return;
                }
                sectionCode = vh.getSectionCode();
                EventBus.getDefault().post(new Events.FlexibleEvent.GSSuperSectionSyncEvent(sectionCode));
            }
        }
    }

    @Override
    protected int getFlexibleViewType(SectionContentList content) {
        int type = getFlexibleViewType(content.viewType);

        if (type == ViewHolderType.TOAST_TYPE_GS_SUPER_TIME) {
            type = ViewHolderType.BANNER_TYPE_NONE;

            CustomToast.makeGSSuperToast(getActivity(), content);
            CustomToast.showGSSuperToast(getActivity(), false);
        }
        return type;
    }

    @Override
    protected int getFlexibleViewType(String viewType) {
        int type = super.getFlexibleViewType(viewType);
        if ("MAP_C8_SLD_GBA".equals(viewType)) {
            // GS Super Menu view pager
            type = ViewHolderType.BANNER_TYPE_GS_SUPER_ROLL_MENU;
        } else if ("BAN_IMG_GSF_GBA".equals(viewType)) {
            // GS Super Title bar (당일배송)
            type = ViewHolderType.VIEW_TYPE_BAN_IMG_GSF_GBA;
        } else if ("BAN_IMG_GSF_GBB".equals(viewType)) {
            // GS Super Title bar (당일배송+새벽)
            type = ViewHolderType.VIEW_TYPE_BAN_IMG_GSF_GBB;
        } else if ("BAN_IMG_GSF_GBC".equals(viewType)) {
            // GS Super Title bar (새벽배송+당일)
            type = ViewHolderType.VIEW_TYPE_BAN_IMG_GSF_GBC;
        } else if ("BAN_IMG_GSF_GBD".equals(viewType)) {
            // GS Super Title bar (택배)
            type = ViewHolderType.VIEW_TYPE_BAN_IMG_GSF_GBD;
        } else if ("BAN_IMG_GSF_GBE".equals(viewType)) {
            // GS Super Title bar (택배 + 새벽)
            type = ViewHolderType.VIEW_TYPE_BAN_IMG_GSF_GBE;
        } else if ("BAN_GSF_LOC_GBA".equals(viewType)) {
            // GS Super select location bar
            type = ViewHolderType.VIEW_TYPE_BAN_GSF_LOC_GBA;
        } else if ("MAP_CX_GBB".equals(viewType)) {
            // Gs Super product item 2ea
            type = ViewHolderType.VIEW_TYPE_MAP_CX_GBB;
        } else if ("BAN_SLD_GBD".equals(viewType)) {
            // Gs Super auto slide image banners
            type = ViewHolderType.BANNER_TYPE_GS_SUPER_SLD_GBD;
        } else if ("TOOLTIP_GSF_GBA".equals(viewType)) {

            type = ViewHolderType.TOAST_TYPE_GS_SUPER_TIME;
        }

        return type;
    }

    public void onEventMainThread(Events.FlexibleEvent.CartPopupEvent event) {

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

                WebUtils.goWeb(getContext(), ServerUrls.WEB.FRESH_SMART_CART_TOP);
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

        mHandlerPopupClose.postDelayed(mRunnable, TIMER_GSHOP_CART_POPUP_MILLISEC);

        mPopupWindowCart.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mHandlerPopupClose.removeCallbacks(mRunnable);
            }
        });
    }

    @Override
    public void updateList(TopSectionList sectionList, ContentsListInfo contentsListInfo, int tab, int listPosition) {
        //사용변수 초기화
        isFirst = true;
        tabPrdMap.clear();

        mDeliveryViewType = getFlexibleViewType(contentsListInfo.productList.get(0).viewType);

        if (mDeliveryViewType == ViewHolderType.TOAST_TYPE_GS_SUPER_TIME) {
            mDeliveryViewType = getFlexibleViewType(contentsListInfo.productList.get(1).viewType);
        }

        super.updateList(sectionList, contentsListInfo, tab, listPosition);

        // 2019.08.21 yun. updateList 함수가 두 번 연속 호출되는 케이스가 있어서 툴 팁 관리 한 번만 호출하도록 함.
        if (mIsVisibleToUser) {
            if (ClickUtils.work(2000)) {
                return;
            } else {
                setNightDeliverTooltipOption(mDeliveryViewType);
            }
        }


    }

    @Override
    public void drawFragment() {
        new GSShopController(getActivity(), tempSection, true).execute();
    }

    /**
     * 2019.08.21 yun.
     * 새벽배송 go 버튼이 노출되는 경우에 새벽배송 뷰홀더 상단에 보여줄 툴팁 셋팅.
     * @param viewType 뷰 타입
     */
    private void setNightDeliverTooltipOption(int viewType) {
        //새벽배송 노출 조건이고, 하루 한 번만 보여주기 위한 조건

        if (viewType == ViewHolderType.VIEW_TYPE_BAN_IMG_GSF_GBB || viewType == ViewHolderType.VIEW_TYPE_BAN_IMG_GSF_GBE) {
            if (!MainApplication.isShownNightDeliveryTooltip) {
                // 딜레이 없이 바로 이벤트버스를 태울 경우 씹히는 문제가 있어서 50millis 딜레이를 줬다.
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post(new Events.NightDeliveryTooltipEvent(true));
                    }
                }, 50);

                // 위 핸들러에서 툴팁을 띄운 후 5초 후 사라지는 애니메이션 시작 시 툴팁이 스크롤 밖으로 나갔을 경우 애니메이션이 동작하지 않고 잡혀있는 현상이 있음.
                // 이를 해결하기 위해 툴팁 사라지는 시간인 5초 후 툴팁이 스크롤 밖에 있을 때 애니메이션을 없애고 툴팁뷰를 gone 시켜주기 위한 핸들러.
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mTotalScroll > 160) {
                            EventBus.getDefault().post(new Events.NightDeliveryTooltipEvent(false));
                        }
                    }
                },3050);
            } else {
                EventBus.getDefault().post(new Events.NightDeliveryTooltipEvent(false));
            }
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    EventBus.getDefault().post(new Events.NightDeliveryTooltipEvent(false));
                }
            }, 50);
        }
    }

    /**
     * 섹션 업데이트
     */
    public class GSShopController extends GetUpdateController {

        protected GSShopController(Context activityContext, TopSectionList sectionList, boolean isCacheData) {
            super(activityContext, sectionList, isCacheData);
        }

        @Override
        protected ContentsListInfo process() throws Exception {
            // 플렉서블 매장 카테고리 탭 이미지 다운로드.
            return (ContentsListInfo) DataUtil.getData(context, restClient, ContentsListInfo.class,
                    isCacheData, true, tempSection.sectionLinkUrl,
                    tempSection.sectionLinkParams + getCookieParam() + "&reorder=true", tempSection.sectionName);
        }

        @Override
        protected void onSuccess(ContentsListInfo listInfo) throws Exception {
            //배송시간 웹뷰에서 쿠키를 기준으로 화면을 변경 하기때문에 쿠키동기화 수행
            CookieUtils.copyRestClientCookiesToWebView(context, restClient);
            super.onSuccess(listInfo);
        }
    }

    /**
     * 당일배송/새벽배송 탭글릭시 매장 업데이트
     */
    public class GSSuperUrlUpdateController extends UrlUpdateController {

        public GSSuperUrlUpdateController(Context activityContext) {
            super(activityContext);
        }

        @Override
        protected void onSuccess(ContentsListInfo listInfo) throws Exception {
            //배송시간 웹뷰에서 쿠키를 기준으로 화면을 변경 하기때문에 쿠키동기화 수행
            CookieUtils.copyRestClientCookiesToWebView(context, restClient);
            super.onSuccess(listInfo);
        }
    }

    /**
     * 매장을 리프레시한다.
     *
     * @param event RefreshShopEvent
     */
    public void onEvent(Events.RefreshShopEvent event) {
        if (getUserVisibleHint()) {
            updateGSSuperEventProc(null);
        }
    }

    /**
     * event busrequest header
     *
     * @param event event
     */
    public void onEventMainThread(Events.FlexibleEvent.UpdateGSSuperEvent event) {
        updateGSSuperEventProc(event.url);
    }

    private void updateGSSuperEventProc(String url) {
        //장바구니 -> 로그인 -> 본매장 집입시 해더영역 남는 현상 개선
        header.setVisibility(View.GONE);
        if (url == null) {
            new UrlUpdateController(getActivity()).execute(
                    tempSection.sectionLinkUrl + "?" + tempSection.sectionLinkParams + getCookieParam(), false);
        } else {
            new UrlUpdateController(getActivity()).execute(url + getCookieParam(), false);
        }
    }

    /**
     * 당일배송/새벽배송 매장을 전환한다.
     *
     * @param event GSSuperToogleEvent
     */
    public void onEvent(Events.FlexibleEvent.GSSuperToogleEvent event) {
        if (isNotEmpty(event.url)) {
            new GSSuperUrlUpdateController(getActivity()).execute(event.url, false);
        }
    }

    /**
     * 섹션 클릭시 해당 포지션으로 이동한다.
     *
     * @param event GSSuperSectionClickEvent
     */
    public void onEvent(Events.FlexibleEvent.GSSuperSectionClickEvent event) {
        if (isNotEmpty(tabPrdMap.get(event.sectionCode))) {
            header.setVisibility(View.VISIBLE);
            int pos = tabPrdMap.get(event.sectionCode);
            mLayoutManager.scrollToPositionWithOffset(pos, 0);
        }
    }

    /**
     * 매장 스크롤중 카테고리 선택시 매장스크롤을 중지한다. (정확한 위치이동 위해)
     *
     * @param event GSSuperStopScrollEvent
     */
    public void onEvent(Events.FlexibleEvent.GSSuperStopScrollEvent event) {
        mRecyclerView.stopScroll();
    }

    /**
     * 당일배송, 새벽배송 여부 쿠키값을 조회한다.
     *
     * @return martDeliFlag 쿠키파라미터
     */
    private String getCookieParam() {
        String retVal = "&" + MART_DELI_COOKIE_NAME + "=";
        NameValuePair pair = CookieUtils.getWebviewCookie(getContext(), MART_DELI_COOKIE_NAME);
        if (pair != null) {
           retVal += pair.getValue();
        }
        return retVal;
    }
}
