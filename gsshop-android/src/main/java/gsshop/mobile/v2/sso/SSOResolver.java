package gsshop.mobile.v2.sso;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;

public class SSOResolver {
    private static final String TAG = "SSOResolver";

    public SSOResolver(Context context) {
        context_ = context;
    }

    public void addProviderUri(String uri) {
        if (uriList_.indexOf(uri) == -1) uriList_.add(uri);
    }

    public void updateToken(String token) {
        for (String uri : uriList_) {
            try {
                ContentValues values = new ContentValues();
                context_.getContentResolver().update(Uri.parse(uri), values, token, null);
                Log.d(TAG, String.format("updateToken() - uri: %s, token: %s", uri, token));
            } catch (Exception e) {
                Log.d(TAG, String.format("updateToken() - uri: %s, token: %s, error: %s", uri, token, e.getMessage()));
            }
        }
    }

    public String getToken() {
        for (String uri : uriList_) {
            Cursor cursor = null;
            try {
                cursor = context_.getContentResolver().query(Uri.parse(uri), null, null, null, null);
            } catch (Exception e) {
                Log.d(TAG, String.format("getToken() - %s, %s", uri, e.getMessage()) );
            }

            String token = "";
            if (cursor != null) token = cursor.getColumnName(0);

            Log.d(TAG, String.format("getToken() - uri: %s, token: %s", uri, token));

            if (token.length() != 0) return token;
        }

        return "";
    }

    private Context context_ = null;
    private ArrayList<String> uriList_ = new ArrayList<>();
}
