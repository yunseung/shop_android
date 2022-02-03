package gsshop.mobile.v2.home.shop.nalbang;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import gsshop.mobile.v2.R;
import roboguice.util.Ln;

/**
 * 숏방 가이드 화면
 */
public class SBGuideDialogFragment extends DialogFragment {

    /**
     * 콜백용 인터페이스 정의
     */
    public interface OnGuideConfirmListener {
        void onGuideConfirmed();
        void onGuideDontShow();
    }

    //콜백용 리스너
    OnGuideConfirmListener guideListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //팝업 외부영역 반투명 설정
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Translucent_NoTitleBar);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        WindowManager.LayoutParams windowParams = getDialog().getWindow().getAttributes();
        windowParams.dimAmount = 0.8f;
        windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        getDialog().getWindow().setAttributes(windowParams);

        View rootView = inflater.inflate(R.layout.fragment_sb_guide_dialog, container, false);

        rootView.findViewById(R.id.btnStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guideListener.onGuideConfirmed();
                dismiss();
            }
        });

        rootView.findViewById(R.id.btnDontShow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guideListener.onGuideDontShow();
                dismiss();
            }
        });

        rootView.findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guideListener.onGuideConfirmed();
                dismiss();
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            guideListener = (OnGuideConfirmListener) activity;
        } catch (ClassCastException e) {
            guideListener = null;
            Ln.e(e);
        }
    }

    /**
     * 하드웨어 백키 클릭시 콜백
     *
     * @param dialog DialogInterface
     */
    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        guideListener.onGuideConfirmed();
        dismiss();
    }
}
