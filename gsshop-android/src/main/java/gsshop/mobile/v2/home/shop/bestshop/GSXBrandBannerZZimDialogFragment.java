package gsshop.mobile.v2.home.shop.bestshop;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gsshop.mocha.ui.util.ViewUtils;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.web.WebUtils;

/**
 * gs x brand 찜 하트 표시 dialog
 */
public class GSXBrandBannerZZimDialogFragment extends DialogFragment {
    private static final String ARG_GS_X_BRAND_ZZIM = "_arg_gs_x_brand_zzim";
    private static final String ARG_GS_X_BRAND_LINK_URL = "_arg_gs_x_brand_link_url";

    /**
     * zzim on/off
     */
    private boolean isZZim;

    /**
     * 찜한 브랜드 보기 링크
     */
    private String linkUrl;

    private View closeView;
    private View zzimView;
    private ImageView zzimImage;
    private View unZZimView;

    public static GSXBrandBannerZZimDialogFragment newInstance(@NonNull boolean isZZim, @Nullable String linkUrl) {
        GSXBrandBannerZZimDialogFragment fragment = new GSXBrandBannerZZimDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_GS_X_BRAND_ZZIM, isZZim);
        args.putString(ARG_GS_X_BRAND_LINK_URL, linkUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        //팝업 외부영역 반투명 설정
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Translucent_NoTitleBar);
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isZZim = getArguments().getBoolean(ARG_GS_X_BRAND_ZZIM);
            linkUrl = getArguments().getString(ARG_GS_X_BRAND_LINK_URL);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gs_x_brand_zzim_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        closeView = view.findViewById(R.id.image_gs_x_brand_zzim_close);
        zzimView = view.findViewById(R.id.view_gs_x_brand_zzim);
        zzimImage = view.findViewById(R.id.image_gs_x_brand_zzim);
        unZZimView = view.findViewById(R.id.image_gs_x_brand_unzzim);

        if (isZZim) {
            /**
             * 찜하기
             */
            ViewUtils.showViews(zzimView);
            ViewUtils.hideViews(unZZimView);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                AnimatorSet animator = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.zzim_scale);
                animator.setTarget(zzimView);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        hideZZimView();
                    }
                });
                animator.start();
            } else {
                hideZZimView();
            }

        } else {
            /**
             * 찜 취소
             */
            ViewUtils.hideViews(zzimView);
            ViewUtils.showViews(unZZimView);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                AnimatorSet animator = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.zzim_scale);
                animator.setTarget(unZZimView);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        hideUnZZimView();
                    }
                });
                animator.start();
            } else {
                hideUnZZimView();
            }
        }

        closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        zzimImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebUtils.goWeb(getContext(), linkUrl);
                dismiss();
            }
        });
    }

    /**
     * 찜 선택 후 노출된 팝업레이어를 숨긴다.
     */
    private void hideZZimView() {
        new Handler().postDelayed(() -> dismiss(), 3200);
    }

    /**
     * 찜 해제 후 노출된 팝업레이어를 숨긴다.
     */
    private void hideUnZZimView() {
        new Handler().postDelayed(() -> dismiss(), 600);
    }
}
