package gsshop.mobile.v2.sso;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import gsshop.mobile.v2.sso.utils.Encrypter;


public class SSODataSource {
    private final String TAG = "SSODataSource";
    private final String PREFERENCE = "gs.sso";

    public SSODataSource(Context context) {
        context_ = context;
        encrypter_ = new Encrypter(context);
    }

    synchronized public String loadFromLocalStorage() {
        SharedPreferences pref = context_.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        token_ = encrypter_.decrypt( pref.getString("token", "") );
        Log.d(TAG, String.format("loadFromLocalStorage() - token: %s", token_));
        return token_;
    }

    synchronized public void saveToken(String token) {
        token_ = token;

        SharedPreferences pref = context_.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("token", encrypter_.encrypt(token) );
        editor.commit();

        Log.d(TAG, String.format("saveToken() - token: %s", token_));
    }

    synchronized public String getToken() {
        return token_;
    }

    private Context context_ = null;
    private Encrypter encrypter_ = null;
    private String token_ = "";
}