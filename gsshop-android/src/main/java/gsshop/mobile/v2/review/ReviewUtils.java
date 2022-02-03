/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.review;

import java.io.File;

import android.content.Context;


/**
 * 상품평 관련 유틸함수
 *
 */
public abstract class ReviewUtils {

	private static final String PREFIX_NAME = "review_";

    /**
     * 내부 캐시 메모리에 임시파일을 생성한다.
     * 
     * @param context 컨텍스트
     * @param idx 파일명 구분을 위한 번호
     * @return File
     */
    public static File getTempReviewImage(Context context, int idx) {
        return new File(context.getCacheDir(), PREFIX_NAME + idx + ".jpg");
    }
}
