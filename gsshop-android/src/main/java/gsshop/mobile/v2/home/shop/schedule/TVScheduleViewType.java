package gsshop.mobile.v2.home.shop.schedule;

import java.util.HashMap;
import java.util.Map;


/**
 * TV 편셩표 view type
 */

public abstract class TVScheduleViewType {
    public static Map<String, Integer> viewTypeMap;

    public static final String TEXT_SCHEDULE_PREV_LINK = "SCH_PRO_TXT_PRE";
    public static final String TEXT_SCHEDULE_NEXT_LINK = "SCH_PRO_TXT_NEXT";
    public static final String TEXT_SCHEDULE_DATE = "SCH_BAN_MUT_DATE";

    public static final String TEXT_PRD_LIVE = "SCH_MAP_MUT_LIVE";

    public static final String TEXT_SPACE = "SCH_PRD_SPACE";

    public static final int TYPE_SCHEDULE_PREV_LINK = 0x00;
    public static final int TYPE_SCHEDULE_NEXT_LINK = 0x01;
    public static final int TYPE_SCHEDULE_DATE = 0x02;

    /**
     * time line
     */
    public static final int TYPE_TIME_LINE_ON_AIR = 0x10;
    public static final int TYPE_TIME_LINE_THUMB = 0x11;

    /**
     * product
     */
    public static final int TYPE_PRD_TIME = 0x20;
    public static final int TYPE_PRD_BAN_W540 = 0x22;
    public static final int TYPE_PRD_LIVE = 0x23;
    public static final int TYPE_PRD_MAIN = 0x24;
    public static final int TYPE_PRD_GOV = 0x26;
    public static final int TYPE_PRD_INSU = 0x27;
    public static final int TYPE_PRD_SPACE = 0x28;
    public static final int TYPE_PRD_NO_DATA = 0x29;

    /**
     * TV편성표 VOD상품아이콘 노출 AB테스트 by hanna
     * "SCH_MAP_MUT_MAIN"로 뷰타입은 같지만 pgmYN으로 생방송 여부를 나눈다
     */
     public static final int TYPE_PRD_LIVE_AB_BCV = 0x30;
     public static final int TYPE_PRD_MAIN_AB_BCV = 0x31;

    /**
     * TV편성표 AB테스트(카테고리 필터링 기능추가)
     */
    public static final int TYPE_PRD_NO_RESULTS = 0x32;

    static {
        viewTypeMap = new HashMap<>();
        viewTypeMap.put(TEXT_SCHEDULE_PREV_LINK, TYPE_SCHEDULE_PREV_LINK);
        viewTypeMap.put(TEXT_SCHEDULE_NEXT_LINK, TYPE_SCHEDULE_NEXT_LINK);
        viewTypeMap.put(TEXT_SCHEDULE_DATE, TYPE_SCHEDULE_DATE);
        // time line
        viewTypeMap.put("SCH_PRO_BAN_THM", TYPE_TIME_LINE_ON_AIR);
        viewTypeMap.put("SCH_PRO_BAN_XXX", TYPE_TIME_LINE_THUMB);

        // product
        //메인개편 버전에서는 방송시간이 뷰홀더 내로 이동하여 아래 뷰타입은 노출안함
        //viewTypeMap.put("SCH_BAN_MUT_TIME", TYPE_PRD_TIME);

        viewTypeMap.put("SCH_BAN_IMG_W540", TYPE_PRD_BAN_W540);
        viewTypeMap.put(TEXT_PRD_LIVE, TYPE_PRD_LIVE);
        viewTypeMap.put("SCH_MAP_MUT_MAIN", TYPE_PRD_MAIN);
        viewTypeMap.put("SCH_MAP_MUT_GOV", TYPE_PRD_GOV);
        viewTypeMap.put("SCH_MAP_MUT_INSU", TYPE_PRD_INSU);
        viewTypeMap.put(TEXT_SPACE, TYPE_PRD_SPACE);
        viewTypeMap.put("SCH_BAN_NO_DATA", TYPE_PRD_NO_DATA);

        //TV편성표 VOD상품아이콘 노출 AB테스트 by hanna
        viewTypeMap.put("SCH_MAP_MUT_LIVE_AB_BCV_VT", TYPE_PRD_LIVE_AB_BCV);
        viewTypeMap.put("SCH_MAP_MUT_MAIN_AB_BCV_VT", TYPE_PRD_MAIN_AB_BCV);

        //TV편성표 AB테스트(카테고리 필터링 기능추가)
        viewTypeMap.put("SCH_NO_RESULTS", TYPE_PRD_NO_RESULTS);
    }


}
