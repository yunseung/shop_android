/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web.handler;

/**
 * 웹로그인 주소 처리.
 * 가끔씩 웹용 로그인 URL이 요청되는 경우가 있음.
 */
public class LoginWebUrlHandler extends LoginUrlHandler {
    //ex) http://m.gsshop.com/member/logIn.gs?returnurl=687474703A2F2F6d2e677373686f702e636f6d2F6d79677373686f702F6d794f726465724c6973742e6773

    @Override
    public boolean match(String url) {
        return url.contains("/member/logIn.gs");
    }
}
