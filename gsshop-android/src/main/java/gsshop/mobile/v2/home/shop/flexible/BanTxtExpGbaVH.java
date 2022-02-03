package gsshop.mobile.v2.home.shop.flexible;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.viewpager.widget.ViewPager;

import com.gsshop.mocha.ui.util.ViewUtils;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.shop.BaseViewHolder;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.util.IconTabBar;
import gsshop.mobile.v2.library.viewpager.InfinitePagerAdapter;
import gsshop.mobile.v2.library.viewpager.InfiniteViewPager;
import gsshop.mobile.v2.util.GlobalTimer;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isEmpty;
import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;

/**
 * 인기검색어 배너
 */
public class BanTxtExpGbaVH extends BaseViewHolder {

    private Context mContext;

    /**
     * 인기검색어 데이타
     */
    private SectionContentList item;

    /**
     * 최상위 뷰
     */
    private View viewRoot;

    /**
     * 펼친화면 상단 타이틀
     */
    private TextView txtVpTitle;

    /**
     * 펼친화면 우하단 설명글
     */
    private TextView txtVpDesc;

    /**
     * 롤링용 뷰스위처
     */
    private ViewSwitcher viewSwitcher;

    /**
     * 뷰 스위쳐를 감싸고 있는 뷰
     */
    private View mFrameSwitcher;

    /**
     * 롤링순환을 위한 카운터
     */
    private int dspNum = 0;

    /**
     * 펼친화면 뷰
     */
    private View layExpand;

    /**
     * 뷰페이저
     */
    private InfiniteViewPager viewPager;

    /**
     * 인디케이터
     */
    private IconTabBar indicators;
    private MySimpleOnPageChangeListener mPageChangeListener = new MySimpleOnPageChangeListener();

    /**
     * 검색어 롤링 주기 (단위:초)
     */
    private final static int INTERVAL_SECOND = 2;

    /**
     * 순차증가용 변수
     */
    private int INTERVAL_SEQ = 2;

    /**
     * 펼침화면에서 한화면에 리스트할 인기검색어 수
     */
    public final static int ITEM_NUM_PER_PAGE = 5;

    private CheckBox btnExpend;

    /**
     * @param itemView
     */
    public BanTxtExpGbaVH(View itemView) {
        super(itemView);

        viewRoot = itemView.findViewById(R.id.view_root);
        txtVpTitle = itemView.findViewById(R.id.txt_vp_title);
        txtVpDesc = itemView.findViewById(R.id.txt_vp_desc);
        layExpand = itemView.findViewById(R.id.lay_expand);

        viewSwitcher = itemView.findViewById(R.id.viewSwitcher);
        mFrameSwitcher = itemView.findViewById(R.id.layout_switcher);
//        ViewUtils.hideViews(viewSwitcher);
        ViewSwitcher.ViewFactory vFactory = new ViewSwitcher.ViewFactory(){
            @Override
            public View makeView() {
                return LayoutInflater.from(itemView.getContext()).inflate(R.layout.home_row_type_fx_popular_searching_banner_item, null);
            }
        };
        viewSwitcher.setInAnimation(itemView.getContext(), R.anim.livetalk_up_in);
        viewSwitcher.setOutAnimation(itemView.getContext(), R.anim.livetalk_up_out);
        viewSwitcher.setFactory(vFactory);

        viewPager = itemView.findViewById(R.id.viewPager);
        indicators = itemView.findViewById(R.id.indicator);
        btnExpend = itemView.findViewById(R.id.btn_expand);
        btnExpend.setContentDescription("인기검색어 펼침");
        btnExpend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnExpend.isChecked()) {
                    double dPageSize = item.subProductList.size() / (double) ITEM_NUM_PER_PAGE;
                    int nPageSize = (int) Math.ceil(dPageSize);

                    int thisPage = viewPager.getRealCurrentItem() % nPageSize;
                    int nRealFirstPage = viewPager.getRealCurrentItem() - thisPage;

                    viewPager.setCurrentItem(nRealFirstPage);
                    ViewUtils.showViews(layExpand);
                    ViewUtils.hideViews(mFrameSwitcher);
                }
                else {
                    ViewUtils.showViews(mFrameSwitcher);
                    ViewUtils.hideViews(layExpand);
                    dspNum = 0;
                    View mView = viewSwitcher.getCurrentView();
                    setExpandViewText(mView);
                    if (dspNum >= item.subProductList.size() -1) {
                        dspNum = 0;
                    } else {
                        dspNum++;
                    }
                }
            }
        });
    }

    @Override
    public void onBindViewHolder(final Context context, int position, ShopInfo info,
                                 final String action, final String label, String sectionName) {
        super.onBindViewHolder(context, position, info, action, label, sectionName);

        this.mContext = context;
        this.item = info.contents.get(position).sectionContent;

        //데이타가 없으면 뷰홀더 노출 안함
        if(isEmpty(item.subProductList)) {
            viewRoot.setVisibility(View.GONE);
            return;
        }

        txtVpTitle.setText(item.exposePriceText);
        txtVpDesc.setText(item.etcText1);

        InfinitePagerAdapter wrappedAdapter = new InfinitePagerAdapter(new PopularSearchingPagerAdapter(context, item.subProductList));
        viewPager.setAdapter(wrappedAdapter);
        viewPager.setCurrentItem(0);

        viewPager.removeOnPageChangeListener(mPageChangeListener);
        viewPager.addOnPageChangeListener(mPageChangeListener);

        // 페이지를 넘길 때 마다 전체 뷰페이지 크기 (최대 21억~) 및 현재 페이지를 읽어주는 문제가 존재.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            viewPager.setAccessibilityDelegate(new View.AccessibilityDelegate() {
                @Override
                public void onPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
                    super.onPopulateAccessibilityEvent(host, event);

                    if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_SELECTED ||
                            event.getEventType() == AccessibilityEvent.TYPE_VIEW_SCROLLED) {

                        String strConDescription = viewPager.getRealCurrentItem() + "페이지";
                        event.setContentDescription(strConDescription);
                    }
                }
            });
        }

        // indicator
        indicators.setIconTabListener(new IndicatorListener(context, item.subProductList.size()));
        indicators.setShouldExpand(false);
        int dotPadding = context.getResources().getDimensionPixelOffset(R.dimen.popular_searching_viewpager_dot_padding);
        //indicators.setPadding(0, 0, dotPadding,  0);
        indicators.setTabPaddingLeftRight(dotPadding);
        indicators.setSelectedTab(0);

        //롤링 시작
        setRollingText();
    }

    /**
     * 뷰페이저 변경시 인디케이터 세팅
     */
    private class MySimpleOnPageChangeListener extends ViewPager.SimpleOnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            int item = viewPager.getCurrentItem();
            indicators.setSelectedTab(item);
        }
    }

    /**
     * 인디케이터 어뎁터
     */
    private class IndicatorListener implements IconTabBar.IconTabListener {

        private final int count;
        private final Context context;

        /**
         * @param count
         */
        public IndicatorListener(Context context, int count) {
            this.context = context;
            this.count = (int)Math.ceil((count/(double)ITEM_NUM_PER_PAGE));
        }

        @Override
        public Drawable getSelectedTabIconDrawable(int position, boolean selected) {
            int id;
            if (selected) {
                id = R.drawable.indicator_dot_on;
            } else {
                id = R.drawable.indicator_dot_off;
            }

            Drawable drawable;
            if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
                drawable = context.getDrawable(id);
            } else {
                drawable = context.getResources().getDrawable(id);
            }

            return drawable;
        }

        @Override
        public void onIconTabClicked(int position, View view) {

        }

        @Override
        public int tabSize() {
            return count;
        }
    }

    private void setExpandViewText(View mView) {
        ((Activity)mContext).runOnUiThread(() -> {
            if (isEmpty(item.subProductList)) {
                return;
            }
            SectionContentList subItem = item.subProductList.get(dspNum);
            if (isEmpty(subItem)) {
                return;
            }

            ((TextView)mView.findViewById(R.id.txt_rank)).setText(subItem.saleQuantity);
            ((TextView)mView.findViewById(R.id.txt_keyword)).setText(subItem.productName);

            View layRankStateNew = mView.findViewById(R.id.lay_rank_state_new);
            View layRankStateUp = mView.findViewById(R.id.lay_rank_state_up);
            View layRankStateDown = mView.findViewById(R.id.lay_rank_state_down);
            View layRankStateSame = mView.findViewById(R.id.lay_rank_state_same);

            ViewUtils.hideViews(layRankStateNew, layRankStateUp, layRankStateDown, layRankStateSame);
            ViewUtils.showViews(viewSwitcher);
            TextView txtRankState = null;
            String rankState = subItem.discountRateText;
            if ("n".equalsIgnoreCase(rankState)) {
                //새로진입
                ViewUtils.showViews(layRankStateNew);
            } else if ("u".equalsIgnoreCase(rankState)) {
                //상승
                ViewUtils.showViews(layRankStateUp);
                txtRankState = (TextView)mView.findViewById(R.id.txt_rank_state_up);
            } else if ("d".equalsIgnoreCase(rankState)) {
                //하락
                ViewUtils.showViews(layRankStateDown);
                txtRankState = (TextView)mView.findViewById(R.id.txt_rank_state_down);
            } else if ("f".equalsIgnoreCase(rankState)) {
                //변화없음
                ViewUtils.showViews(layRankStateSame);
            }

            if (isNotEmpty(txtRankState)) {
                txtRankState.setText(subItem.discountRate);
            }

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WebUtils.goWeb(mView.getContext(), subItem.linkUrl);
                }
            });
        });
    }

    private void setRollingText() {
        ((Activity)mContext).runOnUiThread(() -> {
            if (isEmpty(item.subProductList)) {
                return;
            }
            SectionContentList subItem = item.subProductList.get(dspNum);
            if (isEmpty(subItem)) {
                return;
            }

            View mView = viewSwitcher.getNextView();
            setExpandViewText(mView);

            viewSwitcher.showNext();

            if (dspNum >= item.subProductList.size() -1) {
                dspNum = 0;
            } else {
                dspNum++;
            }
        });
    }

    @Override
    public void onViewAttachedToWindow() {
        super.onViewAttachedToWindow();
        try {
            EventBus.getDefault().register(this);
        } catch (Exception e) {
            Ln.e(e);
        }
        GlobalTimer.getInstance().startTimer();
    }

    @Override
    public void onViewDetachedFromWindow() {
        super.onViewDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 글로벌 타이머 이벤트 수신용 콜백 (1초마다 호출됨)
     *
     * @param event
     */
    public void onEvent(Events.TimerEvent event) {
        if (INTERVAL_SECOND == INTERVAL_SEQ) {
            setRollingText();
            INTERVAL_SEQ = 1;
        } else {
            INTERVAL_SEQ++;
        }
    }
}

