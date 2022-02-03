package gsshop.mobile.v2.intro;

import com.gsshop.mocha.pattern.mvc.Model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.util.PrefRepositoryNamed;

/**
 * Created by jhkim on 16. 2. 25..
 * IntroImageInfo
 *
 */
@Model
public class IntroImageInfo {

    /**
     * imageUrl
     */
    public String imageUrl;
    /**
     * startDate
     */
    public String startDate;
    /**
     * endDate
     */
    public String endDate;
    /**
     * modiDate 에잇 다 퍼블릭이냐 ;
     */
    public String modiDate;

    /**
     * 인트로 등급 appIntroTxt 배열
     */
    public List<AppIntroTxt> appIntroTxt;

    @Model
    public static class AppIntroTxt {
        public String grade; // : VVIP
        public String fontColor; //: #000000
        public String mainTitle;// : 고객님
        public String subTitle; // : 올 한해도 GS SHOP과 함께 행복한 시간 되세요
    }
    /**
     *
     * 현재 시간과 인트로 표시 시간 체크해서 true/false 반환
     * 날짜 비교
     * 오늘이 시작일보다 작거나 종료일보다 크면 fasle
     * 오늘이 시작일보다 크거나 같으면 true
     * 오늘이 종료일보다 작거나 같으면 true
     * 수정일이 저장되어 있는 modify 날짜와 다를 경우 false
     *
     */
    public static boolean isValidDate() {


        try{
            long startDate = Long.parseLong(PrefRepositoryNamed.get(MainApplication.getAppContext(),
                    Keys.PREF.INTRO_IMAGE_INFO, IntroImageInfo.class).startDate);
            long endDate = Long.parseLong(PrefRepositoryNamed.get(MainApplication.getAppContext(),
                    Keys.PREF.INTRO_IMAGE_INFO, IntroImageInfo.class).endDate);

            SimpleDateFormat sdf = new SimpleDateFormat( "yyyyMMddHHmmss" );
            long nowDate = Long.parseLong(sdf.format( Calendar.getInstance().getTime() ));

            if(nowDate < startDate || nowDate > endDate){
                return false;
            }else{
                return true;
            }
        }catch(Exception e){
            return false;
        }

    }
}
