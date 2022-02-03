package gsshop.mobile.v2.home.shop.flexible.shoppinglive;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import gsshop.mobile.v2.AbstractBaseActivity;
import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import gsshop.mobile.v2.util.ImageUtil;
import roboguice.util.Ln;

import static gsshop.mobile.v2.util.ImageUtil.BaseImageResolution.HD;

/**
 * 모바일라이브 탭매장 신설
 * 방송알림 해제 팝업
 */
public class MobileLiveAlarmCancelDialogFragment extends DialogFragment {

    private ImageView imgThumbUrl;
    private Button buttonCancel;
    private Button buttonOk;

    private String mStrNumber;
    private String mStrThumbUrl;

    private String mCaller;

    public static String MOBILELIVE_DELETE = "MOBILELIVE_DELETE"; //해제 확인 누를때 AlarmRegistMLEvent 에서 구분하기위한 caller

    public MobileLiveAlarmCancelDialogFragment() {
    }

    /**
     * 모바일라이브 방송알림 해제 팝업
     * @return
     */
    public static MobileLiveAlarmCancelDialogFragment newInstance() { //String thumbUrl, String[] alarmPeriod, String prdId
        MobileLiveAlarmCancelDialogFragment fragment = new MobileLiveAlarmCancelDialogFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        //팝업 외부영역 반투명 설정
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Translucent_NoTitleBar);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //배경 30% 딤처리
        WindowManager.LayoutParams windowParams = getDialog().getWindow().getAttributes();
        windowParams.dimAmount = 0.3f;
        windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        getDialog().getWindow().setAttributes(windowParams);

        return inflater.inflate(R.layout.fragment_mobilelive_cancel_alarm_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imgThumbUrl = view.findViewById(R.id.img_mobile_live);
        buttonOk = view.findViewById(R.id.btn_mobile_live_broad_alarm_submit);
        buttonCancel = view.findViewById(R.id.btn_mobile_live_broad_alarm_cancel);

        //loadImageResizeToHeight 사용시 이미지가 표시 안되는 현상이 있어 우선 loadImageBadge 사용
        ImageUtil.loadImageBadge(getContext(), mStrThumbUrl, imgThumbUrl, R.drawable.no_img_mobilelive, HD);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                //효율코드
                try {
                    ((AbstractBaseActivity) getContext()).setWiseLogHttpClient(ServerUrls.WEB.MOBILE_LIVE_ALARM_DELETE);
                    //취소에 대한 Controller 호출(caller = MOBILELIVE_DELETE로 전달)
                    ((AbstractBaseActivity) getContext()).onMLRegister(mCaller, MOBILELIVE_DELETE, mStrNumber, null, false);
                }
                catch (ClassCastException | NullPointerException e) {
                    Ln.e(e.getMessage());
                }
            }
        });
    }

    public MobileLiveAlarmCancelDialogFragment setData(String caller, String thumbUrl, String[] alarmPeriod, String prdId) {
        mCaller = caller;
        mStrNumber = prdId;
        mStrThumbUrl = thumbUrl;
        return this;
    }
}
