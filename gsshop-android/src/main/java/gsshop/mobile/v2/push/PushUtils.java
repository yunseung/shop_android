package gsshop.mobile.v2.push;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import androidx.annotation.RequiresApi;

import roboguice.util.Ln;

import static android.content.Context.NOTIFICATION_SERVICE;

public class PushUtils {
    @SuppressWarnings("unused")
    public static void createNotificationChannel(final Context context, final String channelId, final CharSequence channelName, final String channelDescription, final int importance, final boolean showBadge) {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                instance.postAsyncSafely("createNotificationChannel", new Runnable() {
                new AsyncTask<Void, Void, Void>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    protected Void doInBackground(Void... voids) {
                        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                        if (notificationManager == null) return null;

                        NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
                        notificationChannel.setDescription(channelDescription);
                        notificationChannel.setShowBadge(showBadge);
                        notificationManager.createNotificationChannel(notificationChannel);
                        return null;
                    }
                }.execute();
            }
        } catch (Exception e){
            Ln.e(e.getMessage());
        }
    }

    public static void deleteNotificationChannel(final Context context, final String channelId) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                new AsyncTask<Void, Void, Void>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    protected Void doInBackground(Void... voids) {
                        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                        if (notificationManager == null) return null;

                        notificationManager.deleteNotificationChannel(channelId);
                        return null;
                    }
                }.execute();
            }
        } catch (Exception e){
            Ln.e(e.getMessage());
        }
    }
}
