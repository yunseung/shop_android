/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web.handler;

/**
 *
 * droidxantivirus:STATE=run{@literal &}TIME=xxxxxx{@literal &}AUTH=-xxxxxx
 */
public class HyundaiCardUrlHandler extends GeneralCardUrlHandler {

    @Override
    public boolean match(String url) {
        if (url.startsWith("smhyundaiansimclick://")) {
            return true;
        }

        if (url.startsWith("droidxantivirus:")) {
            return true;
        }

        return false;
    }

}
