package gsshop.mobile.v2.menu.navigation;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.aakira.expandablelayout.ExpandableLayoutListener;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.inject.Inject;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.pattern.mvc.BaseAsyncController;
import com.gsshop.mocha.ui.util.ViewUtils;
import com.transitionseverywhere.ChangeBounds;
import com.transitionseverywhere.TransitionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.AbstractBaseActivity;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.support.gtm.AMPAction;
import gsshop.mobile.v2.support.gtm.AMPEnum;
import gsshop.mobile.v2.util.DataUtil;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.util.Ln;

/**
 * 네비게이션 카테고리 레이아웃 뷰
 */
public class CategoryGroupLayout extends LinearLayout {

    //카테고리 그룹뷰
    private ViewGroup all_category;

    private Context mContext;

    //스크롤 컨테이너
    private ViewGroup scene_root, scene_container;
    //현재 선택된 카테고리
    private ExpandableRelativeLayout currentExpandableLayout;

    //카테고리가 비어있는지 여부
    private boolean isEmptyView = true;

    //카테고리가 추가되어있는지 여뷰
    private boolean isCategoryAdd;

    //카테고리 리스트TextView
    private ArrayList<TextView> ExpandableTextViewtList = new ArrayList<TextView>();

    //카테고리 리스트 레이아웃
    private ArrayList<ExpandableRelativeLayout> ExpandableRelativeLayoutList = new ArrayList<ExpandableRelativeLayout>();

    //선택된 카테고리
    private String clickCategory = "";

    private boolean isClick = false;

    private boolean isPerformClick = false;

    public CategoryGroupLayout(Context context) {
        super(context);
        initView(context);
    }

    public CategoryGroupLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CategoryGroupLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        initView(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        all_category = (ViewGroup)findViewById(R.id.all_category);
    }

    /**
     * 카테고리가 비어있는지 리턴
     * @return
     */
    public boolean isEmptyView(){
        return isEmptyView;
    }

    private void initView(Context context) {
        mContext = context;
    }

    /**
     * 카테고리를 추가한다.
     */
    public void addCaterogy(DrawerLayoutHorizontalSupport parent,
                            ViewGroup scene_root, ViewGroup scene_container, final NavigationResult result){

        int index = 0;
        isCategoryAdd = false;

        all_category.removeAllViews();
        ExpandableRelativeLayoutList.clear();
        ExpandableTextViewtList.clear();

        this.scene_root = scene_root;
        this.scene_container = scene_container;

        ViewGroup categoryGroupView = null;

        for(CateContentList category : result.appCmmCategory.cateContentList){
            if(index % 2 == 0) {
                categoryGroupView = addCategoryLeft(parent, category, index);
                // 왼쪽에서 addView를 해주어야 홀수 여도 생긴다.
                all_category.addView(categoryGroupView);
            }else{
                addCategoryRight(parent, categoryGroupView, category, index);
            }
            index++;
        }
        // 홀수 라면 오른쪽에서 화살표 이미지 안보이게.
        if (index % 2 == 1) {
            ImageView arrowRight = categoryGroupView.findViewById(R.id.arrow_right);
            ViewUtils.hideViews(arrowRight);
        }
        isCategoryAdd = true;

        if(index > 0){
            isEmptyView = false;
        }
    }

    public void setEmptyView(boolean isEmptyView){
        this.isEmptyView = isEmptyView;
        if (isEmptyView) {
            currentExpandableLayout = null;
        }
    }

    /**
     * 카테고리 상태 복구
     */
    public void updateCategory(){
        allCategoryClose();
        if(NavigationManager.currentCategoryName != null){
            for(TextView layout : ExpandableTextViewtList){
                if(layout.getTag().toString().equals(NavigationManager.currentCategoryName)){
                    isPerformClick = true;
                    layout.performClick();
                }
            }
        }
    }

    /**
     * 카테고리가 새로고침되어야 하는지 여부 리턴
     */
    public boolean isCategoryRefresh(){
        if(NavigationManager.currentCategoryName != null && NavigationManager.currentCategoryName.equals(clickCategory)){
            return false;
        }
        return true;
    }

    /**
     * 모든 카테고리를 닫는다.
     */
    public void allCategoryClose(){
        for(ExpandableRelativeLayout layout : ExpandableRelativeLayoutList){
            layout.collapse(0, null);
        }
    }
    /**
     * 카테고리를 추가한다.(왼쪽)
     * 후에 수정할 예정. 왼쪽 오른쪽 거의 같은 형식의 내용이 두번 반복. (하나로 합쳐야지..)
     */
    private ViewGroup addCategoryLeft(DrawerLayoutHorizontalSupport parent, final CateContentList category, final int index){
        final ViewGroup categoryGroupView = (ViewGroup) View.inflate(mContext, R.layout.view_navigation_category_group, null);
        ImageView category_img_left = (ImageView)categoryGroupView.findViewById(R.id.category_img_left);
        TextView category_left = (TextView)categoryGroupView.findViewById(R.id.category_left);

        // imageView 크기에 맞게 조정.
        ImageUtil.loadImageFit(getContext(), category.imageUrl, category_img_left, R.drawable.brand_no_android);
        category_left.setText(category.title);

        final ExpandableRelativeLayout expandableLayoutLeft = (ExpandableRelativeLayout)categoryGroupView.findViewById(R.id.expandable_layout_left);
        final ExpandableRelativeLayout expandableLayoutRight = (ExpandableRelativeLayout)categoryGroupView.findViewById(R.id.expandable_layout_right);
        final ImageView arrow_left = (ImageView)categoryGroupView.findViewById(R.id.arrow_left);
        final ImageView arrow_right = (ImageView)categoryGroupView.findViewById(R.id.arrow_right);
        final ImageView dimLeft = (ImageView)categoryGroupView.findViewById(R.id.category_img_left_back_dim);
        final ImageView dimRight = (ImageView)categoryGroupView.findViewById(R.id.category_img_right_back_dim);

        //뷰가 업데이트 될때 상태를 복구하기 위해 gb값으로 tag 셋팅
        category_left.setTag(category.gb);
        ExpandableTextViewtList.add(category_left);
        expandableLayoutLeft.setTag(category.gb);
        ExpandableRelativeLayoutList.add(expandableLayoutLeft);

        LinearLayout expandableContentLayout = (LinearLayout)expandableLayoutLeft.findViewById(R.id.expandable_contents_layout_left);
        RecyclerView recyclerJbContent = (RecyclerView) expandableLayoutLeft.findViewById(R.id.expandable_contents_recycler_left);
        parent.addCheckTouchView(recyclerJbContent);

        AdaptorJBCategory adaptorJBCategory = new AdaptorJBCategory(getContext(), null);
        //        ArrayList<CateContentList> tempCateList = new ArrayList<>();

        int indexSub = 0;
        ViewGroup categorySubGroupView = null;
        expandableContentLayout.removeAllViews();

        String jbpUrl = null;

        JBControllListener listener = new JBControllListener() {

            @Override
            public void onSuccess() {
                recyclerJbContent.setVisibility(VISIBLE);
            }

            @Override
            public void onError(String e) {
                recyclerJbContent.setVisibility(GONE);
                Ln.e("JB Contoller error : " + e);
            }
        };

        //카테고리 서브 추가
        for(final CateContentList content : category.subContentList){
            // LCV 일 경우 (카테고리 리스트)
            if ("LCV".equalsIgnoreCase(content.viewType)) {
                if (indexSub % 2 == 0) {
                    categorySubGroupView = (ViewGroup) View.inflate(mContext, R.layout.view_navigation_category_sub_group, null);
                    TextView category_sub_left = (TextView) categorySubGroupView.findViewById(R.id.category_sub_left);
                    category_sub_left.setText(content.title);

                    categorySubGroupView.findViewById(R.id.category_sub_left_layout).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            goWeb(content.linkUrl);
                        }
                    });
                    expandableContentLayout.addView(categorySubGroupView);

                } else {
                    TextView category_sub_right = (TextView) categorySubGroupView.findViewById(R.id.category_sub_right);
                    category_sub_right.setText(content.title);
                    expandableLayoutLeft.collapse();
                    categorySubGroupView.findViewById(R.id.category_sub_right_layout).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            goWeb(content.linkUrl);
                        }
                    });
                }
            }
            // JBP 매장 리스트일 경우
            else if ("API_JBP".equalsIgnoreCase(content.viewType)){
                recyclerJbContent.setVisibility(VISIBLE);
                recyclerJbContent.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.HORIZONTAL, false));

                jbpUrl = content.linkUrl;
                // Dummy Test hklim
//                jbpUrl = "http://10.52.215.191:9999/app/main/apiJbp";

                recyclerJbContent.setAdapter(adaptorJBCategory);

//                new JBController(mContext, adaptorJBCategory, jbpUrl, null, false, listener).execute();
            }
            else {
                // 둘 중 아무것도 아니면 쓰레기 값으로 판단.
                indexSub++;
                continue;
            }
            indexSub++;

            if(indexSub == category.subContentList.size()){
                categorySubGroupView.findViewById(R.id.bottom_line).setVisibility(View.GONE);
            }
        }

        final String tempJbpUrl = jbpUrl;
        OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isClick){
                    return;
                }
                isClick = true;

//                ScrollView tempScroll = (ScrollView) scene_root;
//                int getYIndex = (index / 2) * DisplayUtils.convertDpToPx(mContext, 48);
//                tempScroll.scrollTo(0, getYIndex);

                if (!TextUtils.isEmpty(tempJbpUrl)) {
                    new JBController(mContext, adaptorJBCategory, tempJbpUrl, null, false, listener).execute();
                }

                // 0.5 초 후에 expandable
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isClick = false;
                    }
                }, 500);

                if(expandableLayoutLeft.isExpanded()){
                    arrow_left.setImageResource(R.drawable.ic_navi_expend);
                    arrow_right.setImageResource(R.drawable.ic_navi_expend);
                    dimLeft.setVisibility(GONE);
                    dimRight.setVisibility(GONE);
                }else{
                    arrow_left.setImageResource(R.drawable.ic_navi_expended);
                    arrow_right.setImageResource(R.drawable.ic_navi_expend);
                    dimLeft.setVisibility(VISIBLE);
                    dimRight.setVisibility(GONE);
                    if(!isPerformClick) {
                        //와이즈로그 전송 (메뉴를 오픈할때만)
                        ((AbstractBaseActivity) mContext).setWiseLogHttpClient(category.wiseLogUrl);

                        //앰플리튜드 그룹 카테고리 오픈이벤트, 어떤이벤트인지 파악이 필요. (hklim)
                        try{
                            //shopNm
                            JSONObject eventProperties = new JSONObject();
                            try {
                                eventProperties.put("shopNm", category.title);
                            } catch (JSONException exception) {
                            }
                            AMPAction.sendAmpEventProperties(AMPEnum.AMP_CLICK_LEFTNAVI_CATEGORYGROUP,eventProperties);
                        }catch (Exception e)
                        {
                            Ln.e(e);
                        }
                    }
                    isPerformClick = false;
                }

                if(expandableLayoutRight.isExpanded() && expandableLayoutLeft.getVisibility() != View.VISIBLE){
                    if(scene_container != null) {
                        TransitionManager.beginDelayedTransition(scene_container, new ChangeBounds());
                    }
                    expandableLayoutRight.setVisibility(View.GONE);
                    expandableLayoutLeft.setVisibility(View.VISIBLE);
                    expandableLayoutLeft.expand(0,null);
                    //메뉴오픈시 gb값 저장
                    currentExpandableLayout = expandableLayoutLeft;
                    NavigationManager.currentCategoryName = category.gb;
                    clickCategory = category.gb;
                    return;
                }

                //현재 오픈된 카테고리와 지금 클릭한 카테고리가 다르면 오픈된 카테고리는 닫는다.
                if(currentExpandableLayout != null && currentExpandableLayout != expandableLayoutLeft){
                    currentExpandableLayout.collapse();
                    currentExpandableLayout = null;
                    NavigationManager.currentCategoryName = null;
                }
                expandableLayoutRight.setVisibility(View.GONE);
                expandableLayoutLeft.setVisibility(View.VISIBLE);

                //카테고리가 열려있다면 닫을거기 때문에 currentCategoryName은 null로 처리
                if(expandableLayoutLeft.isExpanded()){
                    currentExpandableLayout = null;
                    NavigationManager.currentCategoryName = null;
                }else{
                    currentExpandableLayout = expandableLayoutLeft;
                    NavigationManager.currentCategoryName = category.gb;
                    clickCategory = category.gb;
                }
                expandableLayoutLeft.toggle();

            }
        };

        category_left.setOnClickListener(clickListener);
        arrow_left.setOnClickListener(clickListener);

        //카테고리 리스너 추가
        addCategoryListner(categoryGroupView, expandableLayoutLeft, expandableLayoutRight,
                arrow_left, arrow_right, dimLeft, dimRight);

        return categoryGroupView;
    }

    /**
     * 카테고리를 추가한다(오른쪽)
     */
    private void addCategoryRight(DrawerLayoutHorizontalSupport parent,
                                  ViewGroup categoryGroupView , final CateContentList category, final int index){
        ImageView category_img_right = (ImageView)categoryGroupView.findViewById(R.id.category_img_right);
        ImageUtil.loadImageFit(getContext(), category.imageUrl, category_img_right, R.drawable.brand_no_android);

        TextView category_right = (TextView)categoryGroupView.findViewById(R.id.category_right);
        category_right.setText(category.title);

        final ExpandableRelativeLayout expandableLayoutLeft = (ExpandableRelativeLayout)categoryGroupView.findViewById(R.id.expandable_layout_left);
        final ExpandableRelativeLayout expandableLayoutRight = (ExpandableRelativeLayout)categoryGroupView.findViewById(R.id.expandable_layout_right);
        final ImageView arrow_left = (ImageView)categoryGroupView.findViewById(R.id.arrow_left);
        final ImageView arrow_right = (ImageView)categoryGroupView.findViewById(R.id.arrow_right);
        final ImageView dimLeft = (ImageView)categoryGroupView.findViewById(R.id.category_img_left_back_dim);
        final ImageView dimRight = (ImageView)categoryGroupView.findViewById(R.id.category_img_right_back_dim);

        //뷰가 업데이트 될때 상태를 복구하기 위해 gb값으로 tag 셋팅
        category_right.setTag(category.gb);
        ExpandableTextViewtList.add(category_right);
        expandableLayoutRight.setTag(category.gb);
        ExpandableRelativeLayoutList.add(expandableLayoutRight);

        LinearLayout expandableContentLayout = (LinearLayout)expandableLayoutRight.findViewById(R.id.expandable_contents_layout_right);
        RecyclerView recyclerJbContent = (RecyclerView)expandableLayoutRight.findViewById(R.id.expandable_contents_recycler_right);

        parent.addCheckTouchView(recyclerJbContent);

        AdaptorJBCategory adaptorJBCategory = new AdaptorJBCategory(getContext(), null);


        int indexSub = 0;
        ViewGroup categorySubGroupView = null;
        expandableContentLayout.removeAllViews();

        String jbpUrl = null;

        JBControllListener listener = new JBControllListener() {
            @Override
            public void onSuccess() {
                recyclerJbContent.setVisibility(VISIBLE);
            }

            @Override
            public void onError(String e) {
                recyclerJbContent.setVisibility(GONE);
                Ln.e("JB Contoller error : " + e);
            }
        };

        //카테고리 서브 추가
        for(final CateContentList content : category.subContentList){

            // LCV 일 경우 (카테고리 리스트)
            if ("LCV".equalsIgnoreCase(content.viewType)) {
                if (indexSub % 2 == 0) {
                    categorySubGroupView = (ViewGroup) View.inflate(mContext, R.layout.view_navigation_category_sub_group, null);
                    TextView category_sub_left = (TextView) categorySubGroupView.findViewById(R.id.category_sub_left);
                    category_sub_left.setText(content.title);

                    categorySubGroupView.findViewById(R.id.category_sub_left_layout).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            goWeb(content.linkUrl);
                        }
                    });
                    expandableContentLayout.addView(categorySubGroupView);
                } else {
                    TextView category_sub_right = (TextView) categorySubGroupView.findViewById(R.id.category_sub_right);
                    category_sub_right.setText(content.title);
                    expandableLayoutRight.collapse();
                    categorySubGroupView.findViewById(R.id.category_sub_right_layout).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            goWeb(content.linkUrl);
                        }
                    });
                }
            }
            // JBP 매장 리스트일 경우
            else if ("API_JBP".equalsIgnoreCase(content.viewType)){
                recyclerJbContent.setVisibility(VISIBLE);
                recyclerJbContent.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.HORIZONTAL, false));

                jbpUrl = content.linkUrl;
                // Dummy Test hklim
//                jbpUrl = "http://10.52.215.191:9999/app/main/apiJbp";

                recyclerJbContent.setAdapter(adaptorJBCategory);
//                new JBController(mContext, adaptorJBCategory, jbpUrl, null, false, listener).execute();
            }
            else {
                // 둘 중 아무것도 아니면 쓰레기 값으로 판단.
                indexSub++;
                continue;
            }
            indexSub++;

            if(indexSub == category.subContentList.size()){
                categorySubGroupView.findViewById(R.id.bottom_line).setVisibility(View.GONE);
            }
        }

        final String tempJbpUrl = jbpUrl;

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isClick){
                    return;
                }
                isClick = true;

//                ScrollView tempScroll = (ScrollView) scene_root;
//                int getYIndex = (index / 2) * DisplayUtils.convertDpToPx(mContext, 48);
//                tempScroll.scrollTo(0, getYIndex);

                if (!TextUtils.isEmpty(tempJbpUrl)) {
                    new JBController(mContext, adaptorJBCategory, tempJbpUrl, null, false, listener).execute();
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isClick = false;
                    }
                }, 500);

                if(expandableLayoutRight.isExpanded()){
                    arrow_left.setImageResource(R.drawable.ic_navi_expend);
                    arrow_right.setImageResource(R.drawable.ic_navi_expend);
                    dimLeft.setVisibility(GONE);
                    dimRight.setVisibility(GONE);
                }else{
                    arrow_left.setImageResource(R.drawable.ic_navi_expend);
                    arrow_right.setImageResource(R.drawable.ic_navi_expended);
                    dimLeft.setVisibility(GONE);
                    dimRight.setVisibility(VISIBLE);
                    if(!isPerformClick) {
                        //와이즈로그 전송 (메뉴를 오픈할때만)
                        ((AbstractBaseActivity) mContext).setWiseLogHttpClient(category.wiseLogUrl);

                        //앰플리튜드 그룹 카테고리 오픈이벤트
                        try{
                            //shopNm
                            JSONObject eventProperties = new JSONObject();
                            try {
                                eventProperties.put("shopNm", category.title);
                            } catch (JSONException exception) {
                            }
                            AMPAction.sendAmpEventProperties(AMPEnum.AMP_CLICK_LEFTNAVI_CATEGORYGROUP,eventProperties);
                        }catch (Exception e)
                        {
                            Ln.e(e);
                        }
                    }
                    isPerformClick = false;
                }

                //메뉴오픈시 gb값 저장
                if(expandableLayoutLeft.isExpanded() && expandableLayoutRight.getVisibility() != View.VISIBLE){
                    if(scene_container != null) {
                        TransitionManager.beginDelayedTransition(scene_container, new ChangeBounds());
                    }
                    expandableLayoutLeft.setVisibility(View.GONE);
                    expandableLayoutRight.setVisibility(View.VISIBLE);
                    expandableLayoutRight.expand(0, null);
                    //메뉴오픈시 gb값 저장
                    currentExpandableLayout = expandableLayoutRight;
                    NavigationManager.currentCategoryName = category.gb;
                    clickCategory = category.gb;
                    return;
                }

                //현재 오픈된 카테고리와 지금 클릭한 카테고리가 다르면 오픈된 카테고리는 닫는다.
                if(currentExpandableLayout != null && currentExpandableLayout != expandableLayoutRight){
                    currentExpandableLayout.collapse();
                    currentExpandableLayout = null;
                    NavigationManager.currentCategoryName = null;
                }
                expandableLayoutLeft.setVisibility(View.GONE);
                expandableLayoutRight.setVisibility(View.VISIBLE);

                //카테고리가 열려있다면 닫을거기 때문에 currentCategoryName은 null로 처리
                if(expandableLayoutRight.isExpanded()){
                    currentExpandableLayout = null;
                    NavigationManager.currentCategoryName = null;
                }else{
                    currentExpandableLayout = expandableLayoutRight;
                    NavigationManager.currentCategoryName = category.gb;
                    clickCategory = category.gb;
                }
                expandableLayoutRight.toggle();

                currentExpandableLayout = expandableLayoutRight;
            }
        };

        category_right.setOnClickListener(clickListener);
        arrow_right.setOnClickListener(clickListener);
    }

    /**
     * 카테고리 리스너
     * @param categoryGroupView
     * @param expandableLayoutLeft
     * @param expandableLayoutRight
     * @param arrow_left
     * @param arrow_right
     */
    private void addCategoryListner(final ViewGroup categoryGroupView, final ExpandableRelativeLayout expandableLayoutLeft, final ExpandableRelativeLayout expandableLayoutRight,
                                    final ImageView arrow_left, final ImageView arrow_right,
                                    final ImageView dimLeft, final ImageView dimRight){

        expandableLayoutLeft.setListener(new ExpandableLayoutListener() {
            @Override
            public void onAnimationStart() {}

            @Override
            public void onAnimationEnd() {}

            @Override
            public void onPreOpen() {
                //카테고리 오픈전 텍스트에 밑줄을 긋는다.
                View viewCateLeft = categoryGroupView.findViewById(R.id.view_select_left);
                viewCateLeft.setVisibility(VISIBLE);
            }

            @Override
            public void onPreClose() {
                //카테고리 닫기전 밑중을 제거하고 크기를 줄인다.
                if(expandableLayoutLeft.getVisibility() == View.GONE){
                    expandableLayoutLeft.setVisibility(View.INVISIBLE);
                    expandableLayoutLeft.getLayoutParams().height = 0;
                }else{
                    expandableLayoutRight.setVisibility(View.INVISIBLE);
                    expandableLayoutRight.getLayoutParams().height = 0;
                }

                arrow_left.setImageResource(R.drawable.ic_navi_expend);
                View viewCateLeft = categoryGroupView.findViewById(R.id.view_select_left);
                viewCateLeft.setVisibility(GONE);
                dimLeft.setVisibility(GONE);
            }

            @Override
            public void onOpened() {
                //카테고리가 오픈되었을때 오른쪽 카테고리는 닫는다.
                expandableLayoutRight.collapse(0, null);
                expandableLayoutLeft.setLayoutParams(new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            }

            @Override
            public void onClosed() {
                if(scene_container != null) {
                    TransitionManager.endTransitions(scene_container);
                }
            }
        });

        expandableLayoutRight.setListener(new ExpandableLayoutListener() {
            @Override
            public void onAnimationStart() {}

            @Override
            public void onAnimationEnd() {}

            @Override
            public void onPreOpen() {
                if(isCategoryAdd) {
                    View viewCateRight = categoryGroupView.findViewById(R.id.view_select_right);
                    viewCateRight.setVisibility(VISIBLE);
                }
            }

            @Override
            public void onPreClose() {
                //카테고리 닫기전 밑중을 제거하고 크기를 줄인다.
                if(expandableLayoutRight.getVisibility() == View.GONE){
                    expandableLayoutRight.setVisibility(View.INVISIBLE);
                    expandableLayoutRight.getLayoutParams().height = 0;
                }else{
                    expandableLayoutLeft.setVisibility(View.INVISIBLE);
                    expandableLayoutLeft.getLayoutParams().height = 0;
                }
                arrow_right.setImageResource(R.drawable.ic_navi_expend);
                View viewCateRight = categoryGroupView.findViewById(R.id.view_select_right);
                viewCateRight.setVisibility(GONE);
                dimRight.setVisibility(GONE);
            }

            @Override
            public void onOpened() {
                //카테고리가 오픈되었을때 왼쪽 카테고리는 닫는다.
                expandableLayoutLeft.collapse(0, null);
                expandableLayoutRight.setLayoutParams(new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }

            @Override
            public void onClosed() {
                if(scene_container != null) {
                    TransitionManager.endTransitions(scene_container);
                }
            }
        });
    }

    public void openLastClickedItem() {
//        if (currentExpandableLayout != null) {
//            currentExpandableLayout.expand(0, null);
//        }
        if(NavigationManager.currentCategoryName != null){
            for(ExpandableRelativeLayout layout : ExpandableRelativeLayoutList){
                if(NavigationManager.currentCategoryName.equals(layout.getTag().toString())){
                    if (!layout.isExpanded()) {
                        layout.expand(0, null);
                    }
                    break;
                }
            }
        }
    }

    /**
     * 웹으로 이동
     * @param url
     */
    private void goWeb(String url){
        // 첫 호출과 Back키를 구분하기 위해, 타임 시퀀스 파라미터 추가
        // ex) http://sm21.gsshop.com/section/jbp/brandMain.gs?
        // jbpBrandCd=1000000020&mseq=412154&fromApp=Y&_=1486970084963
        WebUtils.goWeb(mContext, Uri.parse(url).buildUpon().
                appendQueryParameter("_", String.valueOf(System.currentTimeMillis())).build().toString());
//        WebUtils.goWeb(mContext, url);
        EventBus.getDefault().post(new Events.NavigationCloseEvent());
    }

    public interface JBControllListener {
        void onSuccess();
        void onError(String e);
    }

    /**
     * 네비게이션 컨트롤러
     */
    protected class JBController extends BaseAsyncController<CateContentList> {

        @Inject
        private RestClient restClient;

        private String url;
        private final Context mContext;
        private View error_view;
        private boolean isDialog;
        private final AdaptorJBCategory adaptorJBCategory;

        private JBControllListener mListener;

        public JBController(Context context, AdaptorJBCategory adaptorJBCategory, String url, View error_view, boolean isDialog, JBControllListener listener) {
            super(context);
            mContext = context;
            this.error_view = error_view;
            this.isDialog = isDialog;
            this.adaptorJBCategory = adaptorJBCategory;
            this.url = url;
            this.mListener = listener;
        }

        @Override
        protected void onPrepare(Object... params) throws Exception {
            if (dialog != null && isDialog) {
                dialog.dismiss();
                dialog.setCancelable(true);
                dialog.show();
            }
            if (params != null) {
                try {
                    url = ((String) params[0]);
                }
                catch (IndexOutOfBoundsException e) {
                    Ln.e(e.getMessage());
                }
                catch (ClassCastException e) {
                    Ln.e(e.getMessage());
                }
            }
        }

        @Override
        protected CateContentList process() throws Exception {
            return (CateContentList) DataUtil.getData(context, restClient, CateContentList.class,
                    false, false, url);
        }

        @Override
        protected void onSuccess(final CateContentList result) throws Exception {
            if(error_view != null){
                error_view.setVisibility(View.GONE);
            }

            if (result != null && result.subContentList.size() > 0) {
                mListener.onSuccess();
            }
            else {
                mListener.onError("List is empty!");
                return;
            }
            adaptorJBCategory.setData(result.subContentList);
            adaptorJBCategory.notifyDataSetChanged();
//            recyclerView.setAdapter(new AdaptorJBCategory(context, result.subContentList));
        }

        @Override
        protected void onError(Throwable e) {
            if (mListener != null)
                mListener.onError(e.getMessage());
//            if(error_view != null){
//                error_view.setVisibility(View.VISIBLE);
//            }
            Ln.e(e);

        }

        @Override
        protected void onFinally() {
            super.onFinally();
            if (dialog != null) {
                dialog.dismiss();
            }
        }
    }
}
