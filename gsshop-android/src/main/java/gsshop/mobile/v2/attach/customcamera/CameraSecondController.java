package gsshop.mobile.v2.attach.customcamera;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.TextureView;

import com.github.florent37.camerafragment.configuration.Configuration;
import com.github.florent37.camerafragment.configuration.ConfigurationProvider;
import com.github.florent37.camerafragment.internal.controller.CameraController;
import com.github.florent37.camerafragment.internal.controller.view.CameraView;
import com.github.florent37.camerafragment.internal.manager.CameraManager;
import com.github.florent37.camerafragment.internal.manager.impl.Camera2Manager;
import com.github.florent37.camerafragment.internal.manager.listener.CameraCloseListener;
import com.github.florent37.camerafragment.internal.manager.listener.CameraOpenListener;
import com.github.florent37.camerafragment.internal.manager.listener.CameraPhotoListener;
import com.github.florent37.camerafragment.internal.manager.listener.CameraVideoListener;
import com.github.florent37.camerafragment.internal.ui.view.AutoFitTextureView;
import com.github.florent37.camerafragment.internal.utils.CameraHelper;
import com.github.florent37.camerafragment.internal.utils.Size;
import com.github.florent37.camerafragment.listeners.CameraFragmentResultListener;

import java.io.File;

/*
상속 받아서 수정 해 봤더니 동작이 정상적이지 않음, 이에 컨트롤러 전부 가져옴.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class CameraSecondController implements CameraController<String>,
        CameraOpenListener<String, TextureView.SurfaceTextureListener>,
        CameraPhotoListener, CameraVideoListener, CameraCloseListener<String> {

    private final static String TAG = "CameraSecondController";

    private final Context context;
    private String currentCameraId;
    private ConfigurationProvider configurationProvider;
    private CameraManager<String, TextureView.SurfaceTextureListener> camera2Manager;
    private CameraView cameraView;

    private File outputFile;

    public CameraSecondController(Context context, CameraView cameraView, ConfigurationProvider configurationProvider) {
        this.context = context;
        this.cameraView = cameraView;
        this.configurationProvider = configurationProvider;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        camera2Manager = new Camera2Manager();
        camera2Manager.initializeCameraManager(configurationProvider, context);
        setCurrentCameraId(camera2Manager.getFaceBackCameraId());
    }

    @Override
    public void onResume() {
        camera2Manager.openCamera(currentCameraId, this);
    }

    @Override
    public void onPause() {
        camera2Manager.closeCamera(null);
        cameraView.releaseCameraPreview();
    }

    @Override
    public void onDestroy() {
        camera2Manager.releaseCameraManager();
    }

    @Override
    public void takePhoto(CameraFragmentResultListener callback) {
        takePhoto(callback, null, null);
    }

    @Override
    public void takePhoto(CameraFragmentResultListener callback, @Nullable String direcoryPath, @Nullable String fileName) {
        outputFile = CameraHelper.getOutputMediaFile(context, Configuration.MEDIA_ACTION_PHOTO, direcoryPath, fileName);
        camera2Manager.takePhoto(outputFile, this, callback);
    }

    @Override
    public void startVideoRecord() {
        startVideoRecord(null, null);
    }

    @Override
    public void startVideoRecord(@Nullable String direcoryPath, @Nullable String fileName) {
        outputFile = CameraHelper.getOutputMediaFile(context, Configuration.MEDIA_ACTION_VIDEO, direcoryPath, fileName);
        camera2Manager.startVideoRecord(outputFile, this);
    }

    @Override
    public void stopVideoRecord(CameraFragmentResultListener callback) {
        camera2Manager.stopVideoRecord(callback);
    }

    @Override
    public boolean isVideoRecording() {
        return camera2Manager.isVideoRecording();
    }

    @Override
    public void switchCamera(final @Configuration.CameraFace int cameraFace) {
    }

    private void setCurrentCameraId(String currentCameraId){
        this.currentCameraId = currentCameraId;
        camera2Manager.setCameraId(currentCameraId);
    }

    @Override
    public void setFlashMode(@Configuration.FlashMode int flashMode) {
        camera2Manager.setFlashMode(flashMode);
    }

    @Override
    public void switchQuality() {
    }

    @Override
    public int getNumberOfCameras() {
        return camera2Manager.getNumberOfCameras();
    }

    @Override
    public int getMediaAction() {
        return configurationProvider.getMediaAction();
    }

    @Override
    public File getOutputFile() {
        return outputFile;
    }

    @Override
    public String getCurrentCameraId() {
        return currentCameraId;
    }

    @Override
    public void onCameraOpened(String openedCameraId, Size previewSize, TextureView.SurfaceTextureListener surfaceTextureListener) {
        cameraView.updateUiForMediaAction(Configuration.MEDIA_ACTION_UNSPECIFIED);
        cameraView.updateCameraPreview(previewSize, new AutoFitTextureView(context, surfaceTextureListener));
        cameraView.updateCameraSwitcher(camera2Manager.getNumberOfCameras());
    }

    @Override
    public void onCameraOpenError() {
        Log.e(TAG, "onCameraOpenError");
    }

    @Override
    public void onCameraClosed(String closedCameraId) {
        // Why this camera open again? just release..
    }

    @Override
    public void onPhotoTaken(byte[] bytes, File photoFile, CameraFragmentResultListener callback) {
        cameraView.onPhotoTaken(bytes, callback);
    }

    @Override
    public void onPhotoTakeError() {
    }

    @Override
    public void onVideoRecordStarted(Size videoSize) {
        cameraView.onVideoRecordStart(videoSize.getWidth(), videoSize.getHeight());
    }

    @Override
    public void onVideoRecordStopped(File videoFile, @Nullable CameraFragmentResultListener callback) {
        cameraView.onVideoRecordStop(callback);
    }

    @Override
    public void onVideoRecordError() {

    }

    @Override
    public CameraManager getCameraManager() {
        return camera2Manager;
    }

    @Override
    public CharSequence[] getVideoQualityOptions() {
        return camera2Manager.getVideoQualityOptions();
    }

    @Override
    public CharSequence[] getPhotoQualityOptions() {
        return camera2Manager.getPhotoQualityOptions();
    }
}
