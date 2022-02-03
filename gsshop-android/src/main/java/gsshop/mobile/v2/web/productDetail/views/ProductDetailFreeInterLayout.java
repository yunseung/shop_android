package gsshop.mobile.v2.web.productDetail.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gsshop.mocha.ui.util.ViewUtils;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.support.gtm.AMPEnum;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.productDetail.ProductDetailWebActivityV2;
import gsshop.mobile.v2.web.productDetail.dto.NativeProductV2;

import static com.blankj.utilcode.util.EmptyUtils.isEmpty;
import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;

/**
 * 단품 무이자 할부 레이아웃.
 */
public class ProductDetailFreeInterLayout extends LinearLayout {
    /**
     * 최대 텍스트 배열 수
     */
    private static final int FREE_INTER_TEXT_MAX_NUM = 3;

    /**
     * 컨텍스트
     */
    private Context mContext;

    /**
     * 더보기
     */
    private LinearLayout mLlFreeInterRoot;

    /**
     * 섬네일 이미지
     */
    private ImageView mIvFreeInterThumbnail;

    /**
     * 타이틀
     */
    private TextView mTvFreeInterTitle1;
    private TextView mTvFreeInterTitle2;
    private TextView mTvFreeInterTitle3;

    /**
     * 더보기
     */
    private LinearLayout mLlFreeInterMore;

    public ProductDetailFreeInterLayout(Context context) {
        super(context);

        mContext = context;
        initLayout();
    }

    public ProductDetailFreeInterLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initLayout();
    }

    public ProductDetailFreeInterLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initLayout();
    }

    /**
     * 레이아웃을 인플레이트한다.
     */
    private void initLayout() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.product_detail_free_inter_area, this, true);
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
     * API에서 수신한 데이타로 뷰를 세팅한다.
     *
     * @param freeInterInfo NativeProductV2.Component
     */
    public void setProductDetailFreeInterArea(NativeProductV2.Component freeInterInfo) {
        if (isEmpty(freeInterInfo)) {
            //로고이미지나 타이틀이 없으면 비노출
            mLlFreeInterRoot.setVisibility(View.GONE);
            return;
        }

        //이미지 로딩
        ImageUtil.loadImage(mContext, freeInterInfo.iConImgUrl, mIvFreeInterThumbnail, R.drawable.icon_discount_rate);

        //텍스트뷰 변수저장
        Object[] objTxt = new Object[3];
        objTxt[0] = mTvFreeInterTitle1;
        objTxt[1] = mTvFreeInterTitle2;
        objTxt[2] = mTvFreeInterTitle3;

        //초기화시 모든 텍스트뷰 숨기고 값이 있는 경우만 노출
        for (int i = 0; i < objTxt.length; i++) {
            ViewUtils.hideViews((TextView) objTxt[i]);
        }

        //텍스트 세팅
        for (int i = 0; i < freeInterInfo.interestTxt.size(); i++) {
            //최대 3개만 처리
            if (i >= FREE_INTER_TEXT_MAX_NUM) {
                break;
            }
            setTextInfo((TextView) objTxt[i], freeInterInfo.interestTxt.get(i));
        }

        //더보기 버튼 디폴트 비노출
        ViewUtils.hideViews(mLlFreeInterMore);

        //더보기 버튼 클릭이벤트 세팅
        if (isNotEmpty(freeInterInfo.addInfoUrl)) {
            ViewUtils.showViews(mLlFreeInterMore);
            mLlFreeInterRoot.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ProductDetailWebActivityV2) mContext).goToLink(freeInterInfo.addInfoUrl);

                    ProductDetailWebActivityV2.prdAmpSend(AMPEnum.AMP_PRD_NOINTERESTINFO_INTERESTTXT);
                }
            });

        }
    }

    public void hideTopLine() {
        ((View)findViewById(R.id.view_top_line)).setVisibility(View.GONE);
    }

    /**
     * 텍스트뷰의 속성을 세팅한다.
     *
     * @param tv      세팅할 텍스트뷰
     * @param txtInfo API에서 전달받은 정보
     */
    private void setTextInfo(TextView tv, NativeProductV2.TxtInfo txtInfo) {
        if (isEmpty(txtInfo)) {
            return;
        }
        ViewUtils.showViews(tv);
        if (isNotEmpty(txtInfo.textValue)) {
            tv.setText(txtInfo.textValue);
        }
        if (isNotEmpty(txtInfo.boldYN)) {
            tv.setTypeface(null, "Y".equalsIgnoreCase(txtInfo.boldYN) ? Typeface.BOLD : Typeface.NORMAL);
        }
        if (isNotEmpty(txtInfo.size)) {
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Float.parseFloat(txtInfo.size));
        }
        tv.setTextColor(DisplayUtils.parseColor(txtInfo.textColor));
    }

    /**
     * 뷰 바인딩
     */
    private void initViews() {
        mLlFreeInterRoot = findViewById(R.id.ll_free_inter_root);
        mIvFreeInterThumbnail = findViewById(R.id.iv_free_inter_thumbnail);
        mTvFreeInterTitle1 = findViewById(R.id.tv_free_inter_title1);
        mTvFreeInterTitle2 = findViewById(R.id.tv_free_inter_title2);
        mTvFreeInterTitle3 = findViewById(R.id.tv_free_inter_title3);
        mLlFreeInterMore = findViewById(R.id.ll_free_inter_more);
    }
}


