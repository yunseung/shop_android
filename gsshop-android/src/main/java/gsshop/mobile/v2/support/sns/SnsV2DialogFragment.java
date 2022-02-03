package gsshop.mobile.v2.support.sns;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import gsshop.mobile.v2.R;
import gsshop.mobile.v2.ServerUrls;
import roboguice.util.Ln;

/**
 * SNS 팝업
 * Activity에서 SNS팝업을 띄울 경우 기 적용된 ShortbangActivity.java 참조
 */
public class SnsV2DialogFragment extends DialogFragment {

    //javascript 함수 정의
    private final static String SCRIPT_KakaoTalk = "shareKakaoTalk";
    private final static String SCRIPT_KakaoStory = "shareKakaoStory";
    private final static String SCRIPT_Line = "shareLine";
    private final static String SCRIPT_SMS = "shareSms";
    private final static String SCRIPT_Facebook = "shareFacebook";
    private final static String SCRIPT_Twitter = "shareTwitter";
    private final static String SCRIPT_Pinterest = "sharePinterest";
    private final static String SCRIPT_Url = "shareUrl";
    private final static String SCRIPT_Etc = "shareEtc";

    //UTM 파라미터 정의
    private final static String UTM_KakaoTalk = "&utm_source=kakao&utm_medium=sns&utm_campaign=sharekakao";
    private final static String UTM_KakaoStory = "&utm_source=kas&utm_medium=sns&utm_campaign=sharekas";
    private final static String UTM_Line = "&utm_source=line&utm_medium=sns&utm_campaign=shareline";
    private final static String UTM_SMS = "";
    private final static String UTM_Facebook = "&utm_source=facebook&utm_medium=sns&utm_campaign=sharefb";
    private final static String UTM_Twitter = "&utm_source=twitter&utm_medium=sns&utm_campaign=sharetw";
    private final static String UTM_Pinterest = "&utm_source=pinterest&utm_medium=sns&utm_campaign=sharepint";
    private final static String UTM_Url = "&utm_source=url&utm_medium=sns&utm_campaign=shareurl";
    private final static String UTM_Etc = "";

    /**
     * 공유하기 타입 정의
     */
    public enum SHARE_TYPE {
        //SNS종류(스크립트함수명, 와이즈로그주소, UTM파라미터)
        KakaoTalk(SCRIPT_KakaoTalk, ServerUrls.WEB.SHORTBANG_SNS_KAKAOTALK, UTM_KakaoTalk),
        KakaoStory(SCRIPT_KakaoStory, ServerUrls.WEB.SHORTBANG_SNS_KAKAOSTORY, UTM_KakaoStory),
        Line(SCRIPT_Line, ServerUrls.WEB.SHORTBANG_SNS_LINE, UTM_Line),
        SMS(SCRIPT_SMS, ServerUrls.WEB.SHORTBANG_SNS_SMS, UTM_SMS),
        Facebook(SCRIPT_Facebook, ServerUrls.WEB.SHORTBANG_SNS_FACEBOOK, UTM_Facebook),
        Twitter(SCRIPT_Twitter, ServerUrls.WEB.SHORTBANG_SNS_TWITTER, UTM_Twitter),
        Pinterest(SCRIPT_Pinterest, ServerUrls.WEB.SHORTBANG_SNS_PINTEREST, UTM_Pinterest),
        Url(SCRIPT_Url, ServerUrls.WEB.SHORTBANG_SNS_URL, UTM_Url),
        Etc(SCRIPT_Etc, ServerUrls.WEB.SHORTBANG_SNS_ETC, UTM_Etc);

        private String scriptName = "";
        private String wiseLogUrl = "";
        private String utmParam = "";

        SHARE_TYPE(String scriptName, String wiseLogUrl, String utmParam) {
            this.scriptName = scriptName;
            this.wiseLogUrl = wiseLogUrl;
            this.utmParam = utmParam;
        }

        public String getScriptName() {
            return scriptName;
        }

        public String getWiseLogUrl() {
            return wiseLogUrl;
        }

        public String getUtmParam() {
            return utmParam;
        }
    }

    //콜백용 리스너
    OnSnsSelectedListener snsListener;

    /**
     * 콜백용 인터페이스 정의
     */
    public interface OnSnsSelectedListener {
        void onSnsSelected(SHARE_TYPE shareType);    //SNS 선택
        void onCloseSelected();  //닫기 선택
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

        View rootView = inflater.inflate(R.layout.fragment_sns_v2_dialog, container, false);

        setItemListener(rootView);

        return rootView;
    }

    /**
     * SNS팝업내에 아이템에 대한 리스너를 등록한다.
     *
     * @param view 레이아웃뷰
     */
    private void setItemListener(View view) {
        //카카오톡
        view.findViewById(R.id.lay_sns_kakaotalk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snsListener.onSnsSelected(SHARE_TYPE.KakaoTalk);
                dismiss();
            }
        });
        //카카오스토리
        view.findViewById(R.id.lay_sns_kakaostory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snsListener.onSnsSelected(SHARE_TYPE.KakaoStory);
                dismiss();
            }
        });
        //라인
        view.findViewById(R.id.lay_sns_line).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snsListener.onSnsSelected(SHARE_TYPE.Line);
                dismiss();
            }
        });
        //SMS
        view.findViewById(R.id.lay_sns_sms).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snsListener.onSnsSelected(SHARE_TYPE.SMS);
                dismiss();
            }
        });
        //페이스북
        view.findViewById(R.id.lay_sns_facebook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snsListener.onSnsSelected(SHARE_TYPE.Facebook);
                dismiss();
            }
        });
        //트위터
        view.findViewById(R.id.lay_sns_twitter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snsListener.onSnsSelected(SHARE_TYPE.Twitter);
                dismiss();
            }
        });


        //URL복사
        view.findViewById(R.id.lay_sns_url).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snsListener.onSnsSelected(SHARE_TYPE.Url);
                dismiss();
            }
        });
        //다른앱 선택하기
        view.findViewById(R.id.lay_sns_etc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snsListener.onSnsSelected(SHARE_TYPE.Etc);
                dismiss();
            }
        });
        //닫기 메뉴
        view.findViewById(R.id.lay_sns_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snsListener.onCloseSelected();
                dismiss();
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            snsListener = (OnSnsSelectedListener) activity;
        }catch (Exception e){
            //위 방식의 리스너 등록 에러나는 경우 ex.쇼핑라이브 공유하기는 에러발생하여 별도 리스너 등록하는 방안 씀
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
        snsListener.onCloseSelected();
        dismiss();
    }

    /**
     * 리스너 설정
     * @param listener
     */
    public void setSnsListener(OnSnsSelectedListener listener){
        if(listener != null){
            this.snsListener = listener;
        }else{
        }
    }
}
