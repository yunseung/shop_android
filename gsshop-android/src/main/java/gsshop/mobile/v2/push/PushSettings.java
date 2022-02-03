/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.push;

import com.gsshop.mocha.pattern.mvc.Model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.annotation.ParametersAreNullableByDefault;

import gsshop.mobile.v2.MainApplication;
import gsshop.mobile.v2.util.PrefRepositoryNamed;

/**
 * PUSH 관련 각종 정보.
 *
 */
@Model
@JsonIgnoreProperties(ignoreUnknown = true)
@ParametersAreNullableByDefault
public class PushSettings {

    /**
     * 메시지 수신할지 여부. 기본값은 false.
     *
     */
    public boolean approve = false;

    /**
     * 광고형 메시지 수신할지 여부.
     */
    public String approveAd = null; //

    /**
     * 메시지 수신시 팝업을 보여줄 것인가.
     */
    public boolean showPopup = false;

    /**
     * 메시지 팝업시 화면을 켤것인가.
     */
    public boolean screenOn = true;

    /**
     * 메시지 수신시 알림음을 낼 것인가.
     */
    public boolean sound = true;

    /**
     * 메시지 수신시 진동을 넣을 것인가.
     */
    public boolean vibration = true;

    /**
     * 알림승인 팝업을 보여준 적이 있는가.
     */
    public boolean shownApprovePopup = false;

    /**
     * 거래 거절 회원 여부 확인 (최초 1회만)
     */
    public boolean isCheckedRejectMember = false;

    /**
     * 고객번호.
     */
    public String customerNumber;

    /**
     * 저장된 PUSH 설정정보 조회.
     *
     * 저장된 설정정보가 없으면 기본값으로 설정정보 저장후 조회.
     *
     * @return PushSettings
     */
    public static PushSettings get() {
        PushSettings pushSettings = PrefRepositoryNamed.get(MainApplication.getAppContext(),
                PushSettings.class.getName(), PushSettings.class);

        if (pushSettings == null) {
            pushSettings = new PushSettings();
            pushSettings.save();
        }

        return pushSettings;
    }

    /**
     * 설정정보 저장.
     */
    public void save() {
        PrefRepositoryNamed.save(MainApplication.getAppContext(), PushSettings.class.getName(), this);
    }
}
