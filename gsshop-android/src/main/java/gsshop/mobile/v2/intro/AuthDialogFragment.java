package gsshop.mobile.v2.intro;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import gsshop.mobile.v2.R;

/**
 * 접근권한 안내 팝업
 */
public class AuthDialogFragment extends DialogFragment {

    /**
     * 콜백용 인터페이스 정의
     */
    public interface OnAuthConfirmListener {
        void onAuthConfirmed();
        void onAuthAfterShow();
    }

    //콜백용 리스너
    OnAuthConfirmListener authListener;

    /**
     * 인스턴스 생성
     *
     * @return AuthDialogFragment
     */
    public static AuthDialogFragment newInstance() {
        AuthDialogFragment fragment = new AuthDialogFragment();
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
        WindowManager.LayoutParams windowParams = getDialog().getWindow().getAttributes();
        windowParams.dimAmount = 0.8f;
        windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        getDialog().getWindow().setAttributes(windowParams);
        View rootView = inflater.inflate(R.layout.fragment_auth_dialog, container, false);

        ImageView imgAuth = (ImageView) rootView.findViewById(R.id.auth_img);
        if (Build.VERSION.SDK_INT >= 23) {
            imgAuth.setImageResource(R.drawable.auth_guide_type1);
        } else {
            imgAuth.setImageResource(R.drawable.auth_guide_type2);
        }

        rootView.findViewById(R.id.btnConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authListener.onAuthConfirmed();
                dismiss();
            }
        });

        rootView.findViewById(R.id.btnConfirm).requestFocus();
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        authListener = (OnAuthConfirmListener) activity;
    }

    /**
     * 하드웨어 백키 클릭시 콜백
     *
     * @param dialog DialogInterface
     */
    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        dismiss();
    }
}
