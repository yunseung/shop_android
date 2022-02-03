/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web.handler;

/**
 * 신한,삼성 카드 결제 요청 처리.
 *
 */
public class ShinhanCardUrlHandler extends GeneralCardUrlHandler {

    @Override
    public boolean match(String url) {
        if (url.startsWith("vguardcheck://")) {//삼성,신한
            return true;
        }

        if (url.startsWith("smshinhanansimclick://")) {
            return true;
        }

        if (url.startsWith("smshinhancardusim://")) {
            return true;
        }
        
        // 신한, 삼성, 현대
        if (url.startsWith("intent")) {                          
            return true;
        }

        return false;
    }

}
