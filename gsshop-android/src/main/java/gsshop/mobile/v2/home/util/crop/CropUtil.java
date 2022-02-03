package gsshop.mobile.v2.home.util.crop;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import roboguice.util.Ln;

class CropUtil {
    private static final String SCHEME_FILE = "file";
    private static final String SCHEME_CONTENT = "content";

    CropUtil() {
    }

    public static void closeSilently(@Nullable Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (Throwable var2) {
            }

        }
    }

    public static int getExifRotation(File imageFile) {
        if (imageFile == null) {
            return 0;
        } else {
            try {
                ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
                switch(exif.getAttributeInt("Orientation", 0)) {
                    case 3:
                        return 180;
                    case 6:
                        return 90;
                    case 8:
                        return 270;
                    default:
                        return 0;
                }
            } catch (IOException var2) {
                Ln.e("Error getting Exif data", var2);
                return 0;
            }
        }
    }

    public static boolean copyExifRotation(File sourceFile, File destFile) {
        if (sourceFile != null && destFile != null) {
            try {
                ExifInterface exifSource = new ExifInterface(sourceFile.getAbsolutePath());
                ExifInterface exifDest = new ExifInterface(destFile.getAbsolutePath());
                exifDest.setAttribute("Orientation", exifSource.getAttribute("Orientation"));
                exifDest.saveAttributes();
                return true;
            } catch (IOException var4) {
                Ln.e("Error copying Exif data", var4);
                return false;
            }
        } else {
            return false;
        }
    }

    @Nullable
    public static File getFromMediaUri(Context context, ContentResolver resolver, Uri uri) {
        if (uri == null) {
            return null;
        } else if ("file".equals(uri.getScheme())) {
            return new File(uri.getPath());
        } else if ("content".equals(uri.getScheme())) {
            String[] filePathColumn = new String[]{"_data", "_display_name"};
            Cursor cursor = null;

            File var6;
            try {
                cursor = resolver.query(uri, filePathColumn, (String)null, (String[])null, (String)null);
                if (cursor == null || !cursor.moveToFirst()) {
                    return null;
                }

                int columnIndex = uri.toString().startsWith("content://com.google.android.gallery3d") ? cursor.getColumnIndex("_display_name") : cursor.getColumnIndex("_data");
                if (columnIndex == -1) {
                    return null;
                }

                String filePath = cursor.getString(columnIndex);
                if (TextUtils.isEmpty(filePath)) {
                    return null;
                }

                File var7 = new File(filePath);
                return var7;
            } catch (IllegalArgumentException var12) {
                var6 = getFromMediaUriPfd(context, resolver, uri);
            } catch (SecurityException var13) {
                return null;
            } finally {
                if (cursor != null) {
                    cursor.close();
                }

            }

            return var6;
        } else {
            return null;
        }
    }

    private static String getTempFilename(Context context) throws IOException {
        File outputDir = context.getCacheDir();
        File outputFile = File.createTempFile("image", "tmp", outputDir);
        return outputFile.getAbsolutePath();
    }

    @Nullable
    private static File getFromMediaUriPfd(Context context, ContentResolver resolver, Uri uri) {
        if (uri == null) {
            return null;
        } else {
            FileInputStream input = null;
            FileOutputStream output = null;

            try {
                ParcelFileDescriptor pfd = resolver.openFileDescriptor(uri, "r");
                FileDescriptor fd = pfd.getFileDescriptor();
                input = new FileInputStream(fd);
                String tempFilename = getTempFilename(context);
                output = new FileOutputStream(tempFilename);
                byte[] bytes = new byte[4096];

                int read;
                while((read = input.read(bytes)) != -1) {
                    output.write(bytes, 0, read);
                }

                File var10 = new File(tempFilename);
                return var10;
            } catch (IOException var14) {
            } finally {
                closeSilently(input);
                closeSilently(output);
            }

            return null;
        }
    }

    public static void startBackgroundJob(MonitoredActivity activity, String title, String message, Runnable job, Handler handler) {
        ProgressDialog dialog = ProgressDialog.show(activity, title, message, true, false);
        (new Thread(new CropUtil.BackgroundJob(activity, job, dialog, handler))).start();
    }

    private static class BackgroundJob extends MonitoredActivity.LifeCycleAdapter implements Runnable {
        private final MonitoredActivity activity;
        private final ProgressDialog dialog;
        private final Runnable job;
        private final Handler handler;
        private final Runnable cleanupRunner = new Runnable() {
            public void run() {
                CropUtil.BackgroundJob.this.activity.removeLifeCycleListener(CropUtil.BackgroundJob.this);
                if (CropUtil.BackgroundJob.this.dialog.getWindow() != null) {
                    CropUtil.BackgroundJob.this.dialog.dismiss();
                }

            }
        };

        public BackgroundJob(MonitoredActivity activity, Runnable job, ProgressDialog dialog, Handler handler) {
            this.activity = activity;
            this.dialog = dialog;
            this.job = job;
            this.activity.addLifeCycleListener(this);
            this.handler = handler;
        }

        public void run() {
            try {
                this.job.run();
            } finally {
                this.handler.post(this.cleanupRunner);
            }

        }

        public void onActivityDestroyed(MonitoredActivity activity) {
            this.cleanupRunner.run();
            this.handler.removeCallbacks(this.cleanupRunner);
        }

        public void onActivityStopped(MonitoredActivity activity) {
            this.dialog.hide();
        }

        public void onActivityStarted(MonitoredActivity activity) {
            this.dialog.show();
        }
    }
}
