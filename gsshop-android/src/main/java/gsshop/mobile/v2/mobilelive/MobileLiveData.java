package gsshop.mobile.v2.mobilelive;

import java.util.ArrayList;

public class MobileLiveData {
    ///////////////////////////////////////////////////////////////////////////
    /**
     * CSP 연동 부분
     *
     */
    ///////////////////////////////////////////////////////////////////////////

    public class StateData{
        /**
         * 스텟 오브프젝트
         */
        public StateObject SO;

        /**
         * 타임 스탭프
         */
        public String TS;
    }
    public class StateObject{
        /**
         * 방문자 카운트
         */
        public Integer PV;

        /**
         * 방문자 유니크
         */
        public Integer UV;

    }
    public class SoObject{
        /**
         * 메세지 리스트 ( 공지 )
         */
        public ArrayList<String> MA;
        /**
         * 타임 스탭프
         */
        public String TS;
    }
    /**
     * 노티(공지) 리스트 데이터
     */
    public class NotiData{
        /**
         * 메세지 리스트 ( 공지 )
         */
        public ArrayList<String> MA;
        /**
         * 타임 스탭프
         */
        public String TS;
    }

    /**
     * Config 데이터
     */
    public class ConfigData{
        public ChatObject CO;
        //Config 리스트를 이 밑으로 추가하면 된다
    }

    public class ChatObject{
        /**
         * 채팅 유무
         */
        public String CHAT; // Y, N
    }

    /**
     *
     */
    public class ChatDataList{
        /**
         * 메세지 리스트 ( 공지 )
         */
        public ArrayList<ChatData> CA;
    }

    /**
     * 채팅 데이터
     * -기획서정의: 댓글(사용자문의글), 대댓글(PD 답변글)
     *
     * {"UO":{"U":"admin","NI":"PD"},"MO":{"MG":"22"},"C":"mobilelive","TS":1549871089465,"PMO":[ChatData]}
     */
    public class ChatData{

        /**
         * 고객객체 (Object)
         */
        public UserObject UO=null;

        /**
         * 메시지 텍스트 (Object)
         */
        public MessageObject MO=null;

        /**
         * 댓글 (PMO가 있는 경우 PMO 외부가 대댓글)
         */
        public ChatData PMO=null;

        /**
         * mobilelive 구분자
         */
        public String C="";

        /**
         * 메시지 수신 시간 (Timestamp)
         */
        public String TS="";

    }
    /**
     * 고객객체  (Object)
     */
    public class UserObject{
        /**
         * 고객번호
         */
        public String U="";
        /**
         * 닉네임 아이디임 별표 쳐진
         */
        public String NI="";
    }

    /**
     * 메세지 관련
     */
    public class MessageObject{
        /**
         * 메세지 본문
         */
        public String MG="";

        /**
         * 채팅 제외 로직을 위함. (블랙리스트)
         */
        public String VI="";
        /**
         * 이모티콘 코드 (String)
         */
        //String EM="";
    }
}
