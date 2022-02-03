package gsshop.mobile.v2.attach;

import android.content.Context;
import androidx.annotation.Nullable;

import com.google.inject.Inject;
import com.gsshop.mocha.pattern.mvc.BaseAsyncController;

import gsshop.mobile.v2.MainApplication;

public class FileUploadController extends BaseAsyncController<FileAttachConnector.ShowmeAttachResult> {

    @Inject
    private FileAttachAction attachAction;

    private final FileAttachInfoV2 attachInfo;
    private boolean isEventVideo = false;
    private boolean isImageEdit = false;

    private OnUploadResultListener mListener = null;

    public interface OnUploadResultListener {
        void onResult(FileAttachConnector.ShowmeAttachResult result);
    }

    public FileUploadController(Context activityContext, FileAttachInfoV2 attachInfo, @Nullable OnUploadResultListener listener) {
        super(activityContext);
        this.attachInfo = attachInfo;
        mListener = listener;
    }

    public FileUploadController(Context activityContext, FileAttachInfoV2 attachInfo, boolean isImageEdit, @Nullable OnUploadResultListener listener) {
        super(activityContext);
        this.attachInfo = attachInfo;
        this.isImageEdit = isImageEdit;
        this.mListener = listener;
    }

    @Override
    protected void onPrepare(Object... params) throws Exception {
        super.onPrepare(params);
        this.dialog.dismiss();

        //이벤트 동영상 업로드의 경우는 별도 프로그레스바 노출
        this.isEventVideo = (boolean) params[0];
        if (!isEventVideo) {
            this.dialog.dismiss();
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        attachInfo.setCaller(MainApplication.fileAttachParamInfo.getCaller());
    }

    @Override
    protected FileAttachConnector.ShowmeAttachResult process() throws Exception {
        return attachAction.showMesaveFiles(attachInfo, MainApplication.fileAttachParamInfo.getUploadUrl(), isEventVideo);
    }

    /**
     * 업로드 완료시 웹뷰의 자바스크립트를 호출하여 알린다.
     */
    @Override
    protected void onSuccess(FileAttachConnector.ShowmeAttachResult result) throws Exception {
        super.onSuccess(result);
        if(result.getResult() != null && "success".equals(result.getResult())) {
            if (mListener != null) {
                mListener.onResult(result);
            }
        }
    }
}