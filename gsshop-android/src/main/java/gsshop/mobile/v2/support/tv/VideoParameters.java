/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.support.tv;

import android.content.pm.ActivityInfo;


public class VideoParameters {

    /**
     * brightcove에 등록된 동영상 번호
     */
    public String videoId;

    /**
     * 재생할 동영상 주소.
     * - http://xxxxx/xxx.mp4
     * - rtsp://xxxxx/xxxx
     */
    public String videoUrl;


    /**
     * 지금상품바로보기 버튼 클릭시 이동할 웹페이지 주소
     */
    public String productInfoUrl;

    /**
     * 상품 이미지 링크
     */
    public String proudctImageUrl;

    /**
     * 화면 가로/세로모드 정보
     */
    public int orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

    /**
     * 효율코드 주소
     */
    public String productWiselogUrl;


    /**
     * 동영상 시작 시간 - 밀리세컨드
     */
    public String videoStartTime;

    /**
     * 재생상태
     */
    public boolean isPlaying;
}
