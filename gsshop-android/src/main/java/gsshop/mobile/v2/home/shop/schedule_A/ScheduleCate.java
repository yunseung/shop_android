package gsshop.mobile.v2.home.shop.schedule_A;/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */

import com.gsshop.mocha.pattern.mvc.Model;

/**
 * TV편성표 AB테스트
 * 카테고리 필터링 기능 추가
 */
@Model
public class ScheduleCate {

    public String cateNo; //카테고리 번호
    public String cateNm; //카테고리
    public String apiUrl; //null로 내려옴
    public String apiParam; //null로 내려옴
}
