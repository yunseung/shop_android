/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.support.pmopopup;

import com.gsshop.mocha.pattern.mvc.Model;

import java.io.Serializable;

/**
 * 매장팝업 정보
 */
@Model
public class PmoPopupInfo implements Serializable {
    /**
     * API에서는 내려주나 앱에서는 사용안함
     */
    public String pmoNo;

    /**
     * 프로모션 번호
     */
    public String dsplSeq;

    /**
     * 이미지 주소
     */
    public String imageUrl;

    /**
     * 링크 주소
     */
    public String linkUrl;

    /**
     * 네비게이션 아이디
     */
    public String naviId;
}
