package gsshop.mobile.v2.support.sns;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import gsshop.mobile.v2.R;

/**
 * SNS 팝업
 * Activity에서 SNS팝업을 띄울 경우 기 적용된 LiveTalkWebActivity.java 참조
 */
public class SnsDialogFragment extends DialogFragment {

    //SNS 리스트뷰
    private ListView snsListView;

    //콜백용 리스너
    OnSnsSelectedListener snsListener;

    //노출할 SNS 정보 (arrays.xml에서 취득)
    String[] snsTitle, snsScript;

    /**
     * 콜백용 인터페이스 정의
     */
    public interface OnSnsSelectedListener {
        public void onSnsSelected(String functionName);    //SNS 선택
        public void onCloseSelected();  //닫기 선택
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //팝업 외부영역 반투명 설정
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Translucent_NoTitleBar);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sns_dialog, container, false);
        snsListView = (ListView) rootView.findViewById(R.id.sns_list);
        //닫기 메뉴 이벤트 정의
        rootView.findViewById(R.id.sns_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snsListener.onCloseSelected();
                dismiss();
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //arrays.xml에서 SNS 정보를 취득한다.
        snsTitle = getResources().getStringArray(R.array.sns_title);
        snsScript = getResources().getStringArray(R.array.sns_script);
        TypedArray snsIcon = getResources().obtainTypedArray(R.array.sns_icon);

        ArrayList<SnsInfo> snsInfoList = new ArrayList<SnsInfo>();
        for (int i=0; i<snsTitle.length; i++) {
            snsInfoList.add(new SnsInfo(snsIcon.getResourceId(i, 0), snsTitle[i], snsScript[i]));
        }
        snsIcon.recycle();

        SnsAdapter snsAdapter = new SnsAdapter(getActivity(), R.layout.fragment_sns_dialog_row, snsInfoList);
        snsListView.setAdapter(snsAdapter);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        snsListener = (OnSnsSelectedListener) activity;
    }

    /**
     * 하드웨어 백키 클릭시 콜백
     *
     * @param dialog DialogInterface
     */
    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        snsListener.onCloseSelected();
        dismiss();
    }

    /**
     * SNS 어뎁터 정의
     */
    private class SnsAdapter extends ArrayAdapter<SnsInfo> {
        private  Context context;
        private final LayoutInflater inflater;
        private ArrayList<SnsInfo> items;
        private int resourceId;

        public SnsAdapter(Context context, int textViewResourceId, ArrayList<SnsInfo> items) {
            super(context, textViewResourceId, items);
            this.context = context;
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.items = items;
            this.resourceId = textViewResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            View v = convertView;
            if (v == null) {
                v = inflater.inflate(resourceId, null);
                holder = new ViewHolder();
                holder.snsLayout = (LinearLayout) v.findViewById(R.id.sns_layout);
                holder.snsIcon = (ImageView) v.findViewById(R.id.sns_icon);
                holder.snsTitle = (TextView) v.findViewById(R.id.sns_title);
                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }

            final SnsInfo snsInfo = items.get(position);
            //SNS 아이콘 세팅
            holder.snsIcon.setImageResource(snsInfo.snsIcon);
            //SNS 타이틀 세팅
            holder.snsTitle.setText(snsInfo.snsTitle);
            //리스트 아이템 클릭시 웹뷰 스크립트명 세팅
            holder.snsLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snsListener.onSnsSelected(snsInfo.snsScriptName);
                    dismiss();
                }
            });

            return v;
        }
    }

    /**
     * 뷰홀더 정의
     */
    class ViewHolder {
        LinearLayout snsLayout;
        ImageView snsIcon;
        TextView snsTitle;
    }

    /**
     * 로컬에 있는 SNS아이콘의 리소스 아이디를 반환한다.
     * (아이콘 다운로드 실패시 대비, 필요시 사용요망)
     *
     * @param scriptName SNS종류를 구분하기 위한 값
     * @return SNS아이콘의 리소스 아이디
     */
    private int getSnsIconResourceId(String scriptName) {
        Map<String, Integer> map  = new HashMap<String, Integer>();
        map.put("KakaoTalk", R.drawable.sns_kakaotalk);
        map.put("KakaoStory", R.drawable.sns_kakaostory);
        map.put("FaceBook", R.drawable.sns_facebook);
        map.put("Twitter", R.drawable.sns_twitter);
        map.put("Sms", R.drawable.sns_sms);
        map.put("Pinterest", R.drawable.sns_pinterest);

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (scriptName.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return android.R.color.transparent;
    }

    /**
     * SNS 정보 모델
     */
    private static class SnsInfo {
        SnsInfo(int icon, String title, String scriptName) {
            this.snsIcon = icon;
            this.snsTitle = title;
            this.snsScriptName = scriptName;
        }

        //아이콘
        private int snsIcon = 0;
        //제목
        private String snsTitle = "";
        //자바스크립트 함수명
        private String snsScriptName = "";
    }
}
