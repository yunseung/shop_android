package gsshop.mobile.v2.home.shop.flexible.shoppinglive;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.google.inject.Inject;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.pattern.mvc.BaseAsyncController;

import java.util.Random;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.home.TopSectionList;
import gsshop.mobile.v2.home.main.ContentsListInfo;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.ViewHolderType;
import gsshop.mobile.v2.home.shop.bestdeal.BestDealShopFragment;
import gsshop.mobile.v2.home.shop.flexible.FlexibleShopAdapter;
import gsshop.mobile.v2.util.ClickUtils;
import gsshop.mobile.v2.util.DataUtil;
import gsshop.mobile.v2.util.LunaUtils;
import roboguice.util.Ln;

public class ShoppingLiveShopFragment extends BestDealShopFragment {

    private boolean isItemLoading = false;
    
    private long mStartDate = 0; // 현재 측정할 시간

    /********************* static ****************************/

    private static int REFRESH_LIMIT = 9;

    private static int mRefreshCnt = 0; // 호출 횟수 제한 확인용

    private static boolean IS_DEBUG = false;

    public static ShoppingLiveShopFragment instance;

    public static ShoppingLiveShopFragment getInstance() {
        return instance;
    }

    public static String KEY_FIRST_SHOW = "KEY_FIRST_SHOW";

    public static ShoppingLiveShopFragment newInstance(int position) {
        instance = newInstanceInternal(position);
        return instance;
    }

    private static ShoppingLiveShopFragment newInstanceInternal(int position) {
        ShoppingLiveShopFragment fragment = new ShoppingLiveShopFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new ShoppingLiveShopAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);
        
    }

    @Override
    public void updateList(TopSectionList sectionList, ContentsListInfo contentsListInfo, int tab, int listPosition) {
        super.updateList(sectionList, contentsListInfo, tab, listPosition);

        mStartDate = getFirstStartTimeFromPrdLive();
    }

    // 타이머를 프래그먼트 자체에 놓는다. (아이템 별로 놓으면 문제 있을 수 있다.)
    private long getFirstStartTimeFromPrdLive() {
        long startDate = 0;
        try {
            // PRD_ML 전체 검사 수행
            for (int i = 0; i < mAdapter.getInfo().contents.size(); i++) {
                // 방송 확인을 위한 아이템.
                ShopInfo.ShopItem content = mAdapter.getInfo().contents.get(i);

                // 해당 타입이 PRD_MOBIOLE_LIVE 이면 타이머 시작을 위해 시작 시간을 확인
                if (content.type == ViewHolderType.VIEW_TYPE_PRD_MOBILE_LIVE) {
                    // 0번째 확인을 위해 0보다 큰지 확인.
                    if (content.sectionContent.subProductList.size() > 0) {
                        try {
                            // 0번째 startDate 가져온다.
                            startDate = content.sectionContent.subProductList.get(0).startDate;
                        } catch (IndexOutOfBoundsException e) {
                            return 0;
                        }
                        break;
                    }
                }
            }
        }
        catch (NullPointerException e) {
            Ln.e(e.getMessage());

            LunaUtils.sendToLuna(getContext(), e, null);
        }
        if (IS_DEBUG)
            Ln.d("hklim nextTime : " + startDate);

        return startDate;
    }

    public void onEventMainThread(Events.TimerEvent event) {
        if (mStartDate > 0 && System.currentTimeMillis() > mStartDate) {
            if (ClickUtils.removePrdLiveItem(60000)) { // 1분 이내로 넉넉하게 해놓으면 문제 없음.
                return;
            }

            if (IS_DEBUG)
                Ln.d("hklim : startDate : " + mStartDate + " / currentTimeMillis : " + System.currentTimeMillis());

            EventBus.getDefault().postSticky(new Events.ShoppingLiveEvent.MobileLiveSldRefreshEvent(true));

            // 위 갱신 로직이 5~15초 이후에 수행 하기 떄문에 30초 후에 갱신 로직 수행,
            // 여기 탄 이후로 60초간은 타지 않기 떄문에 반복 수행 하지 않는다.
            new Handler().postDelayed(() ->
                    mStartDate = getFirstStartTimeFromPrdLive()
            ,30000);
        }
    }

    public void onEvent(Events.ShoppingLiveEvent.MobileLiveSldRefreshEvent event) {
        Events.ShoppingLiveEvent.MobileLiveSldRefreshEvent curationEvent = EventBus.getDefault().getStickyEvent(Events.ShoppingLiveEvent.MobileLiveSldRefreshEvent.class);
        if (curationEvent != null) {
            EventBus.getDefault().removeStickyEvent(curationEvent);
        }
        if (ClickUtils.shoppingLiveTimeCheck(1000)) {
            return;
        }

        // 타임 아웃에 의해 수행되는 event일 경우에는 갱신 컨트롤러 수행
        if (event.isTimedOut) {
            if (IS_DEBUG)
                Ln.d("hklim 최초 비교!! 타임 아웃에 의해 발생하기 때문에 비교 후에 갱신할거야.");

            int randomInteger = (new Random().nextInt(10) + 5) * 1000;

            if (IS_DEBUG)
                Ln.d("hklim 최초 비교!! 갱신 컨트롤러 수행할 시간은 : " + randomInteger);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (IS_DEBUG)
                        Ln.d("hklim 최초 비교!! 갱신 컨트롤러 수행해라!!");

                    new BanMLPrdUpdateContoller(getContext(), mAdapter).execute(mAdapter.getInfo().ajaxPageUrl, false);
                }
            }, randomInteger);
        }

        // 리사이클러 notifyDataSetChanged 하게되면, 스탑 시켜야 한다.
        mRecyclerView.stopScroll();

        ShopInfo.ShopItem content;
        for (int i = 0; i < mAdapter.getInfo().contents.size(); i++) {
            content = mAdapter.getInfo().contents.get(i);

            boolean isAvailable = false;
            if(content.type == ViewHolderType.VIEW_TYPE_PRD_MOBILE_LIVE) {
                try {
                    for (int j = 0; j < content.sectionContent.subProductList.size(); j ++) {
                        SectionContentList item = content.sectionContent.subProductList.get(j);

                        if (IS_DEBUG)
                            Ln.d("hklim 모바일라이브 확인 1 PRD_MOBILE_LIVE 데이터 : " + j);

                        if (item.startDate - System.currentTimeMillis() < 0) {
                            if (IS_DEBUG)
                                Ln.d("hklim 모바일라이브 확인 1 PRD_MOBILE_LIVE 데이터 삭제 : " + j);

                            content.sectionContent.subProductList.remove(j);
                            j--;
                        }
                        else {
                            isAvailable = true;
                            break;
                        }
                    }

                    // 모든 데이터가 표시 시간이 정상적이지 않으면 데이터 remove
                    if (!isAvailable) {

                        if (IS_DEBUG)
                            Ln.d("hklim 모바일라이브 확인 2 모든 데이터 시간 지남");

                        mAdapter.getInfo().contents.remove(i);
                        if (mAdapter.getInfo().contents.get(i - 1).type == ViewHolderType.VIEW_TYPE_TITLE_MOBILE_LIVE) {
                            mAdapter.getInfo().contents.remove(i - 1);
                        }
                    }

                    // 전부 그려지기 전에 리프레시 되는 경우가 존재 하여 0.5초 이후에 호출 되도록 함.
                    new Handler().postDelayed(() ->
                        mAdapter.notifyDataSetChanged(), 500
                    );
                }
                catch (IndexOutOfBoundsException | NullPointerException e) {

                    if (IS_DEBUG)
                        Ln.e("hklim 모바일라이브 데이터 확인 에러 발생");

                    Ln.e(e.getMessage());
                }

                break;
            }
        }
    }

    /**
     * 섹션 업데이트
     */
    public class BanMLPrdUpdateContoller extends BaseAsyncController<ContentsListInfo> {

        @Inject
        private RestClient restClient;
        private String url;
        private boolean isCacheData;
        private Context context;

        private FlexibleShopAdapter flexibleAdapter;

        protected BanMLPrdUpdateContoller(Context activityContext, FlexibleShopAdapter flexibleAdapter) {
            super(activityContext);
            this.context = activityContext;
            this.flexibleAdapter = flexibleAdapter;
        }

        @Override
        protected void onPrepare(Object... params) throws Exception {
            super.onPrepare(params);
            url = (String) params[0];
            isCacheData = (boolean) params[1];
            if (dialog != null) {
                dialog.setCancelable(false);
            }
        }

        @Override
        protected ContentsListInfo process() throws Exception {
            // 플렉서블 매장 카테고리 탭 이미지 다운로드.
            return (ContentsListInfo) DataUtil.getData(context, restClient, ContentsListInfo.class,
                    isCacheData, true, url + "&reorder=true", null);
        }

        @Override
        protected void onError(Throwable e) {
            super.onError(e);
            isItemLoading = true;
        }

        @Override
        protected void onSuccess(ContentsListInfo listInfo) throws Exception {
//            Ln.d("hklim refresh onSuccess");
            isItemLoading = false;
//            flexibleAdapter.getInfo().ajaxPageUrl = listInfo.ajaxfullUrl;
//            ShopInfo tempInfo = flexibleAdapter.getInfo();
//            int startDataSize = flexibleAdapter.getInfo().contents.size();
            int updateDataSize = listInfo.productList.size();

            if (updateDataSize == 0) {
                isItemLoading = true;
                return;
            }
            ShopInfo.ShopItem content;

            // 하나만 들어올꺼야.
//            SectionContentList tempContent = listInfo.productList.get(0);
//            int tempType = getFlexibleViewType(tempContent);

            int tempIndex = 0;

            // 갱신 여부 판단
            boolean isRefresh = true;
            
            // 기존에 이거 있는 지 판단 여부
            boolean isFound = false;

            // 조건 일치 여부 판단
            boolean isSameFound = false;

            // 어댑터에 있는 녀석들 다 뒤져 보자.
            for (int i = 0; i < flexibleAdapter.getInfo().contents.size(); i++) {

                content = flexibleAdapter.getInfo().contents.get(i);

                if (content.type == ViewHolderType.VIEW_TYPE_BAN_SLD_MOBILE_LIVE_BROAD) {
                    if (IS_DEBUG)
                        Ln.d("hklim 기존에 있엇어 이녀석!!");
                    isFound = true;

                    // 이제 비교를 위해서 listInfo 받아온 애를 뒤져보자!
                    for (int j = 0; j < listInfo.productList.size(); j++) {
                        SectionContentList tempContent = listInfo.productList.get(j);
                        int tempType = getFlexibleViewType(tempContent);

                        if (content.type == tempType) {
                            // 야 같은 타입 찾았다!!
                            if (IS_DEBUG)
                                Ln.d("hklim 야 같은 타입 찾았다!!");

                            // 찾았음.
                            isSameFound = true;
                            try {
                                // 받아오는 리스트 BAN_SLD_MOBILE_LIVE_BROAD 타입일때에 첫번째 리스트의
                                // productName 과 linkUrl 비교하여 같으면 갱신 데이터가 아닌것으로 판단하여 최대 10번 까지 재시도
                                // 타이밍 이슈인지, 데이터 바뀌어도 계속 갱신 하려고 하는 문제가 있어 최초 item을 가지고 있게끔 한 후 둘을 비교.
                                if (tempContent.subProductList.size() > 0 &&
                                        content.sectionContent.subProductList.size() > 0 &&
                                        tempContent.subProductList.get(0).linkUrl.equals(
                                                content.sectionContent.subProductList.get(0).linkUrl) &&
                                        tempContent.subProductList.get(0).productName.equals(
                                                content.sectionContent.subProductList.get(0).productName)
                                ) {
                                    // 갱신 안함.
                                    if (IS_DEBUG)
                                        Ln.d("hklim 헐 뭐임? 데이터 같은놈이네 에이...");
                                    isRefresh = false;
                                }
                                else {
                                    // 갱신 할꺼야
                                    if (IS_DEBUG)
                                        Ln.d("hklim 데이터 다르다 아싸 바꿔치기 ㄱㄱ...");
                                    isRefresh = true;

                                    try {
                                        // 조건 충족해서 데이터 바꿔치기
                                        content.sectionContent = tempContent;
                                        flexibleAdapter.getInfo().contents.set(i, content);

                                        // 이전 놈들이 타이틀일때에 타이틀 아이템도 바꾸자 이놈은 바꾸다가 exception 나도 그냥 그러려니 하자
                                        try {
                                            SectionContentList tempContentPrev = listInfo.productList.get(j - 1);
                                            ShopInfo.ShopItem tempShopItemPrev = flexibleAdapter.getInfo().contents.get(i - 1);

                                            if (tempShopItemPrev.type == ViewHolderType.VIEW_TYPE_TITLE_MOBILE_LIVE &&
                                                    getFlexibleViewType(tempContentPrev.viewType) == ViewHolderType.VIEW_TYPE_TITLE_MOBILE_LIVE) {
                                                tempShopItemPrev.sectionContent = tempContentPrev;
                                                flexibleAdapter.getInfo().contents.set(i - 1, tempShopItemPrev);
                                            }
                                        }
                                        catch (IndexOutOfBoundsException | IllegalArgumentException | NullPointerException e) {
                                            Ln.e(e.getMessage());
                                        }
                                        flexibleAdapter.notifyDataSetChanged();

                                    }
                                    catch (IndexOutOfBoundsException | IllegalArgumentException | NullPointerException e) {
                                        Ln.e(e.getMessage());
                                    }
                                }
                            } catch (NullPointerException e) {
                                if (IS_DEBUG)
                                    Ln.d("hklim 갱신 하다가 NullPointerException 에러 났어 임마!");
                                Ln.e(e.getMessage());
                            }
                            break;
                        }
                    }
                }

                //찾았으면 더 비교할 거 없이 빠져나감.
                if (isSameFound) {
                    if (IS_DEBUG)
                        Ln.d("hklim 찾았으면 더 비교할 거 없이 빠져나감.");
                    break;
                }
            }

            // 기존에 얘가 없었네... 전체 갱신? 추후 다시 적용 4월 29일
//            if (!isFound) {
////                Ln.d("hklim 기존에 얘가 없었네... 전체 갱신?");
//                onSwipeRefrehsing();
//            }

            // 찾았는데 갱신을 못했어 그러면 데이터 정합성이 이유니 갱신 다시 요청해라.
            if (!isRefresh && isSameFound) {
                if (IS_DEBUG)
                    Ln.d("hklim 찾았는데 갱신을 못했어 그러면 데이터 정합성이 이유니 갱신 다시 요청해라.");
                // 최대 횟수 확인 후 갱신하도록 수정.
                if (mRefreshCnt <= REFRESH_LIMIT) {
                    if (IS_DEBUG)
                        Ln.d("hklim 다시 refresh!!. mRefreshCnt : " + mRefreshCnt);
                    EventBus.getDefault().post(new Events.ShoppingLiveEvent.MobileLiveSldRefreshEvent(true));

                    mRefreshCnt++;
                }
                else {
                    if (IS_DEBUG)
                        Ln.d("hklim 에이, 10번 요청 했는데도 똑같네 나두 몰겠다.");
                    mRefreshCnt = 0;
                }
            }
            else {
                // 그 외의 경우는 갱신 못하는 경우이니 갱신할 필요 없음 그냥 빠져 나간다.
                mRefreshCnt = 0;
            }
        }
    }
}
