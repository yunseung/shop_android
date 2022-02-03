package gsshop.mobile.v2.web.productDetail.views;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gsshop.mocha.ui.util.ViewUtils;

import java.util.Arrays;
import java.util.List;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.support.gtm.AMPEnum;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.IceBergEncoder;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.productDetail.ProductDetailWebActivityV2;
import gsshop.mobile.v2.web.productDetail.dto.NativeProductV2;

import static com.blankj.utilcode.util.EmptyUtils.isEmpty;
import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;
import static gsshop.mobile.v2.util.ImageUtil.BaseImageResolution.HD;

/**
 * 단품 배송정보 레이아웃.
 * 템플릿은 최대 2개까지 들어올 수 있음.
 * #history
 * -21.04.02 배송지변경 버튼은 재사용 가능성이 있을 것 같아 제거하지 않음
 */
public class ProductDetailDeliveryLayout extends LinearLayout {
    /**
     * 아이템 리소스 정의
     */
    private static int ITEM_COUNT = 2;
    private static int[] ITEM_RES_IDS = {R.id.item_1st, R.id.item_2nd};

    /**
     * UI 오브젝트 변수저장
     */
    Object[][] objList = new Object[9][8];

    /**
     * 배송정보 최대 라인스
     */
    private static final int DELIVERY_TEXT_MAX_LINE = 9;

    /**
     * 컨텍스트
     */
    private Context mContext;

    /**
     * 아이템뷰
     */
    private View[] itemView = new View[2];

    /**
     * 첫번째 라인 (앞에 섬네일이 위치함)
     */
    private TextView[] mTvMain = new TextView[2];

    /**
     * 툴팁
     */
    private FrameLayout mFlTooltip;

    /**
     * 툴팁 닫기버튼
     */
    private ImageView mIvTooltipClose;

    public ProductDetailDeliveryLayout(Context context) {
        super(context);

        mContext = context;
        initLayout();
    }

    public ProductDetailDeliveryLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initLayout();
    }

    public ProductDetailDeliveryLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initLayout();
    }

    /**
     * 레이아웃을 인플레이트한다.
     */
    private void initLayout() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.product_detail_delivery_area, this, true);
        initViews();
    }

    /**
     * inflate 된 후에 호출
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    /**
     * 배송정보 영역을 비노출한다.
     */
    public void hide() {
        ViewUtils.hideViews(itemView[0], itemView[1], mFlTooltip);
    }


    /**
     * API에서 수신한 데이타로 뷰를 세팅한다.
     *
     * @param component NativeProductV2.DeliveryInfo
     */
    public void setProductDetailDeliveryArea(NativeProductV2.Component component) {
        hide();
        if (isEmpty(component) || isEmpty(component.deliveryList) || isEmpty(component.deliveryList.get(0))) {
            return;
        }

        for (int m = 0; m < component.deliveryList.size(); m++) {
            if (m >= ITEM_COUNT) {
                break;
            }

            NativeProductV2.DeliveryList deliveryList = component.deliveryList.get(m);

            LinearLayout mLlChangeAddr = itemView[m].findViewById(R.id.ll_change_addr);
            TextView mTvChangeAddr = itemView[m].findViewById(R.id.tv_change_addr);
            LinearLayout mLlMore = itemView[m].findViewById(R.id.ll_more);
            LinearLayout mLlTxtArea = itemView[m].findViewById(R.id.ll_txt_area);

            ImageView mIvMainThumbnail = itemView[m].findViewById(R.id.iv_main_thumbnail);
            //배송지변경이 노출되는 경우 첫라인 오른쪽 여백 필요 (버튼영역 침범하지 않기 위함)
            View mVwRMargin = itemView[m].findViewById(R.id.vw_r_margin);

            //뷰노출 초기화
            ViewUtils.showViews(itemView[m]);
            ViewUtils.hideViews(mVwRMargin);

            //메인섬네일 로딩
            ImageUtil.loadImage(mContext, deliveryList.mainImgUrl, mIvMainThumbnail, R.drawable.icon_prd_default);

            setObject(m);

            //초기화시 모든 텍스트뷰 숨기고 값이 있는 경우만 노출
            for (int i = 0; i < objList.length; i++) {
                ViewUtils.hideViews((TextView) objList[i][0], (TextView) objList[i][1], (TextView) objList[i][2], (LinearLayout) objList[i][7]);
            }

            //텍스트 세팅
            for (int i = 0; i < deliveryList.deliveryInfoList.size(); i++) {
                //최대값 초과한 내용은 처리 안함
                if (i >= DELIVERY_TEXT_MAX_LINE) {
                    break;
                }

                //일반 텍스트 정보
                List<NativeProductV2.TxtInfo> txtInfoList = deliveryList.deliveryInfoList.get(i).textList;
                //도착확률 정보
                List<NativeProductV2.PrsnlTxtInfoList> pctInfoList = deliveryList.deliveryInfoList.get(i).percentTextList;

                if (isEmpty(txtInfoList) && isEmpty(pctInfoList)) {
                    continue;
                }

                //새로 추가되는 항목을 먼저 체크하기로 아이폰 담당자와 협의함
                if (isNotEmpty(pctInfoList)) {
                    //도착확률 정보가 존재하는 경우
                    setProbLineInfo((LinearLayout)objList[i][7], pctInfoList);
                } else {
                    setCommonLineInfo(txtInfoList, i);
                    String preImgUrl = deliveryList.deliveryInfoList.get(i).preImgUrl;
                    if (isNotEmpty(preImgUrl)) {
                        //ImageUtil.loadImagePostResult(mContext, preImgUrl, (ImageView) objList[i][6], 0, HD, i);
                        ImageUtil.loadImageBadge(mContext, preImgUrl, (ImageView) objList[i][6], 0, HD, true);
                        ViewUtils.showViews((ImageView) objList[i][6]);
                    }
                }
            }

            ViewUtils.hideViews(mLlChangeAddr, mLlMore);
            if (isNotEmpty(deliveryList.changeAddress) && isNotEmpty(deliveryList.changeAddress.get(0))) {
                //배송지 변경
                ViewUtils.showViews(mLlChangeAddr, mVwRMargin);

                //오른쪽 마진 조정 (배송지변경이 노출될때와 그외 경우 마진이 달라짐)
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mLlTxtArea.getLayoutParams();
                params.rightMargin = getResources().getDimensionPixelSize(R.dimen.prd_delivery_r_margin);
                mLlTxtArea.setLayoutParams(params);

                setTextInfo(mTvChangeAddr, deliveryList.changeAddress.get(0), null,  null, true, false);
                //툴팁 노출
                if ("Y".equalsIgnoreCase(deliveryList.addressTooltip)) {
                    mIvTooltipClose.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ViewUtils.hideViews(mFlTooltip);
                        }
                    });
                }
            } else {
                //더보기 링크
                if (isNotEmpty(deliveryList.addInfoUrl)) {
                    ViewUtils.showViews(mLlMore);

                    itemView[m].setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((ProductDetailWebActivityV2) mContext).goToLink(deliveryList.addInfoUrl);

                            ProductDetailWebActivityV2.prdAmpSend(AMPEnum.AMP_PRD_deliveryInfo_addressTooltip);
                        }
                    });
                }
            }
        }

        setTooltip();
    }

    /**
     * 배송정보를 세팅한다.
     *
     * @param txtInfoList 한 라인에 표시될 텍스트 배열
     * @param i 서버에서 내려준 라인 수
     */
    private void setCommonLineInfo(List<NativeProductV2.TxtInfo> txtInfoList, int i) {
        if (txtInfoList.size() == 1) {
            setTextInfo((TextView) objList[i][0], txtInfoList.get(0), (ImageView) objList[i][3], (LinearLayout)objList[i][7], false, true);
        } else if (txtInfoList.size() == 2) {
            setTextInfo((TextView) objList[i][0], txtInfoList.get(0), (ImageView) objList[i][3], (LinearLayout)objList[i][7], false, false);
            setTextInfo((TextView) objList[i][1], txtInfoList.get(1), (ImageView) objList[i][4], (LinearLayout)objList[i][7], false, true);
        } else if (txtInfoList.size() >= 3) {
            setTextInfo((TextView) objList[i][0], txtInfoList.get(0), (ImageView) objList[i][3], (LinearLayout)objList[i][7], false, false);
            setTextInfo((TextView) objList[i][1], txtInfoList.get(1), (ImageView) objList[i][4], (LinearLayout)objList[i][7], false, false);
            setTextInfo((TextView) objList[i][2], txtInfoList.get(2), (ImageView) objList[i][5], (LinearLayout)objList[i][7], false, true);
        }
    }

    /**
     * 도착확률 정보를 세팅한다.
     * -루트뷰 초기화 후 확률정보 UI xml을 인플레이트하여 루트뷰에 추가함
     *
     * @param parent 한 라인의 루트뷰
     * @param txtInfoList 라인에 표시할 정보
     */
    private void setProbLineInfo(LinearLayout parent, List<NativeProductV2.PrsnlTxtInfoList> txtInfoList) {
        //초기화
        parent.removeAllViews();
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.setBackgroundResource(R.drawable.bg_eeeeee_round4);

        //간격조정
        //사각박스 내부 여백
        int paddingHor = DisplayUtils.convertDpToPx(mContext, 12f);
        int paddingVer = DisplayUtils.convertDpToPx(mContext, 8f);
        //사각박스 외부 여백
        int marginTop = DisplayUtils.convertDpToPx(mContext, 2f);
        int marginBottom = DisplayUtils.convertDpToPx(mContext, 4f);
        int marginRight = DisplayUtils.convertDpToPx(mContext, 8f);
        parent.setPadding(paddingHor, paddingVer, paddingHor, paddingVer);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) parent.getLayoutParams();
        lp.setMargins(lp.leftMargin, lp.topMargin + marginTop,
                lp.rightMargin + marginRight, lp.bottomMargin + marginBottom);
        parent.setLayoutParams(lp);

        //줄간격 세팅
        parent.setDividerDrawable(mContext.getDrawable(R.drawable.divider_1_tp));
        parent.setShowDividers(SHOW_DIVIDER_MIDDLE);

        //라인만큼 뷰를 생성 후 추가함
        for (int i = 0; i < txtInfoList.size(); i++) {
            View probView = LayoutInflater.from(mContext).inflate(R.layout.product_detail_delivery_area_item_prob, null);
            //도착확률
            TextView tvProbTitle = probView.findViewById(R.id.tv_prob_title);
            //퍼센트
            TextView tvProbPercent = probView.findViewById(R.id.tv_prob_percent);

            List<NativeProductV2.TxtInfo> leftList = txtInfoList.get(i).leftItem;
            List<NativeProductV2.TxtInfo> rightList = txtInfoList.get(i).rightItem;

            if (isNotEmpty(leftList) && isNotEmpty(rightList)
                    && isNotEmpty(leftList.get(0).textValue) && isNotEmpty(rightList.get(0).textValue)) {
                setTextInfoList(tvProbTitle, leftList, parent);
                setTextInfoList(tvProbPercent, rightList, parent);
                parent.addView(probView);
            }
        }
    }

    public void hideTopLine() {
        ((View)findViewById(R.id.view_top_line)).setVisibility(View.GONE);
    }

    private void setRightMarginZero(TextView tv) {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tv.getLayoutParams();
        lp.setMargins(lp.leftMargin, lp.topMargin, 0, lp.bottomMargin);
        tv.setLayoutParams(lp);
    }

    /**
     * 하나의 텍스트뷰에 배열로 들어온 텍스트 리스트를 세팅한다.
     *
     * @param tv           세팅할 텍스트뷰
     * @param txtInfoList      API에서 전달받은 정보
     * @param parent       한 라인의 루트뷰
     */
    private void setTextInfoList(TextView tv, List<NativeProductV2.TxtInfo> txtInfoList, LinearLayout parent) {
        if (isEmpty(txtInfoList)) {
            return;
        }

        SpannableStringBuilder sb = new SpannableStringBuilder();
        for (int i = 0; i < txtInfoList.size(); i++) {
            NativeProductV2.TxtInfo txtInfo = txtInfoList.get(i);
            if (isEmpty(txtInfo)) {
                continue;
            }
            String txtValue = txtInfo.textValue + " ";
            int txtLen = txtValue.length();

            AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(Integer.parseInt(txtInfo.size), true);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(DisplayUtils.parseColor(txtInfo.textColor));
            StyleSpan boldSpan =  new StyleSpan(Typeface.BOLD);

            sb.append(txtValue);
            if (isNotEmpty(txtInfo.size)) {
                sb.setSpan(sizeSpan, sb.length() - txtLen, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (isNotEmpty(txtInfo.textColor)) {
                sb.setSpan(colorSpan, sb.length() - txtLen, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if ("Y".equalsIgnoreCase(txtInfo.boldYN)) {
                sb.setSpan(boldSpan, sb.length() - txtLen, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        if (isNotEmpty(sb)) {
            sb = (SpannableStringBuilder) sb.subSequence(0, sb.length() - 1);
            tv.setText(sb);
            ViewUtils.showViews(tv, parent);
        }
    }

    /**
     * 텍스트뷰의 속성을 세팅한다.
     *
     * @param tv           세팅할 텍스트뷰
     * @param txtInfo      API에서 전달받은 정보
     * @param arrow        텍스트 뒤에 ">" 이미지 추가
     * @param parent       한 라인의 루트뷰
     * @param needEncoding IceBerg 인코딩이 필요한지 여부
     * @param isLast       라인의 마지막 항목인지 여부
     */
    private void setTextInfo(TextView tv, NativeProductV2.TxtInfo txtInfo,
                             ImageView arrow, LinearLayout parent, boolean needEncoding, boolean isLast) {
        if (isEmpty(txtInfo) || isEmpty(txtInfo.textValue)) {
            return;
        }

        if (isNotEmpty(txtInfo.linkUrl) && isNotEmpty(arrow)) {
            ViewUtils.showViews(arrow);
            //이미지 있는 경우 오른쪽 마진 0 (텍스트와 이미지 여백없이 붙이기 위해)
            setRightMarginZero(tv);
            arrow.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    tv.performClick();
                }
            });
        } else if (isEmpty(txtInfo.linkUrl) && isLast) {
            //이미지 없고 라인의 마지막 항목인 경우 오른쪽 마진 0 (불필요 마진 제거)
            setRightMarginZero(tv);
        }
        //LeadingMarginSpan 이상현상 개선용
        txtInfo.textValue = txtInfo.textValue.trim().replace(" ", "\u00A0");
        tv.setText(txtInfo.textValue);
        ViewUtils.showViews(tv, parent);

        if (isNotEmpty(txtInfo.boldYN)) {
            tv.setTypeface(null, "Y".equalsIgnoreCase(txtInfo.boldYN) ? Typeface.BOLD : Typeface.NORMAL);
        }
        if (isNotEmpty(txtInfo.size)) {
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Float.parseFloat(txtInfo.size));
        }
        tv.setTextColor(DisplayUtils.parseColor(txtInfo.textColor));

        if (isNotEmpty(txtInfo.linkUrl)) {
            tv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProductDetailWebActivityV2 activity = (ProductDetailWebActivityV2) mContext;
                    String linkUrl = activity.getIntent().getStringExtra(Keys.INTENT.WEB_URL);
                    if (needEncoding) {
                        //배송지변경 주소는 아이스버그 인코딩이 필요하다 하여 적용
                        linkUrl = IceBergEncoder.encode(linkUrl);
                    }
                    activity.goToLinkWithParam(txtInfo.linkUrl, Arrays.asList(linkUrl));

                    //모든 링크가 있으면 해당 텍스트를 액션명으로 던진다.
                    if( txtInfo.textValue != null )
                        ProductDetailWebActivityV2.prdAmpSend(txtInfo.textValue);
                }
            });
        }
    }

    /**
     * 뷰 바인딩
     */
    private void initViews() {
        //아이템뷰
        itemView[0] = findViewById(ITEM_RES_IDS[0]);
        itemView[1] = findViewById(ITEM_RES_IDS[1]);

        //메인텍스트뷰
        mTvMain[0] = itemView[0].findViewById(R.id.tv_first1);
        mTvMain[1] = itemView[1].findViewById(R.id.tv_first1);

        //툴팁
        mIvTooltipClose = findViewById(R.id.iv_tooltip_close);
        mFlTooltip = findViewById(R.id.fl_tooltip);
    }

    /**
     * 툴팁영역을 세팅한다.
     * (운영 중 요청에 의해 현재는 비노출 상태)
     */
    private void setTooltip() {
        //두영역 중 1개라도 "Y"이면 툴팁 노출
        boolean hideTooltip = true;

        //두번째 영역에 툴팁 노출되는 경우 위치조정 필요
        boolean changePosition = false;

        //툴립 노출, 비노출 처리
        if (hideTooltip) {
            ViewUtils.hideViews(mFlTooltip);
        } else {
            //비노출 (https://jira.gsenext.com/browse/SQA-954)
            ViewUtils.hideViews(mFlTooltip);
        }

        //툴팁 위치변경이 필요한 경우
        if (changePosition) {
            getRootView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    getRootView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int offset = DisplayUtils.convertDpToPx(mContext, 6);
                    int topMargin = itemView[0].getHeight() + itemView[1].findViewById(R.id.ll_change_addr).getHeight();
                    mFlTooltip.setY(topMargin + offset);
                }
            });
        }
    }

    /**
     * xml 컴포넌트를 오브젝트로 매핑한다.
     *
     * @param m 배송영역은 상하 2단으로 구성됨 (0:상단, 1:하단)
     */
    private void setObject(int m) {
        objList[0][0] = itemView[m].findViewById(R.id.tv_first1);
        objList[0][1] = itemView[m].findViewById(R.id.tv_first2);
        objList[0][2] = itemView[m].findViewById(R.id.tv_first3);
        objList[0][3] = itemView[m].findViewById(R.id.iv_first1);
        objList[0][4] = itemView[m].findViewById(R.id.iv_first2);
        objList[0][5] = itemView[m].findViewById(R.id.iv_first3);
        objList[0][6] = itemView[m].findViewById(R.id.iv_pre_first1);
        objList[0][7] = itemView[m].findViewById(R.id.li_first);
        objList[1][0] = itemView[m].findViewById(R.id.tv_second1);
        objList[1][1] = itemView[m].findViewById(R.id.tv_second2);
        objList[1][2] = itemView[m].findViewById(R.id.tv_second3);
        objList[1][3] = itemView[m].findViewById(R.id.iv_second1);
        objList[1][4] = itemView[m].findViewById(R.id.iv_second2);
        objList[1][5] = itemView[m].findViewById(R.id.iv_second3);
        objList[1][6] = itemView[m].findViewById(R.id.iv_pre_second1);
        objList[1][7] = itemView[m].findViewById(R.id.li_second);
        objList[2][0] = itemView[m].findViewById(R.id.tv_third1);
        objList[2][1] = itemView[m].findViewById(R.id.tv_third2);
        objList[2][2] = itemView[m].findViewById(R.id.tv_third3);
        objList[2][3] = itemView[m].findViewById(R.id.iv_third1);
        objList[2][4] = itemView[m].findViewById(R.id.iv_third2);
        objList[2][5] = itemView[m].findViewById(R.id.iv_third3);
        objList[2][6] = itemView[m].findViewById(R.id.iv_pre_third1);
        objList[2][7] = itemView[m].findViewById(R.id.li_third);
        objList[3][0] = itemView[m].findViewById(R.id.tv_fourth1);
        objList[3][1] = itemView[m].findViewById(R.id.tv_fourth2);
        objList[3][2] = itemView[m].findViewById(R.id.tv_fourth3);
        objList[3][3] = itemView[m].findViewById(R.id.iv_fourth1);
        objList[3][4] = itemView[m].findViewById(R.id.iv_fourth2);
        objList[3][5] = itemView[m].findViewById(R.id.iv_fourth3);
        objList[3][6] = itemView[m].findViewById(R.id.iv_pre_fourth1);
        objList[3][7] = itemView[m].findViewById(R.id.li_fourth);
        objList[4][0] = itemView[m].findViewById(R.id.tv_fifth1);
        objList[4][1] = itemView[m].findViewById(R.id.tv_fifth2);
        objList[4][2] = itemView[m].findViewById(R.id.tv_fifth3);
        objList[4][3] = itemView[m].findViewById(R.id.iv_fifth1);
        objList[4][4] = itemView[m].findViewById(R.id.iv_fifth2);
        objList[4][5] = itemView[m].findViewById(R.id.iv_fifth3);
        objList[4][6] = itemView[m].findViewById(R.id.iv_pre_fifth1);
        objList[4][7] = itemView[m].findViewById(R.id.li_fifth);
        objList[5][0] = itemView[m].findViewById(R.id.tv_sixth1);
        objList[5][1] = itemView[m].findViewById(R.id.tv_sixth2);
        objList[5][2] = itemView[m].findViewById(R.id.tv_sixth3);
        objList[5][3] = itemView[m].findViewById(R.id.iv_sixth1);
        objList[5][4] = itemView[m].findViewById(R.id.iv_sixth2);
        objList[5][5] = itemView[m].findViewById(R.id.iv_sixth3);
        objList[5][6] = itemView[m].findViewById(R.id.iv_pre_sixth1);
        objList[5][7] = itemView[m].findViewById(R.id.li_sixth);
        objList[6][0] = itemView[m].findViewById(R.id.tv_seventh1);
        objList[6][1] = itemView[m].findViewById(R.id.tv_seventh2);
        objList[6][2] = itemView[m].findViewById(R.id.tv_seventh3);
        objList[6][3] = itemView[m].findViewById(R.id.iv_seventh1);
        objList[6][4] = itemView[m].findViewById(R.id.iv_seventh2);
        objList[6][5] = itemView[m].findViewById(R.id.iv_seventh3);
        objList[6][6] = itemView[m].findViewById(R.id.iv_pre_seventh1);
        objList[6][7] = itemView[m].findViewById(R.id.li_seventh);
        objList[7][0] = itemView[m].findViewById(R.id.tv_eighth1);
        objList[7][1] = itemView[m].findViewById(R.id.tv_eighth2);
        objList[7][2] = itemView[m].findViewById(R.id.tv_eighth3);
        objList[7][3] = itemView[m].findViewById(R.id.iv_eighth1);
        objList[7][4] = itemView[m].findViewById(R.id.iv_eighth2);
        objList[7][5] = itemView[m].findViewById(R.id.iv_eighth3);
        objList[7][6] = itemView[m].findViewById(R.id.iv_pre_eighth1);
        objList[7][7] = itemView[m].findViewById(R.id.li_eighth);
        objList[8][0] = itemView[m].findViewById(R.id.tv_ninth1);
        objList[8][1] = itemView[m].findViewById(R.id.tv_ninth2);
        objList[8][2] = itemView[m].findViewById(R.id.tv_ninth3);
        objList[8][3] = itemView[m].findViewById(R.id.iv_ninth1);
        objList[8][4] = itemView[m].findViewById(R.id.iv_ninth2);
        objList[8][5] = itemView[m].findViewById(R.id.iv_ninth3);
        objList[8][6] = itemView[m].findViewById(R.id.iv_pre_ninth1);
        objList[8][7] = itemView[m].findViewById(R.id.li_ninth);
    }
}


