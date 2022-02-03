/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.attach;

import com.gsshop.mocha.core.exception.SystemException;

/**
 * 카메라 미리보기 에러.
 *
 */
@SuppressWarnings("serial")
public class CameraPreviewException extends SystemException {

    /**
     * CameraPreviewException
     */
    public CameraPreviewException() {
    }

    /**
     * CameraPreviewException
     *
     * @param detailMessage detailMessage
     */
    public CameraPreviewException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * CameraPreviewException
     *
     * @param throwable throwable
     */
    public CameraPreviewException(Throwable throwable) {
        super(throwable);
    }

    /**
     * CameraPreviewException
     *
     * @param detailMessage detailMessage
     * @param throwable throwable
     */
    public CameraPreviewException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

}
