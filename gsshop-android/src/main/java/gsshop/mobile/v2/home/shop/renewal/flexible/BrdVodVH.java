package gsshop.mobile.v2.home.shop.renewal.flexible;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.home.main.SectionContentList;
import gsshop.mobile.v2.home.main.TvLiveBanner;
import gsshop.mobile.v2.home.shop.ShopInfo;
import gsshop.mobile.v2.home.shop.schedule.model.DirectOrdInfo;
import gsshop.mobile.v2.util.DisplayUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.util.StringUtils;
import gsshop.mobile.v2.web.WebUtils;
import roboguice.util.Ln;

import static com.blankj.utilcode.util.EmptyUtils.isEmpty;
import static com.blankj.utilcode.util.EmptyUtils.isNotEmpty;
import static gsshop.mobile.v2.util.StringUtils.trim;

/**
 * 내일TV 비디오뷰홀더 V2
 */
public class BrdVodVH extends RenewalPriceInfoBottomViewHolder {

    /**
     * 루트뷰
     */
    private View lay_root;

    /**
     * 방송시간
     */
    private TextView txt_broad_time;

    /**
     * pgm 레이아웃
     */
    private View lay_pgm;

    /**
     * pgm 문구
     */
    private TextView txt_pgm;

    /**
     * 방송종류 (live or myshop)
     */
    private ImageView img_brd_kind;

    /**
     * 상품이미지
     */
    private ImageView img_main_sumnail;

    /**
     * 재생시간
     */
    private View lay_cv_remain_time;
    private TextView txt_remain_time;

    /**
     * 방송시간 패턴
     */
    private static final String BROAD_TIME_PATTERN = "\\d{2}:\\d{2}";

    /**
     * 캐시할 이미지 주소
     */
    private String mImgForCache;

    /**
     * 생성자
     *
     * @param itemView 레이아웃뷰
     */
    public BrdVodVH(View itemView) {
        super(itemView);

        broadComponentType = BroadComponentType.vod;

        lay_root = itemView.findViewById(R.id.lay_root);

        txt_broad_time = itemView.findViewById(R.id.txt_broad_time);
        lay_pgm = itemView.findViewById(R.id.lay_pgm);
        txt_pgm = itemView.findViewById(R.id.txt_pgm);

        img_brd_kind = itemView.findViewById(R.id.img_brd_kind);

        //상품 이미지
        img_main_sumnail = itemView.findViewById(R.id.img_main_sumnail);

        lay_cv_remain_time = itemView.findViewById(R.id.lay_cv_remain_time);
        txt_remain_time = itemView.findViewById(R.id.txt_remain_time);

        DisplayUtils.resizeHeightAtViewToScreenSize(MainApplication.getAppContext(), img_main_sumnail);
    }

    @Override
    public void onBindViewHolder(Context context, int position, final ShopInfo shopInfo, String action, String label, String sectionName) {
        this.context = context;
        this.mInfo = shopInfo;
        this.navigationId = shopInfo.naviId;

        //상품정보 : content.subProductList.get(0)
        //PGM정보 : content.subProductList.get(1)
        SectionContentList content = shopInfo.contents.get(position).sectionContent;

        if (isEmpty(content) || isEmpty(content.subProductList)) {
            lay_root.setVisibility(View.GONE);
            return;
        }

        mImgForCache = content.imageUrl;

        SectionContentList prdItem = content.subProductList.get(0);


        //가격표시용 공통모듈에 맞게 데이타 변경
        TvLiveBanner tvLiveBanner = new TvLiveBanner();
        tvLiveBanner.rProductName = prdItem.productName;
        tvLiveBanner.rSalePrice = prdItem.salePrice;
        tvLiveBanner.rBasePrice = prdItem.basePrice;
        tvLiveBanner.rExposePriceText = prdItem.exposePriceText;
        tvLiveBanner.rLinkUrl = prdItem.linkUrl;
        tvLiveBanner.rDiscountRate = prdItem.discountRate;
        if (isNotEmpty(prdItem.directOrdInfo)) {
            tvLiveBanner.rDirectOrdInfo = new DirectOrdInfo();
            tvLiveBanner.rDirectOrdInfo.text = prdItem.directOrdInfo.text;
            tvLiveBanner.rDirectOrdInfo.linkUrl = prdItem.directOrdInfo.linkUrl;
        }


        tvLiveBanner.rDiscountRate = prdItem.discountRate;

        //렌탈 관련
        //상품 구조체에는
        tvLiveBanner.rRentalText = prdItem.rentalText;
        tvLiveBanner.rRentalPrice = prdItem.mnlyRentCostVal;
        tvLiveBanner.rProductType = prdItem.productType;//;"true".equals(prdItem.isRental) ? "R" : "P";

        //혜택 관련
        tvLiveBanner.rSource = prdItem.source;
        tvLiveBanner.rAllBenefit = prdItem.allBenefit;

        //솔드 아웃 관련 방송중 구매가능관련
        //생방송에 현재는 없다. 하지만 편성표에는 있다. 내일 TV에 있나?
        tvLiveBanner.rImageLayerFlag = prdItem.imageLayerFlag;

        //상품평 관련
        tvLiveBanner.rAddTextLeft = prdItem.addTextLeft;
        tvLiveBanner.rAddTextRight = prdItem.addTextRight;

        //브렌드 관련
        tvLiveBanner.rBrandNm = prdItem.brandNm;

        // 딜 여부 (true/false)
        tvLiveBanner.deal = prdItem.deal;

        super.bindViewHolder(tvLiveBanner, position,null);

        //방송시간 (내용 중 hh:mm 형태가 있으면 bold 처리)
        String broadTime = prdItem.promotionName;
        if (isNotEmpty(broadTime)) {
            Pattern pattern = Pattern.compile(BROAD_TIME_PATTERN);
            Matcher matcher = pattern.matcher(broadTime);
            SpannableStringBuilder sb = new SpannableStringBuilder(broadTime);
            if (matcher.find()) {
                int startPos = matcher.start();
                int endPos = startPos + matcher.group().length();
                StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
                sb.setSpan(styleSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            txt_broad_time.setText(sb);
        } else {
            txt_broad_time.setText(null);
        }

        //방송시간 우측 PGM 텍스트
        lay_pgm.setVisibility(View.GONE);
        if (content.subProductList.size() >= 2 && isNotEmpty(content.subProductList.get(1))
                && isNotEmpty(content.subProductList.get(1).productName)) {
            SectionContentList pgmItem = content.subProductList.get(1);
            txt_pgm.setText(pgmItem.productName);
            lay_pgm.setVisibility(View.VISIBLE);
            lay_pgm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WebUtils.goWeb(context, pgmItem.linkUrl);
                }
            });
        }

        //방송종류 이미지
        if ("LIVE".equalsIgnoreCase(content.cateGb)) {
            img_brd_kind.setImageResource(R.drawable.tit_live_android);
        } else if ("DATA".equalsIgnoreCase(content.cateGb)){
            img_brd_kind.setImageResource(R.drawable.tit_myshop_android);
        } else {
            img_brd_kind.setVisibility(View.GONE);
        }

        // 방송 스크린샷 이미지
        ImageUtil.loadImageTvLive(context.getApplicationContext(),
                !TextUtils.isEmpty(content.imageUrl) ? content.imageUrl.trim() : "", img_main_sumnail, R.drawable.noimg_tv);

        //재생시간 영역
        lay_cv_remain_time.setVisibility(View.GONE);
        if (isNotEmpty(content.videoTime)) {
            txt_remain_time.setText(content.videoTime);
            lay_cv_remain_time.setVisibility(View.VISIBLE);
        }

        // 상세화면 이동
        lay_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(Keys.INTENT.IMAGE_URL, isNotEmpty(mImgForCache) ? mImgForCache.replace(IMG_CACHE_RPL_FROM, IMG_CACHE_RPL_TO) : "");
                intent.putExtra(Keys.INTENT.HAS_VOD, content.hasVod);
                WebUtils.goWeb(context, prdItem.linkUrl, intent);
            }
        });

        // 재생버튼 클릭시 상세화면 이동 (자동재생 플래그 추가)
        if (isNotEmpty(prdItem.playUrl)) {
            lay_cv_remain_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String linkUrl = StringUtils.addUriParameter(Uri.parse(prdItem.playUrl),
                            Keys.PARAM.VOD_PLAY, "VOD").toString();
                    WebUtils.goWeb(context, linkUrl);
                }
            });
        }

        boolean isSameToNext = false;
        if (shopInfo.contents.size() > position + 1) {
            final SectionContentList nextItem = shopInfo.contents.get(position + 1).sectionContent;
            if (nextItem != null && !TextUtils.isEmpty(nextItem.viewType) &&
                    content != null &&  !TextUtils.isEmpty(content.viewType) &&
                    content.viewType.equals(nextItem.viewType)) {
                isSameToNext = true;
            }
        }

        View viewBottomDivider1dp = itemView.findViewById(R.id.view_bottom_divider_1dp);
        View viewBottomDivider10dp = itemView.findViewById(R.id.view_bottom_divider_10dp);

        if (isSameToNext) {
            viewBottomDivider1dp.setVisibility(View.VISIBLE);
            viewBottomDivider10dp.setVisibility(View.GONE);
        }
        else {
            viewBottomDivider1dp.setVisibility(View.GONE);
            viewBottomDivider10dp.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 이미지가 화면상에 노출된 경우에 한해 이미지 캐시작업을 수행한다.
     *
     * @param event ImageCacheStartEvent
     */
    public void onEvent(Events.ImageCacheStartEvent event) {
        if (isNotEmpty(navigationId) && !navigationId.equals(event.naviId)) {
            //내가 속한 매장에 대한 이벤트가 아니면 스킵
            return;
        }
        if (DisplayUtils.isVisible(img_main_sumnail)) {
            if (isNotEmpty(mImgForCache) && mImgForCache.contains(IMG_CACHE_RPL_FROM)) {
                String imgUrl = mImgForCache.replace(IMG_CACHE_RPL_FROM, IMG_CACHE_RPL_TO);
                Glide.with(context).load(trim(imgUrl)).downloadOnly(IMG_CACHE_WIDTH, IMG_CACHE_HEIGHT);
            }
        }
    }

    @Override
    public void onViewAttachedToWindow() {
        super.onViewAttachedToWindow();
        try {
            EventBus.getDefault().register(this);
        }catch(Exception e){
            Ln.e(e);
        }
    }

    @Override
    public void onViewDetachedFromWindow() {
        super.onViewDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }
}
