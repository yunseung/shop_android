/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.support.criteo;

import com.gsshop.mocha.pattern.mvc.Model;

import java.util.ArrayList;

/**
 *  크리테오 데이타 모델
 *
 *  -샘플 JSON (View Home)
 *  {
 *      "account":{"an":"com.myapp","cn":"us","ln":"en"},
 *      "site_type":"aa",
 *      "id": {"gaid": "e16332c1-dd78-4288-a4e3-6190ed632b7e"},
 *      "events":[{"event": "viewHome", "ci": "usr123"}],
 *      "version": "s2s_v1.0.0"
 *  }
 */
@Model
public class CriteoData {

    private Account account;

    private String site_type;   //모바일 디바이스의 OS 타입 정보

    private Id id;

    private ArrayList<Event> events;

    private String version;     //Protocol Version

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public void setAccountData(String an, String cn, String ln) {
        this.account = new Account();
        this.account.setAn(an);
        this.account.setCn(cn);
        this.account.setLn(ln);
    }

    public String getSite_type() {
        return site_type;
    }

    public void setSite_type(String site_type) {
        this.site_type = site_type;
    }

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public void setIdData(String gaid) {
        this.id = new Id();
        this.id.setGaid(gaid);
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    public void setEventsData(String eventName) {
        ArrayList<Event> events = new ArrayList<Event>();
        Event event = new Event();
        event.setEvent(eventName);
        events.add(event);
        this.events = events;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Account 별 정보
     */
    @Model
    private static class Account {
        private String an;  //패키지명
        private String cn;  //국가코드
        private String ln;  //언어코드

        public String getAn() {
            return an;
        }

        public void setAn(String an) {
            this.an = an;
        }

        public String getCn() {
            return cn;
        }

        public void setCn(String cn) {
            this.cn = cn;
        }

        public String getLn() {
            return ln;
        }

        public void setLn(String ln) {
            this.ln = ln;
        }
    }

    /**
     * 앱이 설치된 모바일 디바이스의 광고아이디
     */
    @Model
    private static class Id {
        private String gaid;

        public String getGaid() {
            return gaid;
        }

        public void setGaid(String gaid) {
            this.gaid = gaid;
        }
    }

    /**
     * 이벤트 이름과 요구되는 추가 파라메타 정의
     */
    @Model
    private static class Event {
        private String event;   //이벤트 종류(viewHome, viewProduct 등)
        private String ci;      //앱에서 사용된 사용자 로그인 정보
        private String product; //상품 아이디 정보

        public String getEvent() {
            return event;
        }

        public void setEvent(String event) {
            this.event = event;
        }

        public String getCi() {
            return ci;
        }

        public void setCi(String ci) {
            this.ci = ci;
        }

        public String getProduct() {
            return product;
        }

        public void setProduct(String product) {
            this.product = product;
        }
    }
}
