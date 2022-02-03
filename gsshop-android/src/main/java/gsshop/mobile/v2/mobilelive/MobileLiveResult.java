package gsshop.mobile.v2.mobilelive;





import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.ArrayList;

/**
 * 모바일라이브 API 호출결과 데이타 모델
 */
@JsonIgnoreProperties
public class MobileLiveResult {
    public String liveNo;   // "484",
    public String mainTitle; // 현재 표시할곳이 있을까?? : 방송 유형의 타이틀이다 ex) 모바일 라이브
    public String title;    // "세척이 쉬우면 말 다했지! 윤남텍 가습기",
    public String onAirYn;  // "N",
    public String broadType;    //"방송중" or "replay"
    public String strDate;
    public String endDate;
    public String videoid;  //  그냥 내비뒀음
    public String dbsysdate;  // 디비시간 현재 요청한 순간의 시간 그냥 내비뒀ㅇ
    public String liveUrl;  // m3u8 내려올예정인 지금은 정해지지 않은 mp4 내림 변수명 바뀔듯
    public String gatePageUrl;  //게이트 페이지 주소
    public String currentPrdIndex;
    public ArrayList<MobileLivePrdsInfoList> mobileLivePrdsInfoList;
    public String alarmYn; //알람여부
    public String promotionName; //공유하기에 쓰일 프로그램명
    public String shareUrl; //공유될 url
    public String imgUrl; //공융될 이미지
    public String notiMoreLinkUrl; //공지사항 더보기 주소
    public String attendanceUrl; //출석이벤트 주소
    public String message; //웰컴메시지 (예:"님, 샤피라이브 또 찾아주셨네요! 반갑습니다.")
    public String mskId; //마스킹된 아이디 (예:"as***")
    public String couponPopupUrl;   // 제휴 쿠폰 팝업 노출 URL
    public String moreBtnUrl;   // 더보기 선택시 호출 URL
}
