/*
 * Copyright(C) 2012 by GS HOMESHOPPING.
 * All rights reserved.
 *
 * GS 홈쇼핑 담당자의 허락없이 재배포 할 수 없으며
 * GS 홈쇼핑 외부로 유출해서는 안된다.
 */
package gsshop.mobile.v2.attach;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import com.google.inject.Inject;
import com.gsshop.mocha.device.CameraUtils;
import com.gsshop.mocha.device.DeviceHandler;
import com.gsshop.mocha.pattern.mvc.BaseAsyncController;
import com.gsshop.mocha.ui.util.ImageUtils;

import java.io.IOException;

import gsshop.mobile.v2.AbstractBaseActivity;
import gsshop.mobile.v2.Keys;
import gsshop.mobile.v2.R;
import roboguice.util.Ln;

/**
 * 리뷰 사진 찍기 화면
 *
 */
@SuppressWarnings("unused")
public class FileAttachTakePictureActivity extends AbstractBaseActivity {

    /**
     * 현재 카메라미리보기 화면의 회전각도.
     * 시계방향. landscape기준. 기본값은 0도(portrait).
     */
    private int currentPreviewAngles = 0;

    /**
     * sensorManager
     */
    @Inject
    private SensorManager sensorManager;

    /**
     * surface
     */
    private CameraSurface surface;

    /**
     * 쵤영버튼을 눌러 사진을 찍은 상태인가?
     */
    private boolean pictureTaken = false;

    /**
     * onCreate
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_camera);
        surface = new CameraSurface(this);
        ((FrameLayout) findViewById(R.id.frame_preview)).addView(surface, 0);

        // 클릭리스너 등록
        setClickListener();
    }

    /**
     * onResume
     */
    @Override
    protected void onResume() {
        super.onResume();

        // NOTE 폰에 방향센서 없는 경우 처리
        // NOTE : orientation 센서는 deprecated됨.
        // Google recommends you use the accelerometer and magnetometer to calculate orientation.
        sensorManager.registerListener(orientationSensorListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * onPause
     */
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(orientationSensorListener);
    }

    /**
     * 뷰의 클릭리스너를 세팅한다.
     */
    private void setClickListener() {
        findViewById(R.id.btn_take_picture).setOnClickListener((View v) -> {
                    takePicture(findViewById(R.id.btn_take_picture));
                }
        );
    }

    /**
     * takePicture
     * @param button
     */
    private void takePicture(View button) {
        pictureTaken = true;
        button.setEnabled(false);

        surface.camera.takePicture(null, null, new PictureCallback() {
            /**
             *
             * @param data
             * @param camera
             */
            @Override
			public void onPictureTaken(final byte[] data, Camera camera) {
                new SavePictureController(FileAttachTakePictureActivity.this).execute(data);
            }
        });
    }

    /**
     * 찍은 사진을 적절히 축소 및 회전시켜 저장한다.
     *
     * T : 저장된 사진파일 경로.
     */
    private class SavePictureController extends BaseAsyncController<String> {
        private byte[] pictureData;

        /**
         * SavePictureController
         *
         * @param activityContext
         */
        protected SavePictureController(Context activityContext) {
            super(activityContext);
        }

        /**
         * onPrepare
         *
         * @param params
         * @throws Exception
         */
        @Override
        protected void onPrepare(Object... params) throws Exception {
            super.onPrepare(params);
            this.pictureData = (byte[]) params[0];
        }

        /**
         * process
         *
         * @return
         * @throws Exception
         */
        @Override
        protected String process() throws Exception {
        	//이미지를 촬영한 그대로 저장하고 사이즈를 줄인다. (가로촬영은 가로저장, 세로촬영은 세로저장)
        	Bitmap ori = BitmapFactory.decodeByteArray(pictureData, 0, pictureData.length);
            Bitmap converted = ImageUtils.rotate(ori, currentPreviewAngles);
            Bitmap scaled = ImageUtils.scaleDown(converted, FileAttachUtils.ATTACH_IMAGE_WIDTH);
            return ImageUtils.bitmapToFile(scaled,
                    FileAttachUtils.getTempAttachImage(getApplicationContext())).getPath();
        }

        /**
         * onSuccess
         * @param imagePath
         * @throws Exception
         */
        @Override
        protected void onSuccess(String imagePath) throws Exception {
            Intent data = new Intent();
            //NOTE : 이미지 데이터바이트를 전달하면 데이터가 너무 커서
            // out of memory 에러 발생할 수 있으므로 경로만 넘김
            data.putExtra(Keys.INTENT.ATTACH_IMAGE, imagePath);
            setResult(RESULT_OK, data);
            finish();
        }
    }

    /**
     * 카메라 미리보기 표면.
     *
     */
    private class CameraSurface extends SurfaceView implements SurfaceHolder.Callback {

        /**
         * surfaceHolder
         */
        private final SurfaceHolder surfaceHolder;
        /**
         * camera
         */
        private Camera camera;

        /**
         * CameraSurface
         *
         * @param context
         */
        @SuppressWarnings("deprecation")
        public CameraSurface(Context context) {
            super(context);
            surfaceHolder = getHolder();
            surfaceHolder.addCallback(this);

            // surfaceHolder.setType()은 3.0부터 deprecated 됨
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            }
        }

        /**
         * surfaceCreated
         * @param holder
         */
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // 표면 생성시 카메라 오픈하고 미리보기 설정.
            camera = Camera.open();

            try {
                camera.setPreviewDisplay(surfaceHolder);
                // 이 액티비티의 기본모드는 portrait임
                camera.setDisplayOrientation(90);
            } catch (IOException e) {
                camera.release();
                camera = null;
                setResult(RESULT_CANCELED);
                finish();
                throw new CameraPreviewException(e);
            }
        }

        /**
         * surfaceDestroyed
         * @param holder
         */
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // 표면 소멸시 카메라 자원 해제.
            if (camera != null) {
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        }

        /**
         * surfaceChanged
         *
         * 미리보기 화면의 크기기와 방향을 세팅하고 미리보기 시작.
         * 폰을 회전시키거나 표면 크기가 변경되는 경우에도 호출됨.
         *
         * @param holder
         * @param format
         * @param width
         * @param height
         */
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            // If your preview can change or rotate, take care of those events here.
            // Make sure to stop the preview before resizing or reformatting it.
            if (surfaceHolder.getSurface() == null) {
                return;
            }

            if (camera == null) {
                return;
            }

            // stop preview before making changes
            try {
                camera.stopPreview();
            } catch (Exception e) {
                // ignore: tried to stop a non-existent preview
                // 10/19 품질팀 요청
                // - 시스템 로그로 뿌지 않고 디버깅 상태에서 뿌리도록 변경
                // - 광범위한 예외처리(Exception e) 이유 : 명시적인 예외 이외에도 발생할지 몰라 방어로직을 추가한 경우임 협의
                // - 보편적으로 화면의 일부 영역을 그리거나, 오동작하여도 다른 서비스에 영향이 가지않도록 스킵하고 있다 협의
                Ln.e(e);
            }

            // 표면의 크기가 결정될 때 최적의 미리보기 크기를 구해 설정
            Camera.Parameters myParameters = camera.getParameters();
            Camera.Size myBestSize = CameraUtils.getBestPreviewSize(width, height, myParameters);
            if (myBestSize != null) {
                myParameters.setPreviewSize(myBestSize.width, myBestSize.height);
                camera.setParameters(myParameters);
            }

            try {
                camera.startPreview();
            } catch (Exception e) {
                setResult(RESULT_CANCELED);
                finish();
                throw new CameraPreviewException(e);
            }
        }

    }

    /**
     * orientationSensorListener
     *
     * 방향센서를 이용해 현재 카메라미리보기의 방향을 정한다.
     */
    private final SensorEventListener orientationSensorListener = new SensorEventListener() {

        /**
         * onSensorChanged
         * @param event SensorEvent
         */
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (pictureTaken) {
                return;
            }

            if (Sensor.TYPE_ORIENTATION != event.sensor.getType()) {
                return;
            }

            int rotation = DeviceHandler.getDisplayRotation(event);
            currentPreviewAngles = CameraUtils.screenToPreviewOrientation(rotation);
        }

        /**
         * onAccuracyChanged
         *
         * @param sensor Sensor
         * @param accuracy int
         */
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
}
