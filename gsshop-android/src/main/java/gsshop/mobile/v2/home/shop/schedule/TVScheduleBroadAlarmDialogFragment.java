package gsshop.mobile.v2.home.shop.schedule;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.util.ImageUtil;
import gsshop.mobile.v2.web.WebUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TVScheduleBroadAlarmDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TVScheduleBroadAlarmDialogFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM_THUMB_URL = "_arg_param_thumb_url";
    private static final String ARG_PARAM_PRDID = "_arg_param_prdid";
    private static final String ARG_PARAM_TITLE = "_arg_param_title";
    private static final String ARG_PARAM_PHONE_NUMBER = "_arg_param_phone_number";
    private static final String ARG_PARAM_MESSAGES = "_arg_param_messages";
    private static final String ARG_PARAM_CALLER = "_arg_param_caller";



    // TODO: Rename and change types of parameters
    private String thumbUrl;
    private String prdid;
    private String title;
    private String phoneNumber;
    private String[] messages;
    private RadioGroup monthRadioGroup;
    private RadioGroup timeRadioGroup;

    /**
     * 내일TV에서 알림 등록할경우 Caller
     */
    public static String TOMORROWTV = "TOMORROWTV";


    /**
     * 콜백용 인터페이스 정의
     */
    public interface OnAlarmListener {
        void onRegister(String caller, String prdId, String prdName, String period, String times);
    }

    //콜백용 리스너
    OnAlarmListener onAlarmListener;

    public TVScheduleBroadAlarmDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param thumbUrl Parameter 1.
     * @param prdid Parameter 2.
     * @param title Parameter 3.
     * @param phoneNumber Parameter 4.
     * @param messages Parameter 5.
     * @return A new instance of fragment TVScheduleBroadAlarmDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TVScheduleBroadAlarmDialogFragment newInstance(String thumbUrl, String prdid, String title, String phoneNumber, String[] messages) {
        TVScheduleBroadAlarmDialogFragment fragment = new TVScheduleBroadAlarmDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_THUMB_URL, thumbUrl);
        args.putString(ARG_PARAM_PRDID, prdid);
        args.putString(ARG_PARAM_TITLE, title);
        args.putString(ARG_PARAM_PHONE_NUMBER, phoneNumber);
        args.putString(ARG_PARAM_MESSAGES, new Gson().toJson(messages));

        fragment.setArguments(args);
        return fragment;
    }

    /**
     * 방송알림 AB테스트 - 이게 내일TV, 편성표중에 어디서 호출되었는지 알기위해 caller추가
     * @param thumbUrl
     * @param prdid
     * @param title
     * @param phoneNumber
     * @param messages
     * @param caller - 내일TV, 편성표중에 어디서 호출되었는지 알려줌("TOMORROWTV"로 Define)
     * @return
     */
    public static TVScheduleBroadAlarmDialogFragment newInstance(String thumbUrl, String prdid, String title, String phoneNumber, String[] messages, String caller) {
        TVScheduleBroadAlarmDialogFragment fragment = new TVScheduleBroadAlarmDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_THUMB_URL, thumbUrl);
        args.putString(ARG_PARAM_PRDID, prdid);
        args.putString(ARG_PARAM_TITLE, title);
        args.putString(ARG_PARAM_PHONE_NUMBER, phoneNumber);
        args.putString(ARG_PARAM_MESSAGES, new Gson().toJson(messages));

        args.putString(ARG_PARAM_CALLER, caller);
        fragment.setArguments(args);
        return fragment;
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        //팝업 외부영역 반투명 설정
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Translucent_NoTitleBar);
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            thumbUrl = getArguments().getString(ARG_PARAM_THUMB_URL);
            title = getArguments().getString(ARG_PARAM_TITLE);
            phoneNumber = getArguments().getString(ARG_PARAM_PHONE_NUMBER);
            String json = getArguments().getString(ARG_PARAM_MESSAGES);
            messages = new Gson().fromJson(json, String[].class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //배경 30% 딤처리
        WindowManager.LayoutParams windowParams = getDialog().getWindow().getAttributes();
        windowParams.dimAmount = 0.3f;
        windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        getDialog().getWindow().setAttributes(windowParams);

        return inflater.inflate(R.layout.fragment_tv_schedule_broad_alarm_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView bb = (ImageView) view.findViewById(R.id.img_tv_schedule_broad_alarm_thumb);
        ImageUtil.loadImage(getContext(), thumbUrl, bb, R.drawable.noimg_list);

        ((TextView) view.findViewById(R.id.txt_tv_schedule_broad_alarm_title)).setText(title);
        ((TextView) view.findViewById(R.id.txt_tv_schedule_broad_alarm_phone_number)).setText(phoneNumber);

        TableLayout cc = (TableLayout) view.findViewById(R.id.table_tv_schedule_broad_alarm);
        if (messages != null) {
            for (String msg : messages) {
                TableRow row = (TableRow) LayoutInflater.from(getContext()).inflate(R.layout.row_tv_schedule_broad_alarm, null);
                ((TextView) row.findViewById(R.id.txt_tv_schedule_broad_alarm_message)).setText(msg);
                cc.addView(row);

            }
        }


        monthRadioGroup = (RadioGroup) view.findViewById(R.id.rg_tv_schedule_broad_alarm_month);
        monthRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                for (int i = 0; i < group.getChildCount(); i++) {
                    ((RadioButton) group.getChildAt(i)).setTextAppearance(group.getContext(), R.style.tv_schedule_broad_alarm_text_unselected);
                }
                ((RadioButton) group.findViewById(checkedId)).setTextAppearance(group.getContext(), R.style.tv_schedule_broad_alarm_text_selected);
            }


        });
        timeRadioGroup = (RadioGroup) view.findViewById(R.id.rg_tv_schedule_broad_alarm_time);
        timeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                for (int i = 0; i < group.getChildCount(); i++) {
                    ((RadioButton) group.getChildAt(i)).setTextAppearance(group.getContext(), R.style.tv_schedule_broad_alarm_text_unselected);
                }
                ((RadioButton) group.findViewById(checkedId)).setTextAppearance(group.getContext(), R.style.tv_schedule_broad_alarm_text_selected);
            }
        });

        view.findViewById(R.id.btn_tv_schedule_broad_alarm_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        view.findViewById(R.id.btn_tv_schedule_broad_alarm_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String period = "";
                String times = "";

                switch (monthRadioGroup.getCheckedRadioButtonId()) {
                    case R.id.rb_tv_schedule_broad_alarm_month_one:
                        period = "1";
                        break;
                    case R.id.rb_tv_schedule_broad_alarm_month_two:
                        period = "2";
                        break;
                    case R.id.rb_tv_schedule_broad_alarm_month_tree:
                        period = "3";
                        break;

                }
                switch (timeRadioGroup.getCheckedRadioButtonId()) {
                    case R.id.rb_tv_schedule_broad_alarm_time_one:
                        times = "1";
                        break;
                    case R.id.rb_tv_schedule_broad_alarm_time_two:
                        times = "3";
                        break;
                    case R.id.rb_tv_schedule_broad_alarm_time_tree:
                        times = "99";
                        break;
                }
                dismiss();
               onAlarmListener.onRegister(getArguments().getString(ARG_PARAM_CALLER),getArguments().getString(ARG_PARAM_PRDID),
                            getArguments().getString(ARG_PARAM_TITLE), period, times);


            }
        });
    }

    /**
     * 방송알림등록 확인 dialog
     */
    public static Dialog makeBroadAlarmConfirmDialog(final Context context, String phone, String btnText, final String btnLink) {
        final Dialog dialog = new Dialog(context, R.style.CustomDialog);
        Window window = dialog.getWindow();
        dialog.setCancelable(true);// 백키 통한 취소 허용.
        dialog.setContentView(R.layout.confirm_tv_schedule_broad_sbumit_alarm);

        dialog.findViewById(R.id.bg_tv_schedule_broad_alarm_my).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //탈퇴회원의 경우 팝업닫고 다른 계정으로 로그인시 매핑되지 않도록 한다.
                dialog.dismiss();
                WebUtils.goWeb(context, btnLink);
            }
        });
        dialog.findViewById(R.id.btn_tv_schedule_broad_alarm_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });

        ((TextView) dialog.findViewById(R.id.txt_tv_schedule_broad_alarm_phone_number)).setText(phone);
        ((TextView) dialog.findViewById(R.id.txt_tv_schedule_broad_alarm_my_alarm)).setText(btnText);

        return dialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        onAlarmListener = (OnAlarmListener) activity;
    }
}
