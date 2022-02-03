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
import gsshop.mobile.v2.util.PrefRepositoryNamed;

/**
 * 매장팝업 다시보지않기 프리퍼런스 저장용 모델
 */
@Model
public class PmoNoShowForeverPrefInfo {

    /**
     * 프리퍼런스에 저장할 프로모션번호 최대 갯수
     */
    private static int MAX_PRESERVE_PMO_NUM = 50;

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
        PmoNoShowForeverPrefInfo pref = get();
        if (pref != null && pref.dsplSeqList.contains(dsplSeq)) {
            ret = true;
        }
        return ret;
    }

    /**
     * MAX_PRESERVE_PMO_NUM 이상이 되면 먼저 저장된 값부터 제거한다.
     *
     * @param pref PmoNoShowForeverPrefInfo
     */
    public static void removeGarbade(PmoNoShowForeverPrefInfo pref) {
        if (pref != null) {
            int size = pref.dsplSeqList.size();
            if (size >= MAX_PRESERVE_PMO_NUM) {
                int loopCnt = size - MAX_PRESERVE_PMO_NUM + 1;
                for (int i = 0; i < loopCnt; i++) {
                    pref.dsplSeqList.remove(0);
                }
                PrefRepositoryNamed.save(MainApplication.getAppContext(), Keys.CACHE.PMOPOPUP_FOREVER, pref);
            }
        }
    }

    /**
     * 프리퍼런스에 프로모션번호를 저장한다.
     *
     * @param dsplSeq 프로모션번호
     */
    public static void save(String dsplSeq) {
        PmoNoShowForeverPrefInfo pref = get();
        if (pref == null) {
            pref = new PmoNoShowForeverPrefInfo();
        }

        //저장전에 MAX_PRESERVE_PMO_NUM-1 갯수를 만든다.
        removeGarbade(pref);

        pref.dsplSeqList.add(dsplSeq);
        PrefRepositoryNamed.save(MainApplication.getAppContext(), Keys.CACHE.PMOPOPUP_FOREVER, pref);
    }

    /**
     * 프리퍼런스 정보를 구한다.
     *
     * @return PmoNoShowForeverPrefInfo
     */
    public static PmoNoShowForeverPrefInfo get() {
        return PrefRepositoryNamed.get(MainApplication.getAppContext(),
                Keys.CACHE.PMOPOPUP_FOREVER, PmoNoShowForeverPrefInfo.class);
    }

}
