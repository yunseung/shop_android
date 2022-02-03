package gsshop.mobile.v2.web.productDetail.views;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import gsshop.mobile.v2.Events;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.support.gtm.AMPEnum;
import gsshop.mobile.v2.util.ClickUtils;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.productDetail.ProductDetailWebActivityV2;
import gsshop.mobile.v2.web.productDetail.dto.NativeProductV2;

public class ProductDetailBroadcastLayout extends LinearLayout {
    private Context mContext;
    private Activity mActivity;

    private LinearLayout mLlBroadcastArea;                      // 방송 정보 표시 영역
    private ImageView mIvBroadcastType;                         // 방송 종류 (라이브, 마이샵 등)
    private TextView mTvBroadcastState;
    private LinearLayout mLlNotiBroadcastClickArea;             // 방송알림 버튼 영역
    private ImageView mIvBroadcastNoti;                          // 방송알림 체크박스

    public ProductDetailBroadcastLayout(Context context) {
        super(context);
        this.mContext = (ProductDetailWebActivityV2) context;
        initLayout();
    }

    public ProductDetailBroadcastLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = (ProductDetailWebActivityV2) context;
        initLayout();
    }

    private void initLayout() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.product_detail_broadcast_layout, this, true);
        EventBus.getDefault().register(this);
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
     * 타이틀영역 세팅한다.
     *
     * @param component
     */
    public void setProductDetailBroadcastArea(Activity activity, NativeProductV2.Component component) {
        mActivity = activity;
        mLlBroadcastArea.setVisibility(View.VISIBLE);
        if ("Y".equalsIgnoreCase(component.tvAlarmBtnFlg)) {
            mLlNotiBroadcastClickArea.setVisibility(View.VISIBLE);
        } else {
            mLlNotiBroadcastClickArea.setVisibility(View.GONE);
        }
        ImageUtil.loadImage(mContext, component.broadChannelImgUrl, mIvBroadcastType, 0);
        ((ProductDetailWebActivityV2)mContext).setTextInfoList(mTvBroadcastState, component.broadText);
        if (component.applyBroadAlamYN != null) {
            mIvBroadcastNoti.setSelected("Y".equalsIgnoreCase(component.applyBroadAlamYN));
        }
        setBroadAlert(component);
    }


    private void setBroadAlert(NativeProductV2.Component component) {
        mLlNotiBroadcastClickArea.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //중복클릭 방지
                if (ClickUtils.work(1000)) {
                    return;
                }

                if (!((ProductDetailWebActivityV2)mContext).isLoggedIn()) {
                    ((ProductDetailWebActivityV2)mContext).goToLogin(mContext);
                    return;
                }

                String scriptName = component.runUrl;
                List params = new ArrayList();
                if (mIvBroadcastNoti.isSelected()) {
                    params.add("off");
                } else {
                    params.add("on");
                }
                ((ProductDetailWebActivityV2)mContext).goToLinkWithParam(scriptName, params);

                ProductDetailWebActivityV2.prdAmpSend(AMPEnum.AMP_PRD_broadInfo_runUrl);

                //방송알림버튼 효율(등록/해제상태 상관없이 누를때마다 보낸다)
                if(((ProductDetailWebActivityV2) mContext).getIsDeal()){
                    //딜
                    ((ProductDetailWebActivityV2)mContext).setWiseLogHttpClient(ServerUrls.WEB.MSEQ_DEAL_NATIVE_ALARM);
                }else{
                    //단품
                    ((ProductDetailWebActivityV2)mContext).setWiseLogHttpClient(ServerUrls.WEB.MSEQ_PRD_NATIVE_ALARM);
                }
            }
        });
    }

    public void onEvent(Events.EventProductDetail.BroadAlertEvent event) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mIvBroadcastNoti.setSelected(event.isOn);
            }
        });
    }

    private void initViews() {
        mLlBroadcastArea = findViewById(R.id.ll_broadcast_area);
        mIvBroadcastType = findViewById(R.id.iv_broadcast_type);
        mTvBroadcastState = findViewById(R.id.tv_broadcast_state);
        mLlNotiBroadcastClickArea = findViewById(R.id.ll_noti_broadcast_click_area);
        mIvBroadcastNoti = findViewById(R.id.iv_broadcast_notification);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }
}
