/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.support.pmopopup;

import com.gsshop.mocha.pattern.mvc.Model;

import java.util.ArrayList;
import java.util.List;

import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.util.DateUtils;
import gsshop.mobile.v2.util.PrefRepositoryNamed;

/**
 * 매장팝업 닫기(오늘안보기) 프리퍼런스 저장용 모델
 */
@Model
public class PmoNoShowTodayPrefInfo {

    /**
     * 닫기 클릭한 날자 저장
     */
    public String today;

    /**
     * 프로모션 번호 저장
     */
    public List<String> dsplSeqList = new ArrayList<String>();

    /**
     * 프리퍼런스 값을 체크해서 노출여부 여부를 확인한다.
     *
     * @param dsplSeq 프로모션 번호
     * @return if true, 미노출
     */
    public static boolean isHide(String dsplSeq) {
        boolean ret = false;

        removeGarbade();

        PmoNoShowTodayPrefInfo pref = get();
        if (pref != null && DateUtils.getDifferenceDays(pref.today, DateUtils.getToday("yyyyMMdd")) < 1) {
            if (pref.dsplSeqList.contains(dsplSeq)) {
                ret = true;
            }
        }
        return ret;
    }

    /**
     * 하루가 지난 데이타는 제거한다.
     * (하루가 지난 데이타는 의미가 없고, 데이타 누적을 막기 위함)
     */
    public static void removeGarbade() {
        PmoNoShowTodayPrefInfo pref = get();
        if (pref != null
                && DateUtils.getDifferenceDays(pref.today, DateUtils.getToday("yyyyMMdd")) >= 1) {
            remove();
        }
    }

    /**
     * 프리퍼런스에 날짜와 프로모션번호를 저장한다.
     *
     * @param today 날짜
     * @param dsplSeq 프로모션번호
     */
    public static void save(String today, String dsplSeq) {
        PmoNoShowTodayPrefInfo pref = get();
        if (pref == null) {
            pref = new PmoNoShowTodayPrefInfo();
        }
        pref.today = today;
        pref.dsplSeqList.add(dsplSeq);
        PrefRepositoryNamed.save(MainApplication.getAppContext(), Keys.CACHE.PMOPOPUP_TODAY, pref);
    }

    /**
     * 프리퍼런스 정보를 구한다.
     *
     * @return PmoNoShowTodayPrefInfo
     */
    public static PmoNoShowTodayPrefInfo get() {
        return PrefRepositoryNamed.get(MainApplication.getAppContext(), Keys.CACHE.PMOPOPUP_TODAY, PmoNoShowTodayPrefInfo.class);
    }

    /**
     * 프리퍼런스를 제거한다.
     */
    public static void remove() {
        PrefRepositoryNamed.remove(MainApplication.getAppContext(), Keys.CACHE.PMOPOPUP_TODAY);
    }
}
