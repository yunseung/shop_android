/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.review;

import com.gsshop.mocha.core.exception.WarningException;

/**
 * 상품평 리뷰이미지 로딩 오류
 *
 */
@SuppressWarnings("serial")
public class ReviewImageLoadException extends WarningException {

    /**
     *
     */
    public ReviewImageLoadException() {
    }

    /**
     * @param detailMessage
     */
    public ReviewImageLoadException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * @param throwable
     */
    public ReviewImageLoadException(Throwable throwable) {
        super(throwable);
    }

    /**
     * @param detailMessage
     * @param throwable
     */
    public ReviewImageLoadException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

}
