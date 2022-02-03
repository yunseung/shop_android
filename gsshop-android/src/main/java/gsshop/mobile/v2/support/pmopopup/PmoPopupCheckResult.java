/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.support.pmopopup;

import com.gsshop.mocha.pattern.mvc.Model;

@Model
public class PmoPopupCheckResult {
    /**
     * 로그인정보 (로그인 : "true", 비로그인 : "false")
     */
    public String isLogin;

    /**
     * 해당이벤트 대상여부 (대상 : "true", 비대상 : "false")
     */
    public String data;

    /**
     * 조회상태 (에러 : "ERR", 정상: "SUCC")
     */
    public String retCd;

    /**
     * 결과메세지("로그인 후 시도해 주세요.","이벤트 대상입니다.","이벤트 대상이 아닙니다.")
     */
    public String retMsg;
}
