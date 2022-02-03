package gsshop.mobile.v2.support.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import roboguice.util.Ln;

/**
 * @author leoshin on 15. 10. 7.
 */
public class StoryLink {
    private static StoryLink stroyLink = null;

    private static String storyLinkApiVersion = "1.0";
    private static String storyLinkURLBaseString = "storylink://posting";

    private static Charset storyLinkCharset = Charset.forName("UTF-8");
    private static String storyLinkEncoding = storyLinkCharset.name();

    private Context context;
    private String params;

    private StoryLink(Context context) {
        super();
        this.context = context;
        this.params = getBaseStoryLinkUrl();
    }

    /**
     * Return the default singleton instance
     *
     * @param context
     * @return StroyLink instance.
     */
    public static StoryLink getLink(Context context) {
        if (stroyLink != null)
            return stroyLink;

        return new StoryLink(context);
    }

    /**
     * Opens kakaoLink for parameter.
     *
     * @param activity
     * @param params
     */
    private void openStoryLink(Activity activity, String params) {
        Intent intent = new Intent(Intent.ACTION_SEND, Uri.parse(params));
        activity.startActivity(intent);
    }

    /**
     * Opens kakaoLink URL for parameters.
     *
     * @param activity
     * @param post (message or url)
     * @param appId
     *            your application ID
     * @param appVer
     *            your application version
     * @param appName
     *            your application name
     * @param encoding
     *            recommend UTF-8
     * @param urlInfoAndroid
     */
    public void openKakaoLink(Activity activity, String post, String appId, String appVer, String appName, String encoding, Map<String, Object> urlInfoAndroid) {
        String SharePost = post;
        if (isEmptyString(SharePost) || isEmptyString(appId) || isEmptyString(appVer) || isEmptyString(appName) || isEmptyString(encoding))
            throw new IllegalArgumentException();

        try {
            if (storyLinkCharset.equals(Charset.forName(encoding)))
                SharePost = new String(SharePost.getBytes(encoding), storyLinkEncoding);
        } catch (UnsupportedEncodingException e) {
            // 10/19 품질팀 요청
            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
            // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
            Ln.e(e);
        }

        this.params = getBaseStoryLinkUrl();

        appendParam("post", SharePost);
        appendParam("appid", appId);
        appendParam("appver", appVer);
        appendParam("apiver", storyLinkApiVersion);
        appendParam("appname", appName);
        appendUrlInfo(urlInfoAndroid);

        openStoryLink(activity, params);
    }

    /**
     * @return Whether the application can open StoryLink URLs.
     */
    public boolean isAvailableIntent() {
        Uri kakaoLinkTestUri = Uri.parse(storyLinkURLBaseString);
        Intent intent = new Intent(Intent.ACTION_SEND, kakaoLinkTestUri);
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list == null)
            return false;
        return !list.isEmpty();
    }

    private boolean isEmptyString(String str) {
        return (str == null || str.trim().length() == 0);
    }

    private void appendParam(final String name, final String value) {
        try {
            String encodedValue = URLEncoder.encode(value, storyLinkEncoding);
            params = params + name + "=" + encodedValue + "&";
        } catch (UnsupportedEncodingException e) {
            // 10/19 품질팀 요청
            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
            // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
            Ln.e(e);
        }
    }

    private void appendUrlInfo(Map<String, Object> urlInfoAndroid) {
        params += "urlinfo=";

        JSONObject metaObj = new JSONObject();

        try {
            for (String key : urlInfoAndroid.keySet()) {
                if("imageurl".equals(key)) {
                    metaObj.put(key, getImageUrl(urlInfoAndroid.get(key)));
                } else {
                    metaObj.put(key, urlInfoAndroid.get(key));
                }
            }
        } catch (JSONException e) {
            // 10/19 품질팀 요청
            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
            // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
            Ln.e(e);
        }

        try {
            String encodedValue = URLEncoder.encode(metaObj.toString(), storyLinkEncoding);
            params += encodedValue;
        } catch (UnsupportedEncodingException e) {
            // 10/19 품질팀 요청
            // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
            // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
            // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
            Ln.e(e);
        }
    }

    private JSONArray getImageUrl(Object imageUrl) {
        JSONArray arrImageUrl = new JSONArray();
        String[] objImageUrl = (String[]) imageUrl;

        for(int i=0; i < objImageUrl.length; i++) {
            arrImageUrl.put(objImageUrl[i]);
        }
        return arrImageUrl;
    }

    private String getBaseStoryLinkUrl() {
        return storyLinkURLBaseString + "?";
    }

    /**
     * Opens StoryLink for parameter.
     *
     * @param activity
     * @param path
     */
    public void openStoryLinkImageApp(Activity activity, String path) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
        intent.setPackage("com.kakao.story");
        activity.startActivity(intent);
    }
}
