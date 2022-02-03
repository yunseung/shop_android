/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.support.scheme;

import android.app.Activity;
import android.net.Uri;

/**
 * 여러가지 host를 사용하는 경우에 확장성을 위해
 * WebUrlHandler와 유사한 구조로 작성.
 *
 * NOTE : 구현클래스 추가시 SchemeGatewayActivity에 해당 클래스를 추가해줘야함.
 */
public interface UriHostHandler {

    /**
     * @param data data
     * @param host host
     * @return match
     */
    public boolean match(Uri data, String host);

    /**
     *
     * 주의 : 이 메소드 실행 완료후에 activity는 자동 finish되므로
     * 이 메소드에서 activity를 임의로 finish하지 말 것.
     *
     * @param activity activity
     * @param data data
     * @return return
     */
    public boolean handle(Activity activity, Uri data);
}
