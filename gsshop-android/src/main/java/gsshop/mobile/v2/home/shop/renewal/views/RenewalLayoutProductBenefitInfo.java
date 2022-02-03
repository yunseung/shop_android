package gsshop.mobile.v2.home.shop.renewal.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gsshop.mocha.ui.util.ViewUtils;

import org.springframework.http.HttpEntity;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.home.HomeActivity;
import gsshop.mobile.v2.home.main.ImageBadge;
import gsshop.mobile.v2.home.shop.renewal.utils.SetDtoUtil;
import gsshop.mobile.v2.home.shop.renewal.utils.dto.ProductInfo;
import gsshop.mobile.v2.home.shop.schedule.TVScheduleShopFragment;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;
import static gsshop.mobile.v2.ServerUrls.WEB.RN_SCH_VOD_ALRAM;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleShopFragment.makeFormData;
import static gsshop.mobile.v2.home.shop.schedule.TVScheduleShopFragment.scheduleBroadType;
import static gsshop.mobile.v2.home.shop.schedule.viewholder.TLineBaseViewHolder.AIR_BUY;
import static gsshop.mobile.v2.home.shop.schedule.viewholder.TLineBaseViewHolder.SALE_END;
import static gsshop.mobile.v2.home.shop.schedule.viewholder.TLineBaseViewHolder.SOLD_OUT;
import static gsshop.mobile.v2.home.shop.schedule.viewholder.TLineBaseViewHolder.TO_SALE;

/**
 * 리뉴얼 되는 상품정보 레이아웃 (공통으로 쓰기 위해 레이아웃 상속받아 선언)
 */
public class RenewalLayoutProductBenefitInfo extends LinearLayout {

    private Context mContext;

    private final static String BENEFITS_SEPERATOR_LINE = " | ";
    private final static String BENEFITS_SEPERATOR_DOT = " · ";
    private final static String COLOR_BENEFITS_DEFAULT = "80111111";
    private final static String STYLETYPE_BOLD = "BOLD";

    protected LinearLayout tag_container; // 혜택영역 & 버튼영역 포함하는 전체
    protected TextViewWithListenerWhenOnDraw txt_benefit_container; // TV쇼핑 | 무료배송, 무이자

    protected LinearLayout lay_button_container;
    protected LinearLayout lay_live_talk; //라이브톡

    // 알람
    protected LinearLayout lay_alarm;
    protected View alarm_on;
    protected View alarm_off;

    protected TextView txt_buy; //바로구매

    //1단 640 하단조정을 위한 더미뷰
    private View dummy_benefit_short; //4dp짜리 더미뷰
    private View dummy_benefit_long; //8dp짜리 더미뷰

    private SpannableStringBuilder mStrBuilderSourcing = new SpannableStringBuilder(); //[TV쇼핑], [백화점]
    private SpannableStringBuilder mStrBuilderFooter = new SpannableStringBuilder(); //무료배송, 무이자

    /**
     * 기본 생성자
     * @param context
     */
    public RenewalLayoutProductBenefitInfo(Context context) {
        this(context, null);
    }

    public RenewalLayoutProductBenefitInfo(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RenewalLayoutProductBenefitInfo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initLayout();
    }


    /**
     * 최초 inflate 될 layout을 설정, inflate 되는 놈이 바뀌면 상속 받아서 super 호출 하지 말고 상속받는놈이 다른놈을 inflate ?
     */
    protected void initLayout() {
        LayoutInflater inflater =(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.renewal_layout_product_benefit_info, this, true);
    }

    /**
     * inflate 된 후에 호출
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initViews();
    }

    /**
     * 최초 이닛할 수 있는 뷰를 init
     */
    protected void initViews() {
        // 이 컨테이너 (최상위 뷰)
        tag_container = findViewById(R.id.tag_container);
        // 혜택 뷰
        txt_benefit_container = (TextViewWithListenerWhenOnDraw) findViewById(R.id.txt_benefit_container);
        //버튼 컨테이너 뷰
        lay_button_container = findViewById(R.id.lay_button_container);
        // 라이브톡 뷰
        lay_live_talk = findViewById(R.id.lay_live_talk);

        //알람
        lay_alarm = findViewById(R.id.lay_alarm);
        alarm_on = findViewById(R.id.alarm_on);
        alarm_off = findViewById(R.id.alarm_off);

        //바로구매
        txt_buy = findViewById(R.id.txt_buy);

        //1단 640 하단조정을 위한 더미뷰
        dummy_benefit_short = findViewById(R.id.dummy_benefit_short);
        dummy_benefit_long = findViewById(R.id.dummy_benefit_long);
    }

    /**
     * 뷰 설정 하는 함수
     * @param info 데이터
     */
    public void setViews(ProductInfo info) {
        setViews(info, SetDtoUtil.BroadComponentType.product);
    }

    /**
     * 뷰를 설정하는 함수
     * @param info 보여줄 데이터
     * @param brdComponentType 타입 설정
     */
    public void setViews(ProductInfo info, SetDtoUtil.BroadComponentType brdComponentType) {
        final SetDtoUtil.BroadComponentType broadComponentType;
        if (SetDtoUtil.BroadComponentType.product_640.equals(brdComponentType) ||
                SetDtoUtil.BroadComponentType.product_c_b1.equals(brdComponentType)) {
            txt_benefit_container.setMaxLines(1);
            broadComponentType = SetDtoUtil.BroadComponentType.product;
        } else if (SetDtoUtil.BroadComponentType.signature.equals(brdComponentType)) {
            txt_benefit_container.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            LayoutParams params = (LayoutParams) tag_container.getLayoutParams();
            params.topMargin = DisplayUtils.convertDpToPx(mContext, 7);
            params.rightMargin = DisplayUtils.convertDpToPx(mContext, 15);
            tag_container.setLayoutParams(params);
            txt_benefit_container.setLineSpacing(DisplayUtils.convertDpToPx(mContext, 2), 1.0f);
            broadComponentType = SetDtoUtil.BroadComponentType.product;
        } else if (SetDtoUtil.BroadComponentType.mobilelive_prd_list_type1.equals(brdComponentType)) {
            //화면작게 or 글자작게 등 설정시 구매하기 버튼이 일부 노출되는 현상 개선
            LayoutParams params = (LayoutParams) lay_button_container.getLayoutParams();
            params.topMargin = DisplayUtils.convertDpToPx(mContext, 50);
            lay_button_container.setLayoutParams(params);
            broadComponentType = brdComponentType;
        } else if (SetDtoUtil.BroadComponentType.mobilelive_prd_list_type2.equals(brdComponentType)) {
            //혜택 영역 디자인 변경
            //txt_benefit_container.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            LayoutParams params = (LayoutParams) tag_container.getLayoutParams();
            params.topMargin = DisplayUtils.convertDpToPx(mContext, 3);
            //params.rightMargin = DisplayUtils.convertDpToPx(mContext, 15);
            tag_container.setLayoutParams(params);
            txt_benefit_container.setLineSpacing(DisplayUtils.convertDpToPx(mContext, 2), 1.0f);
            //구매하기 영역 디자인 변경
            txt_buy.setMinWidth(DisplayUtils.convertDpToPx(mContext, 76));
            int paddingHor = DisplayUtils.convertDpToPx(mContext, 8);
            LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, DisplayUtils.convertDpToPx(mContext, 26));
            txt_buy.setPadding(paddingHor, 0, paddingHor, 0);
            txt_buy.setLayoutParams(lp);
            txt_buy.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            broadComponentType = brdComponentType;
        } else {
            broadComponentType = brdComponentType;
        }

        try {
            txt_benefit_container.setText("");

            //상품정보 혜택 표시 영역 ( 소싱 포함 )
            txt_benefit_container.setVisibility(View.INVISIBLE);//혜택영역(무료배송·무이자·적립금), GONE시키면 구매하기 버튼 위치 바뀐다.

            mStrBuilderFooter.clear();
            mStrBuilderSourcing.clear();
        }
        catch (NullPointerException e) {
            Ln.e(e.getMessage());
        }

        // 상품 타입, 버튼 유형 및 링크 존재하면 설정.
        if(!SetDtoUtil.BroadComponentType.product.equals(broadComponentType)
                && info.directOrdInfo != null
                && info.linkUrl != null && !info.linkUrl.isEmpty() ) {

//            txt_buy null 체크 추가.
            if (txt_buy != null) {
                txt_buy.setVisibility(View.VISIBLE);
                txt_buy.setBackgroundResource(R.drawable.renewal_bg_buy);

                //구매하기 버튼 유효 하다 && 실제 구매하기 버튼에 텍스트가 잘못되었을까봐 예외처리 && else 서버에서 준 텍스트 뿌려(상담, 구매, 예약??)
                if (info.directOrdInfo.text != null && info.directOrdInfo.text.isEmpty()) {
                    txt_buy.setText(R.string.layer_flag_buy);
                } else {
                    txt_buy.setText(info.directOrdInfo.text);
                }

                //일단 구매하기 처리는 했으나, rImageLayerFlag 우선순으로 엎어친다
                //구매하기 버튼이 유효한 경우 구매하기 버튼에, 아닌 경우 이미지 레이아웃에 표시한다.
                if (info.imageLayerFlag != null && DisplayUtils.isValidString(info.imageLayerFlag)) {
                    if (SOLD_OUT.equalsIgnoreCase(info.imageLayerFlag)) {
                        //구매하기버튼에 "일시품절" 표시한다.
                        txt_buy.setText(R.string.layer_flag_sold_out);
                        txt_buy.setVisibility(View.VISIBLE);
                        txt_buy.setBackgroundResource(R.drawable.renewal_bg_air_buy);

                    }
                    else if (AIR_BUY.equalsIgnoreCase(info.imageLayerFlag)) {
                        //구매하기버튼에 방송중 구매가능 표시한다.
                        txt_buy.setText(R.string.layer_flag_air_buy);
                        txt_buy.setVisibility(View.VISIBLE);
                        //버튼 회색 처리
                        txt_buy.setBackgroundResource(R.drawable.renewal_bg_air_buy);
                    }
                    else if (TO_SALE.equalsIgnoreCase(info.imageLayerFlag)) {
                        //구매하기버튼에 방송중 구매가능 표시한다.
                        txt_buy.setText(R.string.layer_flag_to_sale);
                        txt_buy.setVisibility(View.VISIBLE);
                        //버튼 회색 처리
                        txt_buy.setBackgroundResource(R.drawable.renewal_bg_air_buy);
                    }
                    else if (SALE_END.equalsIgnoreCase(info.imageLayerFlag)) {
                        //구매하기버튼에 방송중 구매가능 표시한다.
                        txt_buy.setText(R.string.layer_flag_sale_end);
                        txt_buy.setVisibility(View.VISIBLE);
                        //버튼 회색 처리
                        txt_buy.setBackgroundResource(R.drawable.renewal_bg_air_buy);
                    }
                    txt_buy.setOnClickListener(null);
                }

                //버튼종류(구매하기, 일시품정 등)에 관계없이 아래순서로 링크 적용
                //우선순위 1.info.rDirectOrdInfo.linkUrl, 2.info.rLinkUrl
                txt_buy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (SetDtoUtil.BroadComponentType.mobilelive_prd_list_type2.equals(brdComponentType)) {
                            //모바일라이브 구매하기 클릭액션은 MobileLivePrdDialogFragment에서 처리
                            return;
                        }
                        WebUtils.goWeb(mContext, isNotEmpty(info.directOrdInfo.linkUrl) ?
                                DisplayUtils.getFullUrl(info.directOrdInfo.linkUrl) : info.linkUrl);
                    }
                });
            }
        } else {//버튼이 없을때(일시품절, 방송중 구매가능)

            if (txt_buy != null) {
                txt_buy.setOnClickListener(null);
            }

            if(info.imageLayerFlag != null && DisplayUtils.isValidString(info.imageLayerFlag))
            {
                if(SOLD_OUT.equalsIgnoreCase(info.imageLayerFlag)){
                    // 품절이면서 상품 타입이 아니면
                    if(!SetDtoUtil.BroadComponentType.product.equals(broadComponentType)) {
                        //구매하기버튼에 "일시품절" 표시한다.
                        if (isNotEmpty(txt_buy)) {
                            txt_buy.setText(R.string.layer_flag_sold_out);
                            txt_buy.setVisibility(View.VISIBLE);
                            txt_buy.setBackgroundResource(R.drawable.renewal_bg_air_buy);
                        }
                    }
                }else if(AIR_BUY.equalsIgnoreCase(info.imageLayerFlag)){

                    if(!SetDtoUtil.BroadComponentType.product.equals(broadComponentType)) {
                        //구매하기버튼에 방송중 구매가능 표시한다.
                        if (isNotEmpty(txt_buy)) {
                            txt_buy.setText(R.string.layer_flag_air_buy);
                            txt_buy.setVisibility(View.VISIBLE);
                            //버튼 회색 처리
                            txt_buy.setBackgroundResource(R.drawable.renewal_bg_air_buy);
                        }
                    }
                }else if(TO_SALE.equalsIgnoreCase(info.imageLayerFlag)){

                    if(!SetDtoUtil.BroadComponentType.product.equals(broadComponentType)) {
                        //구매하기버튼에 방송중 구매가능 표시한다.
                        if (isNotEmpty(txt_buy)) {
                            txt_buy.setText(R.string.layer_flag_to_sale);
                            txt_buy.setVisibility(View.VISIBLE);
                            //버튼 회색 처리
                            txt_buy.setBackgroundResource(R.drawable.renewal_bg_air_buy);
                        }
                    }
                }else if(SALE_END.equalsIgnoreCase(info.imageLayerFlag)){

                    if(!SetDtoUtil.BroadComponentType.product.equals(broadComponentType)) {
                        //구매하기버튼에 방송중 구매가능 표시한다.
                        if (isNotEmpty(txt_buy)) {
                            txt_buy.setText(R.string.layer_flag_sale_end);
                            txt_buy.setVisibility(View.VISIBLE);
                            //버튼 회색 처리
                            txt_buy.setBackgroundResource(R.drawable.renewal_bg_air_buy);
                        }
                    }
                }
                else{
                    //directOrdInfo에 링크Url이 없으면서 imageLayerFlag ="" 비어있는경우
                    if (isNotEmpty(txt_buy)) {
                        txt_buy.setVisibility(View.GONE);
                    }
                }
            }
            else{
                //directOrdInfo에 링크Url이 없으면서 imageLayerFlag ="" 비어있는경우
                if (isNotEmpty(txt_buy)) {
                    txt_buy.setVisibility(View.GONE);
                }
            }
        }

        // 라이브톡 버튼 노출여부
        // 라이브톡 노출시 방송알림 영역은 종만 표시되고, 미노출시 종+텍스트 표시됨
        if (info.liveTalkYn != null && "Y".equals(info.liveTalkYn) ) {
            if (isNotEmpty(lay_live_talk)) {
                lay_live_talk.setVisibility(View.VISIBLE);
                lay_live_talk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        WebUtils.goWeb(mContext, info.liveTalkUrl, ((Activity) mContext).getIntent());

                        //라이브톡 버튼 효율 코드
                        if (broadComponentType == SetDtoUtil.BroadComponentType.live) {
                            //생방송
                            ((HomeActivity) mContext).setWiseLogHttpClient(ServerUrls.WEB.RN_BESTSHOP_LIVE_TALK);
                        } else if (broadComponentType == SetDtoUtil.BroadComponentType.schedule) {
                            //편성표
                            ((HomeActivity) mContext).setWiseLogHttpClient(ServerUrls.WEB.RN_SCH_LIVE_TALK);
                        }
                    }
                });
            }
        } else {
            if (isNotEmpty(lay_live_talk)) {
                lay_live_talk.setVisibility(View.GONE);
            }
        }

        //방송알림
        //rBroadAlarmDoneYn -> 알람버튼 보이냐 안보이냐가 아닌 // 알람버튼이 VISIBLE이지만 눌린상태냐 아니냐
        if ("Y".equals(info.broadAlarmDoneYn)) {
            alarm_off.setVisibility(View.GONE);
            alarm_on.setVisibility(View.VISIBLE);
            lay_alarm.setVisibility(View.VISIBLE);
        } else if ("N".equals(info.broadAlarmDoneYn)) {
            alarm_off.setVisibility(View.VISIBLE);
            alarm_on.setVisibility(View.GONE);
            lay_alarm.setVisibility(View.VISIBLE);
        } else {
            //"Y", "N" 이외 값은 알람영역 숨김
            lay_alarm.setVisibility(View.GONE);
        }

        //방송알림
        lay_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "";
                String type = "";
                String mseqUrl= "";
                HttpEntity<Object> requestEntity = makeFormData(info.prdId, info.exposPrdName, null, null);

                // 방송 알림 등록 / 취소 효율 코드
                if ("Y".equals(info.broadAlarmDoneYn)) {
                    //방송알림 취소
                    url = ServerUrls.getHttpRoot() + ServerUrls.REST.TV_SCHEDULE_ALARM_DELETE;
                    type = "delete";
                } else if ("N".equals(info.broadAlarmDoneYn)) {
                    //방송알림 설정 팝업
                    url = ServerUrls.getHttpRoot() + ServerUrls.REST.TV_SCHEDULE_ALARM_QUERY;
                    type = "query";
                }
                new TVScheduleShopFragment.BroadAlarmUpdateController(mContext).execute(url, requestEntity, type);

                String wiselogUrl;
                if ("Y".equalsIgnoreCase(info.pgmLiveYn)) {
                    //생방송 (live or data)
                    wiselogUrl = scheduleBroadType == TVScheduleShopFragment.ScheduleBroadType.LIVE ?
                            ServerUrls.WEB.RN_SCH_LIVE_ALRAM : ServerUrls.WEB.RN_SCH_DATA_ALRAM;
                } else {
                    //VOD
                    wiselogUrl = RN_SCH_VOD_ALRAM;
                }
                ((HomeActivity) mContext).setWiseLogHttpClient(wiselogUrl);
            }
        });

        //모든 버튼이 GONE이면 감싸는 영역도 GONE
        try {
            if (txt_buy.getVisibility() == View.GONE && lay_alarm.getVisibility() == View.GONE &&
                    lay_live_talk.getVisibility() == View.GONE) {
                lay_button_container.setVisibility(View.GONE);
            }
        }
        catch (NullPointerException e) {
            Ln.e(e.getMessage());
        }

        //릴레이캐시, 무료배송, 무이자 (혜택영역)
        if (info.allBenefit != null && !info.allBenefit.isEmpty()) {
            int index = 0;
            for (ImageBadge badge : info.allBenefit) {
                if (badge != null && badge.text != null && !badge.text.isEmpty()) {
                    if (mStrBuilderFooter.length() > 0) {
                        SpannableStringBuilder dot_stringBuilder = new SpannableStringBuilder();
                        dot_stringBuilder.append(BENEFITS_SEPERATOR_DOT);

                        //benefit_seperator_dot 색상 적용
                        dot_stringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#4c111111")), 0, BENEFITS_SEPERATOR_DOT.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        mStrBuilderFooter.append(dot_stringBuilder);
                    }
                    mStrBuilderFooter.append(badge.text);
                }
                index++;
            }
        }

        /**
         * 소싱(TV쇼핑, 백화점) & 혜택
         */

        //SetDtoUtil.BroadComponentType 이 product 일때와 tv_new (TV 혜택) 일때만  소싱 append 하도록!
        // 이경우 TV 상품이 안나오면 되기때문에 info.rSource.text 를 빈값으로.
        if((!SetDtoUtil.BroadComponentType.product.equals(broadComponentType)
                && !SetDtoUtil.BroadComponentType.tv_new.equals(broadComponentType)
                && !SetDtoUtil.BroadComponentType.mobilelive_prd_list_type1.equals(broadComponentType)
                && !SetDtoUtil.BroadComponentType.mobilelive_prd_list_type2.equals(broadComponentType))
                && info.source != null){
            info.source.text = "";
        }


        if (mStrBuilderFooter.length() <= 0 && (info.source == null || info.source.text.isEmpty() || info.source.text == null)) {
            if (txt_benefit_container != null) {
                txt_benefit_container.setVisibility(View.INVISIBLE);
            }
        }
        else {
            if (info.source != null && (!info.source.text.isEmpty() || info.source.text == null)) {

                //소싱에 대한 컬러 확인 디파인 필요
                String sourceColor = COLOR_BENEFITS_DEFAULT;
                //유효하면 변경
                if (info.source.type != null && !info.source.type.isEmpty())
                    sourceColor = info.source.type;

                mStrBuilderSourcing.append(info.source.text);

                // styleTpye 확인하여 BOLD처리
                if (STYLETYPE_BOLD.equals(info.source.styleType)) {
                    mStrBuilderSourcing.setSpan(new StyleSpan(Typeface.BOLD), 0, info.source.text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                mStrBuilderSourcing.setSpan(new ForegroundColorSpan(Color.parseColor("#" + sourceColor)), 0, info.source.text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                ViewUtils.showViews(tag_container, txt_benefit_container);
                if (txt_benefit_container != null) {
                    txt_benefit_container.setText(mStrBuilderSourcing);
                }

                if (mStrBuilderFooter.length() > 0) {
                    //benefit_seperator_line 색상 지정
                    SpannableStringBuilder seperator_stringBuilder = new SpannableStringBuilder();
                    seperator_stringBuilder.append(BENEFITS_SEPERATOR_LINE);
                    seperator_stringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#33111111")), 0, BENEFITS_SEPERATOR_LINE.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    mStrBuilderSourcing.append(seperator_stringBuilder);
                }
            }
            //broadComponentType이 product 아닌경우 소싱을 append 하지 않는다!
            if (mStrBuilderFooter.length() > 0) {
                ViewUtils.showViews(tag_container, txt_benefit_container);

                mStrBuilderSourcing.append(mStrBuilderFooter);

                String strText = mStrBuilderSourcing.toString();
                for (ImageBadge badge : info.allBenefit) {
                    int index = strText.indexOf(badge.text);
                    if (index >= 0 && !TextUtils.isEmpty(badge.type)) {

                        try {
                            mStrBuilderSourcing.setSpan(new ForegroundColorSpan(Color.parseColor("#" + badge.type)),
                                    index, index + badge.text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        catch (IllegalArgumentException e) {
                            Ln.e(e.getMessage());
                        }
                        catch (IndexOutOfBoundsException e) {
                            Ln.e(e.getMessage());
                        }
                    }

                    if (STYLETYPE_BOLD.equals(badge.styleType)) {
                        try {
                            mStrBuilderSourcing.setSpan(new StyleSpan(Typeface.BOLD), index, index + badge.text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        catch (IllegalArgumentException e) {
                            Ln.e(e.getMessage());
                        }
                        catch (IndexOutOfBoundsException e) {
                            Ln.e(e.getMessage());
                        }
                    }
                }
                if (txt_benefit_container != null) {
                    txt_benefit_container.setText(mStrBuilderSourcing);
                }
            }
        }

        // 모든 버튼, 혜택 들이 없으면 tag_container 안보이게끔 한다.
        if (txt_benefit_container.getVisibility() != VISIBLE && lay_button_container.getVisibility() != VISIBLE) {
            tag_container.setVisibility(GONE);
        }

        /**
         * 중간 점 처리하는 부분 / UI스레드 사용
         */
        final ProductInfo tempinfo = info;
        final TextViewWithListenerWhenOnDraw tempTextView = txt_benefit_container;

        if (tempTextView != null) {
            tempTextView.setOnDrawListener(new TextViewWithListenerWhenOnDraw.OnDrawListener() {
                public void onDraw(View view) {
                    int lineCnt = tempTextView.getLineCount();
                    if (lineCnt > 1) {
                        String full_text = tempTextView.getText().toString();
                        //str0 = 0번째 줄의 텍스트
                        String str0 = full_text.substring(tempTextView.getLayout().getLineStart(0), tempTextView.getLayout().getLineEnd(0));
                        //str1 = 1번째 줄의 텍스트
                        String str1 = full_text.substring(tempTextView.getLayout().getLineStart(1), tempTextView.getLayout().getLineEnd(1));

                        //                  새로 그리는 타이밍에서 타이밍 이슈가 발생해서 두번 줄바꿈 하는 문제가 발생하는 것으로 보임 이에 \n이 있을 경우 줄바꿈 로직 수행하지 않음.
                        if (str0.contains("\n") || str1.contains("\n")) {
                            return;
                        }

                        //******************
                        // 점 삭제하는 부분
                        //******************
                        if (str1.startsWith(" · ")) {
                            str1 = str1.substring(3, str1.length());
                        } else if (str1.startsWith("· ")) {
                            str1 = str1.substring(2, str1.length());
                        } else {
                            if (str0.endsWith("· ")) {
                                str0 = str0.substring(0, str0.length() - 3);
                            } else if (str0.endsWith("·")) {
                                str0 = str0.substring(0, str0.length() - 2);
                            } else {
                                String tempStr = "";
                                int num = str0.length() - 2;
                                for (; num > 0; num--) {
                                    tempStr = str0.substring(num, num + 2);
                                    if (!TextUtils.isEmpty(tempStr) &&
                                            ("· ".equals(tempStr) || " ·".equals(tempStr))) {
                                        try {
                                            String tempStr2 = str0.substring(0, num);
                                            str1 = str0.substring(num + 2, str0.length()) + str1;
                                            str0 = tempStr2;
                                        } catch (IndexOutOfBoundsException e) {
                                            Ln.e(e.getMessage());
                                        }
                                        break;
                                    }
                                }
                            }
                        }

                        // 가로가 짧아 줄바꿈이 한번 더 이루어질 경우를 대비하여 두번쨰 줄도 삭제
                        if (str1.endsWith("· ")) {
                            str1 = str1.substring(0, str1.length() - 3);
                        } else if (str1.endsWith("·")) {
                            str1 = str1.substring(0, str1.length() - 2);
                        }

                        tempTextView.setText(str0 + "\n" + str1);

                        SpannableStringBuilder sourcingStringBuilder2 = new SpannableStringBuilder();
                        String strText = tempTextView.getText().toString();
                        sourcingStringBuilder2.append(strText);

                        //점처리 이후 소싱부분  Bold처리
                        if (tempinfo.source != null && !tempinfo.source.text.isEmpty()) {

                            //소싱에 대한 컬러 확인 디파인 해야 될것 같은디 ;;
                            String sourceColor = "80111111";
                            //유효하면 변경
                            if (tempinfo.source.type != null && !tempinfo.source.type.isEmpty())
                                sourceColor = tempinfo.source.type;

                            if (SetDtoUtil.BroadComponentType.product.equals(broadComponentType) || SetDtoUtil.BroadComponentType.tv_new.equals(broadComponentType)) {
                                // 볼드이면 볼드로
                                if (STYLETYPE_BOLD.equals(tempinfo.source.styleType)) {
                                    sourcingStringBuilder2.setSpan(new StyleSpan(Typeface.BOLD), 0, tempinfo.source.text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                }
                                sourcingStringBuilder2.setSpan(new ForegroundColorSpan(Color.parseColor("#" + sourceColor)), 0, tempinfo.source.text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                //benefit_seperator_line 색상 지정
                                sourcingStringBuilder2.setSpan(new ForegroundColorSpan(Color.parseColor("#33111111")), tempinfo.source.text.length() + 1, tempinfo.source.text.length() + 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        }
                        if (tempinfo.allBenefit != null && tempinfo.allBenefit.size() > 0) {
                            for (ImageBadge badge : tempinfo.allBenefit) {
                                int index = strText.indexOf(badge.text);
                                if (index >= 0 && !TextUtils.isEmpty(badge.type)) {
                                    try {
                                        sourcingStringBuilder2.setSpan(new ForegroundColorSpan(Color.parseColor("#" + badge.type)), index, index + badge.text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        //benefit_seperator_dot 색상 적용
                                        sourcingStringBuilder2.setSpan(new ForegroundColorSpan(Color.parseColor("#4c111111")), index + badge.text.length() + 1, index + badge.text.length() + 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    } catch (IllegalArgumentException e) {
                                        Ln.e(e.getMessage());
                                    } catch (IndexOutOfBoundsException e) {
                                        Ln.e(e.getMessage());
                                    }
                                }
                                if (STYLETYPE_BOLD.equals(badge.styleType)) {
                                    try {
                                        sourcingStringBuilder2.setSpan(new StyleSpan(Typeface.BOLD), index, index + badge.text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    }
                                    catch (IllegalArgumentException e) {
                                        Ln.e(e.getMessage());
                                    }
                                    catch (IndexOutOfBoundsException e) {
                                        Ln.e(e.getMessage());
                                    }
                                }
                            }
                        }
                        // 무조건 setText 해도 된다. 여기 까지 온거는 이미 줄바꿈이 일어 난 것이기 때문에.
                        tempTextView.setText(sourcingStringBuilder2);
                    }
                }
            });
        }
    }
}
