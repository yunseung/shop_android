package gsshop.mobile.v2.sso;

import android.content.ContentResolver;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class SSOCursor implements Cursor {
    private String TAG = "SimpleCursorData";

    @Override
    public String getColumnName(int i) {
        Log.d(TAG, String.format("getColumnName(i): %d, %s", i, token_));
        return token_;
    }

    @Override
    public String[] getColumnNames() {
        Log.d(TAG, "getColumnNames()");
        String[] result = new String[1];
        result[0] = token_;
        return result;
    }

    @Override
    public int getColumnCount() {
        Log.d(TAG, "getColumnCount()");
//        return SSODataSource.dataCount;
        return 1;
    }

    public void setToken(String token) {
        token_ = token;
    }

    private String token_ = "";

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public int getPosition() {
        return 0;
    }

    @Override
    public boolean move(int i) {
        return true;
    }

    @Override
    public boolean moveToPosition(int i) {
        return true;
    }

    @Override
    public boolean moveToFirst() {
        return true;
    }

    @Override
    public boolean moveToLast() {
        return true;
    }

    @Override
    public boolean moveToNext() {
        return true;
    }

    @Override
    public boolean moveToPrevious() {
        return true;
    }

    @Override
    public boolean isFirst() {
        return true;
    }

    @Override
    public boolean isLast() {
        return false;
    }

    @Override
    public boolean isBeforeFirst() {
        return false;
    }

    @Override
    public boolean isAfterLast() {
        return false;
    }

    @Override
    public int getColumnIndex(String s) {
        return 0;
    }

    @Override
    public int getColumnIndexOrThrow(String s) throws IllegalArgumentException {
        return 0;
    }

    @Override
    public byte[] getBlob(int i) {
        return new byte[0];
    }

    @Override
    public String getString(int i) {
        return "";
    }

    @Override
    public void copyStringToBuffer(int i, CharArrayBuffer charArrayBuffer) {
    }

    @Override
    public short getShort(int i) {
        return 0;
    }

    @Override
    public int getInt(int i) {
        return 0;
    }

    @Override
    public long getLong(int i) {
        return 0;
    }

    @Override
    public float getFloat(int i) {
        return 0;
    }

    @Override
    public double getDouble(int i) {
        return 0;
    }

    @Override
    public int getType(int i) {
        return 0;
    }

    @Override
    public boolean isNull(int i) {
        return false;
    }

    @Override
    public void deactivate() {
    }

    @Override
    public boolean requery() {
        return false;
    }

    @Override
    public void close() {
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public void registerContentObserver(ContentObserver contentObserver) {
    }

    @Override
    public void unregisterContentObserver(ContentObserver contentObserver) {
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
    }

    @Override
    public void setNotificationUri(ContentResolver contentResolver, Uri uri) {
    }

    @Override
    public Uri getNotificationUri() {
        return null;
    }

    @Override
    public boolean getWantsAllOnMoveCalls() {
        return false;
    }

    @Override
    public void setExtras(Bundle bundle) {
    }

    @Override
    public Bundle getExtras() {
        return null;
    }

    @Override
    public Bundle respond(Bundle bundle) {
        return null;
    }
}