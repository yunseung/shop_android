package gsshop.mobile.v2.menu.navigation;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import androidx.drawerlayout.widget.DrawerLayout;

import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.ui.util.ViewUtils;
import com.transitionseverywhere.ChangeBounds;
import com.transitionseverywhere.TransitionManager;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.support.wiselog.WiseLogAction;
import roboguice.util.Ln;

import static android.view.View.inflate;

public class NavigationManager implements DrawerLayout.DrawerListener {

    /**
     * 전체 카테고리
     */
    private CategoryGroupLayout viewNavigationCategory;

    /**
     * GS X 브랜드 카테고리
     */
    private GSXBrandLayout viewGSXCategory;

    /**
     * 파트너스 카테고리
     */
    private PartnerLayout viewPartnerCategory;

    /**
     * 테마관 카테고리
     */
    private ThemeLayout viewThemeCategory;

    /**
     * 네비게이션 캐시 데이터
     */
    private static NavigationResult cacheResult;

    /**
     * 네비게이션 컨테이너
     */
    private ViewGroup scene_container;

    /**
     * 네비게이션 컨테이너
     */
    private ScrollView scene_root;

    private WiseLogAction wiseLog;

    /**
     * 찜 레이아웃
     */
    private View category_bottom_margin;

    /**
     * 관심카테고리 > 설정 클릭시 호출할 와이즈로그 URL 저장
     */
    private String iCateWiseLogUrl = "";

    private DrawerLayoutHorizontalSupport drawerLayout;

    private Context mContext;

    // 네비게이션 상태 북구를 위한 Position
    private static int currentScrollPosition;

    // 네비게이션 레이아웃 마다 TAG를 달고 복구 위한 변수
    public static String currentCategoryName;

    public static boolean isNavigationLogin = false;

    public static boolean isNewHomeGroup = false;

    /**
     * 네비게이션 매니저 생성
     */
    public NavigationManager(ScrollView scene_root, ViewGroup scene_container, DrawerLayoutHorizontalSupport drawerLayout, RestClient restClient){
        this.scene_root = scene_root;
        this.scene_container = scene_container;
        this.drawerLayout = drawerLayout;
        drawerLayout.addDrawerListener(this);
        wiseLog = new WiseLogAction(restClient);
    }

    /**
     * 네비게이션 카테고리를 추가한다.
     * 네비게이션 개선, 카테고리 뷰 말고 다른 뷰들은 여러개 추가 될 수 있음에 addCategory 시에 addview
     */
    public void addCaterory(NavigationResult result, boolean isTransition){
        if (viewNavigationCategory != null && viewNavigationCategory.isEmptyView()) {
            if(isTransition) {
                TransitionManager.beginDelayedTransition(scene_container, new ChangeBounds());
            }
            viewNavigationCategory.addCaterogy(drawerLayout, scene_root, scene_container, result);
        }

        ViewUtils.hideViews(viewGSXCategory, viewPartnerCategory, viewThemeCategory);

        if (result != null && result.itemList != null) {
            for (CateContentList item : result.itemList) {
                try {
                    if ("BAN_SLD_IMG_GBA".equalsIgnoreCase(item.viewType)) {
                        if (item.subContentList.size() > 0) {
                            viewGSXCategory.setVisibility(View.VISIBLE);
                            viewGSXCategory.setView(item);
                        }
                    } else if ("BAN_IMG_PAGE_GBA".equalsIgnoreCase(item.viewType)) {
                        if (item.subContentList.size() > 0) {
                            viewPartnerCategory.setVisibility(View.VISIBLE);
                            viewPartnerCategory.setView(item);
                        }
                    } else if ("BAN_IMG_PAGE_GBB".equalsIgnoreCase(item.viewType)) {
                        if (item.subContentList.size() > 0) {
                            viewThemeCategory.setVisibility(View.VISIBLE);
                            viewThemeCategory.setView(item);
                        }
                    }
                }
                catch (NullPointerException e) {
                    Ln.e(e.getMessage());
                }
            }
        }
//        loadSavedPosition();
    }

    /**
     * 현재 스크롤된 위치를 저장한다(상태복구를 위해)
     */
    public static void setCurrentScrollPosition(int scrollPosition){
        currentScrollPosition = scrollPosition;
    }

    /**
     * 스크롤된 위치를 리턴한다.
     */
    public static int getCurrentScrollPosition(){
        return currentScrollPosition;
    }

    /**
     * 현재 GSX 리스트 포지션을 저장 / 로드
     */
    public void saveGSXPosition() {
        if (viewGSXCategory != null) {
            viewGSXCategory.savePosition();
        }
    }
    public void loadGSXPosition() {
        if (viewGSXCategory != null) {
            viewGSXCategory.setSavedPosition();
        }
    }

    /**
     * 파트너 영역 포지션을 저장 / 로드
     */
    public void savePartnersPosition() {
        if (viewPartnerCategory != null) {
            viewPartnerCategory.savePosition();
        }
    }
    public void loadPartnersPosition() {
        if (viewPartnerCategory != null) {
            viewPartnerCategory.setSavedPosition();
        }
    }

    /**
     * 테마관 영역 포지션을 저장 / 읽기
     */
    public void saveThemePosition() {
        if (viewThemeCategory != null) {
            viewThemeCategory.savePosition();
        }
    }
    public void loadThemePosition() {
        if (viewThemeCategory != null) {
            viewThemeCategory.setSavedPosition();
        }
    }

    /**
     * 카테고리들의 포지션을 초기화.
     */
    public void initCategoryPosition() {
        setCurrentScrollPosition(0);
        viewGSXCategory.setPosition(0);
        viewPartnerCategory.setPosition(0);
        viewThemeCategory.setPosition(0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (scene_root != null) {
                scene_root.setScrollY(0);
            }
        }
    }

    /**
     * 저장된 포지션으로 복귀
     */
    public void loadSavedPosition() {
        if (viewNavigationCategory != null) {
            viewNavigationCategory.openLastClickedItem();
        }

        loadGSXPosition();
        loadPartnersPosition();
        loadThemePosition();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            // delay를 주지않으면 각 아이템이 열리기 전에 스크롤이 일어 나기때문에 정확한 위치로 이동되지 않음.
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (scene_root != null) {
                        scene_root.setScrollY(currentScrollPosition);
                    }
                }
            }, 100);
        }
    }

    /**
     * 카테고리 레이아웃을 리턴한다.
     * @return
     */
    public CategoryGroupLayout getCategoryGroupLayout(){
        return viewNavigationCategory;
    }

    /**
     * 카테고리를 초기화한다.
     */
    public void clearNavigationCategory(){
        viewNavigationCategory.setEmptyView(true);
        initCategoryPosition();
    }

    /**
     * 네비게이션 뷰를 생성한다.
     * @param context
     * @param scene_container
     * @return
     */
    public boolean createNavigationView(Context context, ViewGroup scene_container){
        boolean isViewAdd = false;
        this.mContext = context;
        this.scene_container = scene_container;

        if(viewNavigationCategory == null){
            viewNavigationCategory = (CategoryGroupLayout)inflate(context, R.layout.view_navigation_category, null);
            initCategoryView();
            isViewAdd = true;
        }

        if (viewGSXCategory == null) {
            viewGSXCategory = (GSXBrandLayout) inflate(mContext, R.layout.view_navigation_gs_x_brand, null);
            viewGSXCategory.setVisibility(View.GONE);
            drawerLayout.addCheckTouchView(viewGSXCategory.getGSXBrandRecyclerView());
        }

        if (viewPartnerCategory == null) {
            viewPartnerCategory = (PartnerLayout) inflate(mContext, R.layout.view_navigation_partner, null);
            viewPartnerCategory.setVisibility(View.GONE);
            drawerLayout.addCheckTouchView(viewPartnerCategory.getPartnerPager());
        }

        if (viewThemeCategory == null) {
            viewThemeCategory = (ThemeLayout) inflate(mContext, R.layout.view_navigation_theme, null);
            viewThemeCategory.setVisibility(View.GONE);
            drawerLayout.addCheckTouchView(viewThemeCategory.getPager());
        }

        return isViewAdd;
    }

    public void initCategoryView(){
        category_bottom_margin = viewNavigationCategory.findViewById(R.id.category_bottom_margin);
    }

    /**
     * 캐시데이터 저장
     * @param result
     */
    public static void cacheData(NavigationResult result){
        cacheResult = result;
    }

    /**
     * 캐시데이터 리턴
     * @return
     */
    public static NavigationResult getCacheData(){
        return  cacheResult;
    }

    /**
     * 카테고리 뷰를 addView한다.
     */
    public void displayCategory(){
        category_bottom_margin.setVisibility(View.GONE);
        if(scene_container != null) {
            scene_container.removeAllViews();
            scene_container.addView(viewNavigationCategory);
            scene_container.addView(viewGSXCategory);
            scene_container.addView(viewThemeCategory);
            scene_container.addView(viewPartnerCategory);
        }
    }

    public String getiCateWiseLogUrl(){
        return iCateWiseLogUrl;
    }

    /**
     * 와이즈로그 전송 (restClient사용)
     *
     * @param url 호출주소
     */
    public void setWiseLog(String url) {
        wiseLog.setWiseLogHttpClient(url);
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {}
    @Override
    public void onDrawerOpened(View drawerView) {}
    @Override
    public void onDrawerClosed(View drawerView) {
        saveGSXPosition();
        savePartnersPosition();
        saveThemePosition();
        currentScrollPosition = scene_root.getScrollY();
    }
    @Override
    public void onDrawerStateChanged(int newState) {}
}
