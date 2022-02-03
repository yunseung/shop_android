package gsshop.mobile.v2.web;

import android.content.Context;

import gsshop.mobile.v2.R;

/**
 * Created by cools on 2016. 6. 28.. Leems
 *
 * 1. 새로고침 되면 위험한 리스트 포함
 * 2. 웹뷰가 백그라운드 (스택상 아래) 위치한 경우 이벤트 버스로 호출되는 경우에만 적용
 * 3. 다른곳에 적용될 경우에는 충분한 검토가 이루어져야함
 */
public class NonReloadChecker {

    private final String[] urlNonReloadList;

    /**
     * 리소스에 정의된 노리로드 리스트를 초기화
     * @param context
     */
    public NonReloadChecker(Context context) {

        urlNonReloadList = context.getResources().getStringArray(R.array.url_nonreload_list);
    }

    /**
     * 넘겨준 url이 리로드 하면 안되는 대상 url인지 리턴한다.
     * @param url
     * @return
     */
    public boolean isNonReload(String url) {
        for (String urlShortcut : urlNonReloadList) {
            if (nonReloadCheck(url, urlShortcut)) {
                return true;
            }
        }

        return false;
    }

    /**
     *
     * @param url
     * @param urlShortcut
     * @return
     */
    private boolean nonReloadCheck(String url, String urlShortcut) {
        String urlString = url;
        String shortcutString = urlShortcut;

        urlString = stringReplace(urlString);
        shortcutString = stringReplace(shortcutString);

        if (urlString.indexOf(shortcutString) != -1) {
            return true;
        }
        return false;
    }

    /**
     * 설마?? 특수문자
     * @param str
     * @return
     */
    private String stringReplace(String str) {
        String replaceString = str;
        String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";
        replaceString = replaceString.replaceAll(match, "");
        return replaceString;
    }
}
