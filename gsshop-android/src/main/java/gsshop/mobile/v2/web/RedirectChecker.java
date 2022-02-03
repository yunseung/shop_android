/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.web;

import android.content.Context;

import gsshop.mobile.v2.R;

/**
 * 웹뷰에서 페이지가 리다이렉트되면서 현재 웹뷰 위에 다른 웹뷰 액티비티가
 * 뜨면서 공백화면이 남거나 이벤트 페이지에서 뒤로가기시 메인페이지로 가는 문제를 방지하기 위해 리다이렉트 검출
 * 축약 도메인 목록은 array/url_shortcut_list에서 판단
 * 향후 웹뷰를 한개로 통합하면 필요 없어짐
 * 
 * gsshopmobile://home?http://goo.gl/sJ6H5o - 숏링크단품(축약)
 *  [WebActivity onPageStarted]http://goo.gl/sJ6H5o                                                     
    [WebActivity onPageStarted]http://m.gsshop.com/deal/deal.gs?dealNo=38447{@literal &}mseq=W00054-L-2
    [WebActivity shouldOverrideUrlLoading]http://m.gsshop.com/deal/deal.gs?dealNo=38447{@literal &}mseq=W00054-L-2
    http://goo.gl/sJ6H5o[redfirent url ]http://m.gsshop.com/deal/deal.gs?dealNo=38447{@literal &}mseq=W00054-L-2
    http://m.gsshop.com/deal/deal.gs?dealNo=38447{@literal &}mseq=W00054-L-2
    [WebActivity onPageFinished]http://goo.gl/sJ6H5o                                         
    [리다이렉트]           
 
 * gsshopmobile://home?http://goo.gl/TcRBgy - 이벤트단품(축약)
 *  [WebActivity onPageStarted]http://goo.gl/TcRBgy                                                                                                                                    
    [WebActivity onPageStarted]http://m.gsshop.com/planPrd/planPrd.jsp?planseq=120793{@literal &}mseq=W00060-L-16#1257824
    [WebActivity shouldOverrideUrlLoading]http://m.gsshop.com/planPrd/planPrd.jsp?planseq=120793{@literal &}mseq=W00060-L-16#1257824
    [WebActivity onPageFinished]http://m.gsshop.com/planPrd/planPrd.jsp?planseq=120793{@literal &}mseq=W00060-L-16#1257824
    [WebActivity shouldOverrideUrlLoading]http://m.gsshop.com/prd/prd.gs?prdid=14326780{@literal &}arm=1-M-30M{@literal &}expId=plan120793_1257824
    http://m.gsshop.com/planPrd/planPrd.jsp?planseq=120793{@literal &}mseq=W00060-L-16#1257824[redfirent url ]http://m.gsshop.com/prd/prd.gs?prdid=14326780{@literal &}arm=1-M-30M{@literal &}expId=plan120793_1257824
    http://m.gsshop.com/prd/prd.gs?prdid=14326780{@literal &}arm=1-M-30M{@literal &}expId=plan120793_1257824
    [리다이렉트안함]
     
 * gsshopmobile://home?http://goo.gl/oybyp3 - 리다이렉트(축약)
 *  [WebActivity onPageStarted]http://goo.gl/oybyp3                                                                                           
    [WebActivity onPageStarted]http://m.gsshop.com/jsp/jseis_withLGeshop.jsp?media=BZ{@literal &}gourl=/deal/deal.gs?dealNo=36326{@literal &}baseId=14381
    [WebActivity shouldOverrideUrlLoading]http://m.gsshop.com/jsp/jseis_withLGeshop.jsp?media=BZ{@literal &}gourl=/deal/deal.gs?dealNo=36326{@literal &}baseId=14381
    http://goo.gl/oybyp3[redfirent url ]http://m.gsshop.com/jsp/jseis_withLGeshop.jsp?media=BZ{@literal &}gourl=/deal/deal.gs?dealNo=36326{@literal &}baseId=14381
    [redfirent]http://m.gsshop.com/jsp/jseis_withLGeshop.jsp?media=BZ{@literal &}gourl=/deal/deal.gs?dealNo=36326{@literal &}baseId=14381
    [WebActivity onPageFinished]http://goo.gl/oybyp3                                                                                          
    [리다이렉트] 
    
 * gsshopmobile://home?http://m.gsshop.com/planPrd/planPrd.jsp?planseq=104308{@literal &}planType=E{@literal &}mseq=W00060-L-6#1181572 이벤트
 *  [WebActivity onPageStarted]http://m.gsshop.com/planPrd/planPrd.jsp?planseq=104308{@literal &}planType=E{@literal &}mseq=W00060-L-6
    [WebActivity onPageFinished]http://m.gsshop.com/planPrd/planPrd.jsp?planseq=104308{@literal &}planType=E{@literal &}mseq=W00060-L-6#1181572
    [WebActivity shouldOverrideUrlLoading]http://m.gsshop.com/prd/prd.gs?prdid=14147822{@literal &}arm=2-M-30M{@literal &}expId=plan104308_1181572
    http://m.gsshop.com/planPrd/planPrd.jsp?planseq=104308{@literal &}planType=E{@literal &}mseq=W00060-L-6[redfirent url ]http://m.gsshop.com/prd/prd.gs?prdid=14147822{@literal &}arm=2-M-30M{@literal &}expId=plan104308_1181572
    http://m.gsshop.com/prd/prd.gs?prdid=14147822{@literal &}arm=2-M-30M{@literal &}expId=plan104308_1181572
    [리다이렉트안함]                                                                                 
 * 
 */
public class RedirectChecker {

    /**
     * 웹뷰 로딩시 처음으로 인식하는 url
     */
    private String startUrl = "";

    /**
     * 현재 url
     */
    private String currentUrl;

    /**
     * 이 페이지가 리다이렉트 되었는지 여부
     */
    private boolean isRedirect;

    /**
     * 페이지 로딩이 끝났는지 여부
     */
    private boolean pageFinished;

    /**
     * 페이지가 로딩중인지 여부
     */
    private boolean isPageLoading;

    /**
     * 숏컷 도메인 리스트
     */
    private final String[] urlShortcutList;

    public RedirectChecker(Context context) {
        clear();
        urlShortcutList = context.getResources().getStringArray(R.array.url_shortcut_list);
    }

    /**
     * 이 페이지가 리다이렉트 되었는지 확인
     * @param url url
     * @return boolean
     */
    public boolean isRedirect(String url) {
        if (isUrlShortcut(startUrl)) {
            return true;
        }

        return false;
    }

    /**
     * 현재 url 저장
     * @param url url
     */
    private void setCurrentUrl(String url) {
        this.currentUrl = url;
    }

    /**
     * 페이지 로딩시작
     * @param url url
     */
    public void pageStarted(String url) {
        isPageLoading = true;

        if ("".equals(startUrl)) {
            startUrl = url;
        }

        setCurrentUrl(url);
    }

    /**
     * 웹뷰 시작 url 저장
     * @param url url
     */
    public void setStartUrl(String url) {
        startUrl = url;
        setCurrentUrl(url);
    }

    /**
     * 페이지 로딩끝
     * @param url url
     */
    public void pageFinished(String url) {
        pageFinished = true;
        isPageLoading = false;

        if (startUrl.equals(url) && isUrlShortcut(url)) {
            isRedirect = true;
        }
    }

    /**
     * 넘겨준 url이 숏컷 url인지 리턴한다.
     * @param url url
     * @return boolean
     */
    public boolean isUrlShortcut(String url) {
        for (String urlShortcut : urlShortcutList) {
            if (shortcutCheck(url, urlShortcut)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 현재 페이지가 로딩중인지 리턴한다.
     * @return boolean
     */
    public boolean isPageLoading() {
        return this.isPageLoading;
    }

    private boolean shortcutCheck(String url, String urlShortcut) {
        String urlString = url;
        String shortcutString = urlShortcut;

        urlString = stringReplace(urlString);
        shortcutString = stringReplace(shortcutString);

        if (urlString.indexOf(shortcutString) != -1) {
            return true;
        }
        return false;
    }

    private String stringReplace(String str) {
        String replaceString = str;
        String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";
        replaceString = replaceString.replaceAll(match, "");
        return replaceString;
    }

    /**
     * 초기화
     */
    public void clear() {
        this.currentUrl = "";
        this.isRedirect = false;
        this.pageFinished = false;
    }
}
