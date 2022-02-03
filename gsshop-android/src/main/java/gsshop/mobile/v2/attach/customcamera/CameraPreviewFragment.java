package gsshop.mobile.v2.attach.customcamera;

import android.Manifest;
import androidx.annotation.RequiresPermission;

import com.github.florent37.camerafragment.configuration.Configuration;

public class CameraPreviewFragment extends CameraCustomFragment {
    @RequiresPermission(Manifest.permission.CAMERA)
    public static CameraPreviewFragment newInstance(Configuration configuration) {
        return (CameraPreviewFragment) CameraCustomFragment.newInstance(new CameraPreviewFragment(), configuration);
    }

    public void setFlash(int mode) {
        setFlashMode(mode);
    }

    public float getPreviewRatio() {
        return getPreviewDiffRatio();
    }
}
