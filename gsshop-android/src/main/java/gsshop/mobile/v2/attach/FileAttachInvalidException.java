/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.attach;

import com.gsshop.mocha.core.exception.WarningException;

/**
 * 잘못된(부적합한) 상품평 조회/저장 시도시 발생하는 오류.
 * 서버로부터 명시적인 에러메시지가 전달된 경우에 사용됨.
 */
@SuppressWarnings("serial")
public class FileAttachInvalidException extends WarningException {

    /**
     * FileAttachInvalidException
     */
    public FileAttachInvalidException() {
    }

    /**
     * FileAttachInvalidException
     * @param detailMessage
     */
    public FileAttachInvalidException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * FileAttachInvalidException
     * @param throwable
     */
    public FileAttachInvalidException(Throwable throwable) {
        super(throwable);
    }

    /**
     * FileAttachInvalidException
     * @param detailMessage
     * @param throwable
     */
    public FileAttachInvalidException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

}
