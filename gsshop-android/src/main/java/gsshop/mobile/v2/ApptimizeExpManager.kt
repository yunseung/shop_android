package gsshop.mobile.v2

import java.util.*

object ApptimizeExpManager {
    /**
     * 실행가능한 실험목록
     */
    const val TABNAME2 = "TABNAME2"

    //public static final String TABNAME_TARGET = "ALL"; 실험 value에 네비게이션 번호(타겟번호) 들어있음
    const val IMG = "IMG"

    //public static final String IMG_TARGET = "ALL";
    const val IMGMS = "IMGMS"

    //public static final String IMGMS_TARGET = "ALL";
    const val CONTENTS = "CONTENTS"

    //public static final String CONTENTS_TARGET = "ALL";
    const val SCHEDULE = "SCHEDULE"

    const val SCHEDULE_TARGET = "323";
    const val TODAYOPEN = "TODAYOPEN"

    //public static final String TODAYOPEN_TARGET = "61";
    const val CATEGORY = "CATEGORY"
    const val CATEGORY_TARGET = "ALL" //ALL의 의미는 특정 영역지정이 아님을 뜻한다. ALL은 key값, 실제 타겟영역은 value값에 들어있음.
    const val HOMESUB = "HOMESUB"

    const val CAROUSEL = "CAROUSEL";

    /**
     * 실행가능한 실험타입
     */
    const val TYPE_A = "A"
    const val TYPE_B = "B"
    const val TYPE_C = "C"
    const val TYPE_O = "O" //오리지널 타입

    /**
     * 주상품, 부상품  효율 구분자
     */
    const val MAIN_PRD = "주상품";
    const val SUB_PRD = "부상품";


    /**
     * Launch가 된 운영 실험목록
     */
    @JvmField
    // ApptimizeRunExp 에서 해당 변수를 컨트롤 하는 부분 존재
    var prodArray: ArrayList<String> = ArrayList()

    /**
     * Launch가 된 테스트 실험목록
     */
    @JvmField
    // ApptimizeRunExp 에서 해당 변수를 컨트롤 하는 부분 존재
    var stageArray = ArrayList<String>()

    /**
     * prodArray를 쪼개서 넣은 Array
     */
    @JvmField
    val resultArray: ArrayList<ApptimizeBaseExp> = ArrayList()
    /**
     * 실험과 실험타겟 Array
     */
    //public static Exp[] expArray = {new Exp(TABNAME,TABNAME_TARGET),new Exp(IMG,IMG_TARGET),new Exp(IMGMS,IMGMS_TARGET),
    //       new Exp(TITLEPRICE,TITLEPRICE_TARGET),new Exp(TITLECOLOR,TITLECOLOR_TARGET),new Exp(CONTENTS,CONTENTS_TARGET)};
    //public int[] expInt ={TABNAME_INDEX}
    /**
     * \해당실험 존재 유무 체크
     * * ex)263_54_TITLEPRICE_A 이면 실험명(TITLEPRICE)와 타입(A)가 있는지 체크
     * @param expName
     * @param type
     * @param target
     * @return
     */
    @JvmStatic
    fun findExpNameType(expName: String?, type: String?, target: String?): Boolean {
        if (resultArray.size > 0) {
            for (i in resultArray.indices) {
                if (expName != null && expName == resultArray[i].exp &&
                        type != null && type == resultArray[i].type &&
                        target != null && target == resultArray[i].target) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * 해당실험 존재 유무 체크 , 타겟 필수
     * * ex)ALL_ALL_TABNAME_263_451_HOT신상 이면 실험명(TABNAME)이 있는지 체크
     *
     * @param expName
     * @param target
     * @return
     */
    fun findExpInstance(expName: String, target: String): ApptimizeBaseExp? {
        if (resultArray.size > 0) {
            for (i in resultArray.indices) {
                if (expName == resultArray[i].exp && target == resultArray[i].type) {
                    return resultArray[i]
                }
            }
        }
        return null
    }

    /**
     * 해당실험 존재 유무 체크
     * ex)ALL_ALL_TABNAME_263_451_HOT신상 이면 실험명(TABNAME)이 있는지 체크
     *
     * @param expName
     * @return
     */
    @JvmStatic
    fun findExpInstance(expName: String): ApptimizeBaseExp? {
        if (resultArray.size > 0) {
            for (i in resultArray.indices) {
                if (expName == resultArray[i].exp) {
                    return resultArray[i]
                }
            }
        }
        return null
    }

    /**
     * 앱티마이즈 prodArray 해당 실험 유효성 체크
     * @param expName
     * @return
     */
    @JvmStatic
    fun findProd(expName: String): Array<String>? {
        if (prodArray.size > 0) {
            for (i in prodArray.indices) {
                val arr = prodArray[i].split("_".toRegex()).toTypedArray() // ex) 263_54_IMG
                if (2 < arr.size && expName == arr[2]) {
                    return arr
                }
            }
        }
        return null
    }

    /**
     * resultArray에 추가
     * @param baseExp
     * @return
     */
    @JvmStatic
    fun addExp(baseExp: ApptimizeBaseExp): Boolean {
        return resultArray.add(baseExp)
    }
}