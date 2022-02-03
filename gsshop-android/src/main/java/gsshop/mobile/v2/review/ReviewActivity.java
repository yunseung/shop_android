/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.review;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.inject.Inject;
import com.gsshop.mocha.device.SystemInfo;
import com.gsshop.mocha.network.rest.RestClient;
import com.gsshop.mocha.network.util.HttpUtils;
import com.gsshop.mocha.pattern.mvc.BaseAsyncController;
import com.gsshop.mocha.ui.util.ImageUtils;
import com.gsshop.mocha.ui.util.ViewUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.Keys.ACTION;
import gsshop.mobile.v2.Keys.REQCODE;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.attach.FileAttachUtils;
import gsshop.mobile.v2.attach.PhotoItem;
import gsshop.mobile.v2.menu.BaseTabMenuActivity;
import gsshop.mobile.v2.menu.TabMenu;
import gsshop.mobile.v2.review.ReviewConnector.SaveReviewResult;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog.ButtonClickListener;
import gsshop.mobile.v2.support.ui.CustomOneButtonDialog.CancelListener;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.PermissionUtils;
import gsshop.mobile.v2.util.StringUtils;
import gsshop.mobile.v2.util.ThreadUtils;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

/**
 * 상품평 작성 화면.
 * 본 액티비티는 ReviewUrlHandler로 부터 호출됨
 * <pre>
 * #urlQueryString sample
 * 1)상품평 수정
 * "messageId=102497398&inflowPath=mgntModify&prdCd=15280783&format=json&save_root=mobilePrd&order_num=&lineNum=&modify=&prdid=15280783"
 * 2)신규상품평 작성
 * "prdid=16820569&save_root=mobilePrd&inflowPath=mgntWrite&ordNo=715715720&ordRetUrl=&format=json&order_num=&lineNum="
 * </pre>
 * <p>
 * TODO Bundle savedInstanceState의 활용고민.
 * => 카메라때문에 이 액티비티가 강제 destroy되면 어떻게 되겠는가?
 * http://www.androidpub.com/1933031
 */
@SuppressLint("NewApi")
@SuppressWarnings("unused")
public class ReviewActivity extends BaseTabMenuActivity {

    /**
     * 태그명
     */
    private static final String TAG = "ReviewActivity";

    /**
     * 일반상품 : "generalProduct"
     * 평가상품(사진첨부 및 상품평 수정 안됨) : "evaluationProduct"
     */
    private static final String GNR_PRODUCT_TYPE = "generalProduct";

    private static final String EVAL_PRODUCT_TYPE = "evaluationProduct";

    /**
     * 평가상품 평가항목 width값(DP)
     */
    private static final int RATING_NAME_WIDTH_DP = 40;

    /**
     * 상품평 최소 별점
     */
    private static final float RATING_BAR_MIN_VALUE = 1;

    /**
     * 첨부가능 이미지 최대 갯수
     */
    public static final int MAX_ATTACH_IMAGE_NUM = 5;

    /**
     * 내용입력란에 입력가능한 최대 바이트
     * 20170601 글자 제한 삭제
     */
    //private static final int MAX_COMMENT_LEN = 1000;

    /**
     * 상품이미지 (화면 좌상단에 표시됨)
     */
    @InjectView(R.id.img_product)
    private ImageView imgProduct;

    /**
     * 상품명 (상품이미지 우측에 표시)
     */
    @InjectView(R.id.txt_product)
    private TextView txtProduct;

    /**
     * 스크롤 영역 (해더와 하단 탭메뉴 사이 영역)
     */
    @InjectView(R.id.scroll_content)
    private ScrollView scrollContent;

    /**
     * 상품평 내용
     */
    @InjectView(R.id.edit_review_contents)
    private EditText editReviewContents;

    @InjectView(R.id.edit_review_layout)
    private LinearLayout editReviewLayout;

    /**
     * 식품및 의료기기 안내 정보
     */
    @InjectView(R.id.medical_device_and_food_info)
    private LinearLayout medical_device_and_food_info;


    /**
     * 평가항목 텍스트 (예:디자인)
     */
    @InjectView(R.id.txt_rating1)
    private TextView txtRating1;

    @InjectView(R.id.txt_rating2)
    private TextView txtRating2;

    @InjectView(R.id.txt_rating3)
    private TextView txtRating3;

    @InjectView(R.id.txt_rating4)
    private TextView txtRating4;

    /**
     * 평가항목 별점
     */
    @InjectView(R.id.rating_bar1)
    private RatingBar ratingBar1;

    @InjectView(R.id.rating_bar2)
    private RatingBar ratingBar2;

    @InjectView(R.id.rating_bar3)
    private RatingBar ratingBar3;

    @InjectView(R.id.rating_bar4)
    private RatingBar ratingBar4;

    /**
     * 상품평 사진 리스트 저장용 리사이클러뷰
     */
    @InjectView(R.id.list_photo)
    private RecyclerView listPhotoInfos;

    @InjectView(R.id.layout_tab_menu)
    private View tabMenuView;

    /**
     * 어댑터에서 사용할 상품평 데이타
     */
    private List<ReviewPhotoInfo> photoInfos;

    /**
     * 작성하기/수정하기 버튼
     */
    @InjectView(R.id.btn_save)
    private Button btnSave;

    private ReviewInfoV2 reviewInfo;

    /**
     * 앱 접근성 기능이 켜져있는가?
     */
    private boolean accessibilityOn;

    private RatingBar[] ratingBars;

    @Inject
    private RestClient restClient;

    /**
     * 사용자가 입력한 글자수
     */
    @InjectView(R.id.text_len)
    private TextView textLen;

    /**
     * 리워드 정보 노출/숨김 플래그
     */
    private boolean rewardOn;

    /**
     * 리워드 전체 레이아웃 (신규/수정 구분하여 노출 및 숨김처리용)
     */
    @InjectView(R.id.lay_reward)
    private LinearLayout layReward;

    /**
     * 리워드 안내이미지 레이아웃
     */
    @InjectView(R.id.lay_reward_detail)
    private LinearLayout layRewardDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review);
        setHeaderView("");
        setupFields();

        setListenerToRootView();

        // 클릭리스너 등록
        setClickListener();

        //ReviewUrlHandler로부터 API호출시 사용할 GET파라미터를 전달받는다.
        String urlQuery = getIntent().getStringExtra(Keys.INTENT.REVIEW_QUERY_STRING);
        //신규작성/수정 상관없이 모두 서버에서 상품평 관련 정보를 취득한다.
        new GetReviewController(this).execute(urlQuery);

    }

    boolean isKeyboardOpened = false;

    /**
     * 뷰의 클릭리스너를 세팅한다.
     */
    private void setClickListener() {
        findViewById(R.id.img_product).setOnClickListener((View v) -> {
                    prdDetailimg();
                }
        );
        findViewById(R.id.txt_product).setOnClickListener((View v) -> {
                    prdDetailtxt();
                }
        );
        findViewById(R.id.btn_cancel).setOnClickListener((View v) -> {
                    cancelReview();
                }
        );
        findViewById(R.id.btn_save).setOnClickListener((View v) -> {
                    saveReview();
                }
        );
    }

    /**
     * detect keyboard show or hide
     */
    public void setListenerToRootView() {
        final View activityRootView = getWindow().getDecorView().findViewById(android.R.id.content);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private final Rect r = new Rect();

            @Override
            public void onGlobalLayout() {

//				int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                activityRootView.getWindowVisibleDisplayFrame(r);
                int height = activityRootView.getRootView().getHeight();
                Ln.i("height: " + height + ", bottom: " + r.bottom + ", top: " + r.top);
                int heightDiff = Math.abs(activityRootView.getRootView().getHeight() - (r.bottom + r.top));

                if (heightDiff > 100) { // 99% of the time the height diff will be due to a keyboard.
                    if (isKeyboardOpened == false) {
                        //Do two things, make the view top visible and the editText smaller
                        scrollToEditBoxForReward();
                        ViewUtils.hideViews(tabMenuView);
                    }
                    isKeyboardOpened = true;
                } else if (isKeyboardOpened == true) {
                    isKeyboardOpened = false;
                    scrollToEditBoxForReward();
                    ViewUtils.showViews(tabMenuView);
                }
            }
        });
    }

    /**
     * 스크롤 영역을 에디터까지 스크롤한다.
     */
    private void scrollToEditBoxForReward() {
        ThreadUtils.INSTANCE.runInUiThread(() -> {
            int bottom = editReviewContents.getBottom();
            int height = tabMenuView.getHeight();
            scrollContent.smoothScrollTo(0, bottom + height);
        }, 200);
    }

    /**
     * 스크롤 영역을 바닥까지 스크롤한다.
     */
    private void scrollToBottomForReward() {
        ThreadUtils.INSTANCE.runInUiThread(() -> scrollContent.smoothScrollBy(0, scrollContent.getBottom()), 200);
    }

    /**
     * 스크롤 영역을 위쪽까지 스크롤한다.
     */
    private void scrollToTopForReward() {
        ThreadUtils.INSTANCE.runInUiThread(() -> scrollContent.fullScroll(View.FOCUS_UP), 200);
    }

    /**
     * UI객체 초기화 및 세팅
     */
    private void setupFields() {
        rewardOn = false;
        //화면 로딩시 리워드영역 숨긴 후 GetReviewController호출하여 신규/수정 판단 후 노출여부 결정함
        layReward.setVisibility(View.GONE);

        accessibilityOn = SystemInfo.isAccessbilityEnabled();
        ratingBars = new RatingBar[]{ratingBar1, ratingBar2, ratingBar3, ratingBar4};

        //입력한 글자수 표시 및 최대글자수 초과시 입력제한
        editReviewContents.addTextChangedListener(new TextWatcher() {
            String displayText;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /*
                20170601 배포용으로 글자 제한 삭제
                int len = 0;
                try {
                    len = s.toString().getBytes("EUC-KR").length;

                    if (len > MAX_COMMENT_LEN) {    //최대 글자수 초과시 변경전 텍스트 세팅
                        editReviewContents.setText(displayText);
                        editReviewContents.setSelection(start);
                        alertMessage(getString(R.string.mobiletalk_exceed_len, MAX_COMMENT_LEN), false);
                    } else {    //최대 글자수 이하인 경우 글자수 표시
                        textLen.setText(String.valueOf(len));
                    }
                } catch (UnsupportedEncodingException e) {
                    // 10/19 품질팀 요청
                    // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
                    // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
                    // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
                    Ln.e(e);
                }
                */
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                displayText = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // 상품평 테스트가 focus를 가지면 hint 텍스트가 사라지도록 설정
        editReviewContents.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public CharSequence hintText = null;

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    hintText = editReviewContents.getHint();
                    editReviewContents.setHint("");
                } else if (hintText != null) {
                    editReviewContents.setHint(hintText);

                }
            }
        });
        // 상품평의 최소 별점이 1 이하가 되지 않도록 함.
        // 터치한 상태에서 슬라이드 할 경우, 별점이 0점까지 갔다가 손을 뗀 후에 1로 설정되는 문제가 있음.
        OnRatingBarChangeListener ratingBarChangeListener = new OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                int ratingValue = (int) rating;
                if (rating < RATING_BAR_MIN_VALUE) {
                    ratingValue = (int) RATING_BAR_MIN_VALUE;
                    ratingBar.setRating(ratingValue);
                }

                //(앱 접근성) 음성 데이터를 다시 읽어주기 위해 포커스 강제 변경
                if (accessibilityOn) {
                    ratingBar.setFocusable(false);
                    ratingBar.setFocusableInTouchMode(false);

                    ratingBar.setContentDescription(getString(R.string.review_description_rating,
                            ratingValue));

                    ratingBar.setFocusable(true);
                    ratingBar.setFocusableInTouchMode(true);
                    ratingBar.requestFocus();
                }
            }
        };

        for (RatingBar r : ratingBars) {
            r.setOnRatingBarChangeListener(ratingBarChangeListener);
        }

        //(앱 접근성) 화면로딩 후 처음 포커스 이동 시 현재 점수를 읽어줌
        if (accessibilityOn) {
            for (RatingBar r : ratingBars) {
                r.setContentDescription(getString(R.string.review_description_rating,
                        (int) r.getRating()));
            }
        }

        // 리사이클뷰 세팅
        //아이템 간격 세팅
        RecyclerView.ItemDecoration itemDecoration =
                new SpacesItemDecoration(DisplayUtils.convertDpToPx(this, (float) 8));
        listPhotoInfos.addItemDecoration(itemDecoration);

        // allows for optimizations if all items are of the same size:
        listPhotoInfos.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        listPhotoInfos.setLayoutManager(llm);

        photoInfos = new ArrayList<ReviewPhotoInfo>();
        for (int i = 0; i < MAX_ATTACH_IMAGE_NUM; i++) {
            photoInfos.add(new ReviewPhotoInfo());
        }
        ReviewPhotoAdapter adapter = new ReviewPhotoAdapter(this, photoInfos);
        listPhotoInfos.setAdapter(adapter);
    }

    /**
     * 스크롤 영역을 바닥까지 스크롤하여
     * 제목/내용 입력필드가 화면 상단에 잘 보이도록 한다.
     * <p>
     * 약간의 딜레이를 주어야 정상동작한다.
     */
    private void scrollToBottomForContent() {
        ThreadUtils.INSTANCE.runInUiThread(() -> {
            //bottom까지 올리면 커서가 위로 이동해서 보이지 않음
            //scrollContent.smoothScrollBy(0, scrollContent.getBottom());
            //그래서 일정 크기의 픽셀만 위로 이동시킴
            scrollContent.smoothScrollBy(0, 80);

            // NOTE : 아래 코드를 쓰면 포커스를 자동으로 뺏김
            //scroll.fullScroll(ScrollView.FOCUS_DOWN);
        }, 200);
    }


    /**
     * 상품 아이콘 클릭시 단품페이지로 이동한다.
     */
    private void prdDetailimg() {
        if (reviewInfo != null) {
            WebUtils.goWeb(this, ServerUrls.WEB.PRODUCT_DETAIL + "&prdid=" + reviewInfo.getPrdCd());
        }
    }

    /**
     * 상품명 클릭시 단품페이지로 이동한다.
     */
    private void prdDetailtxt() {
        if (reviewInfo != null) {
            WebUtils.goWeb(this, ServerUrls.WEB.PRODUCT_DETAIL + "&prdid=" + reviewInfo.getPrdCd());
        }
    }

    /**
     * 취소버튼 클릭시 본 액티비티를 종료한다.
     */
    private void cancelReview() {
        finish();
    }

    /**
     * 상품평 저장(등록)
     */
    private void saveReview() {
        // 입력 검증
        String contents = editReviewContents.getText().toString().trim();
        int minLength = getResources().getInteger(R.integer.review_min_length);

        //별점이 유효한지 체크
        boolean isVerificationRating = ratingVerification();

        if (!isVerificationRating) {
            Dialog dialog = new CustomOneButtonDialog(this).message(R.string.review_validation_rating).buttonClick(
                    CustomOneButtonDialog.DISMISS);
            dialog.show();
            return;
        }

        // 일반상품에 한해 상품평 입력 최소 글자수 제한 (일반:generalProduct, 평가:evaluationProduct)
        // 루나에서 수집된 익셉션 대응
        if (reviewInfo != null
                && GNR_PRODUCT_TYPE.equals(reviewInfo.getPrdrevwTypCd().trim())) {
            if (contents.length() < minLength) {
                String message = getString(R.string.review_validation_min_length, minLength);
                Dialog dialog = new CustomOneButtonDialog(this).message(message).buttonClick(
                        CustomOneButtonDialog.DISMISS);
                dialog.show();
                return;
            }
        }

        //상품평 저장
        new SaveReviewController(this).execute();
    }

    /**
     * 별점이 유효한지 확인한다.
     *
     * @return 유효한 경우  true 리턴
     */
    private boolean ratingVerification() {
        boolean isVerificationRating = true;
        if (ratingBar1.getRating() < 1 || ratingBar2.getRating() < 1 || ratingBar3.getRating() < 1 || ratingBar4.getRating() < 1) {
            isVerificationRating = false;
        }

        return isVerificationRating;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //갤러리에서 이미지 선택 또는 사진촬영을 취소한 경우
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }

        //커스텀 갤러리에서 이미지를 선택한 경우
        if (requestCode == REQCODE.GALLERY) {
            if (MainApplication.articlePhotoes != null
                    && !MainApplication.articlePhotoes.isEmpty()) {
                for (PhotoItem item : MainApplication.articlePhotoes) {
                    int i = 0;
                    // 동일한 사진 존재여부 체크
                    // 동일 사진이 존재하는 경우는 (i != photoInfos.size())
                    for (ReviewPhotoInfo info : photoInfos) {
                        if (info.added && info.photo.fullImageUri != null && info.photo.fullImageUri.equals(item.fullImageUri)) {
                            break;
                        }
                        i++;
                    }

                    // 사진 추가. - 동일한 사진 추가를 막으려면 true->false로 변경
                    // TODO: 2016. 10. 6.
                    // 해당 조건은 항상 true 이다. 그런데 고치지 않겠다 10/05 ( 문제가 되면 그떄 지우자 ) 이민수
                    if (true || i == photoInfos.size()) {
                        for (ReviewPhotoInfo info : photoInfos) {
                            if (!info.added) {
                                info.photo = item;
                                info.added = true;
                                break;
                            }
                        }
                    }
                }

                listPhotoInfos.getAdapter().notifyDataSetChanged();
            }
            // 카메라로 촬영한 경우
        } else if (requestCode == REQCODE.ATTACH_CAMERA) {
            try {
                String imagePath;
                imagePath = MainApplication.attechImagePath.getAbsolutePath();

                for (ReviewPhotoInfo info : photoInfos) {
                    if (!info.added) {
                        Uri fullImageUri = Uri.parse(imagePath);

                        List<String> paths = fullImageUri.getPathSegments();
                        String name = paths.get(paths.size() - 2);

                        // Create the list item.
                        info.photo = new PhotoItem(fullImageUri, name);
                        info.added = true;
                        break;
                    }
                }
                listPhotoInfos.getAdapter().notifyDataSetChanged();
            } catch (Exception e) {
                // 10/19 품질팀 요청
                // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
                // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
                // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
                Ln.e(e);
            }
        }
    }

    // android 6.0 지원(카메라 사용 권한에 따른 처리)
    // 퍼미션 허용/거부 팝업 선택 후, 처리할 로직
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQCODE.PERMISSIONS_REQUEST_STORAGE_GALLERY:
                // 저장 권한 득하면 바로 갤러리 실행
                if (PermissionUtils.verifyPermissions(this, permissions, grantResults)) {
                    int count = 0;
                    // 사용자가 선택 가능한 사진 갯수를 계산
                    for (ReviewPhotoInfo photo : photoInfos) {
                        if (!photo.added) {
                            count++;
                        }
                    }
                    FileAttachUtils.startGallery(this, count);
                }
                break;
            case REQCODE.PERMISSIONS_REQUEST_STORAGE_CAMERA:
                // 저장 권한 득하면 바로 카메라 실행
                if (PermissionUtils.verifyPermissions(this, permissions, grantResults)) {
                    FileAttachUtils.startCamera(this);
                }
                break;
        }
    }

    /**
     * 이미지를 축소한 비트맵을 반환한다.
     *
     * @param imagePath 이미지경로
     */
    private Bitmap resizeGalleryImageToBitmap(String imagePath) {
//		Bitmap shrinkedBitmap = FileAttachUtils.rotateAndScaleDown(imagePath, FileAttachUtils.ATTACH_IMAGE_WIDTH);
        return FileAttachUtils.rotateAndScaleDown(imagePath, FileAttachUtils.ATTACH_IMAGE_WIDTH);
    }

    /**
     * 이미지 축소 후 파일객체로 변환한다.
     *
     * @param imagePath 이미지경로
     * @param idx       인덱스번호
     * @return File (review_[1~5].jpg)
     */
    private File resizeGalleryImageToFile(String imagePath, int idx) {
        File file = null;
        Bitmap shrinkedBitmap = FileAttachUtils.rotateAndScaleDown(imagePath, FileAttachUtils.ATTACH_IMAGE_WIDTH);
        try {
            file = ImageUtils.bitmapToFile(shrinkedBitmap, ReviewUtils.getTempReviewImage(getApplicationContext(), idx));
        } catch (IOException e) {
            Ln.e(e);
        }
        return file;
    }

    /**
     * 상품평 저장후 이동 URL
     *
     * @param url
     */
    private void goTargetUrl(String url) {
        if (url == null) {
            finish();
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(Keys.INTENT.WEB_URL, ServerUrls.getHttpRoot() + url);

        if (getIntent().getBooleanExtra(Keys.INTENT.FOR_RESULT, false)) {
            // 이전 웹액티비티에서 해당 페이지로 이동
            setResult(RESULT_OK, intent);
        } else {
            intent.setAction(ACTION.WEB);
            intent.putExtra(Keys.INTENT.TAB_MENU, TabMenu.getTabMenu(getIntent()));

            boolean fromTabMenu = TabMenu.fromTabMenu(getIntent());
            intent.putExtra(Keys.INTENT.FROM_TAB_MENU, fromTabMenu);

            if (fromTabMenu) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }

            // 새 웹액티비티 시작
            startActivity(intent);
        }

        finish();
    }

    /**
     * 상품평 작성을 위한 조회
     */
    private class GetReviewController extends BaseAsyncController<ReviewInfoV2> {

        @Inject
        private ReviewAction reviewAction;

        @Inject
        private ReviewExceptionHandler exceptionHandler;

        private String urlQueryString;

        protected GetReviewController(Context activityContext) {
            super(activityContext);
        }

        @Override
        protected void onPrepare(Object... params) throws Exception {
            super.onPrepare(params);
            this.urlQueryString = (String) params[0];
        }

        @Override
        protected ReviewInfoV2 process() throws Exception {
            ReviewInfoV2 review = reviewAction.getReview(urlQueryString);
            saveImageToLocal(review);
            return review;
        }

        @Override
        protected void onSuccess(ReviewInfoV2 review) throws Exception {
            if (review.getAtachFilePathList() != null && review.getAtachFilePathList().length > 0) {
                listPhotoInfos.getAdapter().notifyDataSetChanged();
            }


            txtProduct.setText(review.getExposPrdNm());

            if (review.getProductImage() == null) {
                imgProduct.setImageResource(R.drawable.app_icon);
            } else {
                imgProduct.setImageBitmap(review.getProductImage());
            }

            if (review.getHiddenData().isReviewUpdate()) {
                //상품평 편집
                setHeaderTitle(R.string.review_title_modify);
                //서버에서 상품평 내용 뒤에 whitespace를 추가해서 내려주는 경우가 있어 rtrim으로 처리함
                editReviewContents.setText(StringUtils.rTrim(review.getPrdrevwBody()));
                btnSave.setText(getString(R.string.review_update));

                ratingBar1.setRating(review.getEvalItmVal1());
                ratingBar2.setRating(review.getEvalItmVal2());
                ratingBar3.setRating(review.getEvalItmVal3());
                ratingBar4.setRating(review.getEvalItmVal4());

            } else {
                //상품평 쓰기
                setHeaderTitle(R.string.review_title_write);
                btnSave.setText(getString(R.string.review_save));

                //상품평 신규 작성일 경우만 리워드 정보 노출
                layReward.setVisibility(View.VISIBLE);

                //상품평쓰기의 경우에도 서버에서 보내준 기본값 사용
                ratingBar1.setRating(review.getEvalItmVal1());
                ratingBar2.setRating(review.getEvalItmVal2());
                ratingBar3.setRating(review.getEvalItmVal3());
                ratingBar4.setRating(review.getEvalItmVal4());
            }

            editReviewLayout.setVisibility(review.getPrdrevwTypCd().trim().endsWith(EVAL_PRODUCT_TYPE) ?
                    View.GONE : View.VISIBLE);

            // 일반상품인 경우만 사진첨부 영역을 노출함
            listPhotoInfos.setVisibility(review.getPrdrevwTypCd().trim().endsWith(GNR_PRODUCT_TYPE) ?
                    View.VISIBLE : View.GONE);

            medical_device_and_food_info.setVisibility(review.getPrdrevwTypCd().trim().endsWith(EVAL_PRODUCT_TYPE) ?
                    View.VISIBLE : View.GONE);

            // 내용입력 힌트 문구 선택
            // 10/04 보안 정책에 따라 문구 변경 의료 및 구분없음 이민수
            editReviewContents.setHint(R.string.review_hint_contents_sec_normal);

            //editReviewContents.setHint(review.getPrdrevwTypCd().trim().endsWith(GNR_PRODUCT_TYPE) ?
            //		R.string.review_hint_contents_normal : R.string.review_hint_contents_restricted);

            //GNR_PRODUCT_TYPE이 아닌 경우(평가상품) 중앙정렬을 위해 width값 조절
            if (!review.getPrdrevwTypCd().trim().endsWith(GNR_PRODUCT_TYPE)) {
                LayoutParams params = txtRating4.getLayoutParams();
                params.width = DisplayUtils.convertDpToPx(getApplicationContext(), RATING_NAME_WIDTH_DP);
                txtRating1.setLayoutParams(params);
                txtRating2.setLayoutParams(params);
                txtRating3.setLayoutParams(params);
                txtRating4.setLayoutParams(params);
            }

            if (review.getPrdrevwTotGrd() <= 0) {
                review.setPrdrevwTotGrd(100);
            }

            txtRating1.setText(review.getEvalItmNm1());
            txtRating2.setText(review.getEvalItmNm2());
            txtRating3.setText(review.getEvalItmNm3());
            txtRating4.setText(review.getEvalItmNm4());

            reviewInfo = review;
        }

        @Override
        protected void onError(Throwable e) {
            exceptionHandler.handle(ReviewActivity.this, e);
        }
    }

    /**
     * 서버에 있는 이미지를 로컬에 저장 후 URI를 추출하여 어뎁터 변수에 세팅한다.
     * 서버에 저장된 이미지가 있는 경우(수정할 때)
     *
     * @param review 서버에서 전달받은 상품평 정보
     */
    private void saveImageToLocal(ReviewInfoV2 review) {
        if (review.getAtachFilePathList() != null && review.getAtachFilePathList().length > 0) {
            try {
                for (int i = 0; i < review.getAtachFilePathList().length; i++) {
                    //서버에 있는 이미지를 로컬에 저장 후 URI를 추출한다.
                    //atachFilePath는 "http://image.gsshop.com/mobile/image/201507/23c4f3c8-40dd-465c-b808-ad23be4aaad4.jpg" 형태
                    //uri는 "file:///data/data/gsshop.mobile.v2/cache/review_1.jpg" 형태
                    Uri uri = Uri.fromFile(HttpUtils.getFile(
                            review.getAtachFilePathList()[i], ReviewUtils.getTempReviewImage(context, i + 1)));

                    //어뎁터에서 사용하는 리스트 데이타에 이미지경로, 상품코드, uri값을 세팅한다.
                    for (ReviewPhotoInfo info : photoInfos) {
                        if (!info.added) {
                            info.photo = new PhotoItem(review.getAtachFilePathList()[i], review.getPrdCd(), uri);
                            info.added = true;
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                //서버 이미지를 폰에 저장 및 uri를 추출하는 과정에서 발생
                //사용자에게 팝업 메시지를 띄우고 확인 또는 백키 클릭시 액티비티를 종료한다.
                String message = context.getString(R.string.review_fail_load_image);
                throw new ReviewImageLoadException(e).setUserMessage(message);
            }
        }
    }

    /**
     * 상품평 저장 콘트롤러.
     */
    private class SaveReviewController extends BaseAsyncController<SaveReviewResult> {
        @Inject
        private ReviewAction reviewAction;
        //첨부할 이미지 저장 변수
        File[] files = new File[MAX_ATTACH_IMAGE_NUM];

        protected SaveReviewController(Context activityContext) {
            super(activityContext);
        }

        @Override
        protected void onPrepare(Object... params) throws Exception {
            super.onPrepare(params);
            if (reviewInfo != null && editReviewContents != null) {
                reviewInfo.setPrdrevwBody(editReviewContents.getText().toString().trim());
                reviewInfo.setEvalItmVal1((int) ratingBar1.getRating());
                reviewInfo.setEvalItmVal2((int) ratingBar2.getRating());
                reviewInfo.setEvalItmVal3((int) ratingBar3.getRating());
                reviewInfo.setEvalItmVal4((int) ratingBar4.getRating());
            }
        }

        @Override
        protected SaveReviewResult process() throws Exception {
            //선택한 이미지를 서버로 전송하기 위해  이미지 정보를 reviewInfo 모델에 저장
            int idx = 0;
            for (ReviewPhotoInfo info : photoInfos) {
                if (info.added) {
                    files[idx] = resizeGalleryImageToFile(info.photo.fullImageUri.getPath(), idx + 1);
                }
                idx++;
            }
            reviewInfo.setReviewImageFiles(files);

            return reviewAction.saveReview(reviewInfo);
        }

        @Override
        protected void onSuccess(SaveReviewResult result) throws Exception {
            Intent data = new Intent();
            if (reviewInfo.getHiddenData().isReviewUpdate()) {
                data.putExtra(Keys.CACHE.REVIEW_SAVE, true); // 수정
            } else {
                data.putExtra(Keys.CACHE.REVIEW_SAVE, false); // 작성
            }
            data.putExtra(Keys.INTENT.WEB_URL, ServerUrls.getHttpRoot() + result.getRtnUrl());
            //2014.02.11 parksegun EC통합 재구축 상품평 저장후 Return URL 처리
            // URL을 Intent에 담아서 전달.
            //goTargetUrl(result.getRtnUrl());
            // 2014.03.03 parksegun EC통합 재구축 상품평 저장후 Return URL 처리 취소
            //BaseWebActivity에서 웹뷰를 리로드한다.(result.rtnUrl에 값은 내려오나 사용하지 않음)
            setResult(RESULT_OK, data);
            finish();
        }
    }

    /**
     * 사용자에게 메시지를 노출한다.
     *
     * @param message  메시지
     * @param isFinish true이면 액티비티 종료
     */
    private void alertMessage(String message, final boolean isFinish) {
        Dialog dialog = new CustomOneButtonDialog(this).message(message)
                .buttonClick(new ButtonClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        if (isFinish) {
                            finish();
                        }
                    }
                })
                .cancelled(new CancelListener() {
                    @Override
                    public void onCancel(Dialog dialog) {
                        if (isFinish) {
                            finish();
                        }
                    }
                });
        dialog.show();
    }

    /**
     * 리사이클러뷰에서 아이템 사이의 간격을 설정한다.
     */
    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private final int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            //마지막 아이템은 오른쪽 여백을 주지 않는다.
            if (parent.getChildAdapterPosition(view) < (MAX_ATTACH_IMAGE_NUM - 1)) {
                outRect.right = space;
            }
        }
    }

}
