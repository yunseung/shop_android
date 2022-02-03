/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web.handler;

/**
 * {@link WebUrlHandler} 목록을 관리하며
 * 주어진 url을 처리할 수 있는 핸들러를 찾는 기능을 제공.
 *
 */
public abstract class WebUrlHandlerManager {

    /**
     * http(s) 주소 처리 핸들러목록.
     *
     * NOTE : 한번 세팅되면 더이상 추가/삭제가 없으므로 List가 아닌
     * array로 구현하였다.
     * member.jg 예외처리를 없앴다 리스크 확인 필요 
     */
    private static WebUrlHandler[] httpUrlHandlers = {
            new GeneralCardUrlHandler() };

    /**
     * 커스텀 주소 처리 핸들러목록.
     */
    private static WebUrlHandler[] customUrlHandlers = { new LoginUrlHandler(), new AutoLoginUrlHandler(),
            new LogoutUrlHandler(), new BackUrlHandler(), new IspUrlHandler(), new KakaoTalkHandler(),
            new ShinhanCardUrlHandler(), new HyundaiCardUrlHandler(), new LotteCardUrlHandler(), new PayNowUrlHandler(),
            new IgnoredUrlHandler(), new LiveTVUrlHandler(), new VodUrlHandler(), new ReviewUrlHandler(),
            new SettingUrlHandler(), new ModalWebUrlHandler(), new ExtModalWebUrlHandler(), new CloseUrlHandler(),
            new ExternalWebHandler(), new MoveMessageUrlHandler(), new DealVodUrlHandler(), new BaseVodUrlHandler(),
            new EventWebUrlHandler(), new MoveSectUrlHandler(), new LiveStreamingUrlHandler(),
            new SmsUrlHandler(), new FileAttachUrlHandler(), new PinterestUrlHandler(),new SearchUrlHandler(),
            new NalbangWebUrlHandler(), new NalbangPrdUrlHandler(), new LiveTalkWebUrlHandler(),
            new DirectBuyUrlHandler(), new ShareUrlHandler(), new FullModalWebUrlHandler(), new ShortbangWebUrlHandler(),
            new ShortbangEventWebUrlHandler(), new SnsDialogShowHandler(), new SNSLoginHandler(),
            new OutSiteModalWebUrlHandler(), new RotationUrlHandler(), new MobileLiveWebUrlHandler(),
            new PhotoEditUrlHandler(), new ExternalAddressHandler(), new OpenNewPageUrlHandler(),
            new ShoppyCouponTodayOpenPassUrlHandler()
    };

    /**
     * http(s) 프로토콜로 시작하는 url을 처리할 수 있는 핸들러를 찾는다.
     *
     * @param url
     * @return 없으면 null
     */
    public static WebUrlHandler findHttpUrlHandler(String url) {
        for (int i = 0; i < httpUrlHandlers.length; i++) {
            WebUrlHandler h = httpUrlHandlers[i];
            if (h.match(url)) {
                return h;
            }
        }

        return null;
    }

    /**
     * http(s) 프로토콜이 아닌 커스텀 프로토콜 url을 처리할 수 있는 핸들러를 찾는다.
     *
     * @param url
     * @return 없으면 null
     */
    public static WebUrlHandler findCustomUrlHandler(String url) {
        for (int i = 0; i < customUrlHandlers.length; i++) {
            WebUrlHandler h = customUrlHandlers[i];
            if (h.match(url)) {
                return h;
            }
        }

        return null;
    }

}
