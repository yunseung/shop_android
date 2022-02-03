package gsshop.mobile.v2.support.buildcheck;

import com.gsshop.mocha.pattern.mvc.Model;

/**
 * Created by azota on 2016-06-17.
 * 빌드정보 check 결과값
 */
@Model
public class BuildLog {

    /**
     * 결과 Y N
     */
    public String result;

    /**
     * 에러 및 성공시 message
     * oops.gssho.com전달
     *
     * 성공
     * -SUCCESS
     * 실패
     * -ERR_SIGN_VAL : 인증서지문 불일치
     * -ERR_HASH_VAL : dex파일 해쉬코드 불일치
     * -ERR_BUILD_INFO : DB에 정보 없음
     * -ERR_PARAM_INFO : 앱에서 파라미터 전송 안함
     */
    public String message;

}
