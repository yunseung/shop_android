package gsshop.mobile.v2.sso;

import android.util.Log;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SSOAPIClient {
    private static final String TAG = "SSOAPIClient";
    private static final String SSO_URL = "https://sso.marketfordev.com";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public String verifySSOAuthToken(String text) {
        String url = SSO_URL + "/api/v1/verifySSOAuthToken";

        try {
            RequestBody requestBody = RequestBody.create(JSON, text);

            Request.Builder builder = new Request.Builder().url(url).get();
            Request request = builder
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json;charset=UTF-8")
                    .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36")
                    .post(requestBody)
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    JSONObject result = new JSONObject( responseBody.string() );
                    Log.d(TAG, "Response (verifySSOAuthToken): " + result.toString());
                    return result.getString("ssoAuthToken");
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "verifySSOAuthToken(String text)");
        }

        return "";
    }

    public String verifySSOAuthToken(String chCode, String token) {
        if (chCode.length() == 0) return "";
        if (token.length() == 0) return "";

        try {
            JSONObject json = new JSONObject();
            json.put("chnnlCode", chCode);
            json.put("ssoAuthToken", token);
            return verifySSOAuthToken(json.toString());
        } catch (Exception e) {
            Log.d(TAG, "verifySSOAuthToken(String chCode, String token");
        }

        return "";
    }

    public boolean removeSSOSession(String chCode, String token, String mbNo) {
        if (chCode.length() == 0) return false;
        if (token.length() == 0) return false;

        String url = SSO_URL + "/api/v1/removeSSOSession";

        try {
            JSONObject json = new JSONObject();
            json.put("chnnlCode", chCode);
            json.put("ssoAuthToken", token);
            json.put("unityMberNo", mbNo);
            RequestBody requestBody = RequestBody.create(JSON, json.toString());
            Log.d(TAG, json.toString());

            Request.Builder builder = new Request.Builder().url(url).get();
            Request request = builder
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json;charset=UTF-8")
                    .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36")
                    .post(requestBody)
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    JSONObject result = new JSONObject( responseBody.string() );
                    Log.d(TAG, "Response (removeSSOSession): " + result.toString());
                    return result.getString("resultCode").equals("E017") == false;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "verifySSOAuthToken");
        }

        return false;
    }

    private OkHttpClient client = new OkHttpClient();
}
