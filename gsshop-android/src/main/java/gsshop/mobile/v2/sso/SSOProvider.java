package gsshop.mobile.v2.sso;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SSOProvider extends ContentProvider {
    private String TAG = "SSOProvider";

    @Override
    public boolean onCreate() {
        Log.d(TAG, "created");
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        SSOCursor cursor = new SSOCursor();
        cursor.setToken(SSOControl.getInstance().getLocalToken());
        Log.d(TAG, String.format("query() - Token: %s", cursor.getColumnName(0)));
        return cursor;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        Log.d(TAG, String.format("update() - %s", s));
        SSOControl.getInstance().saveLocalToken(s);
        return 0;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return "";
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}