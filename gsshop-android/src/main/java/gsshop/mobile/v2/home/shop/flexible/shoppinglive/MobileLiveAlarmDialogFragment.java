package gsshop.mobile.v2.home.shop.flexible.shoppinglive;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

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
 * 방송알림 등록 팝업
 */
public class MobileLiveAlarmDialogFragment extends DialogFragment {

    private String mStrNumber;
    private String mStrImgUrl;

    public static String MOBILELIVE_ADD = "MOBILELIVE_ADD"; //등록 확인 누를때 AlarmRegistMLEvent 에서 구분하기위한 type

    public String mcaller;


    public MobileLiveAlarmDialogFragment() {

    }

    /**
     * 모바일라이브 방송알림 등록 팝업
     * @return
     */
    public static MobileLiveAlarmDialogFragment newInstance() {
        MobileLiveAlarmDialogFragment fragment = new MobileLiveAlarmDialogFragment();
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

        return inflater.inflate(R.layout.fragment_mobilelive_broad_alarm_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            //loadImageResizeToHeight 사용시 이미지가 표시 안되는 현상이 있어 우선 loadImageBadge 사용
            ImageUtil.loadImageBadge(getContext(), mStrImgUrl, view.findViewById(R.id.img_mobile_live), R.drawable.no_img_mobilelive, HD);
            ((TextView) view.findViewById(R.id.txt_mobile_live_broad_alarm_phone_number)).setText(mStrNumber);
        }
        catch (NullPointerException e) {
            Ln.e(e.getMessage());
        }

        /*
        // 메세지 리스트 삭제
        TableLayout cc = (TableLayout) view.findViewById(R.id.table_mobile_live_broad_alarm);
        if (messages != null) {
            for (String msg : messages) {
                TableRow row = (TableRow) LayoutInflater.from(getContext()).inflate(R.layout.row_mobile_live_broad_alarm, null);
                ((TextView) row.findViewById(R.id.txt_mobile_live_broad_alarm_message)).setText(msg);
                cc.addView(row);

            }
        }
        */

        /*
        // 횟수, 기간 삭제
        monthRadioGroup = (RadioGroup) view.findViewById(R.id.rg_mobile_live_broad_alarm_month);
        monthRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                for (int i = 0; i < group.getChildCount(); i++) {
                    ((RadioButton) group.getChildAt(i)).setTextAppearance(group.getContext(), R.style.mobile_live_broad_alarm_text_unselected);
                }
                ((RadioButton) group.findViewById(checkedId)).setTextAppearance(group.getContext(), R.style.mobile_live_broad_alarm_text_selected);
            }


        });
        timeRadioGroup = (RadioGroup) view.findViewById(R.id.rg_mobile_live_broad_alarm_time);
        timeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                for (int i = 0; i < group.getChildCount(); i++) {
                    ((RadioButton) group.getChildAt(i)).setTextAppearance(group.getContext(), R.style.mobile_live_broad_alarm_text_unselected);
                }
                ((RadioButton) group.findViewById(checkedId)).setTextAppearance(group.getContext(), R.style.mobile_live_broad_alarm_text_selected);
            }
        });
        */
        
        view.findViewById(R.id.btn_mobile_live_broad_alarm_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        View viewCheckAlarm = view.findViewById(R.id.view_chk_night_alarm);
        CheckBox checkAlarm = view.findViewById(R.id.chk_night_alarm);

        if (checkAlarm.isChecked()) {
            viewCheckAlarm.setContentDescription("선택 됨, " + getString(R.string.shopping_live_alarm_chk_off_description));
            checkAlarm.setContentDescription(getString(R.string.shopping_live_alarm_chk_off_description));
        }
        else {
            viewCheckAlarm.setContentDescription("선택 안됨, " + getString(R.string.shopping_live_alarm_chk_on_description));
            checkAlarm.setContentDescription(getString(R.string.shopping_live_alarm_chk_on_description));
        }

        view.findViewById(R.id.btn_mobile_live_broad_alarm_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //효율코드
                try {
                    ((AbstractBaseActivity) getContext()).setWiseLogHttpClient(ServerUrls.WEB.MOBILE_LIVE_ALARM_ADD);
                    //등록에 대한 Controller 호출(caller = MOBILELIVE_ADD로 전달)
                    ((AbstractBaseActivity) getContext()).onMLRegister(mcaller, MOBILELIVE_ADD, null, null,
                            ((CheckBox) view.findViewById(R.id.chk_night_alarm)).isChecked());
                }
                catch (ClassCastException | NullPointerException e) {
                        Ln.e(e.getMessage());
                }
                dismiss();
            }
        });

        viewCheckAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAlarm.setChecked(!checkAlarm.isChecked());
            }
        });

        checkAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    buttonView.setContentDescription(getString(R.string.shopping_live_alarm_chk_off_description));
                    viewCheckAlarm.setContentDescription("선택됨, " + getString(R.string.shopping_live_alarm_chk_off_description));
                }
                else {
                    buttonView.setContentDescription(getString(R.string.shopping_live_alarm_chk_on_description));
                    viewCheckAlarm.setContentDescription("선택안됨, " + getString(R.string.shopping_live_alarm_chk_on_description));
                }
            }
        });
    }

    public void setData(String caller, String thumbUrl, String phoneNumber, String[] messages) {
        mStrNumber = phoneNumber;
        mStrImgUrl = thumbUrl;
        mcaller = caller;
    }

}
