package gsshop.mobile.v2.home.shop;

//**

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 기본 view holder
 */
public abstract class BaseViewHolder extends RecyclerView.ViewHolder {
    protected final int C_TYPE = 100;
    protected final int L_TYPE = 200;
    protected final int C_TYPE_PRD_CB1_VIP = 101;

    /**
     * 네비게이션 아이디
     */
    protected String navigationId = "";

    /**
     * 캐싱사이즈 가로 크기
     */
    public static final int IMG_CACHE_WIDTH = 550;

    /**
     * 캐싱사이즈 세로 크기
     */
    public static final int IMG_CACHE_HEIGHT = 550;

    /**
     * 교체할 대상 문자열 (이미지캐싱 용도)
     */
    public static final String IMG_CACHE_RPL_FROM = "_B1";

    /**
     * 교체할 대상 문자열 (이미지캐싱 용도) - 지금BEST 좌우 두개 이미지 타입
     */
    public static final String IMG_CACHE_RPL2_FROM = "_O1";

    /**
     * 교체할 대상 문자열 (이미지캐싱 용도) - 캐러셀 타입
     */
    public static final String IMG_CACHE_RPL3_FROM = "_R1";

    /**
     * 교체할 문자열 (이미지캐싱 용도)
     */
    public static final String IMG_CACHE_RPL_TO = "_L1";

    /**
     * 좌우 스와이프 허용 각도 확대여부
     */
    public boolean increaseSwipeAngle = false;

    /**
     * 해당 뷰가 extension 할 뷰가 있을 수 있어 변수 생성
     */
    protected boolean isExtension = false;
    
    /**
     * @param itemView
     */
    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public void onBindViewHolder(Context context, int position, ShopInfo info,
                                 String action, String label, String sectionName) {
    }

    public void onViewAttachedToWindow() {
    }

    public void onViewDetachedFromWindow() {
    }

    /**
     * 헤더 여부 설정
     * @param isHeader
     */
    public void setIsHeader(boolean isHeader) {
    }

    /**
     * 앵커 형태의 홀더가 이동을 위해 section Code를 가지고 있음.
     * @return
     */
    public String getSectionCode() {
        return null;
    }

    public void setOnTouchUp() {}

    public void setExtension(boolean extension) {
        isExtension = extension;
    }

}