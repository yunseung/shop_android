/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.search;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.loader.content.AsyncTaskLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.inject.Inject;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.pattern.mvc.AbstractAsyncController;
import com.gsshop.mocha.pattern.mvc.BaseAsyncController;
import com.gsshop.mocha.ui.util.ViewUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.home.TopSectionList;
import gsshop.mobile.v2.menu.BaseTabMenuActivity;
import gsshop.mobile.v2.menu.RunningActivityManager;
import gsshop.mobile.v2.search.SoftKeyboardDectectorView.OnShownKeyboardListener;
import gsshop.mobile.v2.support.gtm.AMPAction;
import gsshop.mobile.v2.support.gtm.AMPEnum;
import gsshop.mobile.v2.support.gtm.GTMAction;
import gsshop.mobile.v2.support.gtm.GTMEnum;
import gsshop.mobile.v2.support.ui.ClearButtonTextWatcher;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog.ButtonClickListener;
import gsshop.mobile.v2.user.PopupBasicActivity;
import gsshop.mobile.v2.util.CookieUtils;
import gsshop.mobile.v2.util.DataUtil;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.PrefRepositoryNamed;
import gsshop.mobile.v2.util.ThreadUtils;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

/**
 * TODO 최근검색어/연관검색어 로직을 fragment로 분리 고려.
 */
@SuppressLint("NewApi")
public class HomeSearchActivity extends BaseTabMenuActivity implements OnClickListener, OnScrollListener {

    /**
     * 사용자가 검색어 입력시 연관검색어 조회작업을 시작하기 위한 최소 대기시간. milliseconds.
     * 검색어를 빠르게 입력하는 경우 불필요한 연관검색어 조
     * 회작업을 방지.
     */
    protected static final int RELATED_KEYWORD_DELAY = 150;

    @InjectView(R.id.edit_search)
    protected EditText editKeyword;

    @InjectView(R.id.btn_delete_keyword)
    protected ImageButton btnKeywordDelete;

    @InjectView(R.id.list_popular_keyword)
    protected ListView listPopularKeyword;

    @InjectView(R.id.list_recent_keyword)
    protected ListView listRecentKeyword;

    @InjectView(R.id.list_related_keyword)
    protected ListView listRelatedKeyword;

    @InjectView(R.id.btn_popular_keyword)
    protected Button btnPopularKeyword;

    @InjectView(R.id.btn_recent_keyword)
    protected Button btnRecentKeyword;

    @InjectResource(R.string.search_popular_update_tail)
    private String popularUdateTail;

    /**
     * 인기/최근 검색어 탭메뉴 영역
     */
    @InjectView(R.id.layout_search_tab_menu)
    protected LinearLayout layoutTabMenu;

    /**
     * 인기검색어 영역
     */
    @InjectView(R.id.layout_popular_keyword)
    protected LinearLayout layoutPopularKeyword;

    /**
     * 최근검색어 영역
     */
    @InjectView(R.id.layout_recent_keyword)
    protected RelativeLayout layoutRecentKeyword;

    /**
     * 연관검색어 영역
     */
    @InjectView(R.id.layout_related_keyword)
    protected LinearLayout layoutRelatedKeyword;

    /**
     * 추천검색어 영역
     */
    @InjectView(R.id.recycler_recommend_list)
    protected RecyclerView recycler_recommend_list;
    @InjectView(R.id.rl_recommend_list_area)
    protected RelativeLayout mRlRecommendListArea;
    @InjectView(R.id.ll_recommend_guide)
    protected LinearLayout mLlRecommendGuide;
    @InjectView(R.id.btn_recommend_guide)
    protected Button mBtnRecommendGuide;
    @InjectView(R.id.btn_guide_close)
    protected Button mBtnGuideClose;
    @InjectView(R.id.ll_tooltip_recommend_guide)
    protected LinearLayout mLlTooltipRecommendGuide;

    /**
     * 연관검색어 데이타 없음 표시 영역
     */
    @InjectView(R.id.layout_related_no_data)
    protected LinearLayout layoutRelatedNoData;

    /**
     * 연관검색어 상단라인
     */
    @InjectView(R.id.vw_related_keyword_top_line)
    protected View vwRelatedKeywordTopLine;

    /**
     * 최근검색어 데이타 없음 표시 영역
     */
    @InjectView(R.id.layout_recent_no_data)
    protected LinearLayout layoutRecentNoData;

    @InjectView(R.id.no_data_textview)
    protected TextView no_data_textview;

    /**
     * 최근검색어 전체삭제 영역
     */
    @InjectView(R.id.layout_clear_recent_keywords)
    protected LinearLayout layoutClearRecentKeyword;
    @InjectView(R.id.btn_clear_recent_keywords)
    protected Button btnClearRecentKeyword;

    @InjectView(R.id.layout_content)
    protected FrameLayout layout_search_bar;

    @Inject
    protected SearchAction searchAction;

    @Inject
    protected SearchNavigation searchNavigation;

    protected PopularKeywordListAdapter popularKeywordListAdapter;

    protected RecentKeywordListAdapter recentKeywordAdapter;

    protected RelatedKeywordListAdapter relatedKeywordListAdapter;

    @InjectView(R.id.search_border_layout)
    protected View search_border_layout;

    @InjectView(R.id.search_layout)
    protected RelativeLayout search_layout;

    @InjectView(R.id.line_popular)
    protected View line_popular;

    @InjectView(R.id.line_recent)
    protected View line_recent;

    private ViewGroup categoryView;

    private List<RecentKeyword> recentListFooter;

    private PromotionSearch currentPromotion;

    /**
     * 검색페이지 이동전 자산의 A/B 상태를 유지한다
     * 검색 엑티비티가 살아 있는 동안 유효 하다
     * df = 기본값
     * 서버에서
     */
    private String searchABType = "df";

    //탭메뉴 구분
    private enum TabMenu {
        POPULAR, RECENT, RELATED
    }

    //현재 선택된 탭메뉴 상태 저장
    private TabMenu currentTabMenu;

    private boolean isKeyboardHideFinish = true;

    //최근검색어 기반으로한 연관검색어 리스트
    private ArrayList<RecentRecommandInfo> recentRecommandList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //페이드
        getWindow().getAttributes().windowAnimations = R.style.FadeInAnimation;
        //롤리팝 이상 기기에서 장면전환 에니메이션을 사용하기 위해 추가
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//	        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
//	        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
//	        setupWindowAnimations();
//        } else {
//
//        }

        setContentView(getContentLayout());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        		search_border_layout.setTransitionName(getString(R.string.searchBox));
        	 findViewById(R.id.btn_search).setTransitionName(getString(R.string.searchImage));
        }

        RunningActivityManager.addActivity(this);

        getSearchAB();//AB 상태를 가져옴
        setupKeywordEdit();
        setupPopularKeywordList();
        setupRecentKeywordList();
        setupRelatedKeywordList();
        setupClick();


        // 검색창 비어있는 상태에서 터치하면 최근 검색어가 보이게
        //해당 시나리오 삭제
//        editKeyword.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                if (editKeyword.getText().toString().isEmpty()) {
//                    showRecentKeywords();
//                    editKeyword.setHint("");
////                    editKeyword.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//                }
//            }
//
//        });


        layout_search_bar.setVisibility(View.VISIBLE);
        mLlTooltipRecommendGuide.setVisibility(View.GONE);

        //GTM 클릭이벤트 전달
        String service = getIntent().getBooleanExtra(Keys.INTENT.FROM_WEB, false) ?
        		GTMEnum.GTM_ACTION_SERVICE_WEB : GTMEnum.GTM_ACTION_SERVICE_APP;
		String action = String.format("%s_%s_%s", GTMEnum.GTM_ACTION_HEADER,
				GTMEnum.GTM_SEARCH_ACTION.SEARCH, service);
        GTMAction.sendEvent(context, GTMEnum.GTM_AREA_CATEGORY,
        		action,
        		GTMEnum.GTM_LABEL_CLICK);

		//GTM Ecommerce에서 사용할 스크린 이름 설정
		GTMAction.setScreenName(GTMEnum.GTM_TABMENU_LABEL.Search.getLabel());

        //앰플리듀드 메인 검색창 뷰 이벤트
        try{
            AMPAction.sendAmpEvent(AMPEnum.AMP_VIEW_MAIN_SEARCH);
        }catch (Exception e)
        {
            Ln.e(e);
        }

        //키패드 내려갈때 보이는 검은색화면을 흰색으로
        search_layout.getRootView().setBackgroundColor(Color.parseColor("#ffffff"));

        // HomeSearchActivity 들어올 때 search 초기화.
        CookieUtils.resetSearchCookie(this, "");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager.getInstance().sync();
        }

        // 최근 겁색어가 없다면 디폴트를 인기 검색어로
        if( !isRecentKeywords() )
        {
            setupDefaultView();
        }else{
            //최근 검색어가 있다면 보여주기
            showRecentKeywords();
        }


        //인기검색어, 최근검색어 리스트 스크롤시에 덜컥거리지 않고 키패드 부드럽게 내려가게 하기위해 아래내용 추가
        if(TabMenu.POPULAR.equals(currentTabMenu) ){
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); //키패드가 덮는형태
        } else if(TabMenu.RECENT.equals(currentTabMenu) ){
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); //키패드가 밀어올리는 형태
        }


        //리스트부분, 리스트밑에 여백부분, 결과없을때 부분 --> 터치시 키패드 내려가달라는 요청(20.04.23 배포)
        hideKeyBoardView(layoutRecentNoData,layoutRecentKeyword,listRecentKeyword,layoutRelatedNoData, listRelatedKeyword, layoutRelatedKeyword,listPopularKeyword, layoutPopularKeyword);


        //항상 키패드 자동으로 올라오도록 설정
        editKeyword.setSelection(0);
        editKeyword.postDelayed(() -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editKeyword, InputMethodManager.SHOW_IMPLICIT);
        }, 100);
        editKeyword.requestFocus();

        //최근검색어 기반으로한 연관검색어 리스트
        if(getIntent() != null){
            recentRecommandList =(ArrayList<RecentRecommandInfo>)getIntent().getSerializableExtra(Keys.INTENT.KEYWORD_LIST);
            setUpLayoutRecommandList();
        }
    }

    //리스트부분, 리스트밑에 여백부분, 결과없을때 부분 --> 터치시 키패드 내려가달라는 요청(20.04.23 배포)
    public void hideKeyBoardView(View... views){
            View[] viewarr = views;
            int viewCount = views.length;

            for(int i = 0; i < viewCount; ++i) {
                View v = viewarr[i];
                v.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        ViewUtils.hideSoftInput(editKeyword);
                        return false;
                    }
                });
            }
    }

    /**
     * 롤리팝 이상 기기에서 장면전환 에니메이션을 사용하기 위해 추가
     */
    private void setupWindowAnimations() {
		getWindow().setSharedElementExitTransition(new ChangeBounds().setDuration(200));
		getWindow().setSharedElementReenterTransition(new ChangeBounds().setDuration(200));
		getWindow().setSharedElementEnterTransition(new ChangeBounds().setDuration(200));
        getWindow().setSharedElementReturnTransition(new ChangeBounds().setDuration(200));

        getWindow().setEnterTransition(new ChangeBounds().setDuration(200));
		getWindow().setExitTransition(new ChangeBounds().setDuration(200));
	}

    /**
     *
     */
    private void getSearchAB()
    {
        // 이미 받은것이 있다? 설마?
        // 호출 횟수를 줄여 볼까? : 검색 엑티비티가 살아 있는 동안 유효하다
            // 혹시라도 서머에서 A/B 비율또는 상태를 바뀔수도 있으니
        new GetSearchABController(HomeSearchActivity.this).execute();
    }

    /**
     * 롤리팝 이상 기기에서 장면전환 에니메이션을 사용하기 위해 추가
     */
    @Override
    public void onBackPressed() {
    	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    		finishAfterTransition();
    	}else{
    		super.onBackPressed();
    	}
    }

    protected int getContentLayout() {
        return R.layout.search;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


        // Checks whether a hardware keyboard is available
        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
            Toast.makeText(this, "keyboard visible", Toast.LENGTH_SHORT).show();
        } else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
            Toast.makeText(this, "keyboard hidden", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 최초 로딩시 시작하는 검색종류 세팅 (현재는 인기검색어)
     */
    protected void setupDefaultView() {
        currentTabMenu = TabMenu.POPULAR;
        showPopularKeywords();
    }

    /**
     * 추천검색어가 있다면 보여주기
     */
    protected void setUpLayoutRecommandList(){
        if(recentRecommandList != null && recentRecommandList.size() > 0){
            RecommandListAdapter recommandListAdpater = new RecommandListAdapter(recentRecommandList, new OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (view.getId()) {
                        case R.id.lay_recommand_keyword:
                            searchNavigation.search(HomeSearchActivity.this, view.getTag().toString(), null,ServerUrls.WEB.SEARCH_RECOMMAND,ServerUrls.WEB.SEARCH_ABPARAM + searchABType, GTMEnum.GTM_SEARCH_ACTION.SEARCH_RECOMMAND);
                            finish();
                            EventBus.getDefault().post(new Events.NavigationCloseEvent());
                            break;
                        default:
                            break;
                    }

                }
            });

            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false );
            recycler_recommend_list.setLayoutManager(layoutManager);
            recycler_recommend_list.setAdapter(recommandListAdpater);
            mRlRecommendListArea.setVisibility(View.VISIBLE);
            mBtnRecommendGuide.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (DisplayUtils.isVisible(mLlTooltipRecommendGuide)) {
                        mLlTooltipRecommendGuide.setVisibility(View.GONE);
                    } else {
                        mLlTooltipRecommendGuide.setVisibility(View.VISIBLE);
                    }
                }
            });

            mBtnGuideClose.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLlTooltipRecommendGuide.setVisibility(View.GONE);
                }
            });
        }else{
            mRlRecommendListArea.setVisibility(View.GONE);
        }
    }


    /**
     * 인기검색어 어댑터 및 탭메뉴 이벤트를 등록한다.
     */
    protected void setupPopularKeywordList() {
        popularKeywordListAdapter = new PopularKeywordListAdapter(this, new OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = (String) v.getTag();

                // ex ) ServerUrls.WEB.SEARCH_ABPARAM + searchABType = ( &ab= + "df" )
                searchNavigation.search(HomeSearchActivity.this, keyword,
                        RecentKeyword.InputType.DIRECT,ServerUrls.WEB.SEARCH_POPULAR, ServerUrls.WEB.SEARCH_ABPARAM + searchABType, GTMEnum.GTM_SEARCH_ACTION.SEARCH_POPULAR);
                finish();
                EventBus.getDefault().post(new Events.NavigationCloseEvent());
            }
        });

        //        listPopularKeyword.addFooterView(layoutCommonFooter);
        listPopularKeyword.setAdapter(popularKeywordListAdapter);
        //인기검색어 탭 클릭이벤트 등록
        btnPopularKeyword.setOnClickListener(this);
        //인기검색어 스크롤이벤트 등록
        listPopularKeyword.setOnScrollListener(this);
    }

    /**
     * 최근검색어 어댑터 및 탭메뉴 이벤트를 등록한다.
     */
    protected void setupRecentKeywordList() {
        recentKeywordAdapter = new RecentKeywordListAdapter(this, new OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = ((RecentKeyword) v.getTag()).keyword;
                RecentKeyword.InputType type = ((RecentKeyword) v.getTag()).type;

                switch (v.getId()) {
                //리스트뷰에서 키워드 클릭시
                case R.id.txt_keyword_left:
                case R.id.txt_keyword_right:
                    searchNavigation.search(HomeSearchActivity.this, keyword, null,ServerUrls.WEB.SEARCH_RECENT,ServerUrls.WEB.SEARCH_ABPARAM + searchABType, GTMEnum.GTM_SEARCH_ACTION.SEARCH_RECENT);
                    finish();
                    EventBus.getDefault().post(new Events.NavigationCloseEvent());
                    break;
                //리스트뷰에서 x(삭제)버튼 클릭시
                case R.id.btn_del_left:
                case R.id.btn_del_right:
                	RecentKeyword k = new RecentKeyword(keyword, type);
                    deleteRecentKeywords(k);
                    break;
                default:
                    break;
                }
            }
        });

        // 검색기록 전체삭제.
        btnClearRecentKeyword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 하단공통영역 전체삭제 버튼
                sendBasicPopupEvent();
            }
        });

        listRecentKeyword.setAdapter(recentKeywordAdapter);
        //최근검색어 탭 클릭이벤트 등록
        btnRecentKeyword.setOnClickListener(this);
        //최근검색어 스크롤이벤트 등록
        listRecentKeyword.setOnScrollListener(this);
    }

    /**
     * 모든 최근 검색어 삭제 팝업
     */
    private void sendBasicPopupEvent() {
        Events.BasicPopupEvent event = new Events.BasicPopupEvent(
                getString(R.string.search_recent_keyword_all_delete), null,
                getString(R.string.common_cancel),
                getString(R.string.common_ok),
                0, 2);

        Intent intent = new Intent(HomeSearchActivity.this, PopupBasicActivity.class);
        intent.putExtra(Keys.INTENT.INTENT_POPUP_BASIC, event);
        startActivityForResult(intent, Keys.REQCODE.SHOW_SEARCH_DELETE_POPUP);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case Keys.REQCODE.SHOW_SEARCH_DELETE_POPUP:
                if (resultCode == RESULT_OK) {
                    deleteAllRecentKeywords();
                }
                break;
        }
    }

    /**
     * 연관검색어 어댑터 및 탭메뉴 이벤트를 등록한다.
     */
    protected void setupRelatedKeywordList() {
    	// jelly bean에서 오류 발생.
		View footer = getLayoutInflater().inflate(R.layout.home_row_type_search, null);
		categoryView = (ViewGroup) footer.findViewById(R.id.view_related_keyword_footer);
		listRelatedKeyword.addHeaderView(categoryView);

        relatedKeywordListAdapter = new RelatedKeywordListAdapter(this, new OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = (String) v.getTag();
                searchNavigation.search(HomeSearchActivity.this, keyword,
                        RecentKeyword.InputType.DIRECT, ServerUrls.WEB.SEARCH_AUTO,ServerUrls.WEB.SEARCH_ABPARAM + searchABType, GTMEnum.GTM_SEARCH_ACTION.SEARCH_AUTO);
                finish();
                EventBus.getDefault().post(new Events.NavigationCloseEvent());
            }
        });
        listRelatedKeyword.setAdapter(relatedKeywordListAdapter);
    }

    /**
     * 검색어 입력박스에 대한 이벤트를 정의한다.
     */
    protected void setupKeywordEdit() {
        editKeyword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // 키패드에서 검색키(돋보기모양)를 누른 경우 검색 처리
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchClicked();
                    return true;
                }
                return false;
            }
        });

        //TODO 포커스 변경시 아래 onFocusChange 콜백함수 호출안됨. 의도적으로 작업한 것인지 버그인지 확인 필요
        editKeyword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // 검색어 필드 선택시
                if (hasFocus) {
                    ViewUtils.showViews(layoutRecentKeyword);
                }

                // 검색어 필드 선택된 상태이고 검색어가 없는 경우
                if (hasFocus && editKeyword.getText().length() == 0) {
                    ViewUtils.showViews(layoutRecentKeyword);
                    ViewUtils.hideViews(layoutRelatedKeyword);
                    showRecentKeywords();
                }
            }

        });

        //텍스트가 추가되거나 삭제될때 동작된다.
        editKeyword.addTextChangedListener(new ClearButtonTextWatcher(btnKeywordDelete) {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);

                // 입력값이 있으면 자동완성 검색어 조회
                if (s.length() > 0) {
                    editKeyword.setTypeface(null, Typeface.BOLD);
                    showRelatedKeywords(s.toString());
                } else { // 입력값이 없으면 인기 검색어 조회
                    editKeyword.setTypeface(Typeface.DEFAULT);
                    if (currentTabMenu.equals(TabMenu.POPULAR)) {
                        showPopularKeywords();
                    } else if (currentTabMenu.equals(TabMenu.RECENT)) {
                    	//최근검색어가 없는 경우에는 인기검색탭으로 이동한다.
                    	if(recentListFooter == null || recentListFooter.isEmpty()){
                    		showPopularKeywords();
                    	}else{
                    		 showRecentKeywords();
                    	}

                    }

                }
            }
        });

        //소프트키보드 동작상태를 알기위해 추가
        final SoftKeyboardDectectorView softKeyboardDecector = new SoftKeyboardDectectorView(this);
        addContentView(softKeyboardDecector, new FrameLayout.LayoutParams(-1, -1));

        //키보드가 화면에 올라왔을때
        softKeyboardDecector.setOnShownKeyboard(new OnShownKeyboardListener() {

            @Override
            public void onShowSoftKeyboard() {
            }
        });
    }

    protected void searchLayoutInvalidate() {
        ThreadUtils.INSTANCE.runInUiThread(() -> {
            if (currentTabMenu == TabMenu.RECENT) {
                showRecentKeywords();
            } else if (currentTabMenu == TabMenu.POPULAR) {
                showPopularKeywords();
            }
        }, RELATED_KEYWORD_DELAY);
    }

    /**
     * 최근검색어를 한건씩 삭제한다.
     *
     * @param keyword 삭제할 키워드 정보
     */
    protected void deleteRecentKeywords(final RecentKeyword keyword) {
        searchAction.deleteRecentKeyword(this, keyword);
        searchAction.deleteRecentKeywordAddDate(this, keyword);
        showRecentKeywords();
    }

    /**
     * 최근검색어를 모두 삭제한다.
     */
    protected void deleteAllRecentKeywords() {

    	isKeyboardHideFinish = false;

    	isKeyboardHideFinish = true;
        searchAction.deleteAllRecentKeyword(HomeSearchActivity.this);
        searchAction.deleteAllRecentKeywordAddDate(HomeSearchActivity.this);
        recentKeywordAdapter.setItems(null);
        recentKeywordAdapter.notifyDataSetChanged();
        //최근 검색기록이 없다는 멘트 표시
        ViewUtils.showViews(layoutRecentNoData);
        //전체삭제 영역 비노출
    	ViewUtils.hideViews(layoutClearRecentKeyword);
    }


    /**
     * 검색아이콘(돋보기) 클릭시 검색을 수행한다.
     */
    protected void searchClicked() {
        String keyword = editKeyword.getText().toString().trim();

        try{
            //sm21에서만 동작 하도록 수정, openDate 전용 빌드일때는 N로 설정함
            String openDateMenuFlag = MainApplication.getAppContext().getString(R.string.openDateMenuFlag);
            // 항상 체크 할까? 아니면 운영에서도?
            if ("Y".equals(openDateMenuFlag) && !TextUtils.isEmpty(keyword))
            {
                //openDate= 
                if(ServerUrls.REST.OPEN_DATE_FORMAT.equals(keyword))
                {
                    //초기화
                    PrefRepositoryNamed.save(MainApplication.getAppContext(), Keys.CACHE.HOME_NAVI_OPEN_DATE,"");
                    Toast.makeText(getApplicationContext(), "OPENDATE_RESET", Toast.LENGTH_LONG).show();
                }
                //brdTime= 일때 초기화
                else if(ServerUrls.REST.BRD_TIME_FORMAT.equals(keyword))
                {
                    //초기화
                    PrefRepositoryNamed.save(MainApplication.getAppContext(), Keys.CACHE.HOME_TAB_BRD_TIME,"");
                    Toast.makeText(getApplicationContext(), "BRDTIME_RESET", Toast.LENGTH_LONG).show();
                }
                //openDate= 이건 위에서 걸러졌을거고 openDate=dsfads 버라도 있으면 공백까지 날짜를 저장
                else if(keyword.contains(ServerUrls.REST.OPEN_DATE_FORMAT))
                {
                    //날짜는 2018 01 01 11 22 33 44 까지 이다.
                    PrefRepositoryNamed.save(MainApplication.getAppContext(), Keys.CACHE.HOME_NAVI_OPEN_DATE,keyword);
                    Toast.makeText(getApplicationContext(), keyword, Toast.LENGTH_LONG).show();
                }
                //brdTime= 이건 위에서 걸러졌을거고 openDate=dsfads 버라도 있으면 공백까지 날짜를 저장
                else if(keyword.contains(ServerUrls.REST.BRD_TIME_FORMAT))
                {
                    //데이트 2018 01 01 11 22 33 44 까지 이다.
                    PrefRepositoryNamed.save(MainApplication.getAppContext(), Keys.CACHE.HOME_TAB_BRD_TIME,keyword);
                    Toast.makeText(getApplicationContext(), keyword, Toast.LENGTH_LONG).show();
                }
            }
        }catch (Exception e)
        {

        }


        if (TextUtils.isEmpty(keyword)) {

        	if(currentPromotion != null && currentPromotion.promotionKeyword != null &&
        			editKeyword.getHint().equals(currentPromotion.promotionKeyword)){
        		WebUtils.goWeb(context, currentPromotion.promotionUrl + ServerUrls.WEB.SEARCH_PROMOTION );
                EventBus.getDefault().post(new Events.NavigationCloseEvent());

                //GTM 클릭이벤트 전달
                GTMAction.sendEvent(this, GTMEnum.GTM_AREA_CATEGORY, GTMEnum.GTM_SEARCH_ACTION.SEARCH_PROMOTION.toString(),
                		currentPromotion.promotionKeyword);
                finish();
                return;
        	}

        	isKeyboardHideFinish = false;

            Dialog dialog = new CustomOneButtonDialog(this)
            .message(R.string.search_description_search_main).buttonClick(new ButtonClickListener() {
				@Override
				public void onClick(Dialog dialog) {
					dialog.dismiss();
                    isKeyboardHideFinish = true;
				}
			});
            dialog.show();

            return;
        }

        if(currentPromotion != null && currentPromotion.promotionKeyword != null
        		&& keyword.startsWith(currentPromotion.promotionKeyword)){
        	WebUtils.goWeb(context, currentPromotion.promotionUrl + ServerUrls.WEB.SEARCH_PROMOTION);
            EventBus.getDefault().post(new Events.NavigationCloseEvent());

        	//GTM 클릭이벤트 전달
        	GTMAction.sendEvent(this, GTMEnum.GTM_AREA_CATEGORY, GTMEnum.GTM_SEARCH_ACTION.SEARCH_PROMOTION.toString(), keyword);
        	finish();
    		return;
        }


        // 자동완성 검색어 조회 작업 취소
        if (runningRelatedKeywordsController != null) {
            runningRelatedKeywordsController.cancel();
            runningRelatedKeywordsController = null;
        }

        //검색키워드 초기화
        editKeyword.setText("");

        searchNavigation.search(this, keyword, RecentKeyword.InputType.DIRECT,
        		ServerUrls.WEB.SEARCH_DIRECT,ServerUrls.WEB.SEARCH_ABPARAM + searchABType, GTMEnum.GTM_SEARCH_ACTION.SEARCH_DIRECT);
        EventBus.getDefault().post(new Events.NavigationCloseEvent());
    }

    private void btnClickedImgSearch() {
        // 카메라는 실행 되나, 실행 후 activityResult로 오지않고 닫힘.
        setWiseLogHttpClient(ServerUrls.WEB.IMAGE_SEARCH_TAKE_PHOTO);
        setResult(RESULT_OK);
        finish();
    }

    /**
     * 연관 검색어 조회해서 보여줌
     *
     * @param keyword
     */
    protected void showRelatedKeywords(String keyword) {
        ThreadUtils.INSTANCE.runInUiThread(() -> {
            viewDisplayControl(TabMenu.RELATED);
            String key;
            //연관검색어는 서버에서 UTF-8로 디코딩하기 때문에 UTF-8 인코딩 로직을 추가한다.
            try {
                key = URLEncoder.encode(keyword, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                // 10/19 품질팀 요청
                // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
                // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
                // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
                Ln.e(e);
                return;
            }
            new GetRelatedKeywordsController(HomeSearchActivity.this).execute(key);
        }, RELATED_KEYWORD_DELAY);
    }

    /**
     * 인기 검색어 기록 보여줌
     *
     */
    protected void showPopularKeywords() {
        ThreadUtils.INSTANCE.runInUiThread(() -> {
            currentTabMenu = TabMenu.POPULAR;

            viewDisplayControl(TabMenu.POPULAR);

            btnPopularKeyword.setSelected(true);
            btnPopularKeyword.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
            btnRecentKeyword.setSelected(false);
            btnRecentKeyword.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);

            search_layout.invalidate();
            search_layout.requestLayout();

            new GetPopularKeywordsController(HomeSearchActivity.this).execute();
        }, RELATED_KEYWORD_DELAY);
    }

    /**
     * 진입시 최근 검색어가 없는 경우에 인기 검색어 탭으로 이동하도록
     */
    protected boolean isRecentKeywords() {
        // IllegalArgumentException 시 문제있는 문자열로 판단, false로 던져 버린다.
       try {
           recentListFooter = searchAction.getRecentDateKeywords(this);
       }
       catch (IllegalArgumentException e) {
           Ln.e(e.getMessage());
           return false;
       }

       if (recentListFooter == null || recentListFooter.isEmpty()) {
            return false;
        }
        return true;
    }
    /**
     * 최근 검색어 기록 보여줌
     */
    protected void showRecentKeywords() {
        currentTabMenu = TabMenu.RECENT;

        viewDisplayControl(TabMenu.RECENT);

        btnPopularKeyword.setSelected(false);
        btnPopularKeyword.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
        btnRecentKeyword.setSelected(true);
        btnRecentKeyword.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);

        recentListFooter = searchAction.getRecentDateKeywords(this);

        if (recentListFooter == null || recentListFooter.isEmpty()) {
            ViewUtils.showViews(layoutRecentNoData);
            ViewUtils.hideViews(layoutClearRecentKeyword);
        }
        else
        {
            ViewUtils.showViews(layoutClearRecentKeyword);
        }


        recentKeywordAdapter.setItems(recentListFooter);
        recentKeywordAdapter.notifyDataSetChanged();

        search_layout.invalidate();
        search_layout.requestLayout();
    }

    /**
     * 검색방법 종류별로 뷰를 노출/숨김 처리한다.
     *
     * @param tabMenu 현재 활성화된 검색방법(인기검색 or 최근검색 or 연관검색)
     */
    private void viewDisplayControl(TabMenu tabMenu) {
        ViewUtils.hideViews(layoutRecentNoData);

        switch (tabMenu) {
        case POPULAR:
            ViewUtils.hideViews(layoutRecentKeyword);
            ViewUtils.hideViews(layoutRelatedKeyword);
            ViewUtils.hideViews(layoutRelatedNoData);
            ViewUtils.showViews(layoutTabMenu);
            ViewUtils.showViews(layoutPopularKeyword);

            ViewUtils.hideViews(line_recent);
            ViewUtils.showViews(line_popular);

            layoutTabMenu.invalidate();
            layoutPopularKeyword.invalidate();
            break;

        case RECENT:
            ViewUtils.hideViews(layoutPopularKeyword);
            ViewUtils.hideViews(layoutRelatedKeyword);
            ViewUtils.hideViews(layoutRelatedNoData);
            ViewUtils.showViews(layoutTabMenu);
            ViewUtils.showViews(layoutRecentKeyword);

            ViewUtils.hideViews(line_popular);
            ViewUtils.showViews(line_recent);

            layoutRecentKeyword.invalidate();
            layoutTabMenu.invalidate();
            break;

        case RELATED:
            ViewUtils.hideViews(layoutTabMenu);
            ViewUtils.hideViews(layoutPopularKeyword);
            ViewUtils.hideViews(layoutRecentKeyword);
            ViewUtils.showViews(layoutRelatedKeyword);
            break;

        default:
            break;
        }
    }

    private class GetPromotionSearchController extends BaseAsyncController<PromotionSearchList> {
		@Inject
		private final TopSectionList tempSection = null;
		private final boolean dialogFlag = false;
		private final boolean isCacheData = false;
		private Context context = null;

		@Inject
	    private RestClient restClient;

		protected GetPromotionSearchController(Context activityContext) {
			super(activityContext);
			context = activityContext;
		}

		@Override
		protected void onPrepare(Object... params) throws Exception {
			if (dialogFlag) {
				if (dialog != null) {
					dialog.dismiss();
					dialog.setCancelable(true);
					dialog.show();
				}
			}
		}

		@Override
		protected PromotionSearchList process() throws Exception {

			return (PromotionSearchList)DataUtil.getData(context, restClient, PromotionSearchList.class, isCacheData,
					true, ServerUrls.getHttpRoot() + "/apis/v2.6/search/promotionKeyword");
		}

		@Override
		protected void onSuccess(PromotionSearchList listInfo) throws Exception {

			if(listInfo != null && listInfo.promotionKeywordList != null && listInfo.promotionKeywordList.size() != 0){
				int randomInteger = new Random().nextInt(listInfo.promotionKeywordList.size());

				currentPromotion = listInfo.promotionKeywordList.get(randomInteger);
				editKeyword.setHint(currentPromotion.promotionKeyword);
			}else{
				editKeyword.setHint(R.string.search_description_search_main);
			}

		}

		@Override
		protected void onError(Throwable e) {

			super.onError(e);
		}
	}


    /**
     * 현재 처리중인 GetRelatedKeywordsController.
     * 새로운 GetRelatedKeywordsController 실행시 이전 것을 취소하기 위한 용도.
     */
    protected GetRelatedKeywordsController runningRelatedKeywordsController;

    /**
     * 연관 검색어 목록 조회 콘트롤러.
     *
     * TODO {@link AsyncTaskLoader} 이용 검토.
     */
    protected class GetRelatedKeywordsController extends
            AbstractAsyncController<RelatedKeywordList> {
        private String keyword;

        protected GetRelatedKeywordsController(Context activityContext) {
            super(activityContext);
        }

        @Override
        protected void onPrepare(Object... params) throws Exception {
            super.onPrepare(params);

            // 이전 작업을 계속 진행하는게 의미가 없으므로 취소시켜버림
            if (runningRelatedKeywordsController != null) {
                runningRelatedKeywordsController.cancel();
            }
            runningRelatedKeywordsController = this;

            this.keyword = (String) params[0];
        }

        @Override
        protected RelatedKeywordList process() throws Exception {
            return searchAction.getRelatedKeywords(keyword);
        }

        @Override
        protected void onSuccess(RelatedKeywordList t) throws Exception {

        	if(t.frontSize == 0 && t.backSize ==0 && t.categorySize == 0){
        		relatedKeywordListAdapter.setItems(null);
            	ViewUtils.showViews(layoutRelatedNoData);
        		return;
        	}
        	ViewUtils.hideViews(layoutRelatedNoData);
            if (editKeyword.getText().length() > 0) {
                relatedKeywordListAdapter.setItems(t);

                updateCategoryView(t);

            } else {
                // 서버에서 응답에 왔는데 사용자가 이미 검색어를 지운 경우.
            	relatedKeywordListAdapter.setItems(null);
            }
        }

        @Override
        protected void onError(Throwable e) {
            relatedKeywordListAdapter.setItems(null);
        }

        @Override
        protected void onFinally() {
            relatedKeywordListAdapter.notifyDataSetChanged();
            runningRelatedKeywordsController = null;
            super.onFinally();
        }
    }


    private void updateCategoryView(RelatedKeywordList t){
    	categoryView.removeAllViews();
        vwRelatedKeywordTopLine.setVisibility(View.GONE);

    	int i=0;
    	for(final CategoryKeywordList category: t.category){
    	    //연관검색결과 중 카테고리가 있는 경우만 상단라인 노출
            vwRelatedKeywordTopLine.setVisibility(View.VISIBLE);
    		LinearLayout categoryItem = (LinearLayout)getLayoutInflater().inflate(
    				R.layout.home_row_type_search_item, null);
    		// 간격 조절.
    		i++;

            TextView search_name = (TextView)categoryItem.findViewById(R.id.search_name);
    		TextView category_name = (TextView)categoryItem.findViewById(R.id.category_name);
    		TextView category_arrow = (TextView)categoryItem.findViewById(R.id.category_arrow);
    		TextView category_sub = (TextView)categoryItem.findViewById(R.id.category_sub);

            search_name.setText(t.query);

    		//텍스드를 > 를 기준으로 쪼개서 셋팅한다.
    		//마지막꺼만 볼드 처리를 위한 신규 규경 필요
    		if(category.title != null && !"".equals(category.title) && category.title.indexOf(">") != -1){

    			int pos = category.title.lastIndexOf(">");
    			if(pos > 0){
	    			String subCategory = category.title.substring(pos+1);
	    			String title =  category.title.substring(0, pos);//category.title.replace(subCategory, "");

					category_name.setText(title);
					category_arrow.setText(">");
		    		category_sub.setText(subCategory);
    			}
    		}else{
    			category_name.setText(category.title);
    		}

            //Ln.i("category_name : " + category_name.getText());

    		categoryItem.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					WebUtils.goWeb(context, category.url );
                    EventBus.getDefault().post(new Events.NavigationCloseEvent());
				}
			});

    		categoryView.addView(categoryItem);
    	}

    }

    /**
     * 현재 처리중인 GetPopularKeywordsController.
     * 새로운 GetPopularKeywordsController 실행시 이전 것을 취소하기 위한 용도.
     */
    protected GetPopularKeywordsController runningPopularKeywordsController;

    /**
     * 인기 검색어 목록 조회 콘트롤러.
     *
     * TODO {@link AsyncTaskLoader} 이용 검토.
     */
    protected class GetPopularKeywordsController extends
            AbstractAsyncController<PopularKeywordList> {

        protected GetPopularKeywordsController(Context activityContext) {
            super(activityContext);
        }

        @Override
        protected void onPrepare(Object... params) throws Exception {
            super.onPrepare(params);

            // 이전 작업을 계속 진행하는게 의미가 없으므로 취소시켜버림
            if (runningPopularKeywordsController != null) {
                runningPopularKeywordsController.cancel ();
            }
            runningPopularKeywordsController = this;
        }

        @Override
        protected PopularKeywordList process() throws Exception {
            return searchAction.getPopularKeywords();
        }

        @Override
        protected void onSuccess(PopularKeywordList t) throws Exception {
            popularKeywordListAdapter.setItems(t);
        }

        @Override
        protected void onError(Throwable e) {
            popularKeywordListAdapter.setItems(null);
            Ln.e(e);
        }

        @Override
        protected void onFinally() {
            popularKeywordListAdapter.notifyDataSetChanged();
            runningPopularKeywordsController = null;
            super.onFinally();
        }
    }

    /**
     * 검색 A/B 결과 컨트롤러
     *
     * TODO {@link AsyncTaskLoader} 이용 검토.
     */
    protected class GetSearchABController extends AbstractAsyncController<String> {

        protected GetSearchABController(Context activityContext) {
            super(activityContext);
        }

        @Override
        protected void onPrepare(Object... params) throws Exception {
            super.onPrepare(params);
        }

        @Override
        protected String process() throws Exception {
            return searchAction.getSearchAB();
        }

        @Override
        protected void onSuccess(String t) throws Exception {
            searchABType = t;
        }

        @Override
        protected void onError(Throwable e) {
            Ln.e(e);
        }

        @Override
        protected void onFinally() {
            super.onFinally();
        }
    }

    @Override
    protected void onResume() {
        if (currentTabMenu.equals(TabMenu.RECENT)) {
            showRecentKeywords();
        }

        //프로모션 조회 안함
        //new GetPromotionSearchController(this).execute();

        super.onResume();
    }

    @Override
    protected void onDestroy() {
        ViewUtils.hideSoftInput(editKeyword);

        // 인기 검색어 조회 작업 취소
        if (runningPopularKeywordsController != null) {
            runningPopularKeywordsController.cancel();
            runningPopularKeywordsController = null;
        }

        // 자동완성 검색어 조회 작업 취소
        if (runningRelatedKeywordsController != null) {
            runningRelatedKeywordsController.cancel();
            runningRelatedKeywordsController = null;
        }

        super.onDestroy();
    }

    /**
     * 리소스별 클릭이벤트 등록
     */
    protected void setupClick() {
        findViewById(R.id.btn_search).setOnClickListener(this);
        findViewById(R.id.lay_delete_keyword).setOnClickListener(this);
        findViewById(R.id.close_text).setOnClickListener(this);
        search_layout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search: //화면우상단 돋보기 버튼
                searchClicked();
                break;
            case R.id.lay_delete_keyword: //검색어 입력박스 x 이미지
                editKeyword.getText().clear();
                break;
            case R.id.btn_popular_keyword: //인기검색어 탭메뉴
                this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                showPopularKeywords();
                break;
            case R.id.btn_recent_keyword: //최근검색어 탭메뉴
                this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                showRecentKeywords();
                break;
            case R.id.close_text: //최근검색어 탭메뉴
                onBackPressed();
                break;
            case R.id.search_layout: //하단 반투명 빈공간
               // finish();
                break;
        default:
            break;
        }
    }

	@Override
	public void onScrollStateChanged(AbsListView v, int scrollState) {
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
			int totalItemCount) {
	}
}
